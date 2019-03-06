package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;

import in.securelearning.lil.android.base.dataobjects.QuizWeb;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Created by Prabodh Dhabaria on 23-09-2016.
 */
public class ValidateQuizWebJob extends BaseValidationJobWeb<QuizWeb> {

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public ValidateQuizWebJob(QuizWeb dataObject) {
        super(dataObject);

         /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void saveJson(QuizWeb quiz) {
        mJobModel.saveQuizWeb(quiz);
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    @Override
    public void updateAndSaveCompleteSyncStatus(QuizWeb quiz) {
        mJobModel.updateAndSaveCompleteSyncStatus(quiz);
    }

    @Override
    public boolean executeOtherValidationTasks(QuizWeb quiz) {
        return true;
    }

    @Override
    public String getStorageFolderPrefix() {
        return "";
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
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
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
        return mResourcesToDownload.length+mResourcesDownloaded.length;
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
        return in.securelearning.lil.android.base.R.drawable.quiz;
    }
}
