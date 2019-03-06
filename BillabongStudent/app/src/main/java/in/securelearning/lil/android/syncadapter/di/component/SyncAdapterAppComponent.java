package in.securelearning.lil.android.syncadapter.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.syncadapter.di.module.FlavorSyncAdapterModule;
import in.securelearning.lil.android.syncadapter.di.module.SyncAdapterModule;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.FlavorApiModule;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {SyncAdapterModule.class, FlavorSyncAdapterModule.class, ApiModule.class, FlavorApiModule.class})
public interface SyncAdapterAppComponent extends SyncAdapterComponent {
}
