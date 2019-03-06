package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;


/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj 020-05-2016.
 */
public class LoadSubmittedAssignmentResponseListStudent {
    public ArrayList<AssignmentResponse> getAsiAssignmentResponse() {
        return mAssignments;
    }

    private final ArrayList<AssignmentResponse> mAssignments;

    public LoadSubmittedAssignmentResponseListStudent(ArrayList<AssignmentResponse> assignments) {
        this.mAssignments = assignments;
    }
}
