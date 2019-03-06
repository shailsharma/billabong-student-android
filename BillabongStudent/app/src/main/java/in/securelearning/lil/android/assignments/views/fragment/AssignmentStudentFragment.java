package in.securelearning.lil.android.assignments.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAssignmentFragmentNewBinding;
import in.securelearning.lil.android.app.databinding.LayoutCoursePagerItemBinding;
import in.securelearning.lil.android.app.databinding.RyanLayoutAssignmentItemviewPendingBinding;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.dataobjects.Category;
import in.securelearning.lil.android.home.views.widget.PeriodDetailPopUp;
import in.securelearning.lil.android.quizpreview.events.AssignmentSubmittedEvent;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AssignmentStudentFragment extends Fragment {

    @Inject
    public AssignmentResponseStudentModel mAssignmentResponseStudentModel;
    @Inject
    public RxBus mRxBus;

    private LayoutAssignmentFragmentNewBinding mBinding;
    final LayoutCoursePagerItemBinding[] mViewPagerItemBindings = new LayoutCoursePagerItemBinding[2];
    private int mColumnCount = 0;
    private PendingAssignmentAdapter mPendingAssignmentAdapter;
    private OverDueAssignmentAdapter mOverDueAssignmentAdapter;
    private int mPendingSkip = 0;
    private int mPendingLimit = 10;
    private int mOverDueSkip = 0;
    private int mOverDueLimit = 10;
    private String mFilterBySubject = "";
    private String mToDate = "";
    private String mFromDate = "";
    private Disposable mSubscription;
    private static final String ASSIGNMENT_DATE = "date";
    private String assignmentDate = "";
    private HashMap<String, Category> mSubjectMap = new HashMap<>();
    private boolean mIsOverDueQueryExecutedOnce = false;

    public AssignmentStudentFragment() {

    }

    public static AssignmentStudentFragment newInstance(String date) {
        AssignmentStudentFragment assignmentStudentFragment = new AssignmentStudentFragment();
        if (!TextUtils.isEmpty(date)) {
            Bundle args = new Bundle();
            args.putString(ASSIGNMENT_DATE, date);
            assignmentStudentFragment.setArguments(args);
        }

        return assignmentStudentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        if (getContext().getResources().getBoolean(R.bool.isTablet)) {
            mColumnCount = 2;
        } else {
            mColumnCount = 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorAssignmentPrimary));
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_assignment_fragment_new, container, false);
        //mBinding.appBarContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAssignmentPrimary));
        mSubjectMap = PeriodDetailPopUp.getSubjectMap(getContext());
        setUpFragmentForCalendarAssignment();
        setUpViewPager();
        setUpTabLayout();
        listenRxEvent();
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsOverDueQueryExecutedOnce) {
            if (mOverDueSkip > 0 && mBinding.viewPager.getAdapter() != null && mBinding.viewPager.getAdapter().getCount() > 0) {
                mBinding.viewPager.setCurrentItem(0, false);
            } else if (mBinding.viewPager.getAdapter() != null && mBinding.viewPager.getAdapter().getCount() > 1) {
                mBinding.viewPager.setCurrentItem(1, false);
            }
        }

    }

    private void setUpFragmentForCalendarAssignment() {
        if (getArguments() != null) {
            assignmentDate = getArguments().getString(ASSIGNMENT_DATE);
            long midnightDate = DateUtils.getSecondsForMidnightFromDate(getDateFromString(assignmentDate));
            long morningDate = DateUtils.getSecondsForMorningFromDate(getDateFromString(assignmentDate));
            mFromDate = getSelectedDateToString(midnightDate);
            mToDate = getSelectedDateToString(morningDate);
        } else {
            mFromDate = "";
            mToDate = getTodayDate();
        }
    }

    private void listenRxEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof AssignmentSubmittedEvent || (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(AssignmentResponse.class))) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {

                                    filter(null);
                                    setPendingDefault();
                                    setOverDueDefault();
                                    mBinding.viewPager.getAdapter().notifyDataSetChanged();
                                    setUpTabLayout();
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

    private void setUpViewPager() {

        if (getArguments() != null) {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(1);
            mBinding.viewPager.setAdapter(viewPagerAdapter);
        } else {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(2);
            mBinding.viewPager.setAdapter(viewPagerAdapter);
        }
        mBinding.viewPager.setOffscreenPageLimit(2);

    }

    private void setUpTabLayout() {

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.colorWhite66),
                ContextCompat.getColor(getContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        mBinding.tabLayout.setSelectedTabIndicatorHeight(6);
        if (getArguments() != null) {
            mBinding.tabLayout.setVisibility(View.GONE);
            mBinding.viewPager.setPadding(0, 0, 0, 0);
            if (getDateFromString(assignmentDate).before(getDateFromString(getTodayDate()))) {
                getActivity().setTitle(getString(R.string.string_over_due));
            } else if (getDateFromString(assignmentDate).after(getDateFromString(getTodayDate())) || getDateFromString(assignmentDate).equals(getDateFromString(getTodayDate()))) {
                getActivity().setTitle(getString(R.string.string_pending));
            }
        } else {
            mBinding.tabLayout.getTabAt(0).setText(getString(R.string.string_over_due));
            mBinding.tabLayout.getTabAt(1).setText(getString(R.string.string_pending));
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
            if (mTabsCount == 1) {
                loadPendingView(binding, layoutManager, position);
//                if (position == 0) {
//                    if (mPendingAssignmentAdapter == null) {
//                        mPendingAssignmentAdapter = new AssignmentAdapter(new ArrayList<AssignmentStudent>(), position);
//                    }
//
//                    binding.list.setAdapter(mPendingAssignmentAdapter);
//                    setPendingDefault();
//                    getPendingAssignments(position, mFromDate, mToDate, mFilterBySubject, mPendingSkip, mPendingLimit);
//
//                    hideShowList(mPendingSkip, position);
//
//                    if (layoutManager != null) {
//                        final LinearLayoutManager finalLayoutManager = layoutManager;
//                        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                            @Override
//                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                                if (dy > 0) {
//                                    if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPendingSkip - 1) {
//                                        getPendingAssignments(position, mFromDate, mToDate, mFilterBySubject, mPendingSkip, mPendingLimit);
//
//                                    }
//                                }
//
//                            }
//
//                        });
//                    }
//
//                }

            } else {
                if (position == 0) {
                    loadOverDueView(binding, layoutManager, position);

                } else if (position == 1) {
                    loadPendingView(binding, layoutManager, position);
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

    public void filter(FilterList filterList) {
        if (mPendingAssignmentAdapter != null) {
            mPendingAssignmentAdapter.applyFilter(filterList);
        }
        if (mOverDueAssignmentAdapter != null) {
            mOverDueAssignmentAdapter.applyFilter(filterList);
        }

    }

    public static Date getDateFromString(String assignmentDate) {
        return DateUtils.convertrIsoDate(assignmentDate);
    }

    public static String getTodayDate() {
        long date = DateUtils.getSecondsForMorningFromDate(new Date());
        return DateUtils.getISO8601DateStringFromSeconds(date);
    }

    public static String getSelectedDateToString(long date) {
        return DateUtils.getISO8601DateStringFromSeconds(date);
    }

    private void hideShowList(int skip, int position) {

        if (skip > 0) {
            mViewPagerItemBindings[position].list.setVisibility(View.VISIBLE);
            mViewPagerItemBindings[position].layoutNoResult.setVisibility(View.GONE);
        } else {
            mViewPagerItemBindings[position].list.setVisibility(View.GONE);
            mViewPagerItemBindings[position].layoutNoResult.setVisibility(View.VISIBLE);
        }
    }

    private void setPendingDefault() {
        mFilterBySubject = "";
        mPendingSkip = 0;

        if (mPendingAssignmentAdapter != null) {
            mPendingAssignmentAdapter.clear();
        }

    }

    private void setOverDueDefault() {
        mFilterBySubject = "";
        mOverDueSkip = 0;

        if (mOverDueAssignmentAdapter != null) {
            mOverDueAssignmentAdapter.clear();
        }

    }

    private void getPendingAssignments(final int position, final String fromDate, final String toDate, final String subject, final int skip, final int limit) {
        mAssignmentResponseStudentModel.getPendingAssignmentList(fromDate, toDate, subject, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
            @Override
            public void accept(ArrayList<AssignmentStudent> assignmentStudents) throws Exception {
                if (mPendingSkip == 0) {
                    hideShowList(assignmentStudents.size(), position);
                }

                mPendingSkip += assignmentStudents.size();

                if (mPendingSkip <= 0 && mBinding.viewPager.getAdapter() != null && mBinding.viewPager.getAdapter().getCount() > 0) {
                    mBinding.viewPager.setCurrentItem(0, false);
                }

                if (assignmentStudents.size() < limit) {
                    mViewPagerItemBindings[position].list.removeOnScrollListener(null);
                }

                mPendingAssignmentAdapter.addItem(assignmentStudents);

            }
        });

    }

    private void getOverDueAssignments(final int position, final String fromDate, final String toDate, final String subject, final int skip, final int limit) {
        mAssignmentResponseStudentModel.getOverDueAssignmentList(fromDate, toDate, subject, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
            @Override
            public void accept(ArrayList<AssignmentStudent> assignmentStudents) throws Exception {
                if (mOverDueSkip == 0) {
                    hideShowList(assignmentStudents.size(), position);
                }
                mIsOverDueQueryExecutedOnce = true;
                mOverDueSkip += assignmentStudents.size();
                if (mOverDueSkip <= 0 && mBinding.viewPager.getAdapter() != null && mBinding.viewPager.getAdapter().getCount() > 1) {
                    mBinding.viewPager.setCurrentItem(1, false);
                }
                if (assignmentStudents.size() < limit) {
                    mViewPagerItemBindings[position].list.removeOnScrollListener(null);
                }

                mOverDueAssignmentAdapter.addItem(assignmentStudents);

            }
        });
    }

    private void loadPendingView(LayoutCoursePagerItemBinding binding, LinearLayoutManager layoutManager, final int position) {
        if (mPendingAssignmentAdapter == null) {
            mPendingAssignmentAdapter = new PendingAssignmentAdapter(new ArrayList<AssignmentStudent>(), position);
        }

        binding.list.setAdapter(mPendingAssignmentAdapter);

        setPendingDefault();
        getPendingAssignments(position, mFromDate, mToDate, mFilterBySubject, mPendingSkip, mPendingLimit);

        hideShowList(mPendingSkip, position);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPendingSkip - 1) {
                            getPendingAssignments(position, mFromDate, mToDate, mFilterBySubject, mPendingSkip, mPendingLimit);

                        }
                    }

                }

            });
        }
    }

    private void loadOverDueView(LayoutCoursePagerItemBinding binding, LinearLayoutManager layoutManager, final int position) {
        if (mOverDueAssignmentAdapter == null) {
            mOverDueAssignmentAdapter = new OverDueAssignmentAdapter(new ArrayList<AssignmentStudent>(), position);
        }
        binding.list.setAdapter(mOverDueAssignmentAdapter);

        setOverDueDefault();
        getOverDueAssignments(position, mFromDate, mToDate, mFilterBySubject, mOverDueSkip, mOverDueLimit);

        hideShowList(mOverDueSkip, position);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mOverDueSkip - 1) {
                            getOverDueAssignments(position, mFromDate, mToDate, mFilterBySubject, mOverDueSkip, mOverDueLimit);

                        }
                    }

                }

            });
        }
    }

    private class PendingAssignmentAdapter extends RecyclerView.Adapter<PendingAssignmentAdapter.ViewHolder> {

        ArrayList<AssignmentStudent> mValues;
        int position;
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
            holder.mBinding.textViewTopic.setText(assignmentStudent.getMetaInformation().getTopic().getName());
            holder.mBinding.textviewSubject.setText(assignmentStudent.getSubject().getName());
            holder.mBinding.textViewTopic.setText(assignmentStudent.getMetaInformation().getTopic().getName());
            holder.mBinding.textviewDueOn.setText("Due on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentStudent.getAssignmentDueDate())).toUpperCase());
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
                        mFilterBySubject = filterSectionItem.getName();
                        break;
                    }
                }
            }


            getPendingAssignments(position, mFromDate, mToDate, mFilterBySubject, mPendingSkip, mPendingLimit);
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

    private class OverDueAssignmentAdapter extends RecyclerView.Adapter<OverDueAssignmentAdapter.ViewHolder> {

        ArrayList<AssignmentStudent> mValues;
        int position;
        private int mRandomColorPosition;
        private int[] mExtraColorArray;

        public OverDueAssignmentAdapter(ArrayList<AssignmentStudent> assignmentStudents, int index) {
            this.mValues = assignmentStudents;
            this.position = index;
            this.mRandomColorPosition = 0;
            this.mExtraColorArray = getContext().getResources().getIntArray(R.array.subject_text_color_extra);
        }

        @Override
        public OverDueAssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutAssignmentItemviewPendingBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_assignment_itemview_pending, parent, false);
            return new OverDueAssignmentAdapter.ViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(OverDueAssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentStudent assignmentStudent = mValues.get(position);

            holder.mBinding.textviewAssignmentTitle.setText(assignmentStudent.getAssignmentTitle());
            holder.mBinding.textviewSubject.setText(assignmentStudent.getSubject().getName());
            holder.mBinding.textViewTopic.setText(assignmentStudent.getMetaInformation().getTopic().getName());
            holder.mBinding.textviewDueOn.setText("Due on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentStudent.getAssignmentDueDate())).toUpperCase());
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
            setOverDueDefault();
            if (filterList != null) {

                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        mFilterBySubject = filterSectionItem.getName();
                        break;
                    }
                }
            }
            getOverDueAssignments(position, mFromDate, mToDate, mFilterBySubject, mOverDueSkip, mOverDueLimit);

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

    private void setAssignmentThumbnail(AssignmentStudent assignmentStudent, ImageView assignmentThumbnailImageView) {
        if (assignmentStudent.getThumbnail() != null) {
            String thumbnailPath = assignmentStudent.getThumbnail().getLocalUrl();

            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = assignmentStudent.getThumbnail().getUrl();
            }
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = assignmentStudent.getThumbnail().getThumb();
            }

            try {
                if (!TextUtils.isEmpty(thumbnailPath)) {

                    Picasso.with(getContext()).load(thumbnailPath).resize(600, 440).centerInside().into(assignmentThumbnailImageView);

                } else {
                    Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(assignmentStudent.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnailImageView);

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
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

        if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_OBJECTIVE.getAssignmentType()) ||
                assignmentType.equalsIgnoreCase(AssignmentType.TYPE_SUBJECTIVE.getAssignmentType())) {
            return getString(R.string.quiz);

        } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_RESOURCE.getAssignmentType())) {
            return getString(R.string.resource);
        } else {
            //String type = "";
            if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType())) {
                return AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType())) {
                return AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType())) {
                return AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType())) {
                return AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_Popup.getAssignmentType())) {
                return AssignmentType.TYPE_Popup.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType())) {
                return AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType();
            } else {
                return getString(R.string.title_course);
            }

        }
    }

    private int getSubjectColor(String subjectId, int randomColorPosition, int[] extraColorArray) {
        if (mSubjectMap.containsKey(subjectId)) {
            return mSubjectMap.get(subjectId).getTextColor();
        } else {
            int color = extraColorArray[randomColorPosition % extraColorArray.length];
            randomColorPosition++;
            return color;
        }
    }
}
