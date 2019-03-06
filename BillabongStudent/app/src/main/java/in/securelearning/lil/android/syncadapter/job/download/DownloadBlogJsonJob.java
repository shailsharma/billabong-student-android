package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Download Blog Job.
 */
public class DownloadBlogJsonJob extends BaseDownloadJob<Blog> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadBlogJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadBlogJsonJob(String objectId, String notificationId) {
        super(objectId,notificationId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public Blog get(String objectId) {
        return new Blog();
    }


    /**
     * create validation job for the Blog
     *
     * @param blog to validate
     */
    @Override
    public void createValidationJobs(Blog blog) {
        Log.d(TAG, "Creating Validation Job for Blog : " + blog.getObjectId());
        /*create job to validate the downloaded Blog*/
        JobCreator.createBlogValidationJob(blog).execute();
    }

    /**
     * network call to fetch the Blog
     *
     * @param objectId id of the Blog to fetch
     * @return call : the network call to fetch the digitalBook
     */
    @Override
    public Call<Blog> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching DigitalBook : " + objectId);
        /*fetch DigitalBook from network*/
        return mNetworkModel.fetchBlog(objectId);
    }

    /**
     * persist the Blog
     *
     * @param blog to persist
     * @return the persisted Blog
     */
    @Override
    public Blog save(Blog blog) {
        Log.d(TAG, "Saving Blog : " + blog.getObjectId());
         /*save digitalBook to database*/
        return mJobModel.saveBlog(blog);
    }

    /**
     * get the list of object id in the Blog
     *
     * @param blog containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(Blog blog) {
         /*get id from the Blog*/
        return Collections.singletonList(blog.getObjectId());
    }

}
