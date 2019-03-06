package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutTabViewpagerBinding;
import in.securelearning.lil.android.home.views.fragment.AvailableTrainingsFragment;
import in.securelearning.lil.android.home.views.fragment.MyTrainingsFragment;

/**
 * Created by Chaitendra on 29-Jan-18.
 */

public class TrainingsActivity extends AppCompatActivity {

    LayoutTabViewpagerBinding mBinding;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_tab_viewpager);
        setUpToolbar();
        setUpViewPager();
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
        setTitle(getString(R.string.labelTrainings));
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mBinding.viewPager.setAdapter(viewPagerAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tabLayout.setTabTextColors(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66),
                ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorHeight(4);
        mBinding.tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, TrainingsActivity.class);
        return intent;
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] mTabTitles = new String[]{"Available Trainings", "My Trainings"};

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
                return AvailableTrainingsFragment.newInstance(getColumnCount());
            } else if (position == 1) {
                return MyTrainingsFragment.newInstance(getColumnCount());
            } else {
                return null;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    private int getColumnCount() {
        if (getResources().getBoolean(R.bool.isTablet)) {
            return 2;
        } else {
            return 1;
        }
    }

}
