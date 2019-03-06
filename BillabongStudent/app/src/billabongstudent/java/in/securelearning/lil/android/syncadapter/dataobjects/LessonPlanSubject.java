package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LessonPlanSubject implements Serializable {


    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("status")
    @Expose
    private String mStatus;

    @SerializedName("startDate")
    @Expose
    private String mStartDate;

    @SerializedName("endDate")
    @Expose
    private String mEndDate;

    @SerializedName("url")
    @Expose
    private String mIconUrl;

    @SerializedName("bannerUrl")
    @Expose
    private String mBannerUrl;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public void setIconUrl(String iconUrl) {
        mIconUrl = iconUrl;
    }

    public String getBannerUrl() {
        return mBannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        mBannerUrl = bannerUrl;
    }
}
