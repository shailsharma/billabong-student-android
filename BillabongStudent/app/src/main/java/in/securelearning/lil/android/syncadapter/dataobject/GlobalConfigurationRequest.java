package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GlobalConfigurationRequest implements Serializable {

    @SerializedName("BonusConfig")
    @Expose
    private Boolean isBonusValue;

    @SerializedName("performance")
    @Expose
    private Boolean mIsPerformance;

    @SerializedName("coverage")
    @Expose
    private Boolean mIsCoverage;

    @SerializedName("performanceStandards")
    @Expose
    private Boolean mIsPerformanceStandards;

    public void setBonusValue(boolean bonusValue) {
        isBonusValue = bonusValue;
    }

    public void setPerformance(boolean performance) {
        mIsPerformance = performance;
    }

    public void setCoverage(boolean coverage) {
        mIsCoverage = coverage;
    }

    public void setPerformanceStandards(boolean performanceStandards) {
        mIsPerformanceStandards = performanceStandards;
    }
}
