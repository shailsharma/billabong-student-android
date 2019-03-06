package in.securelearning.lil.android.home.events;

import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Day;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;

/**
 * Created by Pushkar Raj on 6/28/2016.
 */
public class LoadDaysListEvent {
    private final List<Day> mDaysList;

    public LoadDaysListEvent(List<Day> days) {
        this.mDaysList = days;

    }

    public List<Day> getList() {
        return mDaysList;
    }




}
