package in.securelearning.lil.android.courses.views.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BulletSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.Timer;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityCourseDetailBinding;
import in.securelearning.lil.android.app.databinding.RateAndReviewBinding;
import in.securelearning.lil.android.assignments.views.activity.AssignActivity;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.dataobjects.UserRating;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerActivity;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaActivity;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.courses.InjectorCourses;
import in.securelearning.lil.android.courses.models.CoursesModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.profile.views.activity.UserPublicProfileActivity;
import in.securelearning.lil.android.syncadapter.events.FavoriteAboutCourseUpdate;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

public class CourseDetailActivity extends AppCompatActivity {

    @Inject
    CoursesModel mCoursesModel;

    @Inject
    RxBus mRxBus;

    private AboutCourse mAboutCourse;

    @Inject
    WebPlayerLiveModel mWebPlayerLiveModel;

    @Inject
    AppUserModel mAppUserModel;

    public static final String DATA_OBJECT = "data_object";
    public static final String DATA_OBJECT_TYPE = "data_object_type";
    public static final String ASSIGNMENT_RESPONSE_ID = "assignment_response_id";
    public static final String DATA_CM = "conceptMap";
    public static final String DATA_II = "interactiveImage";
    public static final String DATA_IV = "interactiveVideo";
    public static final String DATA_DB = "digitalBook";
    public static final String DATA_PU = "popup";
    public static final String DATA_VC = "videoCourse";

    private Disposable mSubscription;
    private String mId = "";
    private String mType = "";
    private String mAssignmentResponseId = "";
    private Class mClass;
    private boolean isWebVersion = false;
    private Timer mTimer;
    private String mWebUrl = "";
    private ActivityCourseDetailBinding mBinding;
    private static final int MAX_TEXT_LINES = 3;
    private Dialog mAlertDialog;
    private Dialog mRatingDialog;
    private MenuItem menuItemShare, menuItemDownload, menuItemDownloadDone, menuItemDownloadQueue, menuItemPlay;
    private boolean isVideoCourse = false;
    double userRating = 0;
    String userComment = "";
    private String mFavouriteAboutCourseId;

