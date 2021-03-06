package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkTopicData implements Serializable {

    @SerializedName("unitList")
    @Expose
    private ArrayList<in.securelearning.lil.android.thirdparty.dataobjects.MindSparkUnitData> mMindSparkUnitList;

    public ArrayList<in.securelearning.lil.android.thirdparty.dataobjects.MindSparkUnitData> getMindSparkUnitList() {
        return mMindSparkUnitList;
    }

    public void setMindSparkUnitList(ArrayList<in.securelearning.lil.android.thirdparty.dataobjects.MindSparkUnitData> mindSparkUnitList) {
        mMindSparkUnitList = mindSparkUnitList;
    }
}
