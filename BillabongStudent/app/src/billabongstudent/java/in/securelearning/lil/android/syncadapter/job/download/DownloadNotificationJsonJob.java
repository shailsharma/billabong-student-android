package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;
import androidx.annotation.VisibleForTesting;

import com.couchbase.lite.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.NotificationType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Job to download an Notification from server
 */
public class DownloadNotificationJsonJob {

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
    AppUserModel mAppUserModel;

    /**
     * id of the object to download
     */
    protected final String id;

    private final String TAG = this.getClass().getCanonicalName();

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadNotificationJsonJob(String objectId) {
        // super();
        /*initialize the injection component*/
        InjectorSyncAdapter.INSTANCE.initializeComponent();

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);

       /*initialize id of the object to download*/
        id = objectId;

    }

    /**
     * create validation job for the assignedBadges
     *
     * @param notification to validate
     */
    public void createValidationJobs(Notification notification) {
        /*create job to validate the downloaded notification*/
        JobCreator.createNotificationValidationJob(notification).execute();
    }


    /**
     * network call to fetch notification list
     *
     * @param objectId id of the notification to fetch
     * @return call : the network call to fetch notification
     */
    public Call<List<Notification>> fetchFromNetwork(String objectId) {
        /*fetch notification from network*/
        return mNetworkModel.fetchNotificationsList(objectId);
    }

    /**
     * persist Notification
     *
     * @param notification to persist
     * @return the persisted notification
     */
    public Notification save(Notification notification) {
        /*save notification to database*/
        return mJobModel.saveNotification(notification);
    }

    /**
     * get the list of object id in the Notification
     *
     * @param notification containing the id
     * @return list of id
     */
    public List<String> getObjectIdList(Notification notification) {

        /*get id from the notification*/
        return Collections.singletonList(notification.getObjectId());
    }


    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
            Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
            Response<List<Notification>> response = fetchFromNetwork(id).execute();

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
        }
    }

    /**
     * action to take when fetch is not successful
     *
     * @param code
     */
    public void actionFailure(int code) {

    }

    /**
     * action to take when fetch is successful
     *
     * @param notifications downloaded object
     */
    public void actionFetchSuccess(List<Notification> notifications) {

        if (notifications != null) {
            for (Notification notification : notifications) {
                  /*set sync status of the object to json sync*/
                //notification = setSyncStatus(notification, SyncStatus.JSON_SYNC);

                /*save the object into database*/
                //save(notification);

//                /*send sync success message to server*/
//                sendSyncSuccessToServer(getObjectIdList(notification));
//
//                /*create validation job for the download object*/
//                createValidationJobs(notification);

                if (!notification.getSyncStatus().equalsIgnoreCase("Completed")) {


                     /*Create download jobs for notification's objects according to type*/

                    if (notification.getObjectInfo().getType().equalsIgnoreCase(NotificationType.TYPE_POST_DATA.getNotificationType())) {
                        if (notification.getObjectInfo().getObjectId() != null && !notification.getObjectInfo().getObjectId().trim().isEmpty())
                            JobCreator.createDownloadPostDataJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(),false).execute();
                    } else if (notification.getObjectInfo().getType().equalsIgnoreCase(NotificationType.TYPE_POST_RESPONSE.getNotificationType())) {
                        if (notification.getObjectInfo().getObjectId() != null && !notification.getObjectInfo().getObjectId().trim().isEmpty())
                            JobCreator.createPostResponseDownloadJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(),false).execute();
                    } else if (notification.getObjectInfo().getType().equalsIgnoreCase(NotificationType.TYPE_ASSIGNMENT_RESPONSE.getNotificationType())) {
                        if (notification.getObjectInfo().getObjectId() != null && !notification.getObjectInfo().getObjectId().trim().isEmpty())
                            JobCreator.createDownloadAssignmentResponseJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
                    } else if (notification.getObjectInfo().getType().equalsIgnoreCase(NotificationType.TYPE_CALENDAR_EVENT.getNotificationType())) {
                        if (notification.getObjectInfo().getObjectId() != null && !notification.getObjectInfo().getObjectId().trim().isEmpty())
                            JobCreator.createDownloadCalendarEventJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(), false).execute();
                    } else if (notification.getObjectInfo().getType().equalsIgnoreCase(NotificationType.TYPE_DIGITAL_BOOK.getNotificationType())) {
                        //  if (notification.getObjectInfo().getObjectId() != null && !notification.getObjectInfo().getObjectId().trim().isEmpty())
                        // JobCreator.createDownloadDigitalBookJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
                    }
                }
            }
        }

    }


    /**
     * set sync status of the object to the given one
     *
     * @param notification object to update
     * @param syncStatus   new sync status
     * @return object with updated sync status
     */
    public Notification setSyncStatus(Notification notification, SyncStatus syncStatus) {
        /*set sync status of the object*/
        notification.setSyncStatus(syncStatus.toString());

        /*return object*/
        return notification;
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
        return id;
    }


}
