package in.securelearning.lil.android.home.utils;

import android.content.Context;
import android.content.SharedPreferences;

import in.securelearning.lil.android.syncadapter.dataobject.Permission;
import in.securelearning.lil.android.syncadapter.dataobject.RolePermissions;

/**
 * Created by Prabodh Dhabaria on 16-11-2017.
 */

public class PermissionPrefs {
    private final static String USER_SHARED_PREFRENCE = "permission_settings"; // Shared Preference file name
    private static SharedPreferences sPreferences;

    static SharedPreferences getSharePreference(Context context) {
        if (sPreferences == null) {
            sPreferences = context.getSharedPreferences(USER_SHARED_PREFRENCE, 0);
        }
        return sPreferences;
    }

    public static void setPermissions(RolePermissions permissions, Context context) {
        SharedPreferences preferences = getSharePreference(context);
        SharedPreferences.Editor editor = preferences.edit();
        for (Permission permission :
                permissions.getPermissions()) {
            editor.putBoolean(permission.getModule(), permission.isStatus());
        }

        editor.commit();
    }


    public static void clearPrefs(Context context) {
        SharedPreferences preferences = getSharePreference(context);
        preferences.edit().clear().commit();
    }
}
