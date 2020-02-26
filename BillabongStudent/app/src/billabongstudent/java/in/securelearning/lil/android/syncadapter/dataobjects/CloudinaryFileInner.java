package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.securelearning.lil.android.base.dataobjects.Thumbnail;

/**
 * Created by Prabodh Dhabaria on 06-04-2017.
 */

public class CloudinaryFileInner extends Thumbnail{

    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("original_filename")
    @Expose
    private String originalFilename;

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
}
