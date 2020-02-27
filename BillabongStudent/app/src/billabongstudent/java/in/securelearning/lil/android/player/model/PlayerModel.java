package in.securelearning.lil.android.player.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
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
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.QuestionPart;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Skill;
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
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideo;
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideoDetail;
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideoThumbnail;
import in.securelearning.lil.android.player.dataobject.PlayerFilter;
import in.securelearning.lil.android.player.dataobject.PlayerFilterParent;
import in.securelearning.lil.android.player.dataobject.PracticeParent;
import in.securelearning.lil.android.player.dataobject.PracticeQuestionResponse;
import in.securelearning.lil.android.player.dataobject.QuestionFeedback;
import in.securelearning.lil.android.player.dataobject.QuizConfigurationRequest;
import in.securelearning.lil.android.player.dataobject.QuizConfigurationResponse;
import in.securelearning.lil.android.player.dataobject.QuizQuestionResponse;
import in.securelearning.lil.android.player.dataobject.QuizResponsePost;
import in.securelearning.lil.android.player.dataobject.RevisionResponse;
import in.securelearning.lil.android.player.dataobject.RevisionResponsePost;
import in.securelearning.lil.android.player.dataobject.TotalPointPost;
import in.securelearning.lil.android.player.dataobject.TotalPointResponse;
import in.securelearning.lil.android.player.view.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.QuizResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.UserTimeSpent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
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

    /*To play video*/
    public void playVideo(String moduleId, String moduleName, Resource resource) {

        String type = resource.getType();

        String url = "";
        if (!TextUtils.isEmpty(resource.getUrl())) {
            url = resource.getUrl();
        } else if (!TextUtils.isEmpty(resource.getUrlMain())) {
            url = resource.getUrlMain();
        } else if (!TextUtils.isEmpty(resource.getSourceURL())) {
            url = resource.getSourceURL();
        }
        if (TextUtils.isEmpty(type)) {
            if (url.contains(mContext.getString(R.string.typeVimeoVideo))) {
                type = mContext.getString(R.string.typeVimeoVideo);
            } else if (url.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")) {
                type = mContext.getString(R.string.typeYouTubeVideo);
            } else if (url.contains(mContext.getString(R.string.typeVideo))) {
                type = mContext.getString(R.string.typeVideo);
            } else if (url.contains("youtu.be") || url.contains("youtube.com")) {
                type = mContext.getString(R.string.typeYouTubeVideo);
            } else {
                if (url.contains(mContext.getString(R.string.typeVimeoVideo))) {
                    type = mContext.getString(R.string.typeVimeoVideo);
                } else if (url.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")) {
                    type = mContext.getString(R.string.typeYouTubeVideo);
                } else if (url.contains(mContext.getString(R.string.typeVideo))) {
                    type = mContext.getString(R.string.typeVideo);
                } else if (url.contains("youtu.be") || url.contains("youtube.com")) {
                    type = mContext.getString(R.string.typeYouTubeVideo);
                } else {
                    type = mContext.getString(R.string.typeVideo);
                }

            }
        }

        if (type.equalsIgnoreCase(mContext.getString(R.string.typeVideo))) {
            Resource item = new Resource();
            if (!TextUtils.isEmpty(resource.getObjectId())) {
                item.setObjectId(resource.getObjectId());
            }
            item.setType(mContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, moduleId, moduleName, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeYouTubeVideo))) {
            if (!url.contains("https:") && !url.startsWith("www")) {
                FavouriteResource favouriteResource = new FavouriteResource();
                if (!TextUtils.isEmpty(resource.getObjectId())) {
                    favouriteResource.setObjectId(resource.getObjectId());
                }
                favouriteResource.setName(url);
                favouriteResource.setUrlThumbnail("");
                mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
            } else {
                String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

                Pattern compiledPattern = Pattern.compile(pattern);
                Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
                if (matcher.find()) {
                    String videoId = matcher.group();
                    FavouriteResource favouriteResource = new FavouriteResource();
                    favouriteResource.setName(videoId);
                    favouriteResource.setUrlThumbnail("");
                    mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, moduleId, moduleName, favouriteResource));
                }
            }


        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeVimeoVideo))) {
            mContext.startActivity(PlayVimeoFullScreenActivity.getStartIntent(mContext, moduleId, moduleName, ConstantUtil.BLANK, url));
        } else {
            Resource item = new Resource();
            item.setObjectId(resource.getObjectId());
            item.setType(mContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, moduleId, moduleName, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        }
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

    /*Get skill Id list from skill list*/
    public ArrayList<String> getSkillIdList(ArrayList<Skill> skills) {
        ArrayList<String> list = new ArrayList<>();
        for (Skill skill : skills) {
            list.add(skill.getId());
        }
        return list;
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

    /*To check if in 'fill in the blanks' user input is correct or not
     * trimming both correct answer and user answer.*/
    public boolean checkBlankCorrectness(LinearLayout layout) {
        boolean isCorrect = false;
        int count = layout.getChildCount();
        try {
            for (int i = 0; i < count; i++) {
                EditText view = ((EditText) layout.getChildAt(i));
                if (!TextUtils.isEmpty(view.getTag().toString().trim())) {

                    /*Replacing ck-editor ghost character with blank*/
                    String correctAnswer = view.getTag().toString().trim();
                    correctAnswer = correctAnswer.replace(ConstantUtil.CK_EDITOR_GHOST_CHARACTER, ConstantUtil.BLANK);

                    isCorrect = correctAnswer.trim().equalsIgnoreCase(view.getText().toString().trim());

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


    /**
     * To get desired string list from html text, which is in between specific tags
     *
     * @param htmlText text, from which you want to get
     * @param startTag start tag, from where you want to cut text
     * @param endTag   end tag, to where you want to cut text
     * @return
     */
    public ArrayList<String> getStringListFromHtmlText(@NotNull String htmlText, @NotNull String startTag, @NotNull String endTag) {
        final ArrayList<String> list = new ArrayList<>();
        try {
            String completeRegex = startTag + "(.+?)" + endTag;
            Pattern TAG_REGEX = Pattern.compile(completeRegex, Pattern.DOTALL);
            final Matcher matcher = TAG_REGEX.matcher(htmlText);
            while (matcher.find()) {
                list.add(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * To get desired string from html text, which is in between specific tags
     *
     * @param htmlText text, from which you want to get
     * @param startTag start tag, from where you want to cut text
     * @param endTag   end tag, to where you want to cut text
     * @return String
     */
    public String getStringFromHtmlTextAfterTagRemoval(@NotNull String htmlText, @NotNull String startTag, @NotNull String endTag) {
        String value = null;

        try {
            String completeRegex = startTag + "(.+?)" + endTag;

            final Pattern pattern = Pattern.compile(completeRegex, Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(htmlText);
            boolean a = matcher.find();
            String valueMatcher = matcher.group(1);
            String slashRemove = valueMatcher.replaceAll("\\\\", "");
            value = slashRemove.replaceAll("\"", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;

    }

    /*To remove whitespaces from text which matches below type of pattern
     *
     *  --> Two next-line characters which may or may not have simple space(&nbsp;) in front or/and end are replace by a next-line character
     *  and then by using trim completely remove space at front nad end of text
     *
     * @NOT IN USE FOR NOW
     *  */
    public String removeComplexWhiteSpaces(@NotNull String rawText) {
        return rawText.replaceAll("(\\s)?\n\n(\\s)?", "\n").trim();
    }

    /* To extract resource url from text
     * First extract list of resource
     * then extract url from each list item and then add it into resource object
     */
    public ArrayList<Resource> extractResourceListFromText(String rawText) {

        ArrayList<Resource> resourceList = new ArrayList<>();

        if (!TextUtils.isEmpty(rawText)) {

            /* Extraction of all video resource
             * First if text contains '<figure' get list of text from "<figure" to "</figure>"
             * Then if resource-type="video" is in each figure text that means we need to extract it otherwise no action needed
             * Then split the figure string from 'comma' */
            if (rawText.contains("<figure")) {

                ArrayList<String> listFigureVideo = getStringListFromHtmlText(rawText, "<figure", "</figure>");

                if (!listFigureVideo.isEmpty()) {

                    for (int i = 0; i < listFigureVideo.size(); i++) {

                        if (listFigureVideo.get(i).contains("resource-type=\"video\"")) {

                            String figureVideoString = listFigureVideo.get(i);
                            ArrayList<String> commaSeparatedList = new ArrayList<>(Arrays.asList(figureVideoString.split(",")));

                            for (int j = 0; j < commaSeparatedList.size(); j++) {

                                String separatedText = commaSeparatedList.get(j);

                                if (!separatedText.contains("displayResourceFullScreen")
                                        && (separatedText.contains(".youtube.com/") || separatedText.contains("vimeo.com/"))) {

                                    String resourceUrlVideo = separatedText;

                                    if (resourceUrlVideo.startsWith("&quot;")) {
                                        resourceUrlVideo = resourceUrlVideo.replace("&quot;", "");
                                    }
                                    if (resourceUrlVideo.endsWith("&quot;")) {
                                        resourceUrlVideo = resourceUrlVideo.replace("&quot;", "");
                                    }

                                    Resource resourceVideo = new Resource();
                                    resourceVideo.setUrl(resourceUrlVideo);
                                    resourceVideo.setUrlMain(resourceUrlVideo);
                                    resourceVideo.setResourceType(Resource.TYPE_RESOURCE_VIDEO);

                                    if (resourceUrlVideo.contains(".youtube.com/")) {
                                        resourceVideo.setType(mContext.getString(R.string.typeYouTubeVideo));
                                    } else if (resourceUrlVideo.contains("vimeo.com/")) {
                                        resourceVideo.setType(mContext.getString(R.string.typeVimeoVideo));
                                    }

                                    if (listFigureVideo.get(i).contains("<img")) {
                                        String resourceUrlSrc = getStringFromHtmlTextAfterTagRemoval(listFigureVideo.get(i), ConstantUtil.HTML_IMAGE_SRC_TAG, ConstantUtil.HTML_DOUBLE_QUOTE);
                                        resourceVideo.setUrlThumbnail(resourceUrlSrc);
                                    }

                                    resourceList.add(resourceVideo);

                                    /* First time we get our desired url of video, we breaks the loop;
                                     * since after this unnecessary url(s) might add */
                                    break;

                                }
                            }
                        }
                    }
                }


            }


            /*Extraction of all image resource*/
            ArrayList<String> list = getStringListFromHtmlText(rawText, ConstantUtil.HTML_IMAGE_START_TAG, ConstantUtil.HTML_END_TAG);

            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {

                    if (!TextUtils.isEmpty(list.get(i)) && !list.get(i).contains("file:")) {

                        /*if list.get(i) contains resource-type="video" then do not take
                        that image  since it is for video resource*/
                        if (!list.get(i).contains("resource-type=\"video\"")) {

                            String resourceUrl = getStringFromHtmlTextAfterTagRemoval(list.get(i), ConstantUtil.HTML_IMAGE_SRC_TAG, ConstantUtil.HTML_DOUBLE_QUOTE);

                            Resource resourceQue = new Resource();
                            resourceQue.setUrl(resourceUrl);
                            resourceQue.setUrlMain(resourceUrl);

                            String mimeType = URLConnection.guessContentTypeFromName(resourceUrl);
                            if (!TextUtils.isEmpty(mimeType) && mimeType.contains(ConstantUtil.TYPE_IMAGE)) {

                                resourceQue.setUrlThumbnail(resourceUrl);
                                resourceQue.setResourceType(Resource.TYPE_RESOURCE_IMAGE);

                            }

                            resourceList.add(resourceQue);
                        }
                    }

                }
            }

        }

        return resourceList;

    }

    /* To clean html text, before rendering the text in view
     * figureRemove - figure tag extraction as per regex
     * figcaptionRemove - fig-caption tag extraction as per regex
     * almostRefinedText - img tag extraction as per regex
     * */
    public String cleanHtmlTextForPlayer(String unrefinedHtmlText) {

        String figureRemove = replaceAllCharacter(unrefinedHtmlText, ConstantUtil.HTML_EXTRACT_FIGURE_REGEX, ConstantUtil.BLANK);
        String figcaptionRemove = replaceAllCharacter(figureRemove, ConstantUtil.HTML_EXTRACT_FIGCAPTION_REGEX, ConstantUtil.BLANK);
        String almostRefinedText = figcaptionRemove.replaceAll(ConstantUtil.HTML_EXTRACT_IMG_REGEX, ConstantUtil.BLANK);
        return almostRefinedText;

    }

    /*To remove white-space at the end of html text up-to non white-spaces*/
    public CharSequence removeTrailingSpace(String rawHtmlText) {
        Spanned rawSpannedText = Html.fromHtml(rawHtmlText);
        CharSequence finalCharSequence = rawSpannedText;
        int totalTextLength = rawSpannedText.length();

        for (int i = (totalTextLength - 1); i > 0; i--) {
            /*tempText.charAt(i) == ' '
             * In above line the SPACE character is not a SPACE character,
             * it is a ghost char which is looks like space
             * it is coming from ck-editor(creation side)*/
            if (Character.isWhitespace(rawSpannedText.charAt(i)) || rawSpannedText.charAt(i) == ' ') {

                finalCharSequence = rawSpannedText.subSequence(0, i);
            } else {
                break;
            }
        }

        return finalCharSequence;
    }

    /*Replace all character from given string, regex and replacement*/
    public String replaceAllCharacter(String string, String regex, String replacement) {
        return string.replaceAll(regex, replacement);
    }

    /*To refine question hint list
     * if hint is empty or hint equal to 'the ghost character' then it will remove*/
    public ArrayList<QuestionHint> getRefinedHintList(ArrayList<QuestionHint> hints) {

        ArrayList<QuestionHint> questionHintList = new ArrayList<>();
        if (!hints.isEmpty()) {
            for (int i = 0; i < hints.size(); i++) {
                QuestionHint hint = hints.get(i);
                if (!TextUtils.isEmpty(hint.getHintText())
                        && !hint.getHintText().contentEquals(" ")) {
                    questionHintList.add(hint);
                }
            }
            return questionHintList;

        } else {
            return hints;
        }

    }

    /*To transform Khan academy video to resource type video*/
    public ArrayList<Resource> transformKAVideoIntoResource(ArrayList<KhanAcademyVideo> academyVideoList) {

        ArrayList<Resource> resourceList = new ArrayList<>();
        for (KhanAcademyVideo academyVideo : academyVideoList) {

            Resource transformedResource = new Resource();

            if (academyVideo != null && academyVideo.getVideoDetails() != null) {

                KhanAcademyVideoDetail academyVideoDetail = academyVideo.getVideoDetails();
                if (!TextUtils.isEmpty(academyVideoDetail.getVideoId())) {//checking for youtube video id

                    String resourceUrl = mContext.getString(R.string.youtubeUrlWithoutId) + academyVideoDetail.getVideoId();//creating proper youtube url
                    transformedResource.setUrl(resourceUrl);
                    transformedResource.setResourceType(Resource.TYPE_RESOURCE_VIDEO);

                    if (academyVideoDetail.getSnippet() != null && academyVideoDetail.getSnippet().getThumbnail() != null) {

                        KhanAcademyVideoThumbnail academyVideoThumbnails = academyVideoDetail.getSnippet().getThumbnail();
                        String resourceThumbnailUrl = null;

                        if (academyVideoThumbnails.getThumbnailHigh() != null
                                && !TextUtils.isEmpty(academyVideoThumbnails.getThumbnailHigh().getUrl())) {
                            resourceThumbnailUrl = academyVideoThumbnails.getThumbnailHigh().getUrl();
                        } else if (academyVideoThumbnails.getThumbnailStandard() != null
                                && !TextUtils.isEmpty(academyVideoThumbnails.getThumbnailStandard().getUrl())) {
                            resourceThumbnailUrl = academyVideoThumbnails.getThumbnailStandard().getUrl();
                        }

                        transformedResource.setUrlThumbnail(resourceThumbnailUrl);

                    }

                    resourceList.add(transformedResource);

                }
            }

        }


        return resourceList;
    }

    /*To start video player activity*/
    public void startVideoPlayer(Context context, String url) {
        Resource item = new Resource();
        item.setType(context.getString(R.string.typeVideo));
        item.setUrlMain(url);
        context.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(context, ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
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
                context.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(context, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
            }

        } else {
            FavouriteResource favouriteResource = new FavouriteResource();
            favouriteResource.setName(url);
            favouriteResource.setUrlThumbnail(ConstantUtil.BLANK);
            context.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(context, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
        }
    }

    /*To start vimeo player activity*/
    public void startVimeoPlayer(Context context, String url) {
        context.startActivity(PlayVimeoFullScreenActivity.getStartIntent(context, ConstantUtil.BLANK, ConstantUtil.BLANK, ConstantUtil.BLANK, url));
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


    /*To fetch khan academy explanation videos*/
    public Observable<ArrayList<KhanAcademyVideo>> fetchExplanationVideos(final ArrayList<String> skillIdList) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<KhanAcademyVideo>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<KhanAcademyVideo>> e) throws Exception {
                PlayerFilterParent playerFilterParent = new PlayerFilterParent();
                PlayerFilter playerFilter = new PlayerFilter();
                playerFilter.setSkillIdList(skillIdList);
                playerFilterParent.setPlayerFilter(playerFilter);
                Call<ArrayList<KhanAcademyVideo>> call = mNetworkModel.fetchExplanationVideos(playerFilterParent);
                Response<ArrayList<KhanAcademyVideo>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("explanationVideo", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<KhanAcademyVideo>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("explanationVideo", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("explanationVideo", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    /*To fetch quiz configuration for Analytics*/
    public Observable<GlobalConfigurationParent> fetchQuizAnalyticsConfiguration() {
        return Observable.create(new ObservableOnSubscribe<GlobalConfigurationParent>() {
            @Override
            public void subscribe(ObservableEmitter<GlobalConfigurationParent> e) throws Exception {
                GlobalConfigurationRequest chartConfigurationRequest = new GlobalConfigurationRequest();
                chartConfigurationRequest.setQuizConfig(true);
                Call<GlobalConfigurationParent> call = mNetworkModel.fetchQuizAnalyticsConfiguration(chartConfigurationRequest);
                Response<GlobalConfigurationParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("quiz configuration", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<GlobalConfigurationParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("quiz configuration", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("quiz configuration", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }


    /*To fetch question feedback options*/
    public Observable<GlobalConfigurationParent> fetchQuestionFeedbackOptions() {
        return Observable.create(new ObservableOnSubscribe<GlobalConfigurationParent>() {
            @Override
            public void subscribe(ObservableEmitter<GlobalConfigurationParent> e) throws Exception {
                GlobalConfigurationRequest chartConfigurationRequest = new GlobalConfigurationRequest();
                chartConfigurationRequest.setQuestionFeedbackOptions(true);
                Call<GlobalConfigurationParent> call = mNetworkModel.fetchQuizAnalyticsConfiguration(chartConfigurationRequest);
                Response<GlobalConfigurationParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("question feedback", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<GlobalConfigurationParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("question feedback", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("question feedback", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    /*To post question feedback*/
    public Observable<ResponseBody> postQuestionFeedback(final QuestionFeedback questionFeedback) {
        return Observable.create(new ObservableOnSubscribe<ResponseBody>() {
            @Override
            public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {

                Call<ResponseBody> call = mNetworkModel.postQuestionFeedback(questionFeedback);
                Response<ResponseBody> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("question feedback", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ResponseBody> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("question feedback", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("question feedback", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    /*To fetch revision configuration for revision quiz of particular subject's topic*/
    public Observable<GlobalConfigurationParent> fetchRevisionConfiguration() {

        return
                Observable.create(new ObservableOnSubscribe<GlobalConfigurationParent>() {
                    @Override
                    public void subscribe(ObservableEmitter<GlobalConfigurationParent> e) throws Exception {

                        GlobalConfigurationRequest globalConfigurationRequest = new GlobalConfigurationRequest();
                        globalConfigurationRequest.setRevisionConfig(true);

                        Call<GlobalConfigurationParent> call = mNetworkModel.fetchGlobalConfiguration(globalConfigurationRequest);
                        Response<GlobalConfigurationParent> response = call.execute();

                        if (response != null && response.isSuccessful()) {
                            Log.e("RevisionConfig", "Successful");
                            e.onNext(response.body());
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                            Response<GlobalConfigurationParent> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                Log.e("RevisionConfig", "Successful");
                                e.onNext(response2.body());
                            } else if (response2.code() == 401) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("RevisionConfig", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }

                        e.onComplete();
                    }
                });
    }

    /*To fetch list of questions for revision*/
    public Observable<RevisionResponse> fetchQuestionsAndSubmitRevision(final RevisionResponsePost revisionResponsePost) {

        return Observable.create(new ObservableOnSubscribe<RevisionResponse>() {
            @Override
            public void subscribe(ObservableEmitter<RevisionResponse> e) throws Exception {

                Call<RevisionResponse> call = mNetworkModel.fetchQuestionsAndSubmitRevision(revisionResponsePost);
                Response<RevisionResponse> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    RevisionResponse body = response.body();
                    Log.e("QAndSRevision1--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<RevisionResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        RevisionResponse body = response2.body();
                        Log.e("QAndSRevision2--", "Successful");
                        e.onNext(body);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                    } else {
                        Log.e("QAndSRevision2--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                    }
                } else {
                    Log.e("QAndSRevision1--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                }

                e.onComplete();
            }
        });
    }

    @SuppressLint("CheckResult")
    public void uploadUserTimeSpent(final String moduleId, final String moduleName, final String resourceId, final String resourceType, final long startTime, final long endTime) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                @Override
                public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {

                    UserTimeSpent userTimeSpent = new UserTimeSpent();
                    userTimeSpent.setStartTime(startTime);
                    userTimeSpent.setEndTime(endTime);
                    userTimeSpent.setResourceId(resourceId);
                    userTimeSpent.setResourceName(resourceType);
                    userTimeSpent.setModuleId(moduleId);
                    userTimeSpent.setModuleName(moduleName);

                    Call<ResponseBody> call = mNetworkModel.uploadUserTimeSpent(userTimeSpent);
                    Response<ResponseBody> response = call.execute();
                    if (response != null) {
                        int code = response.code();
                        if (response.isSuccessful()) {
                            ResponseBody body = response.body();
                            Log.e("UserTimeSpent--", "Successful");
                            e.onNext(body);
                        } else if (code == 404) {
                            throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                        } else if ((code == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ResponseBody> response2 = call.clone().execute();
                            if (response2 != null) {

                                int code2 = response2.code();

                                if (response2.isSuccessful()) {
                                    ResponseBody body = response2.body();
                                    Log.e("UserTimeSpent--", "Successful");
                                    e.onNext(body);
                                } else if ((code2 == 401)) {
                                    mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                                } else if (code2 == 404) {
                                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                                } else {
                                    Log.e("UserTimeSpent--", "Failed");
                                    throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                                }
                            }
                        } else {
                            Log.e("UserTimeSpent--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageQuestionFetchFailed));
                        }
                    }

                    e.onComplete();

                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
        }

    }


    /*To upload user time spent on activity*/
    @SuppressLint("CheckResult")
    public void uploadVideoWatchState(final boolean isWatchStarted, final String moduleId, final String resourceId) {

        Completable.complete()
                .subscribeOn(Schedulers.io())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                        Response<ResponseBody> response;

                        if (isWatchStarted) {
                            response = mNetworkModel.uploadVideoWatchStarted(resourceId, moduleId).execute();
                        } else {
                            response = mNetworkModel.uploadVideoWatchEnded(resourceId, moduleId).execute();
                        }

                        if (response != null && response.isSuccessful()) {
                            Log.e("VideoState--", "Successful");
                        } else {
                            Log.e("VideoState--", "Failed");

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }

}
