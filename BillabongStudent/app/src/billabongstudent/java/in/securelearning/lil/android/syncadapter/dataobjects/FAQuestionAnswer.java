package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 14/10/19.
 */
public class FAQuestionAnswer implements Serializable {

    @SerializedName("questionText")
    @Expose
    private String mQuestion;

    @SerializedName("answerText")
    @Expose
    private String mAnswer;

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        mQuestion = question;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
    }
}
