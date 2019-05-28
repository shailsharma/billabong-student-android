package in.securelearning.lil.android.syncadapter.rest;


import java.util.List;

import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationParentData;
import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationRequest;
import in.securelearning.lil.android.analytics.dataobjects.ChartDataRequest;
import in.securelearning.lil.android.analytics.dataobjects.CoverageChartData;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataRequest;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataWeekly;
import in.securelearning.lil.android.analytics.dataobjects.EffortvsPerformanceData;
import in.securelearning.lil.android.analytics.dataobjects.PerformanceChartData;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityData;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityRecentlyRead;
import in.securelearning.lil.android.base.dataobjects.AnalysisTopicCovered;
import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.CourseProgress;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupPostsNResponse;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.dataobjects.OGMetaDataResponse;
import in.securelearning.lil.android.base.dataobjects.PerformanceResponseCount;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.dataobjects.UserRating;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.courses.dataobject.CourseReview;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.dataobject.ActivityData;
import in.securelearning.lil.android.syncadapter.dataobject.AppUserAuth0;
import in.securelearning.lil.android.syncadapter.dataobject.BlogResponse;
import in.securelearning.lil.android.syncadapter.dataobject.BroadcastNotification;
import in.securelearning.lil.android.syncadapter.dataobject.EnrollTrainingResponse;
import in.securelearning.lil.android.syncadapter.dataobject.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.dataobject.PasswordChange;
import in.securelearning.lil.android.syncadapter.dataobject.PrerequisiteCoursesPostData;
import in.securelearning.lil.android.syncadapter.dataobject.RecommendedApiObject;
import in.securelearning.lil.android.syncadapter.dataobject.RolePermissions;
import in.securelearning.lil.android.syncadapter.dataobject.SearchPeriodicEventsParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchPeriodsResults;
import in.securelearning.lil.android.syncadapter.dataobject.ServerDataPackage;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPARequest;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectResult;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfile;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Json Download Interface
 */

public interface DownloadApiInterface {

    @GET("users/getCurrentLoggedInUser")
    Call<AppUserAuth0> getCurrentLoggedInUser();

    @GET("AssignmentResponses/getAssignmentResponse")
    Call<ServerDataPackage> getServerDataPackage();

    @GET("Assignments/getAssignment/{objectId}")
    Call<Assignment> getAssignment(@Path("objectId") String objectId);

    @GET("Assignments/fetchByAlias/{aliasId}")
    Call<Assignment> getByAliasAssignment(@Path("aliasId") String aliasId);

    @GET("Quizzes/fetchByAlias/{aliasId}")
    Call<Quiz> getByAliasQuiz(@Path("aliasId") String aliasId);

    @GET("AssignedBadges/{objectId}")
    Call<AssignedBadges> getAssignedBadgesCall(@Path("objectId") String objectId);

    @GET("AssignedBadges/fetchByAlias/{aliasId}")
    Call<AssignedBadges> getByAliasAssignedBadgesCall(@Path("aliasId") String aliasId);

    @GET("LearningNetworkPosts/{objectId}")
    Call<PostData> getPostData(@Path("objectId") String objectId);

    @GET("LearningNetworkPosts/fetchByAlias/{aliasId}")
    Call<PostData> getByAliasPostData(@Path("aliasId") String aliasId);

    @GET("CalendarEvents/{objectId}")
    Call<CalendarEvent> getCalendarEvent(@Path("objectId") String objectId);

    @GET("CalendarEvents/fetchByAlias/{aliasId}")
    Call<CalendarEvent> getByAliasCalendarEvent(@Path("aliasId") String aliasId);

    @GET("Curriculums/fetch")
    Call<ArrayList<Curriculum>> getCurriculumList();

    @GET("http://192.124.120.175:3000/api/LearningMaps/fetchByUser")
    Call<ArrayList<LearningMap>> getLearningMapList();

    @GET("Notifications/{objectId}")
    Call<Notification> getNotifications(@Path("objectId") String objectId);

    @GET("Notifications/{userId}/fetchNotifications")
    Call<List<Notification>> getNotificationsList(@Path("userId") String objectId);

    @POST("Notifications/fetchforAndroid")
    Call<BroadcastNotification> getBroadcastNotificationsList(@Body BroadcastNotification object);

    @GET("Groups/{objectId}")
    Call<Group> getGroup(@Path("objectId") String objectId);

    @GET("users/{objectId}/fetchUserDetails")
    Call<UserProfile> getUserProfile(@Path("objectId") String objectId);

