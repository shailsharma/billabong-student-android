package in.securelearning.lil.android.courses.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityCourseNewBinding;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.courses.views.fragment.CourseFragmentNew;
import in.securelearning.lil.android.courses.views.fragment.RecommendedCourseFragment2;
import in.securelearning.lil.android.home.interfaces.Filterable;
import in.securelearning.lil.android.home.views.activity.NewSearchCourseFilterActivity;
import in.securelearning.lil.android.home.views.fragment.FilterFragment;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;


public class CourseNewActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, FilterFragment.OnFilterFragmentInteractionListener, CourseFragmentNew.OnListFragmentInteractionListener {
    ActivityCourseNewBinding mBinding;
    private MenuItem menuItemBrowse, menuItemDownLoad, menuItemBookMark;
    private FilterList mFilterList;
    private final int FILTER_TYPE_COURSE = 1;
    private String[] mSubjects;
    private Filterable mFilterable;

    @Override
    protected void onStart() {
        super.onStart();
        mSubjects = PrefManager.getSubjectNames(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_new);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        setTitle(getString(R.string.title_courses));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int colCount = 1;
        if (getResources().getBoolean(R.bool.isTablet)) {
            colCount = 2;
        }
//        final CourseFragmentNew fragment = CourseFragmentNew.newInstance(colCount);
        final RecommendedCourseFragment2 fragment = RecommendedCourseFragment2.newInstance(colCount);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_course, fragment);
        fragmentTransaction.commit();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, CourseNewActivity.class);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_screen, menu);
        menuItemBrowse = menu.findItem(R.id.action_browse);
        menuItemDownLoad = menu.findItem(R.id.action_download);
        menuItemBookMark = menu.findItem(R.id.action_bookmark);
        browseVisibility(true);
        downLoadVisibility(true);
        bookMarkVisibility(true);
        return true;
    }


    private void bookMarkVisibility(boolean b) {
        if (menuItemBookMark != null) {
            menuItemBookMark.setVisible(b);
        }
    }

    private void browseVisibility(boolean b) {
        if (menuItemBrowse != null) {
            menuItemBrowse.setVisible(b);
        }
    }

    private void downLoadVisibility(boolean b) {
        if (menuItemDownLoad != null) {
            menuItemDownLoad.setVisible(b);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_browse) {
//            startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", COURSES));
            int colCount = 1;
            if (getResources().getBoolean(R.bool.isTablet)) {
                colCount = 2;
            }
            startActivity(NewSearchCourseFilterActivity.getIntent(this, colCount));
            return true;
        } else if (id == R.id.action_download) {
            startActivity(DownloadCourseActivity.getStartActivityIntent(this));
            return true;
        } else if (id == R.id.action_bookmark) {
            startActivity(FavouriteCourseActivity.getStartActivityIntent(this));
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onSearchRequested();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onFilterFragmentInteraction(FilterList filterList) {
        setFilterOnFragment(filterList);
    }

    private void setFilterOnFragment(FilterList filterList) {
        this.mFilterList = filterList;
        if (mFilterable != null) mFilterable.filter();
    }

    @Override
    public void onListFragmentInteraction(Object object) {

    }
}
