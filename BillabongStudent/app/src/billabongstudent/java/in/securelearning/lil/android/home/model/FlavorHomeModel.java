package in.securelearning.lil.android.home.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.EffortvsPerformanceData;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.homework.dataobject.AssignedHomeworkParent;
import in.securelearning.lil.android.homework.dataobject.Homework;
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
import in.securelearning.lil.android.syncadapter.dataobjects.LogiQidsChallengeParent;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfilePicturePost;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.dataobjects.UserChallengePost;
import in.securelearning.lil.android.syncadapter.dataobjects.VideoForDayParent;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopic;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopicRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
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

    /*To play video*/
    public void playVideo(Resource resource) {

        String type = resource.getType();

        String url = "";
        if (!TextUtils.isEmpty(resource.getUrl())) {
            url = resource.getUrl();
        } else if (!TextUtils.isEmpty(resource.getUrlMain())) {
            url = resource.getUrlMain();
        } else if (!TextUtils.isEmpty(resource.getSourceURL())) {
            url = resource.getSourceURL();
        }
        if (TextUtils.isEmpty(type)) {
            if (url.contains(mContext.getString(R.string.typeVimeoVideo))) {
                type = mContext.getString(R.string.typeVimeoVideo);
            } else if (url.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")) {
                type = mContext.getString(R.string.typeYouTubeVideo);
            } else if (url.contains(mContext.getString(R.string.typeVideo))) {
                type = mContext.getString(R.string.typeVideo);
            } else if (url.contains("youtu.be") || url.contains("youtube.com")) {
                type = mContext.getString(R.string.typeYouTubeVideo);
            } else {
                if (url.contains(mContext.getString(R.string.typeVimeoVideo))) {
                    type = mContext.getString(R.string.typeVimeoVideo);
                } else if (url.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")) {
                    type = mContext.getString(R.string.typeYouTubeVideo);
                } else if (url.contains(mContext.getString(R.string.typeVideo))) {
                    type = mContext.getString(R.string.typeVideo);
                } else if (url.contains("youtu.be") || url.contains("youtube.com")) {
                    type = mContext.getString(R.string.typeYouTubeVideo);
                } else {
                    type = mContext.getString(R.string.typeVideo);
                }

            }
        }

        if (type.equalsIgnoreCase(mContext.getString(R.string.typeVideo))) {
            Resource item = new Resource();
            item.setType(mContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeYouTubeVideo))) {
            if (!url.contains("https:") && !url.startsWith("www")) {
                FavouriteResource favouriteResource = new FavouriteResource();
                favouriteResource.setName(url);
                favouriteResource.setUrlThumbnail("");
                mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, favouriteResource, false));
            } else {
                String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

                Pattern compiledPattern = Pattern.compile(pattern);
                Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
                if (matcher.find()) {
                    String videoId = matcher.group();
                    FavouriteResource favouriteResource = new FavouriteResource();
                    favouriteResource.setName(videoId);
                    favouriteResource.setUrlThumbnail("");
                    mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, favouriteResource, false));
                }
            }


        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeVimeoVideo))) {
            mContext.startActivity(PlayVimeoFullScreenActivity.getStartIntent(mContext, url));
        } else {
            Resource item = new Resource();
            item.setType(mContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        }
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
                        e.onNext(response2.body());
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
                        e.onNext(response2.body());
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
    public Observable<ArrayList<String>> fetchThirdPartyMapping(final ThirdPartyMapping thirdPartyMapping) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> e) throws Exception {

                Call<ArrayList<String>> call = mFlavorNetworkModel.fetchThirdPartyMapping(thirdPartyMapping);
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
        JobCreator.createDownloadGroupJob(groupId, ConstantUtil.GROUP_TYPE_NETWORK).execute();
    }

    public void downloadGroupPostAndResponse(String groupId) {
        JobCreator.createDownloadGroupPostAndResponseJob(groupId).execute();
    }


    public Observable<AssignedHomeworkParent> fetchHomeworkCount(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<AssignedHomeworkParent>() {
            @Override
            public void subscribe(ObservableEmitter<AssignedHomeworkParent> e) throws Exception {

                Call<AssignedHomeworkParent> call = mFlavorNetworkModel.fetchHomework(subjectId);
                Response<AssignedHomeworkParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("fetchHomework", "Successful");
                    //AssignedHomeworkParent assignedHomeworkParent = combineNewTodayUpcomingList(response.body());
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<AssignedHomeworkParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("fetchHomeworkCount", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("fetchHomeworkCount", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

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
            Set<Homework> homeworkSet = new HashSet<>(pendingAssignmentList);
            pendingAssignmentList.clear();
            pendingAssignmentList.addAll(homeworkSet);

            assignedHomeworkParent.setPendingAssignmentList(pendingAssignmentList);
            return assignedHomeworkParent;

        }
        return null;
    }

    /*Method to send status of application for various user activity*/
    @SuppressLint("CheckResult")
    public void checkUserStatus(final String status) {

        Observable.create(new ObservableOnSubscribe<ResponseBody>() {
            @Override
            public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {

                Call<ResponseBody> call = mFlavorNetworkModel.checkUserStatus(status);
                Response<ResponseBody> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("checkUserStatus", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ResponseBody> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("checkUserStatus", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception("Failed");
                    }
                } else {
                    Log.e("checkUserStatus", "Failed");
                    throw new Exception("Failed");
                }

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                        Log.e("userStatusTypeSuccess--", status);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();

                    }
                });

    }

    /*To fetch effort (time spent) data for all subjects*/
    public Observable<ArrayList<EffortvsPerformanceData>> fetchEffortvsPerformanceData() {

        return Observable.create(new ObservableOnSubscribe<ArrayList<EffortvsPerformanceData>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<EffortvsPerformanceData>> e) throws Exception {
                Call<ArrayList<EffortvsPerformanceData>> call = mFlavorNetworkModel.fetchEffortvsPerformanceData();
                Response<ArrayList<EffortvsPerformanceData>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("EffortChartData", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<EffortvsPerformanceData>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("EffortChartData", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("EffortChartData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*To fetch detail of wikiHow card*/
    public Observable<WikiHowParent> fetchWikiHowCardDetail(final String wikiHowId) {

        return Observable.create(new ObservableOnSubscribe<WikiHowParent>() {
            @Override
            public void subscribe(ObservableEmitter<WikiHowParent> e) throws Exception {
                Call<WikiHowParent> call = mFlavorNetworkModel.fetchWikiHowCardDetail(wikiHowId);
                Response<WikiHowParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("WikiHowData", "Successful");
                    e.onNext(response.body());
                } else {
                    Log.e("WikiHowData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();
            }
        });

    }

    /*To get list of wikiHow cards by topicIds*/
    public Observable<ArrayList<WikiHowParent>> getWikiHowData(final ArrayList<String> topicIds) {

        return Observable.create(new ObservableOnSubscribe<ArrayList<WikiHowParent>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<WikiHowParent>> e) throws Exception {
                ArrayList<WikiHowParent> list = new ArrayList<>();

                for (int i = 0; i < topicIds.size(); i++) {
                    Call<WikiHowParent> call = mFlavorNetworkModel.fetchWikiHowCardDetail(topicIds.get(i));
                    Response<WikiHowParent> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        Log.e("WikiHowData", "Successful");
                        list.add(response.body());
                    } else {
                        Log.e("WikiHowData", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                }
                if (!list.isEmpty()) {
                    e.onNext(list);

                } else {
                    e.onError(new Throwable(mContext.getString(R.string.messageNoDataFound)));
                }

                e.onComplete();
            }
        });

    }

    /*To fetch challenge for the day on dashboard*/
    public Observable<LogiQidsChallengeParent> fetchChallengeForTheDay(final String typeChallengeLogiqids) {

        return
                Observable.create(new ObservableOnSubscribe<LogiQidsChallengeParent>() {
                    @Override
                    public void subscribe(ObservableEmitter<LogiQidsChallengeParent> e) throws Exception {
                        Call<LogiQidsChallengeParent> call = mFlavorNetworkModel.fetchChallengeForTheDay(typeChallengeLogiqids);
                        Response<LogiQidsChallengeParent> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            LogiQidsChallengeParent body = response.body();
                            Log.e("ChallengeLQ1--", "Successful");
                            e.onNext(body);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<LogiQidsChallengeParent> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                LogiQidsChallengeParent body2 = response2.body();
                                Log.e("ChallengeLQ2--", "Successful");
                                e.onNext(body2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("ChallengeLQ2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("ChallengeLQ1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To fetch video for the day on Dashboard*/
    public Observable<VideoForDayParent> fetchVideoForTheDay(final String typeVideoPerDay) {

        return
                Observable.create(new ObservableOnSubscribe<VideoForDayParent>() {
                    @Override
                    public void subscribe(ObservableEmitter<VideoForDayParent> e) throws Exception {
                        Call<VideoForDayParent> call = mFlavorNetworkModel.fetchVideoForTheDay(typeVideoPerDay);
                        Response<VideoForDayParent> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            VideoForDayParent body = response.body();
                            Log.e("VideoFD1--", "Successful");
                            e.onNext(body);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<VideoForDayParent> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                VideoForDayParent body = response2.body();
                                Log.e("VideoFD2--", "Successful");
                                e.onNext(body);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("VideoFD2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("VideoFD1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To upload data for Take a Challenge Or Video for day of student when join/complete*/
    public Observable<ResponseBody> uploadTakeChallengeOrVideo(final UserChallengePost post, final int status) {

        return
                Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                    @Override
                    public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                        Call<ResponseBody> call = mFlavorNetworkModel.uploadTakeChallengeOrVideo(post, status);
                        Response<ResponseBody> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            ResponseBody body = response.body();
                            Log.e("uploadChallengeVideo1--", "Successful");
                            e.onNext(body);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ResponseBody> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ResponseBody body = response2.body();
                                Log.e("uploadChallengeVideo2--", "Successful");
                                e.onNext(body);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("uploadChallengeVideo2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("uploadChallengeVideo1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To fetch vocational subject on dashboard(for now, name = life skill)
    * To fetch this post object should be empty*/
    public Observable<ArrayList<VocationalSubject>> fetchVocationalSubject(final VocationalTopicRequest vocationalTopicRequest) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<VocationalSubject>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<VocationalSubject>> e) throws Exception {
                        Call<ArrayList<VocationalSubject>> call = mFlavorNetworkModel.fetchVocationalSubject(vocationalTopicRequest);
                        Response<ArrayList<VocationalSubject>> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            ArrayList<VocationalSubject> list = response.body();
                            Log.e("VocationalSubject1--", "Successful");
                            e.onNext(list);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<VocationalSubject>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<VocationalSubject> list2 = response2.body();
                                Log.e("VocationalSubject2--", "Successful");
                                e.onNext(list2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("VocationalSubject2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("VocationalSubject1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To fetch vocational topic (for now logiqids)*/
    public Observable<ArrayList<VocationalTopic>> fetchVocationalTopics(final VocationalTopicRequest topicRequest) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<VocationalTopic>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<VocationalTopic>> e) throws Exception {
                        Call<ArrayList<VocationalTopic>> call = mFlavorNetworkModel.fetchVocationalTopics(topicRequest);
                        Response<ArrayList<VocationalTopic>> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            ArrayList<VocationalTopic> list = response.body();
                            Log.e("VocationalTopics1--", "Successful");
                            e.onNext(list);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<VocationalTopic>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<VocationalTopic> list2 = response2.body();
                                Log.e("VocationalTopics2--", "Successful");
                                e.onNext(list2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("VocationalTopics2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("VocationalTopics1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }
}