    @POST("Utilities/getOGData")
    Call<OGMetaDataResponse> getOGData(@Body java.util.ArrayList urlList);

    @GET("Groups/fetchGroupById/{objectId}")
    Call<Group> getGroupDetails(@Path("objectId") String objectId);

    @GET("LearningNetworkPostResponses/{objectId}")
    Call<PostResponse> getPostResponse(@Path("objectId") String objectId);

    @GET("LearningNetworkPostResponses/fetchByAlias/{aliasId}")
    Call<PostResponse> getByAliasPostResponse(@Path("aliasId") String aliasId);

    @GET("")
    Call<QuestionResponse> getByAliasQuestionResponse(@Path("aliasId") String aliasId);

    @GET("Quizzes/{objectId}/fetchAllDetails")
    Call<Quiz> getQuiz(@Path("objectId") String objectId);

    @GET("Quizzes/{objectId}/fetchAllDetails")
    Call<ResponseBody> getQuizWeb(@Path("objectId") String objectId);

    @GET("LearningNetworkPosts/fetchAllPost/{objectId}")
    Call<GroupPostsNResponse> fetchAllPostNResponse(@Path("objectId") String objectId);

    @GET("AssignmentResponses/getAssignmentResponse/{objectId}")
    Call<AssignmentResponse> getAssignmentResponse(@Path("objectId") String objectId);

    @GET("DigitalBooks/getDigitalBook/{objectId}")
    Call<DigitalBook> getDigitalBook(@Path("objectId") String objectId);

    @GET("DigitalBooks/{objectId}")
    Call<ResponseBody> getDigitalBook2(@Path("objectId") String objectId);

    @GET("CustomSections/{objectId}")
    Call<ResponseBody> getCustomSectionRaw(@Path("objectId") String objectId);

    @GET("PopUps/{objectId}")
    Call<ResponseBody> getPopUps(@Path("objectId") String objectId);

    @GET("popupactivities/getPopupActivity/{objectId}")
    Call<ResponseBody> getActivityChecklistJson(@Path("objectId") String objectId);

    @GET("LILGoogleAPIs/fetchVideoDuration/{objectId}")
    Call<ResponseBody> getYoutubeVideoDuration(@Path("objectId") String objectId);

    @GET("ConceptMaps/{objectId}")
    Call<ResponseBody> getConceptMap(@Path("objectId") String objectId);

    @GET("InteractiveImages/{objectId}")
    Call<ResponseBody> getInteractiveImage(@Path("objectId") String objectId);

    @GET("VideoCourses/{objectId}/preview")
    Call<ResponseBody> getVideoCourse(@Path("objectId") String objectId);

    @GET("InteractiveVideos/fetchQuizDetails/{objectId}")
    Call<ResponseBody> getInteractiveVideo(@Path("objectId") String objectId);

    @GET("search/digital-book/about/{objectId}")
    Call<AboutCourse> getDigitalBookAbout(@Path("objectId") String objectId);

    @GET("search/popup/about/{objectId}")
    Call<AboutCourse> getPopUpsAbout(@Path("objectId") String objectId);

    @GET("search/concept-map/about/{objectId}")
    Call<AboutCourse> getConceptMapAbout(@Path("objectId") String objectId);

    @GET("search/interactive-image/about/{objectId}")
    Call<AboutCourse> getInteractiveImageAbout(@Path("objectId") String objectId);

    @GET("search/video-course/about/{objectId}")
    Call<AboutCourse> getVideoCourseAbout(@Path("objectId") String objectId);

    @GET("search/interactive-video/about/{objectId}")
    Call<AboutCourse> getInteractiveVideoAbout(@Path("objectId") String objectId);

    @GET("search/digital-book/increaseShareCount/{objectId}")
    Call<ResponseBody> increaseDigitalBookShareCount(@Path("objectId") String objectId);

    @GET("search/popup/increaseShareCount/{objectId}")
    Call<ResponseBody> increasePopupShareCount(@Path("objectId") String objectId);

    @GET("search/concept-map/increaseShareCount/{objectId}")
    Call<ResponseBody> increaseConceptMapShareCount(@Path("objectId") String objectId);

    @GET("search/interactive-image/increaseShareCount/{objectId}")
    Call<ResponseBody> increaseInteractiveImageShareCount(@Path("objectId") String objectId);

    @GET("search/video-course/increaseShareCount/{objectId}")
    Call<ResponseBody> increaseVideoCourseShareCount(@Path("objectId") String objectId);

