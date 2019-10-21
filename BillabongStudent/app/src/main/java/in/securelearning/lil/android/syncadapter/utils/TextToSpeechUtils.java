package in.securelearning.lil.android.syncadapter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.events.SpeakCompletedEvent;
import in.securelearning.lil.android.player.events.TTSInitialiseDoneEvent;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 28-06-2018.
 */
public class TextToSpeechUtils extends UtteranceProgressListener {
    public static final String NO_CARD_CAPTION = "No reading material available for this card.";
    public static final String MANDATORY_CARD = "This card is mandatory.";
    public TextToSpeech mTts;
    public boolean mReady = false;
    public String[] mSentence;
    @Inject
    public RxBus mRxBus;
    public boolean mStopHighlight = false;
    String[] mWords;
    private int mDuration, mIndex;
    private boolean mAllowed = false;
    private boolean isEnglish = true;

    public TextToSpeechUtils() {
        InjectorPlayer.INSTANCE.getComponent().inject(this);

    }

    public TextToSpeechUtils(final Context context) {

        mTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    if (mTts != null) {
                        mReady = true;
                        mTts.setPitch(1.0f);
                        mTts.setSpeechRate(0.8f);
                        if (mTts.isLanguageAvailable(new Locale("hi_IN")) == TextToSpeech.LANG_AVAILABLE) {
                            mTts.setLanguage(new Locale("hi_IN"));


                        } else {
                            mTts.setLanguage(Locale.US);

                        }
                        InjectorPlayer.INSTANCE.getComponent().rxBus().send(new TTSInitialiseDoneEvent(true));
                    }
                } else {
                    mReady = false;
                    InjectorPlayer.INSTANCE.getComponent().rxBus().send(new TTSInitialiseDoneEvent(false));
                }
            }
        });
    }

