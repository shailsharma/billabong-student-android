package in.securelearning.lil.android.syncadapter.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.SyncServiceModel;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.functions.Consumer;

public class CourseService extends IntentService {
    public static final String TAG = CourseService.class.getCanonicalName();

    private static final String ACTION_SYNC = "in.securelearning.lil.android.syncadapter.service.action.SYNC";
    private static final String ACTION_DOWNLOAD_BROADCAST_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BROADCAST_NOTIFICATION";

    private static final String EXTRA_OBJECT_TYPE = "in.securelearning.lil.android.syncadapter.service.extra.OBJECT_TYPE";
    private static final String EXTRA_OBJECT_ID = "in.securelearning.lil.android.syncadapter.service.extra.COURSE_ID";
    private static final String EXTRA_GROUP_ID = "in.securelearning.lil.android.syncadapter.service.extra.GROUP_ID";

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    SyncServiceModel mSyncServiceModel;

    public CourseService() {
        super("CourseService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
        Log.e(TAG, "Sync Process started");
    }

    public static void startSyncService(Context context) {
        Intent intent = new Intent(context, CourseService.class);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }

    public static void startActionDownloadBroadcastNotification(Context context) {
        Intent intent = new Intent(context, CourseService.class);
        intent.setAction(ACTION_DOWNLOAD_BROADCAST_NOTIFICATION);
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
                    //handleActionDownloadBroadcastNotification();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleActionUpdateUserProfile(String userId, String groupId) {
        if (GeneralUtils.isNetworkAvailable(CourseService.this)) {
            FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.SUBSCRIBE_FCM_PREFIX + groupId);
            JobCreator.createDownloadGroupJob(groupId).execute();
            SyncServiceHelper.setCurrentUserProfile(CourseService.this);
            SyncServiceHelper.updateProfile();
        }
    }

    private void handleActionSync() {
        //if (GeneralUtils.isNetworkAvailable(CourseService.this))
        //startSync();
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
        if (GeneralUtils.isNetworkAvailable(CourseService.this)) {
            if (BuildConfig.IS_COURSES_ENABLED) {
                mSyncServiceModel.fetchDigitalBookNotificationListSync().subscribe(new Consumer<Notification>() {
                    @Override
                    public void accept(Notification notification) throws Exception {
                        notification.setBroadcast(true);
                        mSyncServiceModel.addRecommendedCourse(notification.getObjectInfo());
                        try {
//                                if (mSyncServiceModel.isDownloadAllowed()) {
                            if (GeneralUtils.isNetworkAvailable(CourseService.this))
                                JobCreator.createDownloadAboutCourseDigitalBookJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(), notification.isBroadcast(), true).execute();
//                                } else {
//                                    stopSelf();
//                                }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                mSyncServiceModel.fetchVideoCourseNotificationListSync().subscribe(new Consumer<Notification>() {
                    @Override
                    public void accept(Notification notification) throws Exception {
                        notification.setBroadcast(true);
                        mSyncServiceModel.addRecommendedCourse(notification.getObjectInfo());
                        try {
//                                if (mSyncServiceModel.isDownloadAllowed()) {
                            if (GeneralUtils.isNetworkAvailable(CourseService.this))
                                JobCreator.createDownloadAboutCourseVideoCourseJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(), notification.isBroadcast(), true).execute();
//                                } else {
//                                    stopSelf();
//                                }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

}

