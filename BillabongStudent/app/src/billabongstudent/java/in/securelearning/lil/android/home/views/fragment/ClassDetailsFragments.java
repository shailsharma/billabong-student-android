package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentClassDetailsFragmentsBinding;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentFragmentStudentClassDetails;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentFragmentTeacherForClassDetails;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.courses.views.fragment.CourseListFragment;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostListFragment;
import in.securelearning.lil.android.resources.view.fragment.RecommendedListFragment;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;


public class ClassDetailsFragments extends Fragment {
    FragmentClassDetailsFragmentsBinding mBinding;
    ClassPerformancePagerAdapter mAdapter;
    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECTS = "subjects";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String DATE = "date";
    private int mColumnCount = 1;
    private String mSubjectId;
    private ArrayList<String> mSubjects;
    private String mTopicId;
    private String mGradeId;
    private String mGroupId;
    private String mSectionId;
    private String mDate;
    private String mSubjectName;
    int mSubjectColor = Color.BLACK;
    @Inject
    HomeModel mHomeModel;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    PostDataLearningModel mPostDataLearningModel;
    private int[] imageResId = {
            R.drawable.logo_resource_c,
            R.drawable.logo_course_c,
            R.drawable.logo_assignment_c,
            R.drawable.logo_learning_network,
            R.drawable.logo_learning_map
    };

    public ClassDetailsFragments() {
    }


    public static ClassDetailsFragments newInstance() {
        ClassDetailsFragments fragment = new ClassDetailsFragments();
        return fragment;
    }

    public static ClassDetailsFragments newInstance(int columnCount, String subjectId, ArrayList<String> subjects, String topicId, String gradeId, String sectionId, String date, String subjectName) {
        ClassDetailsFragments fragment = new ClassDetailsFragments();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SUBJECT_ID, subjectId);
        args.putStringArrayList(SUBJECTS, subjects);
        args.putString(SUBJECT_NAME, subjectName);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mSubjects = getArguments().getStringArrayList(SUBJECTS);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mSectionId = getArguments().getString(SECTION_ID);
            mDate = getArguments().getString(DATE);

        }
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_class_details_fragments, container, false);
        mAdapter = new ClassPerformancePagerAdapter(getActivity().getSupportFragmentManager(), mColumnCount, mSubjectId, mSubjects, mTopicId, mGradeId, mSectionId, mDate, mSubjectName);
        mBinding.classPerformancePager.setAdapter(mAdapter);
        mSubjectColor = PrefManager.getColorForSubject(getActivity(), mSubjectId);
        mBinding.tabLayout.setupWithViewPager(mBinding.classPerformancePager);
        mGroupId = mHomeModel.getGroupId(mGradeId, mSectionId, mSubjectId);

        mBinding.classPerformancePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    mPostDataLearningModel.deleteAllNewPostByGroupId(mGroupId);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        setUpTabLayoutIcon();
        return mBinding.getRoot();
    }


    private void setUpTabLayoutIcon() {
        if (mBinding.tabLayout.getTabCount() == imageResId.length) {
            for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {
                mBinding.tabLayout.getTabAt(i).setIcon(imageResId[i]);
            }
        }

        mBinding.classPerformancePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (position == 3) {
//                    getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorAssignmentPrimary));
//                    ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorAssignmentPrimary)));
//                } else if (position == 4) {
//                    getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLN));
//                    ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimaryLN)));
//                } else {
//                    getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//                    ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public class ClassPerformancePagerAdapter extends FragmentPagerAdapter {
        //"Lesson Plan"
        //"Recap"
        //"Curriculum Progress"
        private final boolean isTeacher;
        private String[] teacherTitles = new String[]{"Assignment", "Resources", "Courses", "Lesson Plan", "Posts", "Class Performance"};
        private String[] studentTitles = new String[]{"Assignment", "Resources", "Courses", "Recap", "Posts", "Learning Map"};
        private int columnCount = 1;
        private String subjectId;
        private ArrayList<String> subjects;
        private String subjectName;
        private String topicId;
        private String gradeId;
        private String sectionId;
        private String date;

        public ClassPerformancePagerAdapter(FragmentManager fm, int columnCount, String subjectId, ArrayList<String> subjects, String topicId, String gradeId, String sectionId, String date, String subjectName) {
            super(fm);
            this.columnCount = columnCount;
            this.subjectId = subjectId;
            this.subjects = subjects;
            this.topicId = topicId;
            this.gradeId = gradeId;
            this.sectionId = sectionId;
            this.date = date;
            this.subjectName = subjectName;
            this.isTeacher = PermissionPrefsCommon.getClassDetailTeacherViewPermission(getContext());
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return teacherTitles.length;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (isTeacher) {
                        return AssignmentFragmentTeacherForClassDetails.newInstance(subjectId, topicId, gradeId, sectionId, date, columnCount, subjectName, mGroupId);
                    } else {
                        return AssignmentFragmentStudentClassDetails.newInstance(columnCount, mSubjectName,"");
                    }
                case 1:
                    return RecommendedListFragment.newInstanceForTopicBrowse(subjects, topicId, gradeId, columnCount);
                case 2:
                    return CourseListFragment.newInstanceForTopicBrowseCourses(subjects, topicId, gradeId, columnCount);

                case 3:
                    if (isTeacher) {
                        return CourseListFragment.newInstanceForTopicBrowseLessonPlan(subjects, topicId, gradeId, columnCount);

                    } else {
                        return CourseListFragment.newInstanceForTopicBrowseRecap(subjects, topicId, gradeId, columnCount);

                    }

                case 4:
                    return PostListFragment.newInstance(columnCount, mGroupId, false);

                case 5:
                    return PostListFragment.newInstance(columnCount, mGroupId, false);

//                case 6:
//
//                    return CurriculumProgressFragmentForClassDetail.newInstance(columnCount, subjectId, subjects, subjectName, topicId, gradeId, sectionId, date);

                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (isTeacher) {
                return teacherTitles[position];
            } else {
                return studentTitles[position];
            }

        }
    }
}

