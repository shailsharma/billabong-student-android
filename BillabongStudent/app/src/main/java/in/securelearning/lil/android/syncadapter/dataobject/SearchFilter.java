package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Chaitendra on 10-Jun-17.
 */

public class SearchFilter {

    @SerializedName("name")
    @Expose
    private String mName = "";

    @SerializedName("count")
    @Expose
    private int mCount = 0;

    public SearchFilter() {
    }

    public SearchFilter(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }
}
