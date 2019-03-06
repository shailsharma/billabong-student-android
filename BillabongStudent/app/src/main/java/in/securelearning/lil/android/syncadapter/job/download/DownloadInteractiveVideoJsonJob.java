package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Prabodh Dhabaria on 02-02-2017.
 */
public class DownloadInteractiveVideoJsonJob extends BaseDownloadCourseJobWeb<InteractiveVideo> {
    public DownloadInteractiveVideoJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new InteractiveVideo();
    }

    @Override
    public void createValidationJobs(InteractiveVideo interactiveVideo) {
        JobCreator.createInteractiveVideoValidationJob(interactiveVideo, mAboutCourse).execute();
    }

    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        return mNetworkModel.fetchInteractiveVideo(objectId);
    }

    @Override
    public Call<ResponseBody> fetchAboutFromNetwork(String objectId) {
        return mNetworkModel.getInteractiveVideoAboutResponseBody(objectId);
    }

    @Override
    public InteractiveVideo save(InteractiveVideo interactiveVideo) {
        return mJobModel.saveInteractiveVideo(interactiveVideo);
    }

    @Override
    public InteractiveVideo get(String objectId) {
        return mJobModel.fetchInteractiveVideoFromObjectId(objectId);
    }

    @Override
    public List<String> getObjectIdList(InteractiveVideo interactiveVideo) {
        return Collections.singletonList(interactiveVideo.getObjectId());
    }
}
