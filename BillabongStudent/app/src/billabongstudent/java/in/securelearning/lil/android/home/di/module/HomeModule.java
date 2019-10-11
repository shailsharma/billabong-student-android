package in.securelearning.lil.android.home.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.courses.models.CoursesModel;
import in.securelearning.lil.android.gamification.model.MascotModel;
import in.securelearning.lil.android.gamification.views.activity.MascotActivity;
import in.securelearning.lil.android.home.model.CalendarEventModel;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.model.SearchModel;
import in.securelearning.lil.android.homework.model.HomeworkModel;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.profile.model.ProfileModel;
import in.securelearning.lil.android.syncadapter.ftp.FtpFunctions;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
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
    public SearchModel searchModel() {
        return new SearchModel();
    }


    @Provides
    @ActivityScope
    public CalendarEventModel calendarEventModel() {
        return new CalendarEventModel();
    }

    @Provides
    @ActivityScope
    public HomeModel homeModel() {
        return new HomeModel();
    }

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
    public AssignmentTeacherModel assignmentTeacherModel() {
        return new AssignmentTeacherModel();
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
    public AssignmentResponseStudentModel assignmentResponseStudentModel() {
        return new AssignmentResponseStudentModel();
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
    public CoursesModel coursesModel() {
        return new CoursesModel();
    }

    @Provides
    @ActivityScope
    public JobModel jobModel() {
        return new JobModel();
    }

    @Provides
    @ActivityScope
    public FlavorNetworkModel flavorNetworkModel() {
        return new FlavorNetworkModel();
    }

    @Provides
    @ActivityScope
    public FlavorHomeModel flavorHomeModel() {
        return new FlavorHomeModel();
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
}
