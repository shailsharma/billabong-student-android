package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.util.Log;

import java.io.File;

import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Created by Prabodh Dhabaria on 23-09-2016.
 */
public class ValidateResourceWebJob extends BaseValidationJobWeb<Resource> {

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public ValidateResourceWebJob(Resource dataObject) {
        super(dataObject);

         /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public boolean executeValidation() {

        //identify urls
        //download and replace urls

        final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;

        /*count to measure number of resources downloaded*/
        int downloadCount = 0;
        Log.e(TAG, "number of resource to download : " + mResourcesToDownload.length);

            /*download resources*/
        for (int j = 0; j < mResourcesToDownload.length; j++) {
                /*download Resource*/
            String url = mResourcesToDownload[j];
            if (url != null && !url.isEmpty() && !url.endsWith(File.separator) && url.startsWith("http")) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() &&
                        mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                    downloadCount++;

                    String fileUrl = filePathPrefix + resourceLocal.getDeviceURL();

                    ((Resource) mDataObject).setDeviceURL(resourceLocal.getDeviceURL());
                    ((Resource) mDataObject).setJson(((Resource) mDataObject).getJson().replace(url, fileUrl));

                    saveJson((Resource) mDataObject);
                    fileUrl = null;
                    Log.e(TAG, "resource downloaded");
                }
                resourceLocal = null;
            }
            url = null;
        }


        /*if all resources have been downloaded*/
        if (downloadCount == mResourcesToDownload.length) {

            /*convert json to object*/
            Resource temp = (Resource) GeneralUtils.fromGson(((Resource) mDataObject).getJson(), mDataObject.getClass());
            temp.copyFrom((Resource) mDataObject);

            /*save Resource with complete sync status
            and update status of all json's tagging this Resource*/
            updateAndSaveCompleteSyncStatus(temp);
            return true;
        }
        return false;
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((Resource) mDataObject).getName();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Resource - " + ((Resource) mDataObject).getName() + " Download Started !!!";
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((Resource) mDataObject).getName();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Resource - " + ((Resource) mDataObject).getName() + " Download Failed !!!";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((Resource) mDataObject).getName();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "Resource - " + ((Resource) mDataObject).getName() + " Download Done !!!";
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
        return in.securelearning.lil.android.base.R.drawable.bibliography;
    }

    @Override
    public void saveJson(Resource resource) {
        mJobModel.saveResource(resource);
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    @Override
    public void updateAndSaveCompleteSyncStatus(Resource resource) {
        mJobModel.updateAndSaveCompleteSyncStatus(resource);
    }

    @Override
    public boolean executeOtherValidationTasks(Resource resource) {
        return true;
    }

    @Override
    public String getStorageFolderPrefix() {
        return "";
    }

}
