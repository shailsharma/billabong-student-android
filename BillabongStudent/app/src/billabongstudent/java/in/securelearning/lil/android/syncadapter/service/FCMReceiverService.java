package in.securelearning.lil.android.syncadapter.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.homework.event.RefreshHomeworkEvent;
import in.securelearning.lil.android.homework.views.activity.HomeworkDetailActivity;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;


public class FCMReceiverService extends FirebaseMessagingService {

    @Inject
    RxBus mRxBus;

    public static String TAG = "FCM";

    public FCMReceiverService() {
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey("type")) {

            final String type = remoteMessage.getData().get("type");

            assert type != null;
            switch (type) {

                case NetworkModel.TYPE_POST_DATA: {
                    String postId = remoteMessage.getData().get("id");
                    JobCreator.createDownloadPostDataJob(postId, ConstantUtil.BLANK, true).execute();
                    break;
                }

                case NetworkModel.TYPE_POST_RESPONSE: {
                    String responseId = remoteMessage.getData().get("id");
                    JobCreator.createPostResponseDownloadJob(responseId, ConstantUtil.BLANK, true).execute();
                    break;
                }

                case NetworkModel.TYPE_HOMEWORK: {
                    String homeworkTitle = remoteMessage.getData().get("title");
                    String homeworkId = remoteMessage.getData().get("id");
                    String notificationTitle = "New Homework";
                    NotificationUtil.showNotification(getBaseContext(),
                            HomeworkDetailActivity.getStartIntent(this, homeworkId, homeworkTitle),
                            homeworkTitle, notificationTitle);
                    mRxBus.send(new RefreshHomeworkEvent());
                    break;
                }

                case NetworkModel.TYPE_USER_PROFILE: {
                    UserService.startActionUpdateUserProfile(this);
                    break;
                }

                case NetworkModel.TYPE_INSTITUTE_UPDATE: {
                    final String id = remoteMessage.getData().get("id");
                    UserService.startActionUpdateInstitute(this, id);
                    break;
                }

                case NetworkModel.TYPE_GROUP_UPDATE: {
                    final String id = remoteMessage.getData().get("groupId");
                    UserService.startActionUpdateGroup(this, id);
                    break;
                }

                case NetworkModel.TYPE_USER_ARCHIVED: {
                    Intent intent = LoginActivity.getUserArchivedIntent(getBaseContext());
                    startActivity(intent);
                    int pendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(getBaseContext(), pendingIntentId, intent,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    assert mgr != null;
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);
                    break;
                }

            }


        }

        if (remoteMessage.getNotification() != null) {
            if (!isAppIsInBackground(getBaseContext())) {
                // If app is in foreground
                NotificationUtil.addNotification(getBaseContext(), FCMReceiverService.class, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
            }
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }
        return isInBackground;
    }
}
