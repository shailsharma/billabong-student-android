package in.securelearning.lil.android.home.events;

import java.util.List;

import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;

/**
 * Created by Pushkar Raj on 6/28/2016.
 */
public class LoadCalendarEvent {
    private final int mActivityCount, mAnnouncementCount, mPersonalEventCount;
    private final List<CalendarEvent> mCalendarEventList;

    public LoadCalendarEvent(List<CalendarEvent> calendarEvents, int mActivityCount, int mAnnouncementCount, int mPersonalEventCount) {
        this.mCalendarEventList = calendarEvents;
        this.mActivityCount = mActivityCount;
        this.mAnnouncementCount = mAnnouncementCount;
        this.mPersonalEventCount = mPersonalEventCount;
    }

    public int getCalendarActivitiesCount() {
        return mActivityCount;

    }

    public int getCalendarAnnouncementCount() {
        return mAnnouncementCount;

    }

    public int getCalendarPersonalCount() {
        return mPersonalEventCount;

    }


    public List<CalendarEvent> getList() {
        return mCalendarEventList;
    }


}
