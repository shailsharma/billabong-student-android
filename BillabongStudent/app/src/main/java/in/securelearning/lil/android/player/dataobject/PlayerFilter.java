package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerFilter implements Serializable {

    @SerializedName("skillIds")
    @Expose
    private ArrayList<String> mSkillIdList;

    public ArrayList<String> getSkillIdList() {
        return mSkillIdList;
    }

    public void setSkillIdList(ArrayList<String> skillIdList) {
        mSkillIdList = skillIdList;
    }
}
