package in.securelearning.lil.android.profile.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AdministrativeInfo;
import in.securelearning.lil.android.base.dataobjects.CuratorMapping;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.syncadapter.dataobjects.IdNameObject;

/**
 * Created by Rajat Jain on 28-Nov-18
 */
public class TeacherProfile extends UserProfile implements Serializable {

    public static final int VALUE_FULL_TIME = 0;
    public static final int VALUE_PART_TIME = 1;

    public static final String FULL_TIME = "Full Time";
    public static final String PART_TIME = "Part Time";

    @SerializedName("curatorMappingDetails")
    @Expose
    private ArrayList<CuratorMapping> mCuratorMappings;

    @SerializedName("observationByCount")
    @Expose
    private Integer mObservationByMeCount;

    @SerializedName("observationForCount")
    @Expose
    private Integer mObservationForMeCount;

    @SerializedName("completedTrainingCount")
    @Expose
    private Integer mCompletedTrainingCount;

    @SerializedName("duration")
    @Expose
    private Float mDurationCount;//Should be in hours

    @SerializedName("certificates")
    @Expose
    private Integer mCertificateCount;

    @SerializedName("enrolledTrainingCount")
    @Expose
    private Integer mEnrolledTrainingCount;

    @SerializedName("dueTrainings")
    @Expose
    private Integer mDueTrainingCount;

    @SerializedName("overDueTrainings")
    @Expose
    private Integer mOverdueTrainingCount;

    @SerializedName("designationDetail")
    @Expose
    private IdNameObject mDesignationDetail;


    @SerializedName("departmentId")
    @Expose
    private String mDepartmentId;

    @SerializedName("departmentDetail")
    @Expose
    private IdNameObject mDepartmentDetail;

    @SerializedName("employeeType")
    @Expose
    private Integer mEmployeeType;

    @SerializedName("learninglevels")
    @Expose
    private ArrayList<IdNameObject> mLearningLevels;

    @SerializedName("subjects")
    @Expose
    private ArrayList<IdNameObject> mSubjects;

    @SerializedName("learningLevelIds")
    @Expose
    private ArrayList<String> mLearningLevelIds;

    @SerializedName("gradeIds")
    @Expose
    private ArrayList<String> mGradeIds;

    @SerializedName("subjectIds")
    @Expose
    private ArrayList<String> mSubjectIds;

    @SerializedName("observationAvgRating")
    @Expose
    private Float observationAvgRating;

    @SerializedName("trainingAvgRating")
    @Expose
    private Float trainingAvgRating;

    @SerializedName("reflectionAvgRating")
    @Expose
    private Float reflectionAvgRating;

    @SerializedName("reflectionCourseSubmitted")
    @Expose
    private Integer reflectionCourseSubmitted;

    @SerializedName("seniorEmployees")
    @Expose
    private ArrayList<Object> mSeniorEmployees;

    @SerializedName("isCurator")
    @Expose
    private Boolean mIsCurator;

    @SerializedName("isPrincipal")
    @Expose
    private Boolean mIsPrincipal;

    @SerializedName("isSeniorAcademicManager")
    @Expose
    private Boolean mIsSeniorAcademicManager;

    @SerializedName("isJuniorAcademicManager")
    @Expose
    private Boolean mIsJuniorAcademicManager;

    @SerializedName("userVideoThumbnail")
    @Expose
    private ProfileVideo mProfileVideo;

    @SerializedName("administrativeInfo")
    @Expose
    private ArrayList<AdministrativeInfo> mAdministrativeInfo;

    @SerializedName("achievements")
    @Expose
    private TeacherAchievementRewards mTeacherAchievementRewards;

    public Integer getObservationByMeCount() {
        return mObservationByMeCount;
    }

    public void setObservationByMeCount(Integer observationByMeCount) {
        mObservationByMeCount = observationByMeCount;
    }

    public Integer getObservationForMeCount() {
        return mObservationForMeCount;
    }

    public void setObservationForMeCount(Integer observationForMeCount) {
        mObservationForMeCount = observationForMeCount;
    }

    public Integer getCompletedTrainingCount() {
        return mCompletedTrainingCount;
    }

    public void setCompletedTrainingCount(Integer completedTrainingCount) {
        mCompletedTrainingCount = completedTrainingCount;
    }

    public Float getDurationCount() {
        // return Float.parseFloat(new DecimalFormat("##.##").format(mDurationCount));
        return mDurationCount;
    }

    public void setDurationCount(Float durationCount) {
        mDurationCount = durationCount;
    }

    public Integer getCertificateCount() {
        return mCertificateCount;
    }

    public void setCertificateCount(Integer certificateCount) {
        mCertificateCount = certificateCount;
    }

    public Integer getEnrolledTrainingCount() {
        return mEnrolledTrainingCount;
    }

    public void setEnrolledTrainingCount(Integer enrolledTrainingCount) {
        mEnrolledTrainingCount = enrolledTrainingCount;
    }

    public Integer getDueTrainingCount() {
        return mDueTrainingCount;
    }

