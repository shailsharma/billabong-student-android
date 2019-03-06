package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.home.views.fragment.TeacherMapFragmentForClassDetails;

/**
 * Created by Chaitendra on 05-Apr-18.
 */

public class ClassPerformanceActivity extends AppCompatActivity {

    public static final String PERFORMANCE_DATA = "performanceData";
    public static final String SUBJECT_ID = "subjectId";
    public static final String SUBJECTS = "subjects";
    public static final String TOPIC_ID = "topicId";
    public static final String GRADE_ID = "gradeId";
    public static final String SECTION_ID = "sectionId";
    public static final String COLUMN_COUNT = "columnCount";
    public static final String DATE = "date";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_container);
        setUpToolbar();
        handleIntent();
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

    public static Intent getStartIntent(Context context, int columnCount, String subjectId, ArrayList<String> subjects, String topicId, String gradeId, String sectionId, String date) {
        Intent intent = new Intent(context, ClassPerformanceActivity.class);
        Bundle appData = new Bundle();
        appData.putString(SUBJECT_ID, subjectId);
        appData.putStringArrayList(SUBJECTS, subjects);
        appData.putString(TOPIC_ID, topicId);
        appData.putString(GRADE_ID, gradeId);
        appData.putString(SECTION_ID, sectionId);
        appData.putInt(COLUMN_COUNT, columnCount);
        appData.putString(DATE, date);
        intent.putExtra(PERFORMANCE_DATA, appData);
        return intent;
    }

    private void handleIntent() {
        Bundle appData = getIntent().getBundleExtra(PERFORMANCE_DATA);
        if (appData != null) {
            int columnCount = appData.getInt(COLUMN_COUNT);
            String subjectId = appData.getString(SUBJECT_ID);
            ArrayList<String> subjectIds = appData.getStringArrayList(SUBJECTS);
            String topicId = appData.getString(TOPIC_ID);
            String gradeId = appData.getString(GRADE_ID);
            String sectionId = appData.getString(SECTION_ID);
            String date = appData.getString(DATE);

            Fragment fragment = TeacherMapFragmentForClassDetails.newInstance(columnCount, subjectId, subjectIds, topicId, gradeId, sectionId, date);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.layoutContainer, fragment);
            fragmentTransaction.commit();
        }
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLN)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.labelClassPerformance));
    }

}
