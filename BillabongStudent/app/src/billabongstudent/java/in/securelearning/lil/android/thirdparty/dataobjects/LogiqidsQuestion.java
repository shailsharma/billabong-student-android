package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LogiqidsQuestion implements Serializable {

    @SerializedName("question_id")
    @Expose
    private int mQuestionId;

    @SerializedName("question_body")
    @Expose
    private LogiqidsTextImageObject mLogiqidsTextImageObject;

    @SerializedName("options")
    @Expose
    private ArrayList<LogiqidsTextImageObject> mChoiceList;

    public int getQuestionId() {
        return mQuestionId;
    }

    public void setQuestionId(int questionId) {
        mQuestionId = questionId;
    }

    public LogiqidsTextImageObject getLogiqidsTextImageObject() {
        return mLogiqidsTextImageObject;
    }

    public void setLogiqidsTextImageObject(LogiqidsTextImageObject logiqidsTextImageObject) {
        mLogiqidsTextImageObject = logiqidsTextImageObject;
    }

    public ArrayList<LogiqidsTextImageObject> getChoiceList() {
        return mChoiceList;
    }

    public void setChoiceList(ArrayList<LogiqidsTextImageObject> choiceList) {
        mChoiceList = choiceList;
    }
}
