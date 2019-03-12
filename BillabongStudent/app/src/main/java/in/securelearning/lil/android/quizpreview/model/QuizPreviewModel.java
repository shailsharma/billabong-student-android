package in.securelearning.lil.android.quizpreview.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.SubmittedBy;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.events.ErrorEvent;
import in.securelearning.lil.android.base.events.LoadAssignmentResponseQuizPreview;
import in.securelearning.lil.android.base.events.LoadQuizForPreviewEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.DeleteObjectModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.ImageUtils;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_ASSIGNMENT_RESPONSE;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Prabodh Dhabaria on 28-04-2016.
 */
public class QuizPreviewModel extends BaseModelQuizPreview implements QuizPreviewModelInterface {

    public static final int VALIDATION_NO_ERROR = -1;
    public static final int VALIDATION_ERROR1 = 1;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    Context mAppContext;

    @Inject
    RxBus mRxBus;

    @Inject
    QuizModel mQuizModel;

    @Inject
    AssignmentModel mAssignmentModel;

    @Inject
    AssignmentResponseModel mAssignmentResponseModel;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    @Inject
    DeleteObjectModel mDeleteObjectModel;

    public QuizPreviewModel() {
        super();
        getComponent().inject(this);
    }

    /**
     * fetch quiz from database
     *
     * @param docId
     */
    @Override
    public void fetchQuiz(String docId) {

        if (docId.isEmpty()) {
            mRxBus.send(new ErrorEvent("Quiz Not Found"));
        } else {
            Observable<Quiz> fetchQuizFromDb = mQuizModel.fetchQuiz(docId);
            fetchQuizFromDb.subscribeOn(Schedulers.io()).subscribe(new Consumer<Quiz>() {
                @Override
                public void accept(Quiz quiz) {
                    mRxBus.send(new LoadQuizForPreviewEvent(quiz));
                }
            });
        }

    }

    /**
     * validate response of a question
     *
     * @param Attempt
     * @return
     */
    @Override
    public int validateAttempt(Attempt Attempt) {
        return mAssignmentResponseModel.validateAttempt(Attempt);
    }

    /**
     * check if the response is correct or not
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

    public void fetchAssignment(String assignmentDocumentId) {
        mAssignmentModel.getAssignment(assignmentDocumentId).subscribeOn(Schedulers.io()).subscribe(new Consumer<Assignment>() {
            @Override
            public void accept(Assignment assignment) {

            }
        });

    }

    public void fetchAssignmentResponseAndQuiz(String assignmentResponseDocumentId) {
        mAssignmentResponseModel.fetchAssignmentResponse(assignmentResponseDocumentId).subscribeOn(Schedulers.io()).subscribe(new Consumer<AssignmentResponse>() {
            @Override
            public void accept(AssignmentResponse assignmentResponse) {
                sendQuizAndAssignmentResponse(assignmentResponse);
            }
        });

    }

    private void sendQuizAndAssignmentResponse(final AssignmentResponse assignmentResponse) {

        mAssignmentModel.getAssignmentFromUid(assignmentResponse.getAssignmentID()).subscribe(new Consumer<Assignment>() {
            @Override
            public void accept(Assignment assignment) {

                // TODO: 7/25/2017 remove this code when goes live no need of alias

                if (assignment.getUidQuiz() != null && assignment.getUidQuiz().toString().trim().length() > 0) {
                    mQuizModel.fetchQuizfromUid(assignment.getUidQuiz()).subscribe(new Consumer<Quiz>() {
                        @Override
                        public void accept(Quiz quiz) {
                            mRxBus.send(new LoadAssignmentResponseQuizPreview(assignmentResponse, quiz));
                        }
                    });


                } else {
                    Quiz quiz = mQuizModel.fetchQuizFromAliasSync(assignment.getQuizAlias());
                    mRxBus.send(new LoadAssignmentResponseQuizPreview(assignmentResponse, quiz));

                }


            }
        });

    }

    private AssignmentResponse getAssignmentResponseFromAssignment(Assignment assignment) {
        AssignmentResponse assignmentResponse = new AssignmentResponse();
        assignmentResponse.setAssignmentID("");
        assignmentResponse.setTotalScore(0);
        return assignmentResponse;
    }

    /**
     * save assignment response in database and create internal notification for upload.
     *
     * @param assignmentResponse
     * @return
     */
    public int saveAssignmentResponse(AssignmentResponse assignmentResponse) {
        int saveAssignmentResponse = mAssignmentResponseModel.saveAssignmentResponse(assignmentResponse);
        createInternalNotificationForAssignmentResponse(assignmentResponse, ACTION_TYPE_NETWORK_UPLOAD);
        return saveAssignmentResponse;

    }

    /**
     * save assignment response in database and create internal notification for upload.
     *
     * @param assignmentResponse
     * @return
     */
    public int saveAssignmentResponseLocally(AssignmentResponse assignmentResponse) {
        int saveAssignmentResponse = mAssignmentResponseModel.saveAssignmentResponse(assignmentResponse);
        return saveAssignmentResponse;

    }

    /**
     * create internal notification for quiz upload
     *
     * @param assignmentResponse
     * @param action
     */
    private void createInternalNotificationForAssignmentResponse(AssignmentResponse assignmentResponse, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, assignmentResponse.getAlias());
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(assignmentResponse.getAssignmentType());
            internalNotification.setObjectDocId(assignmentResponse.getDocId());
            internalNotification.setObjectId(assignmentResponse.getAlias());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_ASSIGNMENT_RESPONSE);
            internalNotification.setTitle(assignmentResponse.getAssignmentTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mAppContext, internalNotification.getDocId());
    }

    public SubmittedBy getSubmittedBy() {
        SubmittedBy submittedBy = new SubmittedBy();
        UserProfile appUser = mAppUserModel.getApplicationUser();
        submittedBy.setObjectId(appUser.getObjectId());
        submittedBy.setFirstName(appUser.getFirstName());
        submittedBy.setMiddleName(appUser.getMiddleName());
        submittedBy.setLastName(appUser.getLastName());


        Bitmap bitmap = ImageUtils.getScaledBitmapFromPath(mAppContext.getResources(), appUser.getThumbnail().getThumb());
        submittedBy.setUserPic(ImageUtils.encodeToBase64(bitmap));

        return submittedBy;
    }

    public void changeAssignmentStudent(AssignmentResponse assignmentResponse, String oldAssignmentStudentDocId) {
        AssignmentStudent assignmentStudentNew = new AssignmentStudent();
        AssignmentStudent assignmentStudentOld = new AssignmentStudent();
        assignmentStudentOld = mAssignmentResponseModel.getDatabaseQueryHelper().retrieveAssignments(oldAssignmentStudentDocId, AssignmentStudent.class);
        assignmentStudentNew = assignmentStudentOld;
        assignmentStudentNew.setStage(assignmentResponse.getStage());
        assignmentStudentNew.setSubmissionDateTime(assignmentResponse.getSubmissionDateTime());
        assignmentStudentNew.setAssignmentScore(assignmentResponse.getAssignmentScore());
        assignmentStudentNew.setAssignedGroup(assignmentResponse.getAssignedGroup());
        assignmentStudentNew.setDocId("");
        assignmentStudentNew = mAssignmentResponseModel.saveAssignmentResponseToDatabase(assignmentStudentNew, assignmentResponse.getStage());

        mDeleteObjectModel.deleteJsonAssignments(oldAssignmentStudentDocId);
    }
}
