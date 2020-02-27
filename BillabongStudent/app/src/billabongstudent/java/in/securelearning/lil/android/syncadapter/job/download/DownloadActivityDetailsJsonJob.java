package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.AnalysisActivityData;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.ActivityData;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rupsi 29/06/2018.
 */

public class DownloadActivityDetailsJsonJob {

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
    AppUserModel mAppUserModel;
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

    public DownloadActivityDetailsJsonJob(String subid, String startdate, String enddate) {
        subId = subid;
        startDate = startdate;
        endDate = enddate;
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
            Response<ArrayList<AnalysisActivityData>> response = fetchFromNetwork(subId, startDate, endDate).execute();
            if (response.isSuccessful()) {
                Log.d("activitydata", response.body().toString());
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

    private Call<java.util.ArrayList<AnalysisActivityData>> fetchFromNetwork(String subid, String startdate, String enddate) {
        return mNetworkModel.fetchActivityData(subid, startdate, enddate);
    }

    public void actionFetchSuccess(ArrayList<AnalysisActivityData> results) {

        save(results);
        mSkip += results.size();
        if (mLimit == results.size()) {
            execute();
        }

    }

    private void save(ArrayList<AnalysisActivityData> activityDatas) {
        for (AnalysisActivityData activityData : activityDatas) {
            String d = activityData.getDate();
            Date d1 = DateUtils.convertrIsoDate(d);
            Date date=null;

            SimpleDateFormat parseFormat =
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            try {
                date = parseFormat.parse(String.valueOf(d1));
            } catch (ParseException p) {
            }
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String resultDate = format.format(date);
            activityData.setDate(resultDate);
            //activityData.setAlias(GeneralUtils.generateAlias("activityData",mAppUserModel.getObjectId(),String.valueOf(System.currentTimeMillis())));
            //JobCreator.createDownloadActivityJob(subId, startDate, endDate).execute();
           // Arrays.sort(activityDatas.toArray(), Collections.reverseOrder());
            mJobModel.saveActivityData(activityData);
        }

    }
}
