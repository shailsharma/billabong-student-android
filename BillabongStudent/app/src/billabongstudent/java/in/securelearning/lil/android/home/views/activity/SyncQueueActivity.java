package in.securelearning.lil.android.home.views.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;

public class SyncQueueActivity extends AppCompatActivity {
    private static final int MENU_SYNC = Menu.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_view);
        setTitle("Sync Queue");
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SimplePagerAdapter adapter = new SimplePagerAdapter();
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, MENU_SYNC, 100, "Sync Now");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_SYNC:
                ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.sync_started));
                SyncServiceHelper.startSyncService(SyncQueueActivity.this);
                break;
        }
        return false;
    }
}

class SimplePagerAdapter extends PagerAdapter {

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}