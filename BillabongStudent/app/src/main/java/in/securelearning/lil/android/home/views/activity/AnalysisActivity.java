package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutTabViewpagerBinding;
import in.securelearning.lil.android.home.views.fragment.AnalysisActivityFragment;
import in.securelearning.lil.android.home.views.fragment.AnalysisLearningFragment;
import in.securelearning.lil.android.home.views.fragment.AnalysisPerformanceFragment;

/**
 * Created by Rupsi on 6/19/2018.
 */

public class AnalysisActivity extends AppCompatActivity {

    LayoutTabViewpagerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_tab_viewpager);
        setUpToolbar();
        setupViewPager();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
        setTitle(getString(R.string.labelAnalysis));
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AnalysisActivityFragment(), "Activity");
        adapter.addFrag(new AnalysisLearningFragment(), "Learning");
        adapter.addFrag(new AnalysisPerformanceFragment(), "Performance");
        mBinding.viewPager.setAdapter(adapter);

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tabLayout.setTabTextColors(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66),
                ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorHeight(4);
        mBinding.viewPager.setOffscreenPageLimit(0);

    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, AnalysisActivity.class);
        return intent;

    }
}
