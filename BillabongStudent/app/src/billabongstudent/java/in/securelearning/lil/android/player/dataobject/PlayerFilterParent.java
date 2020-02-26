package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlayerFilterParent implements Serializable {

    @SerializedName("filter")
    @Expose
    private PlayerFilter mPlayerFilter;

    public void setPlayerFilter(PlayerFilter playerFilter) {
        mPlayerFilter = playerFilter;
    }

    public PlayerFilter getPlayerFilter() {
        return mPlayerFilter;
    }
}
