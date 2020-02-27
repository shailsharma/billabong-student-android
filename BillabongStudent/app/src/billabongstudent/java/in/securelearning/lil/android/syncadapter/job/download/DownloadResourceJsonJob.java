package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download Web Resource Job.
 */
public class DownloadResourceJsonJob extends BaseDownloadJobWeb<Resource> {
    private final String TAG = this.getClass().getCanonicalName();

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadResourceJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new Resource();
    }

    @Override
    protected Resource updateObjectContent(Resource dataObject, Resource temp) {
        return dataObject;
    }

    @Override
    public void createValidationJobs(Resource resource) {
        Log.d(TAG, "Creating Validation Job for Resource : " + resource.getObjectId());
        /*create job to validate the downloaded Resource*/
        JobCreator.createResourceWebValidationJob(resource).execute();

    }

    /**
     * network call to fetch the Resource
     *
     * @param objectId id of the Resource to fetch
     * @return call : the network call to fetch the Resource
     */
    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching Resource : " + objectId);
        /*fetch Resource from network*/
        return mNetworkModel.fetchResourceWeb(objectId);
    }

    /**
     * persist the resource
     *
     * @param resource to persist
     * @return the persisted resource
     */
    @Override
    public Resource save(Resource resource) {
        Log.d(TAG, "Saving Resource : " + resource.getObjectId());
         /*save resource to database*/
        return mJobModel.saveResource(resource);
    }

    @Override
    public Resource get(String objectId) {
        return mJobModel.fetchResourceFromObjectId(objectId);
    }

    /**
     * get the list of object id in the resource
     *
     * @param resource containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(Resource resource) {
         /*get id from the Resource*/
        return Collections.singletonList(resource.getObjectId());
    }
}
