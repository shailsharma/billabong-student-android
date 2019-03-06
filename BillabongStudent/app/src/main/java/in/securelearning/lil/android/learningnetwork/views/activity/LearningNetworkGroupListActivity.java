package in.securelearning.lil.android.learningnetwork.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.learningnetwork.views.fragment.LearningNetworkGroupListFragment;

/**
 * Created by Chaitendra on 08-Aug-17.
 */

public class LearningNetworkGroupListActivity extends AppCompatActivity {

    public static Intent getIntentForLearningNetwork(Context context) {
        Intent intent = new Intent(context, LearningNetworkGroupListActivity.class);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.layout_activity_learning_network_group_list);
        setUpToolbar();
        handleIntent();
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLN));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLN)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.learning_network));
    }

    private void handleIntent() {
        LearningNetworkGroupListFragment fragment = LearningNetworkGroupListFragment.newInstance(1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerMain, fragment);
        fragmentTransaction.commit();
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
