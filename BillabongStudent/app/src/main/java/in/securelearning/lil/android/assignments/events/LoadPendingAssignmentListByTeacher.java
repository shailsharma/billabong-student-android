package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Assignment;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj on 23-11-2016.
 */
public class LoadPendingAssignmentListByTeacher {

    public ArrayList<Assignment> getTodayAssignedAssignments() {
        return mAssignments;
    }
    public ArrayList<Assignment> getTeachersAllAssignedAssignments() {
        return allAssignedAssignments;
    }

    private final ArrayList<Assignment> mAssignments,allAssignedAssignments;

    public LoadPendingAssignmentListByTeacher(ArrayList<Assignment> assignments,ArrayList<Assignment> allAssignments) {
        this.mAssignments = assignments;
        this.allAssignedAssignments = allAssignments;
    }
}
