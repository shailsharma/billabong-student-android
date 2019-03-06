package in.securelearning.lil.android.base.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.module.BaseModuleExt;
import in.securelearning.lil.android.base.di.scope.ActivityScope;

/**
 * Created by Prabodh Dhabaria on 15-11-2017.
 */
@ActivityScope
@Component(
        modules = {BaseModuleExt.class}
)
public interface AppBaseComponentExt extends BaseComponentExt {
}
