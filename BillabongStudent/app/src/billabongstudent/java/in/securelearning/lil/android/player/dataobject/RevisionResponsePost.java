package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.QuestionResponse;

public class RevisionResponsePost implements Serializable {

    @SerializedName("subjectId")
    @Expose
    private String mRevisionSubjectId;

    @SerializedName("topicId")
    @Expose
    private String mTopicId;

    @SerializedName("totalQuestions")
    @Expose
    private int mTotalQuestions;

    @SerializedName("questionResponses")
    @Expose
    private ArrayList<QuestionResponse> mQuestionResponseList;


    public String getRevisionSubjectId() {
        return mRevisionSubjectId;
    }

    public void setRevisionSubjectId(String revisionSubjectId) {
        mRevisionSubjectId = revisionSubjectId;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }

    public int getTotalQuestions() {
        return mTotalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        mTotalQuestions = totalQuestions;
    }

    public ArrayList<QuestionResponse> getQuestionResponseList() {
        return mQuestionResponseList;
    }

    public void setQuestionResponseList(ArrayList<QuestionResponse> questionResponseList) {
        mQuestionResponseList = questionResponseList;
    }
}
