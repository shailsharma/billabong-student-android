package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download Concept Map Job.
 */
public class DownloadConceptMapJsonJob  extends BaseDownloadCourseJobWeb<ConceptMap>  {
    private final String TAG = this.getClass().getCanonicalName();
    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadConceptMapJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }
    /**
     * initialize data object
     */
    @Override
    protected void initializeDataObject() {
        this.mDataObject = new ConceptMap();
    }

    @Override
    public void createValidationJobs(ConceptMap conceptMap) {
        Log.d(TAG, "Creating Validation Job for ConceptMap : " + conceptMap.getObjectId());
        /*create job to validate the downloaded ConceptMap*/
        JobCreator.createConceptMapValidationJob(conceptMap,mAboutCourse).execute();

    }
    /**
     * network call to fetch the ConceptMap
     *
     * @param objectId id of the ConceptMap to fetch
     * @return call : the network call to fetch the ConceptMap
     */
    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching ConceptMap : " + objectId);
        /*fetch ConceptMap from network*/
        return mNetworkModel.fetchConceptMap(objectId);
    }

    @Override
    public Call<ResponseBody> fetchAboutFromNetwork(String objectId) {
        return mNetworkModel.getConceptMapAboutResponseBody(objectId);
    }

    /**
     * persist the conceptMap
     *
     * @param conceptMap to persist
     * @return the persisted conceptMap
     */
    @Override
    public ConceptMap save(ConceptMap conceptMap) {
        Log.d(TAG, "Saving conceptMap : " + conceptMap.getObjectId());
         /*save conceptMap to database*/
        return mJobModel.saveConceptMap(conceptMap);
    }

    @Override
    public ConceptMap get(String objectId) {
        return mJobModel.fetchConceptMapFromObjectId(objectId);
    }

    /**
     * get the list of object id in the conceptMap
     *
     * @param conceptMap containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(ConceptMap conceptMap) {
         /*get id from the ConceptMap*/
        return Collections.singletonList(conceptMap.getObjectId());
    }
}
