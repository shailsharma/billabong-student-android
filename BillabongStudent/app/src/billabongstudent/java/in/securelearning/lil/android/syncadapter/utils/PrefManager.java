package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.SubjectSuper;
import in.securelearning.lil.android.base.utils.GeneralUtils;

/**
 * Created by Prabodh Dhabaria on 20-06-2017.
 */

public class PrefManager {
    private static final int DEFAULT_SUBJECT_COLOR = 0xffe1000f;
    private static final String USER_SHARED_PREFRENCE = "lil_user_data";
    private static final String SUBJECT_LIST = "subjectList";
    private static final String GRADE_LIST = "gradeList";
    private static final String PERIODIC_EVENT_LAST_SYNC_TIME = "periodicEventLastSyncTime";
    private static final String LAST_LOGIN_TIME = "lastLoginTime";
    private static final String SHOULD_SYNC_PERIODIC_EVENT = "shouldSyncPeriodicEvent";
    private static final String NOTIFICATIONS_LAST_BROADCAST_TIME = "notifications_last_broadcast_time";
    private static final String LAST_SYNC_TIME = "last_sync_time";
    private static final String SHOULD_DOWNLOAD_NETWORK_GROUP = "shouldDownloadNetworkGroup";
    private static final String CURRENT_DATE_LAST_SAVED = "currentDateLastSaved";

    public static final long MINIMUM_SYNC_DELAY = 180000L;

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String UPDATED_TO_VERSION = "updatedToVersion";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(USER_SHARED_PREFRENCE, 0);
    }

    public static void setLastSyncTime(long time, Context con) {
        SharedPreferences sharedPrefs = getPrefs(con);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putLong(LAST_SYNC_TIME, time);
        edit.commit();
    }

    public static void setUpdatedToVersion(Context context, int version) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(UPDATED_TO_VERSION, version);
        editor.commit();
    }

    public static int getUpdatedToVersion(Context context) {
        SharedPreferences pref = getPrefs(context);
        return pref.getInt(UPDATED_TO_VERSION, 1);
    }

    public static java.util.ArrayList<SubjectExt> getSubjectList(Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        java.util.ArrayList<SubjectExt> subjects = new ArrayList<>();
        if (sharedPrefs.contains(SUBJECT_LIST)) {
            String string = sharedPrefs.getString(SUBJECT_LIST, "");
            if (!TextUtils.isEmpty(string)) {
                SubjectExt[] list = GeneralUtils.fromGson(string, SubjectExt[].class);
                subjects = new ArrayList<>(Arrays.asList(list));
            }
        }
        return subjects;
    }

    public static int getColorForSubject(Context context, String subjectId) {
        int color = DEFAULT_SUBJECT_COLOR;
        ArrayList<PrefManager.SubjectExt> mCategories = getSubjectList(context);
        for (int i = 0; i < mCategories.size(); i++) {
            PrefManager.SubjectExt category = mCategories.get(i);
            if (category.getId().equalsIgnoreCase(subjectId)) {
                color = category.getTextColor();
                break;
            }
        }
        return color;
    }

    public static void setSubjectList(java.util.ArrayList<SubjectExt> subjects, Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        String json = GeneralUtils.toGson(subjects);
        editor.putString(SUBJECT_LIST, json);
        editor.commit();
    }

    public static void setGradeList(Collection<Grade> grades, Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        String json = GeneralUtils.toGson(grades);
        editor.putString(GRADE_LIST, json);
        editor.commit();
    }

    public static void setPeriodicEventLastSyncTime(String time, Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PERIODIC_EVENT_LAST_SYNC_TIME, time);
        editor.commit();
    }

    public static void clearPrefs(Context context) {
        SharedPreferences preferences = getPrefs(context);
        preferences.edit().clear().commit();
    }

    public static void setShouldSyncPeriodicEvents(boolean should, Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putBoolean(SHOULD_SYNC_PERIODIC_EVENT, should);
        editor.commit();
    }

    public static HashMap<String, SubjectExt> getSubjectMap(Context context) {
        HashMap<String, SubjectExt> map = new HashMap<>();
        ArrayList<SubjectExt> list = PrefManager.getSubjectList(context);
        for (int i = 0; i < list.size(); i++) {
            SubjectExt subjectExt = list.get(i);
            map.put(subjectExt.getId(), subjectExt);
        }

        return map;
    }

    public static void setShouldDownloadNetworkGroup(boolean should, Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(SHOULD_DOWNLOAD_NETWORK_GROUP, should);
        editor.commit();
    }

    public static SubjectExt getDefaultSubject() {
        return new PrefManager.SubjectExt("", "", DEFAULT_SUBJECT_COLOR, DEFAULT_SUBJECT_COLOR, DEFAULT_SUBJECT_COLOR, R.drawable.white_default_course, R.drawable.transparent_default_course);
    }

    public static class SubjectExt extends SubjectSuper {
        private int mColor = 0xFF000000;
        private int mTextColor = 0xFF000000;
        private int mForegroundColor = 0xFF000000;
        private int mIconWhiteId = 0;
        private int mIconTransparentId = 0;

        public int getIconWhiteId() {
            return mIconWhiteId;
        }

        public void setIconWhiteId(int iconWhiteId) {
            mIconWhiteId = iconWhiteId;
        }

        public int getIconTransparentId() {
            return mIconTransparentId;
        }

        public void setIconTransparentId(int iconTransparentId) {
            mIconTransparentId = iconTransparentId;
        }

        public SubjectExt() {
        }

        public SubjectExt(String id, String name) {
            super(id, name);
        }

        public SubjectExt(String id, String name, ArrayList<SubjectSuper> subjects) {
            super(id, name);
            setSubjects(subjects);
        }

        public int getTextColor() {
            return mTextColor;
        }

        public void setTextColor(int textColor) {
            mTextColor = textColor;
        }

        public SubjectExt(String name, String id, int color, int textColor, int foreGroundColor) {
            super(id, name);
            this.mColor = color;
            this.mTextColor = textColor;
            this.mForegroundColor = foreGroundColor;
        }

        public SubjectExt(String name, String id, int color, int textColor, int foreGroundColor, int iconWhite, int iconTransparent) {
            super(id, name);
            this.mColor = color;
            this.mTextColor = textColor;
            this.mForegroundColor = foreGroundColor;
            this.mIconWhiteId = iconWhite;
            this.mIconTransparentId = iconTransparent;
        }

        public int getForegroundColor() {
            return mForegroundColor;
        }

        public void setForegroundColor(int foregroundColor) {
            this.mForegroundColor = foregroundColor;
        }

        public int getColor() {
            return mColor;
        }

        public void setColor(int color) {
            mColor = color;
        }
    }

    /*Current date last saved for video for the day*/
    public static void setCurrentDateLastSaved(String time, Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(CURRENT_DATE_LAST_SAVED, time);
        editor.commit();
    }

    public static String getCurrentDateLastSaved(Context context) {
        SharedPreferences pref = getPrefs(context);
        return pref.getString(CURRENT_DATE_LAST_SAVED, "");
    }

}
