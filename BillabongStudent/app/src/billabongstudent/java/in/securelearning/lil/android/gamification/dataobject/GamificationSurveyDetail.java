package in.securelearning.lil.android.gamification.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GamificationSurveyDetail implements Serializable {

    @SerializedName("QuestionText")
    @Expose
    private String QuestionText;

    @SerializedName("Option1")
    @Expose
    private String Option1;

    @SerializedName("Option2")
    @Expose
    private String Option2;

    @SerializedName("Option3")
    @Expose
    private String Option3;

    @SerializedName("Option4")
    @Expose
    private String Option4;

    @SerializedName("selectedOption")
    @Expose
    private String selectedOption;

    public String getQuestionText() {
        return QuestionText;
    }

    public void setQuestionText(String questionText) {
        QuestionText = questionText;
    }

    public String getOption1() {
        return Option1;
    }

    public void setOption1(String option1) {
        Option1 = option1;
    }

    public String getOption2() {
        return Option2;
    }

    public void setOption2(String option2) {
        Option2 = option2;
    }

    public String getOption3() {
        return Option3;
    }

    public void setOption3(String option3) {
        Option3 = option3;
    }

    public String getOption4() {
        return Option4;
    }

    public void setOption4(String option4) {
        Option4 = option4;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}
