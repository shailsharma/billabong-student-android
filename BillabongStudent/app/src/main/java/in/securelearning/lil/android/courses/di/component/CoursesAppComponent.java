package in.securelearning.lil.android.courses.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.courses.di.module.CoursesModule;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@ActivityScope
@Component(dependencies = AppComponent.class , modules = CoursesModule.class)
public interface CoursesAppComponent extends CoursesBaseComponent {
}
