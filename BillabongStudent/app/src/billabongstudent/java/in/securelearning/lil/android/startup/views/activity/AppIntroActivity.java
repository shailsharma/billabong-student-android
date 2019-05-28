package in.securelearning.lil.android.startup.views.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAppIntroBinding;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.startup.views.adapters.IntroPagerAdapter;

public class AppIntroActivity extends AppCompatActivity {

    LayoutAppIntroBinding mBinding;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppPrefs.isLoggedIn(AppIntroActivity.this)) {

            finish();
            startActivity(LoginActivity.getStartIntent(getBaseContext(), getIntent().getAction()));
            overridePendingTransition(0, 0);
        } else {
            mBinding = DataBindingUtil.setContentView(this, R.layout.layout_app_intro);
            setUpViewPager();
        }
    }

    private void setUpViewPager() {
        final int[] drawables = new int[]{
                R.drawable.intro_image_one,
                R.drawable.intro_image_two,
                R.drawable.intro_image_three,
                R.drawable.intro_image_four};

        mBinding.viewPagerIntro.setAdapter(new IntroPagerAdapter(getBaseContext(), drawables));

        mBinding.viewPagerIntro.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position != drawables.length - 1) {
                    mBinding.buttonIntroGetStarted.setText(getString(R.string.next));
                } else {
                    mBinding.buttonIntroGetStarted.setText(getString(R.string.labelGetStarted));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBinding.buttonIntroGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.viewPagerIntro.getCurrentItem() != drawables.length - 1) {
                    mBinding.viewPagerIntro.setCurrentItem(mBinding.viewPagerIntro.getCurrentItem() + 1);
                } else {
                    finish();
                    startActivity(LoginActivity.getStartIntent(getBaseContext(), Intent.ACTION_MAIN));
                }
            }
        });

    }
}
