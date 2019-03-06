package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutTabViewpagerBinding;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.courses.views.fragment.CourseListFragment;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostListFragment;
import in.securelearning.lil.android.resources.view.fragment.RecommendedListFragment;

/**
 * Created by Chaitendra on 28-Dec-17.
 */

public class TrainingDetailsFragment extends Fragment {

    public static final String TRAINING_ID = "trainingId";
    public static final String COLUMN_COUNT = "columnCount";
    public static final String TRAINING_GROUP_ID = "trainingGroupId";
    public static final String TRAINING_SUBJECT_IDS = "trainingSubjectIds";
    public static final String VIEW_TYPE = "viewType";
    @Inject
    HomeModel mHomeModel;
    @Inject
    AppUserModel mAppUserModel;
    LayoutTabViewpagerBinding mBinding;
    private int mColumnCount;
    private String mTrainingId;
    private String mTrainingGroupId;
    private int mViewType;
    private ArrayList<String> mSubjectIds = new ArrayList<>();
    private Group mGroup;
    private boolean isModerator = false;
    private OnTrainingDetailFragmentInteractionListener mListener;


    public static TrainingDetailsFragment newInstance(int columnCount, String trainingObjectId, String trainingGroupId, ArrayList<String> subjectIds, int viewType) {
        TrainingDetailsFragment trainingDetailsFragment = new TrainingDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        args.putString(TRAINING_ID, trainingObjectId);
        args.putString(TRAINING_GROUP_ID, trainingGroupId);
        args.putStringArrayList(TRAINING_SUBJECT_IDS, subjectIds);
        args.putInt(VIEW_TYPE, viewType);
        trainingDetailsFragment.setArguments(args);
        return trainingDetailsFragment;
    }

    public interface OnTrainingDetailFragmentInteractionListener {

        void OnTrainingDetailFragmentInteractionListener(Class aClass);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnTrainingDetailFragmentInteractionListener) {
            mListener = (OnTrainingDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTrainingDetailFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
            mTrainingId = getArguments().getString(TRAINING_ID);
            mTrainingGroupId = getArguments().getString(TRAINING_GROUP_ID);
            mSubjectIds = getArguments().getStringArrayList(TRAINING_SUBJECT_IDS);
            mViewType = getArguments().getInt(VIEW_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_tab_viewpager, container, false);
        mGroup = getTrainingGroup(mTrainingGroupId);
        Moderator moderator = new Moderator(mAppUserModel.getObjectId(), "");
        isModerator = mGroup.getModerators().contains(moderator);
        setUpViewPager();
        return mBinding.getRoot();
    }

    private Group getTrainingGroup(final String trainingGroupId) {
        return mHomeModel.getGroupFromId(trainingGroupId);
    }

    private void setUpViewPager() {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mColumnCount, mTrainingId, mTrainingGroupId, mSubjectIds, getTabTitles(mViewType), ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mBinding.viewPager.setAdapter(viewPagerAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.colorWhite66),
                ContextCompat.getColor(getContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorHeight(4);
        mBinding.viewPager.setOffscreenPageLimit(3);

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (getTabTitles(mViewType)[position].equals(getString(R.string.labelAssignments))) {
                    if (isModerator) {
                        mListener.OnTrainingDetailFragmentInteractionListener(TrainerAssignmentsFragment.class);
                    } else {
                        mListener.OnTrainingDetailFragmentInteractionListener(TraineeAssignmentsFragment.class);
                    }
                } else if (getTabTitles(mViewType)[position].equals(getString(R.string.labelGroup))) {
                    mListener.OnTrainingDetailFragmentInteractionListener(PostListFragment.class);
                } else {
                    mListener.OnTrainingDetailFragmentInteractionListener(TrainingStaticDetailsFragment.class);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private String[] getTabTitles(int viewType) {
        if (viewType == 0) {
            return getResources().getStringArray(R.array.training_list_item);
        } else {
            return getResources().getStringArray(R.array.training_join_list_item);
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private int mColumnCount;
        private String mTrainingId;
        private String mTrainingGroupId;
        private int mColor;
        private ArrayList<String> mTrainingSubjectIds;
        private String[] mTabTitles;

        public ViewPagerAdapter(FragmentManager fragmentManager, int columnCount, String trainingId, String trainingGroupId, ArrayList<String> subjectIds, String[] tabTitles, int color) {
            super(fragmentManager);
            mColumnCount = columnCount;
            mTrainingId = trainingId;
            mTrainingGroupId = trainingGroupId;
            mColor = color;
            mTabTitles = tabTitles;
            mTrainingSubjectIds = subjectIds;
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            if (mTabTitles[position].equals(getString(R.string.labelDetails))) {
                return TrainingStaticDetailsFragment.newInstance(mTrainingId, mTrainingGroupId, mViewType);
            } else if (mTabTitles[position].equals(getString(R.string.labelCourses))) {
                return CourseListFragment.newInstanceForTopicBrowse(mTrainingSubjectIds, "", "", mColumnCount);
            } else if (mTabTitles[position].equals(getString(R.string.labelResources))) {
                return RecommendedListFragment.newInstanceForTopicBrowse(mTrainingSubjectIds, "", "", mColumnCount);
            } else if (mTabTitles[position].equals(getString(R.string.labelSessions))) {
                return TraineeSessionsFragment.newInstance(mColumnCount, mTrainingId, mViewType);
            } else if (mTabTitles[position].equals(getString(R.string.labelAssignments))) {
                if (isModerator) {
                    return TrainerAssignmentsFragment.newInstance(mColumnCount, mTrainingId, mTrainingGroupId);
                } else {
                    return TraineeAssignmentsFragment.newInstance(mColumnCount, mTrainingId, mTrainingGroupId);
                }
            } else if (mTabTitles[position].equals(getString(R.string.labelGroup))) {
                return PostListFragment.newInstance(mColumnCount, mTrainingGroupId, false, mColor);
            } else if (mTabTitles[position].equals(getString(R.string.labelPerformance))) {
                return TraineePerformanceFragment.newInstance(mTrainingId);
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
