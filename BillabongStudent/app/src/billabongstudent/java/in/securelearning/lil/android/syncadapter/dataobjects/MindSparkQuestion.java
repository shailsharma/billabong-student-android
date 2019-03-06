package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkQuestion extends MindSparkResult implements Serializable {

    @SerializedName("questionBody")
    @Expose
    private String mQuestionTextBody;

    @SerializedName("response")
    @Expose
    private MindSparkQuestionResponse mQuestionResponse;

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

    public MindSparkQuestionResponse getQuestionResponse() {
        return mQuestionResponse;
    }

    public void setQuestionResponse(MindSparkQuestionResponse questionResponse) {
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
