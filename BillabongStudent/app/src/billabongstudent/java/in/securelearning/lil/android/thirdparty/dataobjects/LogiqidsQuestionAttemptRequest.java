package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsQuestionAttemptRequest implements Serializable {

    @SerializedName("question_id")
    @Expose
    private int mQuestionId;

    @SerializedName("marked")
    @Expose
    private String mMarkedChoice;

    public int getQuestionId() {
        return mQuestionId;
    }

    public void setQuestionId(int questionId) {
        mQuestionId = questionId;
    }

    public String getMarkedChoice() {
        return mMarkedChoice;
    }

    public void setMarkedChoice(String markedChoice) {
        mMarkedChoice = markedChoice;
    }
}
