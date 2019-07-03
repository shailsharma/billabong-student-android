package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Question;

/**
 * Created by Chaitendra on 03-Nov-17.
 */

public class SkillMasteryQuestionData implements Serializable {

    @SerializedName("skillId")
    @Expose
    private String mSkillId = null;
    @SerializedName("low")
    @Expose
    private SkillMasteryQuestionLevels mLowLevelQuestion = null;

    @SerializedName("medium")
    @Expose
    private SkillMasteryQuestionLevels mMediumLevelQuestion = null;

    @SerializedName("high")
    @Expose
    private SkillMasteryQuestionLevels mHighLevelQuestion = null;

    private transient ArrayList<Question>[] mQuestions = null;
    private transient int mQuestionsSize = 0;

    public String getSkillId() {
        return mSkillId;
    }

    public void setSkillId(String skillId) {
        mSkillId = skillId;
    }

    public int getQuestionsSize() {
        return mQuestionsSize;
    }

    public void setQuestionsSize(int questionsSize) {
        mQuestionsSize = questionsSize;
    }

    public ArrayList<Question>[] getQuestions() {
        return mQuestions;
    }

    public void setQuestions(ArrayList<Question>[] questions) {
        mQuestions = questions;
    }

    public SkillMasteryQuestionLevels getLowLevelQuestion() {
        return mLowLevelQuestion;
    }

    public void setLowLevelQuestion(SkillMasteryQuestionLevels lowLevelQuestion) {
        mLowLevelQuestion = lowLevelQuestion;
    }

    public SkillMasteryQuestionLevels getMediumLevelQuestion() {
        return mMediumLevelQuestion;
    }

    public void setMediumLevelQuestion(SkillMasteryQuestionLevels mediumLevelQuestion) {
        mMediumLevelQuestion = mediumLevelQuestion;
    }

    public SkillMasteryQuestionLevels getHighLevelQuestion() {
        return mHighLevelQuestion;
    }

    public void setHighLevelQuestion(SkillMasteryQuestionLevels highLevelQuestion) {
        mHighLevelQuestion = highLevelQuestion;
    }

    public void updateQuestions() {
        mQuestions = new ArrayList[]{
                mLowLevelQuestion == null || mLowLevelQuestion.getResults() == null ? new ArrayList<Question>() : mLowLevelQuestion.getResults(),
                mMediumLevelQuestion == null || mMediumLevelQuestion.getResults() == null ? new ArrayList<Question>() : mMediumLevelQuestion.getResults(),
                mHighLevelQuestion == null || mHighLevelQuestion.getResults() == null ? new ArrayList<Question>() : mHighLevelQuestion.getResults()};
    }
}
