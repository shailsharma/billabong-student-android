package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BonusConfigurationResponse implements Serializable {

    @SerializedName("flashBonus")
    @Expose
    private int mFlashBonus;

    @SerializedName("streakBonus")
    @Expose
    private int mStreakBonus;

    public int getFlashBonus() {
        return mFlashBonus;
    }

    public int getStreakBonus() {
        return mStreakBonus;
    }

}
