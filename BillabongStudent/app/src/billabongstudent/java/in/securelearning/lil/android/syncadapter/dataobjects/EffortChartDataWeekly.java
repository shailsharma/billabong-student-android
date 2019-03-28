package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EffortChartDataWeekly implements Serializable {

    @SerializedName("_id")
    @Expose
    private String mDate;

    @SerializedName("count")
    @Expose
    private float mTime;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public float getTime() {
        return mTime;
    }

    public void setTime(float time) {
        mTime = time;
    }
}
