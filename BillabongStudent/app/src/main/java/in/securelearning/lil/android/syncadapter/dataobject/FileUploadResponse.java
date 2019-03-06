package in.securelearning.lil.android.syncadapter.dataobject;

import android.provider.MediaStore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Prabodh Dhabaria on 06-04-2017.
 */

public class FileUploadResponse implements Serializable {

    @SerializedName("files")
    @Expose
    private FilesList files;
    @SerializedName("fields")
    @Expose
    private FileFields fields;
    @SerializedName("cloudinary")
    @Expose
    private CloudinaryFile cloudinary;

    public FilesList getFiles() {
        return files;
    }

    public void setFiles(FilesList files) {
        this.files = files;
    }

    public FileFields getFields() {
        return fields;
    }

    public void setFields(FileFields fields) {
        this.fields = fields;
    }

    public CloudinaryFile getCloudinary() {
        return cloudinary;
    }

    public void setCloudinary(CloudinaryFile cloudinary) {
        this.cloudinary = cloudinary;
    }
}
