package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LearningMapNewActivityBinding;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.views.fragment.LearningMapFinalFragment;
import in.securelearning.lil.android.home.views.fragment.LearningMapStudentFragment;
import in.securelearning.lil.android.syncadapter.utils.PrefManagerStudentSubjectMapping;


public class LearningMapNewActivity extends AppCompatActivity {
    LearningMapNewActivityBinding mBinding;

    @Inject
    AppUserModel mAppUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.learning_map_new_activity);
        setTitle(getResources().getString(R.string.string_nav_learning_map));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);

        Fragment fragment;
        ArrayList<Subject> subjects = PrefManagerStudentSubjectMapping.getSubjectList(this);
        if (subjects != null && subjects.size() > 0) {
            fragment = LearningMapStudentFragment.newInstance();
        } else {
            fragment = LearningMapFinalFragment.newInstance();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_learningMap, fragment);
        fragmentTransaction.commit();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LearningMapNewActivity.class);
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
