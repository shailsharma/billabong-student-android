package in.securelearning.lil.android.syncadapter.model;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.AssignmentStage;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityData;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityRecentlyRead;
import in.securelearning.lil.android.base.dataobjects.AnalysisTopicCovered;
import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentMinimal;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.BlogReview;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.CuratorMapping;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.CustomSection;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupPostsNResponse;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.dataobjects.ObjectInfo;
import in.securelearning.lil.android.base.dataobjects.PerformanceResponseCount;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.QuizWeb;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.dataobjects.SubjectSuper;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AboutCourseModel;
import in.securelearning.lil.android.base.model.AnalysisActivityRecentlyReadModel;
import in.securelearning.lil.android.base.model.AnalysisLearningModel;
import in.securelearning.lil.android.base.model.AnalysisTopicCoveredModel;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.AnalysisActivityModel;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.BlogCommentModel;
import in.securelearning.lil.android.base.model.BlogModel;
import in.securelearning.lil.android.base.model.BlogReviewModel;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.ConceptMapModel;
import in.securelearning.lil.android.base.model.CuratorMappingModel;
import in.securelearning.lil.android.base.model.CurriculumModel;
import in.securelearning.lil.android.base.model.CustomSectionModel;
import in.securelearning.lil.android.base.model.DigitalBookModel;
import in.securelearning.lil.android.base.model.DownloadedCourseModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.InteractiveImageModel;
import in.securelearning.lil.android.base.model.InteractiveVideoModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.LearningMapModel;
import in.securelearning.lil.android.base.model.NotificationModel;
import in.securelearning.lil.android.base.model.PerformanceResponseCountModel;
import in.securelearning.lil.android.base.model.PeriodicEventsModel;
import in.securelearning.lil.android.base.model.PopUpsModel;
import in.securelearning.lil.android.base.model.PostModel;
import in.securelearning.lil.android.base.model.PostResponseModel;
import in.securelearning.lil.android.base.model.QuestionResponseModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.model.QuizWebModel;
import in.securelearning.lil.android.base.model.RecommendedCourseModel;
import in.securelearning.lil.android.base.model.ResourceModel;
import in.securelearning.lil.android.base.model.TrackingModel;
import in.securelearning.lil.android.base.model.TrainingModel;
import in.securelearning.lil.android.base.model.TrainingSessionModel;
import in.securelearning.lil.android.base.model.VideoCourseModel;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.ServerDataPackage;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;

/**
 * Model for database access.
 */
public class JobModel extends BaseModel {
//    private final String TAG = this.getClass().getCanonicalName();
    /**
     * model for access to quiz
     */
    @Inject
    QuizModel mQuizModel;
    @Inject
    QuizWebModel mQuizWebModel;
    /**
     * model for access to resources
     */
    @Inject
    ResourceModel mResourceModel;
    /**
     * model for access to group
     */
    @Inject
    GroupModel mGroupModel;
    /**
     * model for access to postdata
     */
    @Inject
    AppUserModel mAppUserModel;
    /**
     * model for access to postdata
     */
    @Inject
    PostModel mPostModel;

    @Inject
    PostResponseModel mPostResponseModel;
    /**
     * model for access to assignment
     */
    @Inject
    NotificationModel mNotificationModel;

    @Inject
    PeriodicEventsModel mPeriodicEventsModel;

    @Inject
    AssignmentModel mAssignmentModel;
    /**
     * model for access to assignment response
     */
    @Inject
    AssignmentResponseModel mAssignmentResponseModel;
    /**
     * model for access to digital book
     */
    @Inject
    DigitalBookModel mDigitalBookModel;
    /**
     * model for access to pop ups
     */
    @Inject
    PopUpsModel mPopUpsModel;
    @Inject
    VideoCourseModel mVideoCourseModel;
    @Inject
    InteractiveVideoModel mInteractiveVideoModel;
    @Inject
    AboutCourseModel mAboutCourseModel;
    /**
     * model for access to badges
     */
    @Inject
    BadgesModel mBadgesModel;
    /**
     * model for access to concept maps
     */
    @Inject
    ConceptMapModel mConceptMapModel;
    @Inject
    CustomSectionModel mCustomSectionModel;
    /**
     * model for access to calendar events
     */
    @Inject
    CalEventModel mCalEventModel;
    /**
     * model for access to interactive image
     */
    @Inject
    InteractiveImageModel mInteractiveImageModel;
    /**
     * model for access to blog
     */
    @Inject
    BlogModel mBlogModel;
    @Inject
    BlogCommentModel mBlogCommentModel;
    @Inject
    CurriculumModel mCurriculumModel;
    @Inject
    BlogReviewModel mBlogReviewModel;
    @Inject
    RecommendedCourseModel mRecommendedCourseModel;
    @Inject
    LearningMapModel mLearningMapModel;
    @Inject
    TrackingModel mTrackingModel;
    @Inject
    CuratorMappingModel mCuratorMappingModel;
    @Inject
    InternalNotificationModel mInternalNotificationModel;
    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;
    @Inject
    DownloadedCourseModel mDownloadedCourseModel;

