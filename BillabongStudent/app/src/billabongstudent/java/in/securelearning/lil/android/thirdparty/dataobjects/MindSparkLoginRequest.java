package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkLoginRequest implements Serializable {

    @SerializedName("token")
    @Expose
    private String mAuthToken;

    @SerializedName("username")
    @Expose
    private String mUserId;

    @SerializedName("vendorCode")
    @Expose
    private String mVendorCode;

    @SerializedName("topicID")
    @Expose
    private String mTopicId;

    public String getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(String authToken) {
        mAuthToken = authToken;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getVendorCode() {
        return mVendorCode;
    }

    public void setVendorCode(String vendorCode) {
        mVendorCode = vendorCode;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }
}
