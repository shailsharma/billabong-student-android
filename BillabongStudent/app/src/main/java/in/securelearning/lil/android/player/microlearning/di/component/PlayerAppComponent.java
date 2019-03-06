package in.securelearning.lil.android.player.microlearning.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.player.microlearning.di.module.PlayerModule;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = PlayerModule.class)
public interface PlayerAppComponent extends PlayerBaseComponent {
}
