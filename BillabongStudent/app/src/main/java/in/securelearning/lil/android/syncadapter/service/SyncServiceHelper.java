package in.securelearning.lil.android.syncadapter.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.dataobjects.Credentials;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.AppUserAuth0;
import in.securelearning.lil.android.syncadapter.dataobject.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobject.RefreshToken;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTP;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.BaseApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    //    public static boolean refreshToken(Context context) {
//        return refreshToken(context, AppPrefs.getUserName(context), AppPrefs.getUserPassword(context), AppPrefs.getUserMobile(context));
//    }

    public static synchronized boolean login(Context context, String username, String password, boolean isFromOTP) throws IOException {
        ApiModule apiModule = new ApiModule(context);
        BaseApiInterface apiInterface = apiModule.getBaseClient();

        Response<AuthToken> response = null;

        Log.e("Logging in", "now");
        if (isFromOTP) {
            RequestOTP requestOTP = new RequestOTP();
            requestOTP.setCode(password);
            requestOTP.setMobile(username);
            response = apiInterface.verifyOTP(requestOTP).execute();
        } else {
            Credentials credentials = new Credentials();
            credentials.setUserName(username);
            credentials.setPassword(password);
            response = apiInterface.loginNew(credentials).execute();

        }
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
                if (!TextUtils.isEmpty(message)) {
                    showFinishAlertDialog(context, message);
                } else {
                    showFinishAlertDialog(context, context.getString(R.string.no_internet));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;

        } else if ((response.code() == 500)) {
            if (username.contains("@") && username.contains(".")) {
                showFinishAlertDialog(context, context.getString(R.string.incorrect_email_or_password));
            } else if (username.length() == 10 && TextUtils.isDigitsOnly(username)) {
                showFinishAlertDialog(context, context.getString(R.string.incorrect_phone_number_or_password));
            } else {
                showFinishAlertDialog(context, context.getString(R.string.incorrect_login_information));
            }
            return false;
        } else if (response != null && response.code() == 401) {
            if (username.contains("@") && username.contains(".")) {
                showFinishAlertDialog(context, context.getString(R.string.incorrect_email_or_password));
            } else if (username.length() == 10 && TextUtils.isDigitsOnly(username)) {
                showFinishAlertDialog(context, context.getString(R.string.incorrect_phone_number_or_password));
            } else {
                showFinishAlertDialog(context, context.getString(R.string.incorrect_login_information));
            }
            //context.startActivity(LoginActivity.getUnauthorizedIntent(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        Log.e("SyncService", "err refreshToken" + response.message());


        return false;
    }

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

    public synchronized static boolean refreshToken(Context context) {
        ApiModule apiModule = new ApiModule(context);
        BaseApiInterface apiInterface = apiModule.getBaseClient();

        Response<AuthToken> response = null;
        try {
            Log.e("refreshing token", "now");
            RefreshToken refreshToken = new RefreshToken();
            String idToken = AppPrefs.getIdToken(context);
            if (TextUtils.isEmpty(idToken)) {
                startLoginActivityForUnathorizedAction(context);
                return false;
            } else {
                refreshToken.setIdToken(idToken);
                response = apiInterface.refreshToken(refreshToken).execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("Logging in", "successful");
                    final AuthToken token = response.body();
                    //do not get access_token back
//                AppPrefs.setAccessToken(token.getAccessToken(), context);

                    if (!TextUtils.isEmpty(token.getToken())) {
                        AppPrefs.setIdToken(token.getToken(), context);
                        return true;
                    } else {
                        startLoginActivityForUnathorizedAction(context);
                        return false;
                    }

                } else if (response != null && response.code() == 401) {
                    startLoginActivityForUnathorizedAction(context);
                    return false;
                }
                Log.e("SyncService", "err refreshing token" + response.message());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public static void startLoginActivityForUnathorizedAction(Context context) {
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

    public static UserProfile getCurrentUserProfile(Context context) {
        ApiModule apiModule = new ApiModule(context);
        DownloadApiInterface apiInterface = apiModule.getDownloadClient();


        try {
            Response<AppUserAuth0> response = apiInterface.getCurrentLoggedInUser().execute();
            if (response != null && response.isSuccessful()) {
                final AppUserAuth0 user = response.body();
                if (!TextUtils.isEmpty(user.getUserInfo().getObjectId()))
                    return user.getUserInfo();
            } else if (response != null && response.code() == 401) {
                if (refreshToken(context)) {
                    Response<AppUserAuth0> response2 = apiInterface.getCurrentLoggedInUser().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        final AppUserAuth0 user = response2.body();
                        if (!TextUtils.isEmpty(user.getUserInfo().getObjectId()))
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


// public static boolean refreshToken(Context context, String username, String password) {
//        ApiModule apiModule = new ApiModule(context);
//        BaseApiInterface apiInterface = apiModule.getBaseClient();
//
//
//        Response<AppUser> response = null;
//        try {
//            Credentials credentials = new Credentials();
//            if (context != null) {
//                credentials.setUserName(username);
//                credentials.setPassword(password);
//            }
//            Log.e("Logging in", "now");
//            response = apiInterface.refreshToken(credentials).execute();
//            if (response != null && response.isSuccessful()) {
//                com.couchbase.lite.util.Log.e("Logging in", "successful");
//                final AppUser user = response.body();
//                //store password for local use
//                user.getUserInfo().setPassword(password);
//                user.setUserType(getUserType(user.getUserInfo()));
//
////            setUpUserProfileForApplication(user);
//                Injector.INSTANCE.getComponent().appUserModel().setApplicationUser(user);
//                Injector.INSTANCE.getComponent().appUserModel().saveUserProfileInGuestDB(user.getUserInfo());
////            JobCreator.createUserProfileValidationJob(user.getUserInfo()).execute();
////            JobCreator.createCurriculumDownloadJob().execute();
//
//                return true;
//            } else if (response != null && response.code() == 401) {
//                context.startActivity(LoginActivity.getUnauthorizedIntent(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            }
//            Log.e("SyncService", "err refreshToken" + response.message());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return false;
//    }
//

    public static boolean updateProfile() {

        JobCreator.createUserProfileValidationJob(Injector.INSTANCE.getComponent().appUserModel().getApplicationUser()).execute();
        return true;

    }

//    public static void runSyncServiceTestQuiz(final Context context) {
//        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
//            @Override
//            public void run() {
//                ApiModule apiModule = new ApiModule(context);
//                BaseApiInterface apiInterface = apiModule.getBaseClient();
//                Credentials credentials = new Credentials();
//                credentials.setUserName("lil");
//                credentials.setPassword("lil");
//
//                Response<AppUser> response = null;
//                try {
//                    Log.e("Logging in", "now");
//                    response = apiInterface.refreshToken(credentials).execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (response != null && response.isSuccessful()) {
//                    Log.e("Logging in", "successful");
//                    AppUser user = response.body();
//
//                    Injector.INSTANCE.getComponent().appUserModel().getApplicationUser().setId(user.getId());
//
//                    String id = "57af1a330f5b86dc1abb2cc7";
//
//                         /*   Log.e("Downloading quiz", "now");
//                            DownloadQuizJsonJob job = new DownloadQuizJsonJob(id);
//*/
//                    Quiz quiz = getQuiz();
//                    quiz.setObjectId("98024528475928475982375982759823");
//                    quiz = new JobModel().saveQuiz(quiz);
//                    ValidateQuizJob job = new ValidateQuizJob(quiz);
//                    job.execute();
//
////                            Quiz quiz = new JobModel().fetchQuizFromObjectId(id);
//                    Log.e("quizFetch", quiz.getObjectId());
//
//                }
//
//
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        });
//
//    }

//    public static void runSyncServiceTestStudent(final Context context) {
//        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
//            @Override
//            public void run() {
//                ApiModule apiModule = new ApiModule(context);
//                BaseApiInterface apiInterface = apiModule.getBaseClient();
//                Credentials credentials = new Credentials();
//                credentials.setUserName("lil");
//                credentials.setPassword("lil");
//
//                Response<AppUser> response = null;
//                try {
//                    Log.e("Logging in", "now");
//                    response = apiInterface.refreshToken(credentials).execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (response != null && response.isSuccessful()) {
//                    Log.e("Logging in", "successful");
//                    AppUser user = response.body();
//
//                    Injector.INSTANCE.getComponent().appUserModel().getApplicationUser().setId(user.getId());
//
//                            /*assignment response id*/
//                    String idAssignmentResponse = "a74e70b6-1d7f-4116-9b10-c1ccb124b2b9";
//                    String idAssignment = "57bdcf40e6ea5d782a711662";
//                         /*   Log.e("Downloading quiz", "now");
//                            DownloadQuizJsonJob job = new DownloadQuizJsonJob(id);
//*/
//                   /* try {
//                        Response<Assignment> response1 = new NetworkModel().fetchAssignment(idAssignment).execute();
//                        if (response1.isSuccessful()) {
//                            Assignment assignment = response1.body();
//                            new ValidateAssignmentJob(assignment).execute();
//                        } else {
//                            Log.e("Ass Down Err", response1.message());
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }*/
//                    try {
//                        Response<AssignmentResponse> response1 = new NetworkModel().fetchAssignmentResponse(idAssignmentResponse).execute();
//                        if (response1.isSuccessful()) {
//                            AssignmentResponse assignmentResponse = response1.body();
//                            assignmentResponse.setAssignmentID(idAssignment);
//                            ValidateAssignmentResponseJob job = new ValidateAssignmentResponseJob(assignmentResponse);
//                            job.execute();
//                        } else {
//                            Log.e("AssRes Down Err", response1.message());
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        });
//
//    }

//    public static void runSyncServiceAssignmentResponseTest(final Context context) {
//        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
//                                                                          @Override
//                                                                          public void run() {
//                                                                              ApiModule apiModule = new ApiModule(context);
//                                                                              BaseApiInterface apiInterface = apiModule.getBaseClient();
//                                                                              Credentials credentials = new Credentials();
//                                                                              credentials.setUserName("lil");
//                                                                              credentials.setPassword("lil");
//
//                                                                              Response<AppUser> response = null;
//                                                                              try {
//                                                                                  Log.e("Logging in", "now");
//                                                                                  response = apiInterface.refreshToken(credentials).execute();
//                                                                              } catch (IOException e) {
//                                                                                  e.printStackTrace();
//                                                                              }
//
//                                                                              if (response != null && response.isSuccessful()) {
//                                                                                  Log.e("Logging in", "successful");
//                                                                                  AppUser user = response.body();
//
//                                                                                  Injector.INSTANCE.getComponent().appUserModel().getApplicationUser().setId(user.getId());
//
//                                                                                  String idAssignmentResponse = "ba9798e9-f075-44b1-b662-70e29f845d74";
//                                                                                  String idAssignment = "57bd771349f69f8015091818";
//
//                                                                                  AssignmentResponse assignmentResponse = new AssignmentResponse();
//                                                                                  try {
//                                                                                      assignmentResponse = new NetworkModel().fetchAssignmentResponse(idAssignmentResponse).execute().body();
//                                                                                  } catch (IOException e) {
//                                                                                      e.printStackTrace();
//                                                                                  }
//
//                                                                                  assignmentResponse.setAssignmentID(idAssignment);
//                                                                                  ValidateAssignmentResponseJob job = new ValidateAssignmentResponseJob(assignmentResponse);
//                                                                                  job.execute();
//
//
//                                                                              }
//
//
//                                                                          }
//                                                                      }
//
//                , new Consumer<Throwable>()
//
//                {
//                    @Override
//                    public void accept(Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                }
//
//        );
//
//    }

//    public static void runFtpTest(final Context context) {
////        FtpFunctions f1 = new FtpFunctions();
////
//        String fileName = "quiz_new";
//        String jsonString = getJsonString(getRawId(fileName), context);
//        final Quiz quiz1 = constructUsingGson(Quiz.class, jsonString);
////
////        fileName = "quiz_new2";
////        jsonString = getJsonString(getRawId(fileName), context);
////        final Quiz quiz2 = constructUsingGson(Quiz.class, jsonString);
////
////        fileName = "quiz_new3";
////        jsonString = getJsonString(getRawId(fileName), context);
////        final Quiz quiz3 = constructUsingGson(Quiz.class, jsonString);
//
////        String fileName1 = "course1";
////        String fileName2 = "course2";
////        String fileName6 = "interactive_image";
////        String fileName7 = "interactive_image2";
////        String fileName8 = "popup";
////        String fileName9 = "popup2";
////        String fileName10 = "popup3";
////        String fileName11 = "concept_map";
////        String fileName12 = "concept_map2";
////        String fileName13 = "quiz_web";
////        String fileName14 = "quiz_web2";
////        String jsonString1 = getJsonString(getRawId(fileName1), context);
////        String jsonString2 = getJsonString(getRawId(fileName2), context);
////        String jsonString3 = getJsonString(getRawId(fileName3), context);
////        String jsonString4 = getJsonString(getRawId(fileName4), context);
////        String jsonString5 = getJsonString(getRawId(fileName5), context);
////        String jsonString6 = getJsonString(getRawId(fileName6), context);
////        String jsonString7 = getJsonString(getRawId(fileName7), context);
////        String jsonString8 = getJsonString(getRawId(fileName8), context);
////        String jsonString9 = getJsonString(getRawId(fileName9), context);
////        String jsonString10 = getJsonString(getRawId(fileName10), context);
////        String jsonString11 = getJsonString(getRawId(fileName11), context);
//        String jsonString11 = "{'title':'Over the Top','id':'4308573793845793845794','data':'http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  https://image.freepik.com/free-vector/education-icons-collection_23-2147501305.jpg https://image.freepik.com/free-vector/education-icons-collection_23-2147501305.jpg https://lh3.googleusercontent.com/YGqr3CRLm45jMF8eM8eQxc1VSERDTyzkv1CIng0qjcenJZxqV5DBgH5xlRTawnqNPcOp=w300 https://lh3.googleusercontent.com/YGqr3CRLm45jMF8eM8eQxc1VSERDTyzkv1CIng0qjcenJZxqV5DBgH5xlRTawnqNPcOp=w300 http://image.flaticon.com/sprites/authors/1-freepik.png   http://icons.iconarchive.com/icons/designbolts/free-multimedia/1024/iMac-icon.png http://icons.iconarchive.com/icons/designbolts/free-multimedia/1024/iMac-icon.png http://image.flaticon.com/sprites/authors/28-simpleicon.png http://image.flaticon.com/sprites/authors/28-simpleicon.png http://icons.iconarchive.com/icons/graphicloads/100-flat/256/home-icon.png http://icons.iconarchive.com/icons/graphicloads/100-flat/256/home-icon.png https://lh5.ggpht.com/8h4a-QzLFHu3pxFZ-Np8AyyUCq1G97T1_7F3ScjbBaxb6wGSdoCLHanFtnQa9kbg0d4=w300 https://lh5.ggpht.com/8h4a-QzLFHu3pxFZ-Np8AyyUCq1G97T1_7F3ScjbBaxb6wGSdoCLHanFtnQa9kbg0d4=w300 http://icons.iconarchive.com/icons/graphicloads/colorful-long-shadow/256/Home-icon.png http://icons.iconarchive.com/icons/graphicloads/colorful-long-shadow/256/Home-icon.png'}";
////        String jsonString12 = getJsonString(getRawId(fileName12), context);
//        String jsonString12 = "{'title':'Learn from me','id':'43085737933845793845794','data':'http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png  http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png'}";
////        String jsonString13 = getJsonString(getRawId(fileName13), context);
////        String jsonString14 = getJsonString(getRawId(fileName14), context);
////        final DigitalBook digitalBook1 = constructUsingGson(DigitalBook.class, jsonString1);
////        final DigitalBook digitalBook2 = constructUsingGson(DigitalBook.class, jsonString2);
////        final DigitalBook digitalBook3 = constructUsingGson(DigitalBook.class, jsonString3);
////        final DigitalBook digitalBook4 = constructUsingGson(DigitalBook.class, jsonString4);
////        final DigitalBook digitalBook5 = constructUsingGson(DigitalBook.class, jsonString5);
//
//        final ConceptMap conceptMap = constructUsingGson(ConceptMap.class, jsonString11);
//        final ConceptMap conceptMap2 = constructUsingGson(ConceptMap.class, jsonString12);
//
////        final InteractiveImage interactiveImage = constructUsingGson(InteractiveImage.class, jsonString6);
////        final InteractiveImage interactiveImage2 = constructUsingGson(InteractiveImage.class, jsonString7);
////
////        final PopUps popUps = constructUsingGson(PopUps.class, jsonString8);
////        final PopUps popUps2 = constructUsingGson(PopUps.class, jsonString9);
////        final PopUps popUps3 = constructUsingGson(PopUps.class, jsonString10);
//
////        final Quiz quiz =constructUsingGson(Quiz.class,jsonString13);
////        final Quiz quiz2 =constructUsingGson(Quiz.class,jsonString14);
//
//        conceptMap.setJson(jsonString11);
//        conceptMap2.setJson(jsonString12);
////        interactiveImage.setJson(jsonString6);
////        interactiveImage2.setJson(jsonString7);
////        popUps.setJson(jsonString8);
////        popUps2.setJson(jsonString9);
////        popUps3.setJson(jsonString10);
////        quiz.setJson(jsonString13);
////        quiz2.setJson(jsonString14);
//
////        insertSampleInDb(quiz1, context, DOC_TYPE_QUIZ);
////        insertSampleInDb(quiz2, context, DOC_TYPE_QUIZ);
////        insertSampleInDb(quiz3, context, DOC_TYPE_QUIZ);
////        insertSampleInDb(digitalBook1, context, DOC_TYPE_DIGITAL_BOOK);
////        insertSampleInDb(digitalBook2, context, DOC_TYPE_DIGITAL_BOOK);
////        insertSampleInDb(digitalBook3, context, DOC_TYPE_DIGITAL_BOOK);
////        insertSampleInDb(digitalBook4, context, DOC_TYPE_DIGITAL_BOOK);
////        insertSampleInDb(digitalBook5, context, DOC_TYPE_DIGITAL_BOOK);
////
////        insertSampleInDb(conceptMap, context, DOC_TYPE_CONCEPT_MAP);
////        insertSampleInDb(conceptMap2, context, DOC_TYPE_CONCEPT_MAP);
////        insertSampleInDb(interactiveImage, context, DOC_TYPE_INTERACTIVE_IMAGE);
////        insertSampleInDb(interactiveImage2, context, DOC_TYPE_INTERACTIVE_IMAGE);
////        insertSampleInDb(popUps, context, DOC_TYPE_POP_UPS);
////        insertSampleInDb(popUps2, context, DOC_TYPE_POP_UPS);
////        insertSampleInDb(popUps3, context, DOC_TYPE_POP_UPS);
//
//        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
//            @Override
//            public void run() {
//
//                ApiModule apiModule = new ApiModule(context);
//                BaseApiInterface apiInterface = apiModule.getBaseClient();
//                Credentials credentials = new Credentials();
//                credentials.setUserName("prabodh.dhabaria@securelearning.in");
//                credentials.setPassword("prabodh");
//
////                         Response<AppUser> response = null;
////                        try {
////                            Log.e("Logging in", "now");
////                            response = apiInterface.refreshToken(credentials).execute();
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////                        if (response != null && response.isSuccessful()) {
////                            Log.e("Logging in", "successful");
////                            AppUser user = response.body();
////
////                            Injector.INSTANCE.getComponent().appUserModel().getApplicationUser().setId(user.getId());
////
////                        }
////                        DownloadDigitalBookJsonJob job1 = new DownloadDigitalBookJsonJob("57e0ef99fa2714001a509fde");
////                        job1.execute();
////
////                        DownloadDigitalBookJsonJob job2 = new DownloadDigitalBookJsonJob("57e10629fa2714001a50a334");
////                        job2.execute();
////
////                        DownloadDigitalBookJsonJob job3 = new DownloadDigitalBookJsonJob("57e905582eb25d001eca74f9");
////                        job3.execute();
//
////                        ValidateQuizJob job = new ValidateQuizJob(quiz1);
////                        job.execute();
//
////                        ValidateConceptMapJob job9 = new ValidateConceptMapJob(conceptMap);
////                        job9.execute();
////
////                        ValidateConceptMapJob job10 = new ValidateConceptMapJob(conceptMap2);
////                        job10.execute();
//////
////                        ValidateInteractiveImageJob job11 = new ValidateInteractiveImageJob(interactiveImage);
////                        job11.execute();
////
////                        ValidateInteractiveImageJob job12 = new ValidateInteractiveImageJob(interactiveImage2);
////                        job12.execute();
//
////                        ValidatePopUpsJob job13 = new ValidatePopUpsJob(popUps);
////                        job13.execute();
////
////                        ValidatePopUpsJob job14 = new ValidatePopUpsJob(popUps2);
////                        job14.execute();
////
////                        ValidatePopUpsJob job15 = new ValidatePopUpsJob(popUps3);
////                        job15.execute();
//
////                        ValidateQuizWebJob job16 = new ValidateQuizWebJob(quiz);
////                        job16.execute();
////
////                        ValidateQuizWebJob job17 = new ValidateQuizWebJob(quiz2);
////                        job17.execute();
//
////                        ValidateDigitalBookJob job4 = new ValidateDigitalBookJob(digitalBook1);
////                        job4.execute();
////
////                        ValidateDigitalBookJob job5 = new ValidateDigitalBookJob(digitalBook2);
////                        job5.execute();
//
////                        ValidateDigitalBookJob job6 = new ValidateDigitalBookJob(digitalBook3);
////                        job6.execute();
////
////                        ValidateDigitalBookJob job7 = new ValidateDigitalBookJob(digitalBook4);
////                        job7.execute();
////
////                        ValidateDigitalBookJob job8 = new ValidateDigitalBookJob(digitalBook5);
////                        job8.execute();
//
//            }
//        });
//    }

//    public static Quiz getQuiz() {
//        Resource resource = new Resource();
//        String url = "http://res.cloudinary.com/dbhvgeq0t/image/upload/v1468478565/e1due9mzfizx7wzade9o.jpg";
//        resource.setUrlMain(url);
//        resource.setName("2342343242343dfdf2423455");
//        resource.setDeviceURL("2323232323232/images/2342343242343dfdf2423455");
//        ArrayList<Resource> list = new ArrayList<>();
//        list.add(resource);
//        Question question = new Question();
//        question.setQuestionText("hello");
//        question.setQuestionType(Question.TYPE_QUESTION_SUBJECTIVE);
//        question.setSkillType(Question.TYPE_SKILL1);
//        question.setComplexityLevel(Question.TYPE_COMPLEXITY2);
//        question.setResources(list);
//        ArrayList<Question> questions = new ArrayList<>();
//        questions.add(question);
//
//        Quiz quiz = new Quiz();
//        quiz.getMapping().getSubject().setId("2323232323232");
//        quiz.getMapping().getTopics().add(new Topic("sfsdf", "sdsd"));
//        quiz.setAlias(GeneralUtils.generateAlias("A", "B", DateUtils.getCurrentDateTime()));
//        quiz.setQuestions(questions);
//
//        return quiz;
//    }

//    public static int getRawId(String mName) {
//
//        int resId = 0;
//        try {
//            @SuppressWarnings("rawtypes") Class raw = R.raw.class;
//            Field field = raw.getField(mName);
//            resId = field.getInt(null);
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//
//        return resId;
//
//    }

//    private static String getJsonString(int resId, Context context) {
//        String jsonString = "";
//
//        InputStream resourceReader = context.getResources().openRawResource(resId);
//        Writer writer = new StringWriter();
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceReader, "UTF-8"));
//            String line = reader.readLine();
//            while (line != null) {
//                writer.write(line);
//                line = reader.readLine();
//            }
//        } catch (Exception e) {
//            Log.e("JsonFromRaw", "Unhandled exception while using JSONResourceReader", e);
//        } finally {
//            try {
//                resourceReader.close();
//            } catch (Exception e) {
//                Log.e("JsonFromRaw", "Unhandled exception while using JSONResourceReader", e);
//            }
//        }
//
//        jsonString = writer.toString();
//        return jsonString;
//
//    }

//    public static <T> T constructUsingGson(Class<T> type, String jsonString) {
//        Gson gson = new GsonBuilder().create();
//        return gson.fromJson(jsonString, type);
//    }

}
