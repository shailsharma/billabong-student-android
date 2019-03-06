package in.securelearning.lil.android.syncadapter.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.upload.BaseUploadJob;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import io.reactivex.functions.Consumer;

/**
 * Sync Service.
 */
public class MessageService extends BaseService {
    public static final String TAG = MessageService.class.getCanonicalName();

    private boolean mFirstTime = true;
    private static final String ACTION_SYNC = "in.securelearning.lil.android.syncadapter.service.action.SYNC";
    private static final String ACTION_SYNC_POSTS = "in.securelearning.lil.android.syncadapter.service.action.SYNC_POSTS";
    private static final String ACTION_SYNC_POST = "in.securelearning.lil.android.syncadapter.service.action.SYNC_POST";
    private static final String ACTION_SYNC_POST_RESPONSE = "in.securelearning.lil.android.syncadapter.service.action.SYNC_POST_RESPONSE";
    private static final String ACTION_UPLOAD_BLOG_COMMENTS = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_BLOG_COMMENTS";
    private static final String ACTION_UPLOAD_BLOG_COMMENT = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_BLOG_COMMENT";
    private static final String ACTION_DOWNLOAD_BROADCAST_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BROADCAST_NOTIFICATION";
    private static final String ACTION_DOWNLOAD_BLOG_COMMENTS = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BLOG_COMMENTS";
    private static final String ACTION_SYNC_CALENDAR_EVENT = "in.securelearning.lil.android.syncadapter.service.action.SYNC_CALENDAR_EVENT";
    private static final String ACTION_FETCH_INTERNAL_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.FETCH_INTERNAL_NOTIFICATION";
    // TODO: Rename parameters
    private static final String EXTRA_OBJECT_TYPE = "in.securelearning.lil.android.syncadapter.service.extra.OBJECT_TYPE";
    private static final String EXTRA_OBJECT_ID = "in.securelearning.lil.android.syncadapter.service.extra.COURSE_ID";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MessageService() {
        super("MessageService");
    }

    /**
     * start sync service
     *
     * @param context
     */
    public static void startSyncService(Context context) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }


    public static void startActionSyncPosts(Context context) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_SYNC_POSTS);
//        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionSyncPost(Context context, String alias) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_SYNC_POST);
        intent.putExtra(EXTRA_OBJECT_ID, alias);
        context.startService(intent);
    }

    public static void startActionSyncPostResponse(Context context, String alias) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_SYNC_POST_RESPONSE);
        intent.putExtra(EXTRA_OBJECT_ID, alias);
        context.startService(intent);
    }

    public static void startActionDownloadCalendarEvent(Context context, String id) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_SYNC_CALENDAR_EVENT);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionFetchInternalNotification(Context context, String docId) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_FETCH_INTERNAL_NOTIFICATION);
        intent.putExtra(EXTRA_OBJECT_ID, docId);
        context.startService(intent);
    }

    public static void startActionUploadBlogComments(Context context) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_UPLOAD_BLOG_COMMENTS);
        context.startService(intent);
    }

    public static void startActionUploadBlogComment(Context context, String alias) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_UPLOAD_BLOG_COMMENT);
        intent.putExtra(EXTRA_OBJECT_ID, alias);
        context.startService(intent);
    }

    public static void startActionDownloadBroadcastNotification(Context context) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_DOWNLOAD_BROADCAST_NOTIFICATION);
        context.startService(intent);

    }

    public static void startActionDownloadBlogComments(Context context, String id) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_DOWNLOAD_BLOG_COMMENTS);
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

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            if (intent != null) {
//                if (mSyncServiceModel.isDownloadAllowed()) {
                if (isDataServerAccessible()) {
                    final String action = intent.getAction();
                    if (ACTION_SYNC.equals(action)) {
                        handleActionSync();
                    } else if (ACTION_DOWNLOAD_BROADCAST_NOTIFICATION.equals(action)) {
                        handleActionSyncBroadcastNotification();
                    } else if (ACTION_DOWNLOAD_BLOG_COMMENTS.equals(action)) {
                        final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                        handleActionDownloadBlogComments(id);
                    } else if (ACTION_UPLOAD_BLOG_COMMENTS.equals(action)) {
//                            final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                        handleActionUploadBlogComments();
                    } else if (ACTION_UPLOAD_BLOG_COMMENT.equals(action)) {
                        final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                        handleActionUploadBlogComment(id);
                    } else if (ACTION_SYNC_POSTS.equals(action)) {
//                            final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                        handleActionSyncPosts();
                    } else if (ACTION_SYNC_POST.equals(action)) {
                        final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                        if (!TextUtils.isEmpty(id)) handleActionSyncPost(id);
                    } else if (ACTION_SYNC_POST_RESPONSE.equals(action)) {
                        final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                        if (!TextUtils.isEmpty(id)) handleActionSyncPostResponse(id);
                    } else if (ACTION_SYNC_CALENDAR_EVENT.equals(action)) {
                        final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                        if (!TextUtils.isEmpty(id)) handleActionDownloadCalendarEvent(id);
                    } else if (ACTION_FETCH_INTERNAL_NOTIFICATION.equals(action)) {
                        final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                        handleActionFetchInternalNotification(id);
                    }
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
        if (GeneralUtils.isNetworkAvailable(MessageService.this)) {
            if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD)) {
                if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_POST)) {
//                if (mSyncServiceModel.isDownloadAllowed()) {
                    PostData postData = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostData.class);
                    JobCreator.createPostLearningNetworkPostDataJob(postData).execute();
                    PostData notificationPurgePostData = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostData.class);
                    if (notificationPurgePostData.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    }
