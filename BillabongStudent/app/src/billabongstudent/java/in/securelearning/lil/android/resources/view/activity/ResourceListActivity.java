package in.securelearning.lil.android.resources.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityResourceListBinding;
import in.securelearning.lil.android.home.views.activity.NewSearchResourceFilterActivity;
import in.securelearning.lil.android.resources.view.fragment.RecommendResourceFragment2;
import in.securelearning.lil.android.resources.view.fragment.RecommendedListFragment;


public class ResourceListActivity extends AppCompatActivity {
    private ActivityResourceListBinding mBinding;
    public static final String RESOURCE_DATA = "resource_data";
    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECTS = "subjects";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String FLAG = "flag";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TITLE = "title";
    private String mSubjectId;
    private ArrayList<String> mSubjects;
    private String mTopicId;
    private String mGradeId;
    private boolean mFlag;
    private int mColumnCount = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_resource_list);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handleIntent();
    }

    public static Intent getIntentForBrowse(Context context, String title) {
        int colCount = 1;
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            colCount = 2;
        }
        Intent intent = new Intent(context, ResourceListActivity.class);
        Bundle appData = new Bundle();
        appData.putBoolean(FLAG, true);
        appData.putInt(ARG_COLUMN_COUNT, colCount);
        appData.putString(TITLE, title);
        intent.putExtra(RESOURCE_DATA, appData);
        return intent;
    }

    public static Intent getIntentForTopicBrowse(Context context, String subjectId, ArrayList<String> subjects, String topicId, String gradeId, String title) {
        int colCount = 1;
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            colCount = 2;
        }
        Intent intent = new Intent(context, ResourceListActivity.class);
        Bundle appData = new Bundle();
        appData.putString(SUBJECT_ID, subjectId);
        appData.putStringArrayList(SUBJECTS, subjects);
        appData.putString(TOPIC_ID, topicId);
        appData.putString(GRADE_ID, gradeId);
        appData.putBoolean(FLAG, false);
        appData.putInt(ARG_COLUMN_COUNT, colCount);
        appData.putString(TITLE, title);
        intent.putExtra(RESOURCE_DATA, appData);
        return intent;
    }

    private void handleIntent() {
        Bundle appData = getIntent().getBundleExtra(RESOURCE_DATA);
        if (appData != null) {
            String title = appData.getString(TITLE);
            setTitle(title);
            if (appData.containsKey(FLAG)) {
                mFlag = appData.getBoolean(FLAG);
                mColumnCount = appData.getInt(ARG_COLUMN_COUNT);

                if (!mFlag) {
                    getSupportActionBar().setElevation(4f);
                    mSubjectId = appData.getString(SUBJECT_ID);
                    mSubjects = appData.getStringArrayList(SUBJECTS);
                    mTopicId = appData.getString(TOPIC_ID);
                    mGradeId = appData.getString(GRADE_ID);
                    RecommendedListFragment fragment = RecommendedListFragment.newInstanceForTopicBrowse(mSubjects, mTopicId, mGradeId, mColumnCount);
//                    RecommendResourceFragment fragment = RecommendResourceFragment.newInstance(mColumnCount);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_main, fragment);
                    fragmentTransaction.commit();
                } else {
//                    RecommendedListFragment fragment = RecommendedListFragment.newInstanceForBrowse(mColumnCount);
                    getSupportActionBar().setElevation(0f);
                    RecommendResourceFragment2 fragment = RecommendResourceFragment2.newInstance(mColumnCount);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_main, fragment);
                    fragmentTransaction.commit();
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_bookmark) {
            Intent intent = new Intent(this, FavouriteResourceActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_browse) {
//            startActivity(SearchResourcesListFilterActivity.getStartSearchActivityIntent(this, "", RESOURCES));
            int colCount = 1;
            if (getResources().getBoolean(R.bool.isTablet)) {
                colCount = 2;
            }
            startActivity(NewSearchResourceFilterActivity.getIntent(this, colCount));
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
