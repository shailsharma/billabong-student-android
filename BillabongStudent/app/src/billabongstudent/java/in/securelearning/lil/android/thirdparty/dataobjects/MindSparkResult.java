package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkResult implements Serializable {

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
