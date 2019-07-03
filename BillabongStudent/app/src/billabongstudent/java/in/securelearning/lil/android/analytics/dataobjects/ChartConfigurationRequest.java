package in.securelearning.lil.android.analytics.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChartConfigurationRequest implements Serializable {

    @SerializedName("performance")
    @Expose
    private boolean mIsPerformance;

    @SerializedName("coverage")
    @Expose
    private boolean mIsCoverage;

    @SerializedName("performanceStandards")
    @Expose
    private boolean mIsPerformanceStandards;

    public boolean isPerformance() {
        return mIsPerformance;
    }

    public void setPerformance(boolean performance) {
        mIsPerformance = performance;
    }

    public boolean isCoverage() {
        return mIsCoverage;
    }

    public void setCoverage(boolean coverage) {
        mIsCoverage = coverage;
    }

    public void setPerformanceStandards(boolean performanceStandards) {
        mIsPerformanceStandards = performanceStandards;
    }
}
