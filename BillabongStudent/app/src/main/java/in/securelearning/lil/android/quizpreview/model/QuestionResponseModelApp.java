package in.securelearning.lil.android.quizpreview.model;

import android.content.Context;
import android.text.TextUtils;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.QuestionResponseModel;
import in.securelearning.lil.android.quizpreview.InjectorQuizPreview;
import in.securelearning.lil.android.syncadapter.service.SyncService;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_QUESTION_RESPONSE_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_ASSIGNMENT_RESPONSE;

/**
 * Created by Chaitendra on 06-Nov-17.
 */

public class QuestionResponseModelApp {


    @Inject
    Context mAppContext;

    @Inject
    QuestionResponseModel mQuestionResponseModel;

    public QuestionResponseModelApp() {
        InjectorQuizPreview.INSTANCE.getComponent().inject(this);
    }

    public QuestionResponse saveQuestionResponse(QuestionResponse questionResponse) {
        return mQuestionResponseModel.saveQuestionResponse(questionResponse);
    }

    /**
     * check if the question response is correct or not
     *
     * @param question
     * @param attempt
     * @return
     */
    public boolean checkCorrectness(Question question, Attempt attempt) {
        boolean isCorrect = true;
        int correctChoicesCount = 0;
        //Count number of correct choices for question
        for (QuestionChoice questionChoice : question.getQuestionChoices()) {
            if (questionChoice.isChoiceCorrect())
                correctChoicesCount++;
        }

        if (correctChoicesCount == attempt.getSubmittedAnswer().size()) {
            for (String s : attempt.getSubmittedAnswer()) {
                isCorrect = isCorrect && question.getQuestionChoices().get(Integer.valueOf(s)).isChoiceCorrect();
            }
        } else
            isCorrect = false;

        return isCorrect;

    }

}
