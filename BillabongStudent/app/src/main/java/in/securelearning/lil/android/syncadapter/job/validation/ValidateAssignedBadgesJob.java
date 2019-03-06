package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;

import javax.inject.Inject;


import in.securelearning.lil.android.base.constants.SyncStatus;

import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate learning network post data job.
 *
 * @author Pushkar Raj
 */
public class ValidateAssignedBadgesJob extends BaseValidationJob<AssignedBadges> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    public ValidateAssignedBadgesJob(AssignedBadges dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the learning network post data
     */
    @Override
    public boolean executeValidation() {

            /*save AssignedBadges with complete sync status
            and update status of all json's tagging this AssignedBadges*/
        mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
        mJobModel.saveAssignedBadges(mDataObject);

        return true;
    }

    @Override
    public void saveJson(AssignedBadges assignedBadges) {
        mJobModel.saveAssignedBadges(assignedBadges);
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
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
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_LEARNING_NETWORK_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
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


}
