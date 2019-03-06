package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 15-Feb-19
 */
public class SubjectDetailsItem implements Serializable {

    @SerializedName("title")
    @Expose
    private String mTitle;

    @SerializedName("subTitle")
    @Expose
    private String mSubTitle;

    @SerializedName("duration")
    @Expose
    private String mDuration;

    @SerializedName("rewardPoints")
    @Expose
    private String mRewardPoints;

    @SerializedName("thumbnail")
    @Expose
    private String mThumbnail;

    @SerializedName("color")
    @Expose
    private int mColor;

    public SubjectDetailsItem() {
    }

    public SubjectDetailsItem(String title, String subTitle, String thumbnail) {
        mTitle = title;
        mSubTitle = subTitle;
        mThumbnail = thumbnail;
    }

    public SubjectDetailsItem(String title, String duration, String rewardPoints, String thumbnail) {
        mTitle = title;
        mDuration = duration;
        mRewardPoints = rewardPoints;
        mThumbnail = thumbnail;
    }

    public SubjectDetailsItem(String title, String rewardPoints, String thumbnail, int color) {
        mTitle = title;
        mRewardPoints = rewardPoints;
        mThumbnail = thumbnail;
        mColor = color;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public void setSubTitle(String subTitle) {
        mSubTitle = subTitle;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public String getRewardPoints() {
        return mRewardPoints;
    }

    public void setRewardPoints(String rewardPoints) {
        mRewardPoints = rewardPoints;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
