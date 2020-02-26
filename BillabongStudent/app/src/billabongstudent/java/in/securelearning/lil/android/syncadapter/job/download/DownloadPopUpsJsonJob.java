package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download PopUps Job.
 */
public class DownloadPopUpsJsonJob extends BaseDownloadCourseJobWeb<PopUps> {
    private final String TAG = this.getClass().getCanonicalName();

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadPopUpsJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new PopUps();
    }

    @Override
    public void createValidationJobs(PopUps popUps) {
        Log.d(TAG, "Creating Validation Job for PopUps : " + popUps.getObjectId());
        /*create job to validate the downloaded PopUps*/
        JobCreator.createPopUpsValidationJob(popUps,mAboutCourse).execute();

    }

    /**
     * network call to fetch the popUps
     *
     * @param objectId id of the popUps to fetch
     * @return call : the network call to fetch the popUps
     */
    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching PopUps : " + objectId);
        /*fetch PopUps from network*/
        return mNetworkModel.fetchPopUps(objectId);
    }

    @Override
    public Call<ResponseBody> fetchAboutFromNetwork(String objectId) {
        return mNetworkModel.getPopUpsAboutResponseBody(objectId);
    }

    /**
     * persist the popUps
     *
     * @param popUps to persist
     * @return the persisted popUps
     */
    @Override
    public PopUps save(PopUps popUps) {
        Log.d(TAG, "Saving popUps : " + popUps.getObjectId());
         /*save popUps to database*/
        return mJobModel.savePopUps(popUps);
    }

    @Override
    public PopUps get(String objectId) {
        return mJobModel.fetchPopUpsFromObjectId(objectId);
    }

    /**
     * get the list of object id in the popUps
     *
     * @param popUps containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(PopUps popUps) {
         /*get id from the PopUps*/
        return Collections.singletonList(popUps.getObjectId());
    }
}
