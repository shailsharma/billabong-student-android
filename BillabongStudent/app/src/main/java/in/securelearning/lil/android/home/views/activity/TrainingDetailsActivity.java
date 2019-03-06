package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.views.activity.AssignmentCompletedTraineeActivity;
import in.securelearning.lil.android.assignments.views.activity.AssignmentCompletedTrainerActivity;
import in.securelearning.lil.android.home.views.fragment.TraineeAssignmentsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainerAssignmentsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainingDetailsFragment;
import in.securelearning.lil.android.learningnetwork.views.activity.PostListActivity;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostListFragment;

/**
 * Created by Chaitendra on 28-Dec-17.
 */

public class TrainingDetailsActivity extends AppCompatActivity implements TrainingDetailsFragment.OnTrainingDetailFragmentInteractionListener {

    public static final String TRAINING_ID = "trainingId";
    public static final String TRAINING_TITLE = "trainingTitle";
    public static final String TRAINING_GROUP_ID = "trainingGroupId";
    public static final String TRAINING_SUBJECT_IDS = "trainingSubjectIds";
    public static final String VIEW_TYPE = "viewType";
    private String mTrainingObjectId, mTrainingGroupId;
    private MenuItem mDoneMenuItem, mFavoritePostsMenuItem;
    private Class mInteractionClass;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);
        handleIntent();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_training_screen, menu);
        mDoneMenuItem = menu.findItem(R.id.action_done);
        mFavoritePostsMenuItem = menu.findItem(R.id.action_favorite_posts);
        doneVisibility(false);
        favoriteVisibility(false);
        return true;
    }

    private void doneVisibility(boolean b) {
        if (mDoneMenuItem != null) {
            mDoneMenuItem.setVisible(b);
        }
    }

    private void favoriteVisibility(boolean b) {
        if (mFavoritePostsMenuItem != null) {
            mFavoritePostsMenuItem.setVisible(b);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_done:
                if (mInteractionClass != null) {
                    if (mInteractionClass.equals(TrainerAssignmentsFragment.class)) {
                        startActivity(AssignmentCompletedTrainerActivity.getStartIntent(getBaseContext(), mTrainingGroupId));
                    } else if (mInteractionClass.equals(TraineeAssignmentsFragment.class)) {
                        startActivity(AssignmentCompletedTraineeActivity.getStartIntent(getBaseContext(), mTrainingGroupId));
                    }
                }
                return true;

            case R.id.action_favorite_posts:
                startActivity(PostListActivity.getIntentForPostList(getBaseContext(), mTrainingGroupId, true));

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context, String objectId, String title, String groupId, ArrayList<String> subjectIds, int viewType) {
        Intent intent = new Intent(context, TrainingDetailsActivity.class);
        intent.putExtra(TRAINING_ID, objectId);
        intent.putExtra(TRAINING_TITLE, title);
        intent.putExtra(TRAINING_GROUP_ID, groupId);
        intent.putExtra(TRAINING_SUBJECT_IDS, subjectIds);
        intent.putExtra(VIEW_TYPE, viewType);
        return intent;
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mTrainingObjectId = getIntent().getStringExtra(TRAINING_ID);
            String trainingTitle = getIntent().getStringExtra(TRAINING_TITLE);
            int viewType = getIntent().getIntExtra(VIEW_TYPE, 0);
            mTrainingGroupId = getIntent().getStringExtra(TRAINING_GROUP_ID);
            ArrayList<String> subjectIds = getIntent().getStringArrayListExtra(TRAINING_SUBJECT_IDS);
            setUpToolbar(trainingTitle);
            final TrainingDetailsFragment fragment = TrainingDetailsFragment.newInstance(getColumnCount(), mTrainingObjectId, mTrainingGroupId, subjectIds, viewType);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerClassPerformance, fragment);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setUpToolbar(String trainingTitle) {
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
        if (!TextUtils.isEmpty(trainingTitle)) {
            setTitle(trainingTitle);
        } else {
            setTitle(getString(R.string.labelTraining));
        }
    }

    private int getColumnCount() {
        if (getResources().getBoolean(R.bool.isTablet)) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public void OnTrainingDetailFragmentInteractionListener(Class aClass) {
        if (aClass.equals(TraineeAssignmentsFragment.class) || aClass.equals(TrainerAssignmentsFragment.class)) {
            mInteractionClass = aClass;
            doneVisibility(true);
            favoriteVisibility(false);
        } else if (aClass.equals(PostListFragment.class)) {
            favoriteVisibility(true);
            doneVisibility(false);

        } else {
            favoriteVisibility(false);
            doneVisibility(false);
        }
    }
}
