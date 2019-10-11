package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsLoginResult implements Serializable {

    @SerializedName("data")
    @Expose
    private LogiqidsLoginResponseData mData;

    public LogiqidsLoginResponseData getData() {
        return mData;
    }

    public void setData(LogiqidsLoginResponseData data) {
        mData = data;
    }
}
