package in.securelearning.lil.android.profile.views.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutBottomSheetDialogBinding;
import in.securelearning.lil.android.app.databinding.LayoutPickCaptureImageBinding;
import in.securelearning.lil.android.app.databinding.LayoutProfileTabViewpagerBinding;
import in.securelearning.lil.android.app.databinding.LayoutSimpleTextBinding;
import in.securelearning.lil.android.base.dataobjects.BranchDetail;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.GradeSectionSuper;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.LearningLevel;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.views.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileEditActivity;
import in.securelearning.lil.android.home.views.widget.AndroidPermissions;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.profile.event.StudentPersonalProfileRefreshEvent;
import in.securelearning.lil.android.profile.model.ProfileModel;
import in.securelearning.lil.android.profile.views.fragment.StudentAchievementFragment;
import in.securelearning.lil.android.profile.views.fragment.StudentParentFragment;
import in.securelearning.lil.android.profile.views.fragment.StudentPersonalFragment;
import in.securelearning.lil.android.syncadapter.dataobject.CloudinaryFileInner;
import in.securelearning.lil.android.syncadapter.dataobject.FileChooser;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfile;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfilePicturePost;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.service.UserService;
import in.securelearning.lil.android.syncadapter.utils.AppBarStateChangeListener;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.base.utils.FileUtils.copyFiles;

public class StudentProfileActivity extends AppCompatActivity {

    @Inject
    ProfileModel mProfileModel;

    @Inject
    RxBus mRxBus;

    @Inject
    NetworkModel mNetworkModel;

    private static final String USER_ID = "userId";
    private static final int IMAGE_PICK = 104;
    private final static int IMAGE_CAPTURE = 105;
    public static final String THUMB_FILE_DIRECTORY = File.separator + "tempUserProfile";
    public static final String THUMB_FILE_PATH = File.separator + "tempUserImage" + File.separator + "tempImage";


    LayoutProfileTabViewpagerBinding mBinding;
    private ProgressDialog mProgressDialog;

    private boolean isFromLoggedInUser = false;
    private String mUserObjectId = null;
    private StudentProfile mUserProfile = new StudentProfile();
    private StudentAchievement mStudentAchievement;
    private Disposable mDisposable;

