package in.securelearning.lil.android.resources.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.Serializable;
import java.util.Timer;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.YoutubePlayActivityBinding;
import in.securelearning.lil.android.assignments.views.activity.AssignActivity;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.dataobjects.CourseAnalytics;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.IdType;
import in.securelearning.lil.android.base.events.GenerateSubmissionEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.resources.utils.DeveloperKey;
import in.securelearning.lil.android.resources.view.InjectorYoutube;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@TargetApi(13)
public final class YoutubePlayActivity extends Activity implements OnFullscreenListener {

    @Inject
    AppUserModel mAppUserModel;

    private static final int ANIMATION_DURATION_MILLIS = 300;
    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private VideoFragment videoFragment;

    private boolean isFullscreen = false;
    private YoutubePlayActivityBinding mBinding;
    private int color;
    private FavouriteResource mFavouriteResource;
    private String mAssignmentResponseId = "";
    Timer navigationBarHandler = null;
    private static CourseAnalytics mCourseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorYoutube.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.youtube_play_activity);
        Serializable serializable = getIntent().getExtras().getSerializable("resource");
        mAssignmentResponseId = getIntent().getExtras().getString("isSaveEvent");
        if (serializable instanceof FavouriteResource) {
            color = PrefManager.getColorForSubject(YoutubePlayActivity.this, ((FavouriteResource) serializable).getMetaInformation().getSubject().getId());
            if (!TextUtils.isEmpty(((FavouriteResource) serializable).getTitle()))
                mBinding.textView.setText(((FavouriteResource) serializable).getTitle());
            if (!TextUtils.isEmpty(((FavouriteResource) serializable).getMetaInformation().getSubject().getName())) {
                mBinding.textViewSubejct.setText(((FavouriteResource) serializable).getMetaInformation().getSubject().getName());
                mBinding.textViewSubejct.setTextColor(color);
            }
            if (!TextUtils.isEmpty(((FavouriteResource) serializable).getMetaInformation().getTopic().getName())) {
                mBinding.textViewTopic.setText(((FavouriteResource) serializable).getMetaInformation().getTopic().getName());
            }
            if (!TextUtils.isEmpty(((FavouriteResource) serializable).getMetaInformation().getGrade().getName())) {
                mBinding.layoutGrade.setVisibility(View.VISIBLE);
                mBinding.textViewGrade.setText(((FavouriteResource) serializable).getMetaInformation().getGrade().getName());
                mBinding.textViewGrade.setTextColor(color);
            } else {
                mBinding.layoutGrade.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(((FavouriteResource) serializable).getMetaInformation().getLearningLevel().getName())) {
                mBinding.layoutLearningLavel.setVisibility(View.VISIBLE);
                mBinding.textViewLearningLavel.setText(((FavouriteResource) serializable).getMetaInformation().getLearningLevel().getName());
                mBinding.textViewLearningLavel.setTextColor(color);
            } else {
                mBinding.layoutLearningLavel.setVisibility(View.GONE);
            }
            videoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);

            mBinding.videoBox.setVisibility(View.INVISIBLE);

            layout(getResources().getConfiguration().orientation);

            checkYouTubeApi();

            if (!TextUtils.isEmpty(((FavouriteResource) serializable).getName())) {
                videoFragment.setVideoId(((FavouriteResource) serializable).getName());
                videoFragment.setVideoResource(((FavouriteResource) serializable));
                videoFragment.setGeneratedSubmittedEvent(mAssignmentResponseId);
            }

            // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
            if (mBinding.videoBox.getVisibility() != View.VISIBLE) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Initially translate off the screen so that it can be animated in from below.
                    mBinding.videoBox.setTranslationY(mBinding.videoBox.getHeight());
                }
                mBinding.videoBox.setVisibility(View.VISIBLE);
            }

            if (mBinding.videoBox.getTranslationY() > 0) {
                mBinding.videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
            }
            assignResource(((FavouriteResource) serializable));
        } else {
            finish();
        }
    }

    public static Intent getStartIntent(Context context, FavouriteResource favouriteResourceData, String isSaveEvent) {
        Intent intent = new Intent(context, YoutubePlayActivity.class);
        intent.putExtra("resource", favouriteResourceData);
        intent.putExtra("isSaveEvent", isSaveEvent);
        return intent;
    }

    private void assignResource(final FavouriteResource favouriteResource) {
        if (PermissionPrefsCommon.getDashboardTeacherViewPermission(this)) {
            mBinding.buttonAssignResource.setVisibility(View.VISIBLE);
            mBinding.buttonAssignResource.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = AssignActivity.getLaunchIntentForResource(getBaseContext(),
                            favouriteResource.getObjectId(),
                            favouriteResource.getTitle(),
                            favouriteResource.getType(),
                            favouriteResource.getMetaInformation(),
                            favouriteResource.getUrlMain(),
                            favouriteResource.getUrlThumbnail(),
                            favouriteResource.getDuration(),
                            favouriteResource.getName());
                    startActivity(mIntent);
                }
            });
        } else {
            mBinding.buttonAssignResource.setVisibility(View.GONE);
        }
    }

    private void checkYouTubeApi() {
        YouTubeInitializationResult errorReason =
                YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            String errorMessage =
                    String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            recreate();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        layout(newConfig.orientation);
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        View decorView = getWindow().getDecorView();
        if (isFullscreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mBinding.buttonAssignResource.setVisibility(View.GONE);
            layout(Configuration.ORIENTATION_LANDSCAPE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (PermissionPrefsCommon.getDashboardTeacherViewPermission(this)) {
                mBinding.buttonAssignResource.setVisibility(View.VISIBLE);
            } else {
                mBinding.buttonAssignResource.setVisibility(View.GONE);
            }
            layout(Configuration.ORIENTATION_PORTRAIT);
            decorView.setSystemUiVisibility(View.VISIBLE);
        }
    }

    private void layout(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(mBinding.videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.TOP);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBinding.videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSizeAndGravity(mBinding.videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
            isFullscreen = true;
        } else {
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(mBinding.videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.TOP);
        }
    }

    @TargetApi(16)
    private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            animator.withEndAction(runnable);
        } else {
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mCourseAnalytics != null) {
            Log.e("isVideoClosed--", "Yes");
            mCourseAnalytics.setEndTime(DateUtils.getCurrentISO8601DateString());
            mCourseAnalytics.setObjectId("");
            mCourseAnalytics = Injector.INSTANCE.getComponent().courseAnalyticsModel().saveObject(mCourseAnalytics);
        }
        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            onFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    public static final class VideoFragment extends YouTubePlayerFragment
            implements OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;
        private FavouriteResource favouriteResource;
        private String mAssignmentResponseId;
        @Inject
        RxBus mRxBus;

        @Inject
        AppUserModel mAppUserModel;

        public static VideoFragment newInstance(String videoId, FavouriteResource favouriteResource, boolean genertedSubmittedEvent) {
            VideoFragment fragment = new VideoFragment();
            Bundle args = new Bundle();
            args.putString("videoId", videoId);
            args.putSerializable("favouriteResource", favouriteResource);
            args.putBoolean("genertedSubmittedEvent", genertedSubmittedEvent);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            InjectorYoutube.INSTANCE.getComponent().inject(this);
            if (getArguments() != null) {
                videoId = getArguments().getString("videoId");
                favouriteResource = (FavouriteResource) getArguments().getSerializable("favouriteResource");
                mAssignmentResponseId = getArguments().getString("genertedSubmittedEvent");
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

        public void setVideoResource(FavouriteResource favouriteResource) {
            if (favouriteResource != null) {
                this.favouriteResource = favouriteResource;
            }
        }

        public void setGeneratedSubmittedEvent(String id) {
            mAssignmentResponseId = id;
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        @Override
        public void onInitializationSuccess(Provider provider, final YouTubePlayer player, boolean restored) {
            this.player = player;
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
            player.setOnFullscreenListener((YoutubePlayActivity) getActivity());

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
                    if (!TextUtils.isEmpty(videoId) && player != null) {
                        try {
                            player.play(); //auto play
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // TODO: 26-08-2017 here we are updating assignmentResponse for due/overdue to submitted as after video play
                    if (!TextUtils.isEmpty(mAssignmentResponseId) && mAssignmentResponseId != null) {
                        mRxBus.send(new GenerateSubmissionEvent(mAssignmentResponseId));
                    }
                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {
                    Log.e("isVideoStarted--", "Yes");
                    mCourseAnalytics = new CourseAnalytics();
                    mCourseAnalytics.setUserId(mAppUserModel.getObjectId());
                    IdType video = new IdType();
                    video.setId(videoId);
                    video.setType("video");
                    mCourseAnalytics.setVideo(video);
                    mCourseAnalytics.setStartTime(DateUtils.getCurrentISO8601DateString());

                }

                @Override
                public void onVideoEnded() {
                    Log.e("isVideoEnded--", "Yes");
                    mCourseAnalytics.setEndTime(DateUtils.getCurrentISO8601DateString());
                    mCourseAnalytics.setCompleted(true);
                    mCourseAnalytics = Injector.INSTANCE.getComponent().courseAnalyticsModel().saveObject(mCourseAnalytics);

                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });
        }

        @Override
        public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
            this.player = null;
        }

    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }


}

