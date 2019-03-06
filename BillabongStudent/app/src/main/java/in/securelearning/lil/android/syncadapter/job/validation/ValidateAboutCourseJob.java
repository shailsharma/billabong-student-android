package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import java.io.Serializable;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Created by Prabodh Dhabaria on 11-02-2017.
 */

public class ValidateAboutCourseJob extends BaseValidationJobWeb<AboutCourse> implements Serializable {
    boolean mIsBroadcast = false;

    /**
     * handles initialization of injector component
     * and initializes the object to validate
     *
     * @param dataObject object to validate
     */
    public ValidateAboutCourseJob(AboutCourse dataObject) {
        super(dataObject);
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public ValidateAboutCourseJob(AboutCourse dataObject, boolean isBroadcast) {
        super(dataObject);
        this.mIsBroadcast = isBroadcast;
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void updateAndSaveCompleteSyncStatus(AboutCourse aboutCourse) {
        mJobModel.updateAndSaveCompleteSyncStatus(aboutCourse);
    }

    @Override
    public boolean executeOtherValidationTasks(AboutCourse aboutCourse) {
        return true;
    }

    @Override
    public String getStorageFolderPrefix() {
        return "ac";
    }

    @Override
    public void saveJson(AboutCourse aboutCourse) {
        mJobModel.saveAboutCourse(aboutCourse);
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return ((AboutCourse) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return "New Course Available - " + ((AboutCourse) mDataObject).getTitle();
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_COURSE_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return ((AboutCourse) mDataObject).getTitle();
    }

    @Override
    public void showDownloadFailedNotification() {

    }

    @Override
    public void showDownloadStartNotification() {
        mBuilder.setSmallIcon(getNotificationResourceId())
                .setLargeIcon(getLargeNotificationBitmap())
                .setColor(getSmallBackgroundColor())
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setTicker(getStartNotificationTickerText())
                .setContentTitle(getStartNotificationTitle());

        if (getProgressCountMax() > 0) {
            mBuilder.setProgress(getProgressCountMax(), 0, isIndeterminate());
            mBuilder.setContentText(getProgressText(getProgressCountMax(), 0));
        } else {
            mBuilder.setContentText(getStartNotificationText());
        }
    }

    @Override
    public void showDownloadSuccessfulNotification() {
        if (mIsBroadcast) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                            .setSmallIcon(getNotificationResourceId())
                            .setLargeIcon(getLargeNotificationBitmap())
                            .setColor(getSmallBackgroundColor())
                            .setTicker(getSuccessfulNotificationTickerText())
                            .setAutoCancel(true)
                            .setContentTitle(getSuccessfulNotificationTitle())
                            .setContentText(getSuccessfulNotificationText());

            if (isNotificationSoundEnabled()) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                mBuilder.setOnlyAlertOnce(true);
                Uri uri = getNotificationSoundUri();

                if (uri != null && !TextUtils.isEmpty(uri.toString())) {
                    mBuilder.setSound(uri);
                }
            }


            Class objectClass = null;

            if (mDataObject.getCourseType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
            } else if (mDataObject.getCourseType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
            } else if (mDataObject.getMicroCourseType().toLowerCase().contains("map")) {
                objectClass = ConceptMap.class;
            } else if (mDataObject.getMicroCourseType().toLowerCase().contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
            } else if (mDataObject.getMicroCourseType().toLowerCase().contains("pop")) {
                objectClass = PopUps.class;
            } else if (mDataObject.getMicroCourseType().toLowerCase().contains("video")) {
                objectClass = InteractiveVideo.class;
            }
            if (objectClass != null) {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                stackBuilder.addParentStack(CourseDetailActivity.class);
                stackBuilder.addNextIntent(CourseDetailActivity.getStartActivityIntent(mContext, mDataObject.getObjectId(), objectClass, ""));
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(resultPendingIntent);

                NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    notifyMgr.createNotificationChannel(channel);
                }
                notifyMgr.notify(getNotificationId(), builder.build());
            }
        }

    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    protected CharSequence getSuccessfulNotificationText() {
        return "New Course Available.";
    }

    protected CharSequence getStartNotificationText() {
        return "New Course Available.";
    }

    @Override
    public void updateProgressCountInNotification(int progress) {
        if (isNotificationEnabled()) {
            if (getProgressCountMax() > 0 && progress <= getProgressCountMax()) {

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(mContext,NotificationUtil.NOTIFICATION_CHANNEL_ID)
                                .setColor(Color.RED)
                                .setLargeIcon(getLargeNotificationBitmap())
                                .setColor(getSmallBackgroundColor())
                                .setSmallIcon(getNotificationResourceId())
                                .setProgress(getProgressCountMax(), progress, isIndeterminate())
                                .setContentText(getProgressText(getProgressCountMax(), progress))
                                .setContentTitle(getSuccessfulNotificationTitle());

                NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    notifyMgr.createNotificationChannel(channel);
                }
                notifyMgr.notify(getNotificationId(), builder.build());
            }


        }
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return "";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return ((AboutCourse) mDataObject).getTitle();
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "New Course Available - " + ((AboutCourse) mDataObject).getTitle();
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
        return PreferenceSettingUtilClass.isCourses(mContext);
    }

    @Override
    public int getNotificationResourceId() {
        return R.drawable.course_white;
    }

//    public Bitmap getLargeNotificationBitmap() {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.outWidth = 96;
//        options.outHeight = 96;
//        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notification_large_s);
//        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notification_icon, options);
//    }
//
//    public @ColorInt int getSmallBackgroundColor(){
//        return ContextCompat.getColor(mContext,R.color.notification_small_background);
//    }
}
