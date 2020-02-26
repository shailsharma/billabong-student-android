
package in.securelearning.lil.android.player.view.activity;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.google.android.youtube.player.YouTubePlayer;

import java.io.Serializable;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutYoutubePlayerBinding;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.player.view.fragment.YouTubeVideoFragment;

/**
 * Created by Chaitendra on 07-Dec-17.
 */

public class PlayYouTubeFullScreenActivity extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener {

    @Inject
    PlayerModel mPlayerModel;

    LayoutYoutubePlayerBinding mBinding;

    public static final String MODULE_ID = "moduleId";
    public static final String MODULE_NAME = "moduleName";

    long mStartTime;
    private String mResourceId;
    private String mModuleId, mModuleName;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        uploadVideoWatchState(false);


        if (!TextUtils.isEmpty(mModuleId)
                && !TextUtils.isEmpty(mModuleName)
                && !TextUtils.isEmpty(mResourceId)) {

            long endTime = System.currentTimeMillis();

            String resourceType = getString(R.string.typeYouTubeVideo);

            mPlayerModel.uploadUserTimeSpent(mModuleId, mModuleName, mResourceId, resourceType, mStartTime, endTime);

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);

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
        if (getIntent() != null && getIntent().getExtras() != null) {

            Serializable serializable = getIntent().getExtras().getSerializable("resource");

            if (serializable != null) {
                YouTubeVideoFragment youTubeVideoFragment = (YouTubeVideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
                youTubeVideoFragment.setVideoId(((FavouriteResource) serializable).getName());

                mResourceId = ((FavouriteResource) serializable).getObjectId();
                mModuleId = getIntent().getStringExtra(MODULE_ID);
                mModuleName = getIntent().getStringExtra(MODULE_NAME);
                mStartTime = System.currentTimeMillis();

                uploadVideoWatchState(true);

            } else {
                GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
                onBackPressed();
            }
        } else {
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
            onBackPressed();
        }

    }


    public static Intent getStartIntent(Context context, String moduleId, String moduleName, FavouriteResource favouriteResourceData) {
        Intent intent = new Intent(context, PlayYouTubeFullScreenActivity.class);
        intent.putExtra("resource", favouriteResourceData);
        intent.putExtra(MODULE_ID, moduleId);
        intent.putExtra(MODULE_NAME, moduleName);
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

    /*Upload video watch status*/
    private void uploadVideoWatchState(boolean isWatchStarted) {

        if (!TextUtils.isEmpty(mModuleId)
                && !TextUtils.isEmpty(mResourceId)) {
            /*Make call for video watch end*/
            mPlayerModel.uploadVideoWatchState(isWatchStarted, mResourceId, mModuleId);
        }
    }


}
