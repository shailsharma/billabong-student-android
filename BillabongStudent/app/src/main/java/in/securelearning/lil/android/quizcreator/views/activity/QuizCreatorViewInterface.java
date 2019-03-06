package in.securelearning.lil.android.quizcreator.views.activity;

import android.support.annotation.IdRes;
import android.view.View;

import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.Quiz;


/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 03-05-2016.
 */
public interface QuizCreatorViewInterface {
    int QUESTION_TYPE = 1001;
    int SKILL = 1002;
    int COMPLEXITY = 1003;
    int PROGRESSION = 1004;
    int QUESTION_IMAGE_FROM_CAMERA = 1005;
    int QUESTION_IMAGE_FROM_GALLERY = 1006;
    int QUESTION_VIDEO = 1007;
    int HINT_IMAGE = 1008;
    int HINT_VIDEO = 1009;
    int CHOICE_IMAGE = 1010;
    int EXPLANATION_IMAGE = 1011;
    int EXPLANATION_VIDEO = 1012;
    int DISPLAY = 1013;

    void initializeResourceFolders(String parentFolderAbsolutePath);

    void setupSubscription();

    void showQuestionItem(View view);

    void removeQuestionListItem(View view);

    void initializeQuestionData();

    void setQuestionDataToView(Question question);

    @IdRes int getRadioButtonIdFromString(String tag, int type);

    String getDisplayTypeFromRadioButtonId(@IdRes int radioId);

    String getQuestionTypeFromRadioButtonId(@IdRes int radioId);

    void showChoiceSection();

    void setChoiceType(boolean isChoiceTypeImage);

    void removeChoiceSection();

    void addChoiceImageToSubSection(int questionType, String picturePath, QuestionChoice questionChoice);

    void addChoiceTextToSubSection(int questionType, QuestionChoice questionChoice);

    void addExplanationImage(String path);

    void addExplanationVideoImage(String path);

    void addExplanationText();

    void addHintImage(String path);

    void addHintText(String tet);

    void addHintVideoImage(String path);

    Question getQuestionDataFromView();

    void updateButtonVisibility(boolean isNewQuestion);

    void cancelUpdateOfQuestionOfQuiz();

    void updateQuestion();

    void addQuestionToQuiz();

    void refreshQuizType(String typeQuiz);

    void showErrorMessage(int errorType);

    void showExplanationSection();

    void removeExplanationSection();

    void initializeQuiz(Quiz quiz);

    void getQuizTitle();

    void addHintAction(View view);

    void selectExplanationResource();

    void selectHintType();

    void selectQuestionResource(View view);

    void selectQuestionVideo();

    void selectQuestionImage();

    void selectChoiceImage();

    void saveQuiz();

}
