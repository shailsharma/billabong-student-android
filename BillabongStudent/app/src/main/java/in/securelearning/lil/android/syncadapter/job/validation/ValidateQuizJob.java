package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;


/**
 * Validate quiz job.
 */
public class ValidateQuizJob extends BaseValidationJob<Quiz> {
    public final String TAG = this.getClass().getName();

    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    public ValidateQuizJob(Quiz dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the quiz
     */
    @Override
    public boolean executeValidation() {
        Log.e(TAG, "------------------------------------------------------------------------------------");
        Log.e(TAG, "------------------------------------------------------------------------------------");
        Log.e(TAG, "------------------------------------------------------------------------------------");
        Log.e(TAG, String.format("\nexecuting quiz %s validation", mDataObject.getObjectId()));
        /*list of resources to download*/
        List<Resource> listToDownload = getListOfAllResourcesToDownload(mDataObject);

        /*count to measure number of resources downloaded*/
        int downloadCount = 0;
        Log.e(TAG, "number of resource to download : " + listToDownload.size());
//        final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;
        /*download resources*/
        for (Question question : mDataObject.getQuestions()) {

            /*download question attachments*/

            for (Resource resource : question.getResources()) {

                String url = resource.getUrlMain();
           /*download Resource*/

                if (!TextUtils.isEmpty(url) && !url.endsWith(File.separator)) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                        downloadCount++;

                        url = mBaseFolder + File.separator + resourceLocal.getDeviceURL();

                        //     mDataObject.setPostResources(mDataObject.getPostResources().set(i,url));
                        resource.setDeviceURL(url);
                        saveJson(mDataObject);

                        Log.e(TAG, "resources image downloaded");
                    }
                    resourceLocal = null;
                }

                url = null;
            }


            for (QuestionHint hint : question.getQuestionHints()) {

                String url = hint.getHintResource().getUrlMain();
           /*download Resource*/

                if (!TextUtils.isEmpty(url) && !url.endsWith(File.separator)) {

                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                        downloadCount++;

                        url = mBaseFolder + File.separator + resourceLocal.getDeviceURL();

                        //     mDataObject.setPostResources(mDataObject.getPostResources().set(i,url));
                        hint.getHintResource().setDeviceURL(url);
                        saveJson(mDataObject);

                        Log.e(TAG, "resources image downloaded");
                    }
                    resourceLocal = null;
                }

                url = null;
            }



            /*download hints*/

            /*download choices*/
            for (QuestionChoice choice : question.getQuestionChoices()) {
                /*download Resource*/
                String url = choice.getChoiceResource().getUrlMain();
           /*download Resource*/

                if (!TextUtils.isEmpty(url) && !url.endsWith(File.separator)) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                        downloadCount++;

                        url = mBaseFolder + File.separator + resourceLocal.getDeviceURL();

                        //     mDataObject.setPostResources(mDataObject.getPostResources().set(i,url));
                        choice.getChoiceResource().setDeviceURL(url);
                        saveJson(mDataObject);

                        Log.e(TAG, "resources image downloaded");
                    }
                    resourceLocal = null;
                }
                url = null;
            }

            /*download Explanation Resource*/
            String url = question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getUrlMain();
           /*download Resource*/

            if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                    downloadCount++;

                    url = mBaseFolder + File.separator + resourceLocal.getDeviceURL();

                    //     mDataObject.setPostResources(mDataObject.getPostResources().set(i,url));
                    question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().setDeviceURL(url);
                    saveJson(mDataObject);

                    Log.e(TAG, "resources image downloaded");
                }
                resourceLocal = null;
            }
            url = null;
        }
        int resourcesToBeDownloded = listToDownload.size();

        /*download thumbnail Resource*/
        String thumbnailUrl = mDataObject.getThumbnail().getUrl();
           /*download Resource*/

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty() && !thumbnailUrl.endsWith(File.separator)) {
            Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), thumbnailUrl);
            resourcesToBeDownloded++;
            if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                downloadCount++;

                thumbnailUrl = resourceLocal.getDeviceURL();

                mBaseFolder = mContext.getFilesDir().getAbsolutePath();
                thumbnailUrl = "file://" + mBaseFolder + File.separator + thumbnailUrl;

                mDataObject.getThumbnail().setLocalUrl(thumbnailUrl);
                saveJson(mDataObject);

                Log.e(TAG, "resources image downloaded");
            }
            resourceLocal = null;
        }


        thumbnailUrl = null;
        listToDownload.clear();
        listToDownload = null;
        /*if all resources have been downloaded*/
        if (downloadCount == resourcesToBeDownloded)

        {
            /*save quiz with complete sync status
            and update status of all json's tagging this quiz*/
            updateAndSaveCompleteSyncStatus(mDataObject);
            return true;
        }

        Log.e(TAG, "------------------------------------------------------------------------------------");
        Log.e(TAG, "------------------------------------------------------------------------------------");
        Log.e(TAG, "------------------------------------------------------------------------------------");

        return false;
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
        return NotificationUtil.DOWNLOAD_ASSIGNMENTS_GROUP_NOTIFICATION;
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
        return in.securelearning.lil.android.base.R.drawable.assignment;
    }

    /**
     * save quiz
     *
     * @param quiz to save
     */
    public void saveJson(Quiz quiz) {
        mJobModel.saveQuiz(quiz);
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    /**
     * save and update sync status
     *
     * @param quiz
     */
    public void updateAndSaveCompleteSyncStatus(Quiz quiz) {
        mJobModel.updateAndSaveCompleteSyncStatus(quiz);
    }

    /**
     * get list of all resources to download
     *
     * @param quiz
     * @return list of  resources
     */
    public List<Resource> getListOfAllResourcesToDownload(Quiz quiz) {
         /*list of all resources tagged in quiz and its questions */

        return GeneralUtils.getListOfAllResources(quiz);
    }


}
