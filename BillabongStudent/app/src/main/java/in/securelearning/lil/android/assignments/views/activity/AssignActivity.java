package in.securelearning.lil.android.assignments.views.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAssignmentCreationBinding;
import in.securelearning.lil.android.app.databinding.LayoutFlexiNumberPickerBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerviewPopupBinding;
import in.securelearning.lil.android.assignments.model.AssignResourceActivityModel;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.constants.QuizTypeEnum;
import in.securelearning.lil.android.base.constants.RubricTitle;
import in.securelearning.lil.android.base.dataobjects.AssignedGroup;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Rubric;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.DialogUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.KeyBoardUtil;
import in.securelearning.lil.android.base.utils.NumberUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.quizcreator.events.QuizMinimalDeleteEvent;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.disposables.Disposable;

public class AssignActivity extends AppCompatActivity {
    public static final String TYPE_QUIZ = "quiz";
    private static final String UID = "uid";
    private static final String OBJECT = "object";
    private static final String QUIZ_ALIAS = "alias";
    private static final String ASSIGN_TYPE = "type";
    private static final String TYPE_COURSE = "course";
    private static final String TYPE_RESOURCE = "resource";
    @Inject
    RxBus mRxBus;
    ArrayList<TextView> mRubricViews = new ArrayList<>();
    @Inject
    AssignResourceActivityModel mAssignResourceActivityModel;
    LayoutAssignmentCreationBinding mBinding;
    private String mAssignmentTitle;
    private String mResourceType;
    private Disposable mSubscription;
    private String objectUid = "";
    private String quizAlias = "";
    private String mAssignType = "";
    private String mCourseAssignmentType = "";
    private Assignment mAssignment = new Assignment();
    private Quiz mQuiz = new Quiz();
    private CourseExt mCourse;
    private FavouriteResourceExt mFavouriteResourceExt;
    private AlertDialog mGroupSelectDialog;
    private ArrayList<GroupAbstract> mModeratedGroupsList;
    private boolean mIsInFocus = false;
    private Dialog mGroupDialog;

    public static Intent getLaunchIntentForQuiz(Context context, String quizUid, String quizAlias) {
        Intent intent = new Intent(context, AssignActivity.class);
        intent.putExtra(UID, quizUid);
        intent.putExtra(QUIZ_ALIAS, quizAlias);
        intent.putExtra(ASSIGN_TYPE, TYPE_QUIZ);
        return intent;
    }

    public static Intent getLaunchIntentForResource(Context context, String objectId, String title, String type, MetaInformation metaInformation, String urlMain, String urlThumbnail, double duration, String videoId) {
        Intent intent = new Intent(context, AssignActivity.class);
        intent.putExtra(OBJECT, new FavouriteResourceExt(objectId, title, type, metaInformation, urlMain, urlThumbnail, duration, videoId));
        intent.putExtra(ASSIGN_TYPE, TYPE_RESOURCE);
        return intent;
    }

