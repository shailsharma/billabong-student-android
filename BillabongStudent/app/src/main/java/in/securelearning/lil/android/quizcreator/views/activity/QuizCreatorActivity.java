package in.securelearning.lil.android.quizcreator.views.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.views.activity.QuizMetaDataActivity;
import in.securelearning.lil.android.base.constants.QuizTypeEnum;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionExplanation;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.events.LoadQuizForEditEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.support.RadioButtonList;
import in.securelearning.lil.android.base.support.TabList;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.KeyBoardUtil;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.widget.ErrorToast;
import in.securelearning.lil.android.base.widget.ImageViewPager;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.learningnetwork.adapter.ViewPagerImageAdapter;
import in.securelearning.lil.android.quizcreator.adapter.QuestionListItemAdapter;
import in.securelearning.lil.android.quizcreator.adapter.SkillSpinnerAdapter;
import in.securelearning.lil.android.quizcreator.events.NewQuizCreationEvent;
import in.securelearning.lil.android.quizcreator.model.QuizCreatorModel;
import in.securelearning.lil.android.syncadapter.dataobject.FileChooser;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static in.securelearning.lil.android.base.dataobjects.Question.TYPE_DISPLAY_CHECKBOX;
import static in.securelearning.lil.android.base.dataobjects.Question.TYPE_DISPLAY_RADIO;
import static in.securelearning.lil.android.base.dataobjects.Question.TYPE_QUESTION_MCQ;
import static in.securelearning.lil.android.base.dataobjects.Question.TYPE_QUESTION_SUBJECTIVE;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR1;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR2;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR3;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR4;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR5;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR6;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR7;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR8;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_ERROR9;
import static in.securelearning.lil.android.base.model.QuizModel.VALIDATION_NO_ERROR;
import static in.securelearning.lil.android.base.utils.FileUtils.copyFiles;
import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Activity classs for Quiz Creator Function
 * implements QuizCreatorViewInterface
 * uses RxBus for Event based communication
 * uses QuizCreatorModel to access validations and database related functionality
 */
public class QuizCreatorActivity extends BaseActivityQuizCreator implements QuizCreatorViewInterface {
    public static final String QUIZ_DOCUMENT_ID = "docid";
    public static final String QUIZ_NEW_OBJ = "quizNewObj";

    @Inject
    RxBus mRxBus;

    @Inject
    QuizCreatorModel mQuizCreatorModel;
    @Inject
    QuizModel mQuizModel;


    @Inject
    AppUserModel mAppUserModel;

    Disposable mSubscription;
    RecyclerView mQuestionListRecyclerView;
    private boolean refresh = true;
    private String mQuizResourceVideoFolder;
    private String mQuizResourceImageFolder;
    private String mQuizResourceAudioFolder;
    private String mQuizResourceDocumentFolder;
    private String mBaseFolder;
    private Quiz mQuiz;
    private TabList mTabList;
    private RadioButtonList mRadioButtonList;
    private EditText mQuestionText;
    private EditText mExplanationText;
    private ImageView mExplanationImage;
    private ImageView mAttachmentResourceImage, mRemoveQuestionAttachment, mAttachmentResourceVideo;
    private ImageView mExplanationVideoImage;
    private RadioGroup mQuestionTypeRadioGroup;
    private RadioGroup mExplanationTypeRadioGroup;
    private ImageView mQuestionAttachmentImageView;
    private EditText mSkillsEdittext;
    private String mQuizDocumentID = "";
    private Toast mErrorToast;
    private TextView mTitleHeaderTextView, mTitleQuestionCountTextView;
    private RelativeLayout mQuestionLinearLayout, mQuizScrollViewLayout;
    private LinearLayout mQuestionLableLayout, mToolbarRightLayout;
    private CardView mQuestionAttachmentLayout;
    private CardView mExplanationImageLayout;
    private CardView mExplanationVideoLayout;
    private boolean mIsOpen = false;
    private boolean mIsChoiceTypeImage;
    private boolean mIsAttachmentImage;
    private String mExplanationResourceType = "";
    private QuestionListItemAdapter mQuestionListItemAdapter;
    private View.OnClickListener mAddHintClickListener;
    private ScrollView mLHScrollView;
    private ImageButton mBackButton, mAddNewQuestionButton;
    private long mLastClickTime = 0;
    private FloatingActionButton mSaveQuizButton, addQuestionButton, mUpdateQuestion;
    private Button mCancelQuestionUpdate;
    private RadioGroup mChoiceRadioGroup;
    private RadioButton mTextChoiceTypeButton, mImageChoiceTypeButton;
    private View addHintText, addHintImage, addHintVideo, questionImageView, questionImageViewVideo, mcqSingleCorrectRadioButton,
            mcqMultipleCorrectRadioButton, mSubjectiveRadioButton,
            explanationTextRadioButton, explanationResourceRadioButton, explanationVideoRadioButton, addChoiceItemButton;
    private Toast mToast;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    public static Intent getLaunchIntentForQuizEdit(Context context, String quizDocumentID) {
        Intent intent = new Intent(context, QuizCreatorActivity.class);
        intent.putExtra(QUIZ_DOCUMENT_ID, quizDocumentID);

        return intent;
    }

    public static Intent getLaunchIntentForQuizNew(Context context, String quizDocumentID, Quiz mQuiz) {
        Intent intent = new Intent(context, QuizCreatorActivity.class);
        intent.putExtra(QUIZ_DOCUMENT_ID, quizDocumentID);
        intent.putExtra(QUIZ_NEW_OBJ, mQuiz);

        return intent;
    }

