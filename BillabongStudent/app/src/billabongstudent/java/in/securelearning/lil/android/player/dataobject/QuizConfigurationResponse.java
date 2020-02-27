package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuizConfigurationResponse implements Serializable {

    @SerializedName("afl")
    @Expose
    private boolean mIsAFL;

    @SerializedName("aol")
    @Expose
    private boolean mIsAOL;

    @SerializedName("aflPoints")
    @Expose
    private int mAFLPoints;

    @SerializedName("aolPoints")
    @Expose
    private int mAOLPoints;

    public boolean isAFL() {
        return mIsAFL;
    }

    public boolean isAOL() {
        return mIsAOL;
    }

    public int getAFLPoints() {
        return mAFLPoints;
    }

    public int getAOLPoints() {
        return mAOLPoints;
    }
}
