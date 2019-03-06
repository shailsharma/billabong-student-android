package in.securelearning.lil.android.learningnetwork.events;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadNewAssignmentDownloadEvent {
    private final AssignmentResponse mAssignmentResponse;

    public LoadNewAssignmentDownloadEvent(AssignmentResponse mAssignmentResponse) {
        this.mAssignmentResponse = mAssignmentResponse;
    }

    public AssignmentResponse getmAssignmentResponse() {
        return mAssignmentResponse;
    }

}