    @Inject
    QuestionResponseModel mQuestionResponseModel;

    @Inject
    TrainingModel mTrainingModel;

    @Inject
    AnalysisActivityModel mAnalysisActivityModel;

    @Inject
    AnalysisLearningModel mAnalysisLearningModel;

    @Inject
    AnalysisActivityRecentlyReadModel mAnalysisRecentlyReadModel;

    @Inject
    AnalysisTopicCoveredModel mTopicCoveredModel;

    @Inject
    TrainingSessionModel mTrainingSessionModel;

    @Inject
    PerformanceResponseCountModel mPerformanceResponseCountModel;

    public JobModel() {
//        Log.d(TAG, "inject");
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * save , update and return data package
     *
     * @param dataPackage to save
     * @return updated data package
     */
    public ServerDataPackage saveDataPackage(ServerDataPackage dataPackage) {
        /*save and update quiz list*/
        dataPackage.setQuizList(saveQuizList(dataPackage.getQuizList()));

        /*save and update assignment list*/
        dataPackage.setAssignmentList(saveAssignmentList(dataPackage.getAssignmentList()));

        /*save and update assignment response list*/
        dataPackage.setAssignmentResponseList(saveAssignmentResponseList(dataPackage.getAssignmentResponseList()));


        /*save and update post data response list*/
        dataPackage.setLearningPostDataList(savePostDataList(dataPackage.getLearningNetworkPostDataList()));


        /*save and update postResponse  response list*/
        dataPackage.setPostResponsesList(savePostResponseList(dataPackage.getPostResponsesList()));

        /*return updated data package*/
        return dataPackage;
    }

    /**
     * save a list of quiz
     *
     * @param list of quiz to  save
     * @return updated quiz list
     */
    public ArrayList<Quiz> saveQuizList(ArrayList<Quiz> list) {

        /*save quiz one by one*/
        for (int i = 0; i < list.size(); i++) {

            /*save quiz*/
            Quiz quiz = saveQuiz(list.get(i));

            /*update document id of the list item*/
            list.get(i).setDocId(quiz.getDocId());
        }

        /*return updated list*/
        return list;
    }

    /**
     * save a list of assignments
     *
     * @param list of assignments to save
     * @return updated list of assignments
     */
    public ArrayList<Assignment> saveAssignmentList(ArrayList<Assignment> list) {

        /*save assignment one by one*/
        for (int i = 0; i < list.size(); i++) {

            /*save assignment*/
            Assignment assignment = saveAssignment(list.get(i));

            /*update document id of the list item*/
            list.get(i).setDocId(assignment.getDocId());
        }

        /*return updated list*/
        return list;
    }

    /**
     * save list of assignment responses
     *
     * @param list of assignment responses to save
     * @return updated list of assignment responses
     */
    public ArrayList<AssignmentResponse> saveAssignmentResponseList(ArrayList<AssignmentResponse> list) {

        /*save assignment response one by one*/
        for (int i = 0; i < list.size(); i++) {

            /*save assignment response*/
            AssignmentResponse assignmentResponse = saveAssignmentResponse(list.get(i));

            /*update document id of the list item*/
            list.get(i).setDocId(assignmentResponse.getDocId());
        }

        /*return updated list*/
        return list;
    }

    /**
     * save list of post data
     *
     * @param list of post data to save
     * @return updated list of post data
     */
    public ArrayList<PostData> savePostDataList(ArrayList<PostData> list) {

        /*save PostData response one by one*/
        for (int i = 0; i < list.size(); i++) {

            /*save PostData response*/
            PostData postData = saveLearningNetworkPostData(list.get(i));

            /*update document id of the list item*/
            list.get(i).setDocId(postData.getDocId());
        }

        /*return updated list*/
        return list;
    }

    public ArrayList<PostResponse> savePostResponseList(ArrayList<PostResponse> list) {

        /*save PostResponse response one by one*/
        for (int i = 0; i < list.size(); i++) {

            /*save PostResponse response*/
            PostResponse postResponse = savePostResponse(list.get(i));

            /*update document id of the list item*/
            list.get(i).setDocId(postResponse.getDocId());
        }

        /*return updated list*/
        return list;
    }

    /**
     * save a quiz
     *
     * @param quiz to save
     * @return updated quiz
     */
    public Quiz saveQuiz(Quiz quiz) {

        /*save quiz to database and return response of the save call*/
        return mQuizModel.saveQuizToDatabase(quiz);
    }

    public QuizWeb saveQuizWeb(QuizWeb quiz) {

        /*save quiz to database and return response of the save call*/
        return mQuizWebModel.saveObject(quiz);
    }

    public CustomSection saveCustomSection(CustomSection customSection) {

        /*save CustomSection to database and return response of the save call*/
        return mCustomSectionModel.saveObject(customSection);
    }

    public Resource saveResource(Resource resource) {
        return mResourceModel.saveResource(resource);
    }

    /**
     * save an assignment
     *
     * @param assignment to save
     * @return updated assignment
     */
    public Assignment saveAssignment(Assignment assignment) {

        /*save assignment to database and return response of the save call*/
        return mAssignmentModel.saveAssignment(assignment);
    }

    /**
     * save an PostResponse
     *
     * @param postResponse to save
     * @return updated PostResponse
     */
    public PostResponse savePostResponse(PostResponse postResponse) {
        return mPostResponseModel.saveObject(postResponse);
    }

    public AssignedBadges saveAssignedBadges(AssignedBadges assignedBadges) {
        return mBadgesModel.saveAssignedBadges(assignedBadges);
    }

    public Notification saveNotification(Notification notification) {
        return mNotificationModel.saveNotification(notification);
    }

    public Notification saveNotificationUnsafe(Notification notification) {
        return mNotificationModel.saveNotificationUnsafe(notification);
    }

    /**
     * save an PostData
     *
     * @param postData to save
     * @return updated PostData
     */
    public PostData saveLearningNetworkPostData(PostData postData) {
        return mPostModel.saveObject(postData);
    }

    public CalendarEvent saveCalendarEventData(CalendarEvent calendarEvent) {
        return mCalEventModel.saveCalendarEvent(calendarEvent);
    }

    public QuestionResponse saveQuestionResponse(QuestionResponse questionResponse) {
        return mQuestionResponseModel.saveQuestionResponse(questionResponse);
    }

    /**
     * save an Group
     *
     * @param group to save
     * @return updated Group
     */
    public Group saveGroup(Group group) {
        return mGroupModel.saveGroup(group);
    }


    /**
     * save an FitnessUserProfile
     *
     * @param userProfile to save
     * @return updated FitnessUserProfile
     */
    public UserProfile saveUserProfile(UserProfile userProfile) {
        return mAppUserModel.saveUserProfile(userProfile);
    }

    public GroupPostsNResponse saveGroupPostAndPostResponse(GroupPostsNResponse groupPostsNResponse) {

        if (groupPostsNResponse != null) {
            if (groupPostsNResponse.getPost() != null) {
                for (PostData postData : groupPostsNResponse.getPost()) {
                    postData.setSyncStatus(SyncStatus.JSON_SYNC.toString());
                    PostData alreadySavedObject = mPostModel.getObjectById(postData.getObjectId());
                    if (alreadySavedObject != null && TextUtils.isEmpty(alreadySavedObject.getObjectId())) {
                        mPostModel.saveObject(postData);
                    }
                }
            }

            if (groupPostsNResponse.getPostResponse() != null) {
                for (PostResponse postResponse : groupPostsNResponse.getPostResponse()) {
                    postResponse.setSyncStatus(SyncStatus.JSON_SYNC.toString());
                    PostResponse alreadySavedObject = mPostResponseModel.getObjectById(postResponse.getObjectId());
                    if (alreadySavedObject != null && TextUtils.isEmpty(alreadySavedObject.getObjectId())) {
                        mPostResponseModel.saveObject(postResponse);
                    }
                }
            }

        }


        return groupPostsNResponse;
    }

    /**
     * save an assignment response
     *
     * @param assignmentResponse to save
     * @return updated assignment response
     */
    public AssignmentResponse saveAssignmentResponse(AssignmentResponse assignmentResponse) {

        /*save assignment response to database and return response of the save call*/
        return mAssignmentResponseModel.saveAssignmentResponseToDatabase(assignmentResponse);
    }

    /**
     * save an digitalBook
     *
     * @param digitalBook to save
     * @return updated digitalBook
     */
    public DigitalBook saveDigitalBook(DigitalBook digitalBook) {
        return mDigitalBookModel.saveDigitalBook(digitalBook);
    }

    /**
     * save an popUps
     *
     * @param popUps to save
     * @return updated popUps
     */
    public PopUps savePopUps(PopUps popUps) {
        return mPopUpsModel.savePopUps(popUps);
    }

    /**
     * save an conceptMap
     *
     * @param conceptMap to save
     * @return updated conceptMap
     */
    public ConceptMap saveConceptMap(ConceptMap conceptMap) {
        return mConceptMapModel.saveConceptMap(conceptMap);
    }

    /**
     * save an interactiveImage
     *
     * @param interactiveImage to save
     * @return updated interactiveImage
     */
    public InteractiveImage saveInteractiveImage(InteractiveImage interactiveImage) {
        return mInteractiveImageModel.saveInteractiveImage(interactiveImage);
    }

    public VideoCourse saveVideoCourse(VideoCourse videoCourse) {
        return mVideoCourseModel.saveObject(videoCourse);
    }

    public InteractiveVideo saveInteractiveVideo(InteractiveVideo interactiveVideo) {
        return mInteractiveVideoModel.saveObject(interactiveVideo);
    }

    public AboutCourse saveAboutCourse(AboutCourse aboutCourse) {
        return mAboutCourseModel.saveObject(aboutCourse);
    }

    /**
     * fetch quiz from database using document id
     *
     * @param documentId of the quiz to fetch
     * @return quiz
     */
    public Quiz fetchQuizFromDocumentId(String documentId) {

        /*fetch quiz using quiz model*/
        return mQuizModel.fetchQuizSync(documentId);
    }

    /**
     * fetch quiz from database using object id
     *
     * @param objectId of the quiz to fetch
     * @return quiz
     */
    public Quiz fetchQuizFromObjectId(String objectId) {

        /*fetch using quiz model*/
        return mQuizModel.getQuizFromUidSync(objectId);
    }

    public QuizWeb fetchQuizWebFromObjectId(String objectId) {

        /*fetch using quiz model*/
        return mQuizWebModel.getObjectById(objectId);
    }

    /**
     * fetch quiz from database using alias
     *
     * @param alias of the quiz to fetch
     * @return quiz
     */
    public Quiz fetchQuizFromAlias(String alias) {
        /*fetch quiz using quiz model*/
        return mQuizModel.fetchQuizFromAliasSync(alias);
    }

    /**
     * fetch resource from database using object id
     *
     * @param objectId of the resource to fetch
     * @return resource
     */
    public Resource fetchResourceFromObjectId(String objectId) {

        /*fetch using resource model*/
        return mResourceModel.getResourseFromUidSync(objectId);
    }


    /**
     * fetch PostResponse from database using alias
     *
     * @param alias of the PostResponse to fetch
     * @return PostResponse
     */
    public PostResponse fetchPostResponseFromAlias(String alias) {
        /*fetch PostResponse using quiz model*/
        return mPostResponseModel.getPostResponseByAlias(alias);
    }


    /**
     * fetch postdata from database using alias
     *
     * @param alias of the postdata to fetch
     * @return quiz
     */
    public PostData fetchPostDataFromAlias(String alias) {
        /*fetch quiz using quiz model*/
        return mPostModel.getPostDataByAlias(alias);
    }

    /**
     * fetch assignment from database using document id
     *
     * @param documentId of the assignment to fetch
     * @return assignment
     */
    public Assignment fetchAssignmentFromDocumentId(String documentId) {

        /*fetch assignment using assignment model*/
        return mAssignmentModel.getAssignmentSync(documentId);
    }

    public PostData fetchPostDataFromObjectId(String objectId) {
        return mPostModel.getObjectById(objectId);
    }

    public CalendarEvent fetchCalEventFromObjectId(String objectId) {
        return mCalEventModel.getCalendarEventFromUidSync(objectId);
    }

    public PostResponse fetchPostResponseFromObjectId(String objectId) {
        return mPostResponseModel.getObjectById(objectId);
    }

    public Group fetchGroupFromObjectId(String objectId) {
        return mGroupModel.getGroupFromUidSync(objectId);
    }

    public UserProfile fetchUserProfileFromObjectId(String objectId) {
        return mAppUserModel.getUserProfileFromUidSync(objectId);
    }

    /**
     * fetch assignment from database using object id
     *
     * @param objectId of the assignment to fetch
     * @return assignment
     */
    public Assignment fetchAssignmentFromObjectId(String objectId) {

        /*fetch assignment using assignment model*/
        return mAssignmentModel.getAssignmentFromUidSync(objectId);
    }

    public AssignmentResponse fetchAssignmentResponseFromObjectId(String objectId) {
        /*fetch assignment using assignment model*/
        return mAssignmentResponseModel.getAssignmentResponseFromUidSync(objectId);
    }

    /**
     * fetch assignment response from database using document id
     *
     * @param documentId of the assignment response to fetch
     * @return assignment response
     */
    public AssignmentResponse fetchAssignmentResponseFromDocumentId(String documentId) {

        /*fetch assignment response using assignment response model*/
        return mAssignmentResponseModel.getAssignmentResponseSync(documentId);
    }

    /**
     * fetch DigitalBook from database using object id
     *
     * @param objectId of the DigitalBook to fetch
     * @return DigitalBook
     */
    public DigitalBook fetchDigitalBookFromObjectId(String objectId) {
        return mDigitalBookModel.getDigitalBookFromUidSync(objectId);
    }

    /**
     * fetch interactiveImage from database using object id
     *
     * @param objectId of the interactiveImage to fetch
     * @return
     */
    public InteractiveImage fetchInteractiveImageFromObjectId(String objectId) {
        return mInteractiveImageModel.getInteractiveImageFromUidSync(objectId);
    }

    /**
     * fetch popUps from database using object id
     *
     * @param objectId of the popUps to fetch
     * @return
     */
    public PopUps fetchPopUpsFromObjectId(String objectId) {
        return mPopUpsModel.getPopUpsFromUidSync(objectId);
    }

    /**
     * fetch conceptMap from database using object id
     *
     * @param objectId of the conceptMap to fetch
     * @return
     */
    public ConceptMap fetchConceptMapFromObjectId(String objectId) {
        return mConceptMapModel.getConceptMapFromUidSync(objectId);
    }

    public CustomSection fetchCustomSectionFromObjectId(String objectId) {
        return mCustomSectionModel.getObjectById(objectId);
    }

    public AboutCourse fetchAboutCourseFromObjectId(String objectId) {
        return mAboutCourseModel.getObjectById(objectId);
    }

    public BlogDetails fetchBlogDetailsFromObjectId(String objectId) {
        return mBlogModel.getBlogDetailsFromUidSync(objectId);
    }

    public VideoCourse fetchVideoCourseFromObjectId(String objectId) {
        return mVideoCourseModel.getObjectById(objectId);
    }

    public InteractiveVideo fetchInteractiveVideoFromObjectId(String objectId) {
        return mInteractiveVideoModel.getObjectById(objectId);
    }

    public void updateAndSaveCompleteSyncStatus(Blog blog) {
        /*update status of the blog*/
        blog.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save blog*/
        saveBlog(blog);
    }

    public void updateAndSaveCompleteSyncStatus(BlogReview blogReview) {

        /*update status of the blog*/
        blogReview.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save blog*/
        saveBlogReview(blogReview);

    }

    public void updateAndSaveCompleteSyncStatus(BlogDetails blogDetails) {

        /*update status of the blog*/
        blogDetails.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save blog*/
        saveBlogDetails(blogDetails);

    }

    /**
     * update the sync status of the quiz to complete and save it.
     * also update the sync status of all objects
     * that have a reference of this quiz to complete and save them.
     *
     * @param quiz to update and save
     */
    public void updateAndSaveCompleteSyncStatus(Quiz quiz) {

        /*update status of the quiz*/
        quiz.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save quiz*/
        saveQuiz(quiz);

//        /*get list of assignment tagging the quiz*/
//        List<Assignment> list = mAssignmentModel.getAssignmentListFromQuizUidSync(quiz.getObjectId());
//
//        /*one by one update status of all assignments*/
//        for (Assignment assignment : list) {
//
//            /*update status of assignment*/
//            updateAndSaveCompleteSyncStatus(assignment);
//        }
    }

    public void updateAndSaveCompleteSyncStatus(QuizWeb quiz) {

        /*update status of the quiz*/
        quiz.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save quiz*/
        saveQuizWeb(quiz);

    }

    public void updateAndSaveCompleteSyncStatus(Resource resource) {
        /*update status of the resource*/
        resource.setSyncStatus(SyncStatus.COMPLETE_SYNC.getStatus());

        /*save resource*/
        saveResource(resource);
    }

    public void updateAndSaveCompleteSyncStatus(UserProfile userProfile) {
        /*update status of the userProfile*/
        userProfile.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save userProfile*/
        saveUserProfile(userProfile);


    }

    /**
     * update the sync status of the quiz to complete and save it.
     * also update the sync status of all objects
     * that have a reference of this quiz to complete and save them.
     *
     * @param group to update and save
     */
    public void updateAndSaveCompleteSyncStatus(Group group) {

        /*update status of the quiz*/
        group.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save group*/
        saveGroup(group);

//        /*get list of assignment tagging the quiz*/
//        List<Assignment> list = mAssignmentModel.getAssignmentListFromQuizUidSync(quiz.getObjectId());
//
//        /*one by one update status of all assignments*/
//        for (Assignment assignment : list) {
//
//            /*update status of assignment*/
//            updateAndSaveCompleteSyncStatus(assignment);
//        }
    }

    public void updateAndSaveCompleteSyncStatus(PostData postData) {
        /*update status of the postData*/
        postData.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

//         /*Increament unread post count to one*/
//        mDataObject.setPostUnreadCount(mDataObject.getPostUnreadCount()+1);
//        mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);

        /*save postData*/
        saveLearningNetworkPostData(postData);

    }

    public void updateAndSaveCompleteSyncStatus(CalendarEvent calendarEvent) {
        /*update status of the calendarEvent*/
        calendarEvent.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
        saveCalendarEventData(calendarEvent);

    }

    public void updateAndSaveCompleteSyncStatus(PostResponse postResponse) {
        /*update status of the postResponse*/
        postResponse.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save postResponse*/
        savePostResponse(postResponse);

    }

    /**
     * update the sync status of the assignment to Complete and save it.
     * Also update the sync status of all objects that have reference
     * of this assignment to complete and save them.
     *
     * @param assignment to update and save
     */
    public void updateAndSaveCompleteSyncStatus(Assignment assignment) {

        /*update status of the assignment*/
        assignment.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save assignment*/
        saveAssignment(assignment);

        /*get list of assignment responses tagging the assignment*/
        // List<AssignmentResponse> list = mAssignmentResponseModel.getAssignmentResponseListFromAssignmentUidSync(assignment.getObjectId());

        /*one by one update status of all assignment responses*/
//        for (AssignmentResponse assignmentResponse : list) {
//
//            /*update status of assignment response*/
//            assignmentResponse.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
//
//            /*save assignment response*/
//            saveAssignmentResponse(assignmentResponse);
//        }
    }

    /**
     * update the sync status of the digitalBook to complete and save it.
     * also update the sync status of all objects
     * that have a reference of this digitalBook to complete and save them.
     *
     * @param digitalBook to update and save
     */
    public void updateAndSaveCompleteSyncStatus(DigitalBook digitalBook) {
        /*update status of the digitalBook*/
        digitalBook.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save digitalBook*/
        saveDigitalBook(digitalBook);

    }

    public void updateAndSaveCompleteSyncStatus(CustomSection customSection) {
        /*update status of the customSection*/
        customSection.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save digitalBook*/
        saveCustomSection(customSection);

    }

    /**
     * update the sync status of the conceptMap to complete and save it.
     * also update the sync status of all objects
     * that have a reference of this conceptMap to complete and save them.
     *
     * @param conceptMap to update and save
     */
    public void updateAndSaveCompleteSyncStatus(ConceptMap conceptMap) {
        /*update status of the conceptMap*/
        conceptMap.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save conceptMap*/
        saveConceptMap(conceptMap);
    }

    /**
     * update the sync status of the interactiveImage to complete and save it.
     * also update the sync status of all objects
     * that have a reference of this interactiveImage to complete and save them.
     *
     * @param interactiveImage to update and save
     */
    public void updateAndSaveCompleteSyncStatus(InteractiveImage interactiveImage) {
        /*update status of the interactiveImage*/
        interactiveImage.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save interactiveImage*/
        saveInteractiveImage(interactiveImage);
    }

    /**
     * update the sync status of the popUps to complete and save it.
     * also update the sync status of all objects
     * that have a reference of this popUps to complete and save them.
     *
     * @param popUps to update and save
     */
    public void updateAndSaveCompleteSyncStatus(PopUps popUps) {
        /*update status of the popUps*/
        popUps.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        /*save popUps*/
        savePopUps(popUps);
    }

    public void updateAndSaveCompleteSyncStatus(VideoCourse videoCourse) {
        videoCourse.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        saveVideoCourse(videoCourse);
    }

    public void updateAndSaveCompleteSyncStatus(InteractiveVideo interactiveVideo) {
        interactiveVideo.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());

        saveInteractiveVideo(interactiveVideo);
    }

