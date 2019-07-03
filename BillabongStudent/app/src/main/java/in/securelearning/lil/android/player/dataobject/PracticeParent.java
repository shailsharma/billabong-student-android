package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PracticeParent implements Serializable {

    @SerializedName("filter")
    @Expose
    private PracticeFilter mPracticeFilter;

    public PracticeFilter getPracticeFilter() {
        return mPracticeFilter;
    }

    public void setPracticeFilter(PracticeFilter practiceFilter) {
        mPracticeFilter = practiceFilter;
    }
}
