package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GlobalConfigurationRequest implements Serializable {

    @SerializedName("BonusConfig")
    @Expose
    private Boolean isBonusValue;
    //Added for quiz chart color code

    @SerializedName("quizAnalyticsConfig")
    @Expose
    private Boolean isQuizConfig;

    @SerializedName("performance")
    @Expose
    private Boolean mIsPerformance;

    @SerializedName("coverage")
    @Expose
    private Boolean mIsCoverage;

    @SerializedName("performanceStandards")
    @Expose
    private Boolean mIsPerformanceStandards;

    @SerializedName("improvementTypes")
    @Expose
    private Boolean mQuestionFeedbackOptions;

    @SerializedName("revisionConfig")
    @Expose
    private Boolean mRevisionConfig;

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

    public void setQuizConfig(Boolean quizConfig) {
        isQuizConfig = quizConfig;
    }

    public void setQuestionFeedbackOptions(Boolean questionFeedbackOptions) {
        mQuestionFeedbackOptions = questionFeedbackOptions;
    }

    public void setRevisionConfig(Boolean revisionConfig) {
        mRevisionConfig = revisionConfig;
    }
}
