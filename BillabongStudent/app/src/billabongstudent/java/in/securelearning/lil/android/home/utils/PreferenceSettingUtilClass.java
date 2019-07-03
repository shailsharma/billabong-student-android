package in.securelearning.lil.android.home.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import in.securelearning.lil.android.base.utils.GeneralUtils;

/**
 * Created by Secure on 06-06-2017.
 */

public class PreferenceSettingUtilClass {

    private static SharedPreferences getSharePreference(Context context) {
        SharedPreferences sharedPrefs;
        sharedPrefs = context.getSharedPreferences(USER_SHARED_PREFRENCE, 0);
        return sharedPrefs;
    }

    private final static String USER_SHARED_PREFRENCE = "lil_user_settings"; // Shared Preference file name
    // Key Set up
    private final static String DOWNLOAD_ONWIFI_KEY = "notifications_wifi";
    private final static String SET_ASSIGNMENT_REMINDER_ALARM = "set_assignment_reminder_alarm";
    private final static String MEDIA_AUTO_DOWNLOAD_KEY = "notifications_wifi_on_lnetwork";
    private final static String NOTIFICATIONS_LANGUAGE_KEY = "notifications_language";
    private final static String NOTIFICATIONS_HELP_KEY = "notifications_help";
    private final static String NOTIFICATIONS_ABOUT_KEY = "notifications_about";
    private final static String NOTIFICATIONS_SOUND_KEY = "notifications_sound";
    private final static String NOTIFICATIONS_RINGTONE_KEY = "notifications_ringtone";
    private final static String NOTIFICATIONS_VIBRATE_KEY = "notifications_vibrate";
    private final static String NOTIFICATIONS_COURSE_KEY = "notifications_course";
    private final static String NOTIFICATIONS_LNETWORK_KEY = "notifications_lnetwork";
    private final static String NOTIFICATIONS_ASSIGNMENT_KEY = "notifications_assignment";
    private final static String NOTIFICATIONS_BUS_ARRIVING_KEY = "notifications_bus_arriving";
    private final static String NOTIFICATIONS_BUS_HAS_ARRIVING_KEY = "notifications_bus_has_arriving";
    private final static String HAS_FETCHED_DASHBOARD_DATA = "has_fetched_dashboard_data";


    public static boolean isDownloadOnWiFi(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getBoolean(DOWNLOAD_ONWIFI_KEY, false);
    }

    public static boolean isAssignmentReminderAlarm(Context context) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        return sharedPrefs.getBoolean(SET_ASSIGNMENT_REMINDER_ALARM, false);
    }

    public static void setAssignmentReminderAlarm(Context context, boolean b) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putBoolean(SET_ASSIGNMENT_REMINDER_ALARM, b);
        edit.commit();
    }

    public static Uri getNotifications_soundKey(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return Uri.parse(sharedPrefs.getString(NOTIFICATIONS_RINGTONE_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString()));
    }

    public static void setNotifications_soundKey(Uri soundKey, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putString(NOTIFICATIONS_RINGTONE_KEY, soundKey.toString());
        edit.commit();
    }

    public static void setDownloadOnwifi(boolean downloadOnwifi, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putBoolean(DOWNLOAD_ONWIFI_KEY, downloadOnwifi);
        edit.commit();
    }

    public static boolean isMediaAutoDownload(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getBoolean(MEDIA_AUTO_DOWNLOAD_KEY, true);
    }

    public static void setMediaAutoDownload(boolean mediaAutoDownload, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putBoolean(MEDIA_AUTO_DOWNLOAD_KEY, mediaAutoDownload);
        edit.commit();
    }

    public static boolean isNotificationSound(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getBoolean(NOTIFICATIONS_SOUND_KEY, true);
    }

    public static void setNotificationSound(boolean notificationSound, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putBoolean(NOTIFICATIONS_SOUND_KEY, notificationSound);
        edit.commit();
    }

    public static boolean isCourses(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getBoolean(NOTIFICATIONS_COURSE_KEY, true);
    }

    public static void setCourses(boolean courses, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putBoolean(NOTIFICATIONS_COURSE_KEY, courses);
        edit.commit();
    }

    public static boolean isLearningNetwork(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getBoolean(NOTIFICATIONS_LNETWORK_KEY, true);
    }

    public static void setLearningNetwork(boolean learningNetwork, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putBoolean(NOTIFICATIONS_LNETWORK_KEY, learningNetwork);
        edit.commit();
    }

    public static boolean isAssignment(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getBoolean(NOTIFICATIONS_ASSIGNMENT_KEY, true);
    }

    public static void setAssignment(boolean assignment, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putBoolean(NOTIFICATIONS_ASSIGNMENT_KEY, assignment);
        edit.commit();
    }

    public static String getLanguage(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getString(NOTIFICATIONS_LANGUAGE_KEY, "English");
    }

    public static void setLanguage(String language, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putString(NOTIFICATIONS_LANGUAGE_KEY, language);
        edit.commit();
    }

    public static int getBus_arriving(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getInt(NOTIFICATIONS_BUS_ARRIVING_KEY, 500);
    }

    public static void setBus_arriving(int bus_arriving, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putInt(NOTIFICATIONS_BUS_ARRIVING_KEY, bus_arriving);
        edit.commit();
    }

    public static int getBus_has_arrived(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getInt(NOTIFICATIONS_BUS_HAS_ARRIVING_KEY, 50);
    }

    public static void setBus_has_arrived(int bus_has_arrived, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putInt(NOTIFICATIONS_BUS_HAS_ARRIVING_KEY, bus_has_arrived);
        edit.commit();
    }

    public static void setDashboardDataFetch(boolean isDataFetched, Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putBoolean(HAS_FETCHED_DASHBOARD_DATA, isDataFetched);
        edit.commit();
    }

    public static boolean isDashboardDataFetch(Context con) {
        SharedPreferences sharedPrefs = getSharePreference(con);
        return sharedPrefs.getBoolean(HAS_FETCHED_DASHBOARD_DATA, false);
    }

    public static void clearPrefs(Context context) {
        SharedPreferences preferences = getSharePreference(context);
        preferences.edit().clear().commit();
    }

}
