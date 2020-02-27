package in.securelearning.lil.android.player.view.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.robinhood.ticker.TickerUtils;
import com.squareup.picasso.Picasso;

import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationData;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutQuestionPlayerTopViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizPlayerBinding;
import in.securelearning.lil.android.app.databinding.LayoutRevisionEndBinding;
import in.securelearning.lil.android.app.databinding.LayoutRevisionTopicStartBinding;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.QuestionPart;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideo;
import in.securelearning.lil.android.player.dataobject.MatchingContent;
import in.securelearning.lil.android.player.dataobject.QuestionTypeMatchTheFollowing;
import in.securelearning.lil.android.player.dataobject.RevisionResponse;
import in.securelearning.lil.android.player.dataobject.RevisionResponsePost;
import in.securelearning.lil.android.player.events.RevisionCompletedEvent;
import in.securelearning.lil.android.player.listener.MTFDragListener;
import in.securelearning.lil.android.player.listener.MTFDragListenerNotifier;
import in.securelearning.lil.android.player.listener.MTFDropListenerNotifier;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.player.view.adapter.DropdownAdapter;
import in.securelearning.lil.android.player.view.adapter.MatchTheFollowingAdapter;
import in.securelearning.lil.android.player.view.adapter.QuestionResourceAdapter;
import in.securelearning.lil.android.player.view.fragment.QuestionFeedbackFragment;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.dataobjects.IdNameObject;
import in.securelearning.lil.android.syncadapter.utils.CheckBoxCustom;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.FlyObjectAnimationUtil;
import in.securelearning.lil.android.syncadapter.utils.RadioButtonCustom;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class RevisionPlayerActivity extends AppCompatActivity {

    @Inject
    PlayerModel mPlayerModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AppUserModel mAppUserModel;

    LayoutQuizPlayerBinding mBinding;
    LayoutQuestionPlayerTopViewBinding mTopViewBinding;

    private static final String REVISION_SUBJECT_ID = "revisionSubjectId";
    private static final String COURSE_ID = "courseId";
    private static final String COURSE_TYPE = "courseType";
    private static final String CARD_ID = "cardId";
    private static final String SECTION_ID = "sectionId";
    private final String NO_UPCOMING_TOPIC = "noUpcomingTopic";

    /*Learning type*/
    public static final String TYPE_ASSESSMENT_FOR_LEARNING = "assessmentForLearning";
    public static final String TYPE_ASSESSMENT_OF_LEARNING = "assessmentOfLearning";


    private ArrayList<QuestionResponse> mQuestionResponses = new ArrayList<>();
    private ArrayList<Question> mQuestionList = new ArrayList<>();
    private ArrayList<Attempt> mAttemptList = new ArrayList<>();
    private ArrayList<KhanAcademyVideo> mKhanAcademyVideoList;

    private int mTotalCorrect = 0;
    private int mTotalInCorrect = 0;
    private int mHintCounter = 0;
    private int mQuestionCounter = 0;
    private int mUiQuestionCounter = 1;
    private int mTotalScore = 0;
    private int mStreak = 0;
    private String mQuizId, mCurrentQuestionId, mCourseId, mCardId, mSectionId, mTypeOfLearning, mCourseType, mRevisionSubjectId;
    private int mPointsPerQuestion, mMaxAttemptLimitPerQuestion, mUserAttemptPerQuestion;
    private long mTotalTimeAttempted = 0L;

    private int mScrollTop;
    private int mScrollBottom;
    private int mMTFMatchCount;
    private boolean mUserMTFWrong;
    private MatchTheFollowingAdapter mMatchTheFollowingAdapter;
    private String[] mCorrectMessages;
    private String[] mInCorrectMessages;
    private DropdownAdapter mDropdownAdapter;
    private ProgressDialog mProgressDialog;
    private long mStartTime;
    private boolean mIsRevisionFirstTake = false; //This boolean to determine if user is taking revision for first time after coming from dashboard
    private boolean mIsRevisionFirstTakeFirstQuestion = false; //This boolean to determine if user is taking revision for first time after coming from dashboard and is attempting first question after that timer must not start with delay
    private IdNameObject mPreviousTopic, mOngoingTopic, mUpcomingTopic;
    private RevisionResponse mRevisionResponse;
    private boolean mIsUserSelfExiting = false;
    private ArrayList<ChartConfigurationData> mRevisionAnalyticsConfigData;

    @Override
    public void onBackPressed() {
        showBackAlertDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_quiz_player);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mTopViewBinding = mBinding.includeLayoutTop;
        mStartTime = System.currentTimeMillis();
        handleIntent();
        setUpResponseMessages();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mPlayerModel.uploadUserTimeSpent(mQuizId, getString(R.string.quiz).toLowerCase(),
                null, null, mStartTime, System.currentTimeMillis());
        super.onDestroy();
    }


    public static Intent getStartIntent(Context context, String revisionSubjectId) {
        Intent intent = new Intent(context, RevisionPlayerActivity.class);
        intent.putExtra(REVISION_SUBJECT_ID, revisionSubjectId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /*Handle intent and get bundle data from intent*/
    private void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mRevisionSubjectId = getIntent().getStringExtra(REVISION_SUBJECT_ID);
            mCourseId = getIntent().getStringExtra(COURSE_ID);
            mCourseType = getIntent().getStringExtra(COURSE_TYPE);

            /*since 'CARD_ID' only came with rapid learning*/
            if (!TextUtils.isEmpty(getIntent().getStringExtra(CARD_ID))) {
                mCardId = getIntent().getStringExtra(CARD_ID);
                mSectionId = getIntent().getStringExtra(SECTION_ID);
            }

            /*For now we are treating revision mPlayer as homework but hints are visible as practice*/
            mTypeOfLearning = TYPE_ASSESSMENT_OF_LEARNING;
            mIsRevisionFirstTake = true;
            mIsRevisionFirstTakeFirstQuestion = true;

            getRevisionConfiguration();

            // TODO: 18/11/19 rajatjain
            /*if (mCourseType.equalsIgnoreCase(getString(R.string.assignment).toLowerCase())) {
                // TODO: 12/8/19 hardcoded points and assessment type for homework/assignment
                mPointsPerQuestion = 20;

                fetchQuestionsAndSubmitRevision();
            } else {
                fetchQuizConfiguration();
            }*/

        } else {
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
            finish();
        }
    }

    /*Setup custom toolbar*/
    private void setUpToolbar() {

        if (mOngoingTopic != null && !TextUtils.isEmpty(mOngoingTopic.getName())) {
            mBinding.textViewToolbarTitle.setText(mOngoingTopic.getName());
        } else {
            mBinding.textViewToolbarTitle.setText(getString(R.string.labelRevision));
        }

        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

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

    /*Setting up messages while correct and incorrect response*/
    private void setUpResponseMessages() {
        mCorrectMessages = getResources().getStringArray(R.array.correct_response_messages);
        mInCorrectMessages = getResources().getStringArray(R.array.incorrect_response_messages);
    }

    /*Method to initialize question ui*/
    private void initializeQuestionUi() {

        if (!mQuestionList.isEmpty()) {

            Question question = mQuestionList.get(mQuestionCounter);
            question.setQuestionText(question.getQuestionText());
            setDefaultsForNewQuestion();
            setQuestionCounter(mUiQuestionCounter);
            setQuestionText(question);
            setHintView(question); // We are following revision as homework but hints are visible as practice
            setChoicesView(question);
            startTimer();
            setQuestionResource(question);
            initializeClickListeners(question);
            setMaxAttemptByQuestionAndLearningType(question);
            fetchExplanationVideos(mPlayerModel.getSkillIdList(question.getSkills()));
            mCurrentQuestionId = question.getUidQuestion();
            mQuestionCounter++;
            mUiQuestionCounter++;

        }

    }

    /*To fetch explanation videos*/
    @SuppressLint("CheckResult")
    private void fetchExplanationVideos(ArrayList<String> skillIdList) {

        mPlayerModel.fetchExplanationVideos(skillIdList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<KhanAcademyVideo>>() {
                    @Override
                    public void accept(ArrayList<KhanAcademyVideo> khanAcademyVideos) throws Exception {

                        if (khanAcademyVideos != null && !khanAcademyVideos.isEmpty()) {

                            mBinding.layoutKhanAcademyVideoHeader.setVisibility(View.VISIBLE);

                            mKhanAcademyVideoList = new ArrayList<>();
                            mKhanAcademyVideoList.addAll(khanAcademyVideos);

                        } else {
                            mBinding.layoutKhanAcademyVideoHeader.setVisibility(View.GONE);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mBinding.layoutKhanAcademyVideoHeader.setVisibility(View.GONE);

                    }
                });
    }

    /**
     * Multiple attempt - AFL - From Digital Book
     * Single attempt - AOL - From Homework, Recap/Lesson Plan.
     */
    private void setMaxAttemptByQuestionAndLearningType(Question question) {
        mMaxAttemptLimitPerQuestion = 1;
        /*String questionType = question.getQuestionType();
        if (mTypeOfLearning.equalsIgnoreCase(TYPE_ASSESSMENT_FOR_LEARNING)) {
            switch (questionType) {
                case Question.TYPE_DISPLAY_RADIO:
                case Question.TYPE_DISPLAY_CHECKBOX:
                case Question.TYPE_DISPLAY_TRUE_FALSE:
                    mMaxAttemptLimitPerQuestion = (question.getQuestionChoices().size() - 1);
                    break;
                case Question.TYPE_DISPLAY_FILL_IN_THE_BLANKS:
                    mMaxAttemptLimitPerQuestion = 1;
                    break;
                case Question.TYPE_DISPLAY_DROPDOWN:
                    mMaxAttemptLimitPerQuestion = mPlayerModel.getDropdownAttemptLimit(question) - 1;
                    break;
            }
        } else {
            mMaxAttemptLimitPerQuestion = 1;
        }*/
    }

    /*To enable click on link and identify link from click*/
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

    /*Set/Reset views of mPlayer when new question initialized*/
    private void setDefaultsForNewQuestion() {
        mBinding.buttonDone.setText(getString(R.string.submit));
        mBinding.buttonDone.setVisibility(View.VISIBLE);

        mBinding.layoutChoices.removeAllViews();

        mBinding.layoutMatchTheFollowingContainer.layoutMTF.setVisibility(View.GONE);

        mBinding.layoutExplanation.setVisibility(View.GONE);

        mTopViewBinding.layoutTimer.setVisibility(View.VISIBLE);
        mTopViewBinding.chronometerQuestionTimer.stop();
        mTopViewBinding.chronometerQuestionTimer.setBase(SystemClock.elapsedRealtime());

        mHintCounter = 0;
        mBinding.layoutHints.removeAllViews();
        mBinding.cardViewHints.setVisibility(View.GONE);

        mUserAttemptPerQuestion = 0;
        mAttemptList.clear();

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

            }


        });

    }

    /*Set/Reset views of mPlayer after question attempt.*/
    private void setViewAfterQuestionAttempt(boolean isCorrectResponse) {

        if (isCorrectResponse) {
            mTopViewBinding.chronometerQuestionTimer.stop();
            if (mQuestionList.size() == mQuestionCounter) {
                mBinding.buttonDone.setText(getString(R.string.finish));
            } else {
                mBinding.buttonDone.setText(getString(R.string.next));
            }
            hideHintLayouts();
        } else {
            if (mUserAttemptPerQuestion < mMaxAttemptLimitPerQuestion) {
                mBinding.buttonDone.setText(getString(R.string.submit));
                Question question = mQuestionList.get(mQuestionCounter - 1);
                mBinding.layoutChoices.removeAllViews();
                setChoicesView(question);
            } else if (mQuestionList.size() == mQuestionCounter) {
                mTopViewBinding.chronometerQuestionTimer.stop();
                mBinding.buttonDone.setText(getString(R.string.finish));
                hideHintLayouts();
            } else {
                mTopViewBinding.chronometerQuestionTimer.stop();
                mBinding.buttonDone.setText(getString(R.string.next));
                hideHintLayouts();
            }
        }

    }

    private void hideHintLayouts() {
        mBinding.cardViewHints.setVisibility(View.GONE);
        mTopViewBinding.layoutHintButton.setVisibility(View.GONE);
    }

    private void initializeClickListeners(final Question question) {
        mTopViewBinding.layoutHintButton.setOnClickListener(new View.OnClickListener() {
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
                    //i.e. on finish
                    finishClickAction();
                }
            }
        });

        mBinding.buttonQuestionFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    QuestionFeedbackFragment fragment = QuestionFeedbackFragment.newInstance(mCurrentQuestionId);
                    fragment.show(getSupportFragmentManager(), "bottomSheet");
                } else {
                    SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
                }
            }
        });
    }

    /*Prepare quiz post data to send question responses to server*/
    private RevisionResponsePost prepareQuizResponsePostData() {

        RevisionResponsePost revisionResponsePost = new RevisionResponsePost();

        if (mIsRevisionFirstTake) {
            /*user is taking revision for first time after click on revision subject at dashboard
             * meaning fetching questions for the first time using subject-id*/

            revisionResponsePost.setRevisionSubjectId(mRevisionSubjectId);

        } else {

            /* user already took revision of atleast one topic after click on revision subject at dashboard
             * meaning submitting question response(s) and fetching questions*/

            revisionResponsePost.setRevisionSubjectId(mRevisionSubjectId);
            /* For setTopicId
             * using previous topic when user click on finish or at onBackPress we save ongoing topic in previous topic
             * now you can use mOngoingTopic.getId() instead-of mPreviousTopic.getId()
             * */
            revisionResponsePost.setTopicId(mPreviousTopic.getId());
            revisionResponsePost.setTotalQuestions(mQuestionList.size());
            revisionResponsePost.setQuestionResponseList(mQuestionResponses);

        }


        return revisionResponsePost;
    }


    @SuppressLint("CheckResult")
    private void getRevisionConfiguration() {
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


            mPlayerModel.fetchRevisionConfiguration()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GlobalConfigurationParent>() {
                        @Override
                        public void accept(GlobalConfigurationParent configurationParent) {

                            if (configurationParent.getRevisionConfigurationResponse() != null
                                    && configurationParent.getRevisionConfigurationResponse().getRevisionPoint() >= 0) {

                                mPointsPerQuestion = configurationParent.getRevisionConfigurationResponse().getRevisionPoint();
                                mTypeOfLearning = TYPE_ASSESSMENT_OF_LEARNING;
                                fetchQuestionsAndSubmitRevision();

                            } else {
                                showAlertDialog(getString(R.string.messageQuizConfigurationNotAvailable));
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();

                            mProgressDialog.dismiss();
                            showAlertDialog(getString(R.string.messageQuizConfigurationNotAvailable));

                        }
                    });


        } else {
            showInternetAlertDialog("RevisionConfiguration");
        }

    }

    /*To fetch list of questions for revision*/
    @SuppressLint("CheckResult")
    private void fetchQuestionsAndSubmitRevision() {

        if (GeneralUtils.isNetworkAvailable(this)) {

            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(this, ConstantUtil.BLANK, getString(R.string.messagePleaseWait), false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                /*mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onBackPressed();
                    }
                });*/
            }

            mPlayerModel.fetchQuestionsAndSubmitRevision(prepareQuizResponsePostData())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<RevisionResponse>() {
                        @Override
                        public void accept(RevisionResponse revisionResponse) {
                            mProgressDialog.dismiss();

                            if (revisionResponse != null) {

                                mRevisionResponse = revisionResponse;

                                if (mRevisionResponse.getCurrentTopic() != null) {
                                    mOngoingTopic = mRevisionResponse.getCurrentTopic();
                                } else {
                                    mOngoingTopic = null;
                                }
                                if (mRevisionResponse.getNextTopic() != null) {
                                    mUpcomingTopic = mRevisionResponse.getNextTopic();
                                } else {
                                    //i.e Now all the topic for current revision subject is done
                                    mRxBus.send(new RevisionCompletedEvent());
                                    mUpcomingTopic = null;
                                }

                                if (!mIsRevisionFirstTake) {
                                    fetchQuizAnalyticsConfiguration();
                                }

                                if (mIsRevisionFirstTake) {
                                    showFirstTakeStartDialog();

                                    if (mRevisionResponse.getQuestionList() != null && !mRevisionResponse.getQuestionList().isEmpty()) {
                                        mQuestionList = mRevisionResponse.getQuestionList();
                                    }

                                    setUpToolbar();
                                    initializeQuestionUi();
                                }

                            } else {
                                mOngoingTopic = null;
                                mUpcomingTopic = null;
                                showAlertDialog(getString(R.string.error_something_went_wrong));
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            mProgressDialog.dismiss();

                            mOngoingTopic = null;
                            mUpcomingTopic = null;
                            showAlertDialog(getString(R.string.error_something_went_wrong));
                        }
                    });

        } else {
            showInternetAlertDialog(ConstantUtil.BLANK);
        }

    }

    /*To submit quiz responses*/
    @SuppressLint("CheckResult")
    private void submitQuizResponse() {
        if (GeneralUtils.isNetworkAvailable(this)) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, ConstantUtil.BLANK, getString(R.string.message_uploading_response), false);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            /*mPlayerModel.submitResponseOfQuiz(prepareQuizResponsePostData())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<QuizResponse>() {
                        @Override
                        public void accept(QuizResponse quizResponse) {
                            progressDialog.dismiss();
                            if (quizResponse != null) {
                                mRxBus.send(new QuizCompletedEvent(mQuizId));
                                // showPracticeEndDialog(quizResponse);
                                fetchQuizAnalyticsConfiguration(quizResponse);
                            } else {
                                showAlertDialog(getString(R.string.error_something_went_wrong));
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            progressDialog.dismiss();
                            showAlertDialog(getString(R.string.error_something_went_wrong));
                        }
                    });
*/
        } else {
//            showInternetAlertDialog("submitQuizResponse");
        }
    }


    /*Show point and streak animation.*/
    private void addPoints() {

        mTotalScore = mTotalScore + mPointsPerQuestion;
        mBinding.textViewAddedPoint.setVisibility(View.VISIBLE);
        mBinding.textViewAddedPoint.setText(String.valueOf(mPointsPerQuestion));
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
        if (mStreak > 2) {
            mStreak = 0;
        }
        ArrayList<Integer> streakList = new ArrayList<>();

//        mOngoingTopic = new IdNameObject();
//        mUpcomingTopic = new IdNameObject();

        mQuestionResponses = new ArrayList<>();
//        mQuestionList = new ArrayList<>();
        mAttemptList = new ArrayList<>();

        mTotalCorrect = 0;
        mTotalInCorrect = 0;
        mQuestionCounter = 0;
        mUiQuestionCounter = 1;
        mTotalScore = 0;
        mTotalTimeAttempted = 0L;
    }

    /*Start question timer when question initialize*/
    private void startTimer() {

        if (mIsRevisionFirstTakeFirstQuestion) {

            /*
             * This boolean to get if user is taking revision for first time after coming from dashboard
             * and is attempting first question after that timer must not start with delay
             * */

            Handler hold = new Handler();
            hold.postDelayed(new Runnable() {

                @Override
                public void run() {

                    mTopViewBinding.chronometerQuestionTimer.setBase(SystemClock.elapsedRealtime());
                    mTopViewBinding.chronometerQuestionTimer.start();
                    mIsRevisionFirstTakeFirstQuestion = false;

                }
            }, ConstantUtil.PLAYER_RESPONSE_DURATION);

        } else {
            mTopViewBinding.chronometerQuestionTimer.start();
        }

    }

    /*Setup question counter view when question initialize*/
    private void setQuestionCounter(int questionCounter) {
        AnimationUtils.fadeInFast(getBaseContext(), mBinding.scrollView);
        String counterText = "Question " + questionCounter + "/" + mQuestionList.size();
        mTopViewBinding.textViewQuestionCounter.setText(counterText);
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
            mBinding.listQuestionResource.setLayoutManager(null);
            mBinding.listQuestionResource.setAdapter(null);
            mBinding.listQuestionResource.setVisibility(View.VISIBLE);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getBaseContext());
            mBinding.listQuestionResource.setLayoutManager(layoutManager);
            mBinding.listQuestionResource.setAdapter(new QuestionResourceAdapter(getBaseContext(), questionResourceList, mQuizId, getString(R.string.quiz).toLowerCase()));
        } else {
            mBinding.listQuestionResource.setVisibility(View.GONE);
        }
    }

    /*Explanation Resource Recycler View*/
    private void initializeExplanationResourceRecyclerView(ArrayList<Resource> explanationResourceList) {
        if (!explanationResourceList.isEmpty()) {
            mBinding.listExplanationResource.setLayoutManager(null);
            mBinding.listExplanationResource.setAdapter(null);
            mBinding.listExplanationResource.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
            mBinding.listExplanationResource.setLayoutManager(layoutManager);
            mBinding.listExplanationResource.setAdapter(new QuestionResourceAdapter(getBaseContext(), explanationResourceList, mQuizId, getString(R.string.quiz).toLowerCase()));
        } else {
            mBinding.listExplanationResource.setVisibility(View.GONE);
        }
    }


    /*Setup choice view when question initialize*/
    @SuppressLint("ClickableViewAccessibility")
    private void setChoicesView(final Question question) {

        String questionType = question.getQuestionType();
        ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();

        LayoutInflater layoutInflater = this.getLayoutInflater();
        mBinding.includeLayoutTop.textViewQuestionType.setVisibility(View.VISIBLE);
        mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {

            layoutInflater.inflate(R.layout.layout_response_mcq_single_correct, mBinding.layoutChoices);
            RadioGroup radioGroup = mBinding.layoutChoices.findViewById(R.id.radio_group_response);

            mBinding.layoutChoices.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
            mTopViewBinding.textViewQuestionType.setText(getString(R.string.single_correct));

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
            mTopViewBinding.textViewQuestionType.setText(getString(R.string.multiple_correct));

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

        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_FILL_IN_THE_BLANKS)) {

            mBinding.layoutChoices.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);

            mTopViewBinding.textViewQuestionType.setText(getString(R.string.fill_the_blanks));
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

        } else {
            mBinding.includeLayoutTop.textViewQuestionType.setVisibility(View.INVISIBLE);

        }


    }


    private void setDropdownQuestionView(Question question, boolean isAttempt) {

        if (question.getQuestionPartList() != null && !question.getQuestionPartList().isEmpty()) {
            mTopViewBinding.textViewQuestionType.setText(getString(R.string.fill_the_dropdown));
            mBinding.textViewQuestion.setVisibility(View.GONE);
            mBinding.layoutMatchTheFollowingContainer.layoutMTF.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.VISIBLE);
            mBinding.layoutChoices.setVisibility(View.GONE);

            mBinding.recyclerView.setLayoutManager(new FlexboxLayoutManager(getBaseContext()));
            mBinding.recyclerView.setNestedScrollingEnabled(false);
            mDropdownAdapter = new DropdownAdapter(getBaseContext(), question.getQuestionPartList(), isAttempt);
            mBinding.recyclerView.setAdapter(mDropdownAdapter);

        } else {
            showAlertDialog(getString(R.string.quiz_force_close));
        }
    }

    @SuppressLint("CheckResult")
    private void setMatchTheFollowingView(final Object questionObject) {

        mTopViewBinding.textViewQuestionType.setText(getString(R.string.match_the_following));
        mBinding.textViewQuestion.setVisibility(View.GONE);
        mBinding.layoutChoices.setVisibility(View.GONE);
        mBinding.recyclerView.setVisibility(View.GONE);
        mBinding.layoutMatchTheFollowingContainer.layoutMTF.setVisibility(View.VISIBLE);

        Observable.create(new ObservableOnSubscribe<QuestionTypeMatchTheFollowing>() {
            @Override
            public void subscribe(ObservableEmitter<QuestionTypeMatchTheFollowing> emitter) {

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
                    public void accept(final QuestionTypeMatchTheFollowing matchTheFollowing) {

                        mBinding.layoutMatchTheFollowingContainer.textViewA.setText(matchTheFollowing.getEntityName());
                        mBinding.layoutMatchTheFollowingContainer.textViewB.setText(matchTheFollowing.getPlaceHolderName());

                        final ArrayList<MatchingContent> matchingContentList = matchTheFollowing.getMatchingContentList();

                        for (int i = 0; i < matchingContentList.size(); i++) {
                            matchingContentList.get(i).setId(i);
                            matchingContentList.get(i).setColor(CommonUtils.getInstance().generateRandomColor());
                        }

                        final ArrayList<MatchingContent> questionChoicesA = matchTheFollowing.getMatchingContentList();
                        final ArrayList<MatchingContent> questionChoicesB = new ArrayList<>(matchingContentList);

                        /*Shuffling for making difficult*/
                        Collections.shuffle(questionChoicesB);

                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setNestedScrollingEnabled(false);

                        /*To listen and notify dragging*/
                        MTFDragListenerNotifier mtfDragListenerNotifier = new MTFDragListenerNotifier() {
                            @Override
                            public void OnDraggingStart(int dx, int dy) {
                                mBinding.scrollView.smoothScrollBy(dx, dy);
                            }
                        };


                        /*To listen and notify dropping*/
                        MTFDropListenerNotifier mtfDropListenerNotifier = new MTFDropListenerNotifier() {

                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public void OnDropped(Attempt attempt, boolean isCorrect, View droppedView, View targetView) {
                                if (isCorrect) {

                                    mMTFMatchCount++;

                                    int count = (int) mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.getTag();

                                    if (count == mMTFMatchCount) {
                                        setResponseView(true);
                                    }

                                    if (mMatchTheFollowingAdapter != null) {
                                        mMatchTheFollowingAdapter.updateAdapter(droppedView.getId(), targetView.getId());

                                    }

                                    addAttemptToList(attempt, true);

                                } else {

                                    addAttemptToList(attempt, false);

                                    if (mTypeOfLearning.equalsIgnoreCase(TYPE_ASSESSMENT_FOR_LEARNING)) {
                                        setResponseView(false);

                                        mUserMTFWrong = true;

                                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                return true;
                                            }
                                        });

                                        final ArrayList<MatchingContent> questionChoicesA = matchTheFollowing.getMatchingContentList();
                                        final ArrayList<MatchingContent> questionChoicesB = new ArrayList<>(matchingContentList);
                                        mMatchTheFollowingAdapter = new MatchTheFollowingAdapter(getBaseContext(), questionChoicesA, questionChoicesB, false, null);
                                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setAdapter(mMatchTheFollowingAdapter);
                                    }


                                }
                            }
                        };

                        mMatchTheFollowingAdapter = new MatchTheFollowingAdapter(getBaseContext(), questionChoicesA, questionChoicesB, true, mtfDropListenerNotifier);
                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setAdapter(mMatchTheFollowingAdapter);

                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setOnDragListener(new MTFDragListener(mScrollTop, mScrollBottom, mtfDragListenerNotifier));
                        mBinding.layoutMatchTheFollowingContainer.recyclerViewMTF.setTag(matchTheFollowing.getMatchingContentList().size());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /*Setup hint view when question initialize*/
    private void setHintView(Question question) {

//        if (mTypeOfLearning.equalsIgnoreCase(TYPE_ASSESSMENT_FOR_LEARNING)) {
        if (question.getQuestionHints() != null) {

            ArrayList<QuestionHint> questionHintList = mPlayerModel.getRefinedHintList(question.getQuestionHints());

            if (!questionHintList.isEmpty()) {

                int hintSize = questionHintList.size();

                String hintCounterText = "0/" + hintSize;
                mTopViewBinding.textHintCounter.setText(hintCounterText);
                mTopViewBinding.layoutHintButton.setVisibility(View.VISIBLE);

            } else {
                mTopViewBinding.layoutHintButton.setVisibility(View.GONE);
            }

        } else {
            mTopViewBinding.layoutHintButton.setVisibility(View.GONE);
        }
//        } else {
//            mTopViewBinding.layoutHintButton.setVisibility(View.GONE);
//        }


    }

    /*Show next hint in hint layout, if hint available else show message.*/
    private void showNextHint(int hintCounter, Question question) {

        ArrayList<QuestionHint> questionHintList = mPlayerModel.getRefinedHintList(question.getQuestionHints());
        int hintSize = questionHintList.size();

        if (hintCounter == hintSize) {
            showSnackBar(getString(R.string.no_more_hints));
        } else {
            final QuestionHint questionHint = questionHintList.get(hintCounter);
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
            mTopViewBinding.textHintCounter.setText(hintCounterText);


            /* When user click on the hint to preview hint in MTF(Match the following) question
             * Then the movement of the entity is not smooth as it was before viewing hint
             * To resolve the issue below condition */
            if (!TextUtils.isEmpty(question.getQuestionType())
                    && question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_MATCH_THE_FOLLOWING)) {

                if (mMatchTheFollowingAdapter != null) {

                    mMatchTheFollowingAdapter.notifyDataSetChanged();

                    Handler refocusMTF = new Handler();
                    refocusMTF.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mMatchTheFollowingAdapter.notifyDataSetChanged();
                        }
                    }, 100);
                }
            }

        }
    }

    private void initializeHintResourceRecyclerView(RecyclerView hintResourceRecyclerView, ArrayList<Resource> resourceList) {
        if (!resourceList.isEmpty()) {
            hintResourceRecyclerView.setLayoutManager(null);
            hintResourceRecyclerView.setAdapter(null);
            hintResourceRecyclerView.setVisibility(View.VISIBLE);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getBaseContext());
            hintResourceRecyclerView.setLayoutManager(layoutManager);
            hintResourceRecyclerView.setAdapter(new QuestionResourceAdapter(getBaseContext(), resourceList, mQuizId, getString(R.string.quiz).toLowerCase()));
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
                ArrayList<Resource> convertedResources = new ArrayList<>();
                if (mKhanAcademyVideoList != null && !mKhanAcademyVideoList.isEmpty()) {
                    convertedResources.addAll(mPlayerModel.transformKAVideoIntoResource(mKhanAcademyVideoList));
                }

                ArrayList<Resource> explanationResourcesFinal = new ArrayList<>();
                explanationResourcesFinal.addAll(convertedResources);
                explanationResourcesFinal.addAll(explanationResourceList);

                initializeExplanationResourceRecyclerView(explanationResourcesFinal);

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
                mUserAttemptPerQuestion++;
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));
                boolean isCorrectResponse = mPlayerModel.checkCorrectness(question, attempt);

                if (isCorrectResponse) {

                    mTotalCorrect++;
                    mStreak++;
                    resetTotalInCorrectCounter();
                    setStreakView(mTotalCorrect);
                    addAttemptToList(attempt, true);
                    setResponseView(true);
                    showExplanation(question);

                    generateQuestionResponse(Boolean.toString(true), question, mHintCounter, mAttemptList);

                } else {

                    if (mUserAttemptPerQuestion < mMaxAttemptLimitPerQuestion) {

                        addAttemptToList(attempt, false);

                    } else {

                        mTotalInCorrect++;

                        setStreakView(mTotalCorrect);
                        showExplanation(question);

                        addAttemptToList(attempt, false);

                        generateQuestionResponse(Boolean.toString(false), question, mHintCounter, mAttemptList);

                    }
                    setResponseView(false);
                    resetTotalCorrectCounter();

                }

                setViewAfterQuestionAttempt(isCorrectResponse);

            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.enter_response));
            }

        } else if (question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_FILL_IN_THE_BLANKS)) {
            if (!getSubmittedAnswerFromView(false, question).isEmpty()) {
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));
                mUserAttemptPerQuestion++;

                boolean isCorrectResponse = mPlayerModel.checkBlankCorrectness(mBinding.layoutChoices);

                if (isCorrectResponse) {
                    mTotalCorrect++;
                    resetTotalInCorrectCounter();
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                    setResponseView(true);
                    setStreakView(mTotalCorrect);
                    addAttemptToList(attempt, true);
                } else {
                    mTotalInCorrect++;
                    resetTotalCorrectCounter();
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                    setResponseView(false);
                    setStreakView(mTotalCorrect);
                    addAttemptToList(attempt, false);
                }

                setViewAfterQuestionAttempt(isCorrectResponse);
                showExplanation(question);

                attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
                attempt.setHintsAvailed(mHintCounter);
                attempt.setTimeTaken(SystemClock.elapsedRealtime() - mTopViewBinding.chronometerQuestionTimer.getBase());
                generateQuestionResponse(Boolean.toString(isCorrectResponse), question, mHintCounter, mAttemptList);
            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.please_fill_all_the_blanks));
            }
        } else if (question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_MATCH_THE_FOLLOWING)) {
            if (!getSubmittedAnswerFromView(false, question).isEmpty()) {

                mTotalCorrect++;
                resetTotalInCorrectCounter();
                setStreakView(mTotalCorrect);

                setViewAfterQuestionAttempt(true);
                showExplanation(question);
                generateQuestionResponse(Boolean.toString(true), question, mHintCounter, mAttemptList);

            } else {
                if (mUserMTFWrong) {

                    setStreakView(mTotalCorrect);

                    setViewAfterQuestionAttempt(false);

                    showExplanation(question);

                    generateQuestionResponse(Boolean.toString(false), question, mHintCounter, mAttemptList);

                } else {
                    mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                    showSnackBar(getString(R.string.please_match_all_entities));
                }

            }

        } else if (question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_DROPDOWN)) {
            if (!getSubmittedAnswerFromView(false, question).isEmpty()) {
                mUserAttemptPerQuestion++;
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));

                if (mDropdownAdapter != null) {
                    boolean isCorrectResponse = mPlayerModel.checkDropdownCorrectness(mDropdownAdapter.getResponseList());
                    if (isCorrectResponse) {

                        mTotalCorrect++;
                        mStreak++;
                        resetTotalInCorrectCounter();
                        setStreakView(mTotalCorrect);
                        addAttemptToList(attempt, true);
                        setResponseView(true);
                        showExplanation(question);
                        setDropdownQuestionView(question, false);

                        generateQuestionResponse(Boolean.toString(true), question, mHintCounter, mAttemptList);

                    } else {

                        if (mUserAttemptPerQuestion < mMaxAttemptLimitPerQuestion) {

                            addAttemptToList(attempt, false);

                        } else {

                            mTotalInCorrect++;

                            setStreakView(mTotalCorrect);

                            showExplanation(question);

                            addAttemptToList(attempt, false);

                            setDropdownQuestionView(question, false);

                            generateQuestionResponse(Boolean.toString(false), question, mHintCounter, mAttemptList);

                        }
                        setResponseView(false);
                        resetTotalCorrectCounter();

                    }
                    setViewAfterQuestionAttempt(isCorrectResponse);

                } else {
                    showSnackBar(getString(R.string.practice_force_close));
                }

            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.please_fill_all_the_dropdown));
            }
        }


    }

    /*To add each attempt in attempt list*/
    private void addAttemptToList(Attempt attempt, boolean isCorrectResponse) {
        if (isCorrectResponse) {
            attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
        } else {
            attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
        }
        if (mUserAttemptPerQuestion == 1) {
            mTotalTimeAttempted = SystemClock.elapsedRealtime() - mTopViewBinding.chronometerQuestionTimer.getBase();
            attempt.setTimeTaken(SystemClock.elapsedRealtime() - mTopViewBinding.chronometerQuestionTimer.getBase());
        } else {
            long time = SystemClock.elapsedRealtime() - mTopViewBinding.chronometerQuestionTimer.getBase() - mTotalTimeAttempted;
            mTotalTimeAttempted = mTotalTimeAttempted + time;
            attempt.setTimeTaken(time);
        }
        attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
        attempt.setHintsAvailed(mHintCounter);

        mAttemptList.add(attempt);

    }

    /*Next button click action*/
    private void nextClickAction() {

        if (mQuestionList.size() > mQuestionCounter) {
            initializeQuestionUi();
            mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
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

    /*Click action of done button*/
    private void finishClickAction() {

        if (mOngoingTopic != null) {
            mPreviousTopic = mOngoingTopic;
        }

        fetchQuestionsAndSubmitRevision();

    }

    /*Reset correct question counter and streak counter, when a total incorrect question is greater then 0*/
    private void resetTotalCorrectCounter() {
        mTotalCorrect = 0;
        mStreak = 0;
    }

    /*Reset incorrect question counter, when a total correct question is greater then 0*/
    private void resetTotalInCorrectCounter() {
        mTotalInCorrect = 0;
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
            addPoints();


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
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, item));
            }
        }


    }

    /*Method to get list of submitted answer from the dynamically created choice views*/
    private ArrayList<String> getSubmittedAnswerFromView(boolean isSubmission, Question question) {

        String questionType = question.getQuestionType();
        ArrayList<String> choiceResponses = new ArrayList<>();
        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
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
    private void generateQuestionResponse(String response, Question question, int counterHint, ArrayList<Attempt> attemptList) {

        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setOrder(question.getOrder());//set question order
        questionResponse.setQid(question.getUidQuestion());
        questionResponse.setHintUsed(counterHint);
        if (!TextUtils.isEmpty(response)) {
            questionResponse.setResponse(response);
        }

        for (int i = 0; i < attemptList.size(); i++) {
            questionResponse.addAttempt(attemptList.get(i));
        }

        mQuestionResponses.add(questionResponse);

    }

    /*Show this alert dialog in case of finish activity*/
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

    /*Show this alert dialog in case of practice ends.*/
    private void showPracticeEndDialog(ArrayList<ChartConfigurationData> quizAnalyticsConfigData) {
        LayoutRevisionEndBinding view = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_revision_end, null, false);
        final Dialog dialog = new Dialog(RevisionPlayerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(view.getRoot());
        view.lottie.setVisibility(View.GONE);
        setQuizDataOnEndDialog(view, quizAnalyticsConfigData);


        view.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPointsToServer();
                dialog.dismiss();
                finish();
            }
        });

        view.buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPointsToServer();
                dialog.dismiss();
                finish();
            }
        });

        view.buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setValueDefault();

                mQuestionList = new ArrayList<>();
                if (mRevisionResponse != null
                        && mRevisionResponse.getQuestionList() != null
                        && !mRevisionResponse.getQuestionList().isEmpty()) {
                    mQuestionList = mRevisionResponse.getQuestionList();
                }

                setUpToolbar();
                initializeQuestionUi();
            }
        });


        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Double dialogWidth = metrics.widthPixels * 0.95;
        Double dialogHeight = metrics.heightPixels * 0.85;
        Window win = dialog.getWindow();
        win.setLayout(dialogWidth.intValue(), ViewGroup.LayoutParams.WRAP_CONTENT);


        dialog.show();
    }

    private void setQuizDataOnEndDialog(LayoutRevisionEndBinding endDialogBinding, ArrayList<ChartConfigurationData> quizAnalyticsConfigData) {

        if (mPreviousTopic != null && !TextUtils.isEmpty(mPreviousTopic.getName())) {
            String text = "\"" + mPreviousTopic.getName() + "\"";
            endDialogBinding.textViewCurrentTopicName.setText(text);
        }

        if (mOngoingTopic != null && !TextUtils.isEmpty(mOngoingTopic.getName()) && !mIsUserSelfExiting) {

            endDialogBinding.layoutNextTopic.setVisibility(View.VISIBLE);
            String text = "\"" + mOngoingTopic.getName() + "\"";
            endDialogBinding.textViewNextTopicName.setText(text);

        } else {
            endDialogBinding.layoutNextTopic.setVisibility(View.GONE);
        }

        if (mPreviousTopic != null && mOngoingTopic != null) {
            if (mPreviousTopic.getId().equalsIgnoreCase(mOngoingTopic.getId())) {
                endDialogBinding.layoutNextTopic.setVisibility(View.GONE);
            }
        }

        String score = ConstantUtil.BLANK_SPACE + mTotalScore + ConstantUtil.BLANK_SPACE;
        endDialogBinding.textViewScore.setText(score);

        int totalCorrectAnswers = 0;

        long totalTimeTaken = 0;
        for (int i = 0; i < mQuestionResponses.size(); i++) {

            if (mQuestionResponses.get(i).getResponse().equalsIgnoreCase(Boolean.toString(true))) {
                totalCorrectAnswers++;
            }

            if (!mQuestionResponses.get(i).getAttempts().isEmpty()) {

                for (int j = 0; j < mQuestionResponses.get(i).getAttempts().size(); j++) {
                    totalTimeTaken = totalTimeTaken + mQuestionResponses.get(i).getAttempts().get(j).getTimeTaken();
                }

            }
        }

        String questionCountString = totalCorrectAnswers + "/" + mQuestionList.size();
        endDialogBinding.textViewQuestionsCount.setText(questionCountString);
        endDialogBinding.textViewTotalTime.setText(CommonUtils.getInstance().showSecondAndMinutesFromLong(totalTimeTaken));

        float correctAnsPercentage = (float) (totalCorrectAnswers * 100.00) / mQuestionList.size();
        drawAccuracyChart(correctAnsPercentage, endDialogBinding, quizAnalyticsConfigData);
    }

    /*draw and set values for accuracy pie chart*/
    /*Get color form config */
    private void drawAccuracyChart(float correctAnsPercent, LayoutRevisionEndBinding endDialogBinding, ArrayList<ChartConfigurationData> quizAnalyticsConfigData) {

        float total = 100;
        float remaining = total - correctAnsPercent;

        ArrayList<PieEntry> fillValues = new ArrayList<>();
        int progressColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);

        if (quizAnalyticsConfigData != null && !quizAnalyticsConfigData.isEmpty()) {
            progressColor = pickColorAccording((int) correctAnsPercent, quizAnalyticsConfigData);
        }

        fillValues.add(new PieEntry(correctAnsPercent, 0));
        fillValues.add(new PieEntry(remaining, 1));
        PieDataSet dataSet = new PieDataSet(fillValues, ConstantUtil.BLANK);
        dataSet.setColors(progressColor, ContextCompat.getColor(getBaseContext(), R.color.colorGrey));
        dataSet.setValueTextSize(0f);
        PieData data = new PieData(dataSet);
        endDialogBinding.pieChartAccuracy.setData(data);
        endDialogBinding.pieChartAccuracy.setHoleRadius(90f);
        endDialogBinding.pieChartAccuracy.setDrawHoleEnabled(true);
        endDialogBinding.pieChartAccuracy.setUsePercentValues(false);
        endDialogBinding.pieChartAccuracy.setContentDescription(ConstantUtil.BLANK);
        Description description = new Description();
        description.setText(ConstantUtil.BLANK);
        endDialogBinding.pieChartAccuracy.setDescription(description);
        endDialogBinding.pieChartAccuracy.setDrawCenterText(true);
        endDialogBinding.pieChartAccuracy.setCenterTextSize(24f);
        endDialogBinding.pieChartAccuracy.setCenterTextColor(progressColor);
        String centerTextValue = new DecimalFormat("##.##").format(correctAnsPercent) + "%";
        endDialogBinding.pieChartAccuracy.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if (centerTextValue.contains("NaN")) {
            endDialogBinding.pieChartAccuracy.setCenterText("0%");
        } else {
            endDialogBinding.pieChartAccuracy.setCenterText(centerTextValue);
        }
        endDialogBinding.pieChartAccuracy.getLegend().setEnabled(false);
        endDialogBinding.pieChartAccuracy.invalidate();
        endDialogBinding.pieChartAccuracy.setClickable(false);
        endDialogBinding.pieChartAccuracy.setTouchEnabled(false);
    }

    /*Show this alert dialog in case of revision start for first take.*/
    private void showFirstTakeStartDialog() {
        LayoutRevisionTopicStartBinding view = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_revision_topic_start, null, false);
        final Dialog dialogStart = new Dialog(RevisionPlayerActivity.this);
        dialogStart.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStart.setCancelable(false);
        dialogStart.setContentView(view.getRoot());

        if (mOngoingTopic != null && !TextUtils.isEmpty(mOngoingTopic.getName())) {
            String text = "\"" + mOngoingTopic.getName() + "\"";
            view.textViewTopicName.setText(text);
        }

        Window win = dialogStart.getWindow();
        if (win != null) {
            win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }


        Handler hold = new Handler();
        hold.postDelayed(new Runnable() {

            @Override
            public void run() {

                dialogStart.dismiss();
                mIsRevisionFirstTake = false;

            }
        }, ConstantUtil.PLAYER_RESPONSE_DURATION);

        dialogStart.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorGrey66)));
        dialogStart.show();
    }

    /*Show this alert dialog in case of no internet.*/
    private void showInternetAlertDialog(final String callingFrom) {
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

                        if (callingFrom.equalsIgnoreCase("RevisionConfiguration")) {
                            getRevisionConfiguration();
                        } else if (callingFrom.equalsIgnoreCase("QuizAnalyticsConfiguration")) {
                            fetchQuizAnalyticsConfiguration();
                        } else {
                            fetchQuestionsAndSubmitRevision();
                        }
                    }
                })
                .show();
    }

    /*Show this dialog when user back press.*/
    private void showBackAlertDialog() {

        new AlertDialog.Builder(RevisionPlayerActivity.this)
                .setMessage(getString(R.string.close_alert_title))
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (mOngoingTopic != null) {
                            mPreviousTopic = mOngoingTopic;
                        }

                        if (mQuestionResponses != null && mQuestionResponses.size() > 0) {

                            if (mQuestionList != null && mQuestionList.size() > 0
                                    && mQuestionResponses.size() < mQuestionList.size()) {
                                mIsUserSelfExiting = true;
                            }

                            fetchQuestionsAndSubmitRevision();
                        } else {
                            finish();
                        }

                    }
                })
                .setNegativeButton(R.string.label_continue, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /*General method to show message in snackBar.*/
    private void showSnackBar(String message) {
        SnackBarUtils.showAlertSnackBar(this, mBinding.getRoot(), message);
    }

    @SuppressLint("CheckResult")
    private void fetchQuizAnalyticsConfiguration() {

        if (mRevisionAnalyticsConfigData == null || mRevisionAnalyticsConfigData.isEmpty()) {

            if (GeneralUtils.isNetworkAvailable(this)) {

                mPlayerModel.fetchQuizAnalyticsConfiguration()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<GlobalConfigurationParent>() {
                            @Override
                            public void accept(GlobalConfigurationParent configurationData) throws Exception {

                                if (configurationData != null
                                        && configurationData.getQuizAnalyticsConfig() != null
                                        && !configurationData.getQuizAnalyticsConfig().isEmpty()) {

                                    mRevisionAnalyticsConfigData = configurationData.getQuizAnalyticsConfig();

                                    showPracticeEndDialog(configurationData.getQuizAnalyticsConfig());

                                } else {

                                    showPracticeEndDialog(null);

                                }
                            }

                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                showPracticeEndDialog(null);
                            }
                        });

            } else {
                showInternetAlertDialog("QuizAnalyticsConfiguration");
            }
        } else {
            showPracticeEndDialog(mRevisionAnalyticsConfigData);
        }

    }

    public int pickColorAccording(int performance, ArrayList<ChartConfigurationData> quizAnalyticsConfigData) {

        for (int j = 0; j < quizAnalyticsConfigData.size(); j++) {
            ChartConfigurationData chartConfigurationData = quizAnalyticsConfigData.get(j);
            if (performance >= chartConfigurationData.getFrom() && performance <= chartConfigurationData.getTo()) {
                return Color.parseColor(chartConfigurationData.getColorCode());
            }
        }
        return R.color.colorRed;
    }

}
