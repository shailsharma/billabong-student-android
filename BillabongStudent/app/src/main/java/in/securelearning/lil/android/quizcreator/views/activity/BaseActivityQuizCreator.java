package in.securelearning.lil.android.quizcreator.views.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.securelearning.lil.android.quizcreator.InjectorQuizCreator;
import in.securelearning.lil.android.quizcreator.di.component.QuizCreatorBaseComponent;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 11-05-2016.
 */
public class BaseActivityQuizCreator extends AppCompatActivity {
    private QuizCreatorBaseComponent mQuizCreatorComponent;

    public QuizCreatorBaseComponent getComponent() {
        return mQuizCreatorComponent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorQuizCreator.INSTANCE.initializeComponent();
        mQuizCreatorComponent = InjectorQuizCreator.INSTANCE.getComponent();
    }
}
