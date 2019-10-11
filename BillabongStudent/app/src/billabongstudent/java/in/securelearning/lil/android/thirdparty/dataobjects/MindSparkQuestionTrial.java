package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class MindSparkQuestionTrial implements Serializable {

    @SerializedName("userResponse")
    @Expose
    private Map<String, Object> mUserResponse;

    @SerializedName("timeTaken")
    @Expose
    private int mTimeTaken;

    @SerializedName("result")
    @Expose
    private boolean mResult;

    public Map<String, Object> getUserResponse() {
        return mUserResponse;
    }

    public void setUserResponses(Map<String, Object> userResponse) {
        mUserResponse = userResponse;
    }

    public int getTimeTaken() {
        return mTimeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        mTimeTaken = timeTaken;
    }

    public boolean isResult() {
        return mResult;
    }

    public void setResult(boolean result) {
        mResult = result;
    }
}
