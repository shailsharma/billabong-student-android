package in.securelearning.lil.android.assignments.di.component;

import dagger.Component;
import in.securelearning.lil.android.assignments.di.module.AssignmentModule;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@ActivityScope
@Component(dependencies = AppComponent.class , modules = AssignmentModule.class)
public interface AssignmentAppComponent extends AssignmentBaseComponent {


}
