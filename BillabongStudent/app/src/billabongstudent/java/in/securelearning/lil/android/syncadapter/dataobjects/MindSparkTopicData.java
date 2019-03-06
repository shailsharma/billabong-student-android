package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkTopicData implements Serializable {

    @SerializedName("unitList")
    @Expose
    private ArrayList<MindSparkUnitData> mMindSparkUnitList;

    public ArrayList<MindSparkUnitData> getMindSparkUnitList() {
        return mMindSparkUnitList;
    }

    public void setMindSparkUnitList(ArrayList<MindSparkUnitData> mindSparkUnitList) {
        mMindSparkUnitList = mindSparkUnitList;
    }
}
