package in.securelearning.lil.android.resources.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentRecommendResourceBinding;
import in.securelearning.lil.android.base.dataobjects.SubjectSuper;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;


public class RecommendResourceFragment extends Fragment {
    private FragmentRecommendResourceBinding mBinding;
    private ResourcePagerAdapter mAdapter;
    private static String ARG_COLUMN_COUNT = "column-count";
    public static final String SUBJECT_ID = "subject_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String DATE = "date";
    private int mColumnCount = 1;
    private String mSubjectId;
    private String mTopicId;
    private String mGradeId;
    private String mSectionId;
    private String mDate;

    public RecommendResourceFragment() {
    }


    public static RecommendResourceFragment newInstance(int columnCount) {
        RecommendResourceFragment fragment = new RecommendResourceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment newInstance(int columnCount, String subjectId, String topicId, String gradeId, String sectionId, String date) {
        RecommendResourceFragment fragment = new RecommendResourceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SUBJECT_ID, subjectId);
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putString(SECTION_ID, sectionId);
        args.putString(DATE, date);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mSectionId = getArguments().getString(SECTION_ID);
            mDate = getArguments().getString(DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_recommend_resource, container, false);
        ArrayList<PrefManager.SubjectExt> list = PrefManager.getSubjectList(getActivity());
        mAdapter = new ResourcePagerAdapter(getActivity().getSupportFragmentManager(), getContext(), list);
        mBinding.resourcePager.setAdapter(mAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.resourcePager);
        setUpTabLayoutIcon(list);
        return mBinding.getRoot();
    }

    private void setUpTabLayoutIcon(ArrayList<PrefManager.SubjectExt> list) {
        mBinding.tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.colorWhite66),
                ContextCompat.getColor(getContext(), R.color.colorWhite));
        if (mBinding.tabLayout.getTabCount() == list.size()) {
            for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {
//                mBinding.tabLayout.getTabAt(i).setIcon(list.get(i).getIconId());
                mBinding.tabLayout.getTabAt(i).setIcon(list.get(i).getIconTransparentId());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter = null;
        }
    }

    class ResourcePagerAdapter extends FragmentStatePagerAdapter {
        Context mContext;
        ArrayList<PrefManager.SubjectExt> subjectList;

        public void clear() {
            if (subjectList != null) {
                subjectList.clear();
                subjectList = null;
            }
        }

        public ResourcePagerAdapter(FragmentManager fm, Context context, ArrayList<PrefManager.SubjectExt> list) {
            super(fm);
            this.mContext = context;
            subjectList = list;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return subjectList.get(position).getName();
        }

        @Override
        public Fragment getItem(int position) {
            ArrayList<String> mSubjectId = new ArrayList<>();

            if (subjectList.get(position).getSubjects() != null && subjectList.get(position).getSubjects().size() > 0) {
                for (SubjectSuper subjectSuper :
                        subjectList.get(position).getSubjects()) {
                    mSubjectId.add(subjectSuper.getId());
                }
            } else {
                mSubjectId.add(subjectList.get(position).getId());
            }
            RecommendedListFragment fragment = RecommendedListFragment.newInstanceForTopicBrowse(mSubjectId, "", "", mColumnCount);
            return fragment;
        }

        @Override
        public int getCount() {
            return subjectList.size();
        }

    }
}
