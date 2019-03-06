package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.player.microlearning.InjectorPlayer;
import in.securelearning.lil.android.player.microlearning.events.SpeakCompletedEvent;

/**
 * Created by Chaitendra on 28-06-2018.
 */
public class TextToSpeechUtils extends UtteranceProgressListener implements TextToSpeech.OnInitListener {
    private TextToSpeech mTts;
    private int mDuration, mIndex;
    private boolean mReady = false;
    private boolean mAllowed = false;
    public static final String NO_CARD_CAPTION = "No reading material available for this card.";
    public static final String MANDATORY_CARD = "This card is mandatory.";
    String[] mWords;
    public String[] mSentence;
    @Inject
    public RxBus mRxBus;

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            if (mTts != null) {
                mReady = true;
                if (mTts.isLanguageAvailable(new Locale("hi_IN")) == TextToSpeech.LANG_AVAILABLE) {
                    mTts.setLanguage(new Locale("hi_IN"));
                    mTts.setOnUtteranceProgressListener(this);
                } else {
                    mTts.setLanguage(Locale.US);
                    mTts.setOnUtteranceProgressListener(this);
                }
            }
        } else {
            mReady = false;
        }
    }

    public TextToSpeechUtils(Context context) {
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        mTts = new TextToSpeech(context, this);
    }

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
                for (int i = 0; i < mWords.length; i++) {
                    mTts.speak(mWords[i], TextToSpeech.QUEUE_ADD, hash);
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
                for (int i = 0; i < mSentence.length; i++) {
                    hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "FINISH");
                    mTts.speak(mSentence[i], TextToSpeech.QUEUE_ADD, hash);

                }
            } else {
                mTts.speak(NO_CARD_CAPTION, TextToSpeech.QUEUE_FLUSH, hash);
            }
        }
    }

    public void speakParagraph(String text) {

        if (mReady && mAllowed) {
            HashMap<String, String> hash = new HashMap<String, String>();
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
        setDuration(1500);
        mTts.playSilence(getDuration(), TextToSpeech.QUEUE_FLUSH, null);

    }

    public void destroy() {
        mTts.shutdown();
    }

    @Override
    public void onStart(String s) {

    }

    @Override
    public void onDone(String s) {
//        if (mIndex == mSentence.length) {
//            mRxBus.send(new SpeakCompletedEvent(true));
//        }
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
            destroy();
            Log.d("TTS", "TTS destroyed");
        }
    }

    public void readMandatoryInstruction() {

        if (mReady && mAllowed) {
            mTts.speak(NO_CARD_CAPTION, TextToSpeech.QUEUE_ADD, null);

        }

    }
}
