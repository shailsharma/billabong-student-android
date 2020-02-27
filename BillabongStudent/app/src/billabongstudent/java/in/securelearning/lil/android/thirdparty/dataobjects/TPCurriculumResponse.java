package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TPCurriculumResponse implements Serializable {

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("boardId")
    @Expose
    private String mBoardId;

    @SerializedName("gradeId")
    @Expose
    private String mGradeId;

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    @SerializedName("topicId")
    @Expose
    private String mTopicId;

    @SerializedName("topicName")
    @Expose
    private String mTopicName;

    @SerializedName("tpImageUrl")
    @Expose
    private String mTpImageUrl;

    @SerializedName("tpTopicId")
    @Expose
    private String mTpTopicId;

    @SerializedName("tpTitle")
    @Expose
    private String mTpTitle;

    @SerializedName("tpType")
    @Expose
    private String mTpType;

    @SerializedName("tpDescription")
    @Expose
    private String mTpDescription;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getBoardId() {
        return mBoardId;
    }

    public void setBoardId(String boardId) {
        mBoardId = boardId;
    }

    public String getGradeId() {
        return mGradeId;
    }

    public void setGradeId(String gradeId) {
        mGradeId = gradeId;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }

    public String getTopicName() {
        return mTopicName;
    }

    public void setTopicName(String topicName) {
        mTopicName = topicName;
    }

    public String getTpImageUrl() {
        return mTpImageUrl;
    }

    public void setTpImageUrl(String tpImageUrl) {
        mTpImageUrl = tpImageUrl;
    }

    public String getTpTopicId() {
        return mTpTopicId;
    }

    public void setTpTopicId(String tpTopicId) {
        mTpTopicId = tpTopicId;
    }

    public String getTpTitle() {
        return mTpTitle;
    }

    public void setTpTitle(String tpTitle) {
        mTpTitle = tpTitle;
    }

    public String getTpType() {
        return mTpType;
    }

    public void setTpType(String tpType) {
        mTpType = tpType;
    }

    public String getTpDescription() {
        return mTpDescription;
    }

    public void setTpDescription(String tpDescription) {
        mTpDescription = tpDescription;
    }
}
