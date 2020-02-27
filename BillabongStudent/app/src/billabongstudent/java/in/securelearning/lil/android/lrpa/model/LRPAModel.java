package in.securelearning.lil.android.lrpa.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPARequest;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_COMPLETED;
import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_IN_PROGRESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_YET_TO_START;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_COMPLETED;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_IN_PROGRESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_YET_TO_START;

public class LRPAModel {

    @Inject
    NetworkModel mNetworkModel;

    @Inject
    Context mContext;

    @Inject
    GroupModel mGroupModel;

    public LRPAModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    public Group getGroupFromId(String groupId) {
        return mGroupModel.getGroupFromUidSync(groupId);
    }

    /*saving the group offline and starting network sync*/
    @SuppressLint("CheckResult")
    public void downloadAndSaveGroup(final String groupId) {
        Completable.complete()
                .subscribeOn(Schedulers.computation())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        downloadGroup(groupId);
                        downloadGroupPostAndResponse(groupId);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void downloadGroup(String groupId) {
        JobCreator.createDownloadGroupJob(groupId, ConstantUtil.GROUP_TYPE_NETWORK).execute();
    }

    private void downloadGroupPostAndResponse(String groupId) {
        JobCreator.createDownloadGroupPostAndResponseJob(groupId).execute();
    }

    /*get tab titles from string array according to group availability*/
    public ArrayList<String> getSubjectTabTitles(String groupId) {
        if (!TextUtils.isEmpty(groupId)) {
            return new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.array_subject_detail_tab)));
        } else {
            return new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.array_subject_detail_tab_no_post)));
        }
    }

    /*get status of chapter in string value*/
    public String getChapterStatus(String status) {
        switch (status) {
            case STATUS_IN_PROGRESS:
                return HEADER_IN_PROGRESS;
            case STATUS_YET_TO_START:
                return HEADER_YET_TO_START;
            case STATUS_COMPLETED:
                return HEADER_COMPLETED;
            default:
                return HEADER_IN_PROGRESS;
        }
    }

    /*To check current subject is English
     * and grade is below IV to show Freadom card in practice.*/
    public boolean isEnglishSubject(String subjectName, String gradeName) {
        if (subjectName.contains("Eng")) {
            return gradeName.equals("PG")
                    || gradeName.equals("EuroJunior")
                    || gradeName.equals("EuroSenior")
                    || gradeName.equals("EJ")
                    || gradeName.equals("ES")
                    || gradeName.equals("Nursery")
                    || gradeName.equals("I")
                    || gradeName.equals("II")
                    || gradeName.equals("III");
        } else {
            return false;
        }
    }

    /*To fetch details of subject*/
    public Observable<LessonPlanSubjectDetails> getSubjectDetails(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<LessonPlanSubjectDetails>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanSubjectDetails> e) throws Exception {

                Call<LessonPlanSubjectDetails> call = mNetworkModel.getSubjectDetails(subjectId);
                Response<LessonPlanSubjectDetails> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("SubjectDetails", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LessonPlanSubjectDetails> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("SubjectDetails", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                    } else {
                        Log.e("LessonPlanChapterResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanChapterResult", "Failed");
                    Log.e("SubjectDetails", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    public Observable<LRPAResult> fetchLRPA(final String topicId, final String type) {
        return Observable.create(new ObservableOnSubscribe<LRPAResult>() {
            @Override
            public void subscribe(ObservableEmitter<LRPAResult> e) throws Exception {
                LRPARequest lrpaRequest = new LRPARequest();
                lrpaRequest.setTopicId(topicId);
                lrpaRequest.setType(type);
                Call<LRPAResult> call = mNetworkModel.fetchLRPA(lrpaRequest);
                Response<LRPAResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LRPAResult", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LRPAResult> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LRPAResult", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                    } else {
                        Log.e("LRPAResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LRPAResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To get today recaps*/
    public Observable<LessonPlanChapterResult> getChapterResult(final LessonPlanChapterPost lessonPlanChapterPost) {
        return Observable.create(new ObservableOnSubscribe<LessonPlanChapterResult>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanChapterResult> e) throws Exception {
                Call<LessonPlanChapterResult> call = mNetworkModel.getChapterResult(lessonPlanChapterPost);
                Response<LessonPlanChapterResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanChapterResult", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LessonPlanChapterResult> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanChapterResult", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanSubject", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To fetch third party meta information*/
    public Observable<ArrayList<String>> fetchThirdPartyMapping(final ThirdPartyMapping thirdPartyMapping) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> e) throws Exception {

                Call<ArrayList<String>> call = mNetworkModel.fetchThirdPartyMapping(thirdPartyMapping);
                Response<ArrayList<String>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("SubjectDetails", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<String>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("SubjectDetails", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        Log.e("LessonPlanChapterResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanChapterResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanChapterResult", "Failed");
                    Log.e("SubjectDetails", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

}
