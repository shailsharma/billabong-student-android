package in.securelearning.lil.android.home.views.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivitySearchresultListBinding;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.assignments.views.activity.StudentSummaryActivity;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.ImageUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerActivity;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.dataobjects.Category;
import in.securelearning.lil.android.home.dataobjects.CategorySelection;
import in.securelearning.lil.android.home.dataobjects.SubCategory;
import in.securelearning.lil.android.home.dataobjects.SubSubCategory;
import in.securelearning.lil.android.home.model.SearchModel;
import in.securelearning.lil.android.provider.SearchSuggestionProvider;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesResults;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static in.securelearning.lil.android.home.dataobjects.CategorySelection.CATEGORY_SELECTED;
import static in.securelearning.lil.android.home.dataobjects.CategorySelection.NOTHING_SELECTED;
import static in.securelearning.lil.android.home.dataobjects.CategorySelection.SUB_CATEGORY_SELECTED;
import static in.securelearning.lil.android.home.dataobjects.CategorySelection.SUB_SUB_CATEGORY_SELECTED;

/**
 * An activity representing a list of SearchResults.
 */
public class SearchResultListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final String SEARCH_ON = "search_on";
    public static final String ASSIGNMENTS = "assignments";
    public static final String ASSIGNMENT_RESPONSES = "assignment_responses";
    public static final String COURSES = "courses";
    public static final String RESOURCES = "resources";
    public static final String BLOG = "blog";
    public static final String QUIZ = "quiz";
    public static final String GROUP = "group";
    public static final String CATEGORY_SEARCH = "Category Search";

    private int mGroupPosition = -1;
    private int mPostPosition = -1;

    private final int GROUP_OBJECT = 0;
    private final int POST_DATA_OBJECT = 1;
    private final int POST_RESPONSE_OBJECT = 2;

    //    private static final int[] BACKGROUND_COLORS = {0xFF5C6BC0, 0xFF8D6E63, 0xFF66BB6A, 0xFFAB47BC, 0xFFEC407A, 0xFFEF5350, 0xFFFFA726, 0xFFBDBDBD};
//    private static Random randomGenerator = new Random();
    private FilterList mFilterList;
    private boolean mShowCategory = false;
    private String mSearchOn = "";
    private String mQuery = "";
    private int mProgressBarCount = 0;
    private SimpleItemRecyclerViewAdapter mItemAdapter;
    //    private GroupNdPostItemRecyclerViewAdapter mGroupNdPostItemRecyclerViewAdapter;
    private ActivitySearchresultListBinding mMainBinding;
    @Inject
    RxBus mRxBus;
    @Inject
    SearchModel mSearchModel;
//    @Inject
//    GroupModel mGroupModel;

    ArrayList<Category> mCategories = new ArrayList<>();

    private AlertDialog mFilterDialog;

    private Disposable mSubscription, mSearchDisposable;

    private CategorySelection mCategorySelection = new CategorySelection();

    public static Intent getStartSearchActivityIntent(Context context, String query, String searchOn) {
        Intent intent = new Intent(context, SearchResultListActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        Bundle appData = new Bundle();
        appData.putString(SEARCH_ON, searchOn);
        intent.putExtra(SearchManager.APP_DATA, appData);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_searchresult_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getTitle());

        InjectorHome.INSTANCE.getComponent().inject(this);


        mMainBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mMainBinding.fab.setVisibility(GONE);

        hideProgressBar();

//        mMainBinding.actionFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createFilterDialog();
//                showFilterDialog();
//            }
//        });
        int colCount = 1;
        try {
            colCount = getResources().getInteger(R.integer.search_column_count);
        } catch (Exception e) {

        }
//        if (getResources().getBoolean(R.bool.isTablet)) {
//            mMainBinding.searchresultResults.searchresultList.setLayoutManager(new GridLayoutManager(this, 3));
//            mMainBinding.searchresultCategory.searchresultCategoryList.setLayoutManager(new GridLayoutManager(this, 3));
//        } else {
//            mMainBinding.searchresultResults.searchresultList.setLayoutManager(new LinearLayoutManager(this));
//            mMainBinding.searchresultCategory.searchresultCategoryList.setLayoutManager(new GridLayoutManager(this, 2));
//        }
        if (colCount > 1) {
            mMainBinding.searchresultResults.searchresultList.setLayoutManager(new GridLayoutManager(this, colCount));
            mMainBinding.searchresultCategory.searchresultCategoryList.setLayoutManager(new GridLayoutManager(this, colCount));
        } else {
            mMainBinding.searchresultResults.searchresultList.setLayoutManager(new LinearLayoutManager(this));
            mMainBinding.searchresultCategory.searchresultCategoryList.setLayoutManager(new GridLayoutManager(this, 2));
        }

        setupRecyclerView(mMainBinding.searchresultResults.searchresultList, new ArrayList<BaseDataObject>());

    }

//    private void showFilterDialog() {
//        if (mFilterDialog != null) {
//            mFilterDialog.show();
//        }
//    }

//    private void changeFilterDialogVisibility(boolean show) {
//        if (mFilterDialog != null) {
//            if (show) {
//                mFilterDialog.show();
//            } else {
//                mFilterDialog.hide();
//            }
//        }
//    }

