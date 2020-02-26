package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KhanAcademyVideoDetail implements Serializable {

    @SerializedName("id")
    @Expose
    private String mVideoId;//Youtube video id

    @SerializedName("snippet")
    @Expose
    private KhanAcademyVideoSnippet mSnippet;

    public String getVideoId() {
        return mVideoId;
    }

    public KhanAcademyVideoSnippet getSnippet() {
        return mSnippet;
    }
}
