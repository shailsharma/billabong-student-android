package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.couchbase.lite.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.BaseDataObjectWeb;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Base Download Job for WebDataObjects.
 */
public abstract class BaseDownloadCourseJobWeb<T extends BaseDataObjectWeb> {
    private final String TAG = this.getClass().getCanonicalName();
    protected boolean mIsNotificationEnabled = true;
    protected T mDataObject;
    protected boolean mDoJsonRefresh = false;
    protected AboutCourse mAboutCourse = new AboutCourse();

    @Inject
    Context mContext;

    /**
     * use to make database calls
     */
    @Inject
    JobModel mJobModel;

    /**
     * use to make network calls
     */
    @Inject
    NetworkModel mNetworkModel;

    /**
     * id of the object to download
     */
    protected final String mObjectId;
    protected final String mNotificationId;

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public BaseDownloadCourseJobWeb(String objectId) {

        this(objectId, "");
    }

    public BaseDownloadCourseJobWeb(String objectId, String notificationId) {

        this(objectId, notificationId, true,false);
    }

    public BaseDownloadCourseJobWeb(String objectId, String notificationId, boolean isNotificationEnabled, boolean doJsonRefresh) {
        mIsNotificationEnabled = isNotificationEnabled;
        mObjectId = objectId;
        mNotificationId = notificationId;
        mDoJsonRefresh = doJsonRefresh;
         /*initialize the injection component*/
        InjectorSyncAdapter.INSTANCE.initializeComponent();

        initializeDataObject();
    }

    /**
     * initialize data object
     */
    protected abstract void initializeDataObject();

    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
            AboutCourse object = getAbout(mObjectId);
            if (mObjectId.equals(object.getObjectId())) {
                mAboutCourse = object;

                /*handle the downloaded object*/
                actionAboutFetchSuccess(mAboutCourse);
            } else {
                Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
                Response<ResponseBody> response = fetchAboutFromNetwork(mObjectId).execute();

            /*if fetch if successful*/
                if (response.isSuccessful()) {
                /*set id*/
                    mAboutCourse.setObjectId(mObjectId);

                /*set json in data object*/
                    mAboutCourse.setJson(responseJsonString(response));

                    AboutCourse temp = GeneralUtils.fromGson(mAboutCourse.getJson(), mAboutCourse.getClass());
                    temp.copyFrom(mAboutCourse);

                    mAboutCourse = temp;

                /*handle the downloaded object*/
                    actionAboutFetchSuccess(mAboutCourse);

                } else {
                /*handle failure*/
                    actionFailure(response.code());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get response json String
     *
     * @param response
     * @return jsonString
     * @throws IOException
     */
    public String responseJsonString(Response<ResponseBody> response) throws IOException, JSONException {
        return response.body().string();
    }

    /**
     * action to take when fetch is not successful
     *
     * @param code
     */
    public void actionFailure(int code) {
        if (code == 401) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                execute();
                return;
            }
        } else {
            Injector.INSTANCE.getComponent().rxBus().send(new ObjectDownloadComplete(mObjectId, "", SyncStatus.NOT_SYNC, mDataObject.getClass()));
        }
    }

    /**
     * action to take when fetch is successful
     *
     * @param t downloaded object
     */
    public void actionFetchSuccess(T t) {

        if (!t.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
        /*set sync status of the object to json sync*/
            t = setSyncStatus(t, SyncStatus.JSON_SYNC);

        /*save the object into database*/
            save(t);

        /*send sync success message to server*/
            sendSyncSuccessToServer(getObjectIdList(t));

        /*create validation job for the download object*/
            createValidationJobs(t);
        }
    }

    public void actionAboutFetchSuccess(AboutCourse aboutCourse) {

        if (aboutCourse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {

        } else {
        /*set sync status of the object to json sync*/
            aboutCourse = setSyncStatus(aboutCourse, SyncStatus.JSON_SYNC);

        /*save the object into database*/
            saveAbout(aboutCourse);
        }
        try {
            T object = get(mObjectId);
            if (!mDoJsonRefresh && (mObjectId.equals(object.getObjectId()) && !object.getSyncStatus().equals(SyncStatus.NOT_SYNC.toString()))) {
                mDataObject = object;

                /*handle the downloaded object*/
                actionFetchSuccess(mDataObject);
            } else {
                Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
                Response<ResponseBody> response = fetchFromNetwork(mObjectId).execute();

            /*if fetch if successful*/
                if (response.isSuccessful()) {
                /*set id*/
                    mDataObject.setObjectId(mObjectId);

                /*set json in data object*/
                    mDataObject.setJson(responseJsonString(response));

                    T temp = (T) GeneralUtils.fromGson(mDataObject.getJson(), mDataObject.getClass());
                    temp.copyFrom(mDataObject);

                    mDataObject = temp;

                /*handle the downloaded object*/
                    actionFetchSuccess(mDataObject);

                } else {
                /*handle failure*/
                    actionFailure(response.code());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * create validation job for the object
     *
     * @param t object to validate
     */
    public abstract void createValidationJobs(T t);

    /**
     * network call to fetch the object from the server
     *
     * @param objectId id of the object to fetch
     * @return call : the network call to fetch the object
     */
    public abstract Call<ResponseBody> fetchFromNetwork(String objectId);

    public abstract Call<ResponseBody> fetchAboutFromNetwork(String objectId);

    /**
     * persist the object
     *
     * @param t object to persist
     * @return the persisted object
     */
    public abstract T save(T t);

    public AboutCourse saveAbout(AboutCourse aboutCourse) {
        return mJobModel.saveAboutCourse(aboutCourse);
    }

    public abstract T get(String objectId);

    public AboutCourse getAbout(String objectId) {
        return mJobModel.fetchAboutCourseFromObjectId(objectId);
    }

    /**
     * get the list of object id in the object
     *
     * @param t object containing the id
     * @return list of id
     */
    public abstract List<String> getObjectIdList(T t);

    /**
     * set sync status of the object to the given one
     *
     * @param t          object to update
     * @param syncStatus new sync status
     * @return object with updated sync status
     */
    public <E extends BaseDataObject> E setSyncStatus(E t, SyncStatus syncStatus) {
        /*set sync status of the object*/
        t.setSyncStatus(syncStatus.toString());

        /*return object*/
        return t;
    }

    /**
     * network call to send the list of object id
     * which were successfully synced with server
     *
     * @param objectIds list of object id to send to server
     */
    public void sendSyncSuccessToServer(List<String> objectIds) {
        /*send the list of id to network for successful sync*/
//        mNetworkModel.sendSyncSuccess(objectIds);
    }

    @VisibleForTesting
    public String getObjectId() {
        return mObjectId;
    }

}
