package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsQuestionAttemptResult implements Serializable {

    @SerializedName("data")
    @Expose
    private LogiqidsQuestionAttemptData mData;

    public LogiqidsQuestionAttemptData getData() {
        return mData;
    }

    public void setData(LogiqidsQuestionAttemptData data) {
        mData = data;
    }
}