    @GET("search/interactive-video/increaseShareCount/{objectId}")
    Call<ResponseBody> increaseInteractiveVideoShareCount(@Path("objectId") String objectId);

    @GET("search/digital-book/about/{objectId}")
    Call<ResponseBody> getDigitalBookAboutResponseBody(@Path("objectId") String objectId);

    @GET("search/popup/about/{objectId}")
    Call<ResponseBody> getPopUpsAboutResponseBody(@Path("objectId") String objectId);

    @GET("search/concept-map/about/{objectId}")
    Call<ResponseBody> getConceptMapAboutResponseBody(@Path("objectId") String objectId);

    @GET("search/interactive-image/about/{objectId}")
    Call<ResponseBody> getInteractiveImageAboutResponseBody(@Path("objectId") String objectId);

    @GET("search/video-course/about/{objectId}")
    Call<ResponseBody> getVideoCourseAboutResponseBody(@Path("objectId") String objectId);

    @GET("search/interactive-video/about/{objectId}")
    Call<ResponseBody> getInteractiveVideoAboutResponseBody(@Path("objectId") String objectId);

    @GET("DigitalBooks/recommended")
    Call<java.util.ArrayList<DigitalBook>> getDigitalBookRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("PopUps/recommended")
    Call<java.util.ArrayList<PopUps>> getPopUpRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("ConceptMaps/recommended")
    Call<java.util.ArrayList<ConceptMap>> getConceptMapRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("InteractiveImages/recommended")
    Call<java.util.ArrayList<InteractiveImage>> getInteractiveImageRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("VideoCourses/recommended")
    Call<java.util.ArrayList<VideoCourse>> getVideoCourseRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("InteractiveVideos/recommended")
    Call<java.util.ArrayList<InteractiveVideo>> getInteractiveVideoRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("DigitalBooks/recommended")
    Call<ArrayList<AboutCourse>> getAboutCourseDigitalBookRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("PopUps/recommended")
    Call<ArrayList<AboutCourse>> getAboutCoursePopUpRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("ConceptMaps/recommended")
    Call<ArrayList<AboutCourse>> getAboutCourseConceptMapRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("InteractiveImages/recommended")
    Call<ArrayList<AboutCourse>> getAboutCourseInteractiveImageRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("VideoCourses/recommended")
    Call<ArrayList<AboutCourse>> getAboutCourseVideoCourseRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("InteractiveVideos/recommended")
    Call<ArrayList<AboutCourse>> getAboutCourseInteractiveVideoRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("DigitalBooks/recommended")
    Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectDigitalBookRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("PopUps/recommended")
    Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectPopUpRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("ConceptMaps/recommended")
    Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectConceptMapRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("InteractiveImages/recommended")
    Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectInteractiveImageRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("VideoCourses/recommended")
    Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectVideoCourseRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("InteractiveVideos/recommended")
    Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectInteractiveVideoRecommended(@Query("limit") int limit, @Query("skip") int skip);

    @GET("DigitalBooks/favorite")
    Call<java.util.ArrayList<DigitalBook>> getDigitalBookFavorite(@Query("limit") int limit, @Query("skip") int skip);

    @GET("PopUps/favorite")
    Call<java.util.ArrayList<PopUps>> getPopUpFavorite(@Query("limit") int limit, @Query("skip") int skip);

    @GET("ConceptMaps/favorite")
    Call<java.util.ArrayList<ConceptMap>> getConceptMapFavorite(@Query("limit") int limit, @Query("skip") int skip);

    @GET("InteractiveImages/favorite")
    Call<java.util.ArrayList<InteractiveImage>> getInteractiveImageFavorite(@Query("limit") int limit, @Query("skip") int skip);

    @GET("VideoCourses/favorite")
    Call<java.util.ArrayList<VideoCourse>> getVideoCourseFavorite(@Query("limit") int limit, @Query("skip") int skip);

    @GET("InteractiveVideos/favorite")
    Call<java.util.ArrayList<InteractiveVideo>> getInteractiveVideoFavorite(@Query("limit") int limit, @Query("skip") int skip);

    @GET("users/fetchFavourites")
    Call<java.util.ArrayList<RecommendedApiObject>> getCourseFavorite(@Query("limit") int limit, @Query("skip") int skip);

    @GET("CourseReviews/addToFavourites/{objectId}")
    Call<ResponseBody> addCourseFavorite(@Path("objectId") String objectId, @Query("value") boolean value, @Query("courseType") String courseType);

