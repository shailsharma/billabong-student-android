package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import android.media.MediaPlayer;

import in.securelearning.lil.android.app.R;


/**
 * Created by Chaitendra on 23-May-17.
 */

public class SoundUtils {

    public static final int LEARNING_NETWORK_POST_ADDED = R.raw.network_post_added;
    public static final int LEARNING_NETWORK_POST_RESPONSE_ADDED = R.raw.network_post_added;
    public static final int LEARNING_NETWORK_NEW_POST = R.raw.network_new_post;
    public static final int LEARNING_NETWORK_NEW_POST_RESPONSE = R.raw.network_new_post_response;
    public static final int LEARNING_NETWORK_POST_LIKE = R.raw.network_post_like;
    public static final int LEARNING_NETWORK_POST_FAVORITE = R.raw.network_post_favorite;
    public static final int QUIZ_CORRECT_ANSWER = R.raw.quiz_correct_answer;
    public static final int QUIZ_INCORRECT_ANSWER = R.raw.quiz_incorrect_answer;

    public static void playSound(Context context, int source) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, source);
        mediaPlayer.start();
    }

}