//                } else {
//                    showPendingUploadsNotification();
//                }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_POST_RESPONSE)) {
//                if (mSyncServiceModel.isDownloadAllowed()) {
                    PostResponse postResponse = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostResponse.class);
                    JobCreator.createPostResponseJob(postResponse).execute();
                    PostResponse notificationPurgePostResponse = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostResponse.class);
                    if (notificationPurgePostResponse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    }
//                } else {
//                    showPendingUploadsNotification();
//                }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_CALENDAR_EVENT)) {
//                if (mSyncServiceModel.isDownloadAllowed()) {
                    CalendarEvent calendarEvent = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), CalendarEvent.class);
                    JobCreator.createPostCalendarEventDataJob(calendarEvent).execute();
                    CalendarEvent notificationPurgeCalendarEvent = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), CalendarEvent.class);
                    if (notificationPurgeCalendarEvent.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    }
//                } else {
//                    showPendingUploadsNotification();
//                }
                }
            }
        }
    }


    private void handleActionDownloadCalendarEvent(String id) {
        if (!TextUtils.isEmpty(id)) {
            try {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    JobCreator.createDownloadCalendarEventJob(id, "", true).execute();
                } else {
                    stopSelf();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleActionSyncPostResponse(String alias) {
        PostResponse postResponse = mSyncServiceModel.fetchPostResponseByAlias(alias);
        if (postResponse.getAlias().equals(alias) && postResponse.getSyncStatus().equals(SyncStatus.NOT_SYNC.toString())) {
            try {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    JobCreator.createPostResponseJob(postResponse).execute();
                } else {
                    stopSelf();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleActionSyncPost(String alias) {
        PostData postData = mSyncServiceModel.fetchPostDataByAlias(alias);
        if (postData.getAlias().equals(alias) && postData.getSyncStatus().equals(SyncStatus.NOT_SYNC.toString())) {
            try {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    JobCreator.createPostLearningNetworkPostDataJob(postData).execute();
                } else {
                    stopSelf();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleActionSync() {
        if (GeneralUtils.isNetworkAvailable(this)) startSync();
    }

    private void handleActionDownloadBlogComments(String id) {
        try {
            JobCreator.createDownloadBlogCommentsJob(id).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        removeJobFromMonitoringList(BlogComment.class, id);
    }

    private void handleActionUploadBlogComments() {
        if (BuildConfig.IS_BLOGS_ENABLED)
            mSyncServiceModel.fetchBlogCommentListNotSync().subscribe(new Consumer<BlogComment>() {
                @Override
                public void accept(BlogComment blogComment) throws Exception {
                    try {
                        if (GeneralUtils.isNetworkAvailable(MessageService.this))
                            JobCreator.createBlogCommentJob(blogComment).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private void handleActionUploadBlogComment(String alias) {
        BaseUploadJob<BlogComment> job = JobCreator.createBlogCommentJob(mSyncServiceModel.fetchBlogFromAlias(alias));
        removeJobFromMonitoringList(BlogComment.class, job.getDataObject().getAlias());
    }

    private void handleActionSyncPosts() {
        handleActionSyncBroadcastNotification();

//        if (BuildConfig.IS_LEARNING_NETWORK_ENABLED) {
//            mSyncServiceModel.fetchPostDataListNotSync().subscribe(new Consumer<PostData>() {
//                @Override
//                public void accept(PostData postData) throws Exception {
//
////                Collections.sort(postDatas, new SortPostByCreatedTime.PostCreatedTimeSorter());
////                for (PostData postData :
////                        postDatas) {
//                    if (postData.getSyncStatus().equals(SyncStatus.JSON_SYNC.getStatus())) {
//                        try {
//                            if (mSyncServiceModel.isDownloadAllowed()) {
//
//                                JobCreator.createPostDataValidationJob(postData).execute();
//                            } else {
//                                stopSelf();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else if (postData.getSyncStatus().equals(SyncStatus.NOT_SYNC.getStatus())) {
//                        try {
//                            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
//                                JobCreator.createPostLearningNetworkPostDataJob(postData).execute();
//                            } else {
//                                stopSelf();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
////                }
//                }
//            });
//
//            mSyncServiceModel.fetchPostResponseListNotSync().subscribe(new Consumer<PostResponse>() {
//                @Override
//                public void accept(PostResponse postResponse) throws Exception {
////                Collections.sort(postResponses, new SortPostResponseByDate.CreatedDateSorter());
////                for (PostResponse postResponse :
////                        postResponses) {
//                    if (postResponse.getSyncStatus().equals(SyncStatus.JSON_SYNC.getStatus())) {
//                        try {
//                            if (mSyncServiceModel.isDownloadAllowed()) {
//                                JobCreator.createPostResponseValidationJob(postResponse).execute();
//                            } else {
//                                stopSelf();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else if (postResponse.getSyncStatus().equals(SyncStatus.NOT_SYNC.getStatus())) {
//                        try {
//                            if (mSyncServiceModel.isDownloadAllowed()) {
//                                JobCreator.createPostResponseJob(postResponse).execute();
//                            } else {
//                                stopSelf();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
////                    }
//                    }
//
//                }
//
//
//            });
//        }
//        if (BuildConfig.IS_CALENDAR_ENABLED) {
//            mSyncServiceModel.fetchCalendarEventsListNotSync()
//                    .subscribe(new Consumer<CalendarEvent>() {
//                        @Override
//                        public void accept(CalendarEvent calendarEvent) throws Exception {
//                            if (calendarEvent.getSyncStatus().equalsIgnoreCase(SyncStatus.JSON_SYNC.getStatus())) {
//                                try {
//                                    if (mSyncServiceModel.isDownloadAllowed()) {
//                                        JobCreator.createCalendarEventValidationJob(calendarEvent).execute();
//                                    } else {
//                                        stopSelf();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            } else if (calendarEvent.getSyncStatus().equals(SyncStatus.NOT_SYNC.getStatus())) {
//                                try {
//                                    if (mSyncServiceModel.isDownloadAllowed()) {
//                                        JobCreator.createPostCalendarEventDataJob(calendarEvent).execute();
//                                    } else {
//                                        stopSelf();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    });
//        }

    }

    private void handleActionSyncBroadcastNotification() {

        if (BuildConfig.IS_LEARNING_NETWORK_ENABLED) {
            mSyncServiceModel.fetchPostNotificationListSync().subscribe(new Consumer<Notification>() {
                @Override
                public void accept(Notification notification) throws Exception {
                    try {
//                        if (mSyncServiceModel.isDownloadAllowed()) {
                        if (GeneralUtils.isNetworkAvailable(MessageService.this))
                            JobCreator.createDownloadPostDataJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
//                        } else {
//                            stopSelf();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mSyncServiceModel.fetchPostResponseNotificationListSync().subscribe(new Consumer<Notification>() {
                @Override
                public void accept(Notification notification) throws Exception {
                    try {
//                        if (mSyncServiceModel.isDownloadAllowed()) {
                        if (GeneralUtils.isNetworkAvailable(MessageService.this))
                            JobCreator.createPostResponseDownloadJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
//                        } else {
//                            stopSelf();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        if (BuildConfig.IS_CALENDAR_ENABLED) {
            mSyncServiceModel.fetchCalendarNotificationListSync().subscribe(new Consumer<Notification>() {
                @Override
                public void accept(Notification notification) throws Exception {
                    try {
//                        if (mSyncServiceModel.isDownloadAllowed()) {
                        if (GeneralUtils.isNetworkAvailable(MessageService.this))
                            JobCreator.createDownloadCalendarEventJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(), false).execute();
//                        } else {
//                            stopSelf();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
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
            mSyncServiceModel.fetchInternalNotificationList().subscribe(new Consumer<InternalNotification>() {
                @Override
                public void accept(InternalNotification internalNotification) throws Exception {
                    try {
                        if (GeneralUtils.isNetworkAvailable(MessageService.this))
                            handleInternalNotification(internalNotification);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            handleActionSyncPosts();

            startUploadProcess();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (GeneralUtils.isNetworkAvailable(this))
//            MessageService.startSyncService(this);
    }


    /**
     * start upload process
     */
    public void startUploadProcess() {

        handleActionUploadBlogComments();

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
