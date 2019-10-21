package in.securelearning.lil.android.syncadapter.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.job.JobCreator;

public class UserService extends IntentService {
    public static final String TAG = UserService.class.getCanonicalName();

    private static final String ACTION_SYNC = "in.securelearning.lil.android.syncadapter.service.action.SYNC";
    private static final String ACTION_DOWNLOAD_BROADCAST_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BROADCAST_NOTIFICATION";
    private static final String ACTION_UPDATE_USER_PROFILE = "in.securelearning.lil.android.syncadapter.service.action.UPDATE_USER_PROFILE";
    private static final String ACTION_UPDATE_INSTITUTE = "in.securelearning.lil.android.syncadapter.service.action.UPDATE_INSTITUTE";
    private static final String ACTION_UPDATE_GROUP = "in.securelearning.lil.android.syncadapter.service.action.UPDATE_GROUP";

    private static final String EXTRA_OBJECT_TYPE = "in.securelearning.lil.android.syncadapter.service.extra.OBJECT_TYPE";
    private static final String EXTRA_OBJECT_ID = "in.securelearning.lil.android.syncadapter.service.extra.COURSE_ID";
    private static final String EXTRA_GROUP_ID = "in.securelearning.lil.android.syncadapter.service.extra.GROUP_ID";

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    RxBus mRxBus;

    public UserService() {
        super("UserService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
        Log.e(TAG, "Sync Process started");
    }

    public static void startSyncService(Context context) {
        Intent intent = new Intent(context, UserService.class);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }

    public static void startActionDownloadBroadcastNotification(Context context) {
        Intent intent = new Intent(context, UserService.class);
        intent.setAction(ACTION_DOWNLOAD_BROADCAST_NOTIFICATION);
        context.startService(intent);

    }

    public static void startActionUpdateUserProfile(Context context) {
        Intent intent = new Intent(context, UserService.class);
        intent.setAction(ACTION_UPDATE_USER_PROFILE);
//        intent.putExtra(EXTRA_OBJECT_ID, userId);
//        intent.putExtra(EXTRA_GROUP_ID, groupId);
        context.startService(intent);
    }

    public static void startActionUpdateInstitute(Context context, String id) {
        Intent intent = new Intent(context, UserService.class);
        intent.setAction(ACTION_UPDATE_INSTITUTE);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionUpdateGroup(Context context, String id) {
        Intent intent = new Intent(context, UserService.class);
        intent.setAction(ACTION_UPDATE_GROUP);
        intent.putExtra(EXTRA_GROUP_ID, id);
        context.startService(intent);
    }

    protected void onHandleIntent(Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        try {

            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_SYNC.equals(action)) {
                    handleActionSync();
                } else if (ACTION_DOWNLOAD_BROADCAST_NOTIFICATION.equals(action)) {
                    handleActionDownloadBroadcastNotification();
                } else if (ACTION_UPDATE_USER_PROFILE.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                    final String groupId = intent.getStringExtra(EXTRA_GROUP_ID);
                    handleActionUpdateUserProfile(id, groupId);
                } else if (ACTION_UPDATE_INSTITUTE.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                    handleActionUpdateInstitute(id);
                } else if (ACTION_UPDATE_GROUP.equals(action)) {
                    final String groupId = intent.getStringExtra(EXTRA_GROUP_ID);
                    handleActionUpdateGroup(groupId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleActionUpdateUserProfile(String userId, String groupId) {
        if (GeneralUtils.isNetworkAvailable(UserService.this)) {
            SyncServiceHelper.setCurrentUserProfile(UserService.this);
            SyncServiceHelper.updateProfile();
        }
    }

    private void handleActionUpdateGroup(String groupId) {
        if (GeneralUtils.isNetworkAvailable(UserService.this)) {
            FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.SUBSCRIBE_FCM_PREFIX + groupId);
            JobCreator.createDownloadGroupJob(groupId).execute();
            mRxBus.send(new ObjectDownloadComplete(groupId, Group.class));
        }
    }

    private void handleActionUpdateInstitute(String id) {
        if (GeneralUtils.isNetworkAvailable(UserService.this)) {
            SyncServiceHelper.setCurrentUserProfile(UserService.this);
            SyncServiceHelper.updateProfile();
        }
    }

    private void createDownloadJobForGroups(final UserProfile userProfile) {

        if (userProfile != null) {
            HashSet<String> groupsIdsList = fetchGroupsIdsList(userProfile);

//                    if (SyncService.refreshToken(LoginActivityHenkelOld.this)) {
            if (groupsIdsList != null && groupsIdsList.size() > 0) {
                for (final String s : groupsIdsList) {
                    FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.SUBSCRIBE_FCM_PREFIX + s);
                    JobCreator.createDownloadGroupJob(s).execute();
//                    JobCreator.createTrackingRouteDownloadJob(s).execute();
                }
            }
//                    }
        }
    }

    private HashSet<String> fetchGroupsIdsList(UserProfile userProfile) {
        HashSet<String> groupsIdsList = new HashSet<>();
        if (userProfile.getModeratedGroups() != null) {

            for (GroupAbstract groupAbstract : userProfile.getModeratedGroups()) {
                if (groupAbstract != null) groupsIdsList.add(groupAbstract.getObjectId());
            }
        }
        if (userProfile.getMemberGroups() != null) {
            for (GroupAbstract groupAbstract : userProfile.getMemberGroups()) {
                if (groupAbstract != null) groupsIdsList.add(groupAbstract.getObjectId());
            }
        }

        return groupsIdsList;
    }

    private void handleActionSync() {
        if (GeneralUtils.isNetworkAvailable(UserService.this))
            startSync();
    }

    public void startSync() {
        try {

            handleActionDownloadBroadcastNotification();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleActionDownloadBroadcastNotification() {
        /*create download notification job */
//        Log.e(TAG, "Start download broadcast notification job");
        if (GeneralUtils.isNetworkAvailable(UserService.this)) {

        }
    }

}

