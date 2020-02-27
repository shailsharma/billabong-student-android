package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by
 */

public class TTSPrefs {
    private final static String TTS_SHARED_PREFERENCE = "tss_preference"; // Shared Preference file name
    private static SharedPreferences sTTSPrefs;

    static SharedPreferences getSharePreference(Context context) {
        if (sTTSPrefs == null) {
            sTTSPrefs = context.getSharedPreferences(TTS_SHARED_PREFERENCE, 0);
        }
        return sTTSPrefs;
    }


    public static void setFirstTimeRapidLoaded(Context context, boolean isRanFirstTime) {
        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ConstantUtil.RAPID_LOAD_FIRST_TIME, isRanFirstTime);
        editor.apply();


    }

    public static boolean getFirstTimeRapidLoaded(Context context) {

        SharedPreferences preferences = getSharePreference(context);
        if (preferences.contains(ConstantUtil.RAPID_LOAD_FIRST_TIME)) {

            return preferences.getBoolean(ConstantUtil.RAPID_LOAD_FIRST_TIME, false);
        }
        return false;
    }

    public static void saveRapidCardPosition(Context context, int position) {

        SharedPreferences preferences = getSharePreference(context);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(ConstantUtil.RAPID_CARD_POSITION, position);
        editor.apply();

    }

    public static int getRapidCardPosition(Context context) {

        SharedPreferences preferences = getSharePreference(context);
        return preferences.getInt(ConstantUtil.RAPID_CARD_POSITION, 0);
    }


    public static void clearTSSPrefs(Context context) {
        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor spreferencesEditor = preferences.edit();
        if (preferences.contains(ConstantUtil.RAPID_LOAD_FIRST_TIME)) {
            spreferencesEditor.remove(ConstantUtil.RAPID_LOAD_FIRST_TIME); //we are removing prodId by key
            spreferencesEditor.apply();
        }
    }


}
