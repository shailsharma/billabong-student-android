package in.securelearning.lil.android.courses.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.DownloadedCourseModel;
import in.securelearning.lil.android.courses.models.CoursesModel;
import in.securelearning.lil.android.courses.models.ItemListModel;
import in.securelearning.lil.android.resources.model.ResourcesMapModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@Module
public class CoursesModule {


    @Provides
    @ActivityScope
    public ItemListModel getItemListModel() {
        return new ItemListModel();
    }

    @Provides
    @ActivityScope
    public NetworkModel getNetworkModel() {
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public CoursesModel coursesModel() {
        return new CoursesModel();
    }

    @Provides
    @ActivityScope
    public WebPlayerLiveModel getWebPlayerLiveModel() {
        return new WebPlayerLiveModel();
    }

    @Provides
    @ActivityScope
    public ResourcesMapModel resourcesMapModel() {
        return new ResourcesMapModel();
    }


}
