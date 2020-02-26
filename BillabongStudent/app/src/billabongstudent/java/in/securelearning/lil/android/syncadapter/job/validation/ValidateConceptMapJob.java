package in.securelearning.lil.android.syncadapter.job.validation;

import android.util.Log;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.ObjectInfo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.syncadapter.permission.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadCourseJobWeb;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadJobWeb;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate ConceptMap Job.
 */
public class ValidateConceptMapJob extends BaseValidationCourseJobWeb<ConceptMap> {

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public ValidateConceptMapJob(ConceptMap dataObject, AboutCourse aboutCourse) {
        super(dataObject, aboutCourse);

         /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * save concept map
     *
     * @param conceptMap
     */
    @Override
    public void saveJson(ConceptMap conceptMap) {
        mJobModel.saveConceptMap(conceptMap);
    }

    /**
     * update sync status and save concept map
     *
     * @param conceptMap
     */
    @Override
    public void updateAndSaveCompleteSyncStatus(ConceptMap conceptMap) {
        mJobModel.updateAndSaveCompleteSyncStatus(conceptMap);
    }

    @Override
    public boolean executeOtherValidationTasks(ConceptMap conceptMap) {
        Log.e(TAG, "Searching for jsons");
        for (ObjectInfo object :
                conceptMap.getResources()) {

            if (object.getType().equalsIgnoreCase("quiz")) {
                if (object.getObjectId()
                        != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching quiz " + object.getObjectId());
                    fetchQuiz(object.getObjectId());
                }
            } else if (object.getType().equalsIgnoreCase("popup")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching pop ups " + object.getObjectId());
                    fetchPopUps(object.getObjectId());
                }
            } else if (object.getType().equalsIgnoreCase("conceptmap")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching concept maps " + object.getObjectId());
                    fetchConceptMap(object.getObjectId());
                }
            } else if (object.getType().equalsIgnoreCase("interactiveimage")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching IImages " + object.getObjectId());
                    fetchInteractiveImage(object.getObjectId());
                }
            } else if (object.getType().equalsIgnoreCase("interactivevideo")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching IVideos " + object.getObjectId());
                    fetchInteractiveVideo(object.getObjectId());
                }
            } else if (object.getType().equalsIgnoreCase("videocourse")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching video courses " + object.getObjectId());
                    fetchVideoCourse(object.getObjectId());
                }
            }
        }

        saveJson(conceptMap);


        return areAllJsonsDownloaded(conceptMap);
    }

    private boolean areAllJsonsDownloaded(ConceptMap conceptMap) {
        boolean success = true;
        String completeSyncStatus = SyncStatus.COMPLETE_SYNC.toString();
        for (ObjectInfo object :
                conceptMap.getResources()) {
            if (object.getType().equalsIgnoreCase("quiz")) {
                if (object.getObjectId()
                        != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching quiz " + object.getObjectId());
                    Quiz object2 = mJobModel.fetchQuizFromObjectId(object.getObjectId());
                    success = success && object2.getSyncStatus().equals(completeSyncStatus);
                    object2 = null;
                }
            } else if (object.getType().equalsIgnoreCase("popup")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching pop ups " + object.getObjectId());
                    PopUps object2 = mJobModel.fetchPopUpsFromObjectId(object.getObjectId());
                    success = success && object2.getSyncStatus().equals(completeSyncStatus);
                    object2 = null;
                }
            } else if (object.getType().equalsIgnoreCase("conceptmap")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching concept maps " + object.getObjectId());
                    ConceptMap object2 = mJobModel.fetchConceptMapFromObjectId(object.getObjectId());
                    success = success && object2.getSyncStatus().equals(completeSyncStatus);
                    object2 = null;
                }
            } else if (object.getType().equalsIgnoreCase("interactiveimage")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching IImages " + object.getObjectId());
                    InteractiveImage object2 = mJobModel.fetchInteractiveImageFromObjectId(object.getObjectId());
                    success = success && object2.getSyncStatus().equals(completeSyncStatus);
                    object2 = null;
                }
            } else if (object.getType().equalsIgnoreCase("interactivevideo")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching IVideo " + object.getObjectId());
                    InteractiveVideo object2 = mJobModel.fetchInteractiveVideoFromObjectId(object.getObjectId());
                    success = success && object2.getSyncStatus().equals(completeSyncStatus);
                    object2 = null;
                }
            } else if (object.getType().equalsIgnoreCase("videocourse")) {
                if (object.getObjectId() != null && !object.getObjectId().isEmpty()) {
                    Log.e(TAG, "fetching video courses " + object.getObjectId());
                    VideoCourse object2 = mJobModel.fetchVideoCourseFromObjectId(object.getObjectId());
                    success = success && object2.getSyncStatus().equals(completeSyncStatus);
                    object2 = null;
                }
            }
        }
        return success;
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
     * fetch video course
     *
     * @param id
     */
    private void fetchVideoCourse(String id) {
        BaseDownloadCourseJobWeb job = JobCreator.createDownloadVideoCourseJob(id);
        job.execute();
        job = null;
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((ConceptMap) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Concept Map - " + ((ConceptMap) mDataObject).getTitle() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((ConceptMap) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Concept Map - " + ((ConceptMap) mDataObject).getTitle() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((ConceptMap) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Concept Map - " + ((ConceptMap) mDataObject).getTitle() + " Downloaded !!!";
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
        return PreferenceSettingUtilClass.isCourses(mContext);
    }

    @Override
    public int getNotificationResourceId() {
        return in.securelearning.lil.android.base.R.drawable.concept_map;
    }
}
