package in.securelearning.lil.android.gamification.views.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;
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
import in.securelearning.lil.android.gamification.model.GamificationModel;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifDrawable;

public class GamificationDialog extends DialogFragment {

    public static final String TAG = "GamificationDialog";
    public Context mContext;
    String mMsg;
    LayoutGamificationDialogBinding mBinding;
    @Inject
    GamificationModel mGamificationModel;
    @Inject
    RxBus mRxBus;
    private TextToSpeech mVoice = null;
    private GamificationEvent mGamificationEvent;

    public GamificationDialog() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    public GamificationDialog display(FragmentManager fragmentManager, Context context, String msg, GamificationEvent event) {
        try {


            @SuppressLint("CommitTransaction") FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment prev = fragmentManager.findFragmentByTag(TAG);
            if (prev != null) {
                ft.remove(prev);
                ft.commitAllowingStateLoss();
            }
            ft.addToBackStack(null);
            this.show(fragmentManager, TAG);
            mContext = context;
            this.mMsg = msg;
            this.mGamificationEvent = event;
            if (this.isAdded()) {
                return null;
            } else
                return this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_gamification_dialog, container, false);
        if (mContext != null && GamificationPrefs.getTTS(mContext)) {
            initTTS();
        } else {
            showView();
        }

        handleClick();
        addListenerOnOption();
        availBonus();
        mBinding.text.setMovementMethod(new ScrollingMovementMethod());
        return mBinding.getRoot();
    }

