package in.securelearning.lil.android.home.events;

import java.util.Date;

import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.WeeklySchedule;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj 7/7/2016.
 */
public class LoadNewEventCreated {
    private final Date mStartDate;

    public LoadNewEventCreated(Date startDate) {
        this.mStartDate = startDate;
    }

    public Date getStartDate() {
        return mStartDate;
    }
}
