package in.securelearning.lil.android.player.view.activity;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutVideoPlayerBinding;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

public class PlayVideoFullScreenActivity extends AppCompatActivity implements Player.EventListener {

    @Inject
    PlayerModel mPlayerModel;

    LayoutVideoPlayerBinding mBinding;

    public static final String RESOURCE = "resource";
    public static final String APP_DATA = "app_data";
    public static final String MODULE_ID = "moduleId";
    public static final String MODULE_NAME = "moduleName";
    public static final String NETWORK_TYPE = "network_type";
    public static final String NETWORK_TYPE_LOCAL = "network_type_local";
    public static final String NETWORK_TYPE_ONLINE = "network_type_online";

    private String mVideoUri;
    private SimpleExoPlayer mPlayer;
    private Handler mHandler;
    private Runnable mRunnable;
    private long mStartTime;
    private String mResourceId;
    private String mModuleId, mModuleName;

    public static Intent getStartActivityIntent(Context context, String moduleId, String moduleName, String network_type, Resource resource) {
        Intent intent = new Intent(context, PlayVideoFullScreenActivity.class);
        Bundle appData = new Bundle();
        appData.putString(MODULE_ID, moduleId);
        appData.putString(MODULE_NAME, moduleName);
        appData.putString(NETWORK_TYPE, network_type);
        appData.putSerializable(RESOURCE, resource);
        intent.putExtra(APP_DATA, appData);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        keepScreenOn();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        final View decorView = getWindow().getDecorView();

        getWindow().getDecorView().setSystemUiVisibility(flags);

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });

        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_video_player);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        handleIntent();

    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();

        uploadVideoWatchState(false);

        if (!TextUtils.isEmpty(mModuleId)
                && !TextUtils.isEmpty(mModuleName)
                && !TextUtils.isEmpty(mResourceId)) {

            long endTime = System.currentTimeMillis();

            String resourceType = getString(R.string.typeVideo);

            mPlayerModel.uploadUserTimeSpent(mModuleId, mModuleName, mResourceId, resourceType, mStartTime, endTime);

        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

            case Player.STATE_BUFFERING:
                mBinding.progressBar.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                allowScreenOff();
                mBinding.progressBar.setVisibility(View.GONE);
                break;
            case Player.STATE_IDLE:
                keepScreenOn();
                break;
            case Player.STATE_READY:
                keepScreenOn();
                mBinding.progressBar.setVisibility(View.GONE);
                break;
            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private void handleIntent() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            Bundle appData = getIntent().getBundleExtra(APP_DATA);
            if (appData != null) {
                Resource resource = (Resource) appData.getSerializable(RESOURCE);
                mModuleId = appData.getString(MODULE_ID);
                mModuleName = appData.getString(MODULE_NAME);

                if (resource != null && !TextUtils.isEmpty(resource.getUrlMain())) {

                    mResourceId = resource.getObjectId();
                    mVideoUri = resource.getUrlMain();

                    setUp();
                } else {
                    finishActivityWithMessage(getString(R.string.error_something_went_wrong));
                }

            } else {
                finishActivityWithMessage(getString(R.string.error_something_went_wrong));
            }
        } else {
            finishActivityWithMessage(getString(R.string.error_message_no_internet));
        }


    }

    /*Upload video watch status*/
    private void uploadVideoWatchState(boolean isWatchStarted) {

        if (!TextUtils.isEmpty(mModuleId)
                && !TextUtils.isEmpty(mResourceId)) {
            /*Make call for video watch end*/
            mPlayerModel.uploadVideoWatchState(isWatchStarted, mResourceId, mModuleId);
        }
    }


    private void finishActivityWithMessage(String message) {
        GeneralUtils.showToastShort(getBaseContext(), message);
        finish();
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void allowScreenOff() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setUp() {
        initializePlayer();
        if (mVideoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(mVideoUri));
    }

    private void initializePlayer() {
        if (mPlayer == null) {
            // 1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    ConstantUtil.MIN_BUFFER_DURATION,
                    ConstantUtil.MAX_BUFFER_DURATION,
                    ConstantUtil.MIN_PLAYBACK_START_BUFFER,
                    ConstantUtil.MIN_PLAYBACK_RESUME_BUFFER, -1, true);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            // 2. Create the mPlayer
            mPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
            mBinding.videoFullScreenPlayer.setPlayer(mPlayer);
        }


    }

    private void buildMediaSource(Uri mUri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        // Prepare the mPlayer with the source.
        mPlayer.prepare(videoSource);
        mPlayer.setPlayWhenReady(true);
        mPlayer.addListener(this);

        uploadVideoWatchState(true);

        mStartTime = System.currentTimeMillis();


    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;

        }
    }

    private void pausePlayer() {
        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(false);
            mPlayer.getPlaybackState();
        }
    }

    private void resumePlayer() {
        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(true);
            mPlayer.getPlaybackState();
        }
    }


}
