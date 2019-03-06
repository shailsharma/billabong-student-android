package in.securelearning.lil.android.syncadapter.job.download;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.GroupPostsNResponse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an groupNew from server
 */
public class DownloadGroupPostsNResponseJob extends BaseDownloadJob<GroupPostsNResponse> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadGroupPostsNResponseJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public GroupPostsNResponse get(String objectId) {
        return new GroupPostsNResponse();
    }

    /**
     * create validation job for the group
     *
     * @param groupPostsNResponse to validate
     */
    @Override
    public void createValidationJobs(GroupPostsNResponse groupPostsNResponse) {
//        Log.d(TAG, "Creating Validation Job for group : " + assignment.getUidAssignment());
        /*create job to validate the downloaded group*/

        Log.e(TAG, "-------------------createValidationJobs()---------------------------");
        JobCreator.createGroupPostNResponseValidationJob(groupPostsNResponse).execute();


    }

    /**
     * network call to fetch groupNew
     *
     * @param objectId id of the groupNew to fetch
     * @return call : the network call to fetch groupNew
     */
    @Override
    public Call<GroupPostsNResponse> fetchFromNetwork(String objectId) {
//        Log.d(TAG, "Fetching Group : " + objectId);
        /*fetch Group from network*/
        return mNetworkModel.fetchGroupPostNResponse(objectId);
    }

    @Override
    public GroupPostsNResponse save(GroupPostsNResponse groupPostsNResponse) {

        return mJobModel.saveGroupPostNPostResponse(groupPostsNResponse);
    }


    /**
     * get the list of object id in the group
     *
     * @param group containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(GroupPostsNResponse group) {

        /*get id from the group*/
        return Collections.singletonList(group.getObjectId());
    }


}
