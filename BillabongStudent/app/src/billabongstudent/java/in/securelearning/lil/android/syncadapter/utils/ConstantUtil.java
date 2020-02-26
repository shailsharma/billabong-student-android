package in.securelearning.lil.android.syncadapter.utils;

public class ConstantUtil {
    public static final String OVERDUE = "Overdue";
    public static final String PENDING = "Pending";
    public static final String TITLE = "title";
    public static final String BLANK = "";
    public static final String BLANK_SPACE = " ";
    public static final String FORWARD_SLASH = "/";
    public static final String CK_EDITOR_GHOST_CHARACTER = " ";
    public static final String INDIA_COUNTRY_CODE = "+91";


    public static final String HOMEWORK_ID = "homeworkId";
    public static final String SUBMITTED = "Submitted";

    public static final String SUBJECT_ID = "subject_id";
    public static final int PROFILE_SHIELD_COUNT = 5;
    public static final int VIMEO_LOCAL_PORT = 8760;

    //BENCHMARK_TIME is in mins
    public static final String STRING_ZERO = "0";
    public static final int INT_ZERO = 0;
    public static final String BENCHMARK_PERFORMANCE = "benchmark_performance";
    public static final String EXCELLENCE = "excellence";
    public static final String COVERAGE = "coverage";
    public static final String NEW = "New";
    public static final String UPCOMING = "Upcoming";
    public static final String TODAY = "Today";

    public final static String MIME_TYPE_TEXT = "text/plain";
    public final static String MIME_TYPE_IMAGE = "image/";
    public final static String MIME_TYPE_VIDEO = "video/";


    public final static String TYPE_VIDEO = "video";
    public final static String TYPE_IMAGE = "image";

    public final static float TOOLBAR_ELEVATION = 8f;
    public final static float NO_ELEVATION = 0f;

    public final static int TTS_CHECK_CODE = 1;

    public static final String GAMIFICATION_OBJECT = "gamification_object";
    public static final String DASHBOARD_IS_SUBJECT_DONE = "subject_done";
    public static final String DASHBOARD_LOAD_FIRST_TIME = "first_time";
    public static final String GAMIFICATION_EVENT_POSITION = "gamification_event_position";

    /*userStatus call type*/
    public static final String TYPE_LESSON_PLAN = "lessonPlan";
    public static final String TYPE_NETWORK = "network";
    public static final String TYPE_ACTIVE = "active";
    public static final String TYPE_INACTIVE = "inactive";

    /*GroupType*/
    public static final String GROUP_TYPE_NETWORK = "learningNetwork";
    public static final String GROUP_TYPE_TRAINING = "training";
    public static final String GROUP_TYPE_LRPA = "lrpa";
    public static final String GAMIFICATION_PRACTISE = "gamification_practise";
    public static final String GAMIFICATION_SELECTED_ID = "id";
    public static final String TTS_AVAILABLE = "gamification_survey";
    public static final String EFFORT = "effort";
    public static final float BONUS_PERCENTAGE = 20;
    public static final float BONUS_TIME = 5;

    /*Student Goal and Interest*/
    public static final int PROFILE_ACADEMIC_SUBJECT = 2;
    public static final int PROFILE_CO_CURRICULAR_ACTIVITY = 3;
    public static final int PROFILE_HOBBY = 4;

    public static final String CHALLENGE_PER_DAY_LOGIQIDS = "logiqids";
    public static final String VIDEO_PER_DAY = "video";

    public static final String RAPID_LOAD_FIRST_TIME = "first_time";
    public static final String RAPID_CARD_POSITION = "rapid_card_postion";

    public final static int PROFILE_IMAGE_MAX_SIZE_IN_MB = 10;

    public static final String MESSAGE = "message";
    public static final String GAMIFICATION_EVENT = "gamification_event";

    /*LRPA cards*/
    public final static int LRPA_CARD_CORNER_RADIUS = 36;
    public final static int LRPA_CARD_MARGIN = 0;

    /*Constant related to HTML*/
    public static final String HTML_IMAGE_SRC_TAG = "src=";
    public static final String HTML_DOUBLE_QUOTE = "\"";
    public static final String HTML_IMAGE_START_TAG = "<img";
    public static final String HTML_VALUE_TAG = "value=";
    public static final String HTML_END_TAG = "/>";// please do not use this to equalIgnoreCase or equals for string
    public static final String HTML_INPUT_START_TAG_WITH_SPACE = "<input ";
    public static final String HTML_INPUT_START_TAG_WITH_SPACE_REPLACEMENT = "______<input ";// For fill in the blanks
    public static final String HTML_EXTRACT_FIGURE_REGEX = "<figure.+?>";
    public static final String HTML_EXTRACT_FIGCAPTION_REGEX = "<figcaption>.+?>";
    public static final String HTML_EXTRACT_IMG_REGEX = "<img.+?>";
    public static final String HTML_SUPERSCRIPT_START_TAG = "<sup>";
    public static final String HTML_SUBSCRIPT_START_TAG = "<sub>";

    /*Player*/
    public final static int PLAYER_RESPONSE_DURATION = 3000;// in milliseconds
    public final static int CHOICE_IMAGE_MAX_WIDTH = 136;

    public final static int RECAP_SECTION_TEACHING_STATUS_LOCKED = 0;
    public final static int RECAP_SECTION_TEACHING_STATUS_UNLOCKED = 1;

    /*Third Party*/
    public final static int TP_TYPE_GEO_GEBRA = 4;
    public static final int GEO_GEBRA_LOCAL_PORT = 1969;

    public static final String YOUTUBE_SECRET_KEY = "AIzaSyAhC5yk_S8tVKz9w7G5NTAGs1gTjEs087M";

    public static final String TYPE_LEARN = "l";
    public static final String TYPE_REINFORCE = "r";
    public static final String TYPE_PRACTICE = "p";
    public static final String TYPE_APPLY = "a";

    /*Constants for EXO player*/
    //Minimum Video you want to buffer while Playing
    public static final int MIN_BUFFER_DURATION = 5000;
    //Max Video you want to buffer during PlayBack
    public static final int MAX_BUFFER_DURATION = 7000;
    //Min Video you want to buffer before start Playing it
    public static final int MIN_PLAYBACK_START_BUFFER = 1500;
    //Min video You want to buffer when user resumes video
    public static final int MIN_PLAYBACK_RESUME_BUFFER = 5000;


}
