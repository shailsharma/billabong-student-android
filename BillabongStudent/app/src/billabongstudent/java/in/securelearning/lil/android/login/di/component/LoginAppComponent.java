package in.securelearning.lil.android.login.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.login.di.module.LoginModule;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@ActivityScope
@Component(dependencies = AppComponent.class , modules = LoginModule.class)
public interface LoginAppComponent extends LoginBaseComponent {


}
