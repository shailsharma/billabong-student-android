package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.couchbase.lite.util.Log;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Base class for all Download Jobs.
 */
public abstract class BaseDownloadJobSearch<T extends BaseDataObject> {
    private final String TAG = this.getClass().getCanonicalName();
    private final int MAX_LOGIN_ATTEMPTS = 1;
    protected int mLoginCount = 0;
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

    @Inject
    RxBus mRxBus;
    /**
     * id of the object to download
     */
    protected final String id;
    protected final String notificationId;

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public BaseDownloadJobSearch(String objectId) {

        /*initialize id of the object to download*/
        id = objectId;
        notificationId = "";

        /*initialize the injection component*/
        InjectorSyncAdapter.INSTANCE.initializeComponent();

    }

    public BaseDownloadJobSearch(String objectId, String notificationId) {

        /*initialize id of the object to download*/
        this.id = objectId;
        this.notificationId = notificationId;
        /*initialize the injection component*/
        InjectorSyncAdapter.INSTANCE.initializeComponent();

    }

    public abstract T get(String objectId);

    /**
     * execute downloading of the object
     */
    public void execute() {
        T object = get(getObjectId());
        if (!getObjectId().equals(object.getObjectId()) || !object.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
            try {
                Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
                Response<T> response = fetchFromNetwork(id).execute();

            /*if fetch if successful*/
                if (response.isSuccessful()) {

                /*handle the downloaded object*/
                    actionFetchSuccess(response.body());

                } else {
                /*handle failure*/
                    actionFailure(response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "" + e.getMessage());

            }
        } else {
            if (notificationId != null && !notificationId.isEmpty()) {
                // sendSyncSuccessToServer(Collections.singletonList(notificationId));
                updateNotificationStatus(notificationId, SyncStatus.COMPLETE_SYNC.toString());
            }
        }
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
        } else {
            if (notificationId != null && !notificationId.isEmpty()) {
                updateNotificationStatus(notificationId, SyncStatus.COMPLETE_SYNC.toString());
            }
        }
    }

    /**
     * action to take when fetch is successful
     *
     * @param t downloaded object
     */
    public void actionFetchSuccess(T t) {
        /*set sync status of the object to json sync*/
        t = setSyncStatus(t, SyncStatus.JSON_SYNC);

        /*save the object into database*/
        t = save(t);


        /*send sync success message to server*/
//        sendSyncSuccessToServer(getObjectIdList(t));
//        && !android.text.TextUtils.isEmpty(t.getDocId())
           /*send sync success message to server*/
        if (notificationId != null && !notificationId.isEmpty()) {
            // sendSyncSuccessToServer(Collections.singletonList(notificationId));
            updateNotificationStatus(notificationId, SyncStatus.COMPLETE_SYNC.toString());
        }

        /*create validation job for the download object*/
        createValidationJobs(t);
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
    public abstract Call<T> fetchFromNetwork(String objectId);

    /**
     * persist the object
     *
     * @param t object to persist
     * @return the persisted object
     */
    public abstract T save(T t);

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
        mNetworkModel.sendSyncSuccess(objectIds);
    }

    @VisibleForTesting
    public String getObjectId() {
        return id;
    }
}
