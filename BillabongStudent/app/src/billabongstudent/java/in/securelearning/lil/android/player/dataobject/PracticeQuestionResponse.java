package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Question;

public class PracticeQuestionResponse implements Serializable {

    @SerializedName("isQuestionExist")
    @Expose
    private boolean mIsQuestionExist;

    @SerializedName("isUserThrowOut")
    @Expose
    private boolean mIsUserThrowOut;

    @SerializedName("skillId")
    @Expose
    private String mSkillId;

    @SerializedName("complexityLevel")
    @Expose
    private String mComplexityLevel;

    @SerializedName("questions")
    @Expose
    private ArrayList<Question> mQuestionList;

    public ArrayList<Question> getQuestionList() {
        return mQuestionList;
    }

    public void setQuestionList(ArrayList<Question> questionList) {
        mQuestionList = questionList;
    }

    public boolean isQuestionExist() {
        return mIsQuestionExist;
    }

    public void setQuestionExist(boolean questionExist) {
        mIsQuestionExist = questionExist;
    }

    public boolean isUserThrowOut() {
        return mIsUserThrowOut;
    }

    public void setUserThrowOut(boolean userThrowOut) {
        mIsUserThrowOut = userThrowOut;
    }

    public String getSkillId() {
        return mSkillId;
    }

    public void setSkillId(String skillId) {
        mSkillId = skillId;
    }

    public String getComplexityLevel() {
        return mComplexityLevel;
    }

    public void setComplexityLevel(String complexityLevel) {
        mComplexityLevel = complexityLevel;
    }
}
