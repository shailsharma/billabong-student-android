package in.securelearning.lil.android.home.events;

import in.securelearning.lil.android.base.dataobjects.CalendarEvent;

/**
 * Created by Chaitendra on 08-Sep-17.
 */

public class LoadCalendarEventDownloaded {

    CalendarEvent mCalendarEvent;

    public LoadCalendarEventDownloaded(CalendarEvent calendarEvent) {
        mCalendarEvent = calendarEvent;
    }

    public CalendarEvent getCalendarEvent() {
        return mCalendarEvent;
    }

}