    @Override
    public void onBackPressed() {
        if (mQuestionLinearLayout.getVisibility() == View.VISIBLE) {
            hideQuestionListLayout();

        } else {
            ArrayList<Question> data = mQuiz.getQuestions();
            if (mQuestionText.getText().toString().isEmpty() &&
                    mExplanationText.getText().toString().isEmpty() &&
                    mSkillsEdittext.getText().toString().isEmpty()
                    && data.size() == 0) {
                finish();
            } else {
                new AlertDialog.Builder(this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        setContentView(R.layout.activity_quiz_creator);
        getWindow().setStatusBarColor(ContextCompat.getColor(QuizCreatorActivity.this, R.color.colorQuizCreatorHeader));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mBaseFolder = getFilesDir().getAbsolutePath();

        initializeViews();
        initializeUIandClickListeners();

        Bundle bundle = getIntent().getExtras();
        mQuizDocumentID = "";
        if (bundle != null && !bundle.getString(QUIZ_DOCUMENT_ID).trim().isEmpty()) {
            mQuizDocumentID = bundle.getString(QUIZ_DOCUMENT_ID);
            mQuizCreatorModel.fetchQuiz(mQuizDocumentID);

        } else {
            Quiz quiz = (Quiz) bundle.getSerializable(QUIZ_NEW_OBJ);

            initializeQuiz(quiz);

        }


        this.setupSubscription();

        refresh = true;
    }

    /**
     * initialize views and their listeners
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initializeViews() {

        mQuizScrollViewLayout = (RelativeLayout) this.findViewById(R.id.layout_scrollView);
        mQuestionLableLayout = (LinearLayout) this.findViewById(R.id.layout_question_label);
        mToolbarRightLayout = (LinearLayout) this.findViewById(R.id.layout_toolbar_count_add);
        mLHScrollView = (ScrollView) this.findViewById(R.id.scrollview_lhs);
        mTitleQuestionCountTextView = (TextView) findViewById(R.id.textview_question_count);
        mBackButton = (ImageButton) this.findViewById(R.id.button_back);
        mAddNewQuestionButton = (ImageButton) this.findViewById(R.id.button_add_new_question);
        addQuestionButton = (FloatingActionButton) this.findViewById(R.id.add_question_button);
        mUpdateQuestion = (FloatingActionButton) this.findViewById(R.id.update_question_button);
        mSaveQuizButton = (FloatingActionButton) this.findViewById(R.id.button_save);
        mCancelQuestionUpdate = (Button) this.findViewById(R.id.cancel_question_update_button);
        addHintText = this.findViewById(R.id.button_add_hint_text);
        addHintImage = this.findViewById(R.id.button_add_hint_image);
        addHintVideo = this.findViewById(R.id.button_add_hint_video);
        questionImageView = this.findViewById(R.id.view_question_image);
        questionImageViewVideo = this.findViewById(R.id.view_question_video);
        mSkillsEdittext = (EditText) this.findViewById(R.id.edittext_skills);
        mChoiceRadioGroup = (RadioGroup) this.findViewById(R.id.radio_group_choice_type);
        mQuestionTypeRadioGroup = (RadioGroup) this.findViewById(R.id.radio_group_question_type);
        mQuestionAttachmentImageView = (ImageView) this.findViewById(R.id.image_view_question_attachment);
        mExplanationTypeRadioGroup = (RadioGroup) this.findViewById(R.id.radio_group_explanation);
        mcqSingleCorrectRadioButton = this.findViewById(R.id.radio_button_question_type_single_correct);
        mcqMultipleCorrectRadioButton = this.findViewById(R.id.radio_button_question_type_multiple_correct);
        mSubjectiveRadioButton = this.findViewById(R.id.radio_button_question_type_subjective);
        mQuestionLinearLayout = (RelativeLayout) this.findViewById(R.id.layout_right);
        mQuestionText = (EditText) this.findViewById(R.id.edit_text_question);
        mTextChoiceTypeButton = (RadioButton) this.findViewById(R.id.radio_button_choice_type_text);
        mImageChoiceTypeButton = (RadioButton) this.findViewById(R.id.radio_button_choice_type_image);
        explanationTextRadioButton = this.findViewById(R.id.radio_button_explanation_text);
        explanationResourceRadioButton = this.findViewById(R.id.radio_button_explanation_resource);
        explanationVideoRadioButton = this.findViewById(R.id.radio_button_explanation_video);
        mExplanationText = (EditText) this.findViewById(R.id.edit_text_explanation);
        mExplanationImage = (ImageView) this.findViewById(R.id.image_view_explanation);
        mExplanationVideoImage = (ImageView) this.findViewById(R.id.image_view_explanation_video);
        mExplanationImageLayout = (CardView) this.findViewById(R.id.layout_explanation_image);
        mExplanationVideoLayout = (CardView) this.findViewById(R.id.layout_explanation_video);
        mQuestionAttachmentLayout = (CardView) this.findViewById(R.id.layout_question_attachment);
        mRemoveQuestionAttachment = (ImageView) this.findViewById(R.id.imageView_remove_attachment);
        mAttachmentResourceImage = (ImageView) this.findViewById(R.id.imageView_resource_image);
        mAttachmentResourceVideo = (ImageView) this.findViewById(R.id.imageView_resource_video);
        addChoiceItemButton = this.findViewById(R.id.add_mcq_choice);
        mQuestionListRecyclerView = (RecyclerView) this.findViewById(R.id.question_list_recycler_view);

    }

    private void initializeUIandClickListeners() {


        if (BuildConfig.ViewVisibilty == false) {
            questionImageView.setVisibility(View.GONE);
            questionImageViewVideo.setVisibility(View.GONE);
            mImageChoiceTypeButton.setVisibility(View.GONE);
            explanationResourceRadioButton.setVisibility(View.GONE);
            explanationVideoRadioButton.setVisibility(View.GONE);
            addHintImage.setVisibility(View.GONE);
            addHintVideo.setVisibility(View.GONE);
            findViewById(R.id.text_view_heading_question_attachment).setVisibility(View.GONE);
        }

        mRadioButtonList = new RadioButtonList();
        mIsChoiceTypeImage = false;

        mQuestionLableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mQuestionLinearLayout.getVisibility() == View.GONE) {
                    if (mQuiz.getQuestions().size() == 0) {
                        ToastUtils.showToastAlert(QuizCreatorActivity.this, getString(R.string.error_no_question));
                    } else {
                        hideSoftKeyboard();
                        showQuestionListLayout();
                    }
                }
            }
        });


        mAddNewQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideQuestionListLayout();
            }
        });

        mAddNewQuestionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.prompt_add_question));
                return false;
            }
        });

        mAddHintClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHintAction(v);
            }
        };


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                if (!TextUtils.isEmpty(mSkillsEdittext.getText().toString().trim())) {
                    addQuestionToQuiz();
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), "Please add skill");
                }

            }
        });


        mUpdateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExplanationText.setVisibility(View.GONE);
                updateQuestion();
            }
        });


        mSaveQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuiz.getObjectId() != null && !mQuiz.getObjectId().isEmpty() && mQuizModel.isQuizAlreadyAssignedSync(mQuiz.getObjectId())) {
                    ToastUtils.showToastAlert(QuizCreatorActivity.this, "You can't update quiz after assigned");
                } else
                    saveQuiz();
            }
        });


        mCancelQuestionUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExplanationText.setVisibility(View.GONE);
                cancelUpdateOfQuestionOfQuiz();
            }
        });


        addHintText.setOnClickListener(mAddHintClickListener);


        addHintImage.setOnClickListener(mAddHintClickListener);


        addHintVideo.setOnClickListener(mAddHintClickListener);


        questionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAttachmentImage = true;
                selectQuestionResource(v);
            }
        });


        questionImageViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAttachmentImage = false;
                selectQuestionResource(v);
            }
        });


        mcqSingleCorrectRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceSection();
                showExplanationSection();
            }
        });


        mcqMultipleCorrectRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceSection();
                showExplanationSection();

            }
        });


        mSubjectiveRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeChoiceSection();
                removeExplanationSection();
            }
        });


        mTextChoiceTypeButton.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    setChoiceType(false);
                }
            }

        });

        mImageChoiceTypeButton.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    hideSoftKeyboard();
                    setChoiceType(true);
                }
            }

        });


        explanationTextRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addExplanationText();
                mExplanationText.requestFocus();
                KeyBoardUtil.showSoftKeyboard(mExplanationText, getBaseContext());

            }
        });

        explanationResourceRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // selectExplanationResource();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, EXPLANATION_IMAGE);
            }
        });


        explanationVideoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  selectExplanationResource();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, EXPLANATION_VIDEO);
//                Intent intent = new Intent();
//                intent.setType("video/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, EXPLANATION_VIDEO);
            }
        });

        View layout = this.getLayoutInflater().inflate(R.layout.layout_error_toast_grey, null);

        mErrorToast = new ErrorToast(getApplicationContext(), layout, R.id.text_view_error_toast);
        mErrorToast.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL, 0, 20);
        mErrorToast.setDuration(Toast.LENGTH_LONG);

        mExplanationResourceType = "";


        addChoiceItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsChoiceTypeImage) {
                    selectChoiceImage();
                } else {
                    addChoiceTextToSubSection(mQuestionTypeRadioGroup.getCheckedRadioButtonId(), null);
                }
            }
        });


        mTitleHeaderTextView = (TextView) this.findViewById(R.id.textview_quiz_title_header);


        mQuestionAttachmentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResource(mQuestionAttachmentImageView.getTag().toString());

            }
        });

        mRemoveQuestionAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQuestionAttachmentLayout.setVisibility(View.GONE);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscription.dispose();

    }

    /**
     * provides results when an activity call is made by startActivityForResult
     * here it provides theresources obtained from gallery or another app
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == QUESTION_IMAGE_FROM_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                try {
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String picturePath = copyFiles(f.getAbsolutePath(), mBaseFolder, mQuizResourceImageFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    f.delete();
                    mQuestionAttachmentImageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), mBaseFolder, picturePath));
                    mQuestionAttachmentImageView.setTag(mBaseFolder + File.separator + picturePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == QUESTION_IMAGE_FROM_GALLERY) {

                Uri selectedImage = data.getData();
                if (FileChooser.fileSize(getBaseContext(), selectedImage) < 10) {
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePathOriginal = c.getString(columnIndex);
                    c.close();
                    mQuestionAttachmentLayout.setVisibility(View.VISIBLE);
                    mAttachmentResourceImage.setVisibility(View.VISIBLE);
                    mAttachmentResourceVideo.setVisibility(View.GONE);

                    String picturePath = copyFiles(picturePathOriginal, mBaseFolder, mQuizResourceImageFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mQuestionAttachmentImageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), mBaseFolder, picturePath));
                    mQuestionAttachmentImageView.setTag(mBaseFolder + File.separator + picturePath);
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit));
                }


            } else if (requestCode == QUESTION_VIDEO) {

                Uri selectedVideo = data.getData();
                if (FileChooser.fileSize(getBaseContext(), selectedVideo) < 10) {
                    String[] filePath = {MediaStore.Video.Media.DATA};
                    Cursor c = getContentResolver().query(selectedVideo, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String videoPathOriginal = c.getString(columnIndex);
                    c.close();
                    if (videoPathOriginal.contains(".mp4")) {
                        mQuestionAttachmentLayout.setVisibility(View.VISIBLE);
                        mAttachmentResourceImage.setVisibility(View.GONE);
                        mAttachmentResourceVideo.setVisibility(View.VISIBLE);

                        String videoPath = copyFiles(videoPathOriginal, mBaseFolder, mQuizResourceVideoFolder, String.valueOf(System.currentTimeMillis()) + ".mp4");
                        Bitmap thumbnail = getScaledBitmapFromPath(this.getResources(), mBaseFolder, videoPath);
                        mQuestionAttachmentImageView.setImageBitmap(thumbnail);
                        mQuestionAttachmentImageView.setTag(mBaseFolder + File.separator + videoPath);
                    } else {
                        ToastUtils.showToastAlert(getBaseContext(), getString(R.string.format_not_supported));
                    }

                } else {
                    ToastUtils.showToastAlert(getBaseContext(), "Can't attach file larger then 10 MB");
                }

            } else if (requestCode == HINT_IMAGE) {

                Uri selectedImage = data.getData();
                if (FileChooser.fileSize(getBaseContext(), selectedImage) < 10) {
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePathOriginal = c.getString(columnIndex);
                    c.close();

                    String picturePath = copyFiles(picturePathOriginal, mBaseFolder, mQuizResourceImageFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    addHintImage(mBaseFolder + File.separator + picturePath);
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit));
                }

            } else if (requestCode == HINT_VIDEO) {

                Uri selectedVideo = data.getData();
                if (FileChooser.fileSize(getBaseContext(), selectedVideo) < 10) {
                    String[] filePath = {MediaStore.Video.Media.DATA};
                    Cursor c = getContentResolver().query(selectedVideo, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String videoPathOriginal = c.getString(columnIndex);
                    c.close();
                    if (videoPathOriginal.contains(".mp4")) {
                        String videoPath = copyFiles(videoPathOriginal, mBaseFolder, mQuizResourceVideoFolder, String.valueOf(System.currentTimeMillis()) + ".mp4");
                        addHintVideoImage(mBaseFolder + File.separator + videoPath);
                    } else {
                        ToastUtils.showToastAlert(getBaseContext(), getString(R.string.format_not_supported));
                    }

                } else {
                    ToastUtils.showToastAlert(getBaseContext(), "Can't attach file larger then 10 MB");
                }


//                if (data != null) {
//                    Uri fileUri = data.getData();
//                    String fileType = URLConnection.guessContentTypeFromName(FileChooser.getPath(getBaseContext(), fileUri));
//                    if (fileType == null || fileType.equals("null")) {
//                        Toast.makeText(getBaseContext(), "File not supported", Toast.LENGTH_SHORT).show();
//                    } else {
//
//                        if (FileChooser.fileSize(getBaseContext(), fileUri) < 10) {
//                            String videoPath = copyFiles(FileChooser.getPath(getBaseContext(), fileUri), mBaseFolder, mQuizResourceVideoFolder, String.valueOf(System.currentTimeMillis()) + ".mp4");
//                            addHintVideoImage(videoPath);
//                        } else {
//                            ToastUtils.showToastAlert(getBaseContext(), "Can't attach file larger then 10 MB");
//                        }
//                    }
//                }
            } else if (requestCode == CHOICE_IMAGE) {

                Uri selectedImage = data.getData();
                if (FileChooser.fileSize(getBaseContext(), selectedImage) < 10) {
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePathOriginal = c.getString(columnIndex);
                    c.close();

                    String picturePath = copyFiles(picturePathOriginal, mBaseFolder, mQuizResourceImageFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    addChoiceImageToSubSection(mQuestionTypeRadioGroup.getCheckedRadioButtonId(), mBaseFolder + File.separator + picturePath, null);
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit));
                }


            } else if (requestCode == EXPLANATION_IMAGE) {

                Uri selectedImage = data.getData();
                if (FileChooser.fileSize(getBaseContext(), selectedImage) < 10) {
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePathOriginal = c.getString(columnIndex);
                    c.close();

                    String picturePath = copyFiles(picturePathOriginal, mBaseFolder, mQuizResourceImageFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    addExplanationImage(mBaseFolder + File.separator + picturePath);
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit));
                }

            } else if (requestCode == EXPLANATION_VIDEO) {

                Uri selectedVideo = data.getData();
                if (FileChooser.fileSize(getBaseContext(), selectedVideo) < 10) {
                    String[] filePath = {MediaStore.Video.Media.DATA};
                    Cursor c = getContentResolver().query(selectedVideo, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String videoPathOriginal = c.getString(columnIndex);
                    c.close();
                    if (videoPathOriginal.contains(".mp4")) {
                        String videoPath = copyFiles(videoPathOriginal, mBaseFolder, mQuizResourceVideoFolder, String.valueOf(System.currentTimeMillis()) + ".mp4");
                        addExplanationVideoImage(mBaseFolder + File.separator + videoPath);
                    } else {
                        ToastUtils.showToastAlert(getBaseContext(), getString(R.string.format_not_supported));
                    }

                } else {
                    ToastUtils.showToastAlert(getBaseContext(), "Can't attach file larger then 10 MB");
                }

            }
        }
    }

    /**
     * sets the choice type to text or image
     * and clears all choices
     *
     * @param isChoiceTypeImage
     */
    @Override
    public void setChoiceType(boolean isChoiceTypeImage) {
        mIsChoiceTypeImage = isChoiceTypeImage;
        mRadioButtonList.setmHideEditText(isChoiceTypeImage);
        if (isChoiceTypeImage) {
            ((RadioButton) findViewById(R.id.radio_button_choice_type_image)).setChecked(true);
            ((RadioButton) findViewById(R.id.radio_button_choice_type_image)).setButtonDrawable(R.drawable.image_s);
            ((RadioButton) findViewById(R.id.radio_button_choice_type_text)).setButtonDrawable(R.drawable.text_c);
        } else {
            ((RadioButton) findViewById(R.id.radio_button_choice_type_text)).setChecked(true);
            ((RadioButton) findViewById(R.id.radio_button_choice_type_image)).setButtonDrawable(R.drawable.image_c);
            ((RadioButton) findViewById(R.id.radio_button_choice_type_text)).setButtonDrawable(R.drawable.text_s);
        }

        ((ViewGroup) this.findViewById(R.id.section_choices_sub_section)).removeAllViews();
    }

    /**
     * shows choice section
     */
    @Override
    public void showChoiceSection() {
        ViewGroup sectionQuestionChoices = (ViewGroup) findViewById(R.id.section_question_choices);
        sectionQuestionChoices.setVisibility(View.VISIBLE);

        ViewGroup subSectionQuestionChoices = (ViewGroup) sectionQuestionChoices.findViewById(R.id.section_choices_sub_section);
        subSectionQuestionChoices.removeAllViews();
        mRadioButtonList.clear();

    }

    /**
     * removes choice section
     */
    @Override
    public void removeChoiceSection() {
        ViewGroup sectionQuestionChoices = (ViewGroup) findViewById(R.id.section_question_choices);
        ViewGroup subSectionQuestionChoices = (ViewGroup) sectionQuestionChoices.findViewById(R.id.section_choices_sub_section);
        subSectionQuestionChoices.removeAllViews();
        sectionQuestionChoices.setVisibility(View.GONE);
    }

    /**
     * shows Explanation section
     */
    @Override
    public void showExplanationSection() {
        View sectionExplanation = findViewById(R.id.section_explanation);
        sectionExplanation.setVisibility(View.VISIBLE);

    }

    /**
     * removes Explanation section
     */
    @Override
    public void removeExplanationSection() {
        View sectionExplanation = findViewById(R.id.section_explanation);
        sectionExplanation.setVisibility(View.GONE);

    }

    /**
     * initialize quiz with its data if modifying old quiz
     * else initialize with blank quiz when creating new quiz
     *
     * @param quiz
     */
    @Override
    public void initializeQuiz(Quiz quiz) {
        mQuiz = quiz;
        mQuiz.setDocId(quiz.getDocId());
        mTitleHeaderTextView.setText(quiz.getTitle());
        // initializing folders as /SubjectId/

        if (mQuiz.getMetaInformation() != null && mQuiz.getMetaInformation().getSubject() != null)
            this.initializeResourceFolders(mQuiz.getMetaInformation().getSubject().getId());


        if (quiz.getMetaInformation().getTopic() != null && quiz.getMetaInformation().getTopic().getSkills() != null && quiz.getMetaInformation().getTopic().getSkills().size() > 0)
            setupEdittextForSkills((ArrayList<Skill>) quiz.getMetaInformation().getTopic().getSkills());


        refreshQuizType(mQuiz.getQuizType());

        ArrayList<Question> data = mQuiz.getQuestions();
        mTitleQuestionCountTextView.setText(String.valueOf(data.size()));
        if (mQuizScrollViewLayout.getVisibility() == View.VISIBLE) {
            mQuestionLableLayout.setVisibility(View.VISIBLE);
            mToolbarRightLayout.setVisibility(View.GONE);
        } else {
            mQuestionLableLayout.setVisibility(View.GONE);
            mToolbarRightLayout.setVisibility(View.VISIBLE);

        }


        if (data != null) {
            setupRecyclerView(data);

        }

        initializeQuestionData();
    }

    private void setupRecyclerView(ArrayList<Question> data) {


        mQuestionListItemAdapter = new QuestionListItemAdapter(this, data, ContextCompat.getColor(this, R.color.selectedTextColorQuizCreator), ContextCompat.getColor(this, R.color.colorGreyDark));
        mQuestionListItemAdapter.setItemAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showQuestionItem(v);
            }

        });
        mQuestionListItemAdapter.setRemoveItemAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeQuestionListItem(v);
            }
        });

