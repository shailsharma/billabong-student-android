package in.securelearning.lil.android.profile.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileVideo implements Serializable {

    @SerializedName("url")
    @Expose
    private String mUrl;

    @SerializedName("urlThumbnail")
    @Expose
    private String mUrlThumbnail;

    @SerializedName("type")
    @Expose
    private String mType;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrlThumbnail() {
        return mUrlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        mUrlThumbnail = urlThumbnail;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }
}
