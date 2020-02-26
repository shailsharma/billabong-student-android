package in.securelearning.lil.android.analytics.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.analytics.views.fragment.StudentCoverageFragment;
import in.securelearning.lil.android.analytics.views.fragment.StudentEffortFragment;
import in.securelearning.lil.android.analytics.views.fragment.StudentExcellenceFragment;
import in.securelearning.lil.android.analytics.views.fragment.StudentPerformanceFragment;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCustomAppBarViewpagerBinding;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.utils.AppBarStateChangeListener;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StudentAnalyticsTabActivity extends AppCompatActivity {

    @Inject
    AnalyticsModel mAnalyticsModel;

    @Inject
    AppUserModel mAppUserModel;

    LayoutCustomAppBarViewpagerBinding mBinding;

    GlobalConfigurationParent mChartConfigurationParentData;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, StudentAnalyticsTabActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_custom_app_bar_viewpager);

        CommonUtils.getInstance().setImmersiveStatusBar(getWindow());
        CommonUtils.getInstance().setStatusBarIconsDark(StudentAnalyticsTabActivity.this);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.white));
        mBinding.layoutProgressBar.setVisibility(View.VISIBLE);

        setUpToolbar();

        setStudentDetails();

        fetchChartConfiguration();

    }

    /*set student name and grade-section on header*/
    private void setStudentDetails() {
        mBinding.textViewHeaderTitle.setText(mAppUserModel.getName());
        if (mAppUserModel.getApplicationUser().getGrade() != null && !TextUtils.isEmpty(mAppUserModel.getApplicationUser().getGrade().getName()) && mAppUserModel.getApplicationUser().getSection() != null && !TextUtils.isEmpty(mAppUserModel.getApplicationUser().getSection().getName())) {
            String text = mAppUserModel.getApplicationUser().getGrade().getName() + " (" + mAppUserModel.getApplicationUser().getSection().getName() + ")";
            mBinding.textViewHeaderSubTitle.setText(text);
            mBinding.textViewHeaderSubTitle.setVisibility(View.VISIBLE);
        } else {
            mBinding.textViewHeaderSubTitle.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {

        setSupportActionBar(mBinding.toolbar);
        setTitle(ConstantUtil.BLANK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Picasso.with(getBaseContext()).load(R.drawable.background_analytics_header).fit().centerCrop().into(mBinding.headerImageView);
        mBinding.textViewToolbarTitle.setText(getString(R.string.labelLearningAnalytics));
        mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));
        mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

        mBinding.appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.e("STATE", state.name());
                if (state.name().equalsIgnoreCase(State.COLLAPSED.toString())) {
                    /*collapsed completely*/

                    /*for status bar*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                    } else {
                        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
                    }

                    mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
                    mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));

                    /*For toolbar*/
                    mBinding.toolbar.setNavigationIcon(R.drawable.icon_arrow_left_dark);
                    mBinding.textViewToolbarTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));

                    requestLayout(mBinding.viewPager);

                } else if (state.name().equalsIgnoreCase(State.EXPANDED.toString())) {
                    /* not collapsed*/

                    /*for status bar*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(0);

                    } else {
                        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
                    }


                    mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));
                    mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

                    /*For toolbar*/
                    mBinding.toolbar.setNavigationIcon(R.drawable.arrow_left_white);
                    mBinding.textViewToolbarTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));

                    requestLayout(mBinding.appBarLayout);

                }
            }
        });
    }

    private void requestLayout(View view) {
        view.requestLayout();
    }

    @SuppressLint("CheckResult")
    private void fetchChartConfiguration() {
        if (GeneralUtils.isNetworkAvailable(this)) {
            mBinding.layoutProgressBar.setVisibility(View.GONE);
            mAnalyticsModel.fetchChartConfiguration()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GlobalConfigurationParent>() {
                        @Override
                        public void accept(GlobalConfigurationParent chartConfigurationParentData) throws Exception {

                            mChartConfigurationParentData = chartConfigurationParentData;
                            if (mChartConfigurationParentData != null) {
                                mBinding.textViewNoData.setVisibility(View.GONE);
                                setUpViewPager(0);
                            } else {
                                mBinding.layoutProgressBar.setVisibility(View.GONE);
                                mBinding.textViewNoData.setVisibility(View.VISIBLE);
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();

                            mBinding.layoutProgressBar.setVisibility(View.GONE);
                            mBinding.textViewNoData.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            showInternetSnackBar();
        }

    }

    private void showInternetSnackBar() {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchChartConfiguration();
                    }
                })
                .show();

    }

    private void setUpViewPager(int pagerPosition) {
        mBinding.viewPager.setVisibility(View.VISIBLE);
        StudentAnalyticsTabActivity.ViewPagerAdapter viewPagerAdapter = new StudentAnalyticsTabActivity.ViewPagerAdapter(getSupportFragmentManager());
        mBinding.viewPager.setAdapter(viewPagerAdapter);
        mBinding.viewPager.setCurrentItem(pagerPosition, true);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
    }

    private void setTabText() {

        mBinding.tabLayout.addOnTabSelectedListener
                (new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        ViewGroup vg = (ViewGroup) mBinding.tabLayout.getChildAt(0);
                        ViewGroup vgTab = (ViewGroup) vg.getChildAt(tab.getPosition());
                        int tabChildsCount = vgTab.getChildCount();
                        for (int i = 0; i < tabChildsCount; i++) {
                            View tabViewChild = vgTab.getChildAt(i);
                            if (tabViewChild instanceof TextView) {
                                ((TextView) tabViewChild).setTextSize(20);
                                ((TextView) tabViewChild).setTypeface(null, Typeface.BOLD);
                            }
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        ViewGroup vg = (ViewGroup) mBinding.tabLayout.getChildAt(0);
                        ViewGroup vgTab = (ViewGroup) vg.getChildAt(tab.getPosition());
                        int tabChildsCount = vgTab.getChildCount();
                        for (int i = 0; i < tabChildsCount; i++) {
                            View tabViewChild = vgTab.getChildAt(i);
                            if (tabViewChild instanceof TextView) {
                                ((TextView) tabViewChild).setTextSize(15);
                                ((TextView) tabViewChild).setTypeface(null, Typeface.NORMAL);
                            }
                        }
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] mTabTitles = new String[]{getString(R.string.labelOverall), getString(R.string.label_progress),
                getString(R.string.label_efforts), getString(R.string.label_excellence)};

        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return StudentPerformanceFragment.newInstance(mChartConfigurationParentData.getBenchMarkPerformance());
            } else if (position == 1) {
                return StudentCoverageFragment.newInstance(mChartConfigurationParentData.getCoverageConfiguration(), getBaseContext());
            } else if (position == 2) {
                return StudentEffortFragment.newInstance(mChartConfigurationParentData.getPerformanceConfiguration());
            } else if (position == 3) {
                return StudentExcellenceFragment.newInstance(mChartConfigurationParentData.getPerformanceConfiguration());
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }

    }

}
