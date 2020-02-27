package in.securelearning.lil.android.syncadapter.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserRating;
import in.securelearning.lil.android.base.dataobjects.WebQuizResponse;
import in.securelearning.lil.android.base.interfaces.WebPlayerLiveModelInterface;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.WebQuizResponseModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.view.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.player.view.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.QuizPlayerActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.UserTimeSpent;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_NETWORK_DOWNLOAD;

/**
 * Created by Prabodh Dhabaria on 06-12-2016.
 */

public class WebPlayerLiveModel implements WebPlayerLiveModelInterface {
    @Inject
    Context mContext;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    WebQuizResponseModel mWebQuizResponseModel;
    @Inject
    SyncServiceModel mSyncServiceModel;
    @Inject
    AppUserModel mAppUserModel;

    public WebPlayerLiveModel() {
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public Response<ResponseBody> getQuiz(String id) {
        return webCallExecutor(mNetworkModel.fetchQuizWeb(id));
    }

    public Response<ResponseBody> webCallExecutor(Call<ResponseBody> call) {
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                return response;
            } else {
                final int code = response.code();
                if (code == 401) {
                    if (SyncServiceHelper.refreshToken(mContext)) {
                        response = call.clone().execute();
                        if (response.isSuccessful()) {
                            return response;
                        } else {
                            return response;
                        }
                    }
                } else {
                    return response;
                }
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response<ResponseBody> getDigitalBook(String id) {
        return webCallExecutor(mNetworkModel.fetchDigitalBook2(id));
    }

    public Response<ResponseBody> getCustomSection(String id) {
        return webCallExecutor(mNetworkModel.fetchCustomSection(id));
    }

    @Override
    public Response<ResponseBody> getBlogDetails(String s) {
        return null;
    }

    @Override
    public Response<ResponseBody> getBlogUserDetails(String s) {
        return null;
    }

    public Response<ResponseBody> getConceptMap(String id) {
        return webCallExecutor(mNetworkModel.fetchConceptMap(id));
    }

    public Response<ResponseBody> getConceptMapEs(String id) {
        return webCallExecutor(mNetworkModel.fetchConceptMapEs(id));
    }

    public Response<ResponseBody> getInteractiveImage(String id) {
        return webCallExecutor(mNetworkModel.fetchInteractiveImage(id));
    }

    public Response<ResponseBody> getInteractiveImageEs(String id) {
        return webCallExecutor(mNetworkModel.fetchInteractiveImageEs(id));
    }

    public Response<ResponseBody> getVideoCourse(String id) {
        return webCallExecutor(mNetworkModel.fetchVideoCourse(id));
    }

    public Response<ResponseBody> getInteractiveVideo(String id) {
        return webCallExecutor(mNetworkModel.fetchInteractiveVideo(id));
    }

    public Response<ResponseBody> getInteractiveVideoEs(String id) {
        return webCallExecutor(mNetworkModel.fetchInteractiveVideoEs(id));
    }

    public Response<ResponseBody> getPopUps(String id) {
        return webCallExecutor(mNetworkModel.fetchPopUps(id));
    }

    public Response<ResponseBody> getPopUpsEs(String id) {
        return webCallExecutor(mNetworkModel.fetchPopUpsEs(id));
    }

    @Override
    public void saveComment(BlogComment blogComment) {

    }

    @Override
    public ArrayList<BlogComment> getCommentsByBlogId(String s) {
        return null;
    }

    public Response<ResponseBody> getYoutubeVideoDuration(String id) {
        return webCallExecutor(mNetworkModel.fetchYoutubeVideoDuration(id));
    }

    @Override
    public void saveQuizResponse(WebQuizResponse webQuizResponse) {
        mWebQuizResponseModel.saveObject(webQuizResponse);
    }

    @Override
    public Response<ResponseBody> saveBookmark(JSONObject jsonObject) {
        RequestBody body = GeneralUtils.jsonToRequestBody(jsonObject.toString());
        return webCallExecutor(mNetworkModel.saveBookmark(body));
    }

    public Response<ResponseBody> uploadQuizResponse(String response1) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), response1);
        return webCallExecutor(mNetworkModel.uploadQuizResponse(body));
    }

    @Override
    public Response<ResponseBody> uploadAnnotation(JSONObject s) {
        RequestBody body = GeneralUtils.jsonToRequestBody(s.toString());
        return webCallExecutor(mNetworkModel.uploadAnnotation(body));

    }

    public Response<ResponseBody> deleteAnnotation(String id) {
        return webCallExecutor(mNetworkModel.deleteAnnotation(id));

    }

    @Override
    public boolean addBlogRating(UserRating userRating, String s) {
        return false;
    }

    @Override
    public void updateDigitalBook(String objectId) {
        mSyncServiceModel.createInternalNotificationForBookUpdate(objectId, ACTION_TYPE_NETWORK_DOWNLOAD, true);
    }

    @Override
    public void playMedia(String moduleId, String moduleName, String type, String resourceId, String url) {

        if (type.equalsIgnoreCase(mContext.getString(R.string.typePdf))) {
            try {
                if (!TextUtils.isEmpty(url)) {

                    url = url.trim();
                    if (url.endsWith(".pdf")) {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                        Intent chooser = Intent.createChooser(intent, "Choose");
                        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(chooser);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeImage))) {
            mContext.startActivity(PlayFullScreenImageActivity.getStartIntent(mContext, url, true));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeVideo))) {
            Resource item = new Resource();
            item.setType(mContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            item.setObjectId(resourceId);
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, moduleId, moduleName, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, item));
        } else if (type.contains(mContext.getString(R.string.typeVimeoVideo))) {
            mContext.startActivity(PlayVimeoFullScreenActivity.getStartIntent(mContext, moduleId, moduleName, resourceId, url));
        } else {
            GeneralUtils.showToastShort(mContext, mContext.getString(R.string.error_something_went_wrong));
        }
    }

    @Override
    public Response<ResponseBody> getActivityChecklistJson(String id) {
        return webCallExecutor(mNetworkModel.getActivityChecklistJson(id));

    }

    @Override
    public Response<ResponseBody> savePopupActivity(JSONObject jsonObject) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return webCallExecutor(mNetworkModel.savePopupActivity(body));
    }

    @Override
    public void previewQuizPlayer(String courseId, String courseType, String quizId) {
        mContext.startActivity(QuizPlayerActivity.getStartIntent(mContext, quizId, courseId, courseType));

    }

    @Override
    public Response<ResponseBody> getReportByQuizId(JSONObject jsonObject) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return webCallExecutor(mNetworkModel.getReportByQuizId(body));
    }

    @Override
    public Response<ResponseBody> dictionariesSearch(String response) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), response);
        return webCallExecutor(mNetworkModel.dictionariesSearch(body));
    }

    @Override
    public Response<ResponseBody> getAflAolConfiguration(String response) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), response);
        return webCallExecutor(mNetworkModel.getAflAolConfiguration(body));
    }

    @Override
    public void openBlogCommentView(String blogId) {

    }

    @Override
    public Response<ResponseBody> saveUserTimeSpentProgress(String response) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), response);
        return webCallExecutor(mNetworkModel.saveUserTimeSpentProgress(body));
    }

    @SuppressLint("CheckResult")
    @Override
    public void uploadUserTimeSpent(final String moduleId, final String moduleName, final long startTime, final long endTime) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                @Override
                public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {

                    UserTimeSpent userTimeSpent = new UserTimeSpent();
                    userTimeSpent.setStartTime(startTime);
                    userTimeSpent.setEndTime(endTime);
                    userTimeSpent.setResourceId(null);
                    userTimeSpent.setResourceName(null);
                    userTimeSpent.setModuleId(moduleId);
                    userTimeSpent.setModuleName(moduleName);

                    Call<ResponseBody> call = mNetworkModel.uploadUserTimeSpent(userTimeSpent);
                    Response<ResponseBody> response = call.execute();
                    if (response != null) {
                        int code = response.code();
                        if (response.isSuccessful()) {
                            Log.e("UserTimeSpent--", "Successful");
                        } else if (code == 404) {
                            throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                        } else if ((code == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ResponseBody> response2 = call.clone().execute();
                            if (response2 != null) {

                                int code2 = response2.code();

                                if (response2.isSuccessful()) {
                                    ResponseBody body = response2.body();
                                    Log.e("UserTimeSpent--", "Successful");
                                    e.onNext(body);
                                } else if ((code2 == 401)) {
                                    mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                                } else if (code2 == 404) {
                                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                                } else {
                                    Log.e("UserTimeSpent--", "Failed");
                                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                                }
                            }
                        } else {
                            Log.e("UserTimeSpent--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                        }
                    }

                    e.onComplete();

                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
        }

    }


    @Override
    public Response<ResponseBody> fetchGlobalConfigs(String response) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), response);
        return webCallExecutor(mNetworkModel.fetchGlobalConfigs(body));
    }

    @Override
    public Response<ResponseBody> fetchKhanAcademyVideoDetail(String response) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), response);
        return webCallExecutor(mNetworkModel.fetchKhanAcademyVideoDetail(body));
    }

    @Override
    public Response<ResponseBody> saveQuestionFeedback(String response) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), response);
        return webCallExecutor(mNetworkModel.saveQuestionFeedback(body));
    }

    @Override
    public Response<ResponseBody> uploadVideoStartStatus(String videoId, String moduleId) {
        return webCallExecutor(mNetworkModel.uploadVideoWatchStarted(videoId, moduleId));
    }

    @Override
    public Response<ResponseBody> uploadVideoEndStatus(String videoId, String moduleId) {
        return webCallExecutor(mNetworkModel.uploadVideoWatchEnded(videoId, moduleId));
    }


    @Override
    public Response<ResponseBody> fetchAssessmentConfiguration(String s) {
        return null;
    }

    @Override
    public Response<ResponseBody> fetchAssessmentExam(String s) {
        return null;
    }

    @Override
    public Response<ResponseBody> submitAssessmentExam(String s) {
        return null;
    }

    @Override
    public Response<ResponseBody> uploadPartialAssessmentResponse(String s) {
        return null;
    }

    @Override
    public Response<ResponseBody> fetchAssessmentReport(String s) {
        return null;
    }

    @Override
    public Response<ResponseBody> fetchCourseProgress(String id) {
        return webCallExecutor(mNetworkModel.fetchCourseProgress(id));
    }

    @Override
    public Response<ResponseBody> saveCourseProgress(String s) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), s);
        return webCallExecutor(mNetworkModel.saveCourseProgress(body));
    }

}
