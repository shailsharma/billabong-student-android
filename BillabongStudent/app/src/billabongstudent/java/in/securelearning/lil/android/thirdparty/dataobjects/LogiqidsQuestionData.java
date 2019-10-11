package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsQuestionData implements Serializable {

    @SerializedName("worksheet_id")
    @Expose
    private String mWorksheetId;

    @SerializedName("topic_name")
    @Expose
    private String mTopicName;

    @SerializedName("total_questions")
    @Expose
    private int mTotalQuestion;

    @SerializedName("questions")
    @Expose
    private LogiqidsQuestionParent mLogiqidsQuestionParent;

    public String getWorksheetId() {
        return mWorksheetId;
    }

    public void setWorksheetId(String worksheetId) {
        mWorksheetId = worksheetId;
    }

    public String getTopicName() {
        return mTopicName;
    }

    public void setTopicName(String topicName) {
        mTopicName = topicName;
    }

    public int getTotalQuestion() {
        return mTotalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        mTotalQuestion = totalQuestion;
    }

    public LogiqidsQuestionParent getLogiqidsQuestionParent() {
        return mLogiqidsQuestionParent;
    }

    public void setLogiqidsQuestionParent(LogiqidsQuestionParent logiqidsQuestionParent) {
        mLogiqidsQuestionParent = logiqidsQuestionParent;
    }
}
