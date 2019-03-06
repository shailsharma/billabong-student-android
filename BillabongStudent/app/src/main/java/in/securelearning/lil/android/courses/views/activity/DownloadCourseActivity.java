package in.securelearning.lil.android.courses.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityDownLoadCourseBinding;
import in.securelearning.lil.android.courses.views.fragment.DownloadCourseFragment;

public class DownloadCourseActivity extends AppCompatActivity {
    ActivityDownLoadCourseBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_down_load_course);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        setTitle(getString(R.string.downloadCourse));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int colCount = 1;
        if (getResources().getBoolean(R.bool.isTablet)) {
            colCount = 2;
        }
        DownloadCourseFragment fragment = DownloadCourseFragment.newInstance(colCount);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerCourseDownload, fragment);
        fragmentTransaction.commit();
    }

    public static Intent getStartActivityIntent(Context context) {
        Intent intent = new Intent(context, DownloadCourseActivity.class);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
