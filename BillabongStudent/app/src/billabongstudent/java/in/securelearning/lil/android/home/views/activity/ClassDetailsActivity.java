package in.securelearning.lil.android.home.views.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityClassDetailsBinding;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.model.PeriodicEventsModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.fragment.ClassDetailsFragments;
import in.securelearning.lil.android.quizpreview.activity.PracticeTopicActivity;
import in.securelearning.lil.android.quizpreview.activity.QuestionPlayerActivity;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ClassDetailsActivity extends AppCompatActivity {
    ActivityClassDetailsBinding mBinding;
    public static final String CLASS_DETAILS_DATA = "class_details_data";
    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECTS = "subjects";
    public static final String TITLE = "title";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String TOPIC_ID = "topic_id";
    public static final String TOPIC_NAME = "topic_name";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String DATE = "date";
    public static final String FLAG = "flag";
    private boolean mFlag;
    private int mColumnCount = 1;
    private String mSubjectId;
    private ArrayList<String> mSubjectIds;
    private String mTopicId;
    private String mTopicName;
    private String mGradeId;
    private String mSectionId;
    private String mDate;
    private String mSubjectName;
    private String mTitle;
    @Inject
    PeriodicEventsModel mPeriodicEventsModel;
    @Inject
    HomeModel mHomeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_class_details);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_class_details);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        InjectorHome.INSTANCE.getComponent().inject(this);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handleIntent();
    }

    private void handleIntent() {
        Bundle appData = getIntent().getBundleExtra(CLASS_DETAILS_DATA);
        if (appData != null) {
            if (appData.containsKey(FLAG)) {
                mFlag = appData.getBoolean(FLAG);
                if (!mFlag) {
                    mColumnCount = appData.getInt(ARG_COLUMN_COUNT);
                    mSubjectId = appData.getString(SUBJECT_ID);
                    mSubjectIds = appData.getStringArrayList(SUBJECTS);
                    mTitle = appData.getString(TITLE);
                    mSubjectName = appData.getString(SUBJECT_NAME);
                    mTopicId = appData.getString(TOPIC_ID);
                    mTopicName = appData.getString(TOPIC_NAME);
                    mGradeId = appData.getString(GRADE_ID);
                    mSectionId = appData.getString(SECTION_ID);
                    mDate = appData.getString(DATE);
                    if (TextUtils.isEmpty(mTopicId)) {
                        PeriodNew periodNew = getPeriodicEvent();
                        if (periodNew != null) {
                            mTopicId = periodNew.getTopic().getId();
                            mTopicName = periodNew.getTopic().getName();
//                        mBinding.periodDetail.textViewPeriodNumber.setText(periodNew.getPeriodNo() + "");
//                        mBinding.periodDetail.textViewPeriodTeacherName.setText("assdfasfasdasdasd");
                        } else {
//                        mBinding.periodDetail.getRoot().setVisibility(View.GONE);
                        }
                    }
                    setTitle(mTitle);
                    if (!TextUtils.isEmpty(mTopicName)) {
                        mBinding.toolbar.setSubtitle(mTopicName);
                    }
                    int subjectColor = PrefManager.getColorForSubject(this, mSubjectId);
                    getWindow().setStatusBarColor(subjectColor);
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setBackgroundDrawable(new ColorDrawable(subjectColor));
                    }
                    final ClassDetailsFragments fragment = ClassDetailsFragments.newInstance(mColumnCount, mSubjectId, mSubjectIds, mTopicId, mGradeId, mSectionId, mDate, mSubjectName);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerClassPerformance, fragment);
                    fragmentTransaction.commit();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    private PeriodNew getPeriodicEvent() {
        String topicId = "";
        ArrayList<PeriodNew> periodList = mPeriodicEventsModel.fetchPeriodicEventsBySubjectGradeSection(mSubjectId, mGradeId, mSectionId, DateUtils.getSecondsForMorningFromDate(DateUtils.convertrIsoDate(mDate)), DateUtils.getSecondsForMidnightFromDate(DateUtils.convertrIsoDate(mDate)));
        if (periodList != null && periodList.size() > 0) {
            for (PeriodNew p : periodList) {
                if (!p.isBreak() && p.getTopic() != null) {
                    return p;
                }
            }
        }
        return null;
    }

    public static Intent getStartIntent(Context context, String subjectId, ArrayList<String> subjects, String topicName, String topicId, String gradeId, String sectionId, String date, boolean flag, String subjectName, String title) {
        int colCount = 1;
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            colCount = 2;
        }
        Intent intent = new Intent(context, ClassDetailsActivity.class);
        Bundle appData = new Bundle();
        appData.putString(SUBJECT_ID, subjectId);
        appData.putStringArrayList(SUBJECTS, subjects);
        appData.putString(TOPIC_ID, topicId);
        appData.putString(TOPIC_NAME, topicName);
        appData.putString(GRADE_ID, gradeId);
        appData.putString(SECTION_ID, sectionId);
        appData.putInt(ARG_COLUMN_COUNT, colCount);
        appData.putString(DATE, date);
        appData.putBoolean(FLAG, flag);
        appData.putString(SUBJECT_NAME, subjectName);
        appData.putString(TITLE, title);
        intent.putExtra(CLASS_DETAILS_DATA, appData);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class_detail, menu);
        boolean isTeacher = PermissionPrefsCommon.getAssignmentCreatePermission(this);
        MenuItem item = menu.findItem(R.id.menuStartPractice);
        if (item != null) {
            if (isTeacher) {
                item.setVisible(false);
            } else {
                item.setVisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menuStartPractice) {
            actionStartPractice();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    private void actionStartPractice() {
        ArrayList<String> subjects = new ArrayList<>();
        subjects.add(mSubjectId);
        subjects.addAll(mSubjectIds);
        if (!TextUtils.isEmpty(mTopicId)) {
            fetchCurriculumForClass(mGradeId, subjects, mTopicId);
        } else {
            startActivity(TopicListActivity.getStartIntent(getBaseContext(), mGradeId, subjects, mSubjectName));
        }
    }

    private void fetchCurriculumForClass(String gradeId, ArrayList<String> subjectIds, String topicId) {
        final ProgressDialog progressDialog = ProgressDialog.show(ClassDetailsActivity.this, "", getString(R.string.messagePleaseWait), false);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });
        mHomeModel.getCurriculumList(gradeId, subjectIds, topicId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Curriculum>>() {
                    @Override
                    public void accept(ArrayList<Curriculum> list) throws Exception {
                        progressDialog.dismiss();
                        if (list != null && !list.isEmpty()) {
                            Curriculum curriculum = list.get(0);
                            startActivity(PracticeTopicActivity.getStartIntentForSkills(getBaseContext(), curriculum.getSkills(), curriculum.getTopic().getName(), getString(R.string.label_low), getSkillMap(curriculum)));
                        } else {
                            Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressDialog.dismiss();
                        throwable.printStackTrace();
                    }
                });
    }

    private HomeModel.SkillMap getSkillMap(Curriculum curriculum) {
        HomeModel.SkillMap skillMap = new HomeModel.SkillMap();
        skillMap.setBoard(curriculum.getBoard());
        skillMap.setGrade(curriculum.getGrade());
        skillMap.setLanguage(curriculum.getLang());
        skillMap.setLearningLevel(curriculum.getLearningLevel());
        skillMap.setTopic(curriculum.getTopic());
        skillMap.setSubject(curriculum.getSubject());
        return skillMap;
    }
}
