package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserTimeSpent {

    @SerializedName("startTime")
    @Expose
    private long mStartTime;

    @SerializedName("endTime")
    @Expose
    private long mEndTime;

    @SerializedName("moduleId")
    @Expose
    private String mModuleId;

    @SerializedName("moduleName")
    @Expose
    private String mModuleName;

    @SerializedName("resourceId")
    @Expose
    private String mResourceId;

    @SerializedName("resourceName")
    @Expose
    private String mResourceName;

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public String getModuleId() {
        return mModuleId;
    }

    public void setModuleId(String moduleId) {
        mModuleId = moduleId;
    }

    public String getModuleName() {
        return mModuleName;
    }

    public void setModuleName(String moduleName) {
        mModuleName = moduleName;
    }

    public String getResourceId() {
        return mResourceId;
    }

    public void setResourceId(String resourceId) {
        mResourceId = resourceId;
    }

    public String getResourceName() {
        return mResourceName;
    }

    public void setResourceName(String resourceName) {
        mResourceName = resourceName;
    }
}

