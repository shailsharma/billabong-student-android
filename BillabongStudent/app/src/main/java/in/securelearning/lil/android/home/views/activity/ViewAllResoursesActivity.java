package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityViewAllResoursesActivityBinding;
import in.securelearning.lil.android.courses.views.fragment.CourseListFragment;
import in.securelearning.lil.android.resources.view.fragment.RecommendedListFragment;


public class ViewAllResoursesActivity extends AppCompatActivity {
    ActivityViewAllResoursesActivityBinding mBinding;
    public static final String CLASS_DETAILS_DATA = "class_details_data";
    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECTS = "subjects";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String TOPIC_NAME = "topic_name";
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String IS_RESOURCE = "is_resources";
    private int mColumnCount = 1;
    private String mSubjectId;
    private ArrayList<String> mSubjects;
    private String mTopicId;
    private String mGradeId;
    private String mSubjectName;
    private String mTopicName;
    private boolean isResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_all_resourses_activity);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handleIntent();
        getSupportActionBar().setTitle(mSubjectName);
        getSupportActionBar().setSubtitle(mTopicName);
        identifyOperation();
    }
    private void identifyOperation() {
        if (isResource) {
            RecommendedListFragment fragment = RecommendedListFragment.newInstanceForTopicBrowse(mSubjects, mTopicId, mGradeId, mColumnCount);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_viewall_resources, fragment);
            fragmentTransaction.commit();
        } else {
            CourseListFragment fragment = CourseListFragment.newInstanceForTopicBrowse(mSubjects, mTopicId, mGradeId, mColumnCount);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_viewall_resources, fragment);
            fragmentTransaction.commit();
        }
    }

    private void handleIntent() {
        Bundle appData = getIntent().getBundleExtra(CLASS_DETAILS_DATA);
        if (appData != null) {
            mColumnCount = appData.getInt(ARG_COLUMN_COUNT);
            mSubjectId = appData.getString(SUBJECT_ID);
            mSubjects = appData.getStringArrayList(SUBJECTS);
            mTopicId = appData.getString(TOPIC_ID);
            mGradeId = appData.getString(GRADE_ID);
            mSubjectName = appData.getString(SUBJECT_NAME);
            mTopicName = appData.getString(TOPIC_NAME);
            isResource = appData.getBoolean(IS_RESOURCE);
        }
    }

    public static Intent getIntent(Context context, int colCount, String topicId, String subjectId, String gradeId, ArrayList<String> subjectIds, String subjectName, String topicName, boolean isResource) {
        Intent intent = new Intent(context, ViewAllResoursesActivity.class);
        Bundle appData = new Bundle();
        appData.putString(SUBJECT_ID, subjectId);
        appData.putStringArrayList(SUBJECTS, subjectIds);
        appData.putString(TOPIC_ID, topicId);
        appData.putString(GRADE_ID, gradeId);
        appData.putString(SUBJECT_NAME, subjectName);
        appData.putString(TOPIC_NAME, topicName);
        appData.putInt(ARG_COLUMN_COUNT, colCount);
        appData.putBoolean(IS_RESOURCE, isResource);
        intent.putExtra(CLASS_DETAILS_DATA, appData);
        return intent;
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
}
