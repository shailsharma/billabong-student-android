package in.securelearning.lil.android.thirdparty.views.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutBottomSheetDialogBinding;
import in.securelearning.lil.android.app.databinding.LayoutMindSparkPracticePlayerBinding;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import in.securelearning.lil.android.thirdparty.InjectorThirdParty;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkContentDetails;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionContentData;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionParent;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionSubmit;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionTrial;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionTypeBlank;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionTypeDropdown;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionTypeMCQ;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionUserAttempt;
import in.securelearning.lil.android.thirdparty.model.ThirdPartyModel;
import in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants;
import in.securelearning.lil.android.thirdparty.utils.ThirdPartyPrefs;
import in.securelearning.lil.android.thirdparty.views.adapter.DropdownChoiceAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.base.utils.ImageUtils.getDrawableFromPath;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.ACTION_START;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.MODE_LEARN;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.MS_LANGUAGE_CODE;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.MS_VERSION_ID_SUFFIX;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.TYPE_BLANK;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.TYPE_DROPDOWN;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.TYPE_MCQ;

public class MindSparkPlayerActivity extends AppCompatActivity {

    LayoutMindSparkPracticePlayerBinding mBinding;
    private final static String TOPIC_ID = "topicId";
    private final static String TOPIC_NAME = "topicName";
    @Inject
    ThirdPartyModel mThirdPartyModel;

    private String mTopicId;
    private int mHintsSize = 0;
    private int mHintCounter = 0;

