package in.securelearning.lil.android.syncadapter.job.upload;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.CloudinaryFileInner;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Job to upload quiz resources.
 */
public abstract class PostQuizResourcesJob extends BaseUploadJob<Quiz> {
    /**
     * handles initialization of injector component
     * and initializes the object to upload
     *
     * @param dataObject object to upload
     */
    public PostQuizResourcesJob(Quiz dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void execute() {
        try {
            /*post resources and update the quiz*/
            if (postResourcesAndUpdateQuiz()) {
                /*when post is successful call onComplete*/
                onComplete();
            } else {
                onFail();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onFail();

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
    public boolean postResourcesAndUpdateQuiz() throws IOException {
        boolean success = false;

        /*count of resources to upload*/
        int countToUpload = 0;
        Log.e("res count", countToUpload + "");

        /*count of resources that have been uploaded*/
        int uploadCount = 0;

        /*for loop on each question in the quiz*/
        for (Question question : mDataObject.getQuestions()) {

            /*for loop on each resource of the question*/
            for (Resource resourceLocal : question.getResources()) {

                /*check if resource is to be uploaded*/
                if (resourceLocal.getObjectId().isEmpty() && !resourceLocal.getDeviceURL().isEmpty()) {
                    /*update resource file data*/
                    resourceLocal = updateResourceFileData(resourceLocal);

                    countToUpload++;
                    /*upload resource*/
//                    Response<Results> response = uploadJson(resourceLocal).execute();

                    Call<CloudinaryFileInner> call = uploadFile(resourceLocal);
                    if (call != null) {
                        Response<CloudinaryFileInner> response = call.execute();
                        /*on successful upload*/
                        if (response.isSuccessful()) {
                            /*retrieve Resource from response*/
//                        Resource resourceNetwork = response.body().getResource();
                            CloudinaryFileInner resourceNetwork = response.body();

                            /*update local copy*/
                            resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

                            /*save Quiz*/
                            saveJson(mDataObject);

                            /*increment count*/
                            uploadCount++;
                        } else if (response != null && (response.code() == 401 || response.code() == 403)) {
                            if (SyncServiceHelper.refreshToken(mContext)) {
                                Call<CloudinaryFileInner> call2 = call.clone();
                                response = call2.execute();
                                if (response.isSuccessful()) {
                                    CloudinaryFileInner resourceNetwork = response.body();

                                    resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

                                    saveJson(mDataObject);

                                    uploadCount++;
                                }
                            }
                        } else {
                            Log.e("err Res Upl", response.message());
                        }
                    }
                }
            }

            /*for loop on each hint in the question*/
            for (QuestionHint hint : question.getQuestionHints()) {

                Resource resourceLocal = hint.getHintResource();

                /*check if resource is to be uploaded*/
                if (resourceLocal != null && resourceLocal.getObjectId().isEmpty() && !resourceLocal.getDeviceURL().isEmpty()) {
                    /*update resource file data*/
                    resourceLocal = updateResourceFileData(resourceLocal);
                    countToUpload++;
                    /*upload resource*/
//                    Response<Results> response = uploadJson(resourceLocal).execute();

                    Call<CloudinaryFileInner> call = uploadFile(resourceLocal);
                    if (call != null) {
                        Response<CloudinaryFileInner> response = call.execute();

                        /*on successful upload*/
                        if (response.isSuccessful()) {
                            /*retrieve Resource from response*/
//                        Resource resourceNetwork = response.body().getResource();
                            CloudinaryFileInner resourceNetwork = response.body();
                            /*update local copy*/
                            resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

                            /*save Quiz*/
                            saveJson(mDataObject);

                            /*increment count*/
                            uploadCount++;
                        } else if (response != null && (response.code() == 401 || response.code() == 403)) {
                            if (SyncServiceHelper.refreshToken(mContext)) {
                                Call<CloudinaryFileInner> call2 = call.clone();
                                response = call2.execute();
                                if (response.isSuccessful()) {
                                    CloudinaryFileInner resourceNetwork = response.body();

                                    resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

                                    saveJson(mDataObject);

                                    uploadCount++;
                                }
                            }
                        } else {
                            Log.e("err Res Upl", response.message());
                        }
                    }
                }
            }

            /*for loop on each choice in question*/
            for (QuestionChoice choice : question.getQuestionChoices()) {
                Resource resourceLocal = choice.getChoiceResource();

                /*check if resource is to be uploaded*/
                if (resourceLocal != null && resourceLocal.getObjectId().isEmpty() && !resourceLocal.getDeviceURL().isEmpty()) {
                    /*update resource file data*/
                    resourceLocal = updateResourceFileData(resourceLocal);
                    countToUpload++;
                    /*upload resource*/
//                    Response<Results> response = uploadJson(resourceLocal).execute();

                    Call<CloudinaryFileInner> call = uploadFile(resourceLocal);
                    if (call != null) {
                        Response<CloudinaryFileInner> response = call.execute();

                        /*on successful upload*/
                        if (response.isSuccessful()) {
                            /*retrieve Resource from response*/
//                        Resource resourceNetwork = response.body().getResource();
                            CloudinaryFileInner resourceNetwork = response.body();

                            /*update local copy*/
                            resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

                            /*save Quiz*/
                            saveJson(mDataObject);

                            /*increment count*/
                            uploadCount++;
                        } else if (response != null && (response.code() == 401 || response.code() == 403)) {
                            if (SyncServiceHelper.refreshToken(mContext)) {
                                Call<CloudinaryFileInner> call2 = call.clone();
                                response = call2.execute();
                                if (response.isSuccessful()) {
                                    CloudinaryFileInner resourceNetwork = response.body();

                                    resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

                                    saveJson(mDataObject);

                                    uploadCount++;
                                }
                            }
                        } else {
                            Log.e("err Res Upl", response.message());
                        }
                    }
                }
            }

            /*get resource from explanation*/
            Resource resourceLocal = question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource();

            /*check if resource is to be uploaded*/
            if (resourceLocal != null && resourceLocal.getObjectId().isEmpty() && !resourceLocal.getDeviceURL().isEmpty()) {
                /*update resource file data*/
                resourceLocal = updateResourceFileData(resourceLocal);
                countToUpload++;
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

                        /*save Quiz*/
                        saveJson(mDataObject);

                        /*increment count*/
                        uploadCount++;
                    } else if (response != null && (response.code() == 401 || response.code() == 403)) {
                        if (SyncServiceHelper.refreshToken(mContext)) {
                            Call<CloudinaryFileInner> call2 = call.clone();
                            response = call2.execute();
                            if (response.isSuccessful()) {
                                CloudinaryFileInner resourceNetwork = response.body();

                                resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

                                saveJson(mDataObject);

                                uploadCount++;
                            }
                        }
                    } else {
                        Log.e("err Res Upl", response.message());
                    }
                }
            }

        }


// TODO: 14-11-2017  check thumbnail for object id
        /*get resource from explanation*/
        if (mDataObject.getThumbnail() != null) {
            Resource resourceLocal = createLocaLResource(mDataObject.getThumbnail().getLocalUrl());

            /*check if resource is to be uploaded*/
            if (!resourceLocal.getDeviceURL().isEmpty()) {
                /*update resource file data*/
                //resourceLocal = updateResourceFileData(resourceLocal);
                countToUpload++;
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

                        mDataObject.getThumbnail().setLocalUrl("");
                        mDataObject.getThumbnail().setUrl(resourceNetwork.getUrl());
                        /*save Quiz*/
                        saveJson(mDataObject);

                        /*increment count*/
                        uploadCount++;
                    } else if (response != null && (response.code() == 401 || response.code() == 403)) {
                        if (SyncServiceHelper.refreshToken(mContext)) {
                            Call<CloudinaryFileInner> call2 = call.clone();
                            response = call2.execute();
                            if (response.isSuccessful()) {
                                CloudinaryFileInner resourceNetwork = response.body();

                                resourceLocal = updateResourceWithNetworkData(resourceNetwork, resourceLocal);

                                saveJson(mDataObject);

                                uploadCount++;
                            }
                        }
                    } else {
                        Log.e("err Res Upl", response.message());
                    }
                }
            }
        }


        if (uploadCount == countToUpload) {
            success = true;
        }

        return success;
    }

