package in.securelearning.lil.android.syncadapter.permission;

import android.content.Context;

/**
 * Created by Prabodh Dhabaria on 16-11-2017.
 */

public class PermissionPrefsCommon extends PermissionPrefs {

    private final static String NETWORK_VIEW_PERMISSION = "network.view";
    private final static String COURSES_VIEW_PERMISSION = "courses.view";
    private final static String RESOURCES_VIEW_PERMISSION = "resources.view";

    private final static String LEARNING_MAP_VIEW_PERMISSION = "learning.map.view";

    private final static String CLASS_DETAIL_TEACHER_VIEW_PERMISSION = "class.detail.teacher.view";
    private final static String CLASS_DETAIL_STUDENT_VIEW_PERMISSION = "class.detail.student.view";

    private final static String DASHBOARD_CLASS_VIEW_PERMISSION = "dashboard.class.view";
    private final static String DASHBOARD_TEACHER_VIEW_PERMISSION = "dashboard.teacher.view";
    private final static String DASHBOARD_STUDENT_VIEW_PERMISSION = "dashboard.student.view";

    private final static String CALENDAR_VIEW_PERMISSION = "calendar.view";

    private final static String CALENDAR_ANNOUNCEMENT_CREATE_PERMISSION = "calendar.announcement.create";
    private final static String CALENDAR_ANNOUNCEMENT_VIEW_PERMISSION = "calendar.announcement.view";
    private final static String CALENDAR_ANNOUNCEMENT_EDIT_PERMISSION = "calendar.announcement.edit";

    private final static String CALENDAR_ACTIVITY_CREATE_PERMISSION = "calendar.activity.create";
    private final static String CALENDAR_ACTIVITY_VIEW_PERMISSION = "calendar.activity.view";
    private final static String CALENDAR_ACTIVITY_EDIT_PERMISSION = "calendar.activity.edit";

    private final static String CALENDAR_PERSONAL_CREATE_PERMISSION = "calendar.personal.create";
    private final static String CALENDAR_PERSONAL_VIEW_PERMISSION = "calendar.personal.view";
    private final static String CALENDAR_PERSONAL_EDIT_PERMISSION = "calendar.personal.edit";

    private final static String ASSIGNMENT_CREATE_PERMISSION = "assignment.create";
    private final static String ASSIGNMENT_VIEW_CREATOR_MODE_PERMISSION = "assignment.creator.mode.view";
    private final static String ASSIGNMENT_VIEW_SUBMISSION_MODE_PERMISSION = "assignment.submission.mode.view";
    private final static String ASSIGNMENT_SUBMISSION_PERMISSION = "assignment.submission";
    private final static String ASSIGNMENT_EDIT_PERMISSION = "assignment.edit";

    private final static String POST_CREATE_REFERENCE_PERMISSION = "post.reference.create";
    private final static String POST_CREATE_DISCUSSION_PERMISSION = "post.discussion.create";
    private final static String POST_BADGE_ASSIGN_PERMISSION = "post.badge.assign";

    private final static String TRAINING_JOIN = "training.join";
    private final static String NAVIGATION_LEARNING_MAP = "navigation.learning.map";

    private final static String IS_LEARNER = "configuration.institute.learner";
    public static final String IS_RAN_BEFORE = "is_ran_before";


    public static boolean getAssignmentCreatePermission(Context context) {
        return getSharePreference(context).getBoolean(ASSIGNMENT_CREATE_PERMISSION, false);
    }

    public static boolean getPostBadgeAssignPermission(Context context) {
        return getSharePreference(context).getBoolean(POST_BADGE_ASSIGN_PERMISSION, false);
    }

    public static boolean getPostCreateReferencePermission(Context context) {
        return getSharePreference(context).getBoolean(POST_CREATE_REFERENCE_PERMISSION, false);
    }

    public static boolean getPostCreateDiscussionPermission(Context context) {
        return getSharePreference(context).getBoolean(POST_CREATE_DISCUSSION_PERMISSION, false);
    }

    public static boolean getDashboardTeacherViewPermission(Context context) {
        return getSharePreference(context).getBoolean(DASHBOARD_TEACHER_VIEW_PERMISSION, false);
    }

    public static boolean getClassDetailTeacherViewPermission(Context context) {
        return getSharePreference(context).getBoolean(CLASS_DETAIL_TEACHER_VIEW_PERMISSION, false);
    }

    public static boolean getTrainingJoinPermission(Context context) {
        return getSharePreference(context).getBoolean(TRAINING_JOIN, false);
    }

    public static boolean getNavigationLearningMapPermission(Context context) {
        return getSharePreference(context).getBoolean(NAVIGATION_LEARNING_MAP, false);
    }

    public static boolean isLearner(Context context) {
        return getSharePreference(context).getBoolean(IS_LEARNER, false);
    }

}
