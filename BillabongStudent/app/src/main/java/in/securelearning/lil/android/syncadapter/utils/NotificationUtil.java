package in.securelearning.lil.android.syncadapter.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.Date;

import in.securelearning.lil.android.app.R;

/**
 * Created by Chaitendra on 17-May-17.
 */

public class NotificationUtil {
    final public static int UPLOADS_PENDING_NOTIFICATION = 100001;
    final public static int UPLOAD_LEARNING_NETWORK_GROUP_NOTIFICATION = 100002;
    final public static int UPLOAD_COURSE_GROUP_NOTIFICATION = 100003;
    final public static int UPLOAD_ASSIGNMENTS_GROUP_NOTIFICATION = 100004;
    final public static int UPLOAD_CALENDAR_GROUP_NOTIFICATION = 100005;
    final public static int DOWNLOADS_PENDING_NOTIFICATION = 200001;
    final public static int DOWNLOAD_LEARNING_NETWORK_GROUP_NOTIFICATION = 200002;
    final public static int DOWNLOAD_COURSE_GROUP_NOTIFICATION = 200003;
    final public static int DOWNLOAD_ASSIGNMENTS_GROUP_NOTIFICATION = 200004;
    final public static int DOWNLOAD_CALENDAR_GROUP_NOTIFICATION = 200005;
    final public static int DOWNLOAD_BLOG_GROUP_NOTIFICATION = 200006;
    final public static int REMINDER_ASSIGNMENT_PENDING = 300001;
    final public static int REMINDER_CALENDAR = 300002;
    final public static int BUG_REPORT = 400001;
    final public static String NOTIFICATION_CHANNEL_ID = "MyLiLChannelId";
    final public static String NOTIFICATION_CHANNEL_NAME = "MyLiLChannelName";

    public static void showNotificationForPendingUploads(Context context, String msg, String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setShowWhen(true)
                .setAutoCancel(false)
                .setLargeIcon(getLargeNotificationBitmap(context))
                .setContentTitle(title)
                .setContentText(msg);

        // Add as notification
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(UPLOADS_PENDING_NOTIFICATION, builder.build());
    }

    public static void addNotification(Context context, Class aclass, String msg, String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(getNotificationResourceId())
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setAutoCancel(false)
                .setColor(getSmallBackgroundColor(context))
                .setLargeIcon(getLargeNotificationBitmap(context))
                .setContentTitle(title)
                .setContentText(msg);

        Intent notificationIntent = new Intent(context, aclass);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), builder.build());
    }

    public static void showNotification(Context context, Class aclass, String msg, String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setAutoCancel(true)
                .setColor(getSmallBackgroundColor(context))
                .setLargeIcon(getLargeNotificationBitmap(context))
                .setContentTitle(title)
                .setContentText(msg);

        Intent notificationIntent = new Intent(context, aclass);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), builder.build());
    }

    public static void showNotification(Context context, Intent intent, String msg, String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true)
                .setColor(getSmallBackgroundColor(context))
                .setContentTitle(title)
                .setContentText(msg);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), builder.build());
    }

    private static int getNotificationResourceId() {
        return R.drawable.ic_add_location_black_24dp;
    }

    private static int getSmallBackgroundColor(Context context) {
        return ContextCompat.getColor(context, R.color.notification_small_background);
    }

    private static Bitmap getLargeNotificationBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon);
    }

}
