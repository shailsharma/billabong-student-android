package in.securelearning.lil.android.assignments.views.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.constants.AssignmentStatus;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AssignedGroup;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.events.GenerateSubmissionEvent;
import in.securelearning.lil.android.base.events.QuizCompletedEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.base.views.activity.WebPlayerLiveActivity;
import in.securelearning.lil.android.home.dataobjects.TimeUtils;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.events.LoadRefreshAssignmentStageEvent;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.quizpreview.events.AssignmentSubmittedEvent;
import in.securelearning.lil.android.resources.view.activity.VideoPlayActivity;
import in.securelearning.lil.android.resources.view.activity.VimeoActivity;
import in.securelearning.lil.android.resources.view.activity.YoutubePlayActivity;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AssignmentDetailActivity extends AppCompatActivity {

    public static final String ASSIGNMENT_STUDENT_DOC_ID = "docIdAssignmentStudent";
    public static final String ASSIGNMENT_RESPONSE_OBJECT_ID = "objectIdAssignmentResponse";
    private FloatingActionButton mAttemptButton;
    private AssignmentResponse mAssignmentResponse;
    private Assignment mAssignment;
    private ImageView mAssignmentThumbnail;
    private TextView mSkillTextView, mGroupsTextView, mInstructionTextView, mViewMoreLessTextView;
    private LinearLayout mContentLayout, mDurationLayout;
    private CardView mAssignmentInstructionLayout, mCardScore;
    private Class mObjectClass = null;
    private String mAssignmentStudentDocId = "";
    private String mAssignmentResponseObjectId = "";
    private ImageView mAssignedByImageView, mAssignedToImageView;
    private boolean mGenerateSubmissionEvent = false;
    private Toolbar mToolbar;
    private Disposable mSubscription;
    private Menu menu;
    @Inject
    AppUserModel mAppUserModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AssignmentResponseModel mAssignmentResponseModel;

    @Inject
    AssignmentResponseStudentModel mAssignmentResponseStudentModel;

    @Inject
    AssignmentModel mAssignmentModel;

    private boolean isTypeQuiz = false;
    private boolean isTypeCourse = false;
    private boolean isTypeResource = false;

    public static Intent startAssignmentDetailActivity(Context context, String assignmentResponseObjectId, String assignmentStudentDocId) {
        Intent intent = new Intent(context, AssignmentDetailActivity.class);
        intent.putExtra(ASSIGNMENT_RESPONSE_OBJECT_ID, assignmentResponseObjectId);
        intent.putExtra(ASSIGNMENT_STUDENT_DOC_ID, assignmentStudentDocId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.dispose();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_play) {
            if (mAssignmentResponse != null) {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    if (isTypeQuiz) {
                        boolean isAttempt = true;
                        if (mAssignmentResponse.getStage().equals(AssignmentStage.STAGE_SUBMITTED.getAssignmentStage()) || mAssignmentResponse.getStage().equals(AssignmentStage.STAGE_GRADED.getAssignmentStage())) {
                            isAttempt = false;
                        }
                        WebPlayerLiveActivity.startWebPlayer(getBaseContext(), mAssignment.getUidQuiz(), "", "", Quiz.class, "", false, false);
                    } else if (isTypeCourse) {
                        if (mObjectClass.equals(MicroLearningCourse.class)) {
                            startActivity(RapidLearningSectionListActivity.getStartIntent(getBaseContext(), mAssignment.getUidCourse()));
                            mRxBus.send(new GenerateSubmissionEvent(mAssignmentResponse.getObjectId()));
                        } else {
                            WebPlayerCordovaLiveActivity.startWebPlayer(getBaseContext(), mAssignment.getUidCourse(), "", "", mObjectClass, mAssignmentResponseObjectId, false);

                        }
                    }
                } else {
                    SnackBarUtils.showSnackBar(getBaseContext(), mContentLayout, getString(R.string.connect_internet));
                }
//                else{
//                    startResourceActivity();
//                }
            }
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectorAssignment.INSTANCE.getComponent().inject(this);
        setContentView(R.layout.layout_assignment_detail);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorAssignmentPrimary));
        initializeViews();
        listenRxBusEvents();
        initializeUIAndClickListeners();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mAssignmentStudentDocId = bundle.getString(ASSIGNMENT_STUDENT_DOC_ID);
            mAssignmentResponseObjectId = bundle.getString(ASSIGNMENT_RESPONSE_OBJECT_ID);
            mAssignmentResponse = mAssignmentResponseModel.getAssignmentResponseFromUidSync(mAssignmentResponseObjectId);
        }

        if (mAssignmentResponse != null && !TextUtils.isEmpty(mAssignmentResponse.getObjectId()) && mAssignmentResponse.getObjectId().equals(mAssignmentResponseObjectId)) {

            mAssignment = mAssignmentModel.getAssignmentFromUidSync(mAssignmentResponse.getAssignmentID());
            int color = PrefManager.getColorForSubject(this, mAssignmentResponse.getMetaInformation().getSubject().getId());
            setColor(R.id.textview_subject, color);
            setColor(R.id.textview_due_date, color);
            setColor(R.id.textview_assignment_date, color);
            findViewById(R.id.textview_type).setBackgroundColor(color);
            initializeViewsWithValues(mAssignmentResponse, mAssignment);
            if (mAssignmentResponse.getStage().equalsIgnoreCase(AssignmentStage.STAGE_ASSIGNED.getAssignmentStage()) || mAssignmentResponse.getStage().equalsIgnoreCase(AssignmentStage.STAGE_WIP.getAssignmentStage())) {
                mGenerateSubmissionEvent = true;
            } else {
                mGenerateSubmissionEvent = false;
            }
            if (mAssignmentResponse.getStage().equalsIgnoreCase(AssignmentStage.STAGE_ASSIGNED.getAssignmentStage())) {
                mAssignmentResponse.setStage(AssignmentStage.STAGE_WIP.getAssignmentStage());
                mAssignmentResponseModel.saveAssignmentResponse(mAssignmentResponse);
                mRxBus.send(new LoadRefreshAssignmentStageEvent(mAssignmentResponse));
            }
            purgeNewAssignmentStudentStatus(mAssignmentResponse.getAssignmentID());
        } else {
            finish();
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(mAssignmentResponse.getAssignmentTitle());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mAssignmentResponseObjectId)) {
            mAssignmentResponse = mAssignmentResponseModel.getAssignmentResponseFromUidSync(mAssignmentResponseObjectId);
            if (mAssignmentResponse != null && !TextUtils.isEmpty(mAssignmentResponse.getObjectId()) && mAssignmentResponse.getObjectId().equals(mAssignmentResponseObjectId)) {
                mAssignment = mAssignmentModel.getAssignmentFromUidSync(mAssignmentResponse.getAssignmentID());
                initializeViewsWithValues(mAssignmentResponse, mAssignment);
            }
        }
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof GenerateSubmissionEvent) {
                    if (mGenerateSubmissionEvent) {
                        if (((GenerateSubmissionEvent) event).getAssignmentResponseId().equals(mAssignmentResponseObjectId)) {
                            submitResponse();
                            mGenerateSubmissionEvent = false;
                        }
                    }
                } else if (event instanceof QuizCompletedEvent) {
                    if (mGenerateSubmissionEvent) {
                        submitResponse();
                        mGenerateSubmissionEvent = false;
                    }
                }
            }
        });
    }

    private void submitResponse() {
        if (mAssignmentResponse != null) {
            mAssignmentResponse.setAssignmentScore(0);
            mAssignmentResponse.setStage(AssignmentStage.STAGE_GRADED.getAssignmentStage());
            mAssignmentResponse.setSubmissionDateTime(DateUtils.getISO8601DateStringFromDate(new Date()));
            if (mAssignmentResponse.getSubmittedBy() == null) {
                mAssignmentResponse.setSubmittedBy(mAssignmentResponseStudentModel.getSubmittedBy());
            }
            if (TextUtils.isEmpty(mAssignmentResponse.getSubmittedBy().getObjectId())) {
                mAssignmentResponse.setSubmittedBy(mAssignmentResponseStudentModel.getSubmittedBy());
            }
            mAssignmentResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
            mAssignmentResponseStudentModel.saveAssignmentResponse(mAssignmentResponse);
            mAssignmentResponseStudentModel.changeAssignmentStudent(mAssignmentResponse, mAssignmentStudentDocId);

            mRxBus.send(new AssignmentSubmittedEvent(mAssignmentResponse));
            SyncService.startActionUploadAssignmentResponse(getBaseContext(), mAssignmentResponse.getObjectId());
        }
    }


    private void purgeNewAssignmentStudentStatus(final String assignmentID) {
        Completable.complete().observeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                mAssignmentResponseStudentModel.deleteNewAssignmentStudentStatus(assignmentID);
            }
        });
    }

    private void initializeViews() {
        mAttemptButton = (FloatingActionButton) findViewById(R.id.fab);
        mAssignmentThumbnail = (ImageView) findViewById(R.id.imageview_toolbar_assignment);
        mSkillTextView = (TextView) findViewById(R.id.textview_skills);
        mGroupsTextView = (TextView) findViewById(R.id.textview_groups);
        mInstructionTextView = (TextView) findViewById(R.id.textview_instruction);
        mViewMoreLessTextView = (TextView) findViewById(R.id.textViewMoreLess);
        mContentLayout = (LinearLayout) findViewById(R.id.layout_content);
        mDurationLayout = (LinearLayout) findViewById(R.id.layoutAssignmentDuration);
        mAssignmentInstructionLayout = (CardView) findViewById(R.id.layoutAssignmentInstruction);
        mCardScore = (CardView) findViewById(R.id.cardScore);
        mAssignedByImageView = (ImageView) findViewById(R.id.imageViewAssignedBy);
        mAssignedToImageView = (ImageView) findViewById(R.id.imageViewAssignedTo);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * find ids for views
     *
     * @param assignmentResponse
     * @param assignment
     */
    private void initializeViewsWithValues(final AssignmentResponse assignmentResponse, final Assignment assignment) {

//        setValuesToTextView(R.id.textview_assignment_title, assignmentResponse.getAssignmentTitle());
        setValuesToTextView(R.id.textview_assignment_date, DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(assignmentResponse.getAssignedDateTime())));
        setValuesToTextView(R.id.textview_type, getAssignmentType(assignmentResponse.getAssignmentType()));
        setValuesToTextView(R.id.textview_subject, assignmentResponse.getMetaInformation().getSubject().getName());
        setValuesToTextView(R.id.textview_topic, assignmentResponse.getMetaInformation().getTopic().getName());
        setValuesToTextView(R.id.textview_assigned_by, assignmentResponse.getAssignedBy().getName());
        setValuesToTextView(R.id.textview_due_date, DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate())));
        mGroupsTextView.setText(assignmentResponse.getAssignedGroup().get(0).getName());

        setAssignmentThumbnail(assignmentResponse);
        setAssignmentStatus(assignmentResponse);
        setAssignmentDuration(assignmentResponse);
        setAssignmentSkill(assignmentResponse);
        setAssignmentInstruction(assignmentResponse);
        setAssignedByThumbnail(assignmentResponse);
        setAssignedToThumbnail(assignmentResponse);
        mContentLayout.setVisibility(View.VISIBLE);

