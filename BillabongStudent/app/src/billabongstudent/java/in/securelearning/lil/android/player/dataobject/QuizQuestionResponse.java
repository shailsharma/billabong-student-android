package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Question;

public class QuizQuestionResponse implements Serializable {

    @SerializedName("questions")
    @Expose
    private ArrayList<Question> mQuestionList;

    @SerializedName("quizTitle")
    @Expose
    private String mQuizTitle;

    @SerializedName("metaInformation")
    @Expose
    private MetaInformation mMetaInformation;


    public ArrayList<Question> getQuestionList() {
        return mQuestionList;
    }

    public void setQuestionList(ArrayList<Question> questionList) {
        mQuestionList = questionList;
    }

    public String getQuizTitle() {
        return mQuizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        mQuizTitle = quizTitle;
    }

    public MetaInformation getMetaInformation() {
        return mMetaInformation;
    }

    public void setMetaInformation(MetaInformation metaInformation) {
        mMetaInformation = metaInformation;
    }
}
