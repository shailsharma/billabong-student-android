package in.securelearning.lil.android.syncadapter.service;

import android.app.IntentService;

import javax.inject.Inject;

import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.di.component.SyncAdapterComponent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.model.SyncServiceModel;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
public abstract class BaseService extends IntentService {
    /**
     * use to make network calls
     */
    @Inject
    NetworkModel mNetworkModel;
    /**
     * provides model access to sync service
     */
    @Inject
    SyncServiceModel mSyncServiceModel;

    @Inject
    WebPlayerLiveModel mWebPlayerLiveModel;

    @Inject
    AppUserModel mAppUserModel;

    private SyncAdapterComponent mComponent;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseService(String name) {
        super(name);
        mComponent = InjectorSyncAdapter.INSTANCE.initializeComponent();
    }

//    public BaseService() {
//        mComponent = InjectorSyncAdapter.INSTANCE.initializeComponent();
//    }

    /*getters and setters*/
    public SyncAdapterComponent getComponent() {
        return mComponent;
    }

    public void setComponent(SyncAdapterComponent component) {
        mComponent = component;
    }

}
