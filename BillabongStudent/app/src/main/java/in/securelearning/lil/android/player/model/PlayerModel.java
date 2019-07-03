package in.securelearning.lil.android.player.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.CourseProgress;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.UserCourseProgress;
import in.securelearning.lil.android.base.dataobjects.UserCourseProgressData;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.CourseProgressModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.MicroLearningCourseModel;
import in.securelearning.lil.android.base.model.UserCourseProgressModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.dataobject.PracticeParent;
import in.securelearning.lil.android.player.dataobject.PracticeQuestionResponse;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_COURSE_PROGRESS_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_USER_COURSE_PROGRESS_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_COURSE_PROGRESS;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_USER_COURSE_PROGRESS;

/**
 * Created by Chaitendra on 20-Feb-18.
 */

public class PlayerModel {

    @Inject
    MicroLearningCourseModel mMicroLearningCourseModel;

    @Inject
    CourseProgressModel mCourseProgressModel;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    @Inject
    UserCourseProgressModel mUserCourseProgressModel;

    @Inject
    NetworkModel mNetworkModel;

    @Inject
    Context mContext;

    @Inject
    AppUserModel mAppUserModel;

    public PlayerModel() {
        InjectorPlayer.INSTANCE.getComponent().inject(this);
    }

    public Observable<MicroLearningCourse> getMicroLearningCourse(String id) {
        MicroLearningCourse microLearningCourse = mMicroLearningCourseModel.getObjectById(id);
        if (microLearningCourse != null && !TextUtils.isEmpty(microLearningCourse.getObjectId()) && microLearningCourse.getObjectId().equals(id)) {
            return getMicroLearningCourseOffline(id);
        } else {
            return getRapidLearningCourse(id);
        }
    }

    public Observable<MicroLearningCourse> getMicroLearningCourseOffline(final String id) {

        return
                Observable.create(new ObservableOnSubscribe<MicroLearningCourse>() {
                    @Override
                    public void subscribe(ObservableEmitter<MicroLearningCourse> subscriber) {
                        MicroLearningCourse microLearningCourse = mMicroLearningCourseModel.getObjectById(id);
                        subscriber.onNext(microLearningCourse);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<MicroLearningCourse> getRapidLearningCourse(final String id) {

        return Observable.create(new ObservableOnSubscribe<MicroLearningCourse>() {
            @Override
            public void subscribe(ObservableEmitter<MicroLearningCourse> e) throws Exception {
                Call<MicroLearningCourse> call = mNetworkModel.getRapidLearningCourse(id);
                Response<MicroLearningCourse> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    MicroLearningCourse microLearningCourse = response.body();
                    Log.e("MicroLearningCourse1--", "Successful");
                    e.onNext(microLearningCourse);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageCourseDetailNotFound));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<MicroLearningCourse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        MicroLearningCourse microLearningCourse = response2.body();
                        Log.e("MicroLearningCourse2--", "Successful");
                        e.onNext(microLearningCourse);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageCourseDetailNotFound));
                    } else {
                        Log.e("MicroLearningCourse2--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageCourseDetailNotFound));
                    }
                } else {
                    Log.e("MicroLearningCourse1--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageCourseDetailNotFound));
                }
                e.onComplete();
            }
        });
    }

