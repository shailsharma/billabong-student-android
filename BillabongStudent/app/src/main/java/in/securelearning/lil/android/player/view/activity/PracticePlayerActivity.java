package in.securelearning.lil.android.player.view.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.robinhood.ticker.TickerUtils;
import com.squareup.picasso.Picasso;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.Widgets.CheckBoxCustom;
import in.securelearning.lil.android.app.Widgets.RadioButtonCustom;
import in.securelearning.lil.android.app.databinding.LayoutMatchTheFollowingItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutPracticeEndBinding;
import in.securelearning.lil.android.app.databinding.LayoutPracticePlayerBinding;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.QuestionPart;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.views.activity.MascotActivity;
import in.securelearning.lil.android.home.views.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideo;
import in.securelearning.lil.android.player.dataobject.MatchingContent;
import in.securelearning.lil.android.player.dataobject.PracticeFilter;
import in.securelearning.lil.android.player.dataobject.PracticeParent;
import in.securelearning.lil.android.player.dataobject.PracticeQuestionResponse;
import in.securelearning.lil.android.player.dataobject.PracticeResponse;
import in.securelearning.lil.android.player.dataobject.QuestionTypeMatchTheFollowing;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.player.view.adapter.DropdownAdapter;
import in.securelearning.lil.android.player.view.adapter.ExplanationVideoAdapter;
import in.securelearning.lil.android.player.view.adapter.QuestionResourceAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.BonusConfigurationResponse;
import in.securelearning.lil.android.syncadapter.dataobject.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.FlyObjectAnimationUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class PracticePlayerActivity extends AppCompatActivity {

    @Inject
    PlayerModel mPlayerModel;

    LayoutPracticePlayerBinding mBinding;

    private static final String TITLE = "title";
    private static final String SUBJECT_ID = "subjectId";
    private static final String TOPIC_ID = "topicId";

    private static final int QUESTION_TOTAL_CORRECT = 4;
    private static final int QUESTION_TOTAL_INCORRECT = 4;
    private static final int QUESTION_POINT = 20;
    private static final int LIMIT = 10;

    private int mStreakBonus;// Configurable value, value should come from server.
    private int mTotalCorrect = 0;
    private int mTotalInCorrect = 0;
    private ArrayList<QuestionResponse> mQuestionResponses = new ArrayList<>();
    private int mHintCounter = 0;
    private ArrayList<Question> mQuestionList = new ArrayList<>();
    private String mSubjectId, mTopicId;
    private int mQuestionCounter, mUiQuestionCounter = 0;
    private int mSkip = 0;
    private int mTotalScore = 0, mCorrectAnswerPoint, mStreakBonusPoint;
    private boolean mAllowRepeat;//To fetch repeated question which already practice by the user.
    private boolean mIsFromBack;
    private String mQuestionComplexityLevel = ConstantUtil.BLANK;
    private String mQuestionSkillId = ConstantUtil.BLANK;
    private int mScrollTop, mScrollBottom, mScrollDistance;
    private int mMTFMatchCount;
    private MatchTheFollowingChoiceAdapter mMatchTheFollowingChoiceAdapter;
    private String[] mCorrectMessages;
    private String[] mInCorrectMessages;
    private DropdownAdapter mDropdownAdapter;
    private ProgressDialog mProgressDialog;


    @Override

    public void onBackPressed() {
        showBackAlertDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_practice_player);
        handleIntent();
        setUpResponseMessages();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

    }

    private void setUpResponseMessages() {
        mCorrectMessages = getResources().getStringArray(R.array.correct_response_messages);
        mInCorrectMessages = getResources().getStringArray(R.array.incorrect_response_messages);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context, String topicName, String subjectId, String topicId) {
        Intent intent = new Intent(context, PracticePlayerActivity.class);
        intent.putExtra(TITLE, topicName);
        intent.putExtra(SUBJECT_ID, subjectId);
        intent.putExtra(TOPIC_ID, topicId);
        return intent;
    }

    /*Handle intent and get bundle data from intent*/
    private void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = getIntent().getStringExtra(TITLE);
            mSubjectId = getIntent().getStringExtra(SUBJECT_ID);
            mTopicId = getIntent().getStringExtra(TOPIC_ID);

            setUpToolbar(title);
            fetchStreakBonusValue();

        } else {
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
            finish();
        }
    }

    /*Prepare practice post data to fetch question and send question responses to server*/
    private PracticeParent preparePracticePostData(int streak) {
        PracticeParent practiceParent = new PracticeParent();
        PracticeFilter practiceFilter = new PracticeFilter();
        practiceFilter.setSubjectId(mSubjectId);
        practiceFilter.setTopicIdId(mTopicId);
        practiceFilter.setRepeatAllowed(mAllowRepeat);
        practiceFilter.setSkip(mSkip);
        practiceFilter.setLimit(LIMIT);

        PracticeResponse practiceResponse = new PracticeResponse();
        practiceResponse.setComplexityLevel(mQuestionComplexityLevel);
        practiceResponse.setThreshold(streak);
        practiceResponse.setQuestionResponseList(mQuestionResponses);
        practiceResponse.setSkillId(mQuestionSkillId);
        practiceFilter.setPracticeResponse(practiceResponse);

        practiceParent.setPracticeFilter(practiceFilter);
        return practiceParent;
    }

    /*Setup custom toolbar*/
    private void setUpToolbar(String title) {
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBinding.textViewToolbarTitle.setText(title);
        mBinding.textViewPoint.setCharacterLists(TickerUtils.provideNumberList());
        setTotalScore(mTotalScore);
        Typeface typeface = ResourcesCompat.getFont(getBaseContext(), R.font.digital);
        mBinding.textViewPoint.setTypeface(typeface);

        mBinding.textViewStreak.setCharacterLists(TickerUtils.provideNumberList());
        mBinding.textViewStreak.setText(String.valueOf(0));

    }

    /*Set total points/score on the ui*/
    private void setTotalScore(int totalScore) {
        mBinding.textViewPoint.setText(ConstantUtil.BLANK_SPACE + totalScore + ConstantUtil.BLANK_SPACE);
    }

    /*Method to initialize question ui*/
    private void initializeQuestionUi() {
        if (!mQuestionList.isEmpty()) {
            mUiQuestionCounter++;
            Question question = mQuestionList.get(mQuestionCounter);
            setDefaultsForNewQuestion();
            setQuestionCounter(mUiQuestionCounter);
            setQuestionText(question);
            setHintView(question);
            setChoicesView(question);
            startTimer();
            setQuestionResource(question);
            fetchExplanationVideos(mPlayerModel.getSkillIdList(question.getSkills()));
            initializeClickListeners(question);
            mQuestionComplexityLevel = question.getComplexityLevel();
            mQuestionSkillId = getCurrentQuestionSkillId(question.getSkills());
            mQuestionCounter++;

        }

    }


    /*To fetch explanation videos*/
    @SuppressLint("CheckResult")
    private void fetchExplanationVideos(ArrayList<String> skillIdList) {
        mBinding.layoutExplanationKhanAcademyVideo.getRoot().setVisibility(View.VISIBLE);
        mBinding.layoutExplanationKhanAcademyVideo.progressBar.setVisibility(View.VISIBLE);
        mBinding.layoutExplanationKhanAcademyVideo.recyclerViewExplanationVideos.setVisibility(View.GONE);
        mPlayerModel.fetchExplanationVideos(skillIdList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<KhanAcademyVideo>>() {
                    @Override
                    public void accept(ArrayList<KhanAcademyVideo> khanAcademyVideos) throws Exception {
                        mBinding.layoutExplanationKhanAcademyVideo.progressBar.setVisibility(View.GONE);

                        if (khanAcademyVideos != null && !khanAcademyVideos.isEmpty()) {
                            mBinding.layoutExplanationKhanAcademyVideo.getRoot().setVisibility(View.VISIBLE);
                            mBinding.layoutExplanationKhanAcademyVideo.recyclerViewExplanationVideos.setVisibility(View.VISIBLE);
                            mBinding.layoutExplanationKhanAcademyVideo.recyclerViewExplanationVideos.setAdapter(new ExplanationVideoAdapter(getBaseContext(), khanAcademyVideos));
                        } else {
                            mBinding.layoutExplanationKhanAcademyVideo.getRoot().setVisibility(View.GONE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mBinding.layoutExplanationKhanAcademyVideo.getRoot().setVisibility(View.GONE);

                    }
                });
    }

    /*To handle clicks of url in questionText, choices, explanation and hints*/
    private void setMovementMethod() {
        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
                .setOnLinkClickListener(new BetterLinkMovementMethod.OnLinkClickListener() {
                    @Override
                    public boolean onClick(TextView textView, String url) {
                        if (url.contains("youtube")) {
                            mPlayerModel.startYoutubePlayer(getBaseContext(), url);
                            return true;
                        } else if (url.contains("vimeo")) {
                            mPlayerModel.startVimeoPlayer(getBaseContext(), url);
                            return true;
                        } else if (url.endsWith(".mp4") || url.endsWith(".MP4") ||
                                url.endsWith("wmv") || url.endsWith("WMV") ||
                                url.endsWith("flv") || url.endsWith("FLV")) {
                            mPlayerModel.startVideoPlayer(getBaseContext(), url);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
    }

    /*Set/Reset views of player when new question initialized*/
    private void setDefaultsForNewQuestion() {

        mBinding.buttonDone.setText(getString(R.string.submit));
        mBinding.buttonDone.setVisibility(View.VISIBLE);

        mBinding.layoutChoices.removeAllViews();

        mBinding.layoutMatchTheFollowingContainer.layoutMTF.setVisibility(View.GONE);

        mBinding.layoutExplanation.setVisibility(View.GONE);

        mBinding.layoutTimer.setVisibility(View.VISIBLE);
        mBinding.chronometerQuestionTimer.stop();
        mBinding.chronometerQuestionTimer.setBase(SystemClock.elapsedRealtime());

        mHintCounter = 0;
        mBinding.layoutHints.removeAllViews();
        mBinding.cardViewHints.setVisibility(View.GONE);

        mMTFMatchCount = 0;

        mBinding.scrollView.post(new Runnable() {
            @Override
            public void run() {
                int[] originalPos = new int[2];
                mBinding.scrollView.getLocationOnScreen(originalPos);
                mScrollTop = originalPos[1];
                mScrollBottom = mScrollTop + mBinding.scrollView.getMeasuredHeight();
            }
        });

        mBinding.scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mScrollDistance = scrollY;

            }


        });
    }

    /*Set/Reset views of player after question attempt.*/
    private void setViewAfterQuestionAttempt() {
        mBinding.chronometerQuestionTimer.stop();

        mBinding.buttonDone.setText(getString(R.string.next));

        mBinding.cardViewHints.setVisibility(View.GONE);
        mBinding.buttonQuestionHints.setVisibility(View.GONE);
        mBinding.textQuestionHints.setVisibility(View.GONE);
    }

    /*Handling clicks and listeners of ui*/
    private void initializeClickListeners(final Question question) {
        mBinding.buttonQuestionHints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showNextHint(mHintCounter, question);

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
                    submitClickAction(question);
                } else if (mBinding.buttonDone.getText().toString().equals(getString(R.string.next))) {
                    nextClickAction();
                } else {
                    doneClickAction();
                }
            }
        });

    }

    @SuppressLint("CheckResult")
    private void fetchStreakBonusValue() {
        if (GeneralUtils.isNetworkAvailable(this)) {

            mProgressDialog = ProgressDialog.show(this, ConstantUtil.BLANK, getString(R.string.messagePleaseWait), false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onBackPressed();
                }
            });

            mPlayerModel.fetchBonusConfiguration()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GlobalConfigurationParent>() {
                        @Override
                        public void accept(GlobalConfigurationParent globalConfigurationParent) {

                            BonusConfigurationResponse bonusConfigurationResponse = globalConfigurationParent.getBonus();
                            mStreakBonus = bonusConfigurationResponse.getStreakBonus();
                            if (mStreakBonus == 0) {
                                showAlertDialog(getString(R.string.messagePracticeConfigurationNotAvailable));
                            } else {
                                PracticeParent practiceParent = preparePracticePostData(ConstantUtil.INT_ZERO);
                                submitAndFetchQuestions(practiceParent);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            mProgressDialog.dismiss();
                            showAlertDialog(getString(R.string.messagePracticeConfigurationNotAvailable));

                        }
                    });

        } else {
            showInternetAlertDialog(null, false);
        }

    }

    /*Fetch list of question and submit questionResponses in the same api*/
    @SuppressLint("CheckResult")
    private void submitAndFetchQuestions(final PracticeParent practiceParent) {
        if (GeneralUtils.isNetworkAvailable(this)) {

            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(this, ConstantUtil.BLANK, getString(R.string.messagePleaseWait), false);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onBackPressed();
                    }
                });
            }


            mPlayerModel.fetchQuestions(practiceParent)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<PracticeQuestionResponse>() {
                        @Override
                        public void accept(PracticeQuestionResponse practiceQuestionResponse) {
                            mProgressDialog.dismiss();
                            if (mIsFromBack) {
                                showPracticeEndDialog();
                            } else {
                                if (practiceQuestionResponse != null) {

                                /*When isQuestionExist == true and question array length is greater then 0,
                                  then pass to the ui for practice.*/
                                    if (practiceQuestionResponse.isQuestionExist()
                                            && practiceQuestionResponse.getQuestionList() != null
                                            && !practiceQuestionResponse.getQuestionList().isEmpty()) {
                                        checkLevelUpgrade(practiceQuestionResponse.getQuestionList().get(0).getSkills().get(0), practiceQuestionResponse.getQuestionList().get(0).getComplexityLevel());
                                        setValueDefault();

                                        mQuestionList = practiceQuestionResponse.getQuestionList();
                                        mSkip += mQuestionList.size();
                                        initializeQuestionUi();
                                    }
                                /*When isQuestionExist == true and question array length is empty,
                                then ask user to repeat practice.*/
                                    else if (practiceQuestionResponse.isQuestionExist()
                                            && practiceQuestionResponse.getQuestionList() != null
                                            && practiceQuestionResponse.getQuestionList().isEmpty()) {
                                        if (mUiQuestionCounter > 1) {
                                            showPracticeEndDialog();
                                        } else {
                                            showRepeatAlertDialog();
                                        }
                                    }
                                /*When isQuestionExist == false and question array length is empty,
                                If questionCounter==0 means no question available for the topic.
                                If questionCounter>0, means question finished for the practice.*/
                                    else if (!practiceQuestionResponse.isQuestionExist()
                                            && practiceQuestionResponse.getQuestionList() != null
                                            && practiceQuestionResponse.getQuestionList().isEmpty()) {
                                        if (mUiQuestionCounter == 0) {
                                            showAlertDialog(getString(R.string.messageNoQuestionForTopic));
                                        } else {
                                            if (mUiQuestionCounter == QUESTION_TOTAL_CORRECT && practiceQuestionResponse.isUserThrowOut()) {
                                                showAlertDialog(getString(R.string.messagePracticeMotivation));
                                            } else {
                                                showPracticeEndDialog();
                                            }
                                        }


                                    } else {
                                        showAlertDialog(getString(R.string.error_something_went_wrong));

                                    }

                                } else {
                                    showAlertDialog(getString(R.string.error_something_went_wrong));

                                }
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            mProgressDialog.dismiss();
                            showAlertDialog(getString(R.string.error_something_went_wrong));


                        }
                    });

        } else {
            showInternetAlertDialog(practiceParent, true);
        }

    }

    private void checkLevelUpgrade(Skill skill, String complexityLevel) {
        if (mTotalCorrect == QUESTION_TOTAL_CORRECT) {
            if (mQuestionSkillId.equals(skill.getId())) {
                if (!mQuestionComplexityLevel.equals(complexityLevel)) {
                    if (mQuestionComplexityLevel.equals(getString(R.string.label_low))
                            && (complexityLevel.equals(getString(R.string.label_medium)) || complexityLevel.equals(getString(R.string.label_high)))) {
                        showLevelUpgradeMessage(getString(R.string.messageNextLevel));

                    } else if (mQuestionComplexityLevel.equals(getString(R.string.label_medium)) && complexityLevel.equals(getString(R.string.label_high))) {
                        showLevelUpgradeMessage(getString(R.string.messageNextLevel));
                    }
                }
            } else {
                String skillName = "<b><i>" + skill.getSkillName() + "</i></b>";
                showLevelUpgradeMessage(getString(R.string.messageNextSkill) + ConstantUtil.BLANK_SPACE + skillName);
            }
        }
    }

    /*Show point and streak animation.*/
    private void addPoints(boolean isFromStreak, int points) {

        mTotalScore = mTotalScore + points;
        if (isFromStreak) {
            mStreakBonusPoint = mStreakBonusPoint + points;
        } else {
            mCorrectAnswerPoint = mCorrectAnswerPoint + points;
        }
        mBinding.textViewAddedPoint.setVisibility(View.VISIBLE);
        mBinding.textViewAddedPoint.setText(String.valueOf(points));
        SoundUtils.playSound(getBaseContext(), SoundUtils.STREAK);


        new FlyObjectAnimationUtil().setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setTotalScore(mTotalScore);
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

    /*Set/Reset variables values when new list of questions fetched.*/
    private void setValueDefault() {

        mQuestionCounter = 0;
        resetTotalCorrectCounter();
        resetTotalInCorrectCounter();
        setStreakView(mTotalCorrect);
        mQuestionList = new ArrayList<>();
        mQuestionResponses = new ArrayList<>();
    }

    /*Reset mSkip*/
    private void resetSkip() {
        if (mTotalCorrect == QUESTION_TOTAL_CORRECT || mTotalInCorrect == QUESTION_TOTAL_INCORRECT) {
            mSkip = 0;
        }
    }

    /*Showing ui for showing level upgrade*/
    private void showLevelUpgradeMessage(String message) {

        GamificationEvent event = new GamificationEvent();
        event.setMessage(message);
        event.setEventType(getString(R.string.other));

        startActivity(MascotActivity.getStartIntent(getBaseContext(), message, event));

    }

    /*Start question timer when question initialize*/
    private void startTimer() {

        mBinding.chronometerQuestionTimer.start();
    }

    /*Setup question counter view when question initialize*/
    private void setQuestionCounter(int questionCounter) {
        AnimationUtils.fadeInFast(getBaseContext(), mBinding.scrollView);
        String counterText = "Question " + questionCounter;
        mBinding.textViewQuestionCounter.setText(counterText);
    }

    /*Setup question text view when question initialize*/
    private void setQuestionText(Question question) {
        mBinding.textViewQuestion.setVisibility(View.VISIBLE);

        ArrayList<Resource> questionResourceList = mPlayerModel.extractResourceListFromText(question.getQuestionText());

        initializeQuestionResourceRecyclerView(questionResourceList);

        setMovementMethod();
        String questionText;

        if (!TextUtils.isEmpty(question.getQuestionText())) {
            questionText = question.getQuestionText();

            if (question.getQuestionType().equals(Question.TYPE_DISPLAY_FILL_IN_THE_BLANKS)) {
                questionText = mPlayerModel.replaceAllCharacter(question.getQuestionText(), ConstantUtil.HTML_INPUT_START_TAG_WITH_SPACE, ConstantUtil.HTML_INPUT_START_TAG_WITH_SPACE_REPLACEMENT);
            }

            String questionTextFinal = mPlayerModel.cleanHtmlTextForPlayer(questionText);
            mBinding.textViewQuestion.setText(mPlayerModel.removeTrailingSpace(questionTextFinal));
        }

    }

    /*Setup question resource view when question initialize*/
    private void setQuestionResource(Question question) {
        String resourcePathImage = question.fetchQuestionImage().getUrlMain();
        String resourcePathVideo = question.fetchQuestionVideo().getUrlMain();
        if (!TextUtils.isEmpty(resourcePathImage)) {
            mBinding.layoutQuestionResource.setVisibility(View.VISIBLE);
            Picasso.with(getBaseContext()).load(resourcePathImage).into(mBinding.imageViewResourceThumbnail);
            mBinding.imageViewResourceThumbnail.setTag(question.fetchQuestionImage().getUrlMain());
            mBinding.imageViewResourceType.setImageResource(R.drawable.action_image_w);
        } else if (!TextUtils.isEmpty(resourcePathVideo)) {
            mBinding.layoutQuestionResource.setVisibility(View.VISIBLE);
            Picasso.with(getBaseContext()).load(resourcePathVideo).into(mBinding.imageViewResourceThumbnail);
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

    /*Question Resource Recycler View*/
    private void initializeQuestionResourceRecyclerView(ArrayList<Resource> questionResourceList) {
        if (!questionResourceList.isEmpty()) {
            mBinding.listQuestionResource.setVisibility(View.VISIBLE);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getBaseContext());
            mBinding.listQuestionResource.setLayoutManager(layoutManager);
            mBinding.listQuestionResource.setAdapter(new QuestionResourceAdapter(getBaseContext(), questionResourceList));
        } else {
            mBinding.listQuestionResource.setVisibility(View.GONE);
        }
    }

    /*Explanation Resource Recycler View*/
    private void initializeExplanationResourceRecyclerView(ArrayList<Resource> explanationResourceList) {
        if (!explanationResourceList.isEmpty()) {
            mBinding.listExplanationResource.setVisibility(View.VISIBLE);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getBaseContext());
            mBinding.listExplanationResource.setLayoutManager(layoutManager);
            mBinding.listExplanationResource.setAdapter(new QuestionResourceAdapter(getBaseContext(), explanationResourceList));
        } else {
            mBinding.listExplanationResource.setVisibility(View.GONE);
        }
    }

    /*Setup choice view when question initialize*/
    @SuppressLint("ClickableViewAccessibility")
    private void setChoicesView(Question question) {

        String questionType = question.getQuestionType();
        ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();
        mBinding.textViewQuestionType.setVisibility(View.VISIBLE);
        LayoutInflater layoutInflater = this.getLayoutInflater();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)
                || questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_TRUE_FALSE)) {

            mBinding.layoutChoices.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);

            layoutInflater.inflate(R.layout.layout_response_mcq_single_correct, mBinding.layoutChoices);
            RadioGroup radioGroup = mBinding.layoutChoices.findViewById(R.id.radio_group_response);
            mBinding.textViewQuestionType.setText(getString(R.string.single_correct));

            for (final QuestionChoice questionChoice : questionChoices) {

                layoutInflater.inflate(R.layout.layout_practice_response_item_mcq_single_correct, radioGroup);
                final RadioButtonCustom choice = (RadioButtonCustom) radioGroup.getChildAt(radioGroup.getChildCount() - 1);

                final String thumbUrl, mainUrl;

                if (!TextUtils.isEmpty(questionChoice.getChoiceText())
                        && questionChoice.getChoiceText().contains(ConstantUtil.HTML_IMAGE_START_TAG)
                        && questionChoice.getChoiceText().contains(ConstantUtil.HTML_IMAGE_SRC_TAG)) {

                    String choiceImageUrl = mPlayerModel.getStringFromHtmlTextAfterTagRemoval(questionChoice.getChoiceText(),
                            ConstantUtil.HTML_IMAGE_SRC_TAG, ConstantUtil.HTML_DOUBLE_QUOTE);

                    thumbUrl = choiceImageUrl;
                    mainUrl = choiceImageUrl;


                } else {
                    thumbUrl = questionChoice.getChoiceResource().getUrlMain();
                    mainUrl = questionChoice.getChoiceResource().getUrlMain();
                }

                if (!TextUtils.isEmpty(questionChoice.getChoiceText())) {

                    String choiceTextFinal = mPlayerModel.cleanHtmlTextForPlayer(questionChoice.getChoiceText());
                    choice.setText(mPlayerModel.removeTrailingSpace(choiceTextFinal));

                }

                choice.setClickable(true);
                choice.setAlpha(1f);
                choice.setTag(questionChoice.isChoiceCorrect());
                if (!TextUtils.isEmpty(thumbUrl) && !TextUtils.isEmpty(mainUrl)) {
                    Picasso.with(getBaseContext())
                            .load(thumbUrl)
                            .resize(ConstantUtil.CHOICE_IMAGE_MAX_WIDTH, 0)
                            .into(choice);
                }

            }


        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {

            mBinding.layoutChoices.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
            mBinding.textViewQuestionType.setText(getString(R.string.multiple_correct));

            for (final QuestionChoice questionChoice : questionChoices) {

                layoutInflater.inflate(R.layout.layout_practice_response_item_mcq_multiple_correct, mBinding.layoutChoices);
                CheckBoxCustom choice = (CheckBoxCustom) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);

                final String thumbUrl, mainUrl;

                if (!TextUtils.isEmpty(questionChoice.getChoiceText())
                        && questionChoice.getChoiceText().contains(ConstantUtil.HTML_IMAGE_START_TAG)
                        && questionChoice.getChoiceText().contains(ConstantUtil.HTML_IMAGE_SRC_TAG)) {

                    String choiceImageUrl = mPlayerModel.getStringFromHtmlTextAfterTagRemoval(questionChoice.getChoiceText(),
                            ConstantUtil.HTML_IMAGE_SRC_TAG, ConstantUtil.HTML_DOUBLE_QUOTE);

                    thumbUrl = choiceImageUrl;
                    mainUrl = choiceImageUrl;


                } else {
                    thumbUrl = questionChoice.getChoiceResource().getUrlMain();
                    mainUrl = questionChoice.getChoiceResource().getUrlMain();
                }


                String choiceTextFinal = mPlayerModel.cleanHtmlTextForPlayer(questionChoice.getChoiceText());
                choice.setText(mPlayerModel.removeTrailingSpace(choiceTextFinal));

                choice.setClickable(true);
                choice.setAlpha(1f);
                choice.setTag(questionChoice.isChoiceCorrect());
                if (!TextUtils.isEmpty(thumbUrl) && !TextUtils.isEmpty(mainUrl)) {
                    Picasso.with(getBaseContext())
                            .load(thumbUrl)
                            .resize(ConstantUtil.CHOICE_IMAGE_MAX_WIDTH, 0).into(choice);
                }


            }

        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_FILL_IN_THE_BLANKS)) {

            mBinding.layoutChoices.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);

            mBinding.textViewQuestionType.setText(getString(R.string.fill_the_blanks));
            ArrayList<String> inputTypeList = mPlayerModel.getStringListFromHtmlText(question.getQuestionText(), "<input ", "/>");
            ArrayList<String> fillTheBlankChoiceList = new ArrayList<>();

            for (int i = 0; i < inputTypeList.size(); i++) {
                fillTheBlankChoiceList.add(mPlayerModel.getStringFromHtmlTextAfterTagRemoval(inputTypeList.get(i),
                        ConstantUtil.HTML_VALUE_TAG, ConstantUtil.HTML_DOUBLE_QUOTE));
            }

            for (String questionChoice : fillTheBlankChoiceList) {
                layoutInflater.inflate(R.layout.layout_fill_the_blanks, mBinding.layoutChoices);
                final EditText choice = (EditText) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                choice.setTag(questionChoice);
                choice.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        choice.setSelection(choice.getText().toString().length());
                        choice.requestFocus();
                        choice.requestFocusFromTouch();
                        return false;
                    }
                });
            }

        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_MATCH_THE_FOLLOWING)) {
            setMatchTheFollowingView(question.getQuestionObject());
        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_DROPDOWN)) {
            setDropdownQuestionView(question, true);
        }else {
            mBinding.textViewQuestionType.setVisibility(View.INVISIBLE);

        }

        setMovementMethod();

    }

    private void setDropdownQuestionView(Question question, boolean isAttempt) {

        if (question.getQuestionPartList() != null && !question.getQuestionPartList().isEmpty()) {
            mBinding.textViewQuestionType.setText(getString(R.string.fill_the_dropdown));
            mBinding.textViewQuestion.setVisibility(View.GONE);
            mBinding.layoutMatchTheFollowingContainer.layoutMTF.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.VISIBLE);
            mBinding.layoutChoices.setVisibility(View.GONE);


            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            mBinding.recyclerView.setLayoutManager(layoutManager);
            mBinding.recyclerView.setNestedScrollingEnabled(false);
            mDropdownAdapter = new DropdownAdapter(getBaseContext(), question.getQuestionPartList(), isAttempt);
            mBinding.recyclerView.setAdapter(mDropdownAdapter);

        } else {
            showAlertDialog(getString(R.string.practice_force_close));
        }
    }


    @SuppressLint("CheckResult")
    private void setMatchTheFollowingView(final Object questionObject) {

        mBinding.textViewQuestionType.setText(getString(R.string.match_the_following));
        mBinding.textViewQuestion.setVisibility(View.GONE);
        mBinding.recyclerView.setVisibility(View.GONE);
        mBinding.layoutMatchTheFollowingContainer.layoutMTF.setVisibility(View.VISIBLE);

        Observable.create(new ObservableOnSubscribe<QuestionTypeMatchTheFollowing>() {
            @Override
            public void subscribe(ObservableEmitter<QuestionTypeMatchTheFollowing> emitter) throws Exception {

                Gson gson = new Gson();
                JsonObject jsonObject = gson.toJsonTree(questionObject).getAsJsonObject();
                String json = jsonObject.toString();
                QuestionTypeMatchTheFollowing matchTheFollowing = gson.fromJson(json, QuestionTypeMatchTheFollowing.class);

                emitter.onNext(matchTheFollowing);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QuestionTypeMatchTheFollowing>() {
                    @Override
                    public void accept(QuestionTypeMatchTheFollowing matchTheFollowing) throws Exception {
                        mBinding.layoutMatchTheFollowingContainer.textViewA.setText(matchTheFollowing.getEntityName());
                        mBinding.layoutMatchTheFollowingContainer.textViewB.setText(matchTheFollowing.getPlaceHolderName());

                        ArrayList<MatchingContent> matchingContentList = matchTheFollowing.getMatchingContentList();
                        for (int i = 0; i < matchingContentList.size(); i++) {
                            matchingContentList.get(i).setId(i);
                            matchingContentList.get(i).setColor(CommonUtils.getInstance().generateRandomColor());
                        }

                        ArrayList<MatchingContent> questionChoicesA = matchTheFollowing.getMatchingContentList();
                        ArrayList<MatchingContent> questionChoicesB = new ArrayList<>(matchingContentList);
                        Collections.shuffle(questionChoicesB);
                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
                        mMatchTheFollowingChoiceAdapter = new MatchTheFollowingChoiceAdapter(getBaseContext(), questionChoicesA, questionChoicesB, true);
                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setAdapter(mMatchTheFollowingChoiceAdapter);
                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setNestedScrollingEnabled(false);
                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setOnDragListener(new ChoiceDragListener());
                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setTag(matchTheFollowing.getMatchingContentList().size());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /*Setup hint view when question initialize*/
    private void setHintView(Question question) {

        if (question.getQuestionHints() != null && !question.getQuestionHints().isEmpty()) {
            int hintSize = question.getQuestionHints().size();

            String hintCounterText = "0/" + hintSize;
            mBinding.textQuestionHints.setText(hintCounterText);
            mBinding.buttonQuestionHints.setVisibility(View.VISIBLE);
            mBinding.textQuestionHints.setVisibility(View.VISIBLE);
        } else {
            mBinding.buttonQuestionHints.setVisibility(View.GONE);
            mBinding.textQuestionHints.setVisibility(View.GONE);
        }

    }

    /*Show next hint in hint layout, if hint available else show message.*/
    private void showNextHint(int hintCounter, Question question) {
        int hintSize = question.getQuestionHints().size();
        if (hintCounter == hintSize) {
            showSnackBar(getString(R.string.no_more_hints));
        } else {
            final QuestionHint questionHint = question.getQuestionHints().get(hintCounter);
            int finalCounterHint = hintCounter + 1;
            String currentHint = String.valueOf(finalCounterHint);
            String totalHints = String.valueOf(hintSize);

            if (mBinding.cardViewHints.getVisibility() == View.GONE) {
                AnimationUtils.pushUpEnter(getBaseContext(), mBinding.cardViewHints);
                mBinding.cardViewHints.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(questionHint.getHintText())) {

                this.getLayoutInflater().inflate(R.layout.layout_practice_response_hint_text, mBinding.layoutHints);
                LinearLayout layout = (LinearLayout) mBinding.layoutHints.getChildAt(mBinding.layoutHints.getChildCount() - 1);
                TextView hintTextView = layout.findViewById(R.id.text_view_hint);
                RecyclerView hintResourceRecyclerView = layout.findViewById(R.id.listHintResource);

                final TextView hintCounterTextView = layout.findViewById(R.id.text_view_hint_counter);
                String hintCounterText = currentHint + "/" + totalHints;
                hintCounterTextView.setText(hintCounterText);

                ArrayList<Resource> hintResourceList = mPlayerModel.extractResourceListFromText(questionHint.getHintText());

                initializeHintResourceRecyclerView(hintResourceRecyclerView, hintResourceList);

                String hintTextFinal = mPlayerModel.cleanHtmlTextForPlayer(questionHint.getHintText());

                CharSequence hintCharSequence = mPlayerModel.removeTrailingSpace(hintTextFinal);
                if (!TextUtils.isEmpty(hintCharSequence)) {
                    hintTextView.setVisibility(View.VISIBLE);
                    hintTextView.setText(hintCharSequence);
                } else {
                    hintTextView.setVisibility(View.GONE);
                }

                hintTextView.setMaxLines(Integer.MAX_VALUE);
                hintTextView.setVerticalScrollBarEnabled(true);
                hintTextView.setMovementMethod(new ScrollingMovementMethod());
                setMovementMethod();

            } else {

                final String resourcePath = questionHint.getHintResource().getUrlMain();
                String mimeType = URLConnection.guessContentTypeFromName(resourcePath);

                if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                    this.getLayoutInflater().inflate(R.layout.layout_practice_response_hint_image_thumbnail, mBinding.layoutHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutHints.getChildAt(mBinding.layoutHints.getChildCount() - 1);
                    final TextView hintCounterTextView = mLayout.findViewById(R.id.text_view_hint_counter);
                    String hintCounterText = currentHint + "/" + totalHints;
                    hintCounterTextView.setText(hintCounterText);
                    RelativeLayout layout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                    ImageView imageView = layout.findViewById(R.id.imageview_hint_image);
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
                    final TextView hintCounterTextView = mLayout.findViewById(R.id.text_view_hint_counter);
                    String hintCounterText = currentHint + "/" + totalHints;
                    hintCounterTextView.setText(hintCounterText);
                    RelativeLayout layout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                    ImageView imageView = layout.findViewById(R.id.imageview_hint_video);
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
            String hintCounterText = mHintCounter + "/" + hintSize;
            mBinding.textQuestionHints.setText(hintCounterText);
        }
    }

    private void initializeHintResourceRecyclerView(RecyclerView hintResourceRecyclerView, ArrayList<Resource> resourceList) {
        if (!resourceList.isEmpty()) {
            hintResourceRecyclerView.setLayoutManager(null);
            hintResourceRecyclerView.setAdapter(null);
            hintResourceRecyclerView.setVisibility(View.VISIBLE);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getBaseContext());
            hintResourceRecyclerView.setLayoutManager(layoutManager);
            hintResourceRecyclerView.setAdapter(new QuestionResourceAdapter(getBaseContext(), resourceList));
        } else {
            hintResourceRecyclerView.setVisibility(View.GONE);
        }
    }

    /*Show explanation view if question contains, after submitting the current question response.*/
    private void showExplanation(final Question question) {
        if ((question.getChoiceConfiguration().getQuestionExplanation() != null
                && !TextUtils.isEmpty(question.getChoiceConfiguration().getQuestionExplanation().getExplanationText()))
                || !TextUtils.isEmpty(question.getExplanation())) {

            String explanationText = question.getChoiceConfiguration().getQuestionExplanation().getExplanationText();

            if (TextUtils.isEmpty(explanationText)) {
                explanationText = question.getExplanation();
            }

            if (!TextUtils.isEmpty(explanationText)) {

                ArrayList<Resource> explanationResourceList = mPlayerModel.extractResourceListFromText(explanationText);
                initializeExplanationResourceRecyclerView(explanationResourceList);

                mBinding.layoutExplanation.setVisibility(View.VISIBLE);
                mBinding.textViewExplanationText.setVisibility(View.VISIBLE);

                AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutExplanation);


                String explanationTextFinal = mPlayerModel.cleanHtmlTextForPlayer(explanationText);
                mBinding.textViewExplanationText.setText(mPlayerModel.removeTrailingSpace(explanationTextFinal));


                mBinding.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                    }
                });

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

                mBinding.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);

                    }
                });

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

        setMovementMethod();
    }

    /*Submit click action*/
    private void submitClickAction(Question question) {

        if (question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)
                || question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_TRUE_FALSE)
                || question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {

            if (getSubmittedAnswerFromView(false, question).size() > 0) {
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));
                boolean isCorrectResponse = mPlayerModel.checkCorrectness(question, attempt);
                if (isCorrectResponse) {
                    mTotalCorrect++;
                    mTotalInCorrect = 0;
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                    setResponseView(true);
                    setStreakView(mTotalCorrect);

                } else {
                    mTotalInCorrect++;
                    mTotalCorrect = 0;
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                    setResponseView(false);
                    setStreakView(mTotalCorrect);

                }


                setViewAfterQuestionAttempt();
                showExplanation(question);

                attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
                attempt.setHintsAvailed(mHintCounter);
                attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());
                generateQuestionResponse(attempt, Boolean.toString(isCorrectResponse), question, mHintCounter);


            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.enter_response));
            }
        } else if (question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_FILL_IN_THE_BLANKS)) {
            if (!getSubmittedAnswerFromView(false, question).isEmpty()) {
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));

                boolean isCorrectResponse = mPlayerModel.checkBlankCorrectness(mBinding.layoutChoices);

                if (isCorrectResponse) {
                    mTotalCorrect++;
                    mTotalInCorrect = 0;
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                    setResponseView(true);
                    setStreakView(mTotalCorrect);
                } else {
                    mTotalInCorrect++;
                    mTotalCorrect = 0;
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                    setResponseView(false);
                    setStreakView(mTotalCorrect);
                }

                setViewAfterQuestionAttempt();
                showExplanation(question);

                attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
                attempt.setHintsAvailed(mHintCounter);
                attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());
                generateQuestionResponse(attempt, Boolean.toString(isCorrectResponse), question, mHintCounter);
            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.please_fill_all_the_blanks));
            }
        } else if (question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_MATCH_THE_FOLLOWING)) {
            if (!getSubmittedAnswerFromView(false, question).isEmpty()) {
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));

                mTotalCorrect++;
                resetTotalInCorrectCounter();
                attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                setResponseView(true);
                setStreakView(mTotalCorrect);

                setViewAfterQuestionAttempt();
                showExplanation(question);

                attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
                attempt.setHintsAvailed(mHintCounter);
                attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());
                generateQuestionResponse(attempt, Boolean.toString(true), question, mHintCounter);
            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.please_match_all_entities));
            }
        } else if (question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_DROPDOWN)) {
            if (!getSubmittedAnswerFromView(false, question).isEmpty()) {
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));

                if (mDropdownAdapter != null) {
                    boolean isCorrectResponse = mPlayerModel.checkDropdownCorrectness(mDropdownAdapter.getResponseList());
                    if (isCorrectResponse) {
                        mTotalCorrect++;
                        mTotalInCorrect = 0;
                        attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                        setResponseView(true);
                        setStreakView(mTotalCorrect);
                    } else {
                        mTotalInCorrect++;
                        mTotalCorrect = 0;
                        attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                        setResponseView(false);
                        setStreakView(mTotalCorrect);
                    }
                    setDropdownQuestionView(question, false);
                    setViewAfterQuestionAttempt();
                    showExplanation(question);

                    attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
                    attempt.setHintsAvailed(mHintCounter);
                    attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());
                    generateQuestionResponse(attempt, Boolean.toString(isCorrectResponse), question, mHintCounter);
                } else {
                    showAlertDialog(getString(R.string.practice_force_close));
                }

            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.please_fill_all_the_dropdown));
            }
        }


    }

    /*Next button click action*/
    private void nextClickAction() {
        if (mTotalCorrect == QUESTION_TOTAL_CORRECT) {
            addPoints(true, mStreakBonus);
            resetSkip();
            PracticeParent practiceParent = preparePracticePostData(QUESTION_TOTAL_CORRECT);
            submitAndFetchQuestions(practiceParent);
        } else if (mTotalInCorrect == QUESTION_TOTAL_INCORRECT) {
            resetSkip();
            PracticeParent practiceParent = preparePracticePostData(ConstantUtil.INT_ZERO);
            submitAndFetchQuestions(practiceParent);
        } else if (mQuestionList.size() > mQuestionCounter) {
            initializeQuestionUi();
            mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
        } else {
            PracticeParent practiceParent = preparePracticePostData(getStreakSize());
            submitAndFetchQuestions(practiceParent);
        }


    }

    /*Get streak size*/
    private int getStreakSize() {
        if (mTotalCorrect != 0) {
            return mTotalCorrect;
        } else {
            return mTotalInCorrect;
        }
    }

    /*Get skillId of currently displayed question.*/
    private String getCurrentQuestionSkillId(ArrayList<Skill> skills) {
        if (skills != null && !skills.isEmpty()) {
            if (!TextUtils.isEmpty(skills.get(0).getId())) {
                return skills.get(0).getId();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /*Click action of done button*/
    private void doneClickAction() {
        if (mBinding.layoutResponse.getVisibility() == View.VISIBLE) {
            AnimationUtils.slideOutLeft(getBaseContext(), mBinding.layoutResponse);
            mBinding.layoutResponse.setVisibility(View.GONE);
        }
    }

    /*Reset correct question counter, when a total incorrect question is greater then 0
     * or 4 questions have been correct*/
    private void resetTotalCorrectCounter() {
        if (mTotalCorrect == QUESTION_TOTAL_CORRECT) {
            mTotalCorrect = 0;
        }
    }

    /*Reset incorrect question counter, when a total correct question is greater then 0*/
    private void resetTotalInCorrectCounter() {
        if (mTotalInCorrect == QUESTION_TOTAL_INCORRECT) {
            mTotalInCorrect = 0;
        }
    }

    /**
     * send true if response is correct
     * send false if response is incorrect
     */
    private void setResponseView(boolean response) {

        mBinding.layoutPlayerResponseForUser.layoutQuestionResponse.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutPlayerResponseForUser.layoutQuestionResponse);

        if (response) {

            int randomIndex = new Random().nextInt(mCorrectMessages.length);
            String message = mCorrectMessages[randomIndex];
            mBinding.layoutPlayerResponseForUser.textViewQuestionResponse.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorGreenDark));
            mBinding.layoutPlayerResponseForUser.textViewQuestionResponse.setText(message);
            mBinding.layoutPlayerResponseForUser.imageViewMascotCorrectResponse.setVisibility(View.VISIBLE);
            mBinding.layoutPlayerResponseForUser.imageViewMascotWrongResponse.setVisibility(View.GONE);
//            mBinding.lottie.setAnimation("lottie_success.json");
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_CORRECT_ANSWER);
            addPoints(false, QUESTION_POINT);


        } else {

            int randomIndex = new Random().nextInt(mInCorrectMessages.length);
            String message = mInCorrectMessages[randomIndex];
            mBinding.layoutPlayerResponseForUser.textViewQuestionResponse.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorLNRed));
            mBinding.layoutPlayerResponseForUser.textViewQuestionResponse.setText(message);
            mBinding.layoutPlayerResponseForUser.imageViewMascotCorrectResponse.setVisibility(View.GONE);
            mBinding.layoutPlayerResponseForUser.imageViewMascotWrongResponse.setVisibility(View.VISIBLE);
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_INCORRECT_ANSWER);
//            mBinding.lottie.setAnimation("lottie_failed.json");

        }

