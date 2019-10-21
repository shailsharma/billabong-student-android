package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsQuestionResult implements Serializable {

    @SerializedName("data")
    @Expose
    private LogiqidsQuestionData mData;

    public LogiqidsQuestionData getData() {
        return mData;
    }

    public void setData(LogiqidsQuestionData data) {
        mData = data;
    }
}
