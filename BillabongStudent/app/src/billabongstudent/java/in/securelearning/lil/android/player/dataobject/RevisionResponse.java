package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.syncadapter.dataobjects.IdNameObject;

public class RevisionResponse implements Serializable {

    @SerializedName("questions")
    @Expose
    private ArrayList<Question> mQuestionList;

    @SerializedName("currentTopic")
    @Expose
    private IdNameObject mCurrentTopic;

    @SerializedName("nextTopic")
    @Expose
    private IdNameObject mNextTopic;

    @SerializedName("result")
    @Expose
    private RevisionResult mRevisionResult;


    public ArrayList<Question> getQuestionList() {
        return mQuestionList;
    }

    public void setQuestionList(ArrayList<Question> questionList) {
        mQuestionList = questionList;
    }

    public IdNameObject getCurrentTopic() {
        return mCurrentTopic;
    }

    public void setCurrentTopic(IdNameObject currentTopic) {
        mCurrentTopic = currentTopic;
    }

    public IdNameObject getNextTopic() {
        return mNextTopic;
    }

    public void setNextTopic(IdNameObject nextTopic) {
        mNextTopic = nextTopic;
    }

    public void setRevisionResult(RevisionResult revisionResult) {
        mRevisionResult = revisionResult;
    }
}
