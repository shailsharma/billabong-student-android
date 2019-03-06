package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class SearchPeriodicEventsFilterParams {
    @SerializedName("startTime")
    @Expose
    private String mStartTime = "";
    @SerializedName("endTime")
    @Expose
    private String mEndTime = "";

//    @SerializedName("skip")
//    @Expose
//    private int mSkip = 0;
//
//    @SerializedName("limit")
//    @Expose
//    private int mLimit = 20;
//
//    public int getSkip() {
//        return mSkip;
//    }
//
//    public void setSkip(int skip) {
//        mSkip = skip;
//    }
//
//    public int getLimit() {
//        return mLimit;
//    }
//
//    public void setLimit(int limit) {
//        mLimit = limit;
//    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }
}
