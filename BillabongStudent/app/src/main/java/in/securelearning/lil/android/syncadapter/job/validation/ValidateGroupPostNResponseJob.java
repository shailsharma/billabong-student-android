package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupPostsNResponse;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate  group job.
 *
 * @author Pushkar Raj
 */
public class ValidateGroupPostNResponseJob extends BaseValidationJob<GroupPostsNResponse> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    public ValidateGroupPostNResponseJob(GroupPostsNResponse dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the  group
     */
    @Override
    public boolean executeValidation() {
        /*fetch  group from database*/

        if (mDataObject != null) {
            if (mDataObject.getPost() != null) {
                for (PostData postData : mDataObject.getPost()) {
                    JobCreator.createPostDataValidationJob(postData, false).execute();
                }
            }

            if (mDataObject.getPostResponse() != null) {
                for (PostResponse postResponse : mDataObject.getPostResponse()) {
                    JobCreator.createPostResponseValidationJob(postResponse, false).execute();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void saveJson(GroupPostsNResponse groupPostsNResponse) {

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

    /**
     * save and update sync status
     *
     * @param group
     */
    public void updateAndSaveCompleteSyncStatus(Group group) {
        mJobModel.updateAndSaveCompleteSyncStatus(group);
    }


}
