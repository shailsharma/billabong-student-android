package in.securelearning.lil.android.syncadapter.job;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.BlogReview;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.CustomSection;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupPostsNResponse;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.QuizWeb;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.syncadapter.dataobject.ServerDataPackage;
import in.securelearning.lil.android.syncadapter.dataobject.TeacherGradeMapping;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadArrayJob;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadCourseJobWeb;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadJob;
import in.securelearning.lil.android.syncadapter.job.download.BaseDownloadJobWeb;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseConceptMapJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseDigitalBookJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseInteractiveImageJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseInteractiveVideoJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCoursePopUpsJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseVideoCourseJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadActivityDetailsJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAssignmentJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAssignmentResponseJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadBlogCommentsJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadBlogDetailsJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadBroadcastNotificationJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadCalEventJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadConceptMapJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadCuratorMappingListJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadCurriculumListJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadCustomSectionJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadDigitalBookJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadGroupJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadGroupPostsAndResponseJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadInteractiveImageJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadInteractiveVideoJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadLearningDataJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadLearningMapListJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadNetworkGroupJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadNotificationJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadPackageJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadPerformanceCountDataJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadPeriodicEventsBulkJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadPeriodicEventsListJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadPopUpsJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadPostDataJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadPostResponseJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadQuizJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadQuizWebJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadRecentlyReadDataJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadResourceJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadTopicCoveredDataJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadTrackingRouteListJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadTrainingJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadTrainingsBulkJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadUserProfileJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadVideoCourseJsonJob;
import in.securelearning.lil.android.syncadapter.job.upload.BaseUploadJob;
import in.securelearning.lil.android.syncadapter.job.upload.PostAssignmentJob;
import in.securelearning.lil.android.syncadapter.job.upload.PostAssignmentResponseJob;
import in.securelearning.lil.android.syncadapter.job.upload.PostQuizJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadAssignedBadgeJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadBlogCommentJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadCalEventJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadPostDataJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadPostResponseJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadQuestionResponseJob;
import in.securelearning.lil.android.syncadapter.job.validation.BaseValidationJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateAboutCourseJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateAssignedBadgesJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateAssignmentJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateAssignmentResponseJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateBlogDetailsJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateBlogJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateBlogReviewJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateCalendarEventDataJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateConceptMapJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateCustomSectionJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateDigitalBookJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateGroupJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateGroupPostNResponseJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateInteractiveImageJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateInteractiveVideoJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateLearningNetworkPostDataJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateLearningNetworkPostResponseJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateNotificationJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidatePopUpsJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateQuizJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateQuizWebJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateResourceWebJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateUserJob;
import in.securelearning.lil.android.syncadapter.job.validation.ValidateVideoCourseJob;

/**
 * Job Creator
 */
public class JobCreator {
    /**
     * create quiz download Job
     *
     * @param objectId id of quiz to download
     * @return BaseDownloadJob
     */
    public static BaseDownloadJob<Quiz> createDownloadQuizJob(String objectId) {
        return new DownloadQuizJsonJob(objectId);
    }

    public static BaseUploadJob<Quiz> createUploadQuizJob(Quiz object) {
        return new PostQuizJob(object);
    }

    /**
     * create quiz download Job
     *
     * @param objectId id of quiz to download
     * @return BaseDownloadJobWeb
     */
    public static BaseDownloadJobWeb<QuizWeb> createDownloadWebQuizJob(String objectId) {
        return new DownloadQuizWebJsonJob(objectId);
    }

    public static BaseDownloadJobWeb<CustomSection> createDownloadCustomSectionJob(String objectId) {
        return new DownloadCustomSectionJsonJob(objectId);
    }

    /**
     * create assignment download job
     *
     * @param objectId id of assignment to download
     * @return BaseDownloadJob
     */
    public static BaseDownloadJob<Assignment> createDownloadAssignmentJob(String objectId) {
        return new DownloadAssignmentJsonJob(objectId);
    }

