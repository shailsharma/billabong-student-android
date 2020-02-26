package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionFeedback implements Serializable {


    @SerializedName("questionId")
    @Expose
    private String mQuestionId;

    @SerializedName("issueAppearIn")
    @Expose
    private String mIssueAppearIn;

    @SerializedName("comment")
    @Expose
    private String mComment;


    public void setQuestionId(String questionId) {
        mQuestionId = questionId;
    }

    public void setIssueAppearIn(String issueAppearIn) {
        mIssueAppearIn = issueAppearIn;
    }

    public String getIssueAppearIn() {
        return mIssueAppearIn;
    }

    public void setComment(String comment) {
        mComment = comment;
    }
}
