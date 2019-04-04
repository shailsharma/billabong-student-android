package in.securelearning.lil.android.analytics.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.syncadapter.dataobject.IdNameObject;

public class EffortChartData implements Serializable {

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

    @SerializedName("subject")
    @Expose
    private ArrayList<IdNameObject> mSubject;

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

    public ArrayList<IdNameObject> getSubject() {
        return mSubject;
    }

    public void setSubject(ArrayList<IdNameObject> subject) {
        mSubject = subject;
    }

    public ArrayList<IdNameObject> getTopic() {
        return mTopic;
    }

    public void setTopic(ArrayList<IdNameObject> topic) {
        mTopic = topic;
    }
}
