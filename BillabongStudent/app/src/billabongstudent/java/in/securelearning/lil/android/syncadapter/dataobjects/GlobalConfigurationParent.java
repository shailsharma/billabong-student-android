package in.securelearning.lil.android.syncadapter.dataobjects;

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

    @SerializedName("quizAnalyticsConfig")
    @Expose
    private ArrayList<ChartConfigurationData> quizAnalyticsConfig;

    @SerializedName("performance")
    @Expose
    private ArrayList<ChartConfigurationData> mPerformanceConfiguration;

    @SerializedName("coverage")
    @Expose
    private ArrayList<ChartConfigurationData> mCoverageConfiguration;

    @SerializedName("performanceStandards")
    @Expose
    private BenchMarkPerformance mBenchMarkPerformance;

    @SerializedName("improvementTypes")
    @Expose
    private ArrayList<IdNameObject> mQuestionFeedbackOptions;

    @SerializedName("revisionConfig")
    @Expose
    private RevisionConfigurationResponse mRevisionConfigurationResponse;

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

    public ArrayList<ChartConfigurationData> getQuizAnalyticsConfig() {
        return quizAnalyticsConfig;
    }

    public ArrayList<IdNameObject> getQuestionFeedbackOptions() {
        return mQuestionFeedbackOptions;
    }

    public RevisionConfigurationResponse getRevisionConfigurationResponse() {
        return mRevisionConfigurationResponse;
    }
}
