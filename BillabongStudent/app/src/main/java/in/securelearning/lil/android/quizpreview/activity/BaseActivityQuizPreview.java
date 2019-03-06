package in.securelearning.lil.android.quizpreview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.securelearning.lil.android.quizpreview.InjectorQuizPreview;
import in.securelearning.lil.android.quizpreview.di.component.QuizPreviewComponent;


/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 13-05-2016.
 */
public class BaseActivityQuizPreview extends AppCompatActivity {
    private QuizPreviewComponent mQuizPreviewComponent;

    public QuizPreviewComponent getQuizPreviewComponent() {
        return mQuizPreviewComponent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorQuizPreview.INSTANCE.initializeComponent();
        mQuizPreviewComponent = InjectorQuizPreview.INSTANCE.getComponent();

    }
}
