package in.securelearning.lil.android.assignments.views.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.events.LoadAssignmentResponseListTeacher;
import in.securelearning.lil.android.assignments.model.PendingSummaryTeacherActivityModel;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.assignments.views.fragment.pendingassignments.PendingAssignmentPresenter;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.ImageUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class StudentListFragment extends Fragment {

    @Inject
    public PendingSummaryTeacherActivityModel mPendingSummaryTeacherActivityModel;
    @Inject
    public RxBus mRxBus;
    private View mRootView;
    private RecyclerView mAssignmentRecyclerView;

    private PendingAssignmentPresenter mPendingAssignmentPresenter;
    private Disposable mSubscription;

    private Assignment mAssignment;

    public StudentListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        InjectorAssignment.INSTANCE.getComponent().inject(this);

        mRootView = inflater.inflate(R.layout.layout_assignment_recycler_view, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorQuizPrimaryDark));
        //  getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorGreenDark));
        initializeViews();
        initializeRecyclerView(new ArrayList<AssignmentResponse>());

        listenRxBusEvents();

        mAssignment = (Assignment) getArguments().getSerializable("assignment");


        getData();

        return mRootView;
    }

    private void unsubscribeEvent() {
        mSubscription.dispose();
    }

    private void getData() {

        mPendingSummaryTeacherActivityModel.getAllStudentList(mAssignment.getDocId(), mPendingSummaryTeacherActivityModel.getGroups(mAssignment.getAssignedGroups()));
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object event) {

                        if (event instanceof LoadAssignmentResponseListTeacher)
                            initializeRecyclerView(((LoadAssignmentResponseListTeacher) event).getAssignmentResponses());

                    }


                });
    }


    private void initializeViews() {
        mAssignmentRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);

    }


    private void initializeRecyclerView(List<AssignmentResponse> assignmentResponses) {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAssignmentRecyclerView.setLayoutManager(mLayoutManager);
        AssignmentAdapter mRecommendAdapter = new AssignmentAdapter(assignmentResponses);
        mAssignmentRecyclerView.setAdapter(mRecommendAdapter);
    }


    public class AssignmentAdapter extends RecyclerView.Adapter<StudentListFragment.AssignmentAdapter.ViewHolder> {

        private List<AssignmentResponse> assignmentResponses = new ArrayList<>();

        AssignmentAdapter(List<AssignmentResponse> list) {
            this.assignmentResponses = list;
        }

        @Override
        public StudentListFragment.AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assignment_itemview_pending, parent, false);
            return new StudentListFragment.AssignmentAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final StudentListFragment.AssignmentAdapter.ViewHolder holder, final int position) {


            AssignmentResponse assignmentResponse = assignmentResponses.get(position);


            holder.titleTV.setText(assignmentResponse.getAssignmentTitle());

            holder.subjectTV.setText(assignmentResponse.getMetaInformation().getSubject().getName());
            holder.dueOnTV.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate())));
            holder.assignedByNameTV.setText(assignmentResponse.getAssignedBy() != null ? getString(R.string.assigned_by) + assignmentResponse.getAssignedBy().getName() : "");


            AssignmentStatus assignmentStatus;
            assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate()).getTime());

//            if (assignmentResponse.getStage().equalsIgnoreCase(AssignmentStage.STAGE_ASSIGNED.getAssignmentStage())) {
//                holder.mAssignmentStatusImg.setImageResource(R.drawable.new_icon);
//
//            } else {
            if (assignmentStatus == AssignmentStatus.DUE)
                holder.mAssignmentStatusImg.setImageResource(R.drawable.clock_orange);
            else if (assignmentStatus == AssignmentStatus.OVERDUE)
                holder.mAssignmentStatusImg.setImageResource(R.drawable.clock_red);
            else
                holder.mAssignmentStatusImg.setImageResource(R.drawable.clock_green);

            //}


            if (assignmentResponse.getThumbnail() != null && assignmentResponse.getThumbnail().getUrl() != null && assignmentResponse.getThumbnail() != null && assignmentResponse.getThumbnail().getUrl().toString().trim().length() > 0) {
                Bitmap bitmap = ImageUtils.decodeBase64(assignmentResponse.getThumbnail() != null ? assignmentResponse.getThumbnail().getUrl() : "");
                holder.mAssignmentThumbnail.setImageBitmap(bitmap);
            } else if (assignmentResponse.getThumbnail().getLocalUrl() != null && assignmentResponse.getThumbnail().getLocalUrl().trim().length() > 0) {
                if (ImageUtils.isExternalStorageReadable()) {
                    holder.mAssignmentThumbnail.setImageBitmap(ImageUtils.loadFromStorage(getActivity(), assignmentResponse.getThumbnail().getLocalUrl()));
                }
            } else {
                //Load from server
            }


            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent mIntent = new Intent(getActivity(), AssignmentDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("assignmentResponse", assignmentResponses.get(position));
                    mIntent.putExtras(bundle);
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), holder.mAssignmentThumbnail, holder.mAssignmentThumbnail.getTransitionName());
                    startActivity(mIntent, transitionActivityOptions.toBundle());
                }
            });

        }

        @Override
        public int getItemCount() {
            return assignmentResponses.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private View mItemView;
            private TextView titleTV, assignedByNameTV, assignedOnTV, subjectTV, dueOnTV;
            private ImageView mAssignmentThumbnail, mAssignmentStatusImg;

            public ViewHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                mAssignmentThumbnail = (ImageView) mItemView.findViewById(R.id.imageview_assignment_thumbnail);
                mAssignmentStatusImg = (ImageView) mItemView.findViewById(R.id.imageview_assignment_status);
                titleTV = (TextView) mItemView.findViewById(R.id.textview_assignment_title);
                assignedByNameTV = (TextView) mItemView.findViewById(R.id.textview_assigned_by_name);
                assignedOnTV = (TextView) mItemView.findViewById(R.id.textview_assigned_on);
                subjectTV = (TextView) mItemView.findViewById(R.id.textview_subject);
                dueOnTV = (TextView) mItemView.findViewById(R.id.textview_due_on);
            }
        }
    }

}
