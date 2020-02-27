package in.securelearning.lil.android.profile.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.view.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.profile.dataobject.ProfileVideo;
import in.securelearning.lil.android.profile.dataobject.TeacherProfile;
import in.securelearning.lil.android.profile.dataobject.UserInterest;
import in.securelearning.lil.android.profile.dataobject.UserInterestPost;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfilePicturePost;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileModel {

    @Inject
    Context mContext;

    @Inject
    NetworkModel mNetworkModel;

    public ProfileModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    public void playVideo(ProfileVideo profileVideo) {

        String type = profileVideo.getType();
        String url = profileVideo.getUrl();
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
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeYouTubeVideo))) {
            if (!url.contains("http:") || !url.contains("https:")) {
                FavouriteResource favouriteResource = new FavouriteResource();
                favouriteResource.setName(url);
                favouriteResource.setUrlThumbnail("");
                mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
            } else {
                String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

                Pattern compiledPattern = Pattern.compile(pattern);
                Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
                if (matcher.find()) {
                    String videoId = matcher.group();
                    FavouriteResource favouriteResource = new FavouriteResource();
                    favouriteResource.setName(videoId);
                    favouriteResource.setUrlThumbnail("");
                    mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
                }
            }


        } else if (type.equalsIgnoreCase(mContext.getString(R.string.typeVimeoVideo))) {
            mContext.startActivity(PlayVimeoFullScreenActivity.getStartIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, ConstantUtil.BLANK, url));
        } else {
            Resource item = new Resource();
            item.setType(mContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        }
    }


    /*To fetch student's achievements*/
    public Observable<StudentAchievement> fetchStudentAchievements(final String userId) {

        return
                Observable.create(new ObservableOnSubscribe<StudentAchievement>() {
                    @Override
                    public void subscribe(ObservableEmitter<StudentAchievement> e) throws Exception {
                        Call<StudentAchievement> call = mNetworkModel.fetchStudentAchievements(userId);
                        Response<StudentAchievement> response = call.execute();

                        if (response != null && response.isSuccessful()) {
                            Log.e("StudentAchievements1--", "Successful");
                            e.onNext(response.body());
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                            Response<StudentAchievement> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                Log.e("StudentAchievements2--", "Successful");
                                e.onNext(response.body());
                            } else if (response2.code() == 401) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                            } else {
                                Log.e("StudentAchievements2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("StudentAchievements1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();

                    }
                });

    }

    /*To fetch Student interest Data list according to interest type*/
    public Observable<ArrayList<UserInterest>> fetchStudentInterestData(final String gradeId, final int interestType) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<UserInterest>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<UserInterest>> e) throws Exception {
                        Call<ArrayList<UserInterest>> call = mNetworkModel.fetchStudentInterestData(gradeId, interestType);
                        Response<ArrayList<UserInterest>> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            ArrayList<UserInterest> list = response.body();
                            Log.e("InterestData1--", "Successful");
                            e.onNext(list);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<UserInterest>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<UserInterest> list = response2.body();
                                Log.e("InterestData2--", "Successful");
                                e.onNext(list);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("InterestData2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("InterestData1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

    /*To send selected user interest to server*/
    public Observable<ResponseBody> sendUserInterestToServer(final UserInterestPost post, String userInterestId) {

        if (TextUtils.isEmpty(userInterestId)) {

            /* if userInterestId = empty that means
             * user is adding goal or any kind of user interest for the first time
             */

            return
                    Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                        @Override
                        public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                            Call<ResponseBody> call = mNetworkModel.sendUserInterestInitially(post);
                            Response<ResponseBody> response = call.execute();
                            if (response != null && response.isSuccessful()) {
                                ResponseBody list = response.body();
                                Log.e("UserIFirst1--", "Successful");
                                e.onNext(list);
                            } else if (response.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                                Response<ResponseBody> response2 = call.clone().execute();
                                if (response2 != null && response2.isSuccessful()) {
                                    ResponseBody list = response2.body();
                                    Log.e("UserIFirst2--", "Successful");
                                    e.onNext(list);
                                } else if ((response2.code() == 401)) {
                                    mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                                } else if (response2.code() == 404) {
                                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                                } else {
                                    Log.e("UserIFirst2--", "Failed");
                                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                                }
                            } else {
                                Log.e("UserIFirst1--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                            e.onComplete();
                        }
                    });

        } else {

            /* if userInterestId = not empty that means
             * user is already added goal or any kind of user interest
             */

            return
                    Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                        @Override
                        public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                            Call<ResponseBody> call = mNetworkModel.sendUserInterest(post);
                            Response<ResponseBody> response = call.execute();
                            if (response != null && response.isSuccessful()) {
                                ResponseBody list = response.body();
                                Log.e("UserIOther1--", "Successful");
                                e.onNext(list);
                            } else if (response.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                                Response<ResponseBody> response2 = call.clone().execute();
                                if (response2 != null && response2.isSuccessful()) {
                                    ResponseBody list = response2.body();
                                    Log.e("UserIOther2--", "Successful");
                                    e.onNext(list);
                                } else if ((response2.code() == 401)) {
                                    mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                                } else if (response2.code() == 404) {
                                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                                } else {
                                    Log.e("UserIOther2--", "Failed");
                                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                                }
                            } else {
                                Log.e("UserIOther1--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                            e.onComplete();
                        }
                    });

        }

    }

    /*To fetch non-student users' profile*/
    public Observable<TeacherProfile> fetchNonStudentUserProfile(final String userId) {

        return Observable.create(new ObservableOnSubscribe<TeacherProfile>() {
            @Override
            public void subscribe(ObservableEmitter<TeacherProfile> e) throws Exception {

                Call<TeacherProfile> call = mNetworkModel.fetchNonStudentUserProfileByUserId(userId);
                Response<TeacherProfile> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    Log.e("NonStProfile1--", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<TeacherProfile> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("NonStProfile2--", "Successful");
                        e.onNext(response2.body());
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("NonStProfile2--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("NonStProfile1--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });

    }

    /*To update profile of student with profile picture*/
    public Observable<ResponseBody> updateStudentProfileWithImage(final StudentProfilePicturePost profilePicturePost, final String userId) {

        return
                Observable.create(new ObservableOnSubscribe<ResponseBody>() {
                    @Override
                    public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                        Call<ResponseBody> call = mNetworkModel.updateStudentProfileWithImage(profilePicturePost, userId);
                        Response<ResponseBody> response = call.execute();
                        if (response != null && response.isSuccessful()) {
                            ResponseBody body = response.body();
                            Log.e("StudentImage1--", "Successful");
                            e.onNext(body);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ResponseBody> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ResponseBody body = response2.body();
                                Log.e("StudentImage2--", "Successful");
                                e.onNext(body);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("StudentImage2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("StudentImage1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

}
