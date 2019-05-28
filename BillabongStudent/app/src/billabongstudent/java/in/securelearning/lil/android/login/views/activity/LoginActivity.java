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
import android.os.DeadObjectException;
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
import android.text.Html;
import android.text.TextUtils;
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
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.utils.PermissionPrefs;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.home.views.activity.NavigationDrawerActivity;
import in.securelearning.lil.android.home.views.activity.PasswordChangeActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostSharedIntentActivity;
import in.securelearning.lil.android.login.InjectorLogin;
import in.securelearning.lil.android.login.events.AlreadyLoggedInEvent;
import in.securelearning.lil.android.login.events.PasswordChangeEvent;
import in.securelearning.lil.android.login.views.activity.startup.MyViewPagerAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.RolePermissions;
import in.securelearning.lil.android.syncadapter.fcmservices.FCMToken;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.PrefManagerStudentSubjectMapping;
import in.securelearning.lil.android.syncadapter.utils.ShortcutUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.home.views.activity.PasswordChangeActivity.FROM_LOGIN;

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
    DatabaseQueryHelper mDatabaseQueryHelper;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    RxBus mRxBus;
    private String mAction = "";
    private LayoutAppMainLoginBinding mBinding;

    private final static String EMAIL_PHONE_SHARED_PREFERENCE = "email_phone_pref";
    private final static String SET_LOGGED_IN_EMAIL_PHONE = "set_logged_in_email_phone";
    private boolean mIsAlreadyLoggedIn;
    private Disposable mSubscription;

    public static Intent startIntentLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        return intent;
    }

    @Override
    public void onBackPressed() {

//        switch (mAction) {
//            case Intent.ACTION_MAIN:
//                finish();
//                break;
//            case ACTION_LOGOUT:
//                finishAndRemoveTask();
//                break;
//            case ACTION_UNAUTHORIZED:
//                finishAffinity();
//                break;
//            default:
//                finish();
//                break;
//        }
        finish();
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLogin.INSTANCE.getComponent().inject(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        } else {
            getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
        }
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
                getWindow().getDecorView().setBackgroundResource(R.drawable.app_splash);
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
                listenRxEvent();

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

    @SuppressLint("CheckResult")
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermission();
        }
    }


    /*Listen or catch the respective events*/
    private void listenRxEvent() {
        mSubscription = mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(final Object event) throws Exception {
                        if (event instanceof AlreadyLoggedInEvent) {
                            mIsAlreadyLoggedIn = ((AlreadyLoggedInEvent) event).isAlreadyLoggedIn();
                            hideSoftKeyboard();
                            showProgress(true);
                            attemptLogin(mBinding.getRoot(), mIsAlreadyLoggedIn);
                        } else if (event instanceof PasswordChangeEvent) {
                            mBinding.includeLoginEmail.editTextLoginPassword.setText("");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
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


    }

    @SuppressLint("CheckResult")
    private void setDefaults() {
        Completable.complete().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                mBinding.includeLoginEmail.textViewAppVersion.setText(BuildConfig.VERSION_NAME);

                final Intent intent = getIntent();
                if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
                    switch (mAction) {
                        case Intent.ACTION_MAIN:
                        case ACTION_LEARNING_NETWORK:
                        case ACTION_NOTIFICATION:
                        case ACTION_LN_SHARE:
                            Log.e("in", "actionMain setDef");

                            Log.e("in", "actionMain showIntro");

                            break;
                        case ACTION_LOGOUT:
                            unSubscribeFCM();
                            logoutAction();
                            break;
                        case ACTION_UNAUTHORIZED:
                            unSubscribeFCM();
                            unauthorizedAction();
                            break;
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
            PrefManager.setUpdatedToVersion(this, BuildConfig.VERSION_CODE);
            version = 147;
        }


    }

    private void actionVersionLessThan41() {
        try {
            mDatabaseQueryHelper.reIndexViewFromDatabaseNotifications(DatabaseQueryHelper.VIEW_NAME_EVENT_LIST_BY_CREATION_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mSubscription != null) {
//            mSubscription.dispose();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mSubscription != null) {
//            mSubscription.dispose();
//        }
    }

    private void logoutAction() {
        SyncServiceHelper.stopSyncService(LoginActivity.this);
        clearPreferences(LoginActivity.this);
        mAppUserModel.changeToGuestUser();
        // mAppUserModel.setApplicationUser(new UserProfile());
        //clear notifications
        NotificationManagerCompat.from(LoginActivity.this).cancelAll();
    }

    private void unauthorizedAction() {
        SyncServiceHelper.stopSyncService(this);
        clearPreferences(LoginActivity.this);
        mAppUserModel.changeToGuestUser();
        // mAppUserModel.setApplicationUser(new UserProfile());
        //clear notifications
        NotificationManagerCompat.from(this).cancelAll();
    }

    @SuppressLint("ClickableViewAccessibility")
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


        mBinding.includeLoginEmail.layoutLoginEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });


        mBinding.includeLoginEmail.buttonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                attemptLogin(view, mIsAlreadyLoggedIn);

            }
        });


        mBinding.includeLoginEmail.buttonCreateAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });


        mBinding.includeLoginEmail.buttonLoginForgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    startActivity(GeneratePasswordActivity.getStartIntent(getBaseContext()));
                } else {
                    SnackBarUtils.showNoInternetSnackBar(getBaseContext(), v);
                }
            }
        });

    }

    private void signUp() {
        Intent mIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(mIntent);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    private void checkAndRequestPermission() {
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
        }

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

    /**
     * Attempts to sign in or register the account specified by the refreshToken form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual refreshToken attempt is made.
     *
     * @param view
     * @param isAlreadyLoggedIn
     */
    private void attemptLogin(final View view, boolean isAlreadyLoggedIn) {

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


        /*Check for a valid email address and enrollment number.*/
        if (TextUtils.isEmpty(email)) {
            mBinding.includeLoginEmail.inputLayoutLoginEmail.setError(getString(R.string.error_field_enrollment_number));
            focusView = mBinding.includeLoginEmail.editTextLoginEmail;
            cancel = true;
        }


        /*Check for a valid password, if the user entered one.*/
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


            performLogin(view, email, password, isAlreadyLoggedIn);
        }


    }

    /**
     * check all type of validations for email
     *
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        if (email.contains("@") && email.contains(".")) {
            return true;
        } else if (email.length() == 10 && TextUtils.isDigitsOnly(email)) {
            return true;
        }
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
    private void showProgress(final boolean show) {


        runOnUiThread(new Runnable() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void run() {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

                    mBinding.includeLoginEmail.containerLogin.setVisibility(show ? View.GONE : View.VISIBLE);
                    mBinding.includeLoginProgress.layoutLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);

                } else {

                    mBinding.includeLoginProgress.layoutLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                    mBinding.includeLoginEmail.containerLogin.setVisibility(show ? View.GONE : View.VISIBLE);

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

    /*Perform user login from initially*/
    @SuppressLint("CheckResult")
    private void performLogin(final View view, final String email, final String password, final boolean isAlreadyLoggedIn) {
        showProgress(true);

        Observable.just(LoginActivity.this).subscribeOn(Schedulers.io()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws IOException {

                if (GeneralUtils.isNetworkAvailable(LoginActivity.this)) {
                    performOnlineLogin(view, email, password, isAlreadyLoggedIn);
                } else {
                    Snackbar.make(view, getString(R.string.connect_internet), Snackbar.LENGTH_LONG).show();
                    showProgress(false);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                showProgress(false);
            }
        });
    }

    private void actionOfflineLogin() {
        final String userId = AppPrefs.getUserId(this);
        mAppUserModel.changeToUserDatabase(userId);
        copyUserProfileFromGuestDB(userId);
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

    /*Login through server if network is available*/
    @SuppressLint("CheckResult")
    void performOnlineLogin(final View view, final String email, final String password, final boolean isAlreadyLoggedIn) throws IOException {

        stopServicesAndCancelNotifications(LoginActivity.this);

        if (GeneralUtils.isNetworkAvailable(LoginActivity.this)) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AnimationUtils.continueBlinkAnimation(mBinding.includeLoginProgress.textViewLoadingMessage);

                    mBinding.includeLoginProgress.textViewLoadingMessage.setText(getString(R.string.message_login_wait));

                }
            });

            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> subscriber) {
                    try {
                        if (isAlreadyLoggedIn) {
                            setUserNameAndPasswordInPref(email, password);
                            getCurrentUserProfile();
                        } else {
                            if (SyncServiceHelper.login(LoginActivity.this, email, password)) {
                                setUserNameAndPasswordInPref(email, password);
                                getCurrentUserProfile();
                            } else {
                                showProgress(false);
                                shakeAlert();
                            }
                        }


                    } catch (Exception t) {
                        t.printStackTrace();
                        Log.e("LoginActivity", "err refreshToken" + t.toString());
                        Snackbar.make(view, t.getMessage(), Snackbar.LENGTH_LONG).show();
                        showProgress(false);
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
                    showProgress(false);
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                }
            });


        } else {
            Snackbar.make(view, getString(R.string.connect_internet), Snackbar.LENGTH_LONG).show();

            showProgress(false);

        }

    }

    /*Fetch details of logged in user.*/
    private void getCurrentUserProfile() throws Exception {
        if (SyncServiceHelper.setCurrentUserProfile(LoginActivity.this)) {
            String userId = AppPrefs.getUserId(LoginActivity.this);
            final UserProfile userProfile = mAppUserModel.getUserProfileFromUidSyncGuestDB(userId);
            userProfile.setDocId("");
            if (!performLoginSuccessAction(mContext, userId, userProfile)) {
                throw new Exception(getString(R.string.error_loading_profile));
            }

            /*Storing the permissions*/
            boolean isPermissionSet = fetchAndSetRolePermissions(getBaseContext());

            /*Checking if user isLearner to allow him to login into app, else exit the user.*/
            if (isPermissionSet && !PermissionPrefsCommon.isLearner(getBaseContext())) {
                throw new Exception(getString(R.string.error_invalid_user));
            }

            /*To update UserProfile object stored  locally.*/
            SyncServiceHelper.updateProfile();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.includeLoginProgress.textViewLoadingMessage.setText(getString(R.string.message_loading_groups));

                }
            });

            /*Starting download job to fetch and save user's group.*/
            try {
                createDownloadJobForGroups(userProfile);
            } catch (Exception e) {
                throw new SocketTimeoutException(getString(R.string.error_loading_groups));
            }

            Set<String> editTextValues = getEmailOrPhoneFromSharedPref();
            editTextValues.add(mBinding.includeLoginEmail.editTextLoginEmail.getText().toString().trim());
            saveEmailOrPhoneToSharedPref(editTextValues);

            /*Make mIsAlreadyLoggedIn value false because user already logged in.*/
            mIsAlreadyLoggedIn = false;



            /*checking if user has changed password once,
             *if not then navigating the user to change password screen.*/
            if (!userProfile.isResetInitialPassword()) {
                startChangePasswordActivity();
                showProgress(false);
            }

            /*Handling intent for opening post activity*/
            else if (mAction != null && mAction.equals(ACTION_LN_SHARE)) {
                startCreatePostActivity();
            } else {
                /*Handling intent for home activity*/
                startHomeActivity();
                AppPrefs.setLoggedIn(true, LoginActivity.this);
            }
            /*register device FCM token to lil server*/
            FCMToken.sendRegistrationToServer(getBaseContext(), FirebaseInstanceId.getInstance().getToken());
        } else {
            throw new SocketTimeoutException(getString(R.string.error_loading_profile));
        }
    }


    /*Save credentials to preference which are entered in respective fields*/
    private void setUserNameAndPasswordInPref(String email, String password) {
        AppPrefs.setUserName(email, LoginActivity.this);
        AppPrefs.setUserPassword(password, LoginActivity.this);
    }


    private void startChangePasswordActivity() {

        startActivity(PasswordChangeActivity.getStartIntent(getBaseContext(), mAppUserModel.getObjectId(), getString(R.string.messageChangePasswordLogout), getString(R.string.labelChangePassword), FROM_LOGIN));

    }

    private boolean fetchAndSetRolePermissions(Context context) throws IOException {
        Call<RolePermissions> call = mNetworkModel.fetchRolePermissions();
        Response<RolePermissions> response = call.execute();
        if (response.isSuccessful()) {
            RolePermissions permissions = response.body();
            if (permissions != null && permissions.getPermissions() != null && permissions.getPermissions().length > 0 && response.body() != null) {
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
            return new HashSet<String>(sharedPrefs.getStringSet(SET_LOGGED_IN_EMAIL_PHONE, new HashSet<String>()));
        } else {
            return new HashSet<String>();
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
                        throw new SocketTimeoutException(getString(R.string.error_loading_groups));
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

    public static Intent getStartIntent(Context context, String action) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
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

