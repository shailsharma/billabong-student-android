package in.securelearning.lil.android.assignments.views.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutPickCaptureImageBinding;
import in.securelearning.lil.android.assignments.model.MetaDataScreenModel;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.QuizTypeEnum;
import in.securelearning.lil.android.base.dataobjects.Board;
import in.securelearning.lil.android.base.dataobjects.Category;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.Language;
import in.securelearning.lil.android.base.dataobjects.LearningLevel;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.QuizType;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.dataobjects.SubjectGroup;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.views.widget.AndroidPermissions;
import in.securelearning.lil.android.quizcreator.views.activity.QuizCreatorActivity;
import in.securelearning.lil.android.syncadapter.dataobject.FileChooser;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;

import static in.securelearning.lil.android.base.utils.FileUtils.copyFiles;

/**
 * Created by Cp on 12/20/2016.
 */
public class QuizMetaDataActivity extends AppCompatActivity {

    private static final int IMAGE_PICK = 104;
    private final static int IMAGE_CAPTURE = 105;
    public static final String THUMB_FILE_DIRECTORY = File.separator + "tempQuizThumb";
    public static final String THUMB_FILE_PATH = File.separator + "tempQuizThumb" + File.separator + "tempThumb";
    public static Activity mQuizMetaDataActivity;
    private HashMap<String, GradeExt> mQuizMetaData = new HashMap<String, GradeExt>();
    ArrayList<GradeExt> values = new ArrayList<>();
    private GradeExt mGradeExt;
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    MetaDataScreenModel mMetaDataModel;
    private String mBaseFolder;
    private String mPicturePath;
    private String mQuizThumbnailImageFolder;
    private FloatingActionButton mNextButton;
    private EditText mTitleEditText, mTypeEditText, mBoardEditText, mLearningLevelEditText, mLanguageEditText, mGradeEditText, mSubjectEditText, mTopicEditText;
    private TextInputLayout mTitleLayout, mTypeLayout, mLearningLevelLayout, mBoardLayout, mLanguageLayout, mGradeLayout, mSubjectLayout, mTopicLayout;
    private ImageView mQuizThumbnailImageView;
    private LinearLayout mBrowseImageButton;
    private PopupWindow popupEditbox;
    //private CardView mThumbnailCardView;
    private Toolbar mToolbar;
    private Toast mToast;
    private String strQuizType = "", strQuizSubject = "", strQuizTitle = "", strQuizBoard = "", strLearningLevel = "", strQuizLanguage = "", strQuizGrade = "", strQuizTopic = "";
    private ArrayList<TopicExt> topicArrayList = new ArrayList<>();
    private TopicExt mTopicExt;
    private int mPrimaryColor;
    private String mThumbnailLocalPath;

