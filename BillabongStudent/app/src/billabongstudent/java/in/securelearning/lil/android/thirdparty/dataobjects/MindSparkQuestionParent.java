package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkQuestionParent extends MindSparkResult implements Serializable {

    @SerializedName("contentData")
    @Expose
    private MindSparkQuestionContentData mContentData;

    @SerializedName("redirectionData")
    @Expose
    private MindSparkRedirectionData mRedirectionData;

    public MindSparkQuestionContentData getContentData() {
        return mContentData;
    }

    public void setContentData(MindSparkQuestionContentData contentData) {
        mContentData = contentData;
    }

    public MindSparkRedirectionData getRedirectionData() {
        return mRedirectionData;
    }

    public void setRedirectionData(MindSparkRedirectionData redirectionData) {
        mRedirectionData = redirectionData;
    }
}
