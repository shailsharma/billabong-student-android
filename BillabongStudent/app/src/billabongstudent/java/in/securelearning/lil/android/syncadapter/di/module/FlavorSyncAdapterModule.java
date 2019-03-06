package in.securelearning.lil.android.syncadapter.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.BlogReviewModel;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.NotificationModel;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.syncadapter.ftp.FtpFunctions;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.model.FlavorJobModel;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.model.FlavorSyncServiceModel;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.model.SyncServiceModel;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
@Module
public class FlavorSyncAdapterModule {

    @ActivityScope
    @Provides
    public FlavorSyncServiceModel flavorSyncServiceModel() {
        return new FlavorSyncServiceModel();
    }

    @ActivityScope
    @Provides
    public FlavorNetworkModel flavorNetworkModel() {
        return new FlavorNetworkModel();
    }

    @ActivityScope
    @Provides
    public FlavorJobModel flavorJobModel() {
        return new FlavorJobModel();
    }
}
