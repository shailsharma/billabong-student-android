package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 31/8/19.
 */
public class LogiQidsChallengeParent implements Serializable {

    @SerializedName("views")
    @Expose
    private int mViews;

    @SerializedName("result")
    @Expose
    private LogiQidsChallenge mLogiQidsChallenge;

    public int getViews() {
        return mViews;
    }

    public void setViews(int views) {
        mViews = views;
    }

    public LogiQidsChallenge getLogiQidsChallenge() {
        return mLogiQidsChallenge;
    }

    public void setLogiQidsChallenge(LogiQidsChallenge logiQidsChallenge) {
        mLogiQidsChallenge = logiQidsChallenge;
    }
}
