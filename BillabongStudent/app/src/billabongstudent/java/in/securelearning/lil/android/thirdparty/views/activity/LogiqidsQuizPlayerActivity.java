package in.securelearning.lil.android.thirdparty.views.activity;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
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
import com.robinhood.ticker.TickerUtils;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;

import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationData;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.syncadapter.utils.CheckBoxCustom;
import in.securelearning.lil.android.syncadapter.utils.RadioButtonCustom;
import in.securelearning.lil.android.app.databinding.LayoutQuizEndBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizPlayerBinding;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.events.ChallengeForTheDayCompleteEvent;
import in.securelearning.lil.android.player.view.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.QuizResponse;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.FlyObjectAnimationUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import in.securelearning.lil.android.thirdparty.InjectorThirdParty;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsLoginResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptData;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsWorksheet;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsWorksheetResult;
import in.securelearning.lil.android.thirdparty.model.ThirdPartyModel;
import in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants;
import in.securelearning.lil.android.thirdparty.utils.ThirdPartyPrefs;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class LogiqidsQuizPlayerActivity extends AppCompatActivity {

    @Inject
    ThirdPartyModel mThirdPartyModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AppUserModel mAppUserModel;

    LayoutQuizPlayerBinding mBinding;

    private static final String LOGIQIDS_TOPIC_ID = "logiqidsTopicId";
    private static final String LOGIQIDS_TOPIC_NAME = "logiqidsTopicName";

    private ArrayList<QuestionResponse> mQuestionResponses = new ArrayList<>();
    private ArrayList<Question> mQuestionList = new ArrayList<>();
    private ArrayList<Attempt> mAttemptList = new ArrayList<>();

    private int mTotalCorrect = 0;
    private int mTotalInCorrect = 0;
    private int mHintCounter = 0;
    private int mQuestionCounter;
    private int mUiQuestionCounter;
    private int mTotalScore = 0;
    private int mStreak = 0;
    private int mLogiqidsUserId, mLogiqidsTopicId, mLogiqidsWorksheetId;
    private int mPointsPerQuestion, mMaxAttemptLimitPerQuestion, mUserAttemptPerQuestion;
    private long mTotalTimeAttempted = 0L;

    private String[] mCorrectMessages;
    private String[] mInCorrectMessages;
    private ProgressDialog mProgressDialog;
    private Question mQuestion;
    private String mCorrectChoice;
    private int mTotalQuestion;
    private boolean mIsWorksheetEnded;

    @Override
    public void onBackPressed() {
        showBackAlertDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorThirdParty.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_quiz_player);
        handleIntent();
        setUpResponseMessages();
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

    /**
     * @param context           context
     * @param logiQidsTopicName topicName to be displayed on Toolbar
     * @param logiqidsTopicId   topicId to fetch data
     * @return intent
     */
    public static Intent getStartIntent(Context context, String logiQidsTopicName, int logiqidsTopicId) {
        Intent intent = new Intent(context, LogiqidsQuizPlayerActivity.class);
        intent.putExtra(LOGIQIDS_TOPIC_NAME, logiQidsTopicName);
        intent.putExtra(LOGIQIDS_TOPIC_ID, logiqidsTopicId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /*Handle intent and get bundle data from intent*/
    private void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

//            mLogiqidsTopicId = getIntent().getIntExtra(LOGIQIDS_TOPIC_ID, 0);
            mLogiqidsTopicId = 5;
            String topicName = getIntent().getStringExtra(LOGIQIDS_TOPIC_NAME);
            setUpToolbar(topicName);

            if (mLogiqidsTopicId > 0) {
                loginToLogiqids();
            } else {
                GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
                finish();
            }

        } else {
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
            finish();
        }
    }

    /*Setup custom toolbar*/
    private void setUpToolbar(String quizTitle) {
        if (!TextUtils.isEmpty(quizTitle)) {
            mBinding.textViewToolbarTitle.setText(quizTitle);
        } else {
            mBinding.textViewToolbarTitle.setText(getString(R.string.quiz));
        }
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


        mBinding.layoutPoint.setVisibility(View.GONE);
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
        Question question = mQuestion;
        setDefaultsForNewQuestion();
        setQuestionCounter();
        setQuestionText(question);
        setHintView(question);
        setChoicesView(question);
        startTimer();
        setQuestionResource(question);
        initializeClickListeners(question);
        setMaxAttemptByQuestionAndLearningType(question);
        mUiQuestionCounter++;

    }

    /**
     * Multiple attempt - AFL - From Digital Book
     * Single attempt - AOL - From Homework, Recap/Lesson Plan.
     */
    private void setMaxAttemptByQuestionAndLearningType(Question question) {
        mMaxAttemptLimitPerQuestion = 1;
//        mMaxAttemptLimitPerQuestion = 0;
//        String questionType = question.getQuestionType();
//        if (mTypeOfLearning.equalsIgnoreCase(TYPE_ASSESSMENT_FOR_LEARNING)) {
//            switch (questionType) {
//                case Question.TYPE_DISPLAY_RADIO:
//                case Question.TYPE_DISPLAY_CHECKBOX:
//                case Question.TYPE_DISPLAY_TRUE_FALSE:
//                    mMaxAttemptLimitPerQuestion = (question.getQuestionChoices().size() - 1);
//                    break;
//                case Question.TYPE_DISPLAY_FILL_IN_THE_BLANKS:
//                    mMaxAttemptLimitPerQuestion = 1;
//                    break;
//                case Question.TYPE_DISPLAY_DROPDOWN:
//                    mMaxAttemptLimitPerQuestion = 1;
//                    break;
//            }
//        } else {
//            mMaxAttemptLimitPerQuestion = 1;
//        }
    }

    /*To enable click on link and identify link from click*/
    private void setMovementMethod() {
        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
                .setOnLinkClickListener(new BetterLinkMovementMethod.OnLinkClickListener() {
                    @Override
                    public boolean onClick(TextView textView, String url) {
                        if (url.contains("youtube")) {
                            mThirdPartyModel.startYoutubePlayer(getBaseContext(), url);
                            return true;
                        } else if (url.contains("vimeo")) {
                            mThirdPartyModel.startVimeoPlayer(getBaseContext(), url);
                            return true;
                        } else if (url.endsWith(".mp4") || url.endsWith(".MP4") ||
                                url.endsWith("wmv") || url.endsWith("WMV") ||
                                url.endsWith("flv") || url.endsWith("FLV")) {
                            mThirdPartyModel.startVideoPlayer(getBaseContext(), url);
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

        mBinding.includeLayoutTop.layoutTimer.setVisibility(View.VISIBLE);
        mBinding.includeLayoutTop.chronometerQuestionTimer.stop();
        mBinding.includeLayoutTop.chronometerQuestionTimer.setBase(SystemClock.elapsedRealtime());

        mHintCounter = 0;
        mBinding.layoutHints.removeAllViews();
        mBinding.cardViewHints.setVisibility(View.GONE);

        mUserAttemptPerQuestion = 0;
        mAttemptList.clear();

    }

    /*Set/Reset views of player after question attempt.*/
    private void setViewAfterQuestionAttempt() {

        mBinding.buttonDone.setText(getString(R.string.next));

//        if (isCorrectResponse) {
//            mBinding.chronometerQuestionTimer.stop();
//            if (mQuestionList.size() == mQuestionCounter) {
//                mBinding.buttonDone.setText(getString(R.string.finish));
//            } else {
//                mBinding.buttonDone.setText(getString(R.string.next));
//            }
//            hideHintLayouts();
//        } else {
//            if (mUserAttemptPerQuestion < mMaxAttemptLimitPerQuestion) {
//                mBinding.buttonDone.setText(getString(R.string.submit));
//                Question question = mQuestionList.get(mQuestionCounter - 1);
//                mBinding.layoutChoices.removeAllViews();
//                setChoicesView(question);
//            } else if (mQuestionList.size() == mQuestionCounter) {
//                mBinding.chronometerQuestionTimer.stop();
//                mBinding.buttonDone.setText(getString(R.string.finish));
//                hideHintLayouts();
//            } else {
//                mBinding.chronometerQuestionTimer.stop();
//                mBinding.buttonDone.setText(getString(R.string.next));
//                hideHintLayouts();
//            }
//        }

    }

    private void hideHintLayouts() {
        mBinding.cardViewHints.setVisibility(View.GONE);
        mBinding.includeLayoutTop.layoutHintButton.setVisibility(View.GONE);
    }

    private void initializeClickListeners(final Question question) {
        mBinding.includeLayoutTop.layoutHintButton.setOnClickListener(new View.OnClickListener() {
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
    }


    @SuppressLint("CheckResult")
    private void loginToLogiqids() {
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

            mThirdPartyModel.loginToLogiqids()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LogiqidsLoginResult>() {
                        @Override
                        public void accept(LogiqidsLoginResult logiqidsLoginResult) {

                            if (logiqidsLoginResult != null && logiqidsLoginResult.getData() != null) {
                                fetchWorksheetList(false);
                            } else {
                                showAlertDialog(getString(R.string.messageUnableToLoginOnLogiqids));
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            mProgressDialog.dismiss();
                            showAlertDialog(getString(R.string.messageUnableToLoginOnLogiqids));

                        }
                    });


        } else {
            showInternetAlertDialog(ConstantUtil.BLANK);
        }

    }

    /*Fetch list of questions for quiz*/
    @SuppressLint("CheckResult")
    private void fetchWorksheetList(final boolean isFromQuizEnd) {
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

            mThirdPartyModel.getWorksheetList(ThirdPartyPrefs.getLogiqidsUserId(getBaseContext()), mLogiqidsTopicId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LogiqidsWorksheetResult>() {
                        @Override
                        public void accept(LogiqidsWorksheetResult logiqidsWorksheetResult) {

                            if (logiqidsWorksheetResult != null
                                    && logiqidsWorksheetResult.getWorksheetList() != null) {

                                if (!logiqidsWorksheetResult.getWorksheetList().isEmpty() && !isFromQuizEnd) {



                                /*if data has only 1 item in list
                                * - status_id=0, user can start a new worksheet
                                  - status_id=1, user has an open worksheet*/
                                    if (logiqidsWorksheetResult.getWorksheetList().size() == 1) {
                                        LogiqidsWorksheet logiqidsWorksheet = logiqidsWorksheetResult.getWorksheetList().get(0);

                                        mLogiqidsWorksheetId = logiqidsWorksheet.getWorksheetId();
                                        int statusId = logiqidsWorksheet.getStatusId();

                                        if (statusId == ThirdPartyConstants.TYPE_WORKSHEET_STATUS_OPEN) {
                                            fetchLogiQidsQuestion(ThirdPartyPrefs.getLogiqidsUserId(getBaseContext()), mLogiqidsTopicId, mLogiqidsWorksheetId);
                                        }
                                    }
                                    /*if data has more then 1 items in list
                                     - status_id=0, user can start a new worksheet
                                     - status_id=1, user has an open worksheet*/
                                    else {
                                        LogiqidsWorksheet logiqidsWorksheet = new LogiqidsWorksheet();
                                        for (int i = 0; i < logiqidsWorksheetResult.getWorksheetList().size(); i++) {
                                            LogiqidsWorksheet logiqidsWorksheetInner = logiqidsWorksheetResult.getWorksheetList().get(i);
                                            if (logiqidsWorksheetInner.getStatusId() == ThirdPartyConstants.TYPE_WORKSHEET_STATUS_OPEN) {
                                                logiqidsWorksheet = logiqidsWorksheetInner;
                                                break;
                                            }
                                        }
                                        mLogiqidsWorksheetId = logiqidsWorksheet.getWorksheetId();
                                        int statusId = logiqidsWorksheet.getStatusId();

                                        if (statusId == ThirdPartyConstants.TYPE_WORKSHEET_STATUS_OPEN) {
                                            fetchLogiQidsQuestion(ThirdPartyPrefs.getLogiqidsUserId(getBaseContext()), mLogiqidsTopicId, mLogiqidsWorksheetId);
                                        } else if (statusId == ThirdPartyConstants.TYPE_WORKSHEET_STATUS_NEW) {

                                        }
                                    }
                                } else {
                                    /*If current topic's worksheet array is empty,
                                     * calling refresh event for updating Challenge for the day
                                     * on dashboard.*/
                                    mRxBus.send(new ChallengeForTheDayCompleteEvent());
                                }

                            } else {
                                showAlertDialog(getString(R.string.messageNoMoreQuestionFoundForThisWorksheet));
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
            showInternetAlertDialog(ConstantUtil.BLANK);
        }

    }

    @SuppressLint("CheckResult")
    private void fetchLogiQidsQuestion(int logiqidsUserId, int logiqidsTopicId, int logiqidsWorksheetId) {
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

            mThirdPartyModel.getQuestion(logiqidsUserId, logiqidsTopicId, logiqidsWorksheetId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Question>() {
                        @Override
                        public void accept(Question question) {
                            mProgressDialog.dismiss();

                            if (question != null) {

                                boolean isWorksheetEnded = question.isSelected();
                                if (isWorksheetEnded) {
                                    fetchWorksheetList(true);
                                    showAlertDialog("Congratulations! You have finished the worksheet");
                                } else {
                                    mQuestion = question;
                                    mTotalQuestion = question.getOrder();
                                    mUiQuestionCounter = Integer.parseInt(question.getComplexityLevel()) + 1;
                                    initializeQuestionUi();
                                }
                            } else {
                                showAlertDialog(getString(R.string.error_something_went_wrong));
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
            showInternetAlertDialog(ConstantUtil.BLANK);
        }

    }

    @SuppressLint("CheckResult")
    private void performQuestionAttempt(LogiqidsQuestionAttemptRequest logiqidsQuestionAttemptRequest) {
        if (GeneralUtils.isNetworkAvailable(this)) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, ConstantUtil.BLANK, getString(R.string.messageGettingAnswer), false);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onBackPressed();
                }
            });

            mThirdPartyModel.submitQuestionResponse(ThirdPartyPrefs.getLogiqidsUserId(getBaseContext()), mLogiqidsTopicId, mLogiqidsWorksheetId, logiqidsQuestionAttemptRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LogiqidsQuestionAttemptResult>() {
                        @Override
                        public void accept(LogiqidsQuestionAttemptResult logiqidsQuestionAttemptResult) {
                            progressDialog.dismiss();

                            if (logiqidsQuestionAttemptResult != null) {
                                LogiqidsQuestionAttemptData logiqidsQuestionAttemptData = logiqidsQuestionAttemptResult.getData();
                                mCorrectChoice = logiqidsQuestionAttemptData.getSolution().getAnswer();
                                getSubmittedAnswerFromView(true, mQuestion);
                                setResponseView(logiqidsQuestionAttemptData.isCorrect());
                                setViewAfterQuestionAttempt();
                                //showExplanation();

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

            mThirdPartyModel.submitQuestionResponse(ConstantUtil.INT_ZERO, ConstantUtil.INT_ZERO, ConstantUtil.INT_ZERO, null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LogiqidsQuestionAttemptResult>() {
                        @Override
                        public void accept(LogiqidsQuestionAttemptResult logiqidsQuestionAttemptResult) {
                            progressDialog.dismiss();
                            if (logiqidsQuestionAttemptResult != null) {
                                fetchQuizAnalyticsConfiguration(null);
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

        } else {
            showInternetAlertDialog("submitQuizResponse");
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
    }

    /*Start question timer when question initialize*/
    private void startTimer() {
        mBinding.includeLayoutTop.chronometerQuestionTimer.start();
    }

    /*Setup question counter view when question initialize*/
    private void setQuestionCounter() {
        AnimationUtils.fadeInFast(getBaseContext(), mBinding.scrollView);
        String counterText = "Question " + mUiQuestionCounter + "/" + mTotalQuestion;
        mBinding.includeLayoutTop.textViewQuestionCounter.setText(counterText);
    }


    /*Setup question text view when question initialize*/
    private void setQuestionText(Question question) {
        mBinding.textViewQuestion.setVisibility(View.VISIBLE);
        setMovementMethod();

        String questionText = question.getQuestionText();
        HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(mBinding.textViewQuestion);
        mBinding.textViewQuestion.setText(Html.fromHtml(TextViewMore.removeSpaceTags(questionText), htmlHttpImageGetter, new TextViewMore.UlTagHandler()));


    }

    /*Setup question resource view when question initialize*/
    private void setQuestionResource(Question question) {
        if (question.getResources() != null && !question.getResources().isEmpty()) {
            final String resourcePathImage = question.getResources().get(0).getUrlMain();
            if (!TextUtils.isEmpty(resourcePathImage)) {
                mBinding.layoutQuestionResource.setVisibility(View.VISIBLE);
                Picasso.with(getBaseContext()).load(resourcePathImage)
                        .placeholder(R.drawable.image_placeholder)
                        .fit().centerCrop()
                        .into(mBinding.imageViewResourceThumbnail);
                mBinding.imageViewResourceThumbnail.setTag(question.fetchQuestionImage().getUrlMain());
                mBinding.imageViewResourceType.setImageResource(R.drawable.action_image_w);
            }
//            else if (!TextUtils.isEmpty(resourcePathVideo)) {
//                mBinding.layoutQuestionResource.setVisibility(View.VISIBLE);
//                Picasso.with(getBaseContext()).load(resourcePathVideo).into(mBinding.imageViewResourceThumbnail);
//                mBinding.imageViewResourceThumbnail.setTag(question.fetchQuestionImage().getUrlMain());
//                mBinding.imageViewResourceType.setImageResource(R.drawable.action_video_w);
//            }
            else {
                mBinding.layoutQuestionResource.setVisibility(View.GONE);
            }

            mBinding.imageViewResourceThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showResource(resourcePathImage);
                }
            });
        } else {
            mBinding.layoutQuestionResource.setVisibility(View.GONE);

        }


    }

    /*Setup choice view when question initialize*/
    @SuppressLint("ClickableViewAccessibility")
    private void setChoicesView(final Question question) {

        String questionType = question.getQuestionType();
        ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();

        LayoutInflater layoutInflater = this.getLayoutInflater();

        mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            layoutInflater.inflate(R.layout.layout_response_mcq_single_correct, mBinding.layoutChoices);
            RadioGroup radioGroup = mBinding.layoutChoices.findViewById(R.id.radio_group_response);
            mBinding.layoutChoices.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
            mBinding.includeLayoutTop.textViewQuestionType.setText(getString(R.string.single_correct));
            for (final QuestionChoice questionChoice : questionChoices) {
                String thumbUrl = questionChoice.getChoiceResource().getUrlMain();
                final String mainUrl = questionChoice.getChoiceResource().getUrlMain();

                String choiceText = questionChoice.getChoiceText();
                layoutInflater.inflate(R.layout.layout_practice_response_item_mcq_single_correct, radioGroup);
                final RadioButtonCustom choice = (RadioButtonCustom) radioGroup.getChildAt(radioGroup.getChildCount() - 1);

                if (!TextUtils.isEmpty(choiceText)) {
                    HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(choice);
                    choice.setText(Html.fromHtml(TextViewMore.removeSpaceTags(questionChoice.getChoiceId() + ". " + choiceText), htmlHttpImageGetter, null));
                } else {
                    choice.setText(questionChoice.getChoiceId() + ".");
                }


                choice.setClickable(true);
                choice.setAlpha(1f);
                choice.setTag(questionChoice.getChoiceId());

                if (!TextUtils.isEmpty(thumbUrl) && !TextUtils.isEmpty(mainUrl)) {

                    Picasso.with(getBaseContext()).load(thumbUrl).into(choice);

                }

            }

        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {
            mBinding.layoutChoices.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
            mBinding.includeLayoutTop.textViewQuestionType.setText(getString(R.string.multiple_correct));
            for (final QuestionChoice questionChoice : questionChoices) {
                String thumbUrl = questionChoice.getChoiceResource().getUrlMain();
                String mainUrl = questionChoice.getChoiceResource().getUrlMain();


                String choiceText = questionChoice.getChoiceText();

                layoutInflater.inflate(R.layout.layout_practice_response_item_mcq_multiple_correct, mBinding.layoutChoices);
                CheckBoxCustom choice = (CheckBoxCustom) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(choice);
                choice.setText(Html.fromHtml(TextViewMore.removeSpaceTags(choiceText), htmlHttpImageGetter, null));
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

            }

        }


        setMovementMethod();

    }


    /*Setup hint view when question initialize*/
    private void setHintView(Question question) {

        if (question.getQuestionHints() != null && !question.getQuestionHints().isEmpty()) {
            int hintSize = question.getQuestionHints().size();

            String hintCounterText = "0/" + hintSize;
            mBinding.includeLayoutTop.textHintCounter.setText(hintCounterText);
            mBinding.includeLayoutTop.layoutHintButton.setVisibility(View.VISIBLE);
        } else {
            mBinding.includeLayoutTop.layoutHintButton.setVisibility(View.GONE);
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

                final TextView hintCounterTextView = layout.findViewById(R.id.text_view_hint_counter);
                String hintCounterText = currentHint + "/" + totalHints;
                hintCounterTextView.setText(hintCounterText);

                HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(hintTextView);
                hintTextView.setText(Html.fromHtml(TextViewMore.removeSpaceTags(questionHint.getHintText()), htmlHttpImageGetter, null));


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
            mBinding.includeLayoutTop.textHintCounter.setText(hintCounterText);
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

                mBinding.layoutExplanation.setVisibility(View.VISIBLE);
                mBinding.textViewExplanationText.setVisibility(View.VISIBLE);

                AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutExplanation);

                HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(mBinding.textViewExplanationText);
                mBinding.textViewExplanationText.setText(Html.fromHtml(TextViewMore.removeSpaceTags(explanationText), htmlHttpImageGetter, null));

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
                ArrayList<String> response = getSubmittedAnswerFromView(false, question);
                LogiqidsQuestionAttemptRequest logiqidsQuestionAttemptRequest = new LogiqidsQuestionAttemptRequest();
                logiqidsQuestionAttemptRequest.setMarkedChoice(response.get(0));
                logiqidsQuestionAttemptRequest.setQuestionId(Integer.parseInt(mQuestion.getUidQuestion()));
                performQuestionAttempt(logiqidsQuestionAttemptRequest);
//                mUserAttemptPerQuestion++;
//                Attempt attempt = new Attempt();
//                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));
//                boolean isCorrectResponse = mThirdPartyModel.checkCorrectness(question, attempt);
//
//                if (isCorrectResponse) {
//
//                    mTotalCorrect++;
//                    mStreak++;
//                    resetTotalInCorrectCounter();
//                    setStreakView(mTotalCorrect);
//                    addAttemptToList(attempt, true);
//                    setResponseView(true);
//                    showExplanation(question);
//
//                    generateQuestionResponse(Boolean.toString(true), question, mHintCounter, mAttemptList);
//
//                } else {
//
//                    if (mUserAttemptPerQuestion < mMaxAttemptLimitPerQuestion) {
//
//                        addAttemptToList(attempt, false);
//
//                    } else {
//
//                        mTotalInCorrect++;
//
//                        setStreakView(mTotalCorrect);
//                        showExplanation(question);
//
//                        addAttemptToList(attempt, false);
//
//                        generateQuestionResponse(Boolean.toString(false), question, mHintCounter, mAttemptList);
//
//                    }
//                    setResponseView(false);
//                    resetTotalCorrectCounter();
//
//                }
//
//                setViewAfterQuestionAttempt(isCorrectResponse);

            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.enter_response));
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
            mTotalTimeAttempted = SystemClock.elapsedRealtime() - mBinding.includeLayoutTop.chronometerQuestionTimer.getBase();
            attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.includeLayoutTop.chronometerQuestionTimer.getBase());
        } else {
            long time = SystemClock.elapsedRealtime() - mBinding.includeLayoutTop.chronometerQuestionTimer.getBase() - mTotalTimeAttempted;
            mTotalTimeAttempted = mTotalTimeAttempted + time;
            attempt.setTimeTaken(time);
        }
        attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
        attempt.setHintsAvailed(mHintCounter);

        mAttemptList.add(attempt);

    }

    /*Next button click action*/
    private void nextClickAction() {

        //   if (mQuestionList.size() > mQuestionCounter) {
        fetchLogiQidsQuestion(ThirdPartyPrefs.getLogiqidsUserId(getBaseContext()), mLogiqidsTopicId, mLogiqidsWorksheetId);
        mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
        //   }
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

        submitQuizResponse();

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
            //addPoints();


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

        startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), resourcePath, true));

//        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
//        if (mimeType != null) {
//            if (mimeType.contains("image")) {
//                startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), resourcePath, true));
//
//            } else if (mimeType.contains("video")) {
//                Resource item = new Resource();
//                item.setType("video");
//                item.setUrlMain(resourcePath);
//                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, item));
//            }
//        }


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
                    choiceResponses.add(String.valueOf(view.getTag()));
                }

                if (isSubmission) {
                    if (!TextUtils.isEmpty(mCorrectChoice)
                            && !mCorrectChoice.equalsIgnoreCase(String.valueOf(view.getTag()))) {
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
    private void showPracticeEndDialog(QuizResponse quizResponse, ArrayList<ChartConfigurationData> quizAnalyticsConfigData) {
        LayoutQuizEndBinding view = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_quiz_end, null, false);
        final Dialog dialog = new Dialog(LogiqidsQuizPlayerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(view.getRoot());
        setQuizDataOnEndDialog(view, quizResponse, quizAnalyticsConfigData);
        view.textViewGreetingMessage.setText(getString(R.string.quiz_end_response_submitted));
        view.lottie.setVisibility(View.GONE);

        view.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPointsToServer();
                dialog.dismiss();
                finish();
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

    private void setQuizDataOnEndDialog(LayoutQuizEndBinding endDialogBinding, QuizResponse quizResponse, ArrayList<ChartConfigurationData> quizAnalyticsConfigData) {

//        if (!TextUtils.isEmpty(mAppUserModel.getApplicationUser().getName())) {
//            endDialogBinding.textViewUserName.setText(mAppUserModel.getApplicationUser().getName());
//            endDialogBinding.textViewUserName.setVisibility(View.VISIBLE);
//        } else {
//            endDialogBinding.textViewUserName.setVisibility(View.GONE);
//        }
//
//        String score = ConstantUtil.BLANK_SPACE + mTotalScore + ConstantUtil.BLANK_SPACE;
//        endDialogBinding.textViewScore.setText(score);
//
//        int totalCorrectAnswers = 0;
//
//        long totalTimeTaken = 0;
//        for (int i = 0; i < mQuestionResponses.size(); i++) {
//
//            if (mQuestionResponses.get(i).getResponse().equalsIgnoreCase(Boolean.toString(true))) {
//                totalCorrectAnswers++;
//            }
//
//            if (!mQuestionResponses.get(i).getAttempts().isEmpty()) {
//
//                for (int j = 0; j < mQuestionResponses.get(i).getAttempts().size(); j++) {
//                    totalTimeTaken = totalTimeTaken + mQuestionResponses.get(i).getAttempts().get(j).getTimeTaken();
//                }
//
//            }
//        }
//
//        String questionCountString = totalCorrectAnswers + "/" + mQuestionList.size();
//        endDialogBinding.textViewQuestionsCount.setText(questionCountString);
//        endDialogBinding.textViewTotalTime.setText(CommonUtils.getInstance().showSecondAndMinutesFromLong(totalTimeTaken));
//
//        float correctAnsPercentage = (float) (totalCorrectAnswers * 100.00) / mQuestionList.size();
//        drawAccuracyChart(correctAnsPercentage, endDialogBinding, quizAnalyticsConfigData);
//
//        if (quizResponse.getQuizAttempts() <= 0) {
//            endDialogBinding.textViewAttempt.setVisibility(View.GONE);
//        } else {
//            endDialogBinding.textViewAttempt.setVisibility(View.VISIBLE);
//            if (quizResponse.getQuizAttempts() == 1) {
//                endDialogBinding.textViewAttempt.setText("No of attempt: 1");
//            } else {
//                endDialogBinding.textViewAttempt.setText("No of attempts: " + quizResponse.getQuizAttempts());
//            }
//        }

    }

    /*draw and set values for accuracy pie chart*/
    /*Get color form config */
    private void drawAccuracyChart(float correctAnsPercent, LayoutQuizEndBinding endDialogBinding, ArrayList<ChartConfigurationData> quizAnalyticsConfigData) {

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

                        if (callingFrom.equalsIgnoreCase("submitQuizResponse")) {
                            submitQuizResponse();
                        } else {
                            loginToLogiqids();

                        }
                    }
                })
                .show();
    }

    /*Show this dialog when user back press.*/
    private void showBackAlertDialog() {

        new AlertDialog.Builder(LogiqidsQuizPlayerActivity.this)
                .setMessage(getString(R.string.close_alert_title))
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mQuestionResponses != null && mQuestionResponses.size() > 0) {
                            submitQuizResponse();
                        } else {
                            finish();
                        }

                    }
                }).setNegativeButton(R.string.label_continue, new DialogInterface.OnClickListener() {
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
    private void fetchQuizAnalyticsConfiguration(final QuizResponse quizResponse) {
//        if (GeneralUtils.isNetworkAvailable(this)) {
//            mThirdPartyModel.getQuestion("", "", "").subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<LogiqidsQuestionResult>() {
//                        @Override
//                        public void accept(LogiqidsQuestionResult logiqidsQuestionResult) throws Exception {

//                            if (configurationData != null
//                                    && configurationData.getQuizAnalyticsConfig() != null
//                                    && !configurationData.getQuizAnalyticsConfig().isEmpty()) {
//                                showPracticeEndDialog(quizResponse, configurationData.getQuizAnalyticsConfig());
//                            } else {
//                                showPracticeEndDialog(quizResponse, null);
//                            }
//                        }
//
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            throwable.printStackTrace();
//                            showPracticeEndDialog(quizResponse, null);
//                        }
//                    });
//        } else {
//
//        }

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
