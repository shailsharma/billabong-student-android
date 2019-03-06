package in.securelearning.lil.android.syncadapter.job.upload;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Base Upload Job
 */
public abstract class BaseUploadJob<T extends BaseDataObject> {
    protected final String TAG = this.getClass().getCanonicalName();
    protected final int MAX_LOGIN_ATTEMPT = 1;
    protected final int MAX_ALIAS_ATTEMPT = 1;
    protected int mLoginAttempts = 0;
    protected int mAliasAttempts = 0;

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
     * object to upload
     */
    protected final T mDataObject;


    /**
     * handles initialization of injector component
     * and initializes the object to upload
     *
     * @param dataObject object to upload
     */
    public BaseUploadJob(T dataObject) {
        /*initialize object to upload*/
        mDataObject = dataObject;
        /*initialize the injection component*/
        InjectorSyncAdapter.INSTANCE.initializeComponent();

    }


    /**
     * execute uploading of the object
     */
    public abstract void execute();

    public void showUploadStartNotification(int notificationId) {

        if (isNotificationEnabled()) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(mContext,NotificationUtil.NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(getNotificationResourceId())
                            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                            .setAutoCancel(false)
                            .setOngoing(false)
                            .setColor(getSmallBackgroundColor())
                            .setLargeIcon(getLargeNotificationBitmap())
                            .setTicker(getStartNotificationTickerText())
                            .setContentTitle(getStartNotificationTitle())
                            .setContentText(getStartNotificationText());
            if (isNotificationSoundEnabled()) {
                builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                Uri uri = PreferenceSettingUtilClass.getNotifications_soundKey(mContext);

                if (uri != null && !TextUtils.isEmpty(uri.toString())) {
                    builder.setSound(uri);
                }
            }

            if (getProgressCountMax() > 0) {
                builder.setProgress(getProgressCountMax(), 0, isIndeterminate());
                builder.setContentText(getProgressText(getProgressCountMax(), 0));
            }

//            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
//            notificationManagerCompat.notify(notificationId, builder.build());

            NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notifyMgr.createNotificationChannel(channel);
            }
            notifyMgr.notify(notificationId, builder.build());
        }


    }

    public void showUploadFailedNotification(int notificationId) {
        if (isNotificationEnabled()) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(getNotificationResourceId())
//                        .setAutoCancel(true)
                            .setColor(getSmallBackgroundColor())
                            .setLargeIcon(getLargeNotificationBitmap())
                            .setOngoing(false)
                            .setTicker(getFailedNotificationTickerText())
                            .setContentTitle(getFailedNotificationTitle())
                            .setContentText(getFailedNotificationText());


//            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
//            notificationManagerCompat.notify(notificationId, builder.build());

            NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notifyMgr.createNotificationChannel(channel);
            }
            notifyMgr.notify(notificationId, builder.build());
        }

    }

    public void showUploadSuccessfulNotification(final int notificationId) {
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(mContext)
//                        .setSmallIcon(getNotificationResourceId())
//                        .setColor(getSmallBackgroundColor())
//                        .setLargeIcon(getLargeNotificationBitmap())
//                        .setOngoing(false)
//                        .setAutoCancel(true)
//                        .setTicker(getSuccessfulNotificationTickerText())
//                        .setContentTitle(getSuccessfulNotificationTitle())
//                        .setContentText(getSuccessfulNotificationText());
//
//        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(mContext);
        mNotifyMgr.cancel(notificationId);


    }

    public void updateProgressCountInNotification(int progress) {
        if (isNotificationEnabled()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext).setSmallIcon(getNotificationResourceId())
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setAutoCancel(false)
                    .setColor(getSmallBackgroundColor())
                    .setLargeIcon(getLargeNotificationBitmap())
                    .setContentTitle(getStartNotificationTitle());
            if (getProgressCountMax() > 0 && progress <= getProgressCountMax()) {
                builder.setProgress(getProgressCountMax(), progress, isIndeterminate())
                        .setContentText(getProgressText(getProgressCountMax(), progress));
            }

            NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(mContext);
            mNotifyMgr.notify(mDataObject.getObjectId().hashCode(), builder.build());
        }
    }

    protected abstract CharSequence getStartNotificationTitle();

    protected abstract CharSequence getStartNotificationTickerText();

    protected abstract CharSequence getStartNotificationText();

    protected abstract CharSequence getFailedNotificationTitle();

    protected abstract CharSequence getFailedNotificationText();

    protected abstract CharSequence getFailedNotificationTickerText();

    protected abstract CharSequence getSuccessfulNotificationTitle();

    protected abstract CharSequence getSuccessfulNotificationText();

    protected abstract CharSequence getSuccessfulNotificationTickerText();

    protected abstract int getProgressCountMax();

    protected final String getProgressText(int progressCountMax, int progress) {
        return "Downloaded " + progress + "/" + progressCountMax;
    }

    protected abstract boolean isIndeterminate();

    protected abstract boolean isNotificationEnabled();

    protected boolean isNotificationSoundEnabled() {
        return PreferenceSettingUtilClass.isNotificationSound(mContext);
    }

    public abstract
    @DrawableRes
    int getNotificationResourceId();

    public Bitmap getLargeNotificationBitmap() {

        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notification_icon);
    }

    @ColorInt
    int getSmallBackgroundColor() {
        return ContextCompat.getColor(mContext, R.color.notification_small_background);
    }

    @VisibleForTesting
    public T getDataObject() {
        return mDataObject;
    }
}
