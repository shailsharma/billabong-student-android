package in.securelearning.lil.android.quizpreview.activity;


import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Quiz;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 04-05-2016.
 */
public interface QuizPreviewInterface {
    void initializeQuiz(Quiz quiz);

    void loadQuestionList(int i);

    void loadQuestion(int counter);

    void loadResponseView();

    void initializeViews();

    void showResource(String resourcePath);

    void showNextHint(boolean save);

    ArrayList<String> getSubmittedAnswerFromView();

    void clickSubmitAction();

    void showErrorMessage(int errorType);

    void clickNextAction();

    void clickPreviousAction();

    void setupSubscription();

    void initializeResourceFolders(String parentFolderAbsolutePath);
}
