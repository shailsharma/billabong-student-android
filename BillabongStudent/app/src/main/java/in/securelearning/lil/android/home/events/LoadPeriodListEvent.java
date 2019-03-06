package in.securelearning.lil.android.home.events;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Period;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;

/**
 * Created by Pushkar Raj on 6/28/2016.
 */
public class LoadPeriodListEvent {
    private final List<PeriodNew> mPeriodArrayList;

    public LoadPeriodListEvent(List<PeriodNew> periods) {
        this.mPeriodArrayList = periods;

    }

    public List<PeriodNew> getList() {
        return mPeriodArrayList;
    }




}
