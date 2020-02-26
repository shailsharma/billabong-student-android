package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KhanAcademyVideo implements Serializable {

    @SerializedName("kAVideoDetail")
    @Expose
    KhanAcademyVideoDetail mVideoDetails;

    public KhanAcademyVideoDetail getVideoDetails() {
        return mVideoDetails;
    }
}
