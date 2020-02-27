package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.QuizWeb;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download Web Quiz Job.
 */
public class DownloadQuizWebJsonJob extends BaseDownloadJobWeb<QuizWeb> {
    private final String TAG = this.getClass().getCanonicalName();

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadQuizWebJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new QuizWeb();
    }

    @Override
    protected QuizWeb updateObjectContent(QuizWeb dataObject, QuizWeb temp) {
        return dataObject;
    }

    @Override
    public void createValidationJobs(QuizWeb quiz) {
        Log.d(TAG, "Creating Validation Job for Quiz : " + quiz.getObjectId());
        /*create job to validate the downloaded Quiz*/
        JobCreator.createQuizWebValidationJob(quiz).execute();

    }

    /**
     * network call to fetch the Quiz
     *
     * @param objectId id of the Quiz to fetch
     * @return call : the network call to fetch the Quiz
     */
    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching Quiz : " + objectId);
        /*fetch Quiz from network*/
        return mNetworkModel.fetchQuizWeb(objectId);
    }

    /**
     * persist the quiz
     *
     * @param quiz to persist
     * @return the persisted quiz
     */
    @Override
    public QuizWeb save(QuizWeb quiz) {
        Log.d(TAG, "Saving Quiz : " + quiz.getObjectId());
         /*save quiz to database*/
        return mJobModel.saveQuizWeb(quiz);
    }

    @Override
    public QuizWeb get(String objectId) {
        return mJobModel.fetchQuizWebFromObjectId(objectId);
    }

    /**
     * get the list of object id in the quiz
     *
     * @param quiz containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(QuizWeb quiz) {
         /*get id from the Quiz*/
        return Collections.singletonList(quiz.getObjectId());
    }
}
