package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.analytics.dataobjects.BenchMarkPerformance;
import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationData;

public class GlobalConfigurationParent implements Serializable {

    @SerializedName("BonusConfig")
    @Expose
    private BonusConfigurationResponse mBonus;

    @SerializedName("performance")
    @Expose
    private ArrayList<ChartConfigurationData> mPerformanceConfiguration;

    @SerializedName("coverage")
    @Expose
    private ArrayList<ChartConfigurationData> mCoverageConfiguration;

    @SerializedName("performanceStandards")
    @Expose
    private BenchMarkPerformance mBenchMarkPerformance;

    public BonusConfigurationResponse getBonus() {
        return mBonus;
    }

    public ArrayList<ChartConfigurationData> getPerformanceConfiguration() {
        return mPerformanceConfiguration;
    }

    public ArrayList<ChartConfigurationData> getCoverageConfiguration() {
        return mCoverageConfiguration;
    }

    public BenchMarkPerformance getBenchMarkPerformance() {
        return mBenchMarkPerformance;
    }

}
