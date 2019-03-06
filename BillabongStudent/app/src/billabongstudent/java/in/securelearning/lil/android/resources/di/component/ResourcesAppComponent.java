package in.securelearning.lil.android.resources.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.resources.di.module.ResourcesModule;

/**
 * Created by Secure on 12-06-2017.
 */

@ActivityScope
@Component(dependencies = AppComponent.class , modules = ResourcesModule.class)
public interface ResourcesAppComponent extends ResourcesBaseComponent {
}
