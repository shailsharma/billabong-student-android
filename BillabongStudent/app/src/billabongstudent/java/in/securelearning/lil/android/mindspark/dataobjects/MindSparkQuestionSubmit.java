package in.securelearning.lil.android.mindspark.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class MindSparkQuestionSubmit implements Serializable {

    @SerializedName("jwt")
    @Expose
    private String mJWT;

    @SerializedName("isDynamic")
    @Expose
    private boolean mIsDynamic;

    /*question ID obtained from the response of
    GetNextQuestion API (if first question) or SubmitAnswer API*/
    @SerializedName("contentID")
    @Expose
    private String mContentId;

    @SerializedName("result")
    @Expose
    private String mResult;

    @SerializedName("timeTaken")
    @Expose
    private int mTimeTaken;

    @SerializedName("userResponse")
    @Expose
    private Map<String, Object> mUserResponse;

    @SerializedName("userAttemptData")
    @Expose
    private MindSparkQuestionUserAttempt mUserAttempt;

    @SerializedName("contentInfo")
    @Expose
    private MindSparkContentDetails mContentInfo;

    @SerializedName("contentSeqNum")
    @Expose
    private int mContentSeqNum;

    @SerializedName("mode")
    @Expose
    private String mMode;

    public String getJWT() {
        return mJWT;
    }

    public void setJWT(String JWT) {
        mJWT = JWT;
    }

    public boolean isDynamic() {
        return mIsDynamic;
    }

    public void setDynamic(boolean dynamic) {
        mIsDynamic = dynamic;
    }

    public String getContentId() {
        return mContentId;
    }

    public void setContentId(String contentId) {
        mContentId = contentId;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }

    public int getTimeTaken() {
        return mTimeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        mTimeTaken = timeTaken;
    }

    public Map<String, Object> getUserResponse() {
        return mUserResponse;
    }

    public void setUserResponse(Map<String, Object> userResponse) {
        mUserResponse = userResponse;
    }

    public MindSparkQuestionUserAttempt getUserAttempt() {
        return mUserAttempt;
    }

    public void setUserAttempt(MindSparkQuestionUserAttempt userAttempt) {
        mUserAttempt = userAttempt;
    }

    public MindSparkContentDetails getContentInfo() {
        return mContentInfo;
    }

    public void setContentInfo(MindSparkContentDetails contentInfo) {
        mContentInfo = contentInfo;
    }

    public int getContentSeqNum() {
        return mContentSeqNum;
    }

    public void setContentSeqNum(int contentSeqNum) {
        mContentSeqNum = contentSeqNum;
    }

    public String getMode() {
        return mMode;
    }

    public void setMode(String mode) {
        mMode = mode;
    }
}
