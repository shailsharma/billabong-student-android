package in.securelearning.lil.android.login.views.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityWelcomeBinding;
import in.securelearning.lil.android.app.databinding.LayoutAppMainLoginBinding;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.CorporateSettingsModel;
import in.securelearning.lil.android.base.model.CuratorMappingModel;
import in.securelearning.lil.android.base.model.CurriculumModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.PeriodicEventsModel;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.KeyBoardUtil;
import in.securelearning.lil.android.home.utils.PermissionPrefs;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.home.views.activity.NavigationDrawerActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostSharedIntentActivity;
import in.securelearning.lil.android.login.InjectorLogin;
import in.securelearning.lil.android.login.sample.SampleLandingPageData;
import in.securelearning.lil.android.login.views.activity.startup.MyViewPagerAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTP;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTPResponse;
import in.securelearning.lil.android.syncadapter.dataobject.RolePermissions;
import in.securelearning.lil.android.syncadapter.dataobject.StudentGradeMapping;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.PrefManagerStudentSubjectMapping;
import in.securelearning.lil.android.syncadapter.utils.ShortcutUtil;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A refreshToken screen that offers refreshToken via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static final String ACTION_LOGOUT = "in.securelearning.lil.android.action.ACTION_LOGOUT";
    public static final String ACTION_UNAUTHORIZED = "in.securelearning.lil.android.action.ACTION_UNAUTHORIZED";
    public static final String ACTION_LEARNING_NETWORK = "in.securelearning.lil.android.action.ACTION_LEARNING_NETWORK";
    public static final String ACTION_NOTIFICATION = "in.securelearning.lil.android.action.NOTIFICATION";
    public static final String ACTION_LN_SHARE = "in.securelearning.lil.android.action.LNSHARE";

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private final String TAG = LoginActivity.class.getCanonicalName();
    /**
     * Keep track of the refreshToken task to ensure we can cancel it if requested.
     */
    @Inject
    Context mContext;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    GroupModel mGroupModel;
    @Inject
    CorporateSettingsModel mCorporateSettingsModel;
    @Inject
    CurriculumModel mCurriculumModel;
    @Inject
    PeriodicEventsModel mPeriodicEventsModel;
    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;
    @Inject
    JobModel mJobModel;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    CuratorMappingModel mCuratorMappingModel;
    private UserProfile mLoggedInUser;
    private String mAction = "";
    private LayoutAppMainLoginBinding mBinding;
    int[] layouts = new int[]{
            R.layout.layout_intro_course,
            R.layout.layout_intro_network,
            R.layout.layout_intro_assignment,
            R.layout.layout_intro_learning_map,
            R.layout.layout_intro_blog,
            R.layout.layout_intro_calendar
    };
    private int mPageCount = 0;
    private boolean isOTPCounterIsRunning = false;
    //private SmsVerifyCatcher smsVerifyCatcher;
    private String phoneNumberOTP = "", codeOTP = "";
    private TextWatcher mEmailWatcher, mPasswordWatcher;
    private final static String EMAIL_PHONE_SHARED_PREFERENCE = "email_phone_pref";
    private final static String SET_LOGGED_IN_EMAIL_PHONE = "set_logged_in_email_phone";

    public static Intent startIntentLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        return intent;
    }


    private CountDownTimer mCountDownTimer;

    @Override
    public void onBackPressed() {
//        if (mBinding.includeIntro.layoutAppIntro.getVisibility() == View.GONE && mBinding.includeLoginProgress.layoutLoginProgress.getVisibility() == View.GONE) {
//            if (mBinding.includeLoginEmail.layoutLoginEmail.getVisibility() == View.VISIBLE) {
//                hideLoginViaEmail();
//            } else if (mBinding.includeLoginPhone.layoutOTPProgress.getVisibility() == View.GONE && mBinding.includeLoginPhone.layoutLoginPhone.getVisibility() == View.VISIBLE) {
//                hideLoginViaPhone();
//            } else {
//                finish();
//            }
//        } else {
        switch (mAction) {
            case Intent.ACTION_MAIN:
                finish();
                break;
            case ACTION_LOGOUT:
                finishAndRemoveTask();
                break;
            case ACTION_UNAUTHORIZED:
                finishAffinity();
                break;
            default:
                finish();
                break;
        }
        //  }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLogin.INSTANCE.getComponent().inject(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
        handleUpgrade();

        final Intent intent = getIntent();
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            mAction = intent.getAction();
            if (!mAction.equals(ACTION_LOGOUT)
                    && !mAction.equals(ACTION_UNAUTHORIZED)
                    && AppPrefs.isLoggedIn(LoginActivity.this)) {
                Log.e("in", "actionMain offLogin");
                actionOfflineLogin();
            } else {

                mBinding = DataBindingUtil.setContentView(this, R.layout.layout_app_main_login);
                //setUpViewPager();
                setDefaults();
                if (mAction.equals(ACTION_UNAUTHORIZED)) {
                    intentActionDialog("Your session has expired. Please login again to continue using the app.");
                }
//                else if (mAction.equals(ACTION_USER_ARCHIVED)) {
//                    intentActionDialog("Request has been denied. Please contact your system administrator!");
//                }
                initializeUiAndClickListeners();
                handleException();
                requestAndroidPermission();
            }
        }
    }

    private void intentActionDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle("Message")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void startFullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    private void stopFullscreen() {

    }


    private void hideLoginViaEmail() {
        hideSoftKeyboard();
        mBinding.includeLoginEmail.editTextLoginEmail.removeTextChangedListener(mEmailWatcher);
        mBinding.includeLoginEmail.editTextLoginPassword.removeTextChangedListener(mPasswordWatcher);
        mBinding.includeLoginEmail.inputLayoutLoginEmail.setErrorEnabled(false);
        mBinding.includeLoginEmail.inputLayoutLoginPassword.setErrorEnabled(false);
        mBinding.includeLoginEmail.editTextLoginEmail.setText("");
        mBinding.includeLoginEmail.editTextLoginPassword.setText("");
        mBinding.includeLoginEmail.layoutLoginEmail.setVisibility(View.GONE);
        mBinding.includeLoginPhone.layoutLoginPhone.setVisibility(View.GONE);
        mBinding.includeIntro.layoutAppIntro.setVisibility(View.VISIBLE);
        AnimationUtils.pushDownExit(getBaseContext(), mBinding.includeLoginEmail.layoutLoginEmail);
        AnimationUtils.pushDownEnter(getBaseContext(), mBinding.includeIntro.layoutAppIntro);
    }

    private void hideLoginViaPhone() {
        if (isOTPCounterIsRunning) {
            mCountDownTimer.cancel();
            isOTPCounterIsRunning = false;
        }
        hideSoftKeyboard();
        mBinding.includeLoginPhone.editTextLoginPhone.setText("");
        mBinding.includeLoginPhone.pinViewOTP.setValue("");
        mBinding.includeLoginPhone.textViewOTPTimer.setText("");
        mBinding.includeLoginPhone.layoutSubmitOTP.setVisibility(View.GONE);
        mBinding.includeLoginPhone.layoutLoginPhone.setVisibility(View.GONE);
        mBinding.includeIntro.layoutAppIntro.setVisibility(View.VISIBLE);
        AnimationUtils.pushDownExit(getBaseContext(), mBinding.includeLoginEmail.layoutLoginEmail);
        AnimationUtils.pushDownExit(getBaseContext(), mBinding.includeLoginPhone.layoutLoginPhone);
        AnimationUtils.pushDownEnter(getBaseContext(), mBinding.includeIntro.layoutAppIntro);
    }

    private void showLoginViaEmail() {
        mBinding.includeIntro.layoutAppIntro.setVisibility(View.GONE);
        mBinding.includeLoginPhone.layoutLoginPhone.setVisibility(View.GONE);
        mBinding.includeLoginEmail.layoutLoginEmail.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpExit(getBaseContext(), mBinding.includeIntro.layoutAppIntro);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.includeLoginEmail.layoutLoginEmail);
        mBinding.includeLoginEmail.editTextLoginEmail.addTextChangedListener(mEmailWatcher);
        mBinding.includeLoginEmail.editTextLoginPassword.addTextChangedListener(mPasswordWatcher);
    }

    private void showLoginViaPhone() {
        mBinding.includeIntro.layoutAppIntro.setVisibility(View.GONE);
        mBinding.includeLoginPhone.layoutLoginPhone.setVisibility(View.VISIBLE);
        mBinding.includeLoginPhone.layoutRequestOTP.setVisibility(View.VISIBLE);
        mBinding.includeLoginPhone.layoutSubmitOTP.setVisibility(View.GONE);
        AnimationUtils.pushUpExit(getBaseContext(), mBinding.includeIntro.layoutAppIntro);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.includeLoginPhone.layoutLoginPhone);
        focusPhoneNumberInput();

    }

    private void focusPhoneNumberInput() {
        mBinding.includeLoginPhone.editTextLoginPhone.setFocusableInTouchMode(true);
        mBinding.includeLoginPhone.editTextLoginPhone.setFocusable(true);
        mBinding.includeLoginPhone.editTextLoginPhone.requestFocus();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyBoardUtil.showSoftKeyboard(mBinding.includeLoginPhone.editTextLoginPhone, getBaseContext());

            }
        }, 800);
    }

    private void unSubscribeFCM() {
        if (GeneralUtils.isNetworkAvailable(LoginActivity.this)) {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                        e.onNext("done");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            if (o.equals("done")) {
                                Log.e("TAG", "Successfully unSubscribe");
                            } else {
                                Log.e("TAG", "Failed unSubscribe");
                            }
                        }
                    });
        }
    }

    private void requestAndroidPermission() {
        if (Build.VERSION.SDK_INT < 23) {
        } else {
            checkAndRequestPermission();
        }
    }

    /**
     * Handle exception and send log file to `stered mail ids
     */
    private void handleException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (BuildConfig.DEBUG) {
                    showExceptionNotification(e);
                } else {
                    if (e instanceof IOException || e instanceof IllegalStateException || e instanceof DeadObjectException || e instanceof InterruptedException || e instanceof OutOfMemoryError || e instanceof NullPointerException) {

                    } else {
                        showExceptionNotification(e);
                    }
                }
                if (t != null && t.getId() == Looper.getMainLooper().getThread().getId()) {
                    restartApp(LoginActivity.this);
                }
            }
        });
    }

    private static void restartApp(Context context) {
        Intent intent = LoginActivity.startIntentLoginActivity(context);
        int pendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, pendingIntentId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
        System.exit(0);
    }

    private void showExceptionNotification(Throwable e) {
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
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"chaitendra.singh@securelearning.in,prabodh.dhabaria@securelearning.in,tikam.tailor@securelearning.in"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Crash report ");
        i.putExtra(Intent.EXTRA_TEXT, msg);
        Intent finalIntent = Intent.createChooser(i, "Send Crash Report");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getBaseContext(), NotificationUtil.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_notify_error)
                        .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.notification_icon))
                        .setColor(ContextCompat.getColor(getBaseContext(), R.color.notification_small_background))
                        .setTicker("Bug report")
                        .setAutoCancel(true)
                        .setContentTitle("Bug Report - " + e.getClass().getSimpleName())
                        .setContentText("A bug report has been generated. Please send it to help us improve the application.\n" + e.getMessage());


        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, finalIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(android.R.drawable.sym_action_email, "SEND", pendingIntent);

        builder.setContentIntent(pendingIntent);
        //notificationManager.notify(NotificationUtil.BUG_REPORT, builder.build());

        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(NotificationUtil.BUG_REPORT, builder.build());


