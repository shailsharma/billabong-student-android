package in.securelearning.lil.android.analytics.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EffortBarChartDataWeekly implements Serializable {

    @SerializedName("weekNo")
    @Expose
    private int weekNo;

    @SerializedName("minutes")
    @Expose
    private float minutes;

    public int getWeekNo() {
        return weekNo;
    }

    public void setWeekNo(int weekNo) {
        this.weekNo = weekNo;
    }

    public float getMinutes() {
        return minutes;
    }

    public void setMinutes(float minutes) {
        this.minutes = minutes;
    }
}
