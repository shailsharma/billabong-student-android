package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Base class for all validation jobs.
 */
public abstract class BaseValidationJob<T extends BaseDataObject> implements NotificationProgressUpdate {
    private static String NOTIFICATION_GROUP_KEY_LEARNING_NETWORK = "LN";
    private static String NOTIFICATION_GROUP_KEY_COURSES = "COURSES";
    private static String NOTIFICATION_GROUP_KEY_ASSIGNMENT = "ASSIGNMENT";
    String mBaseFolder = "";
    @Inject
    RxBus mRxBus;
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
     * object to validate
     */
    protected T mDataObject;
    protected boolean mIsNotificationEnabled = true;

    //    private static int deviceNotificationProvider;
//    private int deviceNotificationProviderId;
    NotificationCompat.Builder mBuilder;

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public BaseValidationJob(T dataObject) {
        this(dataObject,true);


    }

    public BaseValidationJob(T dataObject, boolean isNotificationEnabled) {
        mDataObject = dataObject;
        mIsNotificationEnabled = isNotificationEnabled;

         /*initialize the injection component*/
        InjectorSyncAdapter.INSTANCE.initializeComponent();
    }

    /**
     * execute validation of the object
     */
    public final void execute() {
        mBaseFolder = mContext.getFilesDir().getAbsolutePath();

        if (!mDataObject.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
            mBuilder = new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID);
            SyncService.addJobToMonitoringList(mDataObject.getClass(), mDataObject.getObjectId());
            if (mDataObject instanceof Course || mDataObject instanceof AboutCourse) {
                mRxBus.send(new ObjectDownloadComplete(mDataObject.getObjectId(), mDataObject.getAlias(), SyncStatus.JSON_SYNC, mDataObject.getClass()));
            }

            if (isNotificationEnabled()) showDownloadStartNotification();
            Observable.just(executeValidation())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) {
                            SyncService.removeJobFromMonitoringList(mDataObject.getClass(), mDataObject.getObjectId());
                            if (success) {
//                            if (mDataObject instanceof Course || mDataObject instanceof AboutCourse) {
                                mRxBus.send(new ObjectDownloadComplete(mDataObject.getObjectId(), mDataObject.getAlias(), SyncStatus.COMPLETE_SYNC, mDataObject.getClass()));
//                            }

                                if (isNotificationEnabled()) showDownloadSuccessfulNotification();
                                mBaseFolder = null;
                                mDataObject = null;
                            } else {
//                            saveJsonWithSyncStatus(mDataObject, SyncStatus.NOT_SYNC);
//                            if (mDataObject instanceof Course || mDataObject instanceof AboutCourse) {
                                mRxBus.send(new ObjectDownloadComplete(mDataObject.getObjectId(), mDataObject.getAlias(), SyncStatus.NOT_SYNC, mDataObject.getClass()));
//                            }
                                SyncService.removeJobFromMonitoringList(mDataObject.getClass(), mDataObject.getObjectId());
                                if (isNotificationEnabled()) showDownloadFailedNotification();
                                mBaseFolder = null;
                                mDataObject = null;
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            SyncService.removeJobFromMonitoringList(mDataObject.getClass(), mDataObject.getObjectId());
//                        saveJsonWithSyncStatus(mDataObject, SyncStatus.NOT_SYNC);
//                        if (mDataObject instanceof Course || mDataObject instanceof AboutCourse) {
                            mRxBus.send(new ObjectDownloadComplete(mDataObject.getObjectId(), mDataObject.getAlias(), SyncStatus.NOT_SYNC, mDataObject.getClass()));//                        }

                            if (isNotificationEnabled()) showDownloadFailedNotification();
                            Log.e(getClass().getSimpleName(), "OOM");
                            mBaseFolder = null;
                            mDataObject = null;
                            throwable.printStackTrace();
                        }
                    });
        } else {
            mRxBus.send(new ObjectDownloadComplete(mDataObject.getObjectId(), mDataObject.getAlias(), SyncStatus.COMPLETE_SYNC, mDataObject.getClass()));
            mBaseFolder = null;
            mDataObject = null;
        }

    }

