package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;

import static in.securelearning.lil.android.base.utils.FileUtils.getPathFromFilePath;

/**
 * Created by Prabodh Dhabaria on 26-09-2016.
 */
public abstract class BaseValidationCourseJobWeb<T extends Course> extends BaseValidationJob<T> {
    public final String TAG = this.getClass().getName();

    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    String[] mResourcesToDownload;
    String[] mResourcesDownloaded;

    AboutCourse mAboutCourse;

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public BaseValidationCourseJobWeb(T dataObject, AboutCourse aboutCourse) {
        this(dataObject, aboutCourse, true);

    }

    public BaseValidationCourseJobWeb(T dataObject, AboutCourse aboutCourse, boolean isNotificationEnabled) {
        super(dataObject, isNotificationEnabled);
        mAboutCourse = aboutCourse;
        mResourcesToDownload = GeneralUtils.getArrayOfAllCloudinaryUrls(((T) mDataObject).getJson() + mAboutCourse.getJson());
        mResourcesDownloaded = GeneralUtils.getArrayOfAllResourceFileUrls(((T) mDataObject).getJson() + mAboutCourse.getJson());
    }

    @Override
    public boolean executeValidation() {
        try {
            updateProgressCountInNotification(mResourcesDownloaded.length);

            //identify urls
            //download and replace urls

            String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;
            String filePathPrefixAc = "file://" + mContext.getFilesDir() + File.separator + "ac";

            /*count to measure number of resources downloaded*/
            int downloadCount = 0;
            Log.e(TAG, "number of resource to download : " + mResourcesToDownload.length);

            /*download resources*/
            for (int j = 0; j < mResourcesToDownload.length; j++) {
                /*download Resource*/
                String url = mResourcesToDownload[j];
                if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() &&
                            mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                        downloadCount++;
                        updateProgressCountInNotification(mResourcesDownloaded.length + downloadCount);

                        String fileUrl = filePathPrefix + resourceLocal.getDeviceURL();
                        String fileUrlAc = filePathPrefixAc + resourceLocal.getDeviceURL();
                        File original = new File(getPathFromFilePath(fileUrl));
                        if (original.exists() && original.isFile() && original.length() > 0) {


                            ((T) mDataObject).setJson(((T) mDataObject).getJson().replace(url, fileUrl));

                            if (mAboutCourse.getJson().contains(url)) {

                                File originalCopy = new File(getPathFromFilePath(fileUrlAc));
                                if (originalCopy.exists()
                                        && originalCopy.isFile()
                                        && originalCopy.length() == original.length()) {
                                    mAboutCourse.setJson(mAboutCourse.getJson().replace(url, fileUrlAc));

                                } else {
                                    if (original.exists() && original.isFile() && FileUtils.copyFiles(fileUrl, fileUrlAc))
                                        mAboutCourse.setJson(mAboutCourse.getJson().replace(url, fileUrlAc));
                                }

                                originalCopy = null;

                            }


                            saveJson((T) mDataObject);

                            saveAboutJson(mAboutCourse);

                            Log.e(TAG, "resource downloaded");
                        }

                        fileUrl = null;
                        fileUrlAc = null;
                        original = null;

                    }
                    resourceLocal = null;
                }
                url = null;

            }

            filePathPrefix = null;
            filePathPrefixAc = null;
            /*if all resources have been downloaded*/
            if (downloadCount == mResourcesToDownload.length) {

                /*convert json to object*/
                T temp = (T) GeneralUtils.fromGson(((T) mDataObject).getJson(), mDataObject.getClass());
                temp.copyFrom((T) mDataObject);

                AboutCourse temp2 = GeneralUtils.fromGson(mAboutCourse.getJson(), mAboutCourse.getClass());
                temp2.copyFrom(mAboutCourse);

                if (executeOtherValidationTasks(temp)) {
            /*save object with complete sync status
            and update status of all json's tagging this object*/


                    temp.getReviews().setAvgRating(temp2.getReviews().getAvgRating());
                    temp.getReviews().setTotalViews(temp2.getReviews().getTotalViews());

                    updateAndSaveCompleteSyncStatus(temp);
                    updateAndSaveCompleteSyncStatus(temp2);


                    temp = null;
                    temp2 = null;
                    mResourcesDownloaded = null;
                    mResourcesToDownload = null;
                    return true;
                } else {
                    temp = null;
                    temp2 = null;
                    mResourcesDownloaded = null;
                    mResourcesToDownload = null;
                    return false;
                }
            } else {
                mResourcesDownloaded = null;
                mResourcesToDownload = null;
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveAboutJson(AboutCourse aboutCourse) {
        mJobModel.saveAboutCourse(aboutCourse);

    }

    /**
     * save and update sync status
     *
     * @param t
     */
    public abstract void updateAndSaveCompleteSyncStatus(T t);

    public void updateAndSaveCompleteSyncStatus(AboutCourse aboutCourse) {
        aboutCourse.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
        mJobModel.saveAboutCourse(aboutCourse);
        // putting doc Id empty to save about course to downloaded course collection.
        aboutCourse.setDocId("");
        if (!TextUtils.isEmpty(aboutCourse.getObjectId()))
            mJobModel.saveAboutCourseToDownloaded(aboutCourse);

    }

    /**
     * perform other validation tasks
     *
     * @param t
     */
    public abstract boolean executeOtherValidationTasks(T t);

    @Override
    protected PendingIntent getPendingIntent() {

//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
//        stackBuilder.addParentStack(CourseDetailActivity.class);
//        Class klass = mDataObject.getClass();
//        if (in.securelearning.lil.android.app.BuildConfig.FLAVOR.equals("remotech")) {
//            if (klass.equals(VideoCourse.class) || klass.equals(InteractiveVideo.class) || mDataObject.getTotalResourceCount().getVideoCourses() > 0 || mDataObject.getTotalResourceCount().getVideos() > 0) {
//                stackBuilder.addNextIntent(WebPlayerCordovaActivity.startWebPlayerIntent(mContext, mDataObject.getObjectId(),mDataObject.getMetaInformation().getSubject().getId(),mDataObject.getMetaInformation().getTopic().getId(), klass, "", false));
//            } else {
//                stackBuilder.addNextIntent(WebPlayerActivity.startWebPlayerIntent(mContext, mDataObject.getObjectId(),mDataObject.getMetaInformation().getSubject().getId(),mDataObject.getMetaInformation().getTopic().getId(), klass, "", false));
//            }
//        } else {
//            stackBuilder.addNextIntent(CourseDetailActivity.getStartActivityIntent(mContext, mDataObject.getObjectId(), mDataObject.getClass(), ""));
//
//        }
//
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
//        return resultPendingIntent;
        return null;
    }

}