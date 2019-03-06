package in.securelearning.lil.android.quizpreview.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class AssignmentSubmittedEvent {
    private final AssignmentResponse mAssignmentResponse;

    public AssignmentSubmittedEvent(AssignmentResponse mAssignmentResponse) {
        this.mAssignmentResponse = mAssignmentResponse;
    }

    public AssignmentResponse getmAssignmentResponse() {
        return mAssignmentResponse;
    }
}
