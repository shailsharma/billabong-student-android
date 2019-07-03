package in.securelearning.lil.android.player.view.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.Widgets.CheckBoxCustom;
import in.securelearning.lil.android.app.Widgets.RadioButtonCustom;
import in.securelearning.lil.android.app.databinding.LayoutPracticeEndBinding;
import in.securelearning.lil.android.app.databinding.LayoutPracticeTopicBinding;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.views.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.dataobject.PracticeFilter;
import in.securelearning.lil.android.player.dataobject.PracticeParent;
import in.securelearning.lil.android.player.dataobject.PracticeQuestionResponse;
import in.securelearning.lil.android.player.dataobject.PracticeResponse;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.FlyObjectAnimationUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class PracticeQuestionActivity extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    PlayerModel mPlayerModel;

    LayoutPracticeTopicBinding mBinding;

    private static final String SKILL_IDS = "skillIds";
    private static final String TITLE = "title";
    private static final String SUBJECT_ID = "subjectId";
    private static final String TOPIC_ID = "topicId";

    private static final int QUESTION_POINT = 20;
    private static final int LIMIT = 10;

    private int mTotalCorrect = 0;
    private int mTotalInCorrect = 0;
    private ArrayList<QuestionResponse> mQuestionResponses = new ArrayList<>();
    private int mHintCounter = 0;
    private ArrayList<String> mSkillIdList;
    private ArrayList<Question> mQuestionList = new ArrayList<>();
    private ArrayList<Integer> mStreakList = new ArrayList<>();
    private String mSubjectId;
    private String mTopicId;
    private int mQuestionCounter;
    private int mUiQuestionCounter = 1;
    private int mSkip = 0;
    private int mTotalScore = 0;
    private int mStreak = 0;
    private boolean mAllowRepeat;//To fetch repeated question which already practice by the user.
    private boolean mIsFromBack;
    private String mQuestionComplexityLevel = ConstantUtil.BLANK;
    private String mQuestionSkillId = ConstantUtil.BLANK;

    @Override
    public void onBackPressed() {
        showBackAlertDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_practice_topic);
        handleIntent();
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

    public static Intent getStartIntent(Context context, String topicName, ArrayList<String> skillIds, String subjectId, String topicId) {
        Intent intent = new Intent(context, PracticeQuestionActivity.class);
        intent.putStringArrayListExtra(SKILL_IDS, skillIds);
        intent.putExtra(TITLE, topicName);
        intent.putExtra(SUBJECT_ID, subjectId);
        intent.putExtra(TOPIC_ID, topicId);
        return intent;
    }

    /*Handle intent and get bundle data from intent*/
    private void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSkillIdList = getIntent().getStringArrayListExtra(SKILL_IDS);
            String title = getIntent().getStringExtra(TITLE);
            mSubjectId = getIntent().getStringExtra(SUBJECT_ID);
            mTopicId = getIntent().getStringExtra(TOPIC_ID);

            setUpToolbar(title);

            PracticeParent practiceParent = preparePracticePostData();
            submitAndFetchQuestions(practiceParent);

        } else {
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
            finish();
        }
    }

    /*Prepare practice post data to fetch question and send question responses to server*/
    private PracticeParent preparePracticePostData() {
        PracticeParent practiceParent = new PracticeParent();
        PracticeFilter practiceFilter = new PracticeFilter();
        practiceFilter.setSkillIdList(mSkillIdList);
        practiceFilter.setSubjectId(mSubjectId);
        practiceFilter.setTopicIdId(mTopicId);
        practiceFilter.setRepeatAllowed(mAllowRepeat);
        practiceFilter.setSkip(mSkip);
        practiceFilter.setLimit(LIMIT);

        PracticeResponse practiceResponse = new PracticeResponse();
        practiceResponse.setComplexityLevel(mQuestionComplexityLevel);
        practiceResponse.setStreakList(mStreakList);
        practiceResponse.setCurrentStreak(getStreakSize());
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
            Question question = mQuestionList.get(mQuestionCounter);

            setDefaultsForNewQuestion();
            setQuestionCounter(mUiQuestionCounter);
            setQuestionText(question);
            setHintView(question);
            setChoicesView(question);
            startTimer(question.getComplexityLevel());
            setQuestionResource(question);
            initializeClickListeners(question);
            mQuestionComplexityLevel = question.getComplexityLevel();
            mQuestionSkillId = getCurrentQuestionSkillId(question.getSkills());
            mQuestionCounter++;
            mUiQuestionCounter++;

        }

    }

    private void setMovementMethod() {
        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
                .setOnLinkClickListener(new BetterLinkMovementMethod.OnLinkClickListener() {
                    @Override
                    public boolean onClick(TextView textView, String url) {
                        if (url.contains("youtube")) {
                            startYoutubePlayer(url);
                            return true;
                        } else if (url.contains("vimeo")) {
                            startActivity(PlayVimeoFullScreenActivity.getStartIntent(getBaseContext(), url));
                            return true;
                        } else if (url.endsWith(".mp4") || url.endsWith(".MP4") ||
                                url.endsWith("wmv") || url.endsWith("WMV") ||
                                url.endsWith("flv") || url.endsWith("FLV")) {
                            startVideoPlayer(url);
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

        mBinding.layoutExplanation.setVisibility(View.GONE);

        mBinding.layoutTimer.setVisibility(View.VISIBLE);
        mBinding.chronometerQuestionTimer.stop();
        mBinding.chronometerQuestionTimer.setBase(SystemClock.elapsedRealtime());

        mHintCounter = 0;
        mBinding.layoutHints.removeAllViews();
        mBinding.cardViewHints.setVisibility(View.GONE);


    }

    /*Set/Reset views of player after question attempt.*/
    private void setViewAfterQuestionAttempt() {
        mBinding.chronometerQuestionTimer.stop();

        mBinding.buttonDone.setText(getString(R.string.next));

        mBinding.cardViewHints.setVisibility(View.GONE);
        mBinding.buttonQuestionHints.setVisibility(View.GONE);
        mBinding.textQuestionHints.setVisibility(View.GONE);
    }

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

    /*Fetch list of question and submit questionResponses in the same api*/
    @SuppressLint("CheckResult")
    private void submitAndFetchQuestions(final PracticeParent practiceParent) {
        if (GeneralUtils.isNetworkAvailable(this)) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, ConstantUtil.BLANK, getString(R.string.messagePleaseWait), false);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onBackPressed();
                }
            });
            mPlayerModel.fetchQuestions(practiceParent)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<PracticeQuestionResponse>() {
                        @Override
                        public void accept(PracticeQuestionResponse practiceQuestionResponse) {
                            progressDialog.dismiss();
                            if (mIsFromBack) {
                                finish();
                            } else {
                                if (practiceQuestionResponse != null) {

                                /*When isQuestionExist == true and question array length is greater then 0,
                                then pass to the ui for practice.*/
                                    if (practiceQuestionResponse.isQuestionExist()
                                            && practiceQuestionResponse.getQuestionList() != null
                                            && !practiceQuestionResponse.getQuestionList().isEmpty()) {
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
                                        showRepeatAlertDialog();
                                    }
                                /*When isQuestionExist == false and question array length is empty,
                                If questionCounter==0 means no question available for the topic.
                                If questionCounter>0, means question finished for the practice.*/
                                    else if (!practiceQuestionResponse.isQuestionExist()
                                            && practiceQuestionResponse.getQuestionList() != null
                                            && practiceQuestionResponse.getQuestionList().isEmpty()) {

                                        if (mSkip > 1) {
                                            showPracticeEndDialog();
                                        } else {
                                            showAlertDialog(getString(R.string.messageNoQuestionForTopic));

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
                            progressDialog.dismiss();
                            showAlertDialog(getString(R.string.error_something_went_wrong));


                        }
                    });

        } else {
            showInternetAlertDialog(practiceParent);
        }

    }

    /*Show point and streak animation if points value is greater then 0*/
    // private void showStreak(int score) {
    //     if (score > 0) {
    //  mTotalScore += score;
    //  SoundUtils.playSound(getBaseContext(), SoundUtils.STREAK);
    //      }
    //   }

    /*Show point and streak animation.*/
    private void addPoints() {

        mTotalScore = mTotalScore + QUESTION_POINT;
        mBinding.textViewAddedPoint.setVisibility(View.VISIBLE);
        mBinding.textViewAddedPoint.setText(String.valueOf(QUESTION_POINT));
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
        mQuestionList = new ArrayList<>();
        mQuestionResponses = new ArrayList<>();
        mStreakList = new ArrayList<>();
    }

    /*Start question timer when question initialize*/
    private void startTimer(String complexityLevel) {

//        if (complexityLevel.startsWith("l")) {
//            mBinding.textViewQuestionType.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.blue_color));
//
//        } else if (complexityLevel.startsWith("m")) {
//
//            mBinding.textViewQuestionType.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.orange_color));
//
//        } else if (complexityLevel.startsWith("h")) {
//            mBinding.textViewQuestionType.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red_color));
//
//        }


        mBinding.chronometerQuestionTimer.start();
//        mBinding.chronometerQuestionTimer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPracticeEndDialog();
//
//            }
//        });

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
        setMovementMethod();
        String text = "<p>Hello check this <a href=\"https://www.w3schools.com\">Visit W3Schools.com!</a>" +
                "and this" + "<img src=\"https://www.google.co.in/logos/doodles/2019/summer-2019-northern-hemisphere-6566840133222400-s.png\">" +
                "https://www.youtube.com/watch?v=avVg3pLj_Po\n" +
                "and this vimeo video - https://vimeo.com/126100721 and this video - https://sample-videos.com/video123/mp4/480/big_buck_bunny_480p_5mb.mp4</p>";

        HtmlHttpImageGetter htmlHttpImageGetter = new HtmlHttpImageGetter(mBinding.textViewQuestion);
        htmlHttpImageGetter.enableCompressImage(true, 80);
        mBinding.textViewQuestion.setHtml(question.getQuestionText(), htmlHttpImageGetter);
        mBinding.textViewQuestion.setRemoveTrailingWhiteSpace(true);

    }

    private void startVideoPlayer(String url) {
        Resource item = new Resource();
        item.setType(getString(R.string.typeVideo));
        item.setUrlMain(url);
        startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
    }

    private void startYoutubePlayer(String url) {
        if (url.contains("http:") || url.contains("https:")) {

            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
            if (matcher.find()) {
                String videoId = matcher.group();
                FavouriteResource favouriteResource = new FavouriteResource();
                favouriteResource.setName(videoId);
                favouriteResource.setUrlThumbnail(ConstantUtil.BLANK);
                startActivity(PlayYouTubeFullScreenActivity.getStartIntent(getBaseContext(), favouriteResource, false));
            }

        } else {
            FavouriteResource favouriteResource = new FavouriteResource();
            favouriteResource.setName(url);
            favouriteResource.setUrlThumbnail(ConstantUtil.BLANK);
            startActivity(PlayYouTubeFullScreenActivity.getStartIntent(getBaseContext(), favouriteResource, false));
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

    /*Setup choice view when question initialize*/
    private void setChoicesView(Question question) {
        String questionType = question.getQuestionType();
        ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();
        mBinding.layoutChoices.setVisibility(View.VISIBLE);

        LayoutInflater layoutInflater = this.getLayoutInflater();

        if (questionType.equalsIgnoreCase(Question.TYPE_DISPLAY_RADIO) || questionType.equalsIgnoreCase("trueFalse")) {
            layoutInflater.inflate(R.layout.layout_response_mcq_single_correct, mBinding.layoutChoices);
            RadioGroup radioGroup = mBinding.layoutChoices.findViewById(R.id.radio_group_response);
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
                    choice.setText(TextViewMore.stripHtml(choiceText));

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
                    choice.setText(TextViewMore.stripHtml(choiceText));
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

                final TextView hintCounterTextView = layout.findViewById(R.id.text_view_hint_counter);
                String hintCounterText = currentHint + "/" + totalHints;
                hintCounterTextView.setText(hintCounterText);
                hintTextView.setText(TextViewMore.stripHtml(questionHint.getHintText()));
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

    /*Show explanation view if question contains, after submitting the current question response.*/
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

        if (getSubmittedAnswerFromView(false, question).size() > 0) {
            Attempt attempt = new Attempt();
            attempt.setSubmittedAnswer(getSubmittedAnswerFromView(true, question));
            boolean isCorrectResponse = mPlayerModel.checkCorrectness(question, attempt);
            if (isCorrectResponse) {
                mTotalCorrect++;
                mStreak++;
                resetTotalInCorrectCounter();
                attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_CORRECT);
                setResponseView(true);
                setStreakView();

//                if (mTotalCorrect == QUESTION_TOTAL_CORRECT) {
//                    resetTotalCorrectCounter();
//                }

            } else {
                mTotalInCorrect++;
                addStreakIntoStreakList();
                resetTotalCorrectCounter();
                attempt.setStatusCode(Attempt.TYPE_STATUS_CODE_INCORRECT);
                setResponseView(false);
                setStreakView();

//                if (mTotalInCorrect == QUESTION_TOTAL_INCORRECT) {
//                    decrementQuestionComplexity();
//                    resetTotalInCorrectCounter();
//                }
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


    }

    private void addStreakIntoStreakList() {
        if (mStreak > 2) {
            mStreakList.add(mStreak);
            Log.e("StreakList--", String.valueOf(mStreakList));
        }
    }

    /*Next button click action*/
    private void nextClickAction() {
//        if (mTotalCorrect == QUESTION_TOTAL_CORRECT) {
//            String complexityLevel = question.getComplexityLevel();
//            String currentQuestionSkillId = getCurrentQuestionSkillId(question.getSkills());
//            PracticeParent practiceParent = preparePracticePostData(complexityLevel, currentQuestionSkillId, mQuestionResponses);
//            submitAndFetchQuestions(practiceParent);
//        }
//        else if (mTotalInCorrect == QUESTION_TOTAL_INCORRECT) {
//            String complexityLevel = question.getComplexityLevel();
//            String currentQuestionSkillId = getCurrentQuestionSkillId(question.getSkills());
//            PracticeParent practiceParent = preparePracticePostData(complexityLevel, currentQuestionSkillId, mQuestionResponses);
//            submitAndFetchQuestions(practiceParent);
//        }

        if (mQuestionList.size() > mQuestionCounter) {
            initializeQuestionUi();
            mBinding.scrollView.fullScroll(NestedScrollView.FOCUS_UP);
        } else {
            addStreakIntoStreakList();
            PracticeParent practiceParent = preparePracticePostData();
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

        mBinding.layoutQuestionResponse.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutQuestionResponse);

        if (response) {
            mBinding.textViewQuestionResponse.setText(getString(R.string.response_correct));
            mBinding.lottie.setAnimation("lottie_success.json");
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_CORRECT_ANSWER);
            addPoints();


        } else {
            mBinding.textViewQuestionResponse.setText(getString(R.string.question_response_wrong));
            SoundUtils.playSound(getBaseContext(), SoundUtils.QUIZ_INCORRECT_ANSWER);
            mBinding.lottie.setAnimation("lottie_failed.json");

        }

        mBinding.lottie.playAnimation();

        Handler hold = new Handler();
        hold.postDelayed(new Runnable() {

            @Override
            public void run() {

                AnimationUtils.pushDownExit(getBaseContext(), mBinding.layoutQuestionResponse);
                mBinding.layoutQuestionResponse.setVisibility(View.GONE);

            }
        }, 2500);
    }

    /*Set streak count, for ui it a continuously correct answer count.*/
    private void setStreakView() {

        mBinding.textViewStreak.setText(String.valueOf(mTotalCorrect));


    }

    /*Show question resource in full view.*/
    public void showResource(String resourcePath) {

        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
        if (mimeType != null) {
            if (mimeType.contains("image")) {
                startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), resourcePath, true));

                UserProfileActivity.showFullImage(resourcePath, PracticeQuestionActivity.this);

            } else if (mimeType.contains("video")) {
                Resource item = new Resource();
                item.setType("video");
                item.setUrlMain(resourcePath);
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, item));
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

    /*Generate questionResponse object and add to question response array.*/
    private void generateQuestionResponse(Attempt attempt, String response, Question question, int counterHint) {

        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setAlias(GeneralUtils.generateAlias("QuestionResponse", mAppUserModel.getObjectId(), System.currentTimeMillis() + ""));
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
                .setNeutralButton(R.string.go_back, new DialogInterface.OnClickListener() {
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
                        PracticeParent practiceParent = preparePracticePostData();
                        submitAndFetchQuestions(practiceParent);
                        dialog.dismiss();

                    }
                })
                .show();
    }

    /*Show this alert dialog in case of practice ends.*/
    private void showPracticeEndDialog() {
        LayoutPracticeEndBinding view = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_practice_end, null, false);
        final Dialog dialog = new Dialog(PracticeQuestionActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(view.getRoot());
        view.textViewGreeting.setText(getString(R.string.congratulations));
        view.textViewGreetingMessage.setText(getString(R.string.practice_end_greeting_message));
        view.lottie.setAnimation("celebration_star.json");
        view.lottie.setRepeatCount(Animation.INFINITE);
        view.lottie.playAnimation();
        String score = ConstantUtil.BLANK_SPACE + mTotalScore + ConstantUtil.BLANK_SPACE;
        view.textViewScore.setText(score);
        view.buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    private void showInternetAlertDialog(final PracticeParent practiceParent) {
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

                        submitAndFetchQuestions(practiceParent);
                    }
                })
                .show();
    }

    /*Show this dialog when user back press.*/
    private void showBackAlertDialog() {

        new AlertDialog.Builder(PracticeQuestionActivity.this)
                .setMessage(getString(R.string.close_alert_title))
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mQuestionResponses != null && mQuestionResponses.size() > 0) {
                            mIsFromBack = true;
                            PracticeParent practiceParent = preparePracticePostData();
                            submitAndFetchQuestions(practiceParent);
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


}
