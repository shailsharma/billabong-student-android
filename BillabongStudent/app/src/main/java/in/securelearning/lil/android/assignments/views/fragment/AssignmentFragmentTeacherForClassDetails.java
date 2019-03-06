package in.securelearning.lil.android.assignments.views.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.events.AllStudentSubmittedEvent;
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
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.learningnetwork.events.EventNewAssignmentCreated;
import in.securelearning.lil.android.syncadapter.events.SearchCloseEvent;
import in.securelearning.lil.android.syncadapter.events.SearchOpenEvent;
import in.securelearning.lil.android.syncadapter.events.SearchSubmitEvent;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AssignmentFragmentTeacherForClassDetails extends Fragment {

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
    private String mFilterBySubject = "";
    private String assignmentDate = "";

    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String DATE = "date";
    public static final String GROUP_ID = "group_id";
    private int mColumnCount = 1;
    private String mSubjectId;
    private String mSubjectName;
    private String mTopicId;
    private String mGradeId;
    private String mSectionId;
    private String mStartDate;
    private String mEndtDate;
    private String mGroupId;

    public AssignmentFragmentTeacherForClassDetails() {
        // Required empty public constructor
    }

    public static AssignmentFragmentTeacherForClassDetails newInstance(String date) {
        AssignmentFragmentTeacherForClassDetails assignmentFragmentTeacher = new AssignmentFragmentTeacherForClassDetails();
        Bundle args = new Bundle();
        args.putString(ASSIGNMENT_DATE, date);
        assignmentFragmentTeacher.setArguments(args);
        return assignmentFragmentTeacher;
    }

    public static Fragment newInstance(String subjectId, String topicId, String gradeId, String sectionId, String date, int columnCount, String subjectName, String groupId) {
        AssignmentFragmentTeacherForClassDetails fragment = new AssignmentFragmentTeacherForClassDetails();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SUBJECT_ID, subjectId);
        args.putString(SUBJECT_NAME, subjectName);
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putString(SECTION_ID, sectionId);
        args.putString(DATE, date);
        args.putString(GROUP_ID, groupId);
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

        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mRootView = inflater.inflate(R.layout.layout_assigned_recycler_view, container, false);
        initializeViews();
        setUpFragmentForCalendarAssignment();
        setDefault();
        listenRxBusEvents();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mFilterBySubject = mSubjectName;
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mSectionId = getArguments().getString(SECTION_ID);
            String date = getArguments().getString(DATE);
            mStartDate = DateUtils.getISO8601DateStringFromSeconds(DateUtils.getSecondsForMorningFromDate(DateUtils.convertrIsoDate(date)));
            mEndtDate = DateUtils.getISO8601DateStringFromSeconds(DateUtils.getSecondsForMidnightFromDate(DateUtils.convertrIsoDate(date)));
            mGroupId = getArguments().getString(GROUP_ID);
            getData(mSkip, mLimit, mFilterBySubject, mStartDate, mEndtDate, mGroupId);
        }

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
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

    private Date getDateFromString(String assignmentDate) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = new Date();
        try {
            date = format.parse(assignmentDate);
            System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    private void setDefault() {
//        mFilterBySubject = "";
        initializeRecyclerView(new ArrayList<Assignment>());
        mSkip = 0;
    }

    private void unSubscribeEvent() {
        mSubscription.dispose();
    }

    private void getData(int skip, final int limit, String filterBySubject, String startDate, String endDate, String groupId) {

        mTeacherModel.getAssignmentListForPendingView(skip, limit, filterBySubject, startDate, endDate, groupId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Assignment>>() {
            @Override
            public void accept(ArrayList<Assignment> assignments) throws Exception {
                mSkip += assignments.size();
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
                        if (event instanceof AllStudentSubmittedEvent || event instanceof EventNewAssignmentCreated) {
                            Completable.complete().observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action() {
                                        @Override
                                        public void run() {

                                            filter(null);
                                            setDefault();
                                            mAssignmentAdapter.notifyDataSetChanged();
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
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mSkip - 1) {

                            getData(mSkip, mLimit, mFilterBySubject, mStartDate, mEndtDate, mGroupId);

                        }
                    }

                }

            });
        }
    }

    public void filter(FilterList filterList) {
        if (mAssignmentAdapter != null) mAssignmentAdapter.applyFilter(filterList);
    }

    private static FilterList buildFilterListWithTitle(String title) {
        FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
        return builder.addSection(new FilterList.SectionBuilder()
                .addSectionItems(FILTER_BY_LIST2.toArray(new String[FILTER_BY_LIST2.size()]))
                .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
                .sectionTitle("SkillMasteryFilter By")
                .build())
//                .addSection(new FilterList.SectionBuilder()
//                        .addSectionItems(SORT_BY_LIST2)
//                        .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
//                        .sectionTitle("Sort By")
//                        .build())
                .title(title)
                .build();
    }

    private void setupFilterAttributes(List<Assignment> assignments) {
        Set<String> subjectSet = new LinkedHashSet<>();
        for (Assignment assignment : assignments) {
            FILTER_BY_LIST2.add(assignment.getMetaInformation().getSubject().getName());
        }
        FILTER_BY_LIST2.add(0, "All");
        subjectSet = new LinkedHashSet<>(FILTER_BY_LIST2);
        FILTER_BY_LIST2 = new ArrayList<>(subjectSet);

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ryan_layout_assignment_teacher_item, parent, false);
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

            getData(mSkip, mLimit, mFilterBySubject, mStartDate, mEndtDate, mGroupId);
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

            final Assignment assignment = mValues.get(position);
            holder.mTitleTextView.setText(assignment.getTitle());
            holder.mSubjectTextView.setText(assignment.getMetaInformation().getSubject().getName());
            holder.mDueOnTextView.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignment.getDueDate())));
            holder.mAssignedOnTextView.setText("Assigned on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignment.getAssignedDateTime())).toUpperCase());
            holder.mTopicTextView.setText(assignment.getMetaInformation().getTopic().getName());
            holder.mAssignedByNameTextView.setText(assignment.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignment.getAssignedBy().getName() : "");
//            holder.mAssignedToTextView.setText("Assigned to " + assignment.getAssignedGroups().get(0).getName());
            holder.mTotalScoreTextView.setText(String.valueOf(assignment.getTotalScore()));
//            setAssignmentThumbnail(assignment, holder.mAssignmentThumbnail);
            setAssignmentStatus(assignment, holder.mAssignmentStatusImageView);
//            if (!TextUtils.isEmpty(assignment.getObjectId())) {
//                setSubmittedResponseCount(assignment.getObjectId(), "", (TextViewCustom) holder.mSubmittedCountTextView);
//
//            }
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(StudentSummaryActivity.getStartStudentSummaryActivity(getContext(), assignment.getDocId(), ""));
                    holder.mSubmittedCountTextView.setVisibility(View.GONE);

                }
            });

        }

        private void setSubmittedResponseCount(String objectId, String subject, final TextViewCustom textView) {
            mTeacherModel.getSubmittedAssignmentResponseCount(objectId, subject).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    if (integer > 0) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(String.valueOf(integer));
                    } else {
                        textView.setVisibility(View.GONE);
                    }
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
            private TextView mTitleTextView, mAssignedToTextView, mTotalScoreTextView, mAssignedByNameTextView,
                    mAssignedOnTextView, mSubjectTextView, mDueOnTextView, mSubmittedCountTextView, mTopicTextView;
            private ImageView mAssignmentThumbnail, mAssignmentStatusImageView;
            private CardView mCardView;

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
                mSubmittedCountTextView = (TextView) mItemView.findViewById(R.id.textViewSubmittedResponseCounts);
                mTopicTextView = (TextView) mItemView.findViewById(R.id.textViewTopic);
                mCardView= (CardView) mItemView.findViewById(R.id.card_view);
            }
        }
    }

}