//    private void createFilterDialog() {
//        final int size = mCategories.size();
//        mFilterDialog = new AlertDialog.Builder(this)
//                .setTitle("SkillMasteryFilter")
//                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        checked = Arrays.copyOfRange(tempChecked, 0, size);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        tempChecked = Arrays.copyOfRange(checked, 0, size);
//                    }
//                })
//                .setMultiChoiceItems(subjects, checked, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
//                        tempChecked[i] = b;
//                    }
//                }).create();
//    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        setupCategoryRecyclerView(mMainBinding.searchresultCategory.searchresultCategoryList, mCategories);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                mCategories = PrefManager.getCategoryList(SearchResultListActivity.this);
                e.onNext(true);
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        setupCategoryRecyclerView(mMainBinding.searchresultCategory.searchresultCategoryList, mCategories);
                    }
                });
        mMainBinding.resetCategorySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBlankCategorySelectionView(true);
            }
        });
        listenRxBusEvents();
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        performSearchLocal(mQuery, mSearchOn, mCategorySelection);
    }

    @Override
    public void onBackPressed() {

        if (mCategorySelection.canGoBack()) {
            categorySelectionGoBack();
            if (mSearchDisposable != null)
                mSearchDisposable.dispose();
            hideProgressBar();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) {
                if (event instanceof ObjectDownloadComplete) {
                    if (mItemAdapter != null)
                        Completable.complete().observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() {
                                        mItemAdapter.updateSearchResultData(((ObjectDownloadComplete) event).getId(), ((ObjectDownloadComplete) event).getSyncStatus());
                                    }
                                });


                }


            }
        });
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
            if (appData != null) {
                mSearchOn = appData.getString(SearchResultListActivity.SEARCH_ON);
            }
            if (mMainBinding.searchView != null) mMainBinding.searchView.setQuery(mQuery, false);
            showBlankCategorySelectionView(false);
            if (mSearchOn.equals(ASSIGNMENTS) || mSearchOn.equals(ASSIGNMENT_RESPONSES)) {
                showCategorySelectionHeader(false);
                setTitle("Search Assignments");
                prepareFilterForAssignment();
            } else if (mSearchOn.equals(COURSES)) {
                mShowCategory = true;
                showBlankCategorySelectionView(true);
                setTitle("Search Courses");
                prepareFilterForCourse();
            } else if (mSearchOn.equals(RESOURCES)) {
                showCategorySelectionHeader(false);
                setTitle("Search Resources");
                prepareFilterForResources();
            } else if (mSearchOn.equals(BLOG)) {
                showCategorySelectionHeader(false);
                setTitle("Search Blogs");
                prepareFilterForResources();
            } else if (mSearchOn.equals(QUIZ)) {
                showCategorySelectionHeader(false);
                setTitle("Search Quiz");
                prepareFilterForQuiz();
            } else if (mSearchOn.equals(GROUP)) {
                showCategorySelectionHeader(false);
                setTitle("Search Groups & Post");
                prepareFilterForGroup();
            }
        } else {
            finish();
        }
    }

    private void prepareFilterForQuiz() {
        mFilterList = new FilterList.FilterBuilder().build();
    }

    private void prepareFilterForGroup() {
        mFilterList = new FilterList.FilterBuilder().build();
    }

    private void prepareFilterForResources() {
        mFilterList = new FilterList.FilterBuilder().build();
    }

    private void prepareFilterForCourse() {
        mFilterList = new FilterList.FilterBuilder().build();
    }

    private void prepareFilterForAssignment() {
        mFilterList = new FilterList.FilterBuilder().build();
    }

    private void performSearchLocal(final String query, String searchOn, final CategorySelection categorySelection) {
        if (searchOn.equals(ASSIGNMENTS)) {
            Observable.just(mSearchModel.searchForAssignments(query))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
                        @Override
                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
                            addValuesToRecyclerView(baseDataObjects);
                        }
                    });
//            Observable.just(mSearchModel.searchOnlineForAssignments(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });
        } else if (searchOn.equals(BLOG)) {
            Observable.just(mSearchModel.searchForBlogs(query))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
                        @Override
                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
                            addValuesToRecyclerView(baseDataObjects);
                        }
                    });
//            Observable.just(mSearchModel.searchOnlineForAssignments(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });
        } else if (searchOn.equals(ASSIGNMENT_RESPONSES)) {
            Observable.just(mSearchModel.searchForAssignmentResponses(query))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
                        @Override
                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
                            addValuesToRecyclerView(baseDataObjects);
                        }
                    });
//            Observable.just(mSearchModel.searchOnlineForCourses(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });
//            Observable.just(mSearchModel.searchOnlineForMicroCourses(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });

        } else if (searchOn.equals(COURSES)) {
            Observable.create(new ObservableOnSubscribe<ArrayList<BaseDataObject>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<BaseDataObject>> e) throws Exception {
                    e.onNext(mSearchModel.searchForCourses(query, categorySelection));
                    e.onComplete();
                }
            })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
                        @Override
                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
                            addValuesToRecyclerView(baseDataObjects);
                        }
                    });
//            Observable.just(mSearchModel.searchOnlineForCourses(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });
//            Observable.just(mSearchModel.searchOnlineForMicroCourses(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });

        } else if (searchOn.equals(RESOURCES)) {
            Observable.just(mSearchModel.searchForResources(query))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
                        @Override
                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
                            addValuesToRecyclerView(baseDataObjects);
                        }
                    });
//            Observable.just(mSearchModel.searchOnlineForMicroCourses(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });


        }
//        else if (searchOn.equals(GROUP)) {
//            Observable.just(mGroupModel.searchGroupNdPost(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<List<BaseDataObject>>() {
//                        @Override
//                        public void accept(List<BaseDataObject> dataObjects) {
//
//                            if (mGroupNdPostItemRecyclerViewAdapter == null)
//                                mGroupNdPostItemRecyclerViewAdapter = new GroupNdPostItemRecyclerViewAdapter((ArrayList<BaseDataObject>) dataObjects);
//
//
//                            mGroupNdPostItemRecyclerViewAdapter.mValues.addAll((ArrayList<BaseDataObject>) dataObjects);
//                            mGroupNdPostItemRecyclerViewAdapter.notifyDataSetChanged();
//
//
//                        }
//                    });
//        }
    }


//    private void searchGroupNdPost(String query) {
//        Observable.just(mGroupModel.searchGroupNdPost(query.toLowerCase()))
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<BaseDataObject>>() {
//                    @Override
//                    public void accept(List<BaseDataObject> dataObjects) {
//                        if (mGroupNdPostItemRecyclerViewAdapter == null)
//                            mGroupNdPostItemRecyclerViewAdapter = new GroupNdPostItemRecyclerViewAdapter((ArrayList<BaseDataObject>) dataObjects);
//
//                        mGroupNdPostItemRecyclerViewAdapter.mValues.addAll((ArrayList<BaseDataObject>) dataObjects);
//                        mGroupNdPostItemRecyclerViewAdapter.notifyDataSetChanged();
//                    }
//                });
//    }


    private void performSearchOnline(final String query, String searchOn, final CategorySelection categorySelection) {

        if (searchOn.equals(ASSIGNMENTS)) {
            showProgressBar();
//            Observable.just(mSearchModel.searchOnlineForAssignments(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });
            hideProgressBar();
        } else if (searchOn.equals(BLOG)) {
            showProgressBar();
//            Observable.just(mSearchModel.searchOnlineForAssignments(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });
            hideProgressBar();
        } else if (searchOn.equals(COURSES)) {
            showProgressBar();
            Observable observable = null;
            if (query.isEmpty()) {
                observable = Observable.create(new ObservableOnSubscribe<ArrayList<BaseDataObject>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<BaseDataObject>> subscriber) {
                        subscriber.onNext(mSearchModel.searchOnlineForCoursesBySubjectEs(categorySelection));
                        subscriber.onComplete();
                    }
                });
            } else {
                observable = Observable.create(new ObservableOnSubscribe<ArrayList<AboutCourseExt>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<AboutCourseExt>> subscriber) {
                        SearchCoursesResults searchResults = mSearchModel.searchOnlineForCoursesEs(getBaseContext(),getCurrentFocus(),query, categorySelection, 0, 20);
                        subscriber.onNext(searchResults.getList());
                        subscriber.onComplete();
                    }
                });
            }


            mSearchDisposable = observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
                        @Override
                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
                            if (baseDataObjects.size() == 0) {
                                ToastUtils.showToastAlert(getBaseContext(), "No results found online");
                            }
                            addValuesToRecyclerView(baseDataObjects);
                            hideProgressBar();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            hideProgressBar();
                        }
                    });

        } else if (searchOn.equals(RESOURCES)) {
            showProgressBar();
            Observable observable = Observable.create(new ObservableOnSubscribe<ArrayList<BaseDataObject>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<BaseDataObject>> subscriber) {
                    subscriber.onNext(mSearchModel.searchOnlineForResources(query));
                    subscriber.onComplete();
                }
            });
            observable.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
                        @Override
                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
                            addValuesToRecyclerView(baseDataObjects);
                            hideProgressBar();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            hideProgressBar();
                        }
                    });
