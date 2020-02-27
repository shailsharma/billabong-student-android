package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.MyApplication;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutNavigationDrawerBinding;
import in.securelearning.lil.android.app.databinding.LayoutNavigationDrawerHeaderBinding;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.AnimateFragmentEvent;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.fragment.DashboardFragment;
import in.securelearning.lil.android.homework.views.activity.SubmitHomeworkActivity;
import in.securelearning.lil.android.homework.views.fragment.HomeworkFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.LearningNetworkGroupListFragment;
import in.securelearning.lil.android.profile.views.activity.StudentProfileActivity;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.events.UserProfileChangeEvent;
import in.securelearning.lil.android.syncadapter.receiver.ConnectivityChangeReceiver;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class NavigationDrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        DashboardFragment.OnDashboardFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    HomeModel mHomeModel;

    @Inject
    RxBus mRxBus;

    LayoutNavigationDrawerBinding mBinding;
    LayoutNavigationDrawerHeaderBinding mHeaderViewBinding;

    private static final String TAG = "NDActivity";
    private ConnectivityChangeReceiver mReceiver;
    private FragmentManager mFragmentManager;
    private MenuItem mDoneMenuItem;
    private HomeworkFragment mHomeworkFragment;
    private LearningNetworkGroupListFragment mFragmentLearningNetwork;
    private DashboardFragment mFragmentDashboard;
    private String mLoggedInUserId = "";
    private Disposable mSubscription;
    private AlertDialog mVersionCheckDialog;
    boolean mDoubleBackToExitPressedOnce = false;
    private int mColCount;
    private int mCurrentFragmentId = -1;
    private int mLastSelectedBottomNavItemId = -1;

    @Override
    public void onBackPressed() {

        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (!mBinding.appBar.searchView.isIconified()) {
            mBinding.appBar.searchView.setIconified(true);
        } else {
            /*Checking for activity count on back stack*/
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else if (mCurrentFragmentId != R.id.nav_dashboard) {
                mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_dashboard, 0);
                mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
            } else if (!mDoubleBackToExitPressedOnce) {
                this.mDoubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.messageGoBackExit), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDoubleBackToExitPressedOnce = false;
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
        getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_light);
        CommonUtils.getInstance().setStatusBarIconsDark(NavigationDrawerActivity.this);

        checkTTS();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        registerConnectivityChangeReceiver();
        initializeNavigationDrawer();
        initializeToolbar();

        mFragmentManager = getSupportFragmentManager();
        mColCount = getResources().getBoolean(R.bool.isTablet) ? 2 : 1;

        setDefaultFragment();

        UserProfile loggedInUser = mAppUserModel.getApplicationUser();
        setUpAppUserDetail(loggedInUser);

        listenRxBusEvents();

    }

    @Override
    protected void onResume() {
        super.onResume();

        isNewVersionAvailable();

        MyApplication.getInstance().clearPicassoCache(getBaseContext());

        clearResourcesUploadedByUser();

        /* To set last selected item as selected again in bottom navigation view
         * when user come from settings activity */
        if (mLastSelectedBottomNavItemId != -1) {
            mBinding.appBar.navContent.bottomNavigation.setSelectedItemId(mLastSelectedBottomNavItemId);
        }

    }

    /*Here setting up default fragment for activity
     * for now dashboard fragment.*/
    private void setDefaultFragment() {
        mBinding.appBar.navContent.bottomNavigation.getMenu().performIdentifierAction(R.id.nav_dashboard, Menu.FLAG_PERFORM_NO_CLOSE);
        mBinding.appBar.navContent.bottomNavigation.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
    }

    /*checking if current app version is different from published app on play store*/
    @SuppressLint("CheckResult")
    private void isNewVersionAvailable() {

        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release") && mVersionCheckDialog == null) {

            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

                mHomeModel.checkForNewVersionOnPlayStore()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Float>() {
                            @Override
                            public void accept(Float playStoreVersion) throws Exception {

                                Float currentVersion = Float.parseFloat(BuildConfig.VERSION_NAME);
                                if (currentVersion < playStoreVersion) {

                                    mVersionCheckDialog = new AlertDialog.Builder(NavigationDrawerActivity.this)
                                            .setTitle(getString(R.string.labelUpdateAvailable))
                                            .setMessage(getString(R.string.messageNewUpdateIsAvailable))
                                            .setCancelable(false)
                                            .setNegativeButton(getString(R.string.labelUpdate),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            mVersionCheckDialog = null;
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
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
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
        mHeaderViewBinding.textviewEmail.setText(userProfile.getEmail());
        mHeaderViewBinding.textviewUsername.setText(userProfile.getName());

        /*setting user image on navigation drawer header*/
        if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getLocalUrl())) {
            Picasso.with(getBaseContext())
                    .load(userProfile.getThumbnail().getLocalUrl())
                    .transform(new CircleTransform())
                    .resize(256, 256)
                    .centerCrop().into(mHeaderViewBinding.imageViewUserProfile);
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getUrl())) {
            Picasso.with(getBaseContext())
                    .load(userProfile.getThumbnail().getUrl())
                    .transform(new CircleTransform())
                    .resize(256, 256)
                    .centerCrop().into(mHeaderViewBinding.imageViewUserProfile);
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getThumb())) {
            Picasso.with(getBaseContext())
                    .load(userProfile.getThumbnail().getThumb())
                    .transform(new CircleTransform())
                    .resize(256, 256)
                    .centerCrop().into(mHeaderViewBinding.imageViewUserProfile);
        } else {
            if (!TextUtils.isEmpty(userProfile.getFirstName())) {
                String firstWord = userProfile.getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                mHeaderViewBinding.imageViewUserProfile.setImageDrawable(textDrawable);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        mDoneMenuItem = menu.findItem(R.id.action_done);
        doneVisibility(false);
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

        if (mVersionCheckDialog != null) {
            mVersionCheckDialog = null;
        }
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

                mLastSelectedBottomNavItemId = id;

                doneVisibility(false);

                mBinding.appBar.toolbar.setVisibility(View.GONE);
                mBinding.appBar.appBarLayout.setElevation(ConstantUtil.NO_ELEVATION);
                params.setScrollFlags(ConstantUtil.INT_ZERO);

                setTitle(item.getTitle());

                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);

                if (mCurrentFragmentId != id) {
                    mRxBus.send(new AnimateFragmentEvent(R.id.nav_dashboard));
                }

                if (mFragmentDashboard != null) {
                    fragmentTransaction.show(mFragmentDashboard).commitAllowingStateLoss();

                } else {
                    mFragmentDashboard = DashboardFragment.newInstance();
                    fragmentTransaction.add(R.id.container_main, mFragmentDashboard, "dashboard");
                    fragmentTransaction.commitAllowingStateLoss();
                }

                mCurrentFragmentId = id;

            }
            break;

            case R.id.nav_assignments: {

                mLastSelectedBottomNavItemId = id;

                doneVisibility(true);

                mBinding.appBar.toolbar.setVisibility(View.VISIBLE);
                mBinding.appBar.appBarLayout.setElevation(ConstantUtil.NO_ELEVATION);
                mBinding.appBar.toolbar.setTitle(item.getTitle());
                params.setScrollFlags(ConstantUtil.INT_ZERO);


                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);

                if (mCurrentFragmentId != id) {
                    mRxBus.send(new AnimateFragmentEvent(R.id.nav_assignments));
                }

                if (mHomeworkFragment != null) {
                    fragmentTransaction.show(mHomeworkFragment).commitAllowingStateLoss();

                } else {
                    mHomeworkFragment = HomeworkFragment.newInstance();
                    fragmentTransaction.add(R.id.container_main, mHomeworkFragment, "assignmentStudent");
                    fragmentTransaction.commit();
                }

                mCurrentFragmentId = id;


            }
            break;

            case R.id.nav_learning_network: {

                mLastSelectedBottomNavItemId = id;

                doneVisibility(false);

                mBinding.appBar.toolbar.setVisibility(View.VISIBLE);
                mBinding.appBar.toolbar.setTitle(item.getTitle());
                params.setScrollFlags(ConstantUtil.INT_ZERO);


                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    mHomeModel.checkUserStatus(ConstantUtil.TYPE_NETWORK);
                }

                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction = hideFragment(fragmentTransaction, mCurrentFragmentId);

                if (mCurrentFragmentId != id) {
                    mRxBus.send(new AnimateFragmentEvent(R.id.nav_learning_network));
                }
                if (mFragmentLearningNetwork != null) {
                    mBinding.appBar.appBarLayout.setElevation(4f);//since white theme; need elevation only for learning network thus we set elevation and commented this line - 'mBinding.appBar.appBarLayout.setElevation(ConstantUtil.NO_ELEVATION);'
                    fragmentTransaction.show(mFragmentLearningNetwork).commitAllowingStateLoss();

                } else {
                    mFragmentLearningNetwork = LearningNetworkGroupListFragment.newInstance(mColCount);
                    fragmentTransaction.add(R.id.container_main, mFragmentLearningNetwork, "learningNetwork");
                    fragmentTransaction.commit();
                }

                mCurrentFragmentId = id;

            }
            break;

            case R.id.nav_settings: {

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
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);

        mBinding.navView.setNavigationItemSelectedListener(this);
        mBinding.navView.setItemIconTintList(null);

        mHeaderViewBinding = LayoutNavigationDrawerHeaderBinding.bind(mBinding.navView.getHeaderView(0));

        mHeaderViewBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mLoggedInUserId)) {
                    startActivity(StudentProfileActivity.getStartIntent(mLoggedInUserId, NavigationDrawerActivity.this));
                }


            }
        });

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
    public void onDashboardFragmentInteraction(Class aClass) {

        if (aClass.equals(DashboardFragment.class)) {
            mBinding.navView.getMenu().performIdentifierAction(R.id.nav_dashboard, Menu.FLAG_PERFORM_NO_CLOSE);
            mBinding.navView.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
        } else if (aClass.equals(HomeworkFragment.class)) {
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
            startActivityForResult(intent, ConstantUtil.TTS_CHECK_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (requestCode == ConstantUtil.TTS_CHECK_CODE) {

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

    private void clearResourcesUploadedByUser() {

        try {

            /*UserProfile Folder*/
            File fileOrDirectoryUP = new File(FileUtils.getInternalStorageDirectory(getBaseContext()).getPath() + "/" + getString(R.string.pathUserProfile));
            deleteUploadedResources(fileOrDirectoryUP);

            /*LearningNetwork Folder*/
//            File fileOrDirectoryLN = new File(FileUtils.getInternalStorageDirectory(getBaseContext()).getPath() + "/" + getString(R.string.pathLearningNetwork));
//            deleteUploadedResources(fileOrDirectoryLN);


        } catch (Exception e) {
            Log.e(TAG, "accept: " + e.toString());
        }

    }

    /*Setup of background thread to delete resources (image+video) that are uploaded by user*/
    @SuppressLint("CheckResult")
    public void deleteUploadedResources(final File fileOrDirectory) {

        Completable.complete()
                .subscribeOn(Schedulers.io())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        FileUtils.delete(fileOrDirectory);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }


}
