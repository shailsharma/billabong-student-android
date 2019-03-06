package in.securelearning.lil.android.learningnetwork.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.AssignBadgeModel;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.home.model.CalendarEventModel;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
@Module
public class LearningNetworkModule {

    @Provides
    @ActivityScope
    public GroupModel groupModel() {
        return new GroupModel();
    }


    @Provides
    @ActivityScope
    public BadgesModel badgesModel() {
        return new BadgesModel();
    }

    @Provides
    @ActivityScope
    public PostDataLearningModel postDataLearningModel() {
        return new PostDataLearningModel();
    }

    @Provides
    @ActivityScope
    public AssignBadgeModel model() {
        return new AssignBadgeModel();
    }

    @Provides
    @ActivityScope
    public CalendarEventModel calendarEventModel() {
        return new CalendarEventModel();
    }

    @Provides
    @ActivityScope
    public OgUtils ogUtils(){
       return new OgUtils();
    }
}
