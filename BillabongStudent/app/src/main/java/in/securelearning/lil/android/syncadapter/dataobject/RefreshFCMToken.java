package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RefreshFCMToken implements Serializable {

    @SerializedName("token")
    @Expose
    private String mToken;

    @SerializedName("userDeviceType")
    @Expose
    private String mUserDeviceType;

    public RefreshFCMToken() {
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getUserDeviceType() {
        return mUserDeviceType;
    }

    public void setUserDeviceType(String userDeviceType) {
        mUserDeviceType = userDeviceType;
    }
}
