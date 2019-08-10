package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ThirdPartyMapping implements Serializable {

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    @SerializedName("topicId")
    @Expose
    private String mTopicId;

    @SerializedName("MSTopicId")
    @Expose
    private String mMindSparkTopicId;

    @SerializedName("type")
    @Expose
    private String mType;

    public ThirdPartyMapping(String subjectId, String topicId) {
        mSubjectId = subjectId;
        mTopicId = topicId;
    }

    public ThirdPartyMapping(String subjectId, String topicId, String type) {
        mSubjectId = subjectId;
        mTopicId = topicId;
        mType = type;
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

    public String getMindSparkTopicId() {
        return mMindSparkTopicId;
    }

    public void setMindSparkTopicId(String mindSparkTopicId) {
        mMindSparkTopicId = mindSparkTopicId;
    }
}
