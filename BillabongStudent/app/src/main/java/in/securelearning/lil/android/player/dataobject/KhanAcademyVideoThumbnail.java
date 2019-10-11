package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KhanAcademyVideoThumbnail implements Serializable {

    @SerializedName("high")
    @Expose
    private Thumbnail mThumbnailHigh;

    @SerializedName("standard")
    @Expose
    private Thumbnail mThumbnailStandard;

    public Thumbnail getThumbnailHigh() {
        return mThumbnailHigh;
    }

    public Thumbnail getThumbnailStandard() {
        return mThumbnailStandard;
    }



    public class Thumbnail implements Serializable {

        @SerializedName("url")
        @Expose
        private String mUrl;

        public String getUrl() {
            return mUrl;
        }
    }
}