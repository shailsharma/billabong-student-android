package in.securelearning.lil.android.quizpreview.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLConnection;
import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutQuestionPlayerBinding;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionExplanation;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.events.QuizCompletedEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.quizpreview.InjectorQuizPreview;
import in.securelearning.lil.android.quizpreview.model.QuestionResponseModelApp;
import in.securelearning.lil.android.quizpreview.model.QuizPreviewModel;
import in.securelearning.lil.android.syncadapter.dataobject.MasteryRequestObject;
import in.securelearning.lil.android.syncadapter.dataobject.MasteryRequestObjectFilter;
import in.securelearning.lil.android.syncadapter.dataobject.QuizResponse;
import in.securelearning.lil.android.syncadapter.dataobject.SkillMasteryQuestionGetData;
import in.securelearning.lil.android.syncadapter.dataobject.SkillMasteryQuestionLevels;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
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

import static in.securelearning.lil.android.base.utils.ImageUtils.getDrawableFromPath;
import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Chaitendra on 26-Oct-17.
 */

public class QuestionPlayerActivity extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    QuizPreviewModel mQuizPreviewModel;
    @Inject
    QuestionResponseModelApp mQuestionResponseModelApp;
    @Inject
    RxBus mRxBus;
    LayoutQuestionPlayerBinding mBinding;

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DIFFICULTY_LEVEL = "difficultyLevel";
    private static final String SKILL_MAP = "skillMap";
    private static final String TYPE = "type";
    private static final String QUIZ_ID = "quizId";
    private static final String QUESTIONS_LIST = "questionsList";
    private static final int QUESTION_SIZE_LOW = 15;
    private static final int QUESTION_SIZE_MEDIUM = 15;
    private static final int QUESTION_SIZE_HARD = 15;
    private static final int MAX_QUESTION_COMPLEXITY_LEVEL = 2;
    private static final int MIN_QUESTION_COMPLEXITY_LEVEL = 0;
    private static final int QUESTION_TOTAL_CORRECT = 3;
    private static final int QUESTION_TOTAL_INCORRECT = 3;

    private int[] mQuestionsCounterArray;

    private String mId;
    private String mTitle;
    private String mDifficultyLevel;
    private String mType;
    private String mQuizId;
    private HomeModel.SkillMap mSkillMap = null;
    private int mQuestionCounter = 0;
    private int mQuestionsSize = 0;
    private int mHintsSize = 0;
    private int mTotalCorrect = 0;
    private int mTotalInCorrect = 0;
    private int mComplexityLevel = 0;
    //    private int mLowQuestionsCounter = -1;
//    private int mMediumQuestionsCounter = -1;
//    private int mHighQuestionsCounter = -1;
    private ArrayList<QuestionResponse> mQuestionResponses = new ArrayList<>();
    private SkillMasteryQuestionGetData mSkillMasteryQuestionGetData;
    private int mHintCounter = 0;
    private boolean mIsCorrectResponse = false;
    private Question mQuestion;
    private MenuItem mDoneMenuItem;
    private QuizResponse mQuizResponse;

    @Override
    public void onBackPressed() {
        showBackAlertDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorQuizPreview.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_question_player);
        handleIntent();
        initializeClickListeners();

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_action_done, menu);
//        mDoneMenuItem = menu.findItem(R.id.actionDone);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

