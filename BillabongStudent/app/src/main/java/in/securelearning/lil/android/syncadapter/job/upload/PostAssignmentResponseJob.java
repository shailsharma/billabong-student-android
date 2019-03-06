package in.securelearning.lil.android.syncadapter.job.upload;

import android.util.Log;

import java.io.IOException;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Post Assignment Response Job.
 */
public class PostAssignmentResponseJob extends BaseUploadJob<AssignmentResponse> {
    public final String TAG = this.getClass().getCanonicalName();

    public PostAssignmentResponseJob(AssignmentResponse dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute uploading of assignment response
     */
    public void execute() {
        showUploadStartNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
        try {

            /*upload data object*/
            Response<AssignmentResponse> response = uploadJsonToServer(mDataObject).execute();

            /*if upload is successful*/
            if (response.isSuccessful()) {
                /*retrieve assignment response*/

                AssignmentResponse assignmentResponse = response.body();

                Log.e(TAG, "Assignment Response posted : " + assignmentResponse.getObjectId());

                /*set sync status*/
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                if (assignmentResponse.getObjectId() != null && !assignmentResponse.getObjectId().isEmpty()) {
                /*set object id*/
                    mDataObject.setObjectId(assignmentResponse.getObjectId());
                    /*save json to database*/
                    saveJsonToDatabase(mDataObject);
                    uploadToFCM(mDataObject);
                    showUploadSuccessfulNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
                }


            } else {
                Log.e(TAG, "Assign Res err " + response.message());
                showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
        }
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return "Assignment Submission";
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return null;
    }

    @Override
    protected CharSequence getStartNotificationText() {
        return "Uploading Submission of : " + mDataObject.getAssignmentTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return "Assignment Submission";
    }

    @Override
    protected CharSequence getFailedNotificationText() {
        return "Upload failed :" + mDataObject.getAssignmentTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Submission upload failed";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return "Assignment Submission";
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {

        return "Uploaded : " + mDataObject.getAssignmentTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Assignment Submission uploaded";
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
        return true;
    }

    @Override
    public int getNotificationResourceId() {
        return R.drawable.assignment_w;
    }

    private void uploadToFCM(AssignmentResponse assignmentResponse) {
        if (assignmentResponse.getAssignedGroup().size() > 0) {
            try {
                Call<MessageResponse> call = mNetworkModel.sendDataUsingFCM(assignmentResponse.getAssignedGroup().get(0).getId(), assignmentResponse.getObjectId(), NetworkModel.TYPE_ASSIGNMENT_RESPONSE, assignmentResponse.getObjectId(), null);

                Response<MessageResponse> response = call.execute();
                if (response.isSuccessful()) {
                    if (response.body().getMessage() != null) {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * upload assignment response json to network
     *
     * @param assignmentResponse to upload
     * @return network call
     */
    public Call<AssignmentResponse> uploadJsonToServer(AssignmentResponse assignmentResponse) {
        /*network call to post assignment response*/
        return mNetworkModel.postAssignmentResponse(assignmentResponse);
    }

    /**
     * save assignment response to database
     *
     * @param assignmentResponse to save
     */
    public void saveJsonToDatabase(AssignmentResponse assignmentResponse) {
        /*save assignment response*/
        mJobModel.saveAssignmentResponse(assignmentResponse);
    }

}
