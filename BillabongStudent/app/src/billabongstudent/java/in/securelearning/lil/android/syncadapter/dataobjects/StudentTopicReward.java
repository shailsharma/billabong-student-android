package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StudentTopicReward implements Serializable {

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

    @SerializedName("topics")
    @Expose
    private ArrayList<StudentTopicReward> mTopicRewardList;

    @SerializedName("learn")
    @Expose
    private int mLearnEuros;

    @SerializedName("reinforce")
    @Expose
    private int mReinforceEuros;

    @SerializedName("practice")
    @Expose
    private int mPracticeEuros;

    @SerializedName("application")
    @Expose
    private int mApplicationEuros;

    @SerializedName("misc")
    @Expose
    private int mMiscellaneousEuros;

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

    public ArrayList<StudentTopicReward> getTopicRewardList() {
        return mTopicRewardList;
    }

    public void setTopicRewardList(ArrayList<StudentTopicReward> topicRewardList) {
        mTopicRewardList = topicRewardList;
    }

    public int getLearnEuros() {
        return mLearnEuros;
    }

    public void setLearnEuros(int learnEuros) {
        mLearnEuros = learnEuros;
    }

    public int getReinforceEuros() {
        return mReinforceEuros;
    }

    public void setReinforceEuros(int reinforceEuros) {
        mReinforceEuros = reinforceEuros;
    }

    public int getPracticeEuros() {
        return mPracticeEuros;
    }

    public void setPracticeEuros(int practiceEuros) {
        mPracticeEuros = practiceEuros;
    }

    public int getApplicationEuros() {
        return mApplicationEuros;
    }

    public void setApplicationEuros(int applicationEuros) {
        mApplicationEuros = applicationEuros;
    }

    public int getMiscellaneousEuros() {
        return mMiscellaneousEuros;
    }

    public void setMiscellaneousEuros(int miscellaneousEuros) {
        mMiscellaneousEuros = miscellaneousEuros;
    }
}
