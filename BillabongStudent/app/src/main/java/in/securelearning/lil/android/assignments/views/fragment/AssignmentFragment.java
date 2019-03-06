package in.securelearning.lil.android.assignments.views.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.views.fragment.overdueassignments.OverDueAssignmentFragment;
import in.securelearning.lil.android.assignments.views.fragment.pendingassignments.PendingAssignmentFragment;
import in.securelearning.lil.android.assignments.views.fragment.submittedassignment.SubmittedAssignmentFragment;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.utils.SlidingTabLayout;


public class AssignmentFragment extends Fragment {

    private int mTabsCount = 3;
    private View mRootView;
    static private ViewPager mAssignmentViewPager;
    private ViewPagerAdapter pagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private CharSequence mTabsTitles[] = {"Pending", "Overdue", "Submitted"};

    public AssignmentFragment() {
        // Required empty public constructor
    }

    public void filter() {
        if (mAssignmentViewPager.getCurrentItem() == 0) {
            //Pending mValues
            if (PendingAssignmentFragment.mAssignmentAdapter != null)
                PendingAssignmentFragment.mAssignmentAdapter.applyFilter();
        } else if (mAssignmentViewPager.getCurrentItem() == 1) {
            //Overdue mValues
            if (OverDueAssignmentFragment.mAssignmentAdapter != null)
                OverDueAssignmentFragment.mAssignmentAdapter.applyFilter();
        } else if (mAssignmentViewPager.getCurrentItem() == 2) {
            if (SubmittedAssignmentFragment.mAssignmentAdapter != null)
                SubmittedAssignmentFragment.mAssignmentAdapter.applyFilter();
        }
    }

    public static FilterList getFilter() {
        if (mAssignmentViewPager != null && mAssignmentViewPager.getCurrentItem() == 0) {
            return PendingAssignmentFragment.getFilter();
        } else if (mAssignmentViewPager != null && mAssignmentViewPager.getCurrentItem() == 1) {
            return OverDueAssignmentFragment.getFilter();
        } else if (mAssignmentViewPager != null && mAssignmentViewPager.getCurrentItem() == 2) {
            return SubmittedAssignmentFragment.getFilter();
        } else {
            return PendingAssignmentFragment.getFilter();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.layout_assignment_fragment, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorCoursePrimaryDark));
        initializeViews();
        initializeSlidingTabs();

        return mRootView;
    }

    /**
     * find ids of views
     */
    private void initializeViews() {

        mAssignmentViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager_course);
        mSlidingTabLayout = (SlidingTabLayout) mRootView.findViewById(R.id.tabs);

    }

    /**
     * set up slide views and set them into viewpager
     */
    private void initializeSlidingTabs() {

        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mTabsTitles, mTabsCount);
        mAssignmentViewPager.setAdapter(pagerAdapter);

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getActivity(), R.color.colorGreyDark);
            }

        });

        mSlidingTabLayout.setViewPager(mAssignmentViewPager);

        mAssignmentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

            if (position == 0) {
                PendingAssignmentFragment mRecommandFragment = PendingAssignmentFragment.newInstance(mSlidingTabLayout);
                return mRecommandFragment;
            }
            if (position == 1) {
                OverDueAssignmentFragment mOverDueAssignmentFragment = OverDueAssignmentFragment.newInstance(mSlidingTabLayout);
                return mOverDueAssignmentFragment;
            } else {
                SubmittedAssignmentFragment mSubmittedAssignmentFragment = SubmittedAssignmentFragment.newInstance(mSlidingTabLayout);
                return mSubmittedAssignmentFragment;
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

    }


}
