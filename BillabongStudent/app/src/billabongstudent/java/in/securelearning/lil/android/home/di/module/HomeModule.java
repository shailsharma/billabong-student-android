package in.securelearning.lil.android.home.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.gamification.model.MascotModel;
import in.securelearning.lil.android.gamification.views.activity.MascotActivity;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.homework.model.HomeworkModel;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.lrpa.model.LRPAModel;
import in.securelearning.lil.android.profile.model.ProfileModel;
import in.securelearning.lil.android.syncadapter.ftp.FtpFunctions;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.thirdparty.model.ThirdPartyModel;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@Module
public class HomeModule {

    @Provides
    @ActivityScope
    public NetworkModel networkModel() {
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public PostDataLearningModel postDataLearningModel() {
        return new PostDataLearningModel();
    }

    @Provides
    @ActivityScope
    public FtpFunctions ftpFunctions() {
        return new FtpFunctions();
    }

    @Provides
    @ActivityScope
    public BadgesModel badgesModel() {
        return new BadgesModel();
    }

    @Provides
    @ActivityScope
    public CalEventModel calEventModel() {
        return new CalEventModel();
    }

    @Provides
    @ActivityScope
    public GroupModel groupModel() {
        return new GroupModel();
    }

    @Provides
    @ActivityScope
    public JobModel jobModel() {
        return new JobModel();
    }

    @Provides
    @ActivityScope
    public HomeModel homeModel() {
        return new HomeModel();
    }

    @Provides
    @ActivityScope
    public AnalyticsModel analyticsModel() {
        return new AnalyticsModel();
    }

    @Provides
    @ActivityScope
    public ThirdPartyModel mindSparkModel() {
        return new ThirdPartyModel();
    }

    @Provides
    @ActivityScope
    public HomeworkModel homeworkModel() {
        return new HomeworkModel();
    }

    @Provides
    @ActivityScope
    public MascotActivity gamificationDialog() {
        return new MascotActivity();
    }

    @Provides
    @ActivityScope
    public MascotModel gamificationModel() {
        return new MascotModel();
    }

    @Provides
    @ActivityScope
    public ProfileModel profileModel() {
        return new ProfileModel();
    }

    @Provides
    @ActivityScope
    public LRPAModel LRPAModel() {
        return new LRPAModel();
    }
}
