package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.SearchPeriodsResults;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import retrofit2.Call;
import retrofit2.Response;

//import android.util.Log;

/**
 * Created by Prabodh Dhabaria on 21-12-2016.
 */

public class DownloadPeriodicEventsBulkJob {
    private final String TAG = this.getClass().getCanonicalName();
    private final int MAX_LOGIN_ATTEMPTS = 1;
    protected int mLoginCount = 0;

    private String mLastSyncTime = "";
    private final String mStartTime;
    private final String mEndTime;
    private final String mNotoficationId;
    private int mSkip = 0;
    private int mLimit = 20;
    @Inject
    Context mContext;

    /**
     * use to make database calls
     */
    @Inject
    JobModel mJobModel;

    /**
     * use to make network calls
     */
    @Inject
    NetworkModel mNetworkModel;

    @Inject
    RxBus mRxBus;

    boolean mUpdatePreference = false;

    public DownloadPeriodicEventsBulkJob() {
        this("", DateUtils.getISO8601DateStringFromSeconds(DateUtils.getSecondsForMorningFromDate(new Date())), DateUtils.getISO8601DateStringFromSeconds(DateUtils.getSecondsForMidnightFromDate(new Date())), true);
    }

    public DownloadPeriodicEventsBulkJob(String notificationId, String startTime, String endTime, boolean updatePreference) {
        mNotoficationId = notificationId;
        mStartTime = startTime;
        mEndTime = endTime;
        mUpdatePreference = updatePreference;
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
//            Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
            Response<SearchPeriodsResults> response = fetchFromNetwork(mSkip, mLimit).execute();

            /*if fetch if successful*/
            if (response.isSuccessful()) {

                /*handle the downloaded object*/
                actionFetchSuccess(response.body());

            } else {
                /*handle failure*/
                actionFailure(response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG, "" + e.getMessage());

        }
    }

    /**
     * action to take when fetch is not successful
     *
     * @param code
     */
    public void actionFailure(int code) {
        if (code == 401 && mLoginCount < MAX_LOGIN_ATTEMPTS) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                mLoginCount++;
                execute();
            }
        }
    }

    public void actionFetchSuccess(SearchPeriodsResults results) {

        save(results.getList());
        mSkip += results.getList().size();
        if (mSkip < results.getTotalResult() && !mLastSyncTime.equals(mStartTime)) {
            execute();
        } else if (mUpdatePreference) {
            PrefManager.setShouldSyncPeriodicEvents(false, mContext);
        }

        updateNotificationStatus(mNotoficationId, SyncStatus.COMPLETE_SYNC.toString());

    }

    public void updateNotificationStatus(String notificationId, String syncStatus) {
        mJobModel.updateNotificationStatus(notificationId, syncStatus);
    }

    public void createValidationJobs(ArrayList<PeriodNew> periodNews) {

    }

    public Call<SearchPeriodsResults> fetchFromNetwork(int skip, int limit) {
        return mNetworkModel.fetchPeriodNew(mStartTime, mEndTime, skip, limit);
    }

    public ArrayList<PeriodNew> save(ArrayList<PeriodNew> periodNews) {
        for (PeriodNew periodNew :
                periodNews) {
            if (periodNew != null) {
                periodNew.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                periodNew = mJobModel.savePeriodNew(periodNew);
                try {
                    if (periodNew.getTeacher() != null && !TextUtils.isEmpty(periodNew.getObjectId())) {
                        JobCreator.createDownloadUserProfileJob(periodNew.getTeacher().getId()).execute();
                        periodNew.getTeacher().setThumbnail(mJobModel.fetchUserProfileFromObjectId(periodNew.getTeacher().getId()).getThumbnail());
                        periodNew = mJobModel.savePeriodNew(periodNew);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        if (periodNews.size() > 0) {
            PeriodNew periodNew = periodNews.get(periodNews.size() - 1);
            if (periodNew != null) {
                String time = periodNew.getEndTime();
                if (TextUtils.isEmpty(time)) {
                    time = periodNew.getStartTime();
                }
                if (!TextUtils.isEmpty(time)) {
                    mLastSyncTime = time;
                    PrefManager.setPeriodicEventLastSyncTime(time, mContext);
                }
            }
        }
        return periodNews;
    }

}
