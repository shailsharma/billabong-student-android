package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import in.securelearning.lil.android.app.MyApplication;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutNavigationDrawerBinding;
import in.securelearning.lil.android.assignments.views.activity.QuizNewActivity;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentFragmentTeacher;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerActivity;
import in.securelearning.lil.android.blog.views.fragment.BlogFragment;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.AnimateFragmentEvent;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.fragment.DashboardFragment;
import in.securelearning.lil.android.homework.views.activity.SubmitHomeworkActivity;
import in.securelearning.lil.android.homework.views.fragment.HomeworkFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.LearningNetworkGroupListFragment;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.events.UserProfileChangeEvent;
import in.securelearning.lil.android.syncadapter.receiver.ConnectivityChangeReceiver;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import in.securelearning.lil.android.syncadapter.utils.ShortcutUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class NavigationDrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BlogFragment.OnListFragmentInteractionListener,
        DashboardFragment.OnDashboardFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    AppUserModel mAppUserModel;
    int mColCount;
    LayoutNavigationDrawerBinding mBinding;
    boolean doubleBackToExitPressedOnce = false;
    @Inject
    HomeModel mHomeModel;
    @Inject
    FlavorHomeModel mFlavorHomeModel;
    @Inject
    RxBus mRxBus;
    private ConnectivityChangeReceiver mReceiver;
    private int mCurrentFragmentId = -1;
    private FragmentManager mFragmentManager;
    private TextView mNavEmail;
    private TextView mNavUsername;
    private ImageView mNavThumbnail;
    private MenuItem mDoneMenuItem;
    private HomeworkFragment mHomeworkFragment;
    private LearningNetworkGroupListFragment mFragmentLearningNetwork;
    private DashboardFragment mFragmentDashboard;
    private String mLoggedInUserId = "";
    private UserProfile mLoggedInUser;
    private Disposable mSubscription;

    private void initializeFCM() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.SUBSCRIBE_FCM);
    }

    @Override
    public void onBackPressed() {

        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (!mBinding.appBar.searchView.isIconified()) {
            mBinding.appBar.searchView.setIconified(true);
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
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_navigation_drawer);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        checkTTS();


    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mColCount = getResources().getBoolean(R.bool.isTablet) ? 2 : 1;
        registerConnectivityChangeReceiver();
        initializeToolbar();
        mLoggedInUser = mAppUserModel.getApplicationUser();
        initializeNavigationDrawer();
        setUpAppUserDetail(mLoggedInUser);
        listenRxBusEvents();


    }

    @Override
    protected void onResume() {
        super.onResume();
        isNewVersionAvailable();
        SyncServiceHelper.startSyncService(this);
        MyApplication.getInstance().clearPicassoCache(getBaseContext());

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
        }

    }


    /*checking if current app version is different from published app on play store*/
    @SuppressLint("CheckResult")
    private void isNewVersionAvailable() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                mHomeModel.checkForNewVersionOnPlayStore().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String playStoreVersion) throws Exception {
                        if (!BuildConfig.VERSION_NAME.equalsIgnoreCase(playStoreVersion)) {

                            new AlertDialog.Builder(NavigationDrawerActivity.this)
                                    .setTitle(getString(R.string.labelUpdateAvailable))
                                    .setMessage(getString(R.string.messageNewUpdateIsAvailable))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.labelUpdate), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                                    ("market://details?id=" + BuildConfig.APPLICATION_ID)));
                                        }
                                    }).show();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
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
                final String msg = "Device Detail:\nManufacturer: " + Build.MANUFACTURER + "\nModel: " +
                        Build.MODEL +
                        "\nAndroid Version: " + Build.VERSION.RELEASE +
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
                    assert notifyMgr != null;
                    notifyMgr.createNotificationChannel(channel);
                }
                assert notifyMgr != null;
                notifyMgr.notify(1, builder.build());

            }
        });
    }

    @SuppressLint("CheckResult")
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        mDoneMenuItem = menu.findItem(R.id.action_done);
        doneVisibility(false);
        handleIntentActions();
        return true;
    }

    @SuppressLint("CheckResult")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {

            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                startActivity(SubmitHomeworkActivity.getStartIntent(getBaseContext()));

            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
            }

        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null)
            mSubscription.dispose();
        unregisterConnectivityChangeReceiver();
    }


    /**
     * Handle navigation view item clicks here.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

        if (!mBinding.appBar.searchView.isIconified()) {
            mBinding.appBar.searchView.setIconified(true);
        }
        int id = item.getItemId();
        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mBinding.appBar.toolbarContainer.getLayoutParams();

        switch (id) {

            case R.id.nav_dashboard: {

                doneVisibility(false);

                mBinding.appBar.toolbar.setVisibility(View.GONE);
                setTitle(item.getTitle());

                getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_light);
                CommonUtils.getInstance().setStatusBarIconsDark(NavigationDrawerActivity.this);

                mBinding.appBar.appBarLayout.setElevation(0f);
                params.setScrollFlags(0);
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);
                if (mCurrentFragmentId != id) {
                    mRxBus.send(new AnimateFragmentEvent(R.id.nav_dashboard));
                }
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

            case R.id.nav_assignments: {
                doneVisibility(true);
                mBinding.appBar.toolbar.setVisibility(View.VISIBLE);
                setTitle(item.getTitle());

                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().setBackgroundDrawableResource(R.drawable.gradient_app);
                CommonUtils.getInstance().setStatusBarIconsLight(NavigationDrawerActivity.this);

                mBinding.appBar.appBarLayout.setElevation(ConstantUtil.NO_TOOLBAR_ELEVATION);

                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);
                if (mCurrentFragmentId != id) {
                    mRxBus.send(new AnimateFragmentEvent(R.id.nav_assignments));
                }

                if (mHomeworkFragment != null) {
                    fragmentTransaction.show(mHomeworkFragment)
                            .commit();

                } else {
                    mHomeworkFragment = HomeworkFragment.newInstance();
                    fragmentTransaction.add(R.id.container_main, mHomeworkFragment, "assignmentStudent");
                    fragmentTransaction.commit();
                }
                mCurrentFragmentId = id;


            }
            break;

            case R.id.nav_learning_network: {
                doneVisibility(false);
                setTitle(item.getTitle());
                mBinding.appBar.toolbar.setVisibility(View.VISIBLE);

                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().setBackgroundDrawableResource(R.drawable.gradient_app);
                CommonUtils.getInstance().setStatusBarIconsLight(NavigationDrawerActivity.this);

                mBinding.appBar.appBarLayout.setElevation(ConstantUtil.TOOLBAR_ELEVATION);
                params.setScrollFlags(0);

                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    mFlavorHomeModel.checkUserStatus(ConstantUtil.TYPE_NETWORK);
                }

                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);
                if (mCurrentFragmentId != id) {
                    mRxBus.send(new AnimateFragmentEvent(R.id.nav_learning_network));
                }
                if (mFragmentLearningNetwork != null) {
                    fragmentTransaction.show(mFragmentLearningNetwork).commit();

                } else {
                    mFragmentLearningNetwork = LearningNetworkGroupListFragment.newInstance(mColCount);
                    fragmentTransaction.add(R.id.container_main, mFragmentLearningNetwork, "learningNetwork");
                    fragmentTransaction.commit();
                }
                mCurrentFragmentId = id;

            }
            break;

            case R.id.nav_setting: {
                startActivity(SettingActivity.getStartIntent(NavigationDrawerActivity.this));


            }
            break;

            case R.id.navLogout: {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    logoutFromApp(NavigationDrawerActivity.this);
                } else {
                    SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
                }

            }
            break;

            case R.id.nav_quizess: {

                startActivity(QuizNewActivity.getStartIntent(NavigationDrawerActivity.this));
            }
            break;

            case R.id.nav_user_profile: {
                if (!TextUtils.isEmpty(mLoggedInUserId)) {
                    startActivity(StudentProfileActivity.getStartIntent(mLoggedInUserId, NavigationDrawerActivity.this));
                }
            }
            break;
        }


        mBinding.drawerLayout.closeDrawer(Gravity.START);
        return true;
    }

    /*Showing logout dialog to logout.*/
    /*Only activity context is allowed here.*/
    private void logoutFromApp(final Activity context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.logout_message))
                .setPositiveButton(context.getString(R.string.logout), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SyncServiceHelper.performUserLogout(context, getString(R.string.logging_out));
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                })
                .setCancelable(false);
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void unregisterConnectivityChangeReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void doneVisibility(boolean b) {
        if (mDoneMenuItem != null) {
            mDoneMenuItem.setVisible(b);
        }
    }

    /**
     * set up toolbar
     */
    private void initializeToolbar() {
        setSupportActionBar(mBinding.appBar.toolbar);
        mBinding.appBar.appBarLayout.setElevation(0);

    }


    /**
     * set up navigation drawer
     */
    private void initializeNavigationDrawer() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mBinding.drawerLayout, mBinding.appBar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(this);
        mBinding.navView.setItemIconTintList(null);

        View headerView = mBinding.navView.getHeaderView(0);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mLoggedInUserId)) {
                    startActivity(StudentProfileActivity.getStartIntent(mLoggedInUserId, NavigationDrawerActivity.this));
                }


            }
        });

        mNavEmail = headerView.findViewById(R.id.textview_email);
        mNavUsername = headerView.findViewById(R.id.textview_username);
        mNavThumbnail = headerView.findViewById(R.id.imageView_UserProfile);

        if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setTitle(R.string.title_assigned);
            mBinding.navView.getMenu().findItem(R.id.nav_teacher_map).setVisible(false);
            mBinding.navView.getMenu().findItem(R.id.nav_quizess).setVisible(false);
            mBinding.navView.getMenu().findItem(R.id.nav_class_planner).setVisible(false);
        } else if (PermissionPrefsCommon.getAssignmentSubmissionPermission(getBaseContext())) {
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setTitle(R.string.title_assignments);
            mBinding.navView.getMenu().findItem(R.id.nav_quizess).setVisible(false);
            mBinding.navView.getMenu().findItem(R.id.nav_teacher_map).setVisible(false);
        }

        if (PermissionPrefsCommon.getTrainingJoinPermission(getBaseContext())) {
            mBinding.navView.getMenu().findItem(R.id.nav_my_trainings).setVisible(false);
            mBinding.navView.getMenu().findItem(R.id.nav_learning_map).setVisible(false);
        } else {
            mBinding.navView.getMenu().findItem(R.id.nav_my_trainings).setVisible(false);
            mBinding.navView.getMenu().findItem(R.id.nav_learning_map).setVisible(false);
        }

        if (BuildConfig.IS_BLOGS_ENABLED) {
            mBinding.navView.getMenu().findItem(R.id.nav_blogs).setVisible(true);
        }

        if (mLoggedInUser.getGrade() != null && !TextUtils.isEmpty(mLoggedInUser.getGrade().getId())) {
            mBinding.navView.getMenu().findItem(R.id.navStartPractice).setVisible(false);
        }

        mBinding.appBar.navContent.bottomNavigation.enableAnimation(false);
        mBinding.appBar.navContent.bottomNavigation.enableShiftingMode(false);
        mBinding.appBar.navContent.bottomNavigation.enableItemShiftingMode(false);
        mBinding.appBar.navContent.bottomNavigation.setItemIconTintList(null);
        mBinding.appBar.navContent.bottomNavigation.setOnNavigationItemSelectedListener(this);

    }

    private FragmentTransaction hideFragment(FragmentTransaction fragmentTransaction, int id) {
        if (id == R.id.nav_dashboard) {
            if (mFragmentDashboard != null) {
                return fragmentTransaction.hide(mFragmentDashboard);
            }
        } else if (id == R.id.nav_assignments) {

            if (mHomeworkFragment != null) {
                return fragmentTransaction.hide(mHomeworkFragment);
            }

        } else if (id == R.id.nav_learning_network) {
            if (mFragmentLearningNetwork != null) {
                return fragmentTransaction.hide(mFragmentLearningNetwork);
            }
        }
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

        } else if (aClass.equals(HomeworkFragment.class)) {

            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_assignments, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setChecked(true);


        } else if (aClass.equals(AssignmentFragmentTeacher.class)) {

            mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_assignments, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_assignments).setChecked(true);


        } else if (aClass.equals(NavigationDrawerActivity.class)) {
            mBinding.drawerLayout.openDrawer(Gravity.START);
        }

    }

    /*Checking TTS availability on device.*/
    private void checkTTS() {
        try {
            Intent intent = new Intent();
            intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //this.startActivity(intent);
            startActivityForResult(intent, ConstantUtil.CHECK_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {


            if (requestCode == ConstantUtil.CHECK_CODE) {
                if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
                GamificationPrefs.isTTSAvailable(this, true);

            }
        } catch (Exception e) {
            GeneralUtils.showToastLong(getBaseContext(), "Oops! Text To Speech not available in your device.");
            GamificationPrefs.isTTSAvailable(this, false);
            e.printStackTrace();
        }
    }


}
