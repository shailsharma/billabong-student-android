package in.securelearning.lil.android.home.views.activity;

/**
 * Created by Chaitendra on 12-Mar-18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import org.apache.cordova.CordovaActivity;


/**
 * Created by Prabodh Dhabaria on 06-12-2016.
 */

public class PlayVimeoFullScreenActivity extends CordovaActivity {

    private static final String URL = "url";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        }
        finish();
    }

    public static Intent getStartIntent(Context context, String url) {
        Intent intent = new Intent(context, PlayVimeoFullScreenActivity.class);
        intent.putExtra(URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

}
