package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*To send total earned points data*/
public class TotalPointPost implements Serializable {

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    @SerializedName("totalPoints")
    @Expose
    private int mTotalPoints;

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public int getTotalPoints() {
        return mTotalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        mTotalPoints = totalPoints;
    }
}
