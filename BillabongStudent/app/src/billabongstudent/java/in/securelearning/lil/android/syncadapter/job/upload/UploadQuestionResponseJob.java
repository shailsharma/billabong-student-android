package in.securelearning.lil.android.syncadapter.job.upload;

import android.util.Log;

import java.io.IOException;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 06-Nov-17.
 */

public class UploadQuestionResponseJob extends BaseUploadJob<QuestionResponse> {
    /**
     * handles initialization of injector component
     * and initializes the object to upload
     *
     * @param dataObject object to upload
     */
    public UploadQuestionResponseJob(QuestionResponse dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void execute() {
        try {

            /*upload data object*/
            Response<QuestionResponse> response = uploadJsonToServer(mDataObject).execute();

            /*if upload is successful*/
            if (response.isSuccessful()) {
                /*retrieve assignment response*/
                QuestionResponse questionResponse = response.body();

                Log.e(TAG, " PostResponse  posted : " + questionResponse.getObjectId());

                /*set sync status*/
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                mDataObject.setObjectId(questionResponse.getObjectId());

                /*save json to database*/
                saveJsonToDatabase(mDataObject);
            } else if (response.code() == 422 || response.code() == 500) {

            /*upload data object*/
                Response<QuestionResponse> response2 = fetchByAlias(mDataObject.getAlias()).execute();

            /*if upload is successful*/
                if (response2.isSuccessful()) {
                /*retrieve assignment response*/
                    QuestionResponse questionResponse = response2.body();

                    Log.e(TAG, " PostResponse  posted : " + questionResponse.getObjectId());

                /*set sync status*/
                    mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                    mDataObject.setObjectId(questionResponse.getObjectId());

                /*save json to database*/
                    saveJsonToDatabase(mDataObject);
                }
            } else if (response.code() == 401 && mLoginAttempts < MAX_LOGIN_ATTEMPT) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    execute();
                }
            } else if (response.code() == 401 && mLoginAttempts >= MAX_LOGIN_ATTEMPT) {
                // TODO: 07-03-2017 redirect to refreshToken page
            } else {
                Log.e(TAG, "PostResponse Res err " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Call<QuestionResponse> fetchByAlias(String alias) {
        return mNetworkModel.fetchByAliasQuestionResponse(alias);
    }

    /**
     * upload question response json to network
     *
     * @param questionResponse to upload
     * @return network call
     */
    public Call<QuestionResponse> uploadJsonToServer(QuestionResponse questionResponse) {
        Call<QuestionResponse> questionResponseCall = mNetworkModel.uploadQuestionResponse(questionResponse);
        return questionResponseCall;
    }

    /**
     * save postResponse response to database
     *
     * @param questionResponse to save
     */
    public void saveJsonToDatabase(QuestionResponse questionResponse) {
        /*save postResponse response*/
        mJobModel.saveQuestionResponse(questionResponse);
//        try {
//            Response<MessageResponse> response = uploadJsonToServerFCM(postResponse.getGroupId(), postResponse.getObjectId(), postResponse.getCreatedTime()).execute();
//            if (response.isSuccessful()) {
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
    protected CharSequence getStartNotificationText() {
        return null;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getFailedNotificationText() {
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
    protected CharSequence getSuccessfulNotificationText() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return null;
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
        return 0;
    }
}
