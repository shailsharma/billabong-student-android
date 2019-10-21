package in.securelearning.lil.android.homework.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHomeworkDetailBinding;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.dataobjects.AssignedGroup;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.events.QuizCompletedEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.dataobject.HomeworkSubmitResponse;
import in.securelearning.lil.android.homework.event.RefreshHomeworkEvent;
import in.securelearning.lil.android.homework.model.HomeworkModel;
import in.securelearning.lil.android.player.view.activity.QuizPlayerActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.profile.views.activity.UserPublicProfileActivity;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeworkDetailActivity extends AppCompatActivity {

    @Inject
    public AppUserModel mAppUserModel;
    @Inject
    HomeworkModel mHomeworkModel;
    @Inject
    RxBus mRxBus;
    private LayoutHomeworkDetailBinding mBinding;
    private boolean isTypeQuiz = false;
    private boolean isTypeCourse = false;
    private boolean isTypeResource = false;
    private Class mObjectClass = null;
    private Homework mHomework;
    private Disposable mSubscription;
    private MenuItem mPlayMenuItem;

    public static Intent getStartIntent(Context context, String homeworkId, String homeworkTitle) {
        Intent intent = new Intent(context, HomeworkDetailActivity.class);
        intent.putExtra(ConstantUtil.HOMEWORK_ID, homeworkId);
        intent.putExtra(ConstantUtil.TITLE, homeworkTitle);
        return intent;
    }

    public static String getScore(double assignmentScore, double assignmentTotalScore) {
        return new DecimalFormat("##.##").format(assignmentScore) + " / " + new DecimalFormat("##.#").format(assignmentTotalScore);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_homework_detail);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorCenterGradient));

        handleIntent();
        listenRxBusEvents();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        mPlayMenuItem = menu.findItem(R.id.action_play);
        playMenuItemVisibility(false);
        return true;
    }

    private void playMenuItemVisibility(boolean b) {
        if (mPlayMenuItem != null) {
            mPlayMenuItem.setVisible(b);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_play) {
            if (mHomework != null && !TextUtils.isEmpty(mHomework.getAttachmentId()) && mObjectClass != null) {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    if (isTypeQuiz) {
                        if (!mHomework.isSubmitted()) {
                            startActivity(QuizPlayerActivity.getStartIntent(getBaseContext(), mHomework.getAttachmentId(), mHomework.getHomeworkId(), getString(R.string.assignment).toLowerCase()));
                        }

                    } else if (isTypeCourse) {
                        if (mObjectClass != null && mObjectClass.equals(MicroLearningCourse.class)) {
                            startActivity(RapidLearningSectionListActivity.getStartIntent(getBaseContext(), mHomework.getAttachmentId()));
                        } else {
                            WebPlayerCordovaLiveActivity.startWebPlayer(getBaseContext(), mHomework.getAttachmentId(), ConstantUtil.BLANK, ConstantUtil.BLANK, mObjectClass, ConstantUtil.BLANK, false);

                        }
                        submitResponse(mHomework.getHomeworkId());

                    }
                } else {
                    SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
                }

            } else {
                SnackBarUtils.showAlertSnackBar(getBaseContext(), mBinding.getRoot(), getString(R.string.error_something_went_wrong));

            }
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String homeworkId = intent.getStringExtra(ConstantUtil.HOMEWORK_ID);
            String homeworkTitle = intent.getStringExtra(ConstantUtil.TITLE);
            if (!TextUtils.isEmpty(homeworkId)) {
                setUpToolbar(homeworkTitle);
                fetchHomeworkDetail(homeworkId);
            }
        }
    }

    private void setUpToolbar(String homeworkTitle) {
        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(homeworkTitle);
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof QuizCompletedEvent) {
                    if (!TextUtils.isEmpty(mHomework.getHomeworkId()))
                        submitResponse(mHomework.getHomeworkId());
                    else {
                        mBinding.layoutProgress.progress.setVisibility(View.GONE);
                        mBinding.layoutProgress.texViewMessage.setText(R.string.homework_not_submit);
                        finish();
                    }

                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void submitResponse(final String homeworkId) {
        if (homeworkId != null) {
            if (GeneralUtils.isNetworkAvailable(HomeworkDetailActivity.this)) {

                mHomeworkModel.submitHomework(homeworkId).
                        subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<HomeworkSubmitResponse>() {
                            @Override
                            public void accept(HomeworkSubmitResponse response) throws Exception {
                                mBinding.layoutProgress.getRoot().setVisibility(View.GONE);
                                if (response != null && response.isStatus()) {
                                    mRxBus.send(new RefreshHomeworkEvent());
                                    if (isTypeCourse) {
                                        GeneralUtils.showToastLong(getBaseContext(), getString(R.string.homework_pre_read_submit_success));
                                    } else {
                                        GeneralUtils.showToastShort(getBaseContext(), getString(R.string.homework_submit_success));
                                    }
                                    handleIntent();
                                } else {
                                    GeneralUtils.showToastShort(getBaseContext(), getString(R.string.unable_to_submit_homework));
                                }


                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                GeneralUtils.showToastShort(getBaseContext(), getString(R.string.unable_to_submit_homework));
                                finish();

                            }
                        });
            } else {
                Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                        .setAction((R.string.labelRetry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                submitResponse(homeworkId);
                            }
                        })
                        .show();
            }
        }
    }

    /*Fetching the details of that assignment by student how many are done that
    assignment or not done that assignment*/
    @SuppressLint("CheckResult")
    private void fetchHomeworkDetail(final String homeworkId) {
        if (GeneralUtils.isNetworkAvailable(HomeworkDetailActivity.this)) {
            mBinding.layoutProgress.getRoot().setVisibility(View.VISIBLE);
            mBinding.layoutContent.setVisibility(View.GONE);
            mHomeworkModel.fetchHomeworkDetail(homeworkId).
                    subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Homework>() {
                        @Override
                        public void accept(Homework homeworkDetail) throws Exception {
                            mBinding.layoutProgress.getRoot().setVisibility(View.GONE);
                            if (homeworkDetail != null) {
                                mBinding.layoutContent.setVisibility(View.VISIBLE);
                                mHomework = homeworkDetail;
                                fillData(mHomework);
                            } else {
                                mBinding.layoutContent.setVisibility(View.GONE);
                                GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
                                finish();
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.layoutProgress.getRoot().setVisibility(View.GONE);
                            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
                            finish();

                        }
                    });
        } else {
            Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction((R.string.labelRetry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fetchHomeworkDetail(homeworkId);
                        }
                    })
                    .show();
        }

    }

    private void fillData(Homework homeworkDetail) {
        if (!TextUtils.isEmpty(homeworkDetail.getAssignedDateTime())) {
            mBinding.layoutAssignDate.setVisibility(View.VISIBLE);
            setValuesToTextView(mBinding.textviewAssignmentDate, DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(homeworkDetail.getAssignedDateTime())));
        } else {
            mBinding.layoutAssignDate.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(homeworkDetail.getAssignmentDueDate())) {
            mBinding.layoutDueDate.setVisibility(View.VISIBLE);
            setValuesToTextView(mBinding.textviewDueDate, DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(homeworkDetail.getAssignmentDueDate())));
        } else {
            mBinding.layoutDueDate.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(homeworkDetail.getAttachmentType())) {
            setValuesToTextView(mBinding.textviewType, getAssignmentType(homeworkDetail.getAttachmentType()));
            mBinding.textviewType.setVisibility(View.VISIBLE);
        } else {
            mBinding.textviewType.setVisibility(View.GONE);

        }

        if (homeworkDetail.getMetaInformation() != null) {
            mBinding.layoutSubject.setVisibility(View.VISIBLE);
            setValuesToTextView(mBinding.textviewSubject, homeworkDetail.getMetaInformation().getSubject().getName());
            setValuesToTextView(mBinding.textviewTopic, homeworkDetail.getMetaInformation().getTopic().getName());
        } else {
            mBinding.layoutSubject.setVisibility(View.GONE);
        }


        //  CommonUtils.getInstance().setGroupThumbnail(HomeworkDetailActivity.this,homeworkDetail.getAssignedGroups().get);
        setAssignmentStatus(homeworkDetail);
        setAssignmentInstruction(homeworkDetail);
        setAssignmentDuration(homeworkDetail);
        setAssignedByThumbnail(homeworkDetail);
        setAssignedToThumbnail(homeworkDetail);


    }

    private void setValuesToTextView(TextViewCustom textViewCustom, String values) {
        if (!TextUtils.isEmpty(values))
            textViewCustom.setText(values);
    }

    private void setAssignmentInstruction(Homework homework) {
        if (homework.getInstructions() != null && !homework.getInstructions().isEmpty()) {
            mBinding.layoutAssignmentInstruction.setVisibility(View.VISIBLE);
            TextViewMore.viewMore(homework.getInstructions(), mBinding.textviewInstruction, mBinding.idMore.textViewMoreLess);
        } else {
            mBinding.layoutAssignmentInstruction.setVisibility(View.GONE);
        }
    }

    private void setAssignmentDuration(Homework assignmentResponse) {
        if (assignmentResponse.getAllowedDuration() > 0) {
            mBinding.layoutAssignmentDuration.setVisibility(View.VISIBLE);
            //setValuesToTextView(mBinding.textviewDuration, DateUtils.getTimeStringFromSeconds(assignmentResponse.getAllowedDuration()));
            setValuesToTextView(mBinding.textviewDuration, assignmentResponse.getAllowedDuration() + " Minutes");
        } else {
            mBinding.layoutAssignmentDuration.setVisibility(View.GONE);
        }
    }

    public String getAssignmentType(String assignmentType) {
        if (assignmentType != null) {
            if (assignmentType.equalsIgnoreCase("quiz")) {
                isTypeQuiz = true;
                isTypeCourse = false;
                isTypeResource = false;
                String type = "Quiz";
                mObjectClass = Quiz.class;
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
                } else if (assignmentType.contains("interactiveIm")) {
                    type = "Interactive Image";
                    mObjectClass = InteractiveImage.class;
                } else if (assignmentType.contains("interactiveVi")) {
                    type = "Interactive Video";
                    mObjectClass = InteractiveVideo.class;
                } else {
                    if (assignmentType.contains("pop")) {
                        type = "Pop Up";
                        mObjectClass = PopUps.class;
                    }
                }

                return type;
            }
        }
        return null;
    }

    private ArrayList<String> getStringList(ArrayList<AssignedGroup> assignedGroup) {
        ArrayList<String> list = new ArrayList<>();
        for (AssignedGroup group : assignedGroup) {
            list.add(group.getName());
        }
        return list;
    }

    private void setColor(TextViewCustom textViewCustom, int color) {
        textViewCustom.setTextColor(color);
    }

    private void setAssignmentStatus(Homework homework) {
        if (homework.isSubmitted()) {
            setVisibility(R.id.layout_submitted_on, View.VISIBLE);
//            int color = PrefManager.getColorForSubject(this, homework.getMetaInformation().getSubject().getId());
//            setColor(mBinding.textviewSubmittedOn, color);
//            setColor(mBinding.textviewTimeTaken, color);
//            setColor(mBinding.textviewScore, color);
            setValuesToTextView(mBinding.textviewSubmittedOn, DateUtils.getDayMonthStringFromDate(DateUtils.convertrIsoDate(homework.getSubmittedDate())));

            if (isTypeQuiz && homework.getTotalScore() > 0) {
                mBinding.cardScore.setVisibility(View.VISIBLE);
                setValuesToTextView(mBinding.textviewScore, getScore(homework.getUserMarks(), homework.getTotalScore()));
            } else {
                mBinding.cardScore.setVisibility(View.GONE);
            }

        } else {
            playMenuItemVisibility(true);
            setVisibility(R.id.layout_submitted_on, View.GONE);

        }


    }


    private void setVisibility(int resId, int isVisible) {
        View view = findViewById(resId);
        view.setVisibility(isVisible);
    }

    private void setAssignedToThumbnail(Homework homework) {
        // TODO: 17-Jul-17 load group thumbnail whenever it comes with assignment response
        if (homework.getGroupDetail() != null) {
            String groupName = homework.getGroupDetail().get(0).getGroupName();
            mBinding.textviewGroups.setText(groupName);
            Thumbnail thumbnail = homework.getGroupDetail().get(0).getThumbnail();
            CommonUtils.getInstance().setGroupThumbnail(HomeworkDetailActivity.this, groupName, thumbnail, mBinding.imageViewAssignedTo);
            mBinding.layoutAssignedTo.setVisibility(View.VISIBLE);
            //Picasso.with(getBaseContext()).load(R.drawable.icon_audience_large).resize(300, 300).centerCrop().into(mBinding.imageViewAssignedTo);
        } else {
            mBinding.layoutAssignedTo.setVisibility(View.GONE);
        }
    }

    private void setAssignedByThumbnail(final Homework assignmentResponse) {
        if (assignmentResponse.getTeacherInformation() != null) {
            mBinding.layoutAssignedBy.setVisibility(View.VISIBLE);
            Thumbnail teacherThumbnail = assignmentResponse.getTeacherInformation().getThumbnail();
            String teacherName = assignmentResponse.getTeacherInformation().getName();
            setValuesToTextView(mBinding.textviewAssignedBy, teacherName);
            CommonUtils.getInstance().setUserThumbnail(HomeworkDetailActivity.this, teacherName, teacherThumbnail, mBinding.imageViewAssignedBy);

            mBinding.layoutAssignedBy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        startActivity(UserPublicProfileActivity.getStartIntent(HomeworkDetailActivity.this, assignmentResponse.getAssignedBy()));
                    } else {
                        ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
                    }
                }
            });

        } else {
            mBinding.layoutAssignedBy.setVisibility(View.GONE);
        }
    }


//    public static long calculateTimeTaken(Homework assignmentResponse) {
//        long totalTimeTaken = 0;
//        for (QuestionResponse quizResponse : assignmentResponse.getQuizResponses()) {
//            for (Attempt attempt : quizResponse.getAttempts()) {
//                totalTimeTaken = totalTimeTaken + attempt.getTimeTaken();
//            }
//        }
//
//        return totalTimeTaken;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.dispose();
        }

    }
}