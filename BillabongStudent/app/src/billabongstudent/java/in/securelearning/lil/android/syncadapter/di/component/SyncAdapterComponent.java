package in.securelearning.lil.android.syncadapter.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.syncadapter.fcmservices.FlavorFCMReceiverService;
import in.securelearning.lil.android.syncadapter.ftp.FtpFunctions;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseConceptMapJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseDigitalBookJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseInteractiveImageJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseInteractiveVideoJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCoursePopUpsJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAboutCourseVideoCourseJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadActivityDetailsJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAssignedBadgeJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAssignmentJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadAssignmentResponseJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadBlogCommentsJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadBlogDetailsJsonJob;
import in.securelearning.lil.android.syncadapter.job.download.DownloadBlogJsonJob;
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
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.job.upload.PostAssignmentJob;
import in.securelearning.lil.android.syncadapter.job.upload.PostAssignmentResponseJob;
import in.securelearning.lil.android.syncadapter.job.upload.PostQuizJob;
import in.securelearning.lil.android.syncadapter.job.upload.PostQuizResourcesJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadAssignedBadgeJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadBlogCommentJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadCalEventJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadEventResourcesJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadPostDataJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadPostDataResourcesJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadPostResponseJob;
import in.securelearning.lil.android.syncadapter.job.upload.UploadQuestionResponseJob;
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
import in.securelearning.lil.android.syncadapter.model.FlavorJobModel;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.model.FlavorSyncServiceModel;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.model.SyncServiceModel;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;
import in.securelearning.lil.android.syncadapter.receiver.ConnectivityChangeReceiver;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.service.AssignmentService;
import in.securelearning.lil.android.syncadapter.service.CourseService;
import in.securelearning.lil.android.syncadapter.service.MessageService;
import in.securelearning.lil.android.syncadapter.service.PeriodService;
import in.securelearning.lil.android.syncadapter.service.ReminderService;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.UserService;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
public interface SyncAdapterComponent extends BaseComponent {

    NetworkModel networkModel();

    void inject(PostAssignmentJob job);

    void inject(PostAssignmentResponseJob job);

    void inject(UploadPostDataJob job);

    void inject(UploadPostResponseJob job);

    void inject(UploadAssignedBadgeJob job);

    void inject(PostQuizResourcesJob job);

    void inject(UploadPostDataResourcesJob job);

    void inject(DownloadAssignmentJsonJob job);

    void inject(DownloadAssignmentResponseJsonJob job);

    void inject(DownloadPostDataJsonJob job);

    void inject(DownloadGroupJob job);

    void inject(DownloadNotificationJsonJob job);

    void inject(DownloadQuizJsonJob job);

    void inject(DownloadGroupPostsAndResponseJob job);

    void inject(DownloadPostResponseJsonJob job);

    void inject(DownloadAssignedBadgeJsonJob job);

    void inject(DownloadDigitalBookJsonJob job);

    void inject(DownloadPopUpsJsonJob job);

    void inject(DownloadCustomSectionJsonJob job);

    void inject(DownloadConceptMapJsonJob job);

    void inject(DownloadInteractiveImageJsonJob job);

    void inject(DownloadVideoCourseJsonJob job);

    void inject(DownloadQuizWebJsonJob job);

    void inject(DownloadResourceJsonJob job);

    void inject(DownloadPackageJsonJob job);

    void inject(ValidateQuizJob job);

    void inject(ValidateAssignmentJob job);

    void inject(ValidateAssignmentResponseJob job);

    void inject(ValidateLearningNetworkPostDataJob job);

    void inject(ValidateGroupJob job);

    void inject(ValidateGroupPostNResponseJob job);

    void inject(ValidateLearningNetworkPostResponseJob job);

    void inject(ValidateUserJob job);

    void inject(ValidateAssignedBadgesJob job);

    void inject(ValidateNotificationJob job);

    void inject(ValidateDigitalBookJob job);

    void inject(ValidatePopUpsJob job);

    void inject(ValidateConceptMapJob job);

    void inject(ValidateInteractiveImageJob job);

    void inject(ValidateCustomSectionJob job);

