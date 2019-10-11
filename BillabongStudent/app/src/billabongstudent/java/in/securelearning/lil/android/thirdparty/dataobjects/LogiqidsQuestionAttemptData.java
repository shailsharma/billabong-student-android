package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsQuestionAttemptData implements Serializable {

    @SerializedName("is_correct")
    @Expose
    private boolean mIsCorrect;

    @SerializedName("solution")
    @Expose
    private LogiqidsTextImageObject mSolution;

    public boolean isCorrect() {
        return mIsCorrect;
    }

    public void setCorrect(boolean correct) {
        mIsCorrect = correct;
    }

    public LogiqidsTextImageObject getSolution() {
        return mSolution;
    }

    public void setSolution(LogiqidsTextImageObject solution) {
        mSolution = solution;
    }
}