//            Observable.just(mSearchModel.searchOnlineForMicroCourses(query))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
//                        @Override
//                        public void accept(ArrayList<BaseDataObject> baseDataObjects) {
//                            addValuesToRecyclerView(baseDataObjects);
//                        }
//                    });

        }
    }

    private void performSearchOnFtp(final String query, String searchOn, CategorySelection categorySelection) {
        if (!query.trim().isEmpty()) {
            if (searchOn.equals(RESOURCES)) {
                showProgressBar();
                Observable observable = Observable.create(new ObservableOnSubscribe<ArrayList<BaseDataObject>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<BaseDataObject>> subscriber) {
                        subscriber.onNext(mSearchModel.searchFtpForResources(query));
                        subscriber.onComplete();
                    }
                });
                observable.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<BaseDataObject>>() {
                            @Override
                            public void accept(ArrayList<BaseDataObject> baseDataObjects) {
                                if (baseDataObjects.size() > 0) {
                                    addValuesToRecyclerView(baseDataObjects);

                                } else {
//                                    ToastUtils.showToastAlert(getBaseContext(), "No resource found on the FTP drive");
                                }
                                hideProgressBar();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                throwable.printStackTrace();
                                hideProgressBar();
                            }
                        });

            } else {
//                hideProgressBar();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_activity, menu);

        final MenuItem searchItem = menu.findItem(R.id.actionSearch);
//        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
//        searchViewIcon.setImageResource(R.drawable.back);
//        searchViewIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SearchResultListActivity.this.onBackPressed();
//            }
//        });
        ViewGroup linearLayoutSearchView =
                (ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);
        linearLayoutSearchView.addView(searchViewIcon);
        return true;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<BaseDataObject> values) {
//        if (!mSearchOn.equals(GROUP)) {
        mItemAdapter = new SimpleItemRecyclerViewAdapter(values);
        recyclerView.setAdapter(mItemAdapter);
//        } else {
//            mGroupNdPostItemRecyclerViewAdapter = new GroupNdPostItemRecyclerViewAdapter(values);
//            recyclerView.setAdapter(mGroupNdPostItemRecyclerViewAdapter);
//        }
    }


    private void setupCategoryRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<Category> values) {
        recyclerView.setAdapter(new CategoryRecyclerViewAdapter(values));
    }

    private void setupSubCategoryRecyclerView(@NonNull final RecyclerView recyclerView, final ArrayList<SubCategory> values) {
        recyclerView.getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
            @Override
            public void onAnimationsFinished() {
                recyclerView.setAdapter(new SubCategoryRecyclerViewAdapter(values));
            }
        });

    }

    private void setupSubSubCategoryRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<SubSubCategory> values) {
        recyclerView.setAdapter(new SubSubCategoryRecyclerViewAdapter(values));
    }

    private void categorySelectionGoBack() {
        if (mCategorySelection.getStage() == SUB_SUB_CATEGORY_SELECTED) {
            mCategorySelection.goBack();
//            setupCategoryRecyclerView(mCategoryRecyclerView, mCategories);
        } else if (mCategorySelection.getStage() == SUB_CATEGORY_SELECTED) {
            mCategorySelection.goBack();
//            setupCategoryRecyclerView(mCategoryRecyclerView, mCategories);
        } else if (mCategorySelection.getStage() == CATEGORY_SELECTED) {
            mCategorySelection.goBack();
//            setupCategoryRecyclerView(mCategoryRecyclerView, mCategories);
        }
        showBlankCategorySelectionView(true);
    }

    private void addValuesToRecyclerView(ArrayList<BaseDataObject> values) {
        mItemAdapter.addValues(values);
    }

    private void setValuesToRecyclerView(ArrayList<BaseDataObject> values) {
        mItemAdapter.setValues(values);
    }

    private void clearAdapter() {
        if (mItemAdapter != null)
            mItemAdapter.clear();

//        if (mGroupNdPostItemRecyclerViewAdapter != null)
//            mGroupNdPostItemRecyclerViewAdapter.clear();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        mGroupPosition = -1;
        mPostPosition = -1;

//        unSubscribe();
        query = query.trim().toLowerCase();
        if (query.equalsIgnoreCase(CATEGORY_SEARCH)) {
            query = "";
        } else {
            SearchRecentSuggestions suggestions =
                    new SearchRecentSuggestions(this,
                            SearchSuggestionProvider.AUTHORITY,
                            SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
        if (query.isEmpty() && mCategorySelection.getStage() <= NOTHING_SELECTED) {
            setupRecyclerView(mMainBinding.searchresultResults.searchresultList, new ArrayList<BaseDataObject>());
            showBlankCategorySelectionView(true);
        } else {
            clearAdapter();
            showBlankCategorySelectionView(false);
            performSearchLocal(query, mSearchOn, mCategorySelection);
            performSearchOnline(query, mSearchOn, mCategorySelection);
//            performSearchOnFtp(query, mSearchOn, mCategorySelection);

//            if (mSearchOn.equals(GROUP))
//                searchGroupNdPost(query);


        }
        mMainBinding.searchView.clearFocus();
        return true;
    }

    private void showProgressBar() {
        if (mProgressBarCount > 0) {
            mProgressBarCount++;
        } else {
            mProgressBarCount = 1;
        }
        if (mMainBinding.progressBar != null && mProgressBarCount > 0) {

            mMainBinding.progressBar.setVisibility(View.VISIBLE);
            mMainBinding.progressBar.bringToFront();
        }
    }

    private void hideProgressBar() {
        if (mProgressBarCount > 0) {
            mProgressBarCount--;
        } else {
            mProgressBarCount = 0;
        }
        if (mMainBinding.progressBar != null && mProgressBarCount <= 0) {

            mMainBinding.progressBar.setVisibility(GONE);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        unSubscribe();
        hideProgressBar();
        newText = newText.trim().toLowerCase();
        if (newText.isEmpty()) {
            setupRecyclerView(mMainBinding.searchresultResults.searchresultList, new ArrayList<BaseDataObject>());
            showBlankCategorySelectionView(true);
        } else {
//            performSearchLocal(newText, mSearchOn);
            showBlankCategorySelectionView(false);
        }
        return true;
    }

    private void showBlankCategorySelectionView(boolean show) {
        if (show && mShowCategory) {
            showCategorySelectionHeading(true);
            showSelectedCategoryTextViews(NOTHING_SELECTED);
            showCategorySelectionView(true);
            resetCategorySelectionView();
            resetSearchResultView();
        } else {
            showCategorySelectionHeading(false);
//            showSelectedCategoryTextViews(NOTHING_SELECTED);
            showCategorySelectionView(false);
//            resetCategorySelectionView();
        }
    }

    private void showSelectedCategoryTextViews(int level) {
        if (level == NOTHING_SELECTED) {
            mMainBinding.subSubCategoryName.setVisibility(View.GONE);
            mMainBinding.subCategoryName.setVisibility(View.GONE);
            mMainBinding.categoryName.setVisibility(View.GONE);
        } else if (level == CATEGORY_SELECTED) {
            mMainBinding.subSubCategoryName.setVisibility(View.GONE);
            mMainBinding.subCategoryName.setVisibility(View.GONE);
            mMainBinding.categoryName.setVisibility(View.VISIBLE);
        } else if (level == SUB_CATEGORY_SELECTED) {
            mMainBinding.subSubCategoryName.setVisibility(View.GONE);
            mMainBinding.subCategoryName.setVisibility(View.VISIBLE);
            mMainBinding.categoryName.setVisibility(View.VISIBLE);
        } else if (level == SUB_SUB_CATEGORY_SELECTED) {
            mMainBinding.subSubCategoryName.setVisibility(View.VISIBLE);
            mMainBinding.subCategoryName.setVisibility(View.VISIBLE);
            mMainBinding.categoryName.setVisibility(View.VISIBLE);
        }

    }

    private void showCategorySelectionHeader(boolean show) {
        if (show) {
            mMainBinding.categorySelectionHeader.setVisibility(View.VISIBLE);
        } else {
            mMainBinding.categorySelectionHeader.setVisibility(View.GONE);
        }

    }

    private void showCategorySelectionHeading(boolean show) {
        if (show) {
            mMainBinding.searchCategoryHeading.setVisibility(View.VISIBLE);
            mMainBinding.resetCategorySelection.setVisibility(View.GONE);
//            mMainBinding.actionFilter.setVisibility(View.GONE);
        } else {
            mMainBinding.searchCategoryHeading.setVisibility(View.GONE);
            mMainBinding.resetCategorySelection.setVisibility(View.VISIBLE);
//            mMainBinding.actionFilter.setVisibility(View.VISIBLE);
        }

    }

    private void showCategorySelectionView(boolean show) {
        if (show) {
            mMainBinding.searchresultResults.searchresultList.setVisibility(View.GONE);
            mMainBinding.searchresultCategory.getRoot().setVisibility(View.VISIBLE);

        } else {
            mMainBinding.searchresultResults.searchresultList.setVisibility(View.VISIBLE);
            mMainBinding.searchresultCategory.getRoot().setVisibility(View.GONE);
        }
    }

    private void resetCategorySelectionView() {
        mCategorySelection = new CategorySelection();
        setupCategoryRecyclerView(mMainBinding.searchresultCategory.searchresultCategoryList, mCategories);
    }

    private void resetSearchResultView() {
        setupRecyclerView(mMainBinding.searchresultResults.searchresultList, new ArrayList<BaseDataObject>());
    }

    private void unSubscribe() {
        if (mSubscription != null) mSubscription.dispose();
        if (mSearchDisposable != null)
            mSearchDisposable.dispose();
    }
//
//    private static int getRandomColor() {
//
//        int randomNumber = randomGenerator.nextInt(BACKGROUND_COLORS.length);
//
//        return BACKGROUND_COLORS[randomNumber];
//    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private HashMap<String, BaseDataObject> mObjects = new HashMap<>();
        private List<BaseDataObject> mValues = new ArrayList<>();

        public SimpleItemRecyclerViewAdapter(List<BaseDataObject> items) {
            mValues = items;
            addToMap(mObjects, items);
        }

        private void addToMap(HashMap<String, BaseDataObject> objects, List<BaseDataObject> items) {
            if (items != null && objects != null) {
                for (BaseDataObject object :
                        items) {
                    if (!objects.containsKey(object.getObjectId())) {
                        objects.put(object.getObjectId(), object);
                    }
                }
            }
        }

        public SimpleItemRecyclerViewAdapter(HashMap<String, BaseDataObject> map) {
            mObjects = map;
            mValues.addAll(map.values());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.searchresult_list_content_horizontal, parent, false);
//                    .inflate(R.layout.searchresult_list_content_assignment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            setItem(holder);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        private void updateSearchResultData(String objectId, SyncStatus syncStatus) {

            if (mObjects.containsKey(objectId)) {
                for (int i = 0; i < mValues.size(); i++) {
                    BaseDataObject object = mValues.get(i);
                    if (object.getObjectId().equals(objectId)) {
                        object.setSyncStatus(syncStatus.toString());
                        notifyItemChanged(i);
                    }
                }
            }
        }

        private void setItem(final ViewHolder holder) {
            final BaseDataObject item = holder.mItem;
            String title = "";
            String subject = "Science";
            String topic = "Topic covered : ";
            String description = "";
            String learningOutcomes = "";
            String imageFilePath = "";
            String duration = "m";
            String type = "";

           /* Button button = (Button) mRightView.findViewById(R.id.fab);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item instanceof DigitalBook) {
                    *//*requires the json of the digital book and the referenced jsons*//*
                        DigitalBookPreviewActivity.startBookPreview(getContext(), GeneralUtils.toGson(item), mItemListModel.getResources((DigitalBook) item), item.getClass());
                    } else {
                    *//*requires the json of the digital book and the referenced jsons*//*
                        DigitalBookPreviewActivity.startBookPreview(getContext(), GeneralUtils.toGson(item), item.getClass());
                    }
                }
            });*/

            holder.mPlay.setVisibility(GONE);
            holder.mDownload.setVisibility(GONE);
            holder.mAvailability.setVisibility(GONE);
            if (item instanceof Assignment) {

                type = ((Assignment) item).getAssignmentType();
                title = ((Assignment) item).getTitle();
                subject = ((Assignment) item).getMetaInformation().getSubject().getName();
                topic += ((Assignment) item).getMetaInformation().getTopic().getName();
                imageFilePath = ((Assignment) item).getThumbnail().getUrl();
                holder.mPlay.setText("View");
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 18-11-2016 go to about page
                        Intent mIntent = new Intent(getBaseContext(), StudentSummaryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("assignment", (Assignment) item);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                    }
                });

            } else if (item instanceof BlogDetails) {

                type = "";
                title = ((BlogDetails) item).getBlogInstance().getTitle();
                subject = "";
                topic += "";
                imageFilePath = ((BlogDetails) item).getBlogInstance().getThumbnail().getUrl();
                holder.mPlay.setText("View");
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 18-11-2016 go to about page
                        WebPlayerActivity.startWebPlayer(getBaseContext(), item.getObjectId(), "","",item.getClass(),"",false);
                    }
                });

            } else if (item instanceof AssignmentResponse) {

                type = ((AssignmentResponse) item).getAssignmentType();
                title = ((AssignmentResponse) item).getAssignmentTitle();
                subject = ((AssignmentResponse) item).getMetaInformation().getSubject().getName();
                topic += ((AssignmentResponse) item).getMetaInformation().getTopic().getName();
                imageFilePath = ((AssignmentResponse) item).getThumbnail().getUrl();
                holder.mPlay.setText("View");
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 18-11-2016 go to about page
                        Intent mIntent = new Intent(getBaseContext(), AssignmentDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("assignmentResponse", (AssignmentResponse) item);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                    }
                });

            } else if (item instanceof Quiz) {

                type = ((Quiz) item).getType();
                title = ((Quiz) item).getTitle();
//                subject = ((Quiz) item).getSubject().getName();
//                topic += ((Quiz) item).getTopic().getName();
                imageFilePath = ((Quiz) item).getThumbnail().getUrl();
                holder.mPlay.setText("View");
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 18-11-2016 go to about page

                    }
                });

            } else if (item instanceof Course) {
                holder.mPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        CourseDetailActivity.startActivity(getBaseContext(), item.getObjectId(), item.getClass());
                        WebPlayerActivity.startWebPlayer(getBaseContext(), holder.mItem.getObjectId(),"","", item.getClass(),"",false);
                    }
                });

                setButtonStatus(item.getSyncStatus(), holder);
                if (item instanceof DigitalBook) {
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadDigitalBook(item.getObjectId());
                        }
                    });
                    type = "Course Book";
                    title = ((DigitalBook) item).getTitle();
                    subject = ((DigitalBook) item).getMetaInformation().getSubject().getName();
                    topic += ((DigitalBook) item).getMetaInformation().getTopic().getName();
                    imageFilePath = ((DigitalBook) item).getBaseImage();
                    description = ((DigitalBook) item).getDescription();
                    duration = ((DigitalBook) item).getMetaInformation().getDuration() + duration;

                } else if (item instanceof InteractiveImage) {
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadInteractiveImage(item.getObjectId());
                        }
                    });
                    type = "Interactive Image";
                    title = ((InteractiveImage) item).getTitle();
                    topic += ((InteractiveImage) item).getMetaInformation().getTopic().getName();
                    duration = ((InteractiveImage) item).getMetaInformation().getDuration() + duration;
                    imageFilePath = ((InteractiveImage) item).getBaseImage();
                    description = ((InteractiveImage) item).getDescription();

                } else if (item instanceof ConceptMap) {
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadConceptMap(item.getObjectId());
                        }
                    });
                    type = "Concept Map";
                    title = ((ConceptMap) item).getTitle();
                    topic += ((ConceptMap) item).getMetaInformation().getTopic().getName();
                    duration = ((ConceptMap) item).getMetaInformation().getDuration() + duration;
                    imageFilePath = ((ConceptMap) item).getThumbnail().getThumb();
                    description = ((ConceptMap) item).getDescription();

                } else if (item instanceof PopUps) {
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadPopUp(item.getObjectId());
                        }
                    });
                    type = "Pop Up";
                    title = ((PopUps) item).getTitle();
                    topic += ((PopUps) item).getMetaInformation().getTopic().getName();
                    duration = ((PopUps) item).getMetaInformation().getDuration() + duration;
                    imageFilePath = ((PopUps) item).getThumbnail().getThumb();
                    description = ((PopUps) item).getDescription();

                } else if (item instanceof VideoCourse) {
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadVideoCourse(item.getObjectId());
                        }
                    });
                    type = "Video Course";
                    title = ((VideoCourse) item).getTitle();
                    topic += ((VideoCourse) item).getMetaInformation().getTopic().getName();
                    duration = ((VideoCourse) item).getMetaInformation().getDuration() + duration;
                    imageFilePath = ((VideoCourse) item).getThumbnail().getThumb();
                    description = ((VideoCourse) item).getDescription();

                } else if (item instanceof InteractiveVideo) {
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadInteractiveVideo(item.getObjectId());
                        }
                    });
                    type = "Interactive Video";
                    title = ((InteractiveVideo) item).getTitle();
                    topic += ((InteractiveVideo) item).getMetaInformation().getTopic().getName();
                    duration = ((InteractiveVideo) item).getMetaInformation().getDuration() + duration;
                    imageFilePath = ((InteractiveVideo) item).getThumbnail().getThumb();
                    description = ((InteractiveVideo) item).getDescription();

                }
            } else if (item instanceof AboutCourse) {
                imageFilePath = ((AboutCourse) item).getThumbnail().getThumb();
                if (imageFilePath.isEmpty()) {
                    imageFilePath = ((AboutCourse) item).getThumbnail().getUrl();
                }
                if (imageFilePath.isEmpty()) {
                    imageFilePath = ((AboutCourse) item).getMetaInformation().getBanner();
                }
                Class objectClass = DigitalBook.class;
                int typeImage = R.drawable.digital_book;
                if (((AboutCourse) item).getCourseType().equalsIgnoreCase("digitalbook")) {
                    objectClass = DigitalBook.class;
                    type = "Digital Book";
                    typeImage = R.drawable.digital_book;
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadDigitalBook(item.getObjectId());
                        }
                    });
                } else if (((AboutCourse) item).getCourseType().equalsIgnoreCase("videocourse")) {
                    objectClass = VideoCourse.class;
                    type = "Video Course";
                    typeImage = R.drawable.video_course;
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadVideoCourse(item.getObjectId());
                        }
                    });
                } else if (((AboutCourse) item).getMicroCourseType().toLowerCase().contains("map")) {
                    objectClass = ConceptMap.class;
                    type = "Concept Map";
                    typeImage = R.drawable.concept_map;
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadConceptMap(item.getObjectId());
                        }
                    });
                } else if (((AboutCourse) item).getMicroCourseType().toLowerCase().contains("interactiveimage")) {
                    objectClass = InteractiveImage.class;
                    type = "Interactive Image";
                    typeImage = R.drawable.interactive_image;
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadInteractiveImage(item.getObjectId());
                        }
                    });
                } else if (((AboutCourse) item).getMicroCourseType().toLowerCase().contains("pop")) {
                    objectClass = PopUps.class;
                    type = "Pop Up";
                    typeImage = R.drawable.popup;
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadPopUp(item.getObjectId());
                        }
                    });
                } else if (((AboutCourse) item).getMicroCourseType().toLowerCase().contains("video")) {
                    objectClass = InteractiveVideo.class;
                    type = "Interactive Video";
                    typeImage = R.drawable.videio_course_white;
                    holder.mDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mDownload.setEnabled(false);
                            mSearchModel.downloadInteractiveVideo(item.getObjectId());
                        }
                    });
                }
                final Class finalObjectClass = objectClass;
                holder.mPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        CourseDetailActivity.startActivity(getBaseContext(), item.getObjectId(), item.getClass());
                        WebPlayerActivity.startWebPlayer(getBaseContext(), ((AboutCourse) item).getObjectId(),((AboutCourse) item).getMetaInformation().getSubject().getId(),((AboutCourse) item).getMetaInformation().getTopic().getId(), finalObjectClass,"",false);
                    }
                });
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(CourseDetailActivity.getStartActivityIntent(getBaseContext(), ((AboutCourse) item).getObjectId(), finalObjectClass, ""));
                    }
                });

                setButtonStatus(item.getSyncStatus(), holder);

                title = ((AboutCourse) item).getTitle();
                subject = ((AboutCourse) item).getMetaInformation().getSubject().getName();
                topic += ((AboutCourse) item).getMetaInformation().getTopic().getName();
                description = ((AboutCourse) item).getDescription();
                duration = ((AboutCourse) item).getMetaInformation().getDuration() + duration;

            } else if (item instanceof Resource) {

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (((Resource) item).getType().toLowerCase().contains("image")) {
//                            startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), ((Resource) item).getUrlMain(), (Resource) item));
//                        } else if (((Resource) item).getType().toLowerCase().contains("video")) {
//                            Uri intentUri = Uri.parse(((Resource) item).getUrlMain());
                        String url = ((Resource) item).getUrlMain();
                        if (url.startsWith("file")) {
                            if (!url.isEmpty()) {
//                                    File extDir = Environment.getExternalStorageDirectory();
//                                    url = FileUtils.copyFilesExternal(((Resource) item).getUrlMain(), extDir.getAbsolutePath(), "tempLil", "temp");
                                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));

                            }
                        } else if (url.startsWith("http")) {
                            if (url.contains("https")) {
                                url = url.replace("https", "http");
                                ((Resource) item).setUrlMain(url);
                            }
                            startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
                        }
