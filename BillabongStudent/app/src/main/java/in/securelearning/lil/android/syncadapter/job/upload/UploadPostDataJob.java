package in.securelearning.lil.android.syncadapter.job.upload;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Learning Network Post Data Job.
 *
 * @author Pushkar Raj
 */
public class UploadPostDataJob extends BaseUploadJob<PostData> {
    public final String TAG = this.getClass().getCanonicalName();

    public UploadPostDataJob(PostData dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute uploading of Post data
     */
    public void execute() {
         /*post the resource in post*/
        UploadPostDataResourcesJob job = new UploadPostDataResourcesJob(mDataObject) {
            @Override
            public void onComplete() {
                try {
                    actionResourceUploadComplete();
//                    actionResourceUploadCompleteFCM();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
                /*execute the job*/
        job.execute();

    }

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

    private void actionResourceUploadCompleteFCM() {
        if (TextUtils.isEmpty(mDataObject.getObjectId())) {
            mDataObject.setObjectId(UUID.randomUUID().toString());
        }
        try {
            Response<MessageResponse> response = uploadJsonToServerFCM(mDataObject).execute();
            if (response.isSuccessful()) {
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                saveJsonToDatabase(mDataObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * fetch PostData using alias
     *
     * @param alias of the PostData
     * @return PostData
     */
    public PostData getPostDataFromAlias(String alias) {
        /*fetch quiz using alias*/
        return mJobModel.fetchPostDataFromAlias(alias);

    }

    private void actionResourceUploadComplete() throws IOException {

        //Sending UrlMain to server
        mDataObject.setPostResources(mDataObject.getPostResources());

            /*upload data object*/
        Response<PostData> response = uploadJsonToServer(mDataObject).execute();

            /*if upload is successful*/
        if (response.isSuccessful()) {
                /*retrieve assignment response*/
            PostData postData = response.body();

            Log.e(TAG, "Learning Network Post posted : " + postData.getObjectId());

                /*set sync status*/
            mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
            mDataObject.setObjectId(postData.getObjectId());

                /*save json to database*/
            saveJsonToDatabase(mDataObject);
        } else if (response.code() == 422 || response.code() == 500) {
            Response<PostData> response2 = fetchByAlias(mDataObject.getAlias()).execute();



            /*if upload is successful*/
            if (response2.isSuccessful()) {

                /*retrieve assignment response*/
                PostData postData = response2.body();

                Log.e(TAG, "Learning Network Post posted : " + postData.getObjectId());

                /*set sync status*/
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                mDataObject.setObjectId(postData.getObjectId());

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
            Log.e(TAG, "Network Post Res err " + response.message() + response.code());
        }
    }

    /**
     * upload learning network post data json to network
     *
     * @param postData to upload
     * @return network call
     */
    public Call<PostData> uploadJsonToServer(PostData postData) {
        /*network call to post learning network post data */
        return mNetworkModel.postLearningNetworkPostData(postData);
    }

    public Call<MessageResponse> uploadJsonToServerFCM(PostData postData) {
        /*network call to post learning network post data */
        return mNetworkModel.sendDataUsingFCM(postData.getTo().getId(), postData, NetworkModel.TYPE_POST_DATA, postData.getObjectId(), postData.getCreatedTime());
    }

    public Call<MessageResponse> uploadJsonToServerFCM(String groupId, String objectId, String isoDate) {
        /*network call to post learning network post data */
        return mNetworkModel.sendDataUsingFCM(groupId, "New Post", NetworkModel.TYPE_POST_DATA, objectId, isoDate);
    }

    public Call<PostData> fetchByAlias(String alias) {
        /*network call to post learning network post data */
        return mNetworkModel.getLearningNetworkPostData(alias);
    }

    /**
     * save learning network post data to database
     *
     * @param postData to save
     */
    public void saveJsonToDatabase(PostData postData) {
        /*save assignment response*/
        mJobModel.saveLearningNetworkPostData(postData);
        try {
            Response<MessageResponse> response = uploadJsonToServerFCM(postData.getTo().getId(), postData.getObjectId(), postData.getCreatedTime()).execute();

            if (response.isSuccessful()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
