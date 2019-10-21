package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 31/8/19.
 */
public class UserChallengePost implements Serializable {

    @SerializedName("id")
    @Expose
    private String mId;// for challenge id

    @SerializedName("challangeDetail")
    @Expose
    private ChallengeDetail mChallengeDetail;

    public String getId() {
        return mId;
    }

    public void setChallengeId(String id) {
        mId = id;
    }

    public ChallengeDetail getChallengeDetail() {
        return mChallengeDetail;
    }

    public void setChallengeDetail(ChallengeDetail challengeDetail) {
        mChallengeDetail = challengeDetail;
    }
}
