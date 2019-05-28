package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LRPARequest implements Serializable {

    public static final String TYPE_LEARN = "l";
    public static final String TYPE_REINFORCE = "r";
    public static final String TYPE_PRACTICE = "p";
    public static final String TYPE_APPLY = "a";

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
