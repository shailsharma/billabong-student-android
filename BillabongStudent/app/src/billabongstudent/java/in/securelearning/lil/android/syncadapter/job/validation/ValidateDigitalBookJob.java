package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.CustomSection;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroCourseType;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.QuizWeb;
import in.securelearning.lil.android.syncadapter.permission.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadCourseJobWeb;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadJobWeb;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate DigitalBook job.
 */
public class ValidateDigitalBookJob extends BaseValidationCourseJobWeb<DigitalBook> {
    public final String TAG = this.getClass().getName();

    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    public ValidateDigitalBookJob(DigitalBook dataObject, AboutCourse aboutCourse, boolean isNotificationEnabled) {
        super(dataObject, aboutCourse, isNotificationEnabled);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void updateProgressCountInNotification(int progress) {
        if (isNotificationEnabled()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(getNotificationResourceId())
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setAutoCancel(false)
                    .setColor(getSmallBackgroundColor())
                    .setLargeIcon(getLargeNotificationBitmap())
                    .setContentTitle(getStartNotificationTitle())
                    .setSound(null)
                    .setOnlyAlertOnce(true);
            if (getProgressCountMax() > 0 && progress <= getProgressCountMax()) {
                builder.setProgress(getProgressCountMax(), progress, isIndeterminate())
                        .setContentText(getProgressText(getProgressCountMax(), progress));
                if (progress == getProgressCountMax()) {
                    builder.setContentText(getProgressText(getProgressCountMax(), progress) + " ,  Downloading Micro Courses ....");
                }
            }

            NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notifyMgr.createNotificationChannel(channel);
            }
            notifyMgr.notify(getNotificationId(), builder.build());
        }
    }

    @Override
    public void showDownloadFailedNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(getNotificationResourceId())
                        .setColor(getSmallBackgroundColor())
                        .setLargeIcon(getLargeNotificationBitmap())
                        .setTicker(getFailedNotificationTickerText())
                        .setOngoing(false)
                        .setContentTitle(getFailedNotificationTitle())
                        .setContentText(getFailedNotificationText());


        NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(getNotificationId(), builder.build());
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((DigitalBook) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Digital Book - " + ((DigitalBook) mDataObject).getTitle() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((DigitalBook) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Digital Book - " + ((DigitalBook) mDataObject).getTitle() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((DigitalBook) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Digital Book - " + ((DigitalBook) mDataObject).getTitle() + " Downloaded !!!";
    }

    @Override
    protected int getProgressCountMax() {
        return mResourcesToDownload.length + mResourcesDownloaded.length;
    }

    @Override
    protected boolean isIndeterminate() {
        return false;
    }

    @Override
    protected boolean isNotificationEnabled() {

        return super.isNotificationEnabled() && PreferenceSettingUtilClass.isCourses(mContext);
    }

    @Override
    public int getNotificationResourceId() {
        return in.securelearning.lil.android.base.R.drawable.course_gray;
    }

    public boolean executeOtherValidationTasks(DigitalBook digitalBook) {
        Log.e(TAG, "Searching for jsons");
        for (CustomSection customSection :
                digitalBook.getDigitalBook().getSections()) {
            if (customSection != null && customSection.getSectionCode() == 5 && !TextUtils.isEmpty(customSection.getObjectId())) {
                fetchCustomSection(customSection.getObjectId());
            }
        }
        for (MicroCourseType microCourseType : digitalBook.getMicroCourseList()) {

            if (microCourseType.getType().equalsIgnoreCase("quiz")) {
                ((DigitalBook) digitalBook).getQuizList().add(microCourseType.getId());
            } else if (microCourseType.getType().equalsIgnoreCase("popup")) {
                ((DigitalBook) digitalBook).getPopUpsList().add(microCourseType.getId());
            } else if (microCourseType.getType().equalsIgnoreCase("conceptmap")) {
                ((DigitalBook) digitalBook).getConceptMapList().add(microCourseType.getId());
            } else if (microCourseType.getType().equalsIgnoreCase("interactiveimage")) {
                ((DigitalBook) digitalBook).getInteractiveImageList().add(microCourseType.getId());
            } else if (microCourseType.getType().equalsIgnoreCase("interactivevideo")) {
                ((DigitalBook) digitalBook).getInteractiveVideoList().add(microCourseType.getId());
            }
        }

        saveJson(digitalBook);
        String id = null;
        /*find and download quiz*/
        for (int j = 0; j < ((DigitalBook) digitalBook).getQuizList().size(); j++) {
            id = ((DigitalBook) digitalBook).getQuizList().get(j);
            if (id != null && !id.isEmpty()) {
                Log.e(TAG, "fetching quiz " + id);
                fetchQuiz(id);
            }
        }
        /*find and download pop ups*/
        for (int j = 0; j < ((DigitalBook) digitalBook).getPopUpsList().size(); j++) {
            id = ((DigitalBook) digitalBook).getPopUpsList().get(j);
            if (id != null && !id.isEmpty()) {
                Log.e(TAG, "fetching pop ups " + id);
                fetchPopUps(id);
            }
        }
        /*find and download concept maps*/
        for (int j = 0; j < ((DigitalBook) digitalBook).getConceptMapList().size(); j++) {
            id = ((DigitalBook) digitalBook).getConceptMapList().get(j);
            if (id != null && !id.isEmpty()) {
                Log.e(TAG, "fetching concept maps " + id);
                fetchConceptMap(id);
            }
        }
        /*find and download video courses*/
        for (int j = 0; j < ((DigitalBook) digitalBook).getInteractiveVideoList().size(); j++) {
            id = ((DigitalBook) digitalBook).getInteractiveVideoList().get(j);
            if (id != null && !id.isEmpty()) {
                Log.e(TAG, "fetching video courses " + id);
                fetchInteractiveVideo(id);
            }
        }

        /*find and download interactive images*/
        for (int j = 0; j < ((DigitalBook) digitalBook).getInteractiveImageList().size(); j++) {
            id = ((DigitalBook) digitalBook).getInteractiveImageList().get(j);
            if (id != null && !id.isEmpty()) {
                Log.e(TAG, "fetching IImages " + id);
                fetchInteractiveImage(id);
            }
        }
        id = null;

        return areAllJsonsDownloaded(digitalBook);
    }

    /**
     * @param dataObject
     * @return true if all referenced jsons are in complete sync status else false
     */
    private boolean areAllJsonsDownloaded(DigitalBook dataObject) {
        boolean success = true;
        String completeSyncStatus = SyncStatus.COMPLETE_SYNC.toString();

        for (CustomSection customSection :
                dataObject.getDigitalBook().getSections()) {
            if (customSection != null && customSection.getSectionCode() == 5 && !TextUtils.isEmpty(customSection.getObjectId())) {
                CustomSection object = mJobModel.fetchCustomSectionFromObjectId(customSection.getObjectId());
                success = success && object.getSyncStatus().equals(completeSyncStatus);
                object = null;
            }
        }

        for (String id :
                dataObject.getQuizList()) {
            QuizWeb object = mJobModel.fetchQuizWebFromObjectId(id);
            success = success && object.getSyncStatus().equals(completeSyncStatus);
            object = null;
        }

        for (String id :
                dataObject.getConceptMapList()) {
            ConceptMap object = mJobModel.fetchConceptMapFromObjectId(id);
            success = success && object.getSyncStatus().equals(completeSyncStatus);
            object = null;
        }

        for (String id :
                dataObject.getInteractiveImageList()) {
            InteractiveImage object = mJobModel.fetchInteractiveImageFromObjectId(id);
            success = success && object.getSyncStatus().equals(completeSyncStatus);
            object = null;
        }

        for (String id :
                dataObject.getPopUpsList()) {
            PopUps object = mJobModel.fetchPopUpsFromObjectId(id);
            success = success && object.getSyncStatus().equals(completeSyncStatus);
            object = null;
        }

        for (String id :
                dataObject.getInteractiveVideoList()) {
            InteractiveVideo object = mJobModel.fetchInteractiveVideoFromObjectId(id);
            success = success && object.getSyncStatus().equals(completeSyncStatus);
            object = null;
        }

        completeSyncStatus = null;
        return success;
    }

    private void fetchCustomSection(String id) {
        BaseDownloadJobWeb job = JobCreator.createDownloadCustomSectionJob(id);
        job.execute();
        job = null;

    }

    /**
     * fetch quiz
     *
     * @param id
     */
    private void fetchQuiz(String id) {
        BaseDownloadJobWeb job = JobCreator.createDownloadWebQuizJob(id);
        job.execute();
        job = null;

    }

    /**
     * fetch pop ups
     *
     * @param id
     */
    private void fetchPopUps(String id) {
        BaseDownloadCourseJobWeb job = JobCreator.createDownloadPopUpsJob(id);
        job.execute();
        job = null;

    }

    /**
     * fetch conceptMap
     *
     * @param id
     */
    private void fetchConceptMap(String id) {
        BaseDownloadCourseJobWeb job = JobCreator.createDownloadConceptMapJob(id);
        job.execute();
        job = null;

    }

    /**
     * fetch interactiveImage
     *
     * @param id
     */
    private void fetchInteractiveImage(String id) {
        BaseDownloadCourseJobWeb job = JobCreator.createDownloadInteractiveImageJob(id);
        job.execute();
        job = null;
    }

    private void fetchInteractiveVideo(String id) {
        BaseDownloadCourseJobWeb job = JobCreator.createDownloadInteractiveVideoJob(id);
        job.execute();
        job = null;
    }

    /**
     * save digitalBook
     *
     * @param digitalBook to save
     */
    public void saveJson(DigitalBook digitalBook) {
        mJobModel.saveDigitalBook(digitalBook);
    }

    /**
     * save and update sync status
     *
     * @param digitalBook
     */
    public void updateAndSaveCompleteSyncStatus(DigitalBook digitalBook) {
        mJobModel.updateAndSaveCompleteSyncStatus(digitalBook);
    }

}
