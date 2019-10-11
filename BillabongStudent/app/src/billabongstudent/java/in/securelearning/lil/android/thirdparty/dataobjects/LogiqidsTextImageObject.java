package in.securelearning.lil.android.thirdparty.dataobjects;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogiqidsTextImageObject implements Serializable {

    @SerializedName("answer")
    @Expose
    private String mAnswer;

    @SerializedName("text")
    @Expose
    private String mText;

    @SerializedName("group_text")
    @Expose
    private String mGroupText;

    @SerializedName("image")
    @Expose
    private String mImageUrl;

    @SerializedName("group_image")
    @Expose
    private String mGroupImageUrl;

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
    }

    public String getText() {
        return mText;
    }

    public String getGroupText() {
        if (!TextUtils.isEmpty(mGroupText)) {
            return mGroupText + "<br/> <br/>" + mText;
        } else {
            return mText;
        }
    }

    public void setText(String text) {
        mText = text;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getGroupImageUrl() {
        return mGroupImageUrl;
    }
}
