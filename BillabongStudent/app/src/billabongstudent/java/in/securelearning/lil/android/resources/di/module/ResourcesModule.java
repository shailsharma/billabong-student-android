package in.securelearning.lil.android.resources.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.resources.model.ResourcesMapModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;

/**
 * Created by Secure on 12-06-2017.
 */

@Module
public class ResourcesModule {

    @Provides
    @ActivityScope
    public NetworkModel networkModel(){
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public ResourcesMapModel resourcesMapModel(){
        return new ResourcesMapModel();
    }

}
