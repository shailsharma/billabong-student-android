package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 15/11/19.
 */
public class RevisionConfigurationResponse implements Serializable {

    @SerializedName("revisionPoint")
    @Expose
    private int mRevisionPoint = -1;

    public int getRevisionPoint() {
        return mRevisionPoint;
    }

}
