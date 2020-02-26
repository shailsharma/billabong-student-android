package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.utils.GeneralUtils;

import static in.securelearning.lil.android.syncadapter.utils.PrefManager.SubjectExt;

/**
 * Created by Chaitendra on 26-Jul-17.
 */

public class PrefManagerStudentSubjectMapping {
    private static final String USER_SHARED_PREFRENCE = "lil_student_subject_mapping_data";
    private static final String STUDENT_SUBJECT_MAPPING_LIST = "studentSubjectMappingList";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(USER_SHARED_PREFRENCE, 0);
    }


    public static void setSubjectList(java.util.ArrayList<Subject> subjects, Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        String json = GeneralUtils.toGson(subjects);
        editor.putString(STUDENT_SUBJECT_MAPPING_LIST, json);
        editor.commit();
    }

    public static java.util.ArrayList<Subject> getSubjectList(Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        java.util.ArrayList<Subject> subjects = new ArrayList<>();
        if (sharedPrefs.contains(STUDENT_SUBJECT_MAPPING_LIST)) {
            String string = sharedPrefs.getString(STUDENT_SUBJECT_MAPPING_LIST, "");
            if (!TextUtils.isEmpty(string)) {
                Subject[] list = GeneralUtils.fromGson(string, Subject[].class);
                subjects = new ArrayList<>(Arrays.asList(list));
            }
        }
        return subjects;
    }


    public static java.util.ArrayList<SubjectExt> getSubjectListForWhichCoursesAvailable(Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        java.util.ArrayList<SubjectExt> subjects = new ArrayList<>();
        if (sharedPrefs.contains(STUDENT_SUBJECT_MAPPING_LIST)) {
            String string = sharedPrefs.getString(STUDENT_SUBJECT_MAPPING_LIST, "");
            if (!TextUtils.isEmpty(string)) {
                Subject[] list = GeneralUtils.fromGson(string, Subject[].class);
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isCourseAvailable())
                        subjects.add(new SubjectExt(list[i].getId(), list[i].getName(), list[i].getSubjects()));
                }
            }
        }
        return subjects;
    }

    public static java.util.ArrayList<SubjectExt> getSubjectExtList(Context context) {
        SharedPreferences sharedPrefs = getPrefs(context);
        java.util.ArrayList<SubjectExt> subjects = new ArrayList<>();
        if (sharedPrefs.contains(STUDENT_SUBJECT_MAPPING_LIST)) {
            String string = sharedPrefs.getString(STUDENT_SUBJECT_MAPPING_LIST, "");
            if (!TextUtils.isEmpty(string)) {
                Subject[] list = GeneralUtils.fromGson(string, Subject[].class);
                for (int i = 0; i < list.length; i++) {
                    subjects.add(new SubjectExt(list[i].getId(), list[i].getName(), list[i].getSubjects()));
                }
            }
        }
        return subjects;
    }

    public static void clearPrefs(Context context) {
        SharedPreferences preferences = getPrefs(context);
        preferences.edit().clear().commit();
    }

}
