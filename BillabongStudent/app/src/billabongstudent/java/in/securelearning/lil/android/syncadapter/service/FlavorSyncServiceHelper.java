package in.securelearning.lil.android.syncadapter.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import in.securelearning.lil.android.app.BuildConfig;
import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;


/**
 * Sync Service Helper to facilitate interactions with the Sync service.
 */
public class FlavorSyncServiceHelper {

    @SuppressLint("CheckResult")
    public static void startSyncService(final Context context) {

        Completable.complete().observeOn(Schedulers.newThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        BroadcastNotificationService.startSyncService(context);
                    }
                });

        if (BuildConfig.IS_PERIOD_ENABLED) {
            Completable.complete().observeOn(Schedulers.newThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            PeriodService.startSyncService(context);
                        }
                    });
        }


        Completable.complete().observeOn(Schedulers.newThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        AssignmentService.startSyncService(context);
                    }
                });

    }


    public static void stopSyncService(Context context) {
        context.stopService(new Intent(context, BroadcastNotificationService.class));
        context.stopService(new Intent(context, PeriodService.class));
        context.stopService(new Intent(context, AssignmentService.class));
    }

    public static void startReminderIntentService(Context context) {
        ReminderService.startReminderIntentService(context);

    }
}
