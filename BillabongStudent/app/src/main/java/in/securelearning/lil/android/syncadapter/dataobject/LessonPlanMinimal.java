package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.Thumbnail;

public class LessonPlanMinimal implements Serializable {

    /*this _id is objectId of Lesson Plan Configuration*/
    @SerializedName("_id")
    @Expose
    private String mId;

    /*this courseId is objectId of Lesson Plan*/
    @SerializedName("courseId")
    @Expose
    private String mCourseId;

    @SerializedName(value = "title", alternate = "courseTitle")
    @Expose
    private String mTitle;

    @SerializedName("description")
    @Expose
    private String mDescription;

    @SerializedName("subject")
    @Expose
    private String mSubject;

    @SerializedName("subjectColorCode")
    @Expose
    private String mSubjectColorCode;

    @SerializedName("grade")
    @Expose
    private String mGrade;

    @SerializedName("section")
    @Expose
    private String mSection;

    @SerializedName("startDate")
    @Expose
    private String mStartDate;

    @SerializedName("endDate")
    @Expose
    private String mEndDate;

    @SerializedName("order")
    @Expose
    private int mOrder;

    @SerializedName(value = "sessions", alternate = "totalSessions")
    @Expose
    private int mSessions;

    @SerializedName("rating")
    @Expose
    private float mRating;

    @SerializedName("thumbnail")
    @Expose
    private Thumbnail mThumbnail;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getCourseId() {
        return mCourseId;
    }

    public void setCourseId(String courseId) {
        mCourseId = courseId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getSubjectColorCode() {
        return mSubjectColorCode;
    }

    public void setSubjectColorCode(String subjectColorCode) {
        mSubjectColorCode = subjectColorCode;
    }

    public String getGrade() {
        return mGrade;
    }

    public void setGrade(String grade) {
        mGrade = grade;
    }

    public String getSection() {
        return mSection;
    }

    public void setSection(String section) {
        mSection = section;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public int getSessions() {
        return mSessions;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
    }

    public void setSessions(int sessions) {
        mSessions = sessions;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public Thumbnail getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        mThumbnail = thumbnail;
    }
}
