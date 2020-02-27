package in.securelearning.lil.android.syncadapter.job.upload;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.CloudinaryFileInner;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Job to upload post data resources.
 */
public abstract class UploadPostDataResourcesJob extends BaseUploadJob<PostData> {
    /**
     * handles initialization of injector component
     * and initializes the object to upload
     *
     * @param dataObject object to upload
     */
    public UploadPostDataResourcesJob(PostData dataObject) {
        super(dataObject);

         /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void execute() {
        try {
            /*post resources and update the quiz*/
            if (uploadResourcesAndUpdatePostData()) {
                /*when post is successful call onComplete*/
                onComplete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * execute when download complete
     */
    public abstract void onComplete();

    /**
     * posts the resources on network and updates the quiz
     *
     * @return success
     * @throws IOException
     */
    public boolean uploadResourcesAndUpdatePostData() throws IOException {
        boolean success = false;

        /*count of resources to upload*/
        int countToUpload = getListOfAllResourcesToUpload(mDataObject.getPostResources());

        Log.e("res count", countToUpload + "");

        /*count of resources that have been uploaded*/
        int uploadCount = 0;

        /*for loop on each question in the quiz*/

            /*for loop on each resource of the question*/
        for (Resource resourceLocal : mDataObject.getPostResources()) {

                /*check if resource is to be uploaded*/
            if (resourceLocal.getObjectId().isEmpty() && !resourceLocal.getDeviceURL().isEmpty() && resourceLocal.getUrlMain().isEmpty()) {
                    /*update resource file data*/
                resourceLocal = updateResourceFileData(resourceLocal);

                    /*upload resource*/
//                Response<Results> response = uploadJson(resourceLocal).execute();

                Call<CloudinaryFileInner> call = uploadFile(resourceLocal);
                if (call != null) {
                    Response<CloudinaryFileInner> response = call.execute();

                    /*on successful upload*/
                    if (response.isSuccessful()) {
                        /*retrieve Resource from response*/
//                    Resource resourceNetwork = response.body().getResource();
                        CloudinaryFileInner resourceNetwork = response.body();

                        /*update local copy*/
                        resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

//                    mDataObject.getPostResources().add(resourceLocal);

                        /*save Quiz*/
                        saveJson(mDataObject);

                        /*increment count*/
                        uploadCount++;
                    } else {
                        Log.e("err postData Res Upl", response.message() + response.code());
                    }
                }
            }
        }


        if (uploadCount == countToUpload) {
            success = true;
        }

        return success;
    }

    /**
     * save quiz
     *
     * @param postData to save
     */
    public void saveJson(PostData postData) {
        mJobModel.saveLearningNetworkPostData(postData);
    }
//
//    /**
//     * upload resource json to network
//     *
//     * @param resource to upload
//     * @return network call
//     */
//    public Call<Results> uploadJson(Resource resource) {
//        /*network call to post quiz*/
//        return mNetworkModel.postResource(resource);
//    }

    public Call<CloudinaryFileInner> uploadFile(Resource resource) {
        /*network call to post quiz*/
        return mNetworkModel.postFileResource(resource);
    }

    /**
     * get the count of resources that have to be uploaded
     *
     * @param resourceList
     * @return count
     */
    public int getNumberOfResourcesToUpload(List<Resource> resourceList) {
        /*count of resources to upload*/
        int count = 0;


        /*find resources to upload*/
        for (Resource resource : resourceList) {
            /*if resource does not exist on disk*/
            if (resource.getObjectId().isEmpty() && !resource.getDeviceURL().isEmpty()) {

                /*increment count*/
                count++;
            }
        }

        /*return the count*/
        return count;
    }

    private int getListOfAllResourcesToUpload(List<Resource> resources) {
        int resourseUploadCount = 0;
        for (Resource resource : resources) {
            if (resource.getObjectId().isEmpty() && !resource.getDeviceURL().isEmpty() && resource.getUrlMain().isEmpty()) {
                resourseUploadCount++;
            }
        }
        return resourseUploadCount;
    }


    /**
     * update resource cloud url with base64 file data
     *
     * @param resource
     * @return updated resource
     * @throws FileNotFoundException
     */
    public Resource updateResourceFileData(Resource resource) throws FileNotFoundException {

//
//        File file = null;
//        if (resource.getDeviceURL().startsWith("file")) {
//            file = new File(resource.getDeviceURL().substring(8));
//        } else {
//            file = new File(resource.getDeviceURL());
//        }
//        /*get resource file*/
//
//        /*if file exists and is a file and not a directory*/
//        if (file.exists() && file.isFile()) {
//
//            /*if it is type image then add image header and base64 encoded file*/
//            if (resource.getResourceType().equals(Resource.TYPE_RESOURCE_IMAGE)) {
//                resource.setUrlMain("data:image;base64," + FileUtils.convertFileToBase64(file.getAbsolutePath()));
//            } else if (resource.getResourceType().equals(Resource.TYPE_RESOURCE_VIDEO)) {
//                resource.setUrlMain("data:video;base64," + FileUtils.convertFileToBase64(file.getAbsolutePath()));
//            }
//            /*if it is type video then add video header and base64 encoded file*/
//            else {
//                //resource.setUrlMain("data:video;base64," + FileUtils.convertFileToBase64(file.getAbsolutePath()));
//            }
//
//        }

        /*return updated resource*/
        return resource;
    }

    /**
     * update local resource with the network data
     * * @param resourceNetwork received from network
     *
     * @param resourceLocal local working copy
     * @return updated resourceLocal
     */
    public Resource updateResourceWithNetworkData(Resource resourceNetwork, Resource resourceLocal) {
        /*set sync status */
        resourceLocal.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*set object id*/
        resourceLocal.setObjectId(resourceNetwork.getObjectId());

        /*set cloud url*/
        resourceLocal.setUrlMain(resourceNetwork.getUrlMain());

        /*set size*/
        resourceLocal.setBytes(resourceNetwork.getBytes());

        /*return updated resource*/
        return resourceLocal;
    }

    public Resource updateResourceWithNetworkData(CloudinaryFileInner resourceNetwork, Resource resourceLocal) {
        /*set sync status */
        resourceLocal.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*set object id*/
        resourceLocal.setObjectId(resourceNetwork.getPublicId());

        /*set cloud url*/
        resourceLocal.setUrlMain(resourceNetwork.getUrl());
        resourceLocal.setWidth(resourceNetwork.getWidth());
        resourceLocal.setHeight(resourceNetwork.getHeight());

        /*set size*/
        resourceLocal.setBytes(resourceNetwork.getBytes());

        /*return updated resource*/
        return resourceLocal;
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

    @Override
    protected CharSequence getFailedNotificationText() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {
        return null;
    }
}
