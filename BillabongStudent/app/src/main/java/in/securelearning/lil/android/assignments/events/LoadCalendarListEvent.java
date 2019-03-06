package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.CalendarEvent;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class LoadCalendarListEvent {
    private final ArrayList<CalendarEvent> mCalendarEvents;

    public LoadCalendarListEvent(ArrayList<CalendarEvent> calendarEvents) {
        this.mCalendarEvents = calendarEvents;
    }

    public ArrayList<CalendarEvent> getEventList() {
        return mCalendarEvents;
    }
}
