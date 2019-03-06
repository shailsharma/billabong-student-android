package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LessonPlanSubjectPost implements Serializable {


    @SerializedName("type")
    @Expose
    private String mType;

    public LessonPlanSubjectPost(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }
}
