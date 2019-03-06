package in.securelearning.lil.android.quizpreview.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.quizpreview.di.module.QuizPreviewModule;

/**
 * Created by Prabodh Dhabaria on 08-07-2016.
 */
@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {QuizPreviewModule.class})
public interface QuizPreviewAppComponent extends QuizPreviewComponent {
}
