package in.securelearning.lil.android.syncadapter.fcmservices;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.events.TrackingPostEvent;
import in.securelearning.lil.android.syncadapter.events.UserProfileChangeEvent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.BroadcastNotificationService;
import in.securelearning.lil.android.syncadapter.service.MessageService;
import in.securelearning.lil.android.syncadapter.service.UserService;
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
            if (type.equals(NetworkModel.TYPE_TRACKING)) {
                if (!TextUtils.isEmpty(remoteMessage.getFrom())) {

                    if (remoteMessage.getFrom().length() > 7) {
                        scheduleJob(remoteMessage.getFrom().substring(8), remoteMessage.getSentTime(), remoteMessage.getData().get("payload"));
                    }
                }

            } else if (type.equals(NetworkModel.TYPE_POST_DATA)) {
//                JobCreator.createPostDataValidationJob(GeneralUtils.fromGson(remoteMessage.getData().get("payload"), PostData.class)).execute();
                BroadcastNotificationService.startActionDownloadBroadcastNotification(this);
            } else if (type.equals(NetworkModel.TYPE_POST_RESPONSE)) {
//                JobCreator.createPostResponseValidationJob(GeneralUtils.fromGson(remoteMessage.getData().get("payload"), PostResponse.class)).execute();
                BroadcastNotificationService.startActionDownloadBroadcastNotification(this);
            } else if (type.equals(NetworkModel.TYPE_CALENDAR_EVENT)) {
                final String id = remoteMessage.getData().get("id");
                //  BroadcastNotificationService.startActionDownloadBroadcastNotification(this);
                MessageService.startActionDownloadCalendarEvent(this, id);
            } else if (type.equals(NetworkModel.TYPE_ASSIGNMENT)) {
                BroadcastNotificationService.startActionDownloadBroadcastNotification(this);
            } else if (type.equals(NetworkModel.TYPE_ASSIGNMENT_RESPONSE)) {
                BroadcastNotificationService.startActionDownloadBroadcastNotification(this);
            } else if (type.equals(NetworkModel.TYPE_USER_PROFILE)) {
                final String userId = remoteMessage.getData().get("userId");
                final String groupId = remoteMessage.getData().get("groupId");
                final String userName = remoteMessage.getData().get("userFullName");

//                if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userName)) {
//                    mRxBus.send(new UserProfileChangeEvent(userId, userName));
//                }
                UserService.startActionUpdateUserProfile(this, userId, groupId);
            } else if (type.equals(NetworkModel.TYPE_INSTITUTE_UPDATE)) {
                final String id = remoteMessage.getData().get("id");
                UserService.startActionUpdateInstitute(this, id);
            }else if (type.equals(NetworkModel.TYPE_GROUP_UPDATE)) {
                final String id = remoteMessage.getData().get("groupId");
                UserService.startActionUpdateGroup(this, id);
            }
        }

    }

    private void scheduleJob(String objectId, long creationTime, String text) {
        mRxBus.send(new TrackingPostEvent(objectId, creationTime, text));
    }

}
