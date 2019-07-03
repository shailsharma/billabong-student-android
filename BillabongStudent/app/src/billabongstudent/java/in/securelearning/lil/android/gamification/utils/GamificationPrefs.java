package in.securelearning.lil.android.gamification.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.syncadapter.dataobject.Permission;
import in.securelearning.lil.android.syncadapter.dataobject.RolePermissions;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

/**
 * Created by Prabodh Dhabaria on 16-11-2017.
 */

public class GamificationPrefs {
    private static SharedPreferences sGamificationPrefs;
    private final static String GAMIFICATION_SHARED_PREFERENCE = "gamification_preference"; // Shared Preference file name

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
    public static void saveGamificationData(Context context, ArrayList<GamificationEvent> eventList)
    {

        SharedPreferences preferences = getSharePreference(context);

        SharedPreferences.Editor editor = preferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(eventList);
        editor.putString(ConstantUtil.GAMIFICATION_OBJECT, serializedObject);
        editor.apply();

    }

    public static ArrayList<GamificationEvent> getGamificationData(Context context)
    {

        SharedPreferences preferences = getSharePreference(context);

        if (preferences.contains(ConstantUtil.GAMIFICATION_OBJECT)) {
            final Gson gson = new Gson();
            ArrayList<GamificationEvent> eventList;
            String jsonPreferences = preferences.getString(ConstantUtil.GAMIFICATION_OBJECT, "");
            Type type = new TypeToken<ArrayList<GamificationEvent>>() {}.getType();
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