//        if (data.size() > 0) {
//            mQuestionLinearLayout.setVisibility(View.VISIBLE);
//        }

        mQuestionListRecyclerView.setAdapter(mQuestionListItemAdapter);
        mQuestionListRecyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });


    }

    /**
     * sets question data to new question
     */
    @Override
    public void initializeQuestionData() {

        setQuestionDataToView(getBlankQuestion());

//        mTabList.openTab(0);
    }

    private Question getBlankQuestion() {
        Question question = new Question();
        if (mQuiz.getQuizType().equals(QuizTypeEnum.OBJECTIVE.toString())) {
            question.setQuestionType(Question.TYPE_DISPLAY_RADIO);
            //  question.setDisplay(TYPE_DISPLAY_RADIO);
        } else {
            question.setQuestionType(Question.TYPE_QUESTION_SUBJECTIVE);
        }
        return question;
    }

    @Override
    public void saveQuiz() {
        mQuiz.setQuestions(mQuestionListItemAdapter.getmData());
        mQuiz.setCreatedBy(GeneralUtils.generateCreatedBy(mAppUserModel.getObjectId(), mAppUserModel.getName()));
        mQuiz.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        mQuiz.setStatus(1);
        mQuiz.setPublishDateTime(String.valueOf(new Date().getTime()));
        mQuizCreatorModel.saveQuizToDatabase(mQuiz);
        mRxBus.send(new NewQuizCreationEvent(mQuiz));
        if (QuizMetaDataActivity.mQuizMetaDataActivity != null) {
            QuizMetaDataActivity.mQuizMetaDataActivity.finish();
        }

        finish();

//        int errorType = mQuizCreatorModel.validateQuiz(mQuiz);
//        if (errorType == VALIDATION_ERROR10) {
//            DialogUtils.showAlertDialogQuizCreator(QuizCreatorActivity.this, getString(R.string.please_add_a_question), getString(R.string.alert_title_string), false);
//        } else if (errorType == VALIDATION_ERROR11) {
//            getQuizTitle();
//
//        } else if (errorType == VALIDATION_NO_ERROR) {
//            mQuizCreatorModel.saveQuizToDatabase(mQuiz);
//            mRxBus.send(new NewQuizCreationEvent(mQuiz));
//            if (QuizMetaDataActivity.mQuizMetaDataActivity != null) {
//                QuizMetaDataActivity.mQuizMetaDataActivity.finish();
//            }
//
//            finish();
//        }
    }

    /**
     * adds new choice of type image
     *
     * @param questionType
     * @param picturePath
     * @param questionChoice
     */
    @Override
    public void addChoiceImageToSubSection(int questionType, String picturePath, QuestionChoice questionChoice) {
        final ViewGroup viewGroup = (ViewGroup) this.findViewById(R.id.section_choices_sub_section);
        hideSoftKeyboard();
        if (questionType == R.id.radio_button_question_type_multiple_correct) {
            getLayoutInflater().inflate(R.layout.layout_mcq_multiple_correct_choice_item, viewGroup);
            final ViewGroup choiceLayout = (ViewGroup) viewGroup.getChildAt(viewGroup.getChildCount() - 1);

            ImageButton mRemoveChoiceButton = (ImageButton) choiceLayout.findViewById(R.id.multiple_correct_choice_delete);
            mRemoveChoiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewGroup.removeView(choiceLayout);
                }
            });

            CheckBox checkBox = (CheckBox) choiceLayout.findViewById(R.id.checkbox_mcq_multiple_choice);

            choiceLayout.findViewById(R.id.edit_text_mcq_multiple_choice).setVisibility(View.GONE);

            if (questionChoice != null) {
                checkBox.setChecked(questionChoice.isChoiceCorrect());
                picturePath = questionChoice.getChoiceResource().getDeviceURL();
            }

            final String strFullScreen = picturePath;
            checkBox.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showResource(strFullScreen);
                    return false;
                }
            });

            if (picturePath != null) {
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(this.getResources(), getScaledBitmapFromPath(this.getResources(), picturePath));
                Bitmap mBitmap = ((BitmapDrawable) mBitmapDrawable).getBitmap();
                Drawable mDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(mBitmap, 170, 130, true));
                checkBox.setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null);
                checkBox.setTag(picturePath);
            }
        } else if (questionType == R.id.radio_button_question_type_single_correct)

        {
            getLayoutInflater().inflate(R.layout.layout_mcq_single_correct_choice_item, viewGroup);
            final ViewGroup choiceLayout = (ViewGroup) viewGroup.getChildAt(viewGroup.getChildCount() - 1);
            ImageView imageView = (ImageView) choiceLayout.findViewById(R.id.single_correct_choice_delete);
            RadioButton radioButton = (RadioButton) choiceLayout.findViewById(R.id.radio_button_mcq_single_correct);
            EditText editText = (EditText) choiceLayout.findViewById(R.id.edit_text_mcq_single_correct);

            mRadioButtonList.add(radioButton, editText, imageView);
            if (questionChoice != null) {
                if (questionChoice.isChoiceCorrect()) {
                    mRadioButtonList.radioButtonCheck(radioButton);

                }
                picturePath = questionChoice.getChoiceResource().getDeviceURL();
            }

            final String strFullScreen = picturePath;
            radioButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showResource(strFullScreen);
                    return false;
                }
            });

            if (picturePath != null) {
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(this.getResources(), getScaledBitmapFromPath(this.getResources(), picturePath));
                Bitmap mBitmap = ((BitmapDrawable) mBitmapDrawable).getBitmap();
                Drawable mDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(mBitmap, 170, 130, true));
                radioButton.setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null);
                radioButton.setTag(picturePath);
            }

        }

    }

    /**
     * adds new choice of type text
     *
     * @param questionType
     * @param questionChoice
     */
    @Override
    public void addChoiceTextToSubSection(int questionType, QuestionChoice questionChoice) {
        final ViewGroup viewGroup = (ViewGroup) this.findViewById(R.id.section_choices_sub_section);
        if (questionType == R.id.radio_button_question_type_multiple_correct) {
            getLayoutInflater().inflate(R.layout.layout_mcq_multiple_correct_choice_item, viewGroup);
            final ViewGroup choiceLayout = (ViewGroup) viewGroup.getChildAt(viewGroup.getChildCount() - 1);
            ImageButton mRemoveChoiceButton = (ImageButton) choiceLayout.findViewById(R.id.multiple_correct_choice_delete);
            EditText editText = ((EditText) choiceLayout.findViewById(R.id.edit_text_mcq_multiple_choice));
            mRemoveChoiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewGroup.removeView(choiceLayout);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            });


            if (questionChoice != null) {
                ((CheckBox) choiceLayout.findViewById(R.id.checkbox_mcq_multiple_choice)).setChecked(questionChoice.isChoiceCorrect());
                editText.setText(questionChoice.getChoiceText());
            } else {
                editText.requestFocus();
                KeyBoardUtil.showSoftKeyboard(editText, this);
            }
        } else if (questionType == R.id.radio_button_question_type_single_correct) {
            getLayoutInflater().inflate(R.layout.layout_mcq_single_correct_choice_item, viewGroup);
            final ViewGroup choiceLayout = (ViewGroup) viewGroup.getChildAt(viewGroup.getChildCount() - 1);
            ImageButton mRemoveChoiceButton = (ImageButton) choiceLayout.findViewById(R.id.single_correct_choice_delete);
            RadioButton radioButton = (RadioButton) choiceLayout.findViewById(R.id.radio_button_mcq_single_correct);
            EditText editText = (EditText) choiceLayout.findViewById(R.id.edit_text_mcq_single_correct);

            mRadioButtonList.add(radioButton, editText, mRemoveChoiceButton);

            if (questionChoice != null) {
                if (questionChoice.isChoiceCorrect()) {
                    mRadioButtonList.radioButtonCheck(radioButton);
                }
                editText.setText(questionChoice.getChoiceText());
            } else {
                editText.requestFocus();
                KeyBoardUtil.showSoftKeyboard(editText, this);
            }
        }

    }

    /**
     * adds new hint
     *
     * @param text
     */
    @Override
    public void addHintText(String text) {
        final ViewGroup hintLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_hint_item, (ViewGroup) this.findViewById(R.id.section_hints));
        final View childHintLayout = hintLayout.getChildAt(hintLayout.getChildCount() - 1);
        childHintLayout.findViewById(R.id.section_hint_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintLayout.removeView((View) v.getParent());
                KeyBoardUtil.hideSoftKeyboard(childHintLayout.findViewById(R.id.edit_text_section_hint), getBaseContext());
            }
        });

        EditText editText = ((EditText) childHintLayout.findViewById(R.id.edit_text_section_hint));

        if (text != null && !text.isEmpty()) {
            editText.setText(text);
        } else {
            editText.requestFocus();
            KeyBoardUtil.showSoftKeyboard(editText, this);
        }

    }

    /**
     * set image on the new hint
     *
     * @param path
     */
    @Override
    public void addHintImage(final String path) {
        final ViewGroup hintLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_hint_item_image, (ViewGroup) this.findViewById(R.id.section_hints));
        View childHintLayout = hintLayout.getChildAt(hintLayout.getChildCount() - 1);
        childHintLayout.findViewById(R.id.section_hint_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintLayout.removeView((View) v.getParent().getParent());
            }
        });
        if (path != null && !path.isEmpty()) {
            ImageView hintImage = ((ImageView) childHintLayout.findViewById(R.id.section_hint_image_view));
            File file = new File(FileUtils.getPathFromFilePath(path));
            Picasso.with(getBaseContext()).load(file).placeholder(R.drawable.gradient_black_bottom).resize(170, 130).centerInside().into(hintImage);
            //hintImage.setImageBitmap(getScaledBitmapFromPath(this.getResources(), path));
            hintImage.setTag(path);

            hintImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showResource(path);
                }
            });

        }
    }

    /**
     * set video thumbnail on the new hint
     *
     * @param path
     */
    @Override
    public void addHintVideoImage(final String path) {
        final ViewGroup hintLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_hint_item_video, (ViewGroup) this.findViewById(R.id.section_hints));
        View childHintLayout = hintLayout.getChildAt(hintLayout.getChildCount() - 1);
        childHintLayout.findViewById(R.id.section_hint_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintLayout.removeView((View) v.getParent().getParent());
            }
        });
        if (path != null && !path.isEmpty()) {
            ImageView hintVideo = ((ImageView) childHintLayout.findViewById(R.id.section_hint_video_view));
            File file = new File(FileUtils.getPathFromFilePath(path));
            Picasso.with(getBaseContext()).load(file).placeholder(R.drawable.gradient_black_bottom).resize(170, 130).centerInside().into(hintVideo);
            //hintVideo.setImageBitmap(getScaledBitmapFromPath(this.getResources(), path));
            hintVideo.setTag(path);

            hintVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showResource(path);
                }
            });
        }
    }

    /**
     * add explanation text
     */
    @Override
    public void addExplanationText() {
        mExplanationText.setVisibility(View.VISIBLE);
        mExplanationText.setEnabled(true);
        mExplanationResourceType = "";
        mExplanationText.setError(null);
        mExplanationImage.setImageBitmap(null);
        mExplanationImage.setTag(null);
        mExplanationImageLayout.setVisibility(View.GONE);
        mExplanationVideoImage.setImageBitmap(null);
        mExplanationVideoImage.setTag(null);
        mExplanationVideoLayout.setVisibility(View.GONE);
    }

    /**
     * set image on the explanation
     *
     * @param path
     */
    @Override
    public void addExplanationImage(final String path) {

        if (path != null && !path.isEmpty()) {
            hideSoftKeyboard();
            File file = new File(FileUtils.getPathFromFilePath(path));
            Picasso.with(getBaseContext()).load(file).placeholder(R.drawable.gradient_black_bottom).resize(170, 130).centerInside().into(mExplanationImage);
            // mExplanationImage.setImageBitmap(getScaledBitmapFromPath(this.getResources(), path));
            mExplanationImage.setTag(path);
            mExplanationImageLayout.setVisibility(View.VISIBLE);
            mExplanationText.setError(null);
            mExplanationText.setEnabled(false);
            mExplanationText.setVisibility(View.GONE);
            mExplanationVideoImage.setImageBitmap(null);
            mExplanationVideoImage.setTag(null);
            mExplanationVideoLayout.setVisibility(View.GONE);
            mExplanationResourceType = Resource.TYPE_RESOURCE_IMAGE;
            mExplanationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showResource(path);
                }
            });
        }
    }

    /**
     * set video thumbnail on the Explanation
     *
     * @param path
     */
    @Override
    public void addExplanationVideoImage(final String path) {
        if (path != null && !path.isEmpty()) {
            hideSoftKeyboard();
            File file = new File(FileUtils.getPathFromFilePath(path));
            Picasso.with(getBaseContext()).load(file).placeholder(R.drawable.gradient_black_bottom).resize(170, 130).centerInside().into(mExplanationVideoImage);
            //mExplanationVideoImage.setImageBitmap(getScaledBitmapFromPath(this.getResources(), path));
            mExplanationVideoImage.setTag(path);
            mExplanationVideoLayout.setVisibility(View.VISIBLE);
            mExplanationImage.setImageBitmap(null);
            mExplanationImage.setTag(path);
            mExplanationImageLayout.setVisibility(View.GONE);
            mExplanationText.setError(null);
            mExplanationText.setEnabled(false);
            mExplanationText.setVisibility(View.GONE);
            mExplanationResourceType = Resource.TYPE_RESOURCE_VIDEO;

            mExplanationVideoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showResource(path);
                }
            });
        }
    }

    /**
     * shows the data of the question selected from the questions list
     *
     * @param view
     */
    @Override
    public void showQuestionItem(View view) {
        mQuestionText.requestFocus();
        mExplanationText.setVisibility(View.VISIBLE);
        mLHScrollView.fullScroll(ScrollView.FOCUS_UP);
        hideQuestionListLayout();
        hideSoftKeyboard();
        int position = mQuestionListRecyclerView.getChildAdapterPosition(view);
        if (position != RecyclerView.NO_POSITION) {
            setQuestionDataToView(mQuestionListItemAdapter.getQuestionItemData(position));
            updateButtonVisibility(false);
        }

    }

    /**
     * removes question list item
     *
     * @param view
     */
    @Override
    public void removeQuestionListItem(View view) {
        boolean isLastSelected = false;
        mTitleQuestionCountTextView.setText(String.valueOf(mQuiz.getQuestions().size() - 1));
        int position = mQuestionListRecyclerView.getChildAdapterPosition((View) view.getParent().getParent());
        if (position != RecyclerView.NO_POSITION) {
            isLastSelected = mQuestionListItemAdapter.removeItem(position);
            if (mQuestionListItemAdapter.getItemCount() == 0) {
                refreshQuizType(QuizTypeEnum.MIX.toString());
                hideQuestionListLayout();
            }
        }
        if (isLastSelected) {
            initializeQuestionData();
            updateButtonVisibility(true);
        }

    }

    /**
     * adds the new question to the question list and reset question data on screen
     * on click of add button
     */
    @Override
    public void addQuestionToQuiz() {

        Question question = getQuestionDataFromView();
        int errorType = mQuizCreatorModel.validateQuestionData(question);


        if (errorType == VALIDATION_NO_ERROR) {

            if (mQuestionLinearLayout.getVisibility() == View.GONE) {
                mTitleQuestionCountTextView.setText(String.valueOf(mQuiz.getQuestions().size() + 1));
                mExplanationText.setVisibility(View.GONE);
                ToastUtils.showToastSuccess(QuizCreatorActivity.this, getString(R.string.prompt_question_added));
                AnimationUtils.blink(getBaseContext(), mQuestionLableLayout);
                mSkillsEdittext.setText("");
            }
            mQuestionAttachmentLayout.setVisibility(View.GONE);
            mQuestionListItemAdapter.addItem(question);
            mQuestionListRecyclerView.scrollToPosition(mQuestionListItemAdapter.getItemCount() - 1);
            initializeQuestionData();
            if (mQuestionListItemAdapter.getItemCount() == 1) {
                if (mQuestionListItemAdapter.getmData().get(0).getQuestionType().equals(TYPE_QUESTION_SUBJECTIVE)) {
                    refreshQuizType(QuizTypeEnum.SUBJECTIVE.toString());
                } else {
                    refreshQuizType(QuizTypeEnum.OBJECTIVE.toString());
                }
            }

        } else {
            showErrorMessage(errorType);
        }

    }

    /**
     * refresh the display of question type
     * if the quiz is subjective the only subjective question type available
     * else if quiz is objective then single correct and multiple correct type available
     *
     * @param typeQuiz
     */
    @Override
    public void refreshQuizType(String typeQuiz) {
        if (typeQuiz.equals(QuizTypeEnum.OBJECTIVE.toString())) {
            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_subjective).setVisibility(View.GONE);
            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_multiple_correct).setVisibility(View.VISIBLE);
            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_single_correct).setVisibility(View.VISIBLE);
            mSkillsEdittext.setText("");
            showChoiceSection();
            showExplanationSection();
        } else if (typeQuiz.equals(QuizTypeEnum.SUBJECTIVE.toString())) {
            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_subjective).setVisibility(View.VISIBLE);
            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_multiple_correct).setVisibility(View.GONE);
            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_single_correct).setVisibility(View.GONE);
            mSkillsEdittext.setText("");
            removeChoiceSection();
            removeExplanationSection();
        }
