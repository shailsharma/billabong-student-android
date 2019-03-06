package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.app.R;

public class MindSparkContentDetails implements Serializable {

    @SerializedName("contentID")
    @Expose
    private String mContentId;

    @SerializedName("contentVersionID")
    @Expose
    private String mContentVersionId;

    @SerializedName("contentType")
    @Expose
    private String mContentType;

    @SerializedName("questionType")
    @Expose
    private String mQuestionType;

    @SerializedName("revisionNum")
    @Expose
    private String mRevisionNum;

    @SerializedName("langCode")
    @Expose
    private String mLangCode;

    @SerializedName("contentStatus")
    @Expose
    private String mContentStatus;

    @SerializedName("contentName")
    @Expose
    private String mContentName;

    @SerializedName("unitsOverall")
    @Expose
    private int mUnitsOverall;

    @SerializedName("unitsCleared")
    @Expose
    private int mUnitsCleared;

    public String getContentId() {
        return mContentId;
    }

    public void setContentId(String contentId) {
        mContentId = contentId;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType) {
        mContentType = contentType;
    }

    public String getContentStatus() {
        return mContentStatus;
    }

    public void setContentStatus(String contentStatus) {
        mContentStatus = contentStatus;
    }

    public String getContentName() {
        return mContentName;
    }

    public void setContentName(String contentName) {
        mContentName = contentName;
    }

    public int getUnitsOverall() {
        return mUnitsOverall;
    }

    public void setUnitsOverall(int unitsOverall) {
        mUnitsOverall = unitsOverall;
    }

    public int getUnitsCleared() {
        return mUnitsCleared;
    }

    public void setUnitsCleared(int unitsCleared) {
        mUnitsCleared = unitsCleared;
    }

    public int getBackgroundThumb() {
        return R.drawable.background_thumb_mind_spark;
    }

    public String getContentVersionId() {
        return mContentVersionId;
    }

    public void setContentVersionId(String contentVersionId) {
        mContentVersionId = contentVersionId;
    }

    public String getQuestionType() {
        return mQuestionType;
    }

    public void setQuestionType(String questionType) {
        mQuestionType = questionType;
    }

    public String getRevisionNum() {
        return mRevisionNum;
    }

    public void setRevisionNum(String revisionNum) {
        mRevisionNum = revisionNum;
    }

    public String getLangCode() {
        return mLangCode;
    }

    public void setLangCode(String langCode) {
        mLangCode = langCode;
    }
}
