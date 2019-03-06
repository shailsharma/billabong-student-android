package in.securelearning.lil.android.login.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.dataobjects.CorporateSettings;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.CorporateSettingsModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@Module
public class LoginModule {

    @ActivityScope
    @Provides
    public GroupModel groupModel() {
        return new GroupModel();
    }

    @ActivityScope
    @Provides
    public CorporateSettingsModel corporateSettingsModel() {
        return new CorporateSettingsModel();
    }

    @ActivityScope
    @Provides
    public NetworkModel networkModel() {
        return new NetworkModel();
    }

    @ActivityScope
    @Provides
    public JobModel jobModel() {
        return new JobModel();
    }

}
