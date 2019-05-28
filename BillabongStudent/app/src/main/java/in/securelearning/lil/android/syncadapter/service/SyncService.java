package in.securelearning.lil.android.syncadapter.service;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityData;
import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.BookAnnotation;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.CourseAnalytics;
import in.securelearning.lil.android.base.dataobjects.CourseProgress;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.Notification;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.UserCourseProgress;
import in.securelearning.lil.android.base.dataobjects.UserRating;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.dataobjects.WebQuizResponse;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.courses.dataobject.CourseReview;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.events.CourseDeleteEvent;
import in.securelearning.lil.android.syncadapter.events.RefreshTrainingListEvent;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.job.upload.BaseUploadJob;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import in.securelearning.lil.android.base.dataobjects.UserBrowseHistory;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_NETWORK_DOWNLOAD;

/**
 * Sync Service.
 */
public class SyncService extends BaseService {
    public static final String TAG = SyncService.class.getCanonicalName();
    private static final String ACTION_DOWNLOAD_ALL_TRAININGS = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_ALL_TRAININGS";

    private boolean mFirstTime = true;
    private static final String ACTION_DOWNLOAD = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD";
    private static final String ACTION_SYNC = "in.securelearning.lil.android.syncadapter.service.action.SYNC";
    private static final String ACTION_DOWNLOAD_DIGITAL_BOOK_ABOUT = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_DIGITAL_BOOK_ABOUT";
    private static final String ACTION_DOWNLOAD_DIGITAL_BOOK = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_DIGITAL_BOOK";
    private static final String ACTION_DOWNLOAD_BLOG = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BLOG";
    private static final String ACTION_DOWNLOAD_CONCEPT_MAP = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_INTERACTIVE_IMAGE";
    private static final String ACTION_DOWNLOAD_INTERACTIVE_IMAGE = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_CONCEPT_MAP";
    private static final String ACTION_DOWNLOAD_POP_UP = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_POP_UP";
    private static final String ACTION_DOWNLOAD_VIDEO_COURSE = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_VIDEO_COURSE";
    private static final String ACTION_DOWNLOAD_INTERACTIVE_VIDEO = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_INTERACTIVE_VIDEO";
    private static final String ACTION_UPLOAD_POSTS = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_POSTS";
    private static final String ACTION_UPLOAD_BLOG_COMMENTS = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_BLOG_COMMENTS";
    private static final String ACTION_UPLOAD_BLOG_COMMENT = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_BLOG_COMMENT";
    private static final String ACTION_DOWNLOAD_BLOG_COMMENTS = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BLOG_COMMENTS";
    private static final String ACTION_UPLOAD_WEB_QUIZ = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_WEB_QUIZ";
    private static final String ACTION_UPLOAD_BOOK_ANNOTATION = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_BOOK_ANNOTATION";
    private static final String ACTION_DOWNLOAD_BROADCAST_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_BROADCAST_NOTIFICATION";
    private static final String ACTION_DOWNLOAD_LEARNING_MAP = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_LEARNING_MAP";
    private static final String ACTION_UPLOAD_ASSIGNMENT = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_ASSIGNMENT";
    private static final String ACTION_UPLOAD_QUESTION_RESPONSE = "in.securelearning.lil.android.syncadapter.service.action.QUESTION_RESPONSE";
    // TODO: Rename parameters
    private static final String EXTRA_OBJECT_TYPE = "in.securelearning.lil.android.syncadapter.service.extra.OBJECT_TYPE";
    private static final String EXTRA_OBJECT_ID = "in.securelearning.lil.android.syncadapter.service.extra.COURSE_ID";
    private static final String ACTION_UPLOAD_ASSIGNMENT_RESPONSE = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_ASSIGNMENT_RESPONSE";
    private static final String ACTION_FETCH_INTERNAL_NOTIFICATION = "in.securelearning.lil.android.syncadapter.service.action.FETCH_INTERNAL_NOTIFICATION";
    private static final String ACTION_UPLOAD_COURSE_USER_LOG = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_COURSE_USER_LOG";
    //by me
    private static final String ACTION_DOWNLOAD_ACTIVITY = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_ACTIVITY";
    private static final String ACTION_UPLOAD_USER_BROWSE_HISTORY_LOG = "in.securelearning.lil.android.syncadapter.service.action.UPLOAD_USER_BROWSE_HISTORY_LOG";
    private static final String ACTION_DOWNLOAD_LEARNING = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_LEARNING";
    private static final String ACTION_DOWNLOAD_RECENTLY_READ = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_RECENTLY_READ";
    private static final String ACTION_DOWNLOAD_TOPIC_COVERED = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_TOPIC_COVERED";
    private static final String ACTION_DOWNLOAD_PERFORMANCE_COUNT = "in.securelearning.lil.android.syncadapter.service.action.DOWNLOAD_PERFORMANCE_COUNT";

    private static final String SUBJECT_ID = "in.securelearning.lil.android.syncadapter.service.extra.SUBJECT_ID";
    private static final String END_DATE = "in.securelearning.lil.android.syncadapter.service.extra.END_DATE";
    private static final String START_DATE = "in.securelearning.lil.android.syncadapter.service.extra.START_DATE";
    private static final String LIMIT = "in.securelearning.lil.android.syncadapter.service.extra.LIMIT";
    private static final String SKIP = "in.securelearning.lil.android.syncadapter.service.extra.SKIP";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SyncService() {
        super("SyncService");
    }

    /**
     * start sync service
     *
     * @param context
     */
    public static void startSyncService(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }

