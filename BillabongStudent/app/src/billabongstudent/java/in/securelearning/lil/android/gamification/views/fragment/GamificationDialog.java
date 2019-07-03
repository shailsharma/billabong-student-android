package in.securelearning.lil.android.gamification.views.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutGamificationDialogBinding;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.model.GamificationModel;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import pl.droidsonroids.gif.GifDrawable;

public class GamificationDialog extends DialogFragment {

    public static final String TAG = "GamificationDialog";
    public Context mContext;
    String mMsg;
    LayoutGamificationDialogBinding mBinding;
    private TextToSpeech mVoice = null;
    @Inject
    GamificationModel mGamificationModel;
    private GamificationEvent mGamificationEvent;


    public GamificationDialog() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    public GamificationDialog display(FragmentManager fragmentManager, Context context, String msg, GamificationEvent event) {
        @SuppressLint("CommitTransaction") FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
            ft.commit();
        }
        ft.addToBackStack(null);
        this.show(fragmentManager, TAG);
        mContext = context;
        this.mMsg = msg;
        this.mGamificationEvent=event;
        if (this.isAdded()) {
            return null;
        } else
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
        initTTS();
        handleClick();
        mBinding.text.setMovementMethod(new ScrollingMovementMethod());
        return mBinding.getRoot();
    }

    private void handleClick() {
        mBinding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
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
            dialog.hide();
            dialog.setCancelable(true);


        }

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("mTextToSpeech", "TTS Destroyed");
        clearTtsEngine();
        setEventDone();

    }

    private void setEventDone() {
        if(mGamificationEvent!=null) {
            mMsg = null;
            mGamificationEvent.setEventOccurrenceDate(CommonUtils.getInstance().getCurrentTime());
            mGamificationModel.completeEvent(mGamificationEvent);
        }
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
                        hideView();


                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            } else {
                showView();
                hideView();
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
                        //mBinding.text.setText(Html.fromHtml(mMsg));
                       // mBinding.text.setVisibility(View.VISIBLE);
                        mBinding.gifImage.setVisibility(View.VISIBLE);
                        ((GifDrawable) mBinding.gifImage.getDrawable()).stop();

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
        if (mVoice != null) {
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
       // AnimationUtils.slideInRight(mContext, mBinding.text);
        AnimationUtils.zoomIn(mContext, mBinding.gifImage);
        AnimationUtils.zoomIn(mContext, mBinding.closeButton);

    }

    private void stopAnimation() {
       // AnimationUtils.slideOutRight(mContext, mBinding.text);
        AnimationUtils.zoomOut(mContext, mBinding.gifImage);
        AnimationUtils.zoomOut(mContext, mBinding.closeButton);

    }

    public void initTTS() {
        mVoice = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
                if (status != TextToSpeech.SUCCESS) {
                    //show dialog without voice
                    showView();
                    hideView();
                } else {
                    // show dialog with voice
                    showView();
                }

            }
        });

    }


}