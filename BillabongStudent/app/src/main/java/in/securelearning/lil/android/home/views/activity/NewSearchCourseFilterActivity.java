package in.securelearning.lil.android.home.views.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityCourseBrowsePageBinding;
import in.securelearning.lil.android.app.databinding.ActivityNewSearchCourseFilterBinding;
import in.securelearning.lil.android.app.databinding.NewFilterBinding;
import in.securelearning.lil.android.app.databinding.NewFilterItemListBinding;
import in.securelearning.lil.android.app.databinding.OuterFilterLayoutBinding;
import in.securelearning.lil.android.app.databinding.SearchresultListContentHorizontalBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.dataobjects.Category;
import in.securelearning.lil.android.home.dataobjects.CategorySelection;
import in.securelearning.lil.android.home.model.SearchModel;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.provider.SearchSuggestionProvider;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.dataobject.LearningLevelResult;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchFilter;
import in.securelearning.lil.android.syncadapter.dataobject.SearchFilterId;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class NewSearchCourseFilterActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener, OnNewSearchDialogFragmentInteractionListener {
    private ActivityNewSearchCourseFilterBinding mMainBinding;
    static final String COLUMN_COUNT = "count";
    static final String SEARCH_DETAILS_DATA = "serch_details_data";
    static final String TOPIC_ID = "topic_id";
    static final String SUBJECT_ID = "subject_id";
    public static final String CATEGORY_SEARCH = "Category Search";
    SubjectViewAdapter mSubjectViewAdapter;
    SearchItemViewAdapter mSearchItemViewAdapter;
    String mQuery = "";
    static CategorySelection mCategorySelection = new CategorySelection();
    @Inject
    SearchModel mSearchModel;
    static ArrayList<AboutCourseExt> aboutCourseExt;
    static ArrayList<LearningLevelResult> mLearningLevelList;
    static ArrayList<SearchFilterId> mLanguageList;
    static ArrayList<SearchFilter> mCourseTypeList;
    static ArrayList<SearchFilterId> mSubjectList;
    static ArrayList<String> mCourseTypeCheckBoxList = new ArrayList<>();
    static ArrayList<String> mLanguageCheckBoxList = new ArrayList<>();
    static ArrayList<String> mLearningLevelCheckBoxList = new ArrayList<>();
    static ArrayList<String> mInnerLearningLevelCheckBoxList = new ArrayList<>();
    static ArrayList<String> mSubjectCheckBoxList = new ArrayList<>();
    ArrayList<String> mTopicIdList = new ArrayList<>();
    public OnNewSearchDialogFragmentInteractionListener mListener;
    String mTopicId = null;
    String mSubjectId = null;
    private int mTotalResultCount = 0;
    private int mCurrentResultCount = 0;
    private int mDefaultCount = 20;
    private int mColumnCount = 1;
    private boolean isScrollEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_search_course_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getTitle());
        getIntentData();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorGrey77));
        setUpRecycleViewLayout(new ArrayList<AboutCourseExt>());
        //here we check if it is called from SubjectDetailSearchActivity or CourseNewActivity
        identifyOperation();
        mMainBinding.searchView.setOnQueryTextListener(this);
        mMainBinding.txtFilter.setOnClickListener(this);
    }

    private void getIntentData() {
        Bundle appData = getIntent().getBundleExtra(SEARCH_DETAILS_DATA);
        if (appData != null) {
            mColumnCount = appData.getInt(COLUMN_COUNT);
            mTopicId = appData.getString(TOPIC_ID);
            mSubjectId = appData.getString(SUBJECT_ID);
        }
    }

    private void identifyOperation() {
        if (mSubjectId != null && !TextUtils.isEmpty(mSubjectId) && mTopicId != null && !TextUtils.isEmpty(mTopicId)) {
            clearFilter();
            mSubjectCheckBoxList.add(mSubjectId);
            mTopicIdList.add(mTopicId);
            mCategorySelection.setSubjectIds(mSubjectCheckBoxList);
            mCategorySelection.setTopicID(mTopicIdList);
            performSearchOnline("", false);
        } else {
            getSubjectList();
        }
    }

    private void setUpRecycleViewLayout(ArrayList<AboutCourseExt> aboutCourseExts) {
        mSearchItemViewAdapter = new SearchItemViewAdapter(aboutCourseExts);
        mMainBinding.searchItemResultList.setHasFixedSize(true);
        mMainBinding.searchItemResultList.setItemViewCacheSize(40);
        mMainBinding.searchItemResultList.setDrawingCacheEnabled(true);

        LinearLayoutManager layoutManager;
        if (mColumnCount > 1) {
            layoutManager = new GridLayoutManager(NewSearchCourseFilterActivity.this, mColumnCount, GridLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new LinearLayoutManager(NewSearchCourseFilterActivity.this, LinearLayoutManager.VERTICAL, false);
        }
        mMainBinding.searchItemResultList.setLayoutManager(layoutManager);
        mMainBinding.searchItemResultList.setAdapter(mSearchItemViewAdapter);
        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mMainBinding.searchItemResultList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mCurrentResultCount - 1) {
                            if (mCurrentResultCount < mTotalResultCount) {
                                if (GeneralUtils.isNetworkAvailable(NewSearchCourseFilterActivity.this)) {
                                    performSearchOnline(mQuery, true);
                                } else {
                                    ToastUtils.showToastAlert(NewSearchCourseFilterActivity.this, getString(R.string.connect_internet));
                                }
                            } else if (mCurrentResultCount == mTotalResultCount) {
                                mMainBinding.layoutBottomProgress.setVisibility(View.GONE);
//                                mMainBinding.searchItemResultList.clearOnScrollListeners();
                            }
                        }
                    }
                }
            });
        }
        mMainBinding.searchItemResultList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mMainBinding.searchView.clearFocus();
                hideSoftKeyBoard();
                return false;
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_activity, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView mSearchView = mMainBinding.searchView;
        MenuItemCompat.setActionView(searchItem, mSearchView);
        mSearchView.setSearchableInfo(((SearchManager) getSystemService(SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryRefinementEnabled(true);
        mSearchView.setQuery(mQuery, false);
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
        ImageView searchViewIcon =
                (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setQuery(mQuery, true);
            }
        });
        ViewGroup linearLayoutSearchView = (ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);
        linearLayoutSearchView.addView(searchViewIcon);
        return true;
    }

    public static Intent getIntent(Context context, int colCount) {
        Intent intent = new Intent(context, NewSearchCourseFilterActivity.class);
        Bundle appData = new Bundle();
        appData.putInt(COLUMN_COUNT, colCount);
        appData.putString(TOPIC_ID, "");
        appData.putString(SUBJECT_ID, "");
        intent.putExtra(SEARCH_DETAILS_DATA, appData);
        return intent;
    }

    public static Intent getIntent(Context context, int colCount, String topicId, String subjectId) {
        Intent intent = new Intent(context, NewSearchCourseFilterActivity.class);
        Bundle appData = new Bundle();
        appData.putInt(COLUMN_COUNT, colCount);
        appData.putString(TOPIC_ID, topicId);
        appData.putString(SUBJECT_ID, subjectId);
        intent.putExtra(SEARCH_DETAILS_DATA, appData);
        return intent;
    }

    private void getSubjectList() {
        showProgressBar();
        Observable.create(new ObservableOnSubscribe<ArrayList<Category>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Category>> e) throws Exception {
                ArrayList<Category> categories = PrefManager.getCategoryList(NewSearchCourseFilterActivity.this);
                if (categories != null) {
                    e.onNext(categories);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Category>>() {
                    @Override
                    public void accept(ArrayList<Category> categories) throws Exception {
                        if (categories.size() > 0) {
                            LinearLayoutManager layoutManager;
                            if (mColumnCount > 1) {
                                layoutManager = new GridLayoutManager(NewSearchCourseFilterActivity.this, mColumnCount, GridLayoutManager.VERTICAL, false);
                            } else {
                                layoutManager = new LinearLayoutManager(NewSearchCourseFilterActivity.this, LinearLayoutManager.VERTICAL, false);
                            }
                            mMainBinding.subjectList.setLayoutManager(layoutManager);
                            mSubjectViewAdapter = new SubjectViewAdapter(categories);
                            mMainBinding.subjectList.setAdapter(mSubjectViewAdapter);
                            hideProgressbar();
                            mMainBinding.subjectList.setVisibility(View.VISIBLE);
                        } else {
                            hideProgressbar();
                            mMainBinding.layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        hideProgressbar();
                        mMainBinding.layoutNoResult.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void hideProgressbar() {
        mMainBinding.progressBar.setVisibility(View.GONE);
        mMainBinding.layoutBottomProgress.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        mMainBinding.layoutNoResult.setVisibility(View.GONE);
        mMainBinding.searchItemResultList.setVisibility(View.GONE);
        mMainBinding.subjectList.setVisibility(View.GONE);
        mMainBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        query = query.trim().toLowerCase();
        if (query.equalsIgnoreCase(CATEGORY_SEARCH)) {
            query = "";
        } else {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            showProgressBar();
            clearFilter();
            clearCount();
            mMainBinding.layFilter.setVisibility(View.GONE);
            performSearchOnline(query, false);
        } else {
            clearFilter();
            clearCount();
            mMainBinding.subjectList.setVisibility(View.GONE);
            mMainBinding.searchItemResultList.setVisibility(View.GONE);
            mMainBinding.layFilter.setVisibility(View.GONE);
            mMainBinding.txtNoResult.setText("Network not available");
            mMainBinding.layoutNoResult.setVisibility(View.VISIBLE);
            SnackBarUtils.showColoredSnackBar(NewSearchCourseFilterActivity.this, getCurrentFocus(), "Network not available", R.color.colorRed);

//            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
        }
        mMainBinding.searchView.clearFocus();
        return true;
    }

    private void clearCount() {
        mCurrentResultCount = 0;
        mTotalResultCount = 0;
        mSearchItemViewAdapter.clear();
    }

    private void clearFilter() {
        if (mCategorySelection != null && mCategorySelection.getCourseType() != null && mCategorySelection.getCourseType().size() > 0) {
            mCategorySelection.setCourseType(null);
        }
        if (mCategorySelection != null && mCategorySelection.getLanguageIds() != null && mCategorySelection.getLanguageIds().size() > 0) {
            mCategorySelection.setLanguageIds(null);
        }
        if (mCategorySelection != null && mCategorySelection.getLearningLevelIds() != null && mCategorySelection.getLearningLevelIds().size() > 0) {
            mCategorySelection.setLearningLevelIds(null);
        }
        if (mCategorySelection != null && mCategorySelection.getSubjectIds() != null && mCategorySelection.getSubjectIds().size() > 0) {
            mCategorySelection.setSubjectIds(null);
        }
        if (mCategorySelection != null && mCategorySelection.getInnerLearningLevelIds() != null && mCategorySelection.getInnerLearningLevelIds().size() > 0) {
            mCategorySelection.setInnerLearningLevelIds(null);
        }
        if (mCategorySelection != null && mCategorySelection.getTopicID() != null && mCategorySelection.getTopicID().size() > 0) {
            mCategorySelection.setTopicID(null);
        }
        if (mCourseTypeCheckBoxList != null && mCourseTypeCheckBoxList.size() > 0) {
            mCourseTypeCheckBoxList = null;
            mCourseTypeCheckBoxList = new ArrayList<>();
        }
        if (mLanguageCheckBoxList != null && mLanguageCheckBoxList.size() > 0) {
            mLanguageCheckBoxList = null;
            mLanguageCheckBoxList = new ArrayList<>();
        }
        if (mLearningLevelCheckBoxList != null && mLearningLevelCheckBoxList.size() > 0) {
            mLearningLevelCheckBoxList = null;
            mLearningLevelCheckBoxList = new ArrayList<>();
        }
        if (mInnerLearningLevelCheckBoxList != null && mInnerLearningLevelCheckBoxList.size() > 0) {
            mInnerLearningLevelCheckBoxList = null;
            mInnerLearningLevelCheckBoxList = new ArrayList<>();
        }
        if (mSubjectCheckBoxList != null && mSubjectCheckBoxList.size() > 0) {
            mSubjectCheckBoxList = null;
            mSubjectCheckBoxList = new ArrayList<>();
        }
        if (mTopicIdList != null && mTopicIdList.size() > 0) {
            mTopicIdList = null;
            mTopicIdList = new ArrayList<>();
        }
    }

    public void performSearchOnline(final String query, final boolean flag) {
        if (!flag) {
            aboutCourseExt = new ArrayList<>();
            mLearningLevelList = new ArrayList<>();
            mLanguageList = new ArrayList<>();
            mCourseTypeList = new ArrayList<>();
            mSubjectList = new ArrayList<>();
        } else {
            mMainBinding.layoutBottomProgress.setVisibility(View.VISIBLE);
        }
        mMainBinding.layFilter.setVisibility(View.GONE);
        mMainBinding.searchView.clearFocus();
        if (isScrollEnable) {
            isScrollEnable = false;
            Observable observable = Observable.create(new ObservableOnSubscribe<SearchCoursesResults>() {
                @Override
                public void subscribe(ObservableEmitter<SearchCoursesResults> subscriber) {
                    SearchCoursesResults searchResults = mSearchModel.searchOnlineForCoursesEs(NewSearchCourseFilterActivity.this, getCurrentFocus(), query, mCategorySelection, mCurrentResultCount, mDefaultCount);
                    subscriber.onNext(searchResults);
                    subscriber.onComplete();
                }
            });
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<SearchCoursesResults>() {
                        @Override
                        public void accept(SearchCoursesResults searchCoursesResults) {
                            if (searchCoursesResults.getList().size() > 0) {
                                NewSearchCourseFilterActivity.this.mCurrentResultCount += searchCoursesResults.getList().size();
                                NewSearchCourseFilterActivity.this.mTotalResultCount = searchCoursesResults.getTotalResult();
                                aboutCourseExt = searchCoursesResults.getList();
                                mLearningLevelList = searchCoursesResults.getLearningLevelList();
                                mLanguageList = searchCoursesResults.getLanguageList();
                                mCourseTypeList = searchCoursesResults.getCourseTypeList();
                                mSubjectList = searchCoursesResults.getSubjectsList();
                                mMainBinding.txtCountTotalResult.setText(mCurrentResultCount + "/" + mTotalResultCount + " Result found for your search");
                                mMainBinding.subjectList.setVisibility(View.GONE);
                                mMainBinding.layoutNoResult.setVisibility(View.GONE);
                                mMainBinding.layFilter.setVisibility(View.VISIBLE);
                                mMainBinding.searchItemResultList.setVisibility(View.VISIBLE);
                                addValuesToRecyclerView(aboutCourseExt);
                            } else if (flag) {
                                mMainBinding.subjectList.setVisibility(View.GONE);
                                mMainBinding.layFilter.setVisibility(View.GONE);
                                SnackBarUtils.showColoredSnackBar(NewSearchCourseFilterActivity.this, getCurrentFocus(), "Network not responding", R.color.colorRed);
                            } else {
                                mMainBinding.subjectList.setVisibility(View.GONE);
                                mMainBinding.layFilter.setVisibility(View.GONE);
                                mMainBinding.layoutNoResult.setVisibility(View.VISIBLE);
                            }
                            hideProgressbar();
                            mMainBinding.searchView.clearFocus();
                            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                            if (prev != null) {
                                DialogFragment df = (DialogFragment) prev;
                                df.getDialog().dismiss();
                            }
                            hideSoftKeyBoard();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            isScrollEnable = true;
                            if (throwable instanceof SocketTimeoutException) {
                                throwable.printStackTrace();
                                hideProgressbar();
                                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    DialogFragment df = (DialogFragment) prev;
                                    df.getDialog().dismiss();
                                }
                                hideSoftKeyBoard();
                                mMainBinding.subjectList.setVisibility(View.GONE);
                                mMainBinding.layFilter.setVisibility(View.GONE);
                                mMainBinding.txtNoResult.setText("Network not responding");
                                mMainBinding.layoutNoResult.setVisibility(View.VISIBLE);
                                mMainBinding.searchView.clearFocus();
                            } else if (throwable instanceof Exception) {
                                hideProgressbar();
                                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    DialogFragment df = (DialogFragment) prev;
                                    df.getDialog().dismiss();
                                }
                                hideSoftKeyBoard();
                                mMainBinding.subjectList.setVisibility(View.GONE);
                                mMainBinding.layFilter.setVisibility(View.GONE);
                                mMainBinding.txtNoResult.setText("Network error");
                                mMainBinding.layoutNoResult.setVisibility(View.VISIBLE);
                                mMainBinding.searchView.clearFocus();
                            }
                            throwable.printStackTrace();
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            isScrollEnable = true;
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        if (mMainBinding.searchItemResultList.getVisibility() == View.VISIBLE) {
            clearFilter();
            clearCount();
            mMainBinding.layFilter.setVisibility(View.GONE);
            mMainBinding.searchItemResultList.setVisibility(View.GONE);
            mMainBinding.subjectList.setVisibility(View.VISIBLE);
            mMainBinding.searchView.clearFocus();
        } else {
            super.onBackPressed();
        }
    }

    private void addValuesToRecyclerView(ArrayList<AboutCourseExt> aboutCourseExtsList) {
        if (aboutCourseExtsList.size() > 0) {
            mSearchItemViewAdapter.addValues(aboutCourseExtsList);
        }
    }

    private void buildCustomFilter() {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        Fragment prev = this.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        FilterDialog dialogFragment = FilterDialog.newInstance();
        dialogFragment.show(ft, "dialog");
    }

    private void hideSoftKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.trim().toLowerCase();
        mQuery = newText;
        if (newText.isEmpty()) {
            clearFilter();
            clearCount();
            mMainBinding.layFilter.setVisibility(View.GONE);
            mMainBinding.searchItemResultList.setVisibility(View.GONE);
            mMainBinding.subjectList.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_filter:
                buildCustomFilter();
        }
    }

    @Override
    public void applyFilter() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
        showProgressBar();
        clearCount();
        performSearchOnline(mQuery, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class SubjectViewAdapter extends RecyclerView.Adapter<SubjectViewAdapter.ViewHolder> {
        ArrayList<Category> categoriesList;

        SubjectViewAdapter(ArrayList<Category> categories) {
            categoriesList = categories;
        }

        @Override
        public SubjectViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ActivityCourseBrowsePageBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.activity_course_browse_page, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Category subjectValue = categoriesList.get(position);
            holder.subjectBinding.textviewTitle.setText(subjectValue.getName());
            holder.subjectBinding.textviewTitle.setBackgroundColor(subjectValue.getColor());
            holder.subjectBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSubjectDetailSearch(subjectValue.getId(), subjectValue.getColor(), subjectValue.getName());
                }
            });
            holder.subjectBinding.getRoot().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyBoard();
                    return false;
                }
            });
        }

        private void showSubjectDetailSearch(String subjectId, int subjectColor, String subjectName) {
            if (GeneralUtils.isNetworkAvailable(NewSearchCourseFilterActivity.this)) {
                startActivity(SubjectDetailSearchCourseActivity.getIntent(getApplicationContext(), subjectId, subjectColor, subjectName));
            } else {
                ToastUtils.showToastAlert(NewSearchCourseFilterActivity.this, getString(R.string.connect_internet));
            }


//            if (GeneralUtils.isNetworkAvailable(NewSearchCourseFilterActivity.this)) {
//                showProgressBar();
//                clearFilter();
//                mSubjectCheckBoxList.add(subjectId);
//                mCategorySelection.setSubjectIds(mSubjectCheckBoxList);
//                performSearchOnline("", false);
//            } else {
//                ToastUtils.showToastAlert(NewSearchCourseFilterActivity.this, getString(R.string.connect_internet));
//            }
        }

        @Override
        public int getItemCount() {
            return categoriesList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ActivityCourseBrowsePageBinding subjectBinding;

            public ViewHolder(ActivityCourseBrowsePageBinding v) {
                super(v.getRoot());
                subjectBinding = v;
            }
        }
    }

    private class SearchItemViewAdapter extends RecyclerView.Adapter<SearchItemViewAdapter.ItemViewHolder> {
        ArrayList<AboutCourseExt> aboutCourseExts;

        SearchItemViewAdapter(ArrayList<AboutCourseExt> aboutCourseExts) {
            this.aboutCourseExts = aboutCourseExts;
        }

        public void addValues(ArrayList<AboutCourseExt> aboutCourseExtsList) {
            if (aboutCourseExts != null) {
                aboutCourseExts.addAll(aboutCourseExtsList);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (aboutCourseExts != null) {
                aboutCourseExts.clear();
                notifyDataSetChanged();
            }
        }


        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SearchresultListContentHorizontalBinding searchResultBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.searchresult_list_content_horizontal, parent, false);
            return new ItemViewHolder(searchResultBinding);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            final AboutCourseExt item = aboutCourseExts.get(position);
            String title = "";
            String imageFilePath = "";
            String topic = "";
            Class objectClass = null;
            int typeImage = R.drawable.digital_book;
            String typeExt = item.getMicroCourseType().toLowerCase();
            if (item.getCourseType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
                typeImage = R.drawable.digital_book;
            } else if (item.getCourseType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
                typeImage = R.drawable.video_course;
            } else if (item.getCourseType().contains("feature")) {
                objectClass = MicroLearningCourse.class;
                typeImage = R.drawable.video_course;
            } else if (typeExt.contains("map")) {
                objectClass = ConceptMap.class;
                typeImage = R.drawable.concept_map;
            } else if (typeExt.contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
                typeImage = R.drawable.interactive_image;
            } else if (typeExt.contains("video")) {
                objectClass = InteractiveVideo.class;
                typeImage = R.drawable.interactive_image;
            } else if (item.getPopUpType() != null && !TextUtils.isEmpty(item.getPopUpType().getValue())) {
                objectClass = PopUps.class;
                typeImage = R.drawable.popup;
            }
            final Class finalObjectClass = objectClass;
            holder.searchResultBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalObjectClass.equals(MicroLearningCourse.class)) {
                        startActivity(RapidLearningSectionListActivity.getStartIntent(getBaseContext(), item.getObjectId()));
                    } else {
                        NewSearchCourseFilterActivity.this.startActivity(CourseDetailActivity.getStartActivityIntent(NewSearchCourseFilterActivity.this, item.getObjectId(), finalObjectClass, ""));
                    }
                }
            });
            title = item.getTitle();
            imageFilePath = item.getThumbnail().getUrl();
            topic = item.getMetaInformation().getTopic().getName();
            try {
                if (!imageFilePath.isEmpty()) {
                    Picasso.with(getBaseContext()).load(imageFilePath).into(holder.searchResultBinding.imageView);
                } else {
                    Picasso.with(getBaseContext()).load(typeImage).into(holder.searchResultBinding.imageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.searchResultBinding.textviewTitle.setText(title);
            holder.searchResultBinding.textviewType.setText(topic);
        }

        @Override
        public int getItemCount() {
            return aboutCourseExts.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            SearchresultListContentHorizontalBinding searchResultBinding;

            ItemViewHolder(SearchresultListContentHorizontalBinding itemView) {
                super(itemView.getRoot());
                searchResultBinding = itemView;
            }
        }
    }

    public static class FilterDialog extends DialogFragment implements View.OnClickListener {

        private OnNewSearchDialogFragmentInteractionListener mListener;

        public FilterDialog() {
        }

        public static FilterDialog newInstance() {
            return new FilterDialog();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            NewFilterBinding newFilterBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.new_filter, container, false);
            customSubCourseType(newFilterBinding.layFilter);
            newFilterBinding.btnCancel.setOnClickListener(this);
            return newFilterBinding.getRoot();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // the content
            final RelativeLayout root = new RelativeLayout(getActivity());
            root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // creating the fullscreen dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(root);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return dialog;
        }

        private LinearLayout customSubCourseType(LinearLayout parent) {
            OuterFilterLayoutBinding outerFilterLayoutBinding;
            //CourseType layout
            if (mCourseTypeList.size() > 0) {
                outerFilterLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.outer_filter_layout, null, false);
                outerFilterLayoutBinding.filterTitle.setText("Course Type");
                for (int i = 0; i < mCourseTypeList.size(); i++) {
                    final NewFilterItemListBinding newFilterItemListBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.new_filter_item_list, null, false);
                    final SearchFilter item = mCourseTypeList.get(i);
                    String name = correctCourseName(item.getName());
                    newFilterItemListBinding.txtName.setText(name);
                    newFilterItemListBinding.txtCount.setText(item.getCount() + "");
                    if (mCourseTypeCheckBoxList != null && mCourseTypeCheckBoxList.size() > 0 && mCourseTypeCheckBoxList.contains(item.getName()) && mCourseTypeCheckBoxList.get(i).equalsIgnoreCase(item.getName())) {
                        newFilterItemListBinding.chkBox.setChecked(true);
                    }
                    newFilterItemListBinding.layNewFilterItemCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (newFilterItemListBinding.chkBox.isChecked()) {
                                mCourseTypeCheckBoxList.remove(item.getName());
                            } else {
                                if (!mCourseTypeCheckBoxList.contains(item.getName())) {
                                    mCourseTypeCheckBoxList.add(item.getName());
                                }
                            }
                            applyFilter();
                            if (mListener != null)
                                mListener.applyFilter();
                        }
                    });
                    newFilterItemListBinding.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (!mCourseTypeCheckBoxList.contains(item.getName())) {
                                    mCourseTypeCheckBoxList.add(item.getName());
                                }
                            } else {
                                mCourseTypeCheckBoxList.remove(item.getName());
                            }
                            applyFilter();
                            if (mListener != null)
                                mListener.applyFilter();
                        }
                    });
                    outerFilterLayoutBinding.layInnerFilterDialog.addView(newFilterItemListBinding.getRoot());
                }
                parent.addView(outerFilterLayoutBinding.getRoot());
            }
            //Language layout
            if (mLanguageList.size() > 0) {
                outerFilterLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.outer_filter_layout, null, false);
                outerFilterLayoutBinding.filterTitle.setText("Language");
                for (int i = 0; i < mLanguageList.size(); i++) {
                    final NewFilterItemListBinding newFilterItemListBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.new_filter_item_list, null, false);
                    final SearchFilterId item = mLanguageList.get(i);
                    newFilterItemListBinding.txtName.setText(item.getName());
                    newFilterItemListBinding.txtCount.setText(item.getCount() + "");
                    if (mLanguageCheckBoxList != null && mLanguageCheckBoxList.size() > 0 && mLanguageCheckBoxList.contains(item.getId()) && mLanguageCheckBoxList.get(i).equalsIgnoreCase(item.getId())) {
                        newFilterItemListBinding.chkBox.setChecked(true);
                    }
                    newFilterItemListBinding.layNewFilterItemCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (newFilterItemListBinding.chkBox.isChecked()) {
                                mLanguageCheckBoxList.remove(item.getId());
                            } else {
                                if (!mLanguageCheckBoxList.contains(item.getId())) {
                                    mLanguageCheckBoxList.add(item.getId());
                                }
                            }
                            applyFilter();
                            if (mListener != null)
                                mListener.applyFilter();
                        }
                    });
                    newFilterItemListBinding.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (!mLanguageCheckBoxList.contains(item.getId())) {
                                    mLanguageCheckBoxList.add(item.getId());
                                }
                            } else {
                                mLanguageCheckBoxList.remove(item.getId());
                            }
                            applyFilter();
                            if (mListener != null)
                                mListener.applyFilter();
                        }
                    });
                    outerFilterLayoutBinding.layInnerFilterDialog.addView(newFilterItemListBinding.getRoot());
                }
                parent.addView(outerFilterLayoutBinding.getRoot());
            }
            //LerningLevel layout
            if (mLearningLevelList.size() > 0) {
                outerFilterLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.outer_filter_layout, null, false);
                outerFilterLayoutBinding.filterTitle.setText("Learning Levels");
                for (int i = 0; i < mLearningLevelList.size(); i++) {
                    final NewFilterItemListBinding newFilterItemListBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.new_filter_item_list, null, false);
                    final LearningLevelResult item = mLearningLevelList.get(i);
                    newFilterItemListBinding.txtName.setText(item.getName());
                    newFilterItemListBinding.txtCount.setText(item.getCount() + "");
                    if (mLearningLevelCheckBoxList != null && mLearningLevelCheckBoxList.size() > 0 && mLearningLevelCheckBoxList.contains(item.getId()) && mLearningLevelCheckBoxList.get(i).equalsIgnoreCase(item.getId())) {
                        newFilterItemListBinding.chkBox.setChecked(true);
                    }
                    newFilterItemListBinding.layNewFilterItemCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (newFilterItemListBinding.chkBox.isChecked()) {
                                mLearningLevelCheckBoxList.remove(item.getId());
                            } else {
                                if (!mLearningLevelCheckBoxList.contains(item.getId())) {
                                    mLearningLevelCheckBoxList.add(item.getId());
                                }
                            }
                            applyFilter();
                            if (mListener != null)
                                mListener.applyFilter();
                        }
                    });
                    newFilterItemListBinding.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (!mLearningLevelCheckBoxList.contains(item.getId())) {
                                    mLearningLevelCheckBoxList.add(item.getId());
                                }
                            } else {
                                mLearningLevelCheckBoxList.remove(item.getId());
                            }
                            applyFilter();
                            if (mListener != null)
                                mListener.applyFilter();
                        }
                    });
                    for (int j = 0; j < item.getGrades().size(); j++) {
                        final LearningLevelResult.Grades innerData = item.getGrades().get(j);
                        final NewFilterItemListBinding inner = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.new_filter_item_list, null, false);
                        inner.txtName.setText(innerData.getName());
                        inner.txtCount.setText(innerData.getCount() + "");
                        if (mInnerLearningLevelCheckBoxList != null && mInnerLearningLevelCheckBoxList.size() > 0 && mInnerLearningLevelCheckBoxList.contains(innerData.getId())) {
                            for (int k = 0; k < mInnerLearningLevelCheckBoxList.size(); k++) {
                                if (mInnerLearningLevelCheckBoxList.get(k).equalsIgnoreCase(innerData.getId())) {
                                    inner.chkBox.setChecked(true);
                                }
                            }
                        }
                        inner.layNewFilterItemCheck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (inner.chkBox.isChecked()) {
                                    mInnerLearningLevelCheckBoxList.remove(innerData.getId());
                                } else {
                                    if (!mInnerLearningLevelCheckBoxList.contains(innerData.getId())) {
                                        mInnerLearningLevelCheckBoxList.add(innerData.getId());
                                    }
                                }
                                applyFilter();
                                if (mListener != null)
                                    mListener.applyFilter();
                            }
                        });
                        inner.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    if (!mInnerLearningLevelCheckBoxList.contains(innerData.getId())) {
                                        mInnerLearningLevelCheckBoxList.add(innerData.getId());
                                    }
                                } else {
                                    mInnerLearningLevelCheckBoxList.remove(innerData.getId());
                                }
                                applyFilter();
                                if (mListener != null)
                                    mListener.applyFilter();
                            }
                        });
                        newFilterItemListBinding.layInnerLearninfLevel.addView(inner.getRoot());
                    }
                    outerFilterLayoutBinding.layInnerFilterDialog.addView(newFilterItemListBinding.getRoot());
                }
                parent.addView(outerFilterLayoutBinding.getRoot());
            }
            //Subject layout
            if (mSubjectList.size() > 0) {
                outerFilterLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.outer_filter_layout, null, false);
                outerFilterLayoutBinding.filterTitle.setText("Subjects");
                for (int i = 0; i < mSubjectList.size(); i++) {
                    final NewFilterItemListBinding newFilterItemListBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.new_filter_item_list, null, false);
                    final SearchFilterId item = mSubjectList.get(i);
                    newFilterItemListBinding.txtName.setText(item.getName());
                    newFilterItemListBinding.txtCount.setText(item.getCount() + "");
                    if (mSubjectCheckBoxList != null && mSubjectCheckBoxList.size() > 0 && mSubjectCheckBoxList.contains(item.getId()) && mSubjectCheckBoxList.get(i).equalsIgnoreCase(item.getId())) {
                        newFilterItemListBinding.chkBox.setChecked(true);
                    }
                    newFilterItemListBinding.layNewFilterItemCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (newFilterItemListBinding.chkBox.isChecked()) {
                                mSubjectCheckBoxList.remove(item.getId());
                            } else {
                                if (!mSubjectCheckBoxList.contains(item.getId())) {
                                    mSubjectCheckBoxList.add(item.getId());
                                }
                            }
                            applyFilter();
                            if (mListener != null)
                                mListener.applyFilter();
                        }
                    });
                    newFilterItemListBinding.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (!mSubjectCheckBoxList.contains(item.getId())) {
                                    mSubjectCheckBoxList.add(item.getId());
                                }
                            } else {
                                mSubjectCheckBoxList.remove(item.getId());
                            }
                            applyFilter();
                            if (mListener != null)
                                mListener.applyFilter();
                        }
                    });
                    outerFilterLayoutBinding.layInnerFilterDialog.addView(newFilterItemListBinding.getRoot());
                }
                parent.addView(outerFilterLayoutBinding.getRoot());
            }
            return parent;
        }

        private String correctCourseName(String name) {
            String newName = name;
            switch (name) {
                case "activity":
                    newName = "Activity";
                    break;
                case "digitalbook":
                    newName = "Digital Book";
                    break;
                case "do-you-know":
                    newName = "Do You Know";
                    break;
                case "enhanced-learning":
                    newName = "Enhanced Learning";
                    break;
                case "event":
                    newName = "Event";
                    break;
                case "interactivevideo":
                    newName = "Interactive Video";
                    break;
                case "interactiveimage":
                    newName = "Interactive Image";
                    break;
                case "teacher-tip":
                    newName = "Teacher Tip";
                    break;
                case "conceptmap":
                    newName = "Concept Map";
                    break;
                case "videocourse":
                    newName = "Video Course";
                    break;
                case "featuredcard":
                    newName = "Rapid Learning";
                    break;
            }
            return newName;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    dismiss();
                    break;
            }
        }

        private void applyFilter() {
            if (mCourseTypeCheckBoxList.size() > 0) {
                mCategorySelection.setCourseType(mCourseTypeCheckBoxList);
            }
            if (mLanguageCheckBoxList.size() > 0) {
                mCategorySelection.setLanguageIds(mLanguageCheckBoxList);
            }
            if (mLearningLevelCheckBoxList.size() > 0) {
                mCategorySelection.setLearningLevelIds(mLearningLevelCheckBoxList);
            }
            if (mSubjectCheckBoxList.size() > 0) {
                mCategorySelection.setSubjectIds(mSubjectCheckBoxList);
            }
            if (mInnerLearningLevelCheckBoxList.size() > 0) {
                mCategorySelection.setInnerLearningLevelIds(mInnerLearningLevelCheckBoxList);
            }
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof OnNewSearchDialogFragmentInteractionListener) {
                mListener = (OnNewSearchDialogFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnNewSearchDialogFragmentInteractionListener");
            }
        }

        public void dismiss() {
            getDialog().dismiss();
        }
    }
}

interface OnNewSearchDialogFragmentInteractionListener {
    void applyFilter();
}