    public static void startActionDownloadBroadcastNotification(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_BROADCAST_NOTIFICATION);
        context.startService(intent);
    }

    public static void startActionDownload(Context context, String id, Class type) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        intent.putExtra(EXTRA_OBJECT_TYPE, type);
        context.startService(intent);
    }

    public static void startActionDownloadActivity(Context context, String subid, String startDate, String endDate) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_ACTIVITY);
        intent.putExtra(SUBJECT_ID, subid);
        intent.putExtra(END_DATE, endDate);
        intent.putExtra(START_DATE, startDate);
        context.startService(intent);
    }

    public static void startActionDownloadPerformanceCount(Context context, String subid) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_PERFORMANCE_COUNT);
        intent.putExtra(SUBJECT_ID, subid);
        context.startService(intent);
    }

    public static void startActionDownloadActivityRecentlyRead(Context context, String subid) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_RECENTLY_READ);
        intent.putExtra(SUBJECT_ID, subid);
        context.startService(intent);
    }

    public static void startActionDownloadActivityTopicCovered(Context context, String subid) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_TOPIC_COVERED);
        intent.putExtra(SUBJECT_ID, subid);
        context.startService(intent);
    }

    public static void startActionDownloadLearning(Context context, String subid, String startDate, String endDate) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_LEARNING);
        intent.putExtra(SUBJECT_ID, subid);
        intent.putExtra(END_DATE, endDate);
        intent.putExtra(START_DATE, startDate);
        context.startService(intent);
    }

    public static void startActionDownloadLearningMap(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_LEARNING_MAP);
        context.startService(intent);
    }

    public static void startActionFetchInternalNotification(Context context, String docId) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_FETCH_INTERNAL_NOTIFICATION);
        intent.putExtra(EXTRA_OBJECT_ID, docId);
        context.startService(intent);
    }

    public static void startActionDownloadDigitalBookAbout(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_DIGITAL_BOOK_ABOUT);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionDownloadDigitalBook(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_DIGITAL_BOOK);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionDownloadConceptMap(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_CONCEPT_MAP);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionDownloadPopUp(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_POP_UP);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionDownloadInteractiveImage(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_INTERACTIVE_IMAGE);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionDownloadVideoCourse(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_VIDEO_COURSE);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionDownloadInteractiveVideo(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_INTERACTIVE_VIDEO);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionDownloadBlog(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_BLOG);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionSyncPosts(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_POSTS);
