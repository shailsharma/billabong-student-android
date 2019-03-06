package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MindSparkUnitData implements Serializable {

    @SerializedName("unitID")
    @Expose
    private String mUnitId;

    @SerializedName("unitName")
    @Expose
    private String mUnitName;

    public String getUnitId() {
        return mUnitId;
    }

    public void setUnitId(String unitId) {
        mUnitId = unitId;
    }

    public String getUnitName() {
        return mUnitName;
    }

    public void setUnitName(String unitName) {
        mUnitName = unitName;
    }
}
