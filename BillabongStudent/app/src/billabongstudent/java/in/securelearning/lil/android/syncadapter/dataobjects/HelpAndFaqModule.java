package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Rajat Jain on 14/10/19.
 */
public class HelpAndFaqModule implements Serializable {

    @SerializedName("module")
    @Expose
    private String mModule;

    @SerializedName("videoLink")
    @Expose
    private String mVideoLink;

    @SerializedName("videoDuration")
    @Expose
    private int mVideoDuration;

    @SerializedName("thumbnailUrl")
    @Expose
    private String mThumbnailUrl;

    @SerializedName("faq")
    @Expose
    private ArrayList<FAQuestionAnswer> mFAQuestionAnswerList;

    public String getModule() {
        return mModule;
    }

    public void setModule(String module) {
        mModule = module;
    }

    public String getVideoLink() {
        return mVideoLink;
    }

    public void setVideoLink(String videoLink) {
        mVideoLink = videoLink;
    }

    public int getVideoDuration() {
        return mVideoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        mVideoDuration = videoDuration;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public ArrayList<FAQuestionAnswer> getFAQuestionAnswerList() {
        return mFAQuestionAnswerList;
    }

    public void setFAQuestionAnswerList(ArrayList<FAQuestionAnswer> FAQuestionAnswerList) {
        mFAQuestionAnswerList = FAQuestionAnswerList;
    }

}
