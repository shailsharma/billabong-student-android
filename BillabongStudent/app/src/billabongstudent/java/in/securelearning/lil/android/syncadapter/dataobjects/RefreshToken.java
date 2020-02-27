package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Chaitendra on 20-Jul-17.
 */

public class RefreshToken implements Serializable {
    @SerializedName("idToken")
    @Expose
    private String mIdToken;

    public String getIdToken() {
        return mIdToken;
    }

    public void setIdToken(String idToken) {
        mIdToken = idToken;
    }
}
