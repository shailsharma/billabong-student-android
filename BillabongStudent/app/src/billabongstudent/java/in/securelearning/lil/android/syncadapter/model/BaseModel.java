package in.securelearning.lil.android.syncadapter.model;

import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.di.component.SyncAdapterComponent;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
public class BaseModel {
    // TODO: 25-07-2016 initialize Dependency Injection
    private SyncAdapterComponent mComponent;

    public BaseModel() {
        mComponent = InjectorSyncAdapter.INSTANCE.initializeComponent();
    }

    /*getters and setters*/
   /* public SyncAdapterComponent getComponent() {
        return mComponent;
    }

    public void setComponent(SyncAdapterComponent component) {
        mComponent = component;
    }*/
}
