package in.securelearning.lil.android.player.microlearning.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.CourseProgress;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.model.CourseProgressModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.MicroLearningCourseModel;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.microlearning.InjectorPlayer;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_COURSE_PROGRESS_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_COURSE_PROGRESS;

/**
 * Created by Chaitendra on 20-Feb-18.
 */

public class PlayerModel {

    @Inject
    MicroLearningCourseModel mMicroLearningCourseModel;

    @Inject
    CourseProgressModel mCourseProgressModel;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    @Inject
    NetworkModel mNetworkModel;

    @Inject
    Context mContext;

    public PlayerModel() {
        InjectorPlayer.INSTANCE.getComponent().inject(this);
    }

    public Observable<MicroLearningCourse> getMicroLearningCourse(String id) {
        MicroLearningCourse microLearningCourse = mMicroLearningCourseModel.getObjectById(id);
        if (microLearningCourse != null && !TextUtils.isEmpty(microLearningCourse.getObjectId()) && microLearningCourse.getObjectId().equals(id)) {
            return getMicroLearningCourseOffline(id);
        } else {
            return getMicroLearningCourseOnline(id);
        }
    }

    public Observable<MicroLearningCourse> getMicroLearningCourseOffline(final String id) {

        return
                Observable.create(new ObservableOnSubscribe<MicroLearningCourse>() {
                    @Override
                    public void subscribe(ObservableEmitter<MicroLearningCourse> subscriber) {
                        MicroLearningCourse microLearningCourse = mMicroLearningCourseModel.getObjectById(id);
                        subscriber.onNext(microLearningCourse);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<MicroLearningCourse> getMicroLearningCourseOnline(final String id) {

        return Observable.create(new ObservableOnSubscribe<MicroLearningCourse>() {
            @Override
            public void subscribe(ObservableEmitter<MicroLearningCourse> e) throws Exception {
                Call<MicroLearningCourse> call = mNetworkModel.getMicroLearningCourse(id);
                Response<MicroLearningCourse> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    MicroLearningCourse microLearningCourse = response.body();
                    Log.e("MicroLearningCourse1--", "Successful");
                    e.onNext(microLearningCourse);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageCourseDetailNotFound));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<MicroLearningCourse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        MicroLearningCourse microLearningCourse = response2.body();
                        Log.e("MicroLearningCourse2--", "Successful");
                        e.onNext(microLearningCourse);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageCourseDetailNotFound));
                    } else {
                        Log.e("MicroLearningCourse2--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageCourseDetailNotFound));
                    }
                } else {
                    Log.e("MicroLearningCourse1--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageCourseDetailNotFound));
                }
                e.onComplete();
            }
        });
    }

    public Observable<ArrayList<MicroLearningCourse>> getMicroLearningCourseList() {

        return Observable.create(new ObservableOnSubscribe<ArrayList<MicroLearningCourse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MicroLearningCourse>> e) throws Exception {
                Call<ArrayList<MicroLearningCourse>> call = mNetworkModel.getMicroLearningCourseList();
                Response<ArrayList<MicroLearningCourse>> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    ArrayList<MicroLearningCourse> list = response.body();
                    Log.e("MicroLearningCourse1--", "Successful");
                    e.onNext(list);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageFeaturedCardsUnableToNotFound));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<MicroLearningCourse>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        ArrayList<MicroLearningCourse> list = response2.body();
                        Log.e("MicroLearningCourse2--", "Successful");
                        e.onNext(list);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageFeaturedCardsUnableToNotFound));
                    } else {
                        Log.e("MicroLearningCourse2--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageFeaturedCardsNotFound));
                    }
                } else {
                    Log.e("MicroLearningCourse1--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageFeaturedCardsNotFound));
                }
                e.onComplete();
            }
        });
    }

    public void saveCourseProgress(CourseProgress courseProgress, boolean createNotification) {
        courseProgress = mCourseProgressModel.saveObject(courseProgress);
        if (createNotification)
            createInternalNotificationForCourseProgress(courseProgress, ACTION_TYPE_COURSE_PROGRESS_UPLOAD);
    }

    public CourseProgress getCourseProgress(String id) {
        return mCourseProgressModel.getObjectById(id);
    }

    public void createInternalNotificationForCourseProgress(CourseProgress courseProgress, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, courseProgress.getObjectId());
        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType("CourseProgress");
            internalNotification.setObjectDocId(courseProgress.getDocId());
            internalNotification.setObjectId(courseProgress.getObjectId());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_COURSE_PROGRESS);
            internalNotification.setTitle(courseProgress.getObjectId());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        SyncService.startActionFetchInternalNotification(mContext, internalNotification.getDocId());
    }

}
