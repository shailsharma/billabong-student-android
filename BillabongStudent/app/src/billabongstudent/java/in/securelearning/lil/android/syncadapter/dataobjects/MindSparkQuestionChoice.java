package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkQuestionChoice implements Serializable {

    @SerializedName("value")
    @Expose
    private String mValue;

    @SerializedName("correct")
    @Expose
    private String mCorrect;

    @SerializedName("score")
    @Expose
    private int mScore;

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getCorrect() {
        return mCorrect;
    }

    public void setCorrect(String correct) {
        mCorrect = correct;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }
}
