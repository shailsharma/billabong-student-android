package in.securelearning.lil.android.assignments.model;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.assignments.views.activity.AssignActivity;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.constants.AssignmentType;
import in.securelearning.lil.android.base.constants.QuizTypeEnum;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AssignedBy;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.QuizMinimal;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.ConceptMapModel;
import in.securelearning.lil.android.base.model.DeleteObjectModel;
import in.securelearning.lil.android.base.model.DigitalBookModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.InteractiveImageModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.learningnetwork.events.EventNewAssignmentCreated;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;

/**
 * Model class for Assign Resource Activity .
 */
public class AssignResourceActivityModel {
    @Inject
    Context mContext;
    @Inject
    GroupModel mGroupModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AssignmentModel mAssignmentModel;

    @Inject
    AssignmentResponseModel mAssignmentResponseModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    DigitalBookModel mDigitalBookModel;

    @Inject
    ConceptMapModel mConceptMapModel;

    @Inject
    InteractiveImageModel mInteractiveImageModel;

    @Inject
    QuizModel mQuizModel;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    public AssignResourceActivityModel() {
        super();
        InjectorAssignment.INSTANCE.getComponent().inject(this);
    }

    @Inject
    DeleteObjectModel mDeleteObjectModel;

    /**
     * provides assignment type from quiz type
     *
     * @param quizType type of quiz
     * @return type of assignment
     */
    public static String getAssignmentType(String quizType) {
        if (quizType.equals(QuizTypeEnum.OBJECTIVE.toString())) {
            return AssignmentType.TYPE_OBJECTIVE.getAssignmentType();
        } else if (quizType.equals(QuizTypeEnum.SUBJECTIVE.toString())) {
            return AssignmentType.TYPE_SUBJECTIVE.getAssignmentType();
        } else if (quizType.equalsIgnoreCase("digitalbook")) {
            return AssignmentType.TYPE_DIGITAL_BOOK.getAssignmentType();
        } else if (quizType.equalsIgnoreCase("videocourse")) {
            return AssignmentType.TYPE_VIDEO_COURSE.getAssignmentType();
        } else if (quizType.toLowerCase().contains("map")) {
            return AssignmentType.TYPE_CONCEPT_MAP.getAssignmentType();
        } else if (quizType.toLowerCase().contains("interactiveimage")) {
            return AssignmentType.TYPE_INTERACTIVE_IMAGE.getAssignmentType();
        } else if (quizType.toLowerCase().contains("pop")) {
            return AssignmentType.TYPE_Popup.getAssignmentType();
        } else if (quizType.toLowerCase().contains("video")) {
            return AssignmentType.TYPE_INTERACTIVE_VIDEO.getAssignmentType();
        } else {
            return AssignmentType.TYPE_SUBJECTIVE.getAssignmentType();
        }
    }

    /**
     * synchronous load of group list for the current user
     *
     * @return group list
     */
    public ArrayList<Group> getGroupListByUserUId() {
        return mGroupModel.getGroupListByUserUIdSync(mAppUserModel.getObjectId());
    }

    public ArrayList<GroupAbstract> getModeratorGroupsOfUser() {
        UserProfile userProfile = mAppUserModel.getApplicationUser();
        if (userProfile.getModeratedGroups() != null && !userProfile.getModeratedGroups().isEmpty()) {
            return new ArrayList<>(userProfile.getModeratedGroups());
        } else {
            return new ArrayList<GroupAbstract>();
        }
    }

    /**
     * create assignedBy object from current user
     *
     * @return AssignedBy
     */
    public AssignedBy getAssignedBy() {
        return new AssignedBy(mAppUserModel.getObjectId(), mAppUserModel.getApplicationUser().getName());
    }

    /**
     * synchronous save of assignment and create internal notification for assignment
     *
     * @param assignment
     */
    public void saveAssignmentSync(Assignment assignment) {
        assignment.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        mAssignmentModel.saveAssignment(assignment);
        mAssignmentModel.setAssignmentMinimalFromAssignment(assignment, assignment.getStage());
        createInternalNotificationForAssignment(assignment, InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD);
        mRxBus.send(new EventNewAssignmentCreated());

    }

