package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.PopUpType;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;

public class AboutCourseMinimal implements Serializable {

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("title")
    @Expose
    private String mTitle;

    @SerializedName("thumbnail")
    @Expose
    private Thumbnail mThumbnail;

    @SerializedName(value = "courseType", alternate = "course_type")
    @Expose
    private String mCourseType;

    @SerializedName("microCourseType")
    @Expose
    private String mMicroCourseType;

    @SerializedName("popUpType")
    @Expose
    private PopUpType mPopUpType;

    @SerializedName("metaInformation")
    @Expose
    private MetaInformation mMetaInformation;

    @SerializedName("totalMarks")
    @Expose
    private int mTotalMarks;

    private boolean mIsSelected;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Thumbnail getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        mThumbnail = thumbnail;
    }

    public String getCourseType() {
        return mCourseType;
    }

    public void setCourseType(String courseType) {
        mCourseType = courseType;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String getMicroCourseType() {
        return mMicroCourseType;
    }

    public void setMicroCourseType(String microCourseType) {
        mMicroCourseType = microCourseType;
    }

    public PopUpType getPopUpType() {
        return mPopUpType;
    }

    public void setPopUpType(PopUpType popUpType) {
        mPopUpType = popUpType;
    }

    public MetaInformation getMetaInformation() {
        return mMetaInformation;
    }

    public void setMetaInformation(MetaInformation metaInformation) {
        mMetaInformation = metaInformation;
    }

    public int getTotalMarks() {
        return mTotalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        mTotalMarks = totalMarks;
    }
}
