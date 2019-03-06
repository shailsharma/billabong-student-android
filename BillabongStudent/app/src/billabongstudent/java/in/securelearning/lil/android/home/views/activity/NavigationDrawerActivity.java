package in.securelearning.lil.android.home.views.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutNavigationDrawerBinding;
import in.securelearning.lil.android.assignments.views.activity.AssignmentCompletedStudentActivity;
import in.securelearning.lil.android.assignments.views.activity.AssignmentCompletedTeacherActivity;
import in.securelearning.lil.android.assignments.views.activity.QuizNewActivity;
import in.securelearning.lil.android.assignments.views.activity.TeacherAnalysisForStudentActivity;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentFragmentTeacher;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentStudentFragment;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentTeacherFragment;
import in.securelearning.lil.android.assignments.views.fragment.QuizzesFragment;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.views.activity.WebPlayerActivity;
import in.securelearning.lil.android.base.widget.NavigationDrawerTypeface;
import in.securelearning.lil.android.blog.views.activity.BlogNewActivity;
import in.securelearning.lil.android.blog.views.fragment.BlogFragment;
import in.securelearning.lil.android.courses.views.activity.CourseNewActivity;
import in.securelearning.lil.android.courses.views.fragment.CourseFavouritesFragment;
import in.securelearning.lil.android.courses.views.fragment.CourseFragmentNew;
import in.securelearning.lil.android.courses.views.fragment.DemoCourseFragment;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.interfaces.Filterable;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.fragment.ClassPlannerActivity;
import in.securelearning.lil.android.home.views.fragment.DashboardFragment;
import in.securelearning.lil.android.home.views.fragment.FilterFragment;
import in.securelearning.lil.android.home.views.fragment.LearningMapFinalFragment;
import in.securelearning.lil.android.home.views.fragment.ResourceFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.BulletinFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.LearningNetworkGroupListFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.NotificationFragment;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.microlearning.view.activity.FeaturedCardListActivity;
import in.securelearning.lil.android.provider.SearchSuggestionProvider;
import in.securelearning.lil.android.resources.view.activity.FavouriteResourceActivity;
import in.securelearning.lil.android.resources.view.activity.ResourceListActivity;
import in.securelearning.lil.android.resources.view.fragment.VideoPlayerFragment;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.events.SearchCloseEvent;
import in.securelearning.lil.android.syncadapter.events.SearchOpenEvent;
import in.securelearning.lil.android.syncadapter.events.SearchSubmitEvent;
import in.securelearning.lil.android.syncadapter.events.UserProfileChangeEvent;
import in.securelearning.lil.android.syncadapter.receiver.ConnectivityChangeReceiver;
import in.securelearning.lil.android.syncadapter.service.FlavorSyncServiceHelper;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.ShortcutUtil;
import in.securelearning.lil.android.tracking.view.activity.TrackingActivityForStudent;
import in.securelearning.lil.android.tracking.view.activity.TrackingActivityForTeacher;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.home.views.activity.SearchResultListActivity.ASSIGNMENTS;
import static in.securelearning.lil.android.home.views.activity.SearchResultListActivity.ASSIGNMENT_RESPONSES;
import static in.securelearning.lil.android.home.views.activity.SearchResultListActivity.COURSES;
import static in.securelearning.lil.android.home.views.activity.SearchResultListActivity.GROUP;
import static in.securelearning.lil.android.home.views.activity.SearchResultListActivity.QUIZ;
import static in.securelearning.lil.android.home.views.activity.SearchResultListActivity.RESOURCES;


public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, FilterFragment.OnFilterFragmentInteractionListener, BlogFragment.OnListFragmentInteractionListener, ResourceFragment.OnResourceFragmentInteractionListener, DemoCourseFragment.OnListFragmentInteractionListener, CourseFavouritesFragment.OnListFragmentInteractionListener, DashboardFragment.OnDashboardFragmentInteractionListener, CourseFragmentNew.OnListFragmentInteractionListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private final int FILTER_TYPE_COURSE = 1;
    private final int FILTER_TYPE_ASSIGNMENT = 2;
    private final int FILTER_TYPE_RESOURCE = 3;
    private final int FILTER_TYPE_QUIZ = 4;
    private final int FILTER_TYPE_DASHBOARD = 5;
    private final int FILTER_TYPE_ASSIGNED = 6;
    private final int FILTER_TYPE_NETWORK = 7;
    private final int FILTER_TYPE_MAP = 8;
    @Inject
    AppUserModel mAppUserModel;
    private ConnectivityChangeReceiver mReceiver;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private int mCurrentFragmentId = -1;
    private FragmentManager mFragmentManager;
    int mColCount;
    private FilterList mFilterList;
    private Filterable mFilterable;
    private TextView mNavEmail;
    private TextView mNavUsername;
    private ImageView mNavThumbnail;
    private MenuItem menuItemSearch, menuItemFilter, menuItemBrowse, menuItemSearchView, menuItemBookmark,
            menuItemCreateQuiz, menuItemDone;
    LayoutNavigationDrawerBinding mBinding;
    private AssignmentStudentFragment mFragmentAssignmentStudent;
    private AssignmentTeacherFragment mFragmentAssignmentTeacher;
    private CourseFragmentNew mFragmentCourse;
    private BlogFragment mFragmentBlog;
    private LearningNetworkGroupListFragment mFragmentLearningNetwork;
    private LearningMapFinalFragment mFragmentLM;
    private BulletinFragment mFragmentBulletin;
    private NotificationFragment mNotificationFragment;
    private DashboardFragment mFragmentDashboard;
    private QuizzesFragment mFragmentQuiz;
    private VideoPlayerFragment mFragmentVideo;
    private Fragment mFragment;
    boolean doubleBackToExitPressedOnce = false;
    private long mLastItemSelectionTime = 0;
    private String mLoggedInUserId = "";
    private UserProfile mLoggedInUser;
    private String[] mSubjects;

