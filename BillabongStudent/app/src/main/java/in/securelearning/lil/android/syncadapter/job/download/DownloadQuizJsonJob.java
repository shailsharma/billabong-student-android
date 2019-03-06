package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Download Quiz Job.
 */
public class DownloadQuizJsonJob extends BaseDownloadJob<Quiz> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadQuizJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public Quiz get(String objectId) {
        return mJobModel.fetchQuizFromObjectId(objectId);
    }

    /**
     * create validation job for the quiz
     *
     * @param quiz to validate
     */
    @Override
    public void createValidationJobs(Quiz quiz) {
        Log.d(TAG, "Creating Validation Job for Quiz : " + quiz.getObjectId());
        /*create job to validate the downloaded quiz*/
        JobCreator.createQuizValidationJob(quiz).execute();
    }

    /**
     * network call to fetch the quiz
     *
     * @param objectId id of the quiz to fetch
     * @return call : the network call to fetch the quiz
     */
    @Override
    public Call<Quiz> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching Quiz : " + objectId);
        /*fetch quiz from network*/
        return mNetworkModel.fetchQuiz(objectId);
    }

    /**
     * persist the quiz
     *
     * @param quiz to persist
     * @return the persisted quiz
     */
    @Override
    public Quiz save(Quiz quiz) {
        Log.d(TAG, "Saving Quiz : " + quiz.getObjectId());
         /*save quiz to database*/
        return mJobModel.saveQuiz(quiz);
    }

    /**
     * get the list of object id in the quiz
     *
     * @param quiz containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(Quiz quiz) {
         /*get id from the quiz*/
        return Collections.singletonList(quiz.getObjectId());
    }

}
