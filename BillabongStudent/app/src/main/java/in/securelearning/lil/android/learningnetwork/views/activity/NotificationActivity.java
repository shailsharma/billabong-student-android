package in.securelearning.lil.android.learningnetwork.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.learningnetwork.views.fragment.NotificationFragment;

public class NotificationActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_container);
        setUpToolbar();
        NotificationFragment fragment = NotificationFragment.newInstance(getResources().getBoolean(R.bool.isTablet) ? 2 : 1);
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

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        return intent;
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Notifications");

    }


}
