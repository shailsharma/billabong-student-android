package in.securelearning.lil.android.assignments.views.fragment.overdueassignments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentView;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.comparators.SortAssignmentResponse;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.predictions.Predicate;
import in.securelearning.lil.android.base.predictions.PredicateListFilter;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.SlidingTabLayout;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;


public class OverDueAssignmentFragment extends Fragment implements AssignmentView {

    private static FilterList filterList;
    private static SlidingTabLayout slidingTabLayout;
    @Inject
    public AssignmentResponseStudentModel mAssignmentResponseStudentModel;
    @Inject
    public RxBus mRxBus;

    private Context mContext;


    private List<AssignmentResponse> mActualAssignmentResponses = new ArrayList<>();
    private List<AssignmentResponse> mFilterList = new ArrayList<>();

    private static ArrayList<String> FILTER_BY_LIST2 = new ArrayList();
    private static String[] SORT_BY_LIST2 = {"Assigned Date", "Due Date"};

    private View mRootView;
    private RecyclerView mAssignmentRecyclerView;
    public static AssignmentAdapter mAssignmentAdapter;
    private OverDueAssignmentPresenter mOvedueAssignmentPresenter;
    private LinearLayout mNoResultLayout;

    public OverDueAssignmentFragment() {
        // Required empty public constructor
    }

    public void filter() {
        if (mAssignmentAdapter != null) mAssignmentAdapter.applyFilter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static FilterList getFilter() {
        if (filterList == null) {
            filterList = buildFilterListWithTitle("SkillMasteryFilter Assignments");
        }
        return filterList;
    }


    private static FilterList buildFilterListWithTitle(String title) {
        FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
        return builder.addSection(new FilterList.SectionBuilder()
                .addSectionItems(FILTER_BY_LIST2.toArray(new String[FILTER_BY_LIST2.size()]))
                .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
                .sectionTitle("SkillMasteryFilter By")
                .build())
                .addSection(new FilterList.SectionBuilder()
                        .addSectionItems(SORT_BY_LIST2)
                        .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
                        .sectionTitle("Sort By")
                        .build())
                .title(title)
                .build();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mOvedueAssignmentPresenter = new OverdueAssignmentPresenterImpl(this);

        mRootView = inflater.inflate(R.layout.layout_assignment_recycler_view, container, false);
        ((Activity) mContext).getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorAssignmentPrimaryDark));
        initializeViews();
//        initializeRecyclerView(new ArrayList<AssignmentResponse>());

