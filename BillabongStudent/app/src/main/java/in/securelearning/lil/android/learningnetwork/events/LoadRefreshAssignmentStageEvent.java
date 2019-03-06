package in.securelearning.lil.android.learningnetwork.events;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadRefreshAssignmentStageEvent {
    private final AssignmentResponse mAssignmentResponse;

    public LoadRefreshAssignmentStageEvent(AssignmentResponse assignmentResponse) {
        this.mAssignmentResponse = assignmentResponse;
    }

    public AssignmentResponse getmAssignmentResponse() {
        return mAssignmentResponse;
    }

}
