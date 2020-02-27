package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PracticeFilter implements Serializable {

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    @SerializedName("lessonPlanId")
    @Expose
    private String mLessonPlanId;

    @SerializedName("topicId")
    @Expose
    private String mTopicIdId;

    @SerializedName("isRepeatAllowed")
    @Expose
    private boolean mIsRepeatAllowed;

    @SerializedName("skip")
    @Expose
    private int mSkip;

    @SerializedName("limit")
    @Expose
    private int mLimit;

    @SerializedName("practiceResponse")
    @Expose
    private PracticeResponse mPracticeResponse;

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public String getTopicIdId() {
        return mTopicIdId;
    }

    public void setTopicIdId(String topicIdId) {
        mTopicIdId = topicIdId;
    }

    public boolean isRepeatAllowed() {
        return mIsRepeatAllowed;
    }

    public void setRepeatAllowed(boolean repeatAllowed) {
        mIsRepeatAllowed = repeatAllowed;
    }

    public int getSkip() {
        return mSkip;
    }

    public void setSkip(int skip) {
        mSkip = skip;
    }

    public int getLimit() {
        return mLimit;
    }

    public void setLimit(int limit) {
        mLimit = limit;
    }

    public PracticeResponse getPracticeResponse() {
        return mPracticeResponse;
    }

    public void setPracticeResponse(PracticeResponse practiceResponse) {
        mPracticeResponse = practiceResponse;
    }
}