    public void updateAndSaveCompleteSyncStatus(AboutCourse aboutCourse) {
        aboutCourse.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
        saveAboutCourse(aboutCourse);
    }

    public Course fetchCourseFromUid(String uid) {
        return null;
    }

    public Blog saveBlog(Blog blog) {
        return mBlogModel.saveBlog(blog);
    }

    public BlogDetails saveBlogDetails(BlogDetails blogDetails) {
        return mBlogModel.saveBlogDetails(blogDetails);
    }

    public BlogComment saveBlogComment(BlogComment blogComment) {
        return mBlogCommentModel.saveObject(blogComment);
    }

    public Curriculum saveCurriculum(Curriculum curriculum) {
        return mCurriculumModel.saveObject(curriculum);
    }

    public void addRecommendedCourse(ObjectInfo objectInfo) {
        mRecommendedCourseModel.saveObject(objectInfo);
    }

    public LearningMap saveLearningMap(LearningMap learningMap) {
        return mLearningMapModel.saveObject(learningMap);
    }

    public BlogReview saveBlogReview(BlogReview blogReview) {
        return mBlogReviewModel.saveBlogReview(blogReview);
    }

    public void updateNotificationStatus(String notificationId, String syncStatus) {
        if (!TextUtils.isEmpty(notificationId)) {
            Notification notification = mNotificationModel.getNotificationFromUidSync(notificationId);
            notification.setSyncStatus(syncStatus);
            mNotificationModel.getDatabaseQueryHelper().deleteNotifications(notification.getDocId());
        }
    }

