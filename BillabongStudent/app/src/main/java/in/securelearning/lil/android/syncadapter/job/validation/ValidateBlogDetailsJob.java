package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate  blog details job.
 */
public class ValidateBlogDetailsJob extends BaseValidationJobWeb<BlogDetails> {
    private final String TAG = this.getClass().getCanonicalName();

    public ValidateBlogDetailsJob(BlogDetails dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void saveJson(BlogDetails blogDetails) {
        mJobModel.saveBlogDetails(blogDetails);
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }


    /**
     * save and update sync status
     *
     * @param blogDetails
     */
    public void updateAndSaveCompleteSyncStatus(BlogDetails blogDetails) {
        mJobModel.updateAndSaveCompleteSyncStatus(blogDetails);
    }

    @Override
    public boolean executeOtherValidationTasks(BlogDetails blogDetails) {
//        DownloadBlogCommentsJob job = (DownloadBlogCommentsJob) JobCreator.createDownloadBlogCommentsJob(blogDetails.getObjectId());
//        job.execute();
        return true;
    }

    @Override
    public String getStorageFolderPrefix() {
        return "";
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((BlogDetails) mDataObject).getBlogInstance().getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Blog - " + ((BlogDetails) mDataObject).getBlogInstance().getTitle() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_BLOG_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((BlogDetails) mDataObject).getBlogInstance().getTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Blog - " + ((BlogDetails) mDataObject).getBlogInstance().getTitle() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((BlogDetails) mDataObject).getBlogInstance().getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Blog - " + ((BlogDetails) mDataObject).getBlogInstance().getTitle() + " Downloaded !!!";
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
        return true;
    }

    @Override
    public int getNotificationResourceId() {
        return R.drawable.logo_news_g;
    }
}
