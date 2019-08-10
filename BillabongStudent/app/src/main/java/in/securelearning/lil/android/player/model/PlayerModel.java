package in.securelearning.lil.android.player.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.CourseProgress;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionPart;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserCourseProgress;
import in.securelearning.lil.android.base.dataobjects.UserCourseProgressData;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.CourseProgressModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.MicroLearningCourseModel;
import in.securelearning.lil.android.base.model.UserCourseProgressModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.dataobject.PracticeParent;
import in.securelearning.lil.android.player.dataobject.PracticeQuestionResponse;
import in.securelearning.lil.android.player.dataobject.QuizConfigurationRequest;
import in.securelearning.lil.android.player.dataobject.QuizConfigurationResponse;
import in.securelearning.lil.android.player.dataobject.QuizQuestionResponse;
import in.securelearning.lil.android.player.dataobject.QuizResponsePost;
import in.securelearning.lil.android.player.dataobject.TotalPointPost;
import in.securelearning.lil.android.player.dataobject.TotalPointResponse;
import in.securelearning.lil.android.syncadapter.dataobject.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.dataobject.GlobalConfigurationRequest;
import in.securelearning.lil.android.syncadapter.dataobject.QuizResponse;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.player.view.adapter.DropdownAdapter.TYPE_CHOICE;
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

    /*To fetch questions for the quiz*/
    public Observable<QuizQuestionResponse> fetchQuestionsForQuiz(final String quizId) {
        return Observable.create(new ObservableOnSubscribe<QuizQuestionResponse>() {
            @Override
            public void subscribe(ObservableEmitter<QuizQuestionResponse> e) throws Exception {


                Call<QuizQuestionResponse> call = mNetworkModel.fetchQuestionsForQuiz(quizId);
                Response<QuizQuestionResponse> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    QuizQuestionResponse body = response.body();
                    Log.e("fetchQuestions--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<QuizQuestionResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        QuizQuestionResponse body = response2.body();
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


    /*To submit question responses*/
    public Observable<QuizResponse> submitResponseOfQuiz(final QuizResponsePost prepareQuizResponsePostData) {

        return Observable.create(new ObservableOnSubscribe<QuizResponse>() {
            @Override
            public void subscribe(ObservableEmitter<QuizResponse> e) throws Exception {


                Call<QuizResponse> call = mNetworkModel.submitResponseOfQuiz(prepareQuizResponsePostData);
                Response<QuizResponse> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    QuizResponse body = response.body();
                    Log.e("submitResponse--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<QuizResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        QuizResponse body = response2.body();
                        Log.e("submitResponse--", "Successful");
                        e.onNext(body);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToSendData));
                    } else {
                        Log.e("submitResponse--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToSendData));
                    }
                } else {
                    Log.e("submitResponse--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToSendData));
                }

                e.onComplete();
            }
        });

    }

    public boolean checkBlankCorrectness(LinearLayout layout) {
        boolean isCorrect = false;
        int count = layout.getChildCount();
        try {
            for (int i = 0; i < count; i++) {
                EditText view = ((EditText) layout.getChildAt(i));
                if (!TextUtils.isEmpty(view.getTag().toString().trim())
                        && view.getTag().toString().trim().equalsIgnoreCase(view.getText().toString().trim())) {
                    isCorrect = true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCorrect;
    }

    public boolean checkDropdownCorrectness(ArrayList<QuestionPart> responseList) {
        boolean isCorrect = false;
        try {
            for (QuestionPart questionPart : responseList) {
                isCorrect = questionPart.getQuestion().equalsIgnoreCase(questionPart.getCorrectValue());
                if (!isCorrect) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isCorrect;
    }


    public int getDropdownAttemptLimit(Question question) {
        int attempt = 0;

        ArrayList<QuestionPart> list = question.getQuestionPartList();
        for (QuestionPart questionPart : list) {

            if (questionPart.getType().equalsIgnoreCase(TYPE_CHOICE)) {
                attempt = questionPart.getValues().size();
                if (attempt < questionPart.getValues().size()) {
                    attempt = questionPart.getValues().size();
                }
            }

        }
        return attempt;
    }

    public int checkWordOccurrence(String fullString, String occurrenceWord) {
        int i = 0;
        Pattern p = Pattern.compile(occurrenceWord);
        Matcher m = p.matcher(fullString);
        while (m.find()) {
            i++;
        }
        return i;
    }


    public ArrayList<String> getInputTypeListFromString(int count, String string) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String substring = string.substring(string.indexOf("<input "), string.indexOf("/>") + 2);
            list.add(substring);
            string = string.replaceFirst(substring, "____");
        }

        return list;
    }


    public String getInputTypeValueListFromString(String string) {

        String iFrameSubString = string.substring(string.indexOf("value=") + 6, string.indexOf("/>"));
        String slashRemove = iFrameSubString.replaceAll("\\\\", "");
        return slashRemove.replaceAll("\"", "");
    }


    /*Replace all character from given string, regex and replacement*/
    public String replaceAllCharacter(String string, String regex, String replacement) {
        return string.replaceAll(regex, replacement);

    }

    /*To start video player activity*/
    public void startVideoPlayer(Context context, String url) {
        Resource item = new Resource();
        item.setType(context.getString(R.string.typeVideo));
        item.setUrlMain(url);
        context.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(context, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
    }

    /*To start youtube player activity*/
    public void startYoutubePlayer(Context context, String url) {
        if (url.contains("http:") || url.contains("https:")) {

            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
            if (matcher.find()) {
                String videoId = matcher.group();
                FavouriteResource favouriteResource = new FavouriteResource();
                favouriteResource.setName(videoId);
                favouriteResource.setUrlThumbnail(ConstantUtil.BLANK);
                context.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(context, favouriteResource, false));
            }

        } else {
            FavouriteResource favouriteResource = new FavouriteResource();
            favouriteResource.setName(url);
            favouriteResource.setUrlThumbnail(ConstantUtil.BLANK);
            context.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(context, favouriteResource, false));
        }
    }

    /*To start vimeo player activity*/
    public void startVimeoPlayer(Context context, String url) {
        context.startActivity(PlayVimeoFullScreenActivity.getStartIntent(context, url));
    }

    /*To send practice/quiz points to server*/
    public Observable<TotalPointResponse> sendPointsToServer(final TotalPointPost totalPointPost) {
        return Observable.create(new ObservableOnSubscribe<TotalPointResponse>() {
            @Override
            public void subscribe(ObservableEmitter<TotalPointResponse> e) throws Exception {


                Call<TotalPointResponse> call = mNetworkModel.sendPointsToServer(totalPointPost);
                Response<TotalPointResponse> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    TotalPointResponse body = response.body();
                    Log.e("sendPoints--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToSendData));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<TotalPointResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        TotalPointResponse body = response2.body();
                        Log.e("sendPoints--", "Successful");
                        e.onNext(body);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToSendData));
                    } else {
                        Log.e("sendPoints--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToSendData));
                    }
                } else {
                    Log.e("sendPoints--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToSendData));
                }

                e.onComplete();
            }
        });
    }

    /*To fetch bonus configuration for gamification*/
    public Observable<GlobalConfigurationParent> fetchBonusConfiguration() {
        return Observable.create(new ObservableOnSubscribe<GlobalConfigurationParent>() {
            @Override
            public void subscribe(ObservableEmitter<GlobalConfigurationParent> e) throws Exception {
                GlobalConfigurationRequest globalConfigurationRequest = new GlobalConfigurationRequest();
                globalConfigurationRequest.setBonusValue(true);
                Call<GlobalConfigurationParent> call = mNetworkModel.fetchGlobalConfiguration(globalConfigurationRequest);
                Response<GlobalConfigurationParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("BonusConfig", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<GlobalConfigurationParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("BonusConfig", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("BonusConfig", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }


    /*To fetch Configuration for quiz*/
    public Observable<QuizConfigurationResponse> fetchQuizConfiguration(final String courseId, final String courseType) {
        return Observable.create(new ObservableOnSubscribe<QuizConfigurationResponse>() {
            @Override
            public void subscribe(ObservableEmitter<QuizConfigurationResponse> e) throws Exception {

                Call<QuizConfigurationResponse> call = mNetworkModel.fetchQuizConfiguration(new QuizConfigurationRequest(courseId, courseType));
                Response<QuizConfigurationResponse> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("QuizConfig", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<QuizConfigurationResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("QuizConfig", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("QuizConfig", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

}
