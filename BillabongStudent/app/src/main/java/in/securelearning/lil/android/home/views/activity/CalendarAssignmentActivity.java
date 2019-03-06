package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAssignmentCalendarListBinding;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.views.activity.StudentSummaryActivity;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class CalendarAssignmentActivity extends AppCompatActivity {

    @Inject
    AssignmentTeacherModel mAssignmentTeacherModel;
    @Inject
    public RxBus mRxBus;

    private LayoutAssignmentCalendarListBinding mBinding;
    private AssignmentAdapter mAssignmentAdapter;
    private String mTitleDate;

    public static Intent startCalendarAssignmentActivity(Context context, String startDate, String endDate, String titleDate) {
        Intent intent = new Intent(context, CalendarAssignmentActivity.class);
        intent.putExtra("startDate", startDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("titleDate", titleDate);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_assignment_calendar_list);

        InjectorAssignment.INSTANCE.getComponent().inject(this);

        String startDate = getIntent().getStringExtra("startDate");
        String endDate = getIntent().getStringExtra("endDate");
        mTitleDate = getIntent().getStringExtra("titleDate");

        initializeViews();

        getData(startDate, endDate);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar_assignment_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(String startDate, String endDate) {
        mAssignmentTeacherModel.getAssignmentsFromDueDate(startDate, endDate).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Assignment>>() {
            @Override
            public void accept(ArrayList<Assignment> assignments) throws Exception {
                initializeRecyclerView(assignments);

            }
        });
    }

    private void initializeViews() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleDate = mTitleDate.replaceAll("\n", ", ");
        setTitle(mTitleDate);
        getWindow().setStatusBarColor(ContextCompat.getColor(CalendarAssignmentActivity.this, R.color.colorAssignmentPrimary));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * initialize recycler view for mValues
     * check device is tablet or phone and load recycler view according to device
     *
     * @param assignments
     */
    private void initializeRecyclerView(List<Assignment> assignments) {
        if (assignments.isEmpty()) {
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
        } else {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.VISIBLE);

            if (getResources().getBoolean(R.bool.isTablet)) {
                mBinding.recyclerView.setLayoutManager(new GridLayoutManager(CalendarAssignmentActivity.this, 2));
                mAssignmentAdapter = new AssignmentAdapter(assignments);
                mBinding.recyclerView.setAdapter(mAssignmentAdapter);

            } else {

                mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(CalendarAssignmentActivity.this, LinearLayoutManager.VERTICAL, false));
                mAssignmentAdapter = new AssignmentAdapter(assignments);
                mBinding.recyclerView.setAdapter(mAssignmentAdapter);
            }
        }

    }

    public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

        List<Assignment> mValues = new ArrayList<>();

        public AssignmentAdapter(List<Assignment> assignments) {
            this.mValues = assignments;
        }

        @Override
        public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assignment_teacher_item, parent, false);
            return new ViewHolder(view);
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

            setAssignmentStatus(mAssignment, holder.mAssignmentStatusImageView);
            setAssignmentThumbnail(mAssignment, holder.mAssignmentThumbnail);

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(StudentSummaryActivity.getStartStudentSummaryActivity(getBaseContext(), mAssignment.getDocId(), ""));
                }
            });

        }

        private void setAssignmentThumbnail(Assignment assignment, ImageView assignmentThumbnail) {
            if (assignment.getThumbnail() != null && assignment.getThumbnail().getLocalUrl() != null && !assignment.getThumbnail().getLocalUrl().isEmpty()) {
                String strImagePath = assignment.getThumbnail().getLocalUrl();
                Picasso.with(getBaseContext()).load(strImagePath).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnail);
            } else if (assignment.getThumbnail() != null && assignment.getThumbnail().getUrl() != null && !assignment.getThumbnail().getUrl().isEmpty()) {
                Picasso.with(getBaseContext()).load(assignment.getThumbnail().getUrl()).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnail);
            } else if (assignment.getThumbnail() != null && assignment.getThumbnail().getThumb() != null && !assignment.getThumbnail().getThumb().isEmpty()) {
                Picasso.with(getBaseContext()).load(assignment.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnail);
            } else {
                Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnail);
            }

        }

        private void setAssignmentStatus(Assignment assignment, ImageView assignmentStatusImageView) {
            AssignmentStatus assignmentStatus;
            assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignment.getDueDate()).getTime());
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
