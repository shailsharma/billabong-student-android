package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkQuestionResponse implements Serializable {

    @SerializedName("mcqPattern")
    @Expose
    private MindSparkQuestionMCQPattern mMindSparkQuestionMCQPattern;

    public MindSparkQuestionMCQPattern getMindSparkQuestionMCQPattern() {
        return mMindSparkQuestionMCQPattern;
    }

    public void setMindSparkQuestionMCQPattern(MindSparkQuestionMCQPattern mindSparkQuestionMCQPattern) {
        mMindSparkQuestionMCQPattern = mindSparkQuestionMCQPattern;
    }
}
