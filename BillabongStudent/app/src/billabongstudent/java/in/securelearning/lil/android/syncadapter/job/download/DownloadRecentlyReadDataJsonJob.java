package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.AnalysisActivityRecentlyRead;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.ActivityData;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rupsi on 7/19/2018.
 */

public class DownloadRecentlyReadDataJsonJob {

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

    public DownloadRecentlyReadDataJsonJob(String subid) {
        subId=subid;
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
            Response<ArrayList<AnalysisActivityRecentlyRead>> response = fetchFromNetwork(subId,mLimit, mSkip).execute();
            if (response.isSuccessful()) {

                actionFetchSuccess(response.body());
                Log.e("recentdata",response.body().toString());

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

    private Call<ArrayList<AnalysisActivityRecentlyRead>> fetchFromNetwork(String subId,int limit, int skip) {
        return mNetworkModel.fetchRecentlyReadData(subId,limit, skip);
    }

    public void actionFetchSuccess(ArrayList<AnalysisActivityRecentlyRead> results) {

        save(results);
        mSkip += results.size();
        if (mLimit == results.size()) {
            execute();
        }

    }

    private void save(ArrayList<AnalysisActivityRecentlyRead> activityDatas) {
        for (AnalysisActivityRecentlyRead activityData : activityDatas) {
            //JobCreator.createDownloadActivityJob(subId, startDate, endDate).execute();
            mJobModel.saveRecentReadData(activityData);
        }

    }
}
