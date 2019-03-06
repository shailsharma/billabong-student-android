package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;


/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj 020-05-2016.
 */
public class LoadOverDueNdNewCountEvent {

    private final int overDueAssignmentCount;

    public int getDueAssignmentCount() {
        return dueAssignmentCount;
    }

    private final int dueAssignmentCount;

    public int getNewAssignmentCount() {
        return newAssignmentCount;
    }

    public int getOverDueAssignmentCount() {
        return overDueAssignmentCount;
    }

    private final int newAssignmentCount;

    public LoadOverDueNdNewCountEvent(int overDueAssignment,int newAssignment,int dueAssignment) {
        this.overDueAssignmentCount = overDueAssignment;
        this.newAssignmentCount = newAssignment;
        this.dueAssignmentCount = dueAssignment;
    }


}
