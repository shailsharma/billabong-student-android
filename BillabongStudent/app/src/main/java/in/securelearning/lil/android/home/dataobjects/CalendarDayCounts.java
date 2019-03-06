package in.securelearning.lil.android.home.dataobjects;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.CalendarDay;

/**
 * Created by Chaitendra on 12-Jul-17.
 */

public class CalendarDayCounts extends CalendarDayEventCounts implements Serializable {
    private CalendarDay calendarDay;
    private int assignmentCounts = 0;
    private int periodCounts = 0;

    public CalendarDay getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    public int getAssignmentCounts() {
        return assignmentCounts;
    }

    public void setAssignmentCounts(int assignmentCounts) {
        this.assignmentCounts = assignmentCounts;
    }

    public int getPeriodCounts() {
        return periodCounts;
    }

    public void setPeriodCounts(int periodCounts) {
        this.periodCounts = periodCounts;
    }
}
