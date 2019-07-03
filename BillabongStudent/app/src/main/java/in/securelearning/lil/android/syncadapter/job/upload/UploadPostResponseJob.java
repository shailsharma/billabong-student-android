package in.securelearning.lil.android.syncadapter.job.upload;

import android.util.Log;

import java.io.IOException;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Post Assignment Response Job.
 */
public class UploadPostResponseJob extends BaseUploadJob<PostResponse> {
    public final String TAG = this.getClass().getCanonicalName();

    public UploadPostResponseJob(PostResponse dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute uploading of assignment response
     */
    public void execute() {
        try {

            /*upload data object*/
            Response<PostResponse> response = uploadJsonToServer(mDataObject).execute();

            /*if upload is successful*/
            if (response.isSuccessful()) {
                /*retrieve assignment response*/
                PostResponse postResponse = response.body();

                Log.e(TAG, " PostResponse  posted : " + postResponse.getObjectId());

                /*set sync status*/
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                mDataObject.setObjectId(postResponse.getObjectId());

                /*save json to database*/
                saveJsonToDatabase(mDataObject);
            } else if (response.code() == 422 || response.code() == 500) {

                /*upload data object*/
                Response<PostResponse> response2 = fetchByAlias(mDataObject.getAlias()).execute();

                /*if upload is successful*/
                if (response2.isSuccessful()) {
                    /*retrieve assignment response*/
                    PostResponse postResponse = response2.body();

                    Log.e(TAG, " PostResponse  posted : " + postResponse.getObjectId());

                    /*set sync status*/
                    mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                    /*set object id*/
                    mDataObject.setObjectId(postResponse.getObjectId());

                    /*save json to database*/
                    saveJsonToDatabase(mDataObject);
                }
            } else if (response.code() == 401 && mLoginAttempts < MAX_LOGIN_ATTEMPT) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    mLoginAttempts++;
                    execute();
                }
            } else if (response.code() == 401 && mLoginAttempts >= MAX_LOGIN_ATTEMPT) {
                // TODO: 07-03-2017 redirect to refreshToken page
            } else {
                Log.e(TAG, "PostResponse Res err " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public void execute() {
//        if (TextUtils.isEmpty(mDataObject.getObjectId())) {
//            mDataObject.setObjectId(UUID.randomUUID().toString());
//        }
//
//        try {
//            Response<MessageResponse> response = uploadJsonToServerFCM(mDataObject).execute();
//            if (response.isSuccessful()) {
//                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
//                saveJsonToDatabase(mDataObject);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return null;
    }

    @Override
    protected CharSequence getStartNotificationText() {
        return null;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getFailedNotificationText() {
        return null;
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return null;
    }

    @Override
    protected int getProgressCountMax() {
        return 0;
    }

    @Override
    protected boolean isIndeterminate() {
        return false;
    }

    @Override
    protected boolean isNotificationEnabled() {
        return false;
    }

    @Override
    public int getNotificationResourceId() {
        return 0;
    }

    private Call<MessageResponse> uploadJsonToServerFCM(PostResponse postResponse) {
        return mNetworkModel.sendDataUsingFCM(((PostResponse) postResponse).getGroupId(), postResponse, NetworkModel.TYPE_POST_RESPONSE, postResponse.getObjectId(), postResponse.getCreatedTime());
    }

    private Call<MessageResponse> uploadJsonToServerFCM(String groupId, String objectId, String isoDate) {
        return mNetworkModel.sendDataUsingFCM(groupId, "New Post Response", NetworkModel.TYPE_POST_RESPONSE, objectId, isoDate);
    }

    /**
     * upload assignment response json to network
     *
     * @param postResponse to upload
     * @return network call
     */
    public Call<PostResponse> uploadJsonToServer(PostResponse postResponse) {
        /*network call to post assignment response*/

        Call<PostResponse> postResponseCall = mNetworkModel.uploadPostResponse(postResponse);
        return postResponseCall;
    }

    public Call<PostResponse> fetchByAlias(String alias) {
        /*network call to post learning network post data */
        return mNetworkModel.fetchByAliasLearningNetworkPostResponse(alias);
    }

    /**
     * save postResponse response to database
     *
     * @param postResponse to save
     */
    public void saveJsonToDatabase(PostResponse postResponse) {
        /*save postResponse response*/
        mJobModel.savePostResponse(postResponse);
//        try {
//            Response<MessageResponse> response = uploadJsonToServerFCM(postResponse.getGroupId(), postResponse.getObjectId(), postResponse.getCreatedTime()).execute();
//            if (response.isSuccessful()) {
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