//        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionUploadBlogComments(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_BLOG_COMMENTS);
        context.startService(intent);
    }

    public static void startActionUploadBlogComment(Context context, String alias) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_BLOG_COMMENT);
        intent.putExtra(EXTRA_OBJECT_ID, alias);
        context.startService(intent);
    }

    public static void startActionUploadAssignment(Context context, String alias) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_ASSIGNMENT);
        intent.putExtra(EXTRA_OBJECT_ID, alias);
        context.startService(intent);
    }

    public static void startActionDownloadBlogComments(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_BLOG_COMMENTS);
        intent.putExtra(EXTRA_OBJECT_ID, id);
        context.startService(intent);
    }

    public static void startActionUploadWebQuiz(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_WEB_QUIZ);
        context.startService(intent);
    }

    public static void startActionUploadBookAnnotation(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_BOOK_ANNOTATION);
        context.startService(intent);
    }

    public static void startActionUploadCourseUserLog(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_COURSE_USER_LOG);
        context.startService(intent);
    }

    public static void startUploadUserBrowseHistoryLog(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_USER_BROWSE_HISTORY_LOG);
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
                } else if (ACTION_DOWNLOAD_ALL_TRAININGS.equals(action)) {
                    handleActionDownloadAllTrainings();
                } else if (ACTION_DOWNLOAD_LEARNING_MAP.equals(action)) {
                    handleActionDownloadLearningMap();
                } else if (ACTION_DOWNLOAD_BROADCAST_NOTIFICATION.equals(action)) {
                    handleActionDownloadBroadcastNotification();
                } else if (ACTION_DOWNLOAD_DIGITAL_BOOK_ABOUT.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadDigitalBookAbout(id);
                } else if (ACTION_DOWNLOAD_DIGITAL_BOOK.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadDigitalBook(id);
                } else if (ACTION_DOWNLOAD_CONCEPT_MAP.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadConceptMap(id);
                } else if (ACTION_DOWNLOAD_POP_UP.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadPopUp(id);
                } else if (ACTION_DOWNLOAD_INTERACTIVE_IMAGE.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadInteractiveImage(id);
                } else if (ACTION_DOWNLOAD_VIDEO_COURSE.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadVideoCourse(id);
                } else if (ACTION_DOWNLOAD_INTERACTIVE_VIDEO.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadInteractiveVideo(id);
                } else if (ACTION_DOWNLOAD_BLOG.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadBlog(id);
                } else if (ACTION_DOWNLOAD_BLOG_COMMENTS.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionDownloadBlogComments(id);
                } else if (ACTION_UPLOAD_BLOG_COMMENTS.equals(action)) {
//                            final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
//                            handleActionUploadBlogComments();
                } else if (ACTION_UPLOAD_BLOG_COMMENT.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE);
                    handleActionUploadBlogComment(id);
                } else if (ACTION_UPLOAD_ASSIGNMENT.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                    handleActionUploadAssignment(id);
                } else if (ACTION_UPLOAD_QUESTION_RESPONSE.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                    handleActionUploadQuestionResponse(id);
                } else if (ACTION_UPLOAD_WEB_QUIZ.equals(action)) {
//                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
//                            final Class type = (Class) intent.getSerializableExtra(EXTRA_OBJECT_TYPE)
                    handleActionUploadWebQuiz();
                } else if (ACTION_UPLOAD_BOOK_ANNOTATION.equals(action)) {
                    handleActionUploadBookAnnotation();
                } else if (ACTION_UPLOAD_USER_BROWSE_HISTORY_LOG.equals(action)) {
                    // handleActionUploadCourseUserLog();
                    handleActionUploadUserBrowseHistory();
                } else if (ACTION_UPLOAD_ASSIGNMENT_RESPONSE.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                    handleActionUploadAssignmentResponse(id);
                } else if (ACTION_FETCH_INTERNAL_NOTIFICATION.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_OBJECT_ID);
                    handleActionFetchInternalNotification(id);
                } else if (ACTION_DOWNLOAD_ACTIVITY.equals(action)) {
                    final String subid = intent.getStringExtra(SUBJECT_ID);
                    final String startdate = intent.getStringExtra(START_DATE);
                    final String enddate = intent.getStringExtra(END_DATE);
                    handleActionDownloadActivity(subid, startdate, enddate);

                } else if (ACTION_DOWNLOAD_LEARNING.equals(action)) {
                    final String subid = intent.getStringExtra(SUBJECT_ID);
                    final String startdate = intent.getStringExtra(START_DATE);
                    final String enddate = intent.getStringExtra(END_DATE);
                    handleActionDownloadLearning(subid, startdate, enddate);

                } else if (ACTION_DOWNLOAD_RECENTLY_READ.equals(action)) {
                    final String subid = intent.getStringExtra(SUBJECT_ID);
                    final int limit = intent.getIntExtra(LIMIT, 0);
                    final int skip = intent.getIntExtra(SKIP, 0);
                    handleActionDownloadRecentlyRead(subid, limit, skip);

                } else if (ACTION_DOWNLOAD_TOPIC_COVERED.equals(action)) {
                    final String subid = intent.getStringExtra(SUBJECT_ID);
                    final int limit = intent.getIntExtra(LIMIT, 0);
                    final int skip = intent.getIntExtra(SKIP, 0);
                    handleActionDownloadTopicCovered(subid, limit, skip);

                } else if (ACTION_DOWNLOAD_PERFORMANCE_COUNT.equals(action)) {
                    final String subid = intent.getStringExtra(SUBJECT_ID);
                    handleActionDownloadPerformanceCount(subid);

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

    private void handleActionDownloadAllTrainings() {
        if (BuildConfig.IS_TRAINING_ENABLED) {
            try {
                JobCreator.createTrainingsBulkDownloadJob().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if (GeneralUtils.isNetworkAvailable(SyncService.this)) {
            if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_COURSE_FAVORITE_TRUE)) {
                Call<ResponseBody> responseBodyCall = mNetworkModel.addFavorite(true, internalNotification.getObjectId(), internalNotification.getObjectType().toLowerCase());
                Response<ResponseBody> response = responseBodyCall.execute();
                if (response != null && response.isSuccessful()) {
                    mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                } else if (response.code() == 401 || response.code() == 403) {
                    if (SyncServiceHelper.refreshToken(SyncService.this)) {
                        Call<ResponseBody> responseBodyCall2 = responseBodyCall.clone();
                        response = responseBodyCall2.execute();
                        if (response != null && response.isSuccessful()) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                        }

                    }
                }

            } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_COURSE_FAVORITE_FALSE)) {
                Call<ResponseBody> responseBodyCall = mNetworkModel.addFavorite(false, internalNotification.getObjectId(), internalNotification.getObjectType().toLowerCase());
                Response<ResponseBody> response = responseBodyCall.execute();
                if (response != null && response.isSuccessful()) {
                    mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                } else if (response.code() == 401 || response.code() == 403) {
                    if (SyncServiceHelper.refreshToken(SyncService.this)) {
                        Call<ResponseBody> responseBodyCall2 = responseBodyCall.clone();
                        response = responseBodyCall2.execute();
                        if (response != null && response.isSuccessful()) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                        }

                    }
                }

            } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_MICRO_COURSE_FAVORITE_TRUE)) {
                Call<ResponseBody> responseBodyCall = mNetworkModel.addMicroCourseFavorite(true, internalNotification.getObjectId(), internalNotification.getObjectType().toLowerCase());
                Response<ResponseBody> response = responseBodyCall.execute();
                if (response != null && response.isSuccessful()) {
                    mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                } else if (response.code() == 401 || response.code() == 403) {
                    if (SyncServiceHelper.refreshToken(SyncService.this)) {
                        Call<ResponseBody> responseBodyCall2 = responseBodyCall.clone();
                        response = responseBodyCall2.execute();
                        if (response != null && response.isSuccessful()) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                        }

                    }
                }

            } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_MICRO_COURSE_FAVORITE_FALSE)) {
                Call<ResponseBody> responseBodyCall = mNetworkModel.addMicroCourseFavorite(false, internalNotification.getObjectId(), internalNotification.getObjectType().toLowerCase());
                Response<ResponseBody> response = responseBodyCall.execute();
                if (response != null && response.isSuccessful()) {
                    mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                } else if (response.code() == 401 || response.code() == 403) {
                    if (SyncServiceHelper.refreshToken(SyncService.this)) {
                        Call<ResponseBody> responseBodyCall2 = responseBodyCall.clone();
                        response = responseBodyCall2.execute();
                        if (response != null && response.isSuccessful()) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                        }

                    }
                }

            } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_COURSE_REVIEW_ADD)) {

                UserRating rating = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), UserRating.class);
                CourseReview courseReview = new CourseReview(rating, mSyncServiceModel.getCourseType(internalNotification.getObjectType()));
                Call<UserRating> responseBodyCall = mNetworkModel.addRating(internalNotification.getObjectId(), courseReview);
                Response<UserRating> response = responseBodyCall.execute();
                if (response != null && response.isSuccessful()) {
                    mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                } else if (response.code() == 401 || response.code() == 403) {
                    if (SyncServiceHelper.refreshToken(SyncService.this)) {
                        Call<UserRating> responseBodyCall2 = responseBodyCall.clone();
                        response = responseBodyCall2.execute();
                        if (response != null && response.isSuccessful()) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        }
                    }
                }
            } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD)) {
                if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_QUIZ)) {
//                if (mSyncServiceModel.isDownloadAllowed()) {
                    Quiz quiz = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), Quiz.class);
                    JobCreator.createUploadQuizJob(quiz).execute();
                    Quiz notificationPurgeQuiz = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), Quiz.class);
                    if (notificationPurgeQuiz.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    }
//                } else {
//                    showPendingUploadsNotification();
//                }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_ASSIGNMENT)) {
//                if (mSyncServiceModel.isDownloadAllowed()) {
                    Assignment assignment = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), Assignment.class);
                    JobCreator.createPostAssignmentJob(assignment).execute();
                    Assignment notificationPurgeAssignment = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), Assignment.class);
                    if (notificationPurgeAssignment.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    }
//                } else {
//                    showPendingUploadsNotification();
//                }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_ASSIGNMENT_RESPONSE)) {
//                if (mSyncServiceModel.isDownloadAllowed()) {
                    AssignmentResponse assignmentResponse = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), AssignmentResponse.class);
                    JobCreator.createPostAssignmentResponseJob(assignmentResponse).execute();
                    AssignmentResponse notificationPurgeAssignmentResponse = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), AssignmentResponse.class);
                    if (notificationPurgeAssignmentResponse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    }
