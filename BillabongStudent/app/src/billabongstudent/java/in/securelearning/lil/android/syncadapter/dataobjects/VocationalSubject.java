package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocationalSubject implements Serializable {


    @SerializedName("_id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mSubjectName;

    @SerializedName("thumbnailUrl")
    @Expose
    private String mThumbnailUrl;


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getSubjectName() {
        return mSubjectName;
    }

    public void setSubjectName(String subjectName) {
        mSubjectName = subjectName;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }
}
