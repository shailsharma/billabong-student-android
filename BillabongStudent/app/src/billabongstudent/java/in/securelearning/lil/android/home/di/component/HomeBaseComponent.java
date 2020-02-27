package in.securelearning.lil.android.home.di.component;


import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.analytics.views.activity.PerformanceDetailActivity;
import in.securelearning.lil.android.analytics.views.activity.ProgressDetailActivity;
import in.securelearning.lil.android.analytics.views.activity.StudentAnalyticsActivity;
import in.securelearning.lil.android.analytics.views.activity.StudentAnalyticsTabActivity;
import in.securelearning.lil.android.analytics.views.activity.TimeEffortDetailActivity;
import in.securelearning.lil.android.analytics.views.fragment.StudentCoverageFragment;
import in.securelearning.lil.android.analytics.views.fragment.StudentEffortFragment;
import in.securelearning.lil.android.analytics.views.fragment.StudentExcellenceFragment;
import in.securelearning.lil.android.analytics.views.fragment.StudentPerformanceFragment;
import in.securelearning.lil.android.app.MyApplication;
import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.gamification.model.MascotModel;
import in.securelearning.lil.android.gamification.views.activity.MascotActivity;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.activity.HelpAndFAQActivity;
import in.securelearning.lil.android.home.views.activity.NavigationDrawerActivity;
import in.securelearning.lil.android.home.views.activity.PasswordChangeActivity;
import in.securelearning.lil.android.home.views.activity.SettingActivity;
import in.securelearning.lil.android.home.views.activity.VocationalTopicsActivity;
import in.securelearning.lil.android.home.views.adapter.HelpAndFaqModuleListAdapter;
import in.securelearning.lil.android.home.views.fragment.DashboardFragment;
import in.securelearning.lil.android.homework.model.HomeworkModel;
import in.securelearning.lil.android.homework.views.activity.HomeworkDetailActivity;
import in.securelearning.lil.android.homework.views.activity.SubmitHomeworkActivity;
import in.securelearning.lil.android.homework.views.fragment.HomeworkFragment;
import in.securelearning.lil.android.login.views.activity.GeneratePasswordActivity;
import in.securelearning.lil.android.lrpa.model.LRPAModel;
import in.securelearning.lil.android.lrpa.views.activity.SubjectDetailsActivity;
import in.securelearning.lil.android.lrpa.views.adapter.PracticeAdapter;
import in.securelearning.lil.android.lrpa.views.fragment.ChaptersFragment;
import in.securelearning.lil.android.lrpa.views.fragment.SubjectDetailHomeFragment;
import in.securelearning.lil.android.lrpa.views.fragment.SubjectHomeworkFragment;
import in.securelearning.lil.android.profile.model.ProfileModel;
import in.securelearning.lil.android.profile.views.activity.StudentProfileActivity;
import in.securelearning.lil.android.profile.views.activity.StudentProfileCoCurricularActivity;
import in.securelearning.lil.android.profile.views.activity.StudentProfileGoalActivity;
import in.securelearning.lil.android.profile.views.activity.StudentProfileHobbyActivity;
import in.securelearning.lil.android.profile.views.activity.StudentProfileSubjectActivity;
import in.securelearning.lil.android.profile.views.activity.StudentPublicProfileActivity;
import in.securelearning.lil.android.profile.views.activity.UserPublicProfileActivity;
import in.securelearning.lil.android.profile.views.fragment.StudentAchievementFragment;
import in.securelearning.lil.android.profile.views.fragment.StudentParentFragment;
import in.securelearning.lil.android.profile.views.fragment.StudentPersonalFragment;
import in.securelearning.lil.android.thirdparty.views.activity.WikiHowListActivity;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public interface HomeBaseComponent extends BaseComponent {

    void inject(NavigationDrawerActivity activity);

    void inject(DashboardFragment fragment);

    void inject(GroupModel groupModel);

    void inject(SettingActivity settingActivity);

    void inject(PasswordChangeActivity passwordChangeActivity);

    void inject(HomeModel homeModel);

    void inject(ChaptersFragment chaptersFragment);

    void inject(SubjectDetailHomeFragment subjectDetailHomeFragment);

    void inject(SubjectDetailsActivity subjectDetailsActivity);

    void inject(StudentProfileActivity studentProfileActivity);

    void inject(StudentPersonalFragment studentPersonalFragment);

    void inject(StudentParentFragment studentParentFragment);

    void inject(StudentAchievementFragment studentAchievementFragment);

    void inject(StudentAnalyticsActivity studentAnalyticsActivity);

    void inject(AnalyticsModel analyticsModel);

    void inject(TimeEffortDetailActivity timeEffortDetailActivity);

    void inject(PerformanceDetailActivity performanceDetailActivity);

    void inject(ProgressDetailActivity progressDetailActivity);

    void inject(GeneratePasswordActivity generatePasswordActivity);

    void inject(StudentAnalyticsTabActivity studentAnalyticsTabActivity);

    void inject(StudentEffortFragment studentEffortFragment);

    void inject(StudentExcellenceFragment studentExcellenceFragment);

    void inject(StudentCoverageFragment studentCoverageFragment);

    void inject(StudentPerformanceFragment studentPerformanceFragment);

    void inject(HomeworkModel homeworkModel);

    void inject(SubmitHomeworkActivity submitHomeworkActivity);

    void inject(HomeworkFragment homeworkFragment);

    void inject(SubjectHomeworkFragment subjectHomeworkFragment);

    void inject(HomeworkDetailActivity homeworkDetailActivity);

    void inject(MyApplication myApplication);

    void inject(MascotActivity mascotActivity);

    void inject(MascotModel mascotModel);

    void inject(WikiHowListActivity wikiHowListActivity);

    void inject(ProfileModel profileModel);

    void inject(StudentProfileCoCurricularActivity studentProfileCoCurricularActivity);

    void inject(StudentProfileSubjectActivity studentProfileSubjectActivity);

    void inject(StudentProfileGoalActivity studentProfileGoalActivity);

    void inject(StudentProfileHobbyActivity studentProfileHobbyActivity);

    void inject(UserPublicProfileActivity userPublicProfileActivity);

    void inject(StudentPublicProfileActivity studentPublicProfileActivity);

    void inject(VocationalTopicsActivity vocationalTopicsActivity);

    void inject(PracticeAdapter practiceAdapter);

    void inject(HelpAndFAQActivity helpAndFAQActivity);

    void inject(HelpAndFaqModuleListAdapter helpAndFaqModuleListAdapter);

    void inject(LRPAModel lrpaModel);
}
