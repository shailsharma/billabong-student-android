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
import in.securelearning.lil.android.app.databinding.RyanLayoutSubmittedAssignmentItemviewBinding;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.home.dataobjects.Category;
import in.securelearning.lil.android.home.views.widget.PeriodDetailPopUp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AssignmentCompletedStudentFragment extends Fragment {

    private static final String ASSIGNMENT_DATE = "date";
    private LayoutAssignedRecyclerViewBinding mBinding;
    @Inject
    public AssignmentResponseStudentModel mAssignmentResponseStudentModel;
    @Inject
    public RxBus mRxBus;
    @Inject
    AppUserModel mAppUserModel;
    private SubmittedAssignmentAdapter mAssignmentAdapter;
    private Disposable mSubscription;
    private int mLimit = 10;
    private int mSkip = 0;
    private int mPreviousTotal = 0;
    private String mStartDate = "", mEndDate = "";
    private String mFilterBySubject = "";
    private String mAssignmentDate = "";

    private HashMap<String, Category> mSubjectMap = new HashMap<>();

    public AssignmentCompletedStudentFragment() {
        // Required empty public constructor
    }

    public static AssignmentCompletedStudentFragment newInstance(String date) {
        AssignmentCompletedStudentFragment assignmentCompletedTeacherFragment = new AssignmentCompletedStudentFragment();
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
        getSubmittedAssignments(mStartDate, mEndDate, mFilterBySubject, mSkip, mLimit);

        return mBinding.getRoot();
    }


    private void getSubmittedAssignments(final String fromDate, final String toDate, final String subject, final int skip, final int limit) {
        mAssignmentResponseStudentModel.getSubmittedAssignmentList(toDate, fromDate, subject, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
            @Override
            public void accept(ArrayList<AssignmentStudent> assignmentStudents) throws Exception {
                mSkip += assignmentStudents.size();
                mPreviousTotal = assignmentStudents.size();
                noResultFound(mSkip);
                if (assignmentStudents.size() < limit) {
                    mBinding.recyclerView.removeOnScrollListener(null);
                }
                mAssignmentAdapter.addItem(assignmentStudents);

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
        initializeRecyclerView(new ArrayList<AssignmentStudent>());
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


    /**
     * initialize recycler view for mValues
     * check device is tablet or phone and load recycler view according to device
     *
     * @param assignments
     */
    private void initializeRecyclerView(ArrayList<AssignmentStudent> assignments) {

        noResultFound(assignments.size());
        setAdapterAndDoPagination(assignments);

    }

    private void setAdapterAndDoPagination(ArrayList<AssignmentStudent> assignments) {
        LinearLayoutManager layoutManager = null;
        if (getActivity() != null) {
            if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new GridLayoutManager(getActivity(), 2);
                mBinding.recyclerView.setLayoutManager(layoutManager);
                mAssignmentAdapter = new SubmittedAssignmentAdapter(assignments);
                mBinding.recyclerView.setAdapter(mAssignmentAdapter);

            } else {
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mBinding.recyclerView.setLayoutManager(layoutManager);
                mAssignmentAdapter = new SubmittedAssignmentAdapter(assignments);
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

                            getSubmittedAssignments(mStartDate, mEndDate, mFilterBySubject, mSkip, mLimit);

                        }
                    }

                }

            });
        }
    }

    public void filter(FilterList filterList) {
        if (mAssignmentAdapter != null) mAssignmentAdapter.applyFilter(filterList);
    }

    private class SubmittedAssignmentAdapter extends RecyclerView.Adapter<SubmittedAssignmentAdapter.ViewHolder> {

        ArrayList<AssignmentStudent> mValues;
        private int mRandomColorPosition;
        private int[] mExtraColorArray;

        public SubmittedAssignmentAdapter(ArrayList<AssignmentStudent> assignmentStudents) {
            this.mValues = assignmentStudents;
            this.mRandomColorPosition = 0;
            this.mExtraColorArray = getContext().getResources().getIntArray(R.array.subject_text_color_extra);
        }

        @Override
        public SubmittedAssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutSubmittedAssignmentItemviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_submitted_assignment_itemview, parent, false);
            return new SubmittedAssignmentAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(SubmittedAssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentStudent assignmentStudent = mValues.get(position);
            try {
                holder.mBinding.textviewAssignmentTitle.setText(assignmentStudent.getAssignmentTitle());
            } catch (Exception e) {
                holder.mBinding.textviewAssignmentTitle.setText("");
                e.printStackTrace();
            }
            try {
                holder.mBinding.textviewSubject.setText(assignmentStudent.getSubject().getName());
            } catch (Exception e) {
                holder.mBinding.textviewSubject.setText("");
                e.printStackTrace();
            }
            try {
                holder.mBinding.textViewSubmittedOn.setText("Submitted on " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentStudent.getSubmissionDateTime())).toUpperCase());
            } catch (Exception e) {
                holder.mBinding.textViewSubmittedOn.setText("");
                e.printStackTrace();
            }
            try {
                holder.mBinding.textviewAssignedOn.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentStudent.getAssignedDateTime())));
            } catch (Exception e) {
                holder.mBinding.textviewAssignedOn.setText("");
                e.printStackTrace();
            }
            try {
                holder.mBinding.textviewAssignedByName.setText(assignmentStudent.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignmentStudent.getAssignedBy().getName() : "");
            } catch (Exception e) {
                holder.mBinding.textviewAssignedByName.setText("");
                e.printStackTrace();
            }
            try {
                holder.mBinding.textViewAssignmentType.setText(getAssignmentType(assignmentStudent.getAssignmentType()));
            } catch (Exception e) {
                holder.mBinding.textViewAssignmentType.setText("");
                e.printStackTrace();
            }

