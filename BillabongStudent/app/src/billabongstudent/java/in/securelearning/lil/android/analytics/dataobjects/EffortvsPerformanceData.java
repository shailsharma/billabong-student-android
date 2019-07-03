package in.securelearning.lil.android.analytics.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EffortvsPerformanceData implements Serializable {

    @SerializedName("id")
    @Expose
    private String mId;
    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("totalScore")
    @Expose
    private String mTotalScore;
    @SerializedName("percentage")
    @Expose
    private float mPercentage;

    @SerializedName("coverage")
    @Expose
    private Double mCoverage;

    @SerializedName("time")
    @Expose
    private TimeResponse mTimeResponseList;

    @SerializedName("thumbnailUrl")
    @Expose
    private String thumbnailUrl;

    public String getId() {
        return mId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getName() {
        return mName;
    }

    public String getTotalScore() {
        return mTotalScore;
    }

    public float getPercentage() {
        return mPercentage;
    }

    public Double getCoverage() {
        return mCoverage;
    }

    public TimeResponse getTimeResponseList() {
        return mTimeResponseList;
    }

    public class TimeResponse implements Serializable{

        @SerializedName("totalReadTimeSpent")
        @Expose
        private float mTotalReadTimeSpent;

        @SerializedName("totalPracticeTimeSpent")
        @Expose
        private float mTotalPracticeTimeSpent;

        @SerializedName("totalVideoTimeSpent")
        @Expose
        private float mTotalVideoTimeSpent;

        @SerializedName("activeDays")
        @Expose
        private int mActiveDays;

        public float getTotalReadTimeSpent() {
            return mTotalReadTimeSpent;
        }

        public float getTotalPracticeTimeSpent() {
            return mTotalPracticeTimeSpent;
        }

        public float getTotalVideoTimeSpent() {
            return mTotalVideoTimeSpent;
        }

        public int getActiveDays() {
            return mActiveDays;
        }

        public float getTotalTime()
        {
            return (getTotalReadTimeSpent()+getTotalPracticeTimeSpent()+getTotalVideoTimeSpent())/60;
        }

        public float getAvgDaily()
        {

            if(getTotalTime()!=0f && getActiveDays()!=0)
            {

                return getTotalTime()/getActiveDays();
            }
            return 0f;
        }
    }
}