    public PeriodNew savePeriodNew(PeriodNew periodNew) {
        return mPeriodicEventsModel.saveObject(periodNew);
    }

    public TrackingRoute saveTrackingRoute(TrackingRoute trackingRoute) {
        return mTrackingModel.saveObject(trackingRoute);
    }

    public AssignmentStudent saveAssignmentStage(AssignmentResponse assignmentResponse) {
        return mAssignmentResponseModel.setAssignmentStudentFromResponse(assignmentResponse, assignmentResponse.getStage());
    }

    public void saveSubjectList(Collection<Subject> collection, Context context) {
        int[] colors = context.getResources().getIntArray(R.array.subject_color);
        int[] textColors = context.getResources().getIntArray(R.array.subject_text_color);
        int[] foregroundColors = context.getResources().getIntArray(R.array.subject_foreground_color);
        java.util.ArrayList<PrefManager.SubjectExt> categories = new java.util.ArrayList<>();
        int i = 0;
        for (Subject subject : collection) {
            categories.add(new PrefManager.SubjectExt(subject.getName(), subject.getId(), colors[i % colors.length], textColors[i % textColors.length], foregroundColors[i % foregroundColors.length], getSubjectDrawableWhiteIdentifierFromString(context, subject.getId()), getSubjectDrawableTransparentIdentifierFromString(context, subject.getId())));
            i++;
        }

        PrefManager.setSubjectList(categories, context);
    }

