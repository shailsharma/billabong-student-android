package in.securelearning.lil.android.quizpreview.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.quizpreview.model.QuestionResponseModelApp;
import in.securelearning.lil.android.quizpreview.model.QuizPreviewModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 13-05-2016.
 */
@Module
public class QuizPreviewModule {
    @Provides
    @ActivityScope
    public QuizPreviewModel quizPreviewModel() {
        return new QuizPreviewModel();
    }

    @Provides
    @ActivityScope
    public NetworkModel networkModel() {
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public QuestionResponseModelApp questionResponseModelApp() {
        return new QuestionResponseModelApp();
    }

}
