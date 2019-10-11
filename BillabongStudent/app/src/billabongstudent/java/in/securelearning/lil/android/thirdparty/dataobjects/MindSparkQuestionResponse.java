package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class MindSparkQuestionResponse implements Serializable {

    @SerializedName("mcqPattern")
    @Expose
    private MindSparkQuestionTypeMCQ mMindSparkQuestionTypeMCQ;

    private Map<String, MindSparkQuestionTypeBlank> mMindSparkQuestionBlankPattern;

    public MindSparkQuestionTypeMCQ getMindSparkQuestionMCQPattern() {
        return mMindSparkQuestionTypeMCQ;
    }

    public void setMindSparkQuestionMCQPattern(MindSparkQuestionTypeMCQ mindSparkQuestionTypeMCQ) {
        mMindSparkQuestionTypeMCQ = mindSparkQuestionTypeMCQ;
    }

    public Map<String, MindSparkQuestionTypeBlank> getMindSparkQuestionBlankPattern() {
        return mMindSparkQuestionBlankPattern;
    }

    public void setMindSparkQuestionBlankPattern(Map<String, MindSparkQuestionTypeBlank> mindSparkQuestionBlankPattern) {
        mMindSparkQuestionBlankPattern = mindSparkQuestionBlankPattern;
    }
}