//                        else if (((Resource) item).getType().contains("youtube")) {
//
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + ((Resource) item).getObjectId())));
//                        }
                        else {

                            startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_FTP, (Resource) item));

//                            }
//                            Uri intentUri = Uri.parse(url);
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_VIEW);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setDataAndType(intentUri, "video/*");
//                            startActivity(intent);

                        }
                    }

                };

                holder.mPlay.setOnClickListener(listener);
                holder.mView.setOnClickListener(listener);
//                holder.mDownload.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mSearchModel.downloadResource(((Resource) item).getObjectId());
//                    }
//                });
                holder.mDownload.setVisibility(GONE);
                holder.mPlay.setVisibility(GONE);
                holder.mAvailability.setVisibility(View.VISIBLE);
                type = ((Resource) item).getType();
                title = ((Resource) item).getTitle();
                imageFilePath = ((Resource) item).getUrlThumbnail();
                if (imageFilePath.isEmpty()) imageFilePath = ((Resource) item).getUrlMain();
                if (imageFilePath.startsWith("file")) {
                    holder.mAvailability.setText("Offline");
                } else if (imageFilePath.startsWith("http")) {
                    holder.mAvailability.setText("Online");
                } else {
                    imageFilePath = "";
                    holder.mAvailability.setText("Network Drive");
                }
                description = ((Resource) item).getCaption();

            }

            holder.mTitle.setText(title);
