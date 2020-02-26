package in.securelearning.lil.android.syncadapter.ftp;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.inject.Inject;


import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Settings;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.ImageUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;

/**
 * Functions for FTP actions.
 */
public class FtpFunctions {
    private static final int TIMEOUT_LOW = 5000;
    private static final int TIMEOUT_HIGH = 90000;
    @Inject
    Context mContext;

    public final String TAG = this.getClass().getSimpleName();

//    public static String FTP_URL = "192.124.120.142";
    public static String FTP_URL = "192.168.0.105";
//    public static String FTP_URL = "192.168.0.103";
//    public static String FTP_URL = "192.168.43.95";
    public static String FTP_USERNAME = "admin";
    public static String FTP_PASSWORD = "admin";
    public static String FTP_DIRECTORY = "/LilResources";
    public static String FTP_RESOURCE_DIRECTORY = "/resources";
    public static int FTP_PORT = 21;

    private FTPClient mFTPClient = null;

    public FtpFunctions() {
        InjectorSyncAdapter.INSTANCE.initializeComponent();
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * connect to ftp server
     *
     * @return success
     */
    public boolean connect() {
//        Settings settings = AppPrefs.getSettings(mContext);
//        if (settings != null) {
//            FTP_URL = settings.getFtpUrl();
//            FTP_USERNAME = settings.getFtpUsername();
//            FTP_PASSWORD = settings.getFtPassword();
//        }
        return ftpConnect(FTP_URL, FTP_USERNAME, FTP_PASSWORD, FTP_PORT);
    }

    /**
     * connect to given server
     *
     * @param host     host url
     * @param username
     * @param password
     * @param port
     * @return success
     */

    public boolean ftpConnect(String host, String username, String password, int port) {
        try {

            mFTPClient = new FTPClient();

            // connecting to the host
            mFTPClient.setConnectTimeout(TIMEOUT_LOW);
            mFTPClient.connect(host, port);

            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // refreshToken using username & password
                boolean status = mFTPClient.login(username, password);
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                mFTPClient.changeWorkingDirectory(FTP_DIRECTORY);
                return status;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: could not connect to host " + host);
        }
        return false;
    }

    /**
     * disconnect from ftp server
     *
     * @return success
     */
    public boolean disconnect() {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    /**
     * check if ftp server is connected
     *
     * @return success
     */
    public boolean isConnected() {
        return mFTPClient != null && mFTPClient.isConnected();
    }

    /**
     * upload resource to ftp server
     *
     * @param resourceDevicePath path in device
     * @param resourceDeviceUrl  relative url
     * @return success
     */
    public boolean uploadResource(String resourceDevicePath, String resourceDeviceUrl) {
        boolean status = false;
        if (connect()) {
            try {
                String srcFilePath = resourceDevicePath;
                String desFilePath = resourceDeviceUrl;

                FileInputStream srcFileStream = new FileInputStream(srcFilePath);
                File desFile = new File(desFilePath);
                File desParent = desFile.getParentFile();

//                Log.e("path in drive", desParent.getAbsolutePath());
                if (makeDirectories(mFTPClient, desParent.getAbsolutePath())) {

                    status = mFTPClient.storeFile(desFile.getName(), srcFileStream);
                    srcFileStream.close();
//                    if (!status) {
//                        mFTPClient.deleteFile(desFile.getName());
//                    }
                }
                disconnect();
                return status;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "upload failed: " + e);
            }
        } else {
            status = false;
        }

        return status;
    }

    /**
     * download resource from ftp server
     *
     * @param resource to download
     * @return success
     */
    public boolean downloadResource(Resource resource) {
        boolean status = false;
        if (connect()) {

            String desFilePath = mContext.getFilesDir() + File.separator + resource.getDeviceURL();
            String srcFilePath = resource.getDeviceURL();

            File desFile = new File(desFilePath);
            File desParent = desFile.getParentFile();
            File srcFile = new File(srcFilePath);
            File srcParent = srcFile.getParentFile();

            if (!desParent.exists()) desParent.mkdirs();

            OutputStream outputStream = null;
            try {
                desFile.createNewFile();
                outputStream = new BufferedOutputStream(new FileOutputStream(desFile));
                boolean changeSuccess = changeDirectories(mFTPClient, srcParent.getAbsolutePath());
                if (changeSuccess) {
                    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                    status = mFTPClient.retrieveFile(srcFile.getName(), outputStream);
                    if (!status && desFile.length() == 0) {
                        desFile.delete();
                    }
                }
                disconnect();
                return status;
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "download failed: " + ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            status = false;
        }
        return status;

    }

    /**
     * change directory
     *
     * @param ftpClient
     * @param directoryPath
     * @return success
     * @throws IOException
     */
    public boolean changeDirectories(FTPClient ftpClient, String directoryPath) throws IOException {
        directoryPath = cleanPath(directoryPath);
        Log.e("cleaned path", directoryPath);
        String[] pathElements = directoryPath.split("/");
        if (pathElements != null && pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed) {
                    Log.d(TAG, "directory Not Found : " + singleDir);
                    return false;
                } else {
                    Log.d(TAG, "directory Found : " + singleDir);
                }
            }
        }
        return true;
    }