        getData();
        return mRootView;
    }


    private void getData() {
        mOvedueAssignmentPresenter.getAssignmentsList();
    }


    private void initializeViews() {
        mAssignmentRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mNoResultLayout = (LinearLayout) mRootView.findViewById(R.id.layoutNoResult);
    }

    private void initializeRecyclerView(List<AssignmentResponse> assignmentResponses) {
        if (!assignmentResponses.isEmpty()) {
            mNoResultLayout.setVisibility(View.GONE);
            mAssignmentRecyclerView.setVisibility(View.VISIBLE);
            if (mContext.getResources().getBoolean(R.bool.isTablet)) {
                mAssignmentRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
                mAssignmentAdapter = new AssignmentAdapter(assignmentResponses);
                mAssignmentRecyclerView.setAdapter(mAssignmentAdapter);
            } else {
                mAssignmentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                mAssignmentAdapter = new AssignmentAdapter(assignmentResponses);
                mAssignmentRecyclerView.setAdapter(mAssignmentAdapter);
            }
        } else {
            mNoResultLayout.setVisibility(View.VISIBLE);
            mAssignmentRecyclerView.setVisibility(View.GONE);
        }


//        mAssignmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) {
//                    slidingTabLayout.setVisibility(View.GONE);
//                    recyclerView.setPadding(0, 0, 0, 0);
//                    NavigationDrawerActivity.mBottomTabLayout.setVisibility(View.GONE);
//                } else if (dy < 0) {
//                    slidingTabLayout.setVisibility(View.VISIBLE);
//                    recyclerView.setPadding(0, 63, 0, 0);
//                    NavigationDrawerActivity.mBottomTabLayout.setVisibility(View.VISIBLE);
//                }
//            }
//        });

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setupRecyclerViewForList(List<AssignmentResponse> assignmentResponses) {
        mActualAssignmentResponses = assignmentResponses;
        setupFilterAttributes(assignmentResponses);
        initializeRecyclerView(assignmentResponses);
    }

    private void setupFilterAttributes(List<AssignmentResponse> assignmentResponses) {
        Set<String> subjectSet = new LinkedHashSet<>();
        for (AssignmentResponse assignmentResponse : assignmentResponses) {
            FILTER_BY_LIST2.add(assignmentResponse.getMetaInformation().getSubject().getName());
        }
        FILTER_BY_LIST2.add(0, "All");
        subjectSet = new LinkedHashSet<>(FILTER_BY_LIST2);
        FILTER_BY_LIST2 = new ArrayList<>(subjectSet);

    }

    @Override
    public void refreshAssignmentList(AssignmentResponse assignmentResponse) {
        getData();

//        mAssignmentAdapter.mValues.remove(assignmentResponse);
//        mAssignmentAdapter.notifyDataSetChanged();
    }

    public static OverDueAssignmentFragment newInstance(SlidingTabLayout mSlidingTabLayout) {
        OverDueAssignmentFragment overDueAssignmentFragment = new OverDueAssignmentFragment();
        slidingTabLayout = mSlidingTabLayout;
        return overDueAssignmentFragment;
    }

    public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {
        List<AssignmentResponse> mValues = new ArrayList<>();
        List<AssignmentResponse> mPermanentValues = new ArrayList<>();

        AssignmentAdapter(List<AssignmentResponse> list) {
            this.mValues = list;
            this.mPermanentValues.addAll(list);
        }

        public void applyFilter() {
            this.filter();
            this.sort();
        }

        public void filter() {
            boolean noneApplied = true;
//            mValues.clear();
            List<String> filterBySubjectsList = new ArrayList<>();
            if (filterList != null) {

                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        filterBySubjectsList.add(filterSectionItem.getName());
                    }
                }

            }

            if (filterBySubjectsList != null && filterBySubjectsList.isEmpty() || (filterBySubjectsList.contains("All"))) {
                mValues = mActualAssignmentResponses;
            } else {
                filterAssignmentResponses(filterBySubjectsList);
            }

            notifyDataSetChanged();
        }

        public void sort() {
            for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
                if (filterSectionItem.isSelected()) {
                    sortAssignmentResponses(filterSectionItem.getName());
                }
            }
            notifyDataSetChanged();
        }

        private void filterAssignmentResponses(final List<String> filterBySubjectsList) {
            mFilterList = new ArrayList<>();
            mFilterList = PredicateListFilter.filter((ArrayList<AssignmentResponse>) mActualAssignmentResponses, new Predicate<AssignmentResponse>() {
                @Override
                public boolean apply(AssignmentResponse assignmentResponse) {

                    boolean isMatched = false;
                    for (String s : filterBySubjectsList) {
                        if (assignmentResponse.getMetaInformation().getSubject().getName().equalsIgnoreCase(s)) {
                            mFilterList.add(assignmentResponse);
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

        private void sortAssignmentResponses(String sortByAttribute) {
            if (sortByAttribute.equalsIgnoreCase(SORT_BY_LIST2[0])) {
                Collections.sort(mValues, new SortAssignmentResponse.AssignedDateSorter());
            } else if (sortByAttribute.equalsIgnoreCase(SORT_BY_LIST2[1])) {
                Collections.sort(mValues, new SortAssignmentResponse.DueDateSorter());
            }
        }

        public void search(String query) {
            if (!TextUtils.isEmpty(query)) {
                mValues.clear();
                for (AssignmentResponse assignmentResponse :
                        mPermanentValues) {

                    if (assignmentResponse.getAssignmentTitle().toLowerCase().contains(query.toLowerCase())) {
                        mValues.add(assignmentResponse);
                    }
                }

                notifyDataSetChanged();

//                Completable.complete().observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Action() {
//                            @Override
//                            public void run() {
//                                notifyDataSetChanged();
//                            }
//                        });
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
        public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assignment_itemview_pending, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AssignmentAdapter.ViewHolder holder, final int position) {
            AssignmentResponse assignmentResponse = mValues.get(position);

            holder.titleTextView.setText(assignmentResponse.getAssignmentTitle());
            holder.subjectTextView.setText(assignmentResponse.getMetaInformation().getSubject().getName());
            holder.dueOnTV.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate())));
            holder.assignedOnTextView.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentResponse.getAssignedDateTime())));
            holder.assignedByNameTextView.setText(assignmentResponse.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignmentResponse.getAssignedBy().getName() : "");

            setAssignmentThumbnail(assignmentResponse, holder.mAssignmentThumbnail);
            setAssignmentStatus(assignmentResponse,holder.mAssignmentStatusImageView);

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent mIntent = new Intent(mContext, AssignmentDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("assignmentResponse", mValues.get(position));
                    mIntent.putExtras(bundle);
                    startActivity(mIntent);
                }
            });

        }

        private void setAssignmentThumbnail(AssignmentResponse assignmentResponse, ImageView assignmentThumbnailImageView) {
            String thumbnailPath = assignmentResponse.getThumbnail().getLocalUrl();
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = assignmentResponse.getThumbnail().getUrl();
            }
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = assignmentResponse.getThumbnail().getThumb();
            }
            try {
                if (!TextUtils.isEmpty(thumbnailPath)) {
                    Picasso.with(getContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                } else {
                    Picasso.with(getContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(assignmentResponse.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
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
        }

        private void setAssignmentStatus(AssignmentResponse assignmentResponse, ImageView assignmentStatusImageView) {
            AssignmentStatus assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate()).getTime());
            if (assignmentStatus == AssignmentStatus.OVERDUE) {
                assignmentStatusImageView.setImageResource(R.drawable.clock_red);
            } else if (assignmentStatus == AssignmentStatus.DUE) {
                assignmentStatusImageView.setImageResource(R.drawable.clock_orange);
            } else {
                assignmentStatusImageView.setImageResource(R.drawable.clock_green);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private View mItemView;
            private TextView titleTextView, assignedByNameTextView, assignedOnTextView, subjectTextView, dueOnTV;
            private ImageView mAssignmentThumbnail, mAssignmentStatusImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                mAssignmentThumbnail = (ImageView) mItemView.findViewById(R.id.imageview_assignment_thumbnail);
                mAssignmentStatusImageView = (ImageView) mItemView.findViewById(R.id.imageview_assignment_status);
                titleTextView = (TextView) mItemView.findViewById(R.id.textview_assignment_title);
                assignedByNameTextView = (TextView) mItemView.findViewById(R.id.textview_assigned_by_name);
                assignedOnTextView = (TextView) mItemView.findViewById(R.id.textview_assigned_on);
                subjectTextView = (TextView) mItemView.findViewById(R.id.textview_subject);
                dueOnTV = (TextView) mItemView.findViewById(R.id.textview_due_on);
            }
        }

    }


}
