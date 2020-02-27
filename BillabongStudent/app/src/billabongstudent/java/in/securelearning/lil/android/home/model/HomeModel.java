package in.securelearning.lil.android.home.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.EffortvsPerformanceData;
import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.homework.dataobject.AssignedHomeworkParent;
import in.securelearning.lil.android.login.dataobject.ResponseErrorBody;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.view.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobjects.HelpAndFaqCategory;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectResult;
import in.securelearning.lil.android.syncadapter.dataobjects.RequestOTP;
import in.securelearning.lil.android.syncadapter.dataobjects.RequestOTPResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.RevisionSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.UserChallengePost;
import in.securelearning.lil.android.syncadapter.dataobjects.VideoForDayParent;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopic;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopicRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiQidsChallengeParent;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class HomeModel {

    @Inject
    NetworkModel mNetworkModel;

    @Inject
    Context mContext;

    public HomeModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    /*To request OTP sms on mobile*/
    public Observable<RequestOTPResponse> requestOTP(final String mobileNumber) {
        return Observable.create(new ObservableOnSubscribe<RequestOTPResponse>() {
            @Override
            public void subscribe(ObservableEmitter<RequestOTPResponse> e) throws Exception {
                RequestOTP requestOTP = new RequestOTP();
                requestOTP.setCode(null);
                requestOTP.setMobile(mobileNumber);
                Call<RequestOTPResponse> call = mNetworkModel.requestOTP(requestOTP);
                Response<RequestOTPResponse> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    RequestOTPResponse body = response.body();
                    Log.e("RequestOTPResponse--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 400) {
                    String message = getErrorMessage(response, mContext.getString(R.string.enrollment_number_not_exist_in_database));

                    throw new Exception(message);
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<RequestOTPResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        RequestOTPResponse course = response2.body();
                        Log.e("RequestOTPResponse--", "Successful");
                        e.onNext(course);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response.code() == 400) {

                        String message = getErrorMessage(response, mContext.getString(R.string.enrollment_number_not_exist_in_database));

                        throw new Exception(message);

                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("RequestOTPResponse--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("RequestOTPResponse--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    /*Generic method to parse error body of server*/
    private String getErrorMessage(Response<RequestOTPResponse> errorResponse, String localMessage) {
        String errorMessage = ConstantUtil.BLANK;
        try {
            ResponseBody responseBody = errorResponse.errorBody();

            if (responseBody != null) {
                JSONObject jsonObject = new JSONObject(responseBody.string());
                JSONObject error = jsonObject.getJSONObject("error");
                String messageBody = error.getString("message");
                ResponseErrorBody responseErrorBody = GeneralUtils.fromGson(messageBody, ResponseErrorBody.class);

                if (!TextUtils.isEmpty(responseErrorBody.getMessage())) {
                    errorMessage = responseErrorBody.getMessage();
                } else {
                    errorMessage = localMessage;
                }
            } else {
                errorMessage = localMessage;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return errorMessage;

    }

    /*To verify OTP code*/
    public Observable<AuthToken> verifyOTP(final String mobileNumber, final String verificationCode) {
        return Observable.create(new ObservableOnSubscribe<AuthToken>() {
            @Override
            public void subscribe(ObservableEmitter<AuthToken> e) throws Exception {
                RequestOTP requestOTP = new RequestOTP();
                requestOTP.setCode(verificationCode);
                requestOTP.setMobile(mobileNumber);
                Call<AuthToken> call = mNetworkModel.verifyOTP(requestOTP);
                Response<AuthToken> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    AuthToken body = response.body();
                    Log.e("VerifyOTPResponse--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 400) {
                    throw new Exception(mContext.getString(R.string.invalid_otp));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<AuthToken> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        AuthToken course = response2.body();
                        Log.e("VerifyOTPResponse--", "Successful");
                        e.onNext(course);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response.code() == 400) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("VerifyOTPResponse--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("VerifyOTPResponse--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));

                }

                e.onComplete();
            }
        });
    }

    public Observable<Float> checkForNewVersionOnPlayStore() {

        return
                io.reactivex.Observable.create(new ObservableOnSubscribe<Float>() {
                    @Override
                    public void subscribe(ObservableEmitter<Float> e) throws Exception {
                        String newVersion = Jsoup.connect(
                                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "&hl=en")
                                .timeout(30000)
                                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                .referrer("http://www.google.com")
                                .get()
                                .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                                .first()
                                .ownText();
                        e.onNext(Float.parseFloat(newVersion));
                        e.onComplete();
                        Log.e("playStoreVersion--", newVersion);


                    }
                });
    }

    /*To play video*/
    public void playVideo(Resource resource) {

        String type = resource.getType();

        String url = "";
        if (!TextUtils.isEmpty(resource.getUrl())) {
            url = resource.getUrl();
        } else if (!TextUtils.isEmpty(resource.getUrlMain())) {
            url = resource.getUrlMain();
        } else if (!TextUtils.isEmpty(resource.getSourceURL())) {
            url = resource.getSourceURL();
        }
        if (TextUtils.isEmpty(type)) {
            if (url.contains(mContext.getString(R.string.typeVimeoVideo))) {
                type = mContext.getString(R.string.typeVimeoVideo);
            } else if (url.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")) {
                type = mContext.getString(R.string.typeYouTubeVideo);
            } else if (url.contains(mContext.getString(R.string.typeVideo))) {
                type = mContext.getString(R.string.typeVideo);
            } else if (url.contains("youtu.be") || url.contains("youtube.com")) {
                type = mContext.getString(R.string.typeYouTubeVideo);
            } else {
                if (url.contains(mContext.getString(R.string.typeVimeoVideo))) {
                    type = mContext.getString(R.string.typeVimeoVideo);
                } else if (url.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")) {
                    type = mContext.getString(R.string.typeYouTubeVideo);
                } else if (url.contains(mContext.getString(R.string.typeVideo))) {
                    type = mContext.getString(R.string.typeVideo);
                } else if (url.contains("youtu.be") || url.contains("youtube.com")) {
                    type = mContext.getString(R.string.typeYouTubeVideo);
                } else {
                    type = mContext.getString(R.string.typeVideo);
                }

            }
        }

        if (type.equalsIgnoreCase(mContext.getString(R.string.typeVideo))) {
            Resource item = new Resource();
            item.setType(mContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, item));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeYouTubeVideo))) {
            if (!url.contains("https:") && !url.startsWith("www")) {
                FavouriteResource favouriteResource = new FavouriteResource();
                favouriteResource.setName(url);
                favouriteResource.setUrlThumbnail("");
                mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
            } else {
                String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

                Pattern compiledPattern = Pattern.compile(pattern);
                Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
                if (matcher.find()) {
                    String videoId = matcher.group();
                    FavouriteResource favouriteResource = new FavouriteResource();
                    favouriteResource.setName(videoId);
                    favouriteResource.setUrlThumbnail("");
                    mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
                }
            }


        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeVimeoVideo))) {
            //mContext.startActivity(PlayVimeoFullScreenActivity.getStartIntent(mContext, moduleId, moduleName, resourceId, url));
        } else {
            Resource item = new Resource();
            item.setType(mContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        }
    }

    /*To get today recaps*/
    public Observable<ArrayList<LessonPlanMinimal>> getTodayRecaps() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<LessonPlanMinimal>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<LessonPlanMinimal>> e) throws Exception {
                Call<ArrayList<LessonPlanMinimal>> call = mNetworkModel.getTodayRecap();
                Response<ArrayList<LessonPlanMinimal>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanMinimal", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<LessonPlanMinimal>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanMinimal", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanMinimal", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanMinimal", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    public Observable<ArrayList<LessonPlanChapterResult>> getChaptersResult() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<LessonPlanChapterResult>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<LessonPlanChapterResult>> e) throws Exception {
                Call<ArrayList<LessonPlanChapterResult>> call = mNetworkModel.getChaptersResult();
                Response<ArrayList<LessonPlanChapterResult>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanChapterResult", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<LessonPlanChapterResult>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanChapterResult", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanChapterResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanChapterResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To get subjects of logged in user*/
    public Observable<LessonPlanSubjectResult> getMySubject() {
        return Observable.create(new ObservableOnSubscribe<LessonPlanSubjectResult>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanSubjectResult> e) throws Exception {

                Call<LessonPlanSubjectResult> call = mNetworkModel.getMySubject(new LessonPlanSubjectPost(mContext.getString(R.string.subject).toLowerCase()));
                Response<LessonPlanSubjectResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanSubject", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LessonPlanSubjectResult> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanSubject", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanSubject", "Failed");
                        Log.e("LessonPlanSubject", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanSubject", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    public Observable<AssignedHomeworkParent> fetchHomeworkCount(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<AssignedHomeworkParent>() {
            @Override
            public void subscribe(ObservableEmitter<AssignedHomeworkParent> e) throws Exception {

                Call<AssignedHomeworkParent> call = mNetworkModel.fetchHomework(subjectId);
                Response<AssignedHomeworkParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("fetchHomework", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<AssignedHomeworkParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("fetchHomeworkCount", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("fetchHomeworkCount", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    /*Method to send status of application for various user activity*/
    @SuppressLint("CheckResult")
    public void checkUserStatus(final String status) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                @Override
                public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {

                    Call<ResponseBody> call = mNetworkModel.checkUserStatus(status);
                    Response<ResponseBody> response = call.execute();

                    if (response != null && response.isSuccessful()) {
                        Log.e("checkUserStatus", "Successful");
                        e.onNext(response.body());
                    } else if (response.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                        Response<ResponseBody> response2 = call.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            Log.e("checkUserStatus", "Successful");
                            e.onNext(response2.body());
                        } else if (response2.code() == 401) {
                            mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                        } else if (response2.code() == 404) {
                            throw new Exception("Failed");
                        }
                    } else {
                        Log.e("checkUserStatus", "Failed");
                        throw new Exception("Failed");
                    }

                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Exception {

                            Log.e("userStatusTypeSuccess--", status);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();

                        }
                    });
        }

    }

    /*To fetch effort (time spent) data for all subjects*/
    public Observable<ArrayList<EffortvsPerformanceData>> fetchEffortvsPerformanceData() {

        return Observable.create(new ObservableOnSubscribe<ArrayList<EffortvsPerformanceData>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<EffortvsPerformanceData>> e) throws Exception {
                Call<ArrayList<EffortvsPerformanceData>> call = mNetworkModel.fetchEffortvsPerformanceData();
                Response<ArrayList<EffortvsPerformanceData>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("EffortChartData", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<EffortvsPerformanceData>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("EffortChartData", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("EffortChartData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*To get list of wikiHow cards by topicIds*/
    public Observable<ArrayList<WikiHowParent>> getWikiHowData(final ArrayList<String> topicIds) {

        return Observable.create(new ObservableOnSubscribe<ArrayList<WikiHowParent>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<WikiHowParent>> e) throws Exception {
                ArrayList<WikiHowParent> list = new ArrayList<>();

                for (int i = 0; i < topicIds.size(); i++) {
                    Call<WikiHowParent> call = mNetworkModel.fetchWikiHowCardDetail(topicIds.get(i));
                    Response<WikiHowParent> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        Log.e("WikiHowData", "Successful");
                        list.add(response.body());
                    } else {
                        Log.e("WikiHowData", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                }
                if (!list.isEmpty()) {
                    e.onNext(list);

                } else {
                    e.onError(new Throwable(mContext.getString(R.string.messageNoDataFound)));
                }

                e.onComplete();
            }
        });

    }

    /*To fetch challenge for the day on dashboard*/
    public Observable<LogiQidsChallengeParent> fetchChallengeForTheDay(final String typeChallengeLogiqids) {

        return
                Observable.create(new ObservableOnSubscribe<LogiQidsChallengeParent>() {
                    @Override
                    public void subscribe(ObservableEmitter<LogiQidsChallengeParent> e) throws Exception {
                        Call<LogiQidsChallengeParent> call = mNetworkModel.fetchChallengeForTheDay(typeChallengeLogiqids);
                        Response<LogiQidsChallengeParent> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            LogiQidsChallengeParent body = response.body();
                            Log.e("ChallengeLQ1--", "Successful");
                            e.onNext(body);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<LogiQidsChallengeParent> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                LogiQidsChallengeParent body2 = response2.body();
                                Log.e("ChallengeLQ2--", "Successful");
                                e.onNext(body2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("ChallengeLQ2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("ChallengeLQ1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To fetch video for the day on Dashboard*/
    public Observable<VideoForDayParent> fetchVideoForTheDay(final String typeVideoPerDay) {

        return
                Observable.create(new ObservableOnSubscribe<VideoForDayParent>() {
                    @Override
                    public void subscribe(ObservableEmitter<VideoForDayParent> e) throws Exception {
                        Call<VideoForDayParent> call = mNetworkModel.fetchVideoForTheDay(typeVideoPerDay);
                        Response<VideoForDayParent> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            VideoForDayParent body = response.body();
                            Log.e("VideoFD1--", "Successful");
                            e.onNext(body);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<VideoForDayParent> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                VideoForDayParent body = response2.body();
                                Log.e("VideoFD2--", "Successful");
                                e.onNext(body);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("VideoFD2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("VideoFD1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To upload data for Take a Challenge Or Video for day of student when join/complete*/
    public Observable<ResponseBody> uploadTakeChallengeOrVideo(final UserChallengePost post, final int status) {

        return
                Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                    @Override
                    public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                        Call<ResponseBody> call = mNetworkModel.uploadTakeChallengeOrVideo(post, status);
                        Response<ResponseBody> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            ResponseBody body = response.body();
                            Log.e("uploadChallengeVideo1--", "Successful");
                            e.onNext(body);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ResponseBody> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ResponseBody body = response2.body();
                                Log.e("uploadChallengeVideo2--", "Successful");
                                e.onNext(body);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("uploadChallengeVideo2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("uploadChallengeVideo1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To fetch vocational subject on dashboard(for now, name = life skill)
     * To fetch this post object should be empty*/
    public Observable<ArrayList<VocationalSubject>> fetchVocationalSubject(final VocationalTopicRequest vocationalTopicRequest) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<VocationalSubject>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<VocationalSubject>> e) throws Exception {
                        Call<ArrayList<VocationalSubject>> call = mNetworkModel.fetchVocationalSubject(vocationalTopicRequest);
                        Response<ArrayList<VocationalSubject>> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            ArrayList<VocationalSubject> list = response.body();
                            Log.e("VocationalSubject1--", "Successful");
                            e.onNext(list);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<VocationalSubject>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<VocationalSubject> list2 = response2.body();
                                Log.e("VocationalSubject2--", "Successful");
                                e.onNext(list2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("VocationalSubject2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("VocationalSubject1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To fetch vocational topic (for now logiqids)*/
    public Observable<ArrayList<VocationalTopic>> fetchVocationalTopics(final VocationalTopicRequest topicRequest) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<VocationalTopic>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<VocationalTopic>> e) throws Exception {
                        Call<ArrayList<VocationalTopic>> call = mNetworkModel.fetchVocationalTopics(topicRequest);
                        Response<ArrayList<VocationalTopic>> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            ArrayList<VocationalTopic> list = response.body();
                            Log.e("VocationalTopics1--", "Successful");
                            e.onNext(list);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<VocationalTopic>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<VocationalTopic> list2 = response2.body();
                                Log.e("VocationalTopics2--", "Successful");
                                e.onNext(list2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("VocationalTopics2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("VocationalTopics1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To fetch help and faq data*/
    public Observable<ArrayList<HelpAndFaqCategory>> fetchHelpAndFAQ() {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<HelpAndFaqCategory>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<HelpAndFaqCategory>> e) throws Exception {

                        Call<ArrayList<HelpAndFaqCategory>> call = mNetworkModel.fetchHelpAndFAQ();
                        Response<ArrayList<HelpAndFaqCategory>> response = call.execute();

                        if (response != null && response.isSuccessful()) {
                            ArrayList<HelpAndFaqCategory> list = response.body();
                            Log.e("HelpAndFAQ1--", "Successful");
                            e.onNext(list);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<HelpAndFaqCategory>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<HelpAndFaqCategory> list2 = response2.body();
                                Log.e("HelpAndFAQ2--", "Successful");
                                e.onNext(list2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("HelpAndFAQ2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("HelpAndFAQ1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To fetch revision subjects with details*/
    public Observable<ArrayList<RevisionSubject>> fetchRevisionSubjects() {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<RevisionSubject>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<RevisionSubject>> e) throws Exception {

                        Call<ArrayList<RevisionSubject>> call = mNetworkModel.fetchRevisionSubjects();
                        Response<ArrayList<RevisionSubject>> response = call.execute();

                        if (response != null && response.isSuccessful()) {
                            ArrayList<RevisionSubject> list = response.body();
                            Log.e("RevisionSubjects1--", "Successful");
                            e.onNext(list);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<RevisionSubject>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<RevisionSubject> list2 = response2.body();
                                Log.e("RevisionSubjects2--", "Successful");
                                e.onNext(list2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("RevisionSubjects2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("RevisionSubjects1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

}
