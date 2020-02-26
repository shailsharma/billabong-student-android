package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Download DigitalBook Job.
 */
public class DownloadDigitalBookJsonJob extends BaseDownloadCourseJobWeb<DigitalBook> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadDigitalBookJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        mDataObject = new DigitalBook();
    }

    public DownloadDigitalBookJsonJob(String objectId, String notificationId, boolean isNotificationEnabled, boolean doJsonRefresh) {
        super(objectId, notificationId, isNotificationEnabled, doJsonRefresh);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }


    /**
     * create validation job for the DigitalBook
     *
     * @param digitalBook to validate
     */
    @Override
    public void createValidationJobs(DigitalBook digitalBook) {
        Log.d(TAG, "Creating Validation Job for DigitalBook : " + digitalBook.getObjectId());
        /*create job to validate the downloaded DigitalBook*/
        JobCreator.createDigitalBookValidationJob(digitalBook, mAboutCourse, mIsNotificationEnabled).execute();
    }

    /**
     * network call to fetch the digitalBook
     *
     * @param objectId id of the digitalBook to fetch
     * @return call : the network call to fetch the digitalBook
     */
    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching DigitalBook : " + objectId);
        /*fetch DigitalBook from network*/
        return mNetworkModel.fetchDigitalBook2(objectId);
    }

    @Override
    public String responseJsonString(Response<ResponseBody> response) throws IOException, JSONException {
        JSONObject object = new JSONObject(response.body().string());
        try {
            if (object.has("bookmark")) {
                object.remove("bookmark");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.getJSONObject("digitalBook").toString();
    }

    @Override
    public Call<ResponseBody> fetchAboutFromNetwork(String objectId) {
        return mNetworkModel.getDigitalBookAboutResponseBody(objectId);
    }

    /**
     * persist the digitalBook
     *
     * @param digitalBook to persist
     * @return the persisted digitalBook
     */
    @Override
    public DigitalBook save(DigitalBook digitalBook) {
        Log.d(TAG, "Saving digitalBook : " + digitalBook.getObjectId());
         /*save digitalBook to database*/
        return mJobModel.saveDigitalBook(digitalBook);
    }

    @Override
    public DigitalBook get(String objectId) {
        return mJobModel.fetchDigitalBookFromObjectId(objectId);
    }

    /**
     * get the list of object id in the digitalBook
     *
     * @param digitalBook containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(DigitalBook digitalBook) {
         /*get id from the DigitalBook*/
        return Collections.singletonList(digitalBook.getObjectId());
    }

}
