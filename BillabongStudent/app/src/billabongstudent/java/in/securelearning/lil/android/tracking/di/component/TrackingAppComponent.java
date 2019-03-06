package in.securelearning.lil.android.tracking.di.component;

import dagger.Component;

import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.tracking.di.module.TrackingModule;

/**
 * Created by Secure on 26-04-2017.
 */

@ActivityScope
@Component(dependencies = AppComponent.class , modules = TrackingModule.class)
public interface TrackingAppComponent extends TrackingBaseComponent {

}
