package in.securelearning.lil.android.mindspark.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkLoginResponse extends MindSparkResult implements Serializable {

    @SerializedName("jwt")
    @Expose
    private String mMindSparkAuthToken;

    @SerializedName("contentDetails")
    @Expose
    private MindSparkContentDetails mMindSparkContentDetails;

    @SerializedName("topicData")
    @Expose
    private MindSparkTopicData mMindSparkTopicData;

    public String getMindSparkAuthToken() {
        return mMindSparkAuthToken;
    }

    public void setMindSparkAuthToken(String mindSparkAuthToken) {
        mMindSparkAuthToken = mindSparkAuthToken;
    }

    public MindSparkContentDetails getMindSparkContentDetails() {
        return mMindSparkContentDetails;
    }

    public void setMindSparkContentDetails(MindSparkContentDetails mindSparkContentDetails) {
        mMindSparkContentDetails = mindSparkContentDetails;
    }

    public MindSparkTopicData getMindSparkTopicData() {
        return mMindSparkTopicData;
    }

    public void setMindSparkTopicData(MindSparkTopicData mindSparkTopicData) {
        mMindSparkTopicData = mindSparkTopicData;
    }
}
