package in.securelearning.lil.android.syncadapter.model;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.ChartDataRequest;
import in.securelearning.lil.android.analytics.dataobjects.CoverageChartData;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataRequest;
import in.securelearning.lil.android.analytics.dataobjects.EffortvsPerformanceData;
import in.securelearning.lil.android.analytics.dataobjects.PerformanceChartData;
import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityData;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityRecentlyRead;
import in.securelearning.lil.android.base.dataobjects.AnalysisTopicCovered;
import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.CourseProgress;
import in.securelearning.lil.android.base.dataobjects.Credentials;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupPostsNResponse;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.dataobjects.OGMetaDataResponse;
import in.securelearning.lil.android.base.dataobjects.PerformanceResponseCount;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.UserCourseProgress;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.gamification.dataobject.GamificationBonus;
import in.securelearning.lil.android.gamification.dataobject.GamificationSurvey;
import in.securelearning.lil.android.homework.dataobject.AssignedHomeworkParent;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.dataobject.HomeworkSubmitResponse;
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideo;
import in.securelearning.lil.android.player.dataobject.PlayerFilterParent;
import in.securelearning.lil.android.player.dataobject.PracticeParent;
import in.securelearning.lil.android.player.dataobject.PracticeQuestionResponse;
import in.securelearning.lil.android.player.dataobject.QuestionFeedback;
import in.securelearning.lil.android.player.dataobject.QuizConfigurationRequest;
import in.securelearning.lil.android.player.dataobject.QuizConfigurationResponse;
import in.securelearning.lil.android.player.dataobject.QuizQuestionResponse;
import in.securelearning.lil.android.player.dataobject.QuizResponsePost;
import in.securelearning.lil.android.player.dataobject.RevisionResponse;
import in.securelearning.lil.android.player.dataobject.RevisionResponsePost;
import in.securelearning.lil.android.player.dataobject.TotalPointPost;
import in.securelearning.lil.android.player.dataobject.TotalPointResponse;
import in.securelearning.lil.android.profile.dataobject.TeacherProfile;
import in.securelearning.lil.android.profile.dataobject.UserInterest;
import in.securelearning.lil.android.profile.dataobject.UserInterestPost;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.ActivityData;
import in.securelearning.lil.android.syncadapter.dataobjects.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobjects.BroadcastNotification;
import in.securelearning.lil.android.syncadapter.dataobjects.CloudinaryFileInner;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.HelpAndFaqCategory;
import in.securelearning.lil.android.syncadapter.dataobjects.IdNameObject;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPARequest;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectResult;
import in.securelearning.lil.android.syncadapter.dataobjects.Message;
import in.securelearning.lil.android.syncadapter.dataobjects.MessageData;
import in.securelearning.lil.android.syncadapter.dataobjects.MessageDataPayload;
import in.securelearning.lil.android.syncadapter.dataobjects.MessageMultipleTopic;
import in.securelearning.lil.android.syncadapter.dataobjects.MessageResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.PasswordChange;
import in.securelearning.lil.android.syncadapter.dataobjects.QuizResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.RefreshFCMToken;
import in.securelearning.lil.android.syncadapter.dataobjects.RequestOTP;
import in.securelearning.lil.android.syncadapter.dataobjects.RequestOTPResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.RevisionSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.RolePermissions;
import in.securelearning.lil.android.syncadapter.dataobjects.SearchPeriodicEventsFilterParams;
import in.securelearning.lil.android.syncadapter.dataobjects.SearchPeriodicEventsParams;
import in.securelearning.lil.android.syncadapter.dataobjects.SearchPeriodsResults;
import in.securelearning.lil.android.syncadapter.dataobjects.ServerDataPackage;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfilePicturePost;
import in.securelearning.lil.android.syncadapter.dataobjects.TeacherGradeMapping;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.dataobjects.UserChallengePost;
import in.securelearning.lil.android.syncadapter.dataobjects.UserTimeSpent;
import in.securelearning.lil.android.syncadapter.dataobjects.VideoForDayParent;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopic;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopicRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import in.securelearning.lil.android.syncadapter.rest.BaseApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DirectUploadApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DownloadFilesApiInterface;
import in.securelearning.lil.android.syncadapter.rest.FCMApiInterface;
import in.securelearning.lil.android.syncadapter.rest.LogiqidsApiInterface;
import in.securelearning.lil.android.syncadapter.rest.MindSparkApiInterface;
import in.securelearning.lil.android.syncadapter.rest.NewUploadApiInterface;
import in.securelearning.lil.android.syncadapter.rest.SearchApiInterface;
import in.securelearning.lil.android.syncadapter.rest.SyncSuccessApiInterface;
import in.securelearning.lil.android.syncadapter.rest.UploadApiInterface;
import in.securelearning.lil.android.syncadapter.rest.UploadFilesApiInterface;
import in.securelearning.lil.android.syncadapter.rest.WikiHowApiInterface;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiQidsChallengeParent;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsLoginResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsWorksheetResult;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkLoginRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionParent;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionSubmit;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkTopicListRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkTopicResult;
import in.securelearning.lil.android.thirdparty.dataobjects.TPCurriculumResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Model for Network Access.
 */
