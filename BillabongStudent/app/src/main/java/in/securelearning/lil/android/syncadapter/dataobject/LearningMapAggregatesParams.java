package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Prabodh Dhabaria on 31-07-2017.
 */

public class LearningMapAggregatesParams {
//    @SerializedName("skip")
//    @Expose
    private int mSkip = 0;
//
//    @SerializedName("limit")
//    @Expose
    private int mLimit = 20;

    @SerializedName("gradeId")
    @Expose
    private String mGrade = "";

    @SerializedName("subjectId")
    @Expose
    private ArrayList<String> mSubject = null;
    @SerializedName("sectionId")
    @Expose
    private String mSection = "";

    @SerializedName("topicId")
    @Expose
    private String mTopic = "";

    public LearningMapAggregatesParams(int skip, int limit, String grade, ArrayList<String> subject, String section, String topic) {
        mSkip = skip;
        mLimit = limit;
        mGrade = grade;
        mSubject = subject;
        mSection = section;
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

    public String getGrade() {
        return mGrade;
    }

    public void setGrade(String grade) {
        mGrade = grade;
    }

    public ArrayList<String> getSubject() {
        return mSubject;
    }

    public void setSubject(ArrayList<String> subject) {
        mSubject = subject;
    }

    public String getSection() {
        return mSection;
    }

    public void setSection(String section) {
        mSection = section;
    }

    public String getTopic() {
        return mTopic;
    }

    public void setTopic(String topic) {
        mTopic = topic;
    }
}
