package in.securelearning.lil.android.analytics.dataobjects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PerformanceChartData implements Serializable, Comparable<PerformanceChartData> {

    @SerializedName("performance")
    @Expose
    private float mPerformance;

    @SerializedName("total")
    @Expose
    private float mTotal;

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("thumbnail")
    @Expose
    private String mSubjectIcon;

    public float getPerformance() {
        return mPerformance;
    }

    public void setPerformance(float performance) {
        mPerformance = performance;
    }

    public float getTotal() {
        return mTotal;
    }

    public void setTotal(float total) {
        mTotal = total;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


    public String getSubjectIcon() {
        return mSubjectIcon;
    }

    public void setSubjectIcon(String subjectIcon) {
        mSubjectIcon = subjectIcon;
    }

    @Override
    public int compareTo(@NonNull PerformanceChartData performanceChartData) {
        if (this.getPerformance() < performanceChartData.getPerformance()) return 1;
        if (this.getPerformance() > performanceChartData.getPerformance()) return -1;
        return 0;
    }
}
