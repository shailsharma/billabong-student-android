package in.securelearning.lil.android.syncadapter.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class BroadcastNotificationService extends IntentService {
    private static final String ACTION_SYNC = "in.securelearning.lil.android.syncadapter.service.action.SYNC";
    private static final String ACTION_DOWNLOAD_BROADCAST_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BROADCAST_NOTIFICATION";

    public BroadcastNotificationService() {
        super("BroadcastNotificationService");
    }

    public static void startSyncService(Context context) {
        Intent intent = new Intent(context, BroadcastNotificationService.class);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }

    protected void onHandleIntent(Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        try {

            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_SYNC.equals(action)) {
                    handleActionSync();
                } else if (ACTION_DOWNLOAD_BROADCAST_NOTIFICATION.equals(action)) {
                    handleActionDownloadBroadcastNotification();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleActionSync() {
        if (GeneralUtils.isNetworkAvailable(BroadcastNotificationService.this))
            startSync();
    }

    public void startSync() {
        try {

            handleActionDownloadBroadcastNotification();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("CheckResult")
    public void handleActionDownloadBroadcastNotification() {
        /*create download notification job */
//        Log.e(TAG, "Start download broadcast notification job");

        if (GeneralUtils.isNetworkAvailable(BroadcastNotificationService.this)) {
            JobCreator.createDownloadBroadcastNotificationJsonJob(Injector.INSTANCE.getComponent().appUserModel().getObjectId(), AppPrefs.getLastBroadcastNotificationTime(BroadcastNotificationService.this)).execute();

//            Completable.complete()
//                    .observeOn(Schedulers.newThread())
//                    .subscribe(new Action() {
//                        @Override
//                        public void run() throws Exception {
//                            MessageService.startActionSyncPosts(getBaseContext());
//                        }
//                    });
            Completable.complete()
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            SyncService.startActionDownloadBroadcastNotification(getBaseContext());
                        }
                    });

//            Completable.complete()
//                    .observeOn(Schedulers.newThread())
//                    .subscribe(new Action() {
//                        @Override
//                        public void run() throws Exception {
//                             AssignmentService.startActionDownloadBroadcastNotification(getBaseContext());
//                        }
//                    });

//            Completable.complete()
//                    .observeOn(Schedulers.newThread())
//                    .subscribe(new Action() {
//                        @Override
//                        public void run() throws Exception {
//                            CourseService.startActionDownloadBroadcastNotification(getBaseContext());
//                        }
//                    });
        }
    }

}