//        else {
//            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_subjective).setVisibility(View.VISIBLE);
//            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_multiple_correct).setVisibility(View.VISIBLE);
//            mQuestionTypeRadioGroup.findViewById(R.id.radio_button_question_type_single_correct).setVisibility(View.VISIBLE);
//            removeChoiceSection();
//            removeExplanationSection();
//        }
    }

    /**
     * show error message according to error code
     *
     * @param errorType
     */
    @Override
    public void showErrorMessage(int errorType) {
        switch (errorType) {
            case VALIDATION_ERROR1:
                showAToast("Please add question");
                mQuestionText.requestFocus();
                KeyBoardUtil.showSoftKeyboard(mQuestionText, this);
                mLHScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLHScrollView.smoothScrollTo(0, mQuestionText.getTop());
                    }
                });
                break;

            case VALIDATION_ERROR5:
                // DialogUtils.showAlertDialogQuizCreator(QuizCreatorActivity.this, getString(R.string.please_enter_atleast_two_choice), getString(R.string.alert_title_string), false);
                showAToast(getString(R.string.please_enter_atleast_two_choice));
                mLHScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLHScrollView.smoothScrollTo(0, mChoiceRadioGroup.getTop());
                    }
                });
                break;
            case VALIDATION_ERROR6:
                //DialogUtils.showAlertDialogQuizCreator(QuizCreatorActivity.this, getString(R.string.please_fill_the_empty_choice), getString(R.string.alert_title_string), false);
                showAToast(getString(R.string.please_fill_the_empty_choice));
                mLHScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLHScrollView.smoothScrollTo(0, mChoiceRadioGroup.getTop());
                    }
                });
                break;
            case VALIDATION_ERROR7:
                // DialogUtils.showAlertDialogQuizCreator(QuizCreatorActivity.this, getString(R.string.please_select_correct_choice), getString(R.string.alert_title_string), false);
                showAToast(getString(R.string.please_select_correct_choice));
                mLHScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLHScrollView.smoothScrollTo(0, mChoiceRadioGroup.getTop());
                    }
                });
                break;

            case VALIDATION_ERROR2:
                //DialogUtils.showAlertDialogQuizCreator(QuizCreatorActivity.this, getString(R.string.please_fill_explanation), getString(R.string.alert_title_string), false);
                showAToast(getString(R.string.please_fill_explanation));
                mLHScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLHScrollView.smoothScrollTo(0, mExplanationText.getTop());
                    }
                });
                break;

            case VALIDATION_ERROR8:
                //DialogUtils.showAlertDialogQuizCreator(QuizCreatorActivity.this, getString(R.string.please_add_hint), getString(R.string.alert_title_string), false);
                showAToast(getString(R.string.please_add_hint));
                mLHScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        final ViewGroup hintSection = (ViewGroup) findViewById(R.id.section_hints);
                        mLHScrollView.smoothScrollTo(0, hintSection.getTop());
                    }
                });
                break;
            case VALIDATION_ERROR9:
                //DialogUtils.showAlertDialogQuizCreator(QuizCreatorActivity.this, getString(R.string.please_fill_the_empty_hint), getString(R.string.alert_title_string), false);
                showAToast(getString(R.string.please_fill_the_empty_hint));
                mLHScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        final ViewGroup hintSection = (ViewGroup) findViewById(R.id.section_hints);
                        mLHScrollView.smoothScrollTo(0, hintSection.getTop());
                    }
                });
                break;

            case VALIDATION_ERROR3:
                //DialogUtils.showAlertDialogQuizCreator(QuizCreatorActivity.this, getString(R.string.please_select_skill), getString(R.string.alert_title_string), false);
                showAToast(getString(R.string.please_select_skill));
                mLHScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLHScrollView.smoothScrollTo(0, mSkillsEdittext.getTop());
                    }
                });
                break;
            case VALIDATION_ERROR4:
                break;


        }
    }

    public void showAToast(String st) { //"Toast toast" is declared in the class
        try {
            mToast.getView().isShown();     // true if visible
            mToast.setText(st);
        } catch (Exception e) {         // invisible if exception
            mToast = Toast.makeText(QuizCreatorActivity.this, st, Toast.LENGTH_LONG);
        }
        mToast.show();  //finally display it
    }

    /**
     * updates the changes made to a question on click of update button
     */
    @Override
    public void updateQuestion() {
        Question question = getQuestionDataFromView();
        int errorType = mQuizCreatorModel.validateQuestionData(question);

        if (errorType == VALIDATION_NO_ERROR) {
            mQuestionListItemAdapter.updateItem(question);
            initializeQuestionData();
            updateButtonVisibility(true);

            showQuestionListLayout();


        } else {
            showErrorMessage(errorType);
        }
    }

    /**
     * cancels the update process of a question on click of cancel button
     */
    @Override
    public void cancelUpdateOfQuestionOfQuiz() {
        initializeQuestionData();
        mQuestionListItemAdapter.closeAll();
        updateButtonVisibility(true);
    }

    /**
     * gets the question data from ui
     *
     * @return questionItem to be added to the quiz
     */
    @Override
    public Question getQuestionDataFromView() {
        //Add question to quiz
        Question question = new Question();
        ArrayList<QuestionChoice> questionChoices = new ArrayList<>();
        ArrayList<QuestionHint> questionHints = new ArrayList<>();

        question.setChoiceTypeImage(mIsChoiceTypeImage);

        String questionType = getQuestionTypeFromRadioButtonId(mQuestionTypeRadioGroup.getCheckedRadioButtonId());
        question.setQuestionType(questionType);

        ArrayList<Skill> list = new ArrayList<>();
        list.add((Skill) mSkillsEdittext.getTag());
        question.setSkills(list);

        question.setQuestionText(mQuestionText.getText().toString());

        ArrayList<Resource> questionResources = new ArrayList<>();

        Resource resourceQuestion = new Resource();
        if (mQuestionAttachmentImageView.getTag() != null && mQuestionAttachmentImageView.getDrawable() != null) {
            resourceQuestion.setDeviceURL(mQuestionAttachmentImageView.getTag().toString());

            if (mIsAttachmentImage) {
                resourceQuestion.setResourceType(Resource.TYPE_RESOURCE_IMAGE);
            } else {
                resourceQuestion.setResourceType(Resource.TYPE_RESOURCE_VIDEO);
            }
            questionResources.add(resourceQuestion);

        } else {
            resourceQuestion.setDeviceURL("");
        }

        question.setResources(questionResources);

        //Add choice type to quiz if quiz type is objective
        if (mIsChoiceTypeImage) {
            if (questionType.equalsIgnoreCase(TYPE_DISPLAY_CHECKBOX)) {
                CheckBox checkBox;

                ViewGroup choiceSubSection = (ViewGroup) this.findViewById(R.id.section_choices_sub_section);
                int size = choiceSubSection.getChildCount();
                ViewGroup singleChoiceLayout;
                for (int i = 0; i < size; i++) {
                    singleChoiceLayout = (ViewGroup) choiceSubSection.getChildAt(i);
                    checkBox = (CheckBox) singleChoiceLayout.findViewById(R.id.checkbox_mcq_multiple_choice);
                    Resource resource = new Resource();
                    if (checkBox.getTag() != null) {
                        resource.setResourceType(Resource.TYPE_RESOURCE_IMAGE);
                        resource.setDeviceURL(checkBox.getTag().toString());
                        questionChoices.add(new QuestionChoice(String.valueOf(i + 1), checkBox.isChecked(), resource));
                    }

                }

            } else if (questionType.equalsIgnoreCase(TYPE_DISPLAY_RADIO)) {
                int size = mRadioButtonList.size();
                for (int i = 0; i < size; i++) {
                    RadioButton radioButton = mRadioButtonList.getmRadioButtons().get(i);
                    Resource resource = new Resource();
                    if (radioButton.getTag() != null) {
                        resource.setResourceType(Resource.TYPE_RESOURCE_IMAGE);
                        resource.setDeviceURL(radioButton.getTag().toString());
                        questionChoices.add(new QuestionChoice(String.valueOf(i + 1), radioButton.isChecked(), resource));
                    }

                }
            }

        } else {
            if (questionType.equalsIgnoreCase(TYPE_DISPLAY_CHECKBOX)) {
                CheckBox checkBox;

                ViewGroup choiceSubSection = (ViewGroup) this.findViewById(R.id.section_choices_sub_section);
                int size = choiceSubSection.getChildCount();
                ViewGroup singleChoiceLayout;
                for (int i = 0; i < size; i++) {
                    singleChoiceLayout = (ViewGroup) choiceSubSection.getChildAt(i);
                    questionChoices.add(new QuestionChoice(String.valueOf(i + 1), ((CheckBox) singleChoiceLayout.findViewById(R.id.checkbox_mcq_multiple_choice)).isChecked(), ((EditText) singleChoiceLayout.findViewById(R.id.edit_text_mcq_multiple_choice)).getText().toString()));
                }

            } else if (questionType.equalsIgnoreCase(TYPE_DISPLAY_RADIO)) {

                int size = mRadioButtonList.size();
                for (int i = 0; i < size; i++) {
                    RadioButton radioButton = mRadioButtonList.getmRadioButtons().get(i);
                    EditText editText = mRadioButtonList.getmEditTexts().get(i);
                    questionChoices.add(new QuestionChoice(String.valueOf(i + 1), radioButton.isChecked(), editText.getText().toString()));
                }
            }

        }
        question.setQuestionChoices(questionChoices);
        question.deriveCorrectAnswers();

        //Add explanation to quiz if quiz type is objective
        QuestionExplanation questionExplanation = new QuestionExplanation();
        if (mExplanationTypeRadioGroup.getCheckedRadioButtonId() == R.id.radio_button_explanation_text) {
            questionExplanation.setExplanationText(mExplanationText.getText().toString());
            questionExplanation.setExplanationType(QuestionExplanation.TYPE_EXPLANATION_TEXT);
        } else if (mExplanationTypeRadioGroup.getCheckedRadioButtonId() == R.id.radio_button_explanation_resource) {
            Resource resource = new Resource();
            if (resource != null) {
                resource.setDeviceURL(mExplanationImage.getTag().toString());
                resource.setResourceType(mExplanationResourceType);
                questionExplanation.setExplanationResource(resource);
                questionExplanation.setExplanationType(QuestionExplanation.TYPE_EXPLANATION_RESOURCE);
            }

        } else if (mExplanationTypeRadioGroup.getCheckedRadioButtonId() == R.id.radio_button_explanation_video) {
            Resource resource = new Resource();
            if (resource != null) {
                resource.setDeviceURL(mExplanationVideoImage.getTag().toString());
                resource.setResourceType(mExplanationResourceType);
                questionExplanation.setExplanationResource(resource);
                questionExplanation.setExplanationType(QuestionExplanation.TYPE_EXPLANATION_RESOURCE);
            }
        }

        question.setUiQuestionExplanation(questionExplanation);

        //Add hints to quiz
        ViewGroup hintSection = (ViewGroup) this.findViewById(R.id.section_hints);
        int size = hintSection.getChildCount();
        ViewGroup singleHintLayout;
        for (int i = 0; i < size; i++) {
            singleHintLayout = (ViewGroup) hintSection.getChildAt(i);
            if (singleHintLayout.getTag().toString().equals(this.getResources().getString(R.string.hint_item_type_text))) {
                if (!((EditText) singleHintLayout.findViewById(R.id.edit_text_section_hint)).getText().toString().trim().isEmpty()) {
                    questionHints.add(new QuestionHint(i + 1, ((EditText) singleHintLayout.findViewById(R.id.edit_text_section_hint)).getText().toString()));
                }
            } else if (singleHintLayout.getTag().toString().equals(this.getResources().getString(R.string.hint_item_type_image))) {
                Resource resource = new Resource();
                resource.setResourceType(Resource.TYPE_RESOURCE_IMAGE);
                resource.setDeviceURL(singleHintLayout.findViewById(R.id.section_hint_image_view).getTag().toString());
                questionHints.add(new QuestionHint(i + 1, resource));
            } else if (singleHintLayout.getTag().toString().equals(this.getResources().getString(R.string.hint_item_type_video))) {
                Resource resource = new Resource();
                resource.setResourceType(Resource.TYPE_RESOURCE_VIDEO);
                resource.setDeviceURL(singleHintLayout.findViewById(R.id.section_hint_video_view).getTag().toString());
                questionHints.add(new QuestionHint(i + 1, resource));
            }

        }

        question.setQuestionHints(questionHints);
        mQuestionAttachmentImageView.setTag(null);
        return question;
    }

    /**
     * sets the question data on the ui
     *
     * @param question
     */
    @Override
    public void setQuestionDataToView(Question question) {
        int questionTypeCheck = -1;

        questionTypeCheck = getRadioButtonIdFromString(question.getQuestionType(), QUESTION_TYPE);

        checkChoiceConfiguration(question);

        mIsChoiceTypeImage = question.getChoiceTypeImage();

        if (questionTypeCheck == R.id.radio_button_question_type_multiple_correct || questionTypeCheck == R.id.radio_button_question_type_single_correct) {
            mQuestionTypeRadioGroup.check(questionTypeCheck);
            showChoiceSection();
            showExplanationSection();
            setChoiceType(mIsChoiceTypeImage);
        } else {
            mQuestionTypeRadioGroup.check(R.id.radio_button_question_type_subjective);
            removeChoiceSection();
            removeExplanationSection();
        }

        mQuestionText.setText(question.getQuestionText());

        if (question.getSkills().size() > 0) {
            mSkillsEdittext.setText(question.getSkills().get(0).getSkillName());
            mSkillsEdittext.setTag(question.getSkills().get(0));
        }

        if (question.getResources().size() > 0) {
            if (!question.getResources().get(0).getDeviceURL().isEmpty()) {
                mQuestionAttachmentLayout.setVisibility(View.VISIBLE);
                final String pathImage = question.fetchQuestionImage().getDeviceURL();
                if (!pathImage.isEmpty()) {
                    mAttachmentResourceImage.setVisibility(View.VISIBLE);
                    mAttachmentResourceVideo.setVisibility(View.GONE);
                    File file = new File(FileUtils.getPathFromFilePath(pathImage));
                    //Picasso.with(getBaseContext()).load(file).placeholder(R.drawable.gradient_black_bottom).resize(170, 130).centerInside().into(mQuestionAttachmentImageView);
                    mQuestionAttachmentImageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), "", pathImage));
                    mQuestionAttachmentImageView.setTag(pathImage);

                }

                final String pathVideo = question.fetchQuestionVideo().getDeviceURL();
                if (!pathVideo.isEmpty()) {
                    mAttachmentResourceImage.setVisibility(View.GONE);
                    mAttachmentResourceVideo.setVisibility(View.VISIBLE);
                    File file = new File(FileUtils.getPathFromFilePath(pathVideo));
                    //Picasso.with(getBaseContext()).load(file).placeholder(R.drawable.gradient_black_bottom).resize(170, 130).centerInside().into(mQuestionAttachmentImageView);
                    mQuestionAttachmentImageView.setImageBitmap(getScaledBitmapFromPath(this.getResources(), "", pathVideo));
                    mQuestionAttachmentImageView.setTag(pathVideo);

                }

            } else {
                mQuestionAttachmentLayout.setVisibility(View.GONE);
                mAttachmentResourceImage.setVisibility(View.GONE);
                mAttachmentResourceVideo.setVisibility(View.GONE);

            }
        } else {

            mQuestionAttachmentLayout.setVisibility(View.GONE);
        }

        ArrayList<QuestionChoice> questionChoices = question.getQuestionChoices();
        int sizeQuestionChoices = questionChoices.size();
        if (sizeQuestionChoices > 0) {
            if (mIsChoiceTypeImage) {

                mRadioButtonList.setmHideEditText(true);
                for (int i = 0; i < sizeQuestionChoices; i++) {
                    if (i == 0 || i == 1) {
                        addChoiceImageToSubSection(questionTypeCheck, null, questionChoices.get(i));
                    } else {
                        addChoiceImageToSubSection(questionTypeCheck, null, questionChoices.get(i));
                    }
                }
            } else {

                for (int i = 0; i < sizeQuestionChoices; i++) {
                    if (i == 0 || i == 1) {
                        addChoiceTextToSubSection(questionTypeCheck, questionChoices.get(i));
                    } else {
                        addChoiceTextToSubSection(questionTypeCheck, questionChoices.get(i));
                    }
                }
            }

        }

        /***********************************************************************************************************************************************************************************/
        QuestionExplanation questionExplanation = question.getChoiceConfiguration().getQuestionExplanation();

        if (questionExplanation.getExplanationType() == QuestionExplanation.TYPE_EXPLANATION_RESOURCE) {
            RadioButton explanationResourceRadioButton = (RadioButton) this.findViewById(R.id.radio_button_explanation_resource);
            RadioButton explanationVideoRadioButton = (RadioButton) this.findViewById(R.id.radio_button_explanation_video);
            Resource resource = questionExplanation.getExplanationResource();

            if (resource.getResourceType().equalsIgnoreCase(Resource.TYPE_RESOURCE_IMAGE)) {
                addExplanationImage(resource.getDeviceURL());
                explanationResourceRadioButton.setChecked(true);
            } else if (resource.getResourceType().equalsIgnoreCase(Resource.TYPE_RESOURCE_VIDEO)) {

                addExplanationVideoImage(resource.getDeviceURL());
                explanationVideoRadioButton.setChecked(true);
            }

        } else {
            mExplanationText.setText(questionExplanation.getExplanationText());
            RadioButton explanationTextRadioButton = (RadioButton) this.findViewById(R.id.radio_button_explanation_text);
            addExplanationText();
            explanationTextRadioButton.setChecked(true);
        }

        ArrayList<QuestionHint> questionHints = question.getQuestionHints();
        ViewGroup sectionHints = (ViewGroup) this.findViewById(R.id.section_hints);
        sectionHints.removeAllViews();
        int sizeQuestionHints = questionHints.size();
        //Assuming first item is the hint of order 1
        if (sizeQuestionHints >= 1) {
            for (int i = 0; i < sizeQuestionHints; i++) {
                QuestionHint questionHint = questionHints.get(i);
                if (questionHint.getHintResource().getResourceType().equalsIgnoreCase(Resource.TYPE_RESOURCE_IMAGE)) {

                    addHintImage(questionHint.getHintResource().getDeviceURL());
                } else if (questionHint.getHintResource().getResourceType().equalsIgnoreCase(Resource.TYPE_RESOURCE_VIDEO)) {

                    addHintVideoImage(questionHint.getHintResource().getDeviceURL());
                } else {
                    addHintText(questionHint.getHintText());

                }
            }

        } else {
            //new question
            ((ViewGroup) this.findViewById(R.id.section_hints)).removeAllViews();

        }

        // mQuestionImageView;

    }

    private void checkChoiceConfiguration(Question question) {
        ArrayList<String> choiceIds = new ArrayList<>();
        if (question.getQuestionType().equalsIgnoreCase(TYPE_QUESTION_MCQ)) {
            choiceIds = question.getChoiceConfiguration().getUiChoiceIds();

            for (String id : choiceIds) {
                question.getQuestionChoices().get(Integer.valueOf(id)).setChoiceCorrect(true);
            }

        }


    }

    /**
     * toggles the buttons between ADD and UPDATE
     *
     * @param isNewQuestion
     */
    @Override
    public void updateButtonVisibility(boolean isNewQuestion) {
        if (isNewQuestion) {
            this.findViewById(R.id.add_question_button).setVisibility(View.VISIBLE);
            this.findViewById(R.id.update_question_button).setVisibility(View.GONE);
        } else {
            this.findViewById(R.id.add_question_button).setVisibility(View.GONE);
            this.findViewById(R.id.update_question_button).setVisibility(View.VISIBLE);
        }
    }

    /**
     * selects the image for the choice
     */
    @Override
    public void selectChoiceImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOICE_IMAGE);

    }

    /**
     * create alert dialog to take image for the question
     */
    @Override
    public void selectQuestionImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, QUESTION_IMAGE_FROM_CAMERA);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, QUESTION_IMAGE_FROM_GALLERY);

                }
            }
        });
        builder.show();
    }

    /**
     * select resource for the question
     */
    @Override
    public void selectQuestionResource(View view) {
        if (view.getId() == R.id.view_question_image) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, QUESTION_IMAGE_FROM_GALLERY);

        } else if (view.getId() == R.id.view_question_video) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, QUESTION_VIDEO);