//    @Override
//    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//
//            if (mTts != null) {
//                mReady = true;
//                mTts.setPitch(1.0f);
//                mTts.setSpeechRate(0.9f);
//                mTTSListener.isTTSReady(true);
//                if (mTts.isLanguageAvailable(new Locale("hi_IN")) == TextToSpeech.LANG_AVAILABLE) {
//                    mTts.setLanguage(new Locale("hi_IN"));
//                    mTts.setOnUtteranceProgressListener(this);
//
//                } else {
//                    mTts.setLanguage(Locale.US);
//                    mTts.setOnUtteranceProgressListener(this);
//                }
//            }
//        } else {
//            mTTSListener.isTTSReady(false);
//            mReady = false;
//        }
//    }


    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {

        mDuration = duration;
    }

    public boolean isAllowed() {
        return mAllowed;
    }

    public boolean allow(boolean allowed) {
        this.mAllowed = allowed;
        return allowed;
    }

    public void speakWord(String text) {

        if (mReady && mAllowed) {
            HashMap<String, String> hash = new HashMap<String, String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_NOTIFICATION));
            hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
            if (text != null) {
                mWords = text.split(" ");
                for (String word : mWords) {
                    mTts.speak(word, TextToSpeech.QUEUE_ADD, hash);
                }
            }
        }
    }

    public void speakSentence(String text) {

        if (mReady && mAllowed) {
            HashMap<String, String> hash = new HashMap<String, String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));

            if (text != null) {
                mSentence = text.split("[.]");
                for (String s : mSentence) {
                    hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "FINISH");
                    mTts.speak(s, TextToSpeech.QUEUE_ADD, hash);

                }
            } else {
                mTts.speak(NO_CARD_CAPTION, TextToSpeech.QUEUE_FLUSH, hash);
            }
        }
    }

    public void speakParagraph(String text) {

        if (mReady && mAllowed) {
            HashMap<String, String> hash = new HashMap<>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
            hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "FINISH");
            if (!TextUtils.isEmpty(text)) {
                mTts.speak(text, TextToSpeech.QUEUE_ADD, hash);
            } else {
                mTts.speak(NO_CARD_CAPTION, TextToSpeech.QUEUE_ADD, hash);

            }
        }
    }

    public void pause(int duration) {
        mTts.playSilence(getDuration(), TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stop() {
        if (mTts != null) {
            setDuration(1500);
            mTts.playSilence(getDuration(), TextToSpeech.QUEUE_FLUSH, null);
            mTts.stop();
        }
    }

    private void shutdownTTS() {
        mTts.shutdown();
    }

    @Override
    public void onStart(String s) {

    }

    @Override
    public void onDone(String s) {
        mRxBus.send(new SpeakCompletedEvent(true));
    }

    @Override
    public void onError(String s) {
        Log.e("ERROR", "onError: " + s);
    }

    public void destroyTTS() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
            shutdownTTS();
            Log.d("TTS", "TTS destroyed");

        }
    }

    public void readMandatoryInstruction() {

        if (mReady && mAllowed) {
            mTts.speak(NO_CARD_CAPTION, TextToSpeech.QUEUE_ADD, null);

        }

    }


    // speak and highlight word
    public void speakAndHighLight(final String text, final TextView textView, final String[] wordStrings) {

        Random r = new Random();
        final int randomNumber = r.nextInt(111);
        final Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(randomNumber));
        if (!TextUtils.isEmpty(text)) {
            mTts.setPitch(1.0f);
            mTts.setSpeechRate(0.8f);
            mTts.speak(android.text.Html.fromHtml(text).toString(), TextToSpeech.QUEUE_ADD, params, String.valueOf(randomNumber));
            mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onStart(final String s) {
                    highlight(text, textView, wordStrings);

                }

                @Override
                public void onDone(String s) {
                    InjectorPlayer.INSTANCE.getComponent().rxBus().send(new SpeakCompletedEvent(true));
                }

                @Override
                public void onError(String s) {
                    InjectorPlayer.INSTANCE.getComponent().rxBus().send(new SpeakCompletedEvent(false));
                }
            });

        } else {
            mTts.speak("No Data", TextToSpeech.QUEUE_ADD, params, String.valueOf(randomNumber));
        }
    }

    @SuppressLint("CheckResult")
    public void highlight(final String string, final TextView textView, final String[] wordStrings) {

        Observable.create(new ObservableOnSubscribe<SpannableStringBuilder>() {
            @Override
            public void subscribe(final ObservableEmitter<SpannableStringBuilder> emitter) throws Exception {
                if (!TextUtils.isEmpty(string)) {
                    int spam = 0;
                    identifyLanguage(string);
                    for (String speakWord : wordStrings) {
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(Html.fromHtml(string).toString());
                        final UnderlineSpan underlineSpan = new UnderlineSpan(); // Span to make text bold
                        stringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), spam, spam + speakWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        stringBuilder.setSpan(underlineSpan, spam, spam + speakWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spam = spam + speakWord.length() + 1;
                        emitter.onNext(stringBuilder);
                        synchronized (emitter) {
                            if (speakWord.contains("!") || speakWord.contains(",") ||
                                    speakWord.contains("*") ||
                                    speakWord.contains(";") || speakWord.contains(":") ||
                                    speakWord.contains(".") || speakWord.contains("?") ||
                                    speakWord.contains("।") ||
                                    speakWord.contains("|") || speakWord.contains("\n\n\t●")) {
                                emitter.wait(1000);

                            } else if (speakWord.contains("i.e.")) {
                                emitter.wait(200);
                            } else {
                                if (isEnglish) {
                                    emitter.wait(speakWord.length() * 75);
                                } else {
                                    emitter.wait(speakWord.length() * 95);
                                }

                            }


                        }
                        if (mStopHighlight) {
                            break;

                        }


                    }
                }

                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SpannableStringBuilder>() {
                    @Override
                    public void accept(final SpannableStringBuilder s) throws Exception {
                        // Toast.makeText(MainActivity.this,"s"+s, Toast.LENGTH_SHORT).show();
                        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                textView.setText(s);

                            }
                        });


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder((string));
                        stringBuilder.setSpan(new StyleSpan(Typeface.NORMAL), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textView.setText(Html.fromHtml(stringBuilder.toString()));

                    }
                });
    }

    private void identifyLanguage(String text) {
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                isEnglish = false;
                break;
            }
        }

    }
}
