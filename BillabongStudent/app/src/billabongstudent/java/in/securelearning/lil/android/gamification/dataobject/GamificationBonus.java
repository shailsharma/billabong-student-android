package in.securelearning.lil.android.gamification.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GamificationBonus implements Serializable {

    @SerializedName("id")
    @Expose
    private String mBonusId;

    private Integer GamificationId;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("subjectId")
    @Expose
    private String subjectId;

    @SerializedName("subjectName")
    @Expose
    private String subjectName;

    @SerializedName("topicId")
    @Expose
    private String topicId;

    // where we have to update the bonus login , subject
    private String bonusActivity;

    private String bonusSubActivity;

    @SerializedName("percentMultiplier")
    @Expose
    private Integer multiplier;

    @SerializedName("endDateTime")
    @Expose
    private String endDate;

    @SerializedName("startDateTime")
    @Expose
    private String startDate;

    @SerializedName("bonusType")
    @Expose
    private String type;

    @SerializedName("sectionId")
    @Expose
    private String sectionId;

    @SerializedName("gradeId")
    @Expose
    private String gradeId;

    @SerializedName("consumptionStatus")
    @Expose
    private Integer consumptionStatus;

    @SerializedName("academicSessionId")
    @Expose
    private String academicSessionId;

    private Boolean isBonusAvail;


    public String getBonusId() {
        return mBonusId;
    }

    public void setBonusId(String bonusId) {
        mBonusId = bonusId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }


    public String getBonusActivity() {
        return bonusActivity;
    }

    public void setBonusActivity(String bonusActivity) {
        this.bonusActivity = bonusActivity;
    }

    public String getBonusSubActivity() {
        return bonusSubActivity;
    }

    public void setBonusSubActivity(String bonusSubActivity) {
        this.bonusSubActivity = bonusSubActivity;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getBonusType() {
        return type;
    }

    public void setBonusType(String bonusType) {
        this.type = bonusType;
    }

    public int getGamificationId() {
        return GamificationId;
    }

    public void setGamificationId(int gamificationId) {
        GamificationId = gamificationId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setGamificationId(Integer gamificationId) {
        GamificationId = gamificationId;
    }

    public void setMultiplier(Integer multiplier) {
        this.multiplier = multiplier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getConsumptionStatus() {
        return consumptionStatus;
    }

    public void setConsumptionStatus(Integer consumptionStatus) {
        this.consumptionStatus = consumptionStatus;
    }

    public String getAcademicSessionId() {
        return academicSessionId;
    }

    public void setAcademicSessionId(String academicSessionId) {
        this.academicSessionId = academicSessionId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Boolean getBonusAvail() {
        return isBonusAvail;
    }

    public void setBonusAvail(Boolean bonusAvail) {
        isBonusAvail = bonusAvail;
    }
}