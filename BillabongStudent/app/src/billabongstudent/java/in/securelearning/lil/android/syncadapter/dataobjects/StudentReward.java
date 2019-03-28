package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StudentReward implements Serializable {

    @SerializedName("userId")
    @Expose
    private String mUserId;

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    @SerializedName("subjectName")
    @Expose
    private String mSubjectName;

    @SerializedName("pointsRewarded")
    @Expose
    private int mPointsRewarded;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public String getSubjectName() {
        return mSubjectName;
    }

    public void setSubjectName(String subjectName) {
        mSubjectName = subjectName;
    }

    public int getPointsRewarded() {
        return mPointsRewarded;
    }

    public void setPointsRewarded(int pointsRewarded) {
        mPointsRewarded = pointsRewarded;
    }
}
