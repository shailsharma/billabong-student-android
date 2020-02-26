package in.securelearning.lil.android.syncadapter;


import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.syncadapter.di.component.DaggerSyncAdapterAppComponent;
import in.securelearning.lil.android.syncadapter.di.component.SyncAdapterComponent;
import in.securelearning.lil.android.syncadapter.di.module.SyncAdapterModule;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
public class InjectorSyncAdapter {
    public static final InjectorSyncAdapter INSTANCE = new InjectorSyncAdapter();

    protected SyncAdapterComponent mComponent;

    public SyncAdapterComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(SyncAdapterComponent component) {
        mComponent = component;
    }

    public static SyncAdapterComponent initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerSyncAdapterAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .syncAdapterModule(new SyncAdapterModule())
                    .apiModule(new ApiModule(Injector.INSTANCE.getComponent().appContext()))
                    .build();
        }
        return INSTANCE.mComponent;
    }
}
