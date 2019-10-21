package in.securelearning.lil.android.home.views.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.afollestad.materialcamera.MaterialCamera;
import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutPickCaptureImageBinding;
import in.securelearning.lil.android.app.databinding.LayoutUserProfileEditActivityBinding;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.views.widget.AndroidPermissions;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.CloudinaryFileInner;
import in.securelearning.lil.android.syncadapter.dataobject.FileChooser;
import in.securelearning.lil.android.syncadapter.events.UserProfileChangeEvent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
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

/**
 * Created by Chaitendra on 03-Oct-17.
 */

public class UserProfileEditActivity extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    RxBus mRxBus;

    public static String USER_ID = "userId";

    private static final int IMAGE_PICK = 104;
    private final static int IMAGE_CAPTURE = 105;

    public static final String THUMB_FILE_DIRECTORY = File.separator + "tempUserProfile";
    public static final String THUMB_FILE_PATH = File.separator + "tempUserImage" + File.separator + "tempImage";

    private LayoutUserProfileEditActivityBinding mBinding;
    private String mUserObjectId;
    private boolean mCanFullView = false;
    private String mUserProfilePath;
    private String mBaseFolder, mUserFolder;
    private int mPrimaryColor;
    private UserProfile mUserProfile;
    private Disposable mSubscription;

    private boolean isThumbnailChanged = false;
    private boolean isThumbnailUploaded = false;
    private MenuItem menuItemUpdate;
    private Thumbnail mUploadedThumbnail;

    public static Intent getStartIntent(Context context, String userId) {
        Intent intent = new Intent(context, UserProfileEditActivity.class);
        intent.putExtra(USER_ID, userId);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mBinding.editTextFirstName.getText().toString().equals(mUserProfile.getFirstName()) ||
                !mBinding.editTextMiddleName.getText().toString().equals(mUserProfile.getMiddleName()) ||
                !mBinding.editTextLastName.getText().toString().equals(mUserProfile.getLastName()) ||
                !mBinding.editTextAboutMe.getText().toString().equals(mUserProfile.getAboutMe()) ||
                isThumbnailChanged) {
            showExitConfirmationDialog();
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_user_profile_edit_activity);

        mPrimaryColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
        mUserFolder = getString(R.string.pathUserProfile);
        mBaseFolder = getFilesDir().getAbsolutePath();
        setUpToolbar();
        getUserData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_done, menu);
        menuItemUpdate = menu.findItem(R.id.actionDone);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionDone:
                actionUpdateProfile();
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
                        String strPath = copyFiles(picturePathOriginal, mBaseFolder, mUserFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                        String imagePath = "file://" + mBaseFolder + File.separator + strPath;
                        mCanFullView = true;
                        isThumbnailChanged = true;
                        uploadUserThumbnail(imagePath);
                    } else {
                        showAlertMessage(getString(R.string.connect_internet_for_upload));
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

    private void updateVisibility(boolean b) {
        if (menuItemUpdate != null) {
            menuItemUpdate.setVisible(b);
        }
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(mPrimaryColor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mPrimaryColor));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.action_close_w);
        SpannableString title = new SpannableString(getString(R.string.label_edit_profile));
        title.setSpan(new TypefaceSpan("sans-serif-condensed"), 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(title);
    }

    /**
     * get user data from database
     */
    private void getUserData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(USER_ID)) {
            mUserObjectId = getIntent().getStringExtra(USER_ID);
            if (mUserObjectId.equals(mAppUserModel.getObjectId())) {
                mUserProfile = mAppUserModel.getApplicationUser();
                initializeUi(mUserProfile);
            }
        }
    }

    /**
     * set values to views and handle clicks
     *
     * @param userProfile
     */
    private void initializeUi(UserProfile userProfile) {
        setUserThumbnail(userProfile);
        mBinding.editTextFirstName.append(userProfile.getFirstName());
        mBinding.editTextMiddleName.append(userProfile.getMiddleName());
        mBinding.editTextLastName.append(userProfile.getLastName());
        mBinding.editTextAboutMe.append(Html.fromHtml(userProfile.getAboutMe()));

        mBinding.buttonImagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                        showImagePickOptionDialog();
                    } else {
                        actionGalleryClick();
                    }

                } else {
                    showAlertMessage(getString(R.string.connect_internet_for_upload));
                }
            }
        });

        mBinding.imageViewUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCanFullView && !TextUtils.isEmpty(mUserProfilePath)) {
                    PlayFullScreenImageActivity.getStartIntent(getBaseContext(), mUserProfilePath, true);

                }
            }
        });
    }

    /**
     * handle and set user thumbnail to image view
     *
     * @param userProfile
     */
    private void setUserThumbnail(UserProfile userProfile) {
        if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getLocalUrl())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getLocalUrl()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewUserProfile);
            mCanFullView = true;
            mUserProfilePath = userProfile.getThumbnail().getLocalUrl();
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getUrl())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getUrl()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewUserProfile);
            mCanFullView = true;
            mUserProfilePath = userProfile.getThumbnail().getUrl();
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getThumb())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getThumb()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewUserProfile);
            mCanFullView = true;
            mUserProfilePath = userProfile.getThumbnail().getThumb();
        } else {
            if (!TextUtils.isEmpty(userProfile.getFirstName())) {
                String firstWord = userProfile.getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                mBinding.imageViewUserProfile.setImageDrawable(textDrawable);
                mCanFullView = false;
            }
        }
    }

    /**
     * handle click of update profile menu item
     */
    private void actionUpdateProfile() {
        String firstName = mBinding.editTextFirstName.getText().toString().trim();
        String middleName = mBinding.editTextMiddleName.getText().toString().trim();
        String lastName = mBinding.editTextLastName.getText().toString().trim();
        String aboutMe = mBinding.editTextAboutMe.getText().toString().trim();
        if (!firstName.equals(mUserProfile.getFirstName()) ||
                !middleName.equals(mUserProfile.getMiddleName()) ||
                !lastName.equals(mUserProfile.getLastName()) ||
                !aboutMe.equals(mUserProfile.getAboutMe()) ||
                isThumbnailChanged) {
            if (isThumbnailChanged && !isThumbnailUploaded) {
                return;
            }
            if (firstName.isEmpty()) {
                showAlertMessage(getString(R.string.error_firstname));
                mBinding.editTextFirstName.requestFocus();
                mBinding.editTextFirstName.setCursorVisible(true);
            } else if (lastName.isEmpty()) {
                showAlertMessage(getString(R.string.error_lastname));
                mBinding.editTextLastName.requestFocus();
                mBinding.editTextLastName.setCursorVisible(true);
            } else {
                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    updateUserProfile(firstName, middleName, lastName, aboutMe);
                } else {
                    showAlertMessage(getString(R.string.connect_internet));
                }
            }
        } else {
            finish();
        }

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
            updateVisibility(false);
            mBinding.progressBar.setVisibility(View.VISIBLE);
            AnimationUtils.zoomOutFast(getBaseContext(), mBinding.buttonImagePick);
            mBinding.buttonImagePick.setVisibility(View.GONE);
            mBinding.imageViewUserProfile.setImageResource(R.drawable.background_circle_transparent);
            final Call<CloudinaryFileInner> call = uploadFile(resource);
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> b) throws Exception {
                    try {
                        if (call != null) {
                            Response<CloudinaryFileInner> response = call.execute();

                            if (response.isSuccessful()) {

                                CloudinaryFileInner resourceNetwork = response.body();
                                mUploadedThumbnail = resourceNetwork;
                                //  mUserProfile.setThumbnail(resourceNetwork);
                                isThumbnailUploaded = true;
                                b.onNext(true);


                            } else {
                                b.onNext(false);
                                Log.e("err profileThumb upload", response.message());
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
                            updateVisibility(true);

                            if (b) {
                                // setUserThumbnail(mUserProfile);
                                Picasso.with(getBaseContext()).load(imagePath).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewUserProfile);
                                mCanFullView = true;
                                mUserProfilePath = imagePath;
                                mBinding.progressBar.setVisibility(View.GONE);
                                showSuccessMessage(getString(R.string.update_profile_thumb_successful));
                            } else {
                                setUserThumbnail(mUserProfile);
                                mBinding.progressBar.setVisibility(View.GONE);
                                showAlertMessage(getString(R.string.update_profile_thumb_failed));
                            }
                            mBinding.buttonImagePick.setVisibility(View.VISIBLE);
                            AnimationUtils.zoomInFast(getBaseContext(), mBinding.buttonImagePick);

                        }
                    });


        }
    }

    public Call<CloudinaryFileInner> uploadFile(Resource resource) {

        return mNetworkModel.postFileResource(resource);
    }

    /**
     * Network call for updating user profile
     *
     * @param firstName
     * @param middleName
     * @param lastName
     * @param aboutMe
     */
    @SuppressLint("CheckResult")
    private void updateUserProfile(final String firstName, final String middleName, final String lastName, final String aboutMe) {
        final UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(firstName);
        userProfile.setMiddleName(middleName);
        userProfile.setLastName(lastName);
        userProfile.setAboutMe(aboutMe);
        userProfile.setObjectId(mUserObjectId);
        if (isThumbnailChanged) {
            userProfile.setThumbnail(mUploadedThumbnail);
        } else {
            userProfile.setThumbnail(mUserProfile.getThumbnail());
        }
        userProfile.setEmail(null);
        userProfile.setPassword(null);
        userProfile.setGender(null);
        userProfile.setAssociation(null);
        userProfile.setAlias(null);
        userProfile.setGrade(null);
        userProfile.setTitle(null);
        userProfile.setAddress(null);
        userProfile.setBadgesEarned(null);
        userProfile.setBoard(null);
        userProfile.setCreationTime(null);
        userProfile.setDepartment(null);
        userProfile.setDesignationId(null);
        userProfile.setDob(null);
        userProfile.setDoj(null);
        userProfile.setEmailVerified(null);
        userProfile.setLastUpdationTime(null);
        userProfile.setInterest(null);
        userProfile.setLocation(null);
        userProfile.setMobile(null);
        userProfile.setMemberGroups(null);
        userProfile.setModeratedGroups(null);
        userProfile.setQualification(mUserProfile.getQualification());
        userProfile.setRole(null);
        userProfile.setSection(null);
        userProfile.setUserPic(null);

        final ProgressDialog progressDialog = ProgressDialog.show(UserProfileEditActivity.this, "", getString(R.string.message_updating_user_profile), false);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        Observable.create(new ObservableOnSubscribe<UserProfile>() {
            @Override
            public void subscribe(ObservableEmitter<UserProfile> subscriber) {

                try {
                    final Call<ResponseBody> userProfileCall = mNetworkModel.updateUserProfile(userProfile);
                    Response<ResponseBody> response = userProfileCall.execute();
                    if (response != null && response.isSuccessful()) {
                        Log.e("Profile Update", "profileUpdate successful");
                        profileUpdateSuccessful(firstName, middleName, lastName, aboutMe, progressDialog, userProfile.getThumbnail());

                    } else if (response.code() == 401 && SyncServiceHelper.refreshToken(getBaseContext())) {

                        Response<ResponseBody> response2 = userProfileCall.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            Log.e("Profile Update", "profileUpdate successful");
                            profileUpdateSuccessful(firstName, middleName, lastName, aboutMe, progressDialog, userProfile.getThumbnail());
                            subscriber.onNext(userProfile);
                            subscriber.onComplete();

                        } else if (response2.code() == 401) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getBaseContext()));
                        }
                    } else {
                        progressDialog.dismiss();
                        showAlertMessage(getString(R.string.update_profile_failed));
                        Log.e("FitnessUserProfile", "err updating FitnessUserProfile" + response.message());
                    }
                } catch (Exception t) {
                    t.printStackTrace();
                    progressDialog.dismiss();
                    showAlertMessage(getString(R.string.update_profile_failed));
                    Log.e("FitnessUserProfile", "err updating FitnessUserProfile" + t.toString());
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<UserProfile>() {
                    @Override
                    public void accept(UserProfile userProfile) {
                        progressDialog.dismiss();
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) {
                        progressDialog.dismiss();
                        showAlertMessage(getString(R.string.update_profile_failed));
                        Log.e("FitnessUserProfile", "err updating FitnessUserProfile" + t.toString());

                        t.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlertMessage(getString(R.string.update_profile_failed));
                            }
                        });
                    }
                });

    }

    /**
     * action perform after successful profile update
     *
     * @param firstName
     * @param middleName
     * @param lastName
     * @param aboutMe
     * @param progressDialog
     * @param thumbnail
     */
    private void profileUpdateSuccessful(String firstName, String middleName, String lastName, String aboutMe, ProgressDialog progressDialog, Thumbnail thumbnail) {
        mUserProfile.setFirstName(firstName);
        mUserProfile.setMiddleName(middleName);
        mUserProfile.setLastName(lastName);
        mUserProfile.setAboutMe(aboutMe);
        mUserProfile.setThumbnail(thumbnail);
        mAppUserModel.saveUserProfile(mUserProfile);
        mAppUserModel.setApplicationUser(mUserProfile);
        mRxBus.send(new UserProfileChangeEvent(mUserObjectId));
        progressDialog.dismiss();
        finish();

    }

    /**
     * show image pick option dialog
     */
    private void showImagePickOptionDialog() {
        final Dialog dialog = new Dialog(UserProfileEditActivity.this);
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
                    AndroidPermissions.checkCameraPermission(UserProfileEditActivity.this);
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
                    AndroidPermissions.checkStoragePermission(UserProfileEditActivity.this);
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
        if (FileChooser.fileSize(getBaseContext(), uri) < 10) {
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
                    .withMaxResultSize(1024, 1024)
                    .withOptions(options)
                    .start(this);

        } else {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit_10MB));
        }
    }

    /**
     * show conformation dialog if user presses back
     */
    private void showExitConfirmationDialog() {

        new AlertDialog.Builder(this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.alert_changed_data)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    /**
     * show snack bar with provided messages
     *
     * @param string
     */
    private void showAlertMessage(String string) {
        SnackBarUtils.showColoredSnackBar(getBaseContext(), getCurrentFocus(), string, ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLN));
    }

    private void showSuccessMessage(String string) {
        SnackBarUtils.showColoredSnackBar(getBaseContext(), getCurrentFocus(), string, ContextCompat.getColor(getBaseContext(), R.color.colorAssignmentPrimary));
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
