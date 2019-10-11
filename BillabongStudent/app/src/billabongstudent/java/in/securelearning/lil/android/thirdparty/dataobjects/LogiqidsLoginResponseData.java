package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsLoginResponseData implements Serializable {

    @SerializedName("role")
    @Expose
    private String mRole;

    @SerializedName("user_id")
    @Expose
    private int mUserId;

    @SerializedName("session_token")
    @Expose
    private String mSessionToken;

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public void setSessionToken(String sessionToken) {
        mSessionToken = sessionToken;
    }
}