    @Override
    public void onBackPressed() {
        if (mTitleEditText.getText().toString().isEmpty() &&
                mTypeEditText.getText().toString().isEmpty() &&
                mBoardEditText.getText().toString().isEmpty() &&
                mLearningLevelEditText.getText().toString().isEmpty() &&
                mLanguageEditText.getText().toString().isEmpty() &&
                mGradeEditText.getText().toString().isEmpty() &&
                mSubjectEditText.getText().toString().isEmpty() &&
                mTopicEditText.getText().toString().isEmpty()) {

            finish();
        } else {
            new AlertDialog.Builder(QuizMetaDataActivity.this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_teacher_quiz_metadata);
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mBaseFolder = getFilesDir().getAbsolutePath();
        mPrimaryColor = ContextCompat.getColor(QuizMetaDataActivity.this, R.color.colorPrimary);
        mQuizMetaDataActivity = this;
        mQuizThumbnailImageFolder = getString(R.string.quiz);
        // initializeResourceFolders(getString(R.string.quiz));
        initializeViews();
        setUpToolbar();
        initializeUiAndClickListeners();
        fetchDataForMetaScreen();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Uri cameraUri = data.getData();
                startCropping(cameraUri);
            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();

            }
        } else if (data != null && requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            startCropping(imageUri);

        } else if (data != null && resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {

                try {

                    String picturePathOriginal = resultUri.getPath();
                    String strPath = copyFiles(picturePathOriginal, mBaseFolder, mQuizThumbnailImageFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mThumbnailLocalPath = "file://" + mBaseFolder + File.separator + strPath;
                    //mThumbnailCardView.setVisibility(View.VISIBLE);
                    Picasso.with(getBaseContext()).load(mThumbnailLocalPath).resize(1280, 720).centerInside().into(mQuizThumbnailImageView);
                    mQuizThumbnailImageView.setTag(mThumbnailLocalPath);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeViews() {
        mTitleEditText = (EditText) findViewById(R.id.editText_quiz_title);
        mTypeEditText = (EditText) findViewById(R.id.edittext_type);
        mBoardEditText = (EditText) findViewById(R.id.edittext_board);
        mLearningLevelEditText = (EditText) findViewById(R.id.edittext_learning_level);
        mLanguageEditText = (EditText) findViewById(R.id.edittext_language);
        mGradeEditText = (EditText) findViewById(R.id.edittext_grade);
        mSubjectEditText = (EditText) findViewById(R.id.edittext_subject);
        mTopicEditText = (EditText) findViewById(R.id.edittext_topic);
        mTitleLayout = (TextInputLayout) findViewById(R.id.input_layout_title);
        mTypeLayout = (TextInputLayout) findViewById(R.id.input_layout_type);
        mLearningLevelLayout = (TextInputLayout) findViewById(R.id.input_layout_learning_level);
        mBoardLayout = (TextInputLayout) findViewById(R.id.input_layout_board);
        mLanguageLayout = (TextInputLayout) findViewById(R.id.input_layout_language);
        mGradeLayout = (TextInputLayout) findViewById(R.id.input_layout_grade);
        mSubjectLayout = (TextInputLayout) findViewById(R.id.input_layout_subject);
        mTopicLayout = (TextInputLayout) findViewById(R.id.input_layout_topic);
        mQuizThumbnailImageView = (ImageView) findViewById(R.id.imageview_quiz_thumbnail);
        mBrowseImageButton = (LinearLayout) findViewById(R.id.layout_browse_thumbnail);
        mNextButton = (FloatingActionButton) findViewById(R.id.button_next);
        //mThumbnailCardView = (CardView) findViewById(R.id.cardview_thumbnail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(mPrimaryColor);
        setTitle(getString(R.string.string_new));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeUiAndClickListeners() {


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateTitle()) {
                    return;
                }
                if (!validateType()) {
                    return;
                }
                if (!validateLearningLevel()) {
                    return;
                }
//                if (!validateBoard()) {
//                    return;
//                }
//                if (!validateLanguage()) {
//                    return;
//                }
//                if (!validateGrade()) {
//                    return;
//                }
                if (!validateSubject()) {
                    return;
                }
                if (!validateTopic()) {
                    return;
                }
                if (mQuizThumbnailImageView.getTag() == getResources().getString(R.string.string_AddThumbnailImage)) {
                    showToast(getString(R.string.error_thumbnail));
                } else {
                    Quiz mQuiz = new Quiz();
                    MetaInformation metaInformation = new MetaInformation();
                    Thumbnail mThumbnail = new Thumbnail();

                    //set values to data objects
                    mQuiz.setTitle(strQuizTitle);
                    if (strQuizType.equalsIgnoreCase(QuizTypeEnum.OBJECTIVE.toString())) {
                        mQuiz.setQuizType(QuizTypeEnum.OBJECTIVE.toString());
                    } else {
                        mQuiz.setQuizType(QuizTypeEnum.SUBJECTIVE.toString());
                    }


                    final TopicExt topicExt = mTopicExt;
                    metaInformation.setBoard(topicExt.getBoard());
                    metaInformation.setLanguage(topicExt.getLang());
                    metaInformation.setSubject(topicExt.getSubject());
                    metaInformation.setGrade(topicExt.getGrade());
                    metaInformation.setTopic(topicExt);
                    metaInformation.setLearningLevel(topicExt.getLearningLevel());

                    mThumbnail.setLocalUrl(mThumbnailLocalPath);
                    mQuiz.setThumbnail(mThumbnail);

                    mQuiz.setAlias(GeneralUtils.generateAlias("quiz", "" + AppPrefs.getUserId(QuizMetaDataActivity.this), "" + System.currentTimeMillis()));
                    mQuiz.setMetaInformation(metaInformation);
                    startActivity(QuizCreatorActivity.getLaunchIntentForQuizNew(QuizMetaDataActivity.this, "", mQuiz));

                }
            }


        });

        mTitleEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mTitleEditText.setFocusableInTouchMode(true);

                return false;
            }
        });

        // TODO: 05-May-17 only Objective quiz for now
        ArrayList<QuizType> quizTypes = new ArrayList<QuizType>();
        QuizType quizType = new QuizType();
        quizType.setName(QuizTypeEnum.OBJECTIVE.toString());
        quizType.setId("0123456");
        quizTypes.add(quizType);
        mTypeEditText.setText(quizType.getName());

