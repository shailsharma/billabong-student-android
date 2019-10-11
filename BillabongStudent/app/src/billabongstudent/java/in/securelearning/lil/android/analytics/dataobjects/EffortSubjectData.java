package in.securelearning.lil.android.analytics.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EffortSubjectData implements Serializable {

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("thumbnail")
    @Expose
    private String mSubjectIcon;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


    public String getSubjectIcon() {
        return mSubjectIcon;
    }

    public void setSubjectIcon(String subjectIcon) {
        mSubjectIcon = subjectIcon;
    }
}
