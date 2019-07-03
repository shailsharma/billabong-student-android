package in.securelearning.lil.android.homework.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.syncadapter.dataobject.UserMinimal;

public class Homework implements Serializable {

    @SerializedName("title")
    @Expose
    private String mTitle;

    @SerializedName("id")
    @Expose
    private String mHomeworkId;

    @SerializedName("stage")
    @Expose
    private String mStage;

    @SerializedName("assignmentType")
    @Expose
    private String mAssignmentTypeId;

    @SerializedName("assignedGroup")
    @Expose
    private ArrayList<String> mAssignedGroupIds;


    @SerializedName("allowedDuration")
    @Expose
    private int mAllowedDuration;

    @SerializedName("totalScore")
    @Expose
    private int mTotalScore;

    @SerializedName("attachmentType")
    @Expose
    private String mAttachmentType;

    @SerializedName("attachmentId")
    @Expose
    private String mAttachmentId;

    @SerializedName("assignmentDueDate")
    @Expose
    private String mAssignmentDueDate;

    @SerializedName("instructions")
    @Expose
    private String mInstructions;

    @SerializedName("assignedBy")
    @Expose
    private String mAssignedBy;

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    @SerializedName("timed")
    @Expose
    private boolean mTimed;

    @SerializedName("metaInformation")
    @Expose
    private MetaInformation mMetaInformation;

    @SerializedName("assignedDateTime")
    @Expose
    private String mAssignedDateTime;

    @SerializedName("submittedDate")
    @Expose
    private String submittedDate;

    @SerializedName("isSubmitted")
    @Expose
    private boolean isSubmitted;

    @SerializedName("teacher")
    @Expose
    private UserMinimal teacherInformation;

    @SerializedName("assignedGroupDetails")
    @Expose
    private ArrayList<Group> groupDetail;
    @SerializedName("userMarks")
    @Expose
    private int userMarks;

    public String getHomeworkType() {
        return homeworkType;
    }

    public void setHomeworkType(String homeworkType) {
        this.homeworkType = homeworkType;
    }

    private String homeworkType;

    private String mAssignmentType;

    private String mAttachmentThumbnailUrl;

    private String mGradeId;

    private String mTopicId;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getStage() {
        return mStage;
    }

    public void setStage(String stage) {
        mStage = stage;
    }

    public String getAssignmentType() {
        return mAssignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        mAssignmentType = assignmentType;
    }

    public String getAssignmentTypeId() {
        return mAssignmentTypeId;
    }

    public void setAssignmentTypeId(String assignmentTypeId) {
        mAssignmentTypeId = assignmentTypeId;
    }

    public ArrayList<String> getAssignedGroupIds() {
        return mAssignedGroupIds;
    }

    public void setAssignedGroupIds(ArrayList<String> assignedGroups) {
        mAssignedGroupIds = assignedGroups;
    }


    public int getAllowedDuration() {
        return mAllowedDuration;
    }

    public void setAllowedDuration(int allowedDuration) {
        mAllowedDuration = allowedDuration;
    }

    public int getTotalScore() {
        return mTotalScore;
    }

    public void setTotalScore(int totalScore) {
        mTotalScore = totalScore;
    }

    public String getAttachmentType() {
        return mAttachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        mAttachmentType = attachmentType;
    }

    public String getAttachmentId() {
        return mAttachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        mAttachmentId = attachmentId;
    }

    public String getAssignmentDueDate() {
        return mAssignmentDueDate;
    }

    public void setAssignmentDueDate(String assignmentDueDate) {
        mAssignmentDueDate = assignmentDueDate;
    }

    public String getInstructions() {
        return mInstructions;
    }

    public void setInstructions(String instructions) {
        mInstructions = instructions;
    }

    public String getAssignedBy() {
        return mAssignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        mAssignedBy = assignedBy;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public boolean isTimed() {
        return mTimed;
    }

    public void setTimed(boolean timed) {
        mTimed = timed;
    }

    public String getAttachmentThumbnailUrl() {
        return mAttachmentThumbnailUrl;
    }

    public void setAttachmentThumbnailUrl(String attachmentThumbnailUrl) {
        mAttachmentThumbnailUrl = attachmentThumbnailUrl;
    }

    public String getGradeId() {
        return mGradeId;
    }

    public void setGradeId(String gradeId) {
        mGradeId = gradeId;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }

    public MetaInformation getMetaInformation() {
        return mMetaInformation;
    }

    public String getAssignedDateTime() {
        return mAssignedDateTime;
    }

    public String getHomeworkId() {
        return mHomeworkId;
    }

    public UserMinimal getTeacherInformation() {
        return teacherInformation;
    }

    public ArrayList<Group> getGroupDetail() {
        return groupDetail;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Homework)) {
            return false;
        }
        return this.getHomeworkId().equals(((Homework) object).getHomeworkId());
    }

    public int getUserMarks() {
        return userMarks;
    }

    @Override
    public int hashCode() {
        return this.getHomeworkId().hashCode();
    }
}
