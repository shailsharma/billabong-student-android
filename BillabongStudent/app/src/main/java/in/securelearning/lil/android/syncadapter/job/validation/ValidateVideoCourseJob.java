package in.securelearning.lil.android.syncadapter.job.validation;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Created by Prabodh Dhabaria on 02-02-2017.
 */
public class ValidateVideoCourseJob extends BaseValidationCourseJobWeb<VideoCourse> {
    public ValidateVideoCourseJob(VideoCourse videoCourse, AboutCourse aboutCourse) {
        super(videoCourse, aboutCourse);

        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void saveJson(VideoCourse videoCourse) {
        mJobModel.saveVideoCourse(videoCourse);
    }

    @Override
    public void updateAndSaveCompleteSyncStatus(VideoCourse videoCourse) {
        mJobModel.updateAndSaveCompleteSyncStatus(videoCourse);
    }

    @Override
    public boolean executeOtherValidationTasks(VideoCourse videoCourse) {
        return true;
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((VideoCourse) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Video Course - " + ((VideoCourse) mDataObject).getTitle() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((VideoCourse) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Video Course - " + ((VideoCourse) mDataObject).getTitle() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((VideoCourse) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Video Course - " + ((VideoCourse) mDataObject).getTitle() + " Downloaded !!!";
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
        return in.securelearning.lil.android.base.R.drawable.video_course;
    }
}
