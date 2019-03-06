package in.securelearning.lil.android.syncadapter.job.upload;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentMinimal;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.CloudinaryFileInner;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Post Assignment Job.
 */
public class PostAssignmentJob extends BaseUploadJob<Assignment> {
    public final String TAG = this.getClass().getCanonicalName();


    public PostAssignmentJob(Assignment dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute uploading of the assignment
     */
    public void execute() {
        showUploadStartNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
        try {
            /*check if quiz uid tagged in assignment is not empty*/
            if (!TextUtils.isEmpty(mDataObject.getQuizAlias())) {
                /*get quiz from alias in assignment*/
                Quiz quizLocal = getQuizFromAlias(mDataObject.getQuizAlias());
                if (TextUtils.isEmpty(quizLocal.getObjectId()) && !TextUtils.isEmpty(quizLocal.getAlias())) {
                    /*post the resource in quiz*/
                    PostQuizResourcesJob job = new PostQuizResourcesJob(quizLocal) {

                        @Override
                        public void onComplete() {
                            try {
                                actionQuizResourceUploadComplete();
                            } catch (IOException e) {
                                e.printStackTrace();
                                PostAssignmentJob.this.showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
                            }
                        }

                        @Override
                        protected void onFail() {
                            PostAssignmentJob.this.showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
                        }


                    };
                    /*execute the job*/
                    job.execute();
                } else {
                    if (hasQuizInternalNotificationForUpload(quizLocal)) {
                        Log.e("hasQuizINForUpload--", "Yes--uploading");
                    } else if (!TextUtils.isEmpty(quizLocal.getObjectId())) {
                        Log.e("hasQuizINForUpload--", "No");
                        mDataObject.setUidQuiz(quizLocal.getObjectId());
                        postAssignment(mDataObject);
                    }

                }

            } else if (!TextUtils.isEmpty(mDataObject.getUidCourse()) || !TextUtils.isEmpty(mDataObject.getUidResource())) {
                /*post assignment*/
                postAssignment(mDataObject);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean hasQuizInternalNotificationForUpload(Quiz quiz) throws IOException {
        InternalNotification internalNotification = mJobModel.getObjectByActionRangeAndId(quiz.getAlias(), InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD, InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD);
        if (internalNotification != null && internalNotification.getObjectId().equals(quiz.getAlias())) {
            JobCreator.createUploadQuizJob(quiz).execute();
            Quiz notificationPurgeQuiz = getQuizFromAlias(mDataObject.getQuizAlias());
            if (notificationPurgeQuiz.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                mJobModel.purgeInternalNotification(internalNotification.getDocId());
                mDataObject.setUidCourse(notificationPurgeQuiz.getObjectId());
                postAssignment(mDataObject);
            }

            return true;
        } else {
            return false;

        }
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return "Assignment";
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Uploading assignment";
    }

    @Override
    protected CharSequence getStartNotificationText() {
        return "Uploading : " + mDataObject.getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return "Assignment";
    }

    @Override
    protected CharSequence getFailedNotificationText() {
        return "Upload failed :" + mDataObject.getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Upload failed";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return "Assignment";
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {

        return "Uploaded : " + mDataObject.getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Assignment uploaded";
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

    /**
     * action to take when resources in quiz are posted
     *
     * @throws IOException
     */
    private void actionQuizResourceUploadComplete() throws IOException {
        /*get quiz from alias*/
        Quiz quizLocal = getQuizFromAlias(mDataObject.getQuizAlias());

        /*post quiz*/
        if (postQuiz(quizLocal)) {
            /*post assignment*/
            if (postAssignment(mDataObject)) {
                PostAssignmentJob.this.showUploadSuccessfulNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);

                return;
            }
        }
        PostAssignmentJob.this.showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
    }

    /**
     * post quiz
     *
     * @param quizLocal to post
     * @return success
     * @throws IOException
     */
    public boolean postQuiz(Quiz quizLocal) throws IOException {
        boolean success = false;

        /*upload quiz*/
        Response<Quiz> response = uploadJson(quizLocal).execute();

        /*if upload is successful*/
        if (response.isSuccessful()) {
            success = true;
            /*retrieve Quiz from response*/
            Quiz quizNetwork = response.body();

            Log.e(TAG, "quiz posted : " + quizNetwork.getObjectId());

            /*set sync status */
            quizLocal.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

            /*set object id*/
            quizLocal.setObjectId(quizNetwork.getObjectId());

            /*save quiz to database*/
            saveJsonToDatabase(quizLocal);

            /*set uid of quiz in assignment*/
            mDataObject.setUidQuiz(quizNetwork.getObjectId());

            /*save assignment with quiz object id*/
            saveJsonToDatabase(mDataObject);

        } else if (response.code() == 422 || response.code() == 500) {
            /*upload quiz*/
            Response<Quiz> response2 = getByAliasQuiz(quizLocal.getAlias()).execute();

            /*if upload is successful*/
            if (response2.isSuccessful()) {
//                success = true;
                /*retrieve Quiz from response*/
                Quiz quizNetwork = response2.body();

                Log.e(TAG, "quiz posted : " + quizNetwork.getObjectId());
                Response<Quiz> response3 = getByIdQuiz(quizNetwork.getObjectId()).execute();
                if (response3.isSuccessful()) {

                    success = true;
                    /*retrieve Quiz from response*/
                    Quiz quizNetwork2 = response3.body();

                    Log.e(TAG, "quiz posted : " + quizNetwork2.getObjectId());

                    /*set sync status */
                    quizLocal.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                }
                /*set object id*/
                quizLocal.setObjectId(quizNetwork.getObjectId());

                /*save quiz to database*/
                saveJsonToDatabase(quizLocal);

                /*set uid of quiz in assignment*/
                mDataObject.setUidQuiz(quizNetwork.getObjectId());

                /*save assignment with quiz object id*/
                saveJsonToDatabase(mDataObject);

            }
        } else if (response.code() == 401 && mLoginAttempts < MAX_LOGIN_ATTEMPT) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                mLoginAttempts++;
                execute();
            }
        } else if (response.code() == 401 && mLoginAttempts >= MAX_LOGIN_ATTEMPT) {
            // TODO: 07-03-2017 redirect to refreshToken page
        } else {
            Log.e("err quiz Upl", response.message());
        }

        return success;
    }

    /**
     * post assignment
     *
     * @param assignment to post
     * @return success
     * @throws IOException
     */
    public boolean postAssignment(Assignment assignment) throws IOException {
        boolean success = false;
        int countToUpload = 0;
        int uploadCount = 0;
        Log.e(TAG, "Start upload assignment-" + assignment.getAlias());


        /*get resource from explanation*/
        if (mDataObject.getThumbnail() != null && mDataObject.getThumbnail().getLocalUrl() != null) {
            Resource resourceLocal = createLocaLResource(mDataObject.getThumbnail().getLocalUrl());

            /*check if resource is to be uploaded*/
            if (!resourceLocal.getDeviceURL().isEmpty()) {

                Call<CloudinaryFileInner> call = uploadFile(resourceLocal);
                if (call != null) {
                    Response<CloudinaryFileInner> callResponse = call.execute();

                    countToUpload++;
                    /*on successful upload*/
                    if (callResponse.isSuccessful()) {
                        /*retrieve Resource from response*/
//                    Resource resourceNetwork = response.body().getResource();
                        CloudinaryFileInner resourceNetwork = callResponse.body();

                        mDataObject.getThumbnail().setLocalUrl("");
                        mDataObject.getThumbnail().setUrl(resourceNetwork.getUrl());
                        /*save Quiz*/
                        saveJsonToDatabase(mDataObject);

                        /*increment count*/
                        uploadCount++;
                    } else {
                        Log.e("err Res Upl", callResponse.message());
                    }
                }


            }
        }

//        String errorBody = "";
//        ResponseBody body=response.errorBody();
//        if (body!=null){
//            errorBody = body.string();
//        }

        Response<Assignment> response = null;
        if (uploadCount == countToUpload) {
            /*upload data object*/
            response = uploadJson(assignment).execute();

            /*if upload is successful*/
            if (response.isSuccessful()) {
                success = true;
                /*retrieve assignment*/
                Assignment assignmentNetwork = response.body();

                Log.e(TAG, "Assignment posted : " + assignmentNetwork.getObjectId());

                /*set sync status*/
                assignment.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                assignment.setObjectId(assignmentNetwork.getObjectId());
                updateObjectIdForAssignmentMinimal(assignment);
                /*save json to database*/
                saveJsonToDatabase(assignment);

                uploadToFCM(assignment);

                showUploadSuccessfulNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);


            } else if (response.code() == 422 || response.code() == 500) {
                Response<Assignment> response2 = getByAliasAssignment(assignment.getAlias()).execute();

                /*if upload is successful*/
                if (response2.isSuccessful()) {
                    success = true;
                    /*retrieve assignment*/
                    Assignment assignmentNetwork = response2.body();

                    Log.e(TAG, "Assignment posted : " + assignmentNetwork.getObjectId());

                    /*set sync status*/
                    assignment.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                    /*set object id*/
                    assignment.setObjectId(assignmentNetwork.getObjectId());
                    updateObjectIdForAssignmentMinimal(assignment);
                    /*save json to database*/
                    saveJsonToDatabase(assignment);

                    uploadToFCM(assignment);

                    showUploadSuccessfulNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
                }
            } else if (response.code() == 401 && mLoginAttempts < MAX_LOGIN_ATTEMPT) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    mLoginAttempts++;
                    execute();
                }
            } else if (response.code() == 401 && mLoginAttempts >= MAX_LOGIN_ATTEMPT) {
                // TODO: 07-03-2017 redirect to refreshToken page
                showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
            } else {
                Log.e("err Ass Upl", response.message());
                showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
            }


        }
        return success;

    }

