package in.securelearning.lil.android.quizcreator.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.quizcreator.di.module.QuizCreatorModule;

/**
 * Created by Prabodh Dhabaria on 08-07-2016.
 */
@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {QuizCreatorModule.class})
public interface QuizCreatorAppComponent extends QuizCreatorBaseComponent {
}