    public static Intent getStartActivityIntent(Context context, String objectId, Class classType, String assignmentResponseId) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra(DATA_OBJECT, objectId);
        if (classType.equals(DigitalBook.class)) {
            intent.putExtra(DATA_OBJECT_TYPE, DATA_DB);
        } else if (classType.equals(InteractiveImage.class)) {
            intent.putExtra(DATA_OBJECT_TYPE, DATA_II);
        } else if (classType.equals(ConceptMap.class)) {
            intent.putExtra(DATA_OBJECT_TYPE, DATA_CM);
        } else if (classType.equals(PopUps.class)) {
            intent.putExtra(DATA_OBJECT_TYPE, DATA_PU);
        } else if (classType.equals(VideoCourse.class)) {
            intent.putExtra(DATA_OBJECT_TYPE, DATA_VC);
        } else if (classType.equals(InteractiveVideo.class)) {
            intent.putExtra(DATA_OBJECT_TYPE, DATA_IV);
        } else {
            intent.putExtra(DATA_OBJECT_TYPE, DATA_DB);
        }
        intent.putExtra(ASSIGNMENT_RESPONSE_ID, assignmentResponseId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mId = intent.getStringExtra(DATA_OBJECT);
        mType = intent.getStringExtra(DATA_OBJECT_TYPE);
        mAssignmentResponseId = intent.getStringExtra(ASSIGNMENT_RESPONSE_ID);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_detail);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        InjectorCourses.INSTANCE.getComponent().inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setWebUrl();
        createAlertDialog();
        try {
            showProgressBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWebUrl() {
        mWebUrl = getString(R.string.web_url);
        if (mType.equals(DATA_DB)) {
            mClass = DigitalBook.class;
            mWebUrl += "/digitalbook/open/" + mId + "/preview";
        } else if (mType.equals(DATA_CM)) {
            mClass = ConceptMap.class;
            mWebUrl += "/conceptmap/open/" + mId + "/preview";
        } else if (mType.equals(DATA_II)) {
            mClass = InteractiveImage.class;
            mWebUrl += "/interactiveimage/open/" + mId + "/preview";
        } else if (mType.equals(DATA_PU)) {
            mClass = PopUps.class;
            mWebUrl += "/popup/open/" + mId + "/preview";
        } else if (mType.equals(DATA_VC)) {
            mClass = VideoCourse.class;
            isVideoCourse = true;
            mWebUrl += "/videocourses/open/" + mId + "/preview";
        } else if (mType.equals(DATA_IV)) {
            mClass = InteractiveVideo.class;
            isVideoCourse = true;
            mWebUrl += "/interactiveVideo/open/" + mId + "/preview";
        }
    }

    // TODO: 2/8/2017 show correct menu item according to json status
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        menuItemShare = menu.getItem(0);
        menuItemDownload = menu.getItem(1);
        menuItemDownloadDone = menu.getItem(2);
        menuItemDownloadQueue = menu.getItem(3);
        menuItemPlay = menu.getItem(4);
        setButtonStatus(SyncStatus.NOT_SYNC.getStatus());
        listenRxBusEvents();
        // TODO: 23-08-2017 Here we get updated about course data from server if network is exist, otherwise we get it from local database else show error message
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            fetchAboutCourseFromServer();
        } else {
            fetchAboutCourseFromLocalDataBase();
        }
        return true;
    }

    @SuppressLint("CheckResult")
    private void fetchAboutCourseFromLocalDataBase() {

        Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(ObservableEmitter<AboutCourse> e) throws Exception {
                AboutCourse aboutCourse = mCoursesModel.getAboutCourseFromDatabase(mId);
                if (aboutCourse.getObjectId().equals(mId)) {
                    isWebVersion = false;
                    e.onNext(aboutCourse);
                } else {
                    finish();
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse aboutCourse) {
                        if (aboutCourse != null) {
                            setCourseItem(aboutCourse);
                            hideProgressBar();
                        } else {
                            ToastUtils.showToastAlert(getBaseContext(), "Could not Fetch");
                            finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtils.showToastAlert(getBaseContext(), "Could not Fetch");
                        finish();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void fetchAboutCourseFromServer() {

        Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(ObservableEmitter<AboutCourse> e) throws Exception {
                AboutCourse aboutCourse;
                switch (mType) {
                    case DATA_DB:
                        isWebVersion = true;
                        aboutCourse = mCoursesModel.getDigitalBookAbout(mId);
                        aboutCourse = mCoursesModel.saveAbout(aboutCourse);
                        e.onNext(aboutCourse);
                        break;
                    case DATA_CM:
                        isWebVersion = true;
                        aboutCourse = mCoursesModel.getConceptMapAbout(mId);
                        aboutCourse = mCoursesModel.saveAbout(aboutCourse);
                        e.onNext(aboutCourse);
                        break;
                    case DATA_II:
                        isWebVersion = true;
                        aboutCourse = mCoursesModel.getInteractiveImageAbout(mId);
                        aboutCourse = mCoursesModel.saveAbout(aboutCourse);
                        e.onNext(aboutCourse);
                        break;
                    case DATA_PU:
                        isWebVersion = true;
                        aboutCourse = mCoursesModel.getPopUpsAbout(mId);
                        aboutCourse = mCoursesModel.saveAbout(aboutCourse);
                        e.onNext(aboutCourse);
                        break;
                    case DATA_VC:
                        isWebVersion = true;
                        aboutCourse = mCoursesModel.getVideoCourseAbout(mId);
                        aboutCourse = mCoursesModel.saveAbout(aboutCourse);
                        e.onNext(aboutCourse);
                        break;
                    case DATA_IV:
                        isWebVersion = true;
                        aboutCourse = mCoursesModel.getInteractiveVideoAbout(mId);
                        aboutCourse = mCoursesModel.saveAbout(aboutCourse);
                        e.onNext(aboutCourse);
                        break;
                    default:
                        finish();
                        break;
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse aboutCourse) {
                        if (aboutCourse != null) {
                            setCourseItem(aboutCourse);
                            hideProgressBar();
                        } else {
                            fetchAboutCourseFromLocalDataBase();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtils.showToastAlert(getBaseContext(), "Could not Fetch");
                        finish();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_share) {

            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, mWebUrl);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Send to"));
                mCoursesModel.increaseShareCount(mType, mId);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
            }

        } else if (id == R.id.action_download) {

            setButtonStatus(SyncStatus.JSON_SYNC.toString());
            //startTimer();
            if (mType.equals(DATA_DB)) {
                mCoursesModel.downloadDigitalBook(mAboutCourse, mId);
            } else if (mType.equals(DATA_CM)) {
                mCoursesModel.downloadConceptMap(mAboutCourse, mId);
            } else if (mType.equals(DATA_II)) {
                mCoursesModel.downloadInteractiveImage(mAboutCourse, mId);
            } else if (mType.equals(DATA_PU)) {
                mCoursesModel.downloadPopUp(mAboutCourse, mId);
            } else if (mType.equals(DATA_VC)) {
                mCoursesModel.downloadVideoCourse(mAboutCourse, mId);
            } else if (mType.equals(DATA_IV)) {
                mCoursesModel.downloadInteractiveVideo(mAboutCourse, mId);
            }
            ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.added_to_download_queue));
        } else if (id == R.id.action_download_done) {
            showDeleteCourseDialog();
        } else if (id == R.id.action_play) {
            playCourse();
        }

        return true;
    }

    private void showDeleteCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.course_delete_message)
                .setCancelable(true)
                .setPositiveButton(R.string.action_keep, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setNegativeButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCoursesModel.deleteCourse(mId, mType);
                        setButtonStatus(SyncStatus.NOT_SYNC.getStatus());
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void playCourse() {
        Course course = getCourse();
        if (course != null && course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {

            if (mClass.equals(VideoCourse.class) || mClass.equals(InteractiveVideo.class)
                    || course.getTotalResourceCount().getVideoCourses() > 0 || course.getTotalResourceCount().getVideos() > 0) {
                WebPlayerCordovaActivity.startWebPlayer(this, course.getObjectId(), course.getMetaInformation().getSubject().getId(),
                        course.getMetaInformation().getTopic().getId(), course.getClass(), mAssignmentResponseId, false);
            } else {
                WebPlayerActivity.startWebPlayer(this, course.getObjectId(), course.getMetaInformation().getSubject().getId(),
                        course.getMetaInformation().getTopic().getId(), course.getClass(), mAssignmentResponseId, false);
            }
        } else {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                WebPlayerCordovaLiveActivity.startWebPlayer(this, mId, mAboutCourse.getMetaInformation().getSubject().getId(),
                        mAboutCourse.getMetaInformation().getTopic().getId(), mClass, mAssignmentResponseId, false);
            } else {
                ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe();
    }

    private void unSubscribe() {
        if (mSubscription != null)
            mSubscription.dispose();
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(final Object event) throws Exception {
                if (event instanceof ObjectDownloadComplete) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    if (((ObjectDownloadComplete) event).getId().equals(mId))
                                        setButtonStatus(((ObjectDownloadComplete) event).getSyncStatus().toString());
                                }
                            });


                }


            }
        });
    }

    private void hideProgressBar() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mBinding.mainLayout.setVisibility(View.VISIBLE);
            mBinding.mainLayout.bringToFront();


        }
    }

    private void showProgressBar() {
        if (mAlertDialog != null) {
            mAlertDialog.show();
            mBinding.mainLayout.setVisibility(GONE);
        }
    }

    private void showRatingDialog(final AboutCourse item) {

        final RateAndReviewBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.rate_and_review, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(CourseDetailActivity.this).create();
        dialog.setView(binding.getRoot());

        binding.ratingBar.setRating((float) userRating);
        binding.reviewText.append(Html.fromHtml(userComment));
        if (!TextUtils.isEmpty(userComment)) {
            binding.buttonRateIt.setEnabled(true);
        } else {
            binding.buttonRateIt.setEnabled(false);
        }

        binding.reviewText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!binding.reviewText.getText().toString().trim().isEmpty()) {
                    binding.buttonRateIt.setEnabled(true);
                } else {
                    binding.buttonRateIt.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.buttonRateIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.reviewText.getText().toString().trim())) {
                    SnackBarUtils.showColoredSnackBar(getBaseContext(), binding.getRoot(), getString(R.string.messagePleaseAddReview), ContextCompat.getColor(getBaseContext(), R.color.colorRed));
                } else {
                    mCoursesModel.saveUserRatingCourse(binding.ratingBar.getRating(), binding.reviewText.getText().toString(), mId, item.getCourseType(), item.getTitle());
                    mBinding.content.ratingBar.setRating(binding.ratingBar.getRating());
                    dialog.dismiss();
                }
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void createAlertDialog() {
        mAlertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            finish();
                        }
                        return true;
                    }
                })
                .setView(R.layout.layout_progress_bar)
                .create();
    }

    int likeCount = 0;

    @SuppressLint("CheckResult")
    public void setCourseItem(final AboutCourse item) {
        mAboutCourse = item;

        String title = "";
        String subject = "";
        String subjectTitle = "";
        String topic = "";
        String description = "";
        String duration = " mins";
        String publishedDate = "";
        String audience = "";
        String board = "";
        String boardTitle = "";
        String language = "";
        String grade = "";
        String gradeTitle = "";
        String learningStyle = "";
        String license = "";
        String videos = "";
        String images = "";
        String assessments = "";
        String microLearningCourses = "";
        String curatorName = "";
        String curatorAbout = "";

        double courseRating = 0;
        int courseReviews = 0;
        boolean isFavorite = false;
        isFavorite = mCoursesModel.isCourseFavorite(item);
        courseRating = item.getAvgRating();

        UserRating serverRating = null;
        UserRating dataBaseRating = null;
        for (UserRating rating : item.getReviews().getUserRatings()) {
            if (rating.getUserId().equalsIgnoreCase(mAppUserModel.getObjectId())) {
                serverRating = rating;
                break;
            }
        }
        UserRating ratingsList = mCoursesModel.getUserRating(item.getObjectId());
        if (ratingsList != null) {
            if (ratingsList.getObjectId().equalsIgnoreCase(item.getObjectId())) {
                dataBaseRating = ratingsList;
            }
        }

        if (serverRating != null) {
            if (dataBaseRating != null) {
                if (DateUtils.convertrIsoDate(serverRating.getDate()).before(DateUtils.convertrIsoDate(dataBaseRating.getDate()))) {
                    userRating = dataBaseRating.getRating();
                    userComment = dataBaseRating.getComment();
                } else {
                    userRating = serverRating.getRating();
                    userComment = serverRating.getComment();
                    mCoursesModel.saveUserRatingCourse(serverRating);
                }
            } else {
                userRating = serverRating.getRating();
                userComment = serverRating.getComment();
                mCoursesModel.saveUserRatingCourse(serverRating);
            }
        } else if (dataBaseRating != null) {
            userRating = dataBaseRating.getRating();
            userComment = dataBaseRating.getComment();
        }

        courseReviews = item.getTotalViews();

        likeCount = item.getReviews().getFavouriteCourses().size(); // Value from json
        mBinding.content.likesCount.setText("" + likeCount);
        String userAppId = mAppUserModel.getObjectId();
        if (!item.getReviews().getFavouriteCourses().contains(userAppId)) {
            checkFavoriteItem(item).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<AboutCourse>() {
                @Override
                public void accept(final AboutCourse favorite) throws Exception {
                    if (!TextUtils.isEmpty(favorite.getDocId()) && !favorite.getDocId().equals(item.getObjectId())) {
                        likeCount = likeCount + 1;
                        mBinding.content.likesCount.setText("" + likeCount);
                    }
                }
            });
        }

        publishedDate = item.getPublishedDate();
        //audience = item.getMetaInformation().getAudienceFrom() + " " + getResources().getString(R.string.to) + " " + item.getMetaInformation().getAudienceTo() + " " + getResources().getString(R.string.yrs);

        boardTitle = "Board";
        board = item.getMetaInformation().getBoard().getName();

        language = item.getMetaInformation().getLanguage().getName();

        gradeTitle = "Grade";
        grade = item.getMetaInformation().getGrade().getName();


        learningStyle = item.getMetaInformation().getTopic().getName();
        license = item.getMetaInformation().getLicense().getName();
        videos = item.getTotalResourceCount().getVideos() + "";
        images = item.getTotalResourceCount().getImages() + "";
        assessments = item.getTotalResourceCount().getQuizez() + "";
        microLearningCourses = (item.getTotalResourceCount().getConceptMaps() + item.getTotalResourceCount().getInteractiveImages() + item.getTotalResourceCount().getPopups() + item.getTotalResourceCount().getVideoCourses()) + "";
        title = item.getTitle();


        subjectTitle = "Subject";
        subject = item.getMetaInformation().getSubject().getName();
        topic += item.getMetaInformation().getTopic().getName();
        description = item.getDescription();
        duration = item.getMetaInformation().getDuration() + duration;
        curatorAbout = item.getCurator().getAboutMe();
        curatorName = item.getCurator().getFirstName() + " " + item.getCurator().getLastName();


        mBinding.content.textviewAssignTitle.setText(title);

        mBinding.content.publishedDate.setText(DateUtils.getSimpleDateStringFromSeconds(DateUtils.getSecondsOfISODateString(publishedDate)));
        mBinding.content.duration.setText(duration);
        mBinding.content.audience.setText(setAudience(item));
        if (TextUtils.isEmpty(board) || TextUtils.isEmpty(grade)) {
            mBinding.content.boardGradeCards.setVisibility(View.GONE);
        }
        mBinding.content.board.setText(board);
        mBinding.content.language.setText(language);
        mBinding.content.grade.setText(grade);
        mBinding.content.subject.setText(subject);
        mBinding.content.subjectTitle.setText(subjectTitle);
        mBinding.content.gradeTitle.setText(gradeTitle);
        mBinding.content.boardTitle.setText(boardTitle);
        mBinding.content.learningStyle.setText("Verbal Linguistic");
        mBinding.content.license.setText(license);
        mBinding.content.videos.setText(videos);
        mBinding.content.images.setText(images);
        mBinding.content.assessments.setText(assessments);
        mBinding.content.microLearningCourses.setText(microLearningCourses);
        mBinding.content.ratingBar.setRating((float) courseRating);

        mBinding.content.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, mWebUrl);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Send to"));
            }
        });


        mBinding.content.ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showRatingDialog(item);
                return false;
            }
        });
        mBinding.content.ratingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog(item);
            }
        });

        mBinding.content.courseCuratorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    startActivity(UserPublicProfileActivity.getStartIntent(getBaseContext(), item.getCurator().getId()));
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
                }
            }
        });

        mBinding.buttonAssignCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = AssignActivity.getLaunchIntentForCourse(getBaseContext(),
                        item.getObjectId(),
                        item.getTitle(),
                        item.getCourseType(),
                        item.getMetaInformation(),
                        item.getMicroCourseType(),
                        item.getThumbnail());
                startActivity(mIntent);
            }
        });


        mBinding.content.courseCuratorName.setText(curatorName);
        mBinding.content.courseCuratorAbout.setText(TextViewMore.viewMore(curatorAbout, mBinding.content.courseCuratorAbout, mBinding.content.layoutCuratorAbouttextViewMoreLess.textViewMoreLess));
        mBinding.content.courseCuratorAbout.clearComposingText();
        TextViewMore.viewMore(description, mBinding.content.about, mBinding.content.layoutCourseAbouttextViewMoreLess.textViewMoreLess);
        mBinding.content.about.clearComposingText();
        setLearningOutcome(mAboutCourse.getMetaInformation().getSkills());
        mBinding.content.learningOutcomes.clearComposingText();
        mBinding.content.checkboxFavorite.setVisibility(View.VISIBLE);
        // TODO: 13-09-2017 here we manipulate for offline favouriate course
        checkFavoriteItem(mAboutCourse)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(final AboutCourse favorite) throws Exception {
                        if (!TextUtils.isEmpty(favorite.getDocId()) && favorite.getObjectId().equals(mAboutCourse.getObjectId())) {
                            mFavouriteAboutCourseId = favorite.getDocId();
                            mBinding.content.checkboxFavorite.setChecked(true);
                        } else {
                            mBinding.content.checkboxFavorite.setChecked(false);
                        }
                        mBinding.content.checkboxFavorite
                                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                                @Override
                                                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                                    int count = 0;
                                                                    try {
                                                                        count = Integer.parseInt(mBinding.content.likesCount.getText().toString());
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    if (b) {
                                                                        mBinding.content.likesCount.setText("" + (count + 1));
                                                                        // TODO: 13-09-2017 here we are manipulating course favourite in local database
                                                                        mAboutCourse.setDocId("");
                                                                        AboutCourse aboutCourse = mCoursesModel.saveFavouriteCourse(mAboutCourse);
                                                                        mFavouriteAboutCourseId = aboutCourse.getDocId();
                                                                        mCoursesModel.addAboutFavorite(mAboutCourse).subscribeOn(Schedulers.computation())
                                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                                .subscribe(new Consumer<Boolean>() {
                                                                                    @Override
                                                                                    public void accept(Boolean aBoolean) throws Exception {
                                                                                        if (!aBoolean) {
                                                                                            mBinding.content.checkboxFavorite.setChecked(false);
                                                                                            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_favorite_fail));
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        mBinding.content.likesCount.setText("" + (count - 1));
                                                                        // TODO: 13-09-2017 here we are manipulating course favourite in local database
                                                                        if (!TextUtils.isEmpty(mFavouriteAboutCourseId) && mFavouriteAboutCourseId != null) {
                                                                            if (mCoursesModel.deleteFavourite(mFavouriteAboutCourseId)) {
                                                                                mAboutCourse.setDocId("");
                                                                            }
                                                                        }
                                                                        mCoursesModel.removeAboutFavorite(mAboutCourse).subscribeOn(Schedulers.computation())
                                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                                .subscribe(new Consumer<Boolean>() {
                                                                                    @Override
                                                                                    public void accept(Boolean aBoolean) throws Exception {
                                                                                        if (!aBoolean) {
                                                                                            mBinding.content.checkboxFavorite.setChecked(true);
                                                                                            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_unfavorite_fail));
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                    mRxBus.send(new FavoriteAboutCourseUpdate(mAboutCourse.getObjectId(), b));
                                                                }
                                                            }
                                );
                    }
                });


//        mBinding.content.checkboxFavorite.setChecked(isFavorite);
//        mBinding.content.checkboxFavorite
//                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                                @Override
//                                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                                                    int count = 0;
//                                                    try {
//                                                        count = Integer.parseInt(mBinding.content.likesCount.getText().toString());
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                    if (b) {
//                                                        mBinding.content.likesCount.setText("" + (count + 1));
//                                                        mCoursesModel.addAboutFavorite(item).subscribeOn(Schedulers.computation())
//                                                                .observeOn(AndroidSchedulers.mainThread())
//                                                                .subscribe(new Consumer<Boolean>() {
//                                                                    @Override
//                                                                    public void accept(Boolean aBoolean) throws Exception {
//                                                                        if (!aBoolean) {
//                                                                            mBinding.content.checkboxFavorite.setChecked(false);
//                                                                            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_favorite_fail));
//                                                                        }
//                                                                    }
//                                                                });
//                                                    } else {
//                                                        mBinding.content.likesCount.setText("" + (count - 1));
//                                                        mCoursesModel.removeAboutFavorite(item).subscribeOn(Schedulers.computation())
//                                                                .observeOn(AndroidSchedulers.mainThread())
//                                                                .subscribe(new Consumer<Boolean>() {
//                                                                    @Override
//                                                                    public void accept(Boolean aBoolean) throws Exception {
//                                                                        if (!aBoolean) {
//                                                                            mBinding.content.checkboxFavorite.setChecked(true);
//                                                                            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_unfavorite_fail));
//                                                                        }
//                                                                    }
//                                                                });
//                                                    }
//                                                    mRxBus.send(new FavoriteAboutCourseUpdate(item.getObjectId(), b));
//
//                                                }
//                                            }
//                );

        if (mCoursesModel.checkIsBeingDeleted(item.getInternalNotificationDocId())) {
            setButtonStatus(SyncStatus.NOT_SYNC.getStatus());
        } else {
            final Course course = getCourse();

            if (course != null) {
                setButtonStatus(course.getSyncStatus());
//            if (course.getSyncStatus().equals(SyncStatus.JSON_SYNC.toString())) {
//                startTimer();
//            }
            }
        }


        checkCanAssignCourse();
        setCourseView(courseReviews);
        checkIsVideoCourse(item);
        setCourseThumbnailAndBanner(item);
        setTitle(setLearningModuleType(item));
        setCuratorThumbnail(item);
    }

    public Observable<AboutCourse> checkFavoriteItem(final AboutCourse checkCourse) {
        return Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(ObservableEmitter<AboutCourse> e) throws Exception {
                AboutCourse favorite = mCoursesModel.getFavouriteCourse(checkCourse.getObjectId());
                if (favorite != null) {
                    e.onNext(favorite);
                }
                e.onComplete();
            }
        });
    }

    private void setCuratorThumbnail(AboutCourse item) {

        try {
            String curatorImageFilePath = "";
            if (item.getCurator().getUserThumbnail() != null) {
                curatorImageFilePath = item.getCurator().getUserThumbnail().getUrl();
                if (TextUtils.isEmpty(curatorImageFilePath)) {
                    curatorImageFilePath = item.getCurator().getUserThumbnail().getThumb();
                }
            }
            if (TextUtils.isEmpty(curatorImageFilePath)) {
                String firstWord = item.getCurator().getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                mBinding.content.courseCuratorImage.setImageDrawable(textDrawable);
            } else {
                Picasso.with(getBaseContext()).load(curatorImageFilePath).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.content.courseCuratorImage);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String setLearningModuleType(AboutCourse item) {
        if (mType.equals(DATA_DB)) {
            return "Digital Book";
        } else if (mType.equals(DATA_CM)) {
            return "Concept Map";
        } else if (mType.equals(DATA_II)) {
            return "Interactive Image";
        } else if (mType.equals(DATA_PU)) {
            if (item.getPopUpType() != null && !TextUtils.isEmpty(item.getPopUpType().getValue())) {
                return item.getPopUpType().getValue();
            }
            return "PopUp";
        } else if (mType.equals(DATA_VC)) {
            return "Video Course";
        } else if (mType.equals(DATA_IV)) {
            return "Interactive Video";
        } else {
            return "Course";
        }
    }

    private void setCourseThumbnailAndBanner(AboutCourse item) {

        try {
            String imagePath = item.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = item.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = item.getMetaInformation().getBanner();
            }
            if (!imagePath.isEmpty()) {
                Picasso.with(CourseDetailActivity.this).load(imagePath).into(mBinding.content.imageviewCourseThumbnail);
            } else {
                Picasso.with(CourseDetailActivity.this).load(in.securelearning.lil.android.base.R.drawable.image_large).into(mBinding.content.imageviewCourseThumbnail);
            }
        } catch (Exception e) {
            try {
                Picasso.with(CourseDetailActivity.this).load(item.getThumbnail().getThumb()).into(mBinding.content.imageviewCourseThumbnail);
            } catch (Exception e1) {
                try {
                    Picasso.with(CourseDetailActivity.this).load(in.securelearning.lil.android.base.R.drawable.image_large).into(mBinding.content.imageviewCourseThumbnail);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void checkIsVideoCourse(AboutCourse item) {
        if (item.getTotalResourceCount().getVideoCourses() > 0 || item.getTotalResourceCount().getVideos() > 0) {
            isVideoCourse = true;
        }
    }

    private String setAudience(AboutCourse item) {
        int audienceFrom = item.getMetaInformation().getAudienceFrom();
        int audienceTo = item.getMetaInformation().getAudienceTo();
        if (audienceTo == 0 || String.valueOf(audienceTo).isEmpty()) {
            return String.valueOf(audienceFrom) + "+ ";
        } else {
            return String.valueOf(audienceFrom) + " to " + String.valueOf(audienceTo) + " years";
        }

    }

    private void checkCanAssignCourse() {
        if (PermissionPrefsCommon.getAssignmentCreatePermission(this)) {
            if (BuildConfig.CourseAssign == true) {
                mBinding.buttonAssignCourse.setVisibility(View.VISIBLE);
                mBinding.content.scrollViewMain.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY > oldScrollY) {
                            mBinding.buttonAssignCourse.hide();
                        } else {
                            mBinding.buttonAssignCourse.show();
                        }
                    }
                });
            } else {
                mBinding.buttonAssignCourse.setVisibility(View.GONE);
            }

        } else {
            mBinding.buttonAssignCourse.setVisibility(View.GONE);
        }
    }

    private void setCourseView(int courseReviews) {
        if (courseReviews > 1) {
            mBinding.content.ratingCount.setText(" " + courseReviews + " Views");
        } else {
            mBinding.content.ratingCount.setText(" " + courseReviews + " View");
        }
    }

    private void setLearningOutcome(java.util.List<Skill> skills) {
        int size = 0;
        CharSequence textCollapsed = "";
        CharSequence textExpanded = "";
//        text = "<ul>";
        if (skills.size() > 0) {
            for (Skill skill :
                    skills) {
                size += skill.getSkillName().length();

                SpannableString s1 = new SpannableString(skill.getSkillName());
                s1.setSpan(new BulletSpan(20), 0, skill.getSkillName().length(), 0);

                textExpanded = TextUtils.concat(textExpanded, s1);
                textExpanded = TextUtils.concat(textExpanded, "\n");
                if (size < 260) {
                    textCollapsed = TextUtils.concat(textCollapsed, s1);
                    textCollapsed = TextUtils.concat(textCollapsed, "\n");
                }
//                text = text + "<li>" + skill.getSkillName() + "</li>";
            }
            TextViewMore.viewMoreLearningOutcomes(textExpanded, textCollapsed, mBinding.content.learningOutcomes, mBinding.content.layoutLearningOutcometextViewMoreLess.textViewMoreLess);
        }

    }

    private CharSequence getLearningOutcomeString(AboutCourse aboutCourse) {
//        String text = "";
//        char id = 'a';
//        if (aboutCourse.getMetaInformation().getLearningOutcomes().size() > 0) {
//            for (LearningOutcome outcome :
//                    aboutCourse.getMetaInformation().getLearningOutcomes()) {
//                text += id + ". " + outcome.getId() + "\n";
//                id++;
//            }
//        } else if (aboutCourse.getMetaInformation().getLearningGoals().size() > 0) {
//            for (String s :
//                    aboutCourse.getMetaInformation().getLearningGoals()) {
//                text += id + ". " + s + "\n";
//                id++;
//            }
//        } else if (aboutCourse.getMetaInformation().getSkills().size() > 0) {
//            for (Skill skill :
//                    aboutCourse.getMetaInformation().getSkills()) {
//                text += id + ". " + skill.getSkillName() + "\n";
//                id++;
//            }
//        }
        int size = 0;
        CharSequence textCollapsed = "";
        CharSequence textExpanded = "";
//        text = "<ul>";
        if (aboutCourse.getMetaInformation().getSkills().size() > 0) {
            for (Skill skill :
                    aboutCourse.getMetaInformation().getSkills()) {
                size += skill.getSkillName().length();

                SpannableString s1 = new SpannableString(skill.getSkillName());
                s1.setSpan(new BulletSpan(20), 0, skill.getSkillName().length(), 0);
                textExpanded = TextUtils.concat(textExpanded, s1);
                if (size < 260) {
                    textCollapsed = textExpanded;
                }
//                text = text + "<li>" + skill.getSkillName() + "</li>";
            }
        }
//        text = text + "</ul>";

        return textExpanded;
    }

    private void setButtonStatus(String status) {

        if (status.equals(SyncStatus.COMPLETE_SYNC.getStatus())) {
            menuItemDownload.setVisible(false);
            menuItemDownloadDone.setVisible(true);
            menuItemDownloadQueue.setVisible(false);
            menuItemPlay.setVisible(true);
            //cancelTimer();
        } else if (status.equals(SyncStatus.JSON_SYNC.getStatus())) {
            if (mClass.equals(VideoCourse.class)) {
                menuItemDownload.setVisible(false);
                menuItemDownloadDone.setVisible(false);
                menuItemDownloadQueue.setVisible(true);
                //menuItemDownload.setActionView(R.layout.layout_download_course_detail_action_bar);
            } else {
                menuItemDownloadQueue.setVisible(true);
                menuItemDownloadDone.setVisible(false);
                menuItemDownload.setVisible(false);
            }

            menuItemPlay.setVisible(true);

        } else {
            menuItemDownload.setActionView(null);
            menuItemDownload.setIcon(R.drawable.action_download_w);
            menuItemDownload.setVisible(!mClass.equals(VideoCourse.class));
            menuItemPlay.setVisible(true);
            menuItemDownloadDone.setVisible(false);
            menuItemDownloadQueue.setVisible(false);
            //cancelTimer();
        }
    }

    private Course getCourse() {
        Course course = null;
        if (mType.equals(DATA_DB)) {
            course = mCoursesModel.getFromDatabaseDigitalBook(mId);
        } else if (mType.equals(DATA_CM)) {
            course = mCoursesModel.getFromDatabaseConceptMap(mId);
        } else if (mType.equals(DATA_II)) {
            course = mCoursesModel.getFromDatabaseInteractiveImage(mId);
        } else if (mType.equals(DATA_PU)) {
            course = mCoursesModel.getFromDatabasePopUps(mId);
        } else if (mType.equals(DATA_VC)) {
            course = mCoursesModel.getFromDatabaseVideoCourse(mId);
        } else if (mType.equals(DATA_IV)) {
            course = mCoursesModel.getFromDatabaseInteractiveVideo(mId);
        }
        return course;
    }

//    private void startTimer() {
//        if (mTimer != null) {
//            mTimer.cancel();
//        }
//        mTimer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                final Course course = getCourse();
//                final boolean status = SyncService.checkJobStatus(course.getClass(), mId);
//
//                if (course != null) {
//                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Action() {
//                                @Override
//                                public void run() {
//                                    if (status) {
//                                        setButtonStatus(SyncStatus.JSON_SYNC.toString());
//                                    } else {
//                                        if (course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
//                                            setButtonStatus(course.getSyncStatus());
//                                        else setButtonStatus(SyncStatus.NOT_SYNC.toString());
//                                    }
//                                }
//                            });
//                }
//            }
//        };
//        mTimer.schedule(task, 1000, 1000);
//    }
//
//    private void cancelTimer() {
//        if (mTimer != null) {
//            mTimer.cancel();
//        }
//    }

}