    private void updateObjectIdForAssignmentMinimal(Assignment assignment) {
        AssignmentMinimal assignmentMinimal = mJobModel.getAssignmentMinimalByAlias(assignment.getAlias());
        assignmentMinimal.setObjectId(assignment.getObjectId());
        mJobModel.updateAssignmentMinimal(assignmentMinimal);
    }

    private void uploadToFCM(Assignment assignment) {
        if (assignment.getAssignedGroups().size() > 0) {
            try {
                Call<MessageResponse> call = mNetworkModel.sendDataUsingFCM(assignment.getAssignedGroups().get(0).getId(), "New Assignment", NetworkModel.TYPE_ASSIGNMENT, assignment.getObjectId(), null);

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


    public Call<CloudinaryFileInner> uploadFile(Resource resource) {
        /*network call to post quiz*/
        return mNetworkModel.postFileResource(resource);
    }

    /**
     * fetch quiz using alias
     *
     * @param alias of the quiz
     * @return quiz
     */
    public Quiz getQuizFromAlias(String alias) {
        /*fetch quiz using alias*/
        return mJobModel.fetchQuizFromAlias(alias);

    }

    private Resource createLocaLResource(String localUrl) {
        Resource resource = new Resource();
        resource.setDeviceURL(localUrl);
        resource.setType("image");
        return resource;
    }

    /**
     * upload assignment json to network
     *
     * @param assignment to upload
     * @return network call
     */
    public Call<Assignment> uploadJson(Assignment assignment) {
        /*network call to post assignment*/
        return mNetworkModel.postAssignment(assignment);
    }

    public Call<Assignment> getByAliasAssignment(String alias) {
        /*network call to post assignment*/
        return mNetworkModel.fetchByAliasAssignment(alias);
    }

    public Call<Quiz> getByAliasQuiz(String alias) {
        /*network call to post assignment*/
        return mNetworkModel.fetchByAliasQuiz(alias);
    }

    public Call<Quiz> getByIdQuiz(String id) {
        /*network call to post assignment*/
        return mNetworkModel.fetchQuiz(id);
    }

    /**
     * upload quiz json to network
     *
     * @param quiz to upload
     * @return network call
     */
    public Call<Quiz> uploadJson(Quiz quiz) {
        /*network call to post quiz*/
        return mNetworkModel.postQuiz(quiz);
    }

    /**
     * save assignment to database
     *
     * @param assignment to save
     */
    public void saveJsonToDatabase(Assignment assignment) {
        /*save assignment*/
        mJobModel.saveAssignment(assignment);
    }

    /**
     * save quiz to database
     *
     * @param quiz to save
     */
    public void saveJsonToDatabase(Quiz quiz) {
        /*save quiz*/
        mJobModel.saveQuiz(quiz);
    }

}
