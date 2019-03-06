package in.securelearning.lil.android.quizpreview.model;


import in.securelearning.lil.android.base.dataobjects.Attempt;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 03-05-2016.
 */
public interface QuizPreviewModelInterface {
    void fetchQuiz(String docId);


    int validateAttempt(Attempt attempt);
}