//                } else {
//                    showPendingUploadsNotification();
//                }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_POST)) {
                    PostData postData = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostData.class);
                    JobCreator.createPostLearningNetworkPostDataJob(postData).execute();
                    PostData notificationPurgePostData = mSyncServiceModel.retrieveLearningNetwork(internalNotification.getObjectDocId(), PostData.class);
                    if (notificationPurgePostData.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.ACTION_TYPE_QUESTION_RESPONSE_UPLOAD)) {
                    QuestionResponse questionResponse = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), QuestionResponse.class);
                    JobCreator.createUploadQuestionResponseJob(questionResponse).execute();
                    QuestionResponse notificationPurgeQuestionResponse = mSyncServiceModel.retrieveAssignments(internalNotification.getObjectDocId(), QuestionResponse.class);
                    if (notificationPurgeQuestionResponse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                        mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    }
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
            } else if (internalNotification.getObjectAction() == (ACTION_TYPE_NETWORK_DOWNLOAD)) {
                if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_DIGITAL_BOOK)) {
                    if (mSyncServiceModel.isDownloadAllowed()) {
                        JobCreator.createDownloadDigitalBookJob(internalNotification.getObjectId(), "", true, false).execute();
                        DigitalBook notificationPurgeDigitalBook = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), DigitalBook.class);
                        if (notificationPurgeDigitalBook.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        }
                    } else {
                        showPendingUploadsNotification();
                    }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_DIGITAL_BOOK_UPDATE)) {
                    if (mSyncServiceModel.isDownloadAllowed()) {
                        DigitalBook fetchedDigitalBook = mSyncServiceModel.fetchDigitalBookByObjectId(internalNotification.getObjectId());
                        if (fetchedDigitalBook != null && !TextUtils.isEmpty(fetchedDigitalBook.getObjectId()) && fetchedDigitalBook.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            JobCreator.createDownloadDigitalBookJob(internalNotification.getObjectId(), "", false, true).execute();
                            DigitalBook notificationPurgeDigitalBook = mSyncServiceModel.fetchDigitalBookByObjectId(internalNotification.getObjectId());
                            if (notificationPurgeDigitalBook.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                            }
                        } else {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        }

                    } else {
                    }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_ABOUT_VIDEO_COURSE)) {
                    if (mSyncServiceModel.isDownloadAllowed()) {
                        JobCreator.createDownloadVideoCourseJob(internalNotification.getObjectId()).execute();
                        VideoCourse notificationPurgeVideoCourse = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), VideoCourse.class);
                        if (notificationPurgeVideoCourse.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        }
                    } else {
                        showPendingUploadsNotification();
                    }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_CONCEPT_MAP)) {
                    if (mSyncServiceModel.isDownloadAllowed()) {
                        JobCreator.createDownloadConceptMapJob(internalNotification.getObjectId()).execute();
                        ConceptMap notificationPurgeConceptMap = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), ConceptMap.class);
                        if (notificationPurgeConceptMap.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        }
                    } else {
                        showPendingUploadsNotification();
                    }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_POP_UP)) {
                    if (mSyncServiceModel.isDownloadAllowed()) {
                        JobCreator.createDownloadPopUpsJob(internalNotification.getObjectId()).execute();
                        PopUps notificationPurgePopUps = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), PopUps.class);
                        if (notificationPurgePopUps.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        }
                    } else {
                        showPendingUploadsNotification();
                    }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_INTERACTIVE_IMAGE)) {
                    if (mSyncServiceModel.isDownloadAllowed()) {
                        JobCreator.createDownloadInteractiveImageJob(internalNotification.getObjectId()).execute();
                        InteractiveImage notificationPurgeInteractiveImage = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), InteractiveImage.class);
                        if (notificationPurgeInteractiveImage.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        }
                    } else {
                        showPendingUploadsNotification();
                    }
                } else if (internalNotification.getDataObjectType() == (InternalNotificationActionUtils.OBJECT_TYPE_INTERACTIVE_VIDEO)) {
                    if (mSyncServiceModel.isDownloadAllowed()) {
                        JobCreator.createDownloadInteractiveVideoJob(internalNotification.getObjectId()).execute();
                        InteractiveVideo notificationPurgeInteractiveVideo = mSyncServiceModel.retrieveCourses(internalNotification.getObjectDocId(), InteractiveVideo.class);
                        if (notificationPurgeInteractiveVideo.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        }
                    } else {
                        showPendingUploadsNotification();
                    }
                }
            } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_COURSE_DELETE)) {
                if (mSyncServiceModel.deleteCourse(SyncService.this, internalNotification.getObjectId(), internalNotification.getObjectDocId())) {
                    //mSyncServiceModel.deleteFromDownloadedCourse(internalNotification.getObjectId());
                    mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                    InjectorSyncAdapter.INSTANCE.getComponent().rxBus().send(new CourseDeleteEvent(internalNotification.getObjectId()));
                }

            } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_COURSE_PROGRESS_UPLOAD)) {
                CourseProgress courseProgress = mSyncServiceModel.retrieveCourseProgress(internalNotification.getObjectDocId(), CourseProgress.class);
                Call<ResponseBody> responseBodyCall = mNetworkModel.uploadCourseProgress(courseProgress);
                Response<ResponseBody> response = responseBodyCall.execute();
                if (response != null && response.isSuccessful()) {
                    Log.e("CourseProgressUpload1--", "Success");

                } else if (response.code() == 401 || response.code() == 403) {
                    if (SyncServiceHelper.refreshToken(SyncService.this)) {
                        Call<ResponseBody> responseBodyCall2 = responseBodyCall.clone();
                        response = responseBodyCall2.execute();
                        if (response != null && response.isSuccessful()) {
                            Log.e("CourseProgressUpload2--", "Success");

                        } else {
                            Log.e("CourseProgressUpload2--", "Failed");

                        }

                    }
                } else {
                    Log.e("CourseProgressUpload1--", "Failed");

                }

            } else if (internalNotification.getObjectAction() == (InternalNotificationActionUtils.ACTION_TYPE_USER_COURSE_PROGRESS_UPLOAD)) {
                UserCourseProgress userCourseProgress = mSyncServiceModel.retrieveCourseProgress(internalNotification.getObjectDocId(), UserCourseProgress.class);
                Call<ResponseBody> responseBodyCall = mNetworkModel.uploadUserCourseProgress(userCourseProgress);
                Response<ResponseBody> response = responseBodyCall.execute();
                if (response != null && response.isSuccessful()) {
                    Log.e("UserCourseProgress1--", "Success");
                    mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());

                } else if (response.code() == 404) {
                    Log.e("UserCourseProgress1--", "Failed 404");
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(getBaseContext())) {
                    if (SyncServiceHelper.refreshToken(SyncService.this)) {
                        Response<ResponseBody> response2 = responseBodyCall.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            Log.e("UserCourseProgress2--", "Success");
                            mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
                        } else if (response2.code() == 404) {
                            Log.e("UserCourseProgress2--", "Failed 404");
                        } else if ((response2.code() == 401)) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getBaseContext()));
                        } else {
                            Log.e("UserCourseProgress2--", "Failed");

                        }
                    }
                } else {
                    Log.e("UserCourseProgress1--", "Failed");

                }

            }
        }

    }

    public void showPendingUploadsNotification() {
        NotificationUtil.showNotificationForPendingUploads(SyncService.this, "You have pending uploads please connect to a wifi or enable download on data", "Pending uploads");
    }

    private void handleActionUploadAssignmentResponse(String id) {
        AssignmentResponse assignmentResponse = mSyncServiceModel.fetchAssignmentResponseFromUid(id);
        if (assignmentResponse != null && assignmentResponse.getAlias().equals(id)) {
            BaseUploadJob<AssignmentResponse> job = JobCreator.createPostAssignmentResponseJob(assignmentResponse);
            removeJobFromMonitoringList(AssignmentResponse.class, id);
            job.execute();
        }
    }

    private void handleActionUploadAssignment(String alias) {
        Assignment assignment = mSyncServiceModel.fetchAssignmentFromAlias(alias);
        if (assignment != null && assignment.getAlias().equals(alias)) {
            BaseUploadJob<Assignment> job = JobCreator.createPostAssignmentJob(assignment);
            job.execute();
            removeJobFromMonitoringList(Assignment.class, alias);

        }
    }

    private void handleActionUploadQuestionResponse(String alias) {
        QuestionResponse questionResponse = mSyncServiceModel.fetchQuestionResponseFromAlias(alias);
        if (questionResponse != null && questionResponse.getAlias().equals(alias)) {
            BaseUploadJob<QuestionResponse> job = JobCreator.createUploadQuestionResponseJob(questionResponse);
            job.execute();
            removeJobFromMonitoringList(QuestionResponse.class, alias);

        }
    }

    private void handleActionSync() {
        if (GeneralUtils.isNetworkAvailable(SyncService.this))
            startSync();
    }

    private void handleActionUploadWebQuiz() {
        //  if (BuildConfig.IS_COURSES_ENABLED) {
        startUploadWebQuizResponse();
        // }
    }

    private void handleActionUploadBookAnnotation() {
        if (BuildConfig.IS_COURSES_ENABLED) {
            startUploadWebAnnotation();
        }
    }


