package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.BaseDataObjectWeb;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;

/**
 * Created by Prabodh Dhabaria on 26-09-2016.
 */
public abstract class BaseValidationJobWeb<T extends BaseDataObjectWeb> extends BaseValidationJob<T> {
    public final String TAG = this.getClass().getName();

    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    String[] mResourcesToDownload;
    String[] mResourcesDownloaded;

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public BaseValidationJobWeb(T dataObject) {
        super(dataObject);
        mResourcesToDownload = GeneralUtils.getArrayOfAllResourceUrls(((T) mDataObject).getJson());
        mResourcesDownloaded = GeneralUtils.getArrayOfAllResourceFileUrls(((T) mDataObject).getJson());
    }


    @Override
    public boolean executeValidation() {
        try {
            updateProgressCountInNotification(mResourcesDownloaded.length);

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
                    Resource resourceLocal = FileUtils.createResourceFromUrl(getStorageFolderPrefix() + mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() &&
                            mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                        downloadCount++;

                        updateProgressCountInNotification(mResourcesDownloaded.length + downloadCount);

                        String fileUrl = filePathPrefix + resourceLocal.getDeviceURL();

                        ((T) mDataObject).setJson(((T) mDataObject).getJson().replace(url, fileUrl));

                        saveJson((T) mDataObject);

                        Log.e(TAG, "resource downloaded");
                    }
                }

            }


        /*if all resources have been downloaded*/
            if (downloadCount == mResourcesToDownload.length) {

            /*convert json to object*/
                T temp = (T) GeneralUtils.fromGson(((T) mDataObject).getJson(), mDataObject.getClass());
                temp.copyFrom((T) mDataObject);

                if (executeOtherValidationTasks(temp)) {
            /*save object with complete sync status
            and update status of all json's tagging this object*/
                    updateAndSaveCompleteSyncStatus(temp);
                    return true;
                } else return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * save and update sync status
     *
     * @param t
     */
    public abstract void updateAndSaveCompleteSyncStatus(T t);

    /**
     * perform other validation tasks
     *
     * @param t
     */
    public abstract boolean executeOtherValidationTasks(T t);

    public abstract String getStorageFolderPrefix();


}
