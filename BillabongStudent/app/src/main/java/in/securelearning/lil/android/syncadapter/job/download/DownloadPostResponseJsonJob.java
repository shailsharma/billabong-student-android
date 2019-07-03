package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an postResponse from server
 */
public class DownloadPostResponseJsonJob extends BaseDownloadJob<PostResponse> {
    private final String TAG = this.getClass().getCanonicalName();
    private boolean mShouldShowNotification;

    public DownloadPostResponseJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadPostResponseJsonJob(String objectId, String notificationId, boolean shouldShowNotification) {
        super(objectId, notificationId);
        mShouldShowNotification = shouldShowNotification;

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public PostResponse get(String objectId) {
        return mJobModel.fetchPostResponseFromObjectId(objectId);
    }

    @Override
    public void actionFetchSuccess(PostResponse postResponse) {
        if (postResponse.getType().equals(PostResponseType.TYPE_TRACKING.toString())) {
            postResponse = setSyncStatus(postResponse, SyncStatus.COMPLETE_SYNC);
        } else {
            /*set sync status of the object to json sync*/
            postResponse = setSyncStatus(postResponse, SyncStatus.JSON_SYNC);
        }

        if (postResponse.getType().equals(PostResponseType.TYPE_TRACKING.toString())) {
//            if (!DateUtils.convertrIsoDate(postResponse.getCreatedTime()).before(new Date(DateUtils.getSecondsForMorningFromDate(new Date()) * 1000))) {
//                mRxBus.send(new TrackingPostEvent(postResponse.getObjectId(), postResponse.getPostID(), postResponse.getClass(), DateUtils.convertrIsoDate(postResponse.getCreatedTime()), postResponse.getText()));
//            }
            if (notificationId != null && !notificationId.isEmpty()) {
                // sendSyncSuccessToServer(Collections.singletonList(notificationId));
                updateNotificationStatus(notificationId, SyncStatus.COMPLETE_SYNC.toString());
            }
        } else {
            /*save the object into database*/
            postResponse = save(postResponse);
            if (notificationId != null && !notificationId.isEmpty()) {
                // sendSyncSuccessToServer(Collections.singletonList(notificationId));
                updateNotificationStatus(notificationId, SyncStatus.COMPLETE_SYNC.toString());
            }
            /*create validation job for the download object*/
            if (this.isValidationEnabled)
                createValidationJobs(postResponse);
        }

    }

    /**
     * create validation job for the postResponse
     *
     * @param postResponse to validate
     */
    @Override
    public void createValidationJobs(PostResponse postResponse) {
//        Log.d(TAG, "Creating Validation Job for postResponse : " + assignment.getUidAssignment());
        /*create job to validate the downloaded postResponse*/
        JobCreator.createPostResponseValidationJob(postResponse, mShouldShowNotification).execute();
    }

    /**
     * network call to fetch postResponse
     *
     * @param objectId id of the postResponse to fetch
     * @return call : the network call to fetch postResponse
     */
    @Override
    public Call<PostResponse> fetchFromNetwork(String objectId) {
//        Log.d(TAG, "Fetching postResponse : " + objectId);
        /*fetch postResponse from network*/
        return mNetworkModel.fetchLearningNetworkPostResponse(objectId);
    }

    /**
     * persist post response
     *
     * @param postResponse to persist
     * @return the persisted post response
     */
    @Override
    public PostResponse save(PostResponse postResponse) {
//        Log.d(TAG, "Saving postResponse : " + assignment.getUidAssignment());
        /*save post response to database*/
        return mJobModel.savePostResponse(postResponse);
    }

    /**
     * get the list of object id in the postResponse
     *
     * @param postResponse containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(PostResponse postResponse) {

        /*get id from the postResponse*/
        return Collections.singletonList(postResponse.getObjectId());
    }


}
