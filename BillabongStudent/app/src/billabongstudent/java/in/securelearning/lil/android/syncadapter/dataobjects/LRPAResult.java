package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LRPAResult implements Serializable {

    @SerializedName("results")
    @Expose
    private ArrayList<AboutCourseMinimal> mResults;

    @SerializedName("total")
    @Expose
    private int mTotal;

    public ArrayList<AboutCourseMinimal> getResults() {
        return mResults;
    }

    public void setResults(ArrayList<AboutCourseMinimal> results) {
        mResults = results;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }
}
