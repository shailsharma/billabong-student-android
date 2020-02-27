package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Prabodh Dhabaria on 06-04-2017.
 */

public class CloudinaryFile {
    @SerializedName("file")
    @Expose
    private CloudinaryFileInner file;

    public CloudinaryFileInner getFile() {
        return file;
    }

    public void setFile(CloudinaryFileInner file) {
        this.file = file;
    }
}
