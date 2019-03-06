package in.securelearning.lil.android.courses.models;

import android.content.Context;

import com.couchbase.lite.util.Log;
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
import in.securelearning.lil.android.base.dataobjects.ObjectInfo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AboutCourseModel;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.ConceptMapModel;
import in.securelearning.lil.android.base.model.DigitalBookModel;
import in.securelearning.lil.android.base.model.InteractiveImageModel;
import in.securelearning.lil.android.base.model.InteractiveVideoModel;
import in.securelearning.lil.android.base.model.PopUpsModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.model.RecommendedCourseModel;
import in.securelearning.lil.android.base.model.VideoCourseModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.courses.InjectorCourses;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.dataobject.RecommendedApiObject;
import in.securelearning.lil.android.syncadapter.dataobject.SearchFilterId;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResults;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Pushkar Raj on 18-11-2016.
 */

public class ItemListModel {
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    Context mContext;

    @Inject
    NetworkModel mNetworkModel;

    @Inject
    DigitalBookModel mDigitalBookModel;

    @Inject
    InteractiveVideoModel mInteractiveVideoModel;

    @Inject
    ConceptMapModel mConceptMapModel;

    @Inject
    InteractiveImageModel mInteractiveImageModel;

    @Inject
    PopUpsModel mPopUpsModel;

    @Inject
    QuizModel mQuizModel;
    @Inject
    RecommendedCourseModel mRecommendedCourseModel;
    @Inject
    VideoCourseModel mVideoCourseModel;
    @Inject
    AboutCourseModel mAboutCourseModel;

    public ItemListModel() {
        InjectorCourses.INSTANCE.getComponent().inject(this);
    }

    public AboutCourse getAboutCourseFromDatabase(String objectId) {
        return mAboutCourseModel.getObjectById(objectId);
    }

