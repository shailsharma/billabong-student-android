package in.securelearning.lil.android.assignments.views.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityQuizNewBinding;
import in.securelearning.lil.android.app.databinding.LayoutQuizCreationBinding;
import in.securelearning.lil.android.assignments.views.fragment.QuizzesFragment;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.utils.AnimationUtils;
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

import static in.securelearning.lil.android.home.views.activity.SearchResultListActivity.QUIZ;


public class QuizNewActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, FilterFragment.OnFilterFragmentInteractionListener {
    ActivityQuizNewBinding mBinding;
    private MenuItem menuItemSearch, menuItemFilter, menuItemBrowse, menuItemBookmark, menuItemCreateQuiz, menuItemDone;
    private FilterList mFilterList;
    private final int FILTER_TYPE_QUIZ = 4;
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_new);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        setTitle(getString(R.string.label_workspace));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final QuizzesFragment fragment = new QuizzesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_quiz, fragment);
        fragmentTransaction.commit();
        mFilterable = new Filterable() {
            @Override
            public void filter() {
                if (fragment != null) fragment.filter(mFilterList);
            }
        };
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, QuizNewActivity.class);
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
        filterVisibility(false);
        bookmarkVisibility(false);
        createQuizVisibility(true);
        doneVisibility(false);

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
            startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", QUIZ));
        } else if (id == R.id.action_filter) {
            Observable.create(new ObservableOnSubscribe<FilterList>() {
                @Override
                public void subscribe(ObservableEmitter<FilterList> e) throws Exception {
                    if (mSubjects == null) {
                        mSubjects = PrefManager.getSubjectNames(QuizNewActivity.this);
                    }
                    mFilterList = buildFilter(FILTER_TYPE_QUIZ);

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
        } else if (id == R.id.action_create_assignment) {
            showQuizCreationDialog();
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

    private void showQuizCreationDialog() {

        final Dialog dialog = new Dialog(QuizNewActivity.this);
        final LayoutQuizCreationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_quiz_creation, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88000000")));

        AnimationUtils.zoomInFast(getBaseContext(), binding.layoutNew);
        AnimationUtils.zoomInFast(getBaseContext(), binding.layoutAssemble);


        binding.layoutNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), QuizMetaDataActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        binding.layoutAssemble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), QuizAssemblerActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        binding.layoutCreation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                dialog.dismiss();
                return false;
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private FilterList buildFilter(int filterType) {
        FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
        String title = "";
        if (mSubjects == null || mSubjects.length <= 0) {
            mSubjects = PrefManager.getSubjectNames(this);
        }
        if (filterType == FILTER_TYPE_QUIZ) {
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
