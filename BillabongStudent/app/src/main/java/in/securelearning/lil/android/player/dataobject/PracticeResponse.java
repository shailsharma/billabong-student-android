package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.QuestionResponse;

public class PracticeResponse implements Serializable {

    @SerializedName("skillId")
    @Expose
    private String mSkillId;

    @SerializedName("complexityLevel")
    @Expose
    private String mComplexityLevel;

    @SerializedName("questionResponses")
    @Expose
    private ArrayList<QuestionResponse> mQuestionResponseList;

    @SerializedName("streakSize")
    @Expose
    private ArrayList<Integer> mStreakList;

    @SerializedName("currentStreak")
    @Expose
    private int mCurrentStreak;

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

    public ArrayList<QuestionResponse> getQuestionResponseList() {
        return mQuestionResponseList;
    }

    public void setQuestionResponseList(ArrayList<QuestionResponse> questionResponseList) {
        mQuestionResponseList = questionResponseList;
    }

    public ArrayList<Integer> getStreakList() {
        return mStreakList;
    }

    public void setStreakList(ArrayList<Integer> streakList) {
        mStreakList = streakList;
    }

    public int getCurrentStreak() {
        return mCurrentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        mCurrentStreak = currentStreak;
    }
}