    public void setDueTrainingCount(Integer dueTrainingCount) {
        mDueTrainingCount = dueTrainingCount;
    }

    public Integer getOverdueTrainingCount() {
        return mOverdueTrainingCount;
    }

    public void setOverdueTrainingCount(Integer overdueTrainingCount) {
        mOverdueTrainingCount = overdueTrainingCount;
    }

    public ArrayList<CuratorMapping> getCuratorMappings() {
        return mCuratorMappings;
    }

    public void setCuratorMappings(ArrayList<CuratorMapping> curatorMappings) {
        mCuratorMappings = curatorMappings;
    }

    public IdNameObject getDesignationDetail() {
        return mDesignationDetail;
    }

    public void setDesignationDetail(IdNameObject designationDetail) {
        mDesignationDetail = designationDetail;
    }

    public String getDepartmentId() {
        return mDepartmentId;
    }

    public void setDepartmentId(String departmentId) {
        mDepartmentId = departmentId;
    }

    public IdNameObject getDepartmentDetail() {
        return mDepartmentDetail;
    }

    public void setDepartmentDetail(IdNameObject departmentDetail) {
        mDepartmentDetail = departmentDetail;
    }

    public Integer getEmployeeType() {
        return mEmployeeType;
    }

    public void setEmployeeType(Integer employeeType) {
        mEmployeeType = employeeType;
    }

    public ArrayList<IdNameObject> getLearningLevels() {
        return mLearningLevels;
    }

    public void setLearningLevels(ArrayList<IdNameObject> learningLevels) {
        mLearningLevels = learningLevels;
    }

    public ArrayList<IdNameObject> getSubjects() {
        return mSubjects;
    }

    public void setSubjects(ArrayList<IdNameObject> subjects) {
        mSubjects = subjects;
    }

    public ArrayList<String> getLearningLevelIds() {
        return mLearningLevelIds;
    }

    public void setLearningLevelIds(ArrayList<String> learningLevelIds) {
        mLearningLevelIds = learningLevelIds;
    }

    public ArrayList<String> getGradeIds() {
        return mGradeIds;
    }

    public void setGradeIds(ArrayList<String> gradeIds) {
        mGradeIds = gradeIds;
    }

    public ArrayList<String> getSubjectIds() {
        return mSubjectIds;
    }

    public void setSubjectIds(ArrayList<String> subjectIds) {
        mSubjectIds = subjectIds;
    }

    public Float getObservationAvgRating() {
        return observationAvgRating;
    }

    public Float getTrainingAvgRating() {
        return trainingAvgRating;
    }

    public Float getReflectionAvgRating() {
        return reflectionAvgRating;
    }

    public Integer getReflectionCourseSubmitted() {
        return reflectionCourseSubmitted;
    }


    public void setObservationAvgRating(Float observationAvgRating) {
        this.observationAvgRating = observationAvgRating;
    }

    public void setTrainingAvgRating(Float trainingAvgRating) {
        this.trainingAvgRating = trainingAvgRating;
    }

    public void setReflectionAvgRating(Float reflectionAvgRating) {
        this.reflectionAvgRating = reflectionAvgRating;
    }

    public void setReflectionCourseSubmitted(Integer reflectionCourseSubmitted) {
        this.reflectionCourseSubmitted = reflectionCourseSubmitted;
    }

    public ArrayList<Object> getSeniorEmployees() {
        return mSeniorEmployees;
    }

    public void setSeniorEmployees(ArrayList<Object> seniorEmployees) {
        mSeniorEmployees = seniorEmployees;
    }

    public Boolean getCurator() {
        return mIsCurator;
    }

    public void setCurator(Boolean curator) {
        mIsCurator = curator;
    }

    public Boolean getPrincipal() {
        return mIsPrincipal;
    }

    public void setPrincipal(Boolean principal) {
        mIsPrincipal = principal;
    }

    public Boolean getSeniorAcademicManager() {
        return mIsSeniorAcademicManager;
    }

    public void setSeniorAcademicManager(Boolean seniorAcademicManager) {
        mIsSeniorAcademicManager = seniorAcademicManager;
    }

    public Boolean getJuniorAcademicManager() {
        return mIsJuniorAcademicManager;
    }

    public void setJuniorAcademicManager(Boolean juniorAcademicManager) {
        mIsJuniorAcademicManager = juniorAcademicManager;
    }

    public ArrayList<AdministrativeInfo> getAdministrativeInfo() {
        return mAdministrativeInfo;
    }

    public void setAdministrativeInfo(ArrayList<AdministrativeInfo> administrativeInfo) {
        mAdministrativeInfo = administrativeInfo;
    }

    public ProfileVideo getProfileVideo() {
        return mProfileVideo;
    }

    public void setProfileVideo(ProfileVideo profileVideo) {
        mProfileVideo = profileVideo;
    }

    public TeacherAchievementRewards getTeacherAchievementRewards() {
        return mTeacherAchievementRewards;
    }

    public void setTeacherAchievementRewards(TeacherAchievementRewards teacherAchievementRewards) {
        mTeacherAchievementRewards = teacherAchievementRewards;
    }
}
