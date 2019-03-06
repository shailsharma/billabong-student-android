package in.securelearning.lil.android.player.views.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
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
import java.util.Collections;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutPracticePlayerBinding;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.utils.MindSparkPrefs;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkContentDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionContentData;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionMCQPattern;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionParent;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionSubmit;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionTrial;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionUserAttempt;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.base.utils.ImageUtils.getDrawableFromPath;

public class PracticePlayerActivity extends AppCompatActivity {

    LayoutPracticePlayerBinding mBinding;
    private final static String TOPIC_ID = "topicId";
    @Inject
    FlavorHomeModel mFlavorHomeModel;
    public static final String ACTION_START = "start";
    public static final String ACTION_CONTINUE = "continue";
    private String mTopicId;
    private String mAction;
    private int mHintsSize = 0;
    private int mHintCounter = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_practice_player);
        handleIntent();
        mFlavorHomeModel.setImmersiveStatusBar(getWindow());
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mTopicId = getIntent().getStringExtra(TOPIC_ID);
            fetchQuestion();
        }
    }

    @SuppressLint("CheckResult")
    private void fetchQuestion() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            final Dialog dialog = mFlavorHomeModel.loadingDialog(PracticePlayerActivity.this, getString(R.string.messagePleaseWait));
            MindSparkQuestionRequest mindSparkQuestionRequest = new MindSparkQuestionRequest();
            mindSparkQuestionRequest.setTopicId(mTopicId);
            mindSparkQuestionRequest.setMode("learn");
            mindSparkQuestionRequest.setAction(ACTION_START);
            mFlavorHomeModel.getMindSparkQuestion(mindSparkQuestionRequest)
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
                                if (mindSparkQuestionContentData.getQuestions().get(0).getQuestionResponse().getMindSparkQuestionMCQPattern() != null) {
                                    Question question = mFlavorHomeModel.convertMSQuestionToLILQuestion(mindSparkQuestionContentData.getContentSeqNum(), mindSparkQuestionContentData.getContentId(), mindSparkQuestionContentData.getQuestions().get(0));
                                    initializeQuestionView(question);
                                } else {
                                    exitConfirmationDialog(getString(R.string.messageMSNoMCQ));
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            dialog.dismiss();
                            retryDialog(getString(R.string.error_something_went_wrong), null);
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
            final Dialog dialog = mFlavorHomeModel.loadingDialog(PracticePlayerActivity.this, getString(R.string.messageGettingNextQuestion));
            mFlavorHomeModel.submitAndFetchNewQuestion(prepareSubmitResponse(question))
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
                                if (mindSparkQuestionContentData.getQuestions().get(0).getQuestionResponse().getMindSparkQuestionMCQPattern() != null) {
                                    Question question = mFlavorHomeModel.convertMSQuestionToLILQuestion(mindSparkQuestionContentData.getContentSeqNum(), mindSparkQuestionContentData.getContentId(), mindSparkQuestionContentData.getQuestions().get(0));
                                    initializeQuestionView(question);
                                } else {
                                    exitConfirmationDialog(getString(R.string.messageMSNoMCQ));
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

        setQuestionText(question.getOrder(), question.getQuestionText());
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
        mBinding.buttonSubmit.setVisibility(View.VISIBLE);
        mBinding.buttonNext.setVisibility(View.GONE);

        mBinding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
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

        mBinding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private MindSparkQuestionSubmit prepareSubmitResponse(Question question) {
        MindSparkQuestionSubmit mindSparkQuestionSubmit = new MindSparkQuestionSubmit();
        mindSparkQuestionSubmit.setJWT(MindSparkPrefs.getMindSparkJsonWebToken(getBaseContext()));
        mindSparkQuestionSubmit.setContentId(question.getUidQuestion());
        mindSparkQuestionSubmit.setDynamic(false);
        mindSparkQuestionSubmit.setMode("learn");
        mindSparkQuestionSubmit.setResult((String) mBinding.textViewResponse.getTag(R.id.attemptResult));
        mindSparkQuestionSubmit.setContentSeqNum(question.getOrder());
        Long timeTaken = SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase();
        mindSparkQuestionSubmit.setTimeTaken(timeTaken.intValue());

        MindSparkContentDetails mindSparkContentDetails = new MindSparkContentDetails();
        mindSparkContentDetails.setContentId(question.getUidQuestion());
        mindSparkContentDetails.setContentVersionId(question.getUidQuestion() + "_en-IN_1");
        mindSparkContentDetails.setContentType(getString(R.string.question).toLowerCase());
        mindSparkContentDetails.setQuestionType(getString(R.string.questionTypeMCQ));
        mindSparkContentDetails.setRevisionNum(question.getProgressionRule());
        mindSparkContentDetails.setLangCode("en-IN");
        mindSparkQuestionSubmit.setContentInfo(mindSparkContentDetails);


        MindSparkQuestionResponse mindSparkQuestionResponse = new MindSparkQuestionResponse();
        MindSparkQuestionMCQPattern mindSparkQuestionMCQPattern = new MindSparkQuestionMCQPattern();
        mindSparkQuestionMCQPattern.setUserAnswer((Integer) mBinding.layoutChoices.getTag(R.id.selectedChoiceIndex));
        mindSparkQuestionMCQPattern.setQuestionType("MCQ");
        mindSparkQuestionResponse.setMindSparkQuestionMCQPattern(mindSparkQuestionMCQPattern);
        mindSparkQuestionSubmit.setUserResponse(mindSparkQuestionResponse);


        MindSparkQuestionTrial mindSparkQuestionTrial = new MindSparkQuestionTrial();
        mindSparkQuestionTrial.setUserResponses(mindSparkQuestionResponse);
        mindSparkQuestionTrial.setTimeTaken(timeTaken.intValue());
        mindSparkQuestionTrial.setResult((boolean) mBinding.textViewResponse.getTag(R.id.response));

        MindSparkQuestionUserAttempt mindSparkQuestionUserAttempt = new MindSparkQuestionUserAttempt();
        mindSparkQuestionUserAttempt.setHintTaken(mHintCounter > 0);
        mindSparkQuestionUserAttempt.setTrials(new ArrayList<MindSparkQuestionTrial>(Collections.singleton(mindSparkQuestionTrial)));
        /*TODO setting 1 for now, will update the trial logic*/
        mindSparkQuestionUserAttempt.setTrialCount(1);
        mindSparkQuestionSubmit.setUserAttempt(mindSparkQuestionUserAttempt);
        return mindSparkQuestionSubmit;
    }

    private void setQuestionText(int order, String questionText) {
        HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(mBinding.textViewQuestion);
        htmlHttpImageGetter.enableCompressImage(true, 80);
        String newText = questionText.replace("color: #000000", "color: #ffffff");
        mBinding.textViewQuestion.setText(Html.fromHtml("<b> Q." + String.valueOf(order) + " : </b> " + newText.trim(), htmlHttpImageGetter, new TextViewMore.UlTagHandler()));
    }

    public static Intent getStartIntent(Context context, String topicId) {
        Intent intent = new Intent(context, PracticePlayerActivity.class);
        intent.putExtra(TOPIC_ID, topicId);
        return intent;
    }

    private void retryDialog(String message, final Question question) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PracticePlayerActivity.this);
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
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PracticePlayerActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.go_back), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        final android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void setChoicesView(Question question) {
        String questionType = question.getQuestionType();
        boolean isChoiceTypeImage = question.getChoiceTypeImage();
        ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();
        mBinding.layoutChoices.setVisibility(View.VISIBLE);
        mBinding.layoutChoices.removeAllViews();

        LayoutInflater layoutInflater = this.getLayoutInflater();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            layoutInflater.inflate(R.layout.layout_radio_group, mBinding.layoutChoices);
            RadioGroup radioGroup = (RadioGroup) mBinding.layoutChoices.findViewById(R.id.radioGroup);

            for (QuestionChoice questionChoice : questionChoices) {
                layoutInflater.inflate(R.layout.layout_radio_button, radioGroup);
                RadioButton choice = (RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
                choice.setClickable(true);
                choice.setAlpha(1f);
                HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(choice);
                htmlHttpImageGetter.enableCompressImage(true, 50);
                choice.setText(Html.fromHtml(questionChoice.getChoiceText(), htmlHttpImageGetter, new TextViewMore.UlTagHandler()));
                choice.setTag(questionChoice.isChoiceCorrect());
            }


        } else if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_CHECKBOX)) {

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


        }
    }

    private void submitClickAction(Question question) {

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

            mBinding.buttonSubmit.setVisibility(View.GONE);
            mBinding.buttonNext.setVisibility(View.VISIBLE);

            attempt.setSubmissionDateTime(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getCurrentTimeInSeconds()));
            attempt.setHintsAvailed(0);
            attempt.setTimeTaken(SystemClock.elapsedRealtime() - mBinding.chronometerQuestionTimer.getBase());

        } else {
            mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
            showSnackBar(getString(R.string.enter_response));
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
//        if (mQuestionCounter == (mTotalQuestions - 1)) {
//            mBinding.buttonDone.setText(getString(R.string.done));
//        } else {
//            mBinding.buttonDone.setText(getString(R.string.next));
//        }
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
                AnimationUtils.slideOutLeft(getBaseContext(), mBinding.layoutResponse);
                mBinding.layoutResponse.setVisibility(View.GONE);
            }
        }, 1500);
    }

    private void setTimer() {
        mBinding.layoutTimer.setVisibility(View.GONE);
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
                mBinding.textViewExplanationText.setHtml(mFlavorHomeModel.decodeBase64String(explanationText), htmlHttpImageGetter);
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

//                mBinding.imageViewExplanationThumbnail.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showResource(question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getUrlMain());
//                    }
//                });

            } else {
                mBinding.layoutExplanation.setVisibility(View.GONE);

            }
        }
    }

    private void setHintView(Question question) {
        mBinding.buttonQuestionHints.setVisibility(View.VISIBLE);
        mBinding.textQuestionHints.setVisibility(View.VISIBLE);
        String value = "0/" + String.valueOf(question.getQuestionHints().size());
        mBinding.textQuestionHints.setText(value);
        mHintsSize = question.getQuestionHints().size();
//        if (mHintsSize == 0) {
//            mBinding.buttonQuestionHints.setVisibility(View.GONE);
//            mBinding.textQuestionHints.setVisibility(View.GONE);
//        }

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

    private void showSnackBar(String message) {
        SnackBarUtils.showColoredSnackBar(this, mBinding.scrollView, message, ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
    }

    private ArrayList<String> getSubmittedAnswerFromView(Question question, boolean isSubmission) {

        String questionType = question.getQuestionType();
        ArrayList<String> choiceResponses = new ArrayList<>();
        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            RadioGroup radioGroup = (RadioGroup) mBinding.layoutChoices.findViewById(R.id.radioGroup);
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
}