    @GET("MicroCourseReviews/addToFavourites/{objectId}")
    Call<ResponseBody> addMicroCourseFavorite(@Path("objectId") String objectId, @Query("value") boolean value, @Query("microCourseType") String courseType);

    @POST("CourseReviews/addRatings/{objectId}")
    Call<UserRating> addRating(@Path("objectId") String objectId, @Body CourseReview courseReview);

    @GET("Resources/{objectId}")
    Call<ResponseBody> getResource(@Path("objectId") String objectId);

    @GET("search/blog/getPublishedBlogs/{skip}/{limit}")
    Call<BlogResponse> getBlogList(@Path("skip") int skip, @Path("limit") int limit);

    @GET("Blog/getBlog/{objectId}")
    Call<Blog> getBlog(@Path("objectId") String objectId);

    @GET("Blogs/getBlogDetails/{objectId}")
    Call<ResponseBody> getBlogDetails(@Path("objectId") String objectId);

    @GET("search/user/{objectId}/details")
    Call<ResponseBody> getBlogUserDetails(@Path("objectId") String objectId);

    @POST("BlogComments/getCommentsByBlogId")
    Call<ArrayList<BlogComment>> getBlogComments(@Body BlogComment blogComment);

    @POST("search/periodic-event/fetch")
    Call<SearchPeriodsResults> getPeriodicEventsListBulk(@Body SearchPeriodicEventsParams params);

    @POST("search/periodic-event/fetch")
    Call<ArrayList<PeriodNew>> getPeriodicEventsList(@Body SearchPeriodicEventsParams params);

    @GET("BusRoutes/group/{objectId}")
    Call<ArrayList<TrackingRoute>> getRoutesFromGroupId(@Path("objectId") String objectId);

    @GET("listr")
    Call<java.util.ArrayList<FavouriteResource>> getVideoData(@Body FavouriteResource video);

    @GET("InstituteMappings/device/institute/{instituteId}/grade/{gradeId}/section/{sectionId}/subjects")
    Call<java.util.ArrayList<Subject>> getSubjectFromInstituteGradeSection(@Path("instituteId") String instituteId, @Path("gradeId") String gradeId, @Path("sectionId") String sectionId);

    @GET("Institutes/{instituteId}")
    Call<Institution> getInstitute(@Path("instituteId") String id);

    @GET("rolemanagers/permissions")
    Call<RolePermissions> fetchRolePermissions();

    @GET("search/training/getmylist/{skip}/{limit}")
    Call<java.util.ArrayList<Training>> fetchTrainings(@Path("skip") int skip, @Path("limit") int limit);

    @GET("search/training/upcominglist/{skip}/{limit}")
    Call<java.util.ArrayList<Training>> fetchUpcomingTrainings(@Path("skip") int skip, @Path("limit") int limit);

    @GET("search/training/{id}/details")
    Call<Training> fetchTraining(@Path("id") String id);

    @POST("search/training/courseDetails")
    Call<java.util.ArrayList<AboutCourseExt>> getPrerequisiteCourses(@Body PrerequisiteCoursesPostData prerequisiteCoursesPostData);

    @GET("trainings/enroll/{trainingId}")
    Call<EnrollTrainingResponse> enrollTraining(@Path("trainingId") String trainingId);

    @PUT("users")
    Call<ResponseBody> changePassword(@Body PasswordChange passwordChange);

    @GET("search/featured-card/{id}/preview")
    Call<MicroLearningCourse> getRapidLearningCourse(@Path("id") String id);

    @GET("featuredcards")
    Call<java.util.ArrayList<MicroLearningCourse>> getMicroLearningCourseList();

    @POST("devicecourseprogresses")
    Call<ResponseBody> uploadCourseProgress(@Body CourseProgress courseProgress);

    //BY rupsi for download activity data

    @POST("UserBrowseHistories/fetchActivityGraph")
    Call<java.util.ArrayList<AnalysisActivityData>> fetchActivityData(@Body ActivityData activityData);

    @POST("UserBrowseHistories/fetchLearningGraph")
    Call<java.util.ArrayList<AnalysisActivityData>> fetchLearningData(@Body ActivityData activityData);

    @GET("UserBrowseHistories/fetchRecentlyRead/{subjectId}/{skip}/{limit}")
    Call<java.util.ArrayList<AnalysisActivityRecentlyRead>> getRecentlyRead(@Path("subjectId") String subid, @Path("skip") int skip, @Path("limit") int limit);

