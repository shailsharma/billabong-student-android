package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.Resource;

/**
 * Created by Rajat Jain on 31/8/19.
 */
public class VideoForDay implements Serializable {

    @SerializedName("views")
    @Expose
    private int mViews;

    @SerializedName("resources")
    @Expose
    private Resource mVideoResource;

    @SerializedName("subject")
    @Expose
    private IdNameObject mSubject;

    @SerializedName("isViewed")
    @Expose
    private boolean mIsViewed;


    public int getViews() {
        return mViews;
    }

    public void setViews(int views) {
        mViews = views;
    }

    public Resource getVideoResource() {
        return mVideoResource;
    }

    public void setVideoResource(Resource videoResource) {
        mVideoResource = videoResource;
    }

    public IdNameObject getSubject() {
        return mSubject;
    }

    public void setSubject(IdNameObject subject) {
        mSubject = subject;
    }

    public boolean isViewed() {
        return mIsViewed;
    }

    public void setViewed(boolean viewed) {
        mIsViewed = viewed;
    }

}