    /**
     * create user profile download job
     *
     * @param objectId id of user profile to download
     * @return BaseDownloadJob
     */
    public static BaseDownloadJob<UserProfile> createDownloadUserProfileJob(String objectId) {
        return new DownloadUserProfileJob(objectId);
    }

    /**
     * create assignment download job
     *
     * @param objectId id of assignment to download
     * @return BaseDownloadJob
     */
    public static BaseDownloadJob<Assignment> createDownloadAssignmentJob(String objectId, String notificationId) {
        return new DownloadAssignmentJsonJob(objectId, notificationId);
    }

    /**
     * create AssignmentResponse download job
     *
     * @param objectId id of AssignmentResponse to download
     * @return BaseDownloadJob
     */
    public static BaseDownloadJob<AssignmentResponse> createDownloadAssignmentResponseJob(String objectId, String notificationId) {
        return new DownloadAssignmentResponseJsonJob(objectId, notificationId);
    }

    /**
     * create postData download job
     *
     * @param objectId id of postData to download
     * @return
     */
    public static BaseDownloadJob<PostData> createDownloadPostDataJob(String objectId) {
        return new DownloadPostDataJsonJob(objectId);
    }

    /**
     * create postData download job
     *
     * @param objectId id of postData to download
     * @return
     */
    public static BaseDownloadJob<PostData> createDownloadPostDataJob(String objectId, String notificationId, boolean shouldShowNotification) {
        return new DownloadPostDataJsonJob(objectId, notificationId, shouldShowNotification);
    }

    /**
     * create calendar event download job
     *
     * @param objectId id of calendar event to download
     * @param b
     * @return
     */
    public static BaseDownloadJob<CalendarEvent> createDownloadCalendarEventJob(String objectId, String notificationId, boolean b) {
        return new DownloadCalEventJsonJob(objectId, notificationId, b, true);
    }


    /**
     * create PostResponse download job
     *
     * @param objectId id of PostResponse to download
     * @return
     */
    public static BaseDownloadJob<PostResponse> createPostResponseDownloadJob(String objectId) {
        return new DownloadPostResponseJsonJob(objectId);
    }

    /**
     * create PostResponse download job
     *
     * @param objectId id of PostResponse to download
     * @return
     */
    public static BaseDownloadJob<PostResponse> createPostResponseDownloadJob(String objectId, String notificationId, boolean shouldShowNotification) {
        return new DownloadPostResponseJsonJob(objectId, notificationId, shouldShowNotification);
    }

    /**
     * create group download job
     *
     * @param objectId id of group to download
     * @return
     */
    public static BaseDownloadJob<Group> createDownloadGroupJob(String objectId) {
        return new DownloadGroupJob(objectId);
    }


    /**
     * create group download job
     *
     * @param objectId id of group to download
     * @return
     */
    public static BaseDownloadJob<Group> createDownloadGroupJob(String objectId, String groupType) {
        return new DownloadGroupJob(objectId, groupType);
    }


    /**
     * create group post response download job
     *
     * @param objectId id of group  post response to download
     * @return
     */
    public static BaseDownloadJob<GroupPostsNResponse> createDownloadGroupPostAndResponseJob(String objectId) {
        return new DownloadGroupPostsAndResponseJob(objectId);
    }

    /**
     * create notification list download job
     *
     * @param userId id of notification list to download
     * @return
     */
    public static DownloadNotificationJsonJob createDownloadNotificationJsonJob(String userId) {
        return new DownloadNotificationJsonJob(userId);
    }

    public static DownloadBroadcastNotificationJsonJob createDownloadBroadcastNotificationJsonJob(String userId, long timestamp) {
        return new DownloadBroadcastNotificationJsonJob(userId, timestamp);
    }

    public static DownloadBroadcastNotificationJsonJob createDownloadBroadcastNotificationJsonJob(String userId) {
        return new DownloadBroadcastNotificationJsonJob(userId);
    }


    /**
     * create Server Data Package Download Job
     *
     * @return BaseDownloadJob
     */
    public static BaseDownloadJob<ServerDataPackage> createDownloadPackageJob() {
        return new DownloadPackageJsonJob();
    }

    /**
     * create popUps download Job
     *
     * @param objectId id of popUps to download
     * @return BaseDownloadJobWeb
     */
    public static BaseDownloadCourseJobWeb<PopUps> createDownloadPopUpsJob(String objectId) {
        return new DownloadPopUpsJsonJob(objectId);
    }

    /**
     * create conceptMap download Job
     *
     * @param objectId id of conceptMap to download
     * @return BaseDownloadJobWeb
     */
    public static BaseDownloadCourseJobWeb<ConceptMap> createDownloadConceptMapJob(String objectId) {
        return new DownloadConceptMapJsonJob(objectId);
    }

    public static BaseDownloadJobWeb<BlogDetails> createDownloadBlogDetailsJob(String objectId, String notificationId) {
        return new DownloadBlogDetailsJsonJob(objectId, notificationId);
    }


    //by rupsi
    public static DownloadActivityDetailsJsonJob createDownloadActivityJob(String subid, String startdate, String enddate) {
        return new DownloadActivityDetailsJsonJob(subid, startdate, enddate);
    }


    public static DownloadLearningDataJsonJob createDownloadLearningJob(String subid, String startdate, String enddate) {
        return new DownloadLearningDataJsonJob(subid, startdate, enddate);
    }

    public static DownloadRecentlyReadDataJsonJob createDownloadRecentlyReadJob(String subid, int limit, int skip) {
        return new DownloadRecentlyReadDataJsonJob(subid);
    }

    public static DownloadTopicCoveredDataJsonJob createDownloadTopicCoveredJob(String subid, int limit, int skip) {
        return new DownloadTopicCoveredDataJsonJob(subid);
    }


    public static DownloadPerformanceCountDataJsonJob createDownloadPerformanceCountJob(String subid) {
        return new DownloadPerformanceCountDataJsonJob(subid);
    }

    public static BaseDownloadJobWeb<AboutCourse> createDownloadAboutCourseDigitalBookJob(String objectId, String notificationId, boolean isBroadcast, boolean shouldCreateUserNotification) {
        return new DownloadAboutCourseDigitalBookJsonJob(objectId, notificationId, isBroadcast, shouldCreateUserNotification);
    }

    public static BaseDownloadJobWeb<AboutCourse> createDownloadAboutCourseVideoCourseJob(String objectId, String notificationId, boolean isBroadcast, boolean shouldCreateUserNotification) {
        return new DownloadAboutCourseVideoCourseJsonJob(objectId, notificationId, isBroadcast, shouldCreateUserNotification);
    }

    public static BaseDownloadJobWeb<AboutCourse> createDownloadAboutCourseDigitalBookJob(String objectId) {
        return new DownloadAboutCourseDigitalBookJsonJob(objectId);
    }

    public static BaseDownloadJobWeb<AboutCourse> createDownloadAboutCourseVideoCourseJob(String objectId) {
        return new DownloadAboutCourseVideoCourseJsonJob(objectId);
    }

    public static BaseDownloadJobWeb<AboutCourse> createDownloadAboutCourseConceptMapJob(String objectId) {
        return new DownloadAboutCourseConceptMapJsonJob(objectId);
    }

    public static BaseDownloadJobWeb<AboutCourse> createDownloadAboutCoursePopUpsJob(String objectId) {
        return new DownloadAboutCoursePopUpsJsonJob(objectId);
    }

    public static BaseDownloadJobWeb<AboutCourse> createDownloadAboutCourseInteractiveImageJob(String objectId) {
        return new DownloadAboutCourseInteractiveImageJsonJob(objectId);
    }

    public static BaseDownloadJobWeb<AboutCourse> createDownloadAboutCourseInteractiveVideoJob(String objectId) {
        return new DownloadAboutCourseInteractiveVideoJsonJob(objectId);
    }

    /**
     * create Digital book download Job
     *
     * @param objectId id of Digital book to download
     * @return BaseDownloadJobWeb
     */
    public static BaseDownloadCourseJobWeb<DigitalBook> createDownloadDigitalBookJob(String objectId, String notificationId, boolean isNotificationEnabled, boolean doJsonRefresh) {
        return new DownloadDigitalBookJsonJob(objectId, notificationId, isNotificationEnabled, doJsonRefresh);
    }

    /**
     * create interactiveImage download Job
     *
     * @param objectId id of interactiveImage to download
     * @return BaseDownloadJobWeb
     */
    public static BaseDownloadCourseJobWeb<InteractiveImage> createDownloadInteractiveImageJob(String objectId) {
        return new DownloadInteractiveImageJsonJob(objectId);
    }

    public static BaseDownloadCourseJobWeb<VideoCourse> createDownloadVideoCourseJob(String objectId) {
        return new DownloadVideoCourseJsonJob(objectId);
    }

    public static BaseDownloadCourseJobWeb<InteractiveVideo> createDownloadInteractiveVideoJob(String objectId) {
        return new DownloadInteractiveVideoJsonJob(objectId);
    }

    public static BaseDownloadJobWeb<Resource> createDownloadResourceJob(String objectId) {
        return new DownloadResourceJsonJob(objectId);
    }

    /**
     * create Assignment Post Job
     *
     * @param assignment to post
     * @return BaseUploadJob
     */
    public static BaseUploadJob<Assignment> createPostAssignmentJob(Assignment assignment) {
        return new PostAssignmentJob(assignment);
    }

    /**
     * create Question Response upload Job
     *
     * @param questionResponse to post
     * @return BaseUploadJob
     */
    public static BaseUploadJob<QuestionResponse> createUploadQuestionResponseJob(QuestionResponse questionResponse) {
        return new UploadQuestionResponseJob(questionResponse);
    }

    /**
     * create Assignment Post Jobs from list of assignments
     *
     * @param assignments to post
     * @return BaseUploadJob list
     */
    public static ArrayList<BaseUploadJob<Assignment>> createPostAssignmentJobList(ArrayList<Assignment> assignments) {
        ArrayList<BaseUploadJob<Assignment>> list = new ArrayList<>();
        for (int i = 0; i < assignments.size(); i++) {
            list.add(createPostAssignmentJob(assignments.get(i)));
        }
        return list;
    }

    /**
     * create Assignment Response Post Job
     *
     * @param assignmentResponse
     * @return BaseUploadJob
     */
    public static BaseUploadJob<AssignmentResponse> createPostAssignmentResponseJob(AssignmentResponse assignmentResponse) {
        return new PostAssignmentResponseJob(assignmentResponse);
    }

    /**
     * create Learning network PostData Job
     *
     * @param postData
     * @return
     */
    public static BaseUploadJob<PostData> createPostLearningNetworkPostDataJob(PostData postData) {
        return new UploadPostDataJob(postData);
    }

    /**
     * create calendar event Job
     *
     * @param calendarEvent
     * @return
     */
    public static BaseUploadJob<CalendarEvent> createPostCalendarEventDataJob(CalendarEvent calendarEvent) {
        return new UploadCalEventJob(calendarEvent);
    }

    /**
     * create AssignedBadges Job
     *
     * @param assignedBadges
     * @return
     */
    public static BaseUploadJob<AssignedBadges> createPostAssignedBadgeJob(AssignedBadges assignedBadges) {
        return new UploadAssignedBadgeJob(assignedBadges);
    }

    /**
     * create Learning network PostData Job
     *
     * @param postResponse
     * @return
     */
    public static BaseUploadJob<PostResponse> createPostResponseJob(PostResponse postResponse) {
        return new UploadPostResponseJob(postResponse);
    }

    public static BaseUploadJob<BlogComment> createBlogCommentJob(BlogComment blogComment) {
        return new UploadBlogCommentJob(blogComment);
    }


    /**
     * create calendar event Job
     *
     * @param calendarEvent
     * @return
     */
    public static BaseUploadJob<CalendarEvent> createUploadSectionProgresstDataJob(CalendarEvent calendarEvent) {
        return new UploadCalEventJob(calendarEvent);
    }

    /**
     * create Assignment Response Post Jobs from list of assignment responses
     *
     * @param assignmentResponses
     * @return BaseUploadJob list
     */
    public static ArrayList<BaseUploadJob<AssignmentResponse>> createPostAssignmentResponseJobList(ArrayList<AssignmentResponse> assignmentResponses) {
        ArrayList<BaseUploadJob<AssignmentResponse>> list = new ArrayList<>();
        for (int i = 0; i < assignmentResponses.size(); i++) {
            list.add(createPostAssignmentResponseJob(assignmentResponses.get(i)));
        }
        return list;
    }

    /**
     * create Learning network Post jobs from list of PostDatas
     *
     * @param postDatas
     * @return
     */
    public static ArrayList<BaseUploadJob<PostData>> createPostDataJobList(ArrayList<PostData> postDatas) {
        ArrayList<BaseUploadJob<PostData>> list = new ArrayList<>();
        for (int i = 0; i < postDatas.size(); i++) {
            list.add(createPostLearningNetworkPostDataJob(postDatas.get(i)));
        }
        return list;
    }

    /**
     * create Learning network PostResponse from list of PostResponses
     *
     * @param postResponses
     * @return
     */
    public static ArrayList<BaseUploadJob<PostResponse>> createPostResponseJobList(ArrayList<PostResponse> postResponses) {
        ArrayList<BaseUploadJob<PostResponse>> list = new ArrayList<>();
        for (int i = 0; i < postResponses.size(); i++) {
            list.add(createPostResponseJob(postResponses.get(i)));
        }
        return list;
    }

    public static ArrayList<BaseUploadJob<BlogComment>> createBlogCommentJobList(ArrayList<BlogComment> blogComments) {
        ArrayList<BaseUploadJob<BlogComment>> list = new ArrayList<>();
        for (int i = 0; i < blogComments.size(); i++) {
            list.add(createBlogCommentJob(blogComments.get(i)));
        }
        return list;
    }

    /**
     * create assigned badges list
     *
     * @param assignedBadges
     * @return
     */
    public static ArrayList<BaseUploadJob<AssignedBadges>> createAssignedBadgesJobList(ArrayList<AssignedBadges> assignedBadges) {
        ArrayList<BaseUploadJob<AssignedBadges>> list = new ArrayList<>();
        for (int i = 0; i < assignedBadges.size(); i++) {
            list.add(createPostAssignedBadgeJob(assignedBadges.get(i)));
        }
        return list;
    }

    /**
     * create calendar events list
     *
     * @param calendarEvents
     * @return
     */
    public static ArrayList<BaseUploadJob<CalendarEvent>> createCalendareventsJobList(ArrayList<CalendarEvent> calendarEvents) {
        ArrayList<BaseUploadJob<CalendarEvent>> list = new ArrayList<>();
        for (int i = 0; i < calendarEvents.size(); i++) {
            list.add(createPostCalendarEventDataJob(calendarEvents.get(i)));
        }
        return list;
    }

    /**
     * create assignment response validation job
     *
     * @param postData to validate
     * @return
     */
    public static BaseValidationJob<PostData> createPostDataValidationJob(PostData postData, boolean shouldShowNotification) {
        return new ValidateLearningNetworkPostDataJob(postData, shouldShowNotification);
    }

    /**
     * create calendarEvent validation job
     *
     * @param calendarEvent to validate
     * @return
     */
    public static BaseValidationJob<CalendarEvent> createCalendarEventValidationJob(CalendarEvent calendarEvent) {
        return new ValidateCalendarEventDataJob(calendarEvent);
    }

    /**
     * create post response validation job
     *
     * @param postResponse to validate
     * @return
     */
    public static BaseValidationJob<PostResponse> createPostResponseValidationJob(PostResponse postResponse, boolean shouldShowNotification) {
        return new ValidateLearningNetworkPostResponseJob(postResponse, shouldShowNotification);
    }

    /**
     * create assignedBadges validation job
     *
     * @param assignedBadges to validate
     * @return
     */
    public static BaseValidationJob<AssignedBadges> createAssignedBadgesValidationJob(AssignedBadges assignedBadges) {
        return new ValidateAssignedBadgesJob(assignedBadges);
    }

    /**
     * create noti validation job
     *
     * @param notification to validate
     * @return
     */
    public static BaseValidationJob<Notification> createNotificationValidationJob(Notification notification) {
        return new ValidateNotificationJob(notification);
    }


    /**
     * create Group validation job
     *
     * @param group to validate
     * @return
     */
    public static BaseValidationJob<Group> createGroupValidationJob(Group group) {
        return new ValidateGroupJob(group);
    }

    /**
     * create User Profile validation job
     *
     * @param userProfile to validate
     * @return
     */
    public static BaseValidationJob<UserProfile> createGroupValidationJob(UserProfile userProfile) {
        return new ValidateUserJob(userProfile);
    }

    /**
     * create userprofile validation job
     *
     * @param userProfile to validate
     * @return
     */
    public static BaseValidationJob<UserProfile> createUserProfileValidationJob(UserProfile userProfile) {
        return new ValidateUserJob(userProfile);
    }

    /**
     * create Group post and post response validation job
     *
     * @param groupPostsNResponse to validate
     * @return
     */
    public static BaseValidationJob<GroupPostsNResponse> createGroupPostNResponseValidationJob(GroupPostsNResponse groupPostsNResponse) {
        return new ValidateGroupPostNResponseJob(groupPostsNResponse);
    }

    /**
     * create learning network post data validation job
     *
     * @param assignmentResponse to validate
     * @return BaseValidationJob
     */
    public static BaseValidationJob<AssignmentResponse> createAssignmentResponseValidationJob(AssignmentResponse assignmentResponse) {
        return new ValidateAssignmentResponseJob(assignmentResponse);
    }

    /**
     * create assignment response validation jobs from list of assignment responses
     *
     * @param assignmentResponses to validate
     * @return BaseValidationJob list
     */
    public static ArrayList<BaseValidationJob<AssignmentResponse>> createAssignmentResponseValidationJobList(ArrayList<AssignmentResponse> assignmentResponses) {
        ArrayList<BaseValidationJob<AssignmentResponse>> list = new ArrayList<>();
        for (int i = 0; i < assignmentResponses.size(); i++) {
            list.add(createAssignmentResponseValidationJob(assignmentResponses.get(i)));
        }

        return list;
    }

    /**
     * create Learning network post data validation jobs from list of postdata
     *
     * @param postDatas to validate
     * @return
     */
    public static ArrayList<BaseValidationJob<PostData>> createPostDataValidationJobList(ArrayList<PostData> postDatas) {
        ArrayList<BaseValidationJob<PostData>> list = new ArrayList<>();
        for (int i = 0; i < postDatas.size(); i++) {
            list.add(createPostDataValidationJob(postDatas.get(i), false));
        }
        return list;
    }

    /**
     * create assignment validation job
     *
     * @param assignment to validate
     * @return BaseValidationJob
     */
    public static BaseValidationJob<Assignment> createAssignmentValidationJob(Assignment assignment) {
        return new ValidateAssignmentJob(assignment);
    }
    /**
     * create assignedBadges validation job
     *
     * @param assignment to validate
     * @return BaseValidationJob
     */


    /**
     * create assignment validation jobs from list of assignments
     *
     * @param assignments to validate
     * @return BaseValidationJob list
     */
    public static ArrayList<BaseValidationJob<Assignment>> createAssignmentValidationJobList(ArrayList<Assignment> assignments) {
        ArrayList<BaseValidationJob<Assignment>> list = new ArrayList<>();
        for (int i = 0; i < assignments.size(); i++) {
            list.add(createAssignmentValidationJob(assignments.get(i)));
        }
        return list;
    }

    /**
     * create quiz validation job
     *
     * @param quiz to validate
     * @return BaseValidationJob
     */
    public static BaseValidationJob<Quiz> createQuizValidationJob(Quiz quiz) {
        return new ValidateQuizJob(quiz);
    }

    /**
     * create quiz validation Jobs from list of quiz
     *
     * @param quizList to validate
     * @return BaseValidationJob list
     */
    public static ArrayList<BaseValidationJob<Quiz>> createQuizValidationJobList(ArrayList<Quiz> quizList) {
        ArrayList<BaseValidationJob<Quiz>> list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            list.add(createQuizValidationJob(quizList.get(i)));
        }
        return list;
    }

    /**
     * create DigitalBook validation job
     *
     * @param digitalBook to validate
     * @return BaseValidationJob
     */
    public static BaseValidationJob<DigitalBook> createDigitalBookValidationJob(DigitalBook digitalBook, AboutCourse aboutCourse, boolean isNotificationEnabled) {
        return new ValidateDigitalBookJob(digitalBook, aboutCourse, isNotificationEnabled);
    }

    /**
     * create PopUps validation job
     *
     * @param popUps
     * @return BaseValidationJob
     */
    public static BaseValidationJob<PopUps> createPopUpsValidationJob(PopUps popUps, AboutCourse aboutCourse) {
        return new ValidatePopUpsJob(popUps, aboutCourse);
    }

    /**
     * create InteractiveImage validation job
     *
     * @param interactiveImage
     * @return BaseValidationJob
     */
    public static BaseValidationJob<InteractiveImage> createInteractiveImageValidationJob(InteractiveImage interactiveImage, AboutCourse aboutCourse) {
        return new ValidateInteractiveImageJob(interactiveImage, aboutCourse);
    }

    /**
     * create ConceptMap validation job
     *
     * @param conceptMap
     * @return BaseValidationJob
     */
    public static BaseValidationJob<ConceptMap> createConceptMapValidationJob(ConceptMap conceptMap, AboutCourse aboutCourse) {
        return new ValidateConceptMapJob(conceptMap, aboutCourse);
    }

    public static BaseValidationJob<VideoCourse> createVideoCourseValidationJob(VideoCourse videoCourse, AboutCourse aboutCourse) {
        return new ValidateVideoCourseJob(videoCourse, aboutCourse);
    }

    public static BaseValidationJob<InteractiveVideo> createInteractiveVideoValidationJob(InteractiveVideo interactiveVideo, AboutCourse aboutCourse) {
        return new ValidateInteractiveVideoJob(interactiveVideo, aboutCourse);
    }

    public static BaseValidationJob<AboutCourse> createAboutCourseValidationJob(AboutCourse aboutCourse, boolean isBroadcast) {
        return new ValidateAboutCourseJob(aboutCourse, isBroadcast);
    }

    /**
     * create Quiz validation job
     *
     * @param quiz
     * @return BaseValidationJob
     */
    public static BaseValidationJob<QuizWeb> createQuizWebValidationJob(QuizWeb quiz) {
        return new ValidateQuizWebJob(quiz);
    }

    public static BaseValidationJob<CustomSection> createCustomSectionValidationJob(CustomSection customSection) {
        return new ValidateCustomSectionJob(customSection);
    }

    public static BaseValidationJob<Resource> createResourceWebValidationJob(Resource resource) {
        return new ValidateResourceWebJob(resource);
    }

    public static BaseValidationJob<Blog> createBlogValidationJob(Blog blog) {
        return new ValidateBlogJob(blog);
    }

    public static BaseValidationJob<BlogDetails> createBlogDetailsValidationJob(BlogDetails blogDetails) {
        return new ValidateBlogDetailsJob(blogDetails);
    }

    public static BaseValidationJob<BlogReview> createBlogReviewValidationJob(BlogReview blogReview) {
        return new ValidateBlogReviewJob(blogReview);
    }

    public static BaseDownloadArrayJob<in.securelearning.lil.android.base.utils.ArrayList<BlogComment>> createDownloadBlogCommentsJob(String objectId) {
        return new DownloadBlogCommentsJob(objectId);
    }

    public static BaseDownloadArrayJob<in.securelearning.lil.android.base.utils.ArrayList<Curriculum>> createCurriculumDownloadJob() {
        return new DownloadCurriculumListJob();
    }

    public static BaseDownloadArrayJob<in.securelearning.lil.android.base.utils.ArrayList<PeriodNew>> createPeriodNewDownloadJob(String startTime, String endTime) {
        return new DownloadPeriodicEventsListJob(startTime, endTime);
    }

    public static DownloadPeriodicEventsBulkJob createPeriodNewBulkDownloadJob(String objectId, String startTime, String endTime, boolean updatePreference) {
        return new DownloadPeriodicEventsBulkJob(objectId, startTime, endTime, updatePreference);
    }

    public static BaseDownloadArrayJob<in.securelearning.lil.android.base.utils.ArrayList<TrackingRoute>> createTrackingRouteDownloadJob(String objectId) {
        return new DownloadTrackingRouteListJob(objectId);
    }

    public static BaseDownloadArrayJob<in.securelearning.lil.android.base.utils.ArrayList<LearningMap>> createLearningMapDownloadJob() {
        return new DownloadLearningMapListJob();
    }

    public static BaseDownloadJob<TeacherGradeMapping> createCuratorMappingDownloadJob() {
        return new DownloadCuratorMappingListJob("");
    }

    public static DownloadTrainingsBulkJob createTrainingsBulkDownloadJob() {
        return new DownloadTrainingsBulkJob();
    }

    public static DownloadTrainingJob createTrainingDownloadJob(String objectId, String notificationId) {
        return new DownloadTrainingJob(objectId, notificationId);
    }

    public static DownloadNetworkGroupJob createNetworkGroupDownloadJob() {
        return new DownloadNetworkGroupJob();
    }
}
