package in.securelearning.lil.android.assignments.model;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.assignments.views.activity.QuizAssemblerActivity;
import in.securelearning.lil.android.assignments.views.activity.QuizMetaDataActivity;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.QuizMinimal;
import in.securelearning.lil.android.base.model.CurriculumModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.syncadapter.service.SyncService;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_QUIZ;

/**
 * Created by Chaitendra on 5/2/2017.
 */

public class QuizAssemblerModel {

    @Inject
    RxBus mRxBus;

    @Inject
    CurriculumModel mCurriculumModel;

    @Inject
    QuizModel mQuizModel;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    @Inject
    Context mAppContext;

    public QuizAssemblerModel() {
        InjectorAssignment.INSTANCE.getComponent().inject(this);
    }

    public HashMap<String, QuizAssemblerActivity.GradeExt> getData() {
        HashMap<String, QuizAssemblerActivity.GradeExt> gradeExtHashMap = new HashMap<>();
        ArrayList<Curriculum> list = mCurriculumModel.getCompleteList();
        for (Curriculum curriculum : list) {
            if (!gradeExtHashMap.containsKey(curriculum.getGrade().getId())) {
                gradeExtHashMap.put(curriculum.getGrade().getId(), new QuizAssemblerActivity.GradeExt(curriculum.getGrade()));
            }
            if (!gradeExtHashMap.get(curriculum.getGrade().getId()).getSubjectExtHashMap().containsKey(curriculum.getSubject().getId())) {
                gradeExtHashMap.get(curriculum.getGrade().getId()).getSubjectExtHashMap().put(curriculum.getSubject().getId(), new QuizMetaDataActivity.SubjectExt(curriculum.getSubject()));
            }

            QuizMetaDataActivity.TopicExt topic = new QuizMetaDataActivity.TopicExt(curriculum.getTopic());
            topic.setBoard(curriculum.getBoard());
            topic.setSubjectGroup(curriculum.getSubjectGroup());
            topic.setGrade(curriculum.getGrade());
            topic.setLang(curriculum.getLang());
            topic.setSkills(curriculum.getSkills());
            topic.setSubject(curriculum.getSubject());
            topic.setLearningLevel(curriculum.getLearningLevel());
            gradeExtHashMap.get(curriculum.getGrade().getId()).getSubjectExtHashMap().get(curriculum.getSubject().getId()).getTopicExts().add(topic);
        }

        return gradeExtHashMap;

    }


    /**
     * save quiz to database and create internal notification for upload
     *
     * @param quiz
     */
    public void saveQuizToDatabase(Quiz quiz) {
        boolean isNewQuiz = TextUtils.isEmpty(quiz.getDocId());

        quiz = mQuizModel.saveQuizToDatabase(quiz);
        if (isNewQuiz && !TextUtils.isEmpty(quiz.getDocId())) {
            QuizMinimal quizMinimal = new QuizMinimal();
            quizMinimal.setObjectId(quiz.getObjectId());
            quizMinimal.setAlias(quiz.getAlias());
            quizMinimal.setObjectDocId(quiz.getDocId());
            quizMinimal.setTitle(quiz.getTitle());
            quizMinimal.setThumbnail(quiz.getThumbnail());
            quizMinimal.setMetaInformation(quiz.getMetaInformation());
            quizMinimal.setStatus(quiz.getStatus());
            quizMinimal.setPublishDateTime(quiz.getPublishDateTime());
            quizMinimal.setAssembledQuiz(true);
            mQuizModel.saveQuizMinimal(quizMinimal);
        }

        createInternalNotificationForQuiz(quiz, ACTION_TYPE_NETWORK_UPLOAD);
    }

    /**
     * create internal notification for quiz upload
     *
     * @param quiz
     * @param action
     */
    public void createInternalNotificationForQuiz(Quiz quiz, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, quiz.getAlias());
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(quiz.getQuizType());
            internalNotification.setObjectDocId(quiz.getDocId());
            internalNotification.setObjectId(quiz.getAlias());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_QUIZ);
            internalNotification.setTitle(quiz.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mAppContext, internalNotification.getDocId());
    }

}