//    @Inject
//    PostDataLearningModel mPostDataLearningModel;
//    @Inject
//    AssignmentResponseStudentModel mAssignmentResponseStudentModel;

    @Inject
    HomeModel mHomeModel;
    @Inject
    RxBus mRxBus;
    private Disposable mSubscription;

    private void initializeFCM() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.SUBSCRIBE_FCM);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (!mBinding.appBar.searchView.isIconified()) {
            mBinding.appBar.searchView.setIconified(true);
            return;
        } else {
            //Checking for fragment count on back stack
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else if (mCurrentFragmentId != R.id.nav_dashboard) {
                onNavigationItemSelected(mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_dashboard));
                mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("open seq", "1");
        super.onCreate(savedInstanceState);
        Log.e("open seq", "2");
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_navigation_drawer);
        //getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        Log.e("open seq", "3");
        InjectorHome.INSTANCE.getComponent().inject(this);
        Log.e("open seq", "4");

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setBackgroundDrawableResource(R.drawable.gradient_app);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        initializeBugReport();
        mFragmentManager = getSupportFragmentManager();
        mColCount = getResources().getBoolean(R.bool.isTablet) ? 2 : 1;
        Log.e("open seq", "5");
        registerConnectivityChangeReceiver();
        Log.e("open seq", "6");
        initializeViews();
        Log.e("open seq", "7");
        initializeToolbar();
        Log.e("open seq", "8");
        Log.e("open seq", "9");
        Log.e("open seq", "10");
        Log.e("open seq", "11");
        mLoggedInUser = mAppUserModel.getApplicationUser();
        Log.e("open seq", "12");
        initializeNavigationDrawer();
        setUpAppUserDetail(mLoggedInUser);
        Log.e("open seq", "13");
        listenRxBusEvents();
        Log.e("open seq", "14");


    }

    @Override
    protected void onResume() {
        InjectorHome.INSTANCE.getComponent().inject(this);
        SyncServiceHelper.startSyncService(this);
        FlavorSyncServiceHelper.startReminderIntentService(this);
        if (mCurrentFragmentId == R.id.nav_dashboard) {
            onNavigationItemSelected(mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_dashboard));
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
        } else if (mCurrentFragmentId == R.id.nav_assignments) {
            onNavigationItemSelected(mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments));
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setChecked(true);
        } else if (mCurrentFragmentId == R.id.nav_learning_network) {
            onNavigationItemSelected(mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_learning_network));
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_learning_network).setChecked(true);
        }
        super.onResume();
    }

    private void handleIntentActions() {
        mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_dashboard, Menu.FLAG_PERFORM_NO_CLOSE);
        mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
        if (getIntent().getAction() != null && getIntent().getAction().equals(LoginActivity.ACTION_LEARNING_NETWORK)) {
            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_learning_network, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_learning_network).setChecked(true);
        }
