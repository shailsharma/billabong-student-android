package in.securelearning.lil.android.syncadapter.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import javax.inject.Inject;

import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.syncadapter.events.ConnectivityReceivedEvent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;

/**
 * connectivity change receiver.
 */
public class ConnectivityChangeReceiver extends BaseReceiver {
    public final String TAG = this.getClass().getCanonicalName();

    /**
     * use to make network calls
     */
    @Inject
    NetworkModel mNetworkModel;

    @Inject
    RxBus mRxBus;

    public ConnectivityChangeReceiver() {
        getComponent().inject(this);
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG, "Checking Network");
        try {
            if (info != null && info.isAvailable() && info.isConnected()) {
                SyncServiceHelper.startSyncService(context);
                mRxBus.send(new ConnectivityReceivedEvent());
            }
        } catch (Exception e) {

        }
    }
}