    @GET("UserBrowseHistories/fetchTopicCovered/{subjectId}/{skip}/{limit}")
    Call<java.util.ArrayList<AnalysisTopicCovered>> getTopicCovered(@Path("subjectId") String subid, @Path("skip") int skip, @Path("limit") int limit);

    @GET("UserBrowseHistories/aggregatedResponseCount/{subjectId}")
    Call<PerformanceResponseCount> getaggregatedResponseCount(@Path("subjectId") String subid);

    /*Api path for Today Recaps*/
    @GET("LessonPlanConfigurations/fetchForToday/recap")
    Call<java.util.ArrayList<LessonPlanMinimal>> getTodayRecap();

    /*Api path for Today Lesson Plans*/
    @GET("LessonPlanConfigurations/fetchForToday/lessonPlan")
    Call<java.util.ArrayList<LessonPlanMinimal>> getTodayLessonPlan();

    @POST("lessonPlanConfigurations/userSubjectTopic")
    Call<java.util.ArrayList<LessonPlanChapterResult>> getChaptersResult();

    /*Api path to get my subjects*/
    @POST("lessonPlanConfigurations/userSubjectTopic")
    Call<LessonPlanSubjectResult> getMySubjects(@Body LessonPlanSubjectPost lessonPlanSubjectPost);


    /*Api path for Today Lesson Plans chapters*/
    @POST("lessonPlanConfigurations/userSubjectTopic")
    Call<LessonPlanChapterResult> getChapterResult(@Body LessonPlanChapterPost lessonPlanChapterPost);

    @GET("lessonPlanConfigurations/subject/{subjectId}")
    Call<LessonPlanSubjectDetails> getSubjectDetails(@Path("subjectId") String subjectId);

    /*Api to fetch Learn, Reinforce, Practice and Apply by topicId and type*/
    @POST("search/lessonPlanConfiguration/lrpa")
    Call<LRPAResult> fetchLRPA(@Body LRPARequest lrpaRequest);

    @GET("users/profile")
    Call<StudentProfile> getStudentProfile();

    /*Api to fetch third party meta information*/
    @POST("TPCurriculumMappings/fetchTPCurriculumMapping")
    Call<java.util.ArrayList<String>> fetchThirdPartyMapping(@Body ThirdPartyMapping thirdPartyMapping);

    /*Api to fetch coverage data for particular subject*/
    @POST("users/getCoverage")
    Call<java.util.ArrayList<CoverageChartData>> fetchSubjectWiseCoverageData(@Body ChartDataRequest chartDataRequest);

    /*Api to fetch coverage data for all subjects*/
    @POST("users/getCoverage")
    Call<java.util.ArrayList<CoverageChartData>> fetchAllSubjectCoverageData();

    /*Api to fetch performance data for particular subject*/
    @POST("users/getPerformance")
    Call<java.util.ArrayList<PerformanceChartData>> fetchSubjectWisePerformanceData(@Body ChartDataRequest chartDataRequest);

    /*Api to fetch performance data for all subjects*/
    @POST("users/getPerformance")
    Call<java.util.ArrayList<PerformanceChartData>> fetchAllSubjectPerformanceData();

    /*Api to fetch effort (time spent) data for all subjects*/
    @POST("userlogs/getUserSubjectsTimeSpent")
    Call<EffortChartDataParent> fetchEffortData(@Body EffortChartDataRequest effortChartDataRequest);

    /*Api to fetch effort (time spent) data for all subjects*/
    @GET("users/getSubjectLevelPerformance")
    Call<java.util.ArrayList<EffortvsPerformanceData>> fetchEffortvsPerformanceData();

    /*Api to fetch effort (time spent) data for individual subject*/
    @POST("userlogs/getUserTopicTimeSpent")
    Call<EffortChartDataParent> fetchSubjectWiseEffortData(@Body EffortChartDataRequest effortChartDataRequest);

    /*Api to fetch effort (time spent) weekly data for individual subject*/
    @POST("userlogs/getUserDailyTimeSpentWeekly")
    Call<java.util.ArrayList<EffortChartDataWeekly>> fetchWeeklyEffortData(@Body EffortChartDataRequest effortChartDataRequest);

    /*Api to fetch student's achievements*/
    @GET("UserScores/detail")
    Call<StudentAchievement> fetchStudentAchievements();

    /*Api to fetch chart configuration for performance and coverage*/
    @POST("GlobalConfigs/fetchConfig")
    Call<ChartConfigurationParentData> fetchChartConfiguration(@Body ChartConfigurationRequest chartConfigurationRequest);
}