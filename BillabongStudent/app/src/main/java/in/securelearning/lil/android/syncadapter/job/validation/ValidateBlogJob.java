package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadJobWeb;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate Blog Job.
 */


public class ValidateBlogJob extends BaseValidationJob<Blog> {

    private final int mResourceCount;
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    private final String TAG = this.getClass().getCanonicalName();

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public ValidateBlogJob(Blog dataObject) {
        super(dataObject);

         /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);

        mResourceCount = resourceCount();
    }

    @Override
    public boolean executeValidation() {

         /*if  blog is available validate it*/
        if (mDataObject != null && mDataObject.getObjectId().equals(mDataObject.getObjectId())) {

            final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;

            Log.e(TAG, "------------------------------------------------------------------------------------");

            Log.e(TAG, String.format("\nexecuting blog %s validation", mDataObject.getObjectId()));

            int downloadCount = 0;


            if (mDataObject.getResources() != null && mDataObject.getResources().size() > 0) {
                for (Resource resource : mDataObject.getResources()) {
                    fetchResource(resource.getObjectId());
                }
            }

            //Download blog url image
            String url = mDataObject.getThumbnail().getUrl();
            if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                    downloadCount++;

                    url = filePathPrefix + resourceLocal.getDeviceURL();

                    mDataObject.getThumbnail().setUrl(url);

                    mJobModel.saveBlog(mDataObject);

                    Log.e(TAG, "Blog url_image downloaded");
                }
            }

            //Download blog thumbnail image
            url = mDataObject.getThumbnail().getThumb();
            if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                    downloadCount++;

                    url = filePathPrefix + resourceLocal.getDeviceURL();

                    mDataObject.getThumbnail().setThumb(url);

                    mJobModel.saveBlog(mDataObject);

                    Log.e(TAG, "Blog thumb_image downloaded");
                }
            }

            //Download blog secure image
            url = mDataObject.getThumbnail().getSecureUrl();
            if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                    downloadCount++;

                    url = filePathPrefix + resourceLocal.getDeviceURL();

                    mDataObject.getThumbnail().setSecureUrl(url);

                    mJobModel.saveBlog(mDataObject);

                    Log.e(TAG, "Blog secure_url image downloaded");
                }
            }


            String completeSyncStatus = SyncStatus.COMPLETE_SYNC.toString();
            boolean success = true;
            if (mDataObject.getResources() != null && mDataObject.getResources().size() > 0) {
                for (Resource resource : mDataObject.getResources()) {
                    success = success && mJobModel.fetchResourceFromObjectId(resource.getObjectId()).getSyncStatus().equals(completeSyncStatus);
                }
            }

            if (downloadCount == mResourceCount && success) {
                mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);
                return true;
            }


            Log.e(TAG, "------------------------------------------------------------------------------------");


        }

        return false;
        //Download groups Post and post response
        //JobCreator.createDownloadGroupPostAndResponseJob(mDataObject.getObjectId()).execute();

    }

    @Override
    public void saveJson(Blog blog) {

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
     * get resource count
     *
     * @return count
     */
    private int resourceCount() {
        int count = 0;
        if (!mDataObject.getThumbnail().getThumb().isEmpty()) count++;
        if (!mDataObject.getThumbnail().getUrl().isEmpty()) count++;
        if (!mDataObject.getThumbnail().getSecureUrl().isEmpty()) count++;
        return count;
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
