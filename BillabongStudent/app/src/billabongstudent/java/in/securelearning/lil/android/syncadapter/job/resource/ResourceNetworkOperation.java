package in.securelearning.lil.android.syncadapter.job.resource;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.net.SocketTimeoutException;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.ftp.FtpFunctions;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static in.securelearning.lil.android.base.utils.FileUtils.checkIfResourceExists;
import static in.securelearning.lil.android.base.utils.FileUtils.saveFile;


/**
 * Network Operation on resource.
 */
public class ResourceNetworkOperation {
    public final String TAG = this.getClass().getSimpleName();
    //    private static final boolean IS_FTP_ENABLED = true;
    private static final boolean IS_FTP_ENABLED = BuildConfig.IS_FTP_ENABLED;
    /**
     * for FTP operations
     */
    @Inject
    FtpFunctions mFtpFunctions;

    @Inject
    NetworkModel mNetworkModel;

    public ResourceNetworkOperation() {
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * generate device url
     *
     * @param resource
     * @return device url
     */
    private String generateDevicePath(Context context, Resource resource) {
        String url = "";

        url = context.getFilesDir() + File.separator + resource.getDeviceURL();

        return url;
    }

    /**
     * check if resource exists on drive
     *
     * @param resource
     * @return true if exists else false
     */
    private boolean checkIfExistsOnDrive(Resource resource) {
        if (IS_FTP_ENABLED) {
            boolean status = false;
            status = mFtpFunctions.isResourceAvailable(resource);
            if (status) {
                Log.e(TAG, "resource found on network drive");
            }
            return status;
        } else return false;
    }

    /**
     * download resource from drive
     *
     * @param resource
     * @return true if download successful else false
     */
    public boolean downloadFromDrive(Resource resource) {
        if (IS_FTP_ENABLED) {
            boolean status = false;
            status = mFtpFunctions.downloadResource(resource);
            if (status) {
                Log.e(TAG, "resource downloaded from network drive");
            }
            return status;
        } else return IS_FTP_ENABLED;
    }

    /**
     * save resource to drive
     *
     * @param resourceDevicePath path on device storage
     * @param resourceDeviceUrl  relative url
     * @return true if successful else false
     */
    private boolean saveToDrive(String resourceDevicePath, String resourceDeviceUrl) {
        if (IS_FTP_ENABLED) {
            boolean status = false;
            status = mFtpFunctions.uploadResource(resourceDevicePath, resourceDeviceUrl);
            if (status) {
                Log.e(TAG, "resource uploaded to network drive");
            }
            return status;
        } else return IS_FTP_ENABLED;
    }

    /**
     * download resource
     *
     * @param context
     * @param resourceLocal to download
     * @return true if successful else false
     */
    public boolean downloadResource(Context context, final Resource resourceLocal) {
        boolean success = false;
        final String devicePath = generateDevicePath(context, resourceLocal);
        Log.e(TAG, "checking for question resource in device");
        if (!checkIfResourceExists(devicePath)) {
            try {
                Log.e(TAG, "question resource not found on device - checking in network drive");
                if (checkIfExistsOnDrive(resourceLocal)) {
                    if (downloadFromDrive(resourceLocal)) {
                        success = true;
                        return success;
                    }
                }
                if (!success) {
                    Log.e(TAG, "question resource not found on device and network drive");
                    Log.e(TAG, "downloading from cloud : " + resourceLocal.getUrlMain());

                    return downloadResourceFromNetwork(devicePath, resourceLocal);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            success = true;
        }
        return success;
    }

    public boolean downloadResourceFromNetwork(String devicePath, final Resource resourceLocal) {
        boolean success = false;
        try {
            /*download resource from network*/
            Response<ResponseBody> response = mNetworkModel.fetchFileResource(resourceLocal.getUrlMain()).execute();

            /*if network call is successful*/
            if (response != null && response.isSuccessful()) {

                /*save file to disk*/
                success = saveFile(devicePath, response.body());


                Log.e(TAG, "question resource downloaded from cloud");
                /*save to drive*/
                saveToDrive(devicePath, resourceLocal.getDeviceURL());

            } else if (response != null && response.code() == 400) {
                if (resourceLocal.getUrlMain().startsWith("http://")) {
                    resourceLocal.setUrlMain(resourceLocal.getUrlMain().replace("http://", "https://"));
                } else if (resourceLocal.getUrlMain().startsWith("https://")) {
                    resourceLocal.setUrlMain(resourceLocal.getUrlMain().replace("https://", "http://"));
                }
                Response<ResponseBody> response2 = mNetworkModel.fetchFileResource(resourceLocal.getUrlMain()).execute();

                /*if network call is successful*/
                if (response2 != null && response2.isSuccessful()) {

                    /*save file to disk*/
                    success = saveFile(devicePath, response.body());


                    Log.e(TAG, "question resource downloaded from cloud");
                    /*save to drive*/
                    saveToDrive(devicePath, resourceLocal.getDeviceURL());
                } else if (response2 != null) {
                    success = true;
                }
            } else if (response != null) {
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {
                try {
                    /*download resource from network*/
                    Response<ResponseBody> response = mNetworkModel.fetchFileResource(resourceLocal.getUrlMain()).execute();

                    /*if network call is successful*/
                    if (response != null && response.isSuccessful()) {

                        /*save file to disk*/
                        success = saveFile(devicePath, response.body());


                        Log.e(TAG, "question resource downloaded from cloud");
                        /*save to drive*/
                        saveToDrive(devicePath, resourceLocal.getDeviceURL());

                    } else if (response != null && response.code() == 400) {
                        if (resourceLocal.getUrlMain().startsWith("http://")) {
                            resourceLocal.setUrlMain(resourceLocal.getUrlMain().replace("http://", "https://"));
                        } else if (resourceLocal.getUrlMain().startsWith("https://")) {
                            resourceLocal.setUrlMain(resourceLocal.getUrlMain().replace("https://", "http://"));
                        }
                        Response<ResponseBody> response2 = mNetworkModel.fetchFileResource(resourceLocal.getUrlMain()).execute();

                        /*if network call is successful*/
                        if (response2 != null && response2.isSuccessful()) {

                            /*save file to disk*/
                            success = saveFile(devicePath, response.body());


                            Log.e(TAG, "question resource downloaded from cloud");
                            /*save to drive*/
                            saveToDrive(devicePath, resourceLocal.getDeviceURL());
                        } else if (response2 != null) {
                            success = true;
                        }
                    } else if (response != null) {
                        success = true;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                success = true;
            }
        }
        return success;
    }
}
