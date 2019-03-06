package in.securelearning.lil.android.assignments.events;

import in.securelearning.lil.android.base.dataobjects.DayDetails;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class LoadAllCalendarDataListDashboardEvent {
    private final DayDetails dayDetails;

    public LoadAllCalendarDataListDashboardEvent(DayDetails dayDetails) {
        this.dayDetails = dayDetails;
    }

    public DayDetails getDayDetails() {
        return dayDetails;

    }
}
