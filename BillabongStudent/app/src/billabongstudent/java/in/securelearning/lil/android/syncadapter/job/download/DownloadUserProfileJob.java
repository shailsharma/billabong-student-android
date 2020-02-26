package in.securelearning.lil.android.syncadapter.job.download;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfile;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an user profile from server
 */
public class DownloadUserProfileJob extends BaseDownloadJob<UserProfile> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadUserProfileJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public UserProfile get(String objectId) {
        return mJobModel.fetchUserProfileFromObjectId(objectId);
    }

    /**
     * create validation job for the user profile
     *
     * @param userProfile to validate
     */
    @Override
    public void createValidationJobs(UserProfile userProfile) {
        Log.d(TAG, "Creating Validation Job for user profile : " + userProfile.getObjectId());
        /*create job to validate the downloaded user profile*/
        JobCreator.createUserProfileValidationJob(userProfile).execute();
    }

    /**
     * network call to fetch user profile
     * *
     *
     * @param objectId id of the user profile  to fetch
     * @return call : the network call to fetch user profile
     */
    @Override
    public Call<UserProfile> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching user profile  : " + objectId);
        /*fetch Group from network*/
        return mNetworkModel.fetchUserProfile(objectId);
    }

    /**
     * persist user profile
     *
     * @param userProfile to persist
     * @return the persisted user profile
     */
    @Override
    public UserProfile save(UserProfile userProfile) {
        Log.d(TAG, "Saving user profile : " + userProfile.getObjectId());
        /*save user profile to database*/
        return mJobModel.saveUserProfile(userProfile);
    }

    /**
     * get the list of object id in the user profile
     *
     * @param userProfile containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(UserProfile userProfile) {

        /*get id from the group*/
        return Collections.singletonList(userProfile.getObjectId());
    }


}
