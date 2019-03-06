package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkQuestionMCQPattern implements Serializable {

    @SerializedName("type")
    @Expose
    private String mQuestionType;

    @SerializedName("choices")
    @Expose
    private ArrayList<MindSparkQuestionChoice> mQuestionChoices;

    @SerializedName("correctAnswer")
    @Expose
    private String mCorrectAnswer;

    @SerializedName("userAnswer")
    @Expose
    private int mUserAnswer;

    public String getQuestionType() {
        return mQuestionType;
    }

    public void setQuestionType(String questionType) {
        mQuestionType = questionType;
    }

    public ArrayList<MindSparkQuestionChoice> getQuestionChoices() {
        return mQuestionChoices;
    }

    public void setQuestionChoices(ArrayList<MindSparkQuestionChoice> questionChoices) {
        mQuestionChoices = questionChoices;
    }

    public String getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        mCorrectAnswer = correctAnswer;
    }

    public int getUserAnswer() {
        return mUserAnswer;
    }

    public void setUserAnswer(int userAnswer) {
        mUserAnswer = userAnswer;
    }
}