//                if (Looper.getMainLooper().isCurrentThread()) {
//
//                    Intent intent = LoginActivity.startIntentLoginActivity(getBaseContext());
//                    startActivity(intent);
//                    finishAffinity();
//                    int pendingIntentId = 123456;
//                    PendingIntent mPendingIntent = PendingIntent.getActivity(LoginActivity.this, pendingIntentId, intent,
//                            PendingIntent.FLAG_CANCEL_CURRENT);
//                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                    System.exit(0);
//                }
    }

    @SuppressLint("CheckResult")
    private void setDefaults() {
        Completable.complete().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                mBinding.includeLoginEmail.textViewAppVersion.setText(BuildConfig.VERSION_NAME);

                final Intent intent = getIntent();
                if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
                    if (mAction.equals(Intent.ACTION_MAIN) || mAction.equals(ACTION_LEARNING_NETWORK) || mAction.equals(ACTION_NOTIFICATION) || mAction.equals(ACTION_LN_SHARE)) {
                        Log.e("in", "actionMain setDef");

                        Log.e("in", "actionMain showIntro");

                    } else if (mAction.equals(ACTION_LOGOUT)) {
                        unSubscribeFCM();
                        logoutAction();
                    } else if (mAction.equals(ACTION_UNAUTHORIZED)) {
                        unSubscribeFCM();
                        unauthorizedAction();
                    }
//                    else if (mAction.equals(ACTION_USER_ARCHIVED)) {
//                        unSubscribeFCM();
//                        unauthorizedAction();
//                    }
                } else if (AppPrefs.isLoggedIn(LoginActivity.this)) {
                    actionOfflineLogin();
                }
            }
        });

    }

    private void handleUpgrade() {
        int version = PrefManager.getUpdatedToVersion(this);

        if (version < 147) {
            actionVersionLessThan147();
            PrefManager.setUpdatedToVersion(this, BuildConfig.VERSION_CODE);
            version = 147;
        }


    }

    private void actionVersionLessThan41() {
        try {
            mDatabaseQueryHelper.reIndexViewFromDatabaseNotifications(mDatabaseQueryHelper.VIEW_NAME_EVENT_LIST_BY_CREATION_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionVersionLessThan147() {
        try {
            mJobModel.updateSubjectIcons(LoginActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletePreviousPeriods() {
        mPeriodicEventsModel.removeAll();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //smsVerifyCatcher.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //showSplash();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //smsVerifyCatcher.onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void logoutAction() {
        SyncServiceHelper.stopSyncService(LoginActivity.this);
//        removePeriods();
//        AppPrefs.setUserId("", this);
//        AppPrefs.setUserName("", this);
//        AppPrefs.setUserPassword("", this);
//        AppPrefs.setLoggedIn(false, this);
        clearPreferences(LoginActivity.this);
        mAppUserModel.changeToGuestUser();
        mAppUserModel.setApplicationUser(new UserProfile());
        //clear notifications
        NotificationManagerCompat.from(LoginActivity.this).cancelAll();
    }

    private void removePeriods() {
        mPeriodicEventsModel.removeAll();
    }

    private void unauthorizedAction() {
        SyncServiceHelper.stopSyncService(this);
        clearPreferences(LoginActivity.this);
        mAppUserModel.changeToGuestUser();
        mAppUserModel.setApplicationUser(new UserProfile());
        //clear notifications
        NotificationManagerCompat.from(this).cancelAll();
    }

    private void initializeUiAndClickListeners() {
        if (getEmailOrPhoneFromSharedPref() != null && !getEmailOrPhoneFromSharedPref().isEmpty()) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.addAll(getEmailOrPhoneFromSharedPref());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.select_dialog_item, arrayList);
            mBinding.includeLoginEmail.editTextLoginEmail.setThreshold(1);
            mBinding.includeLoginEmail.editTextLoginEmail.setAdapter(adapter);
            mBinding.includeLoginEmail.editTextLoginEmail.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorGreyDark));
        }
        mBinding.includeLoginEmail.editTextLoginEmail.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        mBinding.includeLoginEmail.buttonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBinding.includeLoginPhone.buttonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        mBinding.includeIntro.imageButtonLoginFacebook.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(LoginActivity.this, FacebookConnectActivity.class);
//                startActivity(mIntent);
//            }
//        });
//
//        mBinding.includeIntro.imageButtonLoginGooglePlus.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(LoginActivity.this, GoogleConnectActivity.class);
//                startActivity(mIntent);
//            }
//        });

        mBinding.includeIntro.buttonIntroLoginViaEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginViaEmail();

            }
        });

        mBinding.includeIntro.buttonIntroLoginViaPhone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginViaPhone();

            }
        });

        mBinding.includeLoginEmail.layoutLoginEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });

        mBinding.includeLoginPhone.layoutLoginPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });


