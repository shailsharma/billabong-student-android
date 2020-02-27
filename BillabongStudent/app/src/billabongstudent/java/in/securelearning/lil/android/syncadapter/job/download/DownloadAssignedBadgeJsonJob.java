package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an AssignedBadges from server
 */
public class DownloadAssignedBadgeJsonJob extends BaseDownloadJob<AssignedBadges> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadAssignedBadgeJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public AssignedBadges get(String objectId) {
        return new AssignedBadges();
    }

    /**
     * create validation job for the assignedBadges
     *
     * @param assignedBadges to validate
     */
    @Override
    public void createValidationJobs(AssignedBadges assignedBadges) {
//        Log.d(TAG, "Creating Validation Job for assignedBadges : " + assignment.getUidAssignment());
        /*create job to validate the downloaded assignedBadges*/
        JobCreator.createAssignedBadgesValidationJob(assignedBadges).execute();
    }

    /**
     * network call to fetch postData
     *
     * @param objectId id of the postData to fetch
     * @return call : the network call to fetch postData
     */
    @Override
    public Call<AssignedBadges> fetchFromNetwork(String objectId) {
//        Log.d(TAG, "Fetching assignedBadges : " + objectId);
        /*fetch assignedBadges from network*/
        return mNetworkModel.fetchAssignedBadges(objectId);
    }

    /**
     * persist AssignedBadges
     *
     * @param assignedBadges to persist
     * @return the persisted post response
     */
    @Override
    public AssignedBadges save(AssignedBadges assignedBadges) {
//        Log.d(TAG, "Saving postData : " + assignment.getUidAssignment());
        /*save post response to database*/
        return mJobModel.saveAssignedBadges(assignedBadges);
    }

    /**
     * get the list of object id in the AssignedBadges
     *
     * @param assignedBadges containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(AssignedBadges assignedBadges) {

        /*get id from the postData*/
        return Collections.singletonList(assignedBadges.getObjectId());
    }


}
