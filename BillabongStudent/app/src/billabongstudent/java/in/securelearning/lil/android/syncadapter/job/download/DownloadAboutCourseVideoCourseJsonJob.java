package in.securelearning.lil.android.syncadapter.job.download;

import com.couchbase.lite.util.Log;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.UserNotification;
import in.securelearning.lil.android.base.model.UserNotificationModel;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download AboutCourse Job.
 */
public class DownloadAboutCourseVideoCourseJsonJob extends BaseDownloadJobWeb<AboutCourse> {
    private final String TAG = this.getClass().getCanonicalName();
    private final boolean isBroadcast;
    private boolean shouldCreateUserNotification;
    @Inject
    UserNotificationModel mUserNotificationModel;

    /**
     * handles initialization of injector component
     * and initializes the object id to download
     *
     * @param objectId id of the object to download
     */
    public DownloadAboutCourseVideoCourseJsonJob(String objectId) {
        super(objectId, "");
        this.isBroadcast = false;
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadAboutCourseVideoCourseJsonJob(String objectId, String notificationId) {
        super(objectId, notificationId);
        this.isBroadcast = false;
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadAboutCourseVideoCourseJsonJob(String objectId, String notificationId, boolean isBroadcast, boolean shouldCreateUserNotification) {
        super(objectId, notificationId);
        this.isBroadcast = isBroadcast;
        this.shouldCreateUserNotification = shouldCreateUserNotification;
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void initializeDataObject() {
        this.mDataObject = new AboutCourse();
        this.mDataObject.setBroadcast(isBroadcast);
    }

    @Override
    protected AboutCourse updateObjectContent(AboutCourse dataObject, AboutCourse temp) {
        dataObject.getReviews().setAvgRating(temp.getReviews().getAvgRating());
        dataObject.setCurator(temp.getCurator());
        dataObject.setReviews(temp.getReviews());
        dataObject.getReviews().setTotalViews(temp.getReviews().getTotalViews());
        return dataObject;
    }

    @Override
    public void createValidationJobs(AboutCourse aboutCourse) {
        Log.d(TAG, "Creating Validation Job for AboutCourse : " + aboutCourse.getObjectId());
        /*create job to validate the downloaded AboutCourse*/
        if (shouldCreateUserNotification) {
            UserNotification cloneNotification = new UserNotification();
            cloneNotification.setThumbnail(aboutCourse.getThumbnail());
            cloneNotification.setName(aboutCourse.getName());
            cloneNotification.setTimestamp(0d);
            cloneNotification.setObjectId(aboutCourse.getObjectId());
            cloneNotification.setObjectDocId(aboutCourse.getDocId());
            cloneNotification.setObjectType(aboutCourse.getCourseType());
            cloneNotification.setTitle(aboutCourse.getTitle());
            mUserNotificationModel.saveUserNotification(cloneNotification);
        }

        JobCreator.createAboutCourseValidationJob(aboutCourse, isBroadcast).execute();

    }

    /**
     * network call to fetch the aboutCourse
     *
     * @param objectId id of the aboutCourse to fetch
     * @return call : the network call to fetch the aboutCourse
     */
    @Override
    public Call<ResponseBody> fetchFromNetwork(String objectId) {
        Log.d(TAG, "Fetching AboutCourse : " + objectId);
        /*fetch AboutCourse from network*/
        return mNetworkModel.getVideoCourseAboutResponseBody(objectId);
    }

    /**
     * persist the aboutCourse
     *
     * @param aboutCourse to persist
     * @return the persisted aboutCourse
     */
    @Override
    public AboutCourse save(AboutCourse aboutCourse) {
        Log.d(TAG, "Saving aboutCourse : " + aboutCourse.getObjectId());
         /*save aboutCourse to database*/
        aboutCourse.setBroadcast(isBroadcast);
        return mJobModel.saveAboutCourse(aboutCourse);
    }

    @Override
    public AboutCourse get(String objectId) {
        return mJobModel.fetchAboutCourseFromObjectId(objectId);
    }

    /**
     * get the list of object id in the aboutCourse
     *
     * @param aboutCourse containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(AboutCourse aboutCourse) {
         /*get id from the AboutCourse*/
        return Collections.singletonList(aboutCourse.getObjectId());
    }
}
