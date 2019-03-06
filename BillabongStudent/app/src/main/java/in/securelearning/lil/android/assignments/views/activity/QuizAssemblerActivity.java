package in.securelearning.lil.android.assignments.views.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutMetadataSpinnerItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizAssemblerCheckboxItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizAssemblerChoiceItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizAssemblerCreatorBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizAssemblerHintItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizAssemblerQuestionPreviewItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizAssemblerSelectedQuestionItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerviewPopupBinding;
import in.securelearning.lil.android.assignments.model.QuizAssemblerModel;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.QuizTypeEnum;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.quizcreator.events.NewQuizCreationEvent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 5/2/2017.
 */

public class QuizAssemblerActivity extends AppCompatActivity {

    private LayoutQuizAssemblerCreatorBinding mBinding;
    private String strSubjectGroup, strSubject, strTopic, strSkill, strQuizTitle, strGradeId, strSubjectId;
    private String strSkillId;
    private HashMap<String, GradeExt> dataHashMap = new HashMap<String, GradeExt>();
    ArrayList<GradeExt> values = new ArrayList<>();
    private HashMap<String, QuizMetaDataActivity.SubjectExt> mSubjectExtHashMap = new HashMap<String, QuizMetaDataActivity.SubjectExt>();
    private ArrayList<QuizMetaDataActivity.TopicExt> mTopicExtArrayList = new ArrayList<>();
    private HashMap<String, Question> mSelectedQuestionHashMap = new HashMap<>();
    private ArrayList<Question> mSkillQuestionArrayList = new ArrayList<>();
    private ArrayList<Question> mFinalQuestionArrayList = new ArrayList<>();
    private int mCheckBoxCount = 0;
    ArrayList<Boolean> mCheckedQuestion = new ArrayList<>();
    ArrayList<Boolean> mPresetFalse = new ArrayList<>();
    private int mLimit = 10;
    private int mSkip = 0;
    private int mPreviousTotal = 0;
    private QuestionAdapter mQuestionAdapter;

    @Inject
    QuizAssemblerModel mQuizAssemblerModel;

    @Inject
    NetworkModel networkModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AppUserModel mAppUserModel;

    private PopupWindow mViewPagerPopupWindow;
    Dialog mPreviewDialog;
    private ViewPager viewPagerQuestionPreview;
    private boolean isDialogShown = false;
    private boolean isQuestionSelectable = false;
    private QuizMetaDataActivity.TopicExt mTopic;
    private MenuItem mAddQuestionMenuItem, mViewQuestionMenuItem;

