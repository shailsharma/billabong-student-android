package in.securelearning.lil.android.analytics.dataobjects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.syncadapter.dataobjects.IdNameObject;

public class EffortChartData implements Serializable, Comparable<EffortChartData> {


    @SerializedName("_id")
    @Expose
    private String mId;

    @SerializedName("totalReadTimeSpent")
    @Expose
    private float mTotalReadTimeSpent;

    @SerializedName("totalPracticeTimeSpent")
    @Expose
    private float mTotalPracticeTimeSpent;

    @SerializedName("totalVideoTimeSpent")
    @Expose
    private float mTotalVideoTimeSpent;

    @SerializedName("totalTimeSpent")
    @Expose
    private float mTotalTimeSpent;

    @SerializedName("week")
    @Expose
    private int weekNo;

    @SerializedName("subject")
    @Expose
    private ArrayList<EffortSubjectData> mSubject;

    @SerializedName("topic")
    @Expose
    private ArrayList<IdNameObject> mTopic;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public float getTotalReadTimeSpent() {
        return mTotalReadTimeSpent;
    }

    public void setTotalReadTimeSpent(float totalReadTimeSpent) {
        mTotalReadTimeSpent = totalReadTimeSpent;
    }

    public float getTotalPracticeTimeSpent() {
        return mTotalPracticeTimeSpent;
    }

    public void setTotalPracticeTimeSpent(float totalPracticeTimeSpent) {
        mTotalPracticeTimeSpent = totalPracticeTimeSpent;
    }

    public float getTotalVideoTimeSpent() {
        return mTotalVideoTimeSpent;
    }

    public void setTotalVideoTimeSpent(float totalVideoTimeSpent) {
        mTotalVideoTimeSpent = totalVideoTimeSpent;
    }

    public float getTotalTimeSpent() {
        return mTotalTimeSpent;
    }

    public void setTotalTimeSpent(float totalTimeSpent) {
        mTotalTimeSpent = totalTimeSpent;
    }

    public ArrayList<EffortSubjectData> getSubject() {
        return mSubject;
    }

    public void setSubject(ArrayList<EffortSubjectData> subject) {
        mSubject = subject;
    }

    public ArrayList<IdNameObject> getTopic() {
        return mTopic;
    }

    public void setTopic(ArrayList<IdNameObject> topic) {
        mTopic = topic;
    }

    public int getWeekNo() {
        return weekNo;
    }

    @Override
    public int compareTo(@NonNull EffortChartData effortChartData) {
        if (this.getTotalTimeSpent() < effortChartData.getTotalTimeSpent()) return 1;
        if (this.getTotalTimeSpent() > effortChartData.getTotalTimeSpent()) return -1;
        return 0;
    }
}
