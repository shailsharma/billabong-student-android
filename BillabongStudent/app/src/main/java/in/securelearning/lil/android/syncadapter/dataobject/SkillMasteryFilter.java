
package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SkillMasteryFilter implements Serializable {

    @SerializedName("skillId")
    @Expose
    private String skillId;
    @SerializedName("complexityLevels")
    @Expose
    private List<String> complexityLevels = null;

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public List<String> getComplexityLevels() {
        return complexityLevels;
    }

    public void setComplexityLevels(List<String> complexityLevels) {
        this.complexityLevels = complexityLevels;
    }

}
