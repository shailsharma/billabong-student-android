package in.securelearning.lil.android.syncadapter.job.validation;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.permission.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate PopUps Job.
 */
public class ValidatePopUpsJob extends BaseValidationCourseJobWeb<PopUps> {
    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public ValidatePopUpsJob(PopUps dataObject,AboutCourse aboutCourse) {
        super(dataObject,aboutCourse);

         /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * save pop ups
     *
     * @param popUps
     */
    @Override
    public void saveJson(PopUps popUps) {
        mJobModel.savePopUps(popUps);
    }

    /**
     * save and update sync status
     *
     * @param popUps
     */
    @Override
    public void updateAndSaveCompleteSyncStatus(PopUps popUps) {
        mJobModel.updateAndSaveCompleteSyncStatus(popUps);
    }

    @Override
    public boolean executeOtherValidationTasks(PopUps popUps) {
        return true;
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((PopUps)mDataObject).getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "PopUp - " + ((PopUps)mDataObject).getTitle() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((PopUps)mDataObject).getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "PopUp - " + ((PopUps)mDataObject).getTitle() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((PopUps)mDataObject).getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "PopUp - " + ((PopUps)mDataObject).getTitle() + " Downloaded !!!";
    }

    @Override
    protected int getProgressCountMax() {
        return mResourcesToDownload.length;
    }

    @Override
    protected boolean isIndeterminate() {
        return false;
    }

    @Override
    protected boolean isNotificationEnabled() {
        return PreferenceSettingUtilClass.isCourses(mContext);
    }

    @Override
    public int getNotificationResourceId() {
        return in.securelearning.lil.android.base.R.drawable.popup;
    }
}
