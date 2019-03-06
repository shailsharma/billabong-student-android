package in.securelearning.lil.android.base.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.App;
import in.securelearning.lil.android.base.db.query.FlavorDatabaseQueryHelper;
import in.securelearning.lil.android.base.di.scope.ActivityScope;

/**
 * Created by Prabodh Dhabaria on 15-11-2017.
 */
@Module
public class BaseModuleExt {
    private App mApp;

    public BaseModuleExt(App app) {
        mApp = app;
    }

    @ActivityScope
    @Provides
    public FlavorDatabaseQueryHelper flavorDatabaseQueryHelper() {
        return FlavorDatabaseQueryHelper.getInstance(mApp);
    }
}
