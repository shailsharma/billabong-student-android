package in.securelearning.lil.android.quizpreview.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.quizpreview.activity.PracticeTopicActivity;
import in.securelearning.lil.android.quizpreview.activity.QuestionPlayerActivity;
import in.securelearning.lil.android.quizpreview.activity.QuizPreviewActivity;
import in.securelearning.lil.android.quizpreview.model.QuestionResponseModelApp;
import in.securelearning.lil.android.quizpreview.model.QuizPreviewModel;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 13-05-2016.
 */
public interface QuizPreviewComponent extends BaseComponent {
    QuizPreviewModel quizPreviewModel();

    QuizModel quizModel();

    AssignmentModel assignmentModel();

    AssignmentResponseModel quizResponseModel();

    void inject(QuizPreviewActivity quizPreviewActivity);

    void inject(QuizPreviewModel quizPreviewModel);

    void inject(QuestionPlayerActivity questionPlayerActivity);

    void inject(QuestionResponseModelApp questionResponseModelApp);

    void inject(PracticeTopicActivity practiceTopicActivity);
}
