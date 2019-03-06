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
import in.securelearning.lil.android.app.databinding.RyanLayoutAssignmentTeacherItemBinding;
import in.securelearning.lil.android.assignments.events.AllStudentSubmittedEvent;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.views.activity.StudentSummaryActivity;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.dataobjects.AssignmentMinimal;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.home.dataobjects.Category;
import in.securelearning.lil.android.home.views.widget.PeriodDetailPopUp;
import in.securelearning.lil.android.learningnetwork.events.EventNewAssignmentCreated;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 31-Aug-17.
 */

public class AssignmentTeacherFragment extends Fragment {

    @Inject
    AssignmentTeacherModel mAssignmentTeacherModel;
    @Inject
    RxBus mRxBus;
    @Inject
    AppUserModel mAppUserModel;

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
    private String mAssignById = "";
    private boolean mIsOverDueQueryExecutedOnce = false;

    public AssignmentTeacherFragment() {

    }

    public static AssignmentTeacherFragment newInstance(String date) {
        AssignmentTeacherFragment assignmentTeacherFragment = new AssignmentTeacherFragment();
        if (!TextUtils.isEmpty(date)) {
            Bundle args = new Bundle();
            args.putString(ASSIGNMENT_DATE, date);
            assignmentTeacherFragment.setArguments(args);
        }
        return assignmentTeacherFragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mAssignById = mAppUserModel.getObjectId();
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
                if (event instanceof AllStudentSubmittedEvent || event instanceof EventNewAssignmentCreated) {
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

    public void filter(FilterList filterList) {
        if (mPendingAssignmentAdapter != null) {
            mPendingAssignmentAdapter.applyFilter(filterList);
        }
        if (mOverDueAssignmentAdapter != null) {
            mOverDueAssignmentAdapter.applyFilter(filterList);
        }
    }

    private Date getDateFromString(String assignmentDate) {


        return DateUtils.convertrIsoDate(assignmentDate);
    }

    private String getTodayDate() {

        long date = DateUtils.getSecondsForMorningFromDate(new Date());
        return DateUtils.getISO8601DateStringFromSeconds(date);
    }

    private String getSelectedDateToString(long date) {
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

    private void getPendingAssignments(final int position, final String startDate, final String endDate, final String subject, final int skip, final int limit) {

        mAssignmentTeacherModel.getIncompleteAssignmentMinimalList(mAssignById, subject, startDate, endDate, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentMinimal>>() {
            @Override
            public void accept(ArrayList<AssignmentMinimal> assignmentMinimals) throws Exception {
                if (mPendingSkip == 0) {
                    hideShowList(assignmentMinimals.size(), position);

                }

                mPendingSkip += assignmentMinimals.size();
                if (mPendingSkip <= 0 && mBinding.viewPager.getAdapter() != null && mBinding.viewPager.getAdapter().getCount() > 0) {
                    mBinding.viewPager.setCurrentItem(0, false);
                }
                if (assignmentMinimals.size() < limit) {
                    mViewPagerItemBindings[position].list.removeOnScrollListener(null);
                }

                mPendingAssignmentAdapter.addItem(assignmentMinimals);

            }
        });


    }

    private void getOverDueAssignments(final int position, final String startDate, final String endDate, final String subject, final int skip, final int limit) {

        mAssignmentTeacherModel.getIncompleteAssignmentMinimalList(mAssignById, subject, startDate, endDate, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentMinimal>>() {
            @Override
            public void accept(ArrayList<AssignmentMinimal> assignmentMinimals) throws Exception {
                if (mOverDueSkip == 0) {
                    hideShowList(assignmentMinimals.size(), position);
                }
                mIsOverDueQueryExecutedOnce = true;
                mOverDueSkip += assignmentMinimals.size();
                if (mOverDueSkip <= 0 && mBinding.viewPager.getAdapter() != null && mBinding.viewPager.getAdapter().getCount() > 1) {
                    mBinding.viewPager.setCurrentItem(1, false);
                }

                if (assignmentMinimals.size() < limit) {
                    mViewPagerItemBindings[position].list.removeOnScrollListener(null);
                }

                mOverDueAssignmentAdapter.addItem(assignmentMinimals);

            }
        });

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

    private void loadOverDueView(LayoutCoursePagerItemBinding binding, LinearLayoutManager layoutManager, final int position) {
        if (mOverDueAssignmentAdapter == null) {
            mOverDueAssignmentAdapter = new OverDueAssignmentAdapter(new ArrayList<AssignmentMinimal>(), position);
        }
        binding.list.setAdapter(mOverDueAssignmentAdapter);
        if (mOverDueSkip == 0) {
            getOverDueAssignments(position, mToDate, mFromDate, mFilterBySubject, mOverDueSkip, mOverDueLimit);

        }
        hideShowList(mOverDueSkip, position);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mOverDueSkip - 1) {
                            getOverDueAssignments(position, mToDate, mFromDate, mFilterBySubject, mOverDueSkip, mOverDueLimit);

                        }
                    }

                }

            });
        }
    }

    private void loadPendingView(LayoutCoursePagerItemBinding binding, LinearLayoutManager layoutManager, final int position) {
        if (mPendingAssignmentAdapter == null) {
            mPendingAssignmentAdapter = new PendingAssignmentAdapter(new ArrayList<AssignmentMinimal>(), position);
        }

        binding.list.setAdapter(mPendingAssignmentAdapter);
        if (mPendingSkip == 0) {
            getPendingAssignments(position, mFromDate, mToDate, mFilterBySubject, mPendingSkip, mPendingLimit);
        }

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

    private class PendingAssignmentAdapter extends RecyclerView.Adapter<PendingAssignmentAdapter.ViewHolder> {

        ArrayList<AssignmentMinimal> mValues;
        int position;
        private int mRandomColorPosition;
        private int[] mExtraColorArray;

        public PendingAssignmentAdapter(ArrayList<AssignmentMinimal> assignmentMinimals, int index) {
            this.mValues = assignmentMinimals;
            this.position = index;
            this.mRandomColorPosition = 0;
            this.mExtraColorArray = getContext().getResources().getIntArray(R.array.subject_text_color_extra);
        }

        @Override
        public PendingAssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutAssignmentTeacherItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_assignment_teacher_item, parent, false);
            return new PendingAssignmentAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final PendingAssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentMinimal assignmentMinimal = mValues.get(position);
            holder.mBinding.textviewAssignmentTitle.setText(assignmentMinimal.getTitle());
            holder.mBinding.textviewSubject.setText(assignmentMinimal.getMetaInformation().getSubject().getName());
            holder.mBinding.textviewDueOn.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentMinimal.getDueDate())));
            holder.mBinding.textviewAssignedOn.setText("Assigned on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentMinimal.getAssignedDateTime())).toUpperCase());
            holder.mBinding.textviewAssignedByName.setText(assignmentMinimal.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignmentMinimal.getAssignedBy().getName() : "");
            holder.mBinding.textViewTopic.setText(assignmentMinimal.getMetaInformation().getTopic().getName());
            //      holder.mBinding.textviewAssignedTo.setText("Assigned to " + assignmentMinimal.getAssignedGroups().get(0).getName());
            holder.mBinding.textViewAssignmentType.setText(getAssignmentType(assignmentMinimal.getAssignmentType()));
            //      holder.mBinding.textviewSubject.setTextColor(getSubjectColor(assignmentMinimal.getMetaInformation().getSubject().getId(), mRandomColorPosition, mExtraColorArray));
            //      setAssignmentThumbnail(assignmentMinimal, holder.mBinding.imageviewAssignmentThumbnail);
//            if (!TextUtils.isEmpty(assignmentMinimal.getObjectId())) {
//                setSubmittedResponseCount(assignmentMinimal.getObjectId(), "", holder.mBinding.textViewSubmittedResponseCounts);
//
//            }
            holder.mBinding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(StudentSummaryActivity.getStartStudentSummaryActivity(getContext(), assignmentMinimal.getAssignmentDocId(), assignmentMinimal.getDocId()));
//                    holder.mBinding.textViewSubmittedResponseCounts.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void addItem(ArrayList<AssignmentMinimal> assignmentMinimals) {
            if (mValues != null) {
                mValues.addAll(assignmentMinimals);
                notifyDataSetChanged();
            } else {
                mValues = new ArrayList<>(assignmentMinimals);
                notifyDataSetChanged();
            }
        }

        public void applyFilter(FilterList filterList) {
            this.filter(filterList);
        }

        public void sort(FilterList filterList) {
            if (filterList.getSections().size() > 1) {
                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
                    if (filterSectionItem.isSelected()) {
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
            RyanLayoutAssignmentTeacherItemBinding mBinding;

            public ViewHolder(RyanLayoutAssignmentTeacherItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }
        }
    }

    private void setSubmittedResponseCount(String objectId, String subject, final TextViewCustom textView) {
        mAssignmentTeacherModel.getSubmittedAssignmentResponseCount(objectId, subject).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Turned in " + String.valueOf(integer));
                } else {
                    textView.setVisibility(View.GONE);
                }
            }
        });
    }

    private class OverDueAssignmentAdapter extends RecyclerView.Adapter<OverDueAssignmentAdapter.ViewHolder> {

        ArrayList<AssignmentMinimal> mValues;
        int position;
        private int mRandomColorPosition;
        private int[] mExtraColorArray;

        public OverDueAssignmentAdapter(ArrayList<AssignmentMinimal> assignmentMinimals, int index) {
            this.mValues = assignmentMinimals;
            this.position = index;
            this.mRandomColorPosition = 0;
            this.mExtraColorArray = getContext().getResources().getIntArray(R.array.subject_text_color_extra);
        }

        @Override
        public OverDueAssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutAssignmentTeacherItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_assignment_teacher_item, parent, false);
            return new OverDueAssignmentAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final OverDueAssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentMinimal assignmentMinimal = mValues.get(position);
            holder.mBinding.textviewAssignmentTitle.setText(assignmentMinimal.getTitle());
            holder.mBinding.textviewSubject.setText(assignmentMinimal.getMetaInformation().getSubject().getName());
            holder.mBinding.textviewDueOn.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentMinimal.getDueDate())));
            holder.mBinding.textviewAssignedOn.setText("Assigned on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentMinimal.getAssignedDateTime())).toUpperCase());
            holder.mBinding.textViewTopic.setText(assignmentMinimal.getMetaInformation().getTopic().getName());
            holder.mBinding.textviewAssignedByName.setText(assignmentMinimal.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignmentMinimal.getAssignedBy().getName() : "");
            holder.mBinding.textviewAssignedTo.setText("Assigned to " + assignmentMinimal.getAssignedGroups().get(0).getName());
            holder.mBinding.textViewAssignmentType.setText(getAssignmentType(assignmentMinimal.getAssignmentType()));
            //holder.mBinding.textviewSubject.setTextColor(getSubjectColor(assignmentMinimal.getMetaInformation().getSubject().getId(), mRandomColorPosition, mExtraColorArray));
//            setAssignmentThumbnail(assignmentMinimal, holder.mBinding.imageviewAssignmentThumbnail);
//            if (!TextUtils.isEmpty(assignmentMinimal.getObjectId())) {
//                setSubmittedResponseCount(assignmentMinimal.getObjectId(), "", holder.mBinding.textViewSubmittedResponseCounts);
//
//            }
            holder.mBinding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(StudentSummaryActivity.getStartStudentSummaryActivity(getContext(), assignmentMinimal.getAssignmentDocId(), assignmentMinimal.getDocId()));
//                    holder.mBinding.textViewSubmittedResponseCounts.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void addItem(ArrayList<AssignmentMinimal> assignmentMinimals) {
            if (mValues != null) {
                mValues.addAll(assignmentMinimals);
                notifyDataSetChanged();
            } else {
                mValues = new ArrayList<>(assignmentMinimals);
                notifyDataSetChanged();
            }
        }

        public void applyFilter(FilterList filterList) {
            this.filter(filterList);
        }

        public void sort(FilterList filterList) {
            if (filterList.getSections().size() > 1) {
                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
                    if (filterSectionItem.isSelected()) {
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
            getOverDueAssignments(position, mToDate, mFromDate, mFilterBySubject, mOverDueSkip, mOverDueLimit);

        }

        public void clear() {

            mValues.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RyanLayoutAssignmentTeacherItemBinding mBinding;

            public ViewHolder(RyanLayoutAssignmentTeacherItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }
        }
    }

    private void setAssignmentThumbnail(AssignmentMinimal assignmentMinimal, ImageView assignmentThumbnailImageView) {
        if (assignmentMinimal.getThumbnail() != null) {
            String thumbnailPath = assignmentMinimal.getThumbnail().getLocalUrl();

            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = assignmentMinimal.getThumbnail().getUrl();
            }
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = assignmentMinimal.getThumbnail().getThumb();
            }

            try {
                if (!TextUtils.isEmpty(thumbnailPath)) {

                    Picasso.with(getContext()).load(thumbnailPath).resize(600, 440).centerInside().into(assignmentThumbnailImageView);

                } else {
                    Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(assignmentMinimal.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
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

    private String getAssignmentType(String assignmentType) {

        if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_OBJECTIVE.getAssignmentType()) ||
                assignmentType.equalsIgnoreCase(AssignmentType.TYPE_SUBJECTIVE.getAssignmentType())) {

            String type = "";
            if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_OBJECTIVE.getAssignmentType())) {
                type = "Quiz";
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_SUBJECTIVE.getAssignmentType())) {
                type = "Quiz";
            } else {
                type = "Quiz";
            }
            return type;

        } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_RESOURCE.getAssignmentType())) {
            return getString(R.string.resource);
        } else {
            String type = "";
            if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType())) {
                type = AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType())) {
                type = AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType())) {
                type = AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType())) {
                type = AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_Popup.getAssignmentType())) {
                type = AssignmentType.TYPE_Popup.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType())) {
                type = AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType();
            }
            return type;
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