//        mBinding.lottie.playAnimation();

        Handler hold = new Handler();
        hold.postDelayed(new Runnable() {

            @Override
            public void run() {

                AnimationUtils.pushDownExit(getBaseContext(), mBinding.layoutPlayerResponseForUser.layoutQuestionResponse);
                mBinding.layoutPlayerResponseForUser.layoutQuestionResponse.setVisibility(View.GONE);

            }
        }, ConstantUtil.PLAYER_RESPONSE_DURATION);

    }

    /*Set streak count, for ui it a continuously correct answer count.*/
    private void setStreakView(int streak) {

        mBinding.textViewStreak.setText(String.valueOf(streak));


    }

    /*Show question resource in full view.*/
    public void showResource(String resourcePath) {

        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
        if (mimeType != null) {
            if (mimeType.contains("image")) {
                startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), resourcePath, true));
            } else if (mimeType.contains("video")) {
                Resource item = new Resource();
                item.setType("video");
                item.setUrlMain(resourcePath);
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, item));
            }
        }


    }

    /*Method to get list of submitted answer from the dynamically created choice views*/
    private ArrayList<String> getSubmittedAnswerFromView(boolean isSubmission, Question question) {

        String questionType = question.getQuestionType();
        ArrayList<String> choiceResponses = new ArrayList<>();
        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)
                || questionType.equalsIgnoreCase("trueFalse")) {

            RadioGroup radioGroup = mBinding.layoutChoices.findViewById(R.id.radio_group_response);

            int count = radioGroup.getChildCount();

            for (int i = 0; i < count; i++) {

                RadioButton view = ((RadioButton) radioGroup.getChildAt(i));

                if (view.isChecked()) {
                    choiceResponses.add(String.valueOf(i));
                }

                if (isSubmission) {

                    Drawable drawable;

                    if (view.isChecked() && !(boolean) view.getTag()) {
                        drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.background_choice_selected_incorrect);

                    } else {
                        if ((boolean) view.getTag()) {
                            drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.background_choice_selected_correct);
                        } else {
                            drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.background_choice_unselected);
                        }
                    }

                    view.setBackground(drawable);
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

                    Drawable drawable;

                    if (view.isChecked() && !(boolean) view.getTag()) {
                        drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.background_choice_selected_incorrect);

                    } else {
                        if ((boolean) view.getTag()) {
                            drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.background_choice_selected_correct);
                        } else {
                            drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.background_choice_unselected);
                        }
                    }

                    view.setBackground(drawable);
                    view.setEnabled(false);
                }
            }

        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_FILL_IN_THE_BLANKS)) {
            int count = mBinding.layoutChoices.getChildCount();

            for (int i = 0; i < count; i++) {
                EditText view = ((EditText) mBinding.layoutChoices.getChildAt(i));
                if (!view.getText().toString().trim().isEmpty()) {
                    choiceResponses.add(view.getText().toString().trim());
                } else {
                    choiceResponses.clear();
                    break;
                }

                if (isSubmission) {
                    view.setEnabled(false);
                }
            }

        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_MATCH_THE_FOLLOWING)) {
            int count = (int) mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.getTag();
            if (count == mMTFMatchCount) {
                choiceResponses.add(String.valueOf(mMTFMatchCount));
            } else {
                new ArrayList<>();
            }

        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_DROPDOWN)) {
            if (mDropdownAdapter != null) {
                ArrayList<QuestionPart> questionParts = mDropdownAdapter.getResponseList();
                for (int i = 0; i < questionParts.size(); i++) {
                    if (!TextUtils.isEmpty(questionParts.get(i).getQuestion())) {
                        choiceResponses.add(questionParts.get(i).getQuestion());
                    } else {
                        choiceResponses.clear();
                        break;
                    }
                }

            }
        }

        return choiceResponses;
    }

    /*Generate questionResponse object and add to question response array.*/
    private void generateQuestionResponse(Attempt attempt, String response, Question question,
                                          int counterHint) {

        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setOrder(1);//set question order
        questionResponse.setQid(question.getUidQuestion());
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

    }

    /*Show this alert dialog in case of finish activity*/
    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(message)
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    /*Show this alert dialog in case of repeat allowed*/
    private void showRepeatAlertDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(getString(R.string.practice_repeat_message))
                .setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.repeat, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAllowRepeat = true;
                        PracticeParent practiceParent = preparePracticePostData(ConstantUtil.INT_ZERO);
                        submitAndFetchQuestions(practiceParent);
                        dialog.dismiss();

                    }
                })
                .show();
    }

    /*Show this alert dialog in case of practice ends.*/
    private void showPracticeEndDialog() {
        LayoutPracticeEndBinding view = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_practice_end, null, false);
        final Dialog dialog = new Dialog(PracticePlayerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(view.getRoot());

        if (mTotalScore > 0) {
            view.textViewGreeting.setText(getString(R.string.congratulations));
            view.lottie.setAnimation("celebration_star.json");
        } else {
            view.textViewGreeting.setText(getString(R.string.better_luck_next_time));
            view.lottie.setAnimation("sad.json");
        }

        if (mIsFromBack) {
            view.textViewGreetingMessage.setVisibility(View.GONE);
        } else {
            view.textViewGreetingMessage.setVisibility(View.VISIBLE);
            view.textViewGreetingMessage.setText(getString(R.string.practice_end_greeting_message));

        }

        view.lottie.setRepeatCount(Animation.INFINITE);
        view.lottie.playAnimation();

        String score = ConstantUtil.BLANK_SPACE + mTotalScore + ConstantUtil.BLANK_SPACE;
        String practicePoint = String.valueOf(mCorrectAnswerPoint);
        String streakBonus = String.valueOf(mStreakBonusPoint);

        view.textViewScore.setText(score);
        view.textViewPracticePoint.setText(practicePoint);
        view.textViewStreakBonus.setText(streakBonus);

        view.buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPointsToServer();
                dialog.dismiss();
                finish();
            }
        });

        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Double width = metrics.widthPixels * 0.80;
        Double height = metrics.heightPixels * 0.70;
        Window win = dialog.getWindow();
        assert win != null;
        win.setLayout(width.intValue(), height.intValue());


        dialog.show();


    }

    /*Show this alert dialog in case of no internet.*/
    private void showInternetAlertDialog(final PracticeParent practiceParent, final boolean isFromSubmitAndFetch) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(getString(R.string.error_message_no_internet))
                .setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                })
                .setPositiveButton(R.string.labelRetry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (isFromSubmitAndFetch) {
                            submitAndFetchQuestions(practiceParent);
                        } else {
                            fetchStreakBonusValue();
                        }
                    }
                })
                .show();
    }

    /*Show this dialog when user back press.*/
    private void showBackAlertDialog() {

        new AlertDialog.Builder(PracticePlayerActivity.this)
                .setMessage(getString(R.string.close_alert_title))
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mIsFromBack = true;
                        if (mQuestionResponses != null && mQuestionResponses.size() > 0) {
                            PracticeParent practiceParent = preparePracticePostData(getStreakSize());
                            submitAndFetchQuestions(practiceParent);
                        } else {
                            showPracticeEndDialog();
                        }

                    }
                }).setNegativeButton(R.string.label_continue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mIsFromBack = false;
                dialog.dismiss();
            }
        }).show();
    }

    /*General method to show message in snackBar.*/
    private void showSnackBar(String message) {
        SnackBarUtils.showAlertSnackBar(this, mBinding.getRoot(), message);
    }


    private class ChoiceDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View dropView, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                int y = Math.round(event.getY());
                int threshold = 600;
                if (Math.abs(y - mScrollTop) < threshold) {
                    mBinding.scrollView.smoothScrollBy(0, -30);
                } else if (Math.abs((y + mScrollTop) - mScrollBottom) < threshold) {
                    mBinding.scrollView.smoothScrollBy(0, 30);
                }
            }
            return true;
        }
    }

    public class ChoiceBDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View dropTargetView, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    break;

                case DragEvent.ACTION_DROP:
                    View droppedView = (View) event.getLocalState();
                    if (dropTargetView.getTag() == droppedView.getTag()) {
                        mMTFMatchCount++;
                        int count = (int) mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.getTag();
                        if (count == mMTFMatchCount) {
                            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.all_matched_please_submit));
                        }
                        mMatchTheFollowingChoiceAdapter.updateAdapter(droppedView.getId(), dropTargetView.getId());
                    } else {
                        Log.e("Drop--", "false");
                    }

                    break;

                default:

                    break;
            }
            return true;
        }
    }

    public class MatchTheFollowingChoiceAdapter extends RecyclerView.Adapter<MatchTheFollowingChoiceAdapter.ViewHolder> {
        private ArrayList<MatchingContent> mQuestionChoicesA;
        private ArrayList<MatchingContent> mQuestionChoicesB;
        private boolean mIsAttempt;
        private Context mContext;
        private SparseBooleanArray mItemStateArrayA = new SparseBooleanArray();
        private SparseBooleanArray mItemStateArrayB = new SparseBooleanArray();

        MatchTheFollowingChoiceAdapter(Context context, ArrayList<MatchingContent> questionChoicesA, ArrayList<MatchingContent> questionChoicesB, boolean isAttempt) {
            mQuestionChoicesA = questionChoicesA;
            mQuestionChoicesB = questionChoicesB;
            mIsAttempt = isAttempt;
            mContext = context;
        }

        @NonNull
        @Override
        public MatchTheFollowingChoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMatchTheFollowingItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_match_the_following_item, parent, false);
            return new MatchTheFollowingChoiceAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MatchTheFollowingChoiceAdapter.ViewHolder holder, int position) {
            setDefaults(holder.mBinding);
            int index = position / 2;

            if (position % 2 == 0) {
                /*Left side items*/
                MatchingContent choice = mQuestionChoicesA.get(index);
                setChoiceView(choice, index, holder.mBinding);


            } else {
                /*Right side items*/
                MatchingContent choiceB = mQuestionChoicesB.get(index);
                setChoiceBView(choiceB, index, holder.mBinding);

            }

        }

        @Override
        public int getItemCount() {
            return mQuestionChoicesA.size() + mQuestionChoicesB.size();

        }

        @SuppressLint("ClickableViewAccessibility")
        private void setChoiceView(MatchingContent choice, int index, LayoutMatchTheFollowingItemBinding binding) {
            int choiceId = choice.getId();
            String text = choice.getEntity();
            binding.layoutChoice.setTag(choiceId);
            binding.layoutChoice.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(text)) {
                binding.textView.setVisibility(View.VISIBLE);
                binding.textView.setText(text);
                binding.layoutChoice.setId(index);
            }

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(choice.getColor());
            gradientDrawable.setCornerRadius(12);

            binding.layoutChoice.setBackground(gradientDrawable);
            binding.layoutChoice.setOnTouchListener(new ChoiceTouchListener());

            if (mItemStateArrayA.get(index)) {
                binding.layoutChoice.setEnabled(false);
                binding.textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite66));
                GradientDrawable gradientDrawableDone = new GradientDrawable();
                gradientDrawableDone.setColor(choice.getColor());
                gradientDrawableDone.setCornerRadius(12);
                binding.layoutChoice.setBackground(gradientDrawableDone);
            }

        }

        private void setChoiceBView(MatchingContent choiceB, int index, LayoutMatchTheFollowingItemBinding binding) {
            int choiceIdB = choiceB.getId();
            String textB = choiceB.getPlaceHolder();
            binding.layoutChoiceB.setTag(choiceIdB);
            binding.layoutChoiceB.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(textB)) {
                binding.textViewB.setVisibility(View.VISIBLE);
                binding.textViewB.setText(textB);
                binding.layoutChoiceB.setId(index);
            }

            if (mItemStateArrayB.get(index)) {

                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setColor(choiceB.getColor());
                gradientDrawable.setCornerRadius(12);
                gradientDrawable.setStroke(4, Color.WHITE);

                binding.layoutChoiceB.setBackground(gradientDrawable);
            }

            binding.layoutChoiceB.setOnDragListener(new ChoiceBDragListener());

        }

        private void setReviewMode(MatchingContent choice, LayoutMatchTheFollowingItemBinding binding) {
            binding.layoutChoice.setBackground(null);
            binding.layoutChoice.setVisibility(View.VISIBLE);
            binding.layoutChoiceB.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = binding.layoutChoiceB.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            binding.layoutChoiceB.setLayoutParams(layoutParams);
//        AnimationUtils.fadeIn(mContext, binding.layoutMatchTheFollowingItem);
            binding.layoutMatchTheFollowingItem.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_choice_selected));
            String text = choice.getEntity();

            if (!TextUtils.isEmpty(text)) {
                binding.textView.setVisibility(View.VISIBLE);
                binding.textView.setText(text);
            }

