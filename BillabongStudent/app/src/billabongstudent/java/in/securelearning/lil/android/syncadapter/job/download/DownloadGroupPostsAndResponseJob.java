package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.GroupPostsNResponse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

public class DownloadGroupPostsAndResponseJob extends BaseDownloadJob<GroupPostsNResponse> {
    private String mGroupObjectId;

    public DownloadGroupPostsAndResponseJob(String objectId) {
        super(objectId);
        this.mGroupObjectId = objectId;

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
        /*create job to validate the downloaded group*/
        JobCreator.createGroupPostNResponseValidationJob(groupPostsNResponse, mGroupObjectId).execute();


    }

    /**
     * network call to fetch groupNew
     *
     * @param objectId id of the groupNew to fetch
     * @return call : the network call to fetch groupNew
     */
    @Override
    public Call<GroupPostsNResponse> fetchFromNetwork(String objectId) {
        /*fetch Group from network*/
        return mNetworkModel.fetchGroupPostAndResponse(objectId);
    }

    @Override
    public GroupPostsNResponse save(GroupPostsNResponse groupPostsNResponse) {

        return mJobModel.saveGroupPostAndPostResponse(groupPostsNResponse);
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
