package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Chaitendra on 23-Dec-17.
 */

public class MasteryRequestObject implements Serializable {

    @SerializedName("filter")
    @Expose
    private MasteryRequestObjectFilter mMasteryRequestObjectFilter;

    @SerializedName("skip")
    @Expose
    private int mSkip;

    @SerializedName("limit")
    @Expose
    private int mLimit;

    public MasteryRequestObjectFilter getMasteryRequestObjectFilter() {
        return mMasteryRequestObjectFilter;
    }

    public void setMasteryRequestObjectFilter(MasteryRequestObjectFilter masteryRequestObjectFilter) {
        mMasteryRequestObjectFilter = masteryRequestObjectFilter;
    }

    public int getSkip() {
        return mSkip;
    }

    public void setSkip(int skip) {
        mSkip = skip;
    }

    public int getLimit() {
        return mLimit;
    }

    public void setLimit(int limit) {
        mLimit = limit;
    }
}
