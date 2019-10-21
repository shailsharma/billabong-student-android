package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MindSparkQuestion extends MindSparkResult implements Serializable {

    @SerializedName("questionBody")
    @Expose
    private String mQuestionTextBody;

    @SerializedName("response")
    @Expose
    private Map<String, Object> mQuestionResponse;

    @SerializedName("explanation")
    @Expose
    private String mQuestionExplanation;

    @SerializedName("revisionNo")
    @Expose
    private String mRevisionNo;

    @SerializedName("hints")
    @Expose
    private ArrayList<String> mQuestionHints;

    public String getQuestionTextBody() {
        return mQuestionTextBody;
    }

    public void setQuestionTextBody(String questionTextBody) {
        mQuestionTextBody = questionTextBody;
    }

    public Map<String, Object> getQuestionResponse() {
        return mQuestionResponse;
    }

    public void setQuestionResponse(Map<String, Object> questionResponse) {
        mQuestionResponse = questionResponse;
    }

    public String getQuestionExplanation() {
        return mQuestionExplanation;
    }

    public void setQuestionExplanation(String questionExplanation) {
        mQuestionExplanation = questionExplanation;
    }

    public String getRevisionNo() {
        return mRevisionNo;
    }

    public void setRevisionNo(String revisionNo) {
        mRevisionNo = revisionNo;
    }

    public ArrayList<String> getQuestionHints() {
        return mQuestionHints;
    }

    public void setQuestionHints(ArrayList<String> questionHints) {
        mQuestionHints = questionHints;
    }
}
