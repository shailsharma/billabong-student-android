package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;


/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj 020-05-2016.
 */
public class LoadTodaysSubmittedAssignmentResponseListEvent {
    public int getTodaysTurnedInCount() {
        return mTodaysTurnedIn;
    }
    public int getTotalTurnedInReponses() {
        return mTotalTurnedIn;
    }

    private final int mTotalTurnedIn,mTodaysTurnedIn;

    public LoadTodaysSubmittedAssignmentResponseListEvent(int  mTotalTurnedIn,int mTodaysTurnedIn) {
        this.mTotalTurnedIn=mTotalTurnedIn;
        this.mTodaysTurnedIn=mTodaysTurnedIn;

    }
}