//        else if (getIntent().getAction() != null && getIntent().getAction().equals(LoginActivity.ACTION_NOTIFICATION) && BuildConfig.IS_CALENDAR_ENABLED) {
//            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_notification, Menu.FLAG_PERFORM_NO_CLOSE);
//            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_notification).setChecked(true);
//        }
        else if (getIntent().getAction() != null && getIntent().getAction().equals(ShortcutUtil.ACTION_SHORTCUT_ASSIGNMENT) && BuildConfig.IS_ASSIGNMENT_ENABLED) {
            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_assignments, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setChecked(true);
        } else if (getIntent().getAction() != null && getIntent().getAction().equals(ShortcutUtil.ACTION_SHORTCUT_LEARNING_NETWORK) && BuildConfig.IS_LEARNING_NETWORK_ENABLED) {
            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_learning_network, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_learning_network).setChecked(true);
        } else if (getIntent().getAction() != null && getIntent().getAction().equals(ShortcutUtil.ACTION_SHORTCUT_CALENDAR) && BuildConfig.IS_CALENDAR_ENABLED) {
            startActivity(CalendarActivityNew.getStartIntent(NavigationDrawerActivity.this));
        } else if (getIntent().getAction() != null && getIntent().getAction().equals(ShortcutUtil.ACTION_SHORTCUT_WORKSPACE)) {
            if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                startActivity(QuizNewActivity.getStartIntent(NavigationDrawerActivity.this));
            }
        } else if (getIntent().getAction() != null && getIntent().getAction().equals(ShortcutUtil.ACTION_SHORTCUT_TRAINING) && BuildConfig.IS_TRAINING_ENABLED) {
            if (PermissionPrefsCommon.getTrainingJoinPermission(getBaseContext())) {
                startActivity(TrainingsActivity.getStartIntent(NavigationDrawerActivity.this));
            }
        } else if (getIntent().getAction() != null && getIntent().getAction().equals(ShortcutUtil.ACTION_SHORTCUT_LEARNING_MAP)) {
            if (PermissionPrefsCommon.getNavigationLearningMapPermission(getBaseContext())) {
                startActivity(LearningMapNewActivity.getStartIntent(NavigationDrawerActivity.this));
            }
        }
    }

    /**
     * Handle exception and send log file to registered mail ids
     */
    private void initializeBugReport() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                e.printStackTrace();

                String stackTraceString = stackTrace.toString();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                final String msg = "Device Detail:\nManufacturer: " + android.os.Build.MANUFACTURER + "\nModel: " +
                        android.os.Build.MODEL +
                        "\nAndroid Version: " + android.os.Build.VERSION.RELEASE +
                        "\nAndroid Flavor: " + BuildConfig.FLAVOR +
                        "\nUser ID: " + AppPrefs.getUserId(getBaseContext()) +
                        "\nUser Name: " + AppPrefs.getUserName(getBaseContext()) +
                        "\n App Version:" + BuildConfig.VERSION_NAME +
                        "\nScreen Size: " + width + "*" + height
                        + "\n\nReport:\n" + stackTraceString;

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"chaitendra.singh@securelearning.in,prabodh.dhabaria@securelearning.in"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Crash report ");
                i.putExtra(Intent.EXTRA_TEXT, msg);
                Intent finalIntent = Intent.createChooser(i, "Send Crash Report");

                //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getBaseContext(), NotificationUtil.NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(android.R.drawable.stat_notify_error)
                                .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.notification_icon))
                                .setColor(ContextCompat.getColor(getBaseContext(), R.color.notification_small_background))
                                .setTicker("Bug report")
                                .setAutoCancel(true)
                                .setContentTitle("Bug Report - " + e.getClass().getSimpleName())
                                .setContentText("A bug report has been generated. Please send it to help us improve the application.");


                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, finalIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(android.R.drawable.sym_action_email, "SEND", pendingIntent);
                builder.setContentIntent(pendingIntent);
                // notificationManager.notify(1, builder.build());

                NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    notifyMgr.createNotificationChannel(channel);
                }
                notifyMgr.notify(1, builder.build());

