package in.securelearning.lil.android.login.views.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import javax.inject.Inject;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.model.CorporateSettingsModel;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.home.views.activity.NavigationDrawerActivity;
import in.securelearning.lil.android.login.InjectorLogin;
import in.securelearning.lil.android.app.R;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


public class SplashActivity extends AppCompatActivity {
    final String JSON_FOLDER = "json";
    @Inject
    CorporateSettingsModel mCorporateSettingsModel;
    private long SPLASH_DISPLAY_LENGTH = 3000;
    private AppCompatImageView mAppLogoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        InjectorLogin.INSTANCE.getComponent().inject(this);

        initializeViews();
        autoSkipSplash();


        Observable.just(SplashActivity.this).subscribe(new Consumer<SplashActivity>() {
            @Override
            public void accept(SplashActivity splashActivity) {
                /*create Base folder for jsons*/
                FileUtils.createBaseAppFolder(SplashActivity.this, JSON_FOLDER);

            }
        });


        // checkIfCorporateProfileIsExists();


    }


    private void checkIfCorporateProfileIsExists() {

//        rx.Observable.just(SplashActivity.this).subscribe(new Consumer<SplashActivity>() {
//            @Override
//            public void accept(SplashActivity splashActivity) {
//                if (AppPrefs.isLoggedIn(SplashActivity.this)) {
//                    String associationId = Injector.INSTANCE.getComponent().appUserModel().getApplicationUser().getAssociationId();
//                    if (AppPrefs.getCorporateSettings(SplashActivity.this) != null) {
//                        //  updateCorporateProfile();
//                    }
//                }
//
//            }
//        });
    }

    private void updateCorporateProfile() {

        // TODO: 11/19/2016  Update splashactivity theme here

        String base64 = AppPrefs.getCorporateSettings(SplashActivity.this).getCompanyLogo();
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ((ImageView) findViewById(R.id.imageview_app_logo_splash)).setImageBitmap(decodedByte);

    }

    private void autoSkipSplash() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                if (!AppPrefs.isLoggedIn(SplashActivity.this)) {

                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                View sharedView = mAppLogoImageView;
                String transitionName = getString(R.string.shared_animation);
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, mAppLogoImageView, mAppLogoImageView.getTransitionName());
//                startActivity(mainIntent);
                startActivity(mainIntent, transitionActivityOptions.toBundle());
                finish();
//                } else {
//                       /* Create an Intent that will start the Menu-Activity. */
//                    Intent mainIntent = new Intent(SplashActivity.this, NavigationDrawerActivity.class);
//                    startActivity(mainIntent);
//                    finish();
//                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    /**
     * find ids of views
     */
    private void initializeViews() {
        mAppLogoImageView = (AppCompatImageView) findViewById(R.id.imageview_app_logo_splash);

    }


}
