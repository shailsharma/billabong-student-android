package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WikiHowThumbnail implements Serializable {

    @SerializedName("url")
    @Expose
    private String mThumb;

    @SerializedName("large")
    @Expose
    private String mThumbLarge;

    public String getThumb() {
        return mThumb;
    }

    public String getThumbLarge() {
        return mThumbLarge;
    }
}
