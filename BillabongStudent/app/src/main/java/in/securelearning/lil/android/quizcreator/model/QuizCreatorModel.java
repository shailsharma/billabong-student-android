package in.securelearning.lil.android.quizcreator.model;

import android.content.Context;
import android.text.TextUtils;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.QuizMinimal;
import in.securelearning.lil.android.base.events.LoadQuizForEditEvent;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_QUIZ;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 28-04-2016.
 */
public class QuizCreatorModel extends BaseModelQuizCreator implements QuizCreatorModelInterface {

    @Inject
    Context mAppContext;

    @Inject
    RxBus mRxBus;

    @Inject
    QuizModel mQuizModel;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    public QuizCreatorModel() {
        super();
        getComponent().inject(this);
    }

    /**
     * fetch quiz using the documentId
     *
     * @param docId
     */
    @Override
    public void fetchQuiz(String docId) {

        if (docId.isEmpty()) {
            mRxBus.send(new LoadQuizForEditEvent(new Quiz()));
        } else {
            Observable<Quiz> fetchQuizFromDb = mQuizModel.fetchQuiz(docId);
            fetchQuizFromDb.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Quiz>() {
                        @Override
                        public void accept(Quiz quiz) {
                            mRxBus.send(new LoadQuizForEditEvent(quiz));
                        }
                    });
        }
    }

    /**
     * save quiz to the database
     *
     * @param quiz
     * @return
     */
    @Override
    public int saveQuiz(Quiz quiz) {
//        String nowString = DateUtils.getCurrentDateTime();
//        quiz.setCreatedBy(GeneralUtils.generateCreatedBy(mAppUserModel.getObjectId(), mAppUserModel.getApplicationUser().getUserType().toString(), mAppUserModel.getApplicationUser().getFirstName()));
//        quiz.setSyncStatus(SyncStatus.NOT_SYNC.toString());
//        return mQuizModel.saveQuiz(quiz);
        return 0;
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
            quizMinimal.setAssembledQuiz(false);
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

    /**
     * validate question data
     *
     * @param question
     * @return
     */
    @Override
    public int validateQuestionData(Question question) {
        return mQuizModel.validateQuestionData(question);
    }

}
