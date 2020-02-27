package in.securelearning.lil.android.syncadapter.job.validation;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.permission.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate InteractiveImage Job.
 */
public class ValidateInteractiveImageJob extends BaseValidationCourseJobWeb<InteractiveImage> {
    public final String TAG = this.getClass().getName();

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public ValidateInteractiveImageJob(InteractiveImage dataObject,AboutCourse aboutCourse) {
        super(dataObject,aboutCourse);

         /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * save interactiveImage
     *
     * @param interactiveImage to save
     */
    public void saveJson(InteractiveImage interactiveImage) {
        mJobModel.saveInteractiveImage(interactiveImage);
    }

    /**
     * save and update sync status
     *
     * @param interactiveImage
     */
    public void updateAndSaveCompleteSyncStatus(InteractiveImage interactiveImage) {
        mJobModel.updateAndSaveCompleteSyncStatus(interactiveImage);
    }

    @Override
    public boolean executeOtherValidationTasks(InteractiveImage interactiveImage) {
        return true;
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((InteractiveImage)mDataObject).getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Interactive Image - " + ((InteractiveImage)mDataObject).getTitle() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((InteractiveImage)mDataObject).getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Interactive Image - " + ((InteractiveImage)mDataObject).getTitle() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((InteractiveImage)mDataObject).getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Interactive Image - " + ((InteractiveImage)mDataObject).getTitle() + " Downloaded !!!";
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
        return in.securelearning.lil.android.base.R.drawable.interactive_image;
    }
}