    public void saveGradeList(Collection<Grade> collection, Context context) {
        PrefManager.setGradeList(collection, context);
    }

    public void updateSubjectIcons(Context context) {
        java.util.ArrayList<PrefManager.SubjectExt> collection = PrefManager.getSubjectList(context);
        int[] colors = context.getResources().getIntArray(R.array.subject_color);
        int[] textColors = context.getResources().getIntArray(R.array.subject_text_color);
        int[] foregroundColors = context.getResources().getIntArray(R.array.subject_foreground_color);
        java.util.ArrayList<PrefManager.SubjectExt> categories = new java.util.ArrayList<>();
        int i = 0;
        for (PrefManager.SubjectExt subject : collection) {
            categories.add(new PrefManager.SubjectExt(subject.getName(), subject.getId(), colors[i % colors.length], textColors[i % textColors.length], foregroundColors[i % foregroundColors.length], getSubjectDrawableWhiteIdentifierFromString(context, subject.getId()), getSubjectDrawableTransparentIdentifierFromString(context, subject.getId())));
            i++;
        }

        PrefManager.setSubjectList(categories, context);
    }

    public int getSubjectDrawableWhiteIdentifierFromString(Context context, String subjectId) {
        String name = "subjectWhite" + subjectId;
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        if (id == 0) {
            id = R.drawable.white_default_course;
        }
        return id;

    }

