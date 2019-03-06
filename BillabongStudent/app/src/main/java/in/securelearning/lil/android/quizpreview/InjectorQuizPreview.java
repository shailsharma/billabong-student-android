package in.securelearning.lil.android.quizpreview;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.quizpreview.di.component.DaggerQuizPreviewAppComponent;
import in.securelearning.lil.android.quizpreview.di.component.QuizPreviewComponent;
import in.securelearning.lil.android.quizpreview.di.module.QuizPreviewModule;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 03-06-2016.
 */
public class InjectorQuizPreview {

    public static final InjectorQuizPreview INSTANCE = new InjectorQuizPreview();

    protected QuizPreviewComponent mQuizPreviewComponent;

    public QuizPreviewComponent getComponent() {
        initializeComponent();
        return mQuizPreviewComponent;
    }

    public void setQuizPreviewComponent(QuizPreviewComponent quizPreviewComponent) {
        mQuizPreviewComponent = quizPreviewComponent;
    }

    public static void initializeComponent() {
        if (INSTANCE.mQuizPreviewComponent == null) {
            INSTANCE.mQuizPreviewComponent = DaggerQuizPreviewAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .quizPreviewModule(new QuizPreviewModule())
                    .build();

        }
    }

}
