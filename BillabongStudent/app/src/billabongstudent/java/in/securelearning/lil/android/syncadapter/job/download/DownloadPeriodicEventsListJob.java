package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Date;
import java.util.List;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import retrofit2.Call;

/**
 * Created by Prabodh Dhabaria on 21-12-2016.
 */

public class DownloadPeriodicEventsListJob extends BaseDownloadArrayJob<ArrayList<PeriodNew>> {
    private final String mStartTime;
    private final String mEndTime;

    public DownloadPeriodicEventsListJob() {
        this(DateUtils.getISO8601DateStringFromSeconds(DateUtils.getSecondsForMorningFromDate(new Date())), DateUtils.getISO8601DateStringFromSeconds(DateUtils.getSecondsForMidnightFromDate(new Date())));
    }

    public DownloadPeriodicEventsListJob(String startTime, String endTime) {
        super("");
        mStartTime = startTime;
        mEndTime = endTime;
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void createValidationJobs(ArrayList<PeriodNew> periodNews) {

    }

    @Override
    public Call<ArrayList<PeriodNew>> fetchFromNetwork(String objectId) {
        return mNetworkModel.fetchPeriodNew(mStartTime, mEndTime);
    }

    @Override
    public ArrayList<PeriodNew> save(ArrayList<PeriodNew> periodNews) {
        for (PeriodNew periodNew :
                periodNews) {
            periodNew.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
            periodNew = mJobModel.savePeriodNew(periodNew);
        }
        return periodNews;
    }

    @Override
    public List<String> getObjectIdList(ArrayList<PeriodNew> periodNews) {
        List<String> strings = new java.util.ArrayList<>();

        for (PeriodNew periodNew :
                periodNews) {
            strings.add(periodNew.getObjectId());
        }

        return strings;
    }
}
