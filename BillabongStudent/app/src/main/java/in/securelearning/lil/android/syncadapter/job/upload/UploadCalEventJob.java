package in.securelearning.lil.android.syncadapter.job.upload;

import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Learning CalendarEvent Job.
 *
 * @author Pushkar Raj
 */
public class UploadCalEventJob extends BaseUploadJob<CalendarEvent> {
    public final String TAG = this.getClass().getCanonicalName();

    public UploadCalEventJob(CalendarEvent calendarEvent) {
        super(calendarEvent);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute uploading of CalendarEvent
     */
    public void execute() {

//         /*post the resource in CalendarEvent*/

        showUploadStartNotification(NotificationUtil.UPLOAD_CALENDAR_GROUP_NOTIFICATION);
        UploadEventResourcesJob job = new UploadEventResourcesJob(mDataObject) {
            @Override
            public void onComplete() {
                try {
                    actionResourceUploadComplete();
//                    actionResourceUploadCompleteFCM();
                } catch (Exception e) {
                    UploadCalEventJob.this.showUploadFailedNotification(NotificationUtil.UPLOAD_CALENDAR_GROUP_NOTIFICATION);
                    e.printStackTrace();
                }
            }
        };
                /*execute the job*/
        job.execute();


//        try {
//            actionResourceUploadComplete();
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }

    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return "Calendar";
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "Uploading event";
    }

    @Override
    protected CharSequence getStartNotificationText() {
        return "Uploading : " + mDataObject.getEventTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return "Calendar";
    }

    @Override
    protected CharSequence getFailedNotificationText() {
        return "Upload failed : " + mDataObject.getEventTitle();
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "Upload failed";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return null;
    }

    @Override
    protected int getProgressCountMax() {
        return 0;
    }

    @Override
    protected boolean isIndeterminate() {
        return false;
    }

    @Override
    protected boolean isNotificationEnabled() {
        return true;
    }

    @Override
    public int getNotificationResourceId() {
        return R.drawable.calendar_w;
    }

    private void actionResourceUploadCompleteFCM() {
        mDataObject.setObjectId(UUID.randomUUID().toString());

        try {
            String strGroupIds = "";

            strGroupIds = mDataObject.getGroupAbstract().getObjectId();

            Response<MessageResponse> response = uploadJsonToServerFCM(mDataObject, strGroupIds).execute();
            if (response.isSuccessful()) {
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                saveJsonToDatabase(mDataObject);
                showUploadSuccessfulNotification(NotificationUtil.UPLOAD_CALENDAR_GROUP_NOTIFICATION);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Call<MessageResponse> uploadJsonToServerFCM(CalendarEvent calendarEvent, String groupId) {
        /*network call to post learning network post data */
        return mNetworkModel.sendDataToMultipleTopicsUsingFCM(groupId, calendarEvent, NetworkModel.TYPE_CALENDAR_EVENT);
    }

    public Call<MessageResponse> uploadJsonToServerFCM(String groupId, String objectId, String isoDate) {
        /*network call to post learning network post data */
        return mNetworkModel.sendDataUsingFCM(groupId, "New Calendar Event", NetworkModel.TYPE_CALENDAR_EVENT, objectId, isoDate);
    }

//    /**
//     * fetch PostData using alias
//     *
//     * @param alias of the PostData
//     * @return PostData
//     */
//    public PostData getPostDataFromAlias(String alias) {
//        /*fetch quiz using alias*/
//        return mJobModel.fetchPostDataFromAlias(alias);
//
//    }

    private void actionResourceUploadComplete() throws IOException {


        //Sending UrlMain to server
        mDataObject.setAttachments(mDataObject.getAttachments());

            /*upload data object*/
        Response<CalendarEvent> response = uploadJsonToServer(mDataObject).execute();


            /*if upload is successful*/
        if (response.isSuccessful()) {
                /*retrieve assignment response*/
            CalendarEvent calendarEvent = response.body();

            Log.e(TAG, "Calendar event posted : " + calendarEvent.getObjectId());

                /*set sync status*/
            mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
            mDataObject.setObjectId(calendarEvent.getObjectId());

                /*save json to database*/
            saveJsonToDatabase(mDataObject);

            showUploadSuccessfulNotification(NotificationUtil.UPLOAD_CALENDAR_GROUP_NOTIFICATION);
        } else if (response.code() == 422 || response.code() == 500) {
            Response<CalendarEvent> response2 = getByAlias(mDataObject.getAlias()).execute();

            /*if upload is successful*/
            if (response2.isSuccessful()) {
                /*retrieve assignment response*/
                CalendarEvent calendarEvent = response2.body();

                Log.e(TAG, "Calendar event posted : " + calendarEvent.getObjectId());

                /*set sync status*/
                mDataObject.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

                /*set object id*/
                mDataObject.setObjectId(calendarEvent.getObjectId());

                /*save json to database*/
                saveJsonToDatabase(mDataObject);

                showUploadSuccessfulNotification(NotificationUtil.UPLOAD_CALENDAR_GROUP_NOTIFICATION);
            } else {
                showUploadFailedNotification(NotificationUtil.UPLOAD_CALENDAR_GROUP_NOTIFICATION);
            }
        } else if (response.code() == 401 && mLoginAttempts < MAX_LOGIN_ATTEMPT) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                mLoginAttempts++;
                execute();
            }
        } else if (response.code() == 401 && mLoginAttempts >= MAX_LOGIN_ATTEMPT) {
            // TODO: 07-03-2017 redirect to refreshToken page
        } else {
            Log.e(TAG, "Network  Cal Event err " + response.message());
            showUploadFailedNotification(NotificationUtil.UPLOAD_CALENDAR_GROUP_NOTIFICATION);
        }
    }


    /**
     * upload learning network CalendarEvent json to network
     *
     * @param calendarEvent to upload
     * @return network call
     */
    public Call<CalendarEvent> uploadJsonToServer(CalendarEvent calendarEvent) {
        /*network call to post learning network CalendarEvent */
        return mNetworkModel.postCalenderEvent(calendarEvent);
    }

    public Call<CalendarEvent> getByAlias(String alias) {
        return mNetworkModel.fetchByAliasCalendarEventData(alias);
    }

    /**
     * save calendarEvent to database
     *
     * @param calendarEvent to save
     */
    public void saveJsonToDatabase(CalendarEvent calendarEvent) {
        /*save calendarEvent*/
        mJobModel.saveCalendarEventData(calendarEvent);
        try {
            Response<MessageResponse> response = uploadJsonToServerFCM(mDataObject.getGroupAbstract().getObjectId(), mDataObject.getObjectId(), mDataObject.getStartDate()).execute();
            if (response.isSuccessful()) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
