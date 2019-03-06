package in.securelearning.lil.android.courses.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.ObjectInfo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.UserRating;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AboutCourseModel;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.ConceptMapModel;
import in.securelearning.lil.android.base.model.DeleteObjectModel;
import in.securelearning.lil.android.base.model.DigitalBookModel;
import in.securelearning.lil.android.base.model.DownloadedCourseModel;
import in.securelearning.lil.android.base.model.FavouriteCourseModel;
import in.securelearning.lil.android.base.model.InteractiveImageModel;
import in.securelearning.lil.android.base.model.InteractiveVideoModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.PopUpsModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.model.RecommendedCourseModel;
import in.securelearning.lil.android.base.model.UserRatingCourseModel;
import in.securelearning.lil.android.base.model.VideoCourseModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.courses.InjectorCourses;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.events.CourseDeleteEvent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.courses.views.activity.CourseDetailActivity.DATA_CM;
import static in.securelearning.lil.android.courses.views.activity.CourseDetailActivity.DATA_DB;
import static in.securelearning.lil.android.courses.views.activity.CourseDetailActivity.DATA_II;
import static in.securelearning.lil.android.courses.views.activity.CourseDetailActivity.DATA_IV;
import static in.securelearning.lil.android.courses.views.activity.CourseDetailActivity.DATA_PU;
import static in.securelearning.lil.android.courses.views.activity.CourseDetailActivity.DATA_VC;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_COURSE_DELETE;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_COURSE_FAVORITE_FALSE;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_COURSE_FAVORITE_TRUE;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_COURSE_REVIEW_ADD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_MICRO_COURSE_FAVORITE_FALSE;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_MICRO_COURSE_FAVORITE_TRUE;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_NETWORK_DOWNLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_ABOUT_COURSE;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_CONCEPT_MAP;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_DIGITAL_BOOK;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_INTERACTIVE_IMAGE;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_INTERACTIVE_VIDEO;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_POP_UP;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_VIDEO_COURSE;

/**
 * Created by Prabodh Dhabaria on 19-11-2016.
 */

public class CoursesModel {
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    Context mContext;

    @Inject
    NetworkModel mNetworkModel;

    @Inject
    DigitalBookModel mDigitalBookModel;

    @Inject
    ConceptMapModel mConceptMapModel;

    @Inject
    InteractiveImageModel mInteractiveImageModel;

    @Inject
    PopUpsModel mPopUpsModel;

    @Inject
    QuizModel mQuizModel;

    @Inject
    VideoCourseModel mVideoCourseModel;
    @Inject
    InteractiveVideoModel mInteractiveVideoModel;
    @Inject
    AboutCourseModel mAboutCourseModel;
    @Inject
    RecommendedCourseModel mRecommendedCourseModel;
    @Inject
    InternalNotificationModel mInternalNotificationModel;

    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;

    @Inject
    UserRatingCourseModel mUserRatingCourseModel;

    @Inject
    FavouriteCourseModel mFavouriteCourseModel;

    @Inject
    DownloadedCourseModel mDownloadedCourseModel;

    @Inject
    DeleteObjectModel mDeleteObjectModel;

    public CoursesModel() {
        InjectorCourses.INSTANCE.getComponent().inject(this);
    }

    public AboutCourse getAboutCourseFromDatabase(String objectId) {
        return mAboutCourseModel.getObjectById(objectId);
    }

    /**
     * get all courses
     *
     * @return list
     */
    public Observable<Course> getCoursesList() {
        Observable<Course> observable = Observable.create(new ObservableOnSubscribe<Course>() {
            @Override
            public void subscribe(ObservableEmitter<Course> subscriber) {
                for (Course c :
                        mDigitalBookModel.getCompleteSyncStatusList()) {
                    AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
                    c = updateCourse(c, aboutCourse);
                    subscriber.onNext(c);
                }
                for (Course c :
                        mConceptMapModel.getCompleteSyncStatusList()) {
                    AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
                    c = updateCourse(c, aboutCourse);
                    subscriber.onNext(c);
                }
                for (Course c :
                        mPopUpsModel.getCompleteSyncStatusList()) {
                    AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
                    c = updateCourse(c, aboutCourse);
                    subscriber.onNext(c);
                }
                for (Course c :
                        mVideoCourseModel.getCompleteSyncStatusList()) {
                    AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
                    c = updateCourse(c, aboutCourse);
                    subscriber.onNext(c);
                }
                for (Course c :
                        mInteractiveVideoModel.getCompleteSyncStatusList()) {
                    AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
                    c = updateCourse(c, aboutCourse);
                    subscriber.onNext(c);
                }
                for (Course c :
                        mInteractiveImageModel.getCompleteSyncStatusList()) {
                    AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
                    c = updateCourse(c, aboutCourse);
                    subscriber.onNext(c);
                }
                subscriber.onComplete();
            }
        });

        return observable;

    }

