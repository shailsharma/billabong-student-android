package in.securelearning.lil.android.syncadapter.job.upload;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Upload Blog Comment Job.
 */
public class UploadBlogCommentJob extends BaseUploadJob<BlogComment> {
    public final String TAG = this.getClass().getCanonicalName();

    public UploadBlogCommentJob(BlogComment dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute uploading of BlogComment
     */
    public void execute() {
        try {

            if (!TextUtils.isEmpty(mDataObject.getAlias())) {
            /*upload data object*/
                Response<BlogComment> response = uploadJsonToServer(mDataObject).execute();

            /*if upload is successful*/
                if (response.isSuccessful()) {
                /*retrieve BlogComment*/
                    BlogComment blogComment = response.body();

                    Log.e(TAG, " BlogComment  posted : " + blogComment.getObjectId());

                /*set sync status*/
                    mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                    mDataObject.setObjectId(blogComment.getObjectId());

                /*save json to database*/
                    saveJsonToDatabase(mDataObject);
                } else {
                    Log.e(TAG, "BlogComment Res err " + response.message());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    /**
     * upload BlogComment json to network
     *
     * @param blogComment to upload
     * @return network call
     */
    public Call<BlogComment> uploadJsonToServer(BlogComment blogComment) {
        /*network call to upload BlogComment*/
        return mNetworkModel.uploadBlogComment(blogComment);
    }

    /**
     * save BlogComment to database
     *
     * @param blogComment to save
     */
    public void saveJsonToDatabase(BlogComment blogComment) {
        /*save BlogComment*/
        mJobModel.saveBlogComment(blogComment);
    }

}