//    private void handleActionUploadCourseUserLog() {
//        if (BuildConfig.IS_COURSES_ENABLED) {
//            startUploadCourseUserLog();
//        }
//    }

    //by rupsi
    private void handleActionUploadUserBrowseHistory() {
        startUploadUserBrowseHistoryLog();

    }


    private void handleActionDownload(String id, Class aClass) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionDownloadDigitalBookAbout(String id) {
        JobCreator.createDownloadAboutCourseDigitalBookJob(id, "", false, false).execute();
        removeJobFromMonitoringList(DigitalBook.class, id);
    }

    private void handleActionDownloadDigitalBook(String id) {
        JobCreator.createDownloadDigitalBookJob(id, "", true, false).execute();
        removeJobFromMonitoringList(DigitalBook.class, id);
    }

    private void handleActionDownloadConceptMap(String id) {
        JobCreator.createDownloadConceptMapJob(id).execute();
        removeJobFromMonitoringList(ConceptMap.class, id);
    }

    private void handleActionDownloadInteractiveImage(String id) {
        JobCreator.createDownloadInteractiveImageJob(id).execute();
        removeJobFromMonitoringList(InteractiveImage.class, id);
    }

    private void handleActionDownloadPopUp(String id) {
        JobCreator.createDownloadPopUpsJob(id).execute();
        removeJobFromMonitoringList(PopUps.class, id);
    }

    private void handleActionDownloadVideoCourse(String id) {
        JobCreator.createDownloadVideoCourseJob(id).execute();
        removeJobFromMonitoringList(VideoCourse.class, id);
    }

    private void handleActionDownloadInteractiveVideo(String id) {
        JobCreator.createDownloadInteractiveVideoJob(id).execute();
        removeJobFromMonitoringList(InteractiveVideo.class, id);
    }

    private void handleActionDownloadBlog(String id) {
        JobCreator.createDownloadBlogDetailsJob(id, "").execute();
        removeJobFromMonitoringList(BlogDetails.class, id);
    }

    public void handleActionDownloadActivity(String subId, String startDate, String endDate) {
        if (GeneralUtils.isNetworkAvailable(SyncService.this))
            JobCreator.createDownloadActivityJob(subId, startDate, endDate).execute();
    }

    public void handleActionDownloadLearning(String subId, String startDate, String endDate) {
        if (GeneralUtils.isNetworkAvailable(SyncService.this))
            JobCreator.createDownloadLearningJob(subId, startDate, endDate).execute();
    }

    public void handleActionDownloadRecentlyRead(String subId, int limit, int skip) {
        if (GeneralUtils.isNetworkAvailable(SyncService.this))
            JobCreator.createDownloadRecentlyReadJob(subId, limit, skip).execute();
    }

    public void handleActionDownloadTopicCovered(String subId, int limit, int skip) {
        if (GeneralUtils.isNetworkAvailable(SyncService.this))
            JobCreator.createDownloadTopicCoveredJob(subId, limit, skip).execute();
    }

    public void handleActionDownloadPerformanceCount(String subId) {
        if (GeneralUtils.isNetworkAvailable(SyncService.this))
            JobCreator.createDownloadPerformanceCountJob(subId).execute();
    }


    //by rupsi chnage blog to activity object class in base project
