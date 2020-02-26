package in.securelearning.lil.android.syncadapter.receiver;

import android.content.BroadcastReceiver;

import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.di.component.SyncAdapterComponent;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
public abstract class BaseReceiver extends BroadcastReceiver {
    // TODO: 25-07-2016 initialize Dependency Injection
    private SyncAdapterComponent mComponent;

    public BaseReceiver() {
        mComponent = InjectorSyncAdapter.INSTANCE.initializeComponent();
    }

    public SyncAdapterComponent getComponent() {
        return mComponent;
    }

    public void setComponent(SyncAdapterComponent component) {
        mComponent = component;
    }
}
