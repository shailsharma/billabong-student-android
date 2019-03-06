package in.securelearning.lil.android.home.events;

import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Period;

/**
 * Created by Pushkar Raj on 6/28/2016.
 */
public class LoadPeriodsFromWeeklyScheduleEvent {
    private final List<Period> mPeriodArrayList;

    public LoadPeriodsFromWeeklyScheduleEvent(List<Period> periods) {
        this.mPeriodArrayList = periods;

    }

    public List<Period> getList() {
        return mPeriodArrayList;
    }


}
