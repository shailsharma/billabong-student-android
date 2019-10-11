package in.securelearning.lil.android.thirdparty.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.thirdparty.di.module.ThirdPartyModule;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ThirdPartyModule.class)
public interface ThirdPartyAppComponent extends ThirdPartyBaseComponent {

}
