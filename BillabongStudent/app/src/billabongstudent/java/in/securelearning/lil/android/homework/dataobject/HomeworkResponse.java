package in.securelearning.lil.android.homework.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.syncadapter.dataobjects.UserMinimal;
/* in this class getting pending and submitted student score and submitted date
* we are getting student name ect so we are extend userprofile class*/

public class  HomeworkResponse extends UserMinimal implements Serializable {
    @SerializedName("assignedDateTime")
    @Expose
    private String mAssignedDateTime;
    @SerializedName("assignmentDueDate")
    @Expose
    private String mAssignedDueDate;
    @SerializedName("maxScore")
    @Expose
    private int mMaxScore;

    @SerializedName("totalQuestion")
    @Expose
    private int mTotalQuestion;

    @SerializedName("totalPercent")
    @Expose
    private int mTotalPercent;

    @SerializedName("date")
    @Expose
    private String mSubmittedDate;


    public String getAssignedDateTime() {
        return mAssignedDateTime;
    }

    public String getAssignedDueDate() {
        return mAssignedDueDate;
    }

    public int getMaxScore() {
        return mMaxScore;
    }

    public int getTotalQuestion() {
        return mTotalQuestion;
    }

    public int getTotalPercent() {
        return mTotalPercent;
    }

    public String getSubmittedDate() {
        return mSubmittedDate;
    }

    public void setTotalPercent(int totalPercent) {
        mTotalPercent = totalPercent;
    }
}
