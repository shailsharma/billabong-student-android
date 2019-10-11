package in.securelearning.lil.android.syncadapter.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserRating;
import in.securelearning.lil.android.base.dataobjects.WebQuizResponse;
import in.securelearning.lil.android.base.interfaces.WebPlayerLiveModelInterface;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.BlogCommentModel;
import in.securelearning.lil.android.base.model.BlogModel;
import in.securelearning.lil.android.base.model.WebQuizResponseModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerLiveActivity;
import in.securelearning.lil.android.home.views.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.QuizPlayerActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
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
    BlogCommentModel mBlogCommentModel;
    @Inject
    BlogModel mBlogModel;
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

    public Response<ResponseBody> getBlogDetails(String id) {
        return webCallExecutor(mNetworkModel.fetchBlogDetails(id));
    }

    public Response<ResponseBody> getBlogUserDetails(String id) {
        return webCallExecutor(mNetworkModel.fetchBlogUserDetails(id));
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

    public Response<ResponseBody> getYoutubeVideoDuration(String id) {
        return webCallExecutor(mNetworkModel.fetchYoutubeVideoDuration(id));
    }

    public void saveComment(BlogComment blogComment1) {
        mBlogCommentModel.saveObject(blogComment1);
        SyncService.startActionUploadBlogComment(mContext, blogComment1.getAlias());
    }

    public ArrayList<BlogComment> getCommentsByBlogId(String id) {
        return mBlogCommentModel.getListByBlogId(id);
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
    public void updateDigitalBook(String objectId) {
        mSyncServiceModel.createInternalNotificationForBookUpdate(objectId, ACTION_TYPE_NETWORK_DOWNLOAD, true);
    }

    @Override
    public void playMedia(String type, String url) {

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
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        } else if (type.contains("you") || type.contains("vimeo")) {

            WebPlayerLiveActivity.startWebPlayerForResourcePreview(mContext, mAppUserModel.getObjectId(), url);

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

    public boolean addBlogRating(UserRating rating, String id) {
        boolean done = false;
        BlogDetails blogDetails = mBlogModel.getBlogDetailsFromUidSync(id);
        if (blogDetails.getObjectId().equals(id)) {

            for (UserRating rating1 :
                    blogDetails.getBlogReviewInstance().getUserRatings()) {
                if (rating1.getUserId().equals(rating.getUserId())) {
                    rating1.setRating(rating.getRating());
                    done = true;
                    break;
                }
            }
            if (!done) {
                blogDetails.getBlogReviewInstance().getUserRatings().add(rating);
                done = true;
            }

            mBlogModel.saveBlogDetails(blogDetails);
        }
        return done;
    }

}
