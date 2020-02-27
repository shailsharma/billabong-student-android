package in.securelearning.lil.android.syncadapter.job.download;

import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import retrofit2.Call;

/**
 * Created by Prabodh Dhabaria on 21-12-2016.
 */

public class DownloadLearningMapListJob extends BaseDownloadArrayJob<ArrayList<LearningMap>> {
    @Inject
    RxBus mRxBus;

    public DownloadLearningMapListJob() {
        super("");

        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void createValidationJobs(ArrayList<LearningMap> learningMaps) {

    }

    @Override
    public Call<ArrayList<LearningMap>> fetchFromNetwork(String objectId) {
        return mNetworkModel.fetchLearningMaps();
    }

    @Override
    public ArrayList<LearningMap> save(ArrayList<LearningMap> learningMaps) {
        for (LearningMap learningMap :
                learningMaps) {
            learningMap.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
            learningMap = mJobModel.saveLearningMap(learningMap);
        }
//        mRxBus.send(new RefreshLearningMapEvent());

        return learningMaps;
    }

    @Override
    public List<String> getObjectIdList(ArrayList<LearningMap> learningMaps) {
        List<String> strings = new java.util.ArrayList<>();

        for (LearningMap learningMap :
                learningMaps) {
            strings.add(learningMap.getObjectId());
        }

        return strings;
    }
}
