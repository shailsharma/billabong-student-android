package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an AssignmentResponse from server
 */
public class DownloadAssignmentResponseJsonJob extends BaseDownloadJob<AssignmentResponse> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadAssignmentResponseJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadAssignmentResponseJsonJob(String objectId, String notificationId) {
        super(objectId, notificationId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public AssignmentResponse get(String objectId) {
        return mJobModel.fetchAssignmentResponseFromObjectId(objectId);
    }

    /**
     * create validation job for the assignmentResponse
     *
     * @param assignmentResponse to validate
     */
    @Override
    public void createValidationJobs(AssignmentResponse assignmentResponse) {
        /*create job to validate the downloaded assignmentResponse*/

        JobCreator.createAssignmentResponseValidationJob(assignmentResponse).execute();
    }

    /**
     * network call to fetch assignmentResponse
     *
     * @param objectId id of the assignmentResponse to fetch
     * @return call : the network call to fetch assignmentResponse
     */
    @Override
    public Call<AssignmentResponse> fetchFromNetwork(String objectId) {
//        Log.d(TAG, "Fetching Assignment : " + objectId);
        /*fetch assignmentResponse from network*/
        return mNetworkModel.fetchAssignmentResponse(objectId);
    }

    /**
     * persist assignmentResponse
     *
     * @param assignmentResponse to persist
     * @return the persisted assignmentResponse
     */
    @Override
    public AssignmentResponse save(AssignmentResponse assignmentResponse) {
//        Log.d(TAG, "Saving Assignment : " + assignmentResponse.getUidAssignment());
        /*save assignmentResponse to database*/
        return mJobModel.saveAssignmentResponse(assignmentResponse);
    }

    /**
     * get the list of object id in the assignmentResponse
     *
     * @param assignmentResponse containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(AssignmentResponse assignmentResponse) {

        /*get id from the assignmentResponse*/
        return Collections.singletonList(assignmentResponse.getObjectId());
    }


}
