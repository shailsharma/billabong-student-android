package in.securelearning.lil.android.quizpreview.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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

import com.robinhood.ticker.TickerUtils;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;

import java.net.URLConnection;
import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import in.securelearning.lil.android.app.Widgets.CheckBoxCustom;
import in.securelearning.lil.android.app.Widgets.RadioButtonCustom;
import in.securelearning.lil.android.app.databinding.LayoutPracticePlayerBinding;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.events.QuizCompletedEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.quizpreview.InjectorQuizPreview;
import in.securelearning.lil.android.quizpreview.model.QuestionResponseModelApp;
import in.securelearning.lil.android.quizpreview.model.QuizPreviewModel;
import in.securelearning.lil.android.syncadapter.dataobject.MasteryRequestObject;
import in.securelearning.lil.android.syncadapter.dataobject.MasteryRequestObjectFilter;
import in.securelearning.lil.android.syncadapter.dataobject.QuizResponse;
import in.securelearning.lil.android.syncadapter.dataobject.SkillMasteryQuestionData;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.FlyObjectAnimationUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
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

/**
 * Created by Chaitendra on 26-Oct-17.
 */

public class PracticeTopicActivity extends AppCompatActivity {

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
    LayoutPracticePlayerBinding mBinding;

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DIFFICULTY_LEVEL = "difficultyLevel";
    private static final String SKILL_MAP = "skillMap";
    private static final int QUESTION_SIZE_LOW = 5;
    private static final int QUESTION_SIZE_MEDIUM = 5;
    private static final int QUESTION_SIZE_HARD = 5;
    private static final int MAX_QUESTION_COMPLEXITY_LEVEL = 2;
    private static final int MIN_QUESTION_COMPLEXITY_LEVEL = 0;
    private static final int QUESTION_TOTAL_CORRECT = 2;
    private static final int QUESTION_TOTAL_INCORRECT = 2;

    private int[] mQuestionsCounterArray;

    private String mTitle;
    private int mQuestionCounter = 0;
    private int mSkillCounter = 0;
    private int mComplexityLevelQuestionLimit = 0;
    private int mTotalQuestions = 0;
    private int mHintsSize = 0;
    private int mTotalCorrect = 0;
    private int mTotalInCorrect = 0;
    private int mComplexityLevel = MIN_QUESTION_COMPLEXITY_LEVEL;
    //    private int mLowQuestionsCounter = -1;
//    private int mMediumQuestionsCounter = -1;
//    private int mHighQuestionsCounter = -1;
    private ArrayList<QuestionResponse> mQuestionResponses = new ArrayList<>();
    private ArrayList<SkillMasteryQuestionData> mSkillMasteryQuestionData = new ArrayList<>();
    private int mHintCounter = 0;
    private Question mQuestion;
    private QuizResponse mQuizResponse;
    private int mTotalPoints = 100;

