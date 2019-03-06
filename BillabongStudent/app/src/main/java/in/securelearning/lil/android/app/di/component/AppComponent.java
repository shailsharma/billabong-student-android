package in.securelearning.lil.android.app.di.component;

import dagger.Component;
import in.securelearning.lil.android.app.di.module.AppModule;
import in.securelearning.lil.android.base.di.scope.ActivityScope;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@ActivityScope
@Component(dependencies = in.securelearning.lil.android.base.di.component.AppComponent.class , modules = AppModule.class)
public interface AppComponent extends AppBaseComponent {
}
