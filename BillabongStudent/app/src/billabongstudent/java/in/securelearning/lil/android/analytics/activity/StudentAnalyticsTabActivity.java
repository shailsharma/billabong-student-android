package in.securelearning.lil.android.analytics.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationParentData;
import in.securelearning.lil.android.analytics.fragment.StudentCoverageFragment;
import in.securelearning.lil.android.analytics.fragment.StudentEffortFragment;
import in.securelearning.lil.android.analytics.fragment.StudentExcellenceFragment;
import in.securelearning.lil.android.analytics.fragment.StudentPerformanceFragment;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsStudentTabwiseBinding;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StudentAnalyticsTabActivity extends AppCompatActivity {

    LayoutAnalyticsStudentTabwiseBinding mBinding;

    @Inject
    AnalyticsModel mAnalyticsModel;
    @Inject
    AppUserModel mAppUserModel;

    ChartConfigurationParentData mChartConfigurationParentData;

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
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_analytics_student_tabwise);

        mAnalyticsModel.setImmersiveStatusBar(getWindow());
        mBinding.layoutProgressBar.setVisibility(View.VISIBLE);
        fetchChartConfiguration();

        initializeClickListeners();
    }

    private void initializeClickListeners() {
        mBinding.layoutToolbar.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mBinding.layoutToolbar.textViewToolbarTitle.setText(getString(R.string.labelAnalytics));
        mBinding.layoutToolbar.textViewToolbarTitle.setGravity(Gravity.START);
        mBinding.layoutToolbar.getRoot().setElevation(0f);
//       if(mAppUserModel.getApplicationUser().getName()!=null)
//        {
//           String name= mAppUserModel.getApplicationUser().getName();
//            mBinding.layoutToolbar.textViewToolbarSubTitle.setVisibility(View.VISIBLE);
//            mBinding.layoutToolbar.textViewToolbarSubTitle.setText(name);
//        }
//       else
//       {
//           mBinding.layoutToolbar.textViewToolbarSubTitle.setVisibility(View.GONE);
//       }


    }

    @SuppressLint("CheckResult")
    private void fetchChartConfiguration() {
        if (GeneralUtils.isNetworkAvailable(this)) {
            mBinding.layoutProgressBar.setVisibility(View.GONE);
            mAnalyticsModel.fetchChartConfiguration().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ChartConfigurationParentData>() {
                        @Override
                        public void accept(ChartConfigurationParentData chartConfigurationParentData) throws Exception {

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
                            fetchChartConfiguration();
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
        mBinding.tabLayout.setTabTextColors(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66),
                ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorHeight(4);
        mBinding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        setTabText();
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
                              //  ((TextView) tabViewChild).setTextAppearance(StudentAnalyticsTabActivity.this, android.R.style.TextAppearance_Large);

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

        private String[] mTabTitles = new String[]{getString(R.string.labelPerformance), getString(R.string.label_efforts),
                getString(R.string.label_excellence), getString(R.string.label_progress)
        };

        public ViewPagerAdapter(FragmentManager fragmentManager) {
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
                return StudentEffortFragment.newInstance();
            } else if (position == 2) {
                return StudentExcellenceFragment.newInstance(mChartConfigurationParentData.getPerformanceConfiguration());
            } else if (position == 3) {
                return StudentCoverageFragment.newInstance(mChartConfigurationParentData.getCoverageConfiguration());
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
