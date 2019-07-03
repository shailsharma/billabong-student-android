package in.securelearning.lil.android.mindspark.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkQuestionUserAttempt implements Serializable {

    @SerializedName("hintTaken")
    @Expose
    private boolean mIsHintTaken;

    @SerializedName("trialCount")
    @Expose
    private int mTrialCount;

    @SerializedName("trials")
    @Expose
    private ArrayList<MindSparkQuestionTrial> mTrials;

    public boolean isHintTaken() {
        return mIsHintTaken;
    }

    public void setHintTaken(boolean hintTaken) {
        mIsHintTaken = hintTaken;
    }

    public int getTrialCount() {
        return mTrialCount;
    }

    public void setTrialCount(int trialCount) {
        mTrialCount = trialCount;
    }

    public ArrayList<MindSparkQuestionTrial> getTrials() {
        return mTrials;
    }

    public void setTrials(ArrayList<MindSparkQuestionTrial> trials) {
        mTrials = trials;
    }
}