    public int getSubjectDrawableTransparentIdentifierFromString(Context context, String subjectId) {
        String name = "subjectTransparent" + subjectId;
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        if (id == 0) {
            id = R.drawable.transparent_default_course;
        }
        return id;

    }

    public void saveCuratorMapping(CuratorMapping curatorMapping) {
        mCuratorMappingModel.saveObject(curatorMapping);
    }

    public AssignmentMinimal saveAssignmentMinimal(Assignment assignment) {
        return mAssignmentModel.setAssignmentMinimalFromAssignment(assignment, assignment.getStage());
    }

    public AssignmentMinimal getAssignmentMinimalByAlias(String alias) {

        return mAssignmentModel.getAssignmentMinimalFromAliasSync(alias);
    }

    public void updateAssignmentMinimal(AssignmentMinimal assignmentMinimal) {
        if (assignmentMinimal.getStage().equals(AssignmentStage.STAGE_GRADED.getAssignmentStage())) {
            mAssignmentModel.saveCompletedAssignmentMinimalToDatabase(assignmentMinimal);

        } else {
            mAssignmentModel.saveIncompleteAssignmentMinimalToDatabase(assignmentMinimal);
        }
    }

    public InternalNotification getObjectByActionRangeAndId(String objectId, int actionLimitUpper, int actionLimitLower) {
        return mInternalNotificationModel.getObjectByActionRangeAndId(objectId, actionLimitUpper, actionLimitLower);

    }

