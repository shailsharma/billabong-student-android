package in.securelearning.lil.android.syncadapter.job.upload;

import android.util.Log;

import java.io.IOException;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Post assignedBadges  Job.
 */
public class UploadAssignedBadgeJob extends BaseUploadJob<AssignedBadges> {
    public final String TAG = this.getClass().getCanonicalName();

    public UploadAssignedBadgeJob(AssignedBadges dataObject) {
        super(dataObject);
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute uploading of assignedBadges response
     */
    public void execute() {
        try {

            /*upload data object*/
            Response<AssignedBadges> response = uploadJsonToServer(mDataObject).execute();

            /*if upload is successful*/
            if (response.isSuccessful()) {
                /*retrieve assignedBadges response*/
                AssignedBadges assignedBadges = response.body();

                Log.e(TAG, " assignedBadges  posted : " + assignedBadges.getObjectId());

                /*set sync status*/
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                mDataObject.setObjectId(assignedBadges.getObjectId());

                /*save json to database*/
                saveJsonToDatabase(mDataObject);
            } else if (response.code() == 422 || response.code() == 500) {
                Response<AssignedBadges> response2 = getByAlias(mDataObject.getAlias()).execute();

            /*if upload is successful*/
                if (response2.isSuccessful()) {
                /*retrieve assignedBadges response*/
                    AssignedBadges assignedBadges = response2.body();

                    Log.e(TAG, " assignedBadges  posted : " + assignedBadges.getObjectId());

                /*set sync status*/
                    mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                    mDataObject.setObjectId(assignedBadges.getObjectId());

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
                Log.e(TAG, "assignedBadges Res err " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**
     * upload assignedBadges response json to network
     *
     * @param assignedBadges to upload
     * @return network call
     */
    public Call<AssignedBadges> uploadJsonToServer(AssignedBadges assignedBadges) {
        /*network call to post assignedBadges response*/
        return mNetworkModel.uploadAssignedBadge(assignedBadges);
    }

    public Call<AssignedBadges> getByAlias(String alias) {
        return mNetworkModel.fetchByAliasAssignedBadges(alias);
    }

    /**
     * save assignedBadges response to database
     *
     * @param assignedBadges to save
     */
    public void saveJsonToDatabase(AssignedBadges assignedBadges) {
        /*save assignedBadges response*/
        mJobModel.saveAssignedBadges(assignedBadges);
    }

}
