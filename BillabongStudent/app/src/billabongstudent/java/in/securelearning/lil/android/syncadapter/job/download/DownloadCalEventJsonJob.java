package in.securelearning.lil.android.syncadapter.job.download;

import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import retrofit2.Call;

/**
 * Job to download an CalendarEvent from server
 */
public class DownloadCalEventJsonJob extends BaseDownloadJob<CalendarEvent> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadCalEventJsonJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadCalEventJsonJob(String objectId, String notificationId) {
        super(objectId, notificationId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadCalEventJsonJob(String objectId, String notificationId, boolean doJsonRefresh, boolean isValidationEnabled) {
        super(objectId, notificationId, doJsonRefresh, isValidationEnabled);
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public CalendarEvent get(String objectId) {
        return mJobModel.fetchCalEventFromObjectId(objectId);
    }

    /**
     * create validation job for the calendarEvent
     *
     * @param calendarEvent to validate
     */
    @Override
    public void createValidationJobs(CalendarEvent calendarEvent) {
        /*create job to validate the downloaded calendarEvent*/
        JobCreator.createCalendarEventValidationJob(calendarEvent).execute();
    }

    /**
     * network call to fetch postData
     *
     * @param objectId id of the postData to fetch
     * @return call : the network call to fetch postData
     */
    @Override
    public Call<CalendarEvent> fetchFromNetwork(String objectId) {
        /*fetch calendarEvent from network*/
        return mNetworkModel.fetchCalendarEventData(objectId);
    }

    /**
     * persist calendarEvent
     *
     * @param calendarEvent to persist
     * @return the persisted postData
     */
    @Override
    public CalendarEvent save(CalendarEvent calendarEvent) {
        /*save calendarEvent to database*/
        return mJobModel.saveCalendarEventData(calendarEvent);
    }

    /**
     * get the list of object id in the calendarEvent
     *
     * @param calendarEvent containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(CalendarEvent calendarEvent) {

        /*get id from the calendarEvent*/
        return Collections.singletonList(calendarEvent.getObjectId());
    }


}