//                if (Looper.getMainLooper().isCurrentThread()) {
//
//                    Intent intent = LoginActivity.startIntentLoginActivity(getBaseContext());
//                    startActivity(intent);
//                    finishAffinity();
//                    int pendingIntentId = 123456;
//                    PendingIntent mPendingIntent = PendingIntent.getActivity(NavigationDrawerActivity.this, pendingIntentId, intent,
//                            PendingIntent.FLAG_CANCEL_CURRENT);
//                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                    System.exit(0);
//                }
            }
        });
    }

    private void registerConnectivityChangeReceiver() {
        Completable.complete().observeOn(Schedulers.io())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        IntentFilter filter = new IntentFilter();
                        filter.addAction("");

                        mReceiver = new ConnectivityChangeReceiver();
                        registerReceiver(mReceiver, filter);
                    }
                });


    }

    /**
     * Create Rxbus Disposable for the listen event raised from anywhere in the app
     */
    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(UserProfile.class)) {
                    final UserProfile loggedInUser = mAppUserModel.getApplicationUser();
                    setUpAppUserDetail(loggedInUser);
                } else if (event instanceof UserProfileChangeEvent) {
                    final UserProfile loggedInUser = mAppUserModel.getApplicationUser();
                    setUpAppUserDetail(loggedInUser);
                }

            }
        });
    }

    /**
     * set user details to navigation header
     *
     * @param userProfile
     */
    private void setUpAppUserDetail(UserProfile userProfile) {
        mLoggedInUserId = userProfile.getObjectId();
        mNavEmail.setText(userProfile.getEmail());
        mNavUsername.setText(userProfile.getName());

        if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getLocalUrl())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getLocalUrl()).transform(new CircleTransform()).resize(256, 256).centerCrop().into(mNavThumbnail);
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getUrl())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getUrl()).transform(new CircleTransform()).resize(256, 256).centerCrop().into(mNavThumbnail);
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getThumb())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getThumb()).transform(new CircleTransform()).resize(256, 256).centerCrop().into(mNavThumbnail);
        } else {
            if (!TextUtils.isEmpty(userProfile.getFirstName())) {
                String firstWord = userProfile.getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                mNavThumbnail.setImageDrawable(textDrawable);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (!mBinding.appBar.searchView.isIconified()) {
//            mBinding.appBar.searchView.setIconified(true);
//        }
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
        createQuizVisibility(false);
        doneVisibility(false);

        final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);

        final SearchView mSearchView = mBinding.appBar.searchView;
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
        //        if (menuItemSearch != null) {
//            mBinding.appBar.searchView.setMenuItem(menuItemSearch);
//        }

//        mBinding.appBar.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                suggestions.saveRecentQuery(query, null);
//                search(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                SearchSuggestionProvider provider = new SearchSuggestionProvider();
//                return false;
//            }
//        });
//        mBinding.appBar.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
//                onSearchOpen();
//            }
//
//            @Override
//            public void onSearchViewClosed() {
//                onSearchClose();
//            }
//        });
        handleIntentActions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_browse) {
            if (mCurrentFragmentId == R.id.nav_courses) {
                startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", COURSES));
            } else if (mCurrentFragmentId == R.id.nav_assignments) {
                if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                    startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", ASSIGNMENTS));
                } else if (PermissionPrefsCommon.getAssignmentSubmissionPermission(getBaseContext())) {
                    startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", ASSIGNMENT_RESPONSES));
                }
            } else if (mCurrentFragmentId == R.id.nav_resources) {
                startActivity(SearchResourcesListFilterActivity.getStartSearchActivityIntent(this, "", RESOURCES));
            }
//            else if (mCurrentFragmentId == R.id.nav_assigned) {
//                startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", ASSIGNMENTS));
//            }
            else if (mCurrentFragmentId == R.id.nav_quizess) {
                startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", QUIZ));
            } else if (mCurrentFragmentId == R.id.nav_learning_network) {
                startActivity(SearchResultListFilterActivity.getStartSearchActivityIntent(this, "", GROUP));
            }
//            else if (mCurrentFragmentId == R.id.nav_learning_network) {
//
//                SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItemSearch);
//                searchView.setOnQueryTextListener(this);
//            }


        } else if (id == R.id.action_filter) {
            Observable.create(new ObservableOnSubscribe<FilterList>() {
                @Override
                public void subscribe(ObservableEmitter<FilterList> e) throws Exception {
                    if (mCurrentFragmentId == R.id.nav_courses) {
                        mFilterList = buildFilter(FILTER_TYPE_COURSE);
                    } else if (mCurrentFragmentId == R.id.nav_learning_map) {
                        mFilterList = buildFilter(FILTER_TYPE_MAP);
                    } else if (mCurrentFragmentId == R.id.nav_assignments) {
                        if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                            mFilterList = buildFilter(FILTER_TYPE_ASSIGNED);
                        } else if (PermissionPrefsCommon.getAssignmentSubmissionPermission(getBaseContext())) {
                            mFilterList = buildFilter(FILTER_TYPE_ASSIGNMENT);
                        }
                    } else if (mCurrentFragmentId == R.id.nav_resources) {
                        mFilterList = buildFilter(FILTER_TYPE_RESOURCE);
                    }
//                    else if (mCurrentFragmentId == R.id.nav_assigned) {
//                        mFilterList = buildFilter(FILTER_TYPE_ASSIGNED);
//                    }
                    else if (mCurrentFragmentId == R.id.nav_quizess) {
                        mFilterList = buildFilter(FILTER_TYPE_QUIZ);
                    } else if (mCurrentFragmentId == R.id.nav_learning_network) {
                        mFilterList = buildFilter(FILTER_TYPE_NETWORK);
                    }

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

        } else if (id == R.id.action_bookmark) {
            Intent intent = new Intent(NavigationDrawerActivity.this, FavouriteResourceActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_done) {
            if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                startActivity(AssignmentCompletedTeacherActivity.getStartIntent(getBaseContext()));
            } else {
                startActivity(AssignmentCompletedStudentActivity.getStartIntent(getBaseContext()));
            }
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSubjects = PrefManager.getSubjectNames(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null)
            mSubscription.dispose();
        unregisterConnectivityChangeReceiver();
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

    /**
     * Handle navigation view item clicks here.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        if (!mBinding.appBar.searchView.isIconified()) {
            mBinding.appBar.searchView.setIconified(true);
        }
        int id = item.getItemId();
        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mBinding.appBar.toolbarContainer.getLayoutParams();

        switch (id) {
            case R.id.nav_dashboard: {

                searchVisibility(false);
                filterVisibility(false);
                browseVisibility(false);
                bookmarkVisibility(false);
                createQuizVisibility(false);
                doneVisibility(false);

                mBinding.appBar.toolbar.setVisibility(View.GONE);
                mFilterList = buildFilter(FILTER_TYPE_DASHBOARD);
                setTitle(item.getTitle());
                //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorStartGradient));
                //mToolbar.setBackgroundColor(ContextCompat.getColor(NavigationDrawerActivity.this, R.color.colorPrimary));
                mAppBarLayout.setElevation(10);
                params.setScrollFlags(0);
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);
                if (mFragmentDashboard != null) {
                    fragmentTransaction.show(mFragmentDashboard)
                            .commit();

                } else {
                    mFragmentDashboard = DashboardFragment.newInstance();
                    fragmentTransaction.add(R.id.container_main, mFragmentDashboard, "dashboard");
                    fragmentTransaction.commit();
                }
                mCurrentFragmentId = id;

            }
            break;

            case R.id.nav_resources: {

                startActivity(ResourceListActivity.getIntentForBrowse(this, getString(R.string.title_resources)));

            }
            break;
            case R.id.nav_blogs: {

                startActivity(BlogNewActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;
            case R.id.nav_courses: {

                startActivity(CourseNewActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;

            case R.id.nav_assignments: {
                searchVisibility(false);
                filterVisibility(true);
                browseVisibility(false);
                bookmarkVisibility(false);
                createQuizVisibility(false);
                doneVisibility(true);
                mBinding.appBar.toolbar.setVisibility(View.VISIBLE);
                mFilterList = buildFilter(FILTER_TYPE_ASSIGNMENT);
                setTitle(item.getTitle());
                //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAssignmentPrimary));
                //mToolbar.setBackgroundColor(ContextCompat.getColor(NavigationDrawerActivity.this, R.color.colorAssignmentPrimary));
                mAppBarLayout.setElevation(0);

                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);

                if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                    if (mFragmentAssignmentTeacher != null) {
                        fragmentTransaction.show(mFragmentAssignmentTeacher)
                                .commit();

                    } else {
                        mFragmentAssignmentTeacher = AssignmentTeacherFragment.newInstance("");
                        fragmentTransaction.add(R.id.container_main, mFragmentAssignmentTeacher, "assignmentTeacher");
                        fragmentTransaction.commit();
                    }
                    mCurrentFragmentId = id;
                    mFilterable = new Filterable() {
                        @Override
                        public void filter() {
                            if (mFragmentAssignmentTeacher != null)
                                mFragmentAssignmentTeacher.filter(mFilterList);
                        }
                    };
                } else {
                    if (mFragmentAssignmentStudent != null) {
                        fragmentTransaction.show(mFragmentAssignmentStudent)
                                .commit();

                    } else {
                        mFragmentAssignmentStudent = AssignmentStudentFragment.newInstance("");
                        fragmentTransaction.add(R.id.container_main, mFragmentAssignmentStudent, "assignmentStudent");
                        fragmentTransaction.commit();
                    }
                    mCurrentFragmentId = id;
                    mFilterable = new Filterable() {
                        @Override
                        public void filter() {
                            if (mFragmentAssignmentStudent != null)
                                mFragmentAssignmentStudent.filter(mFilterList);
                        }
                    };
                }


//                startActivity(AssignmentStudentActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;

            case R.id.nav_setting: {
                startActivity(SettingNewActivity.getStartIntent(NavigationDrawerActivity.this));


            }
            break;
            case R.id.nav_quizess: {

                startActivity(QuizNewActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;
            case R.id.nav_learning_network: {
                searchVisibility(false);
                filterVisibility(false);
                browseVisibility(false);
                bookmarkVisibility(false);
                createQuizVisibility(false);
                doneVisibility(false);
                setTitle(item.getTitle());
                mBinding.appBar.toolbar.setVisibility(View.VISIBLE);
                //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryLN));
                //mToolbar.setBackgroundColor(ContextCompat.getColor(NavigationDrawerActivity.this, R.color.colorPrimaryLN));
                mAppBarLayout.setElevation(10);
                params.setScrollFlags(0);

                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);
                if (mFragmentLearningNetwork != null) {
                    fragmentTransaction.show(mFragmentLearningNetwork)
                            .commit();

                } else {
                    mFragmentLearningNetwork = LearningNetworkGroupListFragment.newInstance(mColCount);
                    fragmentTransaction.add(R.id.container_main, mFragmentLearningNetwork, "learningNetwork");
                    fragmentTransaction.commit();
                }
                mCurrentFragmentId = id;

                //               startActivity(LearningNetworkNewActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;
            case R.id.nav_my_trainings: {
                startActivity(TrainingsActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;
            case R.id.nav_featured_card: {
                startActivity(FeaturedCardListActivity.getStartIntent(NavigationDrawerActivity.this));

            }
            break;
            case R.id.nav_class_planner: {
                startActivity(ClassPlannerActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;
            case R.id.navStartPractice: {
                startActivity(StartPracticeTestActivity.getStartIntent(NavigationDrawerActivity.this, mLoggedInUser.getGrade().getId()));
            }
            break;
            case R.id.nav_user_profile: {
                if (!TextUtils.isEmpty(mLoggedInUserId)) {
                    startActivity(StudentUserProfileActivity.getStartIntent(mLoggedInUserId, NavigationDrawerActivity.this));
                }
            }
            break;
            case R.id.nav_learning_map: {
//                mCurrentFragmentId = id;
//                searchVisibility(false);
//                filterVisibility(false);
//                browseVisibility(false);
//                bookmarkVisibility(false);
//                setTitle(item.getTitle());
//                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//                mToolbar.setBackgroundColor(ContextCompat.getColor(NavigationDrawerActivity.this, R.color.colorPrimary));
//                mAppBarLayout.setElevation(10);
//                params.setScrollFlags(0);
//                if (mFragmentLM == null) {
//                    mFragmentLM = new LearningMapFinalFragment();
//                }
//                final FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.container_main, mFragmentLM);
//                fragmentTransaction.commit();
                startActivity(LearningMapNewActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;
            case R.id.nav_tracking: {
                if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                    Intent mIntent = new Intent(NavigationDrawerActivity.this, TrackingActivityForTeacher.class);
                    startActivity(mIntent);
                } else {
                    Intent mIntent = new Intent(NavigationDrawerActivity.this, TrackingActivityForStudent.class);
                    startActivity(mIntent);
                }
            }
            break;
            case R.id.nav_teacher_map: {
                if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                    startActivity(TeacherAnalysisForStudentActivity.getStartIntent(NavigationDrawerActivity.this));
                }
            }
            break;
//            case R.id.nav_notification: {
//                searchVisibility(false);
//                filterVisibility(false);
//                browseVisibility(false);
//                bookmarkVisibility(false);
//                createQuizVisibility(false);
//                doneVisibility(false);
//                setTitle(item.getTitle());
//                mBinding.appBar.toolbar.setVisibility(View.VISIBLE);
//
//                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorCalendarPrimary));
//                mToolbar.setBackgroundColor(ContextCompat.getColor(NavigationDrawerActivity.this, R.color.colorCalendarPrimary));
//                mAppBarLayout.setElevation(10);
//                params.setScrollFlags(0);
//
//                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);
//                if (mNotificationFragment != null) {
//                    fragmentTransaction.show(mNotificationFragment)
//                            .commit();
//
//                } else {
//                    mNotificationFragment = NotificationFragment.newInstance(mColCount);
//                    fragmentTransaction.add(R.id.container_main, mNotificationFragment, "notification");
//                    fragmentTransaction.commit();
//                }
//                mCurrentFragmentId = id;
//            startActivity(UserProfileActivity.getStartIntent(mLoggedInUserId, getBaseContext()));

            //           }
//            break;
        }


        mDrawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }

    private void unregisterConnectivityChangeReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void search(String query) {
        if (!TextUtils.isEmpty(query))
            Injector.INSTANCE.getComponent().rxBus().send(new SearchSubmitEvent(query));
    }

    private void onSearchOpen() {
        Injector.INSTANCE.getComponent().rxBus().send(new SearchOpenEvent());
//        mBottomTabLayout.setVisibility(View.GONE);
    }

    private void onSearchClose() {
        Injector.INSTANCE.getComponent().rxBus().send(new SearchCloseEvent());
//        mBottomTabLayout.setVisibility(View.VISIBLE);
    }

    /**
     * set search menu visibility according to frgament
     *
     * @param b
     */
    private void searchVisibility(boolean b) {
        if (menuItemSearch != null) {
            menuItemSearch.setVisible(b);
        }
    }

    /**
     * set filter menu visibility according to frgament
     *
     * @param b
     */
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

    private void browseVisibility(boolean b) {
        if (menuItemBrowse != null) {
            menuItemBrowse.setVisible(b);
        }
    }

    private void doneVisibility(boolean b) {
        if (menuItemDone != null) {
            menuItemDone.setVisible(b);
        }
    }

    /**
     * find ids of views
     */
    private void initializeViews() {

        mToolbar = mBinding.appBar.toolbar;
//        mToolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.textview_toolbar_title);
        mDrawerLayout = mBinding.drawerLayout;
        mNavigationView = mBinding.navView;
        mAppBarLayout = mBinding.appBar.appBarLayout;
//        mBottomTabLayout = (LinearLayout) findViewById(R.id.layout_bottom_tab);
//        mCourseTabLayout = (RelativeLayout) findViewById(R.id.layout_course_tab);
//        mAssignmentTabLayout = (RelativeLayout) findViewById(R.id.layout_assignment_tab);
//        mDashboardTabLayout = (RelativeLayout) findViewById(R.id.layout_dashboard_tab);
//        mNetworkTabLayout = (RelativeLayout) findViewById(R.id.layout_network_tab);
//        mMapTabLayout = (RelativeLayout) findViewById(R.id.layout_map_tab);
//        mQuizzesTabLayout = (RelativeLayout) findViewById(R.id.layout_quizzes_tab);
//        mAssignmentBottomTextView = (TextView) findViewById(R.id.textview_assignment_bottom);
//        mAssignmentCountTextView = (TextView) findViewById(R.id.textview_assignment_count);
//        mCourseCountTextView = (TextView) findViewById(R.id.textview_course_count);
//        mNetworkCountTextView = (TextView) findViewById(R.id.textview_network_count);
//        mQuizzesCountTextView = (TextView) findViewById(R.id.textview_quizzes_count);


    }

    /**
     * set up toolbar
     */
    private void initializeToolbar() {
        setSupportActionBar(mToolbar);
        mAppBarLayout.setElevation(0);
//        mBinding.viewSyncNow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
//                    ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.sync_started));
//                    //  SyncServiceHelper.runFtpTest(NavigationDrawerActivity.this);
//                    SyncServiceHelper.startSyncService(NavigationDrawerActivity.this);
//                    if (mDrawerLayout != null)
//                        mDrawerLayout.closeDrawer(Gravity.LEFT);
//                } else {
//                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
//                    if (mDrawerLayout != null)
//                        mDrawerLayout.closeDrawer(Gravity.LEFT);
//                }
//
//            }
//        });
//        mBinding.buttonSyncNow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.sync_started));
//
//                SyncServiceHelper.startSyncService(NavigationDrawerActivity.this);
//                if (mDrawerLayout != null)
//                    mDrawerLayout.closeDrawer(Gravity.LEFT);
//            }
//        });

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * set up navigation drawer
     */
    private void initializeNavigationDrawer() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);

        View headerView = mNavigationView.getHeaderView(0);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mLoggedInUserId)) {
                    startActivity(StudentUserProfileActivity.getStartIntent(mLoggedInUserId, NavigationDrawerActivity.this));
                }

            }
        });

        mNavEmail = (TextView) headerView.findViewById(R.id.textview_email);
        mNavUsername = (TextView) headerView.findViewById(R.id.textview_username);
        mNavThumbnail = (ImageView) headerView.findViewById(R.id.imageView_UserProfile);

        if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setTitle(R.string.title_assigned);
            mNavigationView.getMenu().findItem(R.id.nav_teacher_map).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_quizess).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_class_planner).setVisible(false);
        } else if (PermissionPrefsCommon.getAssignmentSubmissionPermission(getBaseContext())) {
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setTitle(R.string.title_assignments);
            mNavigationView.getMenu().findItem(R.id.nav_quizess).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_teacher_map).setVisible(false);
        }

        if (PermissionPrefsCommon.getTrainingJoinPermission(getBaseContext())) {
            mNavigationView.getMenu().findItem(R.id.nav_my_trainings).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_learning_map).setVisible(false);
        } else {
            mNavigationView.getMenu().findItem(R.id.nav_my_trainings).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_learning_map).setVisible(false);
        }

        if (BuildConfig.IS_BLOGS_ENABLED) {
            mNavigationView.getMenu().findItem(R.id.nav_blogs).setVisible(true);
        }

        if (mLoggedInUser.getGrade() != null && !TextUtils.isEmpty(mLoggedInUser.getGrade().getId())) {
            mNavigationView.getMenu().findItem(R.id.navStartPractice).setVisible(false);
        }

        mBinding.appBar.navContent.bottomNavigation.enableAnimation(false);
        mBinding.appBar.navContent.bottomNavigation.enableShiftingMode(false);
        mBinding.appBar.navContent.bottomNavigation.enableItemShiftingMode(false);
        mBinding.appBar.navContent.bottomNavigation.setItemIconTintList(null);
        mBinding.appBar.navContent.bottomNavigation.setOnNavigationItemSelectedListener(this);

    }

    /**
     * set up navigation drawer menu items for font
     */
    private void setNavigationDrawerFont() {

        Menu m = mNavigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);

        }
    }

    /**
     * apply font to navigation drawer menu items
     *
     * @param menuItem
     */
    private void applyFontToMenuItem(MenuItem menuItem) {
        String MY_PREFS_NAME = "LIL_pref";
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, 0);
        String fontStyle = prefs.getString("font", null);
        if (fontStyle != null) {
            Typeface font = Typeface.createFromAsset(getAssets(), fontStyle);
            SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
            mNewTitle.setSpan(new NavigationDrawerTypeface("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            menuItem.setTitle(mNewTitle);

        }
    }

    private FilterList buildFilter(int filterType) {
        FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
        String title = "";
        if (mSubjects == null || mSubjects.length <= 0) {
            mSubjects = PrefManager.getSubjectNames(this);
        }
        if (filterType == FILTER_TYPE_COURSE) {
            title = getResources().getString(R.string.filter_title_course);
            return builder.addSection(new FilterList.SectionBuilder()
                    .addSectionItems(mSubjects)
                    .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
                    .sectionTitle(getString(R.string.label_filter_by))
                    .build())
//                    .addSection(new FilterList.SectionBuilder()
//                            .addSectionItems(SORT_BY_LIST1)
//                            .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
//                            .sectionTitle("Sort By")
//                            .build())
                    .title(title)
                    .build();
        } else if (filterType == FILTER_TYPE_ASSIGNMENT) {
            title = getResources().getString(R.string.filter_homework);
            return builder.addSection(new FilterList.SectionBuilder()
                    .addSectionItems(mSubjects)
                    .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
                    .sectionTitle(getString(R.string.label_filter_by))
                    .build())
//                    .addSection(new FilterList.SectionBuilder()
//                            .addSectionItems(SORT_BY_LIST1)
//                            .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
//                            .sectionTitle("Sort By")
//                            .build())
                    .title(title)
                    .build();

            //return AssignmentFragment.getFilter();

        } else if (filterType == FILTER_TYPE_RESOURCE) {
            title = getResources().getString(R.string.filter_title_assignment);

            return builder.addSection(new FilterList.SectionBuilder()
                    .addSectionItems(mSubjects)
                    .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
                    .sectionTitle(getString(R.string.label_filter_by))
                    .build())
//                    .addSection(new FilterList.SectionBuilder()
//                            .addSectionItems(SORT_BY_LIST1)
//                            .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
//                            .sectionTitle("Sort By")
//                            .build())
                    .title(title)
                    .build();
        } else if (filterType == FILTER_TYPE_ASSIGNED) {
            title = getResources().getString(R.string.filter_title_assignment);

            return builder.addSection(new FilterList.SectionBuilder()
                    .addSectionItems(mSubjects)
                    .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
                    .sectionTitle(getString(R.string.label_filter_by))
                    .build())
//                    .addSection(new FilterList.SectionBuilder()
//                            .addSectionItems(SORT_BY_LIST1)
//                            .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
//                            .sectionTitle("Sort By")
//                            .build())
                    .title(title)
                    .build();
        } else if (filterType == FILTER_TYPE_QUIZ) {
            title = getResources().getString(R.string.filter_title_quiz);

            return builder.addSection(new FilterList.SectionBuilder()
                    .addSectionItems(mSubjects)
                    .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
                    .sectionTitle(getString(R.string.label_filter_by))
                    .build())
//                    .addSection(new FilterList.SectionBuilder()
//                            .addSectionItems(SORT_BY_LIST1)
//                            .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
//                            .sectionTitle("Sort By")
//                            .build())
                    .title(title)
                    .build();
        } else if (filterType == FILTER_TYPE_MAP) {
            title = getResources().getString(R.string.filter_type_map);
            return builder.addSection(new FilterList.SectionBuilder()
                    .addSectionItems(mSubjects)
                    .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
                    .sectionTitle(getString(R.string.label_filter_by))
                    .build())
                    .title(title)
                    .build();
        }
        return builder.build();
    }

    private void setFilterOnFragment(FilterList filterList) {
        this.mFilterList = filterList;
        if (mFilterable != null) mFilterable.filter();
        // TODO: 19-11-2016 apply filter results to fragments
    }

    @Override
    public void onFilterFragmentInteraction(FilterList filterList) {
        setFilterOnFragment(filterList);
    }

    @Override
    public void onResourceFragmentInteraction(Resource item) {
//        if (item.getType().toLowerCase().contains("image")) {
//            startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), item.getUrlMain(), item));
//        } else if (item.getType().toLowerCase().contains("video")) {
//
//
////            File extDir = Environment.getExternalStorageDirectory();
////            String file = FileUtils.copyFilesExternal(item.getUrlMain(), extDir.getAbsolutePath(), "tempLil", "temp");
//
////            Uri intentUri = Uri.parse(file);
////            Intent intent = new Intent();
////            intent.setAction(Intent.ACTION_VIEW);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            intent.setDataAndType(intentUri, "video/*");
////            startActivity(intent);
//            startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), ((Resource) item).getUrlMain(), (Resource) item));
//        }
        String networkType = "";
        if (item.getUrlMain().startsWith("file")) {
            networkType = PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL;
        } else {
            networkType = PlayVideoFullScreenActivity.NETWORK_TYPE_FTP;
        }
        startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getBaseContext(), networkType, item));
    }

    private FragmentTransaction hideFragment(FragmentTransaction fragmentTransaction, int id) {
        if (id == R.id.nav_dashboard) {
            if (mFragmentDashboard != null) {
                return fragmentTransaction.hide(mFragmentDashboard);
            }
        } else if (id == R.id.nav_assignments) {
            if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                if (mFragmentAssignmentTeacher != null) {
                    return fragmentTransaction.hide(mFragmentAssignmentTeacher);
                }
            } else {
                if (mFragmentAssignmentStudent != null) {
                    return fragmentTransaction.hide(mFragmentAssignmentStudent);
                }
            }

        } else if (id == R.id.nav_learning_network) {
            if (mFragmentLearningNetwork != null) {
                return fragmentTransaction.hide(mFragmentLearningNetwork);
            }
        }
//        else if (id == R.id.nav_notification) {
//            if (mNotificationFragment != null) {
//                return fragmentTransaction.hide(mNotificationFragment);
//            }
//        }
        return fragmentTransaction;

    }


    @Override
    public void onListFragmentInteraction(Object object) {
        if (object instanceof BlogDetails) {
            WebPlayerActivity.startWebPlayer(this, ((BlogDetails) object).getObjectId(), "", "", BlogDetails.class, "", false);
        }
    }

    @Override
    public void onDashboardFragmentInteraction(Class aClass) {

        if (aClass.equals(LearningNetworkGroupListFragment.class)) {

            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_learning_network, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_learning_network).setChecked(true);

        } else if (aClass.equals(AssignmentStudentFragment.class)) {

            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_assignments, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setChecked(true);


        } else if (aClass.equals(AssignmentFragmentTeacher.class)) {

            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_assignments, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setChecked(true);


        } else if (aClass.equals(LearningMapFinalFragment.class)) {
            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_learning_map, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_learning_map).setChecked(true);
        } else if (aClass.equals(NavigationDrawerActivity.class)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }

    }

}
