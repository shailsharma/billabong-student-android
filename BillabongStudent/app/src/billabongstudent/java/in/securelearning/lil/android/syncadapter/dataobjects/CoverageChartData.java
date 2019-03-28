package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CoverageChartData implements Serializable {

    @SerializedName("coverage")
    @Expose
    private float mCoverage;

    @SerializedName("pending")
    @Expose
    private float mPending;

    @SerializedName("total")
    @Expose
    private float mTotal;

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mName;

    private float mProgress;

    public float getCoverage() {
        return mCoverage;
    }

    public void setCoverage(float coverage) {
        mCoverage = coverage;
    }

    public float getPending() {
        return mPending;
    }

    public void setPending(float pending) {
        mPending = pending;
    }

    public float getTotal() {
        return mTotal;
    }

    public void setTotal(float total) {
        mTotal = total;
    }

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

    public float getProgress() {
        return (mCoverage / mTotal) * 100;
    }

    public void setProgress(float progress) {
        mProgress = progress;
    }
}
