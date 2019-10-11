package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.Thumbnail;

public class StudentProfilePicturePost implements Serializable {

    @SerializedName("id")
    @Expose
    private String mUserId;

    @SerializedName("userThumbnail")
    @Expose
    private Thumbnail mUserThumbnail;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public Thumbnail getUserThumbnail() {
        return mUserThumbnail;
    }

    public void setUserThumbnail(Thumbnail userThumbnail) {
        mUserThumbnail = userThumbnail;
    }
}