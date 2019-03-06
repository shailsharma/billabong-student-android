package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.QuizMinimal;


/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj 020-05-2016.
 */
public class LoadQuizListEvent {
    public ArrayList<QuizMinimal> getQuizs() {
        return mQuizzes;
    }

    private final ArrayList<QuizMinimal> mQuizzes;

    public LoadQuizListEvent(ArrayList<QuizMinimal> quizs) {
        this.mQuizzes = quizs;
    }
}
