package in.securelearning.lil.android.syncadapter.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.EventType;
import in.securelearning.lil.android.base.constants.NotificationType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.BaseObject;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.BookAnnotation;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.CourseAnalytics;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.dataobjects.ObjectInfo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.UserBrowseHistory;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.dataobjects.WebQuizResponse;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.BlogCommentModel;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.CourseAnalyticsModel;
import in.securelearning.lil.android.base.model.DeleteObjectModel;
import in.securelearning.lil.android.base.model.DigitalBookModel;
import in.securelearning.lil.android.base.model.DownloadedCourseModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.NotificationModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.model.QuestionResponseModel;
import in.securelearning.lil.android.base.model.RecommendedCourseModel;
import in.securelearning.lil.android.base.model.TrainingModel;
import in.securelearning.lil.android.base.model.TrainingSessionModel;
import in.securelearning.lil.android.base.model.UserBrowseHistoryModel;
import in.securelearning.lil.android.base.model.WebAnnotationModel;
import in.securelearning.lil.android.base.utils.DocumentUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_DIGITAL_BOOK_UPDATE;

/**
 * Implementation of SyncServiceModelInterface.
 */
public class SyncServiceModel extends BaseModel {
    public final String TAG = this.getClass().getCanonicalName();

    @Inject
    Context mContext;
    @Inject
    BlogCommentModel mBlogCommentModel;
    @Inject
    PostDataModel mPostDataModel;
    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;
    @Inject
    RecommendedCourseModel mRecommendedCourseModel;
    @Inject
    NotificationModel mNotificationModel;
    @Inject
    AssignmentModel mAssignmentModel;
    @Inject
    QuestionResponseModel mQuestionResponseModel;
    @Inject
    AssignmentResponseModel mAssignmentResponseModel;
    @Inject
    CalEventModel mCalEventModel;
    @Inject
    InternalNotificationModel mInternalNotificationModel;
    @Inject
    DeleteObjectModel mDeleteObjectModel;
    @Inject
    DownloadedCourseModel mDownloadedCourseModel;
    @Inject
    CourseAnalyticsModel mCourseAnalyticsModel;
    @Inject
    WebAnnotationModel mWebAnnotationModel;
    @Inject
    GroupModel mGroupModel;
    @Inject
    TrainingModel mTrainingModel;
    @Inject
    TrainingSessionModel mTrainingSessionModel;
    @Inject
    DigitalBookModel mDigitalBookModel;
    @Inject
    UserBrowseHistoryModel mUserBrowseHistoryModel;


