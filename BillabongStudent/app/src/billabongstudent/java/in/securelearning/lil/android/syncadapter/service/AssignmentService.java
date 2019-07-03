package in.securelearning.lil.android.syncadapter.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.SyncServiceModel;
import io.reactivex.functions.Consumer;

public class AssignmentService extends IntentService {
    public static final String TAG = AssignmentService.class.getCanonicalName();

    private static final String ACTION_SYNC = "in.securelearning.lil.android.syncadapter.service.action.SYNC";
    private static final String ACTION_DOWNLOAD_BROADCAST_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BROADCAST_NOTIFICATION";

    private static final String EXTRA_OBJECT_TYPE = "in.securelearning.lil.android.syncadapter.service.extra.OBJECT_TYPE";
    private static final String EXTRA_OBJECT_ID = "in.securelearning.lil.android.syncadapter.service.extra.OBJECT_ID";
    private static final String EXTRA_GROUP_ID = "in.securelearning.lil.android.syncadapter.service.extra.GROUP_ID";

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    SyncServiceModel mSyncServiceModel;

    public AssignmentService() {
        super("AssignmentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
        Log.e(TAG, "Sync Process started");
    }

    public static void startSyncService(Context context) {
        Intent intent = new Intent(context, AssignmentService.class);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }

    public static void startActionDownloadBroadcastNotification(Context context) {
        Intent intent = new Intent(context, AssignmentService.class);
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
                    handleActionDownloadBroadcastNotification();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //FlavorSyncServiceHelper.startReminderIntentService(getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleActionUpdateInstitute(String id) {
        if (GeneralUtils.isNetworkAvailable(AssignmentService.this)) {
            SyncServiceHelper.setCurrentUserProfile(AssignmentService.this);
            SyncServiceHelper.updateProfile();
        }
    }


    private void handleActionSync() {
        if (GeneralUtils.isNetworkAvailable(AssignmentService.this))
            startSync();
    }

    public void startSync() {
        try {

            handleActionDownloadBroadcastNotification();

            if (BuildConfig.IS_ASSIGNMENT_ENABLED) {
                mSyncServiceModel.fetchAssignmentListSync().subscribe(new Consumer<Assignment>() {
                    @Override
                    public void accept(Assignment assignment) throws Exception {
                        if (assignment.getSyncStatus().equals(SyncStatus.JSON_SYNC.getStatus())) {
                            try {
                                if (GeneralUtils.isNetworkAvailable(AssignmentService.this)) {
                                    JobCreator.createAssignmentValidationJob(assignment).execute();
                                } else {
                                    stopSelf();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                mSyncServiceModel.fetchAssignmentResponseListSync().subscribe(new Consumer<AssignmentResponse>() {
                    @Override
                    public void accept(AssignmentResponse assignmentResponse) throws Exception {
                        if (assignmentResponse.getSyncStatus().equals(SyncStatus.JSON_SYNC.getStatus())) {
                            try {
                                if (GeneralUtils.isNetworkAvailable(AssignmentService.this)) {
                                    JobCreator.createAssignmentResponseValidationJob(assignmentResponse).execute();
                                } else {
                                    stopSelf();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleActionDownloadBroadcastNotification() {
        /*create download notification job */
//        Log.e(TAG, "Start download broadcast notification job");
        if (GeneralUtils.isNetworkAvailable(AssignmentService.this)) {
            if (BuildConfig.IS_ASSIGNMENT_ENABLED) {
                mSyncServiceModel.fetchAssignmentNotificationListSync().subscribe(new Consumer<Notification>() {
                    @Override
                    public void accept(Notification notification) throws Exception {
                        try {
//                                if (mSyncServiceModel.isDownloadAllowed()) {
                            if (GeneralUtils.isNetworkAvailable(AssignmentService.this)) {
                                JobCreator.createDownloadAssignmentResponseJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
                            } else {
                                stopSelf();
                            }
                        } catch (
                                Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

}

