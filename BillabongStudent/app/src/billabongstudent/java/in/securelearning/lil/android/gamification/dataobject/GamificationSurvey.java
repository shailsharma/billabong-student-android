package in.securelearning.lil.android.gamification.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GamificationSurvey implements Serializable {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("detail")
    @Expose
    private GamificationSurveyDetail surveyDetail;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public GamificationSurveyDetail getSurveyDetail() {
        return surveyDetail;
    }

    public void setSurveyDetail(GamificationSurveyDetail surveyDetail) {
        this.surveyDetail = surveyDetail;
    }
}
