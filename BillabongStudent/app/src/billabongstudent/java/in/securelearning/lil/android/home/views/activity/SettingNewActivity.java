package in.securelearning.lil.android.home.views.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityNewSettingBinding;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.provider.SearchSuggestionProvider;
import in.securelearning.lil.android.syncadapter.service.FlavorSyncServiceHelper;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import in.securelearning.lil.android.syncadapter.utils.ShortcutUtil;

import static in.securelearning.lil.android.home.views.activity.PasswordChangeActivity.FROM_OTHER;


public class SettingNewActivity extends AppCompatActivity implements OnClickListener {

    ActivityNewSettingBinding mBinding;

    private String mChosenRingtone;
    private Uri mRingtoneUri;
    @Inject
    AppUserModel mAppUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_setting);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimarySettings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        mBinding.syncQueue.setOnClickListener(this);
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
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SettingNewActivity.class);
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

                if (mBinding.notificationAssignment.isChecked()) {
                    FlavorSyncServiceHelper.startReminderIntentService(this);
                } else {
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

            case R.id.sync_queue:
                intent = new Intent(SettingNewActivity.this, SyncQueueActivity.class);
                startActivity(intent);
                break;

            case R.id.sync_now:
                ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.sync_started));
                SyncServiceHelper.startSyncService(SettingNewActivity.this);
                break;

            case R.id.clear_history:
                clearHistory();
                break;

            case R.id.general_version:
                break;

            case R.id.general_Language:
                final CharSequence colors[] = new CharSequence[]{"English"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Language");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBinding.generalDefaultLanguage.setText(colors[which]);
                        PreferenceSettingUtilClass.setLanguage(mBinding.generalDefaultLanguage.getText().toString(), SettingNewActivity.this);
                    }
                });
                builder.show();
                break;

            case R.id.general_help:
                CustomChromeTabHelper.loadCustomDataUsingColorResource(SettingNewActivity.this, getString(R.string.web_url), R.color.colorLearningNetworkPrimary);
                break;

            case R.id.general_about:
                CustomChromeTabHelper.loadCustomDataUsingColorResource(SettingNewActivity.this, getString(R.string.web_url), R.color.colorLearningNetworkPrimary);
                break;

            case R.id.general_logout:
                logoutFromApp();
                break;

            case R.id.changePassword:
                startActivity(PasswordChangeActivity.getStartIntent(SettingNewActivity.this, mAppUserModel.getObjectId(), getString(R.string.messagePasswordChangeSuccess), getString(R.string.labelChangePassword), FROM_OTHER));

                break;

            case R.id.assignmentShortcut:
                if (PermissionPrefsCommon.getAssignmentCreatePermission(getBaseContext())) {
                    ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idAssignmentShortcut), getString(R.string.assignment), R.drawable.logo_assignment_c, ShortcutUtil.ACTION_SHORTCUT_ASSIGNMENT);
                } else {
                    ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idAssignmentShortcut), getString(R.string.assignment), R.drawable.logo_assignment_c, ShortcutUtil.ACTION_SHORTCUT_ASSIGNMENT);
                }
                break;

            case R.id.learningNetworkShortcut:
                ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idLearningNetworkShortcut), getString(R.string.learning_network), R.drawable.logo_network_c, ShortcutUtil.ACTION_SHORTCUT_LEARNING_NETWORK);
                break;

            case R.id.calendarShortcut:
                ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idCalendarShortcut), getString(R.string.string_calendar), R.drawable.action_calendar_blue, ShortcutUtil.ACTION_SHORTCUT_CALENDAR);

                break;

            case R.id.workspaceShortcut:
                ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idWorkspaceShortcut), getString(R.string.label_workspace), R.drawable.workspace_c, ShortcutUtil.ACTION_SHORTCUT_WORKSPACE);
                break;

            case R.id.trainingShortcut:
                ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idTrainingShortcut), getString(R.string.labelTraining), R.drawable.logo_training_c, ShortcutUtil.ACTION_SHORTCUT_TRAINING);

                break;

            case R.id.learningMapShortcut:
                ShortcutUtil.addShortcut(getBaseContext(), getString(R.string.idLearningMapShortcut), getString(R.string.string_nav_learning_map), R.drawable.logo_learning_map, ShortcutUtil.ACTION_SHORTCUT_LEARNING_MAP);

                break;
        }
    }


    private void clearHistory() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingNewActivity.this);
        builder.setMessage("Are you sure ?")
                .setTitle("Clear History")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SettingNewActivity.this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
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
//                this.chosenRingtone = uri.toString();
                final Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                mBinding.notificationDefaultSound.setText(ringtone.getTitle(this));
                PreferenceSettingUtilClass.setNotifications_soundKey(uri, this);
            } else {
                this.mChosenRingtone = null;
            }
//            mBinding.notificationDefaultSound.setText();
            // Writing data to SharedPreferences

        }
    }

    private void logoutFromApp() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingNewActivity.this);
        builder.setMessage("Are you sure ?")
                .setTitle("Logout")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ShortcutUtil.removeShortcut(getBaseContext());
                        Intent intent = LoginActivity.getLogoutIntent(SettingNewActivity.this);
                        startActivity(intent);
                        finishAffinity();
                        int pendingIntentId = 1234567;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(SettingNewActivity.this, pendingIntentId, intent,
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
