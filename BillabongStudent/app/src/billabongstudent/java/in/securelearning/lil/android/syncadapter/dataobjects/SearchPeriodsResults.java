package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.PeriodEvent;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class SearchPeriodsResults {

    @SerializedName("results")
    @Expose
    private ArrayList<PeriodNew> mList = new ArrayList<>();

    @SerializedName("total")
    @Expose
    private int totalResult = 0;

    public int getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public ArrayList<PeriodNew> getList() {
        return mList;
    }

    public void setList(ArrayList<PeriodNew> list) {
        mList = list;
    }

}
