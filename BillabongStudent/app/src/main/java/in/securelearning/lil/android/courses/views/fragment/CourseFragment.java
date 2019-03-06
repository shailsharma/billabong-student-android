package in.securelearning.lil.android.courses.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.utils.SlidingTabLayout;


public class CourseFragment extends Fragment {

    private int mTabsCount = 3;
    private View mRootView;
    private ViewPager mCourseViewPager;
    private ViewPagerAdapter pagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private CharSequence mTabsTitles[] = {"FOR YOU", "YOURS", "LIKED BY YOU"};
    private FragmentActivity mActivity;


    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FragmentActivity)
            mActivity = (FragmentActivity) context;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCourseViewPager = null;
        pagerAdapter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.layout_fragment_course, container, false);
        mActivity.getWindow().setStatusBarColor(ContextCompat.getColor(mActivity, R.color.colorCoursePrimaryDark));
        initializeViews();
        initializeSlidingTabs();
        return mRootView;
    }

    /**
     * find ids of views
     */
    private void initializeViews() {

        mCourseViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager_course);
        mSlidingTabLayout = (SlidingTabLayout) mRootView.findViewById(R.id.tabs);

    }

    /**
     * set up slide views and set them into viewpager
     */
    private void initializeSlidingTabs() {

        pagerAdapter = new ViewPagerAdapter(mActivity.getSupportFragmentManager(), mTabsTitles, mTabsCount);
        mCourseViewPager.setAdapter(pagerAdapter);
        mCourseViewPager.setOffscreenPageLimit(2);

        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            mSlidingTabLayout.setDistributeEvenly(true);
        } else {
            mSlidingTabLayout.setDistributeEvenly(false);
        }

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(mActivity, R.color.colorAccent);
            }

        });
        mSlidingTabLayout.setViewPager(mCourseViewPager);

        mCourseViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                mSlidingTabLayout.setVisibility(View.VISIBLE);
//                NavigationDrawerActivity.mBottomTabLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * Class to handle sliding fragments.
     */
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        CharSequence mTabsTitles[];
        int mTabsCount;

        public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mCount) {
            super(fm);

            this.mTabsTitles = mTitles;
            this.mTabsCount = mCount;

        }

        @Override
        public Fragment getItem(int position) {

            int colCount = 1;
            if (getResources().getBoolean(R.bool.isTablet)) {
                colCount = 2;
            }
            if (position == 0) {
                CourseRecommendFragment mRecommendFragment = CourseRecommendFragment.newInstance(colCount, mSlidingTabLayout);
                return mRecommendFragment;
            } else if (position == 1) {
                DemoCourseFragment mDemoCourseFragment = DemoCourseFragment.newInstance(colCount, mSlidingTabLayout);
                return mDemoCourseFragment;
            } else {
                CourseFavouritesFragment mFavouritesFragment = CourseFavouritesFragment.newInstance(colCount, mSlidingTabLayout);
                return mFavouritesFragment;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitles[position];
        }

        @Override
        public int getCount() {
            return mTabsCount;
        }

        void clear() {

        }
    }
}