    public Observable<ArrayList<MicroLearningCourse>> getMicroLearningCourseList() {

        return Observable.create(new ObservableOnSubscribe<ArrayList<MicroLearningCourse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MicroLearningCourse>> e) throws Exception {
                Call<ArrayList<MicroLearningCourse>> call = mNetworkModel.getMicroLearningCourseList();
                Response<ArrayList<MicroLearningCourse>> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    ArrayList<MicroLearningCourse> list = response.body();
                    Log.e("MicroLearningCourse1--", "Successful");
                    e.onNext(list);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageFeaturedCardsUnableToNotFound));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<MicroLearningCourse>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        ArrayList<MicroLearningCourse> list = response2.body();
                        Log.e("MicroLearningCourse2--", "Successful");
                        e.onNext(list);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageFeaturedCardsUnableToNotFound));
                    } else {
                        Log.e("MicroLearningCourse2--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageFeaturedCardsNotFound));
                    }
                } else {
                    Log.e("MicroLearningCourse1--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageFeaturedCardsNotFound));
                }
                e.onComplete();
            }
        });
    }

    /*create UserCourseProgress object with passed values*/
    @SuppressLint("CheckResult")
    public void generateUserCourseProgress(final String courseId, final String courseType, final boolean isMicroCourse, final String startTime, final String endTime, final String level1Type, final String level1Id, final String level2Type, final String level2Id) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                String userId = mAppUserModel.getObjectId();
                long timeDifference = DateUtils.getSecondsOfISODateString(endTime) - DateUtils.getSecondsOfISODateString(startTime);
                UserCourseProgress userCourseProgress = new UserCourseProgress();
                userCourseProgress.setCourseId(courseId);
                userCourseProgress.setCourseType(courseType);
                userCourseProgress.setIsMicroCourse(isMicroCourse);
                userCourseProgress.setUserId(userId);
                userCourseProgress.setStartTime(startTime);
                userCourseProgress.setEndTime(endTime);
                userCourseProgress.setTimeSpent(Math.round(timeDifference));
                UserCourseProgressData userCourseProgressData = new UserCourseProgressData();
                userCourseProgressData.setLevel1Type(level1Type);
                userCourseProgressData.setLevel1Id(level1Id);
                userCourseProgressData.setLevel2Type(level2Type);
                userCourseProgressData.setLevel2Id(level2Id);
                userCourseProgress.setData(userCourseProgressData);
                userCourseProgress.setObjectId(GeneralUtils.generateAlias(UserCourseProgress.class.getSimpleName(), userId, String.valueOf(System.currentTimeMillis())));
                userCourseProgress = mUserCourseProgressModel.saveObject(userCourseProgress);
                createInternalNotificationForUserCourseProgress(userCourseProgress, ACTION_TYPE_USER_COURSE_PROGRESS_UPLOAD);
            }
        });
    }


    public void saveCourseProgress(CourseProgress courseProgress, boolean createNotification) {
        courseProgress = mCourseProgressModel.saveObject(courseProgress);
        if (createNotification)
            createInternalNotificationForCourseProgress(courseProgress, ACTION_TYPE_COURSE_PROGRESS_UPLOAD);
    }

    public CourseProgress getCourseProgress(String id) {
        return mCourseProgressModel.getObjectById(id);
    }

    /*create internal notification for UserCourseProgress to upload in background */
    private void createInternalNotificationForUserCourseProgress(UserCourseProgress userCourseProgress, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, userCourseProgress.getObjectId());
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(UserCourseProgress.class.getSimpleName());
            internalNotification.setObjectDocId(userCourseProgress.getDocId());
            internalNotification.setObjectId(userCourseProgress.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_USER_COURSE_PROGRESS);
            internalNotification.setTitle(userCourseProgress.getObjectId());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }

    public void createInternalNotificationForCourseProgress(CourseProgress courseProgress, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, courseProgress.getObjectId());
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType("CourseProgress");
            internalNotification.setObjectDocId(courseProgress.getDocId());
            internalNotification.setObjectId(courseProgress.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_COURSE_PROGRESS);
            internalNotification.setTitle(courseProgress.getObjectId());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }

    /*To fetch questions for the practice*/
    public Observable<PracticeQuestionResponse> fetchQuestions(final PracticeParent practiceParent) {
        return Observable.create(new ObservableOnSubscribe<PracticeQuestionResponse>() {
            @Override
            public void subscribe(ObservableEmitter<PracticeQuestionResponse> e) throws Exception {


                Call<PracticeQuestionResponse> call = mNetworkModel.fetchQuestions(practiceParent);
                Response<PracticeQuestionResponse> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    PracticeQuestionResponse body = response.body();
                    Log.e("fetchQuestions--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<PracticeQuestionResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        PracticeQuestionResponse body = response2.body();
                        Log.e("fetchQuestions--", "Successful");
                        e.onNext(body);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                    } else {
                        Log.e("fetchQuestions--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                    }
                } else {
                    Log.e("fetchQuestions--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                }

//                Gson gson = new Gson();
//                String json = gson.toJson(practiceParent);
//                Log.e("postBody", json);
//
//                PracticeQuestionResponse practiceQuestionResponse = new PracticeQuestionResponse();
//                practiceQuestionResponse.setScore(50);
//                practiceQuestionResponse.setStatus(true);
//                Question question = new Question();
//                question.setQuestionText("What is your name ?");
//                question.setExplanation("Chaitendra");
//
//                QuestionChoice questionChoice1 = new QuestionChoice();
//                questionChoice1.setChoiceCorrect(true);
//                questionChoice1.setChoiceId("1");
//                questionChoice1.setChoiceText("Chaitendra");
//
//                QuestionChoice questionChoice2 = new QuestionChoice();
//                questionChoice2.setChoiceCorrect(false);
//                questionChoice2.setChoiceId("2");
//                questionChoice2.setChoiceText("Gopal");
//
//                QuestionChoice questionChoice3 = new QuestionChoice();
//                questionChoice3.setChoiceCorrect(false);
//                questionChoice3.setChoiceId("3");
//                questionChoice3.setChoiceText("kapil");
//
//                ArrayList<QuestionChoice> questionChoices = new ArrayList<>();
//                questionChoices.add(questionChoice1);
//                questionChoices.add(questionChoice2);
//                questionChoices.add(questionChoice3);
//                question.setQuestionChoices(questionChoices);
//
//                QuestionHint questionHint = new QuestionHint();
//                questionHint.setHintOrder(1);
//                questionHint.setHintText("Name start with Letter C");
//                question.setQuestionHints(new ArrayList<>(Collections.singleton(questionHint)));
//
//                question.setComplexityLevel("low");
//                question.setOrder(1);
//                question.setQuestionType(Question.TYPE_DISPLAY_RADIO);
//
//                ArrayList<Question> questions = new ArrayList<>();
//                questions.add(question);
//                questions.add(question);
//                questions.add(question);
//
//                practiceQuestionResponse.setQuestionList(questions);
//                e.onNext(practiceQuestionResponse);
                e.onComplete();
            }
        });
    }

    /**
     * check if the question response is correct or not
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
