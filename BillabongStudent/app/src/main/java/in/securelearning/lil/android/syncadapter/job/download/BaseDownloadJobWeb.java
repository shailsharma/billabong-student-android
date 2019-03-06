package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.couchbase.lite.util.Log;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.BaseDataObjectWeb;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Base Download Job for WebDataObjects.
 */
public abstract class BaseDownloadJobWeb<T extends BaseDataObjectWeb> {
    private final String TAG = this.getClass().getCanonicalName();
    private final int MAX_LOGIN_ATTEMPTS = 1;
    protected int mLoginCount = 0;
    protected T mDataObject;

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
    public BaseDownloadJobWeb(String objectId) {

        /*initialize id of the object to download*/
        mObjectId = objectId;
        mNotificationId = "";
        /*initialize the injection component*/
        InjectorSyncAdapter.INSTANCE.initializeComponent();

        initializeDataObject();
    }

    public BaseDownloadJobWeb(String objectId, String notificationId) {
        mObjectId = objectId;
        mNotificationId = notificationId;
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
            T object = get(mObjectId);
            if (mObjectId.equals(object.getObjectId())) {
                mDataObject = object;

//                /*fetch the object from the server*/
//                Response<ResponseBody> response = fetchFromNetwork(mObjectId).execute();
//
//            /*if fetch if successful*/
//                if (response.isSuccessful()) {
//
//                    T temp = (T) GeneralUtils.fromGson(responseJsonString(response), mDataObject.getClass());
//                    mDataObject = updateObjectContent(mDataObject, temp);
//                    mDataObject.setJson(GeneralUtils.toGson(mDataObject));
//
//                    save(mDataObject);
//
//                } else {
//                    actionFailure(response.code());
//                }


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

    protected abstract T updateObjectContent(T dataObject, T temp);

    /**
     * get response json String
     *
     * @param response
     * @return jsonString
     * @throws IOException
     */
    public String responseJsonString(Response<ResponseBody> response) throws IOException {
        return response.body().string();
    }

    /**
     * action to take when fetch is not successful
     *
     * @param code
     */
    public void actionFailure(int code) {
        if (code == 401 && mLoginCount < MAX_LOGIN_ATTEMPTS) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                mLoginCount++;
                execute();
            }
        } else if (!TextUtils.isEmpty(mNotificationId)) {
            // sendSyncSuccessToServer(Collections.singletonList(notificationId));
            updateNotificationStatus(mNotificationId, SyncStatus.COMPLETE_SYNC.toString());
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
//        sendSyncSuccessToServer(getObjectIdList(t));

        /*create validation job for the download object*/
            createValidationJobs(t);
        } else {

        }
        if (!TextUtils.isEmpty(mNotificationId)) {
            // sendSyncSuccessToServer(Collections.singletonList(notificationId));
            updateNotificationStatus(mNotificationId, SyncStatus.COMPLETE_SYNC.toString());
        }
    }

    public void updateNotificationStatus(String notificationId, String syncStatus) {
        mJobModel.updateNotificationStatus(notificationId, syncStatus);
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

    /**
     * persist the object
     *
     * @param t object to persist
     * @return the persisted object
     */
    public abstract T save(T t);

    public abstract T get(String objectId);

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
    public T setSyncStatus(T t, SyncStatus syncStatus) {
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
