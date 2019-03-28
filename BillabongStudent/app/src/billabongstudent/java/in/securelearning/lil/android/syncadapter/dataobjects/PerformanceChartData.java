package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PerformanceChartData implements Serializable {

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
}
