package in.securelearning.lil.android.gamification.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

/**
 * Created by Prabodh Dhabaria on 16-11-2017.
 */

public class GamificationPrefs {
    private final static String GAMIFICATION_SHARED_PREFERENCE = "gamification_preference"; // Shared Preference file name
    private static SharedPreferences sGamificationPrefs;

    static SharedPreferences getSharePreference(Context context) {
        if (sGamificationPrefs == null) {
            sGamificationPrefs = context.getSharedPreferences(GAMIFICATION_SHARED_PREFERENCE, 0);
        }
        return sGamificationPrefs;
    }


    public static boolean setRanBefore(Context context) {
        SharedPreferences preferences = getSharePreference(context);
        boolean ranBefore = PermissionPrefsCommon.isRanBefore(context);
        SharedPreferences.Editor editor = preferences.edit();
        if (!ranBefore) {
            // first time
            editor.putBoolean(PermissionPrefsCommon.IS_RAN_BEFORE, true);
            editor.apply();
        }
        return !ranBefore;


    }

    public static void saveGamificationData(Context context, ArrayList<GamificationEvent> eventList) {

        SharedPreferences preferences = getSharePreference(context);

        SharedPreferences.Editor editor = preferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(eventList);
        editor.putString(ConstantUtil.GAMIFICATION_OBJECT, serializedObject);
        editor.apply();

    }

    public static void saveGamificationEventPosition(Context context, int position) {

        SharedPreferences preferences = getSharePreference(context);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(ConstantUtil.GAMIFICATION_EVENT_POSITION, position);
        editor.apply();

    }

    public static void savePractiseObject(Context context, AboutCourseMinimal course) {

        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor editor = preferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(course);
        editor.putString(ConstantUtil.GAMIFICATION_PRACTISE, serializedObject);
        editor.apply();

    }

    public static void isTTSAvailable(Context context, boolean isTTSAvailable) {

        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(ConstantUtil.TTS_AVAILABLE, isTTSAvailable);
        editor.apply();

    }

    public static boolean getTTS(Context context) {

        SharedPreferences preferences = getSharePreference(context);
        if (preferences.contains(ConstantUtil.TTS_AVAILABLE)) {

            return preferences.getBoolean(ConstantUtil.TTS_AVAILABLE, false);
        }
        return false;
    }


    public static void saveSelectedId(Context context, String id) {

        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(ConstantUtil.GAMIFICATION_SELECTED_ID, id);
        editor.apply();

    }

    public static String getSelectedId(Context context) {

        SharedPreferences preferences = getSharePreference(context);
        if (preferences.contains(ConstantUtil.GAMIFICATION_SELECTED_ID)) {

            return preferences.getString(ConstantUtil.GAMIFICATION_SELECTED_ID, "");
        }
        return null;
    }
    public static void clearSelectedId(Context context) {
        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor spreferencesEditor = preferences.edit();
        if (preferences.contains(ConstantUtil.GAMIFICATION_SELECTED_ID)) {
            spreferencesEditor.remove(ConstantUtil.GAMIFICATION_SELECTED_ID); //we are removing prodId by key
            spreferencesEditor.apply();
        }
    }

    public static AboutCourseMinimal getPractiseObject(Context context) {

        SharedPreferences preferences = getSharePreference(context);

        if (preferences.contains(ConstantUtil.GAMIFICATION_PRACTISE)) {
            final Gson gson = new Gson();
            AboutCourseMinimal course;
            String jsonPreferences = preferences.getString(ConstantUtil.GAMIFICATION_PRACTISE, "");
            Type type = new TypeToken<AboutCourseMinimal>() {
            }.getType();
            course = gson.fromJson(jsonPreferences, type);

            return course;
        }
        return null;
    }

    public static void clearPractiseObject(Context context) {
        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor spreferencesEditor = preferences.edit();
        if (preferences.contains(ConstantUtil.GAMIFICATION_PRACTISE)) {
            spreferencesEditor.remove(ConstantUtil.GAMIFICATION_PRACTISE); //we are removing prodId by key
            spreferencesEditor.apply();
        }
    }

    public static int getEventPosition(Context context) {

        SharedPreferences preferences = getSharePreference(context);

        if (preferences.contains(ConstantUtil.GAMIFICATION_EVENT_POSITION)) {

            int position = preferences.getInt(ConstantUtil.GAMIFICATION_EVENT_POSITION, 0);
            return position;
        }
        return 0;
    }

    public static ArrayList<GamificationEvent> getGamificationData(Context context) {

        SharedPreferences preferences = getSharePreference(context);

        if (preferences.contains(ConstantUtil.GAMIFICATION_OBJECT)) {
            final Gson gson = new Gson();
            ArrayList<GamificationEvent> eventList;
            String jsonPreferences = preferences.getString(ConstantUtil.GAMIFICATION_OBJECT, "");
            Type type = new TypeToken<ArrayList<GamificationEvent>>() {
            }.getType();
            eventList = gson.fromJson(jsonPreferences, type);

            return eventList;
        }
        return null;
    }

    public static void clearGamificationPrefs(Context context) {
        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor spreferencesEditor = preferences.edit();
        if (preferences.contains(ConstantUtil.GAMIFICATION_OBJECT)) {
            spreferencesEditor.remove(ConstantUtil.GAMIFICATION_OBJECT); //we are removing prodId by key
            spreferencesEditor.apply();
        }
    }


}
