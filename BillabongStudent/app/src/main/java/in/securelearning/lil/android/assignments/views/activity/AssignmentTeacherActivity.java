package in.securelearning.lil.android.assignments.views.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityAssignmentTeacherBinding;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentTeacherFragment;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.home.interfaces.Filterable;
import in.securelearning.lil.android.home.views.activity.SearchResultListFilterActivity;
import in.securelearning.lil.android.home.views.fragment.FilterFragment;
import in.securelearning.lil.android.provider.SearchSuggestionProvider;
import in.securelearning.lil.android.syncadapter.events.SearchCloseEvent;
import in.securelearning.lil.android.syncadapter.events.SearchOpenEvent;
import in.securelearning.lil.android.syncadapter.events.SearchSubmitEvent;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.home.views.activity.SearchResultListActivity.ASSIGNMENTS;


public class AssignmentTeacherActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, FilterFragment.OnFilterFragmentInteractionListener {
    private ActivityAssignmentTeacherBinding mBinding;
    private MenuItem menuItemSearch, menuItemFilter, menuItemBrowse, menuItemBookmark, menuItemCreateQuiz, menuItemDone;

    private FilterList mFilterList;
    private final int FILTER_TYPE_ASSIGNED = 6;
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_assignment_teacher);
        setTitle(getString(R.string.string_assigned));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpFragment();

    }

    private void setUpFragment() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mBinding.appBarLayout.setElevation(4f);
            String assignmentDate = getIntent().getExtras().getString("date");
            final AssignmentTeacherFragment fragment = AssignmentTeacherFragment.newInstance(assignmentDate);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_assignment_teacher, fragment);
            fragmentTransaction.commit();
            mFilterable = new Filterable() {
                @Override
                public void filter() {
                    if (fragment != null) fragment.filter(mFilterList);
                }
            };
        } else {

            final AssignmentTeacherFragment fragment = new AssignmentTeacherFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_assignment_teacher, fragment);
            fragmentTransaction.commit();
            mFilterable = new Filterable() {
                @Override
                public void filter() {
                    if (fragment != null) fragment.filter(mFilterList);
                }
            };
        }
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, AssignmentTeacherActivity.class);
        return intent;
    }

    public static Intent getStartIntentForCalendar(Context context, String assignmentDate) {
        Intent intent = new Intent(context, AssignmentTeacherActivity.class);
        intent.putExtra("date", assignmentDate);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        menuItemSearch = menu.findItem(R.id.actionSearch);
        menuItemFilter = menu.findItem(R.id.action_filter);
        menuItemBrowse = menu.findItem(R.id.action_browse);
        menuItemBookmark = menu.findItem(R.id.action_bookmark);
        menuItemCreateQuiz = menu.findItem(R.id.action_create_assignment);
        menuItemDone = menu.findItem(R.id.action_done);

        searchVisibility(false);
        browseVisibility(false);
        filterVisibility(true);
        bookmarkVisibility(false);
        createQuizVisibility(false);
        doneVisibility(true);

        final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);

        final SearchView mSearchView = mBinding.searchView;
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        MenuItemCompat.setActionView(menuItemSearch, mSearchView);
        mSearchView.setSearchableInfo(((SearchManager) getSystemService(SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setIconified(true);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryRefinementEnabled(true);
        mSearchView.setQuery("", false);
        ImageView searchViewIcon =
                (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);

        ViewGroup linearLayoutSearchView =
                (ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);
        linearLayoutSearchView.addView(searchViewIcon);
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter suggestionsAdapter = mSearchView.getSuggestionsAdapter();
                Cursor c = suggestionsAdapter.getCursor();
                if ((c != null) && c.moveToPosition(position)) {
                    CharSequence newQuery = suggestionsAdapter.convertToString(c);
                    mSearchView.setQuery(newQuery, true);

                }
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.trim();
                if (!TextUtils.isEmpty(query)) {
                    suggestions.saveRecentQuery(query, null);
                    search(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchOpen();
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onSearchClose();
                return false;
            }
        });
        return true;
    }

    private void searchVisibility(boolean b) {
        if (menuItemSearch != null) {
            menuItemSearch.setVisible(b);
        }
    }

    private void browseVisibility(boolean b) {
        if (menuItemBrowse != null) {
            menuItemBrowse.setVisible(b);
        }
    }

    private void filterVisibility(boolean b) {
        if (menuItemFilter != null) {
            menuItemFilter.setVisible(b);
        }
    }

    private void bookmarkVisibility(boolean b) {
        if (menuItemBookmark != null) {
            menuItemBookmark.setVisible(b);
        }
    }

    private void createQuizVisibility(boolean b) {
        if (menuItemCreateQuiz != null) {
            menuItemCreateQuiz.setVisible(b);
        }
    }

    private void doneVisibility(boolean b) {
        if (menuItemDone != null) {
            menuItemDone.setVisible(b);
        }
    }

    private void onSearchOpen() {
        Injector.INSTANCE.getComponent().rxBus().send(new SearchOpenEvent());
    }

    private void onSearchClose() {
        Injector.INSTANCE.getComponent().rxBus().send(new SearchCloseEvent());
    }

    private void search(String query) {
        if (!TextUtils.isEmpty(query))
            Injector.INSTANCE.getComponent().rxBus().send(new SearchSubmitEvent(query));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_browse) {
            startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", ASSIGNMENTS));
        } else if (id == R.id.action_filter) {
            Observable.create(new ObservableOnSubscribe<FilterList>() {
                @Override
                public void subscribe(ObservableEmitter<FilterList> e) throws Exception {
                    if (mSubjects == null) {
                        mSubjects = PrefManager.getSubjectNames(AssignmentTeacherActivity.this);
                    }
                    mFilterList = buildFilter(FILTER_TYPE_ASSIGNED);

                    if (mFilterList != null) {
                        e.onNext(mFilterList);
                    }
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<FilterList>() {
                        @Override
                        public void accept(FilterList filterList) throws Exception {
                            FilterFragment fragment = FilterFragment.newInstance(mFilterList, "");
                            fragment.show(getSupportFragmentManager(), "FilterFragment");
                        }
                    });
        } else if (id == R.id.action_done) {
            if (getIntent() != null && getIntent().getExtras() != null && !TextUtils.isEmpty(getIntent().getExtras().getString("date"))) {
                String assignmentDate = getIntent().getExtras().getString("date");
                startActivity(AssignmentCompletedTeacherActivity.getStartIntentForCalendar(getBaseContext(),assignmentDate));

            }else {
                startActivity(AssignmentCompletedTeacherActivity.getStartIntent(getBaseContext()));

            }
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private FilterList buildFilter(int filterType) {
        FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
        String title = "";
        if (mSubjects == null || mSubjects.length <= 0) {
            mSubjects = PrefManager.getSubjectNames(this);
        }
        if (filterType == FILTER_TYPE_ASSIGNED) {
            title = getResources().getString(R.string.filter_title_course);
            return builder.addSection(new FilterList.SectionBuilder()
                    .addSectionItems(mSubjects)
                    .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
                    .sectionTitle("SkillMasteryFilter By")
                    .build())
                    .title(title)
                    .build();
        }
        return builder.build();
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

}
