package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkQuestionTrial implements Serializable {

    @SerializedName("userResponse")
    @Expose
    private MindSparkQuestionResponse mUserResponse;

    @SerializedName("timeTaken")
    @Expose
    private int mTimeTaken;

    @SerializedName("result")
    @Expose
    private boolean mResult;

    public MindSparkQuestionResponse getUserResponse() {
        return mUserResponse;
    }

    public void setUserResponses(MindSparkQuestionResponse userResponse) {
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
