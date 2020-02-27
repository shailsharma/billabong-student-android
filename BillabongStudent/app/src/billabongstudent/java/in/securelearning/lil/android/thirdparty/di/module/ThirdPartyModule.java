package in.securelearning.lil.android.thirdparty.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.thirdparty.model.ThirdPartyModel;

@Module
public class ThirdPartyModule {

    @Provides
    @ActivityScope
    public NetworkModel networkModel() {
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public ThirdPartyModel thirdPartyModel() {
        return new ThirdPartyModel();
    }

}
