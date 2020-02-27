package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Token implements Serializable {

    @SerializedName("token")
    @Expose
    private String mToken;

    public Token(String token) {
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }
}
