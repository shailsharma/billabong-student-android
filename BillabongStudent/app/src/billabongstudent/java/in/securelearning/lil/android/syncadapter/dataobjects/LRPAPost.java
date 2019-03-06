package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LRPAPost implements Serializable {

    @SerializedName("type")
    @Expose
    private String mType;

    @SerializedName("topicId")
    @Expose
    private String mTopicId;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }
}