//        if (choice.getChoiceResource() != null && !TextUtils.isEmpty(choice.getChoiceResource().getUrlMain())) {
//            binding.layoutImageView.setVisibility(View.VISIBLE);
//            Picasso.with(mContext).load(choice.getChoiceResource().getUrlMain()).resize(240, 180).into(binding.imageView);
//        }

            String textB = choice.getPlaceHolder();

            if (!TextUtils.isEmpty(textB)) {
                binding.textViewB.setVisibility(View.VISIBLE);
                binding.textViewB.setText(textB);
            }

//        if (choice.getChoiceResourceB() != null && !TextUtils.isEmpty(choice.getChoiceResourceB().getUrlMain())) {
//            binding.layoutImageViewB.setVisibility(View.VISIBLE);
//            Picasso.with(mContext).load(choice.getChoiceResourceB().getUrlMain()).resize(240, 180).into(binding.imageViewB);
//        }

        }

        @SuppressLint("ClickableViewAccessibility")
        private void setDefaults(LayoutMatchTheFollowingItemBinding binding) {
            binding.textView.setVisibility(View.GONE);
            binding.textViewB.setVisibility(View.GONE);
//          binding.layoutImageView.setVisibility(View.GONE);
//          binding.layoutImageViewB.setVisibility(View.GONE);
            binding.layoutChoice.setVisibility(View.GONE);
            binding.layoutChoiceB.setVisibility(View.GONE);
            binding.layoutChoice.setOnTouchListener(null);
            binding.layoutChoice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_choice_unselected));
            binding.layoutChoiceB.setOnDragListener(null);
            binding.layoutChoiceB.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_drop_area));
        }

        public void updateAdapter(int choiceIndexA, int choiceIndexB) {
            mItemStateArrayA.put(choiceIndexA, true);
            mItemStateArrayB.put(choiceIndexB, true);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMatchTheFollowingItemBinding mBinding;

            public ViewHolder(LayoutMatchTheFollowingItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

        public final class ChoiceTouchListener implements View.OnTouchListener {
            @SuppressLint({"NewApi", "ClickableViewAccessibility"})
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    return true;
                } else {
                    return false;
                }
            }
        }

    }

}