    @Override
    public void onBackPressed() {
        showBackAlertDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorQuizPreview.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_practice_player);
        handleIntent();
        initializeClickListeners();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context, ArrayList<String> skillIds, String skillName, String questionLevel, HomeModel.SkillMap skillMap) {
        Intent intent = new Intent(context, PracticeTopicActivity.class);
        intent.putStringArrayListExtra(ID, skillIds);
        intent.putExtra(TITLE, skillName);
        intent.putExtra(DIFFICULTY_LEVEL, questionLevel);
        intent.putExtra(SKILL_MAP, skillMap);
        return intent;
    }

    public static Intent getStartIntentForSkills(Context context, ArrayList<Skill> skills, String skillName, String questionLevel, HomeModel.SkillMap skillMap) {
        ArrayList<String> skillIds = new ArrayList<>();
        for (Skill skill :
                skills) {
            skillIds.add(skill.getId());
        }
        Intent intent = new Intent(context, PracticeTopicActivity.class);
        intent.putStringArrayListExtra(ID, skillIds);
        intent.putExtra(TITLE, skillName);
        intent.putExtra(DIFFICULTY_LEVEL, questionLevel);
        intent.putExtra(SKILL_MAP, skillMap);
        return intent;
    }

    private void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            initializeQuestionCounters();
            ArrayList<String> id = getIntent().getStringArrayListExtra(ID);
            mTitle = getIntent().getStringExtra(TITLE);
            String difficultyLevel = getIntent().getStringExtra(DIFFICULTY_LEVEL);

            HomeModel.SkillMap skillMap = (HomeModel.SkillMap) getIntent().getSerializableExtra(SKILL_MAP);
            mQuizResponse = new QuizResponse();
            if (mSkillMasteryQuestionData != null) {
                MetaInformation metaInformation = new MetaInformation();
                metaInformation.setLanguage(skillMap.getLanguage());
                metaInformation.setBoard(skillMap.getBoard());
                metaInformation.setGrade(skillMap.getGrade());
                metaInformation.setTopic(skillMap.getTopic());
                metaInformation.setSubject(skillMap.getSubject());
                metaInformation.setLearningLevel(skillMap.getLearningLevel());
                mQuizResponse.setMetaInformation(metaInformation);
            }
            setUpToolbar();
            if (!TextUtils.isEmpty(difficultyLevel)) {
                setQuestionSize(difficultyLevel);
            }

            fetchQuestions(id);
        } else {
            finish();
        }
    }

    private void initializeQuestionCounters() {
        mQuestionsCounterArray = new int[MAX_QUESTION_COMPLEXITY_LEVEL + 1];
        for (int i = 0; i < mQuestionsCounterArray.length; i++) {
            mQuestionsCounterArray[i] = -1;
        }
        mComplexityLevel = MIN_QUESTION_COMPLEXITY_LEVEL;
        resetTotalCorrectCounter();
        resetTotalInCorrectCounter();
    }

    private void setQuestionSize(String questionLevel) {
        if (questionLevel.equals(getString(R.string.label_low))) {
            mComplexityLevelQuestionLimit = QUESTION_SIZE_LOW;
            mComplexityLevel = 0;
        } else if (questionLevel.equals(getString(R.string.label_medium))) {
            mComplexityLevelQuestionLimit = QUESTION_SIZE_MEDIUM;
            mComplexityLevel = 1;
        } else if (questionLevel.equals(getString(R.string.label_high))) {
            mComplexityLevelQuestionLimit = QUESTION_SIZE_HARD;
            mComplexityLevel = 2;
        }
    }

    private void setUpToolbar() {

        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        mBinding.textViewToolbarTitle.setText(mTitle);
        mBinding.textViewPoint.setCharacterLists(TickerUtils.provideNumberList());
        mBinding.textViewPoint.setText(" " + mTotalPoints + " ");
        Typeface typeface = ResourcesCompat.getFont(getBaseContext(), R.font.digital);
        mBinding.textViewPoint.setTypeface(typeface);
    }


    private void initializeQuestionUi() {
        boolean shouldSkip = false;
        if (mSkillCounter < mSkillMasteryQuestionData.size()) {
            if (mComplexityLevel <= MAX_QUESTION_COMPLEXITY_LEVEL && mComplexityLevel >= MIN_QUESTION_COMPLEXITY_LEVEL) {
                if (mQuestionsCounterArray[mComplexityLevel] + 1 >= mSkillMasteryQuestionData.get(mSkillCounter).getQuestions()[mComplexityLevel].size()) {
                    shouldSkip = true;

                    int i;
                    i = mComplexityLevel + 1;

                    while (i <= MAX_QUESTION_COMPLEXITY_LEVEL) {
                        if (mQuestionsCounterArray[i] + 1 < mSkillMasteryQuestionData.get(mSkillCounter).getQuestions()[i].size()) {
                            mComplexityLevel = i;
                            shouldSkip = false;
                            break;
                        }
                        i++;
                    }


                }
                if (!shouldSkip && mComplexityLevel <= MAX_QUESTION_COMPLEXITY_LEVEL) {

                    mQuestion = mSkillMasteryQuestionData.get(mSkillCounter).getQuestions()[mComplexityLevel].get(++mQuestionsCounterArray[mComplexityLevel]);

                    mHintsSize = mQuestion.getQuestionHints().size();
                    mBinding.buttonDone.setText(getString(R.string.submit));
                    mBinding.layoutExplanation.setVisibility(View.GONE);
                    setUpdateValues(mQuestionCounter, mComplexityLevelQuestionLimit);
                    setQuestionText(mQuestion);
                    setHintView(mQuestion);
                    setChoicesView(mQuestion);
                    setTimerView(mQuestion);
                    setQuestionResource(mQuestion);
                    setLevelView();
                    return;
                }
            }

            // TODO: 02-05-2018 reset complexity and counters
            initializeQuestionCounters();
            mSkillCounter++;
            initializeQuestionUi();


        } else {

            uploadQuizResponse(mQuizResponse);
        }


    }

    private void initializeClickListeners() {
        mBinding.buttonQuestionHints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showNextHint(mHintCounter);
                mBinding.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);

                    }
                });
            }
        });

        mBinding.buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.buttonDone.getText().toString().equals(getString(R.string.submit))) {
                    submitClickAction();
                } else if (mBinding.buttonDone.getText().toString().equals(getString(R.string.next))) {
                    nextClickAction();
                } else {
                    doneClickAction();
                }

            }
        });
    }

    @SuppressLint("CheckResult")
    private void fetchQuestions(final ArrayList<String> ids) {
        if (GeneralUtils.isNetworkAvailable(this)) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.message_fetching_question), false);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onBackPressed();
                }
            });

            Observable.create(new ObservableOnSubscribe<ArrayList<SkillMasteryQuestionData>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<SkillMasteryQuestionData>> subscriber) throws Exception {
                    MasteryRequestObject masteryRequestObject = new MasteryRequestObject();
                    MasteryRequestObjectFilter masteryRequestObjectFilter = new MasteryRequestObjectFilter();
                    masteryRequestObjectFilter.setSkillIdList(ids);
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(getString(R.string.label_low));
                    arrayList.add(getString(R.string.label_medium));
                    arrayList.add(getString(R.string.label_high));
                    masteryRequestObjectFilter.setComplexityLevels(arrayList);
                    masteryRequestObject.setMasteryRequestObjectFilter(masteryRequestObjectFilter);
                    masteryRequestObject.setLimit(mComplexityLevelQuestionLimit);
                    masteryRequestObject.setSkip(0);

                    final Call<ArrayList<SkillMasteryQuestionData>> questionCall = mNetworkModel.fetchBySkillListAndComplexityLevel(masteryRequestObject);
                    Response<ArrayList<SkillMasteryQuestionData>> response = questionCall.execute();

                    if (response != null && response.isSuccessful()) {
                        mSkillMasteryQuestionData = response.body();
                        Log.e("QuestionFetch1--", "Successful");

                        subscriber.onNext(mSkillMasteryQuestionData);


                    } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getBaseContext())) {
                        Response<ArrayList<SkillMasteryQuestionData>> response2 = questionCall.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            mSkillMasteryQuestionData = response2.body();
                            Log.e("QuestionsFetch2--", "Successful");
                            subscriber.onNext(mSkillMasteryQuestionData);
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

                    subscriber.onComplete();

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<SkillMasteryQuestionData>>() {
                        @Override
                        public void accept(ArrayList<SkillMasteryQuestionData> questionDataArrayList) throws Exception {
                            progressDialog.dismiss();
                            mSkillMasteryQuestionData = questionDataArrayList;

                            int totalQuestions = 0;
                            for (SkillMasteryQuestionData skillMasteryQuestionData : questionDataArrayList) {
                                if (skillMasteryQuestionData != null && skillMasteryQuestionData.getLowLevelQuestion() != null
                                        && skillMasteryQuestionData.getMediumLevelQuestion() != null
                                        && skillMasteryQuestionData.getHighLevelQuestion() != null) {

                                    int questionTotalCount = skillMasteryQuestionData.getLowLevelQuestion().getResults().size() +
                                            skillMasteryQuestionData.getMediumLevelQuestion().getResults().size() +
                                            skillMasteryQuestionData.getHighLevelQuestion().getResults().size();

                                    if (questionTotalCount < mComplexityLevelQuestionLimit) {
                                        skillMasteryQuestionData.setQuestionsSize(questionTotalCount);
                                    } else {
                                        skillMasteryQuestionData.setQuestionsSize(mComplexityLevelQuestionLimit);
                                    }
                                    totalQuestions += skillMasteryQuestionData.getQuestionsSize();
                                    skillMasteryQuestionData.updateQuestions();


                                }
                            }
                            if (totalQuestions <= 0) {
                                //  showAlertDialog(getString(R.string.error_no_question_found));
                                mBinding.buttonDone.setVisibility(View.VISIBLE);
                            } else {
                                mTotalQuestions = totalQuestions;
                                initializeQuestionUi();
                                mBinding.layoutTop.setVisibility(View.VISIBLE);
                                mBinding.buttonDone.setVisibility(View.VISIBLE);
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
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
        mBinding.layoutChoices.setVisibility(View.VISIBLE);
        AnimationUtils.fadeInFast(getBaseContext(), mBinding.scrollView);
        mBinding.textViewQuestionCounter.setText("Question " + String.valueOf(questionCounter + 1));
//        mBinding.textViewQuestionsSize.setText(" / " + String.valueOf(questionsSize));
    }

    private void setQuestionText(Question question) {
        mBinding.textViewQuestion.setText(TextViewMore.stripHtml(question.getQuestionText()));
        HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(mBinding.textViewQuestion);
        htmlHttpImageGetter.enableCompressImage(true, 80);
        // String newText = question.getQuestionText().replace("#000000", "#ffffff");
        mBinding.textViewQuestion.setText(Html.fromHtml(TextViewMore.stripHtmlForQuiz(question.getQuestionText()).trim(), htmlHttpImageGetter, new TextViewMore.UlTagHandler()));
    }

    private void setQuestionResource(Question question) {
        String resourcePathImage = question.fetchQuestionImage().getUrlMain();
        String resourcePathVideo = question.fetchQuestionVideo().getUrlMain();
        if (!TextUtils.isEmpty(resourcePathImage)) {
            mBinding.layoutQuestionResource.setVisibility(View.VISIBLE);
            Picasso.with(getBaseContext()).load(resourcePathImage).into(mBinding.imageViewResourceThumbnail);
            // mBinding.imageViewResourceThumbnail.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePathImage));
            mBinding.imageViewResourceThumbnail.setTag(question.fetchQuestionImage().getUrlMain());
            mBinding.imageViewResourceType.setImageResource(R.drawable.action_image_w);
        } else if (!TextUtils.isEmpty(resourcePathVideo)) {
            mBinding.layoutQuestionResource.setVisibility(View.VISIBLE);
            Picasso.with(getBaseContext()).load(resourcePathVideo).into(mBinding.imageViewResourceThumbnail);
            // mBinding.imageViewResourceThumbnail.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePathVideo));
            mBinding.imageViewResourceThumbnail.setTag(question.fetchQuestionImage().getUrlMain());
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
        ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();
        mBinding.layoutChoices.removeAllViews();

        LayoutInflater layoutInflater = this.getLayoutInflater();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            layoutInflater.inflate(R.layout.layout_response_mcq_single_correct, mBinding.layoutChoices);
            RadioGroup radioGroup = (RadioGroup) mBinding.layoutChoices.findViewById(R.id.radio_group_response);
            mBinding.textViewQuestionType.setText(getString(R.string.single_correct));
            for (final QuestionChoice questionChoice : questionChoices) {
                String thumbUrl = questionChoice.getChoiceResource().getUrlMain();
                final String mainUrl = questionChoice.getChoiceResource().getUrlMain();
                String choiceText = questionChoice.getChoiceText();
                layoutInflater.inflate(R.layout.layout_practice_response_item_mcq_single_correct, radioGroup);
                RadioButtonCustom choice = (RadioButtonCustom) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
                choice.setClickable(true);
                choice.setAlpha(1f);
                choice.setTag(questionChoice.isChoiceCorrect());
                if (!TextUtils.isEmpty(thumbUrl) && !TextUtils.isEmpty(mainUrl)) {
                    Picasso.with(getBaseContext()).load(thumbUrl).into(choice);
                    choice.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            showResource(mainUrl);
                            return false;
                        }
                    });
                }

                if (!TextUtils.isEmpty(choiceText)) {
                    choice.setText(TextViewMore.stripHtml(questionChoice.getChoiceText()));
                }
            }


        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {
            mBinding.textViewQuestionType.setText(getString(R.string.multiple_correct));
            for (final QuestionChoice questionChoice : questionChoices) {
                String thumbUrl = questionChoice.getChoiceResource().getUrlMain();
                String mainUrl = questionChoice.getChoiceResource().getUrlMain();
                String choiceText = questionChoice.getChoiceText();

                layoutInflater.inflate(R.layout.layout_practice_response_item_mcq_multiple_correct, mBinding.layoutChoices);
                CheckBoxCustom choice = (CheckBoxCustom) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                choice.setClickable(true);
                choice.setAlpha(1f);
                choice.setTag(questionChoice.isChoiceCorrect());
                if (!TextUtils.isEmpty(thumbUrl) && !TextUtils.isEmpty(mainUrl)) {
                    Picasso.with(getBaseContext()).load(thumbUrl).into(choice);
                    choice.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            showResource(questionChoice.getChoiceResource().getUrlMain());
                            return false;
                        }
                    });
                }

                if (!TextUtils.isEmpty(choiceText)) {
                    choice.setText(TextViewMore.stripHtml(questionChoice.getChoiceText()));
                }
            }

        }
    }

    private void setHintView(Question question) {
        mBinding.buttonQuestionHints.setVisibility(View.VISIBLE);
        mBinding.textQuestionHints.setVisibility(View.VISIBLE);
        mBinding.textQuestionHints.setText("0/" + String.valueOf(question.getQuestionHints().size()));

        if (mHintsSize == 0) {
            mBinding.buttonQuestionHints.setVisibility(View.GONE);
            mBinding.textQuestionHints.setVisibility(View.GONE);
        }

        mHintCounter = 0;
        mBinding.layoutHints.removeAllViews();
        mBinding.cardViewHints.setVisibility(View.GONE);
    }

    private void showNextHint(int hintCounter) {
        if (hintCounter == mHintsSize) {
            showSnackBar(getString(R.string.no_more_hints));
        } else {
            final QuestionHint questionHint = mQuestion.getQuestionHints().get(hintCounter);
            int finalCounterHint = hintCounter + 1;
            String currentHint = String.valueOf(finalCounterHint);
            String totalHints = String.valueOf(mHintsSize);

            if (mBinding.cardViewHints.getVisibility() == View.GONE) {
                AnimationUtils.pushUpEnter(getBaseContext(), mBinding.cardViewHints);
                mBinding.cardViewHints.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(questionHint.getHintText())) {
                this.getLayoutInflater().inflate(R.layout.layout_practice_response_hint_text, mBinding.layoutHints);
                LinearLayout layout = (LinearLayout) mBinding.layoutHints.getChildAt(mBinding.layoutHints.getChildCount() - 1);
                TextView hintTextView = (TextView) layout.findViewById(R.id.text_view_hint);

                final TextView hintCounterTextView = (TextView) layout.findViewById(R.id.text_view_hint_counter);
                hintCounterTextView.setText(currentHint + "/" + totalHints);
                hintTextView.setText(TextViewMore.stripHtml(questionHint.getHintText()));
                hintTextView.setMaxLines(Integer.MAX_VALUE);
                hintTextView.setVerticalScrollBarEnabled(true);
                hintTextView.setMovementMethod(new ScrollingMovementMethod());
            } else {
                final String resourcePath = questionHint.getHintResource().getUrlMain();
                String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                    this.getLayoutInflater().inflate(R.layout.layout_practice_response_hint_image_thumbnail, mBinding.layoutHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutHints.getChildAt(mBinding.layoutHints.getChildCount() - 1);
                    final TextView hintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    hintCounterTextView.setText(currentHint + "/" + totalHints);
                    RelativeLayout layout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                    ImageView imageView = ((ImageView) layout.findViewById(R.id.imageview_hint_image));
                    Picasso.with(getBaseContext()).load(resourcePath).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showResource(questionHint.getHintResource().getUrlMain());
                        }
                    });

                } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                    this.getLayoutInflater().inflate(R.layout.layout_practice_response_hint_video_thumbnail, mBinding.layoutHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutHints.getChildAt(mBinding.layoutHints.getChildCount() - 1);
                    final TextView hintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    hintCounterTextView.setText(currentHint + "/" + totalHints);
                    RelativeLayout layout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                    ImageView imageView = ((ImageView) layout.findViewById(R.id.imageview_hint_video));
                    Picasso.with(getBaseContext()).load(resourcePath).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showResource(questionHint.getHintResource().getUrlMain());
                        }
                    });

                }


            }
            mHintCounter++;
            mBinding.textQuestionHints.setText(String.valueOf(mHintCounter) + "/" + mHintsSize);
        }
    }

    private void showExplanation(final Question question) {
        if ((question.getChoiceConfiguration().getQuestionExplanation() != null && !TextUtils.isEmpty(question.getChoiceConfiguration().getQuestionExplanation().getExplanationText())) || !TextUtils.isEmpty(question.getExplanation())) {
            String explanationText = TextViewMore.stripHtml(question.getChoiceConfiguration().getQuestionExplanation().getExplanationText());
            if (TextUtils.isEmpty(explanationText)) {
                explanationText = TextViewMore.stripHtml(question.getExplanation());
            }

            if (!TextUtils.isEmpty(explanationText)) {
                mBinding.layoutExplanation.setVisibility(View.VISIBLE);
                mBinding.textViewExplanationText.setVisibility(View.VISIBLE);
                AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutExplanation);
                mBinding.textViewExplanationText.setText(explanationText);
            } else {
                mBinding.layoutExplanation.setVisibility(View.GONE);
            }

        } else {
            String resourcePath = question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getThumb();
            if (TextUtils.isEmpty(resourcePath)) {
                resourcePath = question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getUrlMain();
            }
            if (!TextUtils.isEmpty(resourcePath)) {
                mBinding.layoutExplanation.setVisibility(View.VISIBLE);
                mBinding.layoutExplanationResource.setVisibility(View.VISIBLE);
                AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutExplanation);
                String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                Picasso.with(getBaseContext()).load(resourcePath).into(mBinding.imageViewExplanationThumbnail);
                if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                    mBinding.imageViewExplanationType.setImageResource(R.drawable.action_image_w);
                } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                    mBinding.imageViewExplanationType.setImageResource(R.drawable.action_video_w);
                }

                mBinding.imageViewExplanationThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showResource(question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getUrlMain());
                    }
                });

            } else {
                mBinding.layoutExplanation.setVisibility(View.GONE);

            }
        }
    }

    private void submitClickAction() {

        if (getSubmittedAnswerFromView(false).size() > 0) {
            Attempt attempt = new Attempt();
            attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true));
            boolean isCorrectResponse = mQuestionResponseModelApp.checkCorrectness(mQuestion, attempt);
            if (isCorrectResponse) {
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
            QuestionResponse quizResponse = generateQuestionResponse(attempt, Boolean.toString(isCorrectResponse), mHintCounter);

        } else {
            mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
            showSnackBar(getString(R.string.enter_response));
        }


    }

    private void nextClickAction() {
        mQuestionCounter++;
        initializeQuestionUi();
        mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
    }

    private void doneClickAction() {
        if (mBinding.layoutResponse.getVisibility() == View.VISIBLE) {
            AnimationUtils.slideOutLeft(getBaseContext(), mBinding.layoutResponse);
            mBinding.layoutResponse.setVisibility(View.GONE);
        }
        mQuizResponse.setQuestionResponses(mQuestionResponses);
        uploadQuizResponse(mQuizResponse);
    }


    /**
     * for debug version only
     */
    private void setLevelView() {
//        if (BuildConfig.DEBUG) {
//            if (mComplexityLevel == 0) {
//                getWindow().setStatusBarColor(ContextCompat.getColor(PracticeTopicActivity.this, R.color.colorBlue));
//                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorBlue)));
//                mBinding.layoutTop.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlue));
//            } else if (mComplexityLevel == 1) {
//                getWindow().setStatusBarColor(ContextCompat.getColor(PracticeTopicActivity.this, R.color.orange_color));
//                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.orange_color)));
//                mBinding.layoutTop.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.orange_color));
//            } else if (mComplexityLevel == 2) {
//                getWindow().setStatusBarColor(ContextCompat.getColor(PracticeTopicActivity.this, R.color.colorBackgroundIncorrectResponse));
//                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorBackgroundIncorrectResponse)));
//                mBinding.layoutTop.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorBackgroundIncorrectResponse));
//            }
//        } else {
//            getWindow().setStatusBarColor(ContextCompat.getColor(PracticeTopicActivity.this, R.color.colorGreen));
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorGreen)));
//            mBinding.layoutTop.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorGreen));
//        }

    }

    private void decrementQuestionComplexity() {
        mComplexityLevel--;
    }

    private void incrementQuestionComplexity() {
        mComplexityLevel++;
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
            mBinding.layoutResponse.setVisibility(View.VISIBLE);
            mBinding.layoutResponse.setBackgroundResource(R.drawable.background_correct_question_response);
            mBinding.imageViewResponse.setImageResource(R.drawable.action_done_w);
            mBinding.textViewResponse.setText(getString(R.string.response_correct));
            AnimationUtils.slideInLeft(getBaseContext(), mBinding.layoutResponse);
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_CORRECT_ANSWER);
            addPoints();

        } else {
            mBinding.layoutResponse.setVisibility(View.VISIBLE);
            mBinding.layoutResponse.setBackgroundResource(R.drawable.background_incorrect_question_response);
            mBinding.imageViewResponse.setImageResource(R.drawable.action_close_w);
            mBinding.textViewResponse.setText(getString(R.string.response_incorrect));
            AnimationUtils.slideInLeft(getBaseContext(), mBinding.layoutResponse);
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_INCORRECT_ANSWER);

        }

        mBinding.chronometerQuestionTimer.stop();
        showExplanation(mQuestion);
        mBinding.cardViewHints.setVisibility(View.GONE);
        mBinding.buttonQuestionHints.setVisibility(View.GONE);
        mBinding.textQuestionHints.setVisibility(View.GONE);
        if (mQuestionCounter == (mTotalQuestions - 1)) {
            mBinding.buttonDone.setText(getString(R.string.done));
        } else {
            mBinding.buttonDone.setText(getString(R.string.next));
        }
        //mBinding.scrollView.scrollTo(0,mBinding.scrollView.getBottom());
        mBinding.scrollView.post(new Runnable() {
            @Override
            public void run() {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);

            }
        });

        Handler hold = new Handler();
        hold.postDelayed(new Runnable() {

            @Override
            public void run() {
                //doneMenuItemVisibility(false);
                AnimationUtils.slideOutLeft(getBaseContext(), mBinding.layoutResponse);
                mBinding.layoutResponse.setVisibility(View.GONE);
//                if (mQuestionCounter == (mTotalQuestions - 1)) {
//                    mQuizResponse.setQuestionResponses(mQuestionResponses);
//                    uploadQuizResponse(mQuizResponse);
//                    doneMenuItemVisibility(false);
//                } else {
//                    mQuestionCounter++;
//                    initializeQuestionUi();
//                    mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
//                }
            }
        }, 1500);
    }

    private void addPoints() {
        mBinding.textViewAddedPoint.setVisibility(View.VISIBLE);

        new FlyObjectAnimationUtil().setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTotalPoints = mTotalPoints + 20;
                mBinding.textViewPoint.setText(" " + mTotalPoints + " ");
                AnimationUtils.zoomIn(getBaseContext(), mBinding.imageViewCoin);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation(mBinding.textViewAddedPoint, mBinding.layoutPoint);
    }

    public void showResource(String resourcePath) {

        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
        if (mimeType != null) {
            if (mimeType.contains("image")) {
                startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), resourcePath, true));

                UserProfileActivity.showFullImage(resourcePath, PracticeTopicActivity.this);

            } else if (mimeType.contains("video")) {
                Resource item = new Resource();
                item.setType("video");
                item.setUrlMain(resourcePath);
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
            }
        }


    }

    private ArrayList<String> getSubmittedAnswerFromView(boolean isSubmission) {

        String questionType = mQuestion.getQuestionType();
        ArrayList<String> choiceResponses = new ArrayList<>();
        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            RadioGroup radioGroup = (RadioGroup) mBinding.layoutChoices.findViewById(R.id.radio_group_response);
            int count = radioGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                RadioButton view = ((RadioButton) radioGroup.getChildAt(i));
                if (view.isChecked()) {
                    choiceResponses.add(String.valueOf(i));
                }
                if (isSubmission) {
                    if (!(boolean) view.getTag()) {
                        view.setVisibility(View.GONE);
                    } else {
                        view.setChecked(true);
                    }

                    view.setEnabled(false);
                }
            }
        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {
            int count = mBinding.layoutChoices.getChildCount();

            for (int i = 0; i < count; i++) {
                CheckBox view = ((CheckBox) mBinding.layoutChoices.getChildAt(i));
                if (view.isChecked()) {
                    choiceResponses.add(String.valueOf(i));
                }

                if (isSubmission) {
                    if (!(boolean) view.getTag()) {
                        view.setVisibility(View.GONE);
                    } else {
                        view.setChecked(true);
                    }

                    view.setEnabled(false);
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

    @SuppressLint("CheckResult")
    private void uploadQuizResponse(final QuizResponse quizResponse) {
        if (quizResponse.getQuestionResponses() != null && quizResponse.getQuestionResponses().size() > 0) {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                final ProgressDialog progressDialog = ProgressDialog.show(PracticeTopicActivity.this, "", getString(R.string.message_uploading_response));
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
                                showAlertDialog(getString(R.string.messageMSEndTopic));
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
        } else {
            finish();
        }
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(message)
                .setNeutralButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                }).show();
    }

    private void showBackAlertDialog() {

        new AlertDialog.Builder(PracticeTopicActivity.this)
                .setMessage(getString(R.string.close_alert_title))
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mQuizResponse.setQuestionResponses(mQuestionResponses);
                        uploadQuizResponse(mQuizResponse);
                    }
                }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void showSnackBar(String message) {
        SnackBarUtils.showColoredSnackBar(this, mBinding.scrollView, message, 0x44000000);
    }


}
