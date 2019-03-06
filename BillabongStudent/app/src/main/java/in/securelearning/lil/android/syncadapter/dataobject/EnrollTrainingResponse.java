package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Chaitendra on 30-Jan-18.
 */

public class EnrollTrainingResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private boolean mStatus;

    public boolean getStatus() {
        return mStatus;
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }
}