    @Override
    public void onBackPressed() {
        if (mPreviewDialog != null && isDialogShown == true) {

            isDialogShown = false;
            if (mCheckedQuestion != null) {
                if (mCheckBoxCount == mCheckedQuestion.size()) {
                    mBinding.checkBoxSelectAllQuestions.setChecked(true);
                } else {
                    mBinding.checkBoxSelectAllQuestions.setChecked(false);
                }
                setQuestionSelectionCount(mCheckBoxCount, mSkillQuestionArrayList.size());
            }
            mQuestionAdapter.notifyDataSetChanged();

        } else if (mBinding.includeSelectedQuestion.layoutQuestionSelected.getVisibility() == View.VISIBLE) {
            AnimationUtils.slideOutRight(getBaseContext(), mBinding.includeSelectedQuestion.layoutQuestionSelected);
            mBinding.includeSelectedQuestion.layoutQuestionSelected.setVisibility(View.GONE);
            addQuestionMenuVisibility(false);
            mBinding.layoutQuestionSelection.setVisibility(View.VISIBLE);
            viewQuestionMenuVisibility(true);

        } else {
            if (mBinding.includeMetaData.edittextGrade.getText().toString().isEmpty() &&
                    mBinding.includeMetaData.edittextSubject.getText().toString().isEmpty() &&
                    mBinding.includeMetaData.edittextTopic.getText().toString().isEmpty() &&
                    mBinding.includeMetaData.edittextSkill.getText().toString().isEmpty()) {

                finish();
            } else {
                new AlertDialog.Builder(QuizAssemblerActivity.this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorAssignment.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_quiz_assembler_creator);
        mPresetFalse = new ArrayList<>(mLimit);
        for (int i = 0; i < mLimit; i++) {
            mPresetFalse.add(false);
        }
        initializeUIAndListeners();
        initializeQuestionRecyclerView(new ArrayList<Question>());
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz_assembler, menu);
        mAddQuestionMenuItem = menu.findItem(R.id.actionAddMoreQuestions);
        mViewQuestionMenuItem = menu.findItem(R.id.actionViewSelectedQuestions);
        viewQuestionMenuVisibility(false);
        addQuestionMenuVisibility(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.actionAddMoreQuestions:
                menuAddMoreQuestions();
                return true;

            case R.id.actionViewSelectedQuestions:
                menuViewSelectedQuestions();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void menuViewSelectedQuestions() {
        if (mSelectedQuestionHashMap.size() != 0) {
            mBinding.layoutQuestionSelection.setVisibility(View.GONE);
            AnimationUtils.slideInRight(getBaseContext(), mBinding.includeSelectedQuestion.layoutQuestionSelected);
            mBinding.includeSelectedQuestion.layoutQuestionSelected.setVisibility(View.VISIBLE);
            viewQuestionMenuVisibility(false);
            addQuestionMenuVisibility(true);

        } else {
            ToastUtils.showToastAlert(getBaseContext(), "No question added");
        }
    }

    private void menuAddMoreQuestions() {
        AnimationUtils.slideOutRight(getBaseContext(), mBinding.includeSelectedQuestion.layoutQuestionSelected);
        mBinding.includeSelectedQuestion.layoutQuestionSelected.setVisibility(View.GONE);
        mBinding.layoutQuestionSelection.setVisibility(View.VISIBLE);
        viewQuestionMenuVisibility(true);
        addQuestionMenuVisibility(false);
    }

    private void addQuestionMenuVisibility(boolean b) {
        if (mAddQuestionMenuItem != null) {
            mAddQuestionMenuItem.setVisible(b);
        }
    }

    private void viewQuestionMenuVisibility(boolean b) {
        if (mViewQuestionMenuItem != null) {
            mViewQuestionMenuItem.setVisible(b);
        }
    }

    private void getData() {
        this.dataHashMap = mQuizAssemblerModel.getData();

        ArrayList<String> keys = new ArrayList<>(dataHashMap.keySet());
        values = new ArrayList<>();
        Collections.sort(keys);
        for (String s :
                keys) {
            values.add(dataHashMap.get(s));
        }
    }

    private void initializeUIAndListeners() {

        getWindow().setStatusBarColor(ContextCompat.getColor(QuizAssemblerActivity.this, R.color.colorPrimaryQuizCreator));
        setTitle(getString(R.string.label_assemble));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mBinding.includeMetaData.edittextGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindowEditText(values, mBinding.includeMetaData.edittextGrade, mBinding.includeMetaData.inputLayoutGrade, MetaDataAdapter.TYPE_GRADE);
            }
        });

        mBinding.includeMetaData.edittextSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.includeMetaData.edittextGrade.getText().toString().trim().isEmpty()) {
                    mSubjectExtHashMap = dataHashMap.get(strGradeId).getSubjectExtHashMap();
                    popupWindowEditText(new ArrayList(mSubjectExtHashMap.values()), mBinding.includeMetaData.edittextSubject, mBinding.includeMetaData.inputLayoutSubject, MetaDataAdapter.TYPE_SUBJECT);
                } else {
                    validateGrade();
                }

            }
        });

        mBinding.includeMetaData.edittextTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.includeMetaData.edittextSubject.getText().toString().trim().isEmpty()) {
                    mTopicExtArrayList = new ArrayList(mSubjectExtHashMap.get(strSubjectId).getTopicExts());
                    popupWindowEditText(mTopicExtArrayList, mBinding.includeMetaData.edittextTopic, mBinding.includeMetaData.inputLayoutTopic, MetaDataAdapter.TYPE_TOPIC);

                } else {
                    validateSubject();
                }

            }
        });

        mBinding.includeMetaData.edittextSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.includeMetaData.edittextTopic.getText().toString().trim().isEmpty()) {
                    if (mTopic != null)
                        popupWindowEditText(new ArrayList(mTopic.getSkills()), mBinding.includeMetaData.edittextSkill, mBinding.includeMetaData.inputLayoutSkill, MetaDataAdapter.TYPE_SKILL);
                } else {
                    validateTopic();
                }

            }
        });


        mBinding.includeSelectedQuestion.buttonSaveQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateQuizTitle()) {
                    return;
                } else {
                    mBinding.includeSelectedQuestion.buttonSaveQuiz.setEnabled(false);
                    saveQuestionToQuiz();
                }
            }
        });

        mBinding.includeSelectedQuestion.buttonPreviewAllSelectedQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isQuestionSelectable = false;
                resetBooleanArray(mFinalQuestionArrayList);
                setUpPreviewDialog(mFinalQuestionArrayList);
            }
        });

        mBinding.includeSelectedQuestion.recyclerViewSelectedQuestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mBinding.includeSelectedQuestion.buttonSaveQuiz.hide();
                } else {
                    mBinding.includeSelectedQuestion.buttonSaveQuiz.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    /**
     * add question and meta data to quiz
     * save quiz to database
     * finish current activity
     */
    private void saveQuestionToQuiz() {
        Quiz quiz = new Quiz();
        MetaInformation metaInformation = new MetaInformation();
        quiz.setTitle(strQuizTitle);
        quiz.setQuizType(QuizTypeEnum.OBJECTIVE.toString());
        QuizMetaDataActivity.TopicExt topic = mTopic;
        metaInformation.setBoard(topic.getBoard());
        metaInformation.setLanguage(topic.getLang());
        metaInformation.setSubject(topic.getSubject());
        metaInformation.setSubjectGroup(topic.getSubjectGroup());
        metaInformation.setGrade(topic.getGrade());
        metaInformation.setTopic(topic);
        metaInformation.setLearningLevel(topic.getLearningLevel());
        quiz.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        quiz.setCreatedBy(GeneralUtils.generateCreatedBy(mAppUserModel.getObjectId(), mAppUserModel.getApplicationUser().getName()));
        quiz.setThumbnail(null);
        quiz.setQuestions(mFinalQuestionArrayList);
        quiz.setAlias(GeneralUtils.generateAlias("quiz", "" + AppPrefs.getUserId(QuizAssemblerActivity.this), "" + System.currentTimeMillis()));
        quiz.setMetaInformation(metaInformation);
        quiz.setPublishDateTime(String.valueOf(new Date().getTime()));
        mQuizAssemblerModel.saveQuizToDatabase(quiz);
        mRxBus.send(new NewQuizCreationEvent(quiz));
        finish();

    }

    private boolean validateGrade() {

        strSubjectGroup = mBinding.includeMetaData.edittextGrade.getText().toString().trim();
        if (strSubjectGroup.isEmpty()) {
            mBinding.includeMetaData.inputLayoutGrade.setError(getString(R.string.error_grade));
            return false;
        } else {
            mBinding.includeMetaData.edittextGrade.clearFocus();
            mBinding.includeMetaData.inputLayoutGrade.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateSubject() {

        strSubject = mBinding.includeMetaData.edittextSubject.getText().toString().trim();
        if (strSubject.isEmpty()) {
            mBinding.includeMetaData.inputLayoutSubject.setError(getString(R.string.error_subject));
            return false;
        } else {
            mBinding.includeMetaData.edittextSubject.clearFocus();
            mBinding.includeMetaData.inputLayoutSubject.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateTopic() {

        strTopic = mBinding.includeMetaData.edittextTopic.getText().toString().trim();
        if (strTopic.isEmpty()) {
            mBinding.includeMetaData.inputLayoutTopic.setError(getString(R.string.error_topic));
            return false;
        } else {
            mBinding.includeMetaData.edittextTopic.clearFocus();
            mBinding.includeMetaData.inputLayoutTopic.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateQuizTitle() {

        strQuizTitle = mBinding.includeSelectedQuestion.editTextQuizTitle.getText().toString().trim();
        if (strQuizTitle.isEmpty()) {
            mBinding.includeSelectedQuestion.inputLayoutQuizTitle.setError(getString(R.string.error_title));
            mBinding.includeSelectedQuestion.editTextQuizTitle.requestFocus();
            return false;
        } else {
            mBinding.includeSelectedQuestion.editTextQuizTitle.clearFocus();
            mBinding.includeSelectedQuestion.inputLayoutQuizTitle.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * enable disable meta data fields when calling question api
     *
     * @param isEnable
     */
    private void metaFieldEnable(boolean isEnable) {
        mBinding.includeMetaData.edittextGrade.setEnabled(isEnable);
        mBinding.includeMetaData.edittextSubject.setEnabled(isEnable);
        mBinding.includeMetaData.edittextTopic.setEnabled(isEnable);

    }

    private void setQuestionSelectionCount(int count, int totalCount) {
        mBinding.textviewQuestionCounter.setText(String.valueOf(count));
        mBinding.textviewTotalQuestion.setText(String.valueOf(totalCount) + " Select all");
        if (mCheckBoxCount == 0) {
            mBinding.buttonActionExportQuestions.setVisibility(View.GONE);
            mBinding.checkBoxSelectAllQuestions.setChecked(false);

        } else {
            mBinding.buttonActionExportQuestions.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Pop up window for required fields like type, board,icon_language_c,subject,topic and grade.
     *
     * @param adapterData
     * @param mEditText
     * @param mTextInputLayout
     * @param editTextType
     */
    private void popupWindowEditText(final ArrayList adapterData, final EditText mEditText, final TextInputLayout mTextInputLayout, final int editTextType) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_recyclerview_popup, null);
        RecyclerView mEditTextDataListView = (RecyclerView) layout.findViewById(R.id.listview_edittext_data);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(QuizAssemblerActivity.this);
        mEditTextDataListView.setLayoutManager(layoutManager);

        final MetaDataAdapter arrayAdapter = new MetaDataAdapter(QuizAssemblerActivity.this, adapterData, editTextType);
        mEditTextDataListView.setAdapter(arrayAdapter);

        int popupWidth = mEditText.getWidth() - 20;
        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupMetaData = new PopupWindow(mEditText.getContext());
        popupMetaData.setContentView(layout);
        popupMetaData.setWidth(popupWidth);
        popupMetaData.setHeight(popupHeight);
        popupMetaData.setFocusable(true);

        int OFFSET_X = 10;
        int OFFSET_Y = mEditText.getHeight();

        popupMetaData.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupMetaData.setOutsideTouchable(true);
        popupMetaData.setElevation(10f);
        popupMetaData.setAnimationStyle(android.R.style.Animation_Dialog);

        int[] location = new int[2];
        mEditText.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        popupMetaData.showAtLocation(layout, Gravity.NO_GRAVITY, point.x + OFFSET_X, point.y + OFFSET_Y);

        arrayAdapter.setItemClickAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMetaData.dismiss();
                if (editTextType == MetaDataAdapter.TYPE_GRADE) {
                    if (!mBinding.includeMetaData.edittextGrade.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.setTag(v.getTag());
                        GradeExt gradeExt = (GradeExt) v.getTag();
                        strGradeId = gradeExt.getId();
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mBinding.includeMetaData.edittextSubject.setText("");
                        mBinding.includeMetaData.edittextTopic.setText("");
                        mBinding.includeMetaData.edittextSkill.setText("");
                        mBinding.layoutQuestionList.setVisibility(View.GONE);
                    }

                } else if (editTextType == MetaDataAdapter.TYPE_SUBJECT) {
                    if (!mBinding.includeMetaData.edittextSubject.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.setTag(v.getTag());
                        QuizMetaDataActivity.SubjectExt subject = (QuizMetaDataActivity.SubjectExt) v.getTag();
                        strSubjectId = subject.getId();
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mBinding.includeMetaData.edittextTopic.setText("");
                        mBinding.includeMetaData.edittextSkill.setText("");
                        mBinding.layoutQuestionList.setVisibility(View.GONE);
                    }
                } else if (editTextType == MetaDataAdapter.TYPE_TOPIC) {
                    if (!mBinding.includeMetaData.edittextTopic.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.setTag(v.getTag());
                        mTopic = (QuizMetaDataActivity.TopicExt) v.getTag();
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        mBinding.includeMetaData.edittextSkill.setText("");
                        mBinding.layoutQuestionList.setVisibility(View.GONE);
                    }
                } else if (editTextType == MetaDataAdapter.TYPE_SKILL) {
                    if (!mBinding.includeMetaData.edittextSkill.getText().toString().equals(((TextView) v).getText().toString())) {
                        mEditText.setText(((TextView) v).getText().toString());
                        mEditText.setTag(v.getTag());
                        Skill skill = (Skill) v.getTag();
                        strSkillId = skill.getId();
                        mEditText.clearFocus();
                        mTextInputLayout.setErrorEnabled(false);
                        if (strSkillId != null) {
                            mCheckBoxCount = 0;
                            mSkip = 0;
                            mCheckedQuestion.clear();
                            mSkillQuestionArrayList.clear();
                            mBinding.textviewQuestionCounter.setText("0");
                            fetchQuestionListFromServer(strSkillId, mSkip, mLimit);
                            mBinding.layoutQuestionList.setVisibility(View.GONE);
                            if (mFinalQuestionArrayList.size() == 0) {
                                viewQuestionMenuVisibility(false);
                            }
                        } else {
                            ToastUtils.showToastAlert(getBaseContext(), "Something went wrong");
                        }

                    }
                }

            }
        });


    }

    /**
     * fetch questions by sending selected skill id from server
     *
     * @param strSkillId
     */
    private void fetchQuestionListFromServer(final String strSkillId, final int skip, final int limit) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            if (skip > 0) {
                mBinding.progressBarBottom.setVisibility(View.VISIBLE);
                mBinding.layoutProgress.setVisibility(View.GONE);
            } else {
                mBinding.layoutProgress.setVisibility(View.VISIBLE);
            }

            mBinding.includeMetaData.edittextSkill.setEnabled(false);
            metaFieldEnable(false);
            AnimationUtils.continueBlinkAnimation(mBinding.textViewProgress);
            Observable.create(new ObservableOnSubscribe<java.util.ArrayList<Question>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<Question>> subscriber) throws Exception {
                    final Call<java.util.ArrayList<Question>> questionCall = networkModel.getQuestionFromSkill(strSkillId, skip, limit);
                    Response<ArrayList<Question>> questionResponse = questionCall.execute();
                    if (questionResponse != null && questionResponse.isSuccessful()) {
                        ArrayList<Question> question = questionResponse.body();
                        subscriber.onNext(removeAlreadyAddedQuestions(question, skip));

                    } else if (questionResponse != null && (questionResponse.code() == 401 || questionResponse.code() == 403)) {
                        if (SyncServiceHelper.refreshToken(getBaseContext())) {
                            Response<ArrayList<Question>> questionResponseFail = questionCall.clone().execute();
                            if (questionResponseFail != null && questionResponseFail.isSuccessful()) {
                                ArrayList<Question> question = questionResponseFail.body();
                                subscriber.onNext(removeAlreadyAddedQuestions(question, skip));

                            }
                        }
                    } else if (questionResponse != null && questionResponse.code() == 404) {
                        subscriber.onError(new Throwable(getString(R.string.server_not_responding)));
                    }
                    subscriber.onComplete();

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Question>>() {
                @Override
                public void accept(ArrayList<Question> questions) throws Exception {
                    mBinding.layoutProgress.setVisibility(View.GONE);
                    mBinding.progressBarBottom.setVisibility(View.GONE);
                    mBinding.includeMetaData.edittextSkill.setEnabled(true);
                    metaFieldEnable(true);

                    if (questions.size() > 0) {
                        mSkip += questions.size();
                        mPreviousTotal = questions.size();

                        for (int i = 0; i < mPreviousTotal; i++) {
                            mCheckedQuestion.add(false);
                        }
                        setQuestionSelectionCount(mCheckBoxCount, mCheckedQuestion.size());
                        addQuestionToAdapter(questions);

                        mBinding.layoutQuestionList.setVisibility(View.VISIBLE);
                        mBinding.buttonLoadMore.setVisibility(View.VISIBLE);
                    } else {
                        if (skip == 0) {
                            mBinding.layoutQuestionList.setVisibility(View.GONE);
                        }
                    }

                    if (questions.size() < limit) {
                        mBinding.recyclerViewSkillQuestions.removeOnScrollListener(null);
                        mBinding.buttonLoadMore.setVisibility(View.GONE);
                        if (skip > 0) {
                            Toast.makeText(getBaseContext(), "No more questions", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    SnackBarUtils.showColoredSnackBar(getBaseContext(), mBinding.toolbar, throwable.getMessage(), ContextCompat.getColor(getBaseContext(), R.color.colorRed));
                    metaFieldEnable(true);
                    mBinding.includeMetaData.edittextSkill.setEnabled(true);
                    mBinding.layoutProgress.setVisibility(View.GONE);
                    throwable.printStackTrace();
                }
            }, new Action() {
                @Override
                public void run() throws Exception {

                    metaFieldEnable(true);
                    mBinding.includeMetaData.edittextSkill.setEnabled(true);
                    mBinding.layoutProgress.setVisibility(View.GONE);
                }
            });

        } else {
            metaFieldEnable(true);
            mBinding.includeMetaData.edittextSkill.setEnabled(true);
            mBinding.layoutProgress.setVisibility(View.GONE);
            SnackBarUtils.showColoredSnackBar(getBaseContext(), mBinding.toolbar, getString(R.string.connect_internet), ContextCompat.getColor(getBaseContext(), R.color.colorRed));
        }
    }

    private void addQuestionToAdapter(ArrayList<Question> questions) {
        if (mQuestionAdapter != null) {
            mQuestionAdapter.addValues(questions);
        }

    }

    private void alertDialog(final String title, final String message) {
        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                new AlertDialog.Builder(QuizAssemblerActivity.this)
                        .setTitle(title)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

    }

    private ArrayList<Question> removeAlreadyAddedQuestions(ArrayList<Question> question, int skip) {
        ArrayList<Question> finalQuestionArrayList = new ArrayList<>();
        if (question.size() == 0 && skip == 0) {
            alertDialog("No questions found !", "No questions available for selected skill.\nPlease select another skill.");
        } else {
            if (mSelectedQuestionHashMap.size() > 0) {
                for (Question question1 : question) {
                    if (!mSelectedQuestionHashMap.containsKey(question1.getUidQuestion())) {
                        finalQuestionArrayList.add(question1);
                    }
                }
                if (finalQuestionArrayList.size() == 0) {
                    alertDialog(String.valueOf(question.size()) + " Questions found !", "You have already added all questions to the quiz." +
                            "\nTo add more questions please select another skill.");
                }

            } else {
                return question;
            }

        }


        return finalQuestionArrayList;
    }

    /**
     * set up recycler view and viewpager with question data
     *
     * @param question
     */
    private void initializeQuestionRecyclerView(final ArrayList<Question> question) {
        mSkillQuestionArrayList = question;
        resetBooleanArray(mSkillQuestionArrayList);
        mBinding.recyclerViewSkillQuestions.setHasFixedSize(true);
        mBinding.recyclerViewSkillQuestions.setNestedScrollingEnabled(false);
        setQuestionSelectionCount(mCheckBoxCount, mSkillQuestionArrayList.size());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewSkillQuestions.setLayoutManager(layoutManager);
        mQuestionAdapter = new QuestionAdapter(mSkillQuestionArrayList);
        mBinding.recyclerViewSkillQuestions.setAdapter(mQuestionAdapter);
        mBinding.buttonLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchQuestionListFromServer(strSkillId, mSkip, mLimit);

            }
        });
        mBinding.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) mBinding.scrollView.getChildAt(mBinding.scrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (mBinding.scrollView.getHeight() + mBinding.scrollView.getScrollY()));

                if (diff == 0) {
                    Toast.makeText(getBaseContext(), "Load More", Toast.LENGTH_SHORT).show();
                    fetchQuestionListFromServer(strSkillId, mSkip, mLimit);
                }
            }
        });

        if (layoutManager != null) {
//            mBinding.recyclerViewSkillQuestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                    if (dy > 0) {
//                        if (layoutManager.findLastCompletelyVisibleItemPosition() == mPreviousTotal - 1) {
//
//                            fetchQuestionListFromServer(strSkillId, mSkip, mLimit);
//
//                        }
//                    }
//
//                }
//
//            });
        }

        mBinding.textviewTotalQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.checkBoxSelectAllQuestions.performClick();
            }
        });

        mBinding.checkBoxSelectAllQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mBinding.checkBoxSelectAllQuestions.isChecked();
                if (mCheckedQuestion.size() >= mSkillQuestionArrayList.size()) {
                    for (int i = 0; i < mSkillQuestionArrayList.size(); i++) {
                        mCheckedQuestion.set(i, isChecked);
                        mSelectedQuestionHashMap.put(mSkillQuestionArrayList.get(i).getUidQuestion(), mSkillQuestionArrayList.get(i));
                    }
                    mQuestionAdapter.notifyDataSetChanged();
                }

                if (isChecked) {
                    mCheckBoxCount = mSkillQuestionArrayList.size();
                    setQuestionSelectionCount(mCheckBoxCount, mSkillQuestionArrayList.size());
                } else {
                    mCheckBoxCount = 0;
                    setQuestionSelectionCount(mCheckBoxCount, mSkillQuestionArrayList.size());
                    mSelectedQuestionHashMap.clear();
                }

                if (mSelectedQuestionHashMap.size() > 0) {
                    metaFieldEnable(false);
                } else if (mSelectedQuestionHashMap.size() == 0) {
                    metaFieldEnable(true);
                }

            }
        });

        mBinding.buttonPreviewAllQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isQuestionSelectable = true;
                setUpPreviewDialog(mSkillQuestionArrayList);


            }
        });


        mBinding.buttonActionExportQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ArrayList<Question>(mSelectedQuestionHashMap.values()).size() > 0) {
                    mBinding.includeSelectedQuestion.recyclerViewSelectedQuestions.getItemAnimator().setChangeDuration(0);
                    ((DefaultItemAnimator) mBinding.includeSelectedQuestion.recyclerViewSelectedQuestions.getItemAnimator()).setSupportsChangeAnimations(false);
                    //  mBinding.includeSelectedQuestion.recyclerViewSelectedQuestions.setNestedScrollingEnabled(false);
                    mBinding.includeSelectedQuestion.recyclerViewSelectedQuestions.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
                    SelectedQuestionAdapter mSelectedQuestionAdapter = new SelectedQuestionAdapter(new ArrayList<Question>(mSelectedQuestionHashMap.values()));
                    mBinding.includeSelectedQuestion.recyclerViewSelectedQuestions.setAdapter(mSelectedQuestionAdapter);

                    if (new ArrayList<Question>(mSelectedQuestionHashMap.values()).size() == 1) {
                        ToastUtils.showToastSuccess(getBaseContext(), "Question added to list");
                    } else {
                        ToastUtils.showToastSuccess(getBaseContext(), "Questions added to list");
                    }
                    mSkillQuestionArrayList.removeAll(new ArrayList<Question>(mSelectedQuestionHashMap.values()));
                    resetBooleanArray(mSkillQuestionArrayList);
                    mQuestionAdapter.notifyDataSetChanged();

                    viewQuestionMenuVisibility(true);
                    if (mViewQuestionMenuItem != null) {

                    }
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), "No question selected");
                }


            }
        });

    }

    private void resetBooleanArray(ArrayList<Question> skillQuestionArrayList) {
        mCheckedQuestion = new ArrayList<>();
        for (int i = 0; i < skillQuestionArrayList.size(); i++) {
            mCheckedQuestion.add(false);
        }
        if (mSkillQuestionArrayList.size() == 0) {
            mBinding.layoutQuestionList.setVisibility(View.GONE);
        } else {
            mBinding.layoutQuestionList.setVisibility(View.VISIBLE);
        }
        mCheckBoxCount = 0;
        setQuestionSelectionCount(mCheckBoxCount, mSkillQuestionArrayList.size());
    }

    private void setUpPreviewDialog(ArrayList<Question> questions) {

        mPreviewDialog = new Dialog(QuizAssemblerActivity.this);
        mPreviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPreviewDialog.setContentView(R.layout.layout_quiz_assembler_viewpager_dialog);
        mPreviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewPagerQuestionPreview = (ViewPager) mPreviewDialog.findViewById(R.id.viewPagerQuestionPreview);
        QuestionViewPagerAdapter mQuestionViewPagerAdapter = new QuestionViewPagerAdapter(getBaseContext(), questions);
        viewPagerQuestionPreview.setAdapter(mQuestionViewPagerAdapter);
        viewPagerQuestionPreview.setOffscreenPageLimit(3);
        viewPagerQuestionPreview.setClipToPadding(false);
        viewPagerQuestionPreview.setPadding(30, 0, 30, 0);
        isDialogShown = true;
        //AnimationUtils.pushUpEnter(getBaseContext(), mBinding.viewPagerQuestionPreview);
        // mBinding.textviewToolbar.setText(getString(R.string.questions));
        // mBinding.layoutQuestionSelection.setVisibility(View.GONE);
        if (getResources().getBoolean(R.bool.isTablet)) {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            Double mDialogWidth = metrics.widthPixels * 0.95;
            Double mDialogHeight = metrics.heightPixels * 0.85;
            Window win = mPreviewDialog.getWindow();
            win.setLayout(mDialogWidth.intValue(), mDialogHeight.intValue());
            mPreviewDialog.show();

        } else {
            Window window = mPreviewDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            mPreviewDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mPreviewDialog.show();
        }

        mPreviewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onBackPressed();
            }
        });

    }

    public class MetaDataAdapter extends RecyclerView.Adapter<MetaDataAdapter.ViewHolder> {

        public static final int TYPE_GRADE = 101;
        public static final int TYPE_SUBJECT = 102;
        public static final int TYPE_TOPIC = 103;
        public static final int TYPE_SKILL = 104;
        ArrayList<Object> metaDataArrayList = new ArrayList<Object>();
        Context mContext;
        View.OnClickListener mItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        private int type;

        public MetaDataAdapter(Context context, ArrayList<Object> alMetaData, int viewType) {
            mContext = context;
            metaDataArrayList = alMetaData;
            this.type = viewType;
        }

        public void setItemClickAction(View.OnClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

        @Override
        public MetaDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_metadata_spinner_item, parent, false);

            MetaDataAdapter.ViewHolder vh = new MetaDataAdapter.ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MetaDataAdapter.ViewHolder holder, final int position) {
            holder.mItemValueTxt.setOnClickListener(mItemClickListener);

            switch (type) {

                case TYPE_GRADE:
                    GradeExt gradeExt = (GradeExt) metaDataArrayList.get(position);
                    holder.mItemValueTxt.setText(gradeExt.getName());
                    holder.mItemValueTxt.setTag(gradeExt);
                    break;
                case TYPE_SUBJECT:
                    Subject subject = (Subject) metaDataArrayList.get(position);
                    holder.mItemValueTxt.setText(subject.getName());
                    holder.mItemValueTxt.setTag(subject);
                    break;
                case TYPE_TOPIC:
                    Topic topic = (Topic) metaDataArrayList.get(position);
                    holder.mItemValueTxt.setText(topic.getName());
                    holder.mItemValueTxt.setTag(topic);
                    break;
                case TYPE_SKILL:
                    Skill skill = (Skill) metaDataArrayList.get(position);
                    holder.mItemValueTxt.setText(skill.getSkillName());
                    holder.mItemValueTxt.setTag(skill);
                    break;

                default:
                    holder.mItemValueTxt.setText("");
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return metaDataArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mItemValueTxt;

            public ViewHolder(View v) {
                super(v);
                mItemValueTxt = (TextView) v.findViewById(R.id.textview_quiz_metadata_value);

            }

        }
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
        private ArrayList<Question> mQuestionArrayList = new ArrayList<>();

        public QuestionAdapter(ArrayList<Question> questions) {
            this.mQuestionArrayList = questions;
        }

        @Override
        public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutQuizAssemblerCheckboxItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_quiz_assembler_checkbox_item, parent, false);
            return new QuestionAdapter.ViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(final QuestionAdapter.ViewHolder holder, final int position) {
            final Question question = mQuestionArrayList.get(position);
            holder.mBinding.textViewQuestionTitle.setText(TextViewMore.stripHtml(question.getQuestionText()));
            holder.mBinding.checkBoxQuestion.setChecked(mCheckedQuestion.get(position));
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mBinding.checkBoxQuestion.performClick();
                }
            });
            holder.mBinding.checkBoxQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = holder.mBinding.checkBoxQuestion.isChecked();
                    if (isChecked) {
                        mCheckBoxCount++;
                        setQuestionSelectionCount(mCheckBoxCount, mSkillQuestionArrayList.size());
                        mSelectedQuestionHashMap.put(question.getUidQuestion(), question);
                        if (mCheckBoxCount == mQuestionArrayList.size()) {
                            mBinding.checkBoxSelectAllQuestions.setChecked(true);
                        }
                    } else {
                        mCheckBoxCount--;
                        mBinding.checkBoxSelectAllQuestions.setChecked(false);
                        setQuestionSelectionCount(mCheckBoxCount, mSkillQuestionArrayList.size());
                        mSelectedQuestionHashMap.remove(question.getUidQuestion());
                    }
                    if (position < mCheckedQuestion.size()) {
                        mCheckedQuestion.set(position, isChecked);
                    }

                    if (mSelectedQuestionHashMap.size() > 0) {
                        metaFieldEnable(false);
                    } else if (mSelectedQuestionHashMap.size() == 0) {
                        metaFieldEnable(true);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mQuestionArrayList.size();
        }

        public void addValues(ArrayList<Question> questions) {
            if (mQuestionArrayList != null) {
                mQuestionArrayList.addAll(questions);
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutQuizAssemblerCheckboxItemBinding mBinding;

            public ViewHolder(LayoutQuizAssemblerCheckboxItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }
        }
    }

    public class QuestionViewPagerAdapter extends PagerAdapter {
        Context mContext;
        ArrayList<Question> mQuestionArrayList;

        public QuestionViewPagerAdapter(Context context, ArrayList<Question> questions) {
            mContext = context;
            this.mQuestionArrayList = questions;
        }

        @Override
        public int getCount() {
            return mQuestionArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((CardView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final LayoutQuizAssemblerQuestionPreviewItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.layout_quiz_assembler_question_preview_item, container, false);
            final Question question = mQuestionArrayList.get(position);
            binding.checkboxViewPagerQuestion.setChecked(mCheckedQuestion.get(position));
            binding.textViewQuestionPosition.setText(String.valueOf(position + 1));
            binding.textViewQuestionTotalSize.setText(String.valueOf(mQuestionArrayList.size()));
            binding.textViewViewPagerQuestionTitle.setText(TextViewMore.stripHtml(question.getQuestionText()));
            binding.checkboxViewPagerQuestion.setEnabled(isQuestionSelectable);
            binding.textViewViewPagerQuestionTitle.setEnabled(isQuestionSelectable);
            if (!isQuestionSelectable) {
                binding.checkboxViewPagerQuestion.setChecked(true);
            }
            setQuestionResource(binding, question);
            setQuestionChoices(binding.recyclerViewQuestionChoices, question.getQuestionChoices(), question.getQuestionType());
            setQuestionHints(binding.recyclerViewQuestionHints, question.getQuestionHints());
            setQuestionExplanation(binding, question);
            binding.checkboxViewPagerQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean isChecked = binding.checkboxViewPagerQuestion.isChecked();
                    if (isChecked) {
                        mCheckBoxCount++;
                        mSelectedQuestionHashMap.put(question.getUidQuestion(), question);

                    } else {
                        mCheckBoxCount--;
                        mSelectedQuestionHashMap.remove(question.getUidQuestion());
                    }
                    mCheckedQuestion.set(position, isChecked);
                    if (mSelectedQuestionHashMap.size() > 0) {
                        metaFieldEnable(false);
                    } else if (mSelectedQuestionHashMap.size() == 0) {
                        metaFieldEnable(true);
                    }

                }
            });

            binding.textViewViewPagerQuestionTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.checkboxViewPagerQuestion.performClick();
                }
            });

            binding.textViewQuestionPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    popupWindowList(view, mQuestionArrayList);

                }
            });

            ((ViewPager) container).addView(binding.getRoot());
            return binding.getRoot();
        }

        private void setQuestionResource(final LayoutQuizAssemblerQuestionPreviewItemBinding binding, Question question) {
            String resourcePathImage = question.fetchQuestionImage().getUrlMain();
            String resourcePathVideo = question.fetchQuestionVideo().getUrlMain();
            if (!TextUtils.isEmpty(resourcePathImage)) {
                binding.layoutQuestionResource.setVisibility(View.VISIBLE);
                Picasso.with(getBaseContext()).load(resourcePathImage).into(binding.imageViewResourceThumbnail);
                binding.imageViewResourceThumbnail.setTag(resourcePathImage);
                binding.imageViewResourceType.setImageResource(R.drawable.action_image_w);
            } else if (!TextUtils.isEmpty(resourcePathVideo)) {
                binding.layoutQuestionResource.setVisibility(View.VISIBLE);
                Picasso.with(getBaseContext()).load(resourcePathVideo).into(binding.imageViewResourceThumbnail);
                binding.imageViewResourceThumbnail.setTag(resourcePathVideo);
                binding.imageViewResourceType.setImageResource(R.drawable.action_video_w);
            } else {
                binding.layoutQuestionResource.setVisibility(View.GONE);
            }

            binding.imageViewResourceThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showResource(binding.imageViewResourceThumbnail.getTag().toString());
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((CardView) object);

        }

        /**
         * show question choices
         *
         * @param mChoiceRecyclerView
         * @param questionChoices
         * @param questionType
         */
        private void setQuestionChoices(RecyclerView mChoiceRecyclerView, ArrayList<QuestionChoice> questionChoices, String questionType) {
            mChoiceRecyclerView.setHasFixedSize(true);
            mChoiceRecyclerView.setNestedScrollingEnabled(false);
            mChoiceRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            if (questionType.equals("singleAnswer")) {
                QuestionChoiceRecyclerViewAdapter mQuestionChoiceRecyclerViewAdapter = new QuestionChoiceRecyclerViewAdapter(questionChoices, true);
                mChoiceRecyclerView.setAdapter(mQuestionChoiceRecyclerViewAdapter);
            } else {
                QuestionChoiceRecyclerViewAdapter mQuestionChoiceRecyclerViewAdapter = new QuestionChoiceRecyclerViewAdapter(questionChoices, false);
                mChoiceRecyclerView.setAdapter(mQuestionChoiceRecyclerViewAdapter);
            }

        }

        /**
         * show question hints
         *
         * @param recyclerView
         * @param questionHints
         */
        private void setQuestionHints(RecyclerView recyclerView, ArrayList<QuestionHint> questionHints) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            QuestionHintRecyclerViewAdapter questionHintRecyclerViewAdapter = new QuestionHintRecyclerViewAdapter(questionHints);
            recyclerView.setAdapter(questionHintRecyclerViewAdapter);
        }

        /**
         * show question explanation
         *
         * @param question
         */
        private void setQuestionExplanation(LayoutQuizAssemblerQuestionPreviewItemBinding binding, final Question question) {
            if ((question.getChoiceConfiguration().getQuestionExplanation() != null && !TextUtils.isEmpty(question.getChoiceConfiguration().getQuestionExplanation().getExplanationText())) || !TextUtils.isEmpty(question.getExplanation())) {
                String explanationText = TextViewMore.stripHtml(question.getChoiceConfiguration().getQuestionExplanation().getExplanationText());
                if (TextUtils.isEmpty(explanationText)) {
                    explanationText = TextViewMore.stripHtml(question.getExplanation());
                }
                if (!TextUtils.isEmpty(explanationText)) {
                    binding.layoutViewPagerExplanation.setVisibility(View.VISIBLE);
                    binding.textViewQuestionExplanation.setVisibility(View.VISIBLE);
                    binding.textViewQuestionExplanation.setText(explanationText);
                } else {
                    binding.layoutViewPagerExplanation.setVisibility(View.GONE);
                }
            } else {
                String resourcePath = question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getThumb();
                if (TextUtils.isEmpty(resourcePath)) {
                    resourcePath = question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getUrlMain();
                }
                if (!TextUtils.isEmpty(resourcePath)) {
                    binding.layoutViewPagerExplanation.setVisibility(View.VISIBLE);
                    binding.layoutExplanationResource.setVisibility(View.VISIBLE);
                    String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                    Picasso.with(getBaseContext()).load(resourcePath).into(binding.imageViewExplanationThumbnail);
                    if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                        binding.imageViewExplanationType.setImageResource(R.drawable.action_image_w);
                    } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                        binding.imageViewExplanationType.setImageResource(R.drawable.action_video_w);
                    }

                    binding.imageViewExplanationThumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showResource(question.getChoiceConfiguration().getQuestionExplanation().getExplanationResource().getUrlMain());
                        }
                    });
                } else {
                    binding.layoutViewPagerExplanation.setVisibility(View.GONE);
                }
            }
        }

        /**
         * popup window for showing question counts in list
         *
         * @param view
         * @param mQuestionArrayList
         */
        private void popupWindowList(View view, ArrayList<Question> mQuestionArrayList) {


            LayoutRecyclerviewPopupBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_recyclerview_popup, null, false);
            mBinding.listviewEdittextData.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            QuestionPositionAdapter mQuestionPositionAdapter = new QuestionPositionAdapter(mQuestionArrayList);
            mBinding.listviewEdittextData.setAdapter(mQuestionPositionAdapter);
            int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            int popupWidth = view.getMeasuredWidth() + 60;
            mViewPagerPopupWindow = new PopupWindow(getBaseContext());
            mViewPagerPopupWindow.setContentView(mBinding.getRoot());
            mViewPagerPopupWindow.setWidth(popupWidth);
            mViewPagerPopupWindow.setHeight(popupHeight);
            mViewPagerPopupWindow.setFocusable(true);
            mViewPagerPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            mViewPagerPopupWindow.setOutsideTouchable(true);
            mViewPagerPopupWindow.setElevation(10f);
            mViewPagerPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

            int OFFSET_X = -20;
            int OFFSET_Y = 0;

            int[] location = new int[2];
            view.getLocationOnScreen(location);
            Point point = new Point();
            point.x = location[0];
            point.y = location[1];

            mViewPagerPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, point.x + OFFSET_X, point.y + OFFSET_Y);
            //  mViewPagerPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location.right, location.bottom);

        }

        /**
         * recycler view adapter for question choices
         */
        private class QuestionChoiceRecyclerViewAdapter extends RecyclerView.Adapter<QuestionChoiceRecyclerViewAdapter.ViewHolder> {

            private ArrayList<QuestionChoice> choiceArrayList = new ArrayList<>();
            final boolean isSingleAnswer;

            public QuestionChoiceRecyclerViewAdapter(ArrayList<QuestionChoice> questionChoices, boolean b) {
                this.choiceArrayList = questionChoices;
                this.isSingleAnswer = b;
            }

            @Override
            public QuestionChoiceRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutQuizAssemblerChoiceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_quiz_assembler_choice_item, parent, false);
                return new QuestionChoiceRecyclerViewAdapter.ViewHolder(binding);
            }

            @Override
            public void onBindViewHolder(QuestionChoiceRecyclerViewAdapter.ViewHolder holder, int position) {
                QuestionChoice questionChoice = choiceArrayList.get(position);
                String thumbUrl = questionChoice.getChoiceResource().getUrlMain();
                final String mainUrl = questionChoice.getChoiceResource().getUrlMain();
                String choiceText = questionChoice.getChoiceText();

                if (isSingleAnswer) {
                    holder.mBinding.radioButtonSingleChoice.setVisibility(View.VISIBLE);
                    if (questionChoice.isChoiceCorrect()) {
                        holder.mBinding.radioButtonSingleChoice.setChecked(true);
                    }


                } else {
                    holder.mBinding.checkboxMultipleChoice.setVisibility(View.VISIBLE);
                    if (questionChoice.isChoiceCorrect()) {
                        holder.mBinding.checkboxMultipleChoice.setChecked(true);
                    }


                }

                if (!TextUtils.isEmpty(thumbUrl) && !TextUtils.isEmpty(mainUrl)) {
                    holder.mBinding.imageViewChoiceResource.setVisibility(View.VISIBLE);
                    Picasso.with(getBaseContext()).load(thumbUrl).into(holder.mBinding.imageViewChoiceResource);

                    holder.mBinding.imageViewChoiceResource.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showResource(mainUrl);
                        }
                    });
                }

                if (!TextUtils.isEmpty(choiceText)) {
                    holder.mBinding.textViewQuestionChoice.setVisibility(View.VISIBLE);
                    holder.mBinding.textViewQuestionChoice.setText(TextViewMore.stripHtml(questionChoice.getChoiceText()));
                }


            }

            @Override
            public int getItemCount() {
                return choiceArrayList.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder {

                LayoutQuizAssemblerChoiceItemBinding mBinding;

                public ViewHolder(LayoutQuizAssemblerChoiceItemBinding binding) {
                    super(binding.getRoot());
                    mBinding = binding;
                }
            }
        }

        /**
         * recycler view adapter for question hints
         */
        private class QuestionHintRecyclerViewAdapter extends RecyclerView.Adapter<QuestionHintRecyclerViewAdapter.ViewHolder> {

            private ArrayList<QuestionHint> hintArrayList = new ArrayList<>();

            public QuestionHintRecyclerViewAdapter(ArrayList<QuestionHint> questionHints) {
                this.hintArrayList = questionHints;
            }

            @Override
            public QuestionHintRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutQuizAssemblerHintItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_quiz_assembler_hint_item, parent, false);
                return new QuestionHintRecyclerViewAdapter.ViewHolder(binding);
            }

            @Override
            public void onBindViewHolder(QuestionHintRecyclerViewAdapter.ViewHolder holder, int position) {
                QuestionHint questionHint = hintArrayList.get(position);
                holder.mBinding.textViewHintCount.setText(String.valueOf(position + 1) + " / " + String.valueOf(hintArrayList.size()));
                if (!TextUtils.isEmpty(questionHint.getHintText())) {
                    holder.mBinding.textViewHintText.setVisibility(View.VISIBLE);
                    holder.mBinding.textViewHintText.setText(TextViewMore.stripHtml(questionHint.getHintText()));
                } else {
                    final String resourcePath = questionHint.getHintResource().getUrlMain();
                    if (!TextUtils.isEmpty(resourcePath)) {
                        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
                        holder.mBinding.layoutHintResource.setVisibility(View.VISIBLE);
                        Picasso.with(getBaseContext()).load(resourcePath).resize(350, 250).centerCrop().into(holder.mBinding.imageViewHintResource);
                        if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                            holder.mBinding.imageViewResourceType.setImageResource(R.drawable.action_image_w);
                        } else if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                            holder.mBinding.imageViewResourceType.setImageResource(R.drawable.action_video_w);
                        }

                        holder.mBinding.imageViewHintResource.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showResource(resourcePath);
                            }
                        });
                    }

                }
            }

            @Override
            public int getItemCount() {
                return hintArrayList.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder {

                LayoutQuizAssemblerHintItemBinding mBinding;

                public ViewHolder(LayoutQuizAssemblerHintItemBinding binding) {
                    super(binding.getRoot());
                    mBinding = binding;
                }
            }
        }

        /**
         * recycler view adapter for showing question counts list in popup window
         */
        private class QuestionPositionAdapter extends RecyclerView.Adapter<QuestionPositionAdapter.ViewHolder> {

            ArrayList<Question> questionArrayList = new ArrayList<>();

            public QuestionPositionAdapter(ArrayList<Question> mQuestionArrayList) {
                this.questionArrayList = mQuestionArrayList;
            }

            @Override
            public QuestionPositionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutMetadataSpinnerItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_metadata_spinner_item, parent, false);
                return new QuestionPositionAdapter.ViewHolder(mBinding);
            }

            @Override
            public void onBindViewHolder(QuestionPositionAdapter.ViewHolder holder, final int position) {
                holder.mBinding.textviewQuizMetadataValue.setText(String.valueOf(position + 1));
                holder.mBinding.textviewQuizMetadataValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPagerQuestionPreview.setCurrentItem(position);
                        if (mViewPagerPopupWindow.isShowing()) {
                            mViewPagerPopupWindow.dismiss();
                        }
                    }
                });

            }

            @Override
            public int getItemCount() {
                return questionArrayList.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                LayoutMetadataSpinnerItemBinding mBinding;

                public ViewHolder(LayoutMetadataSpinnerItemBinding binding) {
                    super(binding.getRoot());
                    mBinding = binding;
                }
            }
        }
    }

    public void showResource(String resourcePath) {

        String mimeType = URLConnection.guessContentTypeFromName(resourcePath);
        if (mimeType != null) {
            if (mimeType.contains("image")) {
//                ArrayList<String> pathArrayList = new ArrayList<>();
//                pathArrayList.add("file://" + resourcePath);
                UserProfileActivity.showFullImage(resourcePath, QuizAssemblerActivity.this);

            } else if (mimeType.contains("video")) {
                Resource item = new Resource();
                item.setType("video");
                item.setUrlMain(resourcePath);
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
            }
        }


    }

    private class SelectedQuestionAdapter extends RecyclerView.Adapter<SelectedQuestionAdapter.ViewHolder> {

        public SelectedQuestionAdapter(ArrayList<Question> selectedQuestionArrayList) {
            mFinalQuestionArrayList = selectedQuestionArrayList;
        }

        @Override
        public SelectedQuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutQuizAssemblerSelectedQuestionItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_quiz_assembler_selected_question_item, parent, false);
            return new SelectedQuestionAdapter.ViewHolder(mBinding);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final SelectedQuestionAdapter.ViewHolder holder, final int position) {
            final Question question = mFinalQuestionArrayList.get(position);
            holder.mBinding.buttonRemoveQuestion.setEnabled(true);
            holder.mBinding.buttonRemoveQuestion.setClickable(true);
            holder.mBinding.textViewQuestionNumber.setText(String.valueOf(position + 1));
            holder.mBinding.textViewQuestionTitle.setText(TextViewMore.stripHtml(question.getQuestionText()));

            holder.mBinding.buttonRemoveQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFinalQuestionArrayList.remove(question);
                    mSelectedQuestionHashMap.remove(question.getUidQuestion());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), mFinalQuestionArrayList.size());
                    addRemovedQuestionToMain(question);
                    if (mFinalQuestionArrayList.size() == 0) {
                        holder.mBinding.buttonRemoveQuestion.setEnabled(false);
                        holder.mBinding.buttonRemoveQuestion.setClickable(false);
                        menuAddMoreQuestions();
                        viewQuestionMenuVisibility(false);
                        metaFieldEnable(true);
                    }
                }
            });

        }

        private void addRemovedQuestionToMain(Question question) {

            if (mBinding.includeMetaData.edittextSkill.getTag() != null) {
                Skill skill = (Skill) mBinding.includeMetaData.edittextSkill.getTag();
                if (question.getSkills().contains(skill)) {
                    if (!mSkillQuestionArrayList.contains(question)) {
                        mSkillQuestionArrayList.add(question);
                        resetBooleanArray(mSkillQuestionArrayList);
                        mQuestionAdapter.notifyDataSetChanged();
                    }

                }
            }

        }

        @Override
        public int getItemCount() {
            return mFinalQuestionArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutQuizAssemblerSelectedQuestionItemBinding mBinding;

            public ViewHolder(LayoutQuizAssemblerSelectedQuestionItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }


    }

    public static class GradeExt extends Grade {
        private HashMap<String, QuizMetaDataActivity.SubjectExt> subjectExtHashMap = new HashMap<>();

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
