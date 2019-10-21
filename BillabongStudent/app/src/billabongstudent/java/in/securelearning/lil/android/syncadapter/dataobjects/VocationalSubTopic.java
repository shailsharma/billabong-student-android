package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 4/9/19.
 */
public class VocationalSubTopic implements Serializable {

    @SerializedName("logiQidsTopicId")
    @Expose
    private Integer mLogiQidsTopicId;

    @SerializedName("logiQidsTopicName")
    @Expose
    private String mLogiQidsTopicName;

    @SerializedName("logiQidsImageUrl")
    @Expose
    private String mLogiQidsImageUrl;

    public Integer getLogiQidsTopicId() {
        return mLogiQidsTopicId;
    }

    public void setLogiQidsTopicId(Integer logiQidsTopicId) {
        mLogiQidsTopicId = logiQidsTopicId;
    }

    public String getLogiQidsTopicName() {
        return mLogiQidsTopicName;
    }

    public void setLogiQidsTopicName(String logiQidsTopicName) {
        mLogiQidsTopicName = logiQidsTopicName;
    }

    public String getLogiQidsImageUrl() {
        return mLogiQidsImageUrl;
    }

    public void setLogiQidsImageUrl(String logiQidsImageUrl) {
        mLogiQidsImageUrl = logiQidsImageUrl;
    }
}
