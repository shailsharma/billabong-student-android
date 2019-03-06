//package in.securelearning.lil.android.home.views.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.databinding.DataBindingUtil;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.view.MenuItem;
//
//import java.util.Date;
//
//import in.securelearning.lil.android.app.R;
//import in.securelearning.lil.android.app.databinding.LayoutClassPlannerBinding;
//import in.securelearning.lil.android.base.utils.DateUtils;
//import in.securelearning.lil.android.home.views.fragment.SampleClassPlannerFragment;
//
///**
// * Created by Chaitendra on 10-Feb-18.
// */
//
//public class SampleClassPlannerActivity extends AppCompatActivity {
//
//    LayoutClassPlannerBinding mBinding;
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_class_planner);
//        setUpToolbar();
//        setUpViewPager();
//        mBinding.textViewDate.setText("Today, " + DateUtils.getFormatedDateFromDate(new Date()));
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void setUpToolbar() {
//        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
//        setTitle(getString(R.string.labelClassPlanner));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
//
//    private void setUpViewPager() {
//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        mBinding.viewPager.setAdapter(viewPagerAdapter);
//        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
//        mBinding.tabLayout.setTabTextColors(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55),
//                ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
//        mBinding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
//        mBinding.tabLayout.setSelectedTabIndicatorHeight(4);
//        mBinding.viewPager.setOffscreenPageLimit(3);
//    }
//
//    public static Intent getStartIntent(Context context) {
//        Intent intent = new Intent(context, SampleClassPlannerActivity.class);
//        return intent;
//    }
//
//    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
//
//        private String[] mTabTitles = new String[]{"VI A Bio", "VI B Bio", "VI C Bio"};
//
//        public ViewPagerAdapter(FragmentManager fragmentManager) {
//            super(fragmentManager);
//        }
//
//        @Override
//        public int getCount() {
//            return mTabTitles.length;
//
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//
//            if (position == 0) {
//                return SampleClassPlannerFragment.newInstance();
//            } else if (position == 1) {
//                return SampleClassPlannerFragment.newInstance();
//            } else if (position == 2) {
//                return SampleClassPlannerFragment.newInstance();
//            } else {
//                return null;
//            }
//        }
//
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mTabTitles[position];
//        }
//    }
//}