//        mTypeEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                ArrayList<QuizType> quizTypes = new ArrayList<QuizType>();
//
//                QuizType quizType = new QuizType();
//                quizType.setName(QuizTypeEnum.OBJECTIVE.toString());
//                quizType.setId("0123456");
//                quizTypes.add(quizType);
//
//                quizType = new QuizType();
//                quizType.setName(QuizTypeEnum.SUBJECTIVE.toString());
//                quizType.setId("1123456");
//                quizTypes.add(quizType);
//                hideSoftKeyboard();
//                if (mTitleEditText.getText().toString().trim().isEmpty()) {
//                    validateTitle();
//
//                } else {
//
//                    popupWindowEditText(quizTypes, mTypeEditText, mTitleLayout, MetaDataAdapter.SPINNER_TYPE_QUIZ);
//                }
//
//
//            }
//        });

//        mBoardEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideSoftKeyboard();
//                if (mTypeEditText.getText().toString().trim().isEmpty()) {
//                    validateType();
//                } else {
//                    popupWindowEditText(mQuizMetaData.boardArrayList, mBoardEditText, mTypeLayout, MetaDataAdapter.SPINNER_TYPE_BOARD);
//
//                }
//
//            }
//        });

        mLearningLevelEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();

                if (mTypeEditText.getText().toString().trim().isEmpty()) {
                    validateType();
                } else {
                    popupWindowEditText(values, mLearningLevelEditText, mTypeLayout, MetaDataAdapter.SPINNER_TYPE_LEVEL);

                }

            }
        });


//        mLanguageEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideSoftKeyboard();
//                if (mBoardEditText.getText().toString().trim().isEmpty()) {
//                    validateBoard();
//                } else {
//                    popupWindowEditText(mQuizMetaData.languageArrayList, mLanguageEditText, mBoardLayout, MetaDataAdapter.SPINNER_TYPE_LANGUAGE);
//                }
//
//            }
//        });

