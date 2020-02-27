package in.securelearning.lil.android.home.views.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSettingsBinding;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.permission.PermissionPrefsCommon;
import in.securelearning.lil.android.syncadapter.permission.PreferenceSettingUtilClass;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.provider.SearchSuggestionProvider;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import in.securelearning.lil.android.syncadapter.utils.ShortcutUtil;

import static in.securelearning.lil.android.home.views.activity.PasswordChangeActivity.FROM_OTHER;


public class SettingActivity extends AppCompatActivity implements OnClickListener {

    LayoutSettingsBinding mBinding;

    private Uri mRingtoneUri;

    @Inject
    AppUserModel mAppUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_settings);

        setUpToolbar();
        checkLoggedInUser();
        showVersion();
        // Reading from SharedPreferences
        getSettingValue();
        setShortcutViewsVisibility();
        mBinding.notificationSoundEnable.setOnClickListener(this);
        mBinding.notificationSound.setOnClickListener(this);
        mBinding.notificationCourses.setOnClickListener(this);
        mBinding.notificationLearning.setOnClickListener(this);
        mBinding.notificationAssignment.setOnClickListener(this);
        mBinding.notificationVibCheck.setOnClickListener(this);
        mBinding.syncMediaCheck.setOnClickListener(this);
        mBinding.syncWifiCheck.setOnClickListener(this);
        mBinding.generalAbout.setOnClickListener(this);
        mBinding.generalLanguage.setOnClickListener(this);
        mBinding.generalHelp.setOnClickListener(this);
        mBinding.generalVersion.setOnClickListener(this);
        mBinding.generalLogout.setOnClickListener(this);
        mBinding.clearHistory.setOnClickListener(this);
        mBinding.syncNow.setOnClickListener(this);
        mBinding.changePassword.setOnClickListener(this);
        mBinding.assignmentShortcut.setOnClickListener(this);
        mBinding.learningNetworkShortcut.setOnClickListener(this);
        mBinding.calendarShortcut.setOnClickListener(this);
        mBinding.workspaceShortcut.setOnClickListener(this);
        mBinding.trainingShortcut.setOnClickListener(this);
        mBinding.learningMapShortcut.setOnClickListener(this);
        mBinding.textViewHelpAndFAQ.setOnClickListener(this);
    }

    private void setUpToolbar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_light);
        CommonUtils.getInstance().setStatusBarIconsDark(SettingActivity.this);

        setTitle(getString(R.string.label_settings));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        return intent;
    }

    private void setShortcutViewsVisibility() {
        if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
            mBinding.workspaceShortcut.setVisibility(View.VISIBLE);
        } else {
            mBinding.workspaceShortcut.setVisibility(View.GONE);
        }

        if (PermissionPrefsCommon.getNavigationLearningMapPermission(getBaseContext())) {
            mBinding.learningMapShortcut.setVisibility(View.VISIBLE);
        } else {
            mBinding.learningMapShortcut.setVisibility(View.GONE);
        }
        //for now not showing Training for School build.
        if (PermissionPrefsCommon.getTrainingJoinPermission(getBaseContext())) {
            mBinding.trainingShortcut.setVisibility(View.GONE);
        } else {
            mBinding.trainingShortcut.setVisibility(View.GONE);
        }
    }

    private void checkLoggedInUser() {
        if (PermissionPrefsCommon.getClassDetailTeacherViewPermission(this)) {
            mBinding.notificationAssignment.setVisibility(View.GONE);
            mBinding.viewAboveAssignment.setVisibility(View.GONE);
            mBinding.notificationAssignment.setChecked(false);
        }
    }

    private void getSettingValue() {
        boolean sound_enable = PreferenceSettingUtilClass.isNotificationSound(this);
        mBinding.notificationSoundEnable.setChecked(sound_enable);

        mRingtoneUri = PreferenceSettingUtilClass.getNotifications_soundKey(this);
        if (mRingtoneUri == null || TextUtils.isEmpty(mRingtoneUri.toString())) {
            mRingtoneUri = Settings.System.DEFAULT_NOTIFICATION_URI;
        }
        final Ringtone ringtone = RingtoneManager.getRingtone(this, mRingtoneUri);
        if (ringtone == null || TextUtils.isEmpty(ringtone.getTitle(this))) {
            mBinding.notificationDefaultSound.setText("None");
        } else {
            mBinding.notificationDefaultSound.setText(ringtone.getTitle(this));
        }

        boolean course = PreferenceSettingUtilClass.isCourses(this);
        mBinding.notificationCourses.setChecked(course);

        boolean learning = PreferenceSettingUtilClass.isLearningNetwork(this);
        mBinding.notificationLearning.setChecked(learning);

        boolean assignment = PreferenceSettingUtilClass.isAssignment(this);
        mBinding.notificationAssignment.setChecked(assignment);

        boolean wifi = PreferenceSettingUtilClass.isDownloadOnWiFi(this);
        mBinding.syncWifiCheck.setChecked(wifi);

        boolean learn_wifi = PreferenceSettingUtilClass.isMediaAutoDownload(this);
        mBinding.syncMediaCheck.setChecked(learn_wifi);

        String lang_value = PreferenceSettingUtilClass.getLanguage(this);
        mBinding.generalDefaultLanguage.setText(lang_value);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        Intent intent;
        switch (resId) {

            case R.id.notification_sound_enable:
                PreferenceSettingUtilClass.setNotificationSound(mBinding.notificationSoundEnable.isChecked(), this);
                break;

            case R.id.notification_sound:
                intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Sound");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mRingtoneUri);
                this.startActivityForResult(intent, 5);
                break;

            case R.id.notification_Courses:
                PreferenceSettingUtilClass.setCourses(mBinding.notificationCourses.isChecked(), this);
                break;

            case R.id.notification_learning:
                PreferenceSettingUtilClass.setLearningNetwork(mBinding.notificationLearning.isChecked(), this);
                break;

            case R.id.notification_assignment:
                PreferenceSettingUtilClass.setAssignment(mBinding.notificationAssignment.isChecked(), this);

                if (!mBinding.notificationAssignment.isChecked()) {
                    NotificationManager nMgr = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancel(NotificationUtil.REMINDER_ASSIGNMENT_PENDING);
                }

                break;

            case R.id.notification_vib_check:
                break;

            case R.id.sync_wifi_check:
                PreferenceSettingUtilClass.setDownloadOnwifi(mBinding.syncWifiCheck.isChecked(), this);
                break;

            case R.id.sync_media_check:
                PreferenceSettingUtilClass.setMediaAutoDownload(mBinding.syncMediaCheck.isChecked(), this);
                break;

            case R.id.sync_now:
                ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.sync_started));
                SyncServiceHelper.startSyncService(SettingActivity.this);
                break;

            case R.id.clear_history:
                clearHistory();
                break;

            case R.id.textViewHelpAndFAQ:
                startActivity(HelpAndFAQActivity.getStartIntent(getBaseContext()));

            case R.id.general_version:
                break;

            case R.id.general_Language:
                final CharSequence[] colors = new CharSequence[]{"English"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Language");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBinding.generalDefaultLanguage.setText(colors[which]);
                        PreferenceSettingUtilClass.setLanguage(mBinding.generalDefaultLanguage.getText().toString(), SettingActivity.this);
                    }
                });
                builder.show();
                break;

            case R.id.general_help:
                CustomChromeTabHelper.loadCustomDataUsingColorResource(SettingActivity.this, getString(R.string.web_url), R.color.colorLearningNetworkPrimary);
                break;

            case R.id.general_about:
                CustomChromeTabHelper.loadCustomDataUsingColorResource(SettingActivity.this, getString(R.string.web_url), R.color.colorLearningNetworkPrimary);
                break;

            case R.id.general_logout:
                logoutFromApp();
                break;

            case R.id.changePassword:
                startActivity(PasswordChangeActivity.getStartIntent(SettingActivity.this, mAppUserModel.getObjectId(), getString(R.string.messagePasswordChangeSuccess), getString(R.string.labelChangePassword), FROM_OTHER));

                break;

            case R.id.assignmentShortcut:
                ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idAssignmentShortcut), getString(R.string.homework), R.drawable.logo_homework_c, ShortcutUtil.ACTION_SHORTCUT_ASSIGNMENT);

                break;

            case R.id.learningNetworkShortcut:
                ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idLearningNetworkShortcut), getString(R.string.title_network), R.drawable.logo_network_c, ShortcutUtil.ACTION_SHORTCUT_LEARNING_NETWORK);
                break;

        }
    }


    private void clearHistory() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingActivity.this);
        builder.setMessage("Are you sure ?")
                .setTitle("Clear History")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SettingActivity.this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                        suggestions.clearHistory();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showVersion() {
        mBinding.generalDefaultVersion.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                final Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                mBinding.notificationDefaultSound.setText(ringtone.getTitle(this));
                PreferenceSettingUtilClass.setNotifications_soundKey(uri, this);
            } else {
                String chosenRingtone = null;
            }
            // Writing data to SharedPreferences

        }
    }

    private void logoutFromApp() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingActivity.this);
        builder.setMessage("Are you sure ?")
                .setTitle("Logout")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ShortcutUtil.removeShortcut(getBaseContext());
                        Intent intent = LoginActivity.getLogoutIntent(SettingActivity.this);
                        startActivity(intent);
                        finishAffinity();
                        int pendingIntentId = 1234567;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(SettingActivity.this, pendingIntentId, intent,
                                PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                })
                .setCancelable(false);
        final android.app.AlertDialog alert = builder.create();
        alert.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.menu_logout) {
            logoutFromApp(SettingActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}
