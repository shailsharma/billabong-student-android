package in.securelearning.lil.android.player.view.activity;

/**
 * Created by Chaitendra on 12-Mar-18.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import org.apache.cordova.CordovaActivity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import fi.iki.elonen.NanoHTTPD;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

public class PlayVimeoFullScreenActivity extends CordovaActivity {

    @Inject
    PlayerModel mPlayerModel;

    private LocalWebServer mLocalWebServer;
    private static final String PLAYBACK_VALUE = "playbackValue";
    private String mVideoId;

    private long mStartTime;
    private String mResourceId;
    private String mModuleId, mModuleName;

    public static final String MODULE_ID = "moduleId";
    public static final String MODULE_NAME = "moduleName";
    public static final String RESOURCE_ID = "resourceId";

    public static Intent getStartIntent(Context baseContext, String moduleId, String moduleName, String resourceId, String playbackValue) {
        Intent intent = new Intent(baseContext, PlayVimeoFullScreenActivity.class);
        intent.putExtra(MODULE_ID, moduleId);
        intent.putExtra(MODULE_NAME, moduleName);
        intent.putExtra(RESOURCE_ID, resourceId);
        intent.putExtra(PLAYBACK_VALUE, playbackValue);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        handleIntent();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mStartTime = System.currentTimeMillis();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mLocalWebServer != null) {
            mLocalWebServer.stop();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        uploadVideoWatchState(false);

        if (!TextUtils.isEmpty(mModuleId)
                && !TextUtils.isEmpty(mModuleName)
                && !TextUtils.isEmpty(mResourceId)) {

            long endTime = System.currentTimeMillis();

            String resourceType = getString(R.string.typeVimeoVideo);

            mPlayerModel.uploadUserTimeSpent(mModuleId, mModuleName, mResourceId, resourceType, mStartTime, endTime);

        }

        if (mLocalWebServer != null) {
            mLocalWebServer.stop();
        }
    }

    private void handleIntent() {
        if (getIntent() != null) {
            String playbackValue = getIntent().getStringExtra(PLAYBACK_VALUE);
            mModuleId = getIntent().getStringExtra(MODULE_ID);
            mModuleName = getIntent().getStringExtra(MODULE_NAME);
            mResourceId = getIntent().getStringExtra(RESOURCE_ID);
            extractIdFromUrl(playbackValue);
            uploadVideoWatchState(true);
        } else {
            closeActivity();
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

    /*Extracting videoId form url if playbackValue is an url value
    or playbackValue is not an url then playing directly.*/
    private void extractIdFromUrl(String playbackValue) {
        if (!TextUtils.isEmpty(playbackValue)) {
            if (playbackValue.startsWith("http:") || playbackValue.startsWith("https:")) {
                Pattern VIMEO_PATTERN = Pattern.compile("[http|https]+:\\/\\/(?:www\\.|)vimeo\\.com\\/([a-zA-Z0-9_\\-]+)(&.+)?", Pattern.CASE_INSENSITIVE);
                Matcher matcher = VIMEO_PATTERN.matcher(playbackValue);
                if (matcher.find()) {
                    mVideoId = matcher.group(1);
                    startServerAndPlayVideo();
                } else {
                    closeActivity();
                }
            } else {
                mVideoId = playbackValue;
                startServerAndPlayVideo();
            }

        } else {
            closeActivity();
        }
    }

    /*Initialising local web server and
    loading custom url to cordova webView.*/
    private void startServerAndPlayVideo() {
        try {
            mLocalWebServer = new LocalWebServer(ConstantUtil.VIMEO_LOCAL_PORT);
            mLocalWebServer.start();

            loadUrl("http://localhost:" + mLocalWebServer.getListeningPort() + "/vimeoPlayer");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*To close/finish activity with toast.*/
    private void closeActivity() {
        finish();
        GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
    }


    private class LocalWebServer extends NanoHTTPD {

        private LocalWebServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();

            if (uri.equals("/vimeoPlayer")) {
                String response = "<html>" +
                        "<body style='margin: 0; display: flex;background: rgba(0,0,0,1)'>" +
                        "<iframe style='height: auto; width: 100vw; display: flex; flex: 1;align-items: center; justify-content:center;' " +
                        "src=\"https://mPlayer.vimeo.com/video/" + mVideoId + "?autoplay=true&controls=true&loop=true&title=false" +
                        "width=\"640\" height=\"360\" " +
                        "frameborder=\"0\"\n" +
                        "allow=\"autoplay; fullscreen\" allowfullscreen></iframe>" +
                        "</body>" +
                        "</html>";
                return newFixedLengthResponse(response);
            }
            return null;
        }

    }

}