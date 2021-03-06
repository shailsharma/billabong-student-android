package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StudentAchievement implements Serializable {

    @SerializedName("rewardsList")
    @Expose
    private ArrayList<StudentSubjectReward> mRewardsList;

    @SerializedName("totalRewards")
    @Expose
    private int mTotalRewards;

    public ArrayList<StudentSubjectReward> getRewardsList() {
        return mRewardsList;
    }

    public void setRewardsList(ArrayList<StudentSubjectReward> rewardsList) {
        mRewardsList = rewardsList;
    }

    public int getTotalRewards() {
        return mTotalRewards;
    }

    public void setTotalRewards(int totalRewards) {
        mTotalRewards = totalRewards;
    }
}
