package in.securelearning.lil.android.tracking.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.tracking.model.TrackingMapModel;

/**
 * Created by Secure on 26-04-2017.
 */

@Module
public class TrackingModule {
    @Provides
    @ActivityScope
    public NetworkModel networkModel(){
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public TrackingMapModel trackingMapModel(){
        return new TrackingMapModel();
    }

    @Provides
    @ActivityScope
    public GroupModel groupMapModel(){
        return new GroupModel();
    }

}
