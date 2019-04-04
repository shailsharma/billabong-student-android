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
}
