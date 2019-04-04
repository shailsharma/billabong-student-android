package in.securelearning.lil.android.assignments.views.fragment;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAssignmentFragmentStudentClassDetailsBinding;
import in.securelearning.lil.android.app.databinding.LayoutCoursePagerItemBinding;
import in.securelearning.lil.android.app.databinding.RyanLayoutAssignmentItemviewPendingBinding;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.quizpreview.events.AssignmentSubmittedEvent;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AssignmentFragmentStudentClassDetails extends Fragment {

    @Inject
    public AssignmentResponseStudentModel mAssignmentResponseStudentModel;
    @Inject
    public RxBus mRxBus;
    private Disposable mSubscription;
    private LayoutAssignmentFragmentStudentClassDetailsBinding mBinding;
    final LayoutCoursePagerItemBinding[] mViewPagerItemBindings = new LayoutCoursePagerItemBinding[3];
    private int mColumnCount = 1;
    private PendingAssignmentAdapter mPendingAssignmentAdapter;
    private int mPendingSkip = 0;
    private int mPendingLimit = 10;
    private String mToDate = "";
    private String mFromDate = "";
    private static final String ASSIGNMENT_DATE = "date";

    public static final String SUBJECT_NAME = "subject_name";
    public static final String ARG_COLUMN_COUNT = "column-count";
    private String mSubjectName;

    public AssignmentFragmentStudentClassDetails() {
    }

    public static AssignmentFragmentStudentClassDetails newInstance(int columnCount, String subjectName, String date) {
        AssignmentFragmentStudentClassDetails fragment = new AssignmentFragmentStudentClassDetails();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SUBJECT_NAME, subjectName);
        args.putString(ASSIGNMENT_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorAssignment.INSTANCE.getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_assignment_fragment_student_class_details, container, false);
        setUpFragmentForCalendarAssignment();
        listenRxEvent();
        LoadDataForPendingAssignment();
        return mBinding.getRoot();
    }

    private void listenRxEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof AssignmentSubmittedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {

                                    setPendingDefault();
                                    LoadDataForPendingAssignment();

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });

                }
            }
        });
    }

    private void LoadDataForPendingAssignment() {
        LinearLayoutManager layoutManager = null;
        if (mColumnCount > 1) {
            layoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
            mBinding.list.setLayoutManager(layoutManager);
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mBinding.list.setLayoutManager(layoutManager);
        }
        if (mPendingAssignmentAdapter == null) {
            mPendingAssignmentAdapter = new PendingAssignmentAdapter(new ArrayList<AssignmentStudent>(), 0);
        }
        mBinding.list.setAdapter(mPendingAssignmentAdapter);
        if (mPendingSkip == 0) {
            getPendingAssignments(0, mFromDate, mToDate, mSubjectName, mPendingSkip, mPendingLimit);
        }
//        hideShowList(mPendingSkip, 0);
        final LinearLayoutManager finalLayoutManager = layoutManager;
        mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPendingSkip - 1) {
                        getPendingAssignments(0, mFromDate, mToDate, mSubjectName, mPendingSkip, mPendingLimit);
                    }
                }
            }
        });
    }

    private void setUpFragmentForCalendarAssignment() {
        if (getArguments() != null) {
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            String assignmentDate = getArguments().getString(ASSIGNMENT_DATE);
            long midnightDate = DateUtils.getSecondsForMidnightFromDate(getDateFromString(assignmentDate));
            long morningDate = DateUtils.getSecondsForMorningFromDate(getDateFromString(assignmentDate));
            mFromDate = getSelectedDateToString(midnightDate);
            mToDate = getSelectedDateToString(morningDate);
        }
    }

    public void filter(FilterList filterList) {
        if (mPendingAssignmentAdapter != null) {
            mPendingAssignmentAdapter.applyFilter(filterList);
        }
    }

    private Date getDateFromString(String assignmentDate) {
        return DateUtils.convertrIsoDate(assignmentDate);
    }

    private String getSelectedDateToString(long date) {
        return DateUtils.getISO8601DateStringFromSeconds(date);
    }

    private void setPendingDefault() {
        mPendingSkip = 0;
        if (mPendingAssignmentAdapter != null) {
            mPendingAssignmentAdapter.clear();
        }
    }

    @SuppressLint("CheckResult")
    private void getPendingAssignments(final int position, final String fromDate, final String toDate, final String subject, final int skip, final int limit) {
        mAssignmentResponseStudentModel.getPendingAssignmentList(fromDate, toDate, subject, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
            @Override
            public void accept(ArrayList<AssignmentStudent> assignmentStudents) throws Exception {

                mPendingSkip += assignmentStudents.size();
                noResultFound(mPendingSkip);
                if (assignmentStudents.size() < limit) {
                    mBinding.list.removeOnScrollListener(null);
                }
                mPendingAssignmentAdapter.addItem(assignmentStudents);
            }
        });
    }

    private void noResultFound(int size) {
        if (size > 0) {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.list.setVisibility(View.VISIBLE);
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {

        int mTabsCount;

        public ViewPagerAdapter(int tabsCount) {
            mTabsCount = tabsCount;
        }

        @Override
        public int getCount() {
            return mTabsCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutCoursePagerItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_course_pager_item, container, false);
            LinearLayoutManager layoutManager = null;
            if (mColumnCount > 1) {
                layoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
                binding.list.setLayoutManager(layoutManager);
            } else {
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                binding.list.setLayoutManager(layoutManager);
            }
            mViewPagerItemBindings[position] = binding;
            if (mTabsCount == 2) {
                if (position == 0) {
                    if (mPendingAssignmentAdapter == null) {
                        mPendingAssignmentAdapter = new PendingAssignmentAdapter(new ArrayList<AssignmentStudent>(), position);
                    }

                    binding.list.setAdapter(mPendingAssignmentAdapter);
                    if (mPendingSkip == 0) {
                        getPendingAssignments(position, mFromDate, mToDate, mSubjectName, mPendingSkip, mPendingLimit);
                    }


                    if (layoutManager != null) {
                        final LinearLayoutManager finalLayoutManager = layoutManager;
                        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                                if (dy > 0) {
                                    if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPendingSkip - 1) {
                                        getPendingAssignments(position, mFromDate, mToDate, mSubjectName, mPendingSkip, mPendingLimit);

                                    }
                                }

                            }

                        });
                    }

                }

            }

            container.addView(binding.getRoot());
            mViewPagerItemBindings[position] = binding;
            return binding.getRoot();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

    }

    private class PendingAssignmentAdapter extends RecyclerView.Adapter<PendingAssignmentAdapter.ViewHolder> {

        ArrayList<AssignmentStudent> mValues;
        private int position;
        private int mRandomColorPosition;
        private int[] mExtraColorArray;

        public PendingAssignmentAdapter(ArrayList<AssignmentStudent> assignmentStudents, int index) {
            this.mValues = assignmentStudents;
            this.position = index;
            this.mRandomColorPosition = 0;
            this.mExtraColorArray = getContext().getResources().getIntArray(R.array.subject_text_color_extra);
        }

        @Override
        public PendingAssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutAssignmentItemviewPendingBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_assignment_itemview_pending, parent, false);
            return new PendingAssignmentAdapter.ViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(PendingAssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentStudent assignmentStudent = mValues.get(position);

            holder.mBinding.textviewAssignmentTitle.setText(assignmentStudent.getAssignmentTitle());
            holder.mBinding.textviewSubject.setText(assignmentStudent.getSubject().getName());
            holder.mBinding.textviewDueOn.setText("Due on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentStudent.getAssignmentDueDate())).toUpperCase());
            holder.mBinding.textViewTopic.setText(assignmentStudent.getMetaInformation().getTopic().getName());
            holder.mBinding.textviewAssignedOn.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentStudent.getAssignedDateTime())));
            holder.mBinding.textviewAssignedByName.setText(assignmentStudent.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignmentStudent.getAssignedBy().getName() : "");
            holder.mBinding.textViewAssignmentType.setText(getAssignmentType(assignmentStudent.getAssignmentType()));