//        mGradeEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideSoftKeyboard();
//                if (mLanguageEditText.getText().toString().trim().isEmpty()) {
//                    validateLanguage();
//                } else {
//                    popupWindowEditText(mQuizMetaData.gradeArrayList, mGradeEditText, mLanguageLayout, MetaDataAdapter.SPINNER_TYPE_GRADE);
//                }
//
//            }
//        });

        mSubjectEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard();
                if (mLearningLevelEditText.getText().toString().trim().isEmpty()) {
                    validateLearningLevel();
                } else {
                    if (mGradeExt != null)
                        popupWindowEditText(new ArrayList(mGradeExt.getSubjectExtHashMap().values()), mSubjectEditText, mLearningLevelLayout, MetaDataAdapter.SPINNER_TYPE_SUBJECT);
                }

            }
        });

        mTopicEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                if (mSubjectEditText.getText().toString().trim().isEmpty()) {
                    validateSubject();
                } else {
                    popupWindowEditText(topicArrayList, mTopicEditText, mSubjectLayout, MetaDataAdapter.SPINNER_TYPE_TOPIC);
                }

            }
        });

        mBrowseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    showImagePickOptionDialog();
                } else {
                    actionGalleryClick();
                }
            }
        });


        mQuizThumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  UserProfileActivity.showFullImage(mThumbnailLocalPath, QuizMetaDataActivity.this);
            }
        });

    }

    private boolean validateTitle() {
        strQuizTitle = mTitleEditText.getText().toString().trim();
        if (strQuizTitle.isEmpty()) {
            mTitleLayout.setError(getString(R.string.error_title));
            requestFocus(mTitleEditText);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mTitleEditText, InputMethodManager.SHOW_IMPLICIT);
            return false;
        } else {
            mTitleEditText.clearFocus();
            mTitleLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateType() {
        strQuizType = mTypeEditText.getText().toString().trim();
        if (strQuizType.isEmpty()) {
            mTypeLayout.setError(getString(R.string.error_type));
            return false;
        } else {
            mTypeEditText.clearFocus();
            mTypeLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateBoard() {
        strQuizBoard = mBoardEditText.getText().toString().trim();
        if (strQuizBoard.isEmpty()) {
            mBoardLayout.setError(getString(R.string.error_board));
            return false;
        } else {
            mBoardEditText.clearFocus();
            mBoardLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLearningLevel() {
        strLearningLevel = mLearningLevelEditText.getText().toString().trim();
        if (strLearningLevel.isEmpty()) {
            mLearningLevelLayout.setError(getString(R.string.error_level));
            return false;
        } else {
            mLearningLevelEditText.clearFocus();
            mLearningLevelLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLanguage() {
        strQuizLanguage = mLanguageEditText.getText().toString().trim();
        if (strQuizLanguage.isEmpty()) {
            mLanguageLayout.setError(getString(R.string.error_language));
            return false;
        } else {
            mLanguageEditText.clearFocus();
            mLanguageLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateGrade() {
        strQuizGrade = mGradeEditText.getText().toString().trim();
        if (strQuizGrade.isEmpty()) {
            mGradeLayout.setError(getString(R.string.error_grade));
            return false;
        } else {
            mGradeEditText.clearFocus();
            mGradeLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateSubject() {
        strQuizSubject = mSubjectEditText.getText().toString().trim();
        if (strQuizSubject.isEmpty()) {
            mSubjectLayout.setError(getString(R.string.error_subject));
            return false;
        } else {
            mSubjectEditText.clearFocus();
            mSubjectLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateTopic() {
        strQuizTopic = mTopicEditText.getText().toString().trim();
        if (strQuizTopic.isEmpty()) {
            mTopicLayout.setError(getString(R.string.error_topic));
            return false;
        } else {
            mTopicEditText.clearFocus();
            mTopicLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * requesting keyboard for focused edittext
     *
     * @param view
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * show image pick option dialog
     */
    private void showImagePickOptionDialog() {

        final Dialog dialog = new Dialog(QuizMetaDataActivity.this);
        final LayoutPickCaptureImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_pick_capture_image, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorDialogBackground)));

        AnimationUtils.fadeInFast(getBaseContext(), binding.layoutCamera);
        AnimationUtils.fadeInFast(getBaseContext(), binding.layoutGallery);

        binding.layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCameraClick();
                dialog.dismiss();
            }
        });

        binding.layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionGalleryClick();
                dialog.dismiss();

            }
        });

        binding.layoutMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                dialog.dismiss();
                return false;
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void actionCameraClick() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                PackageManager pm = getPackageManager();
                int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                    AndroidPermissions.checkCameraPermission(QuizMetaDataActivity.this);
                } else {
                    createCameraDirectory();
                }
            } else {
                createCameraDirectory();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionGalleryClick() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                PackageManager pm = getPackageManager();
                int hasPerm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                    AndroidPermissions.checkStoragePermission(QuizMetaDataActivity.this);
                } else {
                    startImagePick();
                }
            } else {
                startImagePick();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * intent action to pick image from gallery
     */
    private void startImagePick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK);
    }

    /**
     * create camera directory for capturing image for user profile
     */
    private void createCameraDirectory() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            File tempFile = new File(mBaseFolder + THUMB_FILE_DIRECTORY);
            if (!tempFile.exists()) {
                tempFile.getParentFile().mkdirs();
            }
            startImageCapture(tempFile);
        } else {
            SnackBarUtils.showColoredSnackBar(getBaseContext(), getCurrentFocus(), getString(R.string.error_no_camera), mPrimaryColor);
        }

    }

    /**
     * set up and start camera for taking picture
     *
     * @param saveFolder
     */
    private void startImageCapture(File saveFolder) {
        new MaterialCamera(this)
                .allowRetry(true)
                .autoSubmit(false)
                .saveDir(saveFolder)
                .primaryColor(mPrimaryColor)
                .showPortraitWarning(true)
                .defaultToFrontFacing(false)
                .retryExits(false)
                .videoPreferredAspect(16f / 9f)
                .maxAllowedFileSize(1024 * 1024 * 5)
                .iconRecord(R.drawable.mcam_action_capture)
                .iconFrontCamera(R.drawable.mcam_camera_front)
                .iconRearCamera(R.drawable.mcam_camera_rear)
                .labelRetry(R.string.mcam_retry)
                .labelConfirm(R.string.label_edit_photo)
                .stillShot()
                .start(IMAGE_CAPTURE);
    }

    /**
     * crop image which was picked from gallery or captured by camera
     *
     * @param uri
     */
    private void startCropping(Uri uri) {
        if (FileChooser.fileSize(getBaseContext(), uri) < 10) {
            File tempFile = new File(mBaseFolder + THUMB_FILE_PATH);
            if (!tempFile.exists()) {
                tempFile.getParentFile().mkdirs();
            }
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(mPrimaryColor);
            options.setStatusBarColor(mPrimaryColor);
            Uri destinationUri = Uri.fromFile(tempFile);
            UCrop.of(uri, destinationUri)
                    .withAspectRatio(16, 9)
                    .withMaxResultSize(1280, 720)
                    .withOptions(options)
                    .start(this);

        } else {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit));
        }
    }

    public void showToast(String st) { //"Toast toast" is declared in the class
        try {
            mToast.getView().isShown();     // true if visible
            mToast.setText(st);
        } catch (Exception e) {         // invisible if exception
            mToast = Toast.makeText(QuizMetaDataActivity.this, st, Toast.LENGTH_LONG);
        }
        mToast.show();  //finally display it
    }

    private void fetchDataForMetaScreen() {

        this.mQuizMetaData = mMetaDataModel.getData();
        ArrayList<String> keys = new ArrayList<>(mQuizMetaData.keySet());
        values = new ArrayList<>();
        Collections.sort(keys);
        for (String s :
                keys) {
            values.add(mQuizMetaData.get(s));
        }
//        Observable.just(null).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) {
//                Log.e(TAG, "Fetch Boards List From DB");
//                boardArrayList = mMetaDataModel.fetchBoardsListFromDbSync();
//            }
//        });
//        Observable.just(null).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) {
//                Log.e(TAG, "Fetch Language List From DB");
//                languageArrayList = mMetaDataModel.fetchLangagesListFromDbSync();
//
//            }
//        });
//        Observable.just(null).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) {
//                Log.e(TAG, "Fetch Grade List From DB");
//                gradeArrayList = mMetaDataModel.fetchGradesListFromDbSync();
//            }
//        });
//        Observable.just(null).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) {
//                Log.e(TAG, "Fetch Subject List From DB");
//                subjectArrayList = mMetaDataModel.fetchSubjectsListFromDbSync();
//
//            }
//        });

    }

    /**
     * Pop up window for required fields like type, board,icon_language_c,subject,topic and grade.
     *
     * @param adapterData
     * @param mEditText
     * @param mTextInputLayout
     * @param editTextType
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void popupWindowEditText(final ArrayList adapterData, final EditText mEditText, final TextInputLayout mTextInputLayout, final int editTextType) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_recyclerview_popup, null);
        RecyclerView mEditTextDataListView = (RecyclerView) layout.findViewById(R.id.listview_edittext_data);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(QuizMetaDataActivity.this);
        mEditTextDataListView.setLayoutManager(layoutManager);

        final MetaDataAdapter arrayAdapter = new MetaDataAdapter(QuizMetaDataActivity.this, adapterData, editTextType);
        mEditTextDataListView.setAdapter(arrayAdapter);

        int popupWidth = mEditText.getWidth() - 20;
        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        popupEditbox = new PopupWindow(mEditText.getContext());
        popupEditbox.setContentView(layout);
        popupEditbox.setWidth(popupWidth);
        popupEditbox.setHeight(popupHeight);
        popupEditbox.setFocusable(true);

        int OFFSET_X = 10;
        int OFFSET_Y = mEditText.getHeight();

        popupEditbox.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupEditbox.setOutsideTouchable(true);
        popupEditbox.setElevation(10f);
        popupEditbox.setAnimationStyle(android.R.style.Animation_Dialog);

        int[] location = new int[2];
        mEditText.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        popupEditbox.showAtLocation(layout, Gravity.NO_GRAVITY, point.x + OFFSET_X, point.y + OFFSET_Y);

        arrayAdapter.setItemclickAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupEditbox.dismiss();

                if (editTextType == MetaDataAdapter.SPINNER_TYPE_QUIZ) {
                    if (!mTypeEditText.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.setTag(v.getTag());
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mBoardEditText.setText("");
                        mLanguageEditText.setText("");
                        mGradeEditText.setText("");
                        mSubjectEditText.setText("");
                        mTopicEditText.setText("");
                    }

                } else if (editTextType == MetaDataAdapter.SPINNER_TYPE_LEVEL) {
                    mEditText.setTag(v.getTag());
//                    topicArrayList = ((LearningLevelExt) mLearningLevelEditText.getTag()).getTopics();
                    mGradeExt = (GradeExt) mLearningLevelEditText.getTag();

                    if (!mLearningLevelEditText.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mBoardEditText.setText("");
                        mLanguageEditText.setText("");
                        mGradeEditText.setText("");
                        mSubjectEditText.setText("");
                        mTopicEditText.setText("");
                    }

                } else if (editTextType == MetaDataAdapter.SPINNER_TYPE_BOARD) {
                    if (!mBoardEditText.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.setTag(v.getTag());
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mLanguageEditText.setText("");
                        mGradeEditText.setText("");
                        mSubjectEditText.setText("");
                        mTopicEditText.setText("");
                    }

                } else if (editTextType == MetaDataAdapter.SPINNER_TYPE_LANGUAGE) {
                    if (!mLanguageEditText.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.setTag(v.getTag());
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mGradeEditText.setText("");
                        mSubjectEditText.setText("");
                        mTopicEditText.setText("");
                    }

                } else if (editTextType == MetaDataAdapter.SPINNER_TYPE_GRADE) {
                    if (!mGradeEditText.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.setTag(v.getTag());
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mSubjectEditText.setText("");
                        mTopicEditText.setText("");
                    }

                } else if (editTextType == MetaDataAdapter.SPINNER_TYPE_SUBJECT) {
                    mEditText.setTag(v.getTag());
                    topicArrayList = ((SubjectExt) mSubjectEditText.getTag()).getTopicExts();
                    if (!mSubjectEditText.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mTopicEditText.setText("");
                    }

                } else if (editTextType == MetaDataAdapter.SPINNER_TYPE_TOPIC) {
                    mEditText.setTag(v.getTag());
                    mTopicExt = (TopicExt) v.getTag();
                    mEditText.setText(((TextView) v).getText().toString());
                    mEditText.clearFocus();
                    mTextInputLayout.setErrorEnabled(false);
                }
            }
        });

    }

    /**
     * initialize the folders for resources
     *
     * @param parentFolderAbsolutePath
     */
    public void initializeResourceFolders(String parentFolderAbsolutePath) {
        mQuizThumbnailImageFolder = parentFolderAbsolutePath + File.separator + "images";
    }

    /**
     * hide keyboard
     */
    public void hideSoftKeyboard() {
        mTitleEditText.clearFocus();
        mTitleEditText.setFocusable(false);
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public class MetaDataAdapter extends RecyclerView.Adapter<MetaDataAdapter.ViewHolder> {

        public static final int SPINNER_TYPE_BOARD = 101;
        public static final int SPINNER_TYPE_LANGUAGE = 102;
        public static final int SPINNER_TYPE_GRADE = 103;
        public static final int SPINNER_TYPE_SUBJECT = 104;
        public static final int SPINNER_TYPE_TOPIC = 105;
        public static final int SPINNER_TYPE_QUIZ = 106;
        public static final int SPINNER_TYPE_LEVEL = 107;
        ArrayList<Object> ArSubTopic = new ArrayList<Object>();
        Context mContext;
        View.OnClickListener mItemclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        private int spinnerType;


        public MetaDataAdapter(Context context, ArrayList<Object> alSubTopic, int spinnerType) {
            mContext = context;
            ArSubTopic = alSubTopic;
            this.spinnerType = spinnerType;
        }

        public void setItemclickAction(View.OnClickListener mItemclickListener) {
            this.mItemclickListener = mItemclickListener;
        }

        @Override
        public MetaDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_metadata_spinner_item, parent, false);

            MetaDataAdapter.ViewHolder vh = new MetaDataAdapter.ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MetaDataAdapter.ViewHolder holder, final int position) {
            holder.mItemValueTxt.setOnClickListener(mItemclickListener);

            switch (spinnerType) {

                case SPINNER_TYPE_BOARD:
                    Board board = (Board) ArSubTopic.get(position);
                    holder.mItemValueTxt.setText(board.getName());
                    holder.mItemValueTxt.setTag(board);
                    break;
                case SPINNER_TYPE_LANGUAGE:
                    Language language = (Language) ArSubTopic.get(position);
                    holder.mItemValueTxt.setText(language.getName());
                    holder.mItemValueTxt.setTag(language);
                    break;
                case SPINNER_TYPE_GRADE:
                    Grade grade = (Grade) ArSubTopic.get(position);
                    holder.mItemValueTxt.setText(grade.getName());
                    holder.mItemValueTxt.setTag(grade);
                    break;
                case SPINNER_TYPE_SUBJECT:
                    SubjectExt subject = (SubjectExt) ArSubTopic.get(position);
                    holder.mItemValueTxt.setText(subject.getName());
                    holder.mItemValueTxt.setTag(subject);
                    break;
                case SPINNER_TYPE_TOPIC:
                    TopicExt topic = (TopicExt) ArSubTopic.get(position);
                    holder.mItemValueTxt.setText(topic.getName());
                    holder.mItemValueTxt.setTag(topic);
                    break;
                case SPINNER_TYPE_QUIZ:
                    QuizType quizType = (QuizType) ArSubTopic.get(position);
                    holder.mItemValueTxt.setText(quizType.getName());
                    holder.mItemValueTxt.setTag(quizType);
                    break;
                case SPINNER_TYPE_LEVEL:
                    GradeExt learningLevel = (GradeExt) ArSubTopic.get(position);
                    holder.mItemValueTxt.setText(learningLevel.getName());
                    holder.mItemValueTxt.setTag(learningLevel);
                    break;
                default:
                    holder.mItemValueTxt.setText("");
                    break;
            }


        }

        @Override
        public int getItemCount() {
            return ArSubTopic.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private TextView mItemValueTxt;

            public ViewHolder(View v) {
                super(v);
                mItemValueTxt = (TextView) v.findViewById(R.id.textview_quiz_metadata_value);

            }

        }
    }

    public static class QuizMetaData {
        private ArrayList<Board> boardArrayList = new ArrayList<>();
        private ArrayList<LearningLevelExt> learningLevelArrayList = new ArrayList<>();
        private ArrayList<Language> languageArrayList = new ArrayList<>();
        private ArrayList<Grade> gradeArrayList = new ArrayList<>();
//        private ArrayList<Subject> subjectArrayList = new ArrayList<>();

        public ArrayList<Board> getBoardArrayList() {
            return boardArrayList;
        }

        public ArrayList<LearningLevelExt> getLearningLevelArrayList() {
            return learningLevelArrayList;
        }

        public void setLearningLevelArrayList(ArrayList<LearningLevelExt> learningLevelArrayList) {
            this.learningLevelArrayList = learningLevelArrayList;
        }

        public void setBoardArrayList(ArrayList<Board> boardArrayList) {
            this.boardArrayList = boardArrayList;
        }

        public ArrayList<Grade> getGradeArrayList() {
            return gradeArrayList;
        }

        public void setGradeArrayList(ArrayList<Grade> gradeArrayList) {
            this.gradeArrayList = gradeArrayList;
        }

        public ArrayList<Language> getLanguageArrayList() {
            return languageArrayList;
        }

        public void setLanguageArrayList(ArrayList<Language> languageArrayList) {
            this.languageArrayList = languageArrayList;
        }

    }

    public static class SubjectExt extends Subject {

        public SubjectExt(Subject subject) {
            super(subject.getId(), subject.getName());
        }

        private ArrayList<TopicExt> topics = new ArrayList();

        public ArrayList<TopicExt> getTopicExts() {
            return topics;
        }

        public void setTopicExts(ArrayList<TopicExt> topics) {
            this.topics = topics;
        }
    }

    public static class TopicExt extends Topic {
        private Board mBoard = new Board();
        private Language mLang = new Language();
        private Grade mGrade = new Grade();
        private Subject mSubject = new Subject();
        private SubjectGroup mSubjectGroup = new SubjectGroup();

        public SubjectGroup getSubjectGroup() {
            return mSubjectGroup;
        }

        public void setSubjectGroup(SubjectGroup mSubjectGroup) {
            this.mSubjectGroup = mSubjectGroup;
        }

        public TopicExt(Topic topic) {
            super(topic.getId(), topic.getName());
            this.setSkills(topic.getSkills());
        }

        public Subject getSubject() {
            return mSubject;
        }

        public void setSubject(Subject subject) {
            mSubject = subject;
        }

        public Board getBoard() {
            return mBoard;
        }

        public void setBoard(Board board) {
            mBoard = board;
        }

        public Language getLang() {
            return mLang;
        }

        public void setLang(Language lang) {
            mLang = lang;
        }

        public Grade getGrade() {
            return mGrade;
        }

        public void setGrade(Grade grade) {
            mGrade = grade;
        }
    }

    public static class LearningLevelExt {

        private ArrayList<SubjectExt> mSubjects = new ArrayList<>();

        public ArrayList<SubjectExt> getSubjects() {
            return mSubjects;
        }

        private String id;
        private String name;
        private Category category = new Category();
        private ArrayList<TopicExt> topics = new ArrayList();

        public LearningLevelExt() {
        }

        public LearningLevelExt(LearningLevel learningLevel) {
            this.setId(learningLevel.getId());
            this.setName(learningLevel.getName());
            this.setCategory(learningLevel.getCategory());
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public ArrayList<TopicExt> getTopics() {
            return topics;
        }

        public void setTopics(ArrayList<TopicExt> topics) {
            this.topics = topics;
        }

        public void setSubjects(ArrayList<SubjectExt> subjects) {
            mSubjects = subjects;
        }
    }

    public static class GradeExt extends Grade {
        private HashMap<String, SubjectExt> subjectExtHashMap = new HashMap<>();

        public GradeExt(Grade grade) {
            this.setId(grade.getId());
            this.setName(grade.getName());
        }

        public HashMap<String, QuizMetaDataActivity.SubjectExt> getSubjectExtHashMap() {
            return subjectExtHashMap;
        }

        public void setSubjectExtHashMap(HashMap<String, QuizMetaDataActivity.SubjectExt> subjectExtHashMap) {
            this.subjectExtHashMap = subjectExtHashMap;
        }
    }


}
