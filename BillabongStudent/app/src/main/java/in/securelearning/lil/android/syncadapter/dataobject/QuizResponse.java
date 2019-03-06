package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;

/**
 * Created by Chaitendra on 06-Nov-17.
 */

public class QuizResponse implements Serializable {

    @SerializedName("metaInformation")
    @Expose
    private MetaInformation mMetaInformation = new MetaInformation();

    @SerializedName("quizResponses")
    @Expose
    private ArrayList<QuestionResponse> mQuestionResponses = null;

    @SerializedName("quizId")
    @Expose
    private String mQuizId = null;

    public String getQuizId() {
        return mQuizId;
    }

    public void setQuizId(String quizId) {
        mQuizId = quizId;
    }

    public MetaInformation getMetaInformation() {
        return mMetaInformation;
    }

    public void setMetaInformation(MetaInformation metaInformation) {
        mMetaInformation = metaInformation;
    }

    public ArrayList<QuestionResponse> getQuestionResponses() {
        return mQuestionResponses;
    }

    public void setQuestionResponses(ArrayList<QuestionResponse> questionResponses) {
        this.mQuestionResponses = questionResponses;
    }
}
