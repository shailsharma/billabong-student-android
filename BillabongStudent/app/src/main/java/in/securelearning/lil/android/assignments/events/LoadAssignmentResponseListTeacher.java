package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;

/**
 * Created by Prabodh Dhabaria on 30-05-2016.
 */
public class LoadAssignmentResponseListTeacher {

    public ArrayList<AssignmentResponse> getAssignmentResponses() {
        return mAssignmentResponses;
    }


    private final ArrayList<AssignmentResponse> mAssignmentResponses;

    public LoadAssignmentResponseListTeacher( ArrayList<AssignmentResponse> assignmentResponses) {
        this.mAssignmentResponses = assignmentResponses;
    }
}
