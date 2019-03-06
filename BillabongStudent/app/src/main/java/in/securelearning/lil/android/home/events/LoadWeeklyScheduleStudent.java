package in.securelearning.lil.android.home.events;

import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.WeeklySchedule;

/**
 * Enter Copyright Javadoc Comments here
 * <p/>
 * Created by Pushkar Raj 7/7/2016.
 */
public class LoadWeeklyScheduleStudent {
    public WeeklySchedule getWeeklySchedule() {
        return mWeeklySchedule;
    }

    private final WeeklySchedule mWeeklySchedule;

    public LoadWeeklyScheduleStudent(WeeklySchedule weeklySchedule) {
        this.mWeeklySchedule = weeklySchedule;
    }
}