//    private void handleActionDownloadActivity(String id) {
//        JobCreator.createDownloadActivityJob().execute();
//        removeJobFromMonitoringList(AnalysisActivityData.class, id);
//    }


    private void handleActionDownloadBlogComments(String id) {
        JobCreator.createDownloadBlogCommentsJob(id).execute();
        removeJobFromMonitoringList(BlogComment.class, id);
    }

    private void handleActionDownloadCourses() {

        mSyncServiceModel.fetchDigitalBookListSync()
                .subscribe(new Consumer<DigitalBook>() {
                    @Override
                    public void accept(DigitalBook object) throws Exception {
                        if (!object.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            if (mSyncServiceModel.isDownloadAllowed()) {
                                JobCreator.createDownloadDigitalBookJob(object.getObjectId(), "", true, false).execute();
                            } else {
                                stopSelf();
                            }
                        }
                    }
                });
        mSyncServiceModel.fetchConceptMapListSync()
                .subscribe(new Consumer<ConceptMap>() {
                    @Override
                    public void accept(ConceptMap object) throws Exception {
                        if (!object.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            if (mSyncServiceModel.isDownloadAllowed()) {
                                JobCreator.createDownloadConceptMapJob(object.getObjectId()).execute();
                            } else {
                                stopSelf();
                            }
                        }
                    }
                });
        mSyncServiceModel.fetchInteractiveImageListSync()
                .subscribe(new Consumer<InteractiveImage>() {
                    @Override
                    public void accept(InteractiveImage object) throws Exception {
                        if (!object.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            if (mSyncServiceModel.isDownloadAllowed()) {
                                JobCreator.createDownloadInteractiveImageJob(object.getObjectId()).execute();
                            } else {
                                stopSelf();
                            }
                        }
                    }
                });
        mSyncServiceModel.fetchInteractiveVideoListSync()
                .subscribe(new Consumer<InteractiveVideo>() {
                    @Override
                    public void accept(InteractiveVideo object) throws Exception {
                        if (!object.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            if (mSyncServiceModel.isDownloadAllowed()) {
                                JobCreator.createDownloadInteractiveVideoJob(object.getObjectId()).execute();
                            } else {
                                stopSelf();
                            }
                        }
                    }
                });
        mSyncServiceModel.fetchVideoCourseListSync()
                .subscribe(new Consumer<VideoCourse>() {
                    @Override
                    public void accept(VideoCourse object) throws Exception {
                        if (!object.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            if (mSyncServiceModel.isDownloadAllowed()) {
                                JobCreator.createDownloadVideoCourseJob(object.getObjectId()).execute();
                            } else {
                                stopSelf();
                            }
                        }
                    }
                });
        mSyncServiceModel.fetchPopUpsListSync()
                .subscribe(new Consumer<PopUps>() {
                    @Override
                    public void accept(PopUps object) throws Exception {
                        if (!object.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                            if (mSyncServiceModel.isDownloadAllowed()) {
                                JobCreator.createDownloadPopUpsJob(object.getObjectId()).execute();
                            } else {
                                stopSelf();
                            }
                        }
                    }
                });
    }
//    private void handleActionUploadBlogComments() {
//        List<BaseUploadJob<BlogComment>> list6 = JobCreator.createBlogCommentJobList(mSyncServiceModel.fetchBlogCommentListNotSync());
//        Log.e("BlogComment Size : ", "" + list6.size());
//        for (BaseUploadJob<BlogComment> job : list6) {
//            job.execute();
//            removeJobFromMonitoringList(BlogComment.class, job.getDataObject().getAlias());
//        }
//    }

    private void handleActionUploadBlogComment(String alias) {
        BaseUploadJob<BlogComment> job = JobCreator.createBlogCommentJob(mSyncServiceModel.fetchBlogFromAlias(alias));
        removeJobFromMonitoringList(BlogComment.class, alias);
        job.execute();

    }

