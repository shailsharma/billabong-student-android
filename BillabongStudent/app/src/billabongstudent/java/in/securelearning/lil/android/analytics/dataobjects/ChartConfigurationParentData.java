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

    public void setPerformanceConfiguration(ArrayList<ChartConfigurationData> performanceConfiguration) {
        mPerformanceConfiguration = performanceConfiguration;
    }

    public ArrayList<ChartConfigurationData> getCoverageConfiguration() {
        return mCoverageConfiguration;
    }

    public void setCoverageConfiguration(ArrayList<ChartConfigurationData> coverageConfiguration) {
        mCoverageConfiguration = coverageConfiguration;
    }
}
