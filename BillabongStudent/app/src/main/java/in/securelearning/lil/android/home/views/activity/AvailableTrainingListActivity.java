package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.home.views.fragment.AvailableTrainingsFragment;

public class AvailableTrainingListActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_container);
        setUpToolbar();
        int columnCount = getResources().getBoolean(R.bool.isTablet) ? 2 : 1;
        AvailableTrainingsFragment fragment = AvailableTrainingsFragment.newInstance(columnCount);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutContainer, fragment);
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

    private void setUpToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.labelAvailableTraining));

    }

    public static Intent getStartIntent(Context context){
        Intent intent=new Intent(context,AvailableTrainingListActivity.class);
        return intent;
    }
}
