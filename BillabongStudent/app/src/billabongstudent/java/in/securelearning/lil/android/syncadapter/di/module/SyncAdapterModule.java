package in.securelearning.lil.android.syncadapter.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.NotificationModel;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.syncadapter.ftp.FtpFunctions;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.model.SyncServiceModel;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
@Module
public class SyncAdapterModule {

    @Provides
    @ActivityScope
    public NetworkModel networkModel() {
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public BadgesModel badgesModel() {
        return new BadgesModel();
    }

    @Provides
    @ActivityScope
    public NotificationModel notificationModel() {
        return new NotificationModel();
    }

    @Provides
    @ActivityScope
    public GroupModel groupModel() {
        return new GroupModel();
    }

    @Provides
    @ActivityScope
    public CalEventModel calEventModel() {
        return new CalEventModel();
    }

    @Provides
    @ActivityScope
    public JobModel jobModel() {
        return new JobModel();
    }

    @Provides
    @ActivityScope
    public SyncServiceModel syncServiceModel() {
        return new SyncServiceModel();
    }

    @Provides
    @ActivityScope
    public FtpFunctions ftpFunctions() {
        return new FtpFunctions();
    }

    @Provides
    @ActivityScope
    public ResourceNetworkOperation resourceNetworkOperation() {
        return new ResourceNetworkOperation();
    }

    @Provides
    @ActivityScope
    public WebPlayerLiveModel webPlayerLiveModel() {
        return new WebPlayerLiveModel();
    }

    @Provides
    @ActivityScope
    public PostDataLearningModel postDataLearningModel() {
        return new PostDataLearningModel();
    }

    @Provides
    @ActivityScope
    public OgUtils ogUtils() {
        return new OgUtils();
    }

}
