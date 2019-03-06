package in.securelearning.lil.android.syncadapter.service;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.functions.Consumer;

/**
 * Period Service.
 */
public class PeriodService extends BaseService {
    public static final String TAG = PeriodService.class.getCanonicalName();

    private boolean mFirstTime = true;
    private static final String ACTION_DOWNLOAD = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD";
    private static final String ACTION_SYNC = "in.securelearning.lil.android.syncadapter.service.action.SYNC";
    private static final String ACTION_DOWNLOAD_BROADCAST_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BROADCAST_NOTIFICATION";
    private static final String ACTION_DOWNLOAD_PERIODIC_EVENTS = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_PERIODIC_EVENTS";
    // TODO: Rename parameters
    private static final String EXTRA_OBJECT_TYPE = "in.securelearning.lil.android.syncadapter.service.extra.OBJECT_TYPE";
    private static final String EXTRA_OBJECT_ID = "in.securelearning.lil.android.syncadapter.service.extra.COURSE_ID";
    private static final String ACTION_FETCH_INTERNAL_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.FETCH_INTERNAL_NOTIFICATION";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PeriodService() {
        super("PeriodService");
    }

    /**
     * start sync service
     *
     * @param context
     */
    public static void startSyncService(Context context) {
        Intent intent = new Intent(context, PeriodService.class);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }

    public static void startActionDownloadBroadcastNotification(Context context) {
        Intent intent = new Intent(context, PeriodService.class);
        intent.setAction(ACTION_DOWNLOAD_BROADCAST_NOTIFICATION);
        context.startService(intent);

    }

    public static void startActionDownload(Context context, String id, Class type) {
        Intent intent = new Intent(context, PeriodService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        intent.putExtra(EXTRA_OBJECT_TYPE, type);
        context.startService(intent);
    }

    public static void startActionFetchInternalNotification(Context context, String docId) {
        Intent intent = new Intent(context, PeriodService.class);
        intent.setAction(ACTION_FETCH_INTERNAL_NOTIFICATION);
        intent.putExtra(EXTRA_OBJECT_ID, docId);
        context.startService(intent);
    }

    public static void startActionDownloadPeriodicEvents(Context context, String id) {
        Intent intent = new Intent(context, PeriodService.class);
        intent.setAction(ACTION_DOWNLOAD_PERIODIC_EVENTS);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
        Log.e(TAG, "Sync Process started");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//
//    @Override
//    public int onStartCommand(final Intent intent, int flags, int startId) {
//        int value = super.onStartCommand(intent, flags, startId);
////        Observable.just(null)
////                .observeOn(Schedulers.newThread())
////                .subscribe(new Consumer<Object>() {
////                    @Override
////                    public void accept(Object o) {
////                        onHandleIntent(intent);
////                    }
////                });
//
//        return value;
//    }

    //    @Override
    protected void onHandleIntent(Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG, "Checking Network");
        try {

            if (intent != null) {
//                if (mSyncServiceModel.isDownloadAllowed()) {
                final String action = intent.getAction();
                if (ACTION_SYNC.equals(action)) {
                    handleActionSync();
                } else if (ACTION_DOWNLOAD.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                    final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownload(id, type);
                } else if (ACTION_DOWNLOAD_BROADCAST_NOTIFICATION.equals(action)) {
                    handleActionDownloadBroadcastNotification();
                } else if (ACTION_DOWNLOAD_PERIODIC_EVENTS.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadPeriodicEvents(id);
                } else if (ACTION_FETCH_INTERNAL_NOTIFICATION.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                    handleActionFetchInternalNotification(id);
                }

//                    if (mFirstTime) {
                /*start sync process*/
//                        mFirstTime = false;
//                    startSync();
//                        mFirstTime = true;

//                    }
//                } else {
//                    Injector.INSTANCE.getComponent().rxBus().send(new ObjectDownloadComplete(intent.getStringExtra(EXTRA_OBJECT_ID), SyncStatus.NOT_SYNC));
//                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
//                        @Override
//                        public void run() {
//                            ToastUtils.showToastAlert(getBaseContext(), "Please connect to Internet");
//                        }
//                    });
//
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleActionFetchInternalNotification(String id) {
        InternalNotification internalNotification = mSyncServiceModel.retrieveNotifications(id, InternalNotification.class);
        internalNotification.setDocId(id);
        try {
            handleInternalNotification(internalNotification);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleInternalNotification(InternalNotification internalNotification) throws IOException {

//        if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.IS_FAVORITE_TRUE)) {
//            Call<ResponseBody> responseBodyCall = mNetworkModel.addFavorite(true, internalNotification.getObjectId(), internalNotification.getObjectType().toLowerCase());
//            Response<ResponseBody> response = responseBodyCall.execute();
//            if (response != null && response.isSuccessful()) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//
//            } else if (response.code() == 401 || response.code() == 403) {
//                if (SyncServiceHelper.refreshToken(this)) {
//                    Call<ResponseBody> responseBodyCall2 = responseBodyCall.clone();
//                    response = responseBodyCall2.execute();
//                    if (response != null && response.isSuccessful()) {
//                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//
//                    }
//
//                }
//            }
//
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.IS_FAVORITE_FALSE)) {
//            Call<ResponseBody> responseBodyCall = mNetworkModel.addFavorite(false, internalNotification.getObjectId(), internalNotification.getObjectType().toLowerCase());
//            Response<ResponseBody> response = responseBodyCall.execute();
//            if (response != null && response.isSuccessful()) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//
//            } else if (response.code() == 401 || response.code() == 403) {
//                if (SyncServiceHelper.refreshToken(this)) {
//                    Call<ResponseBody> responseBodyCall2 = responseBodyCall.clone();
//                    response = responseBodyCall2.execute();
//                    if (response != null && response.isSuccessful()) {
//                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//
//                    }
//
//                }
//            }
//
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.IS_REVIEW_ADDED)) {
//            AboutCourse aboutCourse = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), AboutCourse.class);
//            UserRating userRating = new UserRating();
//            userRating.setUserId(mAppUserModel.getApplicationUser().getId());
//            int index = aboutCourse.getReviews().getUserRatings().indexOf(userRating);
//            if (index > -1) {
//                CourseReview courseReview = new CourseReview(aboutCourse.getReviews().getUserRatings().get(index), mSyncServiceModel.getCourseType(aboutCourse.getCourseType()));
//                Call<UserRating> responseBodyCall = mNetworkModel.addRating(internalNotification.getObjectId(), courseReview);
//                Response<UserRating> response = responseBodyCall.execute();
//                if (response != null && response.isSuccessful()) {
//                    mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//
//                } else if (response.code() == 401 || response.code() == 403) {
//                    if (SyncServiceHelper.refreshToken(this)) {
//                        Call<UserRating> responseBodyCall2 = responseBodyCall.clone();
//                        response = responseBodyCall2.execute();
//                        if (response != null && response.isSuccessful()) {
//                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//
//                        }
//
//                    }
//                }
//            } else {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//
//
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.QUIZ_UPLOAD_NOTIFICATION)) {
//            Quiz quiz = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), Quiz.class);
//            JobCreator.createUploadQuizJob(quiz).execute();
//            Quiz notificationPurgeQuiz = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), Quiz.class);
//            if (notificationPurgeQuiz.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ASSIGNMENT_UPLOAD_NOTIFICATION)) {
//            Assignment assignment = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), Assignment.class);
//            JobCreator.createPostAssignmentJob(assignment).execute();
//            Assignment notificationPurgeAssignment = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), Assignment.class);
//            if (notificationPurgeAssignment.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ASSIGNMENT_RESPONSE_UPLOAD_NOTIFICATION)) {
//            AssignmentResponse assignmentResponse = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), AssignmentResponse.class);
//            JobCreator.createPostAssignmentResponseJob(assignmentResponse).execute();
//            AssignmentResponse notificationPurgeAssignmentResponse = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), AssignmentResponse.class);
//            if (notificationPurgeAssignmentResponse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.POST_UPLOAD_NOTIFICATION)) {
//            PostData postData = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostData.class);
//            JobCreator.createPostLearningNetworkPostDataJob(postData).execute();
//            PostData notificationPurgePostData = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostData.class);
//            if (notificationPurgePostData.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.POST_RESPONSE_UPLOAD_NOTIFICATION)) {
//            PostResponse postResponse = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostResponse.class);
//            JobCreator.createPostResponseJob(postResponse).execute();
//            PostResponse notificationPurgePostResponse = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostResponse.class);
//            if (notificationPurgePostResponse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.DIGITAL_BOOK_DOWNLOAD_NOTIFICATION)) {
//            JobCreator.createDownloadDigitalBookJob(internalNotification.getObjectId(), "").execute();
//            DigitalBook notificationPurgeDigitalBook = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), DigitalBook.class);
//            if (notificationPurgeDigitalBook.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.VIDEO_COURSE_DOWNLOAD_NOTIFICATION)) {
//            JobCreator.createDownloadVideoCourseJob(internalNotification.getObjectId()).execute();
//            VideoCourse notificationPurgeVideoCourse = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), VideoCourse.class);
//            if (notificationPurgeVideoCourse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.CONCEPT_MAP_DOWNLOAD_NOTIFICATION)) {
//            JobCreator.createDownloadConceptMapJob(internalNotification.getObjectId()).execute();
//            ConceptMap notificationPurgeConceptMap = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), ConceptMap.class);
//            if (notificationPurgeConceptMap.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.POP_UP_DOWNLOAD_NOTIFICATION)) {
//            JobCreator.createDownloadPopUpsJob(internalNotification.getObjectId()).execute();
//            PopUps notificationPurgePopUps = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), PopUps.class);
//            if (notificationPurgePopUps.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.INTERACTIVE_IMAGE_DOWNLOAD_NOTIFICATION)) {
//            JobCreator.createDownloadInteractiveImageJob(internalNotification.getObjectId()).execute();
//            InteractiveImage notificationPurgeInteractiveImage = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), InteractiveImage.class);
//            if (notificationPurgeInteractiveImage.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.INTERACTIVE_VIDEO_DOWNLOAD_NOTIFICATION)) {
//            JobCreator.createDownloadInteractiveVideoJob(internalNotification.getObjectId()).execute();
//            InteractiveVideo notificationPurgeInteractiveVideo = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), InteractiveVideo.class);
//            if (notificationPurgeInteractiveVideo.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.calendarEventUploadNotification)) {
//            CalendarEvent calendarEvent = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), CalendarEvent.class);
//            JobCreator.createPostCalendarEventDataJob(calendarEvent).execute();
//            CalendarEvent notificationPurgeCalendarEvent = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), CalendarEvent.class);
//            if (notificationPurgeCalendarEvent.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//            }
//        } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.COURSE_DELETE_NOTIFICATION)) {
//            if (mSyncServiceModel.deleteCourse(this, internalNotification.getObjectId(), internalNotification.getObjectDocId())) {
//                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
//                InjectorSyncAdapter.INSTANCE.getComponent().rxBus().send(new CourseDeleteEvent(internalNotification.getObjectId()));
//            }
//
//        }
    }

    private void handleActionSync() {
        if (GeneralUtils.isNetworkAvailable(this))
            startSync();
    }

    private void handleActionDownload(String id, Class aClass) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionDownloadPeriodicEvents(String id) {
        JobCreator.createDownloadAboutCourseDigitalBookJob(id, "", false, false).execute();
        removeJobFromMonitoringList(DigitalBook.class, id);
    }


    public boolean isDataServerAccessible() {
        return true;//mNetworkModel.connectToServer();
    }

    /**
     * start upload and download process on their
     * individual background thread
     */
    public void startSync() {
        try {

            startUploadProcess();
/**
 * start action for pending internal notification
 */
//            mSyncServiceModel.fetchInternalNotificationList().subscribe(new Consumer<InternalNotification>() {
//                @Override
//                public void accept(InternalNotification internalNotification) throws Exception {
//                    try {
//                        handleInternalNotification(internalNotification);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });

            startDownloadProcess();

            handleLastAction();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleLastAction() {
        if (GeneralUtils.isNetworkAvailable(this))
            if (PrefManager.shouldSyncPeriodicEvents(this)) {
                startSyncService(this);
            }
    }


    /**
     * start upload process
     */
    public void startUploadProcess() {

    }

    /**
     * start download process
     */
    public void startDownloadProcess() {
        String lastSyncTime = PrefManager.getPeriodicEventLastSyncTime(this);
        if (GeneralUtils.isNetworkAvailable(this))
            JobCreator.createPeriodNewBulkDownloadJob("", lastSyncTime, null, true).execute();
//            Date lastSyncDate = DateUtils.convertrIsoDate(lastSyncTime);
//            if (DateUtils.isFutureDay(lastSyncDate.getTime(), TimeUnit.MILLISECONDS)) {
//                String nextDayTime = DateUtils.getISO8601DateStringFromSeconds(DateUtils.getFutureDay(lastSyncDate, 2));
//                JobCreator.createPeriodNewBulkDownloadJob(lastSyncTime, nextDayTime).execute();
//            } else {
//                JobCreator.createPeriodNewBulkDownloadJob(lastSyncTime, DateUtils.getISO8601DateStringFromSeconds(DateUtils.getFutureDay(new Date(), 5))).execute();
//            }
        handleActionDownloadBroadcastNotification();

    }

    public void handleActionDownloadBroadcastNotification() {
        mSyncServiceModel.fetchPeriodEventListSync().subscribe(new Consumer<Notification>() {
            @Override
            public void accept(Notification notification) throws Exception {
                if (DateUtils.convertrIsoDate(notification.getCreationTime()).getTime() > (PrefManager.getLastLoginTime(getBaseContext())) &&
                        notification.getObjectInfo() != null && !TextUtils.isEmpty(notification.getObjectInfo().getFrom()) && !TextUtils.isEmpty(notification.getObjectInfo().getToDate())) {
                    if (GeneralUtils.isNetworkAvailable(PeriodService.this))
                        JobCreator.createPeriodNewBulkDownloadJob(notification.getObjectId(), notification.getObjectInfo().getFrom(), notification.getObjectInfo().getToDate(), false).execute();

                } else {
                    mSyncServiceModel.updateNotificationStatus(notification.getObjectId(), SyncStatus.COMPLETE_SYNC.toString());
                }
            }
        });
    }


    //    private static String TYPE_VALIDATE = "validate";
//    private static String TYPE_DOWNLOAD = "download";
//    private static String TYPE_UPLOAD = "upload";
    private static HashSet<String> mJobList = new HashSet<>();

    //    public static boolean checkDownloadJobStatus(String objectId) {
//        if (mJobList != null && !mJobList.isEmpty() && !TextUtils.isEmpty(objectId)) {
//            return mJobList.contains(objectId + TYPE_DOWNLOAD);
//        }
//        return false;
//
//    }
//
//    public static boolean checkUploadJobStatus(String objectId) {
//        if (mJobList != null && !mJobList.isEmpty() && !TextUtils.isEmpty(objectId)) {
//            return mJobList.contains(objectId + TYPE_UPLOAD);
//        }
//        return false;
//
//    }
//
//    public static boolean checkValidateJobStatus(String objectId) {
//        if (mJobList != null && !mJobList.isEmpty() && !TextUtils.isEmpty(objectId)) {
//            return mJobList.contains(objectId + TYPE_VALIDATE);
//        }
//        return false;
//
//    }
    public static boolean checkJobStatus(Class aClass, String objectId) {
        if (mJobList != null && !mJobList.isEmpty() && !TextUtils.isEmpty(objectId) && aClass != null) {
            return mJobList.contains(aClass.getSimpleName() + objectId);
        }
        return false;

    }

    public static void addJobToMonitoringList(Class aClass, String objectId) {
        if (mJobList != null && !TextUtils.isEmpty(objectId) && aClass != null)
            mJobList.add(aClass.getSimpleName() + objectId);

    }

    public static void removeJobFromMonitoringList(Class aClass, String objectId) {
        if (mJobList != null && !TextUtils.isEmpty(objectId) && aClass != null)
            if (mJobList.contains(aClass.getSimpleName() + objectId)) {
                mJobList.remove(aClass.getSimpleName() + objectId);
            }

    }

// public static void addUploadJobToMonitoringList(String objectId) {
//        if (mJobList != null && !TextUtils.isEmpty(objectId))
//            mJobList.add(objectId + TYPE_UPLOAD);
//
//    }
//
//    public static void addValidationJobToMonitoringList(String objectId) {
//        if (mJobList != null && !TextUtils.isEmpty(objectId))
//            mJobList.add(objectId + TYPE_VALIDATE);
//
//    }
//
//    public static void addDownloadJobToMonitoringList(String objectId) {
//        if (mJobList != null && !TextUtils.isEmpty(objectId))
//            mJobList.add(objectId + TYPE_DOWNLOAD);
//
//    }
//
//    public static void removeDownloadJobFromMonitoringList(String objectId) {
//        if (mJobList != null && !TextUtils.isEmpty(objectId))
//            if (mJobList.contains(objectId + TYPE_DOWNLOAD)) {
//                mJobList.removeItem(objectId + TYPE_DOWNLOAD);
//            }
//
//    }
//
//    public static void removeUploadJobFromMonitoringList(String objectId) {
//        if (mJobList != null && !TextUtils.isEmpty(objectId))
//            if (mJobList.contains(objectId + TYPE_UPLOAD)) {
//                mJobList.removeItem(objectId + TYPE_UPLOAD);
//            }
//
//    }
//
//    public static void removeValidationJobFromMonitoringList(String objectId) {
//        if (mJobList != null && !TextUtils.isEmpty(objectId)) {
//            if (mJobList.contains(objectId + TYPE_VALIDATE)) {
//                mJobList.removeItem(objectId + TYPE_VALIDATE);
//            }
//            Injector.INSTANCE.getComponent().rxBus().send(new DownloadJobCompleteNotification(objectId));
//        }
//
//    }

}
