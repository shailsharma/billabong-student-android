package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Permission implements Serializable {
    @SerializedName("module")
    @Expose
    private String mModule;
    @SerializedName("label")
    @Expose
    private String mLabel;
    @SerializedName("status")
    @Expose
    private boolean mStatus;

    public Permission() {
    }

    public Permission(String module, String label, boolean status) {
        mModule = module;
        mLabel = label;
        mStatus = status;
    }

    public String getModule() {
        return mModule;
    }

    public void setModule(String module) {
        mModule = module;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }
}