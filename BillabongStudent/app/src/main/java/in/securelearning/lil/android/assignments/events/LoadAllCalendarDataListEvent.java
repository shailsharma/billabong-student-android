package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.DayDetails;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class LoadAllCalendarDataListEvent {
    private final DayDetails dayDetails;

    public LoadAllCalendarDataListEvent(DayDetails dayDetails) {
        this.dayDetails = dayDetails;
    }

    public DayDetails getDayDetails() {
        return dayDetails;

    }
}