    void inject(ValidateVideoCourseJob job);

    void inject(ValidateQuizWebJob job);

    void inject(ValidateResourceWebJob job);

    void inject(JobModel model);

    void inject(BadgesModel model);

    void inject(NetworkModel model);

    void inject(GroupModel model);

    void inject(SyncServiceModel model);

    void inject(ConnectivityChangeReceiver receiver);

    void inject(SyncService service);

    void inject(FtpFunctions ftpFunctions);

    void inject(ApiModule ftpFunctions);

    void inject(ResourceNetworkOperation resourceNetworkOperation);

    void inject(DownloadBlogJsonJob job);

    void inject(ValidateBlogJob job);

    void inject(ValidateBlogDetailsJob job);

    void inject(ValidateBlogReviewJob job);

    void inject(DownloadBlogCommentsJob job);

    void inject(UploadCalEventJob job);

    void inject(DownloadCalEventJsonJob job);

    void inject(ValidateCalendarEventDataJob job);

    void inject(ValidateAboutCourseJob job);

    void inject(DownloadBroadcastNotificationJsonJob job);

    void inject(DownloadBlogDetailsJsonJob job);

    void inject(UploadBlogCommentJob job);

    void inject(WebPlayerLiveModel model);

    void inject(DownloadUserProfileJob model);

    void inject(DownloadCurriculumListJob job);

    void inject(MessageService object);

    void inject(UploadEventResourcesJob job);

    void inject(DownloadInteractiveVideoJsonJob job);

    void inject(ValidateInteractiveVideoJob job);

    void inject(DownloadLearningMapListJob downloadLearningMapListJob);

    void inject(DownloadPeriodicEventsListJob downloadPeriodicEventsListJob);

    void inject(PostQuizJob job);

    void inject(DownloadAboutCourseDigitalBookJsonJob downloadAboutCourseDigitalBookJsonJob);

    void inject(DownloadAboutCourseVideoCourseJsonJob downloadAboutCourseVideoCourseJsonJob);

    void inject(DownloadAboutCoursePopUpsJsonJob downloadAboutCoursePopUpsJsonJob);

    void inject(DownloadAboutCourseConceptMapJsonJob downloadAboutCourseConceptMapJsonJob);

    void inject(DownloadAboutCourseInteractiveVideoJsonJob downloadAboutCourseInteractiveVideoJsonJob);

    void inject(DownloadAboutCourseInteractiveImageJsonJob downloadAboutCourseInteractiveImageJsonJob);

    void inject(in.securelearning.lil.android.syncadapter.fcmservices.FCMReceiverService fcmReceiverService);

    void inject(DownloadTrackingRouteListJob downloadTrackingRouteListJob);

    void inject(OgUtils ogUtils);

    void inject(DownloadPeriodicEventsBulkJob job);

    void inject(PeriodService periodService);

    void inject(DownloadCuratorMappingListJob downloadCuratorMappingListJob);

    void inject(UserService userService);

    void inject(AssignmentService assignmentService);

    void inject(CourseService courseService);

    void inject(ReminderService reminderService);

    void inject(FlavorSyncServiceModel flavorSyncServiceModel);

    void inject(FlavorNetworkModel flavorNetworkModel);

    void inject(FlavorJobModel flavorJobModel);

    void inject(FlavorFCMReceiverService flavorFCMReceiverService);

    void inject(UploadQuestionResponseJob uploadQuestionResponseJob);

    void inject(DownloadTrainingsBulkJob downloadTrainingsBulkJob);

    void inject(DownloadTrainingJob downloadTrainingJob);

    void inject(DownloadActivityDetailsJsonJob downloadActivityDetailsJsonJob);

    void inject(DownloadLearningDataJsonJob downloadActivityDetailsJsonJob);

    void inject(DownloadTopicCoveredDataJsonJob downloadTopicCoveredDataJsonJob);

    void inject(DownloadRecentlyReadDataJsonJob downloadRecentlyReadDataJsonJob);

    void inject(DownloadPerformanceCountDataJsonJob downloadPerformanceCountDataJsonJob);

    void inject(DownloadNetworkGroupJob downloadNetworkGroupJob);
}
