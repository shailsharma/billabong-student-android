package in.securelearning.lil.android.assignments.views.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.events.LoadPendingAssignmentListTeacher;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.views.activity.StudentSummaryActivity;
import in.securelearning.lil.android.base.comparators.SortAssignment;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.predictions.Predicate;
import in.securelearning.lil.android.base.predictions.PredicateListFilter;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.syncadapter.events.SearchCloseEvent;
import in.securelearning.lil.android.syncadapter.events.SearchOpenEvent;
import in.securelearning.lil.android.syncadapter.events.SearchSubmitEvent;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AssignmentFragmentTeacher extends Fragment {

    private static final String ASSIGNMENT_DATE = "date";
    @Inject
    public AssignmentTeacherModel mTeacherModel;
    @Inject
    public RxBus mRxBus;

    private View mRootView;
    private RecyclerView mAssignmentRecyclerView;
    private AssignmentAdapter mAssignmentAdapter;
    private List<Assignment> mFilterList = new ArrayList<>();
    private LinearLayout mNoResultLayout;
    private Disposable mSubscription;
    private static ArrayList<String> FILTER_BY_LIST2 = new ArrayList();
    private static String[] SORT_BY_LIST2 = {"Assigned Date"};
    private List<Assignment> mActualAssignments;
    private String mSearchQuery;
    private int mLimit = 10;
    private int mSkip = 0;
    private int mPreviousTotal = 0;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private String mFilterBySubject = "";
    private String assignmentDate = "";

    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String DATE = "date";
    private int mColumnCount = 1;
    private String mSubjectId;
    private String mSubjectName;
    private String mTopicId;
    private String mGradeId;
    private String mSectionId;
    private String mDate;

    public AssignmentFragmentTeacher() {
        // Required empty public constructor
    }

    public static AssignmentFragmentTeacher newInstance(String date) {
        AssignmentFragmentTeacher assignmentFragmentTeacher = new AssignmentFragmentTeacher();
        Bundle args = new Bundle();
        args.putString(ASSIGNMENT_DATE, date);
        assignmentFragmentTeacher.setArguments(args);
        return assignmentFragmentTeacher;
    }

    public static Fragment newInstance(String subjectId, String topicId, String gradeId, String sectionId, String date, int columnCount, String subjectName) {
        AssignmentFragmentTeacher fragment = new AssignmentFragmentTeacher();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SUBJECT_ID, subjectId);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mFilterBySubject = mSubjectName;
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mSectionId = getArguments().getString(SECTION_ID);
            mDate = getArguments().getString(DATE);
        }
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mRootView = inflater.inflate(R.layout.layout_assigned_recycler_view, container, false);
        initializeViews();
        setUpFragmentForCalendarAssignment();
        setDefault();
        listenRxBusEvents();
        getData(mSkip, mLimit, mFilterBySubject);

        return mRootView;
    }

    private void setUpFragmentForCalendarAssignment() {
//        if (getArguments() != null) {
//            assignmentDate = getArguments().getString(ASSIGNMENT_DATE);
//            long midnightDate = DateUtils.getSecondsForMidnightFromDate(getDateFromString(assignmentDate));
//            long morningDate = DateUtils.getSecondsForMorningFromDate(getDateFromString(assignmentDate));
//            mFromDate = getSelectedDateToString(midnightDate);
//            mToDate = getSelectedDateToString(morningDate);
//        } else {
//            mFromDate = "";
//            mToDate = getTodayDate();
//        }
    }

    private void setDefault() {
        mFilterBySubject = "";
        initializeRecyclerView(new ArrayList<Assignment>());
        mSkip = 0;
    }

    private void getData(int skip, final int limit, String filterBySubject) {

        mTeacherModel.getAssignmentListForPendingView(skip, limit, filterBySubject).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Assignment>>() {
            @Override
            public void accept(ArrayList<Assignment> assignments) throws Exception {
                mSkip += assignments.size();
                mPreviousTotal = assignments.size();
                noResultFound(mSkip);
                if (assignments.size() < limit) {
                    mAssignmentRecyclerView.removeOnScrollListener(null);
                }
                mAssignmentAdapter.addItem(assignments);
            }
        });
    }

    private void noResultFound(int size) {
        if (size > 0) {
            mNoResultLayout.setVisibility(View.GONE);
            mAssignmentRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoResultLayout.setVisibility(View.VISIBLE);
            mAssignmentRecyclerView.setVisibility(View.GONE);
        }

    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object event) {

                        if (event instanceof LoadPendingAssignmentListTeacher)
                            initializeRecyclerView(((LoadPendingAssignmentListTeacher) event).getAssignments());
                        else if (event instanceof SearchSubmitEvent) {
                            mSearchQuery = ((SearchSubmitEvent) event).getQueryText();
                            if (mAssignmentAdapter != null)
                                mAssignmentAdapter.search(mSearchQuery);
                        } else if (event instanceof SearchOpenEvent) {
                            mSearchQuery = "";
                        } else if (event instanceof SearchCloseEvent) {
                            mSearchQuery = "";
                            if (mAssignmentAdapter != null)
                                mAssignmentAdapter.clearSearch();
                        }
                    }


                });
    }

    private void initializeViews() {
        mAssignmentRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mNoResultLayout = (LinearLayout) mRootView.findViewById(R.id.layout_no_result);

    }

    /**
     * initialize recycler view for mValues
     * check device is tablet or phone and load recycler view according to device
     *
     * @param assignments
     */
    private void initializeRecyclerView(List<Assignment> assignments) {

        noResultFound(assignments.size());
        // setupFilterAttributes(assignments);
        mActualAssignments = assignments;
        setAdapterAndDoPagination(assignments);

    }

    private void setAdapterAndDoPagination(List<Assignment> assignments) {
        LinearLayoutManager layoutManager = null;
        if (getActivity() != null) {
            if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new GridLayoutManager(getActivity(), 2);
                mAssignmentRecyclerView.setLayoutManager(layoutManager);
                mAssignmentAdapter = new AssignmentAdapter(assignments);
                mAssignmentRecyclerView.setAdapter(mAssignmentAdapter);

            } else {
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mAssignmentRecyclerView.setLayoutManager(layoutManager);
                mAssignmentAdapter = new AssignmentAdapter(assignments);
                mAssignmentRecyclerView.setAdapter(mAssignmentAdapter);


            }

        }

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mAssignmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPreviousTotal - 1) {

                            getData(mSkip, mLimit, mFilterBySubject);

                        }
                    }

                }

            });
        }
    }

    public void filter(FilterList filterList) {
        if (mAssignmentAdapter != null) mAssignmentAdapter.applyFilter(filterList);
    }

    public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

        List<Assignment> mValues = new ArrayList<>();
        List<Assignment> mPermanentValues = new ArrayList<>();

        public AssignmentAdapter(List<Assignment> assignments) {
            this.mValues = assignments;
            this.mPermanentValues.addAll(assignments);
        }

        @Override
        public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assignment_teacher_item, parent, false);
            return new ViewHolder(view);
        }

        public void applyFilter(FilterList filterList) {
            this.filter(filterList);
            this.sort(filterList);
        }

        public void sort(FilterList filterList) {
            if (filterList.getSections().size() > 1) {
                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        sortAssignment(filterSectionItem.getName());
                    }
                }
                notifyDataSetChanged();
            }

        }

        public void filter(FilterList filterList) {
            setDefault();
            if (filterList != null) {

                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        mFilterBySubject = filterSectionItem.getName();
                        break;
                    }
                }
            }

            getData(mSkip, mLimit, mFilterBySubject);
        }

        private void filterAssignment(final List<String> filterBySubjectsList) {
            mFilterList = new ArrayList<>();
            mFilterList = PredicateListFilter.filter((ArrayList<Assignment>) mActualAssignments, new Predicate<Assignment>() {
                @Override
                public boolean apply(Assignment assignment) {

                    boolean isMatched = false;
                    for (String s : filterBySubjectsList) {
                        if (assignment.getMetaInformation().getSubject().getName().equalsIgnoreCase(s)) {
                            mFilterList.add(assignment);
                            isMatched = true;
                            break;
                        }
                    }

                    mValues = mFilterList;
                    if (isMatched)
                        return true;
                    else return false;
                }
            });
        }

        private void sortAssignment(String sortByAttribute) {
            if (sortByAttribute.equalsIgnoreCase(SORT_BY_LIST2[0])) {
                Collections.sort(mValues, new SortAssignment.AssignedDateSorter());
            } else if (sortByAttribute.equalsIgnoreCase(SORT_BY_LIST2[1])) {
                Collections.sort(mValues, new SortAssignment.DueDateSorter());
            } else {
                //Do nothing
            }
        }

        public void search(String query) {
            if (!TextUtils.isEmpty(query)) {
                mValues.clear();
                for (Assignment assignment :
                        mPermanentValues) {
                    if (assignment.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        mValues.add(assignment);
                    }
                }
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        public void clearSearch() {
            if (mValues.size() != mPermanentValues.size()) {
                mValues.clear();
                mValues.addAll(mPermanentValues);
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        @Override
        public void onBindViewHolder(final AssignmentAdapter.ViewHolder holder, final int position) {

            final Assignment mAssignment = mValues.get(position);
            holder.mTitleTextView.setText(mAssignment.getTitle());
            holder.mSubjectTextView.setText(mAssignment.getMetaInformation().getSubject().getName());
            holder.mDueOnTextView.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(mAssignment.getDueDate())));
            holder.mAssignedOnTextView.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(mAssignment.getAssignedDateTime())));
            holder.mAssignedByNameTextView.setText(mAssignment.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + mAssignment.getAssignedBy().getName() : "");
            holder.mAssignedToTextView.setText(mAssignment.getAssignedGroups().get(0).getName());
            holder.mTotalScoreTextView.setText(String.valueOf(mAssignment.getTotalScore()));
            setAssignmentThumbnail(mAssignment, holder.mAssignmentThumbnail);
            setAssignmentStatus(mAssignment, holder.mAssignmentStatusImageView);

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(StudentSummaryActivity.getStartStudentSummaryActivity(getContext(), mAssignment.getDocId(), ""));

                }
            });

        }

        private void setAssignmentThumbnail(Assignment assignment, ImageView assignmentThumbnailImageView) {
            if (assignment.getThumbnail() != null) {

                String thumbnailPath = assignment.getThumbnail().getLocalUrl();
                if (TextUtils.isEmpty(thumbnailPath)) {
                    thumbnailPath = assignment.getThumbnail().getUrl();
                }
                if (TextUtils.isEmpty(thumbnailPath)) {
                    thumbnailPath = assignment.getThumbnail().getThumb();
                }
                try {
                    if (!TextUtils.isEmpty(thumbnailPath)) {
                        Picasso.with(getContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                    } else {
                        Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                    }
                } catch (Exception e) {
                    try {
                        Picasso.with(getContext()).load(assignment.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
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

        private void setAssignmentStatus(Assignment mAssignment, ImageView assignmentStatusImageView) {
            AssignmentStatus assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(mAssignment.getDueDate()).getTime());

            if (assignmentStatus == AssignmentStatus.DUE)
                assignmentStatusImageView.setImageResource(R.drawable.clock_orange);
            else if (assignmentStatus == AssignmentStatus.OVERDUE)
                assignmentStatusImageView.setImageResource(R.drawable.clock_red);
            else
                assignmentStatusImageView.setImageResource(R.drawable.clock_green);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void addItem(ArrayList<Assignment> assignments) {
            if (mValues != null && mPermanentValues != null) {
                mPermanentValues.addAll(assignments);
                mValues.addAll(assignments);
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private View mItemView;
            private TextView mTitleTextView, mAssignedToTextView, mTotalScoreTextView, mAssignedByNameTextView, mAssignedOnTextView, mSubjectTextView, mDueOnTextView;
            private ImageView mAssignmentThumbnail, mAssignmentStatusImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                mAssignmentThumbnail = (ImageView) mItemView.findViewById(R.id.imageview_assignment_thumbnail);
                mAssignmentStatusImageView = (ImageView) mItemView.findViewById(R.id.imageview_assignment_status);
                mTitleTextView = (TextView) mItemView.findViewById(R.id.textview_assignment_title);
                mAssignedByNameTextView = (TextView) mItemView.findViewById(R.id.textview_assigned_by_name);
                mAssignedOnTextView = (TextView) mItemView.findViewById(R.id.textview_assigned_on);
                mSubjectTextView = (TextView) mItemView.findViewById(R.id.textview_subject);
                mDueOnTextView = (TextView) mItemView.findViewById(R.id.textview_due_on);
                mAssignedToTextView = (TextView) mItemView.findViewById(R.id.textview_assigned_to);
                mTotalScoreTextView = (TextView) mItemView.findViewById(R.id.textview_total_score);
            }
        }
    }

}
