package in.securelearning.lil.android.assignments.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.StudentImageListRowBinding;
import in.securelearning.lil.android.assignments.events.LoadAssignmentResponseListTeacher;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.model.PendingSummaryTeacherActivityModel;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Coordinator;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.dataobjects.TimeUtils;
import in.securelearning.lil.android.learningnetwork.views.activity.GroupDetailActivity;
import in.securelearning.lil.android.profile.views.activity.UserPublicProfileActivity;
import in.securelearning.lil.android.resources.view.activity.VideoPlayActivity;
import in.securelearning.lil.android.resources.view.activity.VimeoActivity;
import in.securelearning.lil.android.resources.view.activity.YoutubePlayActivity;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StudentSummaryActivity extends AppCompatActivity {

    @Inject
    public PendingSummaryTeacherActivityModel mPendingSummaryTeacherActivityModel;
    @Inject
    public RxBus mRxBus;
    @Inject
    AssignmentModel mAssignmentModel;
    @Inject
    AssignmentTeacherModel mAssignmentTeacherModel;

    public static final String ASSIGNMENT_MINIMAL_DOC_ID = "assignmentMinimalDocId";
    public static final String ASSIGNMENT_DOC_ID = "docIdAssignment";

    private int mColor = Color.BLACK;
    private int color2 = Color.parseColor("#d3d3d3");
    private Toolbar mToolbar;
    private boolean isTypeQuiz = false;
    private boolean isTypeCourse = false;
    private boolean isTypeResource = false;
    private RecyclerView assignmentExcellentRecycleList, assignmentAverageRecycleList, assignmentPoorRecycleList,
            assignmentPendingRecycleList, assignmentViewedByRecycleList;
    private Disposable mSubscription;
    private LinearLayout lay_submitionDetails, mAssignmentDurationLayout, mLayAssignment4;
    private Assignment mAssignment;
    private ImageView mAssignmentThumbnail;
    private TextView mInstructionTextView, mViewMoreLessTextView, mSubmittedCountTextView, mAssignmentViewedByRecycleListText,
            mPendingCountTextView, mAssignmentText1, mAssignmentText2, mAssignmentText3, mAssignmentText4,
            mAssignmentExcellentRecycleListText, mAssignmentAverageRecycleListText, mAssignmentPoorRecycleListText, mAssignmentPendingRecycleListText;
    private CardView mInstructionView, cardViewGraph;
    private Group mAssignedGroup;
    private ArrayList<GroupMember> mMemberArrayList = new ArrayList<>();
    private List<AssignmentResponse> mAssignmentResponses = new ArrayList<>();
    private Class mObjectClass = null;
    private PieChart mAssignmentChart;
    private View mAssignmentImage1, mAssignmentImage2, mAssignmentImage3, mAssignmentImage4;
    private TextView subjectText, topicText, textview_due_date_new, textview_assigned_date_new, textAssignedByName,
            textAssigneToName, mDurationTextView, mTextviewAssignmentType, mTextViewDueDate, mTxtViewedBy;
    private ImageView imgAssignedBy, imgAssignedTo;
    private ImageView mAssignedByImageView, mAssignedToImageView;
    private Menu menu;
    private PercentRelativeLayout mGraphLayout;
    private String mAssignmentMinimalDocId = "";

    public static Intent getStartStudentSummaryActivity(Context context, String assignmentDocId, String assignmentMinimalDocId) {
        Intent intent = new Intent(context, StudentSummaryActivity.class);
        intent.putExtra(ASSIGNMENT_DOC_ID, assignmentDocId);
        intent.putExtra(ASSIGNMENT_MINIMAL_DOC_ID, assignmentMinimalDocId);
        return intent;
    }

    private static void setTranslucentStatusBarLollipop(Window window) {
        window.setStatusBarColor(
                window.getContext()
                        .getResources()
                        .getColor(R.color.colorTransparent));
    }

    private static void setTranslucentStatusBarKiKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_summary_new_final);
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(StudentSummaryActivity.this, R.color.colorAssignmentPrimary));
        initializeViews();
        listenRxBusEvents();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String assignmentDocId = bundle.getString(ASSIGNMENT_DOC_ID);
            mAssignmentMinimalDocId = bundle.getString(ASSIGNMENT_MINIMAL_DOC_ID);
            if (!TextUtils.isEmpty(assignmentDocId)) {
                mAssignment = mAssignmentModel.getAssignmentSync(assignmentDocId);
            } else {
                ToastUtils.showToastAlert(getBaseContext(), getString(R.string.assignment_not_found));
            }
        }
        if (mAssignment != null) {
            getColorForSubject();
            setAssignmentDetails();
            getData();
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(mAssignment.getTitle());
            setLayoutForResorcesAndCourses(mAssignment.getAssignmentType());
            mAssignmentTeacherModel.deleteAssignmentResponsesForCurrentAssignment(mAssignment.getObjectId());
        } else {
            finish();
        }
    }

    private void setTranslucentStatusBar(Window window) {
        if (window == null) return;
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatusBarLollipop(window);
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusBarKiKat(window);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        if (isTypeQuiz) {
            MenuItem item = menu.findItem(R.id.action_play);
            item.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_play) {
            if (isTypeQuiz) {

            } else if (isTypeCourse) {
                startActivity(CourseDetailActivity.getStartActivityIntent(getBaseContext(), mAssignment.getUidCourse(), mObjectClass, ""));
            } else {
                startResourceActivity();
            }
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getColorForSubject() {
        mColor = PrefManager.getColorForSubject(this, mAssignment.getMetaInformation().getSubject().getId());
    }

    private void initializeViews() {
        mAssignmentThumbnail = (ImageView) findViewById(R.id.imageview_assignment_thumbnail);
        subjectText = (TextView) findViewById(R.id.subjectText);
        topicText = (TextView) findViewById(R.id.topicText);
        textview_due_date_new = (TextView) findViewById(R.id.textview_due_date_new);
        textview_assigned_date_new = (TextView) findViewById(R.id.textview_assigned_date_new);
        textAssignedByName = (TextView) findViewById(R.id.textAssignedByName);
        textAssigneToName = (TextView) findViewById(R.id.textAssigneToName);
        mAssignedByImageView = (ImageView) findViewById(R.id.imgAssignedBy);
        mAssignedToImageView = (ImageView) findViewById(R.id.imgAssignedTo);
        lay_submitionDetails = (LinearLayout) findViewById(R.id.lay_submitionDetails);
        cardViewGraph = (CardView) findViewById(R.id.cardViewGraph);
        mInstructionView = (CardView) findViewById(R.id.layoutInstruction);
        mInstructionTextView = (TextView) findViewById(R.id.textview_instruction);
        mViewMoreLessTextView = (TextView) findViewById(R.id.textViewMoreLess);
        mPendingCountTextView = (TextView) findViewById(R.id.textview_pendingBy_count);
        mSubmittedCountTextView = (TextView) findViewById(R.id.textview_submittedBy_count);
        mAssignmentImage1 = (View) findViewById(R.id.assignmentImage1);
        mAssignmentImage2 = (View) findViewById(R.id.assignmentImage2);
        mAssignmentImage3 = (View) findViewById(R.id.assignmentImage3);
        mAssignmentImage4 = (View) findViewById(R.id.assignmentImage4);
        mAssignmentText1 = (TextView) findViewById(R.id.assignmentText1);
        mAssignmentText2 = (TextView) findViewById(R.id.assignmentText2);
        mAssignmentText3 = (TextView) findViewById(R.id.assignmentText3);
        mAssignmentText4 = (TextView) findViewById(R.id.assignmentText4);
        mAssignmentChart = (PieChart) findViewById(R.id.assignmentChart);
        assignmentExcellentRecycleList = (RecyclerView) findViewById(R.id.assignmentExcellentRecycleList);
        assignmentAverageRecycleList = (RecyclerView) findViewById(R.id.assignmentAverageRecycleList);
        assignmentPoorRecycleList = (RecyclerView) findViewById(R.id.assignmentPoorRecycleList);
        assignmentPendingRecycleList = (RecyclerView) findViewById(R.id.assignmentPendingRecycleList);
        assignmentViewedByRecycleList = (RecyclerView) findViewById(R.id.assignmentViewedByRecycleList);
        mAssignmentExcellentRecycleListText = (TextView) findViewById(R.id.assignmentExcellentRecycleListText);
        mAssignmentAverageRecycleListText = (TextView) findViewById(R.id.assignmentAverageRecycleListText);
        mAssignmentPoorRecycleListText = (TextView) findViewById(R.id.assignmentPoorRecycleListText);
        mAssignmentPendingRecycleListText = (TextView) findViewById(R.id.assignmentPendingRecycleListText);
        mAssignmentViewedByRecycleListText = (TextView) findViewById(R.id.assignmentViewedByRecycleListText);
        mDurationTextView = (TextView) findViewById(R.id.textview_duration);
        mTextviewAssignmentType = (TextView) findViewById(R.id.textviewAssignmentType);
        mTextViewDueDate = (TextView) findViewById(R.id.textViewDueDate);
        mTxtViewedBy = (TextView) findViewById(R.id.txtViewedBy);
        mAssignmentDurationLayout = (LinearLayout) findViewById(R.id.layoutAssignmentDuration);
        mLayAssignment4 = (LinearLayout) findViewById(R.id.layAssignment4);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mGraphLayout = (PercentRelativeLayout) findViewById(R.id.graphLayout);
    }

    private void setAssignmentDetails() {
        getAssignmentType(mAssignment.getAssignmentType());
        setAssignmentThumbnail();
        setAssignedByThumbnail();
        setAssignedToThumbnail();
        setAssignmentStatus();
        setAssignmentDuration();
        setAssignmentInstruction();

        subjectText.setText(mAssignment.getMetaInformation().getSubject().getName());
        subjectText.setTextColor(mColor);
        topicText.setText(mAssignment.getMetaInformation().getTopic().getName());
        textview_due_date_new.setText(DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(mAssignment.getDueDate())));
        textview_due_date_new.setTextColor(mColor);
        textview_assigned_date_new.setText(DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(mAssignment.getAssignedDateTime())));
        textview_assigned_date_new.setTextColor(mColor);
        mTextviewAssignmentType.setBackgroundColor(mColor);
        if (mAssignment.getAssignmentType().equalsIgnoreCase("Objective Evaluation")) {
            mTextviewAssignmentType.setText("Quiz");
        } else {
            mTextviewAssignmentType.setText(mAssignment.getAssignmentType());
        }
        textAssignedByName.setText(mAssignment.getAssignedBy().getName());
        textAssigneToName.setText(mAssignment.getAssignedGroups().get(0).getName());
        ViewParent parent = textAssigneToName.getParent();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GroupDetailActivity.getIntentForGroupDetail(getBaseContext(), (mAssignment.getAssignedGroups().get(0).getId())));
            }
        };
        if (parent instanceof View) {
            ((View) parent).setOnClickListener(listener);
        } else {
            mAssignedToImageView.setOnClickListener(listener);
            textAssigneToName.setOnClickListener(listener);
        }


    }

    private void startResourceActivity() {
        FavouriteResource favouriteResource = new FavouriteResource();
        favouriteResource.setObjectId(mAssignment.getObjectId());
        favouriteResource.setTitle(mAssignment.getTitle());
        MetaInformation metaInformation = mAssignment.getMetaInformation();
        favouriteResource.setMetaInformation(metaInformation);
        favouriteResource.setUrlThumbnail(mAssignment.getThumbnail().getUrl());
        favouriteResource.setName(mAssignment.getUidResource());
        if (!TextUtils.isEmpty(mAssignment.getResourceType())) {
            String type = mAssignment.getResourceType();
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                if (type.equalsIgnoreCase("video")) {
                    favouriteResource.setUrlMain(mAssignment.getUidResource());
                    startActivity(VideoPlayActivity.getStartIntent(getBaseContext(), favouriteResource));
                } else if (type.equalsIgnoreCase("youtube#video")) {
                    startActivity(YoutubePlayActivity.getStartIntent(StudentSummaryActivity.this, favouriteResource, ""));
                } else if (type.equalsIgnoreCase("vimeo")) {
                    startActivity(VimeoActivity.getStartIntent(getBaseContext(), favouriteResource));
                }
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mToolbar);
            }
        }
    }

    private String getAssignmentType(String assignmentType) {
        if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_OBJECTIVE.getAssignmentType()) ||
                assignmentType.equalsIgnoreCase(AssignmentType.TYPE_SUBJECTIVE.getAssignmentType())) {
            isTypeQuiz = true;
            isTypeCourse = false;
            isTypeResource = false;
            String type = "";
            if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_OBJECTIVE.getAssignmentType())) {
                type = AssignmentType.TYPE_OBJECTIVE.getAssignmentType();
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_SUBJECTIVE.getAssignmentType())) {
                type = AssignmentType.TYPE_SUBJECTIVE.getAssignmentType();
            } else {
                type = "Quiz";
            }
            return type;
        } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_RESOURCE.getAssignmentType())) {
            String type = getString(R.string.resource);
            isTypeQuiz = false;
            isTypeCourse = false;
            isTypeResource = true;
            return type;
        } else {
            isTypeQuiz = false;
            isTypeCourse = true;
            isTypeResource = false;
            String type = "";
            if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType())) {
                type = AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType();
                mObjectClass = DigitalBook.class;
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType())) {
                type = AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType();
                mObjectClass = VideoCourse.class;
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType())) {
                type = AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType();
                mObjectClass = ConceptMap.class;
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType())) {
                type = AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType();
                mObjectClass = InteractiveImage.class;
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_Popup.getAssignmentType())) {
                type = AssignmentType.TYPE_Popup.getAssignmentType();
                mObjectClass = PopUps.class;
            } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType())) {
                type = AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType();
                mObjectClass = InteractiveVideo.class;
            }
            return type;
        }
    }

    private void setLayoutForResorcesAndCourses(String assignmentType) {
        if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_OBJECTIVE.getAssignmentType()) ||
                assignmentType.equalsIgnoreCase(AssignmentType.TYPE_SUBJECTIVE.getAssignmentType())) {
            mTxtViewedBy.setText("Submission");
            mGraphLayout.setVisibility(View.VISIBLE);
            mAssignmentExcellentRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentExcellentRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentAverageRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentAverageRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentPoorRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentPoorRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentPendingRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentPendingRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentViewedByRecycleListText.setVisibility(View.GONE);
            mAssignmentViewedByRecycleListText.setVisibility(View.GONE);
        } else {
            mTxtViewedBy.setText("Viewed By");
            mGraphLayout.setVisibility(View.GONE);
            mAssignmentExcellentRecycleListText.setVisibility(View.GONE);
            mAssignmentExcellentRecycleListText.setVisibility(View.GONE);
            mAssignmentAverageRecycleListText.setVisibility(View.GONE);
            mAssignmentAverageRecycleListText.setVisibility(View.GONE);
            mAssignmentPoorRecycleListText.setVisibility(View.GONE);
            mAssignmentPoorRecycleListText.setVisibility(View.GONE);
            mAssignmentPendingRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentPendingRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentViewedByRecycleListText.setVisibility(View.VISIBLE);
            mAssignmentViewedByRecycleListText.setVisibility(View.VISIBLE);
        }
    }

    private void setAssignmentThumbnail() {
        if (mAssignment.getThumbnail() != null) {
            String thumbnailPath = mAssignment.getThumbnail().getLocalUrl();
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = mAssignment.getThumbnail().getUrl();
            }
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = mAssignment.getThumbnail().getThumb();
            }
            try {
                if (!TextUtils.isEmpty(thumbnailPath)) {
                    Picasso.with(getBaseContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(mAssignmentThumbnail);
                } else {
                    Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mAssignmentThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getBaseContext()).load(mAssignment.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(mAssignmentThumbnail);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mAssignmentThumbnail);

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else if (mAssignment.getAssignmentType().equalsIgnoreCase("Objective Evaluation")) {
            try {
                Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mAssignmentThumbnail);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void setAssignedToThumbnail() {
        // TODO: 17-Jul-17 load group thumbnail whenever it comes with assignment response
        Picasso.with(getBaseContext()).load(R.drawable.icon_audience_large).resize(300, 300).centerCrop().into(mAssignedToImageView);
    }

    private void setAssignedByThumbnail() {
        if (!TextUtils.isEmpty(mAssignment.getAssignedBy().getUserPic())) {
            Picasso.with(getBaseContext()).load(mAssignment.getAssignedBy().getUserPic()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(mAssignedByImageView);
        } else {
            String firstWord = mAssignment.getAssignedBy().getName().substring(0, 1).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
            mAssignedByImageView.setImageDrawable(textDrawable);
        }

        mAssignedByImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    startActivity(UserPublicProfileActivity.getStartIntent(getBaseContext(), mAssignment.getAssignedBy().getId()));
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
                }
            }
        });
    }

    private void setAssignmentStatus() {
        mTextViewDueDate.setAllCaps(true);
        AssignmentStatus assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(mAssignment.getDueDate()).getTime());
        String dueString = TimeUtils.getDueString(DateUtils.convertrIsoDate(mAssignment.getDueDate()));
        mTextViewDueDate.setText(dueString);
        if (assignmentStatus == AssignmentStatus.DUE) {
            mTextViewDueDate.setTextColor(ContextCompat.getColor(this, R.color.colorDueAssignment));
        } else if (assignmentStatus == AssignmentStatus.OVERDUE) {
            mTextViewDueDate.setTextColor(ContextCompat.getColor(this, R.color.colorOverdueAssignment));
        } else {
//            mTextViewDueDate.setTextColor(ContextCompat.getColor(this, R.color.colorNewAssignment));
        }
    }

    private void setAssignmentDuration() {
        if (DateUtils.getTimeStringFromSeconds(mAssignment.getAllowedTimeInSeconds()).contains("0")) {
            mAssignmentDurationLayout.setVisibility(View.GONE);
        } else {
            mDurationTextView.setText(DateUtils.getTimeStringFromSeconds(mAssignment.getAllowedTimeInSeconds()));
        }
    }

    private void setAssignmentInstruction() {
        if (mAssignment.getInstructions() != null && !mAssignment.getInstructions().isEmpty()) {
            TextViewMore.viewMore(mAssignment.getInstructions(), mInstructionTextView, mViewMoreLessTextView);
        } else {
            mInstructionView.setVisibility(View.GONE);
        }
    }

    private void unSubscribeEvent() {
        mSubscription.dispose();
    }

    private void getData() {
        mPendingSummaryTeacherActivityModel.getAllStudentList(mAssignment.getDocId(), mPendingSummaryTeacherActivityModel.getGroups(mAssignment.getAssignedGroups()));
    }

    private void getPendingUserList() {
        if (mMemberArrayList != null)
            mMemberArrayList.clear();
        for (GroupMember groupMember :
                mAssignedGroup.getMembers()) {
            if (!mAssignedGroup.getModerators().contains(new Moderator(groupMember.getObjectId(), "")) && !mAssignedGroup.getCoordinators().contains(new Coordinator(groupMember.getObjectId(), ""))) {
                if (groupMember != null) {
                    mMemberArrayList.add(groupMember);
                }
            }

        }
        if (mAssignmentResponses != null && !mAssignmentResponses.isEmpty()) {
            for (AssignmentResponse assignmentResponse : mAssignmentResponses) {
                mMemberArrayList.remove(new GroupMember(assignmentResponse.getSubmittedBy().getObjectId()));
            }
        }
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object event) {
                        if (event instanceof LoadAssignmentResponseListTeacher)
                            mAssignmentResponses = ((LoadAssignmentResponseListTeacher) event).getAssignmentResponses();

                        new GroupModel().fetchGroupFromUid(mAssignment.getAssignedGroups().get(0).getId()).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Group>() {
                            @Override
                            public void accept(Group group) {
                                mAssignedGroup = group;
                                getPendingUserList();
                                setLayoutForResorcesAndCourses(mAssignment.getAssignmentType());
                                // New view for assignment submited and pending status
                                changeAssignmentMinimalStage(mMemberArrayList.size());
                                if (mAssignment.getAssignmentType().equalsIgnoreCase(AssignmentType.TYPE_OBJECTIVE.getAssignmentType()) ||
                                        mAssignment.getAssignmentType().equalsIgnoreCase(AssignmentType.TYPE_SUBJECTIVE.getAssignmentType())) {
                                    drawGraphForAssignmentStatus(mAssignmentResponses, mMemberArrayList);
                                } else {
                                    drawGraphForResourceAndCourse(mAssignmentResponses, mMemberArrayList);
                                }
                            }
                        });
                    }
                });
    }

    private void drawGraphForResourceAndCourse(List<AssignmentResponse> assignmentResponseList, ArrayList<GroupMember> memberArrayList) {
        ArrayList<GroupMember> viewedByStudentList = new ArrayList<>();
        ArrayList<String> vDocId = new ArrayList<>();
        if (!assignmentResponseList.isEmpty()) {
            for (GroupMember groupMember : mAssignedGroup.getMembers()) {
                for (AssignmentResponse assignmentResponse : assignmentResponseList) {
                    if (groupMember.getObjectId().equalsIgnoreCase(assignmentResponse.getSubmittedBy().getObjectId())) {
                        viewedByStudentList.add(groupMember);
                        vDocId.add(assignmentResponse.getDocId());
                    }
                }
            }
            mSubmittedCountTextView.setText(String.valueOf(assignmentResponseList.size()));
            mPendingCountTextView.setText(String.valueOf(memberArrayList.size()));
            mSubmittedCountTextView.setTextColor(mColor);
            mPendingCountTextView.setTextColor(mColor);
            mAssignmentText4.setVisibility(View.GONE);
            mAssignmentImage4.setVisibility(View.GONE);
            mLayAssignment4.setVisibility(View.GONE);
            mAssignmentPendingRecycleListText.setBackgroundColor(mColor);
            mAssignmentPendingRecycleListText.setText(" Students " + getResources().getString(R.string.studentPending));
            mAssignmentViewedByRecycleListText.setBackgroundColor(mColor);
            mAssignmentViewedByRecycleListText.setText(" Students " + getResources().getString(R.string.studentViewedBy));
            fillRecycleListForAssignmentStaus(memberArrayList, viewedByStudentList, vDocId);
            lay_submitionDetails.setVisibility(View.VISIBLE);
            cardViewGraph.setVisibility(View.VISIBLE);
        } else {
            lay_submitionDetails.setVisibility(View.GONE);
            cardViewGraph.setVisibility(View.GONE);
        }

    }

    private void changeAssignmentMinimalStage(int size) {
        if (size == 0) {
            if (!TextUtils.isEmpty(mAssignmentMinimalDocId)) {
                mAssignmentTeacherModel.changeAssignmentMinimal(mAssignmentMinimalDocId);
                SnackBarUtils.showColoredSnackBar(getBaseContext(), mAssignedToImageView, getString(R.string.move_to_submitted), ContextCompat.getColor(getBaseContext(), R.color.orange_color));
            }
        }
    }

    private void drawGraphForAssignmentStatus(List<AssignmentResponse> assignmentResponseList, ArrayList<GroupMember> memberArrayList) {
        double assignmentScore;
        double totalScore;
        int markScore;
        int[] status = new int[3];
        status[0] = 0;
        status[1] = 0;
        status[2] = 0;
        ArrayList<GroupMember> excellentStudentList = new ArrayList<>();
        ArrayList<GroupMember> averageStudentList = new ArrayList<>();
        ArrayList<GroupMember> poorStudentList = new ArrayList<>();
        ArrayList<String> eDocId = new ArrayList<>();
        ArrayList<String> aDocId = new ArrayList<>();
        ArrayList<String> pDocId = new ArrayList<>();
        if (!assignmentResponseList.isEmpty()) {
            for (GroupMember groupMember : mAssignedGroup.getMembers()) {
                for (AssignmentResponse assignmentResponse : assignmentResponseList) {
                    if (groupMember.getObjectId().equalsIgnoreCase(assignmentResponse.getSubmittedBy().getObjectId())) {
                        assignmentScore = assignmentResponse.getAssignmentScore();
                        totalScore = assignmentResponse.getTotalScore();
                        markScore = (int) ((assignmentScore * 100) / totalScore);
                        if (markScore > 60) {
                            status[0] = status[0] + 1;
                            excellentStudentList.add(groupMember);
                            eDocId.add(assignmentResponse.getDocId());
                        } else if (markScore > 36) {
                            status[1] = status[1] + 1;
                            averageStudentList.add(groupMember);
                            aDocId.add(assignmentResponse.getDocId());
                        } else {
                            status[2] = status[2] + 1;
                            poorStudentList.add(groupMember);
                            pDocId.add(assignmentResponse.getDocId());
                        }
                        break;
                    }
                }
            }
            if (status[0] > 1) {
                mAssignmentText1.setText(status[0] + " Students " + getResources().getString(R.string.labelStrong));
            } else {
                mAssignmentText1.setText(status[0] + " Student " + getResources().getString(R.string.labelStrong));
            }
            if (status[1] > 1) {
                mAssignmentText2.setText(status[1] + " Students " + getResources().getString(R.string.labelCruisingWell));
            } else {
                mAssignmentText2.setText(status[1] + " Student " + getResources().getString(R.string.labelCruisingWell));
            }
            if (status[2] > 1) {
                mAssignmentText3.setText(status[2] + " Students " + getResources().getString(R.string.labelNeedsAttention));
            } else {
                mAssignmentText3.setText(status[2] + " Student " + getResources().getString(R.string.labelNeedsAttention));
            }
            mSubmittedCountTextView.setText(String.valueOf(assignmentResponseList.size()));
            mPendingCountTextView.setText(String.valueOf(memberArrayList.size()));
            mSubmittedCountTextView.setTextColor(mColor);
            mPendingCountTextView.setTextColor(mColor);
            mAssignmentText4.setText(String.valueOf(memberArrayList.size()) + " " + getResources().getString(R.string.labelStillHaveToOpenUp));
            mAssignmentText4.setVisibility(View.GONE);
            mAssignmentImage1.setBackgroundColor(mColor);
            mAssignmentImage2.setBackgroundColor(mColor);
            mAssignmentImage2.setAlpha(.6f);
            mAssignmentImage3.setBackgroundColor(mColor);
            mAssignmentImage3.setAlpha(.3f);
            mAssignmentImage4.setBackgroundColor(color2);
            mAssignmentImage4.setVisibility(View.GONE);
            mLayAssignment4.setVisibility(View.GONE);
            mAssignmentExcellentRecycleListText.setBackgroundColor(mColor);
            mAssignmentExcellentRecycleListText.setText(getResources().getString(R.string.labelStrong));
            mAssignmentAverageRecycleListText.setBackgroundColor(mColor);
            mAssignmentAverageRecycleListText.setText(getResources().getString(R.string.labelCruisingWell));
            mAssignmentPoorRecycleListText.setBackgroundColor(mColor);
            mAssignmentPoorRecycleListText.setText(getResources().getString(R.string.labelNeedsAttention));
            mAssignmentPendingRecycleListText.setBackgroundColor(mColor);
            mAssignmentPendingRecycleListText.setText(getResources().getString(R.string.labelStillHaveToOpenUp));
            mAssignmentViewedByRecycleListText.setBackgroundColor(mColor);
            mAssignmentViewedByRecycleListText.setText(getResources().getString(R.string.studentViewedBy));

            drawCircle(assignmentResponseList.size(), memberArrayList.size(), mColor, status);
            fillRecycleListForAssignmentStaus(excellentStudentList, averageStudentList, poorStudentList, memberArrayList, eDocId, aDocId, pDocId);
            lay_submitionDetails.setVisibility(View.VISIBLE);
            cardViewGraph.setVisibility(View.VISIBLE);
        } else {
            lay_submitionDetails.setVisibility(View.GONE);
            cardViewGraph.setVisibility(View.GONE);
        }
    }

    private void fillRecycleListForAssignmentStaus(ArrayList<GroupMember> excellentStudentList, ArrayList<GroupMember> averageStudentList, ArrayList<GroupMember> poorStudentList, ArrayList<GroupMember> memberArrayList, ArrayList<String> eDocId, ArrayList<String> aDocId, ArrayList<String> pDocId) {
        if (excellentStudentList.size() > 0) {
            mAssignmentExcellentRecycleListText.setVisibility(View.VISIBLE);
            assignmentExcellentRecycleList.setVisibility(View.VISIBLE);
            int columnCount = 1;
            if (excellentStudentList.size() > 45) {
                columnCount = 3;
            } else if (excellentStudentList.size() > 15) {
                columnCount = 2;
            }
            assignmentExcellentRecycleList.setLayoutManager(new GridLayoutManager(this, columnCount, GridLayoutManager.HORIZONTAL, false));
            AssignmentExcellentAdapter adapter = new AssignmentExcellentAdapter(excellentStudentList, eDocId);
            assignmentExcellentRecycleList.setAdapter(adapter);
        } else {
            mAssignmentExcellentRecycleListText.setVisibility(View.GONE);
            assignmentExcellentRecycleList.setVisibility(View.GONE);
        }

        if (averageStudentList.size() > 0) {
            mAssignmentAverageRecycleListText.setVisibility(View.VISIBLE);
            assignmentAverageRecycleList.setVisibility(View.VISIBLE);
            int columnCount = 1;
            if (averageStudentList.size() > 45) {
                columnCount = 3;
            } else if (averageStudentList.size() > 15) {
                columnCount = 2;
            }
            assignmentAverageRecycleList.setLayoutManager(new GridLayoutManager(this, columnCount, GridLayoutManager.HORIZONTAL, false));
            AssignmentExcellentAdapter adapter1 = new AssignmentExcellentAdapter(averageStudentList, aDocId);
            assignmentAverageRecycleList.setAdapter(adapter1);
        } else {
            mAssignmentAverageRecycleListText.setVisibility(View.GONE);
            assignmentAverageRecycleList.setVisibility(View.GONE);
        }

        if (poorStudentList.size() > 0) {
            mAssignmentPoorRecycleListText.setVisibility(View.VISIBLE);
            assignmentPoorRecycleList.setVisibility(View.VISIBLE);
            int columnCount = 1;
            if (poorStudentList.size() > 45) {
                columnCount = 3;
            } else if (poorStudentList.size() > 15) {
                columnCount = 2;
            }
            assignmentPoorRecycleList.setLayoutManager(new GridLayoutManager(this, columnCount, GridLayoutManager.HORIZONTAL, false));
            AssignmentExcellentAdapter adapter2 = new AssignmentExcellentAdapter(poorStudentList, pDocId);
            assignmentPoorRecycleList.setAdapter(adapter2);
        } else {
            mAssignmentPoorRecycleListText.setVisibility(View.GONE);
            assignmentPoorRecycleList.setVisibility(View.GONE);
        }

        if (memberArrayList.size() > 0) {
            mAssignmentPendingRecycleListText.setVisibility(View.VISIBLE);
            assignmentPendingRecycleList.setVisibility(View.VISIBLE);
            int columnCount = 1;
            if (memberArrayList.size() > 45) {
                columnCount = 3;
            } else if (memberArrayList.size() > 15) {
                columnCount = 2;
            }
            assignmentPendingRecycleList.setLayoutManager(new GridLayoutManager(this, columnCount, GridLayoutManager.HORIZONTAL, false));
            AssignmentPendingAdapter adapter3 = new AssignmentPendingAdapter(memberArrayList);
            assignmentPendingRecycleList.setAdapter(adapter3);
        } else {
            mAssignmentPendingRecycleListText.setVisibility(View.GONE);
            assignmentPendingRecycleList.setVisibility(View.GONE);
        }
    }

    private void fillRecycleListForAssignmentStaus(ArrayList<GroupMember> memberArrayList, ArrayList<GroupMember> viewedByStudentList, ArrayList<String> vDocId) {
        mAssignmentExcellentRecycleListText.setVisibility(View.GONE);
        assignmentExcellentRecycleList.setVisibility(View.GONE);
        mAssignmentAverageRecycleListText.setVisibility(View.GONE);
        assignmentAverageRecycleList.setVisibility(View.GONE);
        mAssignmentPoorRecycleListText.setVisibility(View.GONE);
        assignmentPoorRecycleList.setVisibility(View.GONE);
        if (memberArrayList.size() > 0) {
            mAssignmentPendingRecycleListText.setVisibility(View.VISIBLE);
            assignmentPendingRecycleList.setVisibility(View.VISIBLE);
            int columnCount = 1;
            if (memberArrayList.size() > 45) {
                columnCount = 3;
            } else if (memberArrayList.size() > 15) {
                columnCount = 2;
            }
            assignmentPendingRecycleList.setLayoutManager(new GridLayoutManager(this, columnCount, GridLayoutManager.HORIZONTAL, false));
            AssignmentPendingAdapter adapter3 = new AssignmentPendingAdapter(memberArrayList);
            assignmentPendingRecycleList.setAdapter(adapter3);
        } else {
            mAssignmentPendingRecycleListText.setVisibility(View.GONE);
            assignmentPendingRecycleList.setVisibility(View.GONE);
        }
        if (viewedByStudentList.size() > 0) {
            mAssignmentViewedByRecycleListText.setVisibility(View.VISIBLE);
            assignmentViewedByRecycleList.setVisibility(View.VISIBLE);
            int columnCount = 1;
            if (viewedByStudentList.size() > 45) {
                columnCount = 3;
            } else if (viewedByStudentList.size() > 15) {
                columnCount = 2;
            }
            assignmentViewedByRecycleList.setLayoutManager(new GridLayoutManager(this, columnCount, GridLayoutManager.HORIZONTAL, false));
            AssignmentPendingAdapter adapter4 = new AssignmentPendingAdapter(viewedByStudentList);
            assignmentViewedByRecycleList.setAdapter(adapter4);
        } else {
            mAssignmentViewedByRecycleListText.setVisibility(View.GONE);
            assignmentViewedByRecycleList.setVisibility(View.GONE);
        }
    }

    private void setCardSize(CardView studentCard) {
//        int val = getResources().getInteger(R.integer.card_dimension);
//        final float scale = getResources().getDisplayMetrics().density;
//        int pixels = (int) (val * scale + 0.5f);
//        studentCard.getLayoutParams().height=pixels;
//        studentCard.getLayoutParams().width=pixels;
    }

    private void drawCircle(int i, int j, int color1, int[] status) {
        int[] colors = new int[4];
        int red = Color.red(color1);
        int green = Color.green(color1);
        int blue = Color.blue(color1);
        colors[0] = Color.rgb(red, green, blue);
        colors[1] = Color.argb(153, red, green, blue);
        colors[2] = Color.argb(76, red, green, blue);
        colors[3] = Color.rgb(Color.red(color2), Color.green(color2), Color.blue(color2));
        float a = status[0];
        float b = status[1];
        float c = status[2];
        float d = j;
        ArrayList<PieEntry> yvalues = new ArrayList<>();
        yvalues.add(new PieEntry(a, 0));
        yvalues.add(new PieEntry(b, 1));
        yvalues.add(new PieEntry(c, 2));
        yvalues.add(new PieEntry(d, 3));
        IPieDataSet dataSet = new PieDataSet(yvalues, "");
        ((PieDataSet) dataSet).setColors(colors);
        dataSet.setValueTextSize(0f);
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        PieData data = new PieData(dataSet);
        mAssignmentChart.setData(data);
        mAssignmentChart.setHoleRadius(90f);
        mAssignmentChart.setDrawHoleEnabled(true);
        mAssignmentChart.setUsePercentValues(false);
        mAssignmentChart.getDescription().setEnabled(false);
        mAssignmentChart.setDrawCenterText(true);
        mAssignmentChart.setCenterTextColor(colors[0]);
        float progress = i * 100 / (i + j);
        int val = getResources().getInteger(R.integer.learning_map_progress_text_size);
        mAssignmentChart.setCenterTextSize(val);
        mAssignmentChart.setCenterText(Float.valueOf(progress).toString().replaceAll("\\.?0*$", "") + "%");
        Legend l = mAssignmentChart.getLegend();
        l.setEnabled(false);
        mAssignmentChart.invalidate();
        mAssignmentChart.setClickable(false);
        mAssignmentChart.setTouchEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribeEvent();
    }

    private void initializeSubmittedRecyclerView(List<AssignmentResponse> assignmentResponseList) {
//        mSubmittedByRecyclerView.setNestedScrollingEnabled(false);
//        if (assignmentResponseList.size() == 1) {
//            mSubmittedCountTextView.setText(String.valueOf(assignmentResponseList.size()) + " Student");
//        } else if (assignmentResponseList.size() > 1) {
//            mSubmittedCountTextView.setText(String.valueOf(assignmentResponseList.size()) + " Students");
//        }
//        mSubmittedByRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
//        AssignmentResponseAdapter assignmentResponseAdapter = new AssignmentResponseAdapter(assignmentResponseList, StudentSummaryActivity.this);
//        mSubmittedByRecyclerView.setAdapter(assignmentResponseAdapter);
    }

    private void initializePendingRecyclerView(ArrayList<GroupMember> groupMembers) {
//        mPendingByRecyclerView.setNestedScrollingEnabled(false);
//        if (groupMembers.size() == 1) {
//            mPendingCountTextView.setText(String.valueOf(groupMembers.size()) + " Student");
//        } else if (groupMembers.size() > 1) {
//            mPendingCountTextView.setText(String.valueOf(groupMembers.size()) + " Students");
//        }
//        mPendingByRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
//        PendingStudentAdapter pendingStudentAdapter = new PendingStudentAdapter(groupMembers, StudentSummaryActivity.this);
//        mPendingByRecyclerView.setAdapter(pendingStudentAdapter);
    }

    private void setUserThumbnail(GroupMember groupMember, ImageView userThumbnailImageView) {
        if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getLocalUrl())) {
            Picasso.with(getBaseContext()).load(groupMember.getPic().getLocalUrl()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(userThumbnailImageView);
        } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getUrl())) {
            Picasso.with(getBaseContext()).load(groupMember.getPic().getUrl()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(userThumbnailImageView);
        } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getThumb())) {
            Picasso.with(getBaseContext()).load(groupMember.getPic().getThumb()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(userThumbnailImageView);
        } else {
            String firstWord = groupMember.getName().substring(0, 1).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder().buildRoundRect(firstWord, R.color.colorAssignmentPrimary, 10);
            userThumbnailImageView.setImageDrawable(textDrawable);
        }
    }

    /**
     * calculating scoring for each student.
     *
     * @param assignmentResponse
     */
    private String getScoring(AssignmentResponse assignmentResponse) {
        ArrayList<Double> mTotal = new ArrayList<>();
        double maxTotalMarks = assignmentResponse.getTotalScore();
        String strTotalMarks = String.valueOf(assignmentResponse.getTotalScore()).replace(".", "");
        int numberOfQuestions = assignmentResponse.getQuizResponses().size();
        double maxMarkQuestion = maxTotalMarks / numberOfQuestions;
        String strMaxMarkQuestion = String.format("%.2f", maxMarkQuestion);

        for (int i = 0; i < numberOfQuestions; i++) {
            int status = assignmentResponse.getQuizResponses().get(i).getAttempts().get(0).getStatusCode();
            String strMarksPerQuestion = "";
            if (status == 0) {
                strMarksPerQuestion = strMaxMarkQuestion;
            } else if (status == 1
            ) {
                strMarksPerQuestion = "0";
            }
            mTotal.add(Double.parseDouble(strMarksPerQuestion));
        }

        double sumOfMarksGain = 0;
        for (int i = 0; i < mTotal.size(); i++) {
            sumOfMarksGain += mTotal.get(i);
        }
        String strFinalMarks = String.valueOf(sumOfMarksGain) + "/" + strTotalMarks;
        return strFinalMarks;
    }

    /**
     * calculating time taken by student to attempt the quiz.
     *
     * @param assignmentResponse
     * @return
     */
    private long calculateTimeTaken(AssignmentResponse assignmentResponse) {
        long totalTimeTaken = 0;
        for (QuestionResponse quizResponse : assignmentResponse.getQuizResponses()) {
            for (Attempt attempt : quizResponse.getAttempts()) {
                totalTimeTaken = totalTimeTaken + attempt.getTimeTaken();
            }
        }
        return totalTimeTaken;
    }

    private class AssignmentExcellentAdapter extends RecyclerView.Adapter<AssignmentExcellentAdapter.MyViewHolder> {
        ArrayList<String> docId = new ArrayList<>();
        private List<GroupMember> groupMembers = new ArrayList<>();

        public AssignmentExcellentAdapter(ArrayList<GroupMember> members, ArrayList<String> eDocId) {
            groupMembers = members;
            docId = eDocId;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            StudentImageListRowBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.student_image_list_row, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final GroupMember groupMember = groupMembers.get(position);
            setUserThumbnail(groupMember, holder.mBinding.student);
//            setCardSize(holder.mBinding.studentCard);
            holder.mBinding.studentName.setText(groupMember.getName());
            for (AssignmentResponse assignmentResponse : mAssignmentResponses) {
                if (groupMember.getObjectId().equalsIgnoreCase(assignmentResponse.getSubmittedBy().getObjectId())) {
                    holder.mBinding.studentScore.setText(String.format("%.2f", assignmentResponse.getAssignmentScore()));
                    break;
                }
            }
            holder.mBinding.student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //    QuizPreviewActivity.startQuizPreview(StudentSummaryActivity.this, docId.get(position), "", false);
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupMembers.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            StudentImageListRowBinding mBinding;

            public MyViewHolder(StudentImageListRowBinding view) {
                super(view.getRoot());
                mBinding = view;
            }
        }
    }

    private class AssignmentPendingAdapter extends RecyclerView.Adapter<AssignmentPendingAdapter.MyViewHolder> {
        private List<GroupMember> groupMembers = new ArrayList<>();


        public AssignmentPendingAdapter(ArrayList<GroupMember> members) {
            groupMembers = members;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            StudentImageListRowBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.student_image_list_row, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            GroupMember groupMember = groupMembers.get(position);
            setUserThumbnail(groupMember, holder.mBinding.student);
//            setCardSize(holder.mBinding.studentCard);
            holder.mBinding.studentName.setText(groupMember.getName());
            holder.mBinding.layoutStudentScore.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return groupMembers.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            StudentImageListRowBinding mBinding;

            public MyViewHolder(StudentImageListRowBinding view) {
                super(view.getRoot());
                mBinding = view;
            }
        }
    }

    class AssignmentResponseAdapter extends RecyclerView.Adapter<AssignmentResponseAdapter.ViewHolder> {

        @Inject
        public Context mContext;
        private List<AssignmentResponse> assignmentResponses = new ArrayList<>();

        AssignmentResponseAdapter(List<AssignmentResponse> list, Context context) {
            this.assignmentResponses = list;
            this.mContext = context;
        }

        @Override
        public AssignmentResponseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assignment_submittedby_itemview, parent, false);
            return new StudentSummaryActivity.AssignmentResponseAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            AssignmentResponse assignmentResponse = assignmentResponses.get(position);

            holder.mSubmittedByTextView.setText(assignmentResponse.getSubmittedBy().getName());
            holder.mSubmittedOnTextView.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentResponse.getSubmissionDateTime())));
            holder.mTimeTakenTextView.setText("" + DateUtils.getTimeStringFromSeconds(calculateTimeTaken(assignmentResponse)));
            holder.mScoringTextView.setText(AssignmentDetailActivity.getScore(assignmentResponse.getAssignmentScore(), assignmentResponse.getTotalScore()));

            if (assignmentResponse.getSubmittedBy() != null && assignmentResponse.getSubmittedBy().getUserPic() != null) {
                String url = assignmentResponse.getSubmittedBy().getUserPic();
                File mImageFile = new File(FileUtils.getPathFromFilePath(url));
                Picasso.with(mContext).load(mImageFile).resize(300, 300).placeholder(R.drawable.icon_profile_large).centerInside().into(holder.mUserThumbnail);

            } else {

            }

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean isAttempt = true;
                    if (assignmentResponses.get(position).getStage().equals(AssignmentStage.STAGE_SUBMITTED.getAssignmentStage()) || assignmentResponses.get(position).getStage().equals(AssignmentStage.STAGE_GRADED.getAssignmentStage())) {
                        isAttempt = false;
                    }
                    //QuizPreviewActivity.startQuizPreview(mContext, assignmentResponses.get(position).getDocId(), "", isAttempt);
                }
            });
        }

        @Override
        public int getItemCount() {
            return assignmentResponses.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private View mItemView;
            private TextView mSubmittedOnTextView, mSubmittedByTextView, mScoringTextView, mTimeTakenTextView;
            private ImageView mUserThumbnail;

            public ViewHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                mUserThumbnail = (ImageView) mItemView.findViewById(R.id.imageview_user_thumbnail);
                mSubmittedOnTextView = (TextView) mItemView.findViewById(R.id.textview_submitted_on);
                mSubmittedByTextView = (TextView) mItemView.findViewById(R.id.textview_submitted_by);
                mScoringTextView = (TextView) mItemView.findViewById(R.id.textview_scoring);
                mTimeTakenTextView = (TextView) mItemView.findViewById(R.id.textview_time_taken);
            }
        }
    }

    class PendingStudentAdapter extends RecyclerView.Adapter<PendingStudentAdapter.ViewHolder> {

        @Inject
        public Context mContext;
        private List<GroupMember> groupMembers = new ArrayList<>();

        PendingStudentAdapter(ArrayList<GroupMember> groupMembers, Context context) {
            this.groupMembers = groupMembers;
            this.mContext = context;
        }

        @Override
        public PendingStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assignment_pendingby_itemview, parent, false);
            return new PendingStudentAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PendingStudentAdapter.ViewHolder holder, int position) {
            GroupMember groupMember = groupMembers.get(position);

            holder.mSubmittedByTextView.setText(groupMember.getName());
            setUserThumbnail(groupMember, holder.mUserThumbnail);

        }

        @Override
        public int getItemCount() {
            return groupMembers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View mItemView;
            private TextView mSubmittedByTextView;
            private ImageView mUserThumbnail;

            public ViewHolder(View view) {
                super(view);
                mItemView = view;
                mUserThumbnail = (ImageView) mItemView.findViewById(R.id.imageview_user_thumbnail);
                mSubmittedByTextView = (TextView) mItemView.findViewById(R.id.textview_submitted_by);
            }
        }
    }

}
