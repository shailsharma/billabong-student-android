package in.securelearning.lil.android.syncadapter.model;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.BookAnnotation;
import in.securelearning.lil.android.base.dataobjects.UserRating;
import in.securelearning.lil.android.base.dataobjects.WebQuizResponse;
import in.securelearning.lil.android.base.interfaces.WebPlayerLiveModelInterface;
import in.securelearning.lil.android.base.model.BlogCommentModel;
import in.securelearning.lil.android.base.model.BlogModel;
import in.securelearning.lil.android.base.model.WebQuizResponseModel;
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
    public Response<ResponseBody> saveBookAnnotation(BookAnnotation bookAnnotation) {
        return null;
    }

    public Response<ResponseBody> uploadQuizResponse(String response1) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), response1);
        return webCallExecutor(mNetworkModel.uploadQuizResponse(body));
    }

    @Override
    public Response<ResponseBody> uploadAnnotation(JSONObject s) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), s.toString());
        return webCallExecutor(mNetworkModel.uploadAnnotation(body));

    }

    public Response<ResponseBody> deleteAnnotation(String id) {
        return webCallExecutor(mNetworkModel.deleteAnnotation(id));

    }

    @Override
    public void updateDigitalBook(String objectId) {
        mSyncServiceModel.createInternalNotificationForBookUpdate(objectId, ACTION_TYPE_NETWORK_DOWNLOAD, true);
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
