package in.securelearning.lil.android.analytics.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChartConfigurationData implements Serializable {

    @SerializedName("from")
    @Expose
    private float mFrom;

    @SerializedName("to")
    @Expose
    private float mTo;

    @SerializedName("label")
    @Expose
    private String mLabel;

    @SerializedName("colorCode")
    @Expose
    private String mColorCode;

    @SerializedName("status")
    @Expose
    private boolean mStatus;

    public float getFrom() {
        return mFrom;
    }

    public void setFrom(float from) {
        mFrom = from;
    }

    public float getTo() {
        return mTo;
    }

    public void setTo(float to) {
        mTo = to;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getColorCode() {
        return mColorCode;
    }

    public void setColorCode(String colorCode) {
        mColorCode = colorCode;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }
}
