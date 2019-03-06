package in.securelearning.lil.android.quizcreator.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.quizcreator.model.QuizCreatorModel;
import in.securelearning.lil.android.quizcreator.views.activity.QuizCreatorActivity;

/**
 * Created by Prabodh Dhabaria on 24-11-2016.
 */

public interface QuizCreatorBaseComponent extends BaseComponent {
    QuizCreatorModel quizCreatorModel();

    QuizModel quizModel();

    void inject(QuizCreatorModel quizCreatorModel);

    void inject(QuizCreatorActivity quizCreatorActivity);
}
