package in.securelearning.lil.android.resources.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentRecommendResourceBinding;
import in.securelearning.lil.android.base.dataobjects.CuratorMapping;
import in.securelearning.lil.android.base.dataobjects.SubjectSuper;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.CuratorMappingModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.resources.view.InjectorYoutube;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.PrefManagerStudentSubjectMapping;

import static in.securelearning.lil.android.syncadapter.utils.PrefManager.SubjectExt;
import static in.securelearning.lil.android.syncadapter.utils.PrefManager.getSubjectList;


public class RecommendResourceFragment2 extends Fragment {
    @Inject
    CuratorMappingModel mCuratorMappingModel;
    @Inject
    AppUserModel mAppUserModel;
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


    public RecommendResourceFragment2() {
    }


    public static RecommendResourceFragment2 newInstance(int columnCount) {
        RecommendResourceFragment2 fragment = new RecommendResourceFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment newInstance(int columnCount, String subjectId, String topicId, String gradeId, String sectionId, String date) {
        RecommendResourceFragment2 fragment = new RecommendResourceFragment2();
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
        InjectorYoutube.INSTANCE.getComponent().inject(this);
        ArrayList<SubjectExt> list = new ArrayList<>();
        if (PermissionPrefsCommon.getDashboardTeacherViewPermission(getContext())) {
            HashMap<String, SubjectExt> map = new HashMap<>();
            for (CuratorMapping curatorMapping :
                    mCuratorMappingModel.getCompleteList()) {
                if (curatorMapping.isCourseAvailable()) {
                    SubjectExt subject = new SubjectExt(curatorMapping.getSubject().getId(), curatorMapping.getSubject().getName(), curatorMapping.getSubject().getSubjects());
                    map.put(curatorMapping.getSubject().getName(), subject);
                }
            }
            list = new ArrayList<>(map.values());

        } else {
            list = PrefManagerStudentSubjectMapping.getSubjectListForWhichCoursesAvailable(getActivity());
        }
        try {
            if (list.isEmpty()) {
                list = getSubjectList(getActivity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAdapter = new ResourcePagerAdapter(getActivity().getSupportFragmentManager(), getContext(), list);
        mBinding.resourcePager.setAdapter(mAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.resourcePager);
        setUpTabLayoutIcon(list);
        return mBinding.getRoot();
    }

    private void setUpTabLayoutIcon(ArrayList<SubjectExt> list) {
        mBinding.tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.colorWhite66),
                ContextCompat.getColor(getContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorHeight(4);
//        if (mBinding.tabLayout.getTabCount() == list.size()) {
//            for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {
//                mBinding.tabLayout.getTabAt(i).setIcon(list.get(i).getIconTransparentId());
//            }
//        }
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

        public ResourcePagerAdapter(FragmentManager fm, Context context, ArrayList<SubjectExt> list) {
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
            String gradeId = "";
            if (subjectList.get(position).getSubjects() != null && subjectList.get(position).getSubjects().size() > 0) {
                for (SubjectSuper subjectSuper :
                        subjectList.get(position).getSubjects()) {
                    mSubjectId.add(subjectSuper.getId());
                }
            } else {
                mSubjectId.add(subjectList.get(position).getId());
            }
            if (PermissionPrefsCommon.getClassDetailStudentViewPermission(getContext())) {
                if (mAppUserModel.getApplicationUser().getGrade() != null && !TextUtils.isEmpty(mAppUserModel.getApplicationUser().getGrade().getId())) {
                    gradeId = mAppUserModel.getApplicationUser().getGrade().getId();
                } else {
                    gradeId = "";
                }
            } else {
                gradeId = "";
            }
            RecommendedListFragment fragment = RecommendedListFragment.newInstanceForTopicBrowse(mSubjectId, "", gradeId, mColumnCount);
            return fragment;
        }

        @Override
        public int getCount() {
            return subjectList.size();
        }
    }
}
