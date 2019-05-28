package in.securelearning.lil.android.home.model;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProgressBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPARequest;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectPost;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectResult;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Response;

public class FlavorHomeModel {

    @Inject
    FlavorNetworkModel mFlavorNetworkModel;

    @Inject
    Context mContext;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    GroupModel mGroupModel;

    public FlavorHomeModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    /*Set activity status bar style immersive*/
    public void setImmersiveStatusBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /*An alert dialog, which contains progress text*/
    public Dialog loadingDialog(Context context, String message) {
        LayoutProgressBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_progress, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(binding.getRoot());
        binding.textViewLoading.setText(message);

        dialog.show();
        return dialog;
    }

    /*To get today recaps*/
    public Observable<ArrayList<LessonPlanMinimal>> getTodayRecaps() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<LessonPlanMinimal>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<LessonPlanMinimal>> e) throws Exception {
                Call<ArrayList<LessonPlanMinimal>> call = mFlavorNetworkModel.getTodayRecap();
                Response<ArrayList<LessonPlanMinimal>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanMinimal", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<LessonPlanMinimal>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanMinimal", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanMinimal", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanMinimal", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }


    public Observable<ArrayList<LessonPlanChapterResult>> getChaptersResult() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<LessonPlanChapterResult>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<LessonPlanChapterResult>> e) throws Exception {
                Call<ArrayList<LessonPlanChapterResult>> call = mFlavorNetworkModel.getChaptersResult();
                Response<ArrayList<LessonPlanChapterResult>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanChapterResult", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<LessonPlanChapterResult>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanChapterResult", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanChapterResult", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("LessonPlanChapterResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To get subjects of logged in user*/
    public Observable<LessonPlanSubjectResult> getMySubject() {
        return Observable.create(new ObservableOnSubscribe<LessonPlanSubjectResult>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanSubjectResult> e) throws Exception {

                Call<LessonPlanSubjectResult> call = mFlavorNetworkModel.getMySubject(new LessonPlanSubjectPost(mContext.getString(R.string.subject).toLowerCase()));
                Response<LessonPlanSubjectResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LessonPlanSubject", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<LessonPlanSubjectResult> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("LessonPlanSubject", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("LessonPlanSubject", "Failed");
                        Log.e("LessonPlanSubject", "Failed");
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

    /*To get today recaps*/
    public Observable<LessonPlanChapterResult> getChapterResult(final LessonPlanChapterPost lessonPlanChapterPost) {
        return Observable.create(new ObservableOnSubscribe<LessonPlanChapterResult>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanChapterResult> e) throws Exception {
                Call<LessonPlanChapterResult> call = mFlavorNetworkModel.getChapterResult(lessonPlanChapterPost);
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

    /*To fetch details of subject*/
    public Observable<LessonPlanSubjectDetails> getSubjectDetails(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<LessonPlanSubjectDetails>() {
            @Override
            public void subscribe(ObservableEmitter<LessonPlanSubjectDetails> e) throws Exception {

                Call<LessonPlanSubjectDetails> call = mFlavorNetworkModel.getSubjectDetails(subjectId);
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

    /*To fetch third party meta information*/
    public Observable<ArrayList<String>> fetchThirdPartyMapping(final String subjectId, final String topicId) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> e) throws Exception {

                Call<ArrayList<String>> call = mFlavorNetworkModel.fetchThirdPartyMapping(new ThirdPartyMapping(subjectId, topicId));
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
                Call<LRPAResult> call = mFlavorNetworkModel.fetchLRPA(lrpaRequest);
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

    public Class getCourseClass(AboutCourseMinimal course) {
        if (!TextUtils.isEmpty(course.getCourseType())) {
            String courseType = course.getCourseType();
            if (courseType.equalsIgnoreCase("digitalbook")) {
                return DigitalBook.class;
            } else if (courseType.equalsIgnoreCase("videocourse")) {
                return VideoCourse.class;
            } else if (courseType.contains("feature")) {
                return MicroLearningCourse.class;
            } else if (courseType.contains("map")) {
                return ConceptMap.class;
            } else if (courseType.contains("interactiveim")) {
                return InteractiveImage.class;
            } else if (courseType.contains("interactivevi")) {
                return InteractiveVideo.class;
            } else {
                if (course.getPopUpType() != null && !TextUtils.isEmpty(course.getPopUpType().getValue())) {
                    return PopUps.class;
                }
            }

        }
        return DigitalBook.class;
    }

    public String getCourseType(AboutCourseMinimal course) {
        if (!TextUtils.isEmpty(course.getCourseType())) {
            String courseType = course.getCourseType();
            if (courseType.equalsIgnoreCase("digitalbook")) {
                return "Digital Book";
            } else if (courseType.equalsIgnoreCase("videocourse")) {
                return "Video Course";
            } else if (courseType.contains("feature")) {
                return "Recap";
            } else if (courseType.contains("map")) {
                return "Concept Map";
            } else if (courseType.contains("interactiveim")) {
                return "Interactive Image";
            } else if (courseType.contains("interactivevi")) {
                return "Interactive Video";
            } else {
                if (course.getPopUpType() != null && !TextUtils.isEmpty(course.getPopUpType().getValue())) {
                    return "Pop Up";
                }
            }
        }
        return "";
    }

    public Group getGroupFromId(String groupId) {
        return mGroupModel.getGroupFromUidSync(groupId);
    }

    public void downloadGroup(String groupId) {
        JobCreator.createDownloadGroupJob(groupId).execute();
    }

    public void downloadGroupPostAndResponse(String groupId) {
        JobCreator.createDownloadGroupPostNResponseJob(groupId).execute();
    }

    /*To fetch student's achievements*/
    public Observable<StudentAchievement> fetchStudentAchievements() {
        return Observable.create(new ObservableOnSubscribe<StudentAchievement>() {
            @Override
            public void subscribe(ObservableEmitter<StudentAchievement> e) throws Exception {
                Call<StudentAchievement> call = mFlavorNetworkModel.fetchStudentAchievements();
                Response<StudentAchievement> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("LRPAResult", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<StudentAchievement> response2 = call.clone().execute();
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


    /*alert dialog to show any error, message*/
    /*Context should be activity context only.*/
    public void showAlertDialog(@NonNull Context context, String message) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })

                .setCancelable(false);
        final android.app.AlertDialog alert = builder.create();
        alert.show();

    }
}