//            Intent intent = new Intent();
//            intent.setType("video/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent, QUESTION_VIDEO);

        }
    }

    /**
     * creates alert dialog to choose video for question
     */
    @Override
    public void selectQuestionVideo() {
        final CharSequence[] options = {"Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });
        builder.setTitle("Add Video!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, QUESTION_VIDEO);

                }
            }
        });
        builder.show();
    }

    /**
     * shows dialog to select the type of hint to show
     */
    @Override
    public void selectHintType() {
        final CharSequence[] options = {"Enter Text", "Choose Image", "Choose Video"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Hint!");
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Enter Text")) {
                    addHintText("");
                } else if (options[item].equals("Choose Image")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, HINT_IMAGE);
                } else if (options[item].equals("Choose Video")) {
//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(intent, HINT_VIDEO);
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, HINT_VIDEO);

                }
            }
        });
        builder.show();
    }

    /**
     * take action according to the view clicked for add hint
     */
    @Override
    public void addHintAction(View view) {
        if (view.getId() == R.id.button_add_hint_text) {
            addHintText("");
        } else if (view.getId() == R.id.button_add_hint_image) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, HINT_IMAGE);
        } else if (view.getId() == R.id.button_add_hint_video) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, HINT_VIDEO);
//            Intent intent = new Intent();
//            intent.setType("video/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent, HINT_VIDEO);
        }
    }

    /**
     * shows dialog to select the type of question explanation to show
     */
    @Override
    public void selectExplanationResource() {
        final CharSequence[] options = {"Choose Image", "Choose Video"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Explanation Resource!");
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                ((RadioButton) findViewById(R.id.radio_button_explanation_text)).setChecked(true);

            }
        });
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose Image")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, EXPLANATION_IMAGE);
                } else if (options[item].equals("Choose Video")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, EXPLANATION_VIDEO);

                }
            }
        });
        builder.show();
    }

    /**
     * create alert dialog to get title of the quiz
     */
    @Override
    public void getQuizTitle() {
        final View view = getLayoutInflater().inflate(R.layout.layout_quiz_title_alert_dialog, null);
        final EditText edittext = (EditText) view.findViewById(R.id.quiz_title);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Add Quiz Title");

        builder.setView(view);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (edittext.getText() != null && !edittext.getText().toString().trim().isEmpty()) {
                    mQuiz.setTitle(edittext.getText().toString());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

    /**
     * finds the id of the icon_radio_button_multiple button from given string
     *
     * @param tag  = String representing the icon_radio_button_multiple button
     * @param type = the icon_radio_button_multiple group of the icon_radio_button_multiple button
     * @return id of the icon_radio_button_multiple button
     */
    @Override
    public int getRadioButtonIdFromString(String tag, int type) {
        int id = -1;
        if (type == QUESTION_TYPE) {
            if (tag.equalsIgnoreCase(TYPE_DISPLAY_RADIO)) {
                id = R.id.radio_button_question_type_single_correct;
            } else if (tag.equalsIgnoreCase(TYPE_DISPLAY_CHECKBOX)) {
                id = R.id.radio_button_question_type_multiple_correct;
            } else if (tag.equalsIgnoreCase(TYPE_QUESTION_SUBJECTIVE)) {
                id = R.id.radio_button_question_type_subjective;
            }
        }
        return id;
    }

    /**
     * finds the question type from given icon_radio_button_multiple button id
     *
     * @param radioId
     * @return
     */
    @Override
    public String getQuestionTypeFromRadioButtonId(@IdRes int radioId) {
        String type = "";
        if (radioId == R.id.radio_button_question_type_single_correct) {
            type = TYPE_DISPLAY_RADIO;
        } else if (radioId == R.id.radio_button_question_type_multiple_correct) {
            type = TYPE_DISPLAY_CHECKBOX;
        } else if (radioId == R.id.radio_button_question_type_subjective) {
            type = TYPE_QUESTION_SUBJECTIVE;
        }
        return type;
    }

    /**
     * finds the display type from given icon_radio_button_multiple button id
     *
     * @param radioId
     * @return
     */
    @Override
    public String getDisplayTypeFromRadioButtonId(@IdRes int radioId) {
        String type = "";
        if (radioId == R.id.radio_button_question_type_single_correct) {
            type = TYPE_DISPLAY_RADIO;
        } else if (radioId == R.id.radio_button_question_type_multiple_correct) {
            type = TYPE_DISPLAY_CHECKBOX;
        }
        return type;
    }

    /**
     * setup the Disposable to listen to events from RxBus
     */
    @Override
    public void setupSubscription() {
        mSubscription = mRxBus.toFlowable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object eventObject) {
                if (eventObject instanceof LoadQuizForEditEvent) {
                    initializeQuiz(((LoadQuizForEditEvent) eventObject).getQuiz());
                }
            }
        });

    }

    private void setupEdittextForSkills(final ArrayList<Skill> skills) {

        mSkillsEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                PopupSkill(skills, point, mSkillsEdittext);
            }

        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void PopupSkill(final ArrayList<Skill> skills, Point point, final EditText mSkillsEdittext) {

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_skill_popup_window, null);
        ListView mSkillListView = (ListView) layout.findViewById(R.id.listview_skill);
        final SkillSpinnerAdapter mSkillsAdapter = new SkillSpinnerAdapter(this, skills);
        mSkillListView.setAdapter(mSkillsAdapter);

        int popupWidth = mSkillsEdittext.getWidth() - 20;
        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupEditbox = new PopupWindow(this);
        popupEditbox.setContentView(layout);
        popupEditbox.setWidth(popupWidth);
        popupEditbox.setHeight(popupHeight);
        popupEditbox.setFocusable(true);

        int OFFSET_X = 10;
        int OFFSET_Y = mSkillsEdittext.getHeight();

        popupEditbox.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupEditbox.setOutsideTouchable(true);
        popupEditbox.setElevation(10f);
        popupEditbox.setAnimationStyle(android.R.style.Animation_Dialog);
        popupEditbox.showAtLocation(layout, Gravity.NO_GRAVITY, point.x + OFFSET_X, point.y + OFFSET_Y);

        mSkillListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSkillsEdittext.setText(skills.get(i).getSkillName());
                mSkillsEdittext.setTag(skills.get(i));
                popupEditbox.dismiss();
            }
        });

    }

    /**
     * initialize the folders for resources
     *
     * @param parentFolderAbsolutePath
     */
    @Override
    public void initializeResourceFolders(String parentFolderAbsolutePath) {
        mQuizResourceImageFolder = parentFolderAbsolutePath + File.separator + "images";
        mQuizResourceVideoFolder = parentFolderAbsolutePath + File.separator + "videos";
        mQuizResourceAudioFolder = parentFolderAbsolutePath + File.separator + "TextDocPdf";
        mQuizResourceDocumentFolder = parentFolderAbsolutePath + File.separator + "audio";
    }

    /**
     * show layout containing list of question added to quiz
     */
    public void showQuestionListLayout() {
        AnimationUtils.slideOutLeft(this, mQuizScrollViewLayout);
        mQuestionLinearLayout.setVisibility(View.VISIBLE);
        mToolbarRightLayout.setVisibility(View.VISIBLE);
        mQuestionLableLayout.setVisibility(View.GONE);
        mQuizScrollViewLayout.setVisibility(View.GONE);
        mSkillsEdittext.setText("");
        AnimationUtils.slideInRight(this, mQuestionLinearLayout);
    }

    /**
     * hide layout containing list of question added to quiz
     */
    public void hideQuestionListLayout() {
        cancelUpdateOfQuestionOfQuiz();
        mExplanationText.setVisibility(View.GONE);
        AnimationUtils.slideOutRight(this, mQuestionLinearLayout);
        mQuestionLinearLayout.setVisibility(View.GONE);
        mToolbarRightLayout.setVisibility(View.GONE);
        mQuizScrollViewLayout.setVisibility(View.VISIBLE);
        mQuestionLableLayout.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mQuestionLinearLayout.getWindowToken(), 0);
        AnimationUtils.slideInLeft(this, mQuizScrollViewLayout);


    }

    public void showResource(String resourcePath) {

        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
        if (mimeType != null) {
            if (mimeType.contains("image")) {

                ArrayList<String> pathArrayList = new ArrayList<>();
                pathArrayList.add("file://" + resourcePath);
                FullScreenImage.setUpFullImageView(this, 0, false, true,FullScreenImage.getResourceArrayList(pathArrayList));
            } else if (mimeType.contains("video")) {

                Resource item = new Resource();
                item.setType("video");
                item.setUrlMain(resourcePath);
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));


            }
        }


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    /**
     * hide keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * setup view for showing images in full view
     * user can slide images.
     *
     * @param position
     * @param isComesFromGrid
     * @param mAttachmentPathList
     */
    public void setUpFullImageView(int position, boolean isComesFromGrid, ArrayList<String> mAttachmentPathList) {
        final Dialog mDialog = new Dialog(QuizCreatorActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_gallery_view);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorGrey77)));
        final ImageViewPager mImageViewPager = (ImageViewPager) mDialog.findViewById(R.id.viewpager_images);
        ImageButton mCloseButton = (ImageButton) mDialog.findViewById(R.id.button_back);
        final LinearLayout mPreviousButton = (LinearLayout) mDialog.findViewById(R.id.button_previous);
        final LinearLayout mNextButton = (LinearLayout) mDialog.findViewById(R.id.button_next);

        setUpFullImageViewItem(mImageViewPager, mAttachmentPathList);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageViewPager.setCurrentItem(mImageViewPager.getCurrentItem() - 1, true);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageViewPager.setCurrentItem(mImageViewPager.getCurrentItem() + 1, true);
            }
        });

        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    mPreviousButton.setVisibility(View.INVISIBLE);

                } else {
                    mPreviousButton.setVisibility(View.VISIBLE);
                }

                if (position == (mImageViewPager.getAdapter().getCount() - 1)) {
                    mNextButton.setVisibility(View.INVISIBLE);

                } else {
                    mNextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.show();

        if (isComesFromGrid) {
            if (position == 0) mPreviousButton.setVisibility(View.INVISIBLE);

            if (mImageViewPager.getAdapter().getCount() <= 1)
                mNextButton.setVisibility(View.INVISIBLE);

            mImageViewPager.setCurrentItem(position, true);
        } else {

            if (position == 0) mPreviousButton.setVisibility(View.INVISIBLE);

            if (mImageViewPager.getAdapter().getCount() <= 1)
                mNextButton.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * set up viewpager item for showing images
     *
     * @param mImageViewPager
     * @param mAttachmentPathList
     */
    private void setUpFullImageViewItem(ViewPager mImageViewPager, ArrayList<String> mAttachmentPathList) {
        ViewPagerImageAdapter mViewPagerImageAdapter;
        mViewPagerImageAdapter = new ViewPagerImageAdapter(QuizCreatorActivity.this, getResourceArrayList(mAttachmentPathList));
        mImageViewPager.setAdapter(mViewPagerImageAdapter);
        mImageViewPager.setOffscreenPageLimit(2);
    }

    private ArrayList<Resource> getResourceArrayList(ArrayList<String> mAttachmentPathList) {
        Resource resource;
        ArrayList<Resource> resources = new ArrayList<>();

        for (int i = 0; i < mAttachmentPathList.size(); i++) {
            resource = new Resource();
            String mimeType = URLConnection.guessContentTypeFromName(mAttachmentPathList.get(i));
            if (mimeType.contains("image")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_IMAGE);
            } else if (mimeType.contains("video")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_VIDEO);
            } else if (mimeType.contains("audio")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_AUDIO);
            } else if (mimeType.contains("application")) {
                resource.setResourceType(Resource.TYPE_RESOURCE_DOC);
            }
            resource.setDeviceURL(mAttachmentPathList.get(i));
            resources.add(resource);

        }

        return resources;
    }

}
