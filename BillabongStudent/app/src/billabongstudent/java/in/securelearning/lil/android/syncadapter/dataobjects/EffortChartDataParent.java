package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EffortChartDataParent implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<EffortChartData> mEffortChartDataList;

    @SerializedName("daysCount")
    @Expose
    private int mDaysCount;

    public ArrayList<EffortChartData> getEffortChartDataList() {
        return mEffortChartDataList;
    }

    public void setEffortChartDataList(ArrayList<EffortChartData> effortChartDataList) {
        mEffortChartDataList = effortChartDataList;
    }

    public int getDaysCount() {
        return mDaysCount;
    }

    public void setDaysCount(int daysCount) {
        mDaysCount = daysCount;
    }
}
