package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.Thumbnail;

public class KhanAcademyVideoSnippet implements Serializable {

    @SerializedName("thumbnails")
    @Expose
    private KhanAcademyVideoThumbnail mThumbnail;

    public KhanAcademyVideoThumbnail getThumbnail() {
        return mThumbnail;
    }
}

