package in.securelearning.lil.android.homework.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AssignedHomeworkParent implements Serializable {

    @SerializedName("new")
    @Expose
    private AssignedHomework mNewStudentAssignment;

    @SerializedName("today")
    @Expose
    private AssignedHomework mTodayStudentAssignment;
    @SerializedName("upComing")
    @Expose
    private AssignedHomework mUpComingStudentAssignment;
    @SerializedName("overDue")
    @Expose
    private AssignedHomework mOverDueStudentAssignment;

    @SerializedName("submitted")
    @Expose
    private AssignedHomework mSubmittedAssignment;

    public AssignedHomework getSubmittedAssignment() {
        return mSubmittedAssignment;
    }

    private List<Homework> pendingAssignmentList;

    public List<Homework> getPendingAssignmentList() {
        return pendingAssignmentList;
    }

    public void setPendingAssignmentList(List<Homework> pendingAssignmentList) {
        this.pendingAssignmentList = pendingAssignmentList;
    }

    public AssignedHomework getNewStudentAssignment() {
        return mNewStudentAssignment;
    }

    public AssignedHomework getTodayStudentAssignment() {
        return mTodayStudentAssignment;
    }

    public AssignedHomework getUpComingStudentAssignment() {
        return mUpComingStudentAssignment;
    }

    public AssignedHomework getOverDueStudentAssignment() {
        return mOverDueStudentAssignment;
    }
    public class AssignedHomework implements Serializable
    {
        @SerializedName("list")
        @Expose
        private List<Homework> mAssignmentsList;

        @SerializedName("count")
        @Expose
        private int mCount;

        public List<Homework> getAssignmentsList() {
            return mAssignmentsList;
        }

        public int getCount() {
            return mCount;
        }
    }

}
