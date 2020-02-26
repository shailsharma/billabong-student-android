package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an postData from server
 */
public class DownloadPostDataJsonJob extends BaseDownloadJob<PostData> {
    private final String TAG = this.getClass().getCanonicalName();
    private boolean mShouldShowNotification;

    public DownloadPostDataJsonJob(String objectId) {
        super(objectId);
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadPostDataJsonJob(String objectId, String notificationId, boolean shouldShowNotification) {
        super(objectId, notificationId);
        mShouldShowNotification = shouldShowNotification;
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public PostData get(String objectId) {
        return mJobModel.fetchPostDataFromObjectId(objectId);
    }

    @Override
    public void actionFetchSuccess(PostData postData) {
        if (postData.getPostType().equals(PostDataType.TYPE_TRACKING.toString())) {
            postData = setSyncStatus(postData, SyncStatus.COMPLETE_SYNC);
        } else {
            /*set sync status of the object to json sync*/
            postData = setSyncStatus(postData, SyncStatus.JSON_SYNC);
        }

        /*save the object into database*/
        postData = save(postData);
        if (notificationId != null && !notificationId.isEmpty()) {
            updateNotificationStatus(notificationId, SyncStatus.COMPLETE_SYNC.toString());
        }
        /*create validation job for the download object*/
        if (this.isValidationEnabled) createValidationJobs(postData);


    }

    /**
     * create validation job for the postData
     *
     * @param postData to validate
     */
    @Override
    public void createValidationJobs(PostData postData) {
        /*create job to validate the downloaded postData*/
        JobCreator.createPostDataValidationJob(postData, mShouldShowNotification).execute();
    }

    /**
     * network call to fetch postData
     *
     * @param objectId id of the postData to fetch
     * @return call : the network call to fetch postData
     */
    @Override
    public Call<PostData> fetchFromNetwork(String objectId) {
        /*fetch PostData from network*/
        return mNetworkModel.fetchLearningNetworkPostData(objectId);
    }

    /**
     * persist postData
     *
     * @param postData to persist
     * @return the persisted postData
     */
    @Override
    public PostData save(PostData postData) {
        /*save postData to database*/
        return mJobModel.saveLearningNetworkPostData(postData);
    }

    /**
     * get the list of object id in the postData
     *
     * @param postData containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(PostData postData) {

        /*get id from the postData*/
        return Collections.singletonList(postData.getObjectId());
    }


}
