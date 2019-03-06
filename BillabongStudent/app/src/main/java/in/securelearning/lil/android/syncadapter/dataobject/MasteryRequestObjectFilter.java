package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MasteryRequestObjectFilter implements Serializable {

    @SerializedName("skillId")
    @Expose
    private String mSkillId;
    @SerializedName("skillIds")
    @Expose
    private ArrayList<String> mSkillIdList;

    @SerializedName("topicId")
    @Expose
    private String mTopicId;

    @SerializedName("complexityLevels")
    @Expose
    private ArrayList<String> mComplexityLevels;

    public String getSkillId() {
        return mSkillId;
    }

    public void setSkillId(String skillId) {
        mSkillId = skillId;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }

    public ArrayList<String> getComplexityLevels() {
        return mComplexityLevels;
    }

    public void setComplexityLevels(ArrayList<String> complexityLevels) {
        mComplexityLevels = complexityLevels;
    }

    public ArrayList<String> getSkillIdList() {
        return mSkillIdList;
    }

    public void setSkillIdList(ArrayList<String> skillIdList) {
        mSkillIdList = skillIdList;
    }
}