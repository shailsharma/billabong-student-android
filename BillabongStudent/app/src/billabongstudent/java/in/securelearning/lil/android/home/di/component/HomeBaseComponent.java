package in.securelearning.lil.android.home.di.component;


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
import in.securelearning.lil.android.home.views.activity.ClassDetailsActivity;
import in.securelearning.lil.android.home.views.activity.ClassPlannerActivity;
import in.securelearning.lil.android.home.views.activity.LearningMapForSkillChartNewActivity;
import in.securelearning.lil.android.home.views.activity.LearningMapForTopicBarChartActivity;
import in.securelearning.lil.android.home.views.activity.LearningMapForTopicChartActivity;
import in.securelearning.lil.android.home.views.activity.LearningMapNewActivity;
import in.securelearning.lil.android.home.views.activity.MindSparkAllTopicListActivity;
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
import in.securelearning.lil.android.home.views.activity.StudentUserProfileActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailSearchCourseActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailSearchResourceActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailsActivity;
import in.securelearning.lil.android.home.views.activity.TopicListActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileEditActivity;
import in.securelearning.lil.android.home.views.fragment.AnalysisActivityFragment;
import in.securelearning.lil.android.home.views.fragment.AnalysisLearningFragment;
import in.securelearning.lil.android.home.views.fragment.AnalysisPerformanceFragment;
import in.securelearning.lil.android.home.views.fragment.AvailableTrainingsFragment;
import in.securelearning.lil.android.home.views.fragment.ChaptersFragment;
import in.securelearning.lil.android.home.views.fragment.ClassDetailsFragments;
import in.securelearning.lil.android.home.views.fragment.CurriculumProgressFragmentForClassDetail;
import in.securelearning.lil.android.home.views.fragment.DashboardFragment;
import in.securelearning.lil.android.home.views.fragment.LearningMapFinalFragment;
import in.securelearning.lil.android.home.views.fragment.LearningMapFragmentForClassDetails;
import in.securelearning.lil.android.home.views.fragment.LearningMapOldFragment;
import in.securelearning.lil.android.home.views.fragment.LearningMapStudentFragment;
import in.securelearning.lil.android.home.views.fragment.LessonPlanFragment;
import in.securelearning.lil.android.home.views.fragment.MyTrainingsFragment;
import in.securelearning.lil.android.home.views.fragment.PeriodicFragmentForClassDetails;
import in.securelearning.lil.android.home.views.fragment.RecapFragment;
import in.securelearning.lil.android.home.views.fragment.ResourceFragment;
import in.securelearning.lil.android.home.views.fragment.SampleClassPlannerFragment;
import in.securelearning.lil.android.home.views.fragment.SubjectDetailHomeFragment;
import in.securelearning.lil.android.home.views.fragment.TeacherMapFragment;
import in.securelearning.lil.android.home.views.fragment.TeacherMapFragmentForClassDetails;
import in.securelearning.lil.android.home.views.fragment.TraineeLearningObjectiveFragment;
import in.securelearning.lil.android.home.views.fragment.TraineePerformanceFragment;
import in.securelearning.lil.android.home.views.fragment.TraineePrerequisiteCoursesFragment;
import in.securelearning.lil.android.home.views.fragment.TraineeSessionsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainingDetailsFragment;
import in.securelearning.lil.android.home.views.fragment.TrainingStaticDetailsFragment;
import in.securelearning.lil.android.home.views.fragment.UserProfileParentFragment;
import in.securelearning.lil.android.home.views.fragment.UserProfilePersonalFragment;
import in.securelearning.lil.android.player.views.activity.PracticePlayerActivity;

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

    void inject(LearningMapForTopicChartActivity learningMapForTopicChartActivity);

    void inject(LearningMapForSkillChartNewActivity learningMapForSkillChartNewActivity);

    void inject(LearningMapFinalFragment learningMapFinalFragment);

    void inject(LearningMapForTopicBarChartActivity learningMapForTopicBarChartActivity);

    void inject(SearchResultListFilterActivity object);

    void inject(SearchResourcesListFilterActivity object);

    void inject(LearningMapOldFragment learningMapOldFragment);

    void inject(CalendarActivityNew calendarActivityNew);

    void inject(TeacherMapFragment teacherMapFragment);

    void inject(ClassDetailsFragments classDetailsFragments);

    void inject(LearningMapFragmentForClassDetails learningMapFragmentForClassDetails);

    void inject(TeacherMapFragmentForClassDetails teacherMapFragmentForClassDetails);

    void inject(PeriodicFragmentForClassDetails periodicFragmentForClassDetails);

    void inject(ClassDetailsActivity classDetailsActivity);

    void inject(LearningMapStudentFragment learningMapStudentFragment);

    void inject(LearningMapNewActivity learningMapNewActivity);

    void inject(SettingNewActivity settingNewActivity);

    void inject(NewSearchCourseFilterActivity newSearchCourseFilterActivity);

    void inject(NewSearchResourceFilterActivity newSearchResourceFilterActivity);

    void inject(SubjectDetailSearchCourseActivity subjectDetailSearchActivity1);

    void inject(SubjectDetailSearchResourceActivity subjectDetailSearchResourceActivity);

    void inject(UserProfileEditActivity userProfileEditActivity);

    void inject(TrainingDetailsFragment trainingDetailsFragment);

    void inject(TraineePerformanceFragment traineePerformanceFragment);

    void inject(TrainingStaticDetailsFragment trainingStaticDetailsFragment);

    void inject(TraineePrerequisiteCoursesFragment traineePrerequisiteCoursesFragment);

    void inject(TraineeLearningObjectiveFragment traineeLearningObjectiveFragment);

    void inject(TraineeSessionsFragment traineeSessionsFragment);

    void inject(SessionDetailActivity sessionDetailActivity);

    void inject(AvailableTrainingsFragment availableTrainingsFragment);

    void inject(MyTrainingsFragment myTrainingsFragment);

    void inject(CurriculumProgressFragmentForClassDetail curriculumProgressFragmentForClassDetail);

    void inject(LessonPlanFragment lessonPlanFragment);

    void inject(SampleClassPlannerFragment sampleClassPlannerFragment);

    void inject(RecapFragment recapFragment);

    void inject(PasswordChangeActivity passwordChangeActivity);

    void inject(ClassPlannerActivity classPlannerActivity);

    void inject(TopicListActivity topicListActivity);

    void inject(AnalysisActivityFragment analysisActivityFragment);

    void inject(AnalysisLearningFragment analysisLearningFragment);

    void inject(AnalysisPerformanceFragment analysisPerformanceFragment);

    void inject(FlavorHomeModel flavorHomeModel);

    void inject(ChaptersFragment chaptersFragment);

    void inject(SampleWebActivity sampleWebActivity);

    void inject(SubjectDetailHomeFragment subjectDetailHomeFragment);

    void inject(SubjectDetailsActivity subjectDetailsActivity);

    void inject(PracticePlayerActivity questionPlayerActivity);

    void inject(StudentUserProfileActivity studentUserProfileActivity);

    void inject(UserProfilePersonalFragment userProfilePersonalFragment);

    void inject(UserProfileParentFragment userProfileParentFragment);

    void inject(MindSparkAllTopicListActivity mindSparkAllTopicListActivity);
}
