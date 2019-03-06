package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSampleWebViewBinding;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.FlavorHomeModel;

public class SampleWebActivity extends AppCompatActivity {

    LayoutSampleWebViewBinding mBinding;

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SampleWebActivity.class);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_sample_web_view);
        InjectorHome.INSTANCE.getComponent().inject(this);
        String url = "https://reader.lightsailed.com/Reader/index.html?jwtToken=" + AppPrefs.getIdToken(getBaseContext());
        mBinding.webView.loadUrl(url);
//        new FinestWebView.Builder(this).titleDefault("The Finest Artist")
//                .show("https://reader.lightsailed.com/Reader/index.html?jwtToken=" + AppPrefs.getIdToken(getBaseContext()));
//        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
//            mFlavorHomeModel.getTodayRecaps().subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<LessonPlanMinimal>>() {
//                        @Override
//                        public void accept(ArrayList<LessonPlanMinimal> lessonPlanMinimals) throws Exception {
//                            String url = "https://reader.lightsailed.com/Reader/index.html?jwtToken=" + AppPrefs.getIdToken(getBaseContext());
//                            Log.e("LightSail", url);
//                            mBinding.webView.loadUrl(url);
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            String url = "https://reader.lightsailed.com/Reader/index.html?jwtToken=" + AppPrefs.getIdToken(getBaseContext());
//                            Log.e("LightSail", url);
//                            mBinding.webView.loadUrl(url);
//                            throwable.printStackTrace();
//                        }
//
//                    });
//        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
