package in.securelearning.lil.android.home.di.component;


import in.securelearning.lil.android.analytics.activity.PerformanceDetailActivity;
import in.securelearning.lil.android.analytics.activity.ProgressDetailActivity;
import in.securelearning.lil.android.analytics.activity.StudentAnalyticsActivity;
import in.securelearning.lil.android.analytics.activity.StudentAnalyticsTabActivity;
import in.securelearning.lil.android.analytics.activity.TimeEffortDetailActivity;
import in.securelearning.lil.android.analytics.fragment.StudentCoverageFragment;
import in.securelearning.lil.android.analytics.fragment.StudentEffortFragment;
import in.securelearning.lil.android.analytics.fragment.StudentExcellenceFragment;
import in.securelearning.lil.android.analytics.fragment.StudentPerformanceFragment;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.base.model.GroupModel;
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
import in.securelearning.lil.android.home.views.activity.SampleWebActivity;
import in.securelearning.lil.android.home.views.activity.SearchResourcesListFilterActivity;
import in.securelearning.lil.android.home.views.activity.SearchResultListActivity;
import in.securelearning.lil.android.home.views.activity.SearchResultListFilterActivity;
import in.securelearning.lil.android.home.views.activity.SessionDetailActivity;
import in.securelearning.lil.android.home.views.activity.SettingNewActivity;
import in.securelearning.lil.android.home.views.activity.StudentProfileActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailSearchCourseActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailSearchResourceActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailsActivity;
import in.securelearning.lil.android.home.views.activity.TopicListActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileEditActivity;
import in.securelearning.lil.android.home.views.fragment.AvailableTrainingsFragment;
import in.securelearning.lil.android.home.views.fragment.ChaptersFragment;
import in.securelearning.lil.android.home.views.fragment.ClassDetailsFragments;
import in.securelearning.lil.android.home.views.fragment.DashboardFragment;
import in.securelearning.lil.android.home.views.fragment.LessonPlanFragment;
import in.securelearning.lil.android.home.views.fragment.MyTrainingsFragment;
import in.securelearning.lil.android.home.views.fragment.PeriodicFragmentForClassDetails;
import in.securelearning.lil.android.home.views.fragment.RecapFragment;
import in.securelearning.lil.android.home.views.fragment.ResourceFragment;
import in.securelearning.lil.android.home.views.fragment.SampleClassPlannerFragment;
import in.securelearning.lil.android.home.views.fragment.StudentAchievementFragment;
import in.securelearning.lil.android.home.views.fragment.StudentParentFragment;
import in.securelearning.lil.android.home.views.fragment.StudentPersonalFragment;
import in.securelearning.lil.android.home.views.fragment.SubjectDetailHomeFragment;
import in.securelearning.lil.android.home.views.fragment.TraineeLearningObjectiveFragment;
import in.securelearning.lil.android.home.views.fragment.TraineePrerequisiteCoursesFragment;
import in.securelearning.lil.android.home.views.fragment.TraineeSessionsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainingDetailsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainingStaticDetailsFragment;
import in.securelearning.lil.android.login.views.activity.GeneratePasswordActivity;
import in.securelearning.lil.android.mindspark.model.MindSparkModel;
import in.securelearning.lil.android.mindspark.views.activity.MindSparkAllTopicListActivity;
import in.securelearning.lil.android.mindspark.views.activity.MindSparkPlayerActivity;

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

    void inject(ClassDetailsFragments classDetailsFragments);

    void inject(PeriodicFragmentForClassDetails periodicFragmentForClassDetails);

    void inject(SettingNewActivity settingNewActivity);

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

    void inject(AvailableTrainingsFragment availableTrainingsFragment);

    void inject(MyTrainingsFragment myTrainingsFragment);

    void inject(LessonPlanFragment lessonPlanFragment);

    void inject(SampleClassPlannerFragment sampleClassPlannerFragment);

    void inject(RecapFragment recapFragment);

    void inject(PasswordChangeActivity passwordChangeActivity);

    void inject(TopicListActivity topicListActivity);

    void inject(FlavorHomeModel flavorHomeModel);

    void inject(ChaptersFragment chaptersFragment);

    void inject(SampleWebActivity sampleWebActivity);

    void inject(SubjectDetailHomeFragment subjectDetailHomeFragment);

    void inject(SubjectDetailsActivity subjectDetailsActivity);

    void inject(StudentProfileActivity studentProfileActivity);

    void inject(StudentPersonalFragment studentPersonalFragment);

    void inject(StudentParentFragment studentParentFragment);

    void inject(MindSparkAllTopicListActivity mindSparkAllTopicListActivity);

    void inject(StudentAchievementFragment studentAchievementFragment);

    void inject(StudentAnalyticsActivity studentAnalyticsActivity);

    void inject(AnalyticsModel analyticsModel);

    void inject(TimeEffortDetailActivity timeEffortDetailActivity);

    void inject(PerformanceDetailActivity performanceDetailActivity);

    void inject(ProgressDetailActivity progressDetailActivity);

    void inject(GeneratePasswordActivity generatePasswordActivity);

    void inject(MindSparkModel mindSparkModel);

    void inject(MindSparkPlayerActivity mindSparkPlayerActivity);

    void inject(StudentAnalyticsTabActivity studentAnalyticsTabActivity);

    void inject(StudentEffortFragment studentEffortFragment);

    void inject(StudentExcellenceFragment studentExcellenceFragment);

    void inject(StudentCoverageFragment studentCoverageFragment);

    void inject(StudentPerformanceFragment studentPerformanceFragment);
}