    private void handleClick() {
        mBinding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user is not interested to avail bonus
                if (mGamificationEvent != null && mGamificationEvent.isBonusAvailable()) {
                    if (mGamificationEvent.getBonusObject() != null) {
                        mGamificationModel.createGamificationBonusObject(mGamificationEvent.getBonusObject(), false);
                    }
                }
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        final Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            // lp.dimAmount = 0.7f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.getWindow().setAttributes(lp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                dialog.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorCenterGradient));
            }
            dialog.hide();
            if (mGamificationEvent != null && !mGamificationEvent.isOptionAvailable() && !mGamificationEvent.isBonusAvailable()) {
                dialog.setCancelable(true);
            } else {
                dialog.setCancelable(false);
            }


        }

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("mTextToSpeech", "TTS Destroyed");
        clearTtsEngine();
        setEventDone();
        if (getDialog() != null) {
            getDialog().dismiss();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setEventDone() {
        if (mGamificationEvent != null) {
            mGamificationEvent.setEventOccurrenceDate(CommonUtils.getInstance().getCurrentTime());
            mGamificationModel.completeEvent(mGamificationEvent);
            if (mGamificationEvent != null && mGamificationEvent.isBonusAvailable()) {
                if (mGamificationEvent.getBonusObject() != null && mGamificationEvent.getBonusObject().getBonusAvail()) {
                    mGamificationModel.createGamificationBonusObject(mGamificationEvent.getBonusObject(), false);
                }
            }
            if (mGamificationEvent != null && !TextUtils.isEmpty(mGamificationEvent.getSubActivity()) && !TextUtils.isEmpty(mGamificationEvent.getActivity())) {
                mRxBus.send(new GamificationEventDone(mGamificationEvent.getSubActivity(), mGamificationEvent.getActivity(), true));
            }


//            if (AppLifecycleHandler.getInstance() != null && AppLifecycleHandler.isApplicationInForeground()) {
//                mRxBus.send(new GamificationEventDone(mGamificationEvent.getSubActivity(), mGamificationEvent.getActivity(), true));
//            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    public void clearTtsEngine() {
        if (mVoice != null) {
            mVoice.stop();
            mVoice.shutdown();
            mVoice = null;
        }


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
                mVoice.speak(text, TextToSpeech.QUEUE_FLUSH, params, String.valueOf(randomNumber));
                mVoice.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                        playGif();

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (mGamificationEvent != null && !mGamificationEvent.isOptionAvailable() && !mGamificationEvent.isBonusAvailable()) {
                            hideView();
                        } else {
                            ((GifDrawable) mBinding.gifImage.getDrawable()).stop();
                            setCancelable(false);

                        }


                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            } else {
                showView();
                if (mGamificationEvent != null && !mGamificationEvent.isOptionAvailable() && !mGamificationEvent.isBonusAvailable()) {
                    hideView();
                } else {
                    ((GifDrawable) mBinding.gifImage.getDrawable()).stop();
                    setCancelable(false);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @SuppressLint("CheckResult")
    private void showView() {
        Completable.complete().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        getDialog().show();
                        mBinding.getRoot().setVisibility(View.VISIBLE);
                        mBinding.closeButton.setVisibility(View.VISIBLE);
                        mBinding.layoutContent.setVisibility(View.VISIBLE);
                        mBinding.text.setText(String.valueOf(Html.fromHtml(mMsg)));
                        mBinding.gifImage.setVisibility(View.VISIBLE);
                        ((GifDrawable) mBinding.gifImage.getDrawable()).stop();
                        checkEventType(mGamificationEvent.getEventType());
                        showAnimation();
                        playVoice();


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


    }

    private void playVoice() {
        if (mVoice != null && GamificationPrefs.getTTS(mContext)) {
            voiceInit(Html.fromHtml(mMsg).toString());
        }
    }

    private void playGif() {
        if (mVoice != null) {
            ((GifDrawable) mBinding.gifImage.getDrawable()).start();
        } else {
            ((GifDrawable) mBinding.gifImage.getDrawable()).stop();
        }
    }


    @SuppressLint("CheckResult")
    private void hideView() {
        Completable.complete().subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        GifDrawable gifDrawable = (GifDrawable) mBinding.gifImage.getDrawable();
                        gifDrawable.seekToFrame(1);
                        gifDrawable.stop();


                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopAnimation();
                                mBinding.closeButton.setVisibility(View.GONE);
                                mBinding.imageArrow.setVisibility(View.GONE);
                                mBinding.gifImage.setVisibility(View.GONE);
                                mBinding.text.setVisibility(View.GONE);

                            }
                        }, 2000);
                        final Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                            }
                        }, 3000);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


    }

    private void showAnimation() {
        AnimationUtils.zoomIn(mContext, mBinding.layoutContent);
        AnimationUtils.zoomIn(mContext, mBinding.gifImage);
        AnimationUtils.zoomIn(mContext, mBinding.closeButton);

    }

    private void stopAnimation() {
        AnimationUtils.zoomOut(mContext, mBinding.layoutContent);
        AnimationUtils.zoomOut(mContext, mBinding.gifImage);
        AnimationUtils.zoomOut(mContext, mBinding.closeButton);

    }

    private void checkEventType(String type) {
        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // Double width = metrics.widthPixels * 0.65;
        Double height = metrics.heightPixels * 0.10;

        switch (type) {
            case "welcome_message": {
                mBinding.layoutSurvey.getRoot().setVisibility(View.GONE);
                mBinding.layoutBonus.getRoot().setVisibility(View.GONE);

//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                //mBinding.text.setLayoutParams(params);
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
                //mBinding.layoutContent.getLayoutParams().height = 400;
//                mBinding.text.setLines(4);
//                mBinding.text.setMinLines(4);

                break;
            }
            case "option": {

                mBinding.layoutSurvey.getRoot().setVisibility(View.VISIBLE);
                mBinding.layoutBonus.getRoot().setVisibility(View.GONE);
                mBinding.closeButton.setVisibility(View.GONE);
//                mBinding.text.setLines(3);
//                mBinding.text.setMinLines(3);
                break;
            }
            case "bonus": {
                mBinding.layoutSurvey.getRoot().setVisibility(View.GONE);
                mBinding.layoutBonus.getRoot().setVisibility(View.VISIBLE);

//                mBinding.text.setLines(3);
//                mBinding.text.setMinLines(3);
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
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mGamificationModel.saveGamificationBonusToServer(bonus)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GamificationBonus>() {
                        @Override
                        public void accept(GamificationBonus bonus) throws Exception {
                            if (bonus != null) {
                                mGamificationModel.createGamificationBonusObject(bonus, false);
                                Toast.makeText(mContext, "Thanks " + AppPrefs.getUserName(mContext) + "Bonus is now available for you. ", Toast.LENGTH_LONG).show();
                                dismiss();
                            } else {
                                mGamificationModel.createGamificationBonusObject(bonus, false);
                                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_LONG).show();
                                dismiss();
                            }


                        }

                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            dismiss();
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

            if (GeneralUtils.isNetworkAvailable(mContext)) {
                mGamificationModel.saveSurveyData(mGamificationModel.createGamificationSurveyForServer(AppPrefs.getUserId(mContext), surveyDetail))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                if (responseBody != null) {
                                    Toast.makeText(mContext, "Thanks " + AppPrefs.getUserName(mContext) + " for your response", Toast.LENGTH_LONG).show();
                                    dismiss();
                                } else {
                                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_LONG).show();
                                    dismiss();
                                }


                            }

                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                dismiss();
                            }
                        });

            }
        }
    }


    public void initTTS() {
        try {
            mVoice = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(final int status) {
                    if (status != TextToSpeech.SUCCESS) {
                        //show dialog without voice
                        showView();
                        if (mGamificationEvent != null && !mGamificationEvent.isOptionAvailable() && !mGamificationEvent.isBonusAvailable()) {
                            hideView();
                        } else {
                            ((GifDrawable) mBinding.gifImage.getDrawable()).stop();
                            setCancelable(false);

                        }
                    } else {
                        // show dialog with voice
                        showView();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}