//    public abstract void showUploadStartNotification();
//
//    public abstract void showUploadSuccessfulNotification();
//
//    public abstract void showUploadFailedNotification();

    protected abstract boolean executeValidation();


    /**
     * save T
     *
     * @param t to save
     */
    public abstract void saveJson(T t);

    protected void saveJsonWithSyncStatus(T t, SyncStatus syncStatus) {
        t.setSyncStatus(syncStatus.toString());
        saveJson(t);
    }

    public void showDownloadStartNotification() {
        mBuilder.setSmallIcon(getNotificationResourceId())
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
//                .setAutoCancel(false)
                .setOngoing(false)
                .setColor(getSmallBackgroundColor())
                .setLargeIcon(getLargeNotificationBitmap())
                .setTicker(getStartNotificationTickerText())
                .setContentTitle(getStartNotificationTitle());

        if (isNotificationSoundEnabled()) {
            mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
            mBuilder.setOnlyAlertOnce(true);
            Uri uri = getNotificationSoundUri();

            if (uri != null && !TextUtils.isEmpty(uri.toString())) {
                mBuilder.setSound(uri);
            }
        }

        if (getProgressCountMax() > 0) {
            mBuilder.setProgress(getProgressCountMax(), 0, isIndeterminate());
            mBuilder.setContentText(getProgressText(getProgressCountMax(), 0));
        } else {
            mBuilder.setContentText(getStartNotificationText());
        }

//        deviceNotificationProviderId = deviceNotificationProvider++;
        NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(getNotificationId(), mBuilder.build());

    }

    public void showDownloadFailedNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(getNotificationResourceId())
                        .setColor(getSmallBackgroundColor())
                        .setLargeIcon(getLargeNotificationBitmap())
                        .setTicker(getFailedNotificationTickerText())
                        .setOngoing(false)
                        .setContentTitle(getFailedNotificationTitle())
                        .setContentText(getFailedNotificationText());

        NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(getNotificationId(), builder.build());
    }

    public void showDownloadSuccessfulNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(getNotificationResourceId())
                .setColor(getSmallBackgroundColor())
                .setLargeIcon(getLargeNotificationBitmap())
                .setTicker(getSuccessfulNotificationTickerText())
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentTitle(getSuccessfulNotificationTitle())
                .setContentText(getSuccessfulNotificationText());
        PendingIntent resultPendingIntent = getPendingIntent();
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.cancel(getNotificationId());
        notifyMgr.notify(getNotificationId(), builder.build());

    }

    protected abstract PendingIntent getPendingIntent();

    public void updateProgressCountInNotification(int progress) {
        if (isNotificationEnabled()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID).setSmallIcon(getNotificationResourceId())
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setAutoCancel(false)
                    .setColor(getSmallBackgroundColor())
                    .setLargeIcon(getLargeNotificationBitmap())
                    .setContentTitle(getStartNotificationTitle())
                    .setSound(null)
                    .setOnlyAlertOnce(true);
            if (getProgressCountMax() > 0 && progress <= getProgressCountMax()) {
                builder.setProgress(getProgressCountMax(), progress, isIndeterminate())
                        .setContentText(getProgressText(getProgressCountMax(), progress));
            }

//            NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(mContext);
//            mNotifyMgr.notify(getNotificationId(), builder.build());

            NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notifyMgr.createNotificationChannel(channel);
            }
            notifyMgr.notify(getNotificationId(), builder.build());
        }


    }

    protected abstract CharSequence getStartNotificationTitle();

    protected CharSequence getStartNotificationText() {
        return "Downloading";
    }

    protected abstract CharSequence getStartNotificationTickerText();

    protected abstract int getNotificationId();

    protected abstract CharSequence getFailedNotificationTitle();

    protected CharSequence getFailedNotificationText() {
        return "Download Failed.";
    }

    protected abstract CharSequence getFailedNotificationTickerText();

    protected abstract CharSequence getSuccessfulNotificationTitle();

    protected CharSequence getSuccessfulNotificationText() {
        return "Download Done.";
    }

    protected abstract CharSequence getSuccessfulNotificationTickerText();

    protected abstract int getProgressCountMax();

    protected final String getProgressText(int progressCountMax, int progress) {
        return "Downloaded " + progress + "/" + progressCountMax;
    }

    protected abstract boolean isIndeterminate();

    protected  boolean isNotificationEnabled(){
        return mIsNotificationEnabled;
    }

    protected boolean isNotificationSoundEnabled() {
        return PreferenceSettingUtilClass.isNotificationSound(mContext);
    }

    protected Uri getNotificationSoundUri() {

        return PreferenceSettingUtilClass.getNotifications_soundKey(mContext);
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

}

interface NotificationProgressUpdate {
    void updateProgressCountInNotification(int progress);
}