package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WikiHow implements Serializable {

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("title")
    @Expose
    private String mTitle;

    @SerializedName("image")
    @Expose
    private WikiHowThumbnail mThumbnail;

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public WikiHowThumbnail getThumbnail() {
        return mThumbnail;
    }
}
