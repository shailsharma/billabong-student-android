package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Assignment;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj on 23-11-2016.
 */
public class LoadPendingAssignmentListTeacher {

    public ArrayList<Assignment> getAssignments() {
        return mAssignments;
    }

    private final ArrayList<Assignment> mAssignments;

    public LoadPendingAssignmentListTeacher(ArrayList<Assignment> assignments) {
        mAssignments = assignments;
    }
}