//            holder.mSubject.setText(subject);
//            holder.mDuration.setText(duration);
//            holder.mImageView.setImageBitmap(ImageUtils.getScaledBitmapFromPath(getResources(), imageFilePath));
//            holder.mImageView.setImageBitmap();
            try {
                if (item instanceof Assignment) {
                    try {
                        if (((Assignment) item).getThumbnail() != null && ((Assignment) item).getThumbnail().getUrl() != null && ((Assignment) item).getThumbnail() != null && ((Assignment) item).getThumbnail().getUrl().toString().trim().length() > 0) {
                            Bitmap bitmap = ImageUtils.decodeBase64(((Assignment) item).getThumbnail() != null ? ((Assignment) item).getThumbnail().getUrl() : "");
                            holder.mImageView.setImageBitmap(bitmap);
                        } else if (((Assignment) item).getThumbnail().getLocalUrl() != null && ((Assignment) item).getThumbnail().getLocalUrl().trim().length() > 0) {
                            if (ImageUtils.isExternalStorageReadable()) {
                                holder.mImageView.setImageBitmap(ImageUtils.loadFromStorage(getBaseContext(), ((Assignment) item).getThumbnail().getLocalUrl()));
                            }
                        } else {
                            //Load from server
                        }
                    } catch (Exception e) {
                        if (type.toLowerCase().contains("image")) {
                            Picasso.with(getBaseContext()).load(R.drawable.image_large).into(holder.mImageView);
                        } else {
                            Picasso.with(getBaseContext()).load(R.drawable.video_large).into(holder.mImageView);
                        }
                    }

                } else if (item instanceof AssignmentResponse) {
                    try {

                        if (((AssignmentResponse) item).getThumbnail() != null && ((AssignmentResponse) item).getThumbnail().getUrl() != null && ((AssignmentResponse) item).getThumbnail() != null && ((AssignmentResponse) item).getThumbnail().getUrl().toString().trim().length() > 0) {
                            Bitmap bitmap = ImageUtils.decodeBase64(((AssignmentResponse) item).getThumbnail() != null ? ((AssignmentResponse) item).getThumbnail().getUrl() : "");
                            holder.mImageView.setImageBitmap(bitmap);
                        } else if (((AssignmentResponse) item).getThumbnail().getLocalUrl() != null && ((AssignmentResponse) item).getThumbnail().getLocalUrl().trim().length() > 0) {
                            if (ImageUtils.isExternalStorageReadable()) {
                                holder.mImageView.setImageBitmap(ImageUtils.loadFromStorage(getBaseContext(), ((AssignmentResponse) item).getThumbnail().getLocalUrl()));
                            }
                        } else {
                            //Load from server
                        }
                    } catch (Exception e) {
                        if (type.toLowerCase().contains("image")) {
                            Picasso.with(getBaseContext()).load(R.drawable.image_large).into(holder.mImageView);
                        } else {
                            Picasso.with(getBaseContext()).load(R.drawable.video_large).into(holder.mImageView);
                        }
                    }
                } else if (!imageFilePath.isEmpty()) {
                    Picasso.with(getBaseContext()).load(imageFilePath).resize(640, 360).onlyScaleDown().into(holder.mImageView);
                } else {
                    if (type.toLowerCase().contains("image")) {
                        Picasso.with(getBaseContext()).load(R.drawable.image_large).into(holder.mImageView);
                    } else {
                        Picasso.with(getBaseContext()).load(R.drawable.video_large).into(holder.mImageView);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.mType.setText(type);
//            holder.mDuration.setVisibility(View.GONE);
//            holder.mViews.setVisibility(View.GONE);
//            holder.mSubject.setVisibility(View.GONE);
        }

        private void setButtonStatus(String status, final ViewHolder holder) {
            if (status.equals(SyncStatus.COMPLETE_SYNC.getStatus())) {
//                if (holder.mPlay != null) holder.mPlay.setVisibility(View.VISIBLE);
//                if (holder.mDownload != null) {
                holder.mDownload.setVisibility(GONE);
//                    holder.mDownload.setEnabled(false);
//                }
                if (holder.mAvailability != null) {
                    holder.mAvailability.setText("Offline");
                    holder.mAvailability.setVisibility(View.VISIBLE);
                }
                if (holder.mView != null) {
                    if (holder.mItem instanceof Course) {
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(CourseDetailActivity.getStartActivityIntent(getBaseContext(), holder.mItem.getObjectId(), holder.mItem.getClass(), ""));
//                                WebPlayerActivity.startWebPlayer(getBaseContext(), holder.mItem.getObjectId(), holder.mItem.getClass());
                            }
                        });
                    }
                }
            } else {
//                if (holder.mPlay != null) holder.mPlay.setVisibility(GONE);
//                if (holder.mDownload != null) {
//                    holder.mDownload.setVisibility(View.VISIBLE);
//                    holder.mDownload.setEnabled(true);
//                }
                if (holder.mAvailability != null) {
                    holder.mAvailability.setText("Online");
                    holder.mAvailability.setVisibility(View.VISIBLE);
                }
                if (holder.mView != null) {
                    if (holder.mItem instanceof Course) {
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(CourseDetailActivity.getStartActivityIntent(getBaseContext(), holder.mItem.getObjectId(), holder.mItem.getClass(), ""));
                            }
                        });
                    }
                }
            }
        }

        private void addValues(ArrayList<BaseDataObject> list) {
            if (mObjects != null && list != null && mValues != null) {
                int position = mValues.size();
                for (BaseDataObject object :
                        list) {
                    if (object instanceof Resource &&
                            (!((Resource) object).getType().toLowerCase().contains("image") &&
                                    !((Resource) object).getType().toLowerCase().contains("video"))) {
                    }
//                    else if(object instanceof ){
//
//                    }

                    else {
                        if (!mObjects.containsKey(object.getObjectId())) {
                            String id = object instanceof Resource ? ((Resource) object).getName() : object.getObjectId();
                            mObjects.put(id, object);
                            mValues.add(object);
                            notifyItemInserted(position++);
                        }
                    }
                }
//                mValues.clear();
//                mValues.addAll(mObjects.values());
//                notifyDataSetChanged();
            }
        }

        private void setValues(ArrayList<BaseDataObject> list) {
            if (mObjects != null && list != null && mValues != null) {
                mObjects.clear();
                mValues.clear();
                for (BaseDataObject object :
                        list) {
                    if (object instanceof Resource &&
                            (!((Resource) object).getType().contains("image") ||
                                    !((Resource) object).getType().contains("video"))) {

                    } else {
                        mValues.add(object);
                        String id = object instanceof Resource ? ((Resource) object).getName() : object.getObjectId();
                        mObjects.put(id, object);
                    }
                }
                notifyDataSetChanged();
            }
        }

        public void clear() {
            mObjects.clear();
            mValues.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder /* implements View.OnCreateContextMenuListener */ {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTitle;
            public final TextView mAvailability;
            public final TextView mType;
            public final View mDownload;
            public final Button mPlay;
            public BaseDataObject mItem;

//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mImageView = (ImageView) view.findViewById(R.id.thumbnail);
//                mTitle = (TextView) view.findViewById(R.id.item_title);
//                mSubject = (TextView) view.findViewById(R.id.subject);
//                mDuration = (TextView) view.findViewById(R.id.duration);
//                mViews = (TextView) view.findViewById(R.id.views);
//                mViewView = (Button) view.findViewById(R.id.button1);
//            }

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image_view);
                mTitle = (TextView) view.findViewById(R.id.textview_title);
                mType = (TextView) view.findViewById(R.id.textview_type);
                mAvailability = (TextView) view.findViewById(R.id.textview_availability);
                mDownload = view.findViewById(R.id.button2);
                mPlay = (Button) view.findViewById(R.id.button1);

//                mView.setOnCreateContextMenuListener(this);
            }
