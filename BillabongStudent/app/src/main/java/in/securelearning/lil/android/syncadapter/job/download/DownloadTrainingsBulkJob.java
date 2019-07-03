package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 27-Dec-17.
 */

public class DownloadTrainingsBulkJob {

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

    public DownloadTrainingsBulkJob() {
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
            Response<ArrayList<Training>> response = fetchFromNetwork(mSkip, mLimit).execute();

            if (response.isSuccessful()) {

                actionFetchSuccess(response.body());

            } else {

                actionFailure(response.code());
            }
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

    private Call<ArrayList<Training>> fetchFromNetwork(int skip, int limit) {
        return mNetworkModel.fetchTrainings(skip, limit);
    }

    public void actionFetchSuccess(ArrayList<Training> results) {

        save(results);
        mSkip += results.size();
        if (mLimit == results.size()) {
            execute();
        }

    }

    private void save(ArrayList<Training> trainings) {
        for (Training training : trainings) {
            JobCreator.createDownloadGroupJob(training.getGroupId(), ConstantUtil.GROUP_TYPE_TRAINING).execute();
            mJobModel.saveTrainingAndSession(training);
        }

    }
}
