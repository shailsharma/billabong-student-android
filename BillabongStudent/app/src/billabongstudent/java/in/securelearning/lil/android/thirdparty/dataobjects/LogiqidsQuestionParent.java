package in.securelearning.lil.android.thirdparty.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LogiqidsQuestionParent implements Serializable {

    @SerializedName("answered")
    @Expose
    private ArrayList<Boolean> mAnsweredList;

    @SerializedName("unanswered")
    @Expose
    private LogiqidsQuestion mQuestion;

    public ArrayList<Boolean> getAnsweredList() {
        return mAnsweredList;
    }

    public LogiqidsQuestion getQuestion() {
        return mQuestion;
    }
}
