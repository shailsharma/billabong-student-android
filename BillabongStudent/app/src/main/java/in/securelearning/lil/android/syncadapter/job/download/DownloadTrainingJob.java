package in.securelearning.lil.android.syncadapter.job.download;

import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.events.RefreshTrainingListEvent;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Created by Chaitendra on 23-Jan-18.
 */

public class DownloadTrainingJob extends BaseDownloadJob<Training> {

    private final String TAG = this.getClass().getCanonicalName();

    public DownloadTrainingJob(String objectId, String notificationId) {
        this(objectId, notificationId, true, false);
    }

    public DownloadTrainingJob(String objectId, String notificationId, boolean doJsonRefresh, boolean isValidationEnabled) {
        super(objectId, notificationId, doJsonRefresh, isValidationEnabled);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public Training get(String objectId) {
        return mJobModel.getTraining(objectId);
    }

    @Override
    public void createValidationJobs(Training training) {
        //mRxBus.send(new ObjectDownloadComplete(training.getObjectId(), "", SyncStatus.COMPLETE_SYNC, training.getClass()));
        mRxBus.send(new RefreshTrainingListEvent());

    }

    @Override
    public Call<Training> fetchFromNetwork(String objectId) {
        return mNetworkModel.fetchTraining(objectId);
    }

    @Override
    public Training save(Training training) {
        JobCreator.createDownloadGroupJob(training.getGroupId()).execute();

        return mJobModel.saveTrainingAndSession(training);
//        Training training1=mJobModel.saveTrainingAndSession(training);
//        mRxBus.send(new RefreshTrainingListEvent());
//        return training1;
    }

    @Override
    public List<String> getObjectIdList(Training training) {
        return null;
    }
}
