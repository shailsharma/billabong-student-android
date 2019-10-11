package in.securelearning.lil.android.thirdparty.utils;

import android.content.Context;
import android.content.SharedPreferences;

import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

/*To save third party client related values to shared preference for any time use*/
public class ThirdPartyPrefs {

    private final static String THIRD_PARTY_PREFS = "third_party_pref";
    private final static String MIND_SPARK_JSON_WEB_TOKEN = "mind_spark_json_web_token";
    private final static String LOGIQIDS_SESSION_TOKEN = "logiqids_session_token";
    private final static String LOGIQIDS_USER_ID = "logiqids_user_id";

    private static SharedPreferences getSharePreference(Context context) {
        SharedPreferences sharedPrefs;
        sharedPrefs = context.getSharedPreferences(THIRD_PARTY_PREFS, 0);
        return sharedPrefs;
    }

    public static String getMindSparkJsonWebToken(Context context) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        return sharedPrefs.getString(MIND_SPARK_JSON_WEB_TOKEN, ConstantUtil.BLANK);
    }

    public static void setMindSparkJsonWebToken(Context context, String jwt) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putString(MIND_SPARK_JSON_WEB_TOKEN, jwt);
        edit.apply();
    }

    public static String getLogiqidsSessionToken(Context context) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        return sharedPrefs.getString(LOGIQIDS_SESSION_TOKEN, ConstantUtil.BLANK);
    }

    public static void setLogiqidsSessionToken(Context context, String token) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putString(LOGIQIDS_SESSION_TOKEN, token);
        edit.apply();
    }

    public static int getLogiqidsUserId(Context context) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        return sharedPrefs.getInt(LOGIQIDS_USER_ID, ConstantUtil.INT_ZERO);
    }

    public static void setLogiqidsUserId(Context context, int userId) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putInt(LOGIQIDS_USER_ID, userId);
        edit.apply();
    }
}