    private String mFinalImageUrl;
    private String mBaseFolder, mUserFolder;
    private int mPrimaryColor;
    private Thumbnail mUploadedThumbnail;
    private int mViewPagerPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_profile_tab_viewpager);

        setUpToolbarAndStatusBar(ConstantUtil.BLANK);
        handleIntent();
        listenRxEvent();

        mPrimaryColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
        mUserFolder = getString(R.string.pathUserProfile);
        mBaseFolder = getFilesDir().getAbsolutePath();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    private void actionEditProfile() {
        startActivity(UserProfileEditActivity.getStartIntent(getBaseContext(), mUserObjectId));
    }


    public static Intent getStartIntent(String userId, Context context) {
        Intent intent = new Intent(context, StudentProfileActivity.class);
        intent.putExtra(USER_ID, userId);
        return intent;
    }

    /*Handle intent*/
    private void handleIntent() {
        if (getIntent() != null) {
            mUserObjectId = getIntent().getStringExtra(USER_ID);
            fetchUserProfileFromServer(mUserObjectId);
        }
    }

    /*Setup toolbar and status bar*/
    private void setUpToolbarAndStatusBar(String title) {
        setSupportActionBar(mBinding.toolbar);
        setTitle(ConstantUtil.BLANK);
        if (!TextUtils.isEmpty(title)) {
            mBinding.textViewToolbarTitle.setText(upperCaseFirstLetter(title));
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));
        mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

        mBinding.appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
                Log.e("STATE", state.name());
                if (state.name().equalsIgnoreCase(State.COLLAPSED.toString())) {
                    /*collapsed completely*/

                    /*for status bar*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                    } else {
                        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
                    }

                    mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
                    mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));

                    /*For toolbar*/
                    mBinding.textViewToolbarTitle.setVisibility(View.VISIBLE);
                    mBinding.toolbar.setNavigationIcon(R.drawable.action_arrow_left_dark);
                    mBinding.textViewToolbarTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));

                    requestLayout(mBinding.viewPager);

                } else if (state.name().equalsIgnoreCase(State.EXPANDED.toString())) {
                    /* not collapsed*/

                    /*for status bar*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(0);

                    } else {
                        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
                    }


                    mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));
                    mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

                    /*For toolbar*/
                    mBinding.textViewToolbarTitle.setVisibility(View.GONE);
                    mBinding.toolbar.setNavigationIcon(R.drawable.action_arrow_left_light);
                    mBinding.textViewToolbarTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));

                    requestLayout(mBinding.appBarLayout);

                }
            }
        });


    }

    private void requestLayout(View view) {
        view.requestLayout();
    }

    private void listenRxEvent() {
        mDisposable = mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(Object event) throws Exception {
                        if (event instanceof StudentPersonalProfileRefreshEvent) {
                            mViewPagerPosition = 1;
                            fetchUserProfileFromServer(mUserObjectId);

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_CAPTURE) {

            if (resultCode == RESULT_OK) {
                Uri cameraUri = data.getData();
                startCropping(cameraUri);
            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();
            }

        } else if (data != null && requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {

            Uri imageUri = data.getData();
            startCropping(imageUri);

        } else if (data != null && resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {

                try {

                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

                        String picturePathOriginal = resultUri.getPath();
                        String strPath = copyFiles(picturePathOriginal, mBaseFolder, mUserFolder, System.currentTimeMillis() + ".jpg");
                        String imagePath = "file://" + mBaseFolder + File.separator + strPath;
                        uploadUserThumbnail(imagePath);

                    } else {
                        GeneralUtils.showToastShort(getBaseContext(), getString(R.string.connect_internet_for_upload));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @SuppressLint("CheckResult")
    private void fetchUserProfileFromServer(String mUserId) {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            mProgressDialog = ProgressDialog.show(StudentProfileActivity.this, ConstantUtil.BLANK, getString(R.string.labelPleaseWait), false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onBackPressed();
                }
            });
            ApiModule apiModule = new ApiModule(getBaseContext());
            DownloadApiInterface downloadApiInterface = apiModule.getDownloadClient();

            final Call<StudentProfile> appUserCall = downloadApiInterface.getStudentProfile(mUserId);

            Observable.create(new ObservableOnSubscribe<StudentProfile>() {
                @Override
                public void subscribe(ObservableEmitter<StudentProfile> subscriber) {

                    try {
                        Response<StudentProfile> response = appUserCall.execute();
                        if (response != null && response.isSuccessful()) {
                            com.couchbase.lite.util.Log.e("UserProfile", "successful");

                            StudentProfile userProfile = response.body();
                            subscriber.onNext(userProfile);
                            subscriber.onComplete();

                        } else if (response != null && response.code() == 401 && SyncServiceHelper.refreshToken(getBaseContext())) {

                            Response<StudentProfile> response2 = appUserCall.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                com.couchbase.lite.util.Log.e("UserProfile", "successful");

                                StudentProfile userProfile = response2.body();
                                subscriber.onNext(userProfile);
                                subscriber.onComplete();

                            } else if (response.code() == 401) {
                                startActivity(LoginActivity.getUnauthorizedIntent(getBaseContext()));
                            }
                        } else {
                            finish();
                            Log.e("UserProfile", "err fetching UserProfile" + response.message());
                        }
                    } catch (Exception t) {
                        t.printStackTrace();
                        mProgressDialog.dismiss();
                        finish();
                        Log.e("UserProfile", "err fetching UserProfile" + t.toString());
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.computation())
                    .subscribe(new Consumer<StudentProfile>() {
                        @Override
                        public void accept(StudentProfile userProfile) {
                            fetchMyAchievements(userProfile);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.error_fetching_user_profile));
                            finish();

                        }
                    });
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.connect_internet), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @SuppressLint("CheckResult")
    private void fetchMyAchievements(final StudentProfile userProfile) {
        mProfileModel.fetchStudentAchievements(mUserObjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StudentAchievement>() {
                    @Override
                    public void accept(StudentAchievement studentAchievement) throws Exception {
                        mProgressDialog.dismiss();
                        mStudentAchievement = studentAchievement;
                        initializeUIAndListeners(userProfile);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        initializeUIAndListeners(userProfile);
                        mProgressDialog.dismiss();
                    }
                });
    }

    /**
     * Network call for uploading user thumbnail
     *
     * @param imagePath
     */
    @SuppressLint("CheckResult")
    private void uploadUserThumbnail(final String imagePath) {
        final Resource resource = new Resource();
        resource.setDeviceURL(imagePath);

        if (resource.getObjectId().isEmpty() && !resource.getDeviceURL().isEmpty() && resource.getUrlMain().isEmpty()) {
            mBinding.layoutProgressBottom.setVisibility(View.VISIBLE);
            final Call<CloudinaryFileInner> call = uploadFile(resource);
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> b) throws Exception {

                    try {

                        if (call != null) {
                            Response<CloudinaryFileInner> response = call.execute();

                            if (response.isSuccessful()) {

                                mUploadedThumbnail = response.body();
                                b.onNext(true);


                            } else {
                                Log.e("err profileThumb upload", response.message());
                                b.onNext(false);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        b.onNext(false);
                    }

                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean b) throws Exception {

                            if (b) {

                                updateUserProfile();

                            } else {
                                setUserThumbnail(mUserProfile);
                                mBinding.layoutProgressBottom.setVisibility(View.GONE);
                                GeneralUtils.showToastShort(getBaseContext(), getString(R.string.update_profile_thumb_failed));
                            }

                        }
                    });


        }
    }

    public Call<CloudinaryFileInner> uploadFile(Resource resource) {
        return mNetworkModel.postFileResource(resource);
    }


    @SuppressLint("CheckResult")
    private void updateUserProfile() {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {


            StudentProfilePicturePost profilePicturePost = new StudentProfilePicturePost();
            profilePicturePost.setUserId(mUserObjectId);
            if (mUploadedThumbnail != null) {
                profilePicturePost.setUserThumbnail(mUploadedThumbnail);
            }

            mProfileModel.updateStudentProfileWithImage(profilePicturePost, mUserObjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody body) throws Exception {

                            UserService.startActionUpdateUserProfile(getBaseContext());
                            mBinding.layoutProgressBottom.setVisibility(View.GONE);
                            GeneralUtils.showToastShort(getBaseContext(), "Profile updated successfully");
                            /*below code line is written because JsonSyntaxException error in memberGroups
                             * and we need latest thumbnail for student*/
                            /*START HERE*/
                            if (mUserProfile == null) {
                                mUserProfile = new StudentProfile();
                            }
                            mUserProfile.setThumbnail(mUploadedThumbnail);
                            /*END HERE*/

                            setUserThumbnail(mUserProfile);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.layoutProgressBottom.setVisibility(View.GONE);
                            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
                        }
                    });

        } else {
            mBinding.layoutProgressBottom.setVisibility(View.GONE);
//            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.connect_internet_for_upload));
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_message_no_internet));
        }
    }

    private void initializeUIAndListeners(StudentProfile userProfile) {

        mUserProfile = userProfile;
        setUpToolbarAndStatusBar(userProfile.getName());
        setUpViewPager();
        setUserThumbnail(userProfile);
        setGradeSection(userProfile.getGrade(), userProfile.getSection());
        setAssociation(userProfile.getAssociation(), userProfile.getBranchDetail());
        setFullUserName(userProfile.getName());

    }

    private void setAssociation(Institution association, BranchDetail branchDetail) {
//        if (association != null && !TextUtils.isEmpty(association.getName()) && branchDetail != null && !TextUtils.isEmpty(branchDetail.getName())) {
//            String text = association.getName() + ", " + branchDetail.getName();
//            mBinding.textViewAddress.setText(text);
//        } else if (association != null && !TextUtils.isEmpty(association.getName())) {
//            mBinding.textViewAddress.setText(association.getName());
//        } else
        if (branchDetail != null && !TextUtils.isEmpty(branchDetail.getName())) {
            String text = branchDetail.getName();
            mBinding.textViewAddress.setText(text);
        } else {
            mBinding.textViewAddress.setVisibility(View.GONE);
        }
    }

    private void setGradeSection(Grade grade, GradeSectionSuper section) {
        if (grade != null && !TextUtils.isEmpty(grade.getName()) && section != null && !TextUtils.isEmpty(section.getName())) {
            String text = grade.getName() + " (" + section.getName() + ")";
            mBinding.textViewGradeSection.setText(text);
        } else {
            mBinding.textViewGradeSection.setVisibility(View.GONE);
        }

    }

    private void setFullUserName(String name) {
        if (!TextUtils.isEmpty(name)) {
            mBinding.textViewName.setText(upperCaseFirstLetter(name));
        }
    }

    private String upperCaseFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }


    private void setUserName(String firstName, String lastName) {

        if (!TextUtils.isEmpty(firstName)) {
            firstName = upperCaseFirstLetter(firstName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            lastName = upperCaseFirstLetter(lastName);
        }

    }

    private void setInterest(UserProfile userProfile) {
        if (isFromLoggedInUser) {
            setInterestLearningLevel(userProfile.getInterest().getLearningLevel());
            //setInterestSubject(userProfile.getInterest().getSubject());
        }
    }

    private void setAddress(UserProfile userProfile) {

        if (isFromLoggedInUser) {
            if (!TextUtils.isEmpty(userProfile.getLocation().getCity()) &&
                    !TextUtils.isEmpty(userProfile.getLocation().getState().getName()) &&
                    !TextUtils.isEmpty(userProfile.getLocation().getCountry().getName())) {
                mBinding.textViewAddress.setVisibility(View.VISIBLE);
                mBinding.textViewAddress.setText(upperCaseFirstLetter(userProfile.getLocation().getCity()) + ", " + upperCaseFirstLetter(userProfile.getLocation().getState().getName()) + ", " + upperCaseFirstLetter(userProfile.getLocation().getCountry().getName()));
            } else {
                mBinding.textViewAddress.setVisibility(View.GONE);
            }
        } else {
            mBinding.textViewAddress.setVisibility(View.GONE);
        }


    }


    private void setInterestLearningLevel(List<LearningLevel> list) {
        try {
            if (list != null) {
                java.util.ArrayList<String> learningLevelList = new java.util.ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    learningLevelList.add(list.get(i).getName());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    private void setUserThumbnail(UserProfile userProfile) {

        final ArrayList<String> profileImageOptions = new ArrayList<>();
        profileImageOptions.add(getString(R.string.labelUpdateProfileImage));


        String imageUrl = null;
        boolean canFullView = false;
        if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getLocalUrl())) {
            imageUrl = userProfile.getThumbnail().getLocalUrl();
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getLocalUrl()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewProfile);
            canFullView = true;
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getUrl())) {
            imageUrl = userProfile.getThumbnail().getUrl();

            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getUrl()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewProfile);
            canFullView = true;
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getThumb())) {
            imageUrl = userProfile.getThumbnail().getThumb();
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getThumb()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewProfile);
            canFullView = true;
        } else {
            if (!TextUtils.isEmpty(userProfile.getFirstName())) {
                String firstWord = userProfile.getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                mBinding.imageViewProfile.setImageDrawable(textDrawable);
                canFullView = false;
            }

        }
        final boolean finalCanFullView = canFullView;
        mFinalImageUrl = imageUrl;

        mBinding.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalCanFullView && !TextUtils.isEmpty(mFinalImageUrl)) {
                    if (!profileImageOptions.contains(getString(R.string.labelViewProfileImage))) {
                        profileImageOptions.add(getString(R.string.labelViewProfileImage));
                    }
                }

                showStudentProfileImageBottomSheetDialog(profileImageOptions, getString(R.string.labelPleaseSelect));

            }
        });
    }

    private void setUpViewPager() {
        final ArrayList<String> tabTitles = getTabTitles();
        mBinding.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), tabTitles));
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.viewPager.setCurrentItem(mViewPagerPosition, true);
    }

    private ArrayList<String> getTabTitles() {
        return new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_student_profile_tab)));
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<String> mList;

        public ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<String> list) {
            super(fragmentManager);
            mList = list;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position);
        }

        @Override
        public Fragment getItem(int position) {

            if (mList.get(position).equals(getString(R.string.label_achievement))) {
                return StudentAchievementFragment.newInstance(mStudentAchievement);
            } else if (mList.get(position).equals(getString(R.string.label_personal))) {
                return StudentPersonalFragment.newInstance(mUserProfile.getClassGroup(), mUserProfile.getEnrollmentNumber(),
                        mUserProfile.getAdmissionDate(), mUserProfile.getDob(), mUserProfile.getAddress()
                        , mUserProfile.getLocation(), mUserProfile.getGrade().getId(), mUserProfile.getUserInterest());
            } else if (mList.get(position).equals(getString(R.string.label_parent))) {
                return StudentParentFragment.newInstance(mUserProfile.getFatherName(), mUserProfile.getFatherMobile(),
                        mUserProfile.getFatherEmail(), mUserProfile.getMotherName(), mUserProfile.getMotherMobile(), mUserProfile.getMotherEmail());
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

    }

    /*Bottom Sheet Dialog*/
    private void showStudentProfileImageBottomSheetDialog(ArrayList<String> arrayList, String title) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        LayoutBottomSheetDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_bottom_sheet_dialog, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());
        binding.textViewTitle.setText(title);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(new StudentProfileImageBottomSheetListAdapter(arrayList, bottomSheetDialog));
        bottomSheetDialog.show();
    }

    private class StudentProfileImageBottomSheetListAdapter extends RecyclerView.Adapter<StudentProfileImageBottomSheetListAdapter.ViewHolder> {

        private ArrayList<String> mList;
        private BottomSheetDialog mBottomSheetDialog;

        StudentProfileImageBottomSheetListAdapter(ArrayList<String> list, BottomSheetDialog bottomSheetDialog) {
            mList = list;
            mBottomSheetDialog = bottomSheetDialog;
        }

        @NotNull
        @Override
        public StudentProfileImageBottomSheetListAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            LayoutSimpleTextBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_simple_text, parent, false);
            return new StudentProfileImageBottomSheetListAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NotNull final StudentProfileImageBottomSheetListAdapter.ViewHolder holder, int position) {
            final String str = mList.get(position);

            holder.mBinding.textView.setText(str);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.mBinding.textView.getText().toString().equalsIgnoreCase(getString(R.string.labelViewProfileImage))) {
                        startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), mFinalImageUrl, true));
                    } else if (holder.mBinding.textView.getText().toString().equalsIgnoreCase(getString(R.string.labelUpdateProfileImage))) {

                        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                            showImagePickOptionDialog();
                        } else {
                            actionGalleryClick();
                        }

                    }


                    mBottomSheetDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutSimpleTextBinding mBinding;

            public ViewHolder(LayoutSimpleTextBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }


    /**
     * show image pick option dialog
     */
    private void showImagePickOptionDialog() {

        final Dialog dialog = new Dialog(StudentProfileActivity.this);
        final LayoutPickCaptureImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_pick_capture_image, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorDialogBackground)));

        AnimationUtils.fadeInFast(getBaseContext(), binding.layoutCamera);
        AnimationUtils.fadeInFast(getBaseContext(), binding.layoutGallery);

        binding.layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCameraClick();
                dialog.dismiss();

            }
        });

        binding.layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionGalleryClick();
                dialog.dismiss();

            }
        });

        binding.layoutMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                dialog.dismiss();
                return false;
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void actionCameraClick() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                PackageManager pm = getPackageManager();
                int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                    AndroidPermissions.checkCameraPermission(StudentProfileActivity.this);
                } else {
                    createCameraDirectory();
                }
            } else {
                createCameraDirectory();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionGalleryClick() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                PackageManager pm = getPackageManager();
                int hasPerm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                    AndroidPermissions.checkStoragePermission(StudentProfileActivity.this);
                } else {
                    startImagePick();
                }
            } else {
                startImagePick();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * intent action to pick image from gallery
     */
    private void startImagePick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK);
    }

    /**
     * create camera directory for capturing image for user profile
     */
    private void createCameraDirectory() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            File tempFile = new File(mBaseFolder + THUMB_FILE_DIRECTORY);
            if (!tempFile.exists()) {
                tempFile.getParentFile().mkdirs();
            }
            startImageCapture(tempFile);
        } else {
            SnackBarUtils.showColoredSnackBar(getBaseContext(), getCurrentFocus(), getString(R.string.error_no_camera), mPrimaryColor);
        }

    }

    /**
     * set up and start camera for taking picture
     *
     * @param saveFolder
     */
    private void startImageCapture(File saveFolder) {
        new MaterialCamera(this)
                .allowRetry(true)
                .autoSubmit(false)
                .saveDir(saveFolder)
                .primaryColor(mPrimaryColor)
                .showPortraitWarning(true)
                .defaultToFrontFacing(false)
                .retryExits(false)
                .videoPreferredAspect(16f / 9f)
                .maxAllowedFileSize(1024 * 1024 * 5)
                .iconRecord(R.drawable.mcam_action_capture)
                .iconFrontCamera(R.drawable.mcam_camera_front)
                .iconRearCamera(R.drawable.mcam_camera_rear)
                .labelRetry(R.string.mcam_retry)
                .labelConfirm(R.string.label_edit_photo)
                .stillShot()
                .start(IMAGE_CAPTURE);
    }

    /**
     * crop image which was picked from gallery or captured by camera
     *
     * @param uri
     */
    private void startCropping(Uri uri) {
        if (FileChooser.fileSize(getBaseContext(), uri) < ConstantUtil.PROFILE_IMAGE_MAX_SIZE_IN_MB) {
            File tempFile = new File(mBaseFolder + THUMB_FILE_PATH);
            if (!tempFile.exists()) {
                tempFile.getParentFile().mkdirs();
            }
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(mPrimaryColor);
            options.setStatusBarColor(mPrimaryColor);
            Uri destinationUri = Uri.fromFile(tempFile);
            UCrop.of(uri, destinationUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(300, 300)
                    .withOptions(options)
                    .start(this);

        } else {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit) + " " + ConstantUtil.PROFILE_IMAGE_MAX_SIZE_IN_MB + " MB");
        }
    }


}