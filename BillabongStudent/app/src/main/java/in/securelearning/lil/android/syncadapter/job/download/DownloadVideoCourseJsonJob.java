package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Prabodh Dhabaria on 02-02-2017.
 */
public class DownloadVideoCourseJsonJob extends BaseDownloadCourseJobWeb<VideoCourse> {
    public DownloadVideoCourseJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new VideoCourse();
    }

    @Override
    public void createValidationJobs(VideoCourse videoCourse) {
        JobCreator.createVideoCourseValidationJob(videoCourse, mAboutCourse).execute();
    }

    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        return mNetworkModel.fetchVideoCourse(objectId);
    }

    @Override
    public Call<ResponseBody> fetchAboutFromNetwork(String objectId) {
        return mNetworkModel.getVideoCourseAboutResponseBody(objectId);
    }

    @Override
    public VideoCourse save(VideoCourse videoCourse) {
        return mJobModel.saveVideoCourse(videoCourse);
    }

    @Override
    public VideoCourse get(String objectId) {
        return mJobModel.fetchVideoCourseFromObjectId(objectId);
    }

    @Override
    public List<String> getObjectIdList(VideoCourse videoCourse) {
        return Collections.singletonList(videoCourse.getObjectId());
    }
}
