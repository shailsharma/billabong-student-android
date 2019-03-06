package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download BlogDetails Job.
 */


public class DownloadBlogDetailsJsonJob extends BaseDownloadJobWeb<BlogDetails> {
    private final String TAG = this.getClass().getCanonicalName();

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadBlogDetailsJsonJob(String objectId) {
        super(objectId, "");

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadBlogDetailsJsonJob(String objectId, String notificationId) {
        super(objectId, notificationId);
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new BlogDetails();
    }

    @Override
    protected BlogDetails updateObjectContent(BlogDetails dataObject, BlogDetails temp) {
        dataObject.setBlogReviewInstance(temp.getBlogReviewInstance());
        return dataObject;
    }

    @Override
    public void createValidationJobs(BlogDetails blogDetails) {
        Log.d(TAG, "Creating Validation Job for BlogDetails : " + blogDetails.getObjectId());
        /*create job to validate the downloaded BlogDetails*/

        JobCreator.createBlogDetailsValidationJob(blogDetails).execute();

    }

    /**
     * network call to fetch the blogDetails
     *
     * @param objectId id of the blogDetails to fetch
     * @return call : the network call to fetch the blogDetails
     */
    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching BlogDetails : " + objectId);
        /*fetch BlogDetails from network*/
        return mNetworkModel.fetchBlogDetails(objectId);
    }

    /**
     * persist the blogDetails
     *
     * @param blogDetails to persist
     * @return the persisted blogDetails
     */
    @Override
    public BlogDetails save(BlogDetails blogDetails) {
        Log.d(TAG, "Saving blogDetails : " + blogDetails.getObjectId());
         /*save blogDetails to database*/
        return mJobModel.saveBlogDetails(blogDetails);
    }

    @Override
    public BlogDetails get(String objectId) {
        return mJobModel.fetchBlogDetailsFromObjectId(objectId);
    }

    /**
     * get the list of object id in the blogDetails
     *
     * @param blogDetails containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(BlogDetails blogDetails) {
         /*get id from the BlogDetails*/
        return Collections.singletonList(blogDetails.getObjectId());
    }
}