//            case R.id.actionDone:
//                doneClickAction();
//                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void doneMenuItemVisibility(boolean b) {
        if (mDoneMenuItem != null) {
            mDoneMenuItem.setVisible(b);
        }
    }

    public static Intent getStartIntent(Context context, String skillId, String skillName, String questionLevel, HomeModel.SkillMap skillMap, String type) {
        Intent intent = new Intent(context, QuestionPlayerActivity.class);
        intent.putExtra(ID, skillId);
        intent.putExtra(TITLE, skillName);
        intent.putExtra(DIFFICULTY_LEVEL, questionLevel);
        intent.putExtra(SKILL_MAP, skillMap);
        intent.putExtra(TYPE, type);
        return intent;
    }

    public static Intent getStartIntentForQuizLocal(Context context, String quizId, String title, HomeModel.SkillMap skillMap, ArrayList<Question> questions) {
        Intent intent = new Intent(context, QuestionPlayerActivity.class);
        intent.putExtra(QUIZ_ID, quizId);
        intent.putExtra(TITLE, title);
        intent.putExtra(DIFFICULTY_LEVEL, 0);
        intent.putExtra(SKILL_MAP, skillMap);
        intent.putExtra(QUESTIONS_LIST, questions);
        return intent;
    }

    public static Intent getStartIntentForQuizOnline(Context context, String quizId) {
        Intent intent = new Intent(context, QuestionPlayerActivity.class);
        intent.putExtra(QUIZ_ID, quizId);
        intent.putExtra(DIFFICULTY_LEVEL, context.getString(R.string.label_low));
        return intent;
    }

    private void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mQuestionsCounterArray = new int[MAX_QUESTION_COMPLEXITY_LEVEL + 1];
            for (int i = 0; i < mQuestionsCounterArray.length; i++) {
                mQuestionsCounterArray[i] = -1;
            }
            mId = getIntent().getStringExtra(ID);
            mTitle = getIntent().getStringExtra(TITLE);
            mDifficultyLevel = getIntent().getStringExtra(DIFFICULTY_LEVEL);
            mType = getIntent().getStringExtra(TYPE);
            mQuizId = getIntent().getStringExtra(QUIZ_ID);

            mSkillMap = (HomeModel.SkillMap) getIntent().getSerializableExtra(SKILL_MAP);
            mQuizResponse = new QuizResponse();
            if (mSkillMasteryQuestionGetData != null) {
                MetaInformation metaInformation = new MetaInformation();
                metaInformation.setLanguage(mSkillMap.getLanguage());
                metaInformation.setBoard(mSkillMap.getBoard());
                metaInformation.setGrade(mSkillMap.getGrade());
                metaInformation.setTopic(mSkillMap.getTopic());
                metaInformation.setSubject(mSkillMap.getSubject());
                metaInformation.setLearningLevel(mSkillMap.getLearningLevel());
                mQuizResponse.setMetaInformation(metaInformation);
            }
            mQuizResponse.setQuizId(mQuizId);
            setUpToolbar();
            if (!TextUtils.isEmpty(mDifficultyLevel)) {
                setQuestionSize(mDifficultyLevel);
            }

            ArrayList<Question> questions = (ArrayList<Question>) getIntent().getSerializableExtra(QUESTIONS_LIST);
            fetchQuestions(mId, mType, mQuizId, questions);
        } else {
            finish();

        }
    }

    private void setQuestionSize(String questionLevel) {
        if (questionLevel.equals(getString(R.string.label_low))) {
            mQuestionsSize = QUESTION_SIZE_LOW;
            mComplexityLevel = 0;
        } else if (questionLevel.equals(getString(R.string.label_medium))) {
            mQuestionsSize = QUESTION_SIZE_MEDIUM;
            mComplexityLevel = 1;
        } else if (questionLevel.equals(getString(R.string.label_high))) {
            mQuestionsSize = QUESTION_SIZE_HARD;
            mComplexityLevel = 2;
        }
    }

    private void setUpToolbar() {
//        getWindow().setStatusBarColor(ContextCompat.getColor(MindSparkPlayerActivity.this, R.color.colorGreen));
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorGreen)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.action_close_w);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setElevation(0f);
        setUiTitle();
    }

    private void setUiTitle() {
        if (!TextUtils.isEmpty(mTitle)) {
            Completable.complete().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            SpannableString title = new SpannableString(mTitle);
                            title.setSpan(new TypefaceSpan("sans-serif-condensed"), 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            setTitle(title);
                        }
                    });

        }
    }

    private void initializeQuestionUi() {


        if (mComplexityLevel <= MAX_QUESTION_COMPLEXITY_LEVEL && mComplexityLevel >= MIN_QUESTION_COMPLEXITY_LEVEL) {
            if (mQuestionsCounterArray[mComplexityLevel] + 1 >= mSkillMasteryQuestionGetData.getQuestions()[mComplexityLevel].size()) {
                int i = mComplexityLevel - 1;
                while (i >= MIN_QUESTION_COMPLEXITY_LEVEL) {
                    if (mQuestionsCounterArray[i] + 1 < mSkillMasteryQuestionGetData.getQuestions()[i].size()) {
                        mComplexityLevel = i;
                        break;
                    }
                    i--;
                }

                if (i < MIN_QUESTION_COMPLEXITY_LEVEL) {
                    i = mComplexityLevel + 1;
                    while (i <= MAX_QUESTION_COMPLEXITY_LEVEL) {
                        if (mQuestionsCounterArray[i] + 1 < mSkillMasteryQuestionGetData.getQuestions()[i].size()) {
                            mComplexityLevel = i;
                            break;
                        }
                        i++;
                    }
                }


            }
            if (mComplexityLevel <= MAX_QUESTION_COMPLEXITY_LEVEL && mComplexityLevel >= MIN_QUESTION_COMPLEXITY_LEVEL) {

                mQuestion = mSkillMasteryQuestionGetData.getQuestions()[mComplexityLevel].get(++mQuestionsCounterArray[mComplexityLevel]);

                mHintsSize = mQuestion.getQuestionHints().size();
                setUpdateValues(mQuestionCounter, mQuestionsSize);
                setQuestionText(mQuestion);
                setHintView(mQuestion);
                setChoicesView(mQuestion);
                setTimerView(mQuestion);
                setQuestionResource(mQuestion);
                setLevelView();
                return;
            }
        }
        finish();

//        if (mComplexityLevel == 0) {
//            mLowQuestionsCounter++;
//            if (mLowQuestionsCounter >= mSkillMasteryQuestionGetData.getLowLevelQuestion().getResults().size()) {
//
//            }
//            mQuestion = mSkillMasteryQuestionGetData.getLowLevelQuestion().getResults().get(mLowQuestionsCounter);
//        } else if (mComplexityLevel == 1) {
//            mMediumQuestionsCounter++;
//            mQuestion = mSkillMasteryQuestionGetData.getMediumLevelQuestion().getResults().get(mMediumQuestionsCounter);
//        } else if (mComplexityLevel > 2 || mComplexityLevel == 2) {
//            mHighQuestionsCounter++;
//        }


    }

    private void initializeClickListeners() {
        mBinding.buttonQuestionHints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showNextHint(mHintCounter);
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
            }
        });

        mBinding.buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doneClickAction();
            }
        });
    }

    private void fetchQuestions(final String id, final String type, final String quizId, final ArrayList<Question> questions) {
        if (GeneralUtils.isNetworkAvailable(this) || questions != null) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.message_fetching_question), false);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onBackPressed();
                }
            });

            Observable.create(new ObservableOnSubscribe<SkillMasteryQuestionGetData>() {
                @Override
                public void subscribe(ObservableEmitter<SkillMasteryQuestionGetData> subscriber) throws Exception {
                    if (questions != null) {
                        mQuestionsSize = questions.size();
                        mSkillMasteryQuestionGetData = new SkillMasteryQuestionGetData();
                        SkillMasteryQuestionLevels levels = new SkillMasteryQuestionLevels(questions, 0);
                        mSkillMasteryQuestionGetData.setLowLevelQuestion(levels);
                        subscriber.onNext(mSkillMasteryQuestionGetData);
                    } else {
                        if (!TextUtils.isEmpty(quizId)) {
                            final Call<Quiz> call = mNetworkModel.fetchQuiz(quizId);
                            Response<Quiz> response = call.execute();
                            if (response != null && response.isSuccessful()) {

                                Quiz quiz = response.body();

                                mQuestionsSize = quiz.getQuestions().size();
                                mTitle = quiz.getTitle();
                                setUiTitle();

                                mQuizResponse.setMetaInformation(quiz.getMetaInformation());

                                mSkillMasteryQuestionGetData = new SkillMasteryQuestionGetData();
                                SkillMasteryQuestionLevels levelsL = new SkillMasteryQuestionLevels(quiz.getQuestions(), 0);
                                SkillMasteryQuestionLevels levelsM = new SkillMasteryQuestionLevels(new ArrayList<Question>(), 0);
                                SkillMasteryQuestionLevels levelsH = new SkillMasteryQuestionLevels(new ArrayList<Question>(), 0);
                                mSkillMasteryQuestionGetData.setLowLevelQuestion(levelsL);
                                mSkillMasteryQuestionGetData.setMediumLevelQuestion(levelsM);
                                mSkillMasteryQuestionGetData.setHighLevelQuestion(levelsH);
                                subscriber.onNext(mSkillMasteryQuestionGetData);

                            } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getBaseContext())) {
                                Response<Quiz> secondResponse = call.clone().execute();
                                if (secondResponse != null && secondResponse.isSuccessful()) {
                                    Quiz quiz = secondResponse.body();

                                    mTitle = quiz.getTitle();
                                    setUiTitle();

                                    mQuizResponse.setMetaInformation(quiz.getMetaInformation());

                                    mSkillMasteryQuestionGetData = new SkillMasteryQuestionGetData();
                                    SkillMasteryQuestionLevels levels = new SkillMasteryQuestionLevels(quiz.getQuestions(), 0);
                                    mSkillMasteryQuestionGetData.setLowLevelQuestion(levels);
                                    subscriber.onNext(mSkillMasteryQuestionGetData);


                                } else if ((secondResponse.code() == 401)) {
                                    startActivity(LoginActivity.getUnauthorizedIntent(getBaseContext()));
                                } else {
                                    Log.e("QuestionsFetch2--", "Failed");
                                    throw new Exception(getString(R.string.messageQuestionFetchFailed));
                                }
                            } else {
                                Log.e("QuestionsFetch1--", "Failed");
                                throw new Exception(getString(R.string.messageQuestionFetchFailed));
                            }

                        } else {
                            MasteryRequestObject masteryRequestObject = new MasteryRequestObject();
                            MasteryRequestObjectFilter masteryRequestObjectFilter = new MasteryRequestObjectFilter();
                            if (type.equals(getString(R.string.labelTopic))) {
                                masteryRequestObjectFilter.setSkillId("");
                                masteryRequestObjectFilter.setTopicId(id);
                            } else if (type.equals(getString(R.string.labelSkill))) {
                                masteryRequestObjectFilter.setSkillId(id);
                                masteryRequestObjectFilter.setTopicId("");
                            }
                            ArrayList<String> arrayList = new ArrayList<String>();
                            arrayList.add(getString(R.string.label_low));
                            arrayList.add(getString(R.string.label_medium));
                            arrayList.add(getString(R.string.label_high));
                            masteryRequestObjectFilter.setComplexityLevels(arrayList);
                            masteryRequestObject.setMasteryRequestObjectFilter(masteryRequestObjectFilter);
                            masteryRequestObject.setLimit(mQuestionsSize);
                            masteryRequestObject.setSkip(0);

                            final Call<SkillMasteryQuestionGetData> questionCall = mNetworkModel.fetchBySkillAndComplexityLevel(masteryRequestObject);
                            Response<SkillMasteryQuestionGetData> response = questionCall.execute();

                            if (response != null && response.isSuccessful()) {
                                mSkillMasteryQuestionGetData = response.body();
                                Log.e("QuestionFetch1--", "Successful");

                                subscriber.onNext(mSkillMasteryQuestionGetData);


                            } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getBaseContext())) {
                                Response<SkillMasteryQuestionGetData> response2 = questionCall.clone().execute();
                                if (response2 != null && response2.isSuccessful()) {
                                    mSkillMasteryQuestionGetData = response2.body();
                                    Log.e("QuestionsFetch2--", "Successful");
                                    subscriber.onNext(mSkillMasteryQuestionGetData);
                                } else if ((response2.code() == 401)) {
                                    startActivity(LoginActivity.getUnauthorizedIntent(getBaseContext()));
                                } else {
                                    Log.e("QuestionsFetch2--", "Failed");
                                    throw new Exception(getString(R.string.messageQuestionFetchFailed));
                                }
                            } else {
                                Log.e("QuestionsFetch1--", "Failed");
                                throw new Exception(getString(R.string.messageQuestionFetchFailed));
                            }
                        }
                    }
                    subscriber.onComplete();

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<SkillMasteryQuestionGetData>() {
                        @Override
                        public void accept(SkillMasteryQuestionGetData questionGetData) throws Exception {
                            progressDialog.dismiss();
                            if (questionGetData != null && questionGetData.getLowLevelQuestion() != null
                                    && questionGetData.getMediumLevelQuestion() != null
                                    && questionGetData.getHighLevelQuestion() != null) {

                                int questionTotalCount = questionGetData.getLowLevelQuestion().getResults().size() +
                                        questionGetData.getMediumLevelQuestion().getResults().size() +
                                        questionGetData.getHighLevelQuestion().getResults().size();

                                if (questionTotalCount <= 0) {

                                    showAlertDialog(getString(R.string.error_no_question_found));

                                } else {
                                    if (questionTotalCount < mQuestionsSize) {
                                        mQuestionsSize = questionTotalCount;
                                    }

                                    mSkillMasteryQuestionGetData = questionGetData;
                                    mSkillMasteryQuestionGetData.updateQuestions();
                                    mBinding.layoutTop.setVisibility(View.VISIBLE);
                                    mBinding.buttonDone.setVisibility(View.VISIBLE);
                                    initializeQuestionUi();
                                }

                            } else {
                                showAlertDialog(getString(R.string.error_no_question_found));
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(getBaseContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                            progressDialog.dismiss();
                            throwable.printStackTrace();
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {

                            progressDialog.dismiss();
                        }
                    });
        } else {
            showAlertDialog(getString(R.string.error_message_no_internet));
        }
    }

    private void setTimerView(Question question) {
        mBinding.layoutTimer.setVisibility(View.VISIBLE);
        mBinding.chronometerQuestionTimer.stop();
        mBinding.chronometerQuestionTimer.setBase(SystemClock.elapsedRealtime() + 0);
        mBinding.chronometerQuestionTimer.start();

    }

    private void setUpdateValues(int questionCounter, int questionsSize) {
        mBinding.cardViewChoices.setVisibility(View.VISIBLE);
        AnimationUtils.fadeInFast(getBaseContext(), mBinding.scrollView);
        mBinding.textViewQuestionCounter.setText(String.valueOf(questionCounter + 1));
        mBinding.textViewQuestionsSize.setText(" / " + String.valueOf(questionsSize));
    }

    private void setQuestionText(Question question) {
        mBinding.textViewQuestion.setText(TextViewMore.stripHtml(question.getQuestionText()));

    }

    private void setQuestionResource(Question question) {
        String resourcePathImage = question.fetchQuestionImage().getDeviceURL();
        String resourcePathVideo = question.fetchQuestionVideo().getDeviceURL();
        if (!TextUtils.isEmpty(resourcePathImage)) {
            mBinding.layoutQuestionResource.setVisibility(View.VISIBLE);
            mBinding.imageViewResourceThumbnail.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePathImage));
            mBinding.imageViewResourceThumbnail.setTag(resourcePathImage);
            mBinding.imageViewResourceType.setImageResource(R.drawable.action_image_w);
        } else if (!TextUtils.isEmpty(resourcePathVideo)) {
            mBinding.layoutQuestionResource.setVisibility(View.VISIBLE);
            mBinding.imageViewResourceThumbnail.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePathVideo));
            mBinding.imageViewResourceThumbnail.setTag(resourcePathVideo);
            mBinding.imageViewResourceType.setImageResource(R.drawable.action_video_w);
        } else {
            mBinding.layoutQuestionResource.setVisibility(View.GONE);
        }

        mBinding.imageViewResourceThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResource(mBinding.imageViewResourceThumbnail.getTag().toString());
            }
        });

    }

    private void setChoicesView(Question question) {
        String questionType = question.getQuestionType();
        boolean isChoiceTypeImage = question.getChoiceTypeImage();
        ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();
        mBinding.layoutChoices.removeAllViews();

        LayoutInflater layoutInflater = this.getLayoutInflater();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            layoutInflater.inflate(R.layout.layout_response_mcq_single_correct, mBinding.layoutChoices);
            RadioGroup radioGroup = (RadioGroup) mBinding.layoutChoices.findViewById(R.id.radio_group_response);

            if (isChoiceTypeImage) {
                //char choiceNumber = 'A';
                for (final QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_response_item_mcq_single_correct, radioGroup);
                    RadioButton choice = (RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
                    choice.setClickable(true);
                    choice.setAlpha(1f);
                    // choice.setText(choiceNumber + ". ");
                    choice.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawableFromPath(this.getResources(), /*mBaseFolder + File.separator*/ questionChoice.getChoiceResource().getDeviceURL()), null);
                    choice.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            String path = /*mBaseFolder + File.separator*/ questionChoice.getChoiceResource().getDeviceURL();
                            showResource(path);
                            return false;
                        }
                    });
                    choice.setTag(questionChoice.isChoiceCorrect());
                    //  choiceNumber++;
                }
            } else {
                for (QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_response_item_mcq_single_correct, radioGroup);
                    RadioButton choice = (RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
                    choice.setClickable(true);
                    choice.setAlpha(1f);
                    choice.setText(TextViewMore.stripHtml(questionChoice.getChoiceText()));
                    choice.setTag(questionChoice.isChoiceCorrect());
                }
            }


        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {

            if (isChoiceTypeImage) {
                // char choiceNumber = 'A';
                for (final QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_response_item_mcq_multiple_correct, mBinding.layoutChoices);
                    CheckBox choice = (CheckBox) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                    choice.setClickable(true);
                    choice.setAlpha(1f);
                    //choice.setText(choiceNumber + ". ");
                    choice.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawableFromPath(this.getResources(), /*mBaseFolder + File.separator*/ questionChoice.getChoiceResource().getDeviceURL()), null);
                    choice.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            String path = /*mBaseFolder + File.separator*/ questionChoice.getChoiceResource().getDeviceURL();
                            showResource(path);
                            return false;
                        }
                    });
                    choice.setTag(questionChoice.isChoiceCorrect());
                    //  choiceNumber++;
                }
            } else {
                for (QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_response_item_mcq_multiple_correct, mBinding.layoutChoices);
                    CheckBox choice = (CheckBox) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                    choice.setClickable(true);
                    choice.setAlpha(1f);
                    choice.setText(TextViewMore.stripHtml(questionChoice.getChoiceText()));
                    choice.setTag(questionChoice.isChoiceCorrect());
                }
            }


        }
    }

    private void setHintView(Question question) {
        if (mHintsSize == 1) {
            mBinding.buttonQuestionHints.setVisibility(View.VISIBLE);
            mBinding.buttonQuestionHints.setText(String.valueOf(question.getQuestionHints().size()) + " " + getString(R.string.label_hint));
        }
        if (mHintsSize > 1) {
            mBinding.buttonQuestionHints.setVisibility(View.VISIBLE);
            mBinding.buttonQuestionHints.setText(String.valueOf(question.getQuestionHints().size()) + " " + getString(R.string.label_hints));
        }
        if (mHintsSize == 0) {
            mBinding.buttonQuestionHints.setVisibility(View.GONE);
        }

        mHintCounter = 0;
        mBinding.layoutHints.removeAllViews();
        mBinding.cardViewHints.setVisibility(View.GONE);
    }

    private void showNextHint(int hintCounter) {
        if (hintCounter == mHintsSize) {
            SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.scrollView, getString(R.string.no_more_hints));
        } else {
            QuestionHint questionHint = mQuestion.getQuestionHints().get(hintCounter);
            int finalCounterHint = hintCounter + 1;
            String currentHint = String.valueOf(finalCounterHint);
            String totalHints = String.valueOf(mHintsSize);

            if (mBinding.cardViewHints.getVisibility() == View.GONE) {
                AnimationUtils.pushUpEnter(getBaseContext(), mBinding.cardViewHints);
                mBinding.cardViewHints.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(questionHint.getHintText())) {
                this.getLayoutInflater().inflate(R.layout.layout_response_hint_text, mBinding.layoutHints);
                LinearLayout layout = (LinearLayout) mBinding.layoutHints.getChildAt(mBinding.layoutHints.getChildCount() - 1);
                TextView hintTextView = (TextView) layout.findViewById(R.id.text_view_hint);

                final TextView hintCounterTextView = (TextView) layout.findViewById(R.id.text_view_hint_counter);
                if (finalCounterHint >= 10) {
                    hintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                } else {
                    hintCounterTextView.setPadding(8, 0, 8, 0);
                }
                hintCounterTextView.setText(currentHint + "/" + totalHints);
                hintTextView.setText(TextViewMore.stripHtml(questionHint.getHintText()));
                hintTextView.setMaxLines(Integer.MAX_VALUE);
                hintTextView.setVerticalScrollBarEnabled(true);
                hintTextView.setMovementMethod(new ScrollingMovementMethod());
            } else {
                final String resourcePath = /*mBaseFolder + File.separator*/ questionHint.getHintResource().getDeviceURL();
                String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                    this.getLayoutInflater().inflate(R.layout.layout_response_hint_image_thumbnail, mBinding.layoutHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutHints.getChildAt(mBinding.layoutHints.getChildCount() - 1);
                    final TextView hintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    if (finalCounterHint >= 10) {
                        hintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                    } else {
                        hintCounterTextView.setPadding(8, 0, 8, 0);
                    }
                    hintCounterTextView.setText(currentHint + "/" + totalHints);
                    RelativeLayout layout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                    ImageView imageView = ((ImageView) layout.findViewById(R.id.imageview_hint_image));
                    imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                    imageView.setTag(resourcePath);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showResource(resourcePath);
                        }
                    });

                } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                    this.getLayoutInflater().inflate(R.layout.layout_response_hint_video_thumbnail, mBinding.layoutHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutHints.getChildAt(mBinding.layoutHints.getChildCount() - 1);
                    final TextView hintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    if (finalCounterHint >= 10) {
                        hintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                    } else {
                        hintCounterTextView.setPadding(8, 0, 8, 0);
                    }
                    hintCounterTextView.setText(currentHint + "/" + totalHints);
                    RelativeLayout layout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                    ImageView imageView = ((ImageView) layout.findViewById(R.id.imageview_hint_video));
                    imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                    imageView.setTag(resourcePath);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showResource(resourcePath);
                        }
                    });

                }
                mHintCounter++;

                int remainingHints = mHintsSize - mHintCounter;
                if (remainingHints == 1) {
                    mBinding.buttonQuestionHints.setText(String.valueOf(1) + " " + getString(R.string.label_hint));

                }
                if (remainingHints > 1) {
                    mBinding.buttonQuestionHints.setText(String.valueOf(remainingHints) + " " + getString(R.string.label_hints));

                }
                if (remainingHints == 0) {
                    mBinding.buttonQuestionHints.setText("0 Hint");
                }
            }

        }
    }

    private void showExplanation(Question question) {
        final int explanationType = question.getChoiceConfiguration().getQuestionExplanation().getExplanationType();
        if (explanationType == QuestionExplanation.TYPE_EXPLANATION_TEXT) {
            String explanationText = TextViewMore.stripHtml(question.getChoiceConfiguration().getQuestionExplanation().getExplanationText());
            mBinding.layoutExplanation.setVisibility(View.VISIBLE);
            mBinding.textViewExplanationText.setVisibility(View.VISIBLE);
            mBinding.textViewExplanationText.setText(explanationText);
        } else if (explanationType == QuestionExplanation.TYPE_EXPLANATION_RESOURCE) {
            mBinding.layoutExplanation.setVisibility(View.VISIBLE);
            mBinding.layoutExplanationResource.setVisibility(View.VISIBLE);
            String resourcePath = /*mBaseFolder + File.separator*/ question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getDeviceURL();
            if (!TextUtils.isEmpty(resourcePath)) {
                String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                mBinding.imageViewExplanationThumbnail.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                    mBinding.imageViewExplanationType.setImageResource(R.drawable.action_image_w);
                } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                    mBinding.imageViewExplanationType.setImageResource(R.drawable.action_video_w);
                }
            } else {
                mBinding.layoutExplanation.setVisibility(View.GONE);

            }
        } else {
            mBinding.layoutExplanation.setVisibility(View.GONE);

        }

    }

    private void doneClickAction() {

        if (getSubmittedAnswerFromView().size() > 0) {
            Attempt attempt = new Attempt();
            attempt.setSubmittedAnswer(getSubmittedAnswerFromView());
            mIsCorrectResponse = mQuestionResponseModelApp.checkCorrectness(mQuestion, attempt);
            if (mIsCorrectResponse) {
                mTotalCorrect++;
                resetTotalInCorrectCounter();
                attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                setResponseView(true);
                if (mTotalCorrect == QUESTION_TOTAL_CORRECT) {
                    incrementQuestionComplexity();
                    resetTotalCorrectCounter();
                }

            } else {
                mTotalInCorrect++;
                resetTotalCorrectCounter();
                attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                setResponseView(false);
                if (mTotalInCorrect == QUESTION_TOTAL_INCORRECT) {
                    decrementQuestionComplexity();
                    resetTotalInCorrectCounter();
                }
            }


            attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
            attempt.setHintsAvailed(mHintCounter);
            attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());
            QuestionResponse quizResponse = generateQuestionResponse(attempt, Boolean.toString(mIsCorrectResponse), mHintCounter);

        } else {
            mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
            SnackBarUtils.showColoredSnackBar(getBaseContext(), mBinding.scrollView, getString(R.string.enter_response), ContextCompat.getColor(getBaseContext(), R.color.colorRed));
        }


    }

    /**
     * for debug version only
     */
    private void setLevelView() {
        if (BuildConfig.DEBUG) {
            if (mComplexityLevel == 0) {
                getWindow().setStatusBarColor(ContextCompat.getColor(QuestionPlayerActivity.this, R.color.colorBlue));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorBlue)));
                mBinding.layoutTop.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlue));
            } else if (mComplexityLevel == 1) {
                getWindow().setStatusBarColor(ContextCompat.getColor(QuestionPlayerActivity.this, R.color.orange_color));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.orange_color)));
                mBinding.layoutTop.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.orange_color));
            } else if (mComplexityLevel == 2) {
                getWindow().setStatusBarColor(ContextCompat.getColor(QuestionPlayerActivity.this, R.color.colorBackgroundIncorrectResponse));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorBackgroundIncorrectResponse)));
                mBinding.layoutTop.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorBackgroundIncorrectResponse));
            }
        } else {
            getWindow().setStatusBarColor(ContextCompat.getColor(QuestionPlayerActivity.this, R.color.colorGreen));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorGreen)));
            mBinding.layoutTop.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorGreen));
        }

    }

    private void decrementQuestionComplexity() {
        if (mComplexityLevel > 0) {
            mComplexityLevel--;

        }
    }

    private void incrementQuestionComplexity() {
        if (mComplexityLevel < MAX_QUESTION_COMPLEXITY_LEVEL) {
            mComplexityLevel++;
            if (mComplexityLevel == 3) {
                mComplexityLevel = 2;
            }
        }
    }

    private void resetTotalCorrectCounter() {
        mTotalCorrect = 0;
    }

    private void resetTotalInCorrectCounter() {
        mTotalInCorrect = 0;
    }

    /**
     * configurable *
     * send true if response is correct
     * send false if response is incorrect
     *
     * @param response
     */
    private void setResponseView(boolean response) {
        if (response) {
            doneMenuItemVisibility(false);
            mBinding.layoutResponse.setVisibility(View.VISIBLE);
            mBinding.layoutResponse.setBackgroundResource(R.drawable.background_correct_question_response);
            mBinding.imageViewResponse.setImageResource(R.drawable.action_done_w);
            mBinding.textViewResponse.setText(getString(R.string.response_correct));
            AnimationUtils.slideInLeft(getBaseContext(), mBinding.layoutResponse);
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_CORRECT_ANSWER);

        } else {
            doneMenuItemVisibility(false);
            mBinding.layoutResponse.setVisibility(View.VISIBLE);
            mBinding.layoutResponse.setBackgroundResource(R.drawable.background_incorrect_question_response);
            mBinding.imageViewResponse.setImageResource(R.drawable.action_close_w);
            mBinding.textViewResponse.setText(getString(R.string.response_incorrect));
            AnimationUtils.slideInLeft(getBaseContext(), mBinding.layoutResponse);
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_INCORRECT_ANSWER);

        }

        mBinding.chronometerQuestionTimer.stop();
        Handler hold = new Handler();
        hold.postDelayed(new Runnable() {

            @Override
            public void run() {
                doneMenuItemVisibility(true);
                AnimationUtils.slideOutLeft(getBaseContext(), mBinding.layoutResponse);
                mBinding.layoutResponse.setVisibility(View.GONE);
                if (mQuestionCounter == (mQuestionsSize - 1)) {
                    mQuizResponse.setQuestionResponses(mQuestionResponses);
                    uploadQuizResponse(mQuizResponse);
                    doneMenuItemVisibility(false);
                } else {
                    mQuestionCounter++;
                    initializeQuestionUi();
                    mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                }
            }
        }, 2500);
    }

    public void showResource(String resourcePath) {

        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
        if (mimeType != null) {
            if (mimeType.contains("image")) {
                ArrayList<String> pathArrayList = new ArrayList<>();
                pathArrayList.add("file://" + resourcePath);
                FullScreenImage.setUpFullImageView(this, 0, false, true, FullScreenImage.getResourceArrayList(pathArrayList));

            } else if (mimeType.contains("video")) {

                Resource item = new Resource();
                item.setType("video");
                item.setUrlMain(resourcePath);
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
            }
        }


    }

    private ArrayList<String> getSubmittedAnswerFromView() {

        String questionType = mQuestion.getQuestionType();
        ArrayList<String> choiceResponses = new ArrayList<>();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            RadioGroup radioGroup = (RadioGroup) mBinding.layoutChoices.findViewById(R.id.radio_group_response);
            int count = radioGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                if (((RadioButton) radioGroup.getChildAt(i)).isChecked()) {
                    choiceResponses.add(String.valueOf(i));
                }
            }
        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {
            int count = mBinding.layoutChoices.getChildCount();

            for (int i = 0; i < count; i++) {
                if (((CheckBox) mBinding.layoutChoices.getChildAt(i)).isChecked()) {
                    choiceResponses.add(String.valueOf(i));
                }
            }


        }

        return choiceResponses;
    }

    private QuestionResponse generateQuestionResponse(Attempt attempt, String response, int counterHint) {

        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setAlias(GeneralUtils.generateAlias("QuestionResponse", mAppUserModel.getObjectId(), System.currentTimeMillis() + ""));
        questionResponse.setOrder(mQuestionCounter);
        questionResponse.setQid(mQuestion.getUidQuestion());
        questionResponse.setHintUsed(counterHint);
        if (!TextUtils.isEmpty(response)) {
            questionResponse.setResponse(response);
        }
        if (questionResponse.getAttempts().size() <= 0) {
            questionResponse.addAttempt(attempt);
        } else {
            questionResponse.getAttempts().set(0, attempt);
        }
        mQuestionResponses.add(questionResponse);

        return questionResponse;
    }

    private void uploadQuizResponse(final QuizResponse quizResponse) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            final ProgressDialog progressDialog = ProgressDialog.show(QuestionPlayerActivity.this, "", getString(R.string.message_uploading_response));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                @Override
                public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                    Call<ResponseBody> call = mNetworkModel.uploadQuizResponse(quizResponse);
                    Response<ResponseBody> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        Log.e("QuizResponse1--", "Successful");
                        e.onNext(response.body());
                    } else if ((response.code() == 400)) {
                        ResponseBody responseBody = response.errorBody();
                        Log.e("QuizResponse--400", "Failed");
                    } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getBaseContext())) {
                        Response<ResponseBody> response2 = call.clone().execute();
                        if (response2 != null && response.isSuccessful()) {
                            Log.e("QuizResponse2--", "Successful");
                            e.onNext(response2.body());
                        } else if ((response2.code() == 401)) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getBaseContext()));
                            throw new Exception(getString(R.string.messageUploadFailed));
                        } else {
                            Log.e("QuizResponse2--", "Failed");
                            e.onComplete();
                            throw new Exception(getString(R.string.messageUploadFailed));
                        }
                    } else {
                        Log.e("QuizResponse1--", "Failed");
                        e.onComplete();
                        throw new Exception(getString(R.string.messageUploadFailed));
                    }
                    e.onComplete();

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Exception {
                            progressDialog.dismiss();
                            SyncServiceHelper.startDownloadLearningMap(getBaseContext());
                            mRxBus.send(new QuizCompletedEvent(quizResponse.getQuizId()));
                            showAlertDialog(getString(R.string.message_mastery_completed));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            progressDialog.dismiss();
                            mBinding.scrollView.setFocusable(false);
                            mBinding.scrollView.setClickable(false);
                            mBinding.scrollView.setFocusableInTouchMode(false);
                            final Snackbar snackBar = Snackbar.make(mBinding.scrollView, getString(R.string.messageUploadFailed), Snackbar.LENGTH_INDEFINITE);

                            snackBar.setAction(getString(R.string.labelRetry), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                                        snackBar.dismiss();
                                        uploadQuizResponse(mQuizResponse);
                                    } else {
                                        showSnackBar(getString(R.string.error_message_no_internet));
                                    }

                                }
                            });
                            snackBar.show();
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            progressDialog.dismiss();
                        }
                    });
        } else {
            showSnackBar(getString(R.string.error_message_no_internet));
        }
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(message)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                }).show();
    }

    private void showBackAlertDialog() {

        new android.app.AlertDialog.Builder(QuestionPlayerActivity.this)
                .setMessage(getString(R.string.close_alert_title))
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void showSnackBar(String message) {
        SnackBarUtils.showColoredSnackBar(this, mBinding.scrollView, message, ContextCompat.getColor(getBaseContext(), R.color.accent_colorRed));
    }

}