    public Observable<AboutCourse> getFavoritesFromDatabase() {
        Observable<AboutCourse> observable = Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(ObservableEmitter<AboutCourse> subscriber) {

                final String id = mAppUserModel.getObjectId();
                for (AboutCourse c :
                        mAboutCourseModel.getCompleteSyncStatusList()) {
                    if (c.getReviews().getFavouriteCourses().contains(id) || c.getFavoriteCourses().contains(id)) {
                        subscriber.onNext(c);
                    }
                }
                subscriber.onComplete();
            }
        });

        return observable;

    }

    private Course updateCourse(Course c, AboutCourse a) {
        c.setAvgRating(a.getReviews().getAvgRating());
        c.getReviews().setAvgRating(a.getReviews().getAvgRating());
        c.getReviews().setTotalViews(a.getReviews().getTotalViews());
        c.getReviews().setViews(a.getReviews().getViews());
        return c;
    }

    public Observable<AboutCourse> getRecommendedCoursesList() {
        Observable<AboutCourse> observable = Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(ObservableEmitter<AboutCourse> subscriber) {
                try {
                    for (ObjectInfo object :
                            mRecommendedCourseModel.getCompleteList()) {

                        AboutCourse aboutCourse = mAboutCourseModel.getObjectById(object.getObjectId());

                        if (aboutCourse.getObjectId().equals(object.getObjectId())) {
                            Course course = null;
                            if (aboutCourse.getCourseType().equalsIgnoreCase("digitalbook")) {
                                course = getFromDatabaseDigitalBook(object.getObjectId());
                            } else if (aboutCourse.getCourseType().equalsIgnoreCase("videocourse")) {
                                course = getFromDatabaseVideoCourse(object.getObjectId());
                            } else if (aboutCourse.getMicroCourseType().toLowerCase().contains("map")) {
                                course = getFromDatabaseConceptMap(object.getObjectId());
                            } else if (aboutCourse.getMicroCourseType().toLowerCase().contains("interactiveimage")) {
                                course = getFromDatabaseInteractiveImage(object.getObjectId());
                            } else if (aboutCourse.getMicroCourseType().toLowerCase().contains("pop")) {
                                course = getFromDatabasePopUps(object.getObjectId());
                            } else if (aboutCourse.getMicroCourseType().toLowerCase().contains("interactivevideo")) {
                                course = getFromDatabaseInteractiveVideo(object.getObjectId());
                            }


                            if (course != null && course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                subscriber.onNext(aboutCourse);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                subscriber.onComplete();
            }
        });

        return observable;

    }

    public boolean checkIsBeingDeleted(String docId) {
        if (!TextUtils.isEmpty(docId)) {
            try {
                InternalNotification internalNotification = mDatabaseQueryHelper.retrieveNotification(docId, InternalNotification.class);
                if (internalNotification != null && internalNotification.getObjectAction() == ACTION_TYPE_COURSE_DELETE) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public void downloadDigitalBook(final AboutCourse aboutCourse, final String objectId) {
        DigitalBook object = new DigitalBook();
        object.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        object.setObjectId(objectId);
        mDigitalBookModel.saveObject(object);
        InternalNotification internalNotification = createInternalNotificationForDigitalBook(object, aboutCourse.getCourseType(), ACTION_TYPE_NETWORK_DOWNLOAD);

        if (aboutCourse != null) {
            aboutCourse.setSyncStatus(SyncStatus.JSON_SYNC.toString());
            aboutCourse.setInternalNotificationDocId(internalNotification.getDocId());
            if (TextUtils.isEmpty(aboutCourse.getJson())) {
                aboutCourse.setJson(GeneralUtils.toGson(aboutCourse));
            }
            mAboutCourseModel.saveObject(aboutCourse);
        }
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());

    }

    public void downloadVideoCourse(final AboutCourse aboutCourse, final String objectId) {
        VideoCourse object = new VideoCourse();
        object.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        object.setObjectId(objectId);
        mVideoCourseModel.saveObject(object);
        InternalNotification internalNotification = createInternalNotificationForVideoCourse(object, aboutCourse.getCourseType(), ACTION_TYPE_NETWORK_DOWNLOAD);

        if (aboutCourse != null) {
            aboutCourse.setSyncStatus(SyncStatus.JSON_SYNC.toString());
            aboutCourse.setInternalNotificationDocId(internalNotification.getDocId());
            mAboutCourseModel.saveObject(aboutCourse);
        }
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }

    public void downloadPopUp(final AboutCourse aboutCourse, final String objectId) {
        PopUps object = new PopUps();
        object.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        object.setObjectId(objectId);
        mPopUpsModel.saveObject(object);
        InternalNotification internalNotification = createInternalNotificationForPopUp(object, aboutCourse.getMicroCourseType(), ACTION_TYPE_NETWORK_DOWNLOAD);

        if (aboutCourse != null) {
            aboutCourse.setSyncStatus(SyncStatus.JSON_SYNC.toString());
            aboutCourse.setInternalNotificationDocId(internalNotification.getDocId());
            if (TextUtils.isEmpty(aboutCourse.getJson())) {
                aboutCourse.setJson(GeneralUtils.toGson(aboutCourse));
            }
            mAboutCourseModel.saveObject(aboutCourse);
        }
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }

    public void downloadConceptMap(final AboutCourse aboutCourse, final String objectId) {

        ConceptMap object = new ConceptMap();
        object.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        object.setObjectId(objectId);
        mConceptMapModel.saveObject(object);
        InternalNotification internalNotification = createInternalNotificationForConceptMap(object, aboutCourse.getMicroCourseType(), ACTION_TYPE_NETWORK_DOWNLOAD);

        if (aboutCourse != null) {
            aboutCourse.setSyncStatus(SyncStatus.JSON_SYNC.toString());
            aboutCourse.setInternalNotificationDocId(internalNotification.getDocId());
            if (TextUtils.isEmpty(aboutCourse.getJson())) {
                aboutCourse.setJson(GeneralUtils.toGson(aboutCourse));
            }
            mAboutCourseModel.saveObject(aboutCourse);
        }
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());

    }

    public void downloadInteractiveImage(final AboutCourse aboutCourse, final String objectId) {

        InteractiveImage object = new InteractiveImage();
        object.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        object.setObjectId(objectId);
        mInteractiveImageModel.saveObject(object);
        InternalNotification internalNotification = createInternalNotificationForInteractiveImage(object, aboutCourse.getMicroCourseType(), ACTION_TYPE_NETWORK_DOWNLOAD);

        if (aboutCourse != null) {
            aboutCourse.setSyncStatus(SyncStatus.JSON_SYNC.toString());
            aboutCourse.setInternalNotificationDocId(internalNotification.getDocId());
            if (TextUtils.isEmpty(aboutCourse.getJson())) {
                aboutCourse.setJson(GeneralUtils.toGson(aboutCourse));
            }
            mAboutCourseModel.saveObject(aboutCourse);
        }
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());

    }

    public void downloadInteractiveVideo(final AboutCourse aboutCourse, final String objectId) {

        InteractiveVideo object = new InteractiveVideo();
        object.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        object.setObjectId(objectId);
        mInteractiveVideoModel.saveObject(object);
        InternalNotification internalNotification = createInternalNotificationForInteractiveVideo(object, aboutCourse.getMicroCourseType(), ACTION_TYPE_NETWORK_DOWNLOAD);

        if (aboutCourse != null) {
            aboutCourse.setSyncStatus(SyncStatus.JSON_SYNC.toString());
            aboutCourse.setInternalNotificationDocId(internalNotification.getDocId());
            if (TextUtils.isEmpty(aboutCourse.getJson())) {
                aboutCourse.setJson(GeneralUtils.toGson(aboutCourse));
            }
            mAboutCourseModel.saveObject(aboutCourse);
        }
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());

    }

    public Quiz getQuiz(String id) {
        return mQuizModel.getQuizFromUidSync(id);
    }

    public ConceptMap getConceptMap(String id) {
        return mConceptMapModel.getConceptMapFromUidSync(id);
    }

    public InteractiveImage getInteractiveImage(String id) {
        return mInteractiveImageModel.getInteractiveImageFromUidSync(id);
    }

    public PopUps getPopUps(String id) {
        return mPopUpsModel.getPopUpsFromUidSync(id);
    }

    public VideoCourse getVideoCourse(String id) {
        return mVideoCourseModel.getObjectById(id);
    }

    public InteractiveVideo getInteractiveVideo(String id) {
        return mInteractiveVideoModel.getObjectById(id);
    }

    /**
     * get all micro courses
     *
     * @return list
     */
    public List<Object> getMicroCoursesList() {
        List<Object> list = new ArrayList<>();
//        list.addAll(mDigitalBookModel.getDigitalBookList());
        list.addAll(mConceptMapModel.getConceptMapList());
        list.addAll(mPopUpsModel.getPopUpsList());
        list.addAll(mInteractiveImageModel.getInteractiveImageList());
        list.addAll(mVideoCourseModel.getCompleteList());
        list.addAll(mInteractiveVideoModel.getCompleteList());

        return list;
    }

    /**
     * get all the referenced jsons in the digital book
     *
     * @param digitalBook
     * @return hashmap <id , json>
     */
    public HashMap<String, String> getResources(DigitalBook digitalBook) {
        HashMap<String, String> map = new HashMap<>();

        for (int i = 0; i < digitalBook.getPopUpsList().size(); i++) {
            String id = digitalBook.getPopUpsList().get(i);

            map.put(id, mPopUpsModel.getPopUpsFromUidSync(id).getJson());

        }

        for (int i = 0; i < digitalBook.getConceptMapList().size(); i++) {
            String id = digitalBook.getConceptMapList().get(i);

            map.put(id, mConceptMapModel.getConceptMapFromUidSync(id).getJson());

        }

        for (int i = 0; i < digitalBook.getInteractiveImageList().size(); i++) {
            String id = digitalBook.getInteractiveImageList().get(i);

            map.put(id, mInteractiveImageModel.getInteractiveImageFromUidSync(id).getJson());

        }
        for (int i = 0; i < digitalBook.getQuizList().size(); i++) {
            String id = digitalBook.getQuizList().get(i);

            map.put(id, new Gson().toJson(mQuizModel.getQuizFromUidSync(id)));

        }

        return map;
    }

    public void increaseShareCount(final String courseType, final String id) {
        try {
            Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                @Override
                public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                    Call<ResponseBody> call = null;
                    if (courseType.equals(DATA_DB)) {
                        call = mNetworkModel.increaseDigitalBookShareCount(id);
                    } else if (courseType.equals(DATA_CM)) {
                        call = mNetworkModel.increaseConceptMapShareCount(id);
                    } else if (courseType.equals(DATA_II)) {
                        call = mNetworkModel.increaseInteractiveImageShareCount(id);
                    } else if (courseType.equals(DATA_PU)) {
                        call = mNetworkModel.increasePopupShareCount(id);
                    } else if (courseType.equals(DATA_VC)) {
                        call = mNetworkModel.increaseVideoCourseShareCount(id);
                    } else if (courseType.equals(DATA_IV)) {
                        call = mNetworkModel.increaseInteractiveVideoShareCount(id);
                    }
                    Response<ResponseBody> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        Log.e("shareCountIncrease--", "Success");
                        e.onNext(response.body());
                    } else if (response.code() == 404) {
                        Log.e("shareCountIncrease--", "Failed 404");
                        throw new Exception("Failed 404");
                    } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                        Response<ResponseBody> response2 = call.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            Log.e("shareCountIncrease--", "Success");
                            e.onNext(response2.body());
                        } else if ((response2.code() == 401)) {
                            Log.e("shareCountIncrease--", "Failed 401");
                            mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                        } else if (response2.code() == 404) {
                            Log.e("shareCountIncrease--", "Failed 404");
                            throw new Exception("Failed 404");
                        } else {
                            Log.e("shareCountIncrease--", "Failed");
                            throw new Exception("Failed");
                        }
                    } else {
                        Log.e("shareCountIncrease--", "Failed");
                        throw new Exception("Failed");
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody body) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public AboutCourse getDigitalBookAbout(String id) {
        try {
            Call<ResponseBody> call = mNetworkModel.getDigitalBookAboutResponseBody(id);
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                final String json = response.body().string();
                AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                aboutCourse.setJson(json);
                return aboutCourse;

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        final String json = response.body().string();
                        AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                        aboutCourse.setJson(json);
                        return aboutCourse;
                    }
                } else {
                    return null;
//                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public AboutCourse getInteractiveImageAbout(String id) {
        try {
            Call<ResponseBody> call = mNetworkModel.getInteractiveImageAboutResponseBody(id);
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                final String json = response.body().string();
                AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                aboutCourse.setJson(json);
                return aboutCourse;

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        final String json = response.body().string();
                        AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                        aboutCourse.setJson(json);
                        return aboutCourse;
                    }
                } else {
                    return null;
//                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public AboutCourse getConceptMapAbout(String id) {
        try {
            Call<ResponseBody> call = mNetworkModel.getConceptMapAboutResponseBody(id);
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                final String json = response.body().string();
                AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                aboutCourse.setJson(json);
                return aboutCourse;

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        final String json = response.body().string();
                        AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                        aboutCourse.setJson(json);
                        return aboutCourse;
                    }
                } else {
                    return null;
//                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public AboutCourse getPopUpsAbout(String id) {
        try {
            Call<ResponseBody> call = mNetworkModel.getPopUpsAboutResponseBody(id);
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                final String json = response.body().string();
                AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                aboutCourse.setJson(json);
                return aboutCourse;

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        final String json = response.body().string();
                        AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                        aboutCourse.setJson(json);
                        return aboutCourse;
                    }
                } else {
                    return null;
//                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public AboutCourse getVideoCourseAbout(String id) {
        try {
            Call<ResponseBody> call = mNetworkModel.getVideoCourseAboutResponseBody(id);
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                final String json = response.body().string();
                AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                aboutCourse.setJson(json);
                return aboutCourse;

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        AboutCourse aboutCourse = GeneralUtils.fromGson(response.body().string(), AboutCourse.class);
                        aboutCourse.setJson(response.body().string());
                        return aboutCourse;
                    }
                } else {
                    return null;
//                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public AboutCourse getInteractiveVideoAbout(String id) {
        try {
            Call<ResponseBody> call = mNetworkModel.getInteractiveVideoAboutResponseBody(id);
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                final String json = response.body().string();
                AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                aboutCourse.setJson(json);
                return aboutCourse;

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        final String json = response.body().string();
                        AboutCourse aboutCourse = GeneralUtils.fromGson(json, AboutCourse.class);
                        aboutCourse.setJson(json);
                        return aboutCourse;
                    }
                } else {
                    return null;
//                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public DigitalBook getFromDatabaseDigitalBook(String objectId) {
        return mDigitalBookModel.getObjectById(objectId);
    }

    public PopUps getFromDatabasePopUps(String objectId) {
        return mPopUpsModel.getObjectById(objectId);
    }

    public ConceptMap getFromDatabaseConceptMap(String objectId) {
        return mConceptMapModel.getObjectById(objectId);
    }

    public InteractiveImage getFromDatabaseInteractiveImage(String objectId) {
        return mInteractiveImageModel.getObjectById(objectId);
    }

    public VideoCourse getFromDatabaseVideoCourse(String objectId) {
        return mVideoCourseModel.getObjectById(objectId);
    }

    public InteractiveVideo getFromDatabaseInteractiveVideo(String objectId) {
        return mInteractiveVideoModel.getObjectById(objectId);
    }

    public boolean isCourseFavorite(AboutCourse aboutCourse) {
        return aboutCourse.getReviews().getFavouriteCourses().contains(mAppUserModel.getObjectId());
    }

    public AboutCourse saveAbout(AboutCourse aboutCourse) {
        return mAboutCourseModel.saveObject(aboutCourse);
    }

    public Observable<Boolean> addAboutFavorite(final AboutCourse item) {
        return
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        createInternalNotificationForCourse(item, ACTION_TYPE_COURSE_FAVORITE_TRUE);
                        e.onNext(true);
                        e.onComplete();
                    }
                });

    }

    public Observable<Boolean> addMicroAboutFavorite(final AboutCourse item) {
        return
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        createInternalNotificationForCourse(item, ACTION_TYPE_MICRO_COURSE_FAVORITE_TRUE);
                        e.onNext(true);
                        e.onComplete();
                    }
                });

    }

    public Observable<Boolean> removeAboutFavorite(final AboutCourse item) {
        return
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        createInternalNotificationForCourse(item, ACTION_TYPE_COURSE_FAVORITE_FALSE);
                        e.onNext(true);
                        e.onComplete();
                    }
                });

    }

    public Observable<Boolean> removeMicroAboutFavorite(final AboutCourse item) {
        return
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        createInternalNotificationForCourse(item, ACTION_TYPE_MICRO_COURSE_FAVORITE_FALSE);
                        e.onNext(true);
                        e.onComplete();
                    }
                });

    }

    /**
     * create internal notification to upload favorite, rating and reviews
     *
     * @param aboutCourse
     * @param action
     */
    private void createInternalNotificationForCourse(AboutCourse aboutCourse, int action) {

        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, aboutCourse.getObjectId());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(setCourseType(aboutCourse));
            internalNotification.setObjectDocId(aboutCourse.getDocId());
            internalNotification.setObjectId(aboutCourse.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_ABOUT_COURSE);
            internalNotification.setTitle(aboutCourse.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }

    /**
     * create internal notification for download digital book
     *
     * @param digitalBook
     * @param courseType
     * @param action
     */
    private InternalNotification createInternalNotificationForDigitalBook(DigitalBook digitalBook, String courseType, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, digitalBook.getObjectId());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(courseType);
            internalNotification.setObjectDocId(digitalBook.getDocId());
            internalNotification.setObjectId(digitalBook.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_DIGITAL_BOOK);
            internalNotification.setTitle(digitalBook.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);

        return internalNotification;
    }

    /**
     * create internal notification for download video course
     *
     * @param videoCourse
     * @param courseType
     * @param action
     */
    private InternalNotification createInternalNotificationForVideoCourse(VideoCourse videoCourse, String courseType, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, videoCourse.getObjectId());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(courseType);
            internalNotification.setObjectDocId(videoCourse.getDocId());
            internalNotification.setObjectId(videoCourse.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_VIDEO_COURSE);
            internalNotification.setTitle(videoCourse.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        return internalNotification;

    }

    /**
     * create internal notification for download interactive image
     *
     * @param interactiveImage
     * @param courseType
     * @param action
     */
    private InternalNotification createInternalNotificationForInteractiveImage(InteractiveImage interactiveImage, String courseType, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, interactiveImage.getObjectId());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(courseType);
            internalNotification.setObjectDocId(interactiveImage.getDocId());
            internalNotification.setObjectId(interactiveImage.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_INTERACTIVE_IMAGE);
            internalNotification.setTitle(interactiveImage.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        return internalNotification;
    }

    /**
     * create internal notification for download interactive video
     *
     * @param interactiveVideo
     * @param courseType
     * @param action
     */
    private InternalNotification createInternalNotificationForInteractiveVideo(InteractiveVideo interactiveVideo, String courseType, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, interactiveVideo.getObjectId());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(courseType);
            internalNotification.setObjectDocId(interactiveVideo.getDocId());
            internalNotification.setObjectId(interactiveVideo.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_INTERACTIVE_VIDEO);
            internalNotification.setTitle(interactiveVideo.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);

        return internalNotification;
    }

    /**
     * create internal notification for download Popup
     *
     * @param popUps
     * @param courseType
     * @param action
     */
    private InternalNotification createInternalNotificationForPopUp(PopUps popUps, String courseType, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, popUps.getObjectId());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(courseType);
            internalNotification.setObjectDocId(popUps.getDocId());
            internalNotification.setObjectId(popUps.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_POP_UP);
            internalNotification.setTitle(popUps.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        return internalNotification;

    }

    /**
     * create internal notification for download concept map
     *
     * @param conceptMap
     * @param courseType
     * @param action
     */
    private InternalNotification createInternalNotificationForConceptMap(ConceptMap conceptMap, String courseType, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, conceptMap.getObjectId());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(courseType);
            internalNotification.setObjectDocId(conceptMap.getDocId());
            internalNotification.setObjectId(conceptMap.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_CONCEPT_MAP);
            internalNotification.setTitle(conceptMap.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);

        return internalNotification;
    }

    private String setCourseType(AboutCourse aboutCourse) {
        if (!aboutCourse.getCourseType().isEmpty()) {
            return aboutCourse.getCourseType();
        } else {
            return aboutCourse.getMicroCourseType();
        }
    }

    public boolean isCourseDownloading(String id, Class aClass) {

        return SyncService.checkJobStatus(aClass, id);
    }


    /**
     * get course type and create internal notification for deletion.
     *
     * @param objectId
     * @param type
     */
    public boolean deleteCourse(String objectId, String type) {
        Course course = null;
        if (type.equals(DATA_DB)) {
            course = getFromDatabaseDigitalBook(objectId);
        } else if (type.equals(DATA_CM)) {
            course = getFromDatabaseConceptMap(objectId);
        } else if (type.equals(DATA_II)) {
            course = getFromDatabaseInteractiveImage(objectId);
        } else if (type.equals(DATA_PU)) {
            course = getFromDatabasePopUps(objectId);
        } else if (type.equals(DATA_VC)) {
            course = getFromDatabaseVideoCourse(objectId);
        } else if (type.equals(DATA_IV)) {
            course = getFromDatabaseInteractiveVideo(objectId);
        }
        if (mDeleteObjectModel.deleteCourse(mContext, objectId, course.getDocId())) {
            //mSyncServiceModel.deleteFromDownloadedCourse(internalNotification.getObjectId());
            // mSyncServiceModel.purgeInternalNotification(internalNotification.getDocId());
            InjectorSyncAdapter.INSTANCE.getComponent().rxBus().send(new CourseDeleteEvent(objectId));
            return true;
        }

        return false;
        //createInternalNotificationForCourseDelete(course, ACTION_TYPE_COURSE_DELETE);
    }

    /**
     * create internal notification for course deletion
     *
     * @param course
     * @param action
     */
    private void createInternalNotificationForCourseDelete(Course course, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, course.getObjectId());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType("");
            internalNotification.setObjectDocId(course.getDocId());
            internalNotification.setObjectId(course.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_ABOUT_COURSE);
            internalNotification.setTitle(course.getTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }

    // Below code after online and offline sync for userRating and favourate in course detail

    public void saveUserRatingCourse(Float rating, String review, String courseId, String courseType, String title) {
        UserRating userRating = mUserRatingCourseModel.getUserRatingFromUidSync(courseId);
        userRating.setRating(rating);
        userRating.setUserId(mAppUserModel.getObjectId());
        userRating.setName(mAppUserModel.getApplicationUser().getName());
        userRating.setComment(review);
        userRating.setObjectId(courseId);
        userRating.setDate(DateUtils.getCurrentISO8601DateString());
        userRating = mUserRatingCourseModel.saveObject(userRating);
        createInternalNotificationForCourse(userRating, ACTION_TYPE_COURSE_REVIEW_ADD, courseType, title);
    }

    private void createInternalNotificationForCourse(UserRating userRating, int action, String courseType, String title) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, userRating.getObjectId());
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(courseType);
            internalNotification.setObjectDocId(userRating.getDocId());
            internalNotification.setObjectId(userRating.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_ABOUT_COURSE);
            internalNotification.setTitle(title);
        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }


    public UserRating getUserRating(String courseUid) {
        return mUserRatingCourseModel.getUserRatingFromUidSync(courseUid);
    }


    public UserRating saveUserRatingCourse(UserRating userRating) {
        return mUserRatingCourseModel.saveUserRating(userRating);
    }

    public ArrayList<AboutCourse> getCompleteListOfFavoriteCourse() {
        return mFavouriteCourseModel.getCompleteList();
    }

    public AboutCourse saveFavouriteCourse(AboutCourse favouriteCourse) {
        return mFavouriteCourseModel.saveFavouriteCourse(favouriteCourse);
    }

    public boolean deleteFavourite(String docId) {
        return mFavouriteCourseModel.delete(docId);
    }

    public boolean deleteDownloaded(String objectId, String type) {
        return deleteCourse(objectId, type);
        //return mFavouriteCourseModel.delete(docId);
    }

    public AboutCourse getFavouriteCourse(String uid) {
        return mFavouriteCourseModel.getObjectById(uid);
    }

    public ArrayList<AboutCourse> getCompleteListOfFavoriteCourse(int skip, int limit) {
        return mFavouriteCourseModel.getCompleteListOfFavoriteCourse(skip, limit);
    }

    public ArrayList<AboutCourse> getCompleteListOfDownloadedCourse(int skip, int limit) {
        return mDownloadedCourseModel.getCompleteListOfDownloadedCourse(skip, limit);
    }

    public AboutCourse saveAboutCourseToDownloaded(AboutCourse aboutCourse) {
        return mDownloadedCourseModel.saveObject(aboutCourse);
    }
}
