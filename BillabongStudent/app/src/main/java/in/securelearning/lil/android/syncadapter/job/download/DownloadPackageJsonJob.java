package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.ServerDataPackage;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.validation.BaseValidationJob;
import retrofit2.Call;

/**
 * Download Server Data Package Job.
 */
public class DownloadPackageJsonJob extends BaseDownloadJob<ServerDataPackage> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadPackageJsonJob() {
        super("");

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public ServerDataPackage get(String objectId) {
        return new ServerDataPackage();
    }

    /**
     * create validation job for the data package
     * * @param dataPackage to validate
     */
    @Override
    public void createValidationJobs(ServerDataPackage dataPackage) {
        Log.d(TAG, "Creating Validation Job for Server Data Package ");
//        /*create quiz validation jobs*/
//        List<BaseValidationJob<Quiz>> list = JobCreator.createQuizValidationJobList(dataPackage.getQuizList());
//        for (BaseValidationJob<Quiz> job :
//                list) {
//            job.execute();
//        }
//
        /*create assignment validation jobs*/
        List<BaseValidationJob<Assignment>> list2 =JobCreator.createAssignmentValidationJobList(dataPackage.getAssignmentList());
        for (BaseValidationJob<Assignment> job :
                list2) {
            job.execute();
        }
//
//        /*create assignment response validation jobs*/
//        List<BaseValidationJob<AssignmentResponse>> list3 =JobCreator.createAssignmentResponseValidationJobList(dataPackage.getAssignmentResponseList());
//        for (BaseValidationJob<AssignmentResponse> job :
//                list3) {
//            job.execute();
//        }


        /*create Learning network post validation jobs*/
        List<BaseValidationJob<PostData>> list4 =JobCreator.createPostDataValidationJobList(dataPackage.getLearningNetworkPostDataList());
        for (BaseValidationJob<PostData> job :
                list4) {
            job.execute();
        }

    }

    /**
     * network call to fetch the data package
     *
     * @param objectId id of the object to fetch
     * @return call : network to fetch the data package
     */
    @Override
    public Call<ServerDataPackage> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching Server data package : " + objectId);
        /*fetch Server data package from network*/
        return mNetworkModel.fetchDataPackage();
    }

    /**
     * persist the data package
     *
     * @param dataPackage to persist
     * @return persisted data package
     */
    @Override
    public ServerDataPackage save(ServerDataPackage dataPackage) {
        Log.d(TAG, "Saving Data package" );
        /*save data package*/
        return mJobModel.saveDataPackage(dataPackage);
    }

    /**
     * get the list of object id in the dataPackage
     *
     * @param dataPackage containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(ServerDataPackage dataPackage) {
        /*initialize id list*/
        ArrayList<String> list = new ArrayList<>();

        ArrayList<Quiz> list1 = dataPackage.getQuizList();
        for (int i = 0; i < list1.size(); i++) {
            /*add quiz id to list*/
            list.add(list1.get(i).getObjectId());
        }

        ArrayList<Assignment> list2 = dataPackage.getAssignmentList();
        for (int i = 0; i < list2.size(); i++) {
            /*add assignment id to list*/
            list.add(list2.get(i).getObjectId());
        }

        ArrayList<AssignmentResponse> list3 = dataPackage.getAssignmentResponseList();
        for (int i = 0; i < list3.size(); i++) {
            /*add assignment response id to list*/
            list.add(list3.get(i).getObjectId());
        }

        ArrayList<PostData> list4 = dataPackage.getLearningNetworkPostDataList();
        for (int i = 0; i < list4.size(); i++) {
            /*add assignment response id to list*/
            list.add(list4.get(i).getObjectId());
        }

        /*return id list*/
        return list;
    }


}