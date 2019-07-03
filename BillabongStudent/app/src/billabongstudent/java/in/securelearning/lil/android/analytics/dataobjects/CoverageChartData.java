package in.securelearning.lil.android.analytics.dataobjects;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CoverageChartData implements Serializable, Comparable<CoverageChartData> {

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

    public void setCoverage(float coverage) {
        mCoverage = coverage;
    }

    public float getCoverage() {
        return mCoverage;
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

    @Override
    public int compareTo(@NonNull CoverageChartData coverageChartData) {
//        float coverage_1 = (coverageChartData.getCoverage() / coverageChartData.getTotal()) * 100;
//        float coverage_2 = (this.getCoverage() /this.getTotal()) * 100;
        if (this.getCoverage() < coverageChartData.getCoverage()) return 1;
        if (this.getCoverage() > coverageChartData.getCoverage()) return -1;
        return 0;
    }

}
