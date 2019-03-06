package in.securelearning.lil.android.home.utils;

import android.content.Context;
import android.content.SharedPreferences;

/*To save mind spark related values to shared preference for any time use*/
public class MindSparkPrefs {

    private final static String MIND_SPARK_PREFS = "mind_spark_pref";
    private final static String MIND_SPARK_JSON_WEB_TOKEN = "mind_spark_json_web_token";

    private static SharedPreferences getSharePreference(Context context) {
        SharedPreferences sharedPrefs;
        sharedPrefs = context.getSharedPreferences(MIND_SPARK_PREFS, 0);
        return sharedPrefs;
    }

    public static String getMindSparkJsonWebToken(Context context) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        return sharedPrefs.getString(MIND_SPARK_JSON_WEB_TOKEN, "");
    }

    public static void setMindSparkJsonWebToken(Context context, String jwt) {
        SharedPreferences sharedPrefs = getSharePreference(context);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putString(MIND_SPARK_JSON_WEB_TOKEN, jwt);
        edit.apply();
    }
}