//            holder.mBinding.textviewSubject.setTextColor(getSubjectColor(assignmentStudent.getSubject().getId(), mRandomColorPosition, mExtraColorArray));
//            setAssignmentThumbnail(assignmentStudent, holder.mBinding.imageviewAssignmentThumbnail);
            setAssignmentStatus(assignmentStudent, holder.mBinding.imageviewAssignmentStatus);

            holder.mBinding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().startActivity(AssignmentDetailActivity.startAssignmentDetailActivity(getContext(), assignmentStudent.getObjectId(), assignmentStudent.getDocId()));

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void addItem(ArrayList<AssignmentStudent> assignmentStudents) {
            if (mValues != null) {
                mValues.addAll(assignmentStudents);
                notifyDataSetChanged();
            } else {
                mValues = new ArrayList<>(assignmentStudents);
                notifyDataSetChanged();
            }
        }

        public void applyFilter(FilterList filterList) {
            this.filter(filterList);
            //this.sort(filterList);
        }

        public void sort(FilterList filterList) {
            if (filterList.getSections().size() > 1) {
                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        //sortAssignment(filterSectionItem.getName());
                    }
                }
                notifyDataSetChanged();
            }

        }

        public void filter(FilterList filterList) {
            setPendingDefault();
            if (filterList != null) {
                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
//                        mFilterBySubject = filterSectionItem.getName();
                        break;
                    }
                }
            }
            getPendingAssignments(position, mFromDate, mToDate, mSubjectName, mPendingSkip, mPendingLimit);
        }

        public void clear() {
            mValues.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RyanLayoutAssignmentItemviewPendingBinding mBinding;

            public ViewHolder(RyanLayoutAssignmentItemviewPendingBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private void setAssignmentStatus(AssignmentStudent assignmentStudent, ImageView assignmentStatusImageView) {
        AssignmentStatus assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignmentStudent.getAssignmentDueDate()).getTime());
        if (assignmentStatus == AssignmentStatus.OVERDUE) {
            assignmentStatusImageView.setImageResource(R.drawable.clock_red);
        } else if (assignmentStatus == AssignmentStatus.DUE) {
            assignmentStatusImageView.setImageResource(R.drawable.clock_orange);
        } else {
            assignmentStatusImageView.setImageResource(R.drawable.clock_green);
        }
    }

    private String getAssignmentType(String assignmentType) {

        if (assignmentType.equalsIgnoreCase("quiz")) {
            return getString(R.string.quiz);

        } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_RESOURCE.getAssignmentType())) {
            return getString(R.string.resource);
        } else {
            if (assignmentType.equalsIgnoreCase("digitalbook")) {
                return "Digital Book";
            } else if (assignmentType.equalsIgnoreCase("videocourse")) {
                return "Video Course";
            } else if (assignmentType.contains("feature")) {
                return "Recap";
            } else if (assignmentType.contains("map")) {
                return "Concept Map";
            } else if (assignmentType.contains("interactiveim")) {
                return "Interactive Image";
            } else if (assignmentType.contains("interactivevi")) {
                return "Interactive Video";
            } else if (assignmentType.contains("pop")) {
                return "Pop Up";
            } else {
                return "Course";
            }

        }
    }

}