    /**
     * clean directory path string
     *
     * @param directoryPath
     * @return cleaned string
     */
    private String cleanPath(String directoryPath) {
        directoryPath = directoryPath.trim();

        if (!directoryPath.isEmpty()) {
            if (directoryPath.charAt(0) == '/' || File.separator.equals(directoryPath.charAt(0))) {
                directoryPath = directoryPath.substring(1);
            }
        }

        return directoryPath;
    }

    /**
     * check if resource is available
     *
     * @param resource
     * @return success
     */
    public boolean isResourceAvailable(Resource resource) {
        if (connect()) {
            File srcFile = new File(resource.getDeviceURL());
            InputStream inputStream;
            try {
                changeDirectories(mFTPClient, srcFile.getParent());
                inputStream = mFTPClient.retrieveFileStream(srcFile.getName());

                int returnCode = mFTPClient.getReplyCode();
                if (inputStream == null || returnCode == 550) {
                    disconnect();
                    return false;
                } else {
                    inputStream.close();
                    disconnect();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
                return false;
            }

        } else {
            return false;
        }
    }

    /**
     * create directories
     *
     * @param ftpClient
     * @param dirPath
     * @return success
     * @throws IOException
     */
    public boolean makeDirectories(FTPClient ftpClient, String dirPath) throws IOException {
        dirPath = cleanPath(dirPath);
        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed) {
                    boolean created = ftpClient.makeDirectory(singleDir);
                    if (created) {
                        Log.d(TAG, "CREATED directory: " + singleDir);
                        ftpClient.changeWorkingDirectory(singleDir);
                    } else {
                        Log.d(TAG, "COULD NOT create directory: " + singleDir);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<Resource> listFiles() {
        ArrayList<Resource> fileResources = new ArrayList<>();
        if (connect()) {
            try {
                changeDirectories(mFTPClient, FTP_RESOURCE_DIRECTORY);
                FTPFile[] list = mFTPClient.listFiles();

                for (int i = 0; i < list.length; i++) {
                    final FTPFile file = list[i];

                    if (file.isFile()) {
                        final String name = file.getName();

                        if (name.toLowerCase().contains("jpg") ||
                                name.toLowerCase().contains("png") ||
                                name.toLowerCase().contains("jpeg")) {
                            Resource resource = new Resource();
                            resource.setTitle(name);
                            resource.setName(name);
                            resource.setSize(file.getSize());
                            resource.setType("image");
                            resource.setUrlMain(name);
                            fileResources.add(resource);
                        } else if (name.toLowerCase().contains("mp4") ||
                                name.toLowerCase().contains("mkv") ||
                                name.toLowerCase().contains("avi")) {
                            Resource resource = new Resource();
                            resource.setTitle(name);
                            resource.setName(name);
                            resource.setSize(file.getSize());
                            resource.setType("video");
                            resource.setUrlMain(name);
                            fileResources.add(resource);
                        }

                    }

                }
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
            }

        }
        return fileResources;
    }

    private String getConnectionString(String ftpResourceDirectory) {
        //ftp://username:password@hostname/
        return "ftp://" + FTP_USERNAME + ":" + FTP_PASSWORD + "@" + FTP_URL + ftpResourceDirectory;
    }

    public boolean downloadResourceToTemp(Resource resource, String filePath, String name) {

        boolean status = false;
        if (connect()) {

            String desFilePath = filePath + File.separator + name;
            String srcFilePath = FTP_RESOURCE_DIRECTORY + File.separator + resource.getName();

            File desFile = new File(desFilePath);
            File desParent = desFile.getParentFile();
            File srcFile = new File(srcFilePath);
            File srcParent = srcFile.getParentFile();

            if (!desParent.exists()) desParent.mkdirs();

            OutputStream outputStream = null;
            try {
                desFile.createNewFile();
                outputStream = new BufferedOutputStream(new FileOutputStream(desFile));
                boolean changeSuccess = changeDirectories(mFTPClient, srcParent.getAbsolutePath());
                if (changeSuccess) {
                    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                    status = mFTPClient.retrieveFile(srcFile.getName(), outputStream);
                    if (!status && desFile.length() == 0) {
                        desFile.delete();
                    }
                }
                disconnect();
                return status;
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "download failed: " + ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            status = false;
        }
        return status;
    }
}
