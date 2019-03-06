package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkQuestionContentData implements Serializable {


    @SerializedName("contentId")
    @Expose
    private String mContentId;

    @SerializedName("contentType")
    @Expose
    private String mContentType;

    @SerializedName("contentSeqNum")
    @Expose
    private int mContentSeqNum;

    @SerializedName("data")
    @Expose
    private ArrayList<MindSparkQuestion> mQuestions;

    public String getContentId() {
        return mContentId;
    }

    public void setContentId(String contentId) {
        mContentId = contentId;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType) {
        mContentType = contentType;
    }

    public int getContentSeqNum() {
        return mContentSeqNum;
    }

    public void setContentSeqNum(int contentSeqNum) {
        mContentSeqNum = contentSeqNum;
    }

    public ArrayList<MindSparkQuestion> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(ArrayList<MindSparkQuestion> questions) {
        mQuestions = questions;
    }
}
