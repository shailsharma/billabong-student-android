package in.securelearning.lil.android.analytics.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BenchMarkPerformance implements Serializable {

        @SerializedName("time")
        @Expose
        private double benchMarkTime;

        @SerializedName("percentage")
        @Expose
        private double benchMarkPercentage;

        public double getBenchMarkTime() {
            return benchMarkTime;
        }

        public double getBenchMarkPercentage() {
            return benchMarkPercentage;
        }

        public void setBenchMarkTime(double benchMarkTime) {
            this.benchMarkTime = benchMarkTime;
        }

        public void setBenchMarkPercentage(double benchMarkPercentage) {
            this.benchMarkPercentage = benchMarkPercentage;
        }
    }