//    private void handleActionSyncPosts() {
//
//        for (PostData postData :
//                mSyncServiceModel.fetchPostDataListNotSync()) {
//            if (postData.getSyncStatus().equals(SyncStatus.JSON_SYNC.getStatus())) {
//                JobCreator.createPostDataValidationJob(postData).execute();
//            } else {
//                JobCreator.createPostLearningNetworkPostDataJob(postData).execute();
//            }
//        }
//        for (PostResponse postResponse :
//                mSyncServiceModel.fetchPostResponseListNotSync()) {
//            if (postResponse.getSyncStatus().equals(SyncStatus.JSON_SYNC.getStatus())) {
//                JobCreator.createPostResponseValidationJob(postResponse).execute();
//            } else {
//                JobCreator.createPostResponseJob(postResponse).execute();
//            }
//        }
//    }

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
            mSyncServiceModel.fetchInternalNotificationList().subscribe(new Consumer<InternalNotification>() {
                @Override
                public void accept(InternalNotification internalNotification) throws Exception {
                    try {
                        handleInternalNotification(internalNotification);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

//            if (BuildConfig.IS_ASSIGNMENT_ENABLED) {
//                mSyncServiceModel.fetchAssignmentListSync().subscribe(new Consumer<Assignment>() {
//                    @Override
//                    public void accept(Assignment assignment) throws Exception {
//                        if (assignment.getSyncStatus().equals(SyncStatus.JSON_SYNC.getStatus())) {
//                            try {
////                            if (mSyncServiceModel.isDownloadAllowed()) {
//                                JobCreator.createAssignmentValidationJob(assignment).execute();
////                            } else {
////                                stopSelf();
////                            }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else if (assignment.getSyncStatus().equals(SyncStatus.NOT_SYNC.getStatus())) {
//                            try {
////                            if (mSyncServiceModel.isDownloadAllowed()) {
//                                JobCreator.createPostAssignmentJob(assignment).execute();
////                            } else {
////                                stopSelf();
////                            }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                });
//
//                mSyncServiceModel.fetchAssignmentResponseListSync().subscribe(new Consumer<AssignmentResponse>() {
//                    @Override
//                    public void accept(AssignmentResponse assignmentResponse) throws Exception {
//                        if (assignmentResponse.getSyncStatus().equals(SyncStatus.JSON_SYNC.getStatus())) {
//                            try {
////                            if (mSyncServiceModel.isDownloadAllowed()) {
//                                JobCreator.createAssignmentResponseValidationJob(assignmentResponse).execute();
////                            } else {
////                                stopSelf();
////                            }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else if (assignmentResponse.getSyncStatus().equals(SyncStatus.NOT_SYNC.getStatus())) {
//                            try {
////                            if (mSyncServiceModel.isDownloadAllowed()) {
//                                JobCreator.createPostAssignmentResponseJob(assignmentResponse).execute();
////                            } else {
////                                stopSelf();
////                            }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                });
//
//            }

            startDownloadProcess();

//            if (BuildConfig.IS_COURSES_ENABLED) {
//                handleActionDownloadCourses();
//            }
            handleLastAction();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleLastAction() {
//        if (GeneralUtils.isNetworkAvailable(SyncService.this)) {
//            SyncServiceHelper.setCurrentUserProfile(SyncService.this);
//            SyncServiceHelper.updateProfile();
//        }
    }

    /**
     * start upload process
     */
    public void startUploadProcess() {
        handleActionUploadUserBrowseHistory();
        handleActionUploadWebQuiz();
        startUploadAndroidQuiz();

        if (BuildConfig.IS_LEARNING_NETWORK_ENABLED) {
            /*create upload AssignedBadges jobs*/
            mSyncServiceModel.fetchAssignedBadgesListNotSync().subscribe(new Consumer<AssignedBadges>() {
                @Override
                public void accept(AssignedBadges assignedBadges) throws Exception {
                    try {
                        if (GeneralUtils.isNetworkAvailable(SyncService.this))
                            JobCreator.createPostAssignedBadgeJob(assignedBadges).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        handleActionUploadBookAnnotation();
    }

    public void startUploadAndroidQuiz() {
        mSyncServiceModel.fetchQuizToSync().subscribe(new Consumer<Quiz>() {
            @Override
            public void accept(Quiz quiz) throws Exception {
                if (TextUtils.isEmpty(quiz.getObjectId()) && !TextUtils.isEmpty(quiz.getAlias()))
                    if (GeneralUtils.isNetworkAvailable(SyncService.this))
                        JobCreator.createUploadQuizJob(quiz).execute();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    //by rupsi
    private void startUploadUserBrowseHistoryLog() {
        mSyncServiceModel.fetchUserBrowseHistoryLogToSync().subscribe(new Consumer<UserBrowseHistory>() {
            @Override
            public void accept(UserBrowseHistory object) throws Exception {
                try {
                    if (GeneralUtils.isNetworkAvailable(SyncService.this)) {
                        object.setAlias(null);
                        Response<UserBrowseHistory> response = mNetworkModel.uploadUserBrowseHistoryLog(object).execute();
                        if (response != null && response.isSuccessful()) {
                            UserBrowseHistory userBrowseHistory = response.body();
                            // if (userBrowseHistory != null && !TextUtils.isEmpty(userBrowseHistory.getObjectId())) {
                            if (userBrowseHistory != null) {
                                if (!TextUtils.isEmpty(object.getDocId())) {
                                    mSyncServiceModel.deleteUserBrowseHistory(object.getDocId());

                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


//    private void startUploadCourseUserLog() {
//        mSyncServiceModel.fetchCourseUserLogToSync().subscribe(new Consumer<CourseAnalytics>() {
//            @Override
//            public void accept(CourseAnalytics object) throws Exception {
//                try {
//                    if (GeneralUtils.isNetworkAvailable(SyncService.this)) {
//                        object.setAlias(null);
//                        Response<CourseAnalytics> response = mNetworkModel.uploadCourseUserLog(object).execute();
//                        if (response != null && response.isSuccessful()) {
//                            CourseAnalytics courseAnalytics = response.body();
//                            if (courseAnalytics != null && !TextUtils.isEmpty(courseAnalytics.getObjectId())) {
//                                if (!TextUtils.isEmpty(object.getDocId())) {
//                                    mSyncServiceModel.deleteCourseAnalytics(object.getDocId());
//
//                                }
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    public void startUploadWebQuizResponse() {

        mSyncServiceModel.fetchWebQuizResponseToSync().subscribe(new Consumer<WebQuizResponse>() {
            @Override
            public void accept(WebQuizResponse object) throws Exception {
                try {
                    if (GeneralUtils.isNetworkAvailable(SyncService.this)) {
                        Response<ResponseBody> response = mWebPlayerLiveModel.uploadQuizResponse(object.getJson());
                        if (response.isSuccessful()) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.has("id")) {
                                object.setObjectId(jsonObject.getString("id"));
                                mWebPlayerLiveModel.saveQuizResponse(object);
                            } else if (jsonObject.has("_id")) {
                                object.setObjectId(jsonObject.getString("_id"));
                                mWebPlayerLiveModel.saveQuizResponse(object);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void startUploadWebAnnotation() {
        mSyncServiceModel.fetchAnnotationToSync().subscribe(new Consumer<BookAnnotation>() {
            @Override
            public void accept(BookAnnotation object) throws Exception {
                try {
                    if (GeneralUtils.isNetworkAvailable(SyncService.this)) {
                        object.setAlias(null);
                        JSONObject jsonObject = new JSONObject(object.getJson());
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        Response<ResponseBody> response = mNetworkModel.uploadAnnotation(body).execute();
                        if (response != null && response.isSuccessful()) {
                            object.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
                            mSyncServiceModel.saveAnnotation(object);

                            mSyncServiceModel.createInternalNotificationForBookUpdate(object.getCourseId(), ACTION_TYPE_NETWORK_DOWNLOAD, false);
                            if (!TextUtils.isEmpty(object.getDocId())) {
                                mSyncServiceModel.deleteAnnotation(object.getDocId());

                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * start download process
     */
    public void startDownloadProcess() {
        handleActionDownloadLearningMap();
        handleActionDownloadBroadcastNotification();

    }

    public void handleActionDownloadLearningMap() {
        if (BuildConfig.IS_LEARNING_MAP_ENABLED) {
            if (GeneralUtils.isNetworkAvailable(SyncService.this))
                JobCreator.createLearningMapDownloadJob().execute();
        }
    }


    public void handleActionDownloadBroadcastNotification() {
        if (BuildConfig.IS_TRAINING_ENABLED) {
            mSyncServiceModel.fetchTrainingNotificationListSync().subscribe(new Consumer<Notification>() {
                @Override
                public void accept(Notification notification) throws Exception {
                    try {

                        if (GeneralUtils.isNetworkAvailable(SyncService.this))
                            JobCreator.createTrainingDownloadJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    InjectorSyncAdapter.INSTANCE.getComponent().rxBus().send(new RefreshTrainingListEvent());
                }
            });

            mSyncServiceModel.fetchTrainingArchivalNotificationListSync().subscribe(new Consumer<Notification>() {
                @Override
                public void accept(Notification notification) throws Exception {
                    try {

                        if (GeneralUtils.isNetworkAvailable(SyncService.this))
                            JobCreator.createTrainingDownloadJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    InjectorSyncAdapter.INSTANCE.getComponent().rxBus().send(new RefreshTrainingListEvent());
                }
            });


            mSyncServiceModel.fetchTrainingListSync().subscribe(new Consumer<Training>() {
                @Override
                public void accept(Training item) throws Exception {
                    try {

                        String groupId = item.getGroupId();
                        if (!TextUtils.isEmpty(groupId)) {
                            Group group = mSyncServiceModel.fetchGroupById(groupId);
                            if (group == null || TextUtils.isEmpty(group.getObjectId()) ||
                                    (!group.getMemberUidList().contains(mAppUserModel.getObjectId())
                                            && !group.getModerators().contains(new Moderator(mAppUserModel.getObjectId(), "")))) {
                                mSyncServiceModel.deleteTraining(item.getDocId());
                                mSyncServiceModel.deleteSessions(item.getObjectId());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    InjectorSyncAdapter.INSTANCE.getComponent().rxBus().send(new RefreshTrainingListEvent());
                }
            });
        }
//        if (BuildConfig.IS_ASSIGNMENT_ENABLED) {
//            mSyncServiceModel.fetchAssignmentNotificationListSync().subscribe(new Consumer<Notification>() {
//                @Override
//                public void accept(Notification notification) throws Exception {
//                    try {
////                                if (mSyncServiceModel.isDownloadAllowed()) {
//                        if (GeneralUtils.isNetworkAvailable(SyncService.this))
//                            JobCreator.createDownloadAssignmentResponseJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
////                                } else {
////                                    stopSelf();
////                                }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//        if (BuildConfig.IS_COURSES_ENABLED) {
//            mSyncServiceModel.fetchDigitalBookNotificationListSync().subscribe(new Consumer<Notification>() {
//                @Override
//                public void accept(Notification notification) throws Exception {
//                    notification.setBroadcast(true);
//                    mSyncServiceModel.addRecommendedCourse(notification.getObjectInfo());
//                    try {
////                                if (mSyncServiceModel.isDownloadAllowed()) {
//                        if (GeneralUtils.isNetworkAvailable(SyncService.this))
//                            JobCreator.createDownloadAboutCourseDigitalBookJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(), notification.isBroadcast()).execute();
////                                } else {
////                                    stopSelf();
////                                }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            mSyncServiceModel.fetchVideoCourseNotificationListSync().subscribe(new Consumer<Notification>() {
//                @Override
//                public void accept(Notification notification) throws Exception {
//                    notification.setBroadcast(true);
//                    mSyncServiceModel.addRecommendedCourse(notification.getObjectInfo());
//                    try {
////                                if (mSyncServiceModel.isDownloadAllowed()) {
//                        if (GeneralUtils.isNetworkAvailable(SyncService.this))
//                            JobCreator.createDownloadAboutCourseVideoCourseJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(), notification.isBroadcast()).execute();
////                                } else {
////                                    stopSelf();
////                                }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }

        //// TODO: 09-Nov-17  disabled blogs auto download
//        if (BuildConfig.IS_BLOGS_ENABLED) {
//            mSyncServiceModel.fetchBlogNotificationListSync().subscribe(new Consumer<Notification>() {
//                @Override
//                public void accept(Notification notification) throws Exception {
//                    notification.setBroadcast(true);
//                    try {
//                        if (mSyncServiceModel.isDownloadAllowed()) {
//                            JobCreator.createDownloadBlogDetailsJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
//                        } else {
//                            stopSelf();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        if (mSyncServiceModel.isDownloadAllowed()) {
//                            JobCreator.createDownloadBlogCommentsJob(notification.getObjectInfo().getObjectId()).execute();
//                        } else {
//                            stopSelf();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }

//        mSyncServiceModel.fetchNotificationListSync().subscribe(new Consumer<Notification>() {
//            @Override
//            public void accept(Notification notification) throws Exception {
//
//                if (!notification.getSyncStatus().equalsIgnoreCase("Completed") && notification.getObjectInfo().getObjectId() != null && !notification.getObjectInfo().getObjectId().trim().isEmpty()) {
//                    if (BuildConfig.IS_ASSIGNMENT_ENABLED && notification.getObjectInfo().getType().equalsIgnoreCase(NotificationType.TYPE_ASSIGNMENT_RESPONSE.getNotificationType())) {
//                        try {
////                                if (mSyncServiceModel.isDownloadAllowed()) {
//                            JobCreator.createDownloadAssignmentResponseJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
////                                } else {
////                                    stopSelf();
////                                }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else if (BuildConfig.IS_COURSES_ENABLED && notification.getObjectInfo().getType().equalsIgnoreCase(NotificationType.TYPE_DIGITAL_BOOK.getNotificationType())) {
//                        notification.setBroadcast(true);
//                        mSyncServiceModel.addRecommendedCourse(notification.getObjectInfo());
//                        try {
////                                if (mSyncServiceModel.isDownloadAllowed()) {
//                            JobCreator.createDownloadAboutCourseDigitalBookJob(notification.getObjectInfo().getObjectId(), notification.getObjectId(), notification.isBroadcast()).execute();
////                                } else {
////                                    stopSelf();
////                                }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else if (BuildConfig.IS_BLOGS_ENABLED && notification.getObjectInfo().getType().equalsIgnoreCase(NotificationType.TYPE_BLOG.getNotificationType())) {
//                        notification.setBroadcast(true);
//                        try {
//                            if (mSyncServiceModel.isDownloadAllowed()) {
//                                JobCreator.createDownloadBlogDetailsJob(notification.getObjectInfo().getObjectId(), notification.getObjectId()).execute();
//                            } else {
//                                stopSelf();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            if (mSyncServiceModel.isDownloadAllowed()) {
//                                JobCreator.createDownloadBlogCommentsJob(notification.getObjectInfo().getObjectId()).execute();
//                            } else {
//                                stopSelf();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//
//            }
//        });
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

    public static void startActionUploadAssignmentResponse(Context context, String objectId) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPLOAD_ASSIGNMENT_RESPONSE);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        context.startService(intent);

    }

    public static void startDownloadAllTrainings(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_DOWNLOAD_ALL_TRAININGS);
        context.startService(intent);
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
