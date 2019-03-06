package in.securelearning.lil.android.quizpreview.model;

import in.securelearning.lil.android.quizpreview.InjectorQuizPreview;
import in.securelearning.lil.android.quizpreview.di.component.QuizPreviewComponent;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 13-05-2016.
 */
public class BaseModelQuizPreview {
    private QuizPreviewComponent mQuizPreviewComponent;

    public QuizPreviewComponent getComponent() {
        return mQuizPreviewComponent;
    }

    public BaseModelQuizPreview() {
        InjectorQuizPreview.INSTANCE.initializeComponent();
        mQuizPreviewComponent = InjectorQuizPreview.INSTANCE.getComponent();
    }
}
