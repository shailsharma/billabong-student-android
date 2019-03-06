package in.securelearning.lil.android.syncadapter.job.validation;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Created by Prabodh Dhabaria on 02-02-2017.
 */
public class ValidateInteractiveVideoJob extends BaseValidationCourseJobWeb<InteractiveVideo> {
    public ValidateInteractiveVideoJob(InteractiveVideo interactiveVideo, AboutCourse aboutCourse) {
        super(interactiveVideo, aboutCourse);

        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void saveJson(InteractiveVideo interactiveVideo) {
        mJobModel.saveInteractiveVideo(interactiveVideo);
    }

    @Override
    public void updateAndSaveCompleteSyncStatus(InteractiveVideo interactiveVideo) {
        mJobModel.updateAndSaveCompleteSyncStatus(interactiveVideo);
    }

    @Override
    public boolean executeOtherValidationTasks(InteractiveVideo interactiveVideo) {
        return true;
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((InteractiveVideo) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Interactive Video - " + ((InteractiveVideo) mDataObject).getTitle() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((InteractiveVideo) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Interactive Video - " + ((InteractiveVideo) mDataObject).getTitle() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((InteractiveVideo) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Interactive Video - " + ((InteractiveVideo) mDataObject).getTitle() + " Downloaded !!!";
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
