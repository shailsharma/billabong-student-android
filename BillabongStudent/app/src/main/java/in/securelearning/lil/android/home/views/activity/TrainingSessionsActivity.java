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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.home.views.fragment.TraineeSessionsFragment;

/**
 * Created by Chaitendra on 12-Jan-18.
 */

public class TrainingSessionsActivity extends AppCompatActivity {

    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String TITLE_DATE = "titleDate";

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context, String startDate, String endDate, String titleDate) {
        Intent intent = new Intent(context, TrainingSessionsActivity.class);
        intent.putExtra(START_DATE, startDate);
        intent.putExtra(END_DATE, endDate);
        intent.putExtra(TITLE_DATE, titleDate);
        return intent;
    }

    private void handleIntent() {
        if (getIntent() != null) {
            String startDate = getIntent().getStringExtra(START_DATE);
            String endDate = getIntent().getStringExtra(END_DATE);
            String titleDate = getIntent().getStringExtra(TITLE_DATE);
            setUpToolbar(titleDate);
            final TraineeSessionsFragment fragment = TraineeSessionsFragment.newInstance(columnCount(), startDate, endDate, 2);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerClassPerformance, fragment);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setUpToolbar(String titleDate) {
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!TextUtils.isEmpty(titleDate)) {
            setTitle(titleDate);
        } else {
            setTitle(getString(R.string.labelSessions));
        }

    }

    private int columnCount() {
        if (getResources().getBoolean(R.bool.isTablet)) {
            return 2;
        } else {
            return 1;
        }
    }
}