    public void purgeInternalNotification(String docId) {
        mDatabaseQueryHelper.deleteNotifications(docId);
    }

    public AboutCourse saveAboutCourseToDownloaded(AboutCourse aboutCourse) {
        return mDownloadedCourseModel.saveObject(aboutCourse);
    }

    public Training saveTraining(Training training) {
        return mTrainingModel.saveObject(training);
    }

    public AnalysisActivityData saveActivityData(AnalysisActivityData activityData) {
        return mAnalysisActivityModel.saveObject(activityData);
    }

    public AnalysisActivityData saveLearningData(AnalysisActivityData activityData) {
        return mAnalysisLearningModel.saveObject(activityData);
    }

    public AnalysisActivityRecentlyRead saveRecentReadData(AnalysisActivityRecentlyRead activityRecentlyRead) {
        return mAnalysisRecentlyReadModel.saveObject(activityRecentlyRead);
    }

    public AnalysisTopicCovered saveTopicCoverData(AnalysisTopicCovered activityData) {
        return mTopicCoveredModel.saveObject(activityData);
    }

    public PerformanceResponseCount savePerformanceResponseCount(PerformanceResponseCount performanceResponseCount) {
        return mPerformanceResponseCountModel.saveObject(performanceResponseCount);
    }

    public Training saveTrainingAndSession(Training training) {
        training = saveTraining(training);
        ArrayList<SubjectSuper> subjects = training.getSubjects();
        if (subjects == null) {
            subjects = new ArrayList<SubjectSuper>();
        }
        mTrainingSessionModel.removeAllByTrainingId(training.getObjectId());
        for (int i = 0; i < training.getSessions().size(); i++) {
            TrainingSession trainingSession = training.getSessions().get(i);
            trainingSession.setTrainingId(training.getObjectId());
            trainingSession.setTrainingTitle(training.getTitle());
            trainingSession.setObjectId(training.getObjectId() + "TS" + String.valueOf(i));
            subjects.add(trainingSession.getSubject());
            saveTrainingSession(trainingSession);
        }
        return training;
    }

    public TrainingSession saveTrainingSession(TrainingSession trainingSession) {
        return mTrainingSessionModel.saveObject(trainingSession);
    }

    public Training getTraining(String objectId) {
        return mTrainingModel.getObjectById(objectId);
    }
}
