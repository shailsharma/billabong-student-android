package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Question;

/**
 * Created by Chaitendra on 04-Nov-17.
 */

public class SkillMasteryQuestionLevels implements Serializable {
    @SerializedName("results")
    @Expose
    private ArrayList<Question> mResults = null;
    @SerializedName("total")
    @Expose
    private Integer mTotal;

    public SkillMasteryQuestionLevels() {

    }

    public SkillMasteryQuestionLevels(ArrayList<Question> results, Integer total) {
        mResults = results;
        mTotal = total;
    }

    public ArrayList<Question> getResults() {
        return mResults;
    }

    public void setResults(ArrayList<Question> results) {
        mResults = results;
    }

    public Integer getTotal() {
        return mTotal;
    }

    public void setTotal(Integer total) {
        mTotal = total;
    }
}