    public SyncServiceModel() {
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public Group fetchGroupById(String id) {

        return mGroupModel.getGroupFromUidSync(id);

    }

    /**
     * fetch list of assignments with Not Sync status
     *
     * @return list of assignments
     */
    public Observable<Assignment> fetchAssignmentListSync() {
        Observable<Assignment> observable = Observable.create(new ObservableOnSubscribe<Assignment>() {
            @Override
            public void subscribe(ObservableEmitter<Assignment> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getAssignmentListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Assignment assignment = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Assignment.class);

                        /*set document id*/
                        assignment.setDocId(queryRow.getDocumentId());

                        e.onNext(assignment);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Training> fetchTrainingListSync() {
        Observable<Training> observable = Observable.create(new ObservableOnSubscribe<Training>() {
            @Override
            public void subscribe(ObservableEmitter<Training> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getTrainingListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Training item = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Training.class);

                        /*set document id*/
                        item.setDocId(queryRow.getDocumentId());

                        e.onNext(item);

                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    /**
     * fetch list of pending internal notification
     *
     * @return list of internal notification
     */
    public Observable<InternalNotification> fetchInternalNotificationList() {
        Observable<InternalNotification> observable = Observable.create(new ObservableOnSubscribe<InternalNotification>() {
            @Override
            public void subscribe(ObservableEmitter<InternalNotification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getInternalNotificationListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow : queryRows) {

                        /*get internal notification*/
                        InternalNotification internalNotification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), InternalNotification.class);

                        /*set document id*/
                        internalNotification.setDocId(queryRow.getDocumentId());

                        e.onNext(internalNotification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public void addRecommendedCourse(ObjectInfo objectInfo) {
        mRecommendedCourseModel.saveObject(objectInfo);
    }

    public Observable<Notification> fetchNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchCalendarNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_CALENDAR_EVENT.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_CALENDAR_EVENT.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchPeriodEventListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_PERIODIC_EVENT.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_PERIODIC_EVENT.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchPostNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_POST_DATA.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_POST_DATA.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchPostResponseNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_POST_RESPONSE.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_POST_RESPONSE.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchNotificationListByTypeSync(final String notificationType) {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {notificationType};
                Object[] endKey = {notificationType, Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchAssignmentNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_ASSIGNMENT_RESPONSE.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_ASSIGNMENT_RESPONSE.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchTrainingNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_TRAINING.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_TRAINING.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchTrainingArchivalNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_TRAINING_ARCHIVAL.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_TRAINING_ARCHIVAL.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchBlogNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_BLOG.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_BLOG.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchDigitalBookNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_DIGITAL_BOOK.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_DIGITAL_BOOK.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<Notification> fetchVideoCourseNotificationListSync() {
        Observable<Notification> observable = Observable.create(new ObservableOnSubscribe<Notification>() {
            @Override
            public void subscribe(ObservableEmitter<Notification> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getNotificationListByObjectTypeQuery();
                Object[] startKey = {NotificationType.TYPE_VIDEO_COURSE.getNotificationType()};
                Object[] endKey = {NotificationType.TYPE_VIDEO_COURSE.getNotificationType(), Collections.EMPTY_MAP};
                query.setDescending(false);
                query.setStartKey(startKey);
                query.setEndKey(endKey);
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        Notification notification = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Notification.class);

                        /*set document id*/
                        notification.setDocId(queryRow.getDocumentId());

                        if (!notification.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString()))
                            e.onNext(notification);


                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<DigitalBook> fetchDigitalBookListSync() {
        Observable<DigitalBook> observable = Observable.create(new ObservableOnSubscribe<DigitalBook>() {
            @Override
            public void subscribe(ObservableEmitter<DigitalBook> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getDigitalBookListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        DigitalBook object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), DigitalBook.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());

                        e.onNext(object);

                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<InteractiveImage> fetchInteractiveImageListSync() {
        Observable<InteractiveImage> observable = Observable.create(new ObservableOnSubscribe<InteractiveImage>() {
            @Override
            public void subscribe(ObservableEmitter<InteractiveImage> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getInteractiveImageListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        InteractiveImage object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), InteractiveImage.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());

                        e.onNext(object);

                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<InteractiveVideo> fetchInteractiveVideoListSync() {
        Observable<InteractiveVideo> observable = Observable.create(new ObservableOnSubscribe<InteractiveVideo>() {
            @Override
            public void subscribe(ObservableEmitter<InteractiveVideo> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getInteractiveVideoListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        InteractiveVideo object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), InteractiveVideo.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());

                        e.onNext(object);

                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<VideoCourse> fetchVideoCourseListSync() {
        Observable<VideoCourse> observable = Observable.create(new ObservableOnSubscribe<VideoCourse>() {
            @Override
            public void subscribe(ObservableEmitter<VideoCourse> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getVideoCourseListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        VideoCourse object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), VideoCourse.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());

                        e.onNext(object);

                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<PopUps> fetchPopUpsListSync() {
        Observable<PopUps> observable = Observable.create(new ObservableOnSubscribe<PopUps>() {
            @Override
            public void subscribe(ObservableEmitter<PopUps> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getPopUpsListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        PopUps object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), PopUps.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());

                        e.onNext(object);

                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    public Observable<ConceptMap> fetchConceptMapListSync() {
        Observable<ConceptMap> observable = Observable.create(new ObservableOnSubscribe<ConceptMap>() {
            @Override
            public void subscribe(ObservableEmitter<ConceptMap> e) throws Exception {
                /*get query*/
                Query query = mDatabaseQueryHelper.getConceptMapListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();

                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment*/
                        ConceptMap object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), ConceptMap.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());

                        e.onNext(object);

                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return observable;
    }

    /**
     * fetch list of assignment responses with Not Sync status
     *
     * @return list of assignment responses
     */
    public Observable<AssignmentResponse> fetchAssignmentResponseListSync() {
        Observable<AssignmentResponse> observable = Observable.create(new ObservableOnSubscribe<AssignmentResponse>() {
            @Override
            public void subscribe(ObservableEmitter<AssignmentResponse> e) throws Exception {


                /*get query*/
                Query query = mDatabaseQueryHelper.getAssignmentResponseListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow :
                            queryRows) {

                        /*get assignment response*/
                        AssignmentResponse assignmentResponse = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), AssignmentResponse.class);

                        /*set document id*/
                        assignmentResponse.setDocId(queryRow.getDocumentId());

                        /*add to list if status is NotSync*/
                        e.onNext(assignmentResponse);
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    /**
     * fetch list of learning network post data with Not Sync status
     *
     * @return list of learning network post data
     */
    public Observable<PostData> fetchPostDataListNotSync() {
        Observable<PostData> observable = Observable.create(new ObservableOnSubscribe<PostData>() {
            @Override
            public void subscribe(ObservableEmitter<PostData> e) throws Exception {



                /*get query*/
                Query query = mDatabaseQueryHelper.getPostDataListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow :
                            queryRows) {

                        /*get post data*/
                        PostData postData = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), PostData.class);

                        /*set document id*/
                        postData.setDocId(queryRow.getDocumentId());

                        /*add to list if status is NotSync*/
                        e.onNext(postData);
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    public Observable<CalendarEvent> fetchCalendarEventsListNotSync() {
        Observable<CalendarEvent> observable = Observable.create(new ObservableOnSubscribe<CalendarEvent>() {
            @Override
            public void subscribe(ObservableEmitter<CalendarEvent> e) throws Exception {

                /*get query*/
                Query query = mDatabaseQueryHelper.getCalendarEventsListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow :
                            queryRows) {

                        /*get CalendarEvent*/
                        CalendarEvent calendarEvent = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), CalendarEvent.class);

                        /*set document id*/
                        calendarEvent.setDocId(queryRow.getDocumentId());
                        if (!calendarEvent.getEventType().equals(EventType.TYPE_PERSONAL.getEventType())) {
                            e.onNext(calendarEvent);
                        }

                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }


    public Observable<BlogComment> fetchBlogCommentListNotSync() {
        Observable<BlogComment> observable = Observable.create(new ObservableOnSubscribe<BlogComment>() {
            @Override
            public void subscribe(ObservableEmitter<BlogComment> e) throws Exception {

                /*get query*/
                Query query = mDatabaseQueryHelper.getBlogCommentListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow :
                            queryRows) {

                        /*get PostResponse*/
                        BlogComment blogComment = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), BlogComment.class);

                        /*set document id*/
                        blogComment.setDocId(queryRow.getDocumentId());

                        e.onNext(blogComment);
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    public BlogComment fetchBlogFromAlias(String alias) {
        return mBlogCommentModel.getObjectByAlias(alias);
    }

    public PostData fetchPostDataByAlias(String alias) {
        return mPostDataModel.fetchPostDataFromAlias(alias);
    }

    public PostResponse fetchPostResponseByAlias(String alias) {
        return mPostDataModel.fetchPostResponseFromAlias(alias);
    }

    public CalendarEvent fetchCalendarEventByAlias(String alias) {
        return mCalEventModel.fetchCalendarEventFromAlias(alias);
    }

    /*public CalendarEvent fetchCalendarEvent(String type, String startDate, String endDate) {
        return mCalEventModel.getEvent(type, startDate, endDate);
    }*/

    /**
     * fetch list of assignedBadges with Not Sync status
     *
     * @return list of assignedBadges
     */
    public Observable<AssignedBadges> fetchAssignedBadgesListNotSync() {
        Observable<AssignedBadges> observable = Observable.create(new ObservableOnSubscribe<AssignedBadges>() {
            @Override
            public void subscribe(ObservableEmitter<AssignedBadges> e) throws Exception {

                /*get query*/
                Query query = mDatabaseQueryHelper.getAssignedBadgesListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow : queryRows) {

                        /*get AssignedBadges*/
                        AssignedBadges assignedBadges = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), AssignedBadges.class);

                        /*set document id*/
                        assignedBadges.setDocId(queryRow.getDocumentId());

                        e.onNext(assignedBadges);
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    public Observable<WebQuizResponse> fetchWebQuizResponseToSync() {
        Observable<WebQuizResponse> observable = Observable.create(new ObservableOnSubscribe<WebQuizResponse>() {
            @Override
            public void subscribe(ObservableEmitter<WebQuizResponse> e) throws Exception {

                /*get query*/
                Query query = mDatabaseQueryHelper.getWebQuizResponseListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow : queryRows) {

                        /*get WebQuizResponse*/
                        WebQuizResponse object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), WebQuizResponse.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());
                        if (TextUtils.isEmpty(object.getObjectId())) {
                            e.onNext(object);
                        }
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    public Observable<CourseAnalytics> fetchCourseUserLogToSync() {
        Observable<CourseAnalytics> observable = Observable.create(new ObservableOnSubscribe<CourseAnalytics>() {
            @Override
            public void subscribe(ObservableEmitter<CourseAnalytics> e) throws Exception {

                /*get query*/
                Query query = mDatabaseQueryHelper.getCourseAnalyticsListQuery();
                try {
                    /*run query*/
                    query.setMapOnly(true);
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow : queryRows) {

                        /*get WebQuizResponse*/
                        CourseAnalytics object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), CourseAnalytics.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());
                        if (TextUtils.isEmpty(object.getObjectId())) {
                            e.onNext(object);
                        }
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    //by rupsi
    public Observable<UserBrowseHistory> fetchUserBrowseHistoryLogToSync() {
        Observable<UserBrowseHistory> observable = Observable.create(new ObservableOnSubscribe<UserBrowseHistory>() {
            @Override
            public void subscribe(ObservableEmitter<UserBrowseHistory> e) throws Exception {

                /*get query*/
                Query query = mDatabaseQueryHelper.getUserBrowseHistoryListQuery();
                try {
                    /*run query*/
                    query.setMapOnly(true);
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow : queryRows) {

                        /*get WebQuizResponse*/
                        UserBrowseHistory object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), UserBrowseHistory.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());
                        if (TextUtils.isEmpty(object.getObjectId())) {
                            e.onNext(object);
                        }
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    public Observable<BookAnnotation> fetchAnnotationToSync() {
        Observable<BookAnnotation> observable = Observable.create(new ObservableOnSubscribe<BookAnnotation>() {
            @Override
            public void subscribe(ObservableEmitter<BookAnnotation> e) throws Exception {

                /*get query*/
                Query query = mDatabaseQueryHelper.getBookAnnotationListQuery();
                try {
                    /*run query*/
                    query.setMapOnly(true);
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow : queryRows) {

                        /*get WebQuizResponse*/
                        BookAnnotation object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), BookAnnotation.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());
                        if (TextUtils.isEmpty(object.getObjectId())) {
                            e.onNext(object);
                        }
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    public CourseAnalytics saveCourseAnalyticsSynced(CourseAnalytics courseAnalytics) {
        return mCourseAnalyticsModel.saveObject(courseAnalytics);
    }

    public void deleteCourseAnalytics(String docId) {
        mCourseAnalyticsModel.removeObject(docId);
    }

    public void deleteUserBrowseHistory(String docId) {
        mUserBrowseHistoryModel.removeObject(docId);
    }

    public Observable<Quiz> fetchQuizToSync() {
        Observable<Quiz> observable = Observable.create(new ObservableOnSubscribe<Quiz>() {
            @Override
            public void subscribe(ObservableEmitter<Quiz> e) throws Exception {

                /*get query*/
                Query query = mDatabaseQueryHelper.getQuizListQuery();
                try {
                    /*run query*/
                    QueryEnumerator queryRows = query.run();
                    DocumentUtils documentUtils = new DocumentUtils();
                    for (QueryRow queryRow : queryRows) {

                        /*get Quiz*/
                        Quiz object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(queryRow.getDocument()), Quiz.class);

                        /*set document id*/
                        object.setDocId(queryRow.getDocumentId());
                        if (TextUtils.isEmpty(object.getObjectId())) {
                            e.onNext(object);
                        }
                    }
                    e.onComplete();
                    documentUtils = null;
                    queryRows = null;
                    query = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return observable;
    }

    public double getNotificationLatestTimestamp() {
        return mNotificationModel.getNotificationLatestTimestampSync();

    }

    public boolean isDownloadAllowed() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (PreferenceSettingUtilClass.isDownloadOnWiFi(mContext)) {
            return wifiInfo.isConnected();
        } else if (info != null && info.isAvailable() && info.isConnected()) {
            return true;
        }
        return false;
    }

    public Assignment fetchAssignmentFromAlias(String alias) {

        return mAssignmentModel.fetchAssignmentFromAlias(alias);
    }

    public QuestionResponse fetchQuestionResponseFromAlias(String alias) {

        return mQuestionResponseModel.getQuestionResponseAlias(alias);
    }

    public AssignmentResponse fetchAssignmentResponseFromUid(String id) {

        return mAssignmentResponseModel.getAssignmentResponseFromUidSync(id);
    }

    public InternalNotification fetchInternalNotificationFromDocId(String id) {
        return mInternalNotificationModel.getObjectByDocId(id);
    }

    public <T extends BaseObject> T retrieveAssignments(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveAssignments(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveBlogs(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveBlogs(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveCourses(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveCourses(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveLearningNetwork(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveLearningNetwork(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrievePeriodicEvents(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrievePeriodicEvents(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveNotifications(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveNotification(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveCourseProgress(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveCourses(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public void uploadCourseFavorite(boolean b, String objectId) {
    }

    public void purgeInternalNotification(String docId) {
        mDatabaseQueryHelper.deleteNotifications(docId);
    }

    public String getCourseType(String type) {
        if (type.equalsIgnoreCase("digitalbook")) {
            return CourseDetailActivity.DATA_DB;
        } else if (type.equalsIgnoreCase("videocourse")) {
            return CourseDetailActivity.DATA_VC;
        } else if (type.toLowerCase().contains("map")) {
            return CourseDetailActivity.DATA_CM;
        } else if (type.toLowerCase().contains("interactiveimage")) {
            return CourseDetailActivity.DATA_II;
        } else if (type.toLowerCase().contains("pop")) {
            return CourseDetailActivity.DATA_PU;
        } else if (type.toLowerCase().contains("video")) {
            return CourseDetailActivity.DATA_IV;
        }
        return "";

    }

    public boolean deleteCourse(Context context, String objectId, String docId) {

        return mDeleteObjectModel.deleteCourse(context, objectId, docId);
    }

    public ArrayList<AssignmentStudent> getNotSubmittedAssignmentList(final String fromDate, final String toDate, final String subject, final int skip, final int limit) {
        return mAssignmentResponseModel.getOverDueAssignmentList(fromDate, toDate, subject, skip, limit);

    }

    public boolean deleteFromDownloadedCourse(String objectId) {
        AboutCourse aboutCourse = mDownloadedCourseModel.getObjectById(objectId);
        if (!TextUtils.isEmpty(aboutCourse.getDocId())) {
            return mDownloadedCourseModel.delete(aboutCourse.getDocId());

        } else {
            return true;
        }
    }

    public BookAnnotation saveAnnotation(BookAnnotation object) {
        return mWebAnnotationModel.saveObject(object);
    }

    public void deleteAnnotation(String docId) {
        mWebAnnotationModel.delete(docId);
    }

    public void deleteTraining(String docId) {
        mDeleteObjectModel.deleteJsonBlogs(docId);
    }

    public void deleteSessions(String objectId) {
        mTrainingSessionModel.removeAllByTrainingId(objectId);
    }

    public void createInternalNotificationForBookUpdate(String objectId, int action, boolean syncImmediate) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, objectId);
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType("Update Digital Book");
            internalNotification.setObjectDocId("");
            internalNotification.setObjectId(objectId);
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_DIGITAL_BOOK_UPDATE);
            internalNotification.setTitle("Update Digital Book");

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        if (syncImmediate) {

            SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
        }

    }

    public DigitalBook fetchDigitalBookByObjectId(String id) {
        return mDigitalBookModel.getObjectById(id);
    }

    public void updateNotificationStatus(String notificationId, String syncStatus) {
        Notification notification = mNotificationModel.getNotificationFromUidSync(notificationId);
        notification.setSyncStatus(syncStatus);
        mNotificationModel.getDatabaseQueryHelper().deleteNotifications(notification.getDocId());
    }

}



