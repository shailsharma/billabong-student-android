package in.securelearning.lil.android.assignments.views.activity;

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
import in.securelearning.lil.android.app.databinding.ActivityTeacherAnalysisForStudentBinding;
import in.securelearning.lil.android.home.views.fragment.TeacherMapFragment;


public class TeacherAnalysisForStudentActivity extends AppCompatActivity {
   ActivityTeacherAnalysisForStudentBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_teacher_analysis_for_student);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        setTitle(getResources().getString(R.string.string_nav_teacher_map));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final TeacherMapFragment fragment = TeacherMapFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_teacher_map, fragment);
        fragmentTransaction.commit();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, TeacherAnalysisForStudentActivity.class);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
