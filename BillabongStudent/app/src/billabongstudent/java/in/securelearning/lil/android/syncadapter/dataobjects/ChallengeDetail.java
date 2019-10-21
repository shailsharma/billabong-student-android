package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 31/8/19.
 */
public class ChallengeDetail implements Serializable {

    @SerializedName("type")
    @Expose
    private String mType;

    @SerializedName("id")
    @Expose
    private Object mId;

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Object getId() {
        return mId;
    }

    public void setIdChallengeForDay(int id) {
        mId = id;
    }

    public void setIdVideoForDay(String id) {
        mId = id;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }
}
