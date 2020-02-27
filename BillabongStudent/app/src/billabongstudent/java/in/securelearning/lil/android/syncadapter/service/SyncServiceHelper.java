package in.securelearning.lil.android.syncadapter.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProgressBarBinding;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.dataobjects.Credentials;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.login.events.AlreadyLoggedInEvent;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AppUserAuth0;
import in.securelearning.lil.android.syncadapter.dataobjects.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobjects.RefreshToken;
import in.securelearning.lil.android.syncadapter.dataobjects.Token;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.BaseApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
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


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startAppSyncServices(context);
        } else {
            Single.just(true)
                    .delaySubscription(300, TimeUnit.MILLISECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            if (aBoolean) {
                                startAppSyncServices(context);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }


    }

    @SuppressLint("CheckResult")
    private static void startAppSyncServices(final Context context) {
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
                            MessageService.startActionDownloadPostAndResponseBulk(context);
                        }
                    });
        }

        Completable.complete().observeOn(Schedulers.newThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        BroadcastNotificationService.startSyncService(context);
                    }
                });

        PrefManager.setLastSyncTime(System.currentTimeMillis(), context);
    }


    public static void stopSyncService(Context context) {
        context.stopService(new Intent(context, BroadcastNotificationService.class));
        context.stopService(new Intent(context, SyncService.class));
        context.stopService(new Intent(context, MessageService.class));
        context.stopService(new Intent(context, UserService.class));
        context.stopService(new Intent(context, CourseService.class));
    }

    public static synchronized boolean login(Context context, String username, String password) throws IOException {
        ApiModule apiModule = new ApiModule(context);
        BaseApiInterface apiInterface = apiModule.getBaseClient();

        Log.e("Logging in", "now");

        Credentials credentials = new Credentials();
        credentials.setUserName(username);
        credentials.setPassword(password);
        Response<AuthToken> response = apiInterface.authLogin(credentials).execute();

        if (response != null && response.isSuccessful()) {
            Log.e("Logging in", "successful");
            final AuthToken token = response.body();

            AppPrefs.setAccessToken(token.getAccessToken(), context);
            AppPrefs.setIdToken(token.getToken(), context);

            return true;
        } else if ((response.code() == 400)) {
            ResponseBody responseBody = response.errorBody();
            try {
                JSONObject jsonObject = new JSONObject(responseBody.string());
                JSONObject error = jsonObject.getJSONObject("error");
                String message = error.getString("message");
                String code = error.getString("code");
                if (!TextUtils.isEmpty(message) && !TextUtils.isEmpty(code) && code.contains("ACCOUNT")) {
                    showFinishAlertDialog(context, message);
                } else {
                    showFinishAlertDialog(context, context.getString(R.string.incorrect_enrollment_number_or_password));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


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
        LayoutProgressBarBinding view = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_progress_bar, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(view.getRoot());
        view.texViewMessage.setText(context.getString(R.string.messagePleaseWait));

        if (!dialog.isShowing()) {
            dialog.show();

        }
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
                        dialog.dismiss();
                        dialogInterface.dismiss();
                        Toast.makeText(context, context.getString(R.string.message_invalidate_other_login_success), Toast.LENGTH_SHORT).show();
                        Injector.INSTANCE.getComponent().rxBus().send(new AlreadyLoggedInEvent(true));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        dialog.dismiss();
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

        Response<AuthToken> response;
        try {
            Log.e("refreshing token", "now");
            RefreshToken refreshToken = new RefreshToken();
            String idToken = AppPrefs.getIdToken(context);
            if (TextUtils.isEmpty(idToken)) {
                startLoginActivityForUnauthorizedAction(context);
                return false;
            } else {
                if (AppPrefs.isLoggedIn(context)) {

                    refreshToken.setIdToken(idToken);
                    response = apiInterface.refreshToken(refreshToken).execute();

                    if (response != null && response.isSuccessful()) {
                        Log.e("Logging in", "successful");
                        final AuthToken token = response.body();

                        if (token != null && !TextUtils.isEmpty(token.getToken())) {
                            AppPrefs.setIdToken(token.getToken(), context);
                            return true;
                        } else {
                            Log.e("SyncService", "err refreshing token: No POST body " + response.code());
                            startLoginActivityForUnauthorizedAction(context);
                            return false;
                        }

                    } else if (response != null && response.code() == 401) {
                        startLoginActivityForUnauthorizedAction(context);
                        Log.e("SyncService", "err refreshing token: " + response.code());
                        return false;
                    } else if (response != null && response.code() == 403) {
                        startLoginActivityForUnauthorizedAction(context);
                        Log.e("SyncService", "err refreshing token: " + response.code());
                        return false;
                    } else {
                        startLoginActivityForUnauthorizedAction(context);
                        Log.e("SyncService", "err refreshing token: " + response.code());
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("Use JsonReader.setLenient(true)")) {
                AppPrefs.setIdToken(AppPrefs.getIdToken(context), context);
                Log.e("refreshToken--", "CatchBlock: duplicate call");
                return true;
            } else {
                startLoginActivityForUnauthorizedAction(context);
                Log.e("refreshToken--", "CatchBlock: error refreshing token");
                return false;
            }

        }


        return false;
    }

    /*Only activity context are allowed here*/
    @SuppressLint("CheckResult")
    public static void performUserLogout(@NonNull final Activity context, String message) {
        LayoutProgressBarBinding view = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_progress_bar, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(view.getRoot());
        view.texViewMessage.setText(message);

        if (!dialog.isShowing()) {
            dialog.show();
        }


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
                    emitter.onError(new Exception(context.getString(R.string.error_something_went_wrong)));
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        dialog.dismiss();
                        if (responseBody != null) {
                            Intent intent = LoginActivity.getLogoutIntent(context);
                            context.startActivity(intent);
                            context.finishAffinity();

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dialog.dismiss();
                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private static void startLoginActivityForUnauthorizedAction(Context context) {
        AppPrefs.setLoggedIn(false, context);
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
