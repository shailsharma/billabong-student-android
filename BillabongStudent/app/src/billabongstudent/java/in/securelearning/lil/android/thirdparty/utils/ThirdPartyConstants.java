package in.securelearning.lil.android.thirdparty.utils;

import com.couchbase.lite.util.Base64;

import java.nio.charset.StandardCharsets;

public class ThirdPartyConstants {
    public static final String MS_RESULT_CODE_SUCCESS = "C001";
    public static final String MS_RESULT_CODE_FAILURE = "C002";
    public static final String MS_RESULT_CODE_UNAUTHORIZED = "CL029";
    public static final String MS_RESULT_CODE_USER_SYNC_IN_PROGRESS = "CL023";
    public static final String MS_RESULT_CODE_JWT_EXPIRED = "PS025";
    public static final String ACTION_START = "start";
    public static final String ACTION_CONTINUE = "continue";
    public static final String MODE_LEARN = "learn";
    public static final String MS_VENDOR = "euro";
    public static final String MS_LANGUAGE_CODE = "en-IN";
    public static final String MS_VERSION_ID_SUFFIX = "_en-IN_1";
    public static final String TYPE_BLANK = "Blank";
    public static final String TYPE_MCQ = "MCQ";
    public static final String TYPE_DROPDOWN = "Dropdown";

    public static final int TYPE_WORKSHEET_STATUS_NEW = 0; /*status_id=0, user can start a new worksheet*/
    public static final int TYPE_WORKSHEET_STATUS_OPEN = 1; /*status_id=1, user has an open worksheet*/

    public static String decodeBase64String(String encodedString) {
        byte[] valueDecoded;
        valueDecoded = Base64.decode(encodedString.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }
}
