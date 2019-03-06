package in.securelearning.lil.android.quizpreview.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.ActivityQuizPreviewBinding;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.constants.QuizTypeEnum;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionExplanation;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.events.ErrorEvent;
import in.securelearning.lil.android.base.events.LoadAssignmentResponseQuizPreview;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.ImageUtils;
import in.securelearning.lil.android.base.utils.KeyBoardUtil;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.widget.ScratchPadView;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.quizpreview.events.AssignmentSubmittedEvent;
import in.securelearning.lil.android.quizpreview.model.QuizPreviewModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.base.utils.ImageUtils.getDrawableFromPath;
import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

public class QuizPreviewActivity extends BaseActivityQuizPreview implements QuizPreviewInterface {
    @Inject
    QuizPreviewModel mQuizPreviewModel;
    @Inject
    RxBus mRxBus;

    private ActivityQuizPreviewBinding mBinding;
    private Html.ImageGetter mImageGetter;
    private String mQuizDocumentId;
    private String mAssignmentResponseDocumentId;
    private Disposable mSubscription;
    private String mBaseFolder;
    private String mQuizResourceImageFolder;
    private String mQuizResourceVideoFolder;
    private String mQuizResourceAudioFolder;
    private String mQuizResourceDocumentFolder;
    private int mHintSize;
    private TextView mQuestionText;
    private TextView mQuizTitleTextView;
    private TextView mExplanationTextView;
    private TextView mExplanationlabelTextView;
    private RelativeLayout mExplanationImageLayout;
    private RelativeLayout mExplanationVideoLayout;
    private LinearLayout mExplanationLayout;
    private ImageButton mBackButton;
    private ImageButton mNextButton;
    private ImageButton mSkipButton;
    private View mPreviousButton;
    private ImageButton mSubmitButton;
    private Button mHintCountButton;
    private ViewGroup mResponseLayout;
    private ViewGroup mQuestionList;
    private View mHintLayoutView;
    private SlidingDrawer mScratchPadDrawer;
    private ViewGroup mHintGroupLayout;
    private ViewGroup mQuestionResourcesGroup;
    private RelativeLayout mHintIconImageView;
    private RelativeLayout mHintButtonLayout;
    private ImageView mQuestionImageView;
    private ImageView mQuestionVideoImageView;
    private ImageView mExplanationImageThumbnail;
    private ImageView mExplanationVideoThumbnail;
    private View mIncorrectSubmit;
    private View mCorrectSubmit;
    private Quiz mQuiz;
    private int mCounterQuestion = 0;
    private int mCounterHint = 0;
    private long mCurrentQuestionStartTime = 0L;
    private boolean mIsCorrectResponse = false;
    private int mResourceGroupVisibility = View.VISIBLE;
    private boolean mIsAttempt = true;
    private AssignmentResponse mAssignmentResponse;
    private String mAssignmentStudentDocId = "";
    private View.OnClickListener mShowResourceAction;
    private RelativeLayout res_type_image_layout;
    private RelativeLayout res_type_video_layout;
    private ScrollView mMainScrollView;
    private ImageButton clearScratchPad;
    private int SUBMIT_CLICKED = 1;
    private boolean mIsResponseSubmitted = false;

