
package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SkillMasteryQuestionPostData implements Serializable {

    @SerializedName("filter")
    @Expose
    private SkillMasteryFilter mFilter;
    @SerializedName("skip")
    @Expose
    private Integer mSkip;
    @SerializedName("limit")
    @Expose
    private Integer mLimit;

    public SkillMasteryFilter getFilter() {
        return mFilter;
    }

    public void setFilter(SkillMasteryFilter filter) {
        this.mFilter = filter;
    }

    public Integer getSkip() {
        return mSkip;
    }

    public void setSkip(Integer skip) {
        this.mSkip = skip;
    }

    public Integer getLimit() {
        return mLimit;
    }

    public void setLimit(Integer limit) {
        this.mLimit = limit;
    }

}
