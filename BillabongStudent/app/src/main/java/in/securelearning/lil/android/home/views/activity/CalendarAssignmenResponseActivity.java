package in.securelearning.lil.android.home.views.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.utils.DateUtils;

public class CalendarAssignmenResponseActivity extends AppCompatActivity {

    List<AssignmentResponse> mDueAssignmentList = new ArrayList<>();
    private RecyclerView mAssignmentRecyclerView;
    private AssignmentAdapter mAssignmentAdapter;
    private ImageButton mBackButton;
    private TextView mAssignmentDateTextView;
    private String strSelectedDate;
    private ImageView mAssignmentImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);
        getWindow().setStatusBarColor(ContextCompat.getColor(CalendarAssignmenResponseActivity.this, R.color.colorAssignment));
        mDueAssignmentList = (List<AssignmentResponse>) getIntent().getSerializableExtra("dueAssignmentList");
        strSelectedDate = getIntent().getExtras().getString("eventDate");
        initializeViews();
        initializeUIAndClickListeners();

        if (mDueAssignmentList != null) {
            initializeRecyclerView(mDueAssignmentList);
        }

    }

    private void initializeViews() {
        mAssignmentImageView = (ImageView) findViewById(R.id.imageViewAssignment);
        mAssignmentDateTextView = (TextView) findViewById(R.id.textView_assignmentDate);
        mBackButton = (ImageButton) findViewById(R.id.button_back);
        mAssignmentRecyclerView = (RecyclerView) findViewById(in.securelearning.lil.android.app.R.id.recycler_view);

    }

    private void initializeUIAndClickListeners() {

        mAssignmentImageView.setVisibility(View.VISIBLE);
        mAssignmentDateTextView.setText(strSelectedDate);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void initializeRecyclerView(List<AssignmentResponse> assignmentResponses) {
        if (getResources().getBoolean(R.bool.isTablet)) {
            mAssignmentRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
            mAssignmentAdapter = new AssignmentAdapter(assignmentResponses);
            mAssignmentRecyclerView.setAdapter(mAssignmentAdapter);
        } else {
            mAssignmentRecyclerView.setLayoutManager(new LinearLayoutManager(CalendarAssignmenResponseActivity.this, LinearLayoutManager.VERTICAL, false));
            mAssignmentAdapter = new AssignmentAdapter(assignmentResponses);
            mAssignmentRecyclerView.setAdapter(mAssignmentAdapter);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

        private List<AssignmentResponse> assignmentResponses = new ArrayList<>();

        AssignmentAdapter(List<AssignmentResponse> list) {
            this.assignmentResponses = list;
        }

        @Override
        public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assignment_itemview_pending, parent, false);
            return new AssignmentAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AssignmentAdapter.ViewHolder holder, final int position) {

            AssignmentResponse assignmentResponse = assignmentResponses.get(position);

            holder.titleTextView.setText(assignmentResponse.getAssignmentTitle());
            holder.subjectTextView.setText(assignmentResponse.getMetaInformation().getSubject().getName());
            holder.dueOnTextView.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate())));
            holder.assignedOnTextView.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentResponse.getAssignedDateTime())));
            holder.assignedByNameTextView.setText(assignmentResponse.getAssignedBy() != null ? getString(in.securelearning.lil.android.app.R.string.assigned_by) + " " + assignmentResponse.getAssignedBy().getName() : "");

            AssignmentStatus assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate()).getTime());

            setAssignmentStatus(assignmentStatus, holder.mAssignmentStatusImg);
            setAssignmentThumbnail(assignmentResponse, holder.mAssignmentThumbnail);


//            if (assignmentResponse.getThumbnail() != null && assignmentResponse.getThumbnail().getImage() != null && assignmentResponse.getThumbnail() != null && assignmentResponse.getThumbnail().getImage().toString().trim().length() > 0) {
//                Bitmap bitmap = ImageUtils.decodeBase64(assignmentResponse.getThumbnail() != null ? assignmentResponse.getThumbnail().getImage() : "");
//                holder.mAssignmentThumbnail.setImageBitmap(bitmap);
//            } else if (assignmentResponse.getThumbnail().getLocalUrl() != null && assignmentResponse.getThumbnail().getLocalUrl().trim().length() > 0) {
//                if (ImageUtils.isExternalStorageReadable()) {
//                    holder.mAssignmentThumbnail.setImageBitmap(ImageUtils.loadFromStorage(CalendarAssignmenResponseActivity.this, assignmentResponse.getThumbnail().getLocalUrl()));
//                }
//            } else {
//                //Load from server
//            }

//            if (assignmentResponse.getThumbnail() != null && assignmentResponse.getThumbnail().getLocalUrl() != null && !assignmentResponse.getThumbnail().getLocalUrl().isEmpty()) {
//                String strImagePath = assignmentResponse.getThumbnail().getLocalUrl();
//                Picasso.with(CalendarAssignmenResponseActivity.this).load(strImagePath).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(holder.mAssignmentThumbnail);
//            }

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent mIntent = new Intent(CalendarAssignmenResponseActivity.this, AssignmentDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("assignmentResponse", assignmentResponses.get(position));
                    mIntent.putExtras(bundle);
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(CalendarAssignmenResponseActivity.this, holder.mAssignmentThumbnail, holder.mAssignmentThumbnail.getTransitionName());
                    startActivity(mIntent, transitionActivityOptions.toBundle());
                }
            });

        }

        private void setAssignmentStatus(AssignmentStatus assignmentStatus, ImageView assignmentThumbnail) {
            if (assignmentStatus == AssignmentStatus.OVERDUE) {
                assignmentThumbnail.setImageResource(R.drawable.clock_red);
            } else if (assignmentStatus == AssignmentStatus.DUE) {
                assignmentThumbnail.setImageResource(R.drawable.clock_orange);
            } else {
                assignmentThumbnail.setImageResource(R.drawable.clock_green);
            }

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
                    Picasso.with(getBaseContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                } else {
                    Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getBaseContext()).load(assignmentResponse.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(600, 440).centerInside().into(assignmentThumbnailImageView);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(600, 440).centerInside().into(assignmentThumbnailImageView);

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return assignmentResponses.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private View mItemView;
            private TextView titleTextView, assignedByNameTextView, assignedOnTextView, subjectTextView, dueOnTextView;
            private ImageView mAssignmentThumbnail, mAssignmentStatusImg;

            public ViewHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                mAssignmentThumbnail = (ImageView) mItemView.findViewById(in.securelearning.lil.android.app.R.id.imageview_assignment_thumbnail);
                mAssignmentStatusImg = (ImageView) mItemView.findViewById(in.securelearning.lil.android.app.R.id.imageview_assignment_status);
                titleTextView = (TextView) mItemView.findViewById(in.securelearning.lil.android.app.R.id.textview_assignment_title);
                assignedByNameTextView = (TextView) mItemView.findViewById(in.securelearning.lil.android.app.R.id.textview_assigned_by_name);
                assignedOnTextView = (TextView) mItemView.findViewById(in.securelearning.lil.android.app.R.id.textview_assigned_on);
                subjectTextView = (TextView) mItemView.findViewById(in.securelearning.lil.android.app.R.id.textview_subject);
                dueOnTextView = (TextView) mItemView.findViewById(in.securelearning.lil.android.app.R.id.textview_due_on);
            }
        }
    }

}
