package in.securelearning.lil.android.assignments.views.fragment;

import android.databinding.DataBindingUtil;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAssignedRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.RyanLayoutAssignmentTeacherItemBinding;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.views.activity.StudentSummaryActivity;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.dataobjects.AssignmentMinimal;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.dataobjects.Category;
import in.securelearning.lil.android.home.views.widget.PeriodDetailPopUp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AssignmentCompletedTeacherFragment extends Fragment {

    private static final String ASSIGNMENT_DATE = "date";
    private LayoutAssignedRecyclerViewBinding mBinding;
    @Inject
    public AssignmentTeacherModel mAssignmentTeacherModel;
    @Inject
    public RxBus mRxBus;
    @Inject
    AppUserModel mAppUserModel;
    private AssignmentAdapter mAssignmentAdapter;
    private Disposable mSubscription;
    private int mLimit = 10;
    private int mSkip = 0;
    private int mPreviousTotal = 0;
    private String mStartDate = "", mEndDate = "";
    private String mFilterBySubject = "";
    private String mAssignmentDate = "";
    private String mAssignById = "";

    private HashMap<String, Category> mSubjectMap = new HashMap<>();

    public AssignmentCompletedTeacherFragment() {
        // Required empty public constructor
    }

    public static AssignmentCompletedTeacherFragment newInstance(String date) {
        AssignmentCompletedTeacherFragment assignmentCompletedTeacherFragment = new AssignmentCompletedTeacherFragment();
        Bundle args = new Bundle();
        args.putString(ASSIGNMENT_DATE, date);
        assignmentCompletedTeacherFragment.setArguments(args);
        return assignmentCompletedTeacherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            mAssignmentDate = getArguments().getString(ASSIGNMENT_DATE);
        }
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_assigned_recycler_view, container, false);
        mSubjectMap = PeriodDetailPopUp.getSubjectMap(getContext());
        setUpFragmentForCalendarAssignment();
        setDefault();
        listenRxBusEvents();
        mAssignById = mAppUserModel.getObjectId();
        getData(mAssignById, mFilterBySubject, mStartDate, mEndDate, mSkip, mLimit);

        return mBinding.getRoot();
    }

    private void getData(String assignById, String subject, String startDate, String endDate, int skip, final int limit) {

        mAssignmentTeacherModel.getCompletedAssignmentMinimalList(assignById, subject, startDate, endDate, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentMinimal>>() {
            @Override
            public void accept(ArrayList<AssignmentMinimal> assignmentMinimals) throws Exception {
                mSkip += assignmentMinimals.size();
                mPreviousTotal = assignmentMinimals.size();
                noResultFound(mSkip);
                if (assignmentMinimals.size() < limit) {
                    mBinding.recyclerView.removeOnScrollListener(null);
                }
                mAssignmentAdapter.addItem(assignmentMinimals);


            }
        });
    }

    private void setUpFragmentForCalendarAssignment() {
        if (getArguments() != null) {
            mAssignmentDate = getArguments().getString(ASSIGNMENT_DATE);
            long midnightDate = DateUtils.getSecondsForMidnightFromDate(getDateFromString(mAssignmentDate));
            long morningDate = DateUtils.getSecondsForMorningFromDate(getDateFromString(mAssignmentDate));
            mStartDate = getSelectedDateToString(midnightDate);
            mEndDate = getSelectedDateToString(morningDate);
        } else {
            mStartDate = "";
            mEndDate = "";
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

    private void setDefault() {
        mFilterBySubject = "";
        initializeRecyclerView(new ArrayList<AssignmentMinimal>());
        mSkip = 0;
    }

    private void noResultFound(int size) {
        if (size > 0) {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
        }

    }

    private void listenRxBusEvents() {

    }

    /**
     * initialize recycler view for mValues
     * check device is tablet or phone and load recycler view according to device
     *
     * @param assignments
     */
    private void initializeRecyclerView(ArrayList<AssignmentMinimal> assignments) {

        noResultFound(assignments.size());
        setAdapterAndDoPagination(assignments);

    }

    private void setAdapterAndDoPagination(ArrayList<AssignmentMinimal> assignments) {
        LinearLayoutManager layoutManager = null;
        if (getActivity() != null) {
            if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new GridLayoutManager(getActivity(), 2);
                mBinding.recyclerView.setLayoutManager(layoutManager);
                mAssignmentAdapter = new AssignmentAdapter(assignments);
                mBinding.recyclerView.setAdapter(mAssignmentAdapter);

            } else {
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mBinding.recyclerView.setLayoutManager(layoutManager);
                mAssignmentAdapter = new AssignmentAdapter(assignments);
                mBinding.recyclerView.setAdapter(mAssignmentAdapter);


            }

        }

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPreviousTotal - 1) {

                            getData(mAssignById, mFilterBySubject, mStartDate, mEndDate, mSkip, mLimit);

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


        ArrayList<AssignmentMinimal> mValues;
        private int mRandomColorPosition;
        private int[] mExtraColorArray;

        public AssignmentAdapter(ArrayList<AssignmentMinimal> assignmentMinimals) {
            this.mValues = assignmentMinimals;
            this.mRandomColorPosition = 0;
            this.mExtraColorArray = getContext().getResources().getIntArray(R.array.subject_text_color_extra);
        }

        @Override
        public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutAssignmentTeacherItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_assignment_teacher_item, parent, false);
            return new AssignmentAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(AssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentMinimal assignmentMinimal = mValues.get(position);
            holder.mBinding.textviewAssignmentTitle.setText(assignmentMinimal.getTitle());
            holder.mBinding.textviewSubject.setText(assignmentMinimal.getMetaInformation().getSubject().getName());
            holder.mBinding.textviewDueOn.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentMinimal.getDueDate())));
            holder.mBinding.textviewAssignedOn.setText("Assigned on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentMinimal.getAssignedDateTime())).toUpperCase());
            holder.mBinding.textviewAssignedByName.setText(assignmentMinimal.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignmentMinimal.getAssignedBy().getName() : "");
            holder.mBinding.textViewTopic.setText(assignmentMinimal.getMetaInformation().getTopic().getName());
//            holder.mBinding.textviewAssignedTo.setText("Assigned to " + assignmentMinimal.getAssignedGroups().get(0).getName());
            holder.mBinding.textViewAssignmentType.setText(getAssignmentType(assignmentMinimal.getAssignmentType()));
//            holder.mBinding.textviewSubject.setTextColor(getSubjectColor(assignmentMinimal.getMetaInformation().getSubject().getId(), mRandomColorPosition, mExtraColorArray));
//            setAssignmentThumbnail(assignmentMinimal, holder.mBinding.imageviewAssignmentThumbnail);
            holder.mBinding.textViewSubmittedResponseCounts.setVisibility(View.GONE);
            holder.mBinding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(StudentSummaryActivity.getStartStudentSummaryActivity(getContext(), assignmentMinimal.getAssignmentDocId(), ""));

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
            setDefault();
            if (filterList != null) {
                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        mFilterBySubject = filterSectionItem.getName();
                        break;
                    }
                }
            }

            getData(mAssignById, mFilterBySubject, mStartDate, mEndDate, mSkip, mLimit);
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

}