    @Override
    public void onBackPressed() {
        showBackAlertDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorThirdParty.INSTANCE.getComponent().inject(this);
        CommonUtils.getInstance().setImmersiveStatusBar(getWindow());
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_mind_spark_practice_player);
        handleIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context, String topicId, String topicName) {
        Intent intent = new Intent(context, MindSparkPlayerActivity.class);
        intent.putExtra(TOPIC_ID, topicId);
        intent.putExtra(TOPIC_NAME, topicName);
        return intent;
    }


    private void handleIntent() {
        if (getIntent() != null) {
            mTopicId = getIntent().getStringExtra(TOPIC_ID);
            String topicName = getIntent().getStringExtra(TOPIC_NAME);
            setUpToolbar(topicName);
            fetchQuestion();
        }
    }

    private void setUpToolbar(String topicName) {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.action_close_w);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(topicName);
    }


    @SuppressLint("CheckResult")
    private void fetchQuestion() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            final Dialog dialog = CommonUtils.getInstance().loadingDialog(MindSparkPlayerActivity.this, getString(R.string.messagePleaseWait));
            MindSparkQuestionRequest mindSparkQuestionRequest = new MindSparkQuestionRequest();
            mindSparkQuestionRequest.setTopicId(mTopicId);
            mindSparkQuestionRequest.setMode(MODE_LEARN);
            mindSparkQuestionRequest.setAction(ACTION_START);
            mThirdPartyModel.getMindSparkQuestion(mindSparkQuestionRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MindSparkQuestionParent>() {
                        @Override
                        public void accept(MindSparkQuestionParent mindSparkQuestionParent) throws Exception {
                            dialog.dismiss();
                            if (mindSparkQuestionParent.getRedirectionData() != null && mindSparkQuestionParent.getRedirectionData().isEndTopicFlag()) {
                                exitConfirmationDialog(getString(R.string.messageMSEndTopic));
                            } else {
                                MindSparkQuestionContentData mindSparkQuestionContentData = mindSparkQuestionParent.getContentData();
                                if (mindSparkQuestionContentData.getQuestions().get(0).getQuestionResponse() != null) {
                                    Question question = mThirdPartyModel.convertMSQuestionToLILQuestion(mindSparkQuestionContentData.getContentSeqNum(), mindSparkQuestionContentData.getContentId(), mindSparkQuestionContentData.getQuestions().get(0));
                                    initializeQuestionView(question);
                                } else {
                                    exitConfirmationDialog(getString(R.string.mindSparkNoUnitMessageList));
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            dialog.dismiss();
                            /*Showing fix message right now because rx stopping to move further response code check.*/
                            retryDialog(getString(R.string.mindSparkNoUnitMessageList), null);
                            throwable.printStackTrace();
                        }
                    });
        } else {
            retryDialog(getString(R.string.error_message_no_internet), null);
        }
    }

    @SuppressLint("CheckResult")
    private void submitAndFetchNewQuestion(final Question question) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            final Dialog dialog = CommonUtils.getInstance().loadingDialog(MindSparkPlayerActivity.this, getString(R.string.messageGettingNextQuestion));
            mThirdPartyModel.submitAndFetchNewQuestion(prepareSubmitResponse(question))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MindSparkQuestionParent>() {
                        @Override
                        public void accept(MindSparkQuestionParent mindSparkQuestionParent) throws Exception {
                            dialog.dismiss();
                            if (mindSparkQuestionParent.getRedirectionData() != null && mindSparkQuestionParent.getRedirectionData().isEndTopicFlag()) {
                                exitConfirmationDialog(getString(R.string.messageMSEndTopic));
                            } else {
                                MindSparkQuestionContentData mindSparkQuestionContentData = mindSparkQuestionParent.getContentData();
                                if (mindSparkQuestionContentData.getQuestions().get(0).getQuestionResponse() != null) {
                                    Question question = mThirdPartyModel.convertMSQuestionToLILQuestion(mindSparkQuestionContentData.getContentSeqNum(), mindSparkQuestionContentData.getContentId(), mindSparkQuestionContentData.getQuestions().get(0));
                                    initializeQuestionView(question);
                                } else {
                                    exitConfirmationDialog(getString(R.string.mindSparkNoUnitMessageList));
                                }


                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            dialog.dismiss();
                            retryDialog(getString(R.string.error_something_went_wrong), question);
                            throwable.printStackTrace();
                        }
                    });
        } else {
            retryDialog(getString(R.string.error_message_no_internet), question);
        }

    }

    private void initializeQuestionView(final Question question) {

        setQuestionText(question.getOrder(), question.getQuestionText(), question.getQuestionType());
        setIFrameInWebView(question.getQuestionText());
        setChoicesView(question);
        setHintView(question);
        setTimer();

        mBinding.scrollView.post(new Runnable() {
            @Override
            public void run() {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);

            }
        });
        mBinding.layoutExplanation.setVisibility(View.GONE);
        mBinding.layoutHints.removeAllViews();
        mBinding.layoutHints.setVisibility(View.GONE);
        mBinding.buttonDone.setVisibility(View.VISIBLE);
        mBinding.buttonNext.setVisibility(View.GONE);

        mBinding.buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitClickAction(question);
            }
        });

        mBinding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAndFetchNewQuestion(question);
            }
        });

        mBinding.layoutHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextHint(mHintCounter, question.getQuestionHints());
                if (mHintsSize > 0) {
                    mBinding.scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);

                        }
                    });
                }

            }
        });

    }


    /*Preparing the response object from ui
     * Handling response according to question type.*/
    private MindSparkQuestionSubmit prepareSubmitResponse(Question question) {
        MindSparkQuestionSubmit mindSparkQuestionSubmit = new MindSparkQuestionSubmit();
        mindSparkQuestionSubmit.setJWT(ThirdPartyPrefs.getMindSparkJsonWebToken(getBaseContext()));
        mindSparkQuestionSubmit.setContentId(question.getUidQuestion());
        mindSparkQuestionSubmit.setDynamic(false);
        mindSparkQuestionSubmit.setMode(MODE_LEARN);
        mindSparkQuestionSubmit.setResult((String) mBinding.textViewResponse.getTag(R.id.attemptResult));
        mindSparkQuestionSubmit.setContentSeqNum(question.getOrder());
        Long timeTaken = SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase();
        mindSparkQuestionSubmit.setTimeTaken(timeTaken.intValue());

        MindSparkContentDetails mindSparkContentDetails = new MindSparkContentDetails();
        mindSparkContentDetails.setContentId(question.getUidQuestion());
        mindSparkContentDetails.setContentVersionId(question.getUidQuestion() + MS_VERSION_ID_SUFFIX);
        mindSparkContentDetails.setContentType(getString(R.string.question).toLowerCase());
        mindSparkContentDetails.setQuestionType(getQuestionType(question.getQuestionType()));
        mindSparkContentDetails.setRevisionNum(question.getProgressionRule());
        mindSparkContentDetails.setLangCode(MS_LANGUAGE_CODE);
        mindSparkQuestionSubmit.setContentInfo(mindSparkContentDetails);

        mindSparkQuestionSubmit.setUserResponse(getUserResponse(question.getQuestionType()));

        MindSparkQuestionTrial mindSparkQuestionTrial = new MindSparkQuestionTrial();
        mindSparkQuestionTrial.setUserResponses(getUserResponse(question.getQuestionType()));
        mindSparkQuestionTrial.setTimeTaken(timeTaken.intValue());
        mindSparkQuestionTrial.setResult((boolean) mBinding.textViewResponse.getTag(R.id.response));

        MindSparkQuestionUserAttempt mindSparkQuestionUserAttempt = new MindSparkQuestionUserAttempt();
        mindSparkQuestionUserAttempt.setHintTaken(mHintCounter > 0);
        mindSparkQuestionUserAttempt.setTrials(new ArrayList<>(Collections.singleton(mindSparkQuestionTrial)));
        /*TODO setting 1 for now, will update the trial logic*/
        mindSparkQuestionUserAttempt.setTrialCount(1);
        mindSparkQuestionSubmit.setUserAttempt(mindSparkQuestionUserAttempt);
        return mindSparkQuestionSubmit;
    }

    private boolean getResult(String questionType) {
        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)) {
            return (boolean) mBinding.textViewResponse.getTag(R.id.response);

        } else if (questionType.equals(TYPE_BLANK)) {
            return false;
        }
        return false;
    }

    private Map<String, Object> getUserResponse(String questionType) {
        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)) {
            Map<String, Object> mcq = new HashMap<>();

            MindSparkQuestionTypeMCQ mindSparkQuestionTypeMCQ = new MindSparkQuestionTypeMCQ();
            mindSparkQuestionTypeMCQ.setUserAnswer((Integer) mBinding.layoutChoices.getTag(R.id.selectedChoiceIndex));
            mindSparkQuestionTypeMCQ.setQuestionType(TYPE_MCQ);

            String key = "mcqPattern";
            mcq.put(key, mindSparkQuestionTypeMCQ);

            return mcq;
        } else if (questionType.equals(TYPE_BLANK)) {
            Map<String, Object> mapBlank = new HashMap<>();
            int count = mBinding.layoutChoices.getChildCount();
            for (int i = 0; i < count; i++) {
                EditText view = ((EditText) mBinding.layoutChoices.getChildAt(i));
                MindSparkQuestionTypeBlank mindSparkQuestionTypeBlank = new MindSparkQuestionTypeBlank();
                mindSparkQuestionTypeBlank.setUserAnswer(view.getText().toString().trim());
                mindSparkQuestionTypeBlank.setQuestionType(TYPE_BLANK);

                String key = view.getTag().toString();
                mapBlank.put(key, mindSparkQuestionTypeBlank);
            }

            return mapBlank;

        } else if (questionType.equals(TYPE_DROPDOWN)) {
            Map<String, Object> mapDropdown = new HashMap<>();
            int count = mBinding.layoutChoices.getChildCount();
            for (int i = 0; i < count; i++) {
                EditText view = ((EditText) mBinding.layoutChoices.getChildAt(i));
                MindSparkQuestionTypeDropdown mindSparkQuestionTypeDropdown = new MindSparkQuestionTypeDropdown();
                mindSparkQuestionTypeDropdown.setUserAnswer(view.getTag(R.id.correctAnswer).toString().trim());
                mindSparkQuestionTypeDropdown.setQuestionType(TYPE_DROPDOWN);

                String key = view.getTag().toString();
                mapDropdown.put(key, mindSparkQuestionTypeDropdown);
            }

            return mapDropdown;

        } else {
            return null;
        }
    }

    private String getQuestionType(String questionType) {
        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)) {
            return TYPE_MCQ;
        } else if (questionType.equals(TYPE_BLANK)) {
            return TYPE_BLANK;
        } else if (questionType.equals(TYPE_DROPDOWN)) {
            return TYPE_DROPDOWN;
        } else {
            return null;

        }
    }

    private void setQuestionText(int order, String questionText, String questionType) {

        String orderText = "Question " + order;
        mBinding.textViewQuestionCounter.setText(orderText);

        HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(mBinding.textViewQuestion);

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)) {
            mBinding.textViewQuestion.setText(Html.fromHtml(questionText.trim(), htmlHttpImageGetter, new TextViewMore.UlTagHandler()));
            mBinding.textViewQuestion.setHtml(questionText, htmlHttpImageGetter);
        } else if (questionType.equalsIgnoreCase(TYPE_BLANK)) {
            mBinding.textViewQuestion.setHtml(mThirdPartyModel.removeBlankFromQuestion(questionText.trim()), htmlHttpImageGetter);
        } else if (questionType.equalsIgnoreCase(TYPE_DROPDOWN)) {
            mBinding.textViewQuestion.setHtml(mThirdPartyModel.removeDropdownFromQuestion(questionText.trim()), htmlHttpImageGetter);
        }
        mBinding.textViewQuestion.setRemoveTrailingWhiteSpace(true);


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setIFrameInWebView(String questionText) {
        int iFrameCount = mThirdPartyModel.checkWordOccurrence(questionText, "<iframe");
        if (iFrameCount > 0) {
            ArrayList<String> iFrameList = mThirdPartyModel.getIFrameListFromString(iFrameCount, questionText);
            String iFrameString = TextUtils.join(", ", iFrameList);
            mBinding.webView.setVisibility(View.VISIBLE);
            mBinding.webView.setBackgroundColor(Color.TRANSPARENT);
            mBinding.webView.loadData(iFrameString, "text/html", "utf-8");
            WebSettings webViewSettings = mBinding.webView.getSettings();
            webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webViewSettings.setJavaScriptEnabled(true);
            webViewSettings.setLoadWithOverviewMode(true);
            webViewSettings.setUseWideViewPort(true);
            mBinding.webView.setClickable(true);
        } else {
            mBinding.webView.setVisibility(View.GONE);

        }
    }


    private void retryDialog(String message, final Question question) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MindSparkPlayerActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.labelRetry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (question == null) {
                            fetchQuestion();
                        } else {
                            submitAndFetchNewQuestion(question);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.go_back), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();

                    }
                })
                .setCancelable(false);
        final android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void exitConfirmationDialog(String message) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MindSparkPlayerActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.go_back), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        final android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void setChoicesView(final Question question) {
        String questionType = question.getQuestionType();
        boolean isChoiceTypeImage = question.getChoiceTypeImage();
        final ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();
        mBinding.layoutChoices.setVisibility(View.VISIBLE);
        mBinding.layoutChoices.removeAllViews();


        LayoutInflater layoutInflater = this.getLayoutInflater();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            layoutInflater.inflate(R.layout.layout_radio_group, mBinding.layoutChoices);
            RadioGroup radioGroup = (RadioGroup) mBinding.layoutChoices.findViewById(R.id.radioGroup);
            mBinding.textViewQuestionType.setText(getString(R.string.single_correct));

            for (QuestionChoice questionChoice : questionChoices) {
                layoutInflater.inflate(R.layout.layout_practice_response_item_mcq_single_correct, radioGroup);
                RadioButton choice = (RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
                choice.setClickable(true);
                choice.setAlpha(1f);
                HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(choice);
                choice.setText(Html.fromHtml(questionChoice.getChoiceText(), htmlHttpImageGetter, new TextViewMore.UlTagHandler()));
                choice.setTag(questionChoice.isChoiceCorrect());
            }


        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {
            mBinding.textViewQuestionType.setText(getString(R.string.multiple_correct));

            if (isChoiceTypeImage) {
                // char choiceNumber = 'A';
                for (final QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_check_box, mBinding.layoutChoices);
                    CheckBox choice = (CheckBox) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                    choice.setClickable(true);
                    choice.setAlpha(1f);
                    //choice.setText(choiceNumber + ". ");
                    choice.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawableFromPath(this.getResources(), /*mBaseFolder + File.separator*/ questionChoice.getChoiceResource().getDeviceURL()), null);
                    choice.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            String path = /*mBaseFolder + File.separator*/ questionChoice.getChoiceResource().getDeviceURL();
                            //showResource(path);
                            return false;
                        }
                    });
                    choice.setTag(questionChoice.isChoiceCorrect());
                    //  choiceNumber++;
                }
            } else {
                for (QuestionChoice questionChoice : questionChoices) {
                    layoutInflater.inflate(R.layout.layout_check_box, mBinding.layoutChoices);
                    CheckBox choice = (CheckBox) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                    choice.setClickable(true);
                    choice.setAlpha(1f);
                    choice.setText(TextViewMore.stripHtml(questionChoice.getChoiceText()));
                    choice.setTag(questionChoice.isChoiceCorrect());
                }
            }


        } else if (questionType.equalsIgnoreCase(TYPE_BLANK)) {
            mBinding.textViewQuestionType.setText(getString(R.string.fill_the_blanks));

            for (QuestionChoice questionChoice : questionChoices) {
                layoutInflater.inflate(R.layout.layout_fill_the_blanks, mBinding.layoutChoices);
                EditText choice = (EditText) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                choice.setAlpha(1f);
                choice.setTag(questionChoice.getChoiceId());
                choice.setTag(R.id.correctAnswer, questionChoice.getChoiceText());
            }


        } else if (questionType.equalsIgnoreCase(TYPE_DROPDOWN)) {
            mBinding.textViewQuestionType.setText(getString(R.string.dropdown));

            for (final QuestionChoice questionChoice : questionChoices) {
                layoutInflater.inflate(R.layout.layout_dropdown_select_correct_answer, mBinding.layoutChoices);
                final EditText choice = (EditText) mBinding.layoutChoices.getChildAt(mBinding.layoutChoices.getChildCount() - 1);
                choice.setAlpha(1f);
                choice.setTag(questionChoice.getChoiceId());
                choice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDropdownChoiceBottomSheetDialog(questionChoice.getExtraValues(), (TextInputEditText) choice, "Select correct option");
                    }
                });
            }


        }
    }

    private void submitClickAction(Question question) {

        if (question.getQuestionType().equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO)) {
            if (getSubmittedAnswerFromView(question, false).size() > 0) {
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(question, true));
                boolean isCorrectResponse = checkCorrectness(question, attempt);
                if (isCorrectResponse) {
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                    setResponseView(question, true);
                } else {
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                    setResponseView(question, false);
                }

                mBinding.buttonDone.setVisibility(View.GONE);
                mBinding.buttonNext.setVisibility(View.VISIBLE);

                attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
                attempt.setHintsAvailed(0);
                attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());

            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.enter_response));
            }
        } else if (question.getQuestionType().equalsIgnoreCase(TYPE_BLANK)) {
            if (!getSubmittedAnswerFromView(question, false).isEmpty()) {
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(question, true));

                boolean isCorrectResponse = checkBlankCorrectness();

                if (isCorrectResponse) {
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                    setResponseView(question, true);
                } else {
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                    setResponseView(question, false);
                }

                mBinding.buttonDone.setVisibility(View.GONE);
                mBinding.buttonNext.setVisibility(View.VISIBLE);

                attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
                attempt.setHintsAvailed(0);
                attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());
            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.please_fill_all_the_blanks));
            }
        } else if (question.getQuestionType().equalsIgnoreCase(TYPE_DROPDOWN)) {
            if (!getSubmittedAnswerFromView(question, false).isEmpty()) {
                Attempt attempt = new Attempt();
                attempt.setSubmittedAnswer(getSubmittedAnswerFromView(question, true));

                boolean isCorrectResponse = checkDropdownCorrectness();

                if (isCorrectResponse) {
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                    setResponseView(question, true);
                } else {
                    attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                    setResponseView(question, false);
                }

                mBinding.buttonDone.setVisibility(View.GONE);
                mBinding.buttonNext.setVisibility(View.VISIBLE);

                attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
                attempt.setHintsAvailed(0);
                attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());
            } else {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                showSnackBar(getString(R.string.please_fill_all_the_dropdown));
            }
        }


    }


    /**
     * configurable *
     * send true if response is correct
     * send false if response is incorrect
     *
     * @param question
     * @param response
     */
    private void setResponseView(Question question, boolean response) {
        if (response) {
            mBinding.layoutResponse.setVisibility(View.VISIBLE);
            mBinding.layoutResponse.setBackgroundResource(R.drawable.background_correct_question_response);
            mBinding.imageViewResponse.setImageResource(R.drawable.action_done_w);
            mBinding.textViewResponse.setText(getString(R.string.response_correct));
            mBinding.textViewResponse.setTag(R.id.attemptResult, getString(R.string.pass));
            mBinding.textViewResponse.setTag(R.id.response, true);
            AnimationUtils.slideInLeft(getBaseContext(), mBinding.layoutResponse);
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_CORRECT_ANSWER);

        } else {
            mBinding.layoutResponse.setVisibility(View.VISIBLE);
            mBinding.layoutResponse.setBackgroundResource(R.drawable.background_incorrect_question_response);
            mBinding.imageViewResponse.setImageResource(R.drawable.action_close_w);
            mBinding.textViewResponse.setText(getString(R.string.response_incorrect));
            mBinding.textViewResponse.setTag(R.id.attemptResult, getString(R.string.fail));
            mBinding.textViewResponse.setTag(R.id.response, false);
            AnimationUtils.slideInLeft(getBaseContext(), mBinding.layoutResponse);
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_INCORRECT_ANSWER);

        }

        mBinding.chronometerQuestionTimer.stop();
        showExplanation(question);

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
                AnimationUtils.slideOutLeft(getBaseContext(), mBinding.layoutResponse);
                mBinding.layoutResponse.setVisibility(View.GONE);
            }
        }, 1500);
    }

    private void setBlankQuestionResponseView(Question question) {

        mBinding.chronometerQuestionTimer.stop();
        showExplanation(question);

        mBinding.scrollView.post(new Runnable() {
            @Override
            public void run() {
                mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_DOWN);

            }
        });

    }

    private void setTimer() {
        mBinding.layoutTimer.setVisibility(View.VISIBLE);
        mBinding.chronometerQuestionTimer.stop();
        mBinding.chronometerQuestionTimer.setBase(SystemClock.elapsedRealtime() + 0);
        mBinding.chronometerQuestionTimer.start();

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
                HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(mBinding.textViewQuestion);
                htmlHttpImageGetter.enableCompressImage(true, 80);
                mBinding.textViewExplanationText.setHtml(ThirdPartyConstants.decodeBase64String(explanationText), htmlHttpImageGetter);
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


            } else {
                mBinding.layoutExplanation.setVisibility(View.GONE);

            }
        }
    }

    private void setHintView(Question question) {
        mBinding.buttonQuestionHints.setVisibility(View.VISIBLE);
        mBinding.textQuestionHints.setVisibility(View.VISIBLE);
        String value = "0/" + question.getQuestionHints().size();
        mBinding.textQuestionHints.setText(value);
        mHintsSize = question.getQuestionHints().size();

        mHintCounter = 0;
        mBinding.layoutHints.removeAllViews();
        mBinding.cardViewHints.setVisibility(View.GONE);
    }

    private void showNextHint(int hintCounter, ArrayList<QuestionHint> questionHints) {
        if (hintCounter == mHintsSize) {
            showSnackBar(getString(R.string.no_more_hints));
        } else {
            final QuestionHint questionHint = questionHints.get(hintCounter);
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
                            //showResource(questionHint.getHintResource().getUrlMain());
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
                            // showResource(questionHint.getHintResource().getUrlMain());
                        }
                    });

                }


            }
            mHintCounter++;
            mBinding.textQuestionHints.setText(String.valueOf(mHintCounter) + "/" + mHintsSize);
        }
    }

    public void showResource(String resourcePath) {

        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
        if (mimeType != null) {
            if (mimeType.contains("image")) {
                ArrayList<String> pathArrayList = new ArrayList<>();
                pathArrayList.add("file://" + resourcePath);
                FullScreenImage.setUpFullImageView(this, 0, false, true, false, FullScreenImage.getResourceArrayList(pathArrayList));

            } else if (mimeType.contains("video")) {

                Resource item = new Resource();
                item.setType("video");
                item.setUrlMain(resourcePath);
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
            }
        }


    }

    private void showSnackBar(String message) {
        SnackBarUtils.showColoredSnackBar(this, mBinding.scrollView, message, 0x44000000);

    }

    private void showDropdownChoiceBottomSheetDialog(ArrayList<Object> arrayList, TextInputEditText appCompatTextView, String title) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        LayoutBottomSheetDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_bottom_sheet_dialog, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());
        binding.textViewTitle.setText(title);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(new DropdownChoiceAdapter(arrayList, bottomSheetDialog, appCompatTextView));
        bottomSheetDialog.show();
    }

    private ArrayList<String> getSubmittedAnswerFromView(Question question, boolean isSubmission) {

        String questionType = question.getQuestionType();
        ArrayList<String> choiceResponses = new ArrayList<>();
        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            RadioGroup radioGroup = mBinding.layoutChoices.findViewById(R.id.radioGroup);
            int count = radioGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                RadioButton view = ((RadioButton) radioGroup.getChildAt(i));
                if (view.isChecked()) {
                    choiceResponses.add(String.valueOf(i));
                    mBinding.layoutChoices.setTag(R.id.selectedChoiceIndex, i);
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

        } else if (questionType.equalsIgnoreCase(TYPE_BLANK)) {
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

        } else if (questionType.equalsIgnoreCase(TYPE_DROPDOWN)) {
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

        }

        return choiceResponses;
    }

    /**
     * check if the question response is correct or not
     *
     * @param question
     * @param attempt
     * @return
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

    private boolean checkBlankCorrectness() {
        boolean isCorrect = false;
        int count = mBinding.layoutChoices.getChildCount();
        try {
            for (int i = 0; i < count; i++) {
                EditText view = ((EditText) mBinding.layoutChoices.getChildAt(i));
                if (!view.getText().toString().trim().isEmpty()) {
                    String userAnswer = GeneralUtils.removeLeadingAndTrailingSpace(view.getText().toString());
                    String correctAnswer = view.getTag(R.id.correctAnswer).toString().trim();
                    String replace1 = correctAnswer.replace("[", "");
                    String replace2 = replace1.replace("]", "");
                    String replace3 = replace2.replaceAll("\"", "");
                    ArrayList<String> correctAnswerList = new ArrayList<>(Arrays.asList(replace3.split(",")));
                    isCorrect = correctAnswerList.contains(userAnswer.toLowerCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCorrect;
    }

    private boolean checkDropdownCorrectness() {
        boolean isCorrect = false;
        int count = mBinding.layoutChoices.getChildCount();
        try {
            for (int i = 0; i < count; i++) {
                EditText view = ((EditText) mBinding.layoutChoices.getChildAt(i));
                isCorrect = (boolean) view.getTag(R.id.isCorrect);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCorrect;
    }


    private void showBackAlertDialog() {

        new AlertDialog.Builder(MindSparkPlayerActivity.this)
                .setMessage(getString(R.string.close_alert_title))
                .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.label_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
