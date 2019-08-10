package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*To send total earned points data*/
public class TotalPointResponse implements Serializable {

    @SerializedName("isTransactionCreated")
    @Expose
    private boolean mIsTransactionCreated;

    @SerializedName("message")
    @Expose
    private String mMessage;

    @SerializedName("points")
    @Expose
    private int mPoints;

    public boolean isTransactionCreated() {
        return mIsTransactionCreated;
    }

    public void setTransactionCreated(boolean transactionCreated) {
        mIsTransactionCreated = transactionCreated;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }
}
