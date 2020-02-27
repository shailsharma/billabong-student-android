package in.securelearning.lil.android.player.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import in.securelearning.lil.android.player.view.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

public final class YouTubeVideoFragment extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayer mPlayer;
    private String mVideoId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideoId = getArguments().getString("mVideoId");
        }
        initialize(ConstantUtil.YOUTUBE_SECRET_KEY, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayer != null) {
            mPlayer.loadVideo(mVideoId);
        }
    }

    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(this.mVideoId)) {
            this.mVideoId = videoId;
            if (mPlayer != null) {
                try {
                    mPlayer.loadVideo(videoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player, boolean restored) {
        this.mPlayer = player;
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        player.setOnFullscreenListener((PlayYouTubeFullScreenActivity) getActivity());
        player.setShowFullscreenButton(false);
        if (!restored && mVideoId != null && !(TextUtils.isEmpty(mVideoId))) {
            try {
                player.loadVideo(mVideoId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.mPlayer = player;
        player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String videoId) {
                if (!TextUtils.isEmpty(videoId)) {
                    try {
                        player.play(); //auto play
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        }); //set mPlayer state change listener
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        this.mPlayer = null;
    }

}