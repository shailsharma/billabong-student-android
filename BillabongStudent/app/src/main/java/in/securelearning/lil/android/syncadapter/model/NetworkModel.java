package in.securelearning.lil.android.syncadapter.model;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
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
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupPostsNResponse;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.LearningLevel;
import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.dataobjects.LilSearch;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.dataobjects.OGMetaDataResponse;
import in.securelearning.lil.android.base.dataobjects.PerformanceResponseCount;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.UserBrowseHistory;
import in.securelearning.lil.android.base.dataobjects.UserCourseProgress;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.dataobjects.UserRating;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.courses.dataobject.CourseReview;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.dataobject.ActivityData;
import in.securelearning.lil.android.syncadapter.dataobject.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobject.BlogResponse;
import in.securelearning.lil.android.syncadapter.dataobject.BroadcastNotification;
import in.securelearning.lil.android.syncadapter.dataobject.CloudinaryFileInner;
import in.securelearning.lil.android.syncadapter.dataobject.EnrollTrainingResponse;
import in.securelearning.lil.android.syncadapter.dataobject.LearningMapAggregatesParams;
import in.securelearning.lil.android.syncadapter.dataobject.MasteryRequestObject;
import in.securelearning.lil.android.syncadapter.dataobject.PasswordChange;
import in.securelearning.lil.android.syncadapter.dataobject.PrerequisiteCoursesPostData;
import in.securelearning.lil.android.syncadapter.dataobject.QuizResponse;
import in.securelearning.lil.android.syncadapter.dataobject.RecommendedApiObject;
import in.securelearning.lil.android.syncadapter.dataobject.RecommendedCourseParams;
import in.securelearning.lil.android.syncadapter.dataobject.RecommendedResourceParams;
import in.securelearning.lil.android.syncadapter.dataobject.RefreshFCMToken;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTP;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTPResponse;
import in.securelearning.lil.android.syncadapter.dataobject.RolePermissions;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchPeriodicEventsFilterParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchPeriodicEventsParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchPeriodsResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchRecommendedCoursesFilterParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchRecommendedCoursesParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResourceParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResourcesResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResults;
import in.securelearning.lil.android.syncadapter.dataobject.ServerDataPackage;
import in.securelearning.lil.android.syncadapter.dataobject.SkillMasteryQuestionGetData;
import in.securelearning.lil.android.syncadapter.dataobject.StudentGradeMapping;
import in.securelearning.lil.android.syncadapter.dataobject.TeacherGradeMapping;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityData;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfile;
import in.securelearning.lil.android.syncadapter.fcmservices.Message;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageData;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageDataPayload;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageMultipleTopic;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import in.securelearning.lil.android.syncadapter.rest.BaseApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DirectUploadApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.rest.DownloadFilesApiInterface;
import in.securelearning.lil.android.syncadapter.rest.FCMApiInterface;
import in.securelearning.lil.android.syncadapter.rest.NewUploadApiInterface;
import in.securelearning.lil.android.syncadapter.rest.SearchApiInterface;
import in.securelearning.lil.android.syncadapter.rest.SyncSuccessApiInterface;
import in.securelearning.lil.android.syncadapter.rest.UploadApiInterface;
import in.securelearning.lil.android.syncadapter.rest.UploadFilesApiInterface;
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
    public final static String TYPE_POST_DATA = "postData";
    public final static String TYPE_POST_RESPONSE = "postResponse";
    public final static String TYPE_ASSIGNMENT = "assignment";
    public final static String TYPE_ASSIGNMENT_RESPONSE = "assignmentResponse";
    public final static String TYPE_CALENDAR_EVENT = "calendarEvent";
    public final static String TYPE_USER_PROFILE = "userProfile";
    public final static String TYPE_GROUP_UPDATE = "groupUpdate";
    public final static String TYPE_INSTITUTE_UPDATE = "instituteUpdate";
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    BaseApiInterface mBaseApiInterface;
    /**
     * for upload functions
     */
    @Inject
    UploadApiInterface mUploadApiInterface;
    @Inject
    DirectUploadApiInterface mDirectUploadApiInterface;
    /**
     * for upload functions without object id or new uploads
     */
    @Inject
    NewUploadApiInterface mNewUploadApiInterface;
    /**
     * for FCM location interface
     */
    @Inject
    FCMApiInterface mFCMApiInterface;
    /**
     * for download functions
     */
    @Inject
    DownloadApiInterface mDownloadApiInterface;
    @Inject
    SearchApiInterface mSearchApiInterface;
    /**
     * for file download functions
     */
    @Inject
    DownloadFilesApiInterface mDownloadFilesApiInterface;
    /**
     * for file Upload functions
     */
    @Inject
    UploadFilesApiInterface mUploadFileApiInterface;
    /**
     * for sync success functions
     */
    @Inject
    SyncSuccessApiInterface mSyncSuccessApiInterface;

    public NetworkModel() {
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public boolean connectToServer() {
        return mBaseApiInterface.connectToServer().isSuccessful();
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
        //mSyncSuccessApiInterface.postSyncSuccess(objectIds);
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

    public Call<java.util.ArrayList<PostData>> postLearningNetworkPostData(java.util.ArrayList<PostData> postData) {
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

    public Call<ResponseBody> uploadQuizResponse(QuizResponse quizResponse) {
        return mSearchApiInterface.uploadQuizResponse(quizResponse);
    }

    public Call<MessageResponse> sendDataForTrackingUsingFCM(String group, Object msdData) {
        return mFCMApiInterface.send(new Message("/topics/" + BuildConfig.SUBSCRIBE_FCM_PREFIX + group, null, new MessageDataPayload(msdData, TYPE_TRACKING)));
    }

    public Call<MessageResponse> sendNotificationForTrackingUsingFCM(String group, String notificationData, Object msdData) {
        return mFCMApiInterface.send(new Message("/topics/" + BuildConfig.SUBSCRIBE_FCM_PREFIX + group, new MessageData(notificationData, "Lil Tracker"), new MessageDataPayload(msdData, TYPE_TRACKING)));
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


//
//    /**
//     * post resource
//     *
//     * @param resource to post
//     * @return network call
//     */
//    public Call<Results> postResource(Resource resource) {
//        return mUploadFileApiInterface.uploadFileUsingJson(resource);
//    }

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
        if (file != null && file.exists() && file.isFile()) {
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

    public Call<Notification> fetchNotifications(String objectId) {
        return mDownloadApiInterface.getNotifications(objectId);
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
//        return mDownloadApiInterface.getGroup(objectId);
    }

    public Call<UserProfile> fetchUserProfile(String objectId) {
        return mDownloadApiInterface.getUserProfile(objectId);
    }

    public Call<StudentProfile> fetchStudentProfile() {
        return mDownloadApiInterface.getStudentProfile();
    }

    public Call<GroupPostsNResponse> fetchGroupPostNResponse(String objectId) {
        return mDownloadApiInterface.fetchAllPostNResponse(objectId);
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

    /**
     * fetch digital book
     *
     * @param objectId of digital book to fetch
     * @return network call
     */
    public Call<DigitalBook> fetchDigitalBook(String objectId) {
        return mDownloadApiInterface.getDigitalBook(objectId);
    }

    public Call<ResponseBody> fetchDigitalBookRaw(String objectId) {
        return mDownloadApiInterface.getDigitalBook2(objectId);
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

    public Call<java.util.ArrayList<Resource>> searchForResourcesOnline(String query) {
        LilSearch search = new LilSearch();
        search.setLimit(10);
        search.setStartIndex(0);
        search.setSearchQuery(query);
        search.addCollection("Resource");
        return mSearchApiInterface.getSearchResults(search);
    }

    public Call<SearchResourcesResults> searchForResourcesOnlineEs(String query, java.util.ArrayList<String> subjectIds, java.util.ArrayList<String> learningLevelIds, java.util.ArrayList<String> languageIds, java.util.ArrayList<String> courseType, int skip, int limit) {
        SearchResourceParams searchResourceParams = new SearchResourceParams();
        SearchParams search = new SearchParams();
        if (query.isEmpty()) {
            search.setSearchQuery(null);
        } else {
            search.setSearchQuery(query);
        }

        if (subjectIds != null) {
            search.setSubjects(subjectIds);
        }
        if (learningLevelIds != null) {
            search.setLearningLevels(learningLevelIds);
        }
        if (languageIds != null) {
            search.setLangs(languageIds);
        }
        if (courseType != null) {
            search.setCollections(courseType);
        }
        searchResourceParams.setSkip(skip);
        searchResourceParams.setLimit(limit);
        searchResourceParams.setSearchParams(search);
        return mSearchApiInterface.getResourceSearchResultsEs(searchResourceParams);
    }

    public Call<java.util.ArrayList<AboutCourse>> searchForCourseOnline(String query, java.util.ArrayList<String> ids, String level1, String level2) {
        LilSearch search = new LilSearch();
//        search.setLimit(limit);
//        search.setStartIndex(skip);
        if (query.isEmpty()) {
            search.setSearchQuery(null);
        } else {
            search.setSearchQuery(query);
        }
        search.addCollection("DigitalBook");
        search.addCollection("VideoCourse");
        search.addCollection("PopUp");
        search.addCollection("ConceptMap");
        search.addCollection("InteractiveImage");
        search.addCollection("InteractiveVideo");
        search.setSubjects(ids);

//        search.setCategory(category);
        search.setLevel1(level1);
        search.setLevel2(level2);
        return mSearchApiInterface.getCourseSearchResults(search);
    }

    public Call<SearchCoursesResults> searchForCourseOnlineEs(String query, java.util.ArrayList<String> subjectIds, java.util.ArrayList<String> learningLevelIds, java.util.ArrayList<String> languageIds, java.util.ArrayList<String> courseType, int skip, int limit) {
        SearchParams search = new SearchParams();
        if (query.isEmpty()) {
            search.setSearchQuery(null);
        } else {
            search.setSearchQuery(query);
        }

        if (subjectIds != null) {
            search.setSubjects(subjectIds);
        }
        if (learningLevelIds != null) {
            search.setLearningLevels(learningLevelIds);
        }
        if (languageIds != null) {
            search.setLangs(languageIds);
        }
        if (courseType != null) {
            search.setCollections(courseType);
        }
        SearchCoursesParams searchCoursesParams = new SearchCoursesParams();
        searchCoursesParams.setSearchParams(search);
        searchCoursesParams.setSkip(skip);
        searchCoursesParams.setLimit(limit);
        return mSearchApiInterface.getCourseSearchResultsEs(searchCoursesParams);
    }

    public Call<SearchCoursesResults> searchForCourseOnlineEs(String query, java.util.ArrayList<String> subjectIds, java.util.ArrayList<String> learningLevelIds, java.util.ArrayList<String> languageIds, java.util.ArrayList<String> courseType, java.util.ArrayList<String> innerLearningLevelIds, int skip, int limit) {
        SearchParams search = new SearchParams();
        if (query.isEmpty()) {
            search.setSearchQuery(null);
        } else {
            search.setSearchQuery(query);
        }

        if (subjectIds != null) {
            search.setSubjects(subjectIds);
        }
        if (learningLevelIds != null) {
            search.setLearningLevels(learningLevelIds);
        }
        if (languageIds != null) {
            search.setLangs(languageIds);
        }
        if (courseType != null) {
            search.setCollections(courseType);
        }
        if (innerLearningLevelIds != null) {
            search.setGrades(innerLearningLevelIds);
        }
        SearchCoursesParams searchCoursesParams = new SearchCoursesParams();
        searchCoursesParams.setSearchParams(search);
        searchCoursesParams.setSkip(skip);
        searchCoursesParams.setLimit(limit);
        return mSearchApiInterface.getCourseSearchResultsEs(searchCoursesParams);
    }

    public Call<SearchResults> searchForCourseOnlineBySubjectEs(String subjectId, String instituteId) {

        SearchRecommendedCoursesFilterParams filterParams = new SearchRecommendedCoursesFilterParams();
        SearchRecommendedCoursesParams params = new SearchRecommendedCoursesParams();
        java.util.ArrayList<String> courseTypes = new java.util.ArrayList<>();
        courseTypes.add("digitalbook");
        courseTypes.add("videocourse");
        filterParams.setCourseTypes(courseTypes);
        params.setSearchParams(filterParams);
        params.setInstituteId(instituteId);
        params.setSubjectId(subjectId);
        return mSearchApiInterface.getCourseSearchResultsBySubjectEs(params);
    }

    public Call<SearchResults> getFilterParamsEs(String instituteId, java.util.ArrayList<LearningLevel> learningLevels) {
        SearchRecommendedCoursesFilterParams filterParams = new SearchRecommendedCoursesFilterParams();
        SearchRecommendedCoursesParams params = new SearchRecommendedCoursesParams();
        java.util.ArrayList<String> learningLevelIds = new java.util.ArrayList<>();
        for (LearningLevel learningLevel :
                learningLevels) {
            learningLevelIds.add(learningLevel.getId());
        }
        filterParams.setSortBy(null);
        filterParams.setLearningLevelIds(learningLevelIds);
        params.setSearchParams(filterParams);
        params.setInstituteId(instituteId);
        return mSearchApiInterface.getFilterParamsEs(params);
    }

    public Call<java.util.ArrayList<DigitalBook>> searchForDigitalBookOnline(String query, String category, String level1, String level2) {
        LilSearch search = new LilSearch();
        search.setLimit(10);
        search.setStartIndex(0);
        search.setSearchQuery(query);
        search.addCollection("DigitalBook");
        if (TextUtils.isEmpty(category)) {
            search.setSubjects(null);
        } else {
            search.getSubjects().add(category);
        }

//        search.setCategory(category);
        search.setLevel1(level1);
        search.setLevel2(level2);
        return mSearchApiInterface.getDigitalBookSearchResults(search);
    }

    public Call<java.util.ArrayList<PopUps>> searchForPopUpOnline(String query, String category, String level1, String level2) {
        LilSearch search = new LilSearch();
        search.setLimit(10);
        search.setStartIndex(0);
        search.setSearchQuery(query);
        search.addCollection("PopUp");
        if (TextUtils.isEmpty(category)) {
            search.setSubjects(null);
        } else {
            search.getSubjects().add(category);
        }
//        search.setCategory(category);
        search.setLevel1(level1);
        search.setLevel2(level2);
        return mSearchApiInterface.getPopUpSearchResults(search);
    }

    public Call<java.util.ArrayList<ConceptMap>> searchForConceptMapOnline(String query, String category, String level1, String level2) {
        LilSearch search = new LilSearch();
        search.setLimit(10);
        search.setStartIndex(0);
        search.setSearchQuery(query);
        search.addCollection("ConceptMap");
        if (TextUtils.isEmpty(category)) {
            search.setSubjects(null);
        } else {
            search.getSubjects().add(category);
        }
//        search.setCategory(category);
        search.setLevel1(level1);
        search.setLevel2(level2);
        return mSearchApiInterface.getConceptMapSearchResults(search);
    }

    public Call<java.util.ArrayList<InteractiveImage>> searchForInteractiveImageOnline(String query, String category, String level1, String level2) {
        LilSearch search = new LilSearch();
        search.setLimit(10);
        search.setStartIndex(0);
        search.setSearchQuery(query);
        search.addCollection("InteractiveImage");
        if (TextUtils.isEmpty(category)) {
            search.setSubjects(null);
        } else {
            search.getSubjects().add(category);
        }
//        search.setCategory(category);
        search.setLevel1(level1);
        search.setLevel2(level2);
        return mSearchApiInterface.getInteractiveImageSearchResults(search);
    }

    public Call<java.util.ArrayList<VideoCourse>> searchForVideoCourseOnline(String query, String category, String level1, String level2) {
        LilSearch search = new LilSearch();
        search.setLimit(10);
        search.setStartIndex(0);
        search.setSearchQuery(query);
        search.addCollection("VideoCourse");
        if (TextUtils.isEmpty(category)) {
            search.setSubjects(null);
        } else {
            search.getSubjects().add(category);
        }
//        search.setCategory(category);
        search.setLevel1(level1);
        search.setLevel2(level2);
        return mSearchApiInterface.getVideoCourseSearchResults(search);
    }

    public Call<java.util.ArrayList<InteractiveVideo>> searchForInteractiveVideoOnline(String query, String category, String level1, String level2) {
        LilSearch search = new LilSearch();
        search.setLimit(10);
        search.setStartIndex(0);
        search.setSearchQuery(query);
        search.addCollection("InteractiveVideo");
        if (TextUtils.isEmpty(category)) {
            search.setSubjects(null);
        } else {
            search.getSubjects().add(category);
        }
//        search.setCategory(category);
        search.setLevel1(level1);
        search.setLevel2(level2);
        return mSearchApiInterface.getInteractiveVideoSearchResults(search);
    }

    public Call<java.util.ArrayList<Assignment>> searchForAssignmentsOnline(String query) {
        LilSearch search = new LilSearch();
        search.setLimit(10);
        search.setStartIndex(0);
        search.setSearchQuery(query);
        search.addCollection("Assignment");
        return mSearchApiInterface.getAssignmentsSearchResults(search);
    }

    public Call<AboutCourse> getDigitalBookAbout(String id) {
        return mDownloadApiInterface.getDigitalBookAbout(id);
    }

    public Call<AboutCourse> getInteractiveImageAbout(String id) {
        return mDownloadApiInterface.getInteractiveImageAbout(id);
    }

    public Call<AboutCourse> getConceptMapAbout(String id) {
        return mDownloadApiInterface.getConceptMapAbout(id);
    }

    public Call<AboutCourse> getPopUpsAbout(String id) {
        return mDownloadApiInterface.getPopUpsAbout(id);
    }

    public Call<AboutCourse> getVideoCourseAbout(String id) {
        return mDownloadApiInterface.getVideoCourseAbout(id);
    }

    public Call<AboutCourse> getInteractiveVideoAbout(String id) {
        return mDownloadApiInterface.getInteractiveVideoAbout(id);
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

//    public Call<CourseAnalytics> uploadCourseUserLog(CourseAnalytics courseAnalytics) {
//        return mNewUploadApiInterface.uploadCourseUserLog(courseAnalytics);
//    }

    public Call<UserBrowseHistory> uploadUserBrowseHistoryLog(UserBrowseHistory userBrowseHistory) {
        return mNewUploadApiInterface.uploadUserBrowseHistory(userBrowseHistory);
   }
    public Call<ResponseBody> increaseVideoCourseShareCount(String id) {
        return mDownloadApiInterface.increaseVideoCourseShareCount(id);
    }

    public Call<ResponseBody> increaseDigitalBookShareCount(String id) {
        return mDownloadApiInterface.increaseDigitalBookShareCount(id);
    }

    public Call<ResponseBody> increaseConceptMapShareCount(String id) {
        return mDownloadApiInterface.increaseConceptMapShareCount(id);
    }

    public Call<ResponseBody> increaseInteractiveImageShareCount(String id) {
        return mDownloadApiInterface.increaseInteractiveImageShareCount(id);
    }

    public Call<ResponseBody> increaseInteractiveVideoShareCount(String id) {
        return mDownloadApiInterface.increaseInteractiveVideoShareCount(id);
    }

    public Call<ResponseBody> increasePopupShareCount(String id) {
        return mDownloadApiInterface.increasePopupShareCount(id);
    }

//    public Call<CourseAnalytics> uploadCourseUserLog(CourseAnalytics courseAnalytics) {
//        return mNewUploadApiInterface.uploadCourseUserLog(courseAnalytics);
//    }

    public Call<java.util.ArrayList<DigitalBook>> getRecommendedDigitalBookOnline(int limit, int skip) {
        return mDownloadApiInterface.getDigitalBookRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<PopUps>> getRecommendedPopUpOnline(int limit, int skip) {
        return mDownloadApiInterface.getPopUpRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<ConceptMap>> getRecommendedConceptMapOnline(int limit, int skip) {
        return mDownloadApiInterface.getConceptMapRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<InteractiveImage>> getRecommendedInteractiveImageOnline(int limit, int skip) {
        return mDownloadApiInterface.getInteractiveImageRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<VideoCourse>> getRecommendedVideoCourseOnline(int limit, int skip) {
        return mDownloadApiInterface.getVideoCourseRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<InteractiveVideo>> getRecommendedInteractiveVideoOnline(int limit, int skip) {
        return mDownloadApiInterface.getInteractiveVideoRecommended(limit, skip);
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

    public Call<ArrayList<AboutCourse>> getAboutCourseRecommendedInteractiveVideoOnline(int limit, int skip) {
        return mDownloadApiInterface.getAboutCourseInteractiveVideoRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectRecommendedDigitalBookOnline(int limit, int skip) {
        return mDownloadApiInterface.getRecommendedApiObjectDigitalBookRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectRecommendedPopUpOnline(int limit, int skip) {
        return mDownloadApiInterface.getRecommendedApiObjectPopUpRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectRecommendedConceptMapOnline(int limit, int skip) {
        return mDownloadApiInterface.getRecommendedApiObjectConceptMapRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectRecommendedInteractiveImageOnline(int limit, int skip) {
        return mDownloadApiInterface.getRecommendedApiObjectInteractiveImageRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectRecommendedVideoCourseOnline(int limit, int skip) {
        return mDownloadApiInterface.getRecommendedApiObjectVideoCourseRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<RecommendedApiObject>> getRecommendedApiObjectRecommendedInteractiveVideoOnline(int limit, int skip) {
        return mDownloadApiInterface.getRecommendedApiObjectInteractiveVideoRecommended(limit, skip);
    }

    public Call<java.util.ArrayList<DigitalBook>> getFavoriteDigitalBookOnline(int limit, int skip) {
        return mDownloadApiInterface.getDigitalBookFavorite(limit, skip);
    }

    public Call<java.util.ArrayList<PopUps>> getFavoritePopUpOnline(int limit, int skip) {
        return mDownloadApiInterface.getPopUpFavorite(limit, skip);
    }

    public Call<java.util.ArrayList<ConceptMap>> getFavoriteConceptMapOnline(int limit, int skip) {
        return mDownloadApiInterface.getConceptMapFavorite(limit, skip);
    }

    public Call<java.util.ArrayList<InteractiveImage>> getFavoriteInteractiveImageOnline(int limit, int skip) {
        return mDownloadApiInterface.getInteractiveImageFavorite(limit, skip);
    }

    public Call<java.util.ArrayList<VideoCourse>> getFavoriteVideoCourseOnline(int limit, int skip) {
        return mDownloadApiInterface.getVideoCourseFavorite(limit, skip);
    }

    public Call<java.util.ArrayList<InteractiveVideo>> getFavoriteInteractiveVideoOnline(int limit, int skip) {
        return mDownloadApiInterface.getInteractiveVideoFavorite(limit, skip);
    }

    public Call<java.util.ArrayList<RecommendedApiObject>> getFavoriteCourseOnline(int limit, int skip) {
        return mDownloadApiInterface.getCourseFavorite(limit, skip);
    }

    public Call<UserRating> addRating(String objectId, CourseReview courseReview) {
        return mDownloadApiInterface.addRating(objectId, courseReview);
    }

    public Call<ResponseBody> addFavorite(boolean isFavorite, String objectId, String courseType) {
        return mDownloadApiInterface.addCourseFavorite(objectId, isFavorite, courseType);
    }

    public Call<ResponseBody> addMicroCourseFavorite(boolean isFavorite, String objectId, String courseType) {
        return mDownloadApiInterface.addMicroCourseFavorite(objectId, isFavorite, courseType);
    }

    public Call<Blog> fetchBlog(String objectId) {
        return mDownloadApiInterface.getBlog(objectId);
    }

    public Call<ResponseBody> fetchBlogDetails(String objectId) {
        return mDownloadApiInterface.getBlogDetails(objectId);
    }

    public Call<ResponseBody> fetchBlogUserDetails(String objectId) {
        return mDownloadApiInterface.getBlogUserDetails(objectId);
    }

    public Call<BlogResponse> fetchBlogList(int limit, int skip) {
        return mDownloadApiInterface.getBlogList(limit, skip);
    }

    public Call<ArrayList<BlogComment>> fetchBlogComments(String objectId) {
        final BlogComment blogComment = new BlogComment();
        blogComment.setBlogId(objectId);
        return mDownloadApiInterface.getBlogComments(blogComment);
    }

    public Call<ArrayList<Curriculum>> fetchCurriculums() {
        return mDownloadApiInterface.getCurriculumList();
    }

    public Call<BlogComment> uploadBlogComment(BlogComment blogComment) {
        return mUploadApiInterface.uploadBlogComment(blogComment);
    }

//    public Call<ArrayList<LearningMap>> fetchLearningMaps() {
//        return mDownloadApiInterface.getLearningMapList();
//    }

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

    public Call<java.util.ArrayList<Question>> getQuestionFromSkill(String strSkillId, int skip, int limit) {
        return mSearchApiInterface.getQuestionFromSkill(strSkillId, skip, limit);
    }

    public Call<SkillMasteryQuestionGetData> fetchBySkillAndComplexityLevel(MasteryRequestObject masteryRequestObject) {
        return mSearchApiInterface.fetchBySkillAndComplexityLevel(masteryRequestObject);
    }

    public Call<java.util.ArrayList<SkillMasteryQuestionGetData>> fetchBySkillListAndComplexityLevel(MasteryRequestObject masteryRequestObject) {
        return mSearchApiInterface.fetchBySkillListAndComplexityLevel(masteryRequestObject);
    }

    public Call<java.util.ArrayList<AboutCourseExt>> getPrerequisiteCourses(PrerequisiteCoursesPostData prerequisiteCoursesPostData) {
        return mDownloadApiInterface.getPrerequisiteCourses(prerequisiteCoursesPostData);
    }

    public Call<java.util.ArrayList<Question>> getQuestionFromSkillEs(String strSkillId, int limit) {
        return mSearchApiInterface.getQuestionsFromSkillIdEs(strSkillId, limit);
    }

    public Call<ArrayList<TrackingRoute>> fetchTrackingRoute(String groupId) {
        return mDownloadApiInterface.getRoutesFromGroupId(groupId);
    }

    public Call<OGMetaDataResponse> getOGData(java.util.ArrayList<String> oGDataList) {
        return mDownloadApiInterface.getOGData(oGDataList);
    }

    public Call<StudentGradeMapping> getSubjectFromInstituteGradeSection(String instituteId, String gradeId, String sectionId) {
        return mSearchApiInterface.getSubjectFromInstituteGradeSection(instituteId, gradeId, sectionId);
    }

    public Call<SearchResourcesResults> getRecommendedResources(java.util.ArrayList<String> subjectId, String topicId, String gradeId, int skip, int limit) {
        if (TextUtils.isEmpty(topicId)) {
            topicId = null;
        }
        if (TextUtils.isEmpty(gradeId)) {
            gradeId = null;
        }
        RecommendedResourceParams params = new RecommendedResourceParams(skip, limit, gradeId, subjectId, topicId);
        return mSearchApiInterface.getResourceBySubjectGrade(params);
    }

    public Call<SearchCoursesResults> getRecommendedCourses(java.util.ArrayList<String> subjectId, String topicId, String gradeId, int skip, int limit, String[] courseType, String[] featureType) {
        if (TextUtils.isEmpty(topicId)) {
            topicId = null;
        }
        if (TextUtils.isEmpty(gradeId)) {
            gradeId = null;
        }
        RecommendedCourseParams params = new RecommendedCourseParams(skip, limit, gradeId, subjectId, topicId, courseType, featureType);
        return mSearchApiInterface.getCourseBySubjectGrade(params);
    }
//    public Call<SearchResults> getRecommendedCourses(String subjectId, String topicId, String gradeId, int skip, int limit) {
//        SearchRecommendedCoursesParams params1 = new SearchRecommendedCoursesParams();
//        params1.setSubjectId(subjectId);
//        ;
//        params1.setInstituteId("LILOPENINSTITUTE");
//
//        return mSearchApiInterface.getCourseSearchResultsBySubjectEs(params1);
//    }

    public Call<java.util.ArrayList<HomeModel.StudentScore>> getStudentMapData(java.util.ArrayList<String> subjectId, String gradeId, String sectionId, String topicId) {
        if (TextUtils.isEmpty(topicId)) {
            topicId = null;
        }
        LearningMapAggregatesParams params = new LearningMapAggregatesParams(0, 0, gradeId, subjectId, sectionId, topicId);
        return mSearchApiInterface.getStudentAggregateDataForMap(params);
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

    public Call<ResponseBody> updateUserProfile(UserProfile userProfile) {
        return mUploadApiInterface.updateUserProfile(userProfile);
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

    public Call<java.util.ArrayList<Training>> fetchUpcomingTrainings(int skip, int limit) {
        return mDownloadApiInterface.fetchUpcomingTrainings(skip, limit);
    }

    public Call<Training> fetchTraining(String id) {
        return mDownloadApiInterface.fetchTraining(id);
    }

    public Call<EnrollTrainingResponse> enrollTraining(String trainingId) {
        return mDownloadApiInterface.enrollTraining(trainingId);
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

    public Call<ResponseBody> deleteAnnotation(String id) {
        return mUploadApiInterface.deleteAnnotation(id);
    }

    public Call<java.util.ArrayList<AnalysisActivityData>> fetchActivityData(String subid,String startdate,String enddate) {
        ActivityData params = new ActivityData();
        params.setSubjectId(subid);
        params.setEndDate(enddate);
        params.setStartDate(startdate);
        return mDownloadApiInterface.fetchActivityData(params);
    }

    public Call<java.util.ArrayList<AnalysisActivityData>> fetchLearningData(String subid,String startdate,String enddate) {
        ActivityData params = new ActivityData();
        params.setSubjectId(subid);
        params.setEndDate(enddate);
        params.setStartDate(startdate);
        return mDownloadApiInterface.fetchLearningData(params);
    }

    public Call<java.util.ArrayList<AnalysisActivityRecentlyRead>> fetchRecentlyReadData(String subid,int limit, int skip) {
        return mDownloadApiInterface.getRecentlyRead(subid,skip,limit);
    }

    public Call<java.util.ArrayList<AnalysisTopicCovered>> fetchTopicData(String subid, int limit, int skip) {
        return mDownloadApiInterface.getTopicCovered(subid,skip,limit);
    }

    public Call<PerformanceResponseCount> fetchPerformanceCount(String subid) {
        return mDownloadApiInterface.getaggregatedResponseCount(subid);
    }

    /*To upload user course progress each time object created*/
    public Call<ResponseBody> uploadUserCourseProgress(UserCourseProgress userCourseProgress) {
        return mUploadApiInterface.uploadUserCourseProgress(userCourseProgress);
    }


}
