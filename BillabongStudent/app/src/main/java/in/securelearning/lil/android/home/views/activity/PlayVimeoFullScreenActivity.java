package in.securelearning.lil.android.home.views.activity;

/**
 * Created by Chaitendra on 12-Mar-18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import org.apache.cordova.CordovaActivity;

public class PlayVimeoFullScreenActivity extends CordovaActivity {

    private static final String URL = "url";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void handleIntent() {
        if (getIntent() != null) {
            String url = getIntent().getStringExtra(URL);

            if (!TextUtils.isEmpty(url)) {
                String[] urlId = url.split("/");
                if (urlId.length > 0) {
                    String id = url.replace("https://vimeo.com/", "");
                    loadUrl("file:///android_asset/vimeoIndex.html?id=" + id);
                    return;
                }

            }
        } else {
            finish();
        }
    }

    public static Intent getStartIntent(Context context, String url) {
        Intent intent = new Intent(context, PlayVimeoFullScreenActivity.class);
        intent.putExtra(URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

}