//            try {
//                holder.mBinding.textviewSubject.setTextColor(getSubjectColor(assignmentStudent.getSubject().getId(), mRandomColorPosition, mExtraColorArray));
//            } catch (Exception e) {
//                holder.mBinding.textviewSubject.setTextColor(Color.BLACK);
//                e.printStackTrace();
//            }
//            setAssignmentScore(assignmentStudent, holder.mBinding.textviewScore);
//            setAssignmentThumbnail(assignmentStudent, holder.mBinding.imageviewAssignmentThumbnail);

            holder.mBinding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().startActivity(AssignmentDetailActivity.startAssignmentDetailActivity(getContext(), assignmentStudent.getObjectId(), assignmentStudent.getDocId()));
                }
            });
        }

        private void setAssignmentScore(AssignmentStudent assignmentStudent, TextViewCustom textView) {
            if (getAssignmentType(assignmentStudent.getAssignmentType()).equals(getString(R.string.quiz))) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(AssignmentDetailActivity.getScore(assignmentStudent.getAssignmentScore(), assignmentStudent.getTotalScore()));

            } else {
                textView.setVisibility(View.GONE);
            }

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
            // this.sort(filterList);
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
            setDefault();
            if (filterList != null) {

                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        mFilterBySubject = filterSectionItem.getName();
                        break;
                    }
                }
            }
            getSubmittedAssignments(mStartDate, mEndDate, mFilterBySubject, mSkip, mLimit);
        }

        public void clear() {

            mValues.clear();
            notifyDataSetChanged();
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

        private int getSubjectColor(String subjectId, int randomColorPosition, int[] extraColorArray) {
            if (mSubjectMap.containsKey(subjectId)) {
                return mSubjectMap.get(subjectId).getTextColor();
            } else {
                int color = extraColorArray[randomColorPosition % extraColorArray.length];
                randomColorPosition++;
                return color;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RyanLayoutSubmittedAssignmentItemviewBinding mBinding;

            public ViewHolder(RyanLayoutSubmittedAssignmentItemviewBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }
}
