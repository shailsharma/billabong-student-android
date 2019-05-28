package in.securelearning.lil.android.mindspark.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkQuestionTypeDropdown implements Serializable {

    @SerializedName("type")
    @Expose
    private String mQuestionType;

    @SerializedName("correctAnswer")
    @Expose
    private String mCorrectAnswerIndex;

    @SerializedName("choices")
    @Expose
    private ArrayList<MindSparkDropdownQuestionChoice> mChoices;

    @SerializedName("userAnswer")
    @Expose
    private String mUserAnswer;

    public String getQuestionType() {
        return mQuestionType;
    }

    public void setQuestionType(String questionType) {
        mQuestionType = questionType;
    }

    public String getCorrectAnswerIndex() {
        return mCorrectAnswerIndex;
    }

    public void setCorrectAnswerIndex(String correctAnswerIndex) {
        mCorrectAnswerIndex = correctAnswerIndex;
    }

    public ArrayList<MindSparkDropdownQuestionChoice> getChoices() {
        return mChoices;
    }

    public void setChoices(ArrayList<MindSparkDropdownQuestionChoice> choices) {
        mChoices = choices;
    }

    public String getUserAnswer() {
        return mUserAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        mUserAnswer = userAnswer;
    }
}
