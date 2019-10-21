package in.securelearning.lil.android.syncadapter.job.download;

import android.text.TextUtils;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an groupNew from server
 */
public class DownloadGroupJob extends BaseDownloadJob<Group> {
    private final String TAG = this.getClass().getCanonicalName();
    private String mGroupType;

    public DownloadGroupJob(String objectId) {
        this(objectId, true, false);
    }

    private DownloadGroupJob(String objectId, boolean doJsonRefresh, boolean isValidationEnabled) {
        super(objectId, "", doJsonRefresh, isValidationEnabled);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadGroupJob(String objectId, String groupType) {
        super(objectId, groupType, true, false);
        mGroupType = groupType;
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public Group get(String objectId) {
        return mJobModel.fetchGroupFromObjectId(objectId);
    }

    /**
     * create validation job for the group
     *
     * @param group to validate
     */
    @Override
    public void createValidationJobs(Group group) {

        Log.d(TAG, "Creating Validation Job for group : " + group.getObjectId());
        /*create job to validate the downloaded group*/
        JobCreator.createGroupValidationJob(group).execute();
    }


    /**
     * network call to fetch groupNew
     *
     * @param objectId id of the groupNew to fetch
     * @return call : the network call to fetch groupNew
     */
    @Override
    public Call<Group> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching Group : " + objectId);
        /*fetch Group from network*/
        return mNetworkModel.fetchGroup(objectId);
    }

    /**
     * persist group
     *
     * @param group to persist
     * @return the persisted group
     */
    @Override
    public Group save(Group group) {
        Log.d(TAG, "Saving group : " + group.getObjectId());
        /*save group to database*/
        String name = group.getGroupName();
        String grade = "";
        String section = "";
        String subject = "";
        if (group.getGrade() != null && !TextUtils.isEmpty(group.getGrade().getName())) {
            grade = group.getGrade().getName();
        }
        if (group.getSection() != null && !TextUtils.isEmpty(group.getSection().getName())) {
            section = group.getSection().getName();
        }
        if (group.getSubject() != null && !TextUtils.isEmpty(group.getSubject().getName())) {
            subject = group.getSubject().getName();
        }
        if (!TextUtils.isEmpty(grade)) {

            group.setNameTeacher(in.securelearning.lil.android.base.utils.TextUtils.join(" ", grade, section, subject));

            if (!TextUtils.isEmpty(subject)) {
                group.setNameStudent(subject);
            } else {
                group.setNameStudent(in.securelearning.lil.android.base.utils.TextUtils.join(" ", grade, section));
            }
        }

        /*For network and training group differentiation adding groupType to group */
        if (!TextUtils.isEmpty(mGroupType)) {
            group.setGroupType(mGroupType);
        }

//        Group localGroup = mJobModel.getGroupByObjectId(group.getObjectId());
//        if (localGroup != null && !TextUtils.isEmpty(localGroup.getObjectId())) {
//            group.setNetworkDataDownloaded(localGroup.isNetworkDataDownloaded());
//        }

        mJobModel.saveGroup(group);

        return group;
    }

    /**
     * get the list of object id in the group
     *
     * @param group containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(Group group) {

        /*get id from the group*/
        return Collections.singletonList(group.getObjectId());
    }

}
