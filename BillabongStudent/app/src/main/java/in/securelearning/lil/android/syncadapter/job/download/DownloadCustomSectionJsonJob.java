package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.CustomSection;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download Web Quiz Job.
 */
public class DownloadCustomSectionJsonJob extends BaseDownloadJobWeb<CustomSection> {
    private final String TAG = this.getClass().getCanonicalName();

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadCustomSectionJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new CustomSection();
    }

    @Override
    protected CustomSection updateObjectContent(CustomSection dataObject, CustomSection temp) {
        return dataObject;
    }

    @Override
    public void createValidationJobs(CustomSection customSection) {
        Log.d(TAG, "Creating Validation Job for CustomSection : " + customSection.getObjectId());
        /*create job to validate the downloaded CustomSection*/
        JobCreator.createCustomSectionValidationJob(customSection).execute();

    }

    /**
     * network call to fetch the CustomSection
     *
     * @param objectId id of the CustomSection to fetch
     * @return call : the network call to fetch the CustomSection
     */
    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching CustomSection : " + objectId);
        /*fetch CustomSection from network*/
        return mNetworkModel.fetchCustomSectionRaw(objectId);
    }

    /**
     * persist the customSection
     *
     * @param customSection to persist
     * @return the persisted customSection
     */
    @Override
    public CustomSection save(CustomSection customSection) {
        Log.d(TAG, "Saving CustomSection : " + customSection.getObjectId());
         /*save customSection to database*/
        return mJobModel.saveCustomSection(customSection);
    }

    @Override
    public CustomSection get(String objectId) {
        return mJobModel.fetchCustomSectionFromObjectId(objectId);
    }

    /**
     * get the list of object id in the customSection
     *
     * @param customSection containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(CustomSection customSection) {
         /*get id from the CustomSection*/
        return Collections.singletonList(customSection.getObjectId());
    }
}
