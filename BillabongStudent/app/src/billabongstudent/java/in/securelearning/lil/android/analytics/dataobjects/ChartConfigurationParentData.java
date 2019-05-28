package in.securelearning.lil.android.analytics.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ChartConfigurationParentData implements Serializable {

    @SerializedName("performance")
    @Expose
    private ArrayList<ChartConfigurationData> mPerformanceConfiguration;

    @SerializedName("coverage")
    @Expose
    private ArrayList<ChartConfigurationData> mCoverageConfiguration;

    public ArrayList<ChartConfigurationData> getPerformanceConfiguration() {
        return mPerformanceConfiguration;
    }

    // Getting
    @SerializedName("performanceStandards")
    @Expose
     private BenchMarkPerformance mBenchMarkPerformance;

    public void setPerformanceConfiguration(ArrayList<ChartConfigurationData> performanceConfiguration) {
        mPerformanceConfiguration = performanceConfiguration;
    }

    public ArrayList<ChartConfigurationData> getCoverageConfiguration() {
        return mCoverageConfiguration;
    }

    public BenchMarkPerformance getBenchMarkPerformance() {
        return mBenchMarkPerformance;
    }

    public void setCoverageConfiguration(ArrayList<ChartConfigurationData> coverageConfiguration) {
        mCoverageConfiguration = coverageConfiguration;

    }

    public class BenchMarkPerformance implements Serializable {

        @SerializedName("time")
        @Expose
        private double benchMarkTime;

        @SerializedName("percentage")
        @Expose
        private double benchMarkPercentage;

        public double getBenchMarkTime() {
            return benchMarkTime;
        }

        public double getBenchMarkPercentage() {
            return benchMarkPercentage;
        }

        public void setBenchMarkTime(double benchMarkTime) {
            this.benchMarkTime = benchMarkTime;
        }

        public void setBenchMarkPercentage(double benchMarkPercentage) {
            this.benchMarkPercentage = benchMarkPercentage;
        }
    }
}