    private Resource createLocaLResource(String localUrl) {
        Resource resource = new Resource();
        resource.setDeviceURL(localUrl);
        resource.setType("image");
        return resource;
    }

    /**
     * save quiz
     *
     * @param quiz to save
     */
    public void saveJson(Quiz quiz) {
        mJobModel.saveQuiz(quiz);
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
     * @param quiz
     * @return count
     */
    public int getNumberOfResourcesToUpload(Quiz quiz) {
        /*count of resources to upload*/
        int count = 0;

        /*list of all resources tagged in quiz and its questions */
        List<Resource> list = GeneralUtils.getListOfAllResources(quiz);

        /*find resources to upload*/
        for (Resource resource : list) {
            /*if resource does not exist on disk*/
            if (resource.getObjectId().isEmpty() || !resource.getDeviceURL().isEmpty()) {
                /*increment count*/
                count++;
            }
        }

        /*return the count*/
        return count;
    }

    /**
     * update resource cloud url with base64 file data
     *
     * @param resource
     * @return updated resource
     * @throws FileNotFoundException
     */
    public Resource updateResourceFileData(Resource resource) throws FileNotFoundException {

//        /*get resource file*/
//        File file = new File(mContext.getFilesDir() + File.separator + resource.getDeviceURL());
//
//        /*if file exists and is a file and not a directory*/
//        if (file.exists() && file.isFile()) {
//
//            /*if it is type image then add image header and base64 encoded file*/
//            if (resource.getResourceType().equals(Resource.TYPE_RESOURCE_IMAGE)) {
//                resource.setUrlMain("data:image;base64," + FileUtils.convertFileToBase64(file.getAbsolutePath()));
//            }
//            /*if it is type video then add video header and base64 encoded file*/
//            else {
//                resource.setUrlMain("data:video;base64," + FileUtils.convertFileToBase64(file.getAbsolutePath()));
//            }
//
//        }

        /*return updated resource*/
        return resource;
    }

    /**
     * update local resource with the network data
     *
     * @param resourceNetwork received from network
     * @param resourceLocal   local working copy
     * @return updated resourceLocal
     */
    public Resource updateResourceWithNetworkData(Resource resourceNetwork, Resource resourceLocal) {
        /*set sync status */
        resourceLocal.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*set object id*/
        resourceLocal.setObjectId(resourceNetwork.getObjectId());
        resourceLocal.setThumb(resourceNetwork.getThumb());

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
        resourceLocal.setObjectId(resourceNetwork.getId());

        /*set cloud url*/
        resourceLocal.setUrlMain(resourceNetwork.getUrl());
        resourceLocal.setThumb(resourceNetwork.getThumb());
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
