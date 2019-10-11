package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkTopicListRequest implements Serializable {

    @SerializedName("jwt")
    @Expose
    private String mJwt;

    public MindSparkTopicListRequest(String jwt) {
        mJwt = jwt;
    }

    public String getJwt() {
        return mJwt;
    }

    public void setJwt(String jwt) {
        mJwt = jwt;
    }
}
