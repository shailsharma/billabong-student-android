package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkQuestionTypeBlank implements Serializable {

    @SerializedName("type")
    @Expose
    private String mQuestionType;

    @SerializedName("score")
    @Expose
    private int mScore;

    @SerializedName("correctAnswers")
    @Expose
    private String mCorrectAnswer;

    @SerializedName("userAnswer")
    @Expose
    private String mUserAnswer;

    public String getQuestionType() {
        return mQuestionType;
    }

    public void setQuestionType(String questionType) {
        mQuestionType = questionType;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

    public String getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        mCorrectAnswer = correctAnswer;
    }

    public String getUserAnswer() {
        return mUserAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        mUserAnswer = userAnswer;
    }
}
