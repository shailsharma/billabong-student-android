package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an Assignment from server
 */
public class DownloadAssignmentJsonJob extends BaseDownloadJob<Assignment> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadAssignmentJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }
    public DownloadAssignmentJsonJob(String objectId,String notificationId) {
        super(objectId,notificationId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public Assignment get(String objectId) {
        return mJobModel.fetchAssignmentFromObjectId(objectId);
    }

    /**
     * create validation job for the assignment
     *
     * @param assignment to validate
     */
    @Override
    public void createValidationJobs(Assignment assignment) {
//        Log.d(TAG, "Creating Validation Job for Assignment : " + assignment.getUidAssignment());
        /*create job to validate the downloaded assignment*/
        JobCreator.createAssignmentValidationJob(assignment).execute();
    }

    /**
     * network call to fetch assignment
     *
     * @param objectId id of the assignment to fetch
     * @return call : the network call to fetch assignment
     */
    @Override
    public Call<Assignment> fetchFromNetwork(String objectId) {
//        Log.d(TAG, "Fetching Assignment : " + objectId);
        /*fetch assignment from network*/
        return mNetworkModel.fetchAssignment(objectId);
    }

    /**
     * persist assignment
     *
     * @param assignment to persist
     * @return the persisted assignment
     */
    @Override
    public Assignment save(Assignment assignment) {
//        Log.d(TAG, "Saving Assignment : " + assignment.getUidAssignment());
        /*save assignment to database*/
        return mJobModel.saveAssignment(assignment);
    }

    /**
     * get the list of object id in the assignment
     *
     * @param assignment containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(Assignment assignment) {

        /*get id from the assignment*/
        return Collections.singletonList(assignment.getObjectId());
    }


}