    /**
     * get all courses
     * right now fetching only digital book and video course
     *
     * @return list
     */
    public Observable<Course> getCoursesList() {
        Observable<Course> observable = Observable.create(new ObservableOnSubscribe<Course>() {
            @Override
            public void subscribe(final ObservableEmitter<Course> subscriber) {
                subscriber.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        Log.e("CD", "dispose");
                        Thread.currentThread().interrupt();
                        subscriber.onComplete();
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });
                for (Course c : mDigitalBookModel.getCompleteSyncStatusList()) {
                    if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                        AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
                        c = updateCourse(c, aboutCourse);
                        subscriber.onNext(c);
                    } else {
                        break;
                    }
                }

//                for (Course c :
//                        mVideoCourseModel.getCompleteSyncStatusList()) {
//                    if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
//                        AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
//                        c = updateCourse(c, aboutCourse);
//                        subscriber.onNext(c);
//                    } else {
//                        break;
//                    }
//                }
//                for (Course c :
//                        mConceptMapModel.getCompleteSyncStatusList()) {
//                    if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
//                        AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
//                        c = updateCourse(c, aboutCourse);
//                        subscriber.onNext(c);
//                    } else {
//                        break;
//                    }
//                }
//                for (Course c :
//                        mPopUpsModel.getCompleteSyncStatusList()) {
//                    if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
//                        AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
//                        c = updateCourse(c, aboutCourse);
//                        subscriber.onNext(c);
//                    } else {
//                        break;
//                    }
//                }

//                for (Course c :
//                        mInteractiveImageModel.getCompleteSyncStatusList()) {
//                    if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
//                        AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
//                        c = updateCourse(c, aboutCourse);
//                        subscriber.onNext(c);
//                    } else {
//                        break;
//                    }
//                }
//                for (Course c :
//                        mInteractiveVideoModel.getCompleteSyncStatusList()) {
//                    if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
//                        AboutCourse aboutCourse = mAboutCourseModel.getObjectById(c.getObjectId());
//                        c = updateCourse(c, aboutCourse);
//                        subscriber.onNext(c);
//                    } else {
//                        break;
//                    }
//                }
                subscriber.onComplete();
            }
        });

        return observable;

    }

    public Observable<AboutCourse> getFavoritesFromDatabase() {
        Observable<AboutCourse> observable = Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(final ObservableEmitter<AboutCourse> subscriber) {
                subscriber.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        Log.e("FD", "dispose");
                        Thread.currentThread().interrupt();
                        subscriber.onComplete();
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });
                final String id = mAppUserModel.getObjectId();
                if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                    for (AboutCourse c :
                            mAboutCourseModel.getCompleteSyncStatusList()) {
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            if (c.getReviews().getFavouriteCourses().contains(id) || c.getFavoriteCourses().contains(id)) {
                                subscriber.onNext(c);
                            }
                        } else {
                            break;
                        }
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
            public void subscribe(final ObservableEmitter<AboutCourse> subscriber) {
                subscriber.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        Log.e("RD", "dispose");
                        Thread.currentThread().interrupt();
                        subscriber.onComplete();
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });
                try {
                    Course course = null;
                    AboutCourse aboutCourse = null;
                    for (ObjectInfo object :
                            mRecommendedCourseModel.getCompleteList()) {
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            aboutCourse = mAboutCourseModel.getObjectById(object.getObjectId());

                            if (aboutCourse.getObjectId().equals(object.getObjectId())) {

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


                                if (course != null && !course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                    subscriber.onNext(aboutCourse);
                                }
                            }
                        } else {
                            break;
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

    public Observable<AboutCourse> getRecommendedCoursesListOnline() {
        Observable<AboutCourse> observable = Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(final ObservableEmitter<AboutCourse> subscriber) {
                subscriber.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        Log.e("RO", "dispose");
                        Thread.currentThread().interrupt();
                        subscriber.onComplete();
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    ArrayList<RecommendedApiObject> list = null;
                    Course course = null;
                    try {
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {

                            Call<ArrayList<RecommendedApiObject>> call = mNetworkModel.getRecommendedApiObjectRecommendedDigitalBookOnline(10, 0);
                            Response<ArrayList<RecommendedApiObject>> response = call.execute();
                            if (response.isSuccessful()) {
                                list = response.body();
                            } else if (response.code() == 401 || response.code() == 403) {
                                if (SyncServiceHelper.refreshToken(mContext)) {
                                    Call<ArrayList<RecommendedApiObject>> call2 = call.clone();
                                    response = call2.execute();
                                    if (response.isSuccessful()) {
                                        list = response.body();
                                    }
                                }
                            }
                        }
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (AboutCourse object :
                                            list.get(i).getAboutCourses()) {
                                        if (object.getCourseType().equalsIgnoreCase("digitalbook")) {
                                            course = getFromDatabaseDigitalBook(object.getObjectId());
                                        }


                                        if (course != null && !course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                            subscriber.onNext(object);
                                        }
                                    }

                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            Call<ArrayList<RecommendedApiObject>> call = mNetworkModel.getRecommendedApiObjectRecommendedVideoCourseOnline(10, 0);
                            Response<ArrayList<RecommendedApiObject>> response = call.execute();
                            if (response.isSuccessful()) {
                                list = response.body();
                            } else if (response.code() == 401 || response.code() == 403) {
                                if (SyncServiceHelper.refreshToken(mContext)) {
                                    Call<ArrayList<RecommendedApiObject>> call2 = call.clone();
                                    response = call2.execute();
                                    if (response.isSuccessful()) {
                                        list = response.body();
                                    }
                                }
                            }
                        }
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (AboutCourse object :
                                            list.get(i).getAboutCourses()) {
                                        if (object.getCourseType().equalsIgnoreCase("videocourse")) {
                                            course = getFromDatabaseVideoCourse(object.getObjectId());
                                        }


                                        if (course != null && !course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                            subscriber.onNext(object);
                                        }
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            Call<ArrayList<RecommendedApiObject>> call = mNetworkModel.getRecommendedApiObjectRecommendedConceptMapOnline(10, 0);
                            Response<ArrayList<RecommendedApiObject>> response = call.execute();
                            if (response.isSuccessful()) {
                                list = response.body();
                            } else if (response.code() == 401 || response.code() == 403) {
                                if (SyncServiceHelper.refreshToken(mContext)) {
                                    Call<ArrayList<RecommendedApiObject>> call2 = call.clone();
                                    response = call2.execute();
                                    if (response.isSuccessful()) {
                                        list = response.body();
                                    }
                                }
                            }
                        }
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (AboutCourse object :
                                            list.get(i).getAboutCourses()) {
                                        if (object.getMicroCourseType().toLowerCase().contains("map")) {
                                            course = getFromDatabaseConceptMap(object.getObjectId());
                                        }


                                        if (course != null && !course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                            subscriber.onNext(object);
                                        }
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            Call<java.util.ArrayList<RecommendedApiObject>> call = mNetworkModel.getRecommendedApiObjectRecommendedInteractiveImageOnline(10, 0);
                            Response<ArrayList<RecommendedApiObject>> response = call.execute();
                            if (response.isSuccessful()) {
                                list = response.body();
                            } else if (response.code() == 401 || response.code() == 403) {
                                if (SyncServiceHelper.refreshToken(mContext)) {
                                    Call<ArrayList<RecommendedApiObject>> call2 = call.clone();
                                    response = call2.execute();
                                    if (response.isSuccessful()) {
                                        list = response.body();
                                    }
                                }
                            }
                        }
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (AboutCourse object :
                                            list.get(i).getAboutCourses()) {
                                        if (object.getMicroCourseType().toLowerCase().contains("interactiveimage")) {
                                            course = getFromDatabaseInteractiveImage(object.getObjectId());
                                        }

                                        if (course != null && !course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                            subscriber.onNext(object);
                                        }
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            Call<ArrayList<RecommendedApiObject>> call = mNetworkModel.getRecommendedApiObjectRecommendedPopUpOnline(10, 0);
                            Response<ArrayList<RecommendedApiObject>> response = call.execute();
                            if (response.isSuccessful()) {
                                list = response.body();
                            } else if (response.code() == 401 || response.code() == 403) {
                                if (SyncServiceHelper.refreshToken(mContext)) {
                                    Call<ArrayList<RecommendedApiObject>> call2 = call.clone();
                                    response = call2.execute();
                                    if (response.isSuccessful()) {
                                        list = response.body();
                                    }
                                }
                            }
                        }
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (AboutCourse object :
                                            list.get(i).getAboutCourses()) {
                                        if (object.getMicroCourseType().toLowerCase().contains("pop")) {
                                            course = getFromDatabasePopUps(object.getObjectId());
                                        }


                                        if (course != null && !course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                            subscriber.onNext(object);
                                        }
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            Call<ArrayList<RecommendedApiObject>> call = mNetworkModel.getRecommendedApiObjectRecommendedInteractiveVideoOnline(10, 0);
                            Response<ArrayList<RecommendedApiObject>> response = call.execute();
                            if (response.isSuccessful()) {
                                list = response.body();
                            } else if (response.code() == 401 || response.code() == 403) {
                                if (SyncServiceHelper.refreshToken(mContext)) {
                                    Call<ArrayList<RecommendedApiObject>> call2 = call.clone();
                                    response = call2.execute();
                                    if (response.isSuccessful()) {
                                        list = response.body();
                                    }
                                }
                            }
                        }
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (AboutCourse object :
                                            list.get(i).getAboutCourses()) {
                                        if (object.getMicroCourseType().toLowerCase().contains("interactivevideo")) {
                                            course = getFromDatabaseInteractiveVideo(object.getObjectId());
                                        }


                                        if (course != null && !course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                            subscriber.onNext(object);
                                        }
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onComplete();
            }
        });

        return observable;

    }

    public Observable<AboutCourse> getRecommendedCoursesListOnlineEs() {
        Observable<AboutCourse> observable = Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(final ObservableEmitter<AboutCourse> subscriber) {
                subscriber.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        Log.e("RO", "dispose");
                        Thread.currentThread().interrupt();
                        subscriber.onComplete();
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });
                final String associationId = mAppUserModel.getApplicationUser().getAssociation().getId();
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    ArrayList<AboutCourseExt> list = null;
                    Course course = null;
                    try {
                        ArrayList<SearchFilterId> subjects = new ArrayList<>();
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted() && mAppUserModel.getApplicationUser().getInterest().getLearningLevel().size() > 0) {

                            Call<SearchResults> call = mNetworkModel.getFilterParamsEs(associationId, mAppUserModel.getApplicationUser().getInterest().getLearningLevel());
                            Response<SearchResults> response = call.execute();
                            if (response.isSuccessful()) {
                                subjects = response.body().getSubjectsList();
                            } else if (response.code() == 401 || response.code() == 403) {
                                if (SyncServiceHelper.refreshToken(mContext)) {
                                    Call<SearchResults> call2 = call.clone();
                                    response = call2.execute();
                                    if (response.isSuccessful()) {
                                        subjects = response.body().getSubjectsList();
                                    }
                                }
                            }
                        }
                        if (subjects == null || subjects.size() <= 0) {
                            subjects = PrefManager.getSubjectsNameAndId(mContext);
                        }
                        if (subjects != null) {

                            for (SearchFilterId subject :
                                    subjects) {
                                if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {

                                    Call<SearchResults> call = mNetworkModel.searchForCourseOnlineBySubjectEs(subject.getId(), mAppUserModel.getApplicationUser().getAssociation().getId());
                                    Response<SearchResults> response = call.execute();
                                    if (response.isSuccessful()) {
                                        list = response.body().getList();
                                    } else if (response.code() == 401 || response.code() == 403) {
                                        if (SyncServiceHelper.refreshToken(mContext)) {
                                            Call<SearchResults> call2 = call.clone();
                                            response = call2.execute();
                                            if (response.isSuccessful()) {
                                                list = response.body().getList();
                                            }
                                        }
                                    }
                                }
                                if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                                    if (list != null) {
                                        for (int i = 0; i < list.size(); i++) {
                                            for (AboutCourseExt object :
                                                    list) {
                                                if (object.getCourseType().equalsIgnoreCase("digitalbook")) {
                                                    course = getFromDatabaseDigitalBook(object.getObjectId());
                                                } else if (object.getCourseType().equalsIgnoreCase("videocourse")) {
                                                    course = getFromDatabaseVideoCourse(object.getObjectId());
                                                } else if (object.getMicroCourseType().toLowerCase().contains("interactivevideo")) {
                                                    course = getFromDatabaseInteractiveVideo(object.getObjectId());
                                                } else if (object.getMicroCourseType().toLowerCase().contains("interactiveimage")) {
                                                    course = getFromDatabaseInteractiveImage(object.getObjectId());
                                                } else if (object.getMicroCourseType().toLowerCase().contains("map")) {
                                                    course = getFromDatabaseConceptMap(object.getObjectId());
                                                } else {
//                                                if (object.getMicroCourseType().toLowerCase().contains("pop")) {
                                                    course = getFromDatabasePopUps(object.getObjectId());
//                                                }
                                                }

                                                if (course != null && !course.getSyncStatus().equals(SyncStatus.COMPLETE_SYNC.toString())) {
                                                    object.getReviews().setTotalViews(object.getTotalViews());
                                                    object.getReviews().setAvgRating(object.getAvgRating());
                                                    subscriber.onNext(object);
                                                }
                                            }

                                        }

                                    }
                                }

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onComplete();
            }
        });

        return observable;

    }

    public Observable<AboutCourse> getFavoriteCoursesList() {
        Observable<AboutCourse> observable = Observable.create(new ObservableOnSubscribe<AboutCourse>() {
            @Override
            public void subscribe(final ObservableEmitter<AboutCourse> subscriber) {
                subscriber.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        Log.e("FCO", "dispose");
                        Thread.currentThread().interrupt();
                        subscriber.onComplete();
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    try {
                        ArrayList<RecommendedApiObject> list = new ArrayList<>();
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            Call<java.util.ArrayList<RecommendedApiObject>> call = mNetworkModel.getFavoriteCourseOnline(20, 0);
                            Response<java.util.ArrayList<RecommendedApiObject>> response = call.execute();
                            if (response.isSuccessful()) {
                                list = response.body();
                            } else if (response.code() == 401 || response.code() == 403) {
                                if (SyncServiceHelper.refreshToken(mContext)) {
                                    Call<ArrayList<RecommendedApiObject>> call2 = call.clone();
                                    response = call2.execute();
                                    if (response.isSuccessful()) {
                                        list = response.body();
                                    }
                                }
                            }
                        }
                        if (!subscriber.isDisposed() && !Thread.currentThread().isInterrupted()) {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (AboutCourse object :
                                            list.get(i).getAboutCourses()) {
                                        subscriber.onNext(object);
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onComplete();
            }
        });

        return observable;

    }

    public void downloadDigitalBook(final String objectId) {
        SyncService.startActionDownloadDigitalBook(mContext, objectId);
    }

    public void downloadPopUp(final String objectId) {
        SyncService.startActionDownloadPopUp(mContext, objectId);
    }

    public void downloadConceptMap(final String objectId) {
        SyncService.startActionDownloadConceptMap(mContext, objectId);
    }

    public void downloadInteractiveImage(final String objectId) {
        SyncService.startActionDownloadInteractiveImage(mContext, objectId);
    }

    public void downloadVideoCourse(final String objectId) {
        SyncService.startActionDownloadVideoCourse(mContext, objectId);
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

    /**
     * get all micro courses
     *
     * @return list
     */
    public List<Object> getMicroCoursesList() {
        List<Object> list = new java.util.ArrayList<>();
//        list.addAll(mDigitalBookModel.getDigitalBookList());
        list.addAll(mConceptMapModel.getConceptMapList());
        list.addAll(mPopUpsModel.getPopUpsList());
        list.addAll(mInteractiveImageModel.getInteractiveImageList());
        list.addAll(mVideoCourseModel.getCompleteList());

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

    public AboutCourse getDigitalBookAbout(String id) {
        try {
            Call<AboutCourse> call = mNetworkModel.getDigitalBookAbout(id);
            Response<AboutCourse> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                return response.body();

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        return response.body();
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
            Call<AboutCourse> call = mNetworkModel.getInteractiveImageAbout(id);
            Response<AboutCourse> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                return response.body();

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        return response.body();
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
            Call<AboutCourse> call = mNetworkModel.getConceptMapAbout(id);
            Response<AboutCourse> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                return response.body();

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        return response.body();
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
            Call<AboutCourse> call = mNetworkModel.getPopUpsAbout(id);
            Response<AboutCourse> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                return response.body();

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        return response.body();
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
            Call<AboutCourse> call = mNetworkModel.getVideoCourseAbout(id);
            Response<AboutCourse> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                return response.body();

            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        return response.body();
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

    public InteractiveVideo getFromDatabaseInteractiveVideo(String objectId) {
        return mInteractiveVideoModel.getObjectById(objectId);
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

    public boolean isCourseFavorite(AboutCourse aboutCourse) {
        return aboutCourse.getReviews().getFavouriteCourses().contains(mAppUserModel.getObjectId());
    }

    public void saveAbout(AboutCourse aboutCourse) {
        mAboutCourseModel.saveObject(aboutCourse);
    }

    public void removeAboutFavorite(AboutCourse item) {
        if (item.getReviews().getFavouriteCourses().contains(mAppUserModel.getObjectId())) {
            item.getReviews().getFavouriteCourses().remove(mAppUserModel.getObjectId());
            saveAbout(item);
        }
    }

    public void addAboutFavorite(AboutCourse item) {
        if (!item.getReviews().getFavouriteCourses().contains(mAppUserModel.getObjectId())) {
            item.getReviews().getFavouriteCourses().add(mAppUserModel.getObjectId());
            saveAbout(item);
        }
    }

    public boolean isCourseDownloading(String id, Class aClass) {

        return SyncService.checkJobStatus(aClass, id);
    }
}
