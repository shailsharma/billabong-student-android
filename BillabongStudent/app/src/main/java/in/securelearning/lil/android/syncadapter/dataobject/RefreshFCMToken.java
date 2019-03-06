package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RefreshFCMToken implements Serializable {

    @SerializedName("token")
    @Expose
    private String mToken;

    @SerializedName("tokenReceivedStage")
    @Expose
    private int mType;

    public RefreshFCMToken() {
    }

    public RefreshFCMToken(String token, int type) {
        mToken = token;
        mType = type;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
