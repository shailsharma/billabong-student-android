package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkResult implements Serializable {

    public static final String RESULT_CODE_SUCCESS = "C001";
    public static final String RESULT_CODE_UNAUTHORIZED = "CL029";
    public static final String RESULT_CODE_USER_SYNC_IN_PROGRESS = "CL023";
    public static final String RESULT_CODE_JWT_EXPIRED = "PS025";

    @SerializedName("resultCode")
    @Expose
    private String mResultCode;

    @SerializedName("resultMessage")
    @Expose
    private String mResultMessage;

    public String getResultCode() {
        return mResultCode;
    }

    public void setResultCode(String resultCode) {
        mResultCode = resultCode;
    }

    public String getResultMessage() {
        return mResultMessage;
    }

    public void setResultMessage(String resultMessage) {
        mResultMessage = resultMessage;
    }
}
