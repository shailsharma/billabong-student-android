package in.securelearning.lil.android.quizcreator.events;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class NewQuizCreationEvent {
    private final Quiz mQuiz;

    public NewQuizCreationEvent(Quiz mQuiz) {
        this.mQuiz = mQuiz;
    }
    public Quiz getQuiz() {
        return mQuiz;
    }
}