    public static Intent getLaunchIntentForCourse(Context context, String mCourseId, String mCourseTitle, String mCourseType, MetaInformation metaInformation, String mMicroCourseType, Thumbnail mCourseThumbnail) {
        Intent intent = new Intent(context, AssignActivity.class);
        intent.putExtra(OBJECT, new CourseExt(mCourseId, mCourseTitle, mCourseType, metaInformation, mMicroCourseType, mCourseThumbnail));
        intent.putExtra(ASSIGN_TYPE, TYPE_COURSE);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_assignment_creation);
        setUpToolbar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mAssignType = getIntent().getStringExtra(ASSIGN_TYPE);
            if (mAssignType.equalsIgnoreCase(TYPE_QUIZ)) {

                objectUid = getIntent().getStringExtra(UID);
                quizAlias = getIntent().getStringExtra(QUIZ_ALIAS);
                if (TextUtils.isEmpty(objectUid)) {
                    mQuiz = mAssignResourceActivityModel.getQuizFromAliasSync(quizAlias);
                } else {
                    mQuiz = mAssignResourceActivityModel.getQuizFromUidSync(objectUid);
                }

                setTopic(mQuiz.getMetaInformation().getTopic().getName());
                setSubject(mQuiz.getMetaInformation().getSubject().getName());
                setGrade(mQuiz.getMetaInformation().getGrade().getName());
                mAssignmentTitle = mQuiz.getTitle();
                mAssignment = mAssignResourceActivityModel.initializeAssignmentFromQuiz(mQuiz);
            } else if (mAssignType.equalsIgnoreCase(TYPE_COURSE)) {

                mCourse = (CourseExt) getIntent().getSerializableExtra(OBJECT);
                mBinding.layoutMarksDuration.setVisibility(View.GONE);
                setTopic(mCourse.getMetaInformation().getTopic().getName());
                setSubject(mCourse.getMetaInformation().getSubject().getName());
                setGrade(mCourse.getMetaInformation().getGrade().getName());
                if (mCourse != null) {
                    setCourseType(mCourse);
                }
                mAssignmentTitle = mCourse.getCourseTitle();
                mAssignment = mAssignResourceActivityModel.initializeAssignmentFromCourse(mCourse);

            } else {
                mFavouriteResourceExt = (FavouriteResourceExt) getIntent().getSerializableExtra(OBJECT);
                mBinding.layoutMarksDuration.setVisibility(View.GONE);
                setTopic(mFavouriteResourceExt.getMetaInformation().getTopic().getName());
                setSubject(mFavouriteResourceExt.getMetaInformation().getSubject().getName());
                setGrade(mFavouriteResourceExt.getMetaInformation().getGrade().getName());
                mResourceType = mFavouriteResourceExt.getVideoType();
                mAssignmentTitle = mFavouriteResourceExt.getVideoTitle();
                mAssignment = mAssignResourceActivityModel.initializeAssignmentFromResource(mFavouriteResourceExt);
            }

        } else {
            if (savedInstanceState != null) {
                objectUid = savedInstanceState.getString(UID);
                quizAlias = savedInstanceState.getString(QUIZ_ALIAS);
                mAssignType = getIntent().getStringExtra(ASSIGN_TYPE);
                if (mAssignType.equalsIgnoreCase(TYPE_QUIZ)) {
                    if (objectUid.isEmpty()) {
                        mQuiz = mAssignResourceActivityModel.getQuizFromAliasSync(quizAlias);

                    } else {
                        mQuiz = mAssignResourceActivityModel.getQuizFromUidSync(objectUid);
                    }
                } else if (mAssignType.equalsIgnoreCase(TYPE_COURSE)) {
                    mBinding.layoutMarksDuration.setVisibility(View.GONE);
                } else {
                    mBinding.layoutMarksDuration.setVisibility(View.GONE);
                }

            }
        }

        initializeUi();
        setupSubscription();
        getModeratedGroups();
    }

    private void getModeratedGroups() {
        mModeratedGroupsList = mAssignResourceActivityModel.getModeratorGroupsOfUser();
        if (mModeratedGroupsList.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage(getString(R.string.propmt_assign_message))
                    .setNeutralButton(R.string.exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(AssignActivity.this, R.color.colorAssignActivity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorAssignActivity)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.action_close_w);
        SpannableString title = new SpannableString(getString(R.string.notification_new_assignment));
        title.setSpan(new TypefaceSpan("sans-serif-condensed"), 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(title);
    }

    private void setTopic(String topic) {
        if (!TextUtils.isEmpty(topic)) {
            mBinding.textViewTopic.setText(topic);
        } else {
            mBinding.layoutTopic.setVisibility(View.GONE);
        }
    }

    private void setSubject(String subject) {
        if (!TextUtils.isEmpty(subject)) {
            mBinding.textViewSubject.setText(subject);
        } else {
            mBinding.layoutSubject.setVisibility(View.GONE);
        }
    }

    private void setGrade(String grade) {
        if (!TextUtils.isEmpty(grade)) {
            mBinding.textViewGrade.setText(grade);
        } else {
            mBinding.layoutGrade.setVisibility(View.GONE);
        }
    }

    private void setCourseType(CourseExt courseExt) {
        String type = "";
        int typeImage = R.drawable.digital_book;
        if (courseExt.getCourseType().equalsIgnoreCase("digitalbook")) {
            type = AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType();
            typeImage = R.drawable.digital_book;
            mCourseAssignmentType = courseExt.getCourseType();
        } else if (courseExt.getCourseType().equalsIgnoreCase("videocourse")) {
            type = AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType();
            typeImage = R.drawable.video_course;
            mCourseAssignmentType = courseExt.getCourseType();
        } else if (courseExt.getMicroCourseType().toLowerCase().contains("map")) {
            type = AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType();
            typeImage = R.drawable.concept_map;
            mCourseAssignmentType = courseExt.getMicroCourseType();
        } else if (courseExt.getMicroCourseType().toLowerCase().contains("interactiveimage")) {
            type = AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType();
            typeImage = R.drawable.interactive_image;
            mCourseAssignmentType = courseExt.getMicroCourseType();
        } else if (courseExt.getMicroCourseType().toLowerCase().contains("pop")) {
            type = AssignmentType.TYPE_Popup.getAssignmentType();
            typeImage = R.drawable.popup;
            mCourseAssignmentType = courseExt.getMicroCourseType();
        } else if (courseExt.getMicroCourseType().toLowerCase().contains("video")) {
            type = AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType();
            typeImage = R.drawable.interactive_image;
            mCourseAssignmentType = courseExt.getMicroCourseType();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        if (getIntent().hasExtra(QUIZ_ALIAS)) {
            outState.putString(UID, objectUid);
            outState.putString(QUIZ_ALIAS, quizAlias);
            outState.putString(ASSIGN_TYPE, mAssignType);
        } else {
            outState.putString(ASSIGN_TYPE, mAssignType);
            outState.putString(UID, objectUid);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBinding.textViewSelectedGroup.getText().toString().isEmpty() &&
                mBinding.textViewDuration.getText().toString().isEmpty() &&
                mBinding.textViewSelectedDueDate.getText().toString().isEmpty() &&
                mBinding.textViewMarks.getText().toString().isEmpty() &&
                mBinding.editTextInstruction.getText().toString().isEmpty()) {

            finish();
        } else {
            showExitConfirmationDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.actionDone:
                assignButtonClickAction();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * initialize ui components
     */
    public void initializeUi() {

        initializeUIAndClickListeners();
        KeyBoardUtil.hideSoftKeyboard(this.getCurrentFocus(), this.getBaseContext());

    }

    /**
     * initialize views and click listeners
     */
    private void initializeUIAndClickListeners() {

//        if (mAssignType.equalsIgnoreCase(TYPE_QUIZ)) {
//            setQuizThumbnail();
//        } else if (mAssignType.equalsIgnoreCase(TYPE_COURSE)) {
//            setCourseThumbnail();
//        } else {
//            setResourceThumbnail();
//        }

        mBinding.editTextInstruction.setText("");
        mBinding.layoutGruopSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGroupListDialog(mModeratedGroupsList);
            }

        });

        mBinding.layoutSelectDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(mBinding.textViewSelectedDueDate);
            }
        });

        mBinding.textViewAssignTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mIsInFocus = true;
                mBinding.textViewAssignTitle.setFocusable(true);
                mBinding.textViewAssignTitle.setFocusableInTouchMode(true);
                mBinding.textViewAssignTitle.setLongClickable(false);
                mBinding.textViewAssignTitle.requestFocus();
                KeyBoardUtil.showSoftKeyboard(mBinding.textViewAssignTitle, getBaseContext());
                return false;
            }
        });

        mBinding.relativeLayoutFake.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsInFocus) {
                    mIsInFocus = false;
                    mBinding.textViewAssignTitle.setFocusable(false);
                    mBinding.textViewAssignTitle.setFocusableInTouchMode(false);
                    mBinding.textViewAssignTitle.setLongClickable(false);
                    mBinding.textViewAssignTitle.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mBinding.textViewAssignTitle.getWindowToken(), 0);
                    return true;
                }

                return false;
            }
        });

        mBinding.marksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFlexLayout(mBinding.textViewMarks.getText().toString().trim(), mBinding.textViewDuration.getText().toString().trim());
            }
        });

        mBinding.durationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFlexLayout(mBinding.textViewMarks.getText().toString().trim(), mBinding.textViewDuration.getText().toString().trim());
            }
        });


        mBinding.textViewAssignTitle.setText(mAssignmentTitle);
        mBinding.textViewAssignTitle.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mBinding.editTextInstruction.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    private void showFlexLayout(String maxMarks, String duration) {
        final Dialog dialog = new Dialog(AssignActivity.this);
        final LayoutFlexiNumberPickerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_flexi_number_picker, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77000000")));

        if (maxMarks.equals(getString(R.string.set_maximum_marks))) {
            binding.textViewFlexBoxMaxMarks.setText("");
        } else {
            binding.textViewFlexBoxMaxMarks.setText(maxMarks);
        }
        if (duration.equals(getString(R.string.set_duration))) {
            binding.textViewFlexBoxDuration.setText("");
        } else {
            binding.textViewFlexBoxDuration.setText(duration);
        }

        binding.buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.textViewFlexBoxMaxMarks.getText().toString().trim().isEmpty()) {
                    showMessage(getString(R.string.error_assignment_max_marks));
                } else if (binding.textViewFlexBoxDuration.getText().toString().trim().isEmpty()) {
                    showMessage(getString(R.string.error_assignment_duration));
                } else {
                    mBinding.textViewMarks.setText(binding.textViewFlexBoxMaxMarks.getText().toString().trim());
                    mBinding.textViewDuration.setText(binding.textViewFlexBoxDuration.getText().toString().trim());
                    mBinding.textViewMarks.setTextSize(24f);
                    mBinding.textViewDuration.setTextSize(24f);
                    dialog.dismiss();
                }
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        setMaxMarks(binding);

        setDuration(binding);

        dialog.show();

    }

    private void setDuration(final LayoutFlexiNumberPickerBinding binding) {
        binding.buttonDuration0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.textViewFlexBoxDuration.getText().toString().trim().isEmpty()) {
                    binding.textViewFlexBoxDuration.append("0");

                }
            }
        });

        binding.buttonDuration1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("1");
            }
        });

        binding.buttonDuration2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("2");
            }
        });

        binding.buttonDuration3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("3");
            }
        });

        binding.buttonDuration4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("4");
            }
        });

        binding.buttonDuration5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("5");
            }
        });

        binding.buttonDuration6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("6");
            }
        });

        binding.buttonDuration7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("7");
            }
        });

        binding.buttonDuration8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("8");
            }
        });

        binding.buttonDuration9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.append("9");
            }
        });

        binding.buttonDurationClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxDuration.setText("");
            }
        });
    }

    private void setMaxMarks(final LayoutFlexiNumberPickerBinding binding) {
        binding.buttonMarks0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.textViewFlexBoxMaxMarks.getText().toString().trim().isEmpty()) {
                    binding.textViewFlexBoxMaxMarks.append("0");

                }
            }
        });

        binding.buttonMarks1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("1");
            }
        });

        binding.buttonMarks2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("2");
            }
        });

        binding.buttonMarks3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("3");
            }
        });

        binding.buttonMarks4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("4");
            }
        });

        binding.buttonMarks5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("5");
            }
        });

        binding.buttonMarks6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("6");
            }
        });

        binding.buttonMarks7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("7");
            }
        });

        binding.buttonMarks8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("8");
            }
        });

        binding.buttonMarks9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.append("9");
            }
        });

        binding.buttonMarksClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewFlexBoxMaxMarks.setText("");
            }
        });
    }

    private void setResourceThumbnail() {
        String thumbnailPath = mFavouriteResourceExt.getUrlThumbnail();
        if (!TextUtils.isEmpty(thumbnailPath)) {
            Picasso.with(getBaseContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(mBinding.imageviewAssign);
        } else {
            Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mBinding.imageviewAssign);
        }

    }

    private void setCourseThumbnail() {
        String thumbnailPath = mCourse.getCourseThumbnail().getLocalUrl();
        if (TextUtils.isEmpty(thumbnailPath)) {
            thumbnailPath = mCourse.getCourseThumbnail().getUrl();
        }
        if (TextUtils.isEmpty(thumbnailPath)) {
            thumbnailPath = mCourse.getCourseThumbnail().getThumb();
        }
        try {
            if (!TextUtils.isEmpty(thumbnailPath)) {
                Picasso.with(getBaseContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(mBinding.imageviewAssign);
            } else {
                Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mBinding.imageviewAssign);
            }
        } catch (Exception e) {
            try {
                Picasso.with(getBaseContext()).load(mCourse.getCourseThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(mBinding.imageviewAssign);
            } catch (Exception e1) {
                try {
                    Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mBinding.imageviewAssign);

                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void setQuizThumbnail() {
        String thumbnailPath = "";
        if (mQuiz.getThumbnail() != null) {
            thumbnailPath = mQuiz.getThumbnail().getLocalUrl();
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = mQuiz.getThumbnail().getUrl();
            }
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = mQuiz.getThumbnail().getThumb();
            }
        }
        try {
            if (!TextUtils.isEmpty(thumbnailPath)) {
                Picasso.with(getBaseContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(mBinding.imageviewAssign);
            } else {
                Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mBinding.imageviewAssign);
            }
        } catch (Exception e) {
            try {
                Picasso.with(getBaseContext()).load(mQuiz.getThumbnail().getThumb()).placeholder(R.drawable.gradient_black_bottom).resize(800, 640).centerInside().into(mBinding.imageviewAssign);
            } catch (Exception e1) {
                try {
                    Picasso.with(getBaseContext()).load(R.drawable.image_quiz_default).resize(800, 640).centerInside().into(mBinding.imageviewAssign);

                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    /**
     * initialize views and variables related to group select mechanism
     */
    public void initializeGroupSelectViews() {

        View mGroupSelectDialogView;
        final ArrayList<Group> groups;
        final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        ViewGroup viewGroupPicker;
        AlertDialog.Builder groupSelectDialogBuilder;

        groups = mAssignResourceActivityModel.getGroupListByUserUId();
        mGroupSelectDialogView = getLayoutInflater().inflate(R.layout.popup_select_groups, null);

        viewGroupPicker = (ViewGroup) mGroupSelectDialogView.findViewById(R.id.layout_groups);
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            View view = getLayoutInflater().inflate(R.layout.layout_assign_resource_group_picker_dialog_item, null);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_group_picker);
            checkBox.setTag(i);
            checkBox.setText(group.getGroupName());
            checkBoxes.add(checkBox);
            viewGroupPicker.addView(view);
        }

        groupSelectDialogBuilder = new AlertDialog.Builder(this);
        groupSelectDialogBuilder.setView(mGroupSelectDialogView);
        groupSelectDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAssignment.getAssignedGroups().clear();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isChecked()) {
                        mAssignment.getAssignedGroups().add(new AssignedGroup(groups.get(i).getObjectId(), groups.get(i).getGroupName()));
                    }
                }


                if (mAssignment.getAssignedGroups().size() > 0)
                    mBinding.textViewSelectedGroup.setText(getString(R.string.selected_group) + " - " + mAssignment.getAssignedGroups().get(0).getName());
            }
        });
        groupSelectDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBinding.textViewSelectedGroup.setText("");
                clearChecks(checkBoxes);
                dialog.dismiss();
            }
        });
        mGroupSelectDialog = groupSelectDialogBuilder.create();

    }

    /**
     * Show confirmation dialog when any edittext is filled on back press
     */
    private void showExitConfirmationDialog() {

        new AlertDialog.Builder(this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    /**
     * Show confirmation dialog when any edittext is filled on back press
     *
     * @param type
     */
    private void showAssignConfirmationDialog(final String type) {
        new AlertDialog.Builder(AssignActivity.this)
                .setTitle("Assignment")
                .setMessage(mBinding.textViewAssignTitle.getText().toString() + " \n\n"
                        + mBinding.textViewSelectedGroup.getText().toString() + "\n"
                        + mBinding.textViewSelectedDueDate.getText().toString())
                .setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals(TYPE_QUIZ)) {
                            getQuizToAssign();
                        } else if (type.equals(TYPE_COURSE)) {
                            getCourseToAssign();
                        } else if (type.equals(TYPE_RESOURCE)) {
                            getResourceToAssign();
                        }
                    }
                })
                .setNeutralButton("Edit", null)
                .show();
    }

    /**
     * clear checks from all the selected groups
     * and clear the assigned groups list in the mAssignment variable
     *
     * @param checkBoxes
     */
    private void clearChecks(ArrayList<CheckBox> checkBoxes) {
        for (CheckBox checkBox :
                checkBoxes) {
            checkBox.setChecked(false);
        }
        mAssignment.getAssignedGroups().clear();
    }

    /**
     * show the dialog to select groups to assign the assignment
     */
    private void showSelectGroupDialog() {
        mGroupSelectDialog.show();

        KeyBoardUtil.hideSoftKeyboard(this.getCurrentFocus(), this.getBaseContext());
    }

    /**
     * show the date picker dialog
     *
     * @param dueDateTextView the text view to show the selected date on
     */
    private void showDatePickerDialog(final TextView dueDateTextView) {
        Date date = new Date();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                long secondsSelected = DateUtils.getSecondsForMidnight(year, monthOfYear, dayOfMonth);
                if (DateUtils.isFutureDay(secondsSelected, TimeUnit.SECONDS)) {
                    mAssignment.setDueDate(DateUtils.getISO8601DateStringFromDate(new Date(secondsSelected * 1000)));
                    SpannableString title = new SpannableString(getString(R.string.due_on) + " " + DateUtils.getSimpleDateStringFromSeconds(secondsSelected));
                    title.setSpan(new TypefaceSpan("sans-serif-condensed"), 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    dueDateTextView.setText(title);


                } else {
                    showErrorMessage("Please select date after " + DateUtils.getCurrentDate());
                }
            }
        }, date.getYear(), date.getMonth(), date.getDay());

        datePickerDialog.getDatePicker().setMinDate(date.getTime());
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        datePickerDialog.setCustomTitle(linearLayout);
        datePickerDialog.show();
    }

    private void showGroupListDialog(final ArrayList adapterData) {
        LayoutRecyclerviewPopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_recyclerview_popup, null, false);
        mGroupDialog = new Dialog(AssignActivity.this);
        mGroupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGroupDialog.setContentView(binding.getRoot());
        mGroupDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mGroupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77000000")));
        binding.textViewTitle.setVisibility(View.VISIBLE);
        binding.textViewTitle.setText(getString(R.string.select_group));
        binding.listviewEdittextData.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        final GroupAdapter arrayAdapter = new GroupAdapter(getBaseContext(), adapterData, mGroupDialog);
        binding.listviewEdittextData.setAdapter(arrayAdapter);

        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Double width = metrics.widthPixels * 0.80;
        Window win = mGroupDialog.getWindow();
        win.setLayout(width.intValue(), ViewGroup.LayoutParams.WRAP_CONTENT);
        mGroupDialog.show();

    }

    /**
     * click action for assign button
     */
    private void assignButtonClickAction() {
        if (mAssignType.equalsIgnoreCase(TYPE_QUIZ)) {
            if (mBinding.textViewAssignTitle.getText().toString().trim().isEmpty()) {
                showMessage(getString(R.string.error_assignment_title));
            } else if (mBinding.textViewSelectedGroup.getText().toString().equals(getString(R.string.label_select_group))) {
                showMessage(getString(R.string.error_assignment_group));
            } else if (mBinding.textViewSelectedDueDate.getText().toString().equals(getString(R.string.label_select_due_date))) {
                showMessage(getString(R.string.error_assignment_due_date));
            } else if (mBinding.textViewMarks.getText().toString().equals(getString(R.string.set_maximum_marks))) {
                showMessage(getString(R.string.error_assignment_max_marks));
            } else if (mBinding.textViewDuration.getText().toString().equals(getString(R.string.set_duration))) {
                showMessage(getString(R.string.error_assignment_duration));
            } else {
                showAssignConfirmationDialog(TYPE_QUIZ);

            }


        } else if (mAssignType.equalsIgnoreCase(TYPE_COURSE)) {

            if (mBinding.textViewAssignTitle.getText().toString().trim().isEmpty()) {
                showMessage(getString(R.string.error_assignment_title));
            } else if (mBinding.textViewSelectedGroup.getText().toString().equals(getString(R.string.label_select_group))) {
                showMessage(getString(R.string.error_assignment_group));
            } else if (mBinding.textViewSelectedDueDate.getText().toString().equals(getString(R.string.label_select_due_date))) {
                showMessage(getString(R.string.error_assignment_due_date));
            } else {
                showAssignConfirmationDialog(TYPE_COURSE);

            }


        } else {

            if (mBinding.textViewAssignTitle.getText().toString().trim().isEmpty()) {
                showMessage(getString(R.string.error_assignment_title));
            } else if (mBinding.textViewSelectedGroup.getText().toString().equals(getString(R.string.label_select_group))) {
                showMessage(getString(R.string.error_assignment_group));
            } else if (mBinding.textViewSelectedDueDate.getText().toString().equals(getString(R.string.label_select_due_date))) {
                showMessage(getString(R.string.error_assignment_due_date));
            } else {
                showAssignConfirmationDialog(TYPE_RESOURCE);
            }
        }
    }

    private void showMessage(String string) {
        SnackBarUtils.showColoredSnackBar(getBaseContext(), getCurrentFocus(), string, ContextCompat.getColor(getBaseContext(), R.color.colorRed));
    }

    /**
     * synchronous save assignment in database
     */
    private void saveAssignment() {
        mAssignResourceActivityModel.saveAssignmentSync(mAssignment);

    }

    /**
     * get assignment object updated with ui data if quiz
     */
    private void getQuizToAssign() {
        mBinding.editTextInstruction.clearComposingText();
        mAssignment.setTitle(mBinding.textViewAssignTitle.getText().toString().trim());
        mAssignment.setInstructions(Html.toHtml(mBinding.editTextInstruction.getEditableText()).toString());
        mAssignment.setAssignedDateTime(DateUtils.getCurrentISO8601DateString());
        mAssignment.setAssignmentType(mAssignResourceActivityModel.getAssignmentType(mQuiz.getQuizType()));
        if (mQuiz != null && mQuiz.getQuizType().equals(QuizTypeEnum.OBJECTIVE.toString())) {
            mAssignment.setTotalScore(NumberUtils.getIntFromTextView(mBinding.textViewMarks));
        }
        if (mAssignment.getToBeScored()) {
            mAssignment.setTotalScore(NumberUtils.getIntFromTextView(mBinding.textViewMarks));
            mAssignment.setRubric(getRubricArray());
        }
        mAssignment.setTimed(true);
        mAssignment.setAllowedTimeInSeconds(DateUtils.getSecondsFromMinutes(NumberUtils.getLongFromTextView(mBinding.textViewDuration)));
        mAssignment.setAssignedBy(mAssignResourceActivityModel.getAssignedBy());
        mAssignment.setStage(AssignmentStage.STAGE_ASSIGNED.getAssignmentStage());
        mAssignment.setObjectId(null);
        mAssignment.setAlias(GeneralUtils.generateAlias("Assignment", AppPrefs.getUserId(AssignActivity.this), "" + System.currentTimeMillis()));
        saveAssignment();
        mQuiz.setStatus(4);
        mAssignResourceActivityModel.updateQuizWithStatus(mQuiz);
        mAssignResourceActivityModel.deleteQuizMinimal(mQuiz.getAlias());
        mRxBus.send(new QuizMinimalDeleteEvent(mQuiz.getAlias()));
        ToastUtils.showToastSuccess(getApplicationContext(), getTitle().toString() + " " + getString(R.string.string_assigned_success));
        finish();
    }

    /**
     * get assignment object updated with ui data if course
     */
    private void getCourseToAssign() {
        if (!TextUtils.isEmpty(mCourseAssignmentType)) {
            mBinding.editTextInstruction.clearComposingText();
            mAssignment.setTitle(mBinding.textViewAssignTitle.getText().toString().trim());
            mAssignment.setInstructions(Html.toHtml(mBinding.editTextInstruction.getEditableText()).toString());
            mAssignment.setAssignedDateTime(DateUtils.getCurrentISO8601DateString());
            mAssignment.setTimed(false);
            mAssignment.setAllowedTimeInSeconds(0);
            mAssignment.setTotalScore(0);
            mAssignment.setAssignmentType(mAssignResourceActivityModel.getAssignmentType(mCourseAssignmentType));
            mAssignment.setAssignedBy(mAssignResourceActivityModel.getAssignedBy());
            mAssignment.setStage(AssignmentStage.STAGE_ASSIGNED.getAssignmentStage());
            mAssignment.setObjectId(null);
            mAssignment.setAlias(GeneralUtils.generateAlias("Assignment", AppPrefs.getUserId(AssignActivity.this), "" + System.currentTimeMillis()));
            saveAssignment();
            ToastUtils.showToastSuccess(getApplicationContext(), getTitle().toString() + " " + getString(R.string.string_assigned_success));
        } else {
            ToastUtils.showToastSuccess(getApplicationContext(), getTitle().toString() + " " + getString(R.string.string_assigned_failure));
        }
        finish();
    }

    /**
     * get assignment object updated with ui data if course
     */
    private void getResourceToAssign() {
        mBinding.editTextInstruction.clearComposingText();
        mAssignment.setTitle(mBinding.textViewAssignTitle.getText().toString().trim());
        mAssignment.setInstructions(Html.toHtml(mBinding.editTextInstruction.getEditableText()).toString());
        mAssignment.setAssignedDateTime(DateUtils.getCurrentISO8601DateString());
        mAssignment.setTimed(false);
        mAssignment.setAllowedTimeInSeconds(0);
        mAssignment.setTotalScore(0);
        mAssignment.setAssignmentType(AssignmentType.TYPE_RESOURCE.getAssignmentType());
        mAssignment.setResourceType(mResourceType);
        mAssignment.setAssignedBy(mAssignResourceActivityModel.getAssignedBy());
        mAssignment.setStage(AssignmentStage.STAGE_ASSIGNED.getAssignmentStage());
        mAssignment.setObjectId(null);
        mAssignment.setAlias(GeneralUtils.generateAlias("Assignment", AppPrefs.getUserId(AssignActivity.this), "" + System.currentTimeMillis()));
        saveAssignment();
        ToastUtils.showToastSuccess(getApplicationContext(), getTitle().toString() + " " + getString(R.string.string_assigned_success));
        finish();
    }

    /**
     * show error toast
     *
     * @param message
     */
    private void showErrorMessage(String message) {
        DialogUtils.showAlertDialog(AssignActivity.this, message, getString(R.string.alert_title_string), false);
    }

    public void setupSubscription() {

    }

    /**
     * get rubric array list from rubric data from ui
     *
     * @return rubric list
     */
    public ArrayList<Rubric> getRubricArray() {

        Rubric rubric1 = new Rubric(RubricTitle.RUBRIC_ESSAY_FORMAT.getRubricTitle(), Integer.valueOf(mRubricViews.get(0).getText().toString()));
        Rubric rubric2 = new Rubric(RubricTitle.RUBRIC_GRAMMAR.getRubricTitle(), Integer.valueOf(mRubricViews.get(1).getText().toString()));
        Rubric rubric4 = new Rubric(RubricTitle.RUBRIC_CREATIVITY.getRubricTitle(), Integer.valueOf(mRubricViews.get(2).getText().toString()));
        Rubric rubric3 = new Rubric(RubricTitle.RUBRIC_LANGUAGE.getRubricTitle(), Integer.valueOf(mRubricViews.get(3).getText().toString()));
        Rubric rubric5 = new Rubric(RubricTitle.RUBRIC_SENTENCE.getRubricTitle(), Integer.valueOf(mRubricViews.get(4).getText().toString()));

        ArrayList<Rubric> rubrics = new ArrayList<>();
        rubrics.add(rubric1);
        rubrics.add(rubric2);
        rubrics.add(rubric3);
        rubrics.add(rubric4);
        rubrics.add(rubric5);
        return rubrics;
    }

    /**
     * calculate total of the values of the rubric weightage
     *
     * @return sum
     */
    private int calculateTotalOfRubrics() {
        int sum = 0;
        for (TextView textView :
                mRubricViews) {
            sum += Integer.valueOf(textView.getText().toString());
        }
        return sum;
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
        private Dialog mGroupListDialog;
        private ArrayList<GroupAbstract> groupList = new ArrayList<>();
        private Context mContext;

        public GroupAdapter(Context context, ArrayList adapterData, Dialog dialog) {
            this.mContext = context;
            this.mGroupListDialog = dialog;
            this.groupList = adapterData;
        }

        @Override
        public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_metadata_spinner_item, parent, false);
            GroupAdapter.ViewHolder viewHolder = new GroupAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final GroupAdapter.ViewHolder holder, final int position) {
            final GroupAbstract group = groupList.get(position);
            holder.mItemTextView.setText(group.getName());
            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAssignment.getAssignedGroups().clear();
                    mAssignment.getAssignedGroups().add(new AssignedGroup(group.getObjectId(), group.getName()));
                    SpannableString title = new SpannableString(getString(R.string.selected_group) + " - " + holder.mItemTextView.getText().toString());
                    title.setSpan(new TypefaceSpan("sans-serif-condensed"), 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    mBinding.textViewSelectedGroup.setText(title);
                    mBinding.textViewSelectedGroup.setTag(view.getTag());
                    mGroupListDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mItemTextView;
            private View mRootView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mItemTextView = (TextView) itemView.findViewById(R.id.textview_quiz_metadata_value);
            }
        }
    }

    public static class FavouriteResourceExt implements Serializable {
        final String mObjectId;
        final String mVideoTitle;
        final String mVideoType;
        final String mUrlMain;
        final String mUrlThumbnail;
        final double mVideoDuration;
        final String mVideoId;
        final MetaInformation mMetaInformation;

        public FavouriteResourceExt(String objectId, String title, String type, MetaInformation metaInformation, String urlMain, String urlThumbnail, double duration, String videoId) {
            mObjectId = objectId;
            mVideoTitle = title;
            mVideoType = type;
            this.mMetaInformation = metaInformation;
            mUrlMain = urlMain;
            mUrlThumbnail = urlThumbnail;
            mVideoDuration = duration;
            mVideoId = videoId;
        }

        public String getObjectId() {
            return mObjectId;
        }

        public String getVideoTitle() {
            return mVideoTitle;
        }

        public String getVideoType() {
            return mVideoType;
        }

        public MetaInformation getMetaInformation() {
            return mMetaInformation;
        }

        public String getUrlMain() {
            return mUrlMain;
        }

        public String getUrlThumbnail() {
            return mUrlThumbnail;
        }

        public double getVideoDuration() {
            return mVideoDuration;
        }

        public String getVideoId() {
            return mVideoId;
        }
    }

    public static class CourseExt implements Serializable {
        final String mCourseId;
        final String mCourseTitle;
        final String mCourseType;
        final String mMicroCourseType;
        final Thumbnail mCourseThumbnail;
        final MetaInformation mMetaInformation;

        public CourseExt(String mCourseId, String mCourseTitle, String mCourseType, MetaInformation metaInformation, String mMicroCourseType, Thumbnail mCourseThumbnail) {
            this.mCourseId = mCourseId;
            this.mCourseTitle = mCourseTitle;
            this.mCourseType = mCourseType;
            this.mMetaInformation = metaInformation;
            this.mMicroCourseType = mMicroCourseType;
            this.mCourseThumbnail = mCourseThumbnail;
        }

        public MetaInformation getMetaInformation() {
            return mMetaInformation;
        }

        public String getMicroCourseType() {
            return mMicroCourseType;
        }

        public String getCourseId() {
            return mCourseId;
        }

        public String getCourseTitle() {
            return mCourseTitle;
        }

        public String getCourseType() {
            return mCourseType;
        }

        public Thumbnail getCourseThumbnail() {
            return mCourseThumbnail;
        }

    }
}