    public void updateQuizWithStatus(Quiz quiz) {
        mQuizModel.saveQuiz(quiz);
    }

    public void deleteQuizMinimal(String alias) {
        QuizMinimal quizMinimal = mQuizModel.fetchQuizMinimalFromAliasSync(alias);
        mDeleteObjectModel.deleteJsonAssignments(quizMinimal.getDocId());
    }

    /**
     * create internal notification for assignment upload
     *
     * @param assignment
     * @param action
     */
    private void createInternalNotificationForAssignment(Assignment assignment, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, assignment.getAlias());
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(assignment.getAssignmentType());
            internalNotification.setDataObjectType(InternalNotificationActionUtils.OBJECT_TYPE_ASSIGNMENT);
            internalNotification.setObjectDocId(assignment.getDocId());
            internalNotification.setObjectId(assignment.getAlias());
            internalNotification.setObjectAction(action);
            internalNotification.setTitle(assignment.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }

    /**
     * synchronous load of quiz from uid
     *
     * @param uidQuiz uid of the quiz
     * @return quiz
     */
    public Quiz getQuizFromUidSync(String uidQuiz) {
        return mQuizModel.getQuizFromUidSync(uidQuiz);
    }

    public Quiz getQuizFromAliasSync(String aliasQuiz) {
        return mQuizModel.fetchQuizFromAliasSync(aliasQuiz);
    }

    public QuizMinimal getQuizMinimalFromAliasSync(String aliasQuiz) {
        return mQuizModel.fetchQuizMinimalFromAliasSync(aliasQuiz);
    }

    public DigitalBook getDigitalFromUidSync(String uid) {
        return mDigitalBookModel.getDigitalBookFromUidSync(uid);
    }

    public ConceptMap getConceptMapFromUidSync(String uid) {
        return mConceptMapModel.getConceptMapFromUidSync(uid);
    }

    public InteractiveImage getInteractiveImageFromUidSync(String uid) {
        return mInteractiveImageModel.getInteractiveImageFromUidSync(uid);
    }

    /**
     * initializes assignment with quiz information
     *
     * @param quiz
     * @return assignment
     */
    public Assignment initializeAssignmentFromQuiz(Quiz quiz) {
        Assignment assignment = new Assignment();
        assignment.setUidCourse("");
        assignment.setUidQuiz(quiz.getObjectId());
        assignment.setAssignmentType(getAssignmentType(quiz.getQuizType()));
//        assignment.setTitle(quiz.getTitle());
        assignment.setMetaInformation(quiz.getMetaInformation());
        assignment.setThumbnail(quiz.getThumbnail());
        assignment.setUidQuiz(quiz.getObjectId());
        assignment.setQuizAlias(quiz.getAlias());
        return assignment;
    }

    /**
     * initializes assignment with course information
     *
     * @param course
     * @return assignment
     */
    public Assignment initializeAssignmentFromCourse(AssignActivity.CourseExt course) {
        Assignment assignment = new Assignment();
        assignment.setUidQuiz("");
        assignment.setUidCourse(course.getCourseId());
//        assignment.setTitle(course.getCourseTitle());
        assignment.setMetaInformation(course.getMetaInformation());
        assignment.setThumbnail(course.getCourseThumbnail());
        return assignment;
    }

    /**
     * initializes assignment with resource information
     *
     * @param favouriteResource
     * @return assignment
     */
    public Assignment initializeAssignmentFromResource(AssignActivity.FavouriteResourceExt favouriteResource) {
        Assignment assignment = new Assignment();
        assignment.setUidQuiz("");
        assignment.setUidResource(favouriteResource.getVideoId());
//        assignment.setTitle(favouriteResource.getVideoTitle());
        assignment.setMetaInformation(favouriteResource.getMetaInformation());
        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setUrl(favouriteResource.getUrlThumbnail());
        assignment.setThumbnail(thumbnail);
        return assignment;
    }
}
