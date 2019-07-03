package in.securelearning.lil.android.mindspark.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkQuestionRequest implements Serializable {

    @SerializedName("jwt")
    @Expose
    private String mJWT;

    @SerializedName("mode")
    @Expose
    private String mMode;

    @SerializedName("Action")
    @Expose
    private String mAction;

    @SerializedName("topicID")
    @Expose
    private String mTopicId;

    public String getJWT() {
        return mJWT;
    }

    public void setJWT(String JWT) {
        mJWT = JWT;
    }

    public String getMode() {
        return mMode;
    }

    public void setMode(String mode) {
        mMode = mode;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        mAction = action;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }
}
