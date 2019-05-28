package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import retrofit2.Response;

/**
 * Validate  User profile job.
 *
 * @author Pushkar Raj
 */
public class ValidateUserJob extends BaseValidationJob<UserProfile> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    public ValidateUserJob(UserProfile dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);

    }

    /**
     * get resource count
     *
     * @return count
     */
    private int resourceCount() {
        int count = 0;
        if (mDataObject.getThumbnail() != null) {
            if (!mDataObject.getThumbnail().getThumb().isEmpty()) count++;
            if (!mDataObject.getThumbnail().getUrl().isEmpty()) count++;
            if (!mDataObject.getThumbnail().getSecureUrl().isEmpty()) count++;

        }
        if (!TextUtils.isEmpty(mDataObject.getAssociation().getThumbnail().getUrl())) count++;
        if (!TextUtils.isEmpty(mDataObject.getAssociation().getSplashThumbnail().getUrl()))
            count++;
        return count;
    }

    /**
     * execute validation of the  User profile
     */
    @Override
    public boolean executeValidation() {
        /*fetch  User profile  from database*/


        /*if  User profile  is available validate it*/
        if (mDataObject != null && mDataObject.getObjectId().equals(mDataObject.getObjectId())) {

            final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;

            Log.e(TAG, "------------------------------------------------------------------------------------");

            Log.e(TAG, String.format("\nexecuting User profile  %s validation", mDataObject.getObjectId()));

            /*count to measure number of resources downloaded*/
            int downloadCount = 0;


            //Download User profile 's thumbnail image
            if (mDataObject.getThumbnail() != null) {
                String url = mDataObject.getThumbnail().getUrl();
                if (url != null && !url.isEmpty() && !url.endsWith(File.separator) && url.startsWith("http")) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                        downloadCount++;

                        url = filePathPrefix + resourceLocal.getDeviceURL();

                        mDataObject.getThumbnail().setUrl(url);

                        //Updating current user thumbnail with the file path
                        if (mDataObject.getObjectId().equalsIgnoreCase(Injector.INSTANCE.getComponent().appUserModel().getObjectId()))
                            Injector.INSTANCE.getComponent().appUserModel().getApplicationUser().getThumbnail().setUrl(url);

                        mJobModel.saveUserProfile(mDataObject);

                        Log.e(TAG, "user profile image downloaded");
                    }
                    resourceLocal = null;
                }

                //Download User profile 's  thumbnail image
                url = mDataObject.getThumbnail().getThumb();
                if (url != null && !url.isEmpty() && !url.endsWith(File.separator) && url.startsWith("http")) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                        downloadCount++;

                        url = filePathPrefix + resourceLocal.getDeviceURL();

                        mDataObject.getThumbnail().setThumb(url);

                        //Updating current user thumbnail with the file path
                        if (mDataObject.getObjectId().equalsIgnoreCase(Injector.INSTANCE.getComponent().appUserModel().getObjectId()))
                            Injector.INSTANCE.getComponent().appUserModel().getApplicationUser().getThumbnail().setThumb(url);

                        mJobModel.saveUserProfile(mDataObject);


                        Log.e(TAG, "user profile thumb_image downloaded");
                    }
                    resourceLocal = null;
                }

                //Download User profile 's  secure image
                url = mDataObject.getThumbnail().getSecureUrl();
                if (url != null && !url.isEmpty() && !url.endsWith(File.separator) && url.startsWith("http")) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                        downloadCount++;

                        url = filePathPrefix + resourceLocal.getDeviceURL();

                        mDataObject.getThumbnail().setSecureUrl(url);

                        //Updating current user thumbnail with the file path
                        if (mDataObject.getObjectId().equalsIgnoreCase(Injector.INSTANCE.getComponent().appUserModel().getObjectId()))
                            Injector.INSTANCE.getComponent().appUserModel().getApplicationUser().getThumbnail().setSecureUrl(url);

                        mJobModel.saveUserProfile(mDataObject);

                        Log.e(TAG, "User Profile secure_url image downloaded");
                    }
                    resourceLocal = null;
                }
            }

            Institution institute = getInstitute(mDataObject.getAssociation().getId());
            if (institute != null && institute.getThumbnail() != null && !TextUtils.isEmpty(institute.getThumbnail().getThumb())) {
                mDataObject.getAssociation().setThumbnail(institute.getThumbnail());
                String url = mDataObject.getAssociation().getThumbnail().getUrl();
                if (url != null && !url.isEmpty() && !url.endsWith(File.separator) && url.startsWith("http")) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                        downloadCount++;

                        url = filePathPrefix + resourceLocal.getDeviceURL();

                        mDataObject.getAssociation().getThumbnail().setUrl(url);
                        AppPrefs.setBannerPath(url, mContext);

                        mJobModel.saveUserProfile(mDataObject);

                        Log.e(TAG, "institute thumbnail / banner image downloaded");
                    }
                    resourceLocal = null;
                }

            }

            if (institute != null && institute.getSplashThumbnail() != null && !TextUtils.isEmpty(institute.getSplashThumbnail().getUrl())) {
                mDataObject.getAssociation().setSplashThumbnail(institute.getSplashThumbnail());
                String url = mDataObject.getAssociation().getSplashThumbnail().getThumb();
                if (url != null && !url.isEmpty() && !url.endsWith(File.separator) && url.startsWith("http")) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                        downloadCount++;

                        url = filePathPrefix + resourceLocal.getDeviceURL();

                        mDataObject.getAssociation().getSplashThumbnail().setUrl(url);
                        mDataObject.getAssociation().getSplashThumbnail().setThumb(url);
                        AppPrefs.setSplashPath(url, mContext);

                        mJobModel.saveUserProfile(mDataObject);

                        Log.e(TAG, "institute thumbnail / banner image downloaded");
                    }
                    resourceLocal = null;
                }

            }

            if (downloadCount == resourceCount()) {
            /*save userProfile with complete sync status
            and update status of all json's tagging this userProfile*/

                mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);

                return true;

            }

            Log.e(TAG, "------------------------------------------------------------------------------------");
            return false;

        }
        return false;

    }

    public Institution getInstitute(String id) {
        try {
            Response<Institution> response = mNetworkModel.getInstitute(id).execute();
            if (response != null && response.isSuccessful()) {
                Institution institution = response.body();
                return institution;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void saveJson(UserProfile userProfile) {
        mJobModel.saveUserProfile(userProfile);
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
        return NotificationUtil.DOWNLOAD_LEARNING_NETWORK_GROUP_NOTIFICATION;
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

}
