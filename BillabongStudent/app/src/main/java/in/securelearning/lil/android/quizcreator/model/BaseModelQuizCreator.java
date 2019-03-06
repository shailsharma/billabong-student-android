package in.securelearning.lil.android.quizcreator.model;

import in.securelearning.lil.android.quizcreator.InjectorQuizCreator;
import in.securelearning.lil.android.quizcreator.di.component.QuizCreatorBaseComponent;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 11-05-2016.
 */
public class BaseModelQuizCreator {
    private QuizCreatorBaseComponent mQuizCreatorComponent;

    public QuizCreatorBaseComponent getComponent() {
        return mQuizCreatorComponent;
    }

    public BaseModelQuizCreator() {
        InjectorQuizCreator.INSTANCE.initializeComponent();
        mQuizCreatorComponent = InjectorQuizCreator.INSTANCE.getComponent();
    }
}
