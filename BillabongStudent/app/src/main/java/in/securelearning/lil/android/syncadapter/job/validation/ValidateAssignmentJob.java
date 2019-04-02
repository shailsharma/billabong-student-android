package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentMinimal;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadJob;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadJobWeb;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate assignment job.
 */
public class ValidateAssignmentJob extends BaseValidationJob<Assignment> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;
    AssignmentMinimal mAssignmentMinimal;

    public ValidateAssignmentJob(Assignment dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the assignment
     */
    @Override
    public boolean executeValidation() {
        boolean thumbnailDownloaded = false;
        // TODO: 4/25/2017 download thumbnail


        if (mDataObject.getThumbnail() != null && mDataObject.getThumbnail().getLocalUrl() != null && !mDataObject.getThumbnail().getLocalUrl().contains("file:")) {
            /*download thumbnail Resource*/
            String thumbnailUrl = mDataObject.getThumbnail().getUrl();
            /*download Resource*/

            if (thumbnailUrl != null && !thumbnailUrl.isEmpty() && !thumbnailUrl.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), thumbnailUrl);
                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    thumbnailUrl = resourceLocal.getDeviceURL();

                    thumbnailUrl = "file://" + mBaseFolder + File.separator + thumbnailUrl;

                    Log.e(TAG, "resources image downloaded");

                    mDataObject.getThumbnail().setLocalUrl(thumbnailUrl);
                    saveJson(mDataObject);

                    thumbnailDownloaded = true;


                } else {
                    thumbnailDownloaded = false;
                }
                resourceLocal = null;
            } else {
                thumbnailDownloaded = true;
            }
            thumbnailUrl = null;
        } else {
            thumbnailDownloaded = true;
        }


        if (!TextUtils.isEmpty(mDataObject.getUidQuiz())) {
            /*fetch quiz from database*/
            Quiz quiz = fetchQuizFromDatabase(mDataObject.getUidQuiz());

            /*if quiz is available validate it*/
            if (quiz == null || !quiz.getObjectId().equals(mDataObject.getUidQuiz())) {
                BaseDownloadJob<Quiz> job = downloadQuiz(mDataObject.getUidQuiz());
                job.execute();
                quiz = fetchQuizFromDatabase(mDataObject.getUidQuiz());
                job = null;
            } else if (quiz != null && quiz.getObjectId().equals(mDataObject.getUidQuiz()) && quiz.getSyncStatus().equals(SyncStatus.JSON_SYNC.toString())) {
                BaseValidationJob<Quiz> job = validateQuiz(quiz);
                job.execute();
                quiz = fetchQuizFromDatabase(mDataObject.getUidQuiz());
                job = null;
            }
            /*if quiz is completely in sync*/
            if (quiz != null && quiz.getObjectId().equals(mDataObject.getUidQuiz()) && quiz.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                if (thumbnailDownloaded) {
                    mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                    saveJson(mDataObject);
                    mAssignmentMinimal = mJobModel.saveAssignmentMinimal(mDataObject);
                    quiz = null;
                    mAssignmentMinimal = null;
                    return true;
                }
            }
            quiz = null;
            return false;
        } else {
            if (!TextUtils.isEmpty(mDataObject.getUidCourse())) {
                saveJson(mDataObject);

                //  AboutCourse aboutCourse = fetchAboutCourseFromDatabase(mDataObject.getUidCourse());

                /*if course is available validate it*/
                //if (aboutCourse == null || !aboutCourse.getObjectId().equals(mDataObject.getUidCourse())) {
                //   BaseDownloadJobWeb<AboutCourse> job = downloadAboutCourse(mDataObject.getUidCourse(), mDataObject.getAssignmentType());
                //     if (job != null) {
                //       job.execute();
                //    } else {
                //  mDataObject.setAssignmentType(mDataObject.getUidCourse());
                //    }
                //    aboutCourse = fetchAboutCourseFromDatabase(mDataObject.getUidCourse());
                //    job = null;
                // } else if (aboutCourse != null && aboutCourse.getObjectId().equals(mDataObject.getUidCourse()) && aboutCourse.getSyncStatus().equals(SyncStatus.JSON_SYNC.toString())) {
                //      BaseValidationJob<AboutCourse> job = validateAboutCourse(aboutCourse);
                //     job.execute();
                //     aboutCourse = fetchAboutCourseFromDatabase(mDataObject.getUidCourse());
                //     job = null;
                //     if (mDataObject.getAssignmentType().equals(AssignmentType.TYPE_SUBJECTIVE.getAssignmentType())) {
                //        mDataObject.setAssignmentType(getCourseType(aboutCourse));
                //        saveJson(mDataObject);
                //     }
                // }
                /*if course is completely in sync*/
                //   if (aboutCourse != null && aboutCourse.getObjectId().equals(mDataObject.getUidCourse()) && aboutCourse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                if (thumbnailDownloaded) {
                    mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                    saveJson(mDataObject);
                    mAssignmentMinimal = mJobModel.saveAssignmentMinimal(mDataObject);
                    //         aboutCourse = null;
                    mAssignmentMinimal = null;
                    return true;
                }
                //   }
                //   aboutCourse = null;
                return false;

            } else if (!TextUtils.isEmpty(mDataObject.getUidResource())) {
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                saveJson(mDataObject);
                mAssignmentMinimal = mJobModel.saveAssignmentMinimal(mDataObject);
                mAssignmentMinimal = null;
                return true;

            }
            return false;
        }

    }

    private String findAndUpdateAssignmentType(String uid) {

        JobCreator.createDownloadAboutCourseDigitalBookJob(uid).execute();
        AboutCourse aboutCourse = fetchAboutCourseFromDatabase(uid);
        if (aboutCourse == null || !aboutCourse.getObjectId().equals(uid)) {
            JobCreator.createDownloadAboutCourseVideoCourseJob(uid).execute();
            aboutCourse = fetchAboutCourseFromDatabase(uid);
            if (aboutCourse == null || !aboutCourse.getObjectId().equals(uid)) {
                JobCreator.createDownloadAboutCourseVideoCourseJob(uid).execute();
                aboutCourse = fetchAboutCourseFromDatabase(uid);
                if (aboutCourse == null || !aboutCourse.getObjectId().equals(uid)) {
                    JobCreator.createDownloadAboutCourseConceptMapJob(uid).execute();
                    aboutCourse = fetchAboutCourseFromDatabase(uid);
                    if (aboutCourse == null || !aboutCourse.getObjectId().equals(uid)) {
                        JobCreator.createDownloadAboutCourseInteractiveImageJob(uid).execute();
                        aboutCourse = fetchAboutCourseFromDatabase(uid);
                        if (aboutCourse == null || !aboutCourse.getObjectId().equals(uid)) {
                            JobCreator.createDownloadAboutCoursePopUpsJob(uid).execute();
                            aboutCourse = fetchAboutCourseFromDatabase(uid);
                            if (aboutCourse == null || !aboutCourse.getObjectId().equals(uid)) {
                                JobCreator.createDownloadAboutCourseInteractiveVideoJob(uid).execute();
                                aboutCourse = fetchAboutCourseFromDatabase(uid);
                            }
                        }
                    }
                }
            }
        }

        if (aboutCourse != null && aboutCourse.getObjectId().equals(uid)) {
            return getCourseType(aboutCourse);
        } else {
            return "";
        }

    }

    private String getCourseType(AboutCourse aboutCourse) {
        if (aboutCourse.getCourseType().equalsIgnoreCase("digitalbook")) {
            return AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType();
        } else if (aboutCourse.getCourseType().equalsIgnoreCase("videocourse")) {
            return AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType();
        } else if (aboutCourse.getMicroCourseType().toLowerCase().contains("map")) {
            return AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType();
        } else if (aboutCourse.getMicroCourseType().toLowerCase().contains("interactiveimage")) {
            return AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType();
        } else if (aboutCourse.getMicroCourseType().toLowerCase().contains("pop")) {
            return AssignmentType.TYPE_Popup.getAssignmentType();
        } else if (aboutCourse.getMicroCourseType().toLowerCase().contains("video")) {
            return AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType();
        } else return "";
    }

    private BaseValidationJob<AboutCourse> validateAboutCourse(AboutCourse aboutCourse) {

        return JobCreator.createAboutCourseValidationJob(aboutCourse, false);


    }

    private BaseDownloadJobWeb<AboutCourse> downloadAboutCourse(String uidCourse, String assignmentType) {
        if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_DIGITAL_BOOK.toString())) {
            return JobCreator.createDownloadAboutCourseDigitalBookJob(uidCourse);
        } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_VIDEO_COURSE.toString())) {
            return JobCreator.createDownloadAboutCourseVideoCourseJob(uidCourse);
        } else if (assignmentType.toLowerCase().contains(AssignmentType.TYPE_CONCEPT_MAP.toString())) {
            return JobCreator.createDownloadAboutCourseConceptMapJob(uidCourse);
        } else if (assignmentType.toLowerCase().contains(AssignmentType.TYPE_INTERACTIVE_IMAGE.toString())) {
            return JobCreator.createDownloadAboutCourseInteractiveImageJob(uidCourse);
        } else if (assignmentType.toLowerCase().contains(AssignmentType.TYPE_Popup.toString())) {
            return JobCreator.createDownloadAboutCoursePopUpsJob(uidCourse);
        } else if (assignmentType.toLowerCase().contains(AssignmentType.TYPE_INTERACTIVE_VIDEO.toString())) {
            return JobCreator.createDownloadAboutCourseInteractiveVideoJob(uidCourse);
        }

        return null;
    }

    private AboutCourse fetchAboutCourseFromDatabase(String uidCourse) {
        return mJobModel.fetchAboutCourseFromObjectId(uidCourse);
    }

    @Override

    public void saveJson(Assignment assignment) {
        mJobModel.saveAssignment(assignment);
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return mDataObject.getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Assignment - " + mDataObject.getTitle() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_ASSIGNMENTS_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return mDataObject.getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Assignment - " + mDataObject.getTitle() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return mDataObject.getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Assignment - " + mDataObject.getTitle() + " Downloaded !!!";
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
        return in.securelearning.lil.android.base.R.drawable.assignment;
    }

    /**
     * fetch quiz from database
     *
     * @param objectId of the quiz to fetch
     * @return quiz
     */
    public Quiz fetchQuizFromDatabase(String objectId) {
        return mJobModel.fetchQuizFromObjectId(objectId);
    }

    /**
     * save assignment
     *
     * @param assignment to save
     * @return updated assignment
     */
    public Assignment saveAssignment(Assignment assignment) {
        return mJobModel.saveAssignment(assignment);
    }

    /**
     * create quiz validation job
     *
     * @param quiz to validate
     */
    public BaseValidationJob<Quiz> validateQuiz(Quiz quiz) {
        return JobCreator.createQuizValidationJob(quiz);
    }

    /**
     * create quiz download job
     *
     * @param objectId of the quiz to download
     */
    public BaseDownloadJob<Quiz> downloadQuiz(String objectId) {
        return JobCreator.createDownloadQuizJob(objectId);
    }
}
