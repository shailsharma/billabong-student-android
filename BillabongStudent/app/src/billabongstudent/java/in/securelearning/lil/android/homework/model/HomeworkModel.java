package in.securelearning.lil.android.homework.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.homework.dataobject.AssignedHomeworkParent;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.dataobject.HomeworkDetail;
import in.securelearning.lil.android.homework.dataobject.HomeworkSubmitResponse;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

public class HomeworkModel {

    @Inject
    Context mContext;

    @Inject
    FlavorNetworkModel mFlavorNetworkModel;

    public HomeworkModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }


    public Observable<AssignedHomeworkParent> fetchHomework(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<AssignedHomeworkParent>() {
            @Override
            public void subscribe(ObservableEmitter<AssignedHomeworkParent> e) throws Exception {

                Call<AssignedHomeworkParent> call = mFlavorNetworkModel.fetchHomework(subjectId);
                Response<AssignedHomeworkParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("fetchHomework", "Successful");
                    AssignedHomeworkParent assignedHomeworkParent = combineNewTodayUpcomingList(response.body());
                    e.onNext(assignedHomeworkParent);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<AssignedHomeworkParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("fetchHomework", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("fetchHomework", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    public Observable<AssignedHomeworkParent> fetchSubmittedHomework(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<AssignedHomeworkParent>() {
            @Override
            public void subscribe(ObservableEmitter<AssignedHomeworkParent> e) throws Exception {

                Call<AssignedHomeworkParent> call = mFlavorNetworkModel.fetchHomework(subjectId);
                Response<AssignedHomeworkParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("fetchHomework", "Successful");
                    AssignedHomeworkParent assignedHomeworkParent = combineNewTodayUpcomingList(response.body());
                    e.onNext(assignedHomeworkParent);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<AssignedHomeworkParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("fetchHomework", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("fetchHomework", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    public Observable<Homework> fetchHomeworkDetail(final String homeworkId) {
        return Observable.create(new ObservableOnSubscribe<Homework>() {
            @Override
            public void subscribe(ObservableEmitter<Homework> e) throws Exception {

                Call<Homework> call = mFlavorNetworkModel.fetchHomeworkDetail(homeworkId);
                Response<Homework> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("fetchHomework", "Successful");

                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<Homework> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("fetchHomework", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("fetchHomework", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    public Observable<HomeworkSubmitResponse> submitHomework(final String homeworkId) {
        return Observable.create(new ObservableOnSubscribe<HomeworkSubmitResponse>() {
            @Override
            public void subscribe(ObservableEmitter<HomeworkSubmitResponse> e) throws Exception {

                Call<HomeworkSubmitResponse> call = mFlavorNetworkModel.submitHomework(homeworkId);
                Response<HomeworkSubmitResponse> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("fetchHomework", "Successful");

                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<HomeworkSubmitResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("fetchHomework", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("fetchHomework", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    //For Assign homework neeed to combine list
    private AssignedHomeworkParent combineNewTodayUpcomingList(AssignedHomeworkParent assignedHomeworkParent) {
        if (assignedHomeworkParent != null) {
            ArrayList<Homework> pendingAssignmentSet = new ArrayList<>();
            AssignedHomeworkParent.AssignedHomework newAssignmentList = assignedHomeworkParent.getNewStudentAssignment();
            if (newAssignmentList != null && newAssignmentList.getCount() > 0) {
                for (Homework newHomework : newAssignmentList.getAssignmentsList()) {
                    newHomework.setHomeworkType(ConstantUtil.NEW);
                }
                pendingAssignmentSet.addAll(newAssignmentList.getAssignmentsList());
            }

            AssignedHomeworkParent.AssignedHomework todayAssignmentList = assignedHomeworkParent.getTodayStudentAssignment();
            if (todayAssignmentList != null && todayAssignmentList.getCount() > 0) {
                for (Homework todayHomework : todayAssignmentList.getAssignmentsList()) {
                    todayHomework.setHomeworkType(ConstantUtil.TODAY);
                }
                pendingAssignmentSet.addAll(todayAssignmentList.getAssignmentsList());
            }

            AssignedHomeworkParent.AssignedHomework upComingAssignmentList = assignedHomeworkParent.getUpComingStudentAssignment();
            if (upComingAssignmentList != null && upComingAssignmentList.getCount() > 0) {
                for (Homework upcomingHomework : upComingAssignmentList.getAssignmentsList()) {
                    upcomingHomework.setHomeworkType(ConstantUtil.UPCOMING);
                }
                pendingAssignmentSet.addAll(upComingAssignmentList.getAssignmentsList());
            }

            /*clearing repeated value from list*/
            List<Homework> pendingAssignmentList = new ArrayList<>(pendingAssignmentSet);
            Set<Homework> homeworkSet = new LinkedHashSet<>(pendingAssignmentList);
            pendingAssignmentList.clear();
            pendingAssignmentList.addAll(homeworkSet);

            assignedHomeworkParent.setPendingAssignmentList(pendingAssignmentList);
            return assignedHomeworkParent;

        }
        return null;
    }
}
