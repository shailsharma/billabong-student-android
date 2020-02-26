package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RevisionResult implements Serializable {

    @SerializedName("totalMarks")
    @Expose
    private int mTotalMarks;

    @SerializedName("totalTime")
    @Expose
    private long mTotalTime;

    @SerializedName("accuracy")
    @Expose
    private float mAccuracy;

    @SerializedName("totalQuestions")
    @Expose
    private int mTotalQuestions;

    @SerializedName("correctQuestion")
    @Expose
    private int mCorrectQuestion;


    public int getTotalMarks() {
        return mTotalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        mTotalMarks = totalMarks;
    }

    public long getTotalTime() {
        return mTotalTime;
    }

    public void setTotalTime(long totalTime) {
        mTotalTime = totalTime;
    }

    public float getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(float accuracy) {
        mAccuracy = accuracy;
    }

    public int getTotalQuestions() {
        return mTotalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        mTotalQuestions = totalQuestions;
    }

    public int getCorrectQuestion() {
        return mCorrectQuestion;
    }

    public void setCorrectQuestion(int correctQuestion) {
        mCorrectQuestion = correctQuestion;
    }
}
