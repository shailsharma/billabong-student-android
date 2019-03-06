package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.dataobjects.BlogReview;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadJobWeb;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate BlogReview Job.
 */
public class ValidateBlogReviewJob extends BaseValidationJob<BlogReview> {

    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    private final String TAG = this.getClass().getCanonicalName();

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public ValidateBlogReviewJob(BlogReview dataObject) {
        super(dataObject);

         /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);

        // mResourceCount=resourceCount();
    }

    @Override
    public boolean executeValidation() {

         /*if  BlogReview is available validate it*/
        if (mDataObject != null && mDataObject.getObjectId().equals(mDataObject.getObjectId())) {

            final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;

            Log.e(TAG, "------------------------------------------------------------------------------------");

            Log.e(TAG, String.format("\nexecuting blog Review %s validation", mDataObject.getObjectId()));


            mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);


//            int downloadCount = 0;
//
//
//            if(mDataObject.getResources()!=null && mDataObject.getResources().size()>0)
//            {
//                for(Resource resource:mDataObject.getResources())
//                {
//                    fetchResource(resource.getObjectId());
//                }
//            }
//
//
//            if (downloadCount == mResourceCount && success) {
//                mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);
//            }


            Log.e(TAG, "------------------------------------------------------------------------------------");
            return true;

        }

        return false;
        //Download groups Post and post response
        //JobCreator.createDownloadGroupPostNResponseJob(mDataObject.getObjectId()).execute();

    }

    @Override
    public void saveJson(BlogReview blogReview) {

    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
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
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_BLOG_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
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
     * fetch resource
     *
     * @param id
     */
    private void fetchResource(String id) {
        BaseDownloadJobWeb job = JobCreator.createDownloadResourceJob(id);
        job.execute();
    }


    public int getCountOfResourcesToBeDownload(Blog blog) {
        int downloadResCount = 0;
        /*Fetch all members of groups and their pic*/
        for (Resource resource : blog.getResources()) {
            if (resource.getObjectId() != null) {
                downloadResCount++;
            }
        }
        return downloadResCount;
    }

}
