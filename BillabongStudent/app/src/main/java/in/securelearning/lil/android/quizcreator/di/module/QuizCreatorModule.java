package in.securelearning.lil.android.quizcreator.di.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.quizcreator.model.QuizCreatorModel;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 11-05-2016.
 */
@Module
public class QuizCreatorModule {
    @Provides
    @ActivityScope
    public QuizCreatorModel quizCreatorModel() {
        return new QuizCreatorModel();
    }
}
