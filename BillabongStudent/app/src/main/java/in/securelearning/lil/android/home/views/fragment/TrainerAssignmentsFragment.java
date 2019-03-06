package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.RyanLayoutAssignmentTeacherItemBinding;
import in.securelearning.lil.android.assignments.events.AllStudentSubmittedEvent;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.views.activity.StudentSummaryActivity;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentMinimal;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.learningnetwork.events.LoadNewAssignmentDownloadEvent;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class TrainerAssignmentsFragment extends Fragment {

    @Inject
    public AssignmentTeacherModel mTeacherModel;
    @Inject
    public RxBus mRxBus;

    private View mRootView;
    private RecyclerView mAssignmentRecyclerView;
    private AssignmentAdapter mAssignmentAdapter;
    private List<Assignment> mFilterList = new ArrayList<>();
    private LinearLayout mNoResultLayout;

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
    private Disposable mSubscription;

    public TrainerAssignmentsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(int columnCount, String trainingId, String trainingGroupId) {
        TrainerAssignmentsFragment fragment = new TrainerAssignmentsFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        args.putString(TRAINING_ID, trainingId);
        args.putString(TRAINING_GROUP_ID, trainingGroupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mRootView = inflater.inflate(R.layout.layout_assigned_recycler_view, container, false);
        listenRxEvent();
        initializeViews();
        setDefault();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
            mTrainingId = getArguments().getString(TRAINING_ID);
            mTrainingGroupId = getArguments().getString(TRAINING_GROUP_ID);
            mFromDate = "";
            mToDate = DateUtils.getCurrentISO8601DateString();
            getData(mSkip, mLimit, mFromDate, mToDate, mTrainingGroupId);
        }

        return mRootView;
    }

    private void listenRxEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof LoadNewAssignmentDownloadEvent || event instanceof AllStudentSubmittedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    setDefault();
                                    getData(mSkip, mLimit, mFromDate, mToDate, mTrainingGroupId);


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

    private void setDefault() {
        initializeRecyclerView(new ArrayList<AssignmentMinimal>());
        mSkip = 0;
    }

    private void getData(int skip, final int limit, String fromDate, String toDate, String groupId) {
        mTeacherModel.getAssignmentListByGroupId(fromDate, toDate, groupId, skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentMinimal>>() {
            @Override
            public void accept(ArrayList<AssignmentMinimal> assignmentStudents) throws Exception {

                mSkip += assignmentStudents.size();
                noResultFound(mSkip);
                if (assignmentStudents.size() < limit) {
                    mAssignmentRecyclerView.removeOnScrollListener(null);
                }
                mAssignmentAdapter.addItem(assignmentStudents);
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
    private void initializeRecyclerView(ArrayList<AssignmentMinimal> assignments) {

        noResultFound(assignments.size());
        setAdapterAndDoPagination(assignments);

    }

    private void setAdapterAndDoPagination(ArrayList<AssignmentMinimal> assignments) {
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

                            getData(mSkip, mLimit, mFromDate, mToDate, mTrainingGroupId);

                        }
                    }

                }

            });
        }
    }

    private class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

        ArrayList<AssignmentMinimal> mValues;

        public AssignmentAdapter(ArrayList<AssignmentMinimal> assignmentMinimals) {
            this.mValues = assignmentMinimals;
        }

        @Override
        public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutAssignmentTeacherItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_assignment_teacher_item, parent, false);
            return new AssignmentAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final AssignmentAdapter.ViewHolder holder, final int position) {
            final AssignmentMinimal assignmentMinimal = mValues.get(position);
            holder.mBinding.textviewAssignmentTitle.setText(assignmentMinimal.getTitle());
            holder.mBinding.textviewSubject.setText(assignmentMinimal.getMetaInformation().getSubject().getName());
            holder.mBinding.textviewDueOn.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentMinimal.getDueDate())));
            holder.mBinding.textviewAssignedOn.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentMinimal.getAssignedDateTime())));
            holder.mBinding.textviewAssignedByName.setText(assignmentMinimal.getAssignedBy() != null ? getString(R.string.assigned_by) + " " + assignmentMinimal.getAssignedBy().getName() : "");
            holder.mBinding.textviewAssignedTo.setText(assignmentMinimal.getAssignedGroups().get(0).getName());
            holder.mBinding.textViewAssignmentType.setText(getAssignmentType(assignmentMinimal.getAssignmentType()));
            holder.mBinding.textviewSubject.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            setAssignmentThumbnail(assignmentMinimal, holder.mBinding.imageviewAssignmentThumbnail);
            if (!TextUtils.isEmpty(assignmentMinimal.getObjectId())) {
                setSubmittedResponseCount(assignmentMinimal.getObjectId(), "", holder.mBinding.textViewSubmittedResponseCounts);

            }
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(StudentSummaryActivity.getStartStudentSummaryActivity(getContext(), assignmentMinimal.getAssignmentDocId(), assignmentMinimal.getDocId()));
                    holder.mBinding.textViewSubmittedResponseCounts.setVisibility(View.GONE);
                }
            });
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

}
