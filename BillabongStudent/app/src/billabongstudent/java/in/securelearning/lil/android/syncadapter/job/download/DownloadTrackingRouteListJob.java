package in.securelearning.lil.android.syncadapter.job.download;

import java.util.List;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import retrofit2.Call;

/**
 * Created by Prabodh Dhabaria on 21-12-2016.
 */

public class DownloadTrackingRouteListJob extends BaseDownloadArrayJob<ArrayList<TrackingRoute>> {
    public DownloadTrackingRouteListJob(String objectId) {
        super(objectId);
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void createValidationJobs(ArrayList<TrackingRoute> trackingRoutes) {

    }

    @Override
    public Call<ArrayList<TrackingRoute>> fetchFromNetwork(String objectId) {
        return mNetworkModel.fetchTrackingRoute(objectId);
    }

    @Override
    public ArrayList<TrackingRoute> save(ArrayList<TrackingRoute> trackingRoutes) {
        for (TrackingRoute trackingRoute :
                trackingRoutes) {
            trackingRoute.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
            trackingRoute = mJobModel.saveTrackingRoute(trackingRoute);
        }
        return trackingRoutes;
    }

    @Override
    public List<String> getObjectIdList(ArrayList<TrackingRoute> trackingRoutes) {
        List<String> strings = new java.util.ArrayList<>();

        for (TrackingRoute trackingRoute :
                trackingRoutes) {
            strings.add(trackingRoute.getObjectId());
        }

        return strings;
    }
}
