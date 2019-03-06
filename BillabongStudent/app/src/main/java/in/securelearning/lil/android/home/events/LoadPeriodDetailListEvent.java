package in.securelearning.lil.android.home.events;

import java.util.List;

import in.securelearning.lil.android.base.dataobjects.PeriodDetail;


/**
 * Created by Pushkar Raj on 7/25/2016.
 */
public class LoadPeriodDetailListEvent {
    private final List<PeriodDetail> mPeriodDetailList;

    public LoadPeriodDetailListEvent(List<PeriodDetail> periods) {
        this.mPeriodDetailList = periods;

    }

    public List<PeriodDetail> getList() {
        return mPeriodDetailList;
    }


}
