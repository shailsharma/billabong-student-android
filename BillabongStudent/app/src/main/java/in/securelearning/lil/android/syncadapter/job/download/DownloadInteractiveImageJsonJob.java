package in.securelearning.lil.android.syncadapter.job.download;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download InteractiveImage Job.
 */
public class DownloadInteractiveImageJsonJob  extends BaseDownloadCourseJobWeb<InteractiveImage> {
    private final String TAG = this.getClass().getCanonicalName();
    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadInteractiveImageJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new InteractiveImage();
    }

    @Override
    public void createValidationJobs(InteractiveImage interactiveImage) {
        Log.d(TAG, "Creating Validation Job for Interactive Image : " + interactiveImage.getObjectId());
        /*create job to validate the downloaded InteractiveImage*/
        JobCreator.createInteractiveImageValidationJob(interactiveImage,mAboutCourse).execute();

    }

    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching InteractiveImage : " + objectId);
        /*fetch InteractiveImage from network*/
        return mNetworkModel.fetchInteractiveImage(objectId);
    }

    @Override
    public Call<ResponseBody> fetchAboutFromNetwork(String objectId) {
        return mNetworkModel.getInteractiveImageAboutResponseBody(objectId);
    }

    @Override
    public InteractiveImage save(InteractiveImage interactiveImage) {
        Log.d(TAG, "Saving InteractiveImage : " + interactiveImage.getObjectId());
         /*save InteractiveImage to database*/
        return mJobModel.saveInteractiveImage(interactiveImage);
    }

    @Override
    public InteractiveImage get(String objectId) {
        return mJobModel.fetchInteractiveImageFromObjectId(objectId);
    }

    @Override
    public List<String> getObjectIdList(InteractiveImage interactiveImage) {
          /*get id from the InteractiveImage*/
        return Collections.singletonList(interactiveImage.getObjectId());
    }
}
