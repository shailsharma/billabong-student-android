package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.Serializable;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutYoutubePlayerBinding;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.resources.utils.DeveloperKey;

/**
 * Created by Chaitendra on 07-Dec-17.
 */

public class PlayYouTubeFullScreenActivity extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener {

    LayoutYoutubePlayerBinding mBinding;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_youtube_player);
        View decorView = getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        handleIntent();
    }

    protected void handleIntent() {
        //  super.handleIntent(getStartIntent());
        Serializable serializable = getIntent().getExtras().getSerializable("resource");
        if (serializable != null) {
            VideoFragment videoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
            videoFragment.setVideoId(((FavouriteResource) serializable).getName());
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    public static Intent getStartIntent(Context context, FavouriteResource favouriteResourceData) {
        Intent intent = new Intent(context, PlayYouTubeFullScreenActivity.class);
        intent.putExtra("resource", favouriteResourceData);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getStartIntent(Context context, FavouriteResource favouriteResourceData, boolean isConnectivityCheck) {
        Intent intent = new Intent(context, PlayYouTubeFullScreenActivity.class);
        intent.putExtra("resource", favouriteResourceData);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onFullscreen(boolean b) {
        View decorView = getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static final class VideoFragment extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                videoId = getArguments().getString("videoId");
            }
            initialize(DeveloperKey.DEVELOPER_KEY, this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (player != null) {
                player.release();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if (player != null) {
                player.loadVideo(videoId);
//                player.cueVideo(videoId);
            }
        }

        public void setVideoId(String videoId) {
            if (videoId != null && !videoId.equals(this.videoId)) {
                this.videoId = videoId;
                if (player != null) {
                    try {
                        player.loadVideo(videoId);
//                        player.cueVideo(videoId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        @Override
        public void onInitializationSuccess(Provider provider, final YouTubePlayer player, boolean restored) {
            this.player = player;
//            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
//            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
            player.setOnFullscreenListener((PlayYouTubeFullScreenActivity) getActivity());
            player.setShowFullscreenButton(false);
            if (!restored && videoId != null && !(TextUtils.isEmpty(videoId))) {
                try {
                    player.loadVideo(videoId);
//                    player.cueVideo(videoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.player = player;
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
            }); //set player state change listener
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            this.player = null;
        }

    }

}