//        mBinding.includeLoginEmail.editTextLoginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin(textView);
//                    return true;
//                }
//                return false;
//            }
//        });


        mBinding.includeLoginEmail.buttonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                attemptLogin(view);

            }
        });

        mBinding.includeLoginPhone.editTextLoginPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.includeLoginPhone.editTextLoginPhone.getText().toString().trim().length() == 10) {
                    mBinding.includeLoginPhone.buttonRequestOTP.setEnabled(true);
                    mBinding.includeLoginPhone.buttonRequestOTP.setClickable(true);
                    mBinding.includeLoginPhone.inputLayoutLoginPhone.setHint("");
                    hideSoftKeyboard();
                } else {
                    mBinding.includeLoginPhone.buttonRequestOTP.setEnabled(false);
                    mBinding.includeLoginPhone.buttonRequestOTP.setClickable(false);
                    mBinding.includeLoginPhone.inputLayoutLoginPhone.setHint("Enter 10 digit mobile number");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.includeLoginPhone.buttonRequestOTP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.includeLoginPhone.pinViewOTP.setValue("");
                mBinding.includeLoginPhone.pinViewOTP.setEnabled(true);
                mBinding.includeLoginPhone.pinViewOTP.setClickable(true);
                mBinding.includeLoginPhone.pinViewOTP.setFocusable(true);
                mBinding.includeLoginPhone.pinViewOTP.setFocusableInTouchMode(true);
                performOTPRequest(view, mBinding.includeLoginPhone.editTextLoginPhone.getText().toString().trim());

            }
        });

        mBinding.includeLoginPhone.buttonSubmitOTP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(phoneNumberOTP)) {
                    try {

                        performOnlineLogin(view, phoneNumberOTP, mBinding.includeLoginPhone.pinViewOTP.getValue().toString().trim(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        mBinding.includeLoginPhone.textViewOTPTimer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOTPSubmissionLayout();

            }
        });


        mBinding.includeLoginEmail.buttonCreateAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        mBinding.includeIntro.buttonIntroCreateAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        mEmailWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.includeLoginEmail.editTextLoginEmail.getText().toString().trim().isEmpty()) {
                    mBinding.includeLoginEmail.inputLayoutLoginEmail.setError(getString(R.string.error_field_email_phone));
                    mBinding.includeLoginEmail.inputLayoutLoginEmail.setErrorEnabled(true);
                } else if (!isEmailValid(mBinding.includeLoginEmail.editTextLoginEmail.getText().toString().trim())) {
                    mBinding.includeLoginEmail.inputLayoutLoginEmail.setError(getString(R.string.error_invalid_email));
                } else {
                    mBinding.includeLoginEmail.inputLayoutLoginEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

        };

        mPasswordWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.includeLoginEmail.editTextLoginPassword.getText().toString().trim().isEmpty()) {
                    mBinding.includeLoginEmail.inputLayoutLoginPassword.setError(getString(R.string.error_field_password));
                    mBinding.includeLoginEmail.inputLayoutLoginPassword.setErrorEnabled(true);
                } else if (!isPasswordValid(mBinding.includeLoginEmail.editTextLoginPassword.getText().toString().trim())) {
                    mBinding.includeLoginEmail.inputLayoutLoginPassword.setError(getString(R.string.error_invalid_password));
                } else {
                    mBinding.includeLoginEmail.inputLayoutLoginPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

        };

    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only six numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    private void performOTPRequest(final View view, String mobileNo) {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mBinding.includeLoginPhone.pinViewOTP.setValue("");
            mBinding.includeLoginPhone.textViewOTPTimer.setText("");
            if (mCountDownTimer != null && !isOTPCounterIsRunning) {
                mCountDownTimer.cancel();
            }
            mBinding.includeLoginPhone.containerPhoneLogin.setVisibility(View.GONE);
            mBinding.includeLoginPhone.layoutOTPProgress.setVisibility(View.VISIBLE);
            RequestOTP requestOTP = new RequestOTP();
            requestOTP.setCode(null);
            requestOTP.setMobile("+91" + mobileNo);
            final Call<RequestOTPResponse> appUserCall = mNetworkModel.requestOTP(requestOTP);

            Observable.create(new ObservableOnSubscribe<RequestOTPResponse>() {
                @Override
                public void subscribe(ObservableEmitter<RequestOTPResponse> subscriber) {

                    try {
                        Response<RequestOTPResponse> response = appUserCall.execute();
                        if (response != null && response.isSuccessful()) {
                            com.couchbase.lite.util.Log.e("OTP", "successful");
                            RequestOTPResponse requestOTPResponse = response.body();
                            if (!TextUtils.isEmpty(requestOTPResponse.getPhoneNumber())) {
                                subscriber.onNext(requestOTPResponse);
                            } else if (!TextUtils.isEmpty(requestOTPResponse.getOnError())) {
                                subscriber.onError(new Throwable(requestOTPResponse.getOnError()));
                            }

                        } else {
                            Log.e("OTP", "api failed to connect" + response.message());
                            subscriber.onError(new Throwable("Failed to connect to server"));
                        }
                        subscriber.onComplete();
                    } catch (SocketTimeoutException t) {
                        subscriber.onError(new Throwable("OTP request timeout"));
                        subscriber.onComplete();
                        t.printStackTrace();
                        Log.e("OTP", "request timeout" + t.toString());
                    } catch (Exception t) {
                        subscriber.onComplete();
                        t.printStackTrace();
                        Log.e("OTP", "err getting OTP" + t.toString());
                    }

                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.computation())
                    .subscribe(new Consumer<RequestOTPResponse>() {
                        @Override
                        public void accept(RequestOTPResponse requestOTPResponse) {

                            showOTPSubmissionLayout(requestOTPResponse.getPhoneNumber());
                            phoneNumberOTP = requestOTPResponse.getPhoneNumber();
                            //startReceivingOTPCodeFromSMS(view,requestOTPResponse);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable t) {
                            mBinding.includeLoginPhone.layoutOTPProgress.setVisibility(View.GONE);
                            mBinding.includeLoginPhone.containerPhoneLogin.setVisibility(View.VISIBLE);
                            Snackbar.make(view, t.getMessage(), Snackbar.LENGTH_LONG).show();
                            t.printStackTrace();

                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            mBinding.includeLoginPhone.layoutOTPProgress.setVisibility(View.GONE);
                            mBinding.includeLoginPhone.containerPhoneLogin.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            Snackbar.make(view, getString(R.string.connect_internet), Snackbar.LENGTH_LONG).show();
        }

    }

    private void startReceivingOTPCodeFromSMS(final View view, final RequestOTPResponse requestOTPResponse) {

    }

    private void hideOTPSubmissionLayout() {
        if (mBinding.includeLoginPhone.textViewOTPTimer.getText().toString().trim().equals(getString(R.string.label_request_otp_again))) {
            mBinding.includeLoginPhone.layoutSubmitOTP.setVisibility(View.GONE);
            mBinding.includeLoginPhone.layoutRequestOTP.setVisibility(View.VISIBLE);
            mBinding.includeLoginPhone.textViewOTPTimer.setText("");
            KeyBoardUtil.hideSoftKeyboard(mBinding.includeLoginPhone.pinViewOTP, getBaseContext());
            focusPhoneNumberInput();
        }
    }

    private void showOTPSubmissionLayout(String phoneNumber) {
        KeyBoardUtil.showSoftKeyboard(mBinding.includeLoginPhone.pinViewOTP, getBaseContext());
        mBinding.includeLoginPhone.layoutRequestOTP.setVisibility(View.GONE);
        mBinding.includeLoginPhone.containerPhoneLogin.setVisibility(View.GONE);
        mBinding.includeLoginPhone.layoutSubmitOTP.setVisibility(View.VISIBLE);
        mBinding.includeLoginPhone.textViewOTPTimer.setVisibility(View.VISIBLE);
        mBinding.includeLoginPhone.textViewOTPText.setText(getString(R.string.label_otp_text)
                + " " + phoneNumber);

        mCountDownTimer = new CountDownTimer(180000, 1000) { // adjust the milli seconds here

            public void onTick(long millis) {
                isOTPCounterIsRunning = true;
                long seconds = (millis / 1000) % 60;
                String strSeconds = "";
                if (seconds <= 9) {
                    strSeconds = "0" + String.valueOf(seconds);
                } else {
                    strSeconds = String.valueOf(seconds);
                }
                mBinding.includeLoginPhone.textViewOTPTimer.setText("0" + String.valueOf((millis / 1000) / 60) + ":" + strSeconds);
            }

            public void onFinish() {
                isOTPCounterIsRunning = false;
                mBinding.includeLoginPhone.textViewOTPText.setText(getString(R.string.label_otp_not_received));
                mBinding.includeLoginPhone.textViewOTPTimer.setText(getString(R.string.label_request_otp_again));
                mBinding.includeLoginPhone.pinViewOTP.setValue("");
            }

        }.start();
    }

    private void signUp() {
        Intent mIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(mIntent);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    private boolean checkAndRequestPermission() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int receiveSMSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readSMSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (receiveSMSPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (readSMSPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }

        return true;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }

//    /**
//     * need for Android 6 real time permissions
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    /**
     * Attempts to sign in or register the account specified by the refreshToken form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual refreshToken attempt is made.
     *
     * @param view
     */
    private void attemptLogin(final View view) {

        // Reset errors.
        mBinding.includeLoginEmail.inputLayoutLoginEmail.setErrorEnabled(false);
        mBinding.includeLoginEmail.inputLayoutLoginPassword.setErrorEnabled(false);

        // Store values at the time of the refreshToken attempt.
        String email = mBinding.includeLoginEmail.editTextLoginEmail.getText().toString().trim();
        String password = mBinding.includeLoginEmail.editTextLoginPassword.getText().toString().trim();

        mBinding.includeLoginEmail.editTextLoginEmail.setText(email);
        mBinding.includeLoginEmail.editTextLoginPassword.setText(password);

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mBinding.includeLoginEmail.inputLayoutLoginEmail.setError(getString(R.string.error_field_email_phone));
            focusView = mBinding.includeLoginEmail.editTextLoginEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mBinding.includeLoginEmail.inputLayoutLoginEmail.setError(getString(R.string.error_invalid_email));
            focusView = mBinding.includeLoginEmail.editTextLoginEmail;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password)) {
            mBinding.includeLoginEmail.inputLayoutLoginPassword.setError(getString(R.string.error_field_password));
            focusView = mBinding.includeLoginEmail.editTextLoginPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mBinding.includeLoginEmail.inputLayoutLoginPassword.setError(getString(R.string.error_invalid_password));
            focusView = mBinding.includeLoginEmail.editTextLoginPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt refreshToken and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user refreshToken attempt.
            hideSoftKeyboard();


            performLogin(view, email, password, false);
        }


    }

    /**
     * check all type of validations for email
     *
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
//        if (email.contains("@") && email.contains(".")) {
//            return true;
//        } else if (email.length() == 10 && TextUtils.isDigitsOnly(email)) {
//            return true;
//        }
        return true;
    }

    /**
     * check all type of validations for password
     *
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the refreshToken form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, final boolean isOTPLogin) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isOTPLogin) {
                    mBinding.includeLoginPhone.layoutLoginPhone.setVisibility(View.VISIBLE);
                    mBinding.includeLoginProgress.layoutLoginProgress.setVisibility(View.GONE);
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                        //                       int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

                        mBinding.includeLoginEmail.containerLogin.setVisibility(show ? View.GONE : View.VISIBLE);
//                        mBinding.includeLoginEmail.containerLogin.animate().setDuration(shortAnimTime).alpha(
//                                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                mBinding.includeLoginEmail.containerLogin.setVisibility(show ? View.GONE : View.VISIBLE);
//                            }
//                        });

                        mBinding.includeLoginProgress.layoutLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
//                        mBinding.includeLoginProgress.layoutLoginProgress.animate().setDuration(shortAnimTime).alpha(
//                                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                mBinding.includeLoginProgress.layoutLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
//                            }
//                        });


                    } else {

                        mBinding.includeLoginProgress.layoutLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                        mBinding.includeLoginEmail.containerLogin.setVisibility(show ? View.GONE : View.VISIBLE);

                    }
                }


            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    private void performLogin(final View view, final String email, final String password, final boolean isFromOTP) {
        showProgress(true, false);

//        AppPrefs.setUserName(email, LoginActivity.this);
//        AppPrefs.setUserPassword(password, LoginActivity.this);

        //Check if user is exist in local database
        Observable.just(LoginActivity.this).subscribeOn(Schedulers.io()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws IOException {
                //First time user profile not available so refreshToken online
                if (GeneralUtils.isNetworkAvailable(LoginActivity.this)) {
                    performOnlineLogin(view, email, password, isFromOTP);
                } else {
                    Snackbar.make(view, getString(R.string.connect_internet), Snackbar.LENGTH_LONG).show();
                    showProgress(false, false);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                showProgress(false, false);
            }
        });
    }

    private void actionOfflineLogin() {
        final String userId = AppPrefs.getUserId(this);
        mAppUserModel.changeToUserDatabase(userId);
        copyUserProfileFromGuestDB(userId);
//            setUpUserProfileForApplication(appUser);
        if (mAction != null && mAction.equals(ACTION_LEARNING_NETWORK)) {
            startHomeActivityForLearningNetwork();
            Log.e("in", "actionMain startLN");
        } else if (mAction != null && mAction.equals(ACTION_NOTIFICATION)) {
            startHomeActivityForNotification();
            Log.e("in", "actionMain startN");
        } else if (mAction != null && mAction.equals(ACTION_LN_SHARE)) {
            startCreatePostActivity();
            Log.e("in", "actionSEND startCreatePost");
        } else if (mAction != null && mAction.equals(ShortcutUtil.ACTION_SHORTCUT_ASSIGNMENT)) {
            startHomeActivityForShortcuts(ShortcutUtil.ACTION_SHORTCUT_ASSIGNMENT);
            Log.e("in", "actionShortcut startAssignment");
        } else if (mAction != null && mAction.equals(ShortcutUtil.ACTION_SHORTCUT_LEARNING_NETWORK)) {
            startHomeActivityForShortcuts(ShortcutUtil.ACTION_SHORTCUT_LEARNING_NETWORK);
            Log.e("in", "actionShortcut startLearningNetwork");
        } else if (mAction != null && mAction.equals(ShortcutUtil.ACTION_SHORTCUT_CALENDAR)) {
            startHomeActivityForShortcuts(ShortcutUtil.ACTION_SHORTCUT_CALENDAR);
            Log.e("in", "actionShortcut startCalendar");
        } else if (mAction != null && mAction.equals(ShortcutUtil.ACTION_SHORTCUT_WORKSPACE)) {
            startHomeActivityForShortcuts(ShortcutUtil.ACTION_SHORTCUT_WORKSPACE);
            Log.e("in", "actionShortcut startWorkspace");
        } else if (mAction != null && mAction.equals(ShortcutUtil.ACTION_SHORTCUT_TRAINING)) {
            startHomeActivityForShortcuts(ShortcutUtil.ACTION_SHORTCUT_TRAINING);
            Log.e("in", "actionShortcut startTraining");
        } else if (mAction != null && mAction.equals(ShortcutUtil.ACTION_SHORTCUT_LEARNING_MAP)) {
            startHomeActivityForShortcuts(ShortcutUtil.ACTION_SHORTCUT_LEARNING_MAP);
            Log.e("in", "actionShortcut startLearningMap");
        } else {
            startHomeActivity();
            Log.e("in", "actionMain startND");
        }

    }


    private void copyUserProfileFromGuestDB(String userId) {
        int version = PrefManager.getUpdatedToVersion(this);

        if (version < 89) {
            UserProfile userProfile = mAppUserModel.getUserProfileFromUidSyncGuestDB(userId);
            userProfile.setDocId("");
            mAppUserModel.saveUserProfile(userProfile);

            PrefManager.setUpdatedToVersion(this, BuildConfig.VERSION_CODE);
            version = 89;
            userProfile = null;
        }
    }

    /*Set refreshToken user credentials for future use*/
//    public void setUpUserProfileForApplication(AppUser user) {
//        user.setUserType(getUserType(user.getUserInfo()));
//        Injector.INSTANCE.getComponent().appUserModel().setApplicationUser(user);
//    }

    @SuppressLint("CheckResult")
    void performOnlineLogin(final View view, final String email, final String password, final boolean isFromOTP) throws IOException {
        //Here we unSubscribe fcm
//        if (FirebaseInstanceId.getInstance()!=null){
//            FirebaseInstanceId.getInstance().deleteInstanceId();
//        }
        stopServicesAndCancelNotifications(LoginActivity.this);
        //Login through server if network is available
        if (GeneralUtils.isNetworkAvailable(LoginActivity.this)) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFromOTP) {

                        mBinding.includeLoginPhone.layoutLoginPhone.setVisibility(View.GONE);
                        mBinding.includeLoginPhone.layoutSubmitOTP.setVisibility(View.GONE);
                        mBinding.includeLoginPhone.layoutRequestOTP.setVisibility(View.VISIBLE);
                        mBinding.includeLoginProgress.layoutLoginProgress.setVisibility(View.VISIBLE);
                    }
                    AnimationUtils.continueBlinkAnimation(mBinding.includeLoginProgress.textViewLoadingMessage);
                    if (mLoggedInUser != null) {
                        mBinding.includeLoginProgress.textViewLoadingMessage.setText(getString(R.string.message_login_wait));
                    } else {
                        mBinding.includeLoginProgress.textViewLoadingMessage.setText(getString(R.string.message_login_wait));
                        //mBinding.includeLoginProgress.textViewLoadingMessage.setText(getString(R.string.message_login));

                    }

                }
            });

            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> subscriber) {
                    try {
                        if (SyncServiceHelper.login(LoginActivity.this, email, password, isFromOTP)) {

                            AppPrefs.setUserName(email, LoginActivity.this);
                            AppPrefs.setUserPassword(password, LoginActivity.this);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.includeLoginProgress.textViewLoadingMessage.setText(R.string.message_login);

                                }
                            });
                            if (SyncServiceHelper.setCurrentUserProfile(LoginActivity.this)) {
                                String userId = AppPrefs.getUserId(LoginActivity.this);
                                final UserProfile userProfile = mAppUserModel.getUserProfileFromUidSyncGuestDB(userId);
                                userProfile.setDocId("");
                                if (!performLoginSuccessAction(mContext, userId, userProfile)) {
                                    throw new Exception("Failed to load your Profile");
                                }
                                new SampleLandingPageData().insertSampleData();
                                fetchAndSetRolePermissions(getBaseContext());
                                SyncServiceHelper.updateProfile();
                                fetchUserSubjectsFromInstituteGradeSection();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.includeLoginProgress.textViewLoadingMessage.setText("Loading groups...");

                                    }
                                });
                                try {
                                    createDownloadJobForGroups(userProfile);
                                } catch (Exception e) {
                                    throw new SocketTimeoutException("Failed to load groups");
                                }

                                Set<String> editTextValues = getEmailOrPhoneFromSharedPref();
                                editTextValues.add(mBinding.includeLoginEmail.editTextLoginEmail.getText().toString().trim());
                                saveEmailOrPhoneToSharedPref(editTextValues);
                                if (mAction != null && mAction.equals(ACTION_LN_SHARE)) {
                                    startCreatePostActivity();
                                } else {
                                    startHomeActivity();
                                }
                                AppPrefs.setLoggedIn(true, LoginActivity.this);
                            } else {
                                throw new SocketTimeoutException("Failed to load your Profile");
                            }

                        } else {
                            if (email.contains("@") && email.contains(".")) {
                                Snackbar.make(view, getString(R.string.incorrect_email_or_password), Snackbar.LENGTH_LONG).show();
                            } else if (email.length() == 10 && TextUtils.isDigitsOnly(email)) {
                                Snackbar.make(view, getString(R.string.incorrect_phone_number_or_password), Snackbar.LENGTH_LONG).show();

                            } else {
                                Snackbar.make(view, getString(R.string.incorrect_login_information), Snackbar.LENGTH_LONG).show();
                            }
                            showProgress(false, isFromOTP);
                            shakeAlert();
                        }


                    } catch (Exception t) {
                        t.printStackTrace();
                        Log.e("LoginActivity", "err refreshToken" + t.toString());
                        Snackbar.make(view, t.getMessage(), Snackbar.LENGTH_LONG).show();
                        showProgress(false, isFromOTP);
                    }
                }
            }).subscribeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    String message = t.getMessage();
                    Log.e("LoginActivity", "err refreshToken" + message);
                    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                    showProgress(false, isFromOTP);
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
//                    showProgress(false, isFromOTP);
                }
            });


        } else {
            Snackbar.make(view, getString(R.string.connect_internet), Snackbar.LENGTH_LONG).show();
            if (isFromOTP) {
                mBinding.includeLoginPhone.layoutLoginPhone.setVisibility(View.VISIBLE);
                mBinding.includeLoginProgress.layoutLoginProgress.setVisibility(View.GONE);
            } else {
                showProgress(false, false);
            }
        }

    }

    private boolean fetchAndSetRolePermissions(Context context) throws IOException {
        Call<RolePermissions> call = mNetworkModel.fetchRolePermissions();
        Response<RolePermissions> response = call.execute();
        if (response.isSuccessful()) {
            RolePermissions permissions = response.body();
            if (permissions != null && permissions.getPermissions() != null && permissions.getPermissions().length > 0) {
                PermissionPrefs.setPermissions(response.body(), context);
                return true;
            }

        }
        return false;
    }

    private void saveEmailOrPhoneToSharedPref(Set<String> editTextValues) {
        SharedPreferences sharedPrefs = getSharedPreferences(EMAIL_PHONE_SHARED_PREFERENCE, 0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet(SET_LOGGED_IN_EMAIL_PHONE, editTextValues);
        editor.commit();
    }

    public Set<String> getEmailOrPhoneFromSharedPref() {

        SharedPreferences sharedPrefs = getSharedPreferences(EMAIL_PHONE_SHARED_PREFERENCE, 0);
        if (sharedPrefs.getStringSet(SET_LOGGED_IN_EMAIL_PHONE, null) != null && !sharedPrefs.getStringSet(SET_LOGGED_IN_EMAIL_PHONE, null).isEmpty()) {
            Set<String> s = new HashSet<String>(sharedPrefs.getStringSet(SET_LOGGED_IN_EMAIL_PHONE, new HashSet<String>()));
            return s;
        } else {
            return new HashSet<String>();
        }

    }

    private void fetchUserSubjectsFromInstituteGradeSection() throws IOException {
        UserProfile userProfile = mAppUserModel.getApplicationUser();
        if (userProfile != null
                && userProfile.getAssociation() != null
                && userProfile.getGrade() != null
                && userProfile.getSection() != null
                && !TextUtils.isEmpty(userProfile.getAssociation().getId())
                && !TextUtils.isEmpty(userProfile.getGrade().getId())
                && !TextUtils.isEmpty(userProfile.getSection().getId())) {
            Call<StudentGradeMapping> arrayListCall = mNetworkModel.getSubjectFromInstituteGradeSection(userProfile.getAssociation().getId(), userProfile.getGrade().getId(), userProfile.getSection().getId());
            Response<StudentGradeMapping> response = arrayListCall.execute();
            if (response != null && response.isSuccessful()) {
                PrefManagerStudentSubjectMapping.setSubjectList(response.body().getSubjects(), getBaseContext());
            }
        }
    }

    private void shakeAlert() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AnimationUtils.shake(getBaseContext(), mBinding.includeLoginEmail.inputLayoutLoginEmail);
                AnimationUtils.shake(getBaseContext(), mBinding.includeLoginEmail.inputLayoutLoginPassword);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(400);
                } else {
                    Log.v("Can Vibrate", "NO");
                }
            }
        });


    }

    private void createDownloadJobForGroups(ArrayList<String> groupsIdsList) {

        if (groupsIdsList != null && groupsIdsList.size() > 0) {
            for (final String s : groupsIdsList) {
                if (!TextUtils.isEmpty(s)) {
                    JobCreator.createDownloadGroupJob(s).execute();
//                    JobCreator.createTrackingRouteDownloadJob(s).execute();
                }
            }
            for (final String s : groupsIdsList) {
                if (!TextUtils.isEmpty(s)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.SUBSCRIBE_FCM_PREFIX + s);
                }
            }
        }
    }

    private void createDownloadJobForGroups(UserProfile userProfile) throws SocketTimeoutException {
        HashSet<String> groupSet = new HashSet<String>();

        for (GroupAbstract groupAbstract :
                userProfile.getMemberGroups()) {
            if (groupAbstract != null && !TextUtils.isEmpty(groupAbstract.getObjectId())) {
                String groupId = groupAbstract.getObjectId();
                if (!groupSet.contains(groupId)) {

                    groupSet.add(groupId);
                    JobCreator.createDownloadGroupJob(groupId).execute();
                    Group group = mGroupModel.getGroupFromUidSync(groupId);
                    if (group == null || TextUtils.isEmpty(group.getObjectId())) {
                        throw new SocketTimeoutException("Failed to load groups");
                    }
                }
            }


        }

        for (GroupAbstract groupAbstract :
                userProfile.getModeratedGroups()) {
            if (groupAbstract != null && !TextUtils.isEmpty(groupAbstract.getObjectId())) {
                String groupId = groupAbstract.getObjectId();
                if (!groupSet.contains(groupId)) {
                    groupSet.add(groupId);
                    JobCreator.createDownloadGroupJob(groupId).execute();
                    Group group = mGroupModel.getGroupFromUidSync(groupId);
                    if (group == null || TextUtils.isEmpty(group.getObjectId())) {
                        throw new SocketTimeoutException("Failed to load groups");
                    }
                }


            }
        }


        if (groupSet != null && groupSet.size() > 0) {
            for (final String s : groupSet) {
                if (!TextUtils.isEmpty(s)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.SUBSCRIBE_FCM_PREFIX + s);
                }
            }
        }
    }

    private ArrayList<String> fetchGroupsIdsList(UserProfile mLoggedInUserProfile) {
        ArrayList<String> groupsIdsList = new ArrayList<>();
        if (mLoggedInUserProfile.getModeratedGroups() != null) {

            for (GroupAbstract groupAbstract : mLoggedInUserProfile.getModeratedGroups()) {
                if (groupAbstract != null) groupsIdsList.add(groupAbstract.getObjectId());
            }
        }
        if (mLoggedInUserProfile.getMemberGroups() != null) {
            for (GroupAbstract groupAbstract : mLoggedInUserProfile.getMemberGroups()) {
                if (groupAbstract != null) groupsIdsList.add(groupAbstract.getObjectId());
            }
        }

        return groupsIdsList;
    }

    private boolean performLoginSuccessAction(Context context, String email, UserProfile userProfile) {
        try {

            mAppUserModel.changeToUserDatabase(email);
            mAppUserModel.saveUserProfile(userProfile);
            //clear notifications
            NotificationManagerCompat.from(context).cancelAll();
            return true;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    private static void stopServicesAndCancelNotifications(Context context) {
        try {
            // stop sync
            SyncServiceHelper.stopSyncService(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //clear notifications
        NotificationManagerCompat.from(context).cancelAll();

    }

    private void startHomeActivity() {

        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        startActivity(intent);
        finish();

    }

    private void startHomeActivityForLearningNetwork() {
        SyncServiceHelper.startSyncService(this);
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        intent.setAction(ACTION_LEARNING_NETWORK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startHomeActivityForShortcuts(String action) {
        SyncServiceHelper.startSyncService(this);
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        intent.setAction(action);
        startActivity(intent);
        finish();
    }

    private void startHomeActivityForNotification() {
        SyncServiceHelper.startSyncService(this);
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        intent.setAction(ACTION_NOTIFICATION);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startCreatePostActivity() {
        startActivity(CreatePostSharedIntentActivity.getIntentForCreatePost(this, getIntent().getType(), getIntent().getExtras()));
        finish();
    }

    public static Intent getLogoutIntent(Context context) {
        clearPreferences(context);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction(ACTION_LOGOUT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getUnauthorizedIntent(Context context) {
        stopServicesAndCancelNotifications(context);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction(ACTION_UNAUTHORIZED);
        return intent;
    }

    public static void clearPreferences(Context context) {
        AppPrefs.clearPrefs(context);
        PermissionPrefs.clearPrefs(context);
        PreferenceSettingUtilClass.clearPrefs(context);
        PrefManager.clearPrefs(context);
        PrefManagerStudentSubjectMapping.clearPrefs(context);

    }

    public static Intent startLoginActivityFromPostActivity(Context context, String action, String type, Bundle bundle) {

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(action);
        intent.setType(type);
        intent.putExtras(bundle);
        return intent;
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void setUpIntroViewPager() {
        mBinding.includeIntro.layoutAppIntro.setVisibility(View.VISIBLE);
        mBinding.includeLoginEmail.layoutLoginEmail.setVisibility(View.GONE);
        addBottomDots(0);
        changeStatusBarColor();
//        final AppIntroViewPagerAdapter appIntroViewPagerAdapter = new AppIntroViewPagerAdapter(this, layouts);
//        mBinding.includeIntro.viewPagerIntro.setAdapter(appIntroViewPagerAdapter);
//        mBinding.includeIntro.viewPagerIntro.addOnPageChangeListener(viewPagerPageChangeListener);

        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                mPageCount = mBinding.includeIntro.viewPagerIntro.getCurrentItem();
                if (mPageCount == layouts.length - 1) {
                    mPageCount = 0;
                } else {
                    mPageCount++;
                }
                mBinding.includeIntro.viewPagerIntro.setCurrentItem(mPageCount, true);
            }
        };


        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 6000);
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        mBinding.includeIntro.layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            mBinding.includeIntro.layoutDots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive[currentPage]);
            //binding.btnNext.setTextColor(colorsActive[currentPage]);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                //binding.btnNext.setText(getString(R.string.start));
                mBinding.includeIntro.buttonSkipIntro.setVisibility(View.GONE);
            } else {
                // still pages are left
                // binding.btnNext.setText(getString(R.string.next));
                mBinding.includeIntro.buttonSkipIntro.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class AppIntroViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private int[] layouts;
        Context mContext;

        public AppIntroViewPagerAdapter(Context context, int[] layouts) {
            this.mContext = context;
            this.layouts = layouts;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

    }

    public static final class WelcomeFragment extends Fragment {
        private MyViewPagerAdapter myViewPagerAdapter;
        private TextView[] dots;
        private int[] layouts;
        private ActivityWelcomeBinding binding;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_welcome, container, false);

            // Making notification bar transparent
            if (Build.VERSION.SDK_INT >= 21) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

            // layouts of all welcome sliders
            // add few more layouts if you want
            layouts = new int[]{
                    R.layout.layout_intro_course,
                    R.layout.layout_intro_network,
                    R.layout.layout_intro_learning_map,
                    R.layout.layout_intro_assignment};

            // adding bottom dots
            addBottomDots(0);

            // making notification bar transparent
            changeStatusBarColor();

            myViewPagerAdapter = new MyViewPagerAdapter(getActivity(), layouts);
            binding.wowoViewpager.setAdapter(myViewPagerAdapter);
            binding.wowoViewpager.addOnPageChangeListener(viewPagerPageChangeListener);

            binding.btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mBinding.includeIntro.layoutAppIntro.setVisibility(View.GONE);
//                    mBinding.includeLoginEmail.layoutLogin.setVisibility(View.VISIBLE);
                }
            });

            binding.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current = getItem(+1);
                    if (current < layouts.length) {
                        binding.wowoViewpager.setCurrentItem(current);
                    } else {
//                        mBinding.includeIntro.layoutAppIntro.setVisibility(View.GONE);
//                        mBinding.includeLoginEmail.layoutLogin.setVisibility(View.VISIBLE);
                    }
                }
            });
            return binding.getRoot();
        }


        private void addBottomDots(int currentPage) {
            dots = new TextView[layouts.length];

            int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
            int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

            binding.layoutDots.removeAllViews();
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(getActivity());
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(colorsInactive[currentPage]);
                binding.layoutDots.addView(dots[i]);
            }

            if (dots.length > 0) {
                dots[currentPage].setTextColor(colorsActive[currentPage]);
                binding.btnNext.setTextColor(colorsActive[currentPage]);
            }
        }

        private int getItem(int i) {
            return binding.wowoViewpager.getCurrentItem() + i;
        }

        //  viewpager change listener
        ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts.length - 1) {
                    // last page. make button text to GOT IT
                    binding.btnNext.setText(getString(R.string.start));
                    binding.btnSkip.setVisibility(View.GONE);
                } else {
                    // still pages are left
                    binding.btnNext.setText(getString(R.string.next));
                    binding.btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        };

        private void changeStatusBarColor() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }


}

