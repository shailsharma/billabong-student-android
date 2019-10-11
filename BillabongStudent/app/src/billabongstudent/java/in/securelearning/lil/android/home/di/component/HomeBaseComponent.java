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
import in.securelearning.lil.android.home.model.CalendarEventModel;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.model.SearchModel;
import in.securelearning.lil.android.home.views.activity.ActivityCreationActivity;
import in.securelearning.lil.android.home.views.activity.AnnouncementCreationActivity;
import in.securelearning.lil.android.home.views.activity.CalendarActivityNew;
import in.securelearning.lil.android.home.views.activity.CalendarEventListActivity;
import in.securelearning.lil.android.home.views.activity.CalendarPeriodListActivity;
import in.securelearning.lil.android.home.views.activity.NavigationDrawerActivity;
import in.securelearning.lil.android.home.views.activity.NewSearchCourseFilterActivity;
import in.securelearning.lil.android.home.views.activity.NewSearchResourceFilterActivity;
import in.securelearning.lil.android.home.views.activity.PasswordChangeActivity;
import in.securelearning.lil.android.home.views.activity.PersonalEventCreationActivity;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.SearchResourcesListFilterActivity;
import in.securelearning.lil.android.home.views.activity.SearchResultListActivity;
import in.securelearning.lil.android.home.views.activity.SearchResultListFilterActivity;
import in.securelearning.lil.android.home.views.activity.SessionDetailActivity;
import in.securelearning.lil.android.home.views.activity.SettingActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailSearchCourseActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailSearchResourceActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailsActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileEditActivity;
import in.securelearning.lil.android.home.views.activity.VocationalTopicsActivity;
import in.securelearning.lil.android.home.views.activity.WikiHowListActivity;
import in.securelearning.lil.android.home.views.adapter.LRAAdapter;
import in.securelearning.lil.android.home.views.adapter.PracticeAdapter;
import in.securelearning.lil.android.home.views.fragment.ChaptersFragment;
import in.securelearning.lil.android.home.views.fragment.DashboardFragment;
import in.securelearning.lil.android.home.views.fragment.PeriodicFragmentForClassDetails;
import in.securelearning.lil.android.home.views.fragment.ResourceFragment;
import in.securelearning.lil.android.home.views.fragment.SubjectDetailHomeFragment;
import in.securelearning.lil.android.home.views.fragment.SubjectHomeworkFragment;
import in.securelearning.lil.android.home.views.fragment.TraineeLearningObjectiveFragment;
import in.securelearning.lil.android.home.views.fragment.TraineePrerequisiteCoursesFragment;
import in.securelearning.lil.android.home.views.fragment.TraineeSessionsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainingDetailsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainingStaticDetailsFragment;
import in.securelearning.lil.android.homework.model.HomeworkModel;
import in.securelearning.lil.android.homework.views.activity.HomeworkDetailActivity;
import in.securelearning.lil.android.homework.views.activity.SubmitHomeworkActivity;
import in.securelearning.lil.android.homework.views.fragment.HomeworkFragment;
import in.securelearning.lil.android.login.views.activity.GeneratePasswordActivity;
import in.securelearning.lil.android.player.view.adapter.QuestionResourceAdapter;
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

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public interface HomeBaseComponent extends BaseComponent {

    void inject(SearchModel model);

    void inject(HomeModel model);

    void inject(ResourceFragment fragment);

    void inject(SearchResultListActivity activity);

    void inject(NavigationDrawerActivity activity);

    void inject(PlayVideoFullScreenActivity activity);

    void inject(DashboardFragment fragment);

    void inject(UserProfileActivity calendarActivity);

    void inject(CalendarEventModel calendarEventModel);

    void inject(GroupModel groupModel);

    void inject(PersonalEventCreationActivity personalEventCreationActivity);

    void inject(AnnouncementCreationActivity announcementCreationActivity);

    void inject(ActivityCreationActivity activityCreationActivity);

    void inject(CalendarPeriodListActivity calendarPeriodListActivity);

    void inject(CalendarEventListActivity calendarEventListActivity);

    void inject(SearchResultListFilterActivity object);

    void inject(SearchResourcesListFilterActivity object);

    void inject(CalendarActivityNew calendarActivityNew);

    void inject(PeriodicFragmentForClassDetails periodicFragmentForClassDetails);

    void inject(SettingActivity settingActivity);

    void inject(NewSearchCourseFilterActivity newSearchCourseFilterActivity);

    void inject(NewSearchResourceFilterActivity newSearchResourceFilterActivity);

    void inject(SubjectDetailSearchCourseActivity subjectDetailSearchActivity1);

    void inject(SubjectDetailSearchResourceActivity subjectDetailSearchResourceActivity);

    void inject(UserProfileEditActivity userProfileEditActivity);

    void inject(TrainingDetailsFragment trainingDetailsFragment);

    void inject(TrainingStaticDetailsFragment trainingStaticDetailsFragment);

    void inject(TraineePrerequisiteCoursesFragment traineePrerequisiteCoursesFragment);

    void inject(TraineeLearningObjectiveFragment traineeLearningObjectiveFragment);

    void inject(TraineeSessionsFragment traineeSessionsFragment);

    void inject(SessionDetailActivity sessionDetailActivity);

    void inject(PasswordChangeActivity passwordChangeActivity);

    void inject(FlavorHomeModel flavorHomeModel);

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

    void inject(LRAAdapter LRAAdapter);

    void inject(PracticeAdapter practiceAdapter);
}
