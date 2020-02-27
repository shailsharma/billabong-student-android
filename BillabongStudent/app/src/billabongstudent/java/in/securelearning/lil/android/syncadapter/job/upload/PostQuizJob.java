package in.securelearning.lil.android.syncadapter.job.upload;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Post Quiz Job.
 */
public class PostQuizJob extends BaseUploadJob<Quiz> {
    public final String TAG = this.getClass().getCanonicalName();


    public PostQuizJob(Quiz dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute uploading of the assignment
     */
    public void execute() {
        try {
            /*check if quiz uid tagged in assignment is not empty*/
            if (!TextUtils.isEmpty(mDataObject.getAlias())) {
                PostQuizResourcesJob job = new PostQuizResourcesJob(mDataObject) {
                    @Override
                    protected void onFail() {
                        PostQuizJob.this.showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
                    }

                    @Override
                    public void onComplete() {
                        try {
                            actionQuizResourceUploadComplete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                };
                /*execute the job*/
                job.execute();
            }


        } catch (Exception e) {
            e.printStackTrace();
            showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
        }

    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return "Quiz";
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Uploading Quiz";
    }

    @Override
    protected CharSequence getStartNotificationText() {
        return "Uploading : " + mDataObject.getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return "Quiz";
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
        return "Quiz";
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {
        return "Uploaded : " + mDataObject.getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Quiz uploaded";
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
        return R.drawable.quiz_creator_w;
    }

    /**
     * action to take when resources in quiz are posted
     *
     * @throws IOException
     */
    private void actionQuizResourceUploadComplete() throws IOException {
        /*get quiz from alias*/
        Quiz quizLocal = getQuizFromAlias(mDataObject.getAlias());

        /*post quiz*/
        if (postQuiz(quizLocal)) {
            showUploadSuccessfulNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
        } else {
            showUploadFailedNotification(NotificationUtil.UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION);
        }
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

        } else if (response.code() == 422 || response.code() == 500) {
        /*upload quiz*/
            Response<Quiz> response2 = getByAliasQuiz(quizLocal.getAlias()).execute();

        /*if upload is successful*/
            if (response2.isSuccessful()) {
//                success = true;
            /*retrieve Quiz from response*/
                Quiz quizNetwork = response2.body();

                Log.e(TAG, "quiz posted : " + quizNetwork.getObjectId());
//                Response<Quiz> response3 = getByIdQuiz(quizNetwork.getObjectId()).execute();
//                if (response3.isSuccessful()) {
//
//                    success = true;
//            /*retrieve Quiz from response*/
//                    Quiz quizNetwork2 = response3.body();
//
//                    Log.e(TAG, "quiz posted : " + quizNetwork2.getObjectId());
//
//                    /*set sync status */
//                    quizLocal.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
//                }
            /*set object id*/
                quizLocal.setObjectId(quizNetwork.getObjectId());

            /*save quiz to database*/
                saveJsonToDatabase(quizLocal);
                if (mAliasAttempts < MAX_ALIAS_ATTEMPT) {
                    PostQuizJob.this.postQuiz(quizLocal);
                    mAliasAttempts++;
                }

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
     * fetch quiz using alias
     *
     * @param alias of the quiz
     * @return quiz
     */
    public Quiz getQuizFromAlias(String alias) {
        /*fetch quiz using alias*/
        return mJobModel.fetchQuizFromAlias(alias);

    }

    public Call<Quiz> getByAliasQuiz(String alias) {
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
        if (!TextUtils.isEmpty(quiz.getObjectId())) {
            return mNetworkModel.updateQuiz(quiz);

        } else {
            return mNetworkModel.postQuiz(quiz);

        }
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
