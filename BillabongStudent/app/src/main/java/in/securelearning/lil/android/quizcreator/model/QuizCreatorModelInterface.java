package in.securelearning.lil.android.quizcreator.model;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.Quiz;


/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 03-05-2016.
 */
public interface QuizCreatorModelInterface {
    void fetchQuiz(String docId);

    int saveQuiz(Quiz quiz);

    int validateQuestionData(Question question);
}
