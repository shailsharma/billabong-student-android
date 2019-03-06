package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.RyanLayoutAssignmentItemviewPendingBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
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

/**
 * Created by Chaitendra on 28-Dec-17.
 */

public class TraineeAssignmentsFragment extends Fragment {

    @Inject
    AssignmentResponseStudentModel mAssignmentResponseStudentModel;
    @Inject
    RxBus mRxBus;
    private Disposable mSubscription;
    public static final String TRAINING_ID = "trainingId";
    public static final String COLUMN_COUNT = "columnCount";
    public static final String TRAINING_GROUP_ID = "trainingGroupId";
    private int mColumnCount;
    private String mTrainingId;
    private String mTrainingGroupId;
    private int mSkip = 0;
    private int mLimit = 10;
    private String mToDate = "";
    private String mFromDate = "";
    LayoutRecyclerViewBinding mBinding;
    AssignmentAdapter mAssignmentAdapter;

    public static Fragment newInstance(int columnCount, String trainingId, String trainingGroupId) {
        TraineeAssignmentsFragment traineeAssignmentsFragment = new TraineeAssignmentsFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        args.putString(TRAINING_ID, trainingId);
        args.putString(TRAINING_GROUP_ID, trainingGroupId);
        traineeAssignmentsFragment.setArguments(args);
        return traineeAssignmentsFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorAssignment.INSTANCE.getComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_recycler_view, container, false);
        mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
            mTrainingId = getArguments().getString(TRAINING_ID);
            mTrainingGroupId = getArguments().getString(TRAINING_GROUP_ID);
            mFromDate = "";
            mToDate = DateUtils.getCurrentISO8601DateString();
        }
        listenRxEvent();
        initializeRecyclerView();
        return mBinding.getRoot();
    }

    private void listenRxEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof AssignmentSubmittedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    setDefault();
                                    getAssignments(mFromDate, mToDate, mTrainingGroupId, mSkip, mLimit);

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

    private void initializeRecyclerView() {
        LinearLayoutManager layoutManager = null;
        if (mColumnCount > 1) {
            layoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
            mBinding.list.setLayoutManager(layoutManager);
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mBinding.list.setLayoutManager(layoutManager);
        }
        if (mAssignmentAdapter == null) {
            mAssignmentAdapter = new AssignmentAdapter(new ArrayList<AssignmentStudent>(), 0);
        }
        mBinding.list.setAdapter(mAssignmentAdapter);
        if (mSkip == 0) {
            getAssignments(mFromDate, mToDate, mTrainingGroupId, mSkip, mLimit);
        }

        final LinearLayoutManager finalLayoutManager = layoutManager;
        mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mSkip - 1) {
                        getAssignments(mFromDate, mToDate, mTrainingGroupId, mSkip, mLimit);
                    }
                }
            }
        });
    }

    private void getAssignments(final String fromDate, final String toDate, final String groupId, final int skip, final int limit) {
        mAssignmentResponseStudentModel.getAssignmentListByGroupId(fromDate, toDate, groupId, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
            @Override
            public void accept(ArrayList<AssignmentStudent> assignmentStudents) throws Exception {

                mSkip += assignmentStudents.size();
                noResultFound(mSkip);
                if (assignmentStudents.size() < limit) {
                    mBinding.list.removeOnScrollListener(null);
                }
                mAssignmentAdapter.addItem(assignmentStudents);
            }
        });
    }

    private void setDefault() {
        mSkip = 0;
        if (mAssignmentAdapter != null) {
            mAssignmentAdapter.clear();
        }
    }

    private void noResultFound(int size) {
        if (size > 0) {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.list.setVisibility(View.VISIBLE);
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.imageViewNoResult.setImageResource(R.drawable.assignment);
            mBinding.textViewNoResult.setText(getContext().getString(R.string.messageNoAssignments));
        }
    }

    private class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

        ArrayList<AssignmentStudent> mValues;
        private int position;
        private int mRandomColorPosition;
        private int[] mExtraColorArray;

        public AssignmentAdapter(ArrayList<AssignmentStudent> assignmentStudents, int index) {
            this.mValues = assignmentStudents;
            this.position = index;
            this.mRandomColorPosition = 0;
            this.mExtraColorArray = getContext().getResources().getIntArray(R.array.subject_text_color_extra);
        }

        @Override
        public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutAssignmentItemviewPendingBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_assignment_itemview_pending, parent, false);
            return new AssignmentAdapter.ViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(AssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentStudent assignmentStudent = mValues.get(position);

            holder.mBinding.textviewAssignmentTitle.setText(assignmentStudent.getAssignmentTitle());
            holder.mBinding.textviewSubject.setText(assignmentStudent.getSubject().getName());
            holder.mBinding.textviewDueOn.setText("Due On " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentStudent.getAssignmentDueDate())));
            holder.mBinding.textviewAssignedOn.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentStudent.getAssignedDateTime())));
            holder.mBinding.textviewAssignedByName.setText(assignmentStudent.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignmentStudent.getAssignedBy().getName() : "");
            holder.mBinding.textViewAssignmentType.setText(getAssignmentType(assignmentStudent.getAssignmentType()));
//            holder.mBinding.textviewSubject.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//            setAssignmentThumbnail(assignmentStudent, holder.mBinding.imageviewAssignmentThumbnail);
            setAssignmentStatus(assignmentStudent, holder.mBinding.imageviewAssignmentStatus);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
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
            setDefault();
            if (filterList != null) {
                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        break;
                    }
                }
            }
            getAssignments(mFromDate, mToDate, mTrainingGroupId, mSkip, mLimit);
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
    }
}
