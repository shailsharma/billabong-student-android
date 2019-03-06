package in.securelearning.lil.android.home.model;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.couchbase.lite.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProgressBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.utils.MindSparkPrefs;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectResult;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkLoginRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestion;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionChoice;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionParent;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionSubmit;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkTopicListRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkTopicResult;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.syncadapter.dataobjects.MindSparkLoginResponse.RESULT_CODE_SUCCESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.MindSparkLoginResponse.RESULT_CODE_UNAUTHORIZED;
import static in.securelearning.lil.android.syncadapter.dataobjects.MindSparkResult.RESULT_CODE_JWT_EXPIRED;

public class FlavorHomeModel {

    @Inject
    FlavorNetworkModel mFlavorNetworkModel;

    @Inject
    Context mContext;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    GroupModel mGroupModel;

    public FlavorHomeModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    /*Set activity status bar style immersive*/
    public void setImmersiveStatusBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /*An alert dialog, which contains progress text*/
    public Dialog loadingDialog(Context context, String message) {
        LayoutProgressBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_progress, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(binding.getRoot());
        binding.textViewLoading.setText(message);

        dialog.show();
        return dialog;
    }

    /*To get today recaps*/
    public Observable<ArrayList<LessonPlanMinimal>> getTodayRecaps() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<LessonPlanMinimal>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<LessonPlanMinimal>> e) throws Exception {
                Call<ArrayList<LessonPlanMinimal>> call = mFlavorNetworkModel.getTodayRecap();
                Response<ArrayList<LessonPlanMinimal>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanMinimal", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<LessonPlanMinimal>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanMinimal", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanMinimal", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanMinimal", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }


    public Observable<ArrayList<LessonPlanChapterResult>> getChaptersResult() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<LessonPlanChapterResult>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<LessonPlanChapterResult>> e) throws Exception {
                Call<ArrayList<LessonPlanChapterResult>> call = mFlavorNetworkModel.getChaptersResult();
                Response<ArrayList<LessonPlanChapterResult>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanChapterResult", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<LessonPlanChapterResult>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanChapterResult", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanChapterResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanChapterResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To get subjects of logged in user*/
    public Observable<LessonPlanSubjectResult> getMySubject() {
        return Observable.create(new ObservableOnSubscribe<LessonPlanSubjectResult>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanSubjectResult> e) throws Exception {

                Call<LessonPlanSubjectResult> call = mFlavorNetworkModel.getMySubject(new LessonPlanSubjectPost(mContext.getString(R.string.subject).toLowerCase()));
                Response<LessonPlanSubjectResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanSubject", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LessonPlanSubjectResult> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanSubject", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanSubject", "Failed");
                        Log.e("LessonPlanSubject", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanSubject", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To get today recaps*/
    public Observable<LessonPlanChapterResult> getChapterResult(final LessonPlanChapterPost lessonPlanChapterPost) {
        return Observable.create(new ObservableOnSubscribe<LessonPlanChapterResult>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanChapterResult> e) throws Exception {
                Call<LessonPlanChapterResult> call = mFlavorNetworkModel.getChapterResult(lessonPlanChapterPost);
                Response<LessonPlanChapterResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanChapterResult", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LessonPlanChapterResult> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanChapterResult", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanSubject", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To fetch details of subject*/
    public Observable<LessonPlanSubjectDetails> getSubjectDetails(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<LessonPlanSubjectDetails>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanSubjectDetails> e) throws Exception {

                Call<LessonPlanSubjectDetails> call = mFlavorNetworkModel.getSubjectDetails(subjectId);
                Response<LessonPlanSubjectDetails> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("SubjectDetails", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LessonPlanSubjectDetails> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("SubjectDetails", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                    } else {
                        Log.e("LessonPlanChapterResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanChapterResult", "Failed");
                    Log.e("SubjectDetails", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To fetch third party meta information*/
    public Observable<ThirdPartyMapping> fetchThirdPartyMapping(final String subjectId, final String topicId) {
        return Observable.create(new ObservableOnSubscribe<ThirdPartyMapping>() {
            @Override
            public void subscribe(ObservableEmitter<ThirdPartyMapping> e) throws Exception {

                Call<ThirdPartyMapping> call = mFlavorNetworkModel.fetchThirdPartyMapping(new ThirdPartyMapping(subjectId, topicId));
                Response<ThirdPartyMapping> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("SubjectDetails", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ThirdPartyMapping> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("SubjectDetails", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                    } else {
                        Log.e("LessonPlanChapterResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanChapterResult", "Failed");
                    Log.e("SubjectDetails", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*Contains logic to login user to Mind Spark environment and
     * getting response to use it for practice. */
    public Observable<MindSparkLoginResponse> loginUserToMindSpark(final String topicId) {
        return Observable.create(new ObservableOnSubscribe<MindSparkLoginResponse>() {
            @Override
            public void subscribe(ObservableEmitter<MindSparkLoginResponse> e) throws Exception {

                MindSparkLoginRequest mindSparkLoginRequest = new MindSparkLoginRequest();
                mindSparkLoginRequest.setAuthToken(AppPrefs.getIdToken(mContext));
                mindSparkLoginRequest.setUserId(mAppUserModel.getApplicationUser().getMindSparkUserName());
                //mindSparkLoginRequest.setTopicId("59d98cfa1c5edc24e4024985");
                mindSparkLoginRequest.setTopicId(topicId);
                mindSparkLoginRequest.setVendorCode("euro");

                Call<MindSparkLoginResponse> call = mFlavorNetworkModel.loginUserToMindSpark(mindSparkLoginRequest);
                Response<MindSparkLoginResponse> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkLoginResponse mindSparkLoginResponse = response.body();

                    assert mindSparkLoginResponse != null;
                    if (!TextUtils.isEmpty(mindSparkLoginResponse.getResultCode()) && mindSparkLoginResponse.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                        Log.e("MindSparkLoginResponse", "Successful");
                        saveMindSparkJwt(mindSparkLoginResponse.getMindSparkAuthToken());
                        e.onNext(mindSparkLoginResponse);
                    } else if (mindSparkLoginResponse.getResultCode().equals(RESULT_CODE_UNAUTHORIZED) && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkLoginResponse> response2 = call.clone().execute();
                        Log.e("MindSparkLoginResponse", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkLoginResponse mindSparkLoginResponse2 = response2.body();

                            assert mindSparkLoginResponse2 != null;
                            if (!TextUtils.isEmpty(mindSparkLoginResponse2.getResultCode()) && mindSparkLoginResponse2.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                                Log.e("MindSparkLoginResponse", "Successful");
                                saveMindSparkJwt(mindSparkLoginResponse.getMindSparkAuthToken());
                                e.onNext(mindSparkLoginResponse2);
                            } else if (mindSparkLoginResponse2.getResultCode().equals(RESULT_CODE_UNAUTHORIZED)) {
                                Log.e("MindSparkLoginResponse", "Failed");
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));

                            }

                        }
                    } else {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }

                } else {
                    Log.e("MindSparkLoginResponse", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*Contains logic to login user to Mind Spark environment and
     * getting response to use it for practice. */
    public Observable<MindSparkQuestionParent> getMindSparkQuestion(final MindSparkQuestionRequest mindSparkQuestionRequest) {
        return Observable.create(new ObservableOnSubscribe<MindSparkQuestionParent>() {
            @Override
            public void subscribe(ObservableEmitter<MindSparkQuestionParent> e) throws Exception {
                mindSparkQuestionRequest.setJWT(MindSparkPrefs.getMindSparkJsonWebToken(mContext));
                Call<MindSparkQuestionParent> call = mFlavorNetworkModel.getMindSparkQuestion(mindSparkQuestionRequest);
                Response<MindSparkQuestionParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkQuestionParent mindSparkQuestionResponse = response.body();

                    assert mindSparkQuestionResponse != null;
                    if (!TextUtils.isEmpty(mindSparkQuestionResponse.getResultCode()) && mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                        Log.e("MSQuestionResponse", "Successful");
                        e.onNext(mindSparkQuestionResponse);
                    } else if ((mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_UNAUTHORIZED) ||
                            mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_JWT_EXPIRED))
                            && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkQuestionParent> response2 = call.clone().execute();
                        Log.e("MSQuestionResponse", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkQuestionParent mindSparkQuestionResponse2 = response2.body();

                            assert mindSparkQuestionResponse2 != null;
                            if (!TextUtils.isEmpty(mindSparkQuestionResponse2.getResultCode()) && mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                                Log.e("MSQuestionResponse", "Successful");
                                e.onNext(mindSparkQuestionResponse2);
                            } else if (mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_UNAUTHORIZED)) {
                                Log.e("MSQuestionResponse", "Failed");
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));

                            }

                        }
                    } else {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }

                } else {
                    Log.e("MindSparkLoginResponse", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*To submit current question response and fetch new question*/
    public Observable<MindSparkQuestionParent> submitAndFetchNewQuestion(final MindSparkQuestionSubmit mindSparkQuestionSubmit) {
        return Observable.create(new ObservableOnSubscribe<MindSparkQuestionParent>() {
            @Override
            public void subscribe(ObservableEmitter<MindSparkQuestionParent> e) throws Exception {

                Call<MindSparkQuestionParent> call = mFlavorNetworkModel.submitAndFetchNewQuestion(mindSparkQuestionSubmit);
                Response<MindSparkQuestionParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkQuestionParent mindSparkQuestionResponse = response.body();

                    assert mindSparkQuestionResponse != null;
                    if (!TextUtils.isEmpty(mindSparkQuestionResponse.getResultCode()) && mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                        Log.e("MSQuestionResponse", "Successful");
                        e.onNext(mindSparkQuestionResponse);
                    } else if (mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_UNAUTHORIZED) && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkQuestionParent> response2 = call.clone().execute();
                        Log.e("MSQuestionResponse", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkQuestionParent mindSparkQuestionResponse2 = response2.body();

                            assert mindSparkQuestionResponse2 != null;
                            if (!TextUtils.isEmpty(mindSparkQuestionResponse2.getResultCode()) && mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                                Log.e("MSQuestionResponse", "Successful");
                                e.onNext(mindSparkQuestionResponse2);
                            } else if (mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_UNAUTHORIZED)) {
                                Log.e("MSQuestionResponse", "Failed");
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));

                            }

                        }
                    } else {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }

                } else {
                    Log.e("MindSparkLoginResponse", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }


    /*To fetch all topic list of mind spark*/
    public Observable<MindSparkTopicResult> getMindSparkTopicResult() {
        return Observable.create(new ObservableOnSubscribe<MindSparkTopicResult>() {
            @Override
            public void subscribe(ObservableEmitter<MindSparkTopicResult> e) throws Exception {

                Call<MindSparkTopicResult> call = mFlavorNetworkModel.getMindSparkTopicResult(new MindSparkTopicListRequest(MindSparkPrefs.getMindSparkJsonWebToken(mContext)));
                Response<MindSparkTopicResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkTopicResult mindSparkTopicResult = response.body();

                    assert mindSparkTopicResult != null;
                    if (!TextUtils.isEmpty(mindSparkTopicResult.getResultCode()) && mindSparkTopicResult.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                        Log.e("MSTopicResult", "Successful");
                        e.onNext(mindSparkTopicResult);
                    } else if ((mindSparkTopicResult.getResultCode().equals(RESULT_CODE_UNAUTHORIZED) ||
                            mindSparkTopicResult.getResultCode().equals(RESULT_CODE_JWT_EXPIRED))
                            && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkTopicResult> response2 = call.clone().execute();
                        Log.e("MSTopicResult", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkTopicResult mindSparkTopicResult2 = response2.body();

                            assert mindSparkTopicResult2 != null;
                            if (!TextUtils.isEmpty(mindSparkTopicResult2.getResultCode()) && mindSparkTopicResult2.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                                Log.e("MSTopicResult", "Successful");
                                e.onNext(mindSparkTopicResult2);
                            } else if (mindSparkTopicResult2.getResultCode().equals(RESULT_CODE_UNAUTHORIZED)) {
                                Log.e("MSTopicResult", "Failed");
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));

                            }

                        }
                    } else {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }

                } else {
                    Log.e("MindSparkLoginResponse", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*Method to save Mind Spark Json Web Token to shared preference*/
    private void saveMindSparkJwt(String mindSparkAuthToken) {
        if (!TextUtils.isEmpty(mindSparkAuthToken)) {
            Log.e("MS_JWT--", mindSparkAuthToken);
            MindSparkPrefs.setMindSparkJsonWebToken(mContext, mindSparkAuthToken);
        }
    }

    public Question convertMSQuestionToLILQuestion(int questionOrder, String contentId, MindSparkQuestion mindSparkQuestion) {
        Question question = new Question();
        question.setOrder(questionOrder);
        question.setUidQuestion(contentId);
        question.setQuestionText(mindSparkQuestion.getQuestionTextBody());
        question.setExplanation(mindSparkQuestion.getQuestionExplanation());
        question.setProgressionRule(mindSparkQuestion.getRevisionNo());
        ArrayList<String> hints = new ArrayList<>();
        hints.add("This page contains HTML examples of text - examples of text-specific code that you can use for your own website.\n" +
                "\n" +
                "To use the code, copy it straight from the text box and paste it into your own website. Feel free to modify it as required.");
        hints.add("Example HTML Document. The following text should be typed in to a local file on a system which is equipped with a Web browser.");
        question.setQuestionHints(getQuestionHints(mindSparkQuestion.getQuestionHints()));
        question.setQuestionChoices(getQuestionChoices(mindSparkQuestion.getQuestionResponse().getMindSparkQuestionMCQPattern().getCorrectAnswer(), mindSparkQuestion.getQuestionResponse().getMindSparkQuestionMCQPattern().getQuestionChoices()));
        question.setQuestionType(Question.TYPE_DISPLAY_RADIO);

        return question;
    }

    private ArrayList<QuestionHint> getQuestionHints(ArrayList<String> mindSparkQuestionHints) {
        ArrayList<QuestionHint> questionHints = new ArrayList<>();
        for (int i = 0; i < mindSparkQuestionHints.size(); i++) {
            questionHints.add(new QuestionHint(i, mindSparkQuestionHints.get(i)));
        }
        return questionHints;
    }

    private ArrayList<QuestionChoice> getQuestionChoices(String correctAnswer, ArrayList<MindSparkQuestionChoice> mindSparkQuestionChoices) {
        ArrayList<QuestionChoice> questionChoices = new ArrayList<>();
        correctAnswer = decodeBase64String(correctAnswer);
        int correctIndex = Integer.parseInt(correctAnswer);
        for (int i = 0; i < mindSparkQuestionChoices.size(); i++) {
            MindSparkQuestionChoice mindSparkQuestionChoice = mindSparkQuestionChoices.get(i);
            if (correctIndex == i) {
                questionChoices.add(new QuestionChoice("", true, mindSparkQuestionChoice.getValue()));
            } else {
                questionChoices.add(new QuestionChoice("", false, mindSparkQuestionChoice.getValue()));

            }
        }
        return questionChoices;
    }

    public Observable<LRPAResult> fetchLRPA(final String topicId, final String type) {
        return Observable.create(new ObservableOnSubscribe<LRPAResult>() {
            @Override
            public void subscribe(ObservableEmitter<LRPAResult> e) throws Exception {
                LRPAPost lrpaPost = new LRPAPost();
                lrpaPost.setTopicId(topicId);
                lrpaPost.setType(type);
                Call<LRPAResult> call = mFlavorNetworkModel.fetchLRPA(lrpaPost);
                Response<LRPAResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LRPAResult", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LRPAResult> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LRPAResult", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                    } else {
                        Log.e("LRPAResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LRPAResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    public Class getCourseClass(AboutCourseMinimal course) {
        if (!TextUtils.isEmpty(course.getCourseType())) {
            String courseType = course.getCourseType();
            if (courseType.equalsIgnoreCase("digitalbook")) {
                return DigitalBook.class;
            } else if (courseType.equalsIgnoreCase("videocourse")) {
                return VideoCourse.class;
            } else if (courseType.contains("feature")) {
                return MicroLearningCourse.class;
            } else if (courseType.contains("map")) {
                return ConceptMap.class;
            } else if (courseType.contains("interactiveim")) {
                return InteractiveImage.class;
            } else if (courseType.contains("interactivevi")) {
                return InteractiveVideo.class;
            } else {
                if (course.getPopUpType() != null && !TextUtils.isEmpty(course.getPopUpType().getValue())) {
                    return PopUps.class;
                }
            }

        }
        return DigitalBook.class;
    }


    public String getCourseType(AboutCourseMinimal course) {
        if (!TextUtils.isEmpty(course.getCourseType())) {
            String courseType = course.getCourseType();
            if (courseType.equalsIgnoreCase("digitalbook")) {
                return "Digital Book";
            } else if (courseType.equalsIgnoreCase("videocourse")) {
                return "Video Course";
            } else if (courseType.contains("feature")) {
                return "Recap";
            } else if (courseType.contains("map")) {
                return "Concept Map";
            } else if (courseType.contains("interactiveim")) {
                return "Interactive Image";
            } else if (courseType.contains("interactivevi")) {
                return "Interactive Video";
            } else {
                if (course.getPopUpType() != null && !TextUtils.isEmpty(course.getPopUpType().getValue())) {
                    return "Pop Up";
                }
            }
        }
        return "";
    }

    public Group getGroupFromId(String groupId) {
        return mGroupModel.getGroupFromUidSync(groupId);
    }

    public void downloadGroup(String groupId) {
        JobCreator.createDownloadGroupJob(groupId).execute();
    }

    public void downloadGroupPostAndResponse(String groupId) {
        JobCreator.createDownloadGroupPostNResponseJob(groupId).execute();
    }

    public String decodeBase64String(String encodedString) {
        byte[] valueDecoded = new byte[0];
        try {
            valueDecoded = Base64.decode(encodedString.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String(valueDecoded);
    }


}
