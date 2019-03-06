package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.home.events.LoadCalendarEventDownloaded;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate CalendarEvent data job.
 *
 * @author Pushkar Raj
 */
public class ValidateCalendarEventDataJob extends BaseValidationJob<CalendarEvent> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;
    @Inject
    AppUserModel mAppUserModel;

    public ValidateCalendarEventDataJob(CalendarEvent calendarEvent) {
        super(calendarEvent);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the calendarEvent data
     */
    @Override
    public boolean executeValidation() {
        /*fetch calendarEvent data from database*/
        //CalendarEvent calendarEvent = mJobModel.fetchCalEventFromObjectId(mDataObject.getObjectId());


        Log.e(TAG, "number of calendarEvent resource to download : " + mDataObject.getAttachments().size());
        final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;

        int downloadCount = 0;

        for (Resource resource : mDataObject.getAttachments()) {


            String url = resource.getUrlMain();
           /*download Resource*/

            if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                    downloadCount++;

                    url = filePathPrefix + resourceLocal.getDeviceURL();

                    //     mDataObject.setPostResources(mDataObject.getPostResources().set(i,url));
                    //mDataObject.getAttachments().set(i, url);


                    resource.setDeviceURL(url);
                    saveJson(mDataObject);

                    Log.e(TAG, "resources image downloaded");
                }
                resourceLocal = null;
            }
            url = null;
        }
        if (downloadCount == mDataObject.getAttachments().size()) {
            /*save postdata with complete sync status
            and update status of all json's tagging this postdata*/
            mDataObject.setUnread(true);
            mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);

            mRxBus.send(new LoadCalendarEventDownloaded(mDataObject));

            return true;
        }

        return false;
    }

    @Override
    public void showDownloadStartNotification() {

    }

    @Override
    public void showDownloadFailedNotification() {

    }

    @Override
    public void showDownloadSuccessfulNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                        .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                        .setSmallIcon(getNotificationResourceId())
                        .setLargeIcon(getLargeNotificationBitmap())
                        .setColor(getSmallBackgroundColor())
                        .setTicker(getSuccessfulNotificationTickerText())
                        .setAutoCancel(true)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentTitle(getSuccessfulNotificationTitle())
                        .setContentText(getSuccessfulNotificationText());

        if (isNotificationSoundEnabled()) {
            mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
            Uri uri = getNotificationSoundUri();

            if (uri != null && !TextUtils.isEmpty(uri.toString())) {
                mBuilder.setSound(uri);
            }
        }

        Intent resultIntent = new Intent(mContext, LoginActivity.class);
        resultIntent.setAction(LoginActivity.ACTION_NOTIFICATION);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, 0);

        builder.setContentIntent(pendingIntent);

//        NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(mContext);
//        mNotifyMgr.notify(getNotificationId(), builder.build());

        NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(getNotificationId(), builder.build());
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return null;
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_CALENDAR_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return "Calendar";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "New " + mDataObject.getEventType();
    }

    protected CharSequence getSuccessfulNotificationText() {
        return "New " + mDataObject.getEventType() + " : " + mDataObject.getEventTitle();
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
//        if (!mDataObject.getCreatedBy().equals(mAppUserModel.getObjectId())) {
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    @Override
    public int getNotificationResourceId() {
        return R.drawable.calendar_w;
    }

    public Bitmap getLargeNotificationBitmap() {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.outWidth = 96;
//        options.outHeight = 96;
        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notification_icon);
    }

    @ColorInt
    int getSmallBackgroundColor() {
        return ContextCompat.getColor(mContext, R.color.notification_small_background);
    }

    /**
     * save calendarEvent
     *
     * @param calendarEvent to save
     */
    public void saveJson(CalendarEvent calendarEvent) {
        mJobModel.saveCalendarEventData(calendarEvent);
    }


}
