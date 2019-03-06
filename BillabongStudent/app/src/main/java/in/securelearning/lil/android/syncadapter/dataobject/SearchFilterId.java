package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Chaitendra on 10-Jun-17.
 */

public class SearchFilterId extends SearchFilter {

    @SerializedName("id")
    @Expose
    private String mId = "";

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = mId;
    }
}
