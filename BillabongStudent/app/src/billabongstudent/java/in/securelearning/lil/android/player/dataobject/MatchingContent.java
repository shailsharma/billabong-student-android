package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MatchingContent implements Serializable {

    @SerializedName("entity")
    @Expose
    private String mEntity;

    @SerializedName("placeholder")
    @Expose
    private String mPlaceHolder;

    @SerializedName("url")
    @Expose
    private String mUrl;

    private int mId;

    private int mColor;

    public String getEntity() {
        return mEntity;
    }

    public void setEntity(String entity) {
        mEntity = entity;
    }

    public String getPlaceHolder() {
        return mPlaceHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        mPlaceHolder = placeHolder;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
