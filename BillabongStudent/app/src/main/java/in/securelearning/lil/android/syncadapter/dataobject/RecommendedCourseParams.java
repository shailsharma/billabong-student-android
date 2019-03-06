package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class RecommendedCourseParams {

    public RecommendedCourseParams() {

    }

    public RecommendedCourseParams(int skip, int limit, String grade, ArrayList<String> subject, String topic, String[] courseType, String[] featureType) {
        this(skip, limit, grade, subject, topic);
        this.mCourseType = courseType;
        this.mFeatureType = featureType;
    }

    public RecommendedCourseParams(int skip, int limit, String grade, ArrayList<String> subject, String topic) {
        mSkip = skip;
        mLimit = limit;
        mGrade = grade;
        mSubject = subject;
        mTopic = topic;
    }

    public RecommendedCourseParams(String grade, ArrayList<String> subject, String topic) {
        mGrade = grade;
        mSubject = subject;
        mTopic = topic;
    }

    @SerializedName("skip")
    @Expose
    private int mSkip = 0;

    @SerializedName("limit")
    @Expose
    private int mLimit = 20;

    @SerializedName("gradeId")
    @Expose
    private String mGrade = "";

    @SerializedName("subjectId")
    @Expose
    private ArrayList<String> mSubject = null;

    @SerializedName("courseType")
    @Expose
    private String[] mCourseType = null;

    @SerializedName("featuredType")
    @Expose
    private String[] mFeatureType = null;

    @SerializedName("topicId")
    @Expose
    private String mTopic = "";

    public String getGrade() {
        return mGrade;
    }

    public void setGrade(String grade) {
        mGrade = grade;
    }

    public String[] getCourseType() {
        return mCourseType;
    }

    public void setCourseType(String[] courseType) {
        mCourseType = courseType;
    }

    public String[] getFeatureType() {
        return mFeatureType;
    }

    public void setFeatureType(String[] featureType) {
        mFeatureType = featureType;
    }

    public ArrayList<String> getSubject() {
        return mSubject;
    }

    public void setSubject(ArrayList<String> subject) {
        mSubject = subject;
    }

    public String getTopic() {
        return mTopic;
    }

    public void setTopic(String topic) {
        mTopic = topic;
    }

    public int getSkip() {
        return mSkip;
    }

    public void setSkip(int skip) {
        mSkip = skip;
    }

    public int getLimit() {
        return mLimit;
    }

    public void setLimit(int limit) {
        mLimit = limit;
    }

}