//
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add("Save Offline");
//            }
        }
    }

    public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

        private final List<Category> mValues;

        public CategoryRecyclerViewAdapter(List<Category> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.searchresult_category_list_content, parent, false);
//                    .inflate(R.layout.searchresult_list_content_assignment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            setItem(holder);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        private void setItem(final ViewHolder holder) {

//            final int color = getRandomColor();
            holder.mTitle.setText(holder.mItem.getName());
            holder.mTitle.setBackgroundColor(holder.mItem.getColor());
            holder.mImageView.setVisibility(GONE);
//            Picasso.with(SearchResultListActivity.this).load(holder.mItem.mImageDrawableId).into(holder.mImageView);
//            holder.mImageView.setImageResource(holder.mItem.mImageDrawableId);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCategorySelection.setCategory(holder.mItem);
                    mMainBinding.categoryName.setText(holder.mItem.getName());
//                    mMainBinding.categoryName.setBackgroundColor(holder.mItem.getColor());
                    showSelectedCategoryTextViews(mCategorySelection.getStage());
                    showCategorySelectionHeading(false);
                    if (holder.mItem.getSubCategories().size() > 0) {
                        setupSubCategoryRecyclerView(mMainBinding.searchresultCategory.searchresultCategoryList, holder.mItem.getSubCategories());
                    } else {
                        onQueryTextSubmit(CATEGORY_SEARCH);
                    }
                }
            });
        }

        private void addValues(ArrayList<Category> list) {
            if (list != null && mValues != null) {
                mValues.addAll(list);
                notifyDataSetChanged();
            }
        }

        private void setValues(ArrayList<Category> list) {
            if (list != null && mValues != null) {
                mValues.clear();
                mValues.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            mValues.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder /* implements View.OnCreateContextMenuListener */ {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTitle;
            //            public final TextView mAvailability;
//            public final TextView mType;
//            public final View mDownload;
//            public final Button mPlay;
            public Category mItem;

//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mImageView = (ImageView) view.findViewById(R.id.thumbnail);
//                mTitle = (TextView) view.findViewById(R.id.item_title);
//                mSubject = (TextView) view.findViewById(R.id.subject);
//                mDuration = (TextView) view.findViewById(R.id.duration);
//                mViews = (TextView) view.findViewById(R.id.views);
//                mViewView = (Button) view.findViewById(R.id.button1);
//            }

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image_view);
                mTitle = (TextView) view.findViewById(R.id.textview_title);
//                mType = (TextView) view.findViewById(R.id.textview_type);
//                mAvailability = (TextView) view.findViewById(R.id.textview_availability);
//                mDownload = view.findViewById(R.id.button2);
//                mPlay = (Button) view.findViewById(R.id.button1);

//                mView.setOnCreateContextMenuListener(this);
            }
//
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add("Save Offline");
//            }
        }
    }

    public class SubCategoryRecyclerViewAdapter extends RecyclerView.Adapter<SubCategoryRecyclerViewAdapter.ViewHolder> {

        private final List<SubCategory> mValues;

        public SubCategoryRecyclerViewAdapter(List<SubCategory> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.searchresult_category_list_content, parent, false);
//                    .inflate(R.layout.searchresult_list_content_assignment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            setItem(holder);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        private void setItem(final ViewHolder holder) {

//            final int color = getRandomColor();
            holder.mTitle.setText(holder.mItem.getName());
            holder.mTitle.setBackgroundColor(holder.mItem.getColor());
            holder.mImageView.setVisibility(GONE);
//            holder.mImageView.setImageResource(holder.mItem.mImageDrawableId);
//            Picasso.with(SearchResultListActivity.this).load(holder.mItem.mImageDrawableId).into(holder.mImageView);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCategorySelection.setSubCategory(holder.mItem);
                    mMainBinding.subCategoryName.setText(holder.mItem.getName());
//                    mMainBinding.subCategoryName.setBackgroundColor(holder.mItem.getColor());
                    showSelectedCategoryTextViews(mCategorySelection.getStage());
                    if (holder.mItem.getSubSubCategories().size() > 0) {
                        setupSubSubCategoryRecyclerView(mMainBinding.searchresultCategory.searchresultCategoryList, holder.mItem.getSubSubCategories());
                    } else {
                        onQueryTextSubmit(CATEGORY_SEARCH);
                    }
                }
            });
        }

        private void addValues(ArrayList<SubCategory> list) {
            if (list != null && mValues != null) {
                mValues.addAll(list);
                notifyDataSetChanged();
            }
        }

        private void setValues(ArrayList<SubCategory> list) {
            if (list != null && mValues != null) {
                mValues.clear();
                mValues.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            mValues.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder /* implements View.OnCreateContextMenuListener */ {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTitle;
            //            public final TextView mAvailability;
//            public final TextView mType;
//            public final View mDownload;
//            public final Button mPlay;
            public SubCategory mItem;

//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mImageView = (ImageView) view.findViewById(R.id.thumbnail);
//                mTitle = (TextView) view.findViewById(R.id.item_title);
//                mSubject = (TextView) view.findViewById(R.id.subject);
//                mDuration = (TextView) view.findViewById(R.id.duration);
//                mViews = (TextView) view.findViewById(R.id.views);
//                mViewView = (Button) view.findViewById(R.id.button1);
//            }

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image_view);
                mTitle = (TextView) view.findViewById(R.id.textview_title);
//                mType = (TextView) view.findViewById(R.id.textview_type);
//                mAvailability = (TextView) view.findViewById(R.id.textview_availability);
//                mDownload = view.findViewById(R.id.button2);
//                mPlay = (Button) view.findViewById(R.id.button1);

//                mView.setOnCreateContextMenuListener(this);
            }
//
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add("Save Offline");
//            }
        }
    }

    public class SubSubCategoryRecyclerViewAdapter extends RecyclerView.Adapter<SubSubCategoryRecyclerViewAdapter.ViewHolder> {

        private final List<SubSubCategory> mValues;

        public SubSubCategoryRecyclerViewAdapter(List<SubSubCategory> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.searchresult_category_list_content, parent, false);
//                    .inflate(R.layout.searchresult_list_content_assignment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            setItem(holder);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        private void setItem(final ViewHolder holder) {

//            final int color = getRandomColor();
            holder.mTitle.setText(holder.mItem.getName());
            holder.mTitle.setBackgroundColor(holder.mItem.getColor());
            holder.mImageView.setVisibility(GONE);
//            Picasso.with(SearchResultListActivity.this).load(holder.mItem.mImageDrawableId).into(holder.mImageView);
//            holder.mImageView.setImageResource(holder.mItem.mImageDrawableId);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCategorySelection.setSubSubCategory(holder.mItem);
                    mMainBinding.subSubCategoryName.setText(holder.mItem.getName());
//                    mMainBinding.subSubCategoryName.setBackgroundColor(holder.mItem.getColor());
                    showSelectedCategoryTextViews(mCategorySelection.getStage());
                    onQueryTextSubmit(CATEGORY_SEARCH);
                }
            });
        }

        private void addValues(ArrayList<SubSubCategory> list) {
            if (list != null && mValues != null) {
                mValues.addAll(list);
                notifyDataSetChanged();
            }
        }

        private void setValues(ArrayList<SubSubCategory> list) {
            if (list != null && mValues != null) {
                mValues.clear();
                mValues.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            mValues.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder /* implements View.OnCreateContextMenuListener */ {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTitle;
            //            public final TextView mAvailability;
//            public final TextView mType;
//            public final View mDownload;
//            public final Button mPlay;
            public SubSubCategory mItem;

//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mImageView = (ImageView) view.findViewById(R.id.thumbnail);
//                mTitle = (TextView) view.findViewById(R.id.item_title);
//                mSubject = (TextView) view.findViewById(R.id.subject);
//                mDuration = (TextView) view.findViewById(R.id.duration);
//                mViews = (TextView) view.findViewById(R.id.views);
//                mViewView = (Button) view.findViewById(R.id.button1);
//            }

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image_view);
                mTitle = (TextView) view.findViewById(R.id.textview_title);
//                mType = (TextView) view.findViewById(R.id.textview_type);
//                mAvailability = (TextView) view.findViewById(R.id.textview_availability);
//                mDownload = view.findViewById(R.id.button2);
//                mPlay = (Button) view.findViewById(R.id.button1);

//                mView.setOnCreateContextMenuListener(this);
            }
//
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add("Save Offline");
//            }
        }
    }

}
