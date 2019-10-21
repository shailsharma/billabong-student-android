package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StudentSubjectReward implements Serializable {

    @SerializedName("userId")
    @Expose
    private String mUserId;

    @SerializedName(value = "id", alternate = "subjectId")
    @Expose
    private String mSubjectId;

    @SerializedName("name")
    @Expose
    private String mSubjectName;

    @SerializedName("totalScore")
    @Expose
    private int mPointsRewarded;

    @SerializedName("thumbnailUrl")
    @Expose
    private String mThumbnailUrl;

    @SerializedName("topics")
    @Expose
    private ArrayList<StudentTopicReward> topicRewardList;

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

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public ArrayList<StudentTopicReward> getTopicRewardList() {
        return topicRewardList;
    }
}
