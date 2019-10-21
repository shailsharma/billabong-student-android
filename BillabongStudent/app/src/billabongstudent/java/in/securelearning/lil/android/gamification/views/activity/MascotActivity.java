package in.securelearning.lil.android.gamification.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutGamificationDialogBinding;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.gamification.dataobject.GamificationBonus;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.dataobject.GamificationSurveyDetail;
import in.securelearning.lil.android.gamification.event.GamificationEventDone;
import in.securelearning.lil.android.gamification.model.MascotModel;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifDrawable;

/* This class is for showing mascot */
public class MascotActivity extends AppCompatActivity {

    @Inject
    RxBus mRxBus;

    @Inject
    MascotModel mMascotModel;

    private String mMsg;
    private LayoutGamificationDialogBinding mBinding;
    private TextToSpeech mVoice = null;
    private GamificationEvent mGamificationEvent;


    public static Intent getStartIntent(Context context, String message, GamificationEvent event) {
        Intent intent = new Intent(context, MascotActivity.class);
        intent.putExtra(ConstantUtil.MESSAGE, message);
        intent.putExtra(ConstantUtil.GAMIFICATION_EVENT, event);
        return intent;
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mMsg = intent.getStringExtra(ConstantUtil.MESSAGE);
            mGamificationEvent = (GamificationEvent) intent.getSerializableExtra(ConstantUtil.GAMIFICATION_EVENT);

        }
    }

    /*show the view the check TTS if TTS is successful then
     * play voice and animation */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_gamification_dialog);
        handleIntent();

        initValues();

    }


    /*If TTS is not installed in mobile
    then just show the view and hide view after some time
    if mascot speak is true then only play voice
    */
    private void initValues() {
        if (mGamificationEvent != null && mGamificationEvent.isMascotSpeak()) {
            if (GamificationPrefs.getTTS(MascotActivity.this)) {
                showView();
                initTTS();
            } else {
                showView();
                if (checkIfEventIsUserInputTypeNot()) {
                    hideView();
                }
            }
        } else {
            showView();
            if (checkIfEventIsUserInputTypeNot()) {
                hideView();
            }
        }

        handleCloseClick();
        addListenerOnOption();
        availBonus();
        mBinding.text.setMovementMethod(new ScrollingMovementMethod());

    }


    private void handleCloseClick() {
        mBinding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user is not interested to avail bonus
                if (mGamificationEvent != null && mGamificationEvent.isBonusAvailable()) {
                    clearTtsEngine();
                    setEventDone();
                } else {
                    markEventCompleteAndClearResources();
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (checkIfEventIsUserInputTypeNot()) {
            markEventCompleteAndClearResources();
            super.onBackPressed();
        }
    }

    @Override
    public void onStop() {
        Log.d("mTextToSpeech", "TTS Destroyed");
        markEventCompleteAndClearResources();
        super.onStop();


    }


    @Override
    public void onPause() {
        markEventCompleteAndClearResources();
        if (checkIfEventIsUserInputTypeNot()) {
            finish();
        }
        super.onPause();

    }

    /* If event is  related to take input from user then
     * not need to call HideView until user give the input */
    private boolean checkIfEventIsUserInputTypeNot() {
        return mGamificationEvent != null
                && !mGamificationEvent.isOptionAvailable()
                && !mGamificationEvent.isBonusAvailable();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setEventDone() {
        if (mGamificationEvent != null) {
            mGamificationEvent.setEventOccurrenceDate(CommonUtils.getInstance().getCurrentTime());
            mMascotModel.completeEvent(mGamificationEvent);
            if (mGamificationEvent != null && mGamificationEvent.isBonusAvailable()) {
                if (mGamificationEvent.getBonusObject() != null && mGamificationEvent.getBonusObject().getBonusAvail()) {
                    mMascotModel.createGamificationBonusObject(mGamificationEvent.getBonusObject(), false);
                }
            }
            if (mGamificationEvent != null && !TextUtils.isEmpty(mGamificationEvent.getSubActivity()) && !TextUtils.isEmpty(mGamificationEvent.getActivity())) {
                mRxBus.send(new GamificationEventDone(mGamificationEvent.getSubActivity(), mGamificationEvent.getActivity(), true));
            }

        }

    }

    @Override
    public void onDestroy() {
        markEventCompleteAndClearResources();
        super.onDestroy();


    }

    /* This event is called from on
    click,on stop, on destroy and from hide view event
    will be done only when event does not take input from user*/
    private void markEventCompleteAndClearResources() {
        clearTtsEngine();
        stopGif();
        if (checkIfEventIsUserInputTypeNot()) {
            setEventDone();

        }
    }


    public void clearTtsEngine() {
        if (mVoice != null) {
            mVoice.stop();
            mVoice.shutdown();
            mVoice = null;
        }


    }

    private void stopGif() {
        GifDrawable gifDrawable = (GifDrawable) mBinding.gifImage.getDrawable();
        gifDrawable.seekToFrame(1);
        gifDrawable.stop();
    }

    public void voiceInit(final String text) {
        try {
            Random r = new Random();
            final int randomNumber = r.nextInt(111);
            final Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(randomNumber));


            if (mVoice != null) {
                mVoice.setLanguage(Locale.UK);
                mVoice.setSpeechRate(0.9f);
                mVoice.setPitch(1.0f);
                mVoice.speak(text, TextToSpeech.QUEUE_FLUSH, params, String.valueOf(randomNumber));
                mVoice.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                        playGif();

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (checkIfEventIsUserInputTypeNot()) {
                            hideView();
                        } else {
                            stopGif();
                        }


                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            } else {
                showView();
                if (checkIfEventIsUserInputTypeNot()) {
                    hideView();
                } else {
                    stopGif();


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @SuppressLint("CheckResult")
    private void showView() {
        Completable.complete()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        mBinding.getRoot().setVisibility(View.VISIBLE);
                        mBinding.closeButton.setVisibility(View.VISIBLE);
                        mBinding.layoutContent.setVisibility(View.VISIBLE);
                        mBinding.text.setText(Html.fromHtml(mMsg));
                        mBinding.gifImage.setVisibility(View.VISIBLE);
                        mBinding.imageArrow.setVisibility(View.VISIBLE);
                        ((GifDrawable) mBinding.gifImage.getDrawable()).stop();
                        checkEventType(mGamificationEvent.getEventType());
                        showAnimation();
                        // playVoice();


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


    }

    private void playVoice() {
        if (mVoice != null && GamificationPrefs.getTTS(MascotActivity.this)) {
            voiceInit(Html.fromHtml(mMsg).toString());
        }
    }

    /* If event have mascot play then only need to play GIF*/
    private void playGif() {
        if (mGamificationEvent != null && mGamificationEvent.isMascotShouldPlay()) {
            ((GifDrawable) mBinding.gifImage.getDrawable()).start();
        } else {
            ((GifDrawable) mBinding.gifImage.getDrawable()).stop();
        }
    }

    /* if voice is not there then delay must 5000 and event must done */
    @SuppressLint("CheckResult")
    private void hideView() {

        Completable.complete().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {


                        long exitTime;

                        if (mVoice != null && mGamificationEvent.isMascotSpeak()) {
                            exitTime = 2000;
                        } else if (mVoice != null) {
                            exitTime = mGamificationEvent.getMsgDuration();
                        } else {
                            exitTime = 5000;
                        }

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mVoice != null) {
                                    stopAnimation();
                                }
                                markEventCompleteAndClearResources();
                                finish();

                            }
                        }, exitTime);


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


    }

    private void showAnimation() {
        AnimationUtils.zoomIn(MascotActivity.this, mBinding.layoutContent);
        AnimationUtils.zoomIn(MascotActivity.this, mBinding.imageArrow);
        AnimationUtils.zoomInFast(MascotActivity.this, mBinding.gifImage);
        AnimationUtils.zoomIn(MascotActivity.this, mBinding.closeButton);

    }

    private void stopAnimation() {
        AnimationUtils.zoomOut(MascotActivity.this, mBinding.layoutContent);
        AnimationUtils.zoomOut(MascotActivity.this, mBinding.imageArrow);
        AnimationUtils.zoomOut(MascotActivity.this, mBinding.gifImage);
        AnimationUtils.zoomOut(MascotActivity.this, mBinding.closeButton);

    }

    private void checkEventType(String type) {

        switch (type) {
            case "welcome_message": {
                mBinding.layoutSurvey.getRoot().setVisibility(View.GONE);
                mBinding.layoutBonus.getRoot().setVisibility(View.GONE);
                mBinding.text.setLines(2);
                mBinding.text.setMinLines(2);
                break;
            }
            case "notification": {
                mBinding.layoutSurvey.getRoot().setVisibility(View.GONE);
                mBinding.layoutBonus.getRoot().setVisibility(View.GONE);
                break;
            }
            case "points": {
                mBinding.layoutSurvey.getRoot().setVisibility(View.GONE);
                mBinding.layoutBonus.getRoot().setVisibility(View.GONE);
                break;
            }
            case "option": {
                mBinding.layoutSurvey.getRoot().setVisibility(View.VISIBLE);
                mBinding.layoutBonus.getRoot().setVisibility(View.GONE);
                mBinding.closeButton.setVisibility(View.GONE);
                break;
            }
            case "bonus": {
                mBinding.layoutSurvey.getRoot().setVisibility(View.GONE);
                mBinding.layoutBonus.getRoot().setVisibility(View.VISIBLE);

                break;
            }

        }
    }

    private void addListenerOnOption() {
        mBinding.layoutSurvey.layoutGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSurveyData("good");

            }
        });
        mBinding.layoutSurvey.layoutOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSurveyData("ok");
            }
        });
        mBinding.layoutSurvey.layoutSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSurveyData("sad");
            }
        });


    }

    private void availBonus() {
        mBinding.layoutBonus.textViewBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GamificationBonus bonusObject = mGamificationEvent.getBonusObject();
                if (mGamificationEvent != null && bonusObject != null) {
                    bonusObject.setStartDate(DateUtils.getCurrentISO8601DateString());
                    bonusObject.setEndDate(CommonUtils.getInstance().getNextDayISODate(1, 0));
                    saveBonusToServer(bonusObject);
                }


            }
        });


    }


    @SuppressLint("CheckResult")
    private void saveBonusToServer(GamificationBonus bonus) {
        if (GeneralUtils.isNetworkAvailable(MascotActivity.this)) {
            mMascotModel.saveGamificationBonusToServer(bonus)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GamificationBonus>() {
                        @Override
                        public void accept(GamificationBonus bonus) throws Exception {
                            clearTtsEngine();
                            setEventDone();
                            if (bonus != null) {
                                mMascotModel.createGamificationBonusObject(bonus, false);
                                Toast.makeText(MascotActivity.this, "Thanks " + AppPrefs.getUserName(MascotActivity.this) + " Bonus is now available for you.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                mMascotModel.createGamificationBonusObject(bonus, false);
                                Toast.makeText(MascotActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                finish();
                            }


                        }

                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            clearTtsEngine();
                            setEventDone();
                            finish();
                        }
                    });

        }
    }


    @SuppressLint("CheckResult")
    private void saveSurveyData(String value) {
        if (!TextUtils.isEmpty(value)) {

            GamificationSurveyDetail surveyDetail = new GamificationSurveyDetail();
            surveyDetail.setOption1("good");
            surveyDetail.setOption2("ok");
            surveyDetail.setOption3("sad");
            surveyDetail.setOption4("compulsory");
            surveyDetail.setSelectedOption(value);
            clearTtsEngine();

            if (GeneralUtils.isNetworkAvailable(MascotActivity.this)) {
                mBinding.layoutProgressBar.setVisibility(View.VISIBLE);
                mMascotModel.saveSurveyData(mMascotModel.createGamificationSurveyForServer(AppPrefs.getUserId(MascotActivity.this), surveyDetail))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                mBinding.layoutProgressBar.setVisibility(View.GONE);
                                if (responseBody != null) {
                                    GeneralUtils.showToastLong(MascotActivity.this, "Thanks " + AppPrefs.getUserName(MascotActivity.this) + ", for your response");
                                    setEventDone();
                                    finish();

                                } else {
                                    GeneralUtils.showToastLong(MascotActivity.this, "Something went wrong");
                                    setEventDone();
                                    finish();
                                }


                            }

                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mBinding.layoutProgressBar.setVisibility(View.GONE);
                                setEventDone();
                                throwable.printStackTrace();
                                finish();
                            }
                        });

            }
        }
    }


    public void initTTS() {
        try {
            mVoice = new TextToSpeech(MascotActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(final int status) {
                    if (status != TextToSpeech.SUCCESS) {
                        //show dialog without voice
                        showView();
                        if (checkIfEventIsUserInputTypeNot()) {
                            hideView();
                        } else {
                            stopGif();


                        }
                    } else {
                        // show dialog with voice
                        //showView();
                        playVoice();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}