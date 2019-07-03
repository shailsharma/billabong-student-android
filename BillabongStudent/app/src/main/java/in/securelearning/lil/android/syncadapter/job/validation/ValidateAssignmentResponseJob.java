package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.assignments.views.activity.StudentSummaryActivity;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.learningnetwork.events.AssignmentResponseDownloadEvent;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate assignment response job.
 */
public class ValidateAssignmentResponseJob extends BaseValidationJob<AssignmentResponse> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    private AssignmentStudent mAssignmentStudent;
    private String mAssignmentDocId = "";

    public ValidateAssignmentResponseJob(AssignmentResponse dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the assignment response
     */
    @Override
    public boolean executeValidation() {
        boolean thumbnailDownloaded = false;


        if (mDataObject.getThumbnail() != null && mDataObject.getThumbnail().getLocalUrl() != null && !mDataObject.getThumbnail().getLocalUrl().contains("file:")) {
        /*download thumbnail Resource*/
            String thumbnailUrl = mDataObject.getThumbnail().getUrl();
           /*download Resource*/

            if (thumbnailUrl != null && !thumbnailUrl.isEmpty() && !thumbnailUrl.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), thumbnailUrl);
                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    thumbnailUrl = resourceLocal.getDeviceURL();

                    mBaseFolder = mContext.getFilesDir().getAbsolutePath();
                    thumbnailUrl = "file://" + mBaseFolder + File.separator + thumbnailUrl;


                    mDataObject.getThumbnail().setLocalUrl(thumbnailUrl);
                    saveJson(mDataObject);

                    Log.e(TAG, "resources image downloaded");

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

        /*fetch assignment from database*/
        Assignment assignment = mJobModel.fetchAssignmentFromObjectId(mDataObject.getAssignmentID());

        /*if assignment is available validate it*/
        if (assignment == null || !assignment.getObjectId().equals(mDataObject.getAssignmentID())) {
            /*create download assignment job*/
            JobCreator.createDownloadAssignmentJob(mDataObject.getAssignmentID()).execute();
            assignment = mJobModel.fetchAssignmentFromObjectId(mDataObject.getAssignmentID());
        } else if (assignment != null && assignment.getObjectId().equals(mDataObject.getAssignmentID()) && assignment.getSyncStatus().equals(SyncStatus.JSON_SYNC.toString())) {
             /*create assignment validation job*/
            JobCreator.createAssignmentValidationJob(assignment).execute();
            assignment = mJobModel.fetchAssignmentFromObjectId(mDataObject.getAssignmentID());
        }
            /*if quiz is completely in sync*/
        if (assignment != null && assignment.getObjectId().equals(mDataObject.getAssignmentID()) && assignment.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
            if (thumbnailDownloaded) {
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                if (!assignment.getAssignmentType().equalsIgnoreCase(mDataObject.getAssignmentType())) {
                    mDataObject.setAssignmentType(assignment.getAssignmentType());
                }
                saveJson(mDataObject);
                mAssignmentDocId = assignment.getDocId();
                mAssignmentStudent = mJobModel.saveAssignmentStage(mDataObject);
                mRxBus.send(new AssignmentResponseDownloadEvent(mDataObject));

                assignment = null;
                return true;
            }
        }
        assignment = null;

        return false;
    }

    @Override
    public void saveJson(AssignmentResponse assignmentResponse) {
        mJobModel.saveAssignmentResponse(assignmentResponse);

    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return mDataObject.getAssignmentTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return mContext.getString(R.string.notification_download_started);
    }

    @Override
    protected CharSequence getStartNotificationText() {
        return mContext.getString(R.string.notification_download_started);
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_ASSIGNMENTS_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return mDataObject.getAssignmentTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return mContext.getString(R.string.notification_download_failed);
    }

    @Override
    protected CharSequence getFailedNotificationText() {
        return mContext.getString(R.string.notification_download_failed);
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        if (PermissionPrefsCommon.getAssignmentCreatePermission(mContext)) {
            return mContext.getString(R.string.notification_new_assignment_submission);

        } else {
            return mContext.getString(R.string.notification_new_assignment);

        }
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {
        if (PermissionPrefsCommon.getAssignmentCreatePermission(mContext)) {
            return mDataObject.getAssignmentTitle() + " Submitted by " + mDataObject.getSubmittedBy().getName();

        } else {
            return mDataObject.getAssignmentTitle() + " Assigned by " + mDataObject.getAssignedBy().getName();

        }
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        if (PermissionPrefsCommon.getAssignmentCreatePermission(mContext)) {
            return mContext.getString(R.string.notification_new_assignment_submission);

        } else {
            return mContext.getString(R.string.notification_new_assignment);

        }
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
//        if (mAppUserModel.getUserType().equals(AppUser.USERTYPE.TEACHER)) {
//            if (mDataObject.getSubmittedBy().getObjectId().equals(mAppUserModel.getObjectId())
//                    || TextUtils.isEmpty(mDataObject.getSubmittedBy().getObjectId())) {
//                return false;
//            } else if (mDataObject.getAssignedBy().getId().equals(mAppUserModel.getObjectId())) {
//                return PreferenceSettingUtilClass.isAssignment(mContext);
//            } else {
//                return false;
//            }
//
//        } else {
//            return PreferenceSettingUtilClass.isAssignment(mContext);
//
//        }
        return false;
    }

    @Override
    public int getNotificationResourceId() {
        return in.securelearning.lil.android.base.R.drawable.assignment;
    }

    @Override
    protected PendingIntent getPendingIntent() {
        if (PermissionPrefsCommon.getAssignmentCreatePermission(mContext)) {
            if (!TextUtils.isEmpty(mAssignmentDocId)) {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                stackBuilder.addParentStack(StudentSummaryActivity.class);
                stackBuilder.addNextIntent(StudentSummaryActivity.getStartStudentSummaryActivity(mContext, mAssignmentDocId, ""));
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
                return resultPendingIntent;
            } else {

                return null;
            }
        } else {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addParentStack(AssignmentDetailActivity.class);
            stackBuilder.addNextIntent(AssignmentDetailActivity.startAssignmentDetailActivity(mContext, mDataObject.getObjectId(), mAssignmentStudent.getDocId()));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            return resultPendingIntent;
        }

    }


}
