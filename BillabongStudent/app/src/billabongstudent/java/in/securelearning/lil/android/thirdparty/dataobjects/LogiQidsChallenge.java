package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 31/8/19.
 */
public class LogiQidsChallenge implements Serializable {

    @SerializedName("challangeId")
    @Expose
    private String mChallengeId;

    @SerializedName("gradeId")
    @Expose
    private String mGradeId;

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    @SerializedName("topicId")
    @Expose
    private String mTopicId;

    @SerializedName("topicName")
    @Expose
    private String mTopicName;

    @SerializedName("logiQidsTopicId")
    @Expose
    private int mLogiQidsTopicId;

    @SerializedName("logiQidsTopicName")
    @Expose
    private String mLogiQidsTopicName;

    @SerializedName("logiQidsDescription")
    @Expose
    private String mLogiQidsDescription;

    @SerializedName("logiQidsImageUrl")
    @Expose
    private String mLogiQidsImageUrl;


    public String getChallengeId() {
        return mChallengeId;
    }

    public void setChallengeId(String challengeId) {
        mChallengeId = challengeId;
    }

    public String getGradeId() {
        return mGradeId;
    }

    public void setGradeId(String gradeId) {
        mGradeId = gradeId;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }

    public String getTopicName() {
        return mTopicName;
    }

    public void setTopicName(String topicName) {
        mTopicName = topicName;
    }

    public int getLogiQidsTopicId() {
        return mLogiQidsTopicId;
    }

    public void setLogiQidsTopicId(int logiQidsTopicId) {
        mLogiQidsTopicId = logiQidsTopicId;
    }

    public String getLogiQidsTopicName() {
        return mLogiQidsTopicName;
    }

    public void setLogiQidsTopicName(String logiQidsTopicName) {
        mLogiQidsTopicName = logiQidsTopicName;
    }

    public String getLogiQidsDescription() {
        return mLogiQidsDescription;
    }

    public void setLogiQidsDescription(String logiQidsDescription) {
        mLogiQidsDescription = logiQidsDescription;
    }

    public String getLogiQidsImageUrl() {
        return mLogiQidsImageUrl;
    }

    public void setLogiQidsImageUrl(String logiQidsImageUrl) {
        mLogiQidsImageUrl = logiQidsImageUrl;
    }
}