public class NetworkModel extends BaseModel {
    public final String TAG = this.getClass().getCanonicalName();
    public final static String TYPE_TRACKING = "tracking";
    public final static String TYPE_POST_DATA = "LearningNetworkPost";
    public final static String TYPE_POST_RESPONSE = "LearningNetworkPostResponse";
    public final static String TYPE_HOMEWORK = "homework";
    public final static String TYPE_ASSIGNMENT_RESPONSE = "AssignmentResponse";
    public final static String TYPE_CALENDAR_EVENT = "calendarEvent";
    public final static String TYPE_USER_PROFILE = "userProfile";
    public final static String TYPE_GROUP_UPDATE = "groupUpdate";
    public final static String TYPE_INSTITUTE_UPDATE = "instituteUpdate";
    public final static String TYPE_USER_ARCHIVED = "user_archived";

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    BaseApiInterface mBaseApiInterface;

    @Inject
    UploadApiInterface mUploadApiInterface;

    @Inject
    DirectUploadApiInterface mDirectUploadApiInterface;

    @Inject
    NewUploadApiInterface mNewUploadApiInterface;

    @Inject
    FCMApiInterface mFCMApiInterface;

    @Inject
    DownloadApiInterface mDownloadApiInterface;

    @Inject
    SearchApiInterface mSearchApiInterface;

    @Inject
    DownloadFilesApiInterface mDownloadFilesApiInterface;

    @Inject
    UploadFilesApiInterface mUploadFileApiInterface;

    @Inject
    SyncSuccessApiInterface mSyncSuccessApiInterface;

    @Inject
    MindSparkApiInterface mMindSparkApiInterface;

    @Inject
    LogiqidsApiInterface mLogiqidsApiInterface;

    @Inject
    WikiHowApiInterface mWikiHowApiInterface;

    public NetworkModel() {
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }


    /**
     * send sync success
     *
     * @param objectIds id successfully synced
     */
    public void sendSyncSuccess(List<String> objectIds) {
        for (String s : objectIds) {
            try {
                mSyncSuccessApiInterface.postSyncSuccess(s).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * post quiz
     *
     * @param quiz to post
     * @return network call
     */
    public Call<Quiz> postQuiz(Quiz quiz) {
        return mNewUploadApiInterface.uploadQuiz(quiz);
    }

    /**
     * update quiz
     *
     * @param quiz to update
     * @return network call
     */
    public Call<Quiz> updateQuiz(Quiz quiz) {
        return mNewUploadApiInterface.updateQuiz(quiz, quiz.getObjectId());
    }

    /**
     * post assignment
     *
     * @param assignment to post
     * @return network call
     */
    public Call<Assignment> postAssignment(Assignment assignment) {
        return mNewUploadApiInterface.uploadAssignment(assignment);
    }

    /**
     * post assignment response
     *
     * @param assignmentResponse to post
     * @return network call
     */
    public Call<AssignmentResponse> postAssignmentResponse(AssignmentResponse assignmentResponse) {
        return mUploadApiInterface.uploadNewAssignmentResponse(assignmentResponse);

    }

    public Call<PostResponse> uploadPostResponse(PostResponse postResponse) {
        return mNewUploadApiInterface.uploadPostResponseCall(postResponse);
    }

    public Call<AssignedBadges> uploadAssignedBadge(AssignedBadges assignedBadges) {
        return mNewUploadApiInterface.uploadAssignedBadgeCall(assignedBadges);
    }

    public Call<PostData> postLearningNetworkPostData(PostData postData) {
        return mNewUploadApiInterface.uploadPostData(postData);
    }

    public Call<PostData> getLearningNetworkPostData(String aliasId) {
        return mDownloadApiInterface.getByAliasPostData(aliasId);
    }

    public Call<CalendarEvent> postCalenderEvent(CalendarEvent calendarEvent) {
        return mNewUploadApiInterface.uploadCalendarEvent(calendarEvent);
    }

    public Call<QuestionResponse> uploadQuestionResponse(QuestionResponse questionResponse) {
        return mNewUploadApiInterface.uploadQuestionResponseCall(questionResponse);
    }

    public Call<MessageResponse> sendDataUsingFCM(String group, Object msdData, String type, String objectId, String isoDate) {
        return mFCMApiInterface.send(new Message("/topics/" + BuildConfig.SUBSCRIBE_FCM_PREFIX + group, null, new MessageDataPayload(msdData, type, objectId, isoDate)));
    }

    public Call<MessageResponse> sendDataToMultipleTopicsUsingFCM(String group, Object msdData, String type) {
        return mFCMApiInterface.send(new MessageMultipleTopic(group, null, new MessageDataPayload(msdData, type)));
    }

    public Call<MessageResponse> sendNotificationUsingFCM(String group, String notificationData, Object msdData, String type, String title) {
        return mFCMApiInterface.send(new Message("/topics/" + BuildConfig.SUBSCRIBE_FCM_PREFIX + group, new MessageData(notificationData, title), new MessageDataPayload(msdData, type)));
    }


    public Call<CloudinaryFileInner> postFileResource(Resource resource) {

        File file = null;
        MultipartBody.Part filePart = null;
        MultipartBody.Part type = null;
        MultipartBody.Part save = null;
        MultipartBody.Part isPrivate = null;
        if (resource.getDeviceURL().startsWith("file")) {
            file = new File(resource.getDeviceURL().substring(8));
        } else {
            file = new File(resource.getDeviceURL());
        }
        if (file.exists() && file.isFile()) {
            String strMimeType = URLConnection.guessContentTypeFromName(resource.getDeviceURL());
            if (!strMimeType.isEmpty()) {
                filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(strMimeType), file));
                if (strMimeType.contains("video")) {
                    type = MultipartBody.Part.createFormData("type", "video");
                } else if (strMimeType.contains("image")) {
                    type = MultipartBody.Part.createFormData("type", "image");
                }
            }
            save = MultipartBody.Part.createFormData("save", "false");
            isPrivate = MultipartBody.Part.createFormData("isPrivate", "false");
            final String auth = AppPrefs.getIdToken(InjectorSyncAdapter.INSTANCE.getComponent().appContext());
            if (filePart != null) {
                if (type != null) {
                    return mUploadFileApiInterface.uploadVideo(auth, filePart, type, save, isPrivate);
                }
                return mUploadFileApiInterface.upload(auth, filePart, type, save, isPrivate);
            } else {
                return null;
            }


        } else {
            return null;
        }
    }

    /**
     * fetch quiz
     *
     * @param objectId id of quiz to fetch
     * @return network call
     */
    public Call<Quiz> fetchQuiz(String objectId) {
        return mBaseApiInterface.getQuiz(objectId);
    }

    public Call<Quiz> fetchByAliasQuiz(String aliasId) {
        return mDownloadApiInterface.getByAliasQuiz(aliasId);
    }

    /**
     * fetch quiz
     *
     * @param objectId id of quiz to fetch
     * @return network call
     */
    public Call<ResponseBody> fetchQuizWeb(String objectId) {
        return mDownloadApiInterface.getQuizWeb(objectId);
    }

    /**
     * fetch assignment
     *
     * @param objectId of assignment to fetch
     * @return network call
     */
    public Call<Assignment> fetchAssignment(String objectId) {
        return mDownloadApiInterface.getAssignment(objectId);
    }

    public Call<Assignment> fetchByAliasAssignment(String alias) {
        return mDownloadApiInterface.getByAliasAssignment(alias);
    }

    public Call<AssignedBadges> fetchAssignedBadges(String objectId) {
        return mDownloadApiInterface.getAssignedBadgesCall(objectId);
    }

    public Call<AssignedBadges> fetchByAliasAssignedBadges(String aliasId) {
        return mDownloadApiInterface.getByAliasAssignedBadgesCall(aliasId);
    }

    public Call<List<Notification>> fetchNotificationsList(String objectId) {
        return mDownloadApiInterface.getNotificationsList(objectId);
    }

    public Call<BroadcastNotification> fetchBroadcastNotificationsList(BroadcastNotification object) {
        return mDownloadApiInterface.getBroadcastNotificationsList(object);
    }

    public Call<PostData> fetchLearningNetworkPostData(String objectId) {
        return mDownloadApiInterface.getPostData(objectId);
    }

    public Call<CalendarEvent> fetchCalendarEventData(String objectId) {
        return mDownloadApiInterface.getCalendarEvent(objectId);
    }

    public Call<QuestionResponse> fetchByAliasQuestionResponse(String alias) {
        return mDownloadApiInterface.getByAliasQuestionResponse(alias);
    }

    public Call<CalendarEvent> fetchByAliasCalendarEventData(String aliasId) {
        return mDownloadApiInterface.getByAliasCalendarEvent(aliasId);
    }

    public Call<PostResponse> fetchLearningNetworkPostResponse(String objectId) {
        return mDownloadApiInterface.getPostResponse(objectId);
    }

    public Call<PostResponse> fetchByAliasLearningNetworkPostResponse(String aliasId) {
        return mDownloadApiInterface.getByAliasPostResponse(aliasId);
    }

    public Call<Group> fetchGroup(String objectId) {
        return mDownloadApiInterface.getGroupDetails(objectId);
    }

    public Call<UserProfile> fetchUserProfile(String objectId) {
        return mDownloadApiInterface.getUserProfile(objectId);
    }


    public Call<GroupPostsNResponse> fetchGroupPostAndResponse(String objectId) {
        return mDownloadApiInterface.fetchGroupPostAndResponse(objectId);
    }


    /**
     * fetch assignment response
     *
     * @param objectId of assignment response to fetch
     * @return network call
     */
    public Call<AssignmentResponse> fetchAssignmentResponse(String objectId) {
        return mDownloadApiInterface.getAssignmentResponse(objectId);
    }

    public Call<ResponseBody> fetchDigitalBook2(String objectId) {
        return mSearchApiInterface.getDigitalBook2(objectId);
    }

    public Call<ResponseBody> fetchCustomSectionRaw(String objectId) {
        return mDownloadApiInterface.getCustomSectionRaw(objectId);
    }

    public Call<ResponseBody> fetchCustomSection(String objectId) {
        return mSearchApiInterface.getCustomSection(objectId);
    }

    /**
     * fetch pop ups
     *
     * @param objectId of pop ups to fetch
     * @return network call
     */
    public Call<ResponseBody> fetchPopUps(String objectId) {
        return mDownloadApiInterface.getPopUps(objectId);
    }

    public Call<ResponseBody> fetchPopUpsEs(String objectId) {
        return mSearchApiInterface.getPopUp(objectId);
    }

    public Call<ResponseBody> getActivityChecklistJson(String id) {
        return mDownloadApiInterface.getActivityChecklistJson(id);

    }

    public Call<ResponseBody> fetchYoutubeVideoDuration(String objectId) {
        return mDownloadApiInterface.getYoutubeVideoDuration(objectId);
    }

    public Call<ResponseBody> fetchCourseProgress(String objectId) {
        return mDownloadApiInterface.fetchCourseProgress(objectId);
    }

    public Call<ResponseBody> saveCourseProgress(RequestBody request) {
        return mDirectUploadApiInterface.saveCourseProgress(request);

    }

    /**
     * fetch concept map
     *
     * @param objectId of concept map to fetch
     * @return network call
     */
    public Call<ResponseBody> fetchConceptMap(String objectId) {
        return mDownloadApiInterface.getConceptMap(objectId);
    }

    public Call<ResponseBody> fetchConceptMapEs(String objectId) {
        return mSearchApiInterface.getConceptMap(objectId);
    }

    /**
     * fetch interactive image
     *
     * @param objectId of interactive image to fetch
     * @return network call
     */
    public Call<ResponseBody> fetchInteractiveImage(String objectId) {
        return mDownloadApiInterface.getInteractiveImage(objectId);
    }

    public Call<ResponseBody> fetchInteractiveImageEs(String objectId) {
        return mSearchApiInterface.getInteractiveImage(objectId);
    }

    public Call<ResponseBody> fetchVideoCourse(String objectId) {
        return mDownloadApiInterface.getVideoCourse(objectId);
    }

    public Call<ResponseBody> fetchInteractiveVideo(String objectId) {
        return mDownloadApiInterface.getInteractiveVideo(objectId);
    }

    public Call<ResponseBody> fetchInteractiveVideoEs(String objectId) {
        return mSearchApiInterface.getInteractiveVideo(objectId);
    }

    /**
     * fetch data package
     *
     * @return network call
     */
    public Call<ServerDataPackage> fetchDataPackage() {
        return mDownloadApiInterface.getServerDataPackage();
    }

    /**
     * fetch file
     *
     * @param cloudUrl of the file to fetch
     * @return network call
     */
    public Call<ResponseBody> fetchFileResource(String cloudUrl) {
        return mDownloadFilesApiInterface.downloadFileFromUrl(cloudUrl);
    }

    public Call<ResponseBody> fetchResourceWeb(String objectId) {
        return mDownloadApiInterface.getResource(objectId);
    }

    public Call<ResponseBody> getDigitalBookAboutResponseBody(String id) {
        return mDownloadApiInterface.getDigitalBookAboutResponseBody(id);
    }

    public Call<ResponseBody> getInteractiveImageAboutResponseBody(String id) {
        return mDownloadApiInterface.getInteractiveImageAboutResponseBody(id);
    }

    public Call<ResponseBody> getConceptMapAboutResponseBody(String id) {
        return mDownloadApiInterface.getConceptMapAboutResponseBody(id);
    }

    public Call<ResponseBody> getPopUpsAboutResponseBody(String id) {
        return mDownloadApiInterface.getPopUpsAboutResponseBody(id);
    }

    public Call<ResponseBody> getVideoCourseAboutResponseBody(String id) {
        return mDownloadApiInterface.getVideoCourseAboutResponseBody(id);
    }

    public Call<ResponseBody> getInteractiveVideoAboutResponseBody(String id) {
        return mDownloadApiInterface.getInteractiveVideoAboutResponseBody(id);
    }

    public Call<ResponseBody> uploadQuizResponse(RequestBody request) {
        return mDirectUploadApiInterface.uploadQuizResponse(request);
    }

    public Call<ArrayList<AboutCourse>> getAboutCourseRecommendedDigitalBookOnline(int limit, int skip) {
        return mDownloadApiInterface.getAboutCourseDigitalBookRecommended(limit, skip);
    }

    public Call<ArrayList<AboutCourse>> getAboutCourseRecommendedPopUpOnline(int limit, int skip) {
        return mDownloadApiInterface.getAboutCoursePopUpRecommended(limit, skip);
    }

    public Call<ArrayList<AboutCourse>> getAboutCourseRecommendedConceptMapOnline(int limit, int skip) {
        return mDownloadApiInterface.getAboutCourseConceptMapRecommended(limit, skip);
    }

    public Call<ArrayList<AboutCourse>> getAboutCourseRecommendedInteractiveImageOnline(int limit, int skip) {
        return mDownloadApiInterface.getAboutCourseInteractiveImageRecommended(limit, skip);
    }

    public Call<ArrayList<AboutCourse>> getAboutCourseRecommendedVideoCourseOnline(int limit, int skip) {
        return mDownloadApiInterface.getAboutCourseVideoCourseRecommended(limit, skip);
    }

    public Call<ResponseBody> addFavorite(boolean isFavorite, String objectId, String courseType) {
        return mDownloadApiInterface.addCourseFavorite(objectId, isFavorite, courseType);
    }

    public Call<ResponseBody> addMicroCourseFavorite(boolean isFavorite, String objectId, String courseType) {
        return mDownloadApiInterface.addMicroCourseFavorite(objectId, isFavorite, courseType);
    }

    public Call<ArrayList<LearningMap>> fetchLearningMaps() {
        return mSearchApiInterface.getLearningMapList();
    }

    public Call<SearchPeriodsResults> fetchPeriodNew(String startTime, String endTime, int skip, int limit) {
        SearchPeriodicEventsParams params = new SearchPeriodicEventsParams();
        SearchPeriodicEventsFilterParams params1 = new SearchPeriodicEventsFilterParams();
        params1.setStartTime(startTime);
        params1.setEndTime(endTime);
        params.setSearchParams(params1);
        params.setSkip(skip);
        params.setLimit(limit);
        return mDownloadApiInterface.getPeriodicEventsListBulk(params);
    }

    public Call<ArrayList<PeriodNew>> fetchPeriodNew(String startTime, String endTime) {
        SearchPeriodicEventsParams params = new SearchPeriodicEventsParams();
        SearchPeriodicEventsFilterParams params1 = new SearchPeriodicEventsFilterParams();
        params1.setStartTime(startTime);
        params1.setEndTime(endTime);
        params.setSearchParams(params1);
        return mDownloadApiInterface.getPeriodicEventsList(params);
    }

    public Call<ArrayList<TrackingRoute>> fetchTrackingRoute(String groupId) {
        return mDownloadApiInterface.getRoutesFromGroupId(groupId);
    }

    public Call<OGMetaDataResponse> getOGData(java.util.ArrayList<String> oGDataList) {
        return mDownloadApiInterface.getOGData(oGDataList);
    }

    public Call<TeacherGradeMapping> getTeacherMapData() {
        return mSearchApiInterface.getTeacherMapDataForMap();
    }

    public Call<RequestOTPResponse> requestOTP(RequestOTP requestOTP) {
        return mBaseApiInterface.requestOTP(requestOTP);
    }

    public Call<AuthToken> verifyOTP(RequestOTP requestOTP) {
        return mBaseApiInterface.verifyOTP(requestOTP);
    }

    public Call<Institution> getInstitute(String id) {
        return mDownloadApiInterface.getInstitute(id);
    }

    public Call<RolePermissions> fetchRolePermissions() {
        return mDownloadApiInterface.fetchRolePermissions();
    }

    public Call<ResponseBody> sendRegistrationToServer(RefreshFCMToken refreshFCMToken) {
        return mUploadApiInterface.sendRegistrationToServer(refreshFCMToken);
    }

    public Call<java.util.ArrayList<Training>> fetchTrainings(int skip, int limit) {
        return mDownloadApiInterface.fetchTrainings(skip, limit);
    }

    public Call<Training> fetchTraining(String id) {
        return mDownloadApiInterface.fetchTraining(id);
    }

    public Call<ResponseBody> changePassword(PasswordChange passwordChange) {
        return mDownloadApiInterface.changePassword(passwordChange);
    }

    public Call<MicroLearningCourse> getRapidLearningCourse(String id) {
        return mDownloadApiInterface.getRapidLearningCourse(id);
    }

    public Call<java.util.ArrayList<MicroLearningCourse>> getMicroLearningCourseList() {
        return mDownloadApiInterface.getMicroLearningCourseList();
    }

    public Call<ResponseBody> uploadCourseProgress(CourseProgress courseProgress) {
        return mDownloadApiInterface.uploadCourseProgress(courseProgress);
    }

    public Call<ResponseBody> uploadAnnotation(RequestBody bookAnnotation) {
        return mDirectUploadApiInterface.uploadAnnotation(bookAnnotation);
    }

    public Call<ResponseBody> savePopupActivity(RequestBody requestBody) {
        return mDirectUploadApiInterface.savePopupActivity(requestBody);
    }

    public Call<ResponseBody> getReportByQuizId(RequestBody requestBody) {
        return mDirectUploadApiInterface.getReportByQuizId(requestBody);
    }

    public Call<ResponseBody> dictionariesSearch(RequestBody requestBody) {
        return mDirectUploadApiInterface.dictionariesSearch(requestBody);
    }

    public Call<ResponseBody> getAflAolConfiguration(RequestBody requestBody) {
        return mDirectUploadApiInterface.getAflAolConfiguration(requestBody);
    }

    public Call<ResponseBody> fetchGlobalConfigs(RequestBody requestBody) {
        return mDirectUploadApiInterface.fetchGlobalConfigs(requestBody);
    }

    public Call<ResponseBody> fetchKhanAcademyVideoDetail(RequestBody requestBody) {
        return mDirectUploadApiInterface.fetchKhanAcademyVideoDetail(requestBody);
    }

    public Call<ResponseBody> saveQuestionFeedback(RequestBody requestBody) {
        return mDirectUploadApiInterface.saveQuestionFeedback(requestBody);
    }


    public Call<ResponseBody> deleteAnnotation(String id) {
        return mUploadApiInterface.deleteAnnotation(id);
    }

    public Call<ResponseBody> saveBookmark(RequestBody requestBody) {
        return mDirectUploadApiInterface.saveBookmark(requestBody);
    }

    public Call<java.util.ArrayList<AnalysisActivityData>> fetchActivityData(String subid, String startdate, String enddate) {
        ActivityData params = new ActivityData();
        params.setSubjectId(subid);
        params.setEndDate(enddate);
        params.setStartDate(startdate);
        return mDownloadApiInterface.fetchActivityData(params);
    }

    public Call<java.util.ArrayList<AnalysisActivityData>> fetchLearningData(String subid, String startdate, String enddate) {
        ActivityData params = new ActivityData();
        params.setSubjectId(subid);
        params.setEndDate(enddate);
        params.setStartDate(startdate);
        return mDownloadApiInterface.fetchLearningData(params);
    }

    public Call<java.util.ArrayList<AnalysisActivityRecentlyRead>> fetchRecentlyReadData(String subid, int limit, int skip) {
        return mDownloadApiInterface.getRecentlyRead(subid, skip, limit);
    }

    public Call<java.util.ArrayList<AnalysisTopicCovered>> fetchTopicData(String subid, int limit, int skip) {
        return mDownloadApiInterface.getTopicCovered(subid, skip, limit);
    }

    public Call<PerformanceResponseCount> fetchPerformanceCount(String subid) {
        return mDownloadApiInterface.getaggregatedResponseCount(subid);
    }

    /*To upload user course progress each time object created*/
    public Call<ResponseBody> uploadUserCourseProgress(UserCourseProgress userCourseProgress) {
        return mUploadApiInterface.uploadUserCourseProgress(userCourseProgress);
    }

    /*To fetch questions for the practice*/
    public Call<PracticeQuestionResponse> fetchQuestions(PracticeParent practiceParent) {
        return mDownloadApiInterface.fetchQuestions(practiceParent);

    }

    /*Api to fetch learning network groups*/
    public Call<java.util.ArrayList<IdNameObject>> fetchNetworkGroup(int skip, int limit) {
        return mDownloadApiInterface.fetchNetworkGroup(skip, limit);
    }

    /*To fetch questions for the quiz*/
    public Call<QuizQuestionResponse> fetchQuestionsForQuiz(String quizId) {
        return mDownloadApiInterface.fetchQuestionsForQuiz(quizId);

    }

    /*To submit question responses*/
    public Call<QuizResponse> submitResponseOfQuiz(QuizResponsePost prepareQuizResponsePostData) {
        return mDownloadApiInterface.submitResponseOfQuiz(prepareQuizResponsePostData);
    }

    /*To send practice/quiz points to server*/
    public Call<TotalPointResponse> sendPointsToServer(TotalPointPost totalPointPost) {
        return mDownloadApiInterface.sendPointsToServer(totalPointPost);
    }

    /*To fetch configuration of quiz */
    public Call<QuizConfigurationResponse> fetchQuizConfiguration(QuizConfigurationRequest quizConfigurationRequest) {
        return mDownloadApiInterface.fetchQuizConfiguration(quizConfigurationRequest);

    }

    /*To fetch khan academy explanation videos*/
    public Call<java.util.ArrayList<KhanAcademyVideo>> fetchExplanationVideos(PlayerFilterParent playerFilterParent) {
        return mDownloadApiInterface.fetchExplanationVideos(playerFilterParent);

    }

    /*To fetch Quiz configuration */
    public Call<GlobalConfigurationParent> fetchQuizAnalyticsConfiguration(GlobalConfigurationRequest chartConfigurationRequest) {
        return mDownloadApiInterface.fetchGlobalConfiguration(chartConfigurationRequest);

    }

    /*To upload user time spent on activity*/
    public Call<ResponseBody> saveUserTimeSpentProgress(RequestBody requestBody) {
        return mDirectUploadApiInterface.saveUserTimeSpentProgress(requestBody);
    }


    /*To post question feedback*/
    public Call<ResponseBody> postQuestionFeedback(QuestionFeedback questionFeedback) {
        return mUploadApiInterface.postQuestionFeedback(questionFeedback);
    }

    /*To fetch list of questions for revision*/
    public Call<RevisionResponse> fetchQuestionsAndSubmitRevision(RevisionResponsePost revisionResponsePost) {
        return mDownloadApiInterface.fetchQuestionsAndSubmitRevision(revisionResponsePost);

    }

    public Call<ResponseBody> updateUserProfile(UserProfile userProfile) {
        return mUploadApiInterface.updateUserProfile(userProfile);
    }

    /*To fetch Today Recap*/
    public Call<java.util.ArrayList<LessonPlanMinimal>> getTodayRecap() {
        return mDownloadApiInterface.getTodayRecap();

        /*To fetch my subjects*/
    }

    /*To fetch Today Recap*/
    public Call<LessonPlanChapterResult> getChapterResult(LessonPlanChapterPost lessonPlanChapterPost) {
        return mDownloadApiInterface.getChapterResult(lessonPlanChapterPost);
    }

    /*To fetch My Subject*/
    public Call<LessonPlanSubjectResult> getMySubject(LessonPlanSubjectPost lessonPlanSubjectPost) {
        return mDownloadApiInterface.getMySubjects(lessonPlanSubjectPost);
    }

    public Call<java.util.ArrayList<LessonPlanChapterResult>> getChaptersResult() {
        return mDownloadApiInterface.getChaptersResult();
    }

    /*To login user on mind spark*/
    public Call<MindSparkLoginResponse> loginUserToMindSpark(MindSparkLoginRequest mindSparkLoginRequest) {
        return mMindSparkApiInterface.loginUserToMindSpark(mindSparkLoginRequest);
    }

    /*To fetch question data from mind spark*/
    public Call<MindSparkQuestionParent> getMindSparkQuestion(MindSparkQuestionRequest mindSparkQuestionRequest) {
        return mMindSparkApiInterface.getMindSparkQuestion(mindSparkQuestionRequest);
    }

    /*To submit current question response and fetch new question*/
    public Call<MindSparkQuestionParent> submitAndFetchNewQuestion(MindSparkQuestionSubmit mindSparkQuestionSubmit) {
        return mMindSparkApiInterface.submitAndFetchNewQuestion(mindSparkQuestionSubmit);

    }

    /*To fetch all topic list of mind spark*/
    public Call<MindSparkTopicResult> getMindSparkTopicResult(MindSparkTopicListRequest mindSparkTopicListRequest) {
        return mMindSparkApiInterface.getMindSparkTopicResult(mindSparkTopicListRequest);
    }

    /*To fetch details of subject*/
    public Call<LessonPlanSubjectDetails> getSubjectDetails(String subjectId) {
        return mDownloadApiInterface.getSubjectDetails(subjectId);
    }

    /*To fetch Learn, Reinforce, Practice and Apply by topicId and type*/
    public Call<LRPAResult> fetchLRPA(LRPARequest lrpaRequest) {
        return mDownloadApiInterface.fetchLRPA(lrpaRequest);
    }

    /*To fetch third party meta information*/
    public Call<java.util.ArrayList<String>> fetchThirdPartyMapping(ThirdPartyMapping thirdPartyMapping) {
        return mDownloadApiInterface.fetchThirdPartyMapping(thirdPartyMapping);

    }

    /*To fetch coverage data for particular subject*/
    public Call<java.util.ArrayList<CoverageChartData>> fetchSubjectWiseCoverageData(ChartDataRequest chartDataRequest) {
        return mDownloadApiInterface.fetchSubjectWiseCoverageData(chartDataRequest);
    }

    /*To fetch coverage data for all subjects*/
    public Call<java.util.ArrayList<CoverageChartData>> fetchAllSubjectCoverageData(ChartDataRequest chartDataRequest) {
        return mDownloadApiInterface.fetchAllSubjectCoverageData();
    }

    /*To fetch performance data for particular subject*/
    public Call<java.util.ArrayList<PerformanceChartData>> fetchSubjectWisePerformanceData(ChartDataRequest chartDataRequest) {
        return mDownloadApiInterface.fetchSubjectWisePerformanceData(chartDataRequest);
    }

    /*To fetch performance data for all subjects*/
    public Call<java.util.ArrayList<PerformanceChartData>> fetchAllSubjectPerformanceData(ChartDataRequest chartDataRequest) {
        return mDownloadApiInterface.fetchAllSubjectPerformanceData();
    }

    /*To fetch effort (time spent) data for all subjects*/
    public Call<EffortChartDataParent> fetchEffortData(EffortChartDataRequest effortChartDataRequest) {
        return mDownloadApiInterface.fetchEffortData(effortChartDataRequest);
    }

    /*To fetch effort (time spent) data for all subjects*/
    public Call<java.util.ArrayList<EffortvsPerformanceData>> fetchEffortvsPerformanceData() {
        return mDownloadApiInterface.fetchEffortVsPerformanceData();
    }

    /*To fetch effort (time spent) data for individual subject*/
    public Call<EffortChartDataParent> fetchSubjectWiseEffortData(EffortChartDataRequest effortChartDataRequest) {
        return mDownloadApiInterface.fetchSubjectWiseEffortData(effortChartDataRequest);
    }

    /*To fetch effort (time spent) weekly data for individual subject*/
    public Call<EffortChartDataParent> fetchWeeklyEffortData(EffortChartDataRequest effortChartDataRequest) {
        return mDownloadApiInterface.fetchWeeklyEffortData(effortChartDataRequest);
    }

    /*To fetch student's achievements*/
    public Call<StudentAchievement> fetchStudentAchievements(String userId) {
        return mDownloadApiInterface.fetchStudentAchievements(userId);
    }

    /*To fetch chart configuration for performance and coverage*/
    public Call<GlobalConfigurationParent> fetchGlobalConfiguration(GlobalConfigurationRequest chartConfigurationRequest) {
        return mDownloadApiInterface.fetchGlobalConfiguration(chartConfigurationRequest);
    }

    /*To fetch details of overdue and pending list of student homework*/
    public Call<AssignedHomeworkParent> fetchHomework(String subjectId) {
        return mDownloadApiInterface.fetchHomework(subjectId);
    }

    /*To fetch details of overdue and pending list of student homework*/
    public Call<Homework> fetchHomeworkDetail(String homeworkId) {
        return mDownloadApiInterface.fetchHomeworkDetail(homeworkId);
    }

    public Call<HomeworkSubmitResponse> submitHomework(String homeworkId) {
        return mDownloadApiInterface.submitHomework(homeworkId);
    }

    /*To send status of application for various user activity*/
    public Call<ResponseBody> checkUserStatus(String status) {
        return mDownloadApiInterface.checkUserStatus(status);
    }

    public Call<GamificationBonus> saveBonus(GamificationBonus bonus) {
        return mDownloadApiInterface.saveBonus(bonus);
    }

    public Call<GamificationBonus> getBonus(String bonusId) {
        return mDownloadApiInterface.getBonus(bonusId);
    }

    public Call<ResponseBody> saveGamificationSurvey(GamificationSurvey survey) {
        return mDownloadApiInterface.saveGamificationSurvey(survey);
    }

    /*To fetch detail of wikiHow card*/
    public Call<WikiHowParent> fetchWikiHowCardDetail(String wikiHowId) {
        return mWikiHowApiInterface.fetchWikiHowCardDetail("app", "article", wikiHowId, "json");
    }

    /*To fetch Student interest Data list according to interest type*/
    public Call<java.util.ArrayList<UserInterest>> fetchStudentInterestData(String gradeId, int interestType) {
        return mDownloadApiInterface.fetchStudentInterestData(gradeId, interestType);
    }

    /*To send selected goal/user interest first time to server*/
    public Call<ResponseBody> sendUserInterestInitially(UserInterestPost post) {
        return mDownloadApiInterface.sendUserInterestInitially(post);
    }

    /*To send selected goal/user interest after first time to server*/
    public Call<ResponseBody> sendUserInterest(UserInterestPost post) {
        return mDownloadApiInterface.sendUserInterest(post);
    }

    /*To fetch challenge for the day on Dashboard*/
    public Call<LogiQidsChallengeParent> fetchChallengeForTheDay(String typeChallengeLogiqids) {
        return mDownloadApiInterface.fetchChallengeForTheDay(typeChallengeLogiqids);
    }

    /*To fetch video for the day on dashboard*/
    public Call<VideoForDayParent> fetchVideoForTheDay(String typeVideoPerDay) {
        return mDownloadApiInterface.fetchVideoForTheDay(typeVideoPerDay);
    }

    /*To upload data for Take a Challenge Or Video for day of student when join/complete*/
    public Call<ResponseBody> uploadTakeChallengeOrVideo(UserChallengePost post, int status) {
        return mDownloadApiInterface.uploadTakeChallengeOrVideo(post, status);
    }

    /*To fetch non-student users' profile*/
    public Call<TeacherProfile> fetchNonStudentUserProfileByUserId(String userId) {
        return mDownloadApiInterface.fetchNonStudentUserProfileByUserId(userId);
    }

    /*To update profile of student with profile picture*/
    public Call<ResponseBody> updateStudentProfileWithImage(StudentProfilePicturePost profilePicturePost, String userId) {
        return mDownloadApiInterface.updateStudentProfileWithImage(profilePicturePost, userId);
    }


    /*To login student to Logiqids server*/
    public Call<LogiqidsLoginResult> loginToLogiqids(Credentials credentials) {
        return mLogiqidsApiInterface.loginToLogiqids(credentials);
    }

    /*To fetch worksheet list from Logiqids server*/
    public Call<LogiqidsWorksheetResult> getWorksheetList(int userId, int topicId) {
        return mLogiqidsApiInterface.getWorksheetList(userId, topicId);
    }

    /*To fetch question from Logiqids server*/
    public Call<LogiqidsQuestionResult> getQuestion(int userId, int topicId, int worksheetId) {
        return mLogiqidsApiInterface.getQuestion(userId, topicId, worksheetId);
    }

    /*To submit question response to Logiqids server*/
    public Call<LogiqidsQuestionAttemptResult> submitQuestionResponse(int userId, int topicId, int worksheetId, LogiqidsQuestionAttemptRequest logiqidsQuestionAttemptRequest) {
        return mLogiqidsApiInterface.submitQuestionResponse(userId, topicId, worksheetId, logiqidsQuestionAttemptRequest);
    }

    /*To fetch vocational subject on dashboard(for now, name = life skill)
     * To fetch this post object should be empty*/
    public Call<java.util.ArrayList<VocationalSubject>> fetchVocationalSubject(VocationalTopicRequest vocationalTopicRequest) {
        return mDownloadApiInterface.fetchVocationalSubject(vocationalTopicRequest);
    }

    /*To fetch vocational topic (for now logiqids)*/
    public Call<java.util.ArrayList<VocationalTopic>> fetchVocationalTopics(VocationalTopicRequest topicRequest) {
        return mDownloadApiInterface.fetchVocationalTopics(topicRequest);
    }

    /*To fetch help and faq data*/
    public Call<java.util.ArrayList<HelpAndFaqCategory>> fetchHelpAndFAQ() {
        return mDownloadApiInterface.fetchHelpAndFAQ();
    }

    /*To fetch revision subjects with details*/
    public Call<java.util.ArrayList<RevisionSubject>> fetchRevisionSubjects() {
        return mDownloadApiInterface.fetchRevisionSubjects();
    }

    /*To fetch Geo-Gebra card detail list for Apply*/
    public Call<java.util.ArrayList<TPCurriculumResponse>> fetchGeoGebraCardDetail(ThirdPartyMapping thirdPartyMapping) {
        return mDownloadApiInterface.fetchGeoGebraCardDetail(thirdPartyMapping);
    }

    public Call<ResponseBody> uploadVideoWatchStarted(String videoId, String moduleId) {
        return mUploadApiInterface.uploadVideoWatchStarted(videoId, moduleId);
    }

    public Call<ResponseBody> uploadVideoWatchEnded(String videoId, String moduleId) {
        return mUploadApiInterface.uploadVideoWatchEnded(videoId, moduleId);
    }

    public Call<ResponseBody> uploadUserTimeSpent(UserTimeSpent userTimeSpent) {
        return mUploadApiInterface.uploadUserTimeSpent(userTimeSpent);
    }
}
