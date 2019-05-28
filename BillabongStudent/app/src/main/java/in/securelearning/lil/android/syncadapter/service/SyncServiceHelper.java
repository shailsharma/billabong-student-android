package in.securelearning.lil.android.syncadapter.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.dataobjects.Credentials;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.login.events.AlreadyLoggedInEvent;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.AppUserAuth0;
import in.securelearning.lil.android.syncadapter.dataobject.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobject.RefreshToken;
import in.securelearning.lil.android.syncadapter.dataobject.Token;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.BaseApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * Sync Service Helper to facilitate interactions with the Sync service.
 */
public class SyncServiceHelper {
    /**
     * start the sync service
     *
     * @param context
     */
    @SuppressLint("CheckResult")
    public static void startSyncService(final Context context) {
        //if (System.currentTimeMillis() - PrefManager.getLastSyncTime(context) > PrefManager.MINIMUM_SYNC_DELAY) {
        Completable.complete().observeOn(Schedulers.newThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        SyncService.startSyncService(context);
                    }
                });


        if (BuildConfig.IS_LEARNING_NETWORK_ENABLED) {
            Completable.complete().observeOn(Schedulers.newThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            MessageService.startSyncService(context);
                        }
                    });
        }
        Completable.complete().observeOn(Schedulers.newThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        CourseService.startSyncService(context);
                    }
                });

        FlavorSyncServiceHelper.startSyncService(context);
        PrefManager.setLastSyncTime(System.currentTimeMillis(), context);
        //}
    }

    public static void startUploadPostData(Context context, String alias) {
        MessageService.startActionSyncPost(context, alias);
    }

    public static void startUploadPostResponse(Context context, String alias) {
        MessageService.startActionSyncPostResponse(context, alias);
    }

    public static void startUploadWebQuiz(Context context) {
        SyncService.startActionUploadWebQuiz(context);
    }

    public static void startUploadBookAnnotation(Context context) {
        SyncService.startActionUploadBookAnnotation(context);
    }

    public static void startDownloadLearningMap(Context context) {
        SyncService.startActionDownloadLearningMap(context);
    }

    public static void startUploadUserBrowseHistoryLog(Context context) {
        SyncService.startUploadUserBrowseHistoryLog(context);
    }

    public static void startActionDownloadPerformanceCount(Context context, String subid) {
        SyncService.startActionDownloadPerformanceCount(context, subid);
    }

    public static void startActionDownloadActivity(Context context, String subid, String startDate, String endDate) {
        SyncService.startActionDownloadActivity(context, subid, startDate, endDate);
    }

    public static void startActionDownloadActivityRecentlyRead(Context context, String subid) {
        SyncService.startActionDownloadActivityRecentlyRead(context, subid);
    }

    public static void startActionDownloadActivityTopicCovered(Context context, String subid) {
        SyncService.startActionDownloadActivityTopicCovered(context, subid);
    }

    public static void startActionDownloadLearning(Context context, String subid, String startDate, String endDate) {
        SyncService.startActionDownloadLearning(context, subid, startDate, endDate);
    }

    public static void stopSyncService(Context context) {
        context.stopService(new Intent(context, BroadcastNotificationService.class));
        context.stopService(new Intent(context, SyncService.class));
        context.stopService(new Intent(context, MessageService.class));
        context.stopService(new Intent(context, PeriodService.class));
        context.stopService(new Intent(context, UserService.class));
        context.stopService(new Intent(context, AssignmentService.class));
        context.stopService(new Intent(context, CourseService.class));
    }


    public static synchronized boolean login(Context context, String username, String password) throws IOException {
        ApiModule apiModule = new ApiModule(context);
        BaseApiInterface apiInterface = apiModule.getBaseClient();

        Response<AuthToken> response = null;

        Log.e("Logging in", "now");

        Credentials credentials = new Credentials();
        credentials.setUserName(username);
        credentials.setPassword(password);
        response = apiInterface.authLogin(credentials).execute();

        if (response != null && response.isSuccessful()) {
            Log.e("Logging in", "successful");
            final AuthToken token = response.body();

            AppPrefs.setAccessToken(token.getAccessToken(), context);
            AppPrefs.setIdToken(token.getToken(), context);

            return true;
        } else if ((response.code() == 400)) {
//            ResponseBody responseBody = response.errorBody();
//            JSONObject jsonObject = new JSONObject(responseBody.string());
//            JSONObject error = jsonObject.getJSONObject("error");
//            String message = error.getString("message");

            showFinishAlertDialog(context, context.getString(R.string.incorrect_enrollment_number_or_password));


            return false;

        } else if ((response.code() == 403)) {
            ResponseBody responseBody = response.errorBody();
            try {
                JSONObject jsonObject = new JSONObject(responseBody.string());
                JSONObject error = jsonObject.getJSONObject("error");
                String token = error.getString("token");
                AppPrefs.setIdToken(token, context);
                showMultipleLoginAlertDialog(context, context.getString(R.string.message_multiple_login), token);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;

        } else if ((response.code() == 500)) {

            showFinishAlertDialog(context, context.getString(R.string.error_something_went_wrong));
            return false;
        } else if (response.code() == 401) {

            showFinishAlertDialog(context, context.getString(R.string.incorrect_enrollment_number_or_password));

        }
        Log.e("SyncService", "err refreshToken" + response.message());


        return false;
    }

    @SuppressLint("CheckResult")
    private static void showFinishAlertDialog(final Context context, final String message) {
        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                new AlertDialog.Builder(context)
                        .setMessage(message)
                        .setCancelable(false)
                        .setNeutralButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }

                        }).show();
            }
        }, new Consumer<Throwable>() {

            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });

    }

    @SuppressLint("CheckResult")
    private static void showMultipleLoginAlertDialog(final Context context, final String message, final String token) {
        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                new AlertDialog.Builder(context)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                invalidateOtherLogin(context, token, dialogInterface);

                            }

                        })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }

                        })
                        .show();
            }
        }, new Consumer<Throwable>() {

            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });

    }

    /*To invalidate other login of current logged in user.*/
    @SuppressLint("CheckResult")
    private static synchronized void invalidateOtherLogin(final Context context, final String token, final DialogInterface dialogInterface) {
        Observable.create(new ObservableOnSubscribe<ResponseBody>() {
            @Override
            public void subscribe(ObservableEmitter<ResponseBody> emitter) throws Exception {
                if (GeneralUtils.isNetworkAvailable(context)) {
                    ApiModule apiModule = new ApiModule(context);
                    BaseApiInterface apiInterface = apiModule.getBaseClient();

                    Response<ResponseBody> response = apiInterface.invalidateOtherLogin(new Token(token)).execute();
                    if (response != null && response.isSuccessful()) {
                        emitter.onNext(response.body());
                    } else {
                        emitter.onError(new Exception());
                    }
                    emitter.onComplete();

                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                        dialogInterface.dismiss();
                        Toast.makeText(context, context.getString(R.string.message_invalidate_other_login_success), Toast.LENGTH_SHORT).show();
                        Injector.INSTANCE.getComponent().rxBus().send(new AlreadyLoggedInEvent(true));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        dialogInterface.dismiss();
                        Toast.makeText(context, context.getString(R.string.messageUnableToGetData), Toast.LENGTH_SHORT).show();
                        Injector.INSTANCE.getComponent().rxBus().send(new AlreadyLoggedInEvent(false));

                    }
                });


    }

    /*To fetch and updated authorization token of logged in user.*/
    public synchronized static boolean refreshToken(Context context) {
        ApiModule apiModule = new ApiModule(context);
        BaseApiInterface apiInterface = apiModule.getBaseClient();

        Response<AuthToken> response = null;
        try {
            Log.e("refreshing token", "now");
            RefreshToken refreshToken = new RefreshToken();
            String idToken = AppPrefs.getIdToken(context);
            if (TextUtils.isEmpty(idToken)) {
                startLoginActivityForUnauthorizedAction(context);
                return false;
            } else {
                refreshToken.setIdToken(idToken);
                response = apiInterface.refreshToken(refreshToken).execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("Logging in", "successful");
                    final AuthToken token = response.body();

                    if (!TextUtils.isEmpty(token.getToken())) {
                        AppPrefs.setIdToken(token.getToken(), context);
                        return true;
                    } else {
                        startLoginActivityForUnauthorizedAction(context);
                        return false;
                    }

                } else if (response != null && response.code() == 401) {
                    startLoginActivityForUnauthorizedAction(context);
                    Log.e("SyncService", "err refreshing token" + response.code());
                    return false;
                } else if (response != null && response.code() == 403) {
                    startLoginActivityForUnauthorizedAction(context);
                    Log.e("SyncService", "err refreshing token" + response.code());
                    return false;
                } else {
                    startLoginActivityForUnauthorizedAction(context);
                    Log.e("SyncService", "err refreshing token" + response.code());
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    /*Only activity context are allowed here*/
    @SuppressLint("CheckResult")
    public static void performUserLogout(@NonNull final Activity context) {

        Observable.create(new ObservableOnSubscribe<ResponseBody>() {
            @Override
            public void subscribe(ObservableEmitter<ResponseBody> emitter) throws Exception {
                ApiModule apiModule = new ApiModule(context);
                BaseApiInterface apiInterface = apiModule.getBaseClient();

                String token = AppPrefs.getIdToken(context);
                Response<ResponseBody> response = apiInterface.authLogout(new Token(token)).execute();
                if (response != null && response.isSuccessful()) {
                    emitter.onNext(response.body());
                } else {
                    emitter.onError(new Exception(context.getString(R.string.messageUnableToGetData)));
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        if (responseBody != null) {
                            Intent intent = LoginActivity.getLogoutIntent(context);
                            context.startActivity(intent);
                            context.finishAffinity();
//                            int pendingIntentId = 1234567;
//                            PendingIntent mPendingIntent = PendingIntent.getActivity(context, pendingIntentId, intent,
//                                    PendingIntent.FLAG_CANCEL_CURRENT);
//                            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                            assert mgr != null;
//                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                            System.exit(0);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private static void startLoginActivityForUnauthorizedAction(Context context) {
        context.startActivity(LoginActivity.getUnauthorizedIntent(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static boolean setCurrentUserProfile(Context context) {
        UserProfile userProfile = getCurrentUserProfile(context);
        if (userProfile != null) {

            Injector.INSTANCE.getComponent().appUserModel().setApplicationUser(userProfile);
            Injector.INSTANCE.getComponent().appUserModel().saveUserProfileInGuestDB(userProfile);

            return true;
        }
        return false;
    }

    /*Fetch full user profile of current logged in user*/
    private static UserProfile getCurrentUserProfile(Context context) {
        ApiModule apiModule = new ApiModule(context);
        DownloadApiInterface apiInterface = apiModule.getDownloadClient();

        try {
            Response<AppUserAuth0> response = apiInterface.getCurrentLoggedInUser().execute();
            if (response != null && response.isSuccessful()) {
                final AppUserAuth0 user = response.body();
                if (user != null && user.getUserInfo() != null && !TextUtils.isEmpty(user.getUserInfo().getObjectId()))
                    return user.getUserInfo();
            } else if (response != null && response.code() == 401) {
                if (refreshToken(context)) {
                    Response<AppUserAuth0> response2 = apiInterface.getCurrentLoggedInUser().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        final AppUserAuth0 user = response2.body();
                        if (user != null && user.getUserInfo() != null && !TextUtils.isEmpty(user.getUserInfo().getObjectId()))
                            return user.getUserInfo();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static void updateProfile() {

        JobCreator.createUserProfileValidationJob(Injector.INSTANCE.getComponent().appUserModel().getApplicationUser()).execute();

    }

}
