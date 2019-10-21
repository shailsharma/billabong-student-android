package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkRedirectionData implements Serializable {

    @SerializedName("endTopicFlag")
    @Expose
    private boolean mEndTopicFlag;

    public boolean isEndTopicFlag() {
        return mEndTopicFlag;
    }

    public void setEndTopicFlag(boolean endTopicFlag) {
        mEndTopicFlag = endTopicFlag;
    }
}
