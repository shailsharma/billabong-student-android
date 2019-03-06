package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Question;

/**
 * Created by Chaitendra on 23-Dec-17.
 */

public class MasteryResponseObject implements Serializable {

    @SerializedName("low")
    @Expose
    private ArrayList<Question> mLowQuestionArrayList;

    @SerializedName("medium")
    @Expose
    private ArrayList<Question> mMediumQuestionArrayList;

    @SerializedName("hard")
    @Expose
    private ArrayList<Question> mHardQuestionArrayList;

    public ArrayList<Question> getLowQuestionArrayList() {
        return mLowQuestionArrayList;
    }

    public void setLowQuestionArrayList(ArrayList<Question> lowQuestionArrayList) {
        mLowQuestionArrayList = lowQuestionArrayList;
    }

    public ArrayList<Question> getMediumQuestionArrayList() {
        return mMediumQuestionArrayList;
    }

    public void setMediumQuestionArrayList(ArrayList<Question> mediumQuestionArrayList) {
        mMediumQuestionArrayList = mediumQuestionArrayList;
    }

    public ArrayList<Question> getHardQuestionArrayList() {
        return mHardQuestionArrayList;
    }

    public void setHardQuestionArrayList(ArrayList<Question> hardQuestionArrayList) {
        mHardQuestionArrayList = hardQuestionArrayList;
    }
}
