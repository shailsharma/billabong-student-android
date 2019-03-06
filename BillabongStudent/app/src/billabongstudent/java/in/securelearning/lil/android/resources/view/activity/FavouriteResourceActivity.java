package in.securelearning.lil.android.resources.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityFavouriteResourceBinding;
import in.securelearning.lil.android.resources.view.fragment.FavouriteListFragment;


public class FavouriteResourceActivity extends AppCompatActivity {
    ActivityFavouriteResourceBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favourite_resource);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        setTitle(getString(R.string.favorites_resources));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int colCount = 1;
        if (getResources().getBoolean(R.bool.isTablet)) {
            colCount = 2;
        }
        FavouriteListFragment fragment =  FavouriteListFragment.newInstance(colCount);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_main, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