    public static void startQuizPreview(Context context, String assignmentResponseDocId, String assignmentStudentDocId, boolean isAttempt) {
        Intent intent = new Intent(context, QuizPreviewActivity.class);
        intent.putExtra("docIdAssignmentResponse", assignmentResponseDocId);
        intent.putExtra("docIdAssignmentStudent", assignmentStudentDocId);
        intent.putExtra("isAttempt", isAttempt);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (mIsAttempt && mAssignmentResponse.getQuizResponses().size() > 0) {
            if (mIsResponseSubmitted) {
                finish();
            } else {
                String message = "You have attempted " + String.valueOf(mAssignmentResponse.getQuizResponses().size()) + " out of " + String.valueOf(mQuiz.getQuestions().size()) + " questions.\nFor final submission, complete the assignment before " + DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(mAssignmentResponse.getAssignmentDueDate()));
                new android.app.AlertDialog.Builder(QuizPreviewActivity.this)
                        .setTitle(getString(R.string.close_alert_title))
                        .setMessage(message)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                QuizPreviewActivity.super.onBackPressed();
                            }
                        }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }

        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getQuizPreviewComponent().inject(this);
        setContentView(R.layout.activity_quiz_preview);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_preview);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        mBaseFolder = getFilesDir().getAbsolutePath();
        mQuizDocumentId = "";
        mAssignmentResponseDocumentId = "";
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mAssignmentResponseDocumentId = bundle.getString("docIdAssignmentResponse");
            mAssignmentStudentDocId = bundle.getString("docIdAssignmentStudent");
            mIsAttempt = bundle.getBoolean("isAttempt");
            Log.e("Preview", mAssignmentResponseDocumentId + " " + mIsAttempt);
        }


        initializeImageGetter();
        initializeViews();
        initializeUi();
        setupSubscription();

        if (!getResources().getBoolean(R.bool.isTablet)) {
            //ImageButton mSkipButton = (ImageButton) this.findViewById(R.id.imagebutton_show_explanation);
        }

        mQuizPreviewModel.fetchAssignmentResponseAndQuiz(mAssignmentResponseDocumentId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) mSubscription.dispose();
    }

    /**
     * initializes the views of the activity
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initializeViews() {
        mQuestionText = (TextView) this.findViewById(R.id.text_view_question);
        mQuizTitleTextView = (TextView) this.findViewById(R.id.textview_quiz_title_header);
        mExplanationTextView = (TextView) this.findViewById(R.id.textview_explanation);
        mSkipButton = (ImageButton) this.findViewById(R.id.imagebutton_skip);
        mExplanationlabelTextView = (TextView) this.findViewById(R.id.textview_explanation_label);
        mBackButton = (ImageButton) this.findViewById(R.id.imagebutton_exit_quiz);
        mNextButton = (ImageButton) this.findViewById(R.id.button_next);
        mPreviousButton = this.findViewById(R.id.button_previous);
        mSubmitButton = (ImageButton) this.findViewById(R.id.button_submit);
        mHintIconImageView = (RelativeLayout) this.findViewById(R.id.image_view_hint);
        mHintButtonLayout = (RelativeLayout) this.findViewById(R.id.layout_hint_button);
        mResponseLayout = (LinearLayout) this.findViewById(R.id.group_response);
        mQuestionList = (LinearLayout) this.findViewById(R.id.group_response_question_list);
        mExplanationVideoLayout = (RelativeLayout) this.findViewById(R.id.videothumbnail_explanation);
        mExplanationLayout = (LinearLayout) this.findViewById(R.id.layout_explanation);
        res_type_image_layout = (RelativeLayout) this.findViewById(R.id.res_type_image_layout);
        res_type_video_layout = (RelativeLayout) this.findViewById(R.id.res_type_video_layout);
        mQuestionImageView = (ImageView) this.findViewById(R.id.image_view_question_image);
        mHintGroupLayout = (ViewGroup) this.findViewById(R.id.group_response_hint);
        mHintLayoutView = this.findViewById(R.id.hintLayoutView);
        mQuestionVideoImageView = (ImageView) this.findViewById(R.id.image_view_question_video);
        mExplanationImageThumbnail = (ImageView) this.findViewById(R.id.imageview_explanation_image);
        mExplanationVideoThumbnail = (ImageView) this.findViewById(R.id.imageview_explanation_video);
        mExplanationImageLayout = (RelativeLayout) this.findViewById(R.id.imagethumbnail_explanation);
        mMainScrollView = (ScrollView) this.findViewById(R.id.scrollview_main_view);
        mHintCountButton = (Button) this.findViewById(R.id.button_hint_count);
        mCorrectSubmit = this.findViewById(R.id.group_correct_response);
        mIncorrectSubmit = this.findViewById(R.id.group_incorrect_response);
        mQuestionResourcesGroup = (ViewGroup) this.findViewById(R.id.group_question_resources);
        mScratchPadDrawer = (SlidingDrawer) this.findViewById(R.id.sliding_drawer_scratchpad);
        clearScratchPad = (ImageButton) mScratchPadDrawer.findViewById(R.id.image_view_clear_scratchpad);

    }

    private void initializeUi() {

        mShowResourceAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResource(v.getTag().toString());
            }
        };

        hideHintLayout();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsResponseSubmitted) {
                    finish();
                } else {
                    clickNextAction();
                }
            }
        });

        if (!mIsAttempt) {
            mNextButton.setVisibility(View.VISIBLE);
        }

        if (mIsAttempt) {
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clickSubmitAction();

                }
            });
        } else {
            mSubmitButton.setEnabled(false);
            mSubmitButton.setVisibility(View.GONE);
        }

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPreviousAction();
            }
        });

        if (mIsAttempt) {
            mPreviousButton.setVisibility(View.GONE);
        } else {
            mPreviousButton.setVisibility(View.VISIBLE);
        }
        mPreviousButton.setEnabled(false);

        mHintButtonLayout.setVisibility(View.VISIBLE);
        mHintButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextHint(true);
            }
        });

        mQuestionImageView.setOnClickListener(mShowResourceAction);

        mQuestionVideoImageView.setOnClickListener(mShowResourceAction);


        mScratchPadDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                //  mScratchPadDrawer.findViewById(R.id.content_scratchpad).setVisibility(View.VISIBLE);
                //mScratchPadDrawer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                // mQuestionResourcesGroup.setVisibility(View.INVISIBLE);
            }
        });
        mScratchPadDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                // mScratchPadDrawer.findViewById(R.id.content_scratchpad).setVisibility(View.INVISIBLE);
                //mScratchPadDrawer.setBackground(null);
                //hideSoftKeyboard();
                //  mQuestionResourcesGroup.setVisibility(mResourceGroupVisibility);
            }
        });

        clearScratchPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScratchPadView scratchPadView = (ScratchPadView) mScratchPadDrawer.findViewById(R.id.scratchpad_view_canvas);
                scratchPadView.clear();
            }
        });


    }

    private void initializeImageGetter() {

        mImageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                try {
                    if (!TextUtils.isEmpty(source)) {
                        return ImageUtils.getDrawableFromPath(getBaseContext().getResources(), source);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        };


    }

    /**
     * starts intialization of the quiz
     *
     * @param quiz
     */
    @Override
    public void initializeQuiz(Quiz quiz) {

        if (quiz != null && quiz.getQuestions() != null && quiz.getQuestions().size() > 0) {
            this.initializeResourceFolders(quiz.getMetaInformation().getSubject().getId());
            mQuiz = quiz;
            if (mIsAttempt && mAssignmentResponse != null && mAssignmentResponse.getQuizResponses() != null && mAssignmentResponse.getQuizResponses().size() > 0) {
                mCounterQuestion = mAssignmentResponse.getQuizResponses().size();
                if (TextUtils.isEmpty(mAssignmentResponse.getQuizResponses().get(mCounterQuestion - 1).getResponse())) {
                    mCounterQuestion = mCounterQuestion - 1;
                }
                if (mCounterQuestion <= 0) {
                    mCounterQuestion = 0;
                    loadQuestionList(0);

                } else {
                    loadQuestionList(mCounterQuestion - 1);

                }

            } else {
                mCounterQuestion = 0;
                loadQuestionList(0);
            }

            loadQuestion(mCounterQuestion);
            mQuizTitleTextView.setText(mQuiz.getTitle());

        }
    }

    /**
     * loads the number list of the questions at the bottom of the screen
     */
    @Override
    public void loadQuestionList(int size) {

        LayoutInflater layoutInflater = this.getLayoutInflater();
        int noOfQuestions = mQuiz.getQuestions().size();
        if (mIsAttempt) {
            for (int i = 0; i <= size; i++) {
                int status = 0;
                try {
                    status = mAssignmentResponse.getQuizResponses().get(i).getAttempts().get(0).getStatusCode();
                } catch (Exception e) {
                    e.printStackTrace();

                }

                layoutInflater.inflate(R.layout.layout_response_question_list_item, mQuestionList);
                TextView textView = (TextView) mQuestionList.getChildAt(i);
                textView.setText(Integer.toString(i + 1));
                if (status == 0) {
                    textView.setBackgroundResource(R.drawable.background_correct_question);
                } else if (status == 1) {
                    textView.setBackgroundResource(R.drawable.background_incorrect_question);
                } else {
                    textView.setBackgroundResource(R.drawable.background_unanswered_question);
                }
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            }

            for (int i = size + 1; i < noOfQuestions; i++) {
                layoutInflater.inflate(R.layout.layout_response_question_list_item, mQuestionList);
                TextView textView = (TextView) mQuestionList.getChildAt(i);
                textView.setText(Integer.toString(i + 1));
                textView.setBackgroundResource(R.drawable.background_yet_to_view_question);
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorGreyDark));
            }
        } else {
            if (mQuiz.getQuizType().equals(QuizTypeEnum.SUBJECTIVE.toString())) {
                for (int i = 0; i < noOfQuestions; i++) {
                    layoutInflater.inflate(R.layout.layout_response_question_list_item, mQuestionList);
                    TextView textView = (TextView) mQuestionList.getChildAt(i);
                    textView.setText(Integer.toString(i + 1));
                    textView.setBackgroundResource(R.drawable.background_unanswered_question);
                    textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                }
            } else {
                for (int i = 0; i < noOfQuestions; i++) {
                    int status = 0;
                    try {
                        status = mAssignmentResponse.getQuizResponses().get(i).getAttempts().get(0).getStatusCode();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    layoutInflater.inflate(R.layout.layout_response_question_list_item, mQuestionList);
                    TextView textView = (TextView) mQuestionList.getChildAt(i);
                    textView.setText(Integer.toString(i + 1));
                    if (status == 0) {
                        textView.setBackgroundResource(R.drawable.background_correct_question);
                    } else if (status == 1) {
                        textView.setBackgroundResource(R.drawable.background_incorrect_question);
                    } else {
                        textView.setBackgroundResource(R.drawable.background_unanswered_question);
                    }
                    textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                }
            }
        }

    }




    /**
     * loads the question and its resource thumbnails
     *
     * @param counter = question number being shown
     */
    @Override
    public void loadQuestion(int counter) {
        mMainScrollView.fullScroll(HorizontalScrollView.FOCUS_UP);
        mCounterHint = 0;
        mHintSize = mQuiz.getQuestions().get(mCounterQuestion).getQuestionHints().size();
        mHintCountButton.setText(Integer.toString(mHintSize).trim());

        if (mHintSize == 0) {
            mHintButtonLayout.setVisibility(View.GONE);
        } else {
            mHintButtonLayout.setVisibility(View.VISIBLE);
        }

        if (!mIsAttempt && mCounterQuestion == 0 && mQuiz.getQuizType().equalsIgnoreCase(QuizTypeEnum.OBJECTIVE.toString())) {
            final int mExplanationType = mQuiz.getQuestions().get(0).getChoiceConfiguration().getQuestionExplanation().getExplanationType();
            showExplanationView(mExplanationType);

        } else if (!mIsAttempt && mCounterQuestion > 0 && mQuiz.getQuizType().equalsIgnoreCase(QuizTypeEnum.OBJECTIVE.toString())) {
            final int mExplanationType = mQuiz.getQuestions().get(mCounterQuestion).getChoiceConfiguration().getQuestionExplanation().getExplanationType();
            showExplanationView(mExplanationType);
        }

        Question question = mQuiz.getQuestions().get(counter);
        String questionText = TextViewMore.stripHtml((mCounterQuestion + 1) + ". " + question.getQuestionText());
        mQuestionText.setText(questionText);
        mQuestionText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyDark));
        String resourcePathImage = question.fetchQuestionImage().getDeviceURL();
        if (!TextUtils.isEmpty(resourcePathImage)) {
//            resourcePathImage = mBaseFolder + File.separator + resourcePathImage;
            mQuestionImageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePathImage));
            mQuestionImageView.setTag(resourcePathImage);
            mQuestionImageView.setVisibility(View.VISIBLE);
            res_type_image_layout.setVisibility(View.VISIBLE);

        } else {
            mQuestionImageView.setVisibility(View.GONE);
            res_type_image_layout.setVisibility(View.GONE);
        }

        String resourcePathVideo = question.fetchQuestionVideo().getDeviceURL();

        if (!TextUtils.isEmpty(resourcePathVideo)) {
//            resourcePathVideo = mBaseFolder + File.separator + resourcePathVideo;
            mQuestionVideoImageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePathVideo));
            mQuestionVideoImageView.setTag(resourcePathVideo);
            mQuestionVideoImageView.setVisibility(View.VISIBLE);
            res_type_video_layout.setVisibility(View.VISIBLE);
        } else {
            mQuestionVideoImageView.setVisibility(View.GONE);
            res_type_video_layout.setVisibility(View.GONE);

        }

        if (mQuestionVideoImageView.getVisibility() != View.VISIBLE && mQuestionImageView.getVisibility() != View.VISIBLE) {
            mResourceGroupVisibility = View.INVISIBLE;
        } else {
            mResourceGroupVisibility = View.VISIBLE;
        }
        mQuestionResourcesGroup.setVisibility(mResourceGroupVisibility);

        loadResponseView();

        if (mIsAttempt) {

            //resetCurrentQuestionStartTime();
        }

        if (!mIsAttempt) {
            if (mQuiz.getQuestions().size() == 1) {
                mNextButton.setImageResource(R.drawable.next_g);
                mSubmitButton.setVisibility(View.GONE);
                mPreviousButton.setVisibility(View.GONE);
            }
            if (mQuiz.getQuestions().size() > 1) {
                if (counter == 0) {
                    mPreviousButton.setVisibility(View.GONE);
                } else {
                    mPreviousButton.setVisibility(View.VISIBLE);
                }
            }

            if (mAssignmentResponse != null && mAssignmentResponse.getQuizResponses() != null
                    && mAssignmentResponse.getQuizResponses().size() > counter
                    && mAssignmentResponse.getQuizResponses().get(counter) != null
                    && mAssignmentResponse.getQuizResponses().get(counter).getAttempts().size() > 0) {
                Attempt attempt = mAssignmentResponse.getQuizResponses().get(counter).getAttempts().get(0);
                mHintButtonLayout.setVisibility(View.GONE);
                int totalHints = question.getQuestionHints().size();
                int usedHints = mAssignmentResponse.getQuizResponses().get(counter).getHintUsed();
                int unusedHints = totalHints - usedHints;
                long timeTaken = attempt.getTimeTaken();

                mBinding.textViewTimeTaken.setText(DateUtils.convertSecondToHourMinuteSecond(timeTaken));

                if (usedHints == 0) {
                    mBinding.layoutHintUsed.setVisibility(View.GONE);
                } else {
                    mBinding.layoutHintUsed.setVisibility(View.VISIBLE);
                    mBinding.layoutAllHint.setVisibility(View.GONE);
                    showUsedHints(usedHints, question);
                }
                if (unusedHints == 0) {
                    mBinding.layoutHintUnused.setVisibility(View.GONE);
                } else {
                    mBinding.layoutHintUnused.setVisibility(View.VISIBLE);
                    mBinding.layoutAllHint.setVisibility(View.GONE);
                    showUnusedHints(usedHints, question);
                }

            } else {
                mBinding.textViewTimeTaken.setVisibility(View.GONE);
                mBinding.layoutHintUsed.setVisibility(View.GONE);
                mBinding.layoutHintUnused.setVisibility(View.GONE);
                mHintButtonLayout.setVisibility(View.GONE);
                mBinding.layoutAllHint.setVisibility(View.VISIBLE);
                showAllHints(0, question);

            }

        } else {
            mQuestionList.getChildAt(mCounterQuestion).setBackgroundResource(R.drawable.background_unanswered_question);
            ((TextView) mQuestionList.getChildAt(mCounterQuestion)).setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            if (mAssignmentResponse != null && mAssignmentResponse.getQuizResponses().size() > mCounterQuestion && TextUtils.isEmpty(mAssignmentResponse.getQuizResponses().get(counter).getResponse())) {
                Attempt attempt = mAssignmentResponse.getQuizResponses().get(counter).getAttempts().get(0);
                int usedHints = mAssignmentResponse.getQuizResponses().get(counter).getHintUsed();
                long timeTaken = attempt.getTimeTaken();
                resetCurrentQuestionStartTime(timeTaken);
                for (int i = 0; i < usedHints; i++) {
                    showNextHint(false);
                }

//                if (usedHints == 0) {
//                    mBinding.layoutAllHint.setVisibility(View.GONE);
//                } else if (usedHints == totalHints) {
//                    mBinding.layoutAllHint.setVisibility(View.VISIBLE);
//                    mHintButtonLayout.setVisibility(View.GONE);
//                    showAllHints(usedHints, question);
//                } else {
//                    mBinding.layoutHintUsed.setVisibility(View.GONE);
//                    mBinding.layoutHintUnused.setVisibility(View.GONE);
//                    mBinding.layoutAllHint.setVisibility(View.VISIBLE);
//                    showAllHints(usedHints, question);
//                    mHintCountButton.setText(String.valueOf(unusedHints));
//                }
//                if (unusedHints == 0) {
//                    mBinding.layoutHintUnused.setVisibility(View.GONE);
//                } else {
//                    mBinding.layoutHintUnused.setVisibility(View.VISIBLE);
//                    mBinding.layoutAllHint.setVisibility(View.GONE);
//                    showUnusedHints(usedHints, question);
//                }
            } else {
                resetCurrentQuestionStartTime(0);
            }

        }
    }

    private void showUsedHints(int usedHints, Question question) {
        int totalHints = question.getQuestionHints().size();
        if (usedHints <= totalHints) {
            for (int i = 0; i < usedHints; i++) {
                QuestionHint questionHint = question.getQuestionHints().get(i);
                String mTotalHint = String.valueOf(totalHints);
                int mFinalCounterHint = i + 1;
                String mCurrentHint = String.valueOf(mFinalCounterHint);
                if (!questionHint.getHintText().isEmpty()) {
                    this.getLayoutInflater().inflate(R.layout.layout_response_hint_text, mBinding.layoutDrawUsedHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawUsedHints.getChildAt(mBinding.layoutDrawUsedHints.getChildCount() - 1);
                    TextView mHintTextView = (TextView) mLayout.findViewById(R.id.text_view_hint);

                    final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    if (mFinalCounterHint >= 10) {
                        mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                    } else {
                        mHintCounterTextView.setPadding(8, 0, 8, 0);
                    }
                    mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                    String hintSpanned = TextViewMore.stripHtml(questionHint.getHintText());
                    mHintTextView.setText(hintSpanned);
                    mHintTextView.setMaxLines(Integer.MAX_VALUE);
                    mHintTextView.setVerticalScrollBarEnabled(true);
                    mHintTextView.setMovementMethod(new ScrollingMovementMethod());
                } else {
                    String resourcePath = /*mBaseFolder + File.separator + */questionHint.getHintResource().getDeviceURL();
                    String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                    if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                        this.getLayoutInflater().inflate(R.layout.layout_response_hint_image_thumbnail, mBinding.layoutDrawUsedHints);
                        LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawUsedHints.getChildAt(mBinding.layoutDrawUsedHints.getChildCount() - 1);
                        final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                        if (mFinalCounterHint >= 10) {
                            mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                        } else {
                            mHintCounterTextView.setPadding(8, 0, 8, 0);
                        }
                        mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                        RelativeLayout mImageThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                        ImageView imageView = ((ImageView) mImageThumbnailLayout.findViewById(R.id.imageview_hint_image));
                        imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                        imageView.setOnClickListener(mShowResourceAction);
                        imageView.setTag(resourcePath);

                    } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                        this.getLayoutInflater().inflate(R.layout.layout_response_hint_video_thumbnail, mBinding.layoutDrawUsedHints);
                        LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawUsedHints.getChildAt(mBinding.layoutDrawUsedHints.getChildCount() - 1);
                        final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                        if (mFinalCounterHint >= 10) {
                            mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                        } else {
                            mHintCounterTextView.setPadding(8, 0, 8, 0);
                        }
                        mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                        RelativeLayout mVideoThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                        ImageView imageView = ((ImageView) mVideoThumbnailLayout.findViewById(R.id.imageview_hint_video));
                        imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                        imageView.setOnClickListener(mShowResourceAction);
                        imageView.setTag(resourcePath);

                    }

                }
            }
        }
    }

    private void showUnusedHints(int usedHints, Question question) {
        int totalHints = question.getQuestionHints().size();
        if (usedHints <= totalHints) {
            for (int i = usedHints; i < totalHints; i++) {
                QuestionHint questionHint = question.getQuestionHints().get(i);
                String mTotalHint = String.valueOf(totalHints);
                int mFinalCounterHint = i + 1;
                String mCurrentHint = String.valueOf(mFinalCounterHint);
                if (!questionHint.getHintText().isEmpty()) {
                    this.getLayoutInflater().inflate(R.layout.layout_response_hint_text, mBinding.layoutDrawUnusedHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawUnusedHints.getChildAt(mBinding.layoutDrawUnusedHints.getChildCount() - 1);
                    TextView mHintTextView = (TextView) mLayout.findViewById(R.id.text_view_hint);

                    final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    if (mFinalCounterHint >= 10) {
                        mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                    } else {
                        mHintCounterTextView.setPadding(8, 0, 8, 0);
                    }
                    mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                    String hintSpanned = TextViewMore.stripHtml(questionHint.getHintText());
                    mHintTextView.setText(hintSpanned);
                    mHintTextView.setMaxLines(Integer.MAX_VALUE);
                    mHintTextView.setVerticalScrollBarEnabled(true);
                    mHintTextView.setMovementMethod(new ScrollingMovementMethod());
                } else {
                    String resourcePath = /*mBaseFolder + File.separator + */questionHint.getHintResource().getDeviceURL();
                    String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                    if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                        this.getLayoutInflater().inflate(R.layout.layout_response_hint_image_thumbnail, mBinding.layoutDrawUnusedHints);
                        LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawUnusedHints.getChildAt(mBinding.layoutDrawUnusedHints.getChildCount() - 1);
                        final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                        if (mFinalCounterHint >= 10) {
                            mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                        } else {
                            mHintCounterTextView.setPadding(8, 0, 8, 0);
                        }
                        mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                        RelativeLayout mImageThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                        ImageView imageView = ((ImageView) mImageThumbnailLayout.findViewById(R.id.imageview_hint_image));
                        imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                        imageView.setOnClickListener(mShowResourceAction);
                        imageView.setTag(resourcePath);

                    } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                        this.getLayoutInflater().inflate(R.layout.layout_response_hint_video_thumbnail, mBinding.layoutDrawUnusedHints);
                        LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawUnusedHints.getChildAt(mBinding.layoutDrawUnusedHints.getChildCount() - 1);
                        final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                        if (mFinalCounterHint >= 10) {
                            mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                        } else {
                            mHintCounterTextView.setPadding(8, 0, 8, 0);
                        }
                        mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                        RelativeLayout mVideoThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                        ImageView imageView = ((ImageView) mVideoThumbnailLayout.findViewById(R.id.imageview_hint_video));
                        imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                        imageView.setOnClickListener(mShowResourceAction);
                        imageView.setTag(resourcePath);

                    }

                }
            }
        }

    }

    private void showAllHints(int usedHints, Question question) {
        int totalHints = question.getQuestionHints().size();
        if (usedHints <= totalHints) {
            for (int i = 0; i < usedHints; i++) {
                QuestionHint questionHint = question.getQuestionHints().get(i);
                String mTotalHint = String.valueOf(totalHints);
                int mFinalCounterHint = i + 1;
                String mCurrentHint = String.valueOf(mFinalCounterHint);
                if (!questionHint.getHintText().isEmpty()) {
                    this.getLayoutInflater().inflate(R.layout.layout_response_hint_text, mBinding.layoutDrawAllHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawAllHints.getChildAt(mBinding.layoutDrawAllHints.getChildCount() - 1);
                    TextView mHintTextView = (TextView) mLayout.findViewById(R.id.text_view_hint);

                    final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    if (mFinalCounterHint >= 10) {
                        mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                    } else {
                        mHintCounterTextView.setPadding(8, 0, 8, 0);
                    }
                    mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                    String hintSpanned = TextViewMore.stripHtml(questionHint.getHintText());
                    mHintTextView.setText(hintSpanned);
                    mHintTextView.setMaxLines(Integer.MAX_VALUE);
                    mHintTextView.setVerticalScrollBarEnabled(true);
                    mHintTextView.setMovementMethod(new ScrollingMovementMethod());
                } else {
                    String resourcePath = /*mBaseFolder + File.separator + */questionHint.getHintResource().getDeviceURL();
                    String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                    if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                        this.getLayoutInflater().inflate(R.layout.layout_response_hint_image_thumbnail, mBinding.layoutDrawAllHints);
                        LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawAllHints.getChildAt(mBinding.layoutDrawAllHints.getChildCount() - 1);
                        final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                        if (mFinalCounterHint >= 10) {
                            mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                        } else {
                            mHintCounterTextView.setPadding(8, 0, 8, 0);
                        }
                        mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                        RelativeLayout mImageThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                        ImageView imageView = ((ImageView) mImageThumbnailLayout.findViewById(R.id.imageview_hint_image));
                        imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                        imageView.setOnClickListener(mShowResourceAction);
                        imageView.setTag(resourcePath);

                    } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                        this.getLayoutInflater().inflate(R.layout.layout_response_hint_video_thumbnail, mBinding.layoutDrawAllHints);
                        LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawAllHints.getChildAt(mBinding.layoutDrawAllHints.getChildCount() - 1);
                        final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                        if (mFinalCounterHint >= 10) {
                            mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                        } else {
                            mHintCounterTextView.setPadding(8, 0, 8, 0);
                        }
                        mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                        RelativeLayout mVideoThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                        ImageView imageView = ((ImageView) mVideoThumbnailLayout.findViewById(R.id.imageview_hint_video));
                        imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                        imageView.setOnClickListener(mShowResourceAction);
                        imageView.setTag(resourcePath);

                    }

                }
            }
        } else if (usedHints == 0) {
            for (int i = 0; i < totalHints; i++) {
                QuestionHint questionHint = question.getQuestionHints().get(i);
                String mTotalHint = String.valueOf(totalHints);
                int mFinalCounterHint = i + 1;
                String mCurrentHint = String.valueOf(mFinalCounterHint);
                if (!questionHint.getHintText().isEmpty()) {
                    this.getLayoutInflater().inflate(R.layout.layout_response_hint_text, mBinding.layoutDrawAllHints);
                    LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawAllHints.getChildAt(mBinding.layoutDrawAllHints.getChildCount() - 1);
                    TextView mHintTextView = (TextView) mLayout.findViewById(R.id.text_view_hint);

                    final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    if (mFinalCounterHint >= 10) {
                        mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                    } else {
                        mHintCounterTextView.setPadding(8, 0, 8, 0);
                    }
                    mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                    String hintSpanned = TextViewMore.stripHtml(questionHint.getHintText());
                    mHintTextView.setText(hintSpanned);
                    mHintTextView.setMaxLines(Integer.MAX_VALUE);
                    mHintTextView.setVerticalScrollBarEnabled(true);
                    mHintTextView.setMovementMethod(new ScrollingMovementMethod());
                } else {
                    String resourcePath = /*mBaseFolder + File.separator + */questionHint.getHintResource().getDeviceURL();
                    String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                    if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                        this.getLayoutInflater().inflate(R.layout.layout_response_hint_image_thumbnail, mBinding.layoutDrawAllHints);
                        LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawAllHints.getChildAt(mBinding.layoutDrawAllHints.getChildCount() - 1);
                        final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                        if (mFinalCounterHint >= 10) {
                            mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                        } else {
                            mHintCounterTextView.setPadding(8, 0, 8, 0);
                        }
                        mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                        RelativeLayout mImageThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                        ImageView imageView = ((ImageView) mImageThumbnailLayout.findViewById(R.id.imageview_hint_image));
                        imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                        imageView.setOnClickListener(mShowResourceAction);
                        imageView.setTag(resourcePath);

                    } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                        this.getLayoutInflater().inflate(R.layout.layout_response_hint_video_thumbnail, mBinding.layoutDrawAllHints);
                        LinearLayout mLayout = (LinearLayout) mBinding.layoutDrawAllHints.getChildAt(mBinding.layoutDrawAllHints.getChildCount() - 1);
                        final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                        if (mFinalCounterHint >= 10) {
                            mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                        } else {
                            mHintCounterTextView.setPadding(8, 0, 8, 0);
                        }
                        mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                        RelativeLayout mVideoThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                        ImageView imageView = ((ImageView) mVideoThumbnailLayout.findViewById(R.id.imageview_hint_video));
                        imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                        imageView.setOnClickListener(mShowResourceAction);
                        imageView.setTag(resourcePath);

                    }

                }
            }
        }


    }

    /**
     * reset question time
     */
    private void resetCurrentQuestionStartTime(long offsetSeconds) {
        mCurrentQuestionStartTime = DateUtils.getCurrentTimeInSeconds() - offsetSeconds;
    }

    /**
     * populates the response section with appropriate choices
     */
    @Override
    public void loadResponseView() {

        Question question = mQuiz.getQuestions().get(mCounterQuestion);
        String questionType = question.getQuestionType();
        boolean isChoiceTypeImage = question.getChoiceTypeImage();

        mResponseLayout.removeAllViews();

        LayoutInflater layoutInflater = this.getLayoutInflater();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)) {
            ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();
            layoutInflater.inflate(R.layout.layout_response_mcq_single_correct, mResponseLayout);
            RadioGroup radioGroup = (RadioGroup) mResponseLayout.findViewById(R.id.radio_group_response);

            if (isChoiceTypeImage) {
                char choiceNumber = 'A';
                for (final QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_response_item_mcq_single_correct, radioGroup);
                    RadioButton choice = (RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
                    choice.setClickable(mIsAttempt);
                    choice.setAlpha(1f);
                    choice.setText(choiceNumber + ". ");
                    if (!mIsAttempt && questionChoice.isChoiceCorrect()) {
                        choice.setTextColor(ContextCompat.getColor(this, R.color.colorGreenDark));

                    } else {
                        choice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyDark));

                    }
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
                    choiceNumber++;
                }
            } else {
                char choiceNumber = 'A';
                for (QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_response_item_mcq_single_correct, radioGroup);
                    RadioButton choice = (RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
                    choice.setClickable(mIsAttempt);
                    choice.setAlpha(1f);
                    String choiceText = TextViewMore.stripHtml(choiceNumber + ". " + questionChoice.getChoiceText());
                    choice.setText(choiceText);
                    if (!mIsAttempt && questionChoice.isChoiceCorrect()) {
                        choice.setTextColor(ContextCompat.getColor(this, R.color.colorGreenDark));
                        choice.setTextSize(18);
                        choice.setTypeface(null, Typeface.BOLD);
                    } else {
                        choice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyDark));
                    }
                    choice.setTag(questionChoice.isChoiceCorrect());
                    choiceNumber++;
                }
            }
            if (!mIsAttempt) {
                int i = 0;
                try {
                    i = Integer.valueOf(mAssignmentResponse.getQuizResponses().get(mCounterQuestion).getAttempts().get(0).getSubmittedAnswer().get(0));
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                    radioButton.setChecked(true);
                    if (mAssignmentResponse.getQuizResponses().get(mCounterQuestion).getAttempts().get(0).getStatusCode() == 1) {
                        radioButton.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {
            ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();

            if (isChoiceTypeImage) {
                char choiceNumber = 'A';
                for (final QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_response_item_mcq_multiple_correct, mResponseLayout);
                    CheckBox choice = (CheckBox) mResponseLayout.getChildAt(mResponseLayout.getChildCount() - 1);
                    choice.setClickable(mIsAttempt);
                    choice.setAlpha(1f);
                    choice.setText(choiceNumber + ". ");
                    if (!mIsAttempt && questionChoice.isChoiceCorrect()) {
                        choice.setTextColor(ContextCompat.getColor(this, R.color.colorGreenDark));
                    } else {
                        choice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyDark));
                    }
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
                    choiceNumber++;
                }
            } else {
                char choiceNumber = 'A';
                for (QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_response_item_mcq_multiple_correct, mResponseLayout);
                    CheckBox choice = (CheckBox) mResponseLayout.getChildAt(mResponseLayout.getChildCount() - 1);
                    choice.setClickable(mIsAttempt);
                    choice.setAlpha(1f);
                    String choiceText = TextViewMore.stripHtml(choiceNumber + ". " + questionChoice.getChoiceText());
                    choice.setText(choiceText);
                    if (!mIsAttempt && questionChoice.isChoiceCorrect()) {
                        choice.setTextColor(ContextCompat.getColor(this, R.color.colorGreenDark));
                        choice.setTextSize(18);
                        choice.setTypeface(null, Typeface.BOLD);
                    } else {
                        choice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyDark));
                    }
                    choice.setTag(questionChoice.isChoiceCorrect());
                    choiceNumber++;
                }
            }
            if (!mIsAttempt) {
                for (String s : mAssignmentResponse.getQuizResponses().get(mCounterQuestion).getAttempts().get(0).getSubmittedAnswer()) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(s);
                        CheckBox checkBox = (CheckBox) mResponseLayout.getChildAt(i);
                        checkBox.setChecked(true);
                        checkBox.setClickable(false);
                        checkBox.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } else if (questionType.equalsIgnoreCase(Question.TYPE_QUESTION_SUBJECTIVE)) {
            layoutInflater.inflate(R.layout.layout_response_subjective, mResponseLayout);
            EditText editText = (EditText) mResponseLayout.findViewById(R.id.edit_text_response_subjective);

            if (!mIsAttempt) {
                String response = "";
                try {
                    response = mAssignmentResponse.getQuizResponses().get(mCounterQuestion).getAttempts().get(0).getSubmittedAnswer().get(0);
                } catch (Exception e) {

                }
                editText.setText(response);
                editText.setEnabled(false);
                editText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyDark));
                editText.setAlpha(1.0f);
            }
        }
    }

    /**
     * prepares the dialog box to show the resource
     *
     * @param resourcePath
     */
    @Override
    public void showResource(String resourcePath) {

        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
        if (mimeType != null) {
            if (mimeType.contains("image")) {
                ArrayList<String> pathArrayList = new ArrayList<>();
                pathArrayList.add("file://" + resourcePath);
                FullScreenImage.setUpFullImageView(this, 0, false,true, FullScreenImage.getResourceArrayList(pathArrayList));

            } else if (mimeType.contains("video")) {

                Resource item = new Resource();
                item.setType("video");
                item.setUrlMain(resourcePath);
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
            }
        }


    }

    /**
     * shows the next_white hint
     *
     * @param save
     */
    @Override
    public void showNextHint(boolean save) {
        KeyBoardUtil.hideSoftKeyboard(getCurrentFocus(), this);
        showHintLayout();

        if (mHintCountButton.getText().toString().trim().equals("0")) {
            ToastUtils.showToastAlert(QuizPreviewActivity.this, getResources().getString(R.string.no_hint));
            mHintCountButton.setEnabled(false);
            mHintCountButton.setClickable(false);
        } else {

            int remainingHints = mHintSize - mCounterHint;
            mHintCountButton.setText(String.valueOf(remainingHints - 1));
            mHintCountButton.setEnabled(true);
            mHintCountButton.setClickable(true);
        }

        if (mCounterHint < mQuiz.getQuestions().get(mCounterQuestion).getQuestionHints().size()) {
            QuestionHint questionHint = mQuiz.getQuestions().get(mCounterQuestion).getQuestionHints().get(mCounterHint);
            String mTotalHint = String.valueOf(mHintSize);
            int mFinalCounterHint = mCounterHint + 1;
            String mCurrentHint = String.valueOf(mFinalCounterHint);
            if (!questionHint.getHintText().isEmpty()) {
                this.getLayoutInflater().inflate(R.layout.layout_response_hint_text, mHintGroupLayout);
                LinearLayout mLayout = (LinearLayout) mHintGroupLayout.getChildAt(mHintGroupLayout.getChildCount() - 1);
                TextView mHintTextView = (TextView) mLayout.findViewById(R.id.text_view_hint);

                final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                if (mFinalCounterHint >= 10) {
                    mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                } else {
                    mHintCounterTextView.setPadding(8, 0, 8, 0);
                }
                mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                String hintSpanned = TextViewMore.stripHtml(questionHint.getHintText());
                mHintTextView.setText(hintSpanned);
                mHintTextView.setMaxLines(Integer.MAX_VALUE);
                mHintTextView.setVerticalScrollBarEnabled(true);
                mHintTextView.setMovementMethod(new ScrollingMovementMethod());
            } else {
                String resourcePath = /*mBaseFolder + File.separator*/ questionHint.getHintResource().getDeviceURL();
                String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                    this.getLayoutInflater().inflate(R.layout.layout_response_hint_image_thumbnail, mHintGroupLayout);
                    LinearLayout mLayout = (LinearLayout) mHintGroupLayout.getChildAt(mHintGroupLayout.getChildCount() - 1);
                    final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    if (mFinalCounterHint >= 10) {
                        mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                    } else {
                        mHintCounterTextView.setPadding(8, 0, 8, 0);
                    }
                    mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                    RelativeLayout mImageThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                    ImageView imageView = ((ImageView) mImageThumbnailLayout.findViewById(R.id.imageview_hint_image));
                    imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                    imageView.setOnClickListener(mShowResourceAction);
                    imageView.setTag(resourcePath);

                } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                    this.getLayoutInflater().inflate(R.layout.layout_response_hint_video_thumbnail, mHintGroupLayout);
                    LinearLayout mLayout = (LinearLayout) mHintGroupLayout.getChildAt(mHintGroupLayout.getChildCount() - 1);
                    final TextView mHintCounterTextView = (TextView) mLayout.findViewById(R.id.text_view_hint_counter);
                    if (mFinalCounterHint >= 10) {
                        mHintCounterTextView.setPadding(0 - 4, 0, 8, 0);
                    } else {
                        mHintCounterTextView.setPadding(8, 0, 8, 0);
                    }
                    mHintCounterTextView.setText(mCurrentHint + "/" + mTotalHint);
                    RelativeLayout mVideoThumbnailLayout = (RelativeLayout) mLayout.getChildAt(mLayout.getChildCount() - 1);
                    ImageView imageView = ((ImageView) mVideoThumbnailLayout.findViewById(R.id.imageview_hint_video));
                    imageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), resourcePath));
                    imageView.setOnClickListener(mShowResourceAction);
                    imageView.setTag(resourcePath);

                }

            }
            mCounterHint++;
            if (save) {
                Attempt attempt = new Attempt();
                attempt.setTimeTaken(DateUtils.getCurrentTimeInSeconds() - mCurrentQuestionStartTime);
                updateCurrentQuestionResponse(attempt, "", mCounterHint);
            }
        }
    }

    private void updateCurrentQuestionResponse(Attempt attempt, String response, int counterHint) {
        Question question = mQuiz.getQuestions().get(mCounterQuestion);
        if (mAssignmentResponse.getQuizResponses().size() <= mCounterQuestion) {
            QuestionResponse quizResponse = new QuestionResponse();
            quizResponse.setOrder(mCounterQuestion);
            quizResponse.setQid(question.getUidQuestion());
            mAssignmentResponse.getQuizResponses().add(mCounterQuestion, quizResponse);
        }

        QuestionResponse quizResponse = mAssignmentResponse.getQuizResponses().get(mCounterQuestion);
        quizResponse.setHintUsed(counterHint);
        attempt.setHintsAvailed(counterHint);
        if (quizResponse.getAttempts().size() <= 0) {
            quizResponse.addAttempt(attempt);
        } else {
            quizResponse.getAttempts().set(0, attempt);
        }

        if (!TextUtils.isEmpty(response)) {
            quizResponse.setResponse(response);
        }
        mQuizPreviewModel.saveAssignmentResponseLocally(mAssignmentResponse);
    }


    /**
     * get response object from view data
     *
     * @return
     */
    @Override
    public ArrayList<String> getSubmittedAnswerFromView() {

        String questionType = mQuiz.getQuestions().get(mCounterQuestion).getQuestionType();
        ArrayList<String> responseString = new ArrayList<>();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)) {
            RadioGroup radioGroup = (RadioGroup) mResponseLayout.findViewById(R.id.radio_group_response);
            int count = radioGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                if (((RadioButton) radioGroup.getChildAt(i)).isChecked()) {
                    responseString.add(String.valueOf(i));
                }
            }
        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {
            int count = mResponseLayout.getChildCount();

            for (int i = 0; i < count; i++) {
                if (((CheckBox) mResponseLayout.getChildAt(i)).isChecked()) {
                    responseString.add(String.valueOf(i));
                }
            }


        } else if (questionType.equalsIgnoreCase(Question.TYPE_QUESTION_SUBJECTIVE)) {
            EditText editText = (EditText) mResponseLayout.findViewById(R.id.edit_text_response_subjective);
            if (editText.getText() != null && !editText.getText().toString().isEmpty()) {
                responseString.add(editText.getText().toString());
            }

        }

        return responseString;
    }

    /**
     * action for submit click
     */
    @Override
    public void clickSubmitAction() {
        KeyBoardUtil.hideSoftKeyboard(getCurrentFocus(), this);
        if (mIsAttempt) {

            Attempt attempt = new Attempt();
            attempt.setSubmittedAnswer(getSubmittedAnswerFromView());
            int errorType = mQuizPreviewModel.validateAttempt(attempt);

            if (errorType == QuizPreviewModel.VALIDATION_NO_ERROR) {
                long currentQuestionEndTime = DateUtils.getCurrentTimeInSeconds();
                mSubmitButton.setVisibility(View.GONE);
                mNextButton.setVisibility(View.VISIBLE);

                final int mExplanationType = mQuiz.getQuestions().get(0).getChoiceConfiguration().getQuestionExplanation().getExplanationType();

                showExplanationView(mExplanationType);

                if (mCounterQuestion == mQuiz.getQuestions().size() - 1) {
                    mNextButton.setImageResource(R.drawable.exit_g);

                } else {
                    mNextButton.setImageResource(R.drawable.next_g);
                }
                Question question = mQuiz.getQuestions().get(mCounterQuestion);
                if (!question.getQuestionType().equalsIgnoreCase(Question.TYPE_QUESTION_SUBJECTIVE)) {
                    mIsCorrectResponse = mQuizPreviewModel.checkCorrectness(question, attempt);

                    if (mIsCorrectResponse) {
                        attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                        mCorrectSubmit.setVisibility(View.VISIBLE);
                        AnimationUtils.zoomIn(getBaseContext(), mCorrectSubmit);
                        SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_CORRECT_ANSWER);
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                mCorrectSubmit.setVisibility(View.GONE);
                            }
                        }, 2000);

                        mIncorrectSubmit.setVisibility(View.GONE);
                        mQuestionList.getChildAt(mCounterQuestion).setBackgroundResource(R.drawable.background_correct_question);

                        if (!mIsAttempt && mCounterQuestion == 0 && mQuiz.getQuizType().equalsIgnoreCase(QuizTypeEnum.OBJECTIVE.toString())) {

                            showExplanationView(mExplanationType);

                        } else if (!mIsAttempt && mCounterQuestion > 0 && mQuiz.getQuizType().equalsIgnoreCase(QuizTypeEnum.OBJECTIVE.toString())) {

                            showExplanationView(mExplanationType);

                        }

                    } else {
                        attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                        mCorrectSubmit.setVisibility(View.GONE);
                        mIncorrectSubmit.setVisibility(View.VISIBLE);
                        AnimationUtils.zoomIn(getBaseContext(), mIncorrectSubmit);
                        SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_INCORRECT_ANSWER);
                        Handler h = new Handler();

                        h.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                mIncorrectSubmit.setVisibility(View.GONE);
                            }
                        }, 2000);
                        mQuestionList.getChildAt(mCounterQuestion).setBackgroundResource(R.drawable.background_incorrect_question);

                        showExplanationView(mExplanationType);


                    }

                    View choiceView = mResponseLayout.getChildAt(0);
                    if (choiceView instanceof RadioGroup) {
                        for (int i = 0; i < ((RadioGroup) choiceView).getChildCount(); i++) {
                            RadioButton radioButton = (RadioButton) ((RadioGroup) choiceView).getChildAt(i);
                            if (!radioButton.isChecked())
                                radioButton.setEnabled(false);
                        }
                    } else {
                        for (int i = 0; i < mResponseLayout.getChildCount(); i++) {
                            CheckBox checkBox = (CheckBox) mResponseLayout.getChildAt(i);
                            checkBox.setClickable(false);
                        }
                    }

                }

                attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(currentQuestionEndTime));
                attempt.setHintsAvailed(mCounterHint);
                attempt.setTimeTaken(currentQuestionEndTime - mCurrentQuestionStartTime);
                updateCurrentQuestionResponse(attempt, Boolean.toString(mIsCorrectResponse), mCounterHint);

                if (mCounterQuestion == mQuiz.getQuestions().size() - 1) {
                    submitResponse();
                }

            } else {
                showErrorMessage(errorType);
            }
        } else {

        }
    }

    /**
     * shows error message according to the error code
     *
     * @param errorType
     */
    @Override
    public void showErrorMessage(int errorType) {
        switch (errorType) {
            case QuizPreviewModel.VALIDATION_ERROR1:
                ToastUtils.showToastAlert(getApplicationContext(), getString(R.string.enter_response));
                mMainScrollView.fullScroll(HorizontalScrollView.FOCUS_UP);
                break;
        }
    }

    /**
     * show explanations if device type is tablet
     *
     * @param mExplanationType
     */

    private void showExplanationView(int mExplanationType) {

        if (mExplanationType == QuestionExplanation.TYPE_EXPLANATION_TEXT) {
            String mExplanationText = mQuiz.getQuestions().get(mCounterQuestion).getChoiceConfiguration().getQuestionExplanation().getExplanationText();
            if (!TextUtils.isEmpty(mExplanationText)) {
                mExplanationlabelTextView.setVisibility(View.VISIBLE);
                mExplanationLayout.setVisibility(View.VISIBLE);
                mExplanationTextView.setVisibility(View.VISIBLE);
                mExplanationImageLayout.setVisibility(View.GONE);
                mExplanationVideoLayout.setVisibility(View.GONE);
                String explanationText = TextViewMore.stripHtml(mExplanationText);
                mExplanationTextView.setText(explanationText);
                mExplanationTextView.setVerticalScrollBarEnabled(true);
                mExplanationTextView.setMovementMethod(new ScrollingMovementMethod());
            }

        } else if (mExplanationType == QuestionExplanation.TYPE_EXPLANATION_RESOURCE) {
            mExplanationLayout.setVisibility(View.VISIBLE);
            mExplanationTextView.setVisibility(View.GONE);
            String mResourcePath = /*mBaseFolder + File.separator*/ mQuiz.getQuestions().get(mCounterQuestion).getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getDeviceURL();
            String mimeType = URLConnection.guessContentTypeFromName(mResourcePath);
            if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                mExplanationlabelTextView.setVisibility(View.VISIBLE);
                mExplanationImageLayout.setVisibility(View.VISIBLE);
                mExplanationVideoLayout.setVisibility(View.GONE);
                Bitmap mBitmap = getScaledBitmapFromPath(this.getResources(), mResourcePath);
                Drawable mDrawable = new BitmapDrawable(getResources(), mBitmap);
                mExplanationImageThumbnail.setBackground(mDrawable);
                mExplanationImageThumbnail.setOnClickListener(mShowResourceAction);
                mExplanationImageThumbnail.setTag(mResourcePath);
            } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                mExplanationlabelTextView.setVisibility(View.VISIBLE);
                mExplanationVideoLayout.setVisibility(View.VISIBLE);
                mExplanationImageLayout.setVisibility(View.GONE);
                Bitmap mBitmap = getScaledBitmapFromPath(this.getResources(), mResourcePath);
                Drawable mDrawable = new BitmapDrawable(getResources(), mBitmap);
                mExplanationVideoThumbnail.setBackground(mDrawable);
                mExplanationVideoThumbnail.setOnClickListener(mShowResourceAction);
                mExplanationVideoThumbnail.setTag(mResourcePath);
            }
        } else {

            mExplanationlabelTextView.setVisibility(View.GONE);
        }

    }

    /**
     * show explanation of the question if screen type is phone in popup
     *
     * @param mExplanationType
     */
    private void showExplanationInPopup(int mExplanationType) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.layout_show_explanation_popup_phone, null);
        TextView mPopupExplanationText = (TextView) layout.findViewById(R.id.textview_explanation);
        RelativeLayout mPopupExplanationImageThumbnailLayout = (RelativeLayout) layout.findViewById(R.id.imagethumbnail_explanation);
        RelativeLayout mPopupExplanationVideoThumbnailLayout = (RelativeLayout) layout.findViewById(R.id.videothumbnail_explanation);
        ImageView mPopupExplanantionImageThumbnail = (ImageView) layout.findViewById(R.id.imageview_explanation_image);
        ImageView mPopupExplanantionVideoThumbnail = (ImageView) layout.findViewById(R.id.imageview_explanation_video);
        ImageButton mPopupExplanantionClose = (ImageButton) layout.findViewById(R.id.imagebutton_close_explanation_popup);

        if (mExplanationType == QuestionExplanation.TYPE_EXPLANATION_TEXT) {
            mPopupExplanationText.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
            mPopupExplanationText.setVisibility(View.VISIBLE);
            mPopupExplanationImageThumbnailLayout.setVisibility(View.GONE);
            mPopupExplanationVideoThumbnailLayout.setVisibility(View.GONE);
            String mExplanationText = mQuiz.getQuestions().get(mCounterQuestion).getChoiceConfiguration().getQuestionExplanation().getExplanationText();
            mPopupExplanationText.setText(mExplanationText);
            mPopupExplanationText.setVerticalScrollBarEnabled(true);
            mPopupExplanationText.setMovementMethod(new ScrollingMovementMethod());
        } else if (mExplanationType == QuestionExplanation.TYPE_EXPLANATION_RESOURCE) {
            layout.setVisibility(View.VISIBLE);
            mPopupExplanationText.setVisibility(View.GONE);
            String mResourcePath = /*mBaseFolder + File.separator*/ mQuiz.getQuestions().get(mCounterQuestion).getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getDeviceURL();
            String mimeType = URLConnection.guessContentTypeFromName(mResourcePath);
            if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                mPopupExplanationText.setVisibility(View.VISIBLE);
                mPopupExplanationImageThumbnailLayout.setVisibility(View.VISIBLE);
                mPopupExplanationVideoThumbnailLayout.setVisibility(View.GONE);
                Bitmap mBitmap = getScaledBitmapFromPath(this.getResources(), mResourcePath);
                Drawable mDrawable = new BitmapDrawable(getResources(), mBitmap);
                mPopupExplanantionImageThumbnail.setBackground(mDrawable);
                mPopupExplanantionImageThumbnail.setOnClickListener(mShowResourceAction);
                mPopupExplanantionImageThumbnail.setTag(mResourcePath);
            } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                mPopupExplanationText.setVisibility(View.VISIBLE);
                mPopupExplanationVideoThumbnailLayout.setVisibility(View.VISIBLE);
                mPopupExplanationImageThumbnailLayout.setVisibility(View.GONE);
                Bitmap mBitmap = getScaledBitmapFromPath(this.getResources(), mResourcePath);
                Drawable mDrawable = new BitmapDrawable(getResources(), mBitmap);
                mPopupExplanantionVideoThumbnail.setBackground(mDrawable);
                mPopupExplanantionVideoThumbnail.setOnClickListener(mShowResourceAction);
                mPopupExplanantionVideoThumbnail.setTag(mResourcePath);
            }
        } else {

            mPopupExplanationText.setVisibility(View.GONE);
        }


        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int popupWidth = displayMetrics.widthPixels * 1 / 2;
        int popupHeight = displayMetrics.heightPixels - 40;
        final PopupWindow popup = new PopupWindow(QuizPreviewActivity.this);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        int OFFSET_X = 0;
        int OFFSET_Y = 0;
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setOutsideTouchable(true);
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        popup.showAtLocation(layout, Gravity.RIGHT, OFFSET_X, OFFSET_Y);

        mPopupExplanantionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });

    }

    /**
     * action to take when next_white button is pressed
     * if it is the last question then preview is closed
     * else next_white question is shown
     */
    @Override
    public void clickNextAction() {
        KeyBoardUtil.hideSoftKeyboard(getCurrentFocus(), this);

        if (mIsAttempt) {
            if (mCounterQuestion < mQuiz.getQuestions().size() - 1) {
                mCounterQuestion++;
                mSubmitButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.GONE);
                mResponseLayout.removeAllViews();
                hideHintLayout();
                mHintGroupLayout.removeAllViews();

                mCorrectSubmit.setVisibility(View.GONE);
                mIncorrectSubmit.setVisibility(View.GONE);
                mExplanationLayout.setVisibility(View.GONE);
                loadQuestion(mCounterQuestion);

            } else {
                finish();
                //   submitResponse();

            }
        } else {
            if (mCounterQuestion < mQuiz.getQuestions().size() - 1) {
                mCounterQuestion++;
                mPreviousButton.setEnabled(true);
                mResponseLayout.removeAllViews();
                mHintGroupLayout.removeAllViews();
                mBinding.layoutDrawUsedHints.removeAllViews();
                mBinding.layoutDrawUnusedHints.removeAllViews();
                hideHintLayout();

                mCorrectSubmit.setVisibility(View.GONE);
                mIncorrectSubmit.setVisibility(View.GONE);
                mExplanationLayout.setVisibility(View.GONE);
                loadQuestion(mCounterQuestion);

            } else {
                finish();
            }
        }
    }

    /**
     * action to take when previous button is clicked
     */
    @Override
    public void clickPreviousAction() {
        if (!mIsAttempt) {
            if (mCounterQuestion > 0) {
                mCounterQuestion--;
                if (mCounterQuestion == 0) mPreviousButton.setEnabled(false);
                if (mCounterQuestion == mQuiz.getQuestions().size() - 1) {
                    // mNextButton.setText(getResources().getString(R.string.close));
                } else {
                    //mNextButton.setText(getResources().getString(R.string.next));
                }
                mResponseLayout.removeAllViews();
                mHintGroupLayout.removeAllViews();
                mBinding.layoutDrawUsedHints.removeAllViews();
                mBinding.layoutDrawUnusedHints.removeAllViews();
                hideHintLayout();
                mCorrectSubmit.setVisibility(View.GONE);
                mIncorrectSubmit.setVisibility(View.GONE);
                mExplanationLayout.setVisibility(View.GONE);
                loadQuestion(mCounterQuestion);

            }
        }
    }

    /**
     * hide hint layout
     */
    private void hideHintLayout() {
        mHintLayoutView.setVisibility(View.GONE);
    }

    /**
     * show hint layout
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showHintLayout() {
        mMainScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMainScrollView.fullScroll(HorizontalScrollView.FOCUS_DOWN);
            }
        }, 50);

        mHintLayoutView.setVisibility(View.VISIBLE);
    }

    /**
     * submit question response
     */
    private void submitResponse() {
        if (mQuiz.getQuizType().equals(QuizTypeEnum.SUBJECTIVE.toString())) {
            mAssignmentResponse.setStage(AssignmentStage.STAGE_SUBMITTED.getAssignmentStage());
        } else {
            mAssignmentResponse = generateScoreNew(mAssignmentResponse, mQuiz);
            mAssignmentResponse.setStage(AssignmentStage.STAGE_GRADED.getAssignmentStage());
        }

        mAssignmentResponse.setSubmissionDateTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        if (mAssignmentResponse.getSubmittedBy() == null) {
            mAssignmentResponse.setSubmittedBy(mQuizPreviewModel.getSubmittedBy());
        }
        if (TextUtils.isEmpty(mAssignmentResponse.getSubmittedBy().getObjectId())) {
            mAssignmentResponse.setSubmittedBy(mQuizPreviewModel.getSubmittedBy());
        }
        mAssignmentResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        mQuizPreviewModel.saveAssignmentResponse(mAssignmentResponse);
        mQuizPreviewModel.changeAssignmentStudent(mAssignmentResponse, mAssignmentStudentDocId);

        mRxBus.send(new AssignmentSubmittedEvent(mAssignmentResponse));
        SyncService.startActionUploadAssignmentResponse(getBaseContext(), mAssignmentResponse.getObjectId());
        mIsResponseSubmitted = true;
    }

    private AssignmentResponse generateScoreNew(AssignmentResponse assignmentResponse, Quiz quiz) {
        double sum = 0;
        final int HINT_DEDUCTION_PERCENTAGE = 20;
        if (assignmentResponse.getTotalScore() > 0) {
            final double MAX_MARKS_FOR_QUESTION = assignmentResponse.getTotalScore() / quiz.getQuestions().size();
            for (QuestionResponse quizResponse : assignmentResponse.getQuizResponses()) {
                if (quizResponse.getResponse().equals(String.valueOf(true))) {
                    int hintsDeduction = 0;
                    for (int hintindex = 0; hintindex < quizResponse.getHintUsed(); hintindex++) {
                        hintsDeduction += (HINT_DEDUCTION_PERCENTAGE * MAX_MARKS_FOR_QUESTION) / 100;
                    }
                    int attemptsDeduction = 0;
                    if (quizResponse.getAttempts().size() > 1) {
                        for (int attemptIndex = 1; attemptIndex < quizResponse.getAttempts().size(); attemptIndex++) {
                            attemptsDeduction += (HINT_DEDUCTION_PERCENTAGE * MAX_MARKS_FOR_QUESTION) / 100;
                        }
                    }
                    quizResponse.setMarksObtained(MAX_MARKS_FOR_QUESTION - hintsDeduction - attemptsDeduction);
                    if (quizResponse.getMarksObtained() < 0) {
                        quizResponse.setMarksObtained(0);
                    }
                } else {
                    quizResponse.setMarksObtained(0);
                }
                sum += quizResponse.getMarksObtained();
            }
            sum = Math.round(sum * 100) / 100;
            assignmentResponse.setAssignmentScore(sum);
        }
        return assignmentResponse;
    }

    /**
     * set up Disposable to listen to RxBus
     */
    @Override
    public void setupSubscription() {

        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.newThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object eventObject) throws Exception {
                if (eventObject instanceof LoadAssignmentResponseQuizPreview) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAssignmentResponse = ((LoadAssignmentResponseQuizPreview) eventObject).getAssignmentResponse();
                            initializeQuiz(((LoadAssignmentResponseQuizPreview) eventObject).getQuiz());

                        }
                    });

                } else if (eventObject instanceof ErrorEvent) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog(((ErrorEvent) eventObject).getmMessage());
                        }
                    });

                }
            }
        });
    }

    /**
     * show the error dialog
     *
     * @param messageString
     */
    public void showErrorDialog(String messageString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage(messageString);
        builder.show();
    }

    /**
     * initialize the folders for resources
     *
     * @param parentFolderAbsolutePath
     */
    @Override
    public void initializeResourceFolders(String parentFolderAbsolutePath) {
        mQuizResourceImageFolder = getFilesDir() + File.separator + parentFolderAbsolutePath + File.separator + "images";
        mQuizResourceVideoFolder = getFilesDir() + File.separator + parentFolderAbsolutePath + File.separator + "videos";
        mQuizResourceAudioFolder = getFilesDir() + File.separator + parentFolderAbsolutePath + File.separator + "TextDocPdf";
        mQuizResourceDocumentFolder = getFilesDir() + File.separator + parentFolderAbsolutePath + File.separator + "audio";
    }


}
