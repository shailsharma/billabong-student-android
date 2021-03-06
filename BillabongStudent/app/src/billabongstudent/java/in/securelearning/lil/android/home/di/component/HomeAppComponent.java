package in.securelearning.lil.android.home.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.home.di.module.HomeModule;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@ActivityScope
@Component(dependencies = AppComponent.class , modules = HomeModule.class)
public interface HomeAppComponent extends HomeBaseComponent {
}