//        mAttemptButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mAssignmentResponse != null) {
//                    if (isTypeQuiz) {
//                        boolean isAttempt = true;
//                        if (mAssignmentResponse.getStage().equals(AssignmentStage.STAGE_SUBMITTED.getAssignmentStage()) || mAssignmentResponse.getStage().equals(AssignmentStage.STAGE_GRADED.getAssignmentStage())) {
//                            isAttempt = false;
//                        }
//                        QuizPreviewActivity.startQuizPreview(AssignmentDetailActivity.this, assignmentResponse.getDocId(), mAssignmentStudentDocId, isAttempt);
//                    } else if (isTypeCourse) {
//                        if (mGenerateSubmissionEvent) {
//                            startActivity(CourseDetailActivity.getStartActivityIntent(getBaseContext(), assignment.getUidCourse(), mObjectClass, mAssignmentResponseObjectId));
//                        } else {
//                            startActivity(CourseDetailActivity.getStartActivityIntent(getBaseContext(), assignment.getUidCourse(), mObjectClass, ""));
//                        }
//
//                    } else {
//                        startResourceActivity();
//                    }
//                }
//
//            }
//        });

//        mBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

    }

    private void setAssignedToThumbnail(AssignmentResponse assignmentResponse) {
        // TODO: 17-Jul-17 load group thumbnail whenever it comes with assignment response
        Picasso.with(getBaseContext()).load(R.drawable.icon_audience_large).resize(300, 300).centerCrop().into(mAssignedToImageView);

    }

    private void setAssignedByThumbnail(final AssignmentResponse assignmentResponse) {
        if (!TextUtils.isEmpty(assignmentResponse.getAssignedBy().getUserPic())) {
            Picasso.with(getBaseContext()).load(assignmentResponse.getAssignedBy().getUserPic()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(mAssignedByImageView);
        } else {
            String firstWord = assignmentResponse.getAssignedBy().getName().substring(0, 1).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
            mAssignedByImageView.setImageDrawable(textDrawable);
        }

        mAssignedByImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    startActivity(UserProfileActivity.getStartIntent(assignmentResponse.getAssignedBy().getId(), AssignmentDetailActivity.this));
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
                }
            }
        });

    }

    private void setColor(int resId, int color) {
        ((TextView) findViewById(resId)).setTextColor(color);
    }

    private void startResourceActivity() {
        FavouriteResource favouriteResource = new FavouriteResource();
        favouriteResource.setObjectId(mAssignmentResponse.getObjectId());
        favouriteResource.setTitle(mAssignmentResponse.getAssignmentTitle());
        MetaInformation metaInformation = mAssignmentResponse.getMetaInformation();
        favouriteResource.setMetaInformation(metaInformation);
        favouriteResource.setUrlThumbnail(mAssignmentResponse.getThumbnail().getUrl());
        favouriteResource.setName(mAssignment.getUidResource());

        if (!TextUtils.isEmpty(mAssignmentResponse.getResourceType())) {
            String type = mAssignmentResponse.getResourceType();
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                if (type.equalsIgnoreCase("video")) {
                    if (mGenerateSubmissionEvent) {
                        startActivity(VideoPlayActivity.getStartIntent(getBaseContext(), favouriteResource, mAssignmentResponseObjectId));
                    } else {
                        startActivity(VideoPlayActivity.getStartIntent(getBaseContext(), favouriteResource));

                    }
                } else if (type.equalsIgnoreCase("youtube#video")) {
                    if (mGenerateSubmissionEvent) {
                        startActivity(YoutubePlayActivity.getStartIntent(AssignmentDetailActivity.this, favouriteResource, mAssignmentResponseObjectId));
                    } else {
                        startActivity(YoutubePlayActivity.getStartIntent(AssignmentDetailActivity.this, favouriteResource, ""));
                    }
                } else if (type.equalsIgnoreCase("vimeo")) {
                    if (mGenerateSubmissionEvent) {
                        startActivity(VimeoActivity.getStartIntent(getBaseContext(), favouriteResource, mAssignmentResponseObjectId));
                    } else {
                        startActivity(VimeoActivity.getStartIntent(getBaseContext(), favouriteResource));

                    }
                }
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mToolbar);
            }
        }


    }

    private void setAssignmentDuration(AssignmentResponse assignmentResponse) {
        if (!DateUtils.getTimeStringFromSeconds(assignmentResponse.getAllowed_TimeInSeconds()).contains("0")) {
            setValuesToTextView(R.id.textview_duration, DateUtils.getTimeStringFromSeconds(assignmentResponse.getAllowed_TimeInSeconds()));
        } else {
            mDurationLayout.setVisibility(View.GONE);
        }
        setValuesToTextView(R.id.textview_duration, DateUtils.getTimeStringFromSeconds(assignmentResponse.getAllowed_TimeInSeconds()));
    }

    private void setAssignmentSkill(final AssignmentResponse assignmentResponse) {
        mSkillTextView.setText(assignmentResponse.getSkills() != null && !assignmentResponse.getSkills().isEmpty() ? assignmentResponse.getSkills().get(0) : "");
        if (assignmentResponse.getSkills() != null && assignmentResponse.getSkills().size() > 0) {
            mSkillTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    skillPopup(assignmentResponse.getSkills(), view);
                }
            });
        }
    }

    private void setAssignmentStatus(AssignmentResponse assignmentResponse) {
        if (assignmentResponse.getStage().equalsIgnoreCase(AssignmentStage.STAGE_SUBMITTED.getAssignmentStage()) || assignmentResponse.getStage().equalsIgnoreCase(AssignmentStage.STAGE_GRADED.getAssignmentStage())) {
            setVisibility(R.id.layout_submitted_on, View.VISIBLE);
            int color = PrefManager.getColorForSubject(this, mAssignment.getMetaInformation().getSubject().getId());
            setColor(R.id.textview_submitted_on, color);
            setColor(R.id.textview_time_taken, color);
            setColor(R.id.textview_score, color);
            setValuesToTextView(R.id.textview_submitted_on, DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(assignmentResponse.getSubmissionDateTime())));
            setValuesToTextView(R.id.textview_time_taken, DateUtils.getTimeStringFromSeconds(calculateTimeTaken(assignmentResponse)));
            setValuesToTextView(R.id.textview_score, getScore(assignmentResponse.getAssignmentScore(), assignmentResponse.getTotalScore()));
        } else {
            setVisibility(R.id.layout_submitted_on, View.GONE);
//            setVisibility(R.id.layout_timetaken, View.GONE);
//            setVisibility(R.id.layout_score, View.GONE);

            AssignmentStatus assignmentStatus = DateUtils.checkAssignmentDueStatus(DateUtils.convertrIsoDate(assignmentResponse.getAssignmentDueDate()).getTime());
            String statusStr = TimeUtils.getDueString(DateUtils.convertrIsoDate(mAssignment.getDueDate()));

            TextView view = ((TextView) findViewById(R.id.textViewDueOn));
            if (view != null) {
                view.setAllCaps(true);
                view.setText(statusStr);
                if (assignmentStatus == AssignmentStatus.DUE) {
                    setColor(R.id.textViewDueOn, ContextCompat.getColor(this, R.color.colorDueAssignment));
                } else if (assignmentStatus == AssignmentStatus.OVERDUE) {
                    setColor(R.id.textViewDueOn, ContextCompat.getColor(this, R.color.colorOverdueAssignment));
                } else {
//                setColor(R.id.textViewDueOn, ContextCompat.getColor(this,R.color.colorAssignmentPrimary));
                }

            }

            setValuesToTextView(R.id.textview_status, getCapitalNameString(statusStr));
        }
    }

    private void setAssignmentThumbnail(AssignmentResponse assignmentResponse) {
        if (assignmentResponse.getThumbnail() != null) {
            String thumbnailPath = assignmentResponse.getThumbnail().getLocalUrl();
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = assignmentResponse.getThumbnail().getUrl();
            }
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = assignmentResponse.getThumbnail().getThumb();
            }
            try {
                if (!TextUtils.isEmpty(thumbnailPath)) {
                    Picasso.with(getBaseContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).into(mAssignmentThumbnail);
                } else {
                    Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mAssignmentThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getBaseContext()).load(assignmentResponse.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(mAssignmentThumbnail);
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
        }

    }

    private void setAssignmentInstruction(AssignmentResponse assignmentResponse) {
        if (assignmentResponse.getInstruction() != null && !assignmentResponse.getInstruction().isEmpty()) {
            TextViewMore.viewMore(assignmentResponse.getInstruction(), mInstructionTextView, mViewMoreLessTextView);
        } else {
            mAssignmentInstructionLayout.setVisibility(View.GONE);
        }
    }

    public static String getScore(double assignmentScore, double assignmentTotalScore) {
        return String.valueOf(new DecimalFormat("##.##").format(assignmentScore)) + " / " + String.valueOf(new DecimalFormat("##.#").format(assignmentTotalScore));
    }

    public String getAssignmentType(String assignmentType) {
        if (assignmentType.equalsIgnoreCase("quiz")) {
            isTypeQuiz = true;
            isTypeCourse = false;
            isTypeResource = false;
            String type = "";
            type = "Quiz";
            mCardScore.setVisibility(View.GONE);
            return type;

        } else if (assignmentType.equalsIgnoreCase(AssignmentType.TYPE_RESOURCE.getAssignmentType())) {
            String type = getString(R.string.resource);
            isTypeQuiz = false;
            isTypeCourse = false;
            isTypeResource = true;
            mCardScore.setVisibility(View.GONE);
            return type;
        } else {
            isTypeQuiz = false;
            isTypeCourse = true;
            isTypeResource = false;
            String type = "";
            if (assignmentType.equalsIgnoreCase("digitalbook")) {
                type = "Digital Book";
                mObjectClass = DigitalBook.class;
            } else if (assignmentType.equalsIgnoreCase("videocourse")) {
                type = "Video Course";
                mObjectClass = VideoCourse.class;
            } else if (assignmentType.contains("feature")) {
                type = "Recap";
                mObjectClass = MicroLearningCourse.class;
            } else if (assignmentType.contains("map")) {
                type = "Concept Map";
                mObjectClass = ConceptMap.class;
            } else if (assignmentType.contains("interactiveim")) {
                type = "Interactive Image";
                mObjectClass = InteractiveImage.class;
            } else if (assignmentType.contains("interactivevi")) {
                type = "Interactive Video";
                mObjectClass = InteractiveVideo.class;
            } else {
                if (assignmentType.contains("pop")) {
                    type = "Pop Up";
                    mObjectClass = PopUps.class;
                }
            }

            mCardScore.setVisibility(View.GONE);
            return type;
        }
    }

    private ArrayList<String> getStringList(ArrayList<AssignedGroup> assignedGroup) {
        ArrayList<String> list = new ArrayList<>();
        for (AssignedGroup group : assignedGroup) {
            list.add(group.getName());
        }
        return list;
    }

    /**
     * show skills of assignment in popup list.
     *
     * @param adapterdata
     * @param view
     */
    private void skillPopup(ArrayList<String> adapterdata, View view) {


        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_simple_list, null);
        ListView mEditTextDataListView = (ListView) layout.findViewById(R.id.listview_simple);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(AssignmentDetailActivity.this, R.layout.layout_simple_list_item, R.id.textview_list_item, adapterdata);
        mEditTextDataListView.setAdapter(arrayAdapter);

        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = layout.getMeasuredWidth();

        PopupWindow popupSkills = new PopupWindow(AssignmentDetailActivity.this);
        popupSkills.setContentView(layout);
        popupSkills.setWidth(popupWidth);
        popupSkills.setHeight(popupHeight);
        popupSkills.setFocusable(true);

        popupSkills.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupSkills.setOutsideTouchable(true);
        popupSkills.setAnimationStyle(android.R.style.Animation_Dialog);
        popupSkills.showAsDropDown(view, -10, -40, Gravity.NO_GRAVITY);

    }

    private void setValuesToTextView(int resId, String values) {
        ((TextView) findViewById(resId)).setText(values.toString());
    }

    private void setVisibility(int resId, int isVisible) {
        View view = findViewById(resId);
        view.setVisibility(isVisible);
    }

    private void initializeUIAndClickListeners() {


    }

    /**
     * calculating time taken by student to attempt the quiz.
     *
     * @param assignmentResponse
     * @return
     */
    public static long calculateTimeTaken(AssignmentResponse assignmentResponse) {
        long totalTimeTaken = 0;
        for (QuestionResponse quizResponse : assignmentResponse.getQuizResponses()) {
            for (Attempt attempt : quizResponse.getAttempts()) {
                totalTimeTaken = totalTimeTaken + attempt.getTimeTaken();
            }
        }

        return totalTimeTaken;
    }

    public static String getCapitalNameString(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
