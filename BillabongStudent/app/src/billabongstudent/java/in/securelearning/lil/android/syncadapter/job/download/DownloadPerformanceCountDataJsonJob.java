package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.PerformanceResponseCount;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.ActivityData;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rupsi on 8/13/2018.
 */

public class DownloadPerformanceCountDataJsonJob {

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

    private final String TAG = this.getClass().getCanonicalName();
    private final int MAX_LOGIN_ATTEMPTS = 1;
    protected int mLoginCount = 0;
    private int mSkip = 0;
    private int mLimit = 20;
    String id;
    ActivityData activityData;
    String userid, subId, startDate, endDate;

    public DownloadPerformanceCountDataJsonJob(String subid) {
        subId = subid;
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
            Response<PerformanceResponseCount> response = fetchFromNetwork(subId).execute();
            if (response.isSuccessful()) {

                actionFetchSuccess(response.body());


            } else {

                actionFailure(response.code());
            }

            //  actionFetchSuccess(actvitydata);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void actionFailure(int code) {
        if (code == 401 && mLoginCount < MAX_LOGIN_ATTEMPTS) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                mLoginCount++;
                execute();
            }
        }
    }

    private Call<PerformanceResponseCount> fetchFromNetwork(String subId) {
        return mNetworkModel.fetchPerformanceCount(subId);
    }

    public void actionFetchSuccess(PerformanceResponseCount results) {

        save(results);


    }

    private void save(PerformanceResponseCount performanceResponseCount) {
        //JobCreator.createDownloadActivityJob(subId, startDate, endDate).execute();
        mJobModel.savePerformanceResponseCount(performanceResponseCount);


    }
}
