package in.securelearning.lil.android.learningnetwork.views.activity;

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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCreatePostBinding;
import in.securelearning.lil.android.app.databinding.LayoutPickCaptureImageBinding;
import in.securelearning.lil.android.app.databinding.LayoutProgressBarBinding;
import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.OGMetaDataResponse;
import in.securelearning.lil.android.base.dataobjects.PostByUser;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostToGroup;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Result;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.widget.AndroidPermissions;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostCreatedEvent;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.syncadapter.dataobject.FileChooser;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Chaitendra on 3/9/2017.
 */


public class CreatePostActivity extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    GroupModel mGroupModel;
    @Inject
    PostDataLearningModel mPostDataLearningModel;
    @Inject
    RxBus mRxBus;
    @Inject
    PostDataModel mPostDataModel;
    @Inject
    OgUtils mOgUtils;
    private static final int FILE_PICK_REQUEST = 101;
    private static final int VIDEO_PICK = 103;
    private static final int IMAGE_PICK = 104;
    private final static int IMAGE_CAPTURE = 105;
    public static final String THUMB_FILE_PATH = File.separator + "tempLNImage" + File.separator + "tempImage";
    public static final String THUMB_FILE_DIRECTORY = File.separator + "tempLNImage";
    private Disposable mSubscription;
    private ScrollView mPostScrollView;
    private Spinner mPostTypeSpinner, mPostToGroupSpinner;
    private EditText mPostEditText;
    private LinearLayout mDiscussionLayout;
    private RecyclerView mResourceRecyclerView;
    private View mViewFocus;
    private ArrayList<Group> mGroupsList = new ArrayList<>();
    private ArrayList<String> mAttachPathList = new ArrayList<>();
    private ArrayList<Uri> mAttachUriList = new ArrayList<>();
    private PopupWindow popupMenu;
    private int mSelectedGroupIndex;
    private String mGroupId;
    private String mLearningNetworkFolder, strFileName;
    private ResourceGridAdapter mResourceGridAdapter;
    private ProgressDialog progressDialog;
    private Uri cameraOutputUri;
    private static String GROUP_ID = "group_object_id";
    private MenuItem mAttachMenuItem;
    private Toolbar mToolbar;
    private TextView mGroupNameTextView;
    private Group mGroup;
    private int mColor = 0;
    private int mPrimaryColor = 0;
    private LayoutCreatePostBinding mBinding;
    private String mFirstUrl = "";
    private String mBaseFolder;

    public static Intent getIntentForCreatePost(Context context, String groupId) {
        Intent intent = new Intent(context, CreatePostActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (mPostEditText.getText().toString().isEmpty() && mAttachPathList.isEmpty()) {
            closeActivity();
        } else {
            showExitConfirmationDialog();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        setContentView(R.layout.layout_create_post);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_create_post);
        mPrimaryColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLN);
        mBaseFolder = getFilesDir().getAbsolutePath();
//        mBaseFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mGroupId = bundle.getString(GROUP_ID);
            getGroup(mGroupId);
        }
        initializeResourceFolders(getString(R.string.pathLearningNetwork));
        initializeViews();
        initializeUiAndListeners();

    }

    private void getGroup(String groupId) {
        mGroup = mGroupModel.getGroupFromUidSync(groupId);
        PrefManager.SubjectExt subjectExt = null;
        if (mGroup.getSubject() != null && !TextUtils.isEmpty(mGroup.getSubject().getId())) {
            HashMap<String, PrefManager.SubjectExt> mSubjectMap = PrefManager.getSubjectMap(getBaseContext());
            subjectExt = mSubjectMap.get(mGroup.getSubject().getId());
        }
        if (subjectExt == null) {
            subjectExt = PrefManager.getDefaultSubject();
        }
        mColor = subjectExt.getTextColor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        mAttachMenuItem = menu.findItem(R.id.actionAttach);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.actionAttach:
                menuActionAttach();
                return true;

            case R.id.actionPost:
                menuActionCreatePost();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("CheckResult")
    private void menuActionCreatePost() {
        if (!TextUtils.isEmpty(mBinding.layoutDiscussion.editTextWritePost.getText().toString().trim()) &&
                mBinding.spinnerCategory.getTag() != null) {

            LayoutProgressBarBinding view = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_progress_bar, null, false);
            final Dialog dialog = new Dialog(CreatePostActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(view.getRoot());
            view.texViewMessage.setText(getString(R.string.messagePleaseWait));
            dialog.show();

            final String postText = Html.toHtml(mBinding.layoutDiscussion.editTextWritePost.getText()).trim();
            final String categoryType = mBinding.spinnerCategory.getTag().toString();
            final ArrayList<String> ogList = mOgUtils.extractUrls(mBinding.layoutDiscussion.editTextWritePost.getText().toString().trim());

            Observable.create(new ObservableOnSubscribe<PostData>() {
                @Override
                public void subscribe(ObservableEmitter<PostData> emitter) {
                    PostData postData = new PostData();
                    postData.setObjectId(null);
                    postData.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));

                    PostByUser fromUser = new PostByUser();
                    fromUser.setName(mAppUserModel.getApplicationUser().getName());
                    fromUser.setId(mAppUserModel.getObjectId());
                    fromUser.setRole(setUserRole());
                    postData.setFrom(fromUser);

                    PostToGroup toGroup = new PostToGroup();
                    toGroup.setName(mGroup.getGroupName());
                    toGroup.setId(mGroup.getObjectId());
                    postData.setTo(toGroup);
                    postData.setAlias(GeneralUtils.generateAlias("LNPostData", "" + mAppUserModel.getObjectId(), System.currentTimeMillis() + ""));
                    postData.setSyncStatus(SyncStatus.NOT_SYNC.toString());
                    postData.setUnread(false);
                    postData.setLastMessageTime(new Date());
                    postData.setoGDataList(ogList);
                    postData.setPostResources(FullScreenImage.getResourceArrayList(mAttachPathList));
                    postData.setPostText(postText);
                    postData.setPostType(categoryType);
                    postData.setLastUpdationTime(new Date());
                    postData.setBadgeAssigningEnabled(isBadgeAssigningEnabled());
                    mPostDataLearningModel.savePost(postData);
                    emitter.onNext(postData);
                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<PostData>() {
                        @Override
                        public void accept(PostData postData) throws Exception {
                            dialog.dismiss();
                            mBinding.layoutDiscussion.editTextWritePost.clearComposingText();
                            mRxBus.send(new LoadNewPostCreatedEvent());
                            closeActivity();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            dialog.dismiss();
                            SnackBarUtils.showColoredSnackBar(getBaseContext(), mBinding.toolbar, getString(R.string.error_something_went_wrong), ContextCompat.getColor(getBaseContext(), R.color.colorRed));

                        }
                    });


        } else {
            mBinding.layoutDiscussion.editTextWritePost.requestFocus();
            mBinding.layoutDiscussion.editTextWritePost.setCursorVisible(true);
            SnackBarUtils.showColoredSnackBar(getBaseContext(), mBinding.toolbar, getString(R.string.label_empty_post), ContextCompat.getColor(getBaseContext(), R.color.colorRed));
        }
    }

    private boolean isBadgeAssigningEnabled() {
        if (!mGroup.getModerators().contains(new Moderator(mAppUserModel.getObjectId(), ""))) {
            return true;
        }
        return false;
    }

    private void menuAttachVisibility(boolean b) {
        if (mAttachMenuItem != null) {
            mAttachMenuItem.setVisible(b);
        }
    }

    private void menuActionAttach() {
        if (mAttachPathList.size() == 6) {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_attach_limit));
        } else {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                showImagePickOptionDialog(true);
            } else {
                showImagePickOptionDialog(false);
            }
            //showAttachmentOptionPopup(mToolbar);
        }
    }

    private void initializeResourceFolders(String learningNetworkPath) {
        mLearningNetworkFolder = learningNetworkPath;
    }

    private void initializeViews() {
        mPostScrollView = (ScrollView) findViewById(R.id.scrollview_post);
        mPostTypeSpinner = (Spinner) findViewById(R.id.spinner_category);
        mPostToGroupSpinner = (Spinner) findViewById(R.id.spinner_group);
        mPostEditText = (EditText) findViewById(R.id.editText_write_post);
        mDiscussionLayout = (LinearLayout) findViewById(R.id.layout_discussion);
        mResourceRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewResource);
        mGroupNameTextView = (TextView) findViewById(R.id.textViewGroupName);
        mViewFocus = (View) findViewById(R.id.view_focus);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initializeUiAndListeners() {

        getWindow().setStatusBarColor(mColor);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setBackgroundColor(mColor);
        setTitle(getString(R.string.label_create_post));
        final ArrayList<String> postTypeList = new ArrayList<>();
        if (PermissionPrefsCommon.getPostCreateReferencePermission(this)) {
            postTypeList.add(PostDataType.TYPE_REFERENCE_POST.getPostDataType());
        }
        if (PermissionPrefsCommon.getPostCreateDiscussionPermission(this)) {
            postTypeList.add(PostDataType.TYPE_DISCUSSION.getPostDataType());
        }
        if (postTypeList != null && !postTypeList.isEmpty()) {
            mPostEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String typed = mPostEditText.getText().toString().trim();
                    ArrayList<String> urlArrayList = extractUrls(typed);

                    if (urlArrayList.size() > 0) {
                        if (!TextUtils.isEmpty(mFirstUrl)) {
                            if (!mFirstUrl.equals(urlArrayList.get(0))) {
                                mFirstUrl = urlArrayList.get(0);
                                //   mBinding.textViewUrl.setText(TextUtils.join("\n", urlArrayList));
                                fetchOgFromServer(urlArrayList);
                            }
                        } else {
                            mFirstUrl = urlArrayList.get(0);
                            //  mBinding.textViewUrl.setText(TextUtils.join("\n", urlArrayList));
                            fetchOgFromServer(urlArrayList);
                        }

                    } else {
                        mBinding.layoutOgCard.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            ArrayAdapter<String> mPostTypeAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.layout_spinner, R.id.txt_spinner, postTypeList);
            mPostTypeSpinner.setAdapter(mPostTypeAdapter);
            mPostTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mPostTypeSpinner.setTag(postTypeList.get(position));
                    if (mPostTypeSpinner.getSelectedItem().toString().trim().equals(PostDataType.TYPE_DISCUSSION.getPostDataType())) {
                        mDiscussionLayout.setVisibility(ViewPager.VISIBLE);
                    } else if (mPostTypeSpinner.getSelectedItem().toString().trim().equals(PostDataType.TYPE_EVENT.getPostDataType())) {
                        mDiscussionLayout.setVisibility(ViewPager.GONE);
                    }
                    hideSoftKeyboard(mPostEditText);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            mGroupNameTextView.setText(mGroup.getGroupName());
        } else {
            finish();
        }


    }

    public static ArrayList<String> extractUrls(String text) {
        String data[] = GeneralUtils.getArrayOfAllUrls(text);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(data));

        return list;
    }

    private void fetchOgFromServer(ArrayList<String> urlArrayList) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            try {
                mOgUtils.getOgDataFromServer(urlArrayList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OGMetaDataResponse>() {
                    @Override
                    public void accept(OGMetaDataResponse ogData) throws Exception {
                        Result response = new Result();
                        Result responseToSet = null;
                        for (int i = 0; i < ogData.getResults().size(); i++) {
                            response = ogData.getResults().get(i);
                            if (response.getOg().equals(true)) {
                                if (responseToSet == null) {
                                    responseToSet = response;
                                }
                            }
                        }
                        if (responseToSet != null) {
                            final Result finalResponseToSet = responseToSet;
                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() {

                                    showOgView(finalResponseToSet);


                                }
                            });
                        } else if (response != null) {

                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() {
                                    mBinding.layoutOgCard.setVisibility(View.GONE);
                                    AnimationUtils.pushDownExit(getBaseContext(), mBinding.layoutOgCard);

                                }
                            });
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
            } catch (Exception t) {
                t.printStackTrace();
                Log.e("OgIconData", "err fetching getOgIconData" + t.toString());
            }

        }

    }

    private void showOgView(final Result finalResponseToSet) {
        mBinding.layoutOgCard.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.layoutOgCard);
        Picasso.with(getBaseContext()).load(finalResponseToSet.getOgMeta().getOgImage().getUrl()).placeholder(R.drawable.icon_og_broken).resize(90, 90).centerCrop().into(mBinding.imageViewOg);
        mBinding.textViewOgTitle.setText(finalResponseToSet.getOgMeta().getOgTitle());
        mBinding.textViewOgUrl.setText(finalResponseToSet.getUrl());
        mBinding.textViewOgDescription.setText(finalResponseToSet.getOgMeta().getOgTitle());
        mBinding.layoutOgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomChromeTabHelper.loadCustomDataUsingColorResource(CreatePostActivity.this, finalResponseToSet.getUrl(), R.color.colorLearningNetworkPrimary);
            }
        });
    }

    private String setUserRole() {
        UserProfile userProfile = mAppUserModel.getApplicationUser();
        if (userProfile.getRole() != null && !TextUtils.isEmpty(userProfile.getRole().getName())) {
            return userProfile.getRole().getName();
        } else {
            return ConstantUtil.BLANK;

        }
    }

    private void closeActivity() {

        hideSoftKeyboard(mPostEditText);
        mAttachPathList.clear();
        mAttachUriList.clear();
        finish();
    }

    private void showExitConfirmationDialog() {

        new AlertDialog.Builder(this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    /**
     * show image pick option dialog
     *
     * @param isCameraAvailble
     */
    private void showImagePickOptionDialog(boolean isCameraAvailble) {

        final Dialog dialog = new Dialog(CreatePostActivity.this);
        final LayoutPickCaptureImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_pick_capture_image, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.colorDialogBackground)));
        if (isCameraAvailble) {
            binding.layoutCamera.setVisibility(View.VISIBLE);

        } else {
            binding.layoutCamera.setVisibility(View.GONE);

        }
        binding.layoutGalleryVideo.setVisibility(View.VISIBLE);

        AnimationUtils.fadeInFast(getBaseContext(), binding.layoutCamera);
        AnimationUtils.fadeInFast(getBaseContext(), binding.layoutGallery);
        AnimationUtils.fadeInFast(getBaseContext(), binding.layoutGalleryVideo);

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
                actionGalleryImageClick();
                dialog.dismiss();

            }
        });

        binding.layoutGalleryVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionGalleryVideoClick();
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
                    AndroidPermissions.checkCameraPermission(CreatePostActivity.this);
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

    private void actionGalleryImageClick() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                PackageManager pm = getPackageManager();
                int hasPerm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                    AndroidPermissions.checkStoragePermission(CreatePostActivity.this);
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

    private void actionGalleryVideoClick() {
        if (mAttachPathList != null && mAttachPathList.size() >= 1 && mAttachPathList.get(0).contains("jpg")) {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_cannot_attach_video));
        } else {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    PackageManager pm = getPackageManager();
                    int hasPerm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                    if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                        AndroidPermissions.checkStoragePermission(CreatePostActivity.this);
                    } else {
                        startVideoPick();
                    }
                } else {
                    startVideoPick();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAttachmentOptionPopup(View view) {

        int[] loc_int = new int[2];
        view.getLocationOnScreen(loc_int);
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + view.getWidth();
        location.bottom = location.top + view.getHeight() + 10;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_attachment_popup, null);

        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = ViewGroup.LayoutParams.MATCH_PARENT;

        LinearLayout mCameraButton = (LinearLayout) layout.findViewById(R.id.button_camera);
        LinearLayout mGalleryButton = (LinearLayout) layout.findViewById(R.id.button_gallery);
        LinearLayout mImageButton = (LinearLayout) layout.findViewById(R.id.button_image);
        LinearLayout mVideoButton = (LinearLayout) layout.findViewById(R.id.button_video);
        LinearLayout mAudioButton = (LinearLayout) layout.findViewById(R.id.button_audio);
        LinearLayout mDocumentButton = (LinearLayout) layout.findViewById(R.id.button_document);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            mCameraButton.setVisibility(View.GONE);
        }

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= 23) {
                        PackageManager pm = getPackageManager();
                        int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());

                        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                            AndroidPermissions.checkCameraPermission(CreatePostActivity.this);
                        } else {
                            createCameraDirectory();
                        }
                    } else {
                        createCameraDirectory();

                    }
                    attachmentPopUpDismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this)
                .setMessage(R.string.file_explorer_install_message)
                .setTitle(R.string.message);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    AndroidPermissions.checkAndRequestPermission(CreatePostActivity.this);
                    if (AndroidPermissions.checkAndRequestPermission(CreatePostActivity.this) == true) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        try {
                            startActivityForResult(Intent.createChooser(intent, "Select a File to Post"), FILE_PICK_REQUEST);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a Dialog
                            Toast.makeText(getBaseContext(), "Please install a File Manager.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    try {
                        startActivityForResult(Intent.createChooser(intent, "Select a File to Post"), FILE_PICK_REQUEST);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog
                        Toast.makeText(getBaseContext(), "Please install a File Manager.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= 23) {
                        PackageManager pm = getPackageManager();
                        int hasPerm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                            AndroidPermissions.checkStoragePermission(CreatePostActivity.this);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, IMAGE_PICK);

                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, IMAGE_PICK);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    builder.show();
                }
                attachmentPopUpDismiss();
            }
        });

        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAttachPathList != null && mAttachPathList.size() >= 1 && mAttachPathList.get(0).contains("jpg")) {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_cannot_attach_video));
                } else {
                    try {
                        if (Build.VERSION.SDK_INT >= 23) {
                            PackageManager pm = getPackageManager();
                            int hasPerm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                                AndroidPermissions.checkStoragePermission(CreatePostActivity.this);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, VIDEO_PICK);

                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, VIDEO_PICK);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        builder.show();
                    }
                }
                attachmentPopUpDismiss();

            }
        });

        mAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Post"), FILE_PICK_REQUEST);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(getBaseContext(), "Please install a File Manager.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDocumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    PackageManager pm = getPackageManager();
                    int hasPerm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                    if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                        AndroidPermissions.checkStoragePermission(CreatePostActivity.this);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("application/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, FILE_PICK_REQUEST);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("application/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, FILE_PICK_REQUEST);
                }
//                Intent intent = new Intent();
//                intent.setType("application/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, FILE_PICK_REQUEST);
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(Intent.createChooser(intent, "Select a File to Post"), FILE_PICK_REQUEST);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    // Potentially direct the user to the Market with a Dialog
//                    Toast.makeText(getBaseContext(), "Please install a File Manager.",
//                            Toast.LENGTH_SHORT).show();
//                }
                //popupMenu.dismiss();
            }
        });

        popupMenu = new PopupWindow(getBaseContext());
        popupMenu.setContentView(layout);
        popupMenu.setWidth(popupWidth);
        popupMenu.setHeight(popupHeight);
        popupMenu.setFocusable(true);
        popupMenu.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        popupMenu.setOutsideTouchable(true);
        popupMenu.setAnimationStyle(android.R.style.Animation_Dialog);
        popupMenu.showAtLocation(view, Gravity.NO_GRAVITY, Gravity.CENTER_HORIZONTAL, location.bottom);
        mViewFocus.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
        popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupMenu.dismiss();
                mViewFocus.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));
            }
        });


    }

    /**
     * intent action to pick image from gallery
     */
    private void startImagePick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK);
    }

    /**
     * intent action to pick video from gallery
     */
    private void startVideoPick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, VIDEO_PICK);

    }

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

    private void startImageCapture(File saveFolder) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float ratio = ((float) metrics.heightPixels / (float) metrics.widthPixels);
        new MaterialCamera(this)
                .allowRetry(true)
                .autoSubmit(false)
                .saveDir(saveFolder)
                .primaryColor(mPrimaryColor)
                .showPortraitWarning(true)
                .defaultToFrontFacing(false)
                .retryExits(false)
                .videoPreferredAspect(9f / 16f)
                .maxAllowedFileSize(1024 * 1024 * 5)
                .iconRecord(R.drawable.mcam_action_capture)
                .iconFrontCamera(R.drawable.mcam_camera_front)
                .iconRearCamera(R.drawable.mcam_camera_rear)
                .labelRetry(R.string.mcam_retry)
                .labelConfirm(R.string.label_edit_photo)
                .stillShot()
                .start(IMAGE_CAPTURE);
    }

    private void attachmentPopUpDismiss() {
        popupMenu.dismiss();
        mViewFocus.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));
    }

//    File createImageFile() throws IOException {
//
//        File tempPath = new File(mBaseFolder + THUMB_FILE_DIRECTORY);
//        File tempFile = File.createTempFile("image", ".jpg", tempPath);
//        if (!tempFile.exists()) {
//            tempFile.getParentFile().mkdirs();
//        }
//
//        return tempFile;
//
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {

                final File file = new File(data.getData().getPath());
                Uri cameraUri = Uri.fromFile(file);
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
                    String picturePathOriginal = resultUri.getPath();
                    String strPath = FileUtils.copyFiles(picturePathOriginal, mBaseFolder, mLearningNetworkFolder + File.separator + "images", String.valueOf(System.currentTimeMillis()) + ".jpg");
                    if (!TextUtils.isEmpty(strPath))
                        addFileToPreviewLayout(strPath, resultUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        } else if (data != null && requestCode == VIDEO_PICK && resultCode == RESULT_OK) {
            Uri selectedVideo = data.getData();
            if (FileChooser.fileSize(getBaseContext(), selectedVideo) < 10) {
                String videoPathOriginal = FileChooser.getPath(this, selectedVideo);
                // if (videoPathOriginal.endsWith(".mp4")) {
                String extension = "";

                int i = videoPathOriginal.lastIndexOf('.');
                if (i > 0) {
                    extension = videoPathOriginal.substring(i + 1);
                }
                if (!TextUtils.isEmpty(extension)) {
                    String strPath = FileUtils.copyFiles(videoPathOriginal, mBaseFolder, mLearningNetworkFolder, String.valueOf(System.currentTimeMillis()) + "." + extension);
                    if (!TextUtils.isEmpty(strPath)) addFileToPreviewLayout(strPath, selectedVideo);
//                } else {
//                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.format_not_supported));
//                }
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.valid_file));

                }
            } else {
                ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit_10MB));
            }
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

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
                    .useSourceImageAspectRatio()
                    .withOptions(options)
                    .start(this);

        } else {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit_10MB));
        }
    }

    /**
     * Add image to attachment layout
     *
     * @param selectedFilePath
     * @param fileUri
     */
    private void addFileToPreviewLayout(final String selectedFilePath, Uri fileUri) {
        if (mAttachPathList == null) {
            mAttachPathList = new ArrayList<>();
            mAttachUriList = new ArrayList<>();
        }

        mAttachPathList.add("file://" + mBaseFolder + File.separator + selectedFilePath);
        mAttachUriList.add(fileUri);

        if (mAttachPathList.size() > 0) {
            mResourceRecyclerView.setVisibility(View.VISIBLE);
            String fileType = URLConnection.guessContentTypeFromName(selectedFilePath);
            if (fileType.contains("video") || fileType.contains("pdf") || fileType.contains("doc")) {
                mResourceRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
            } else {
                if (getBaseContext().getResources().getBoolean(R.bool.isTablet)) {
                    mResourceRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
                } else {
                    mResourceRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
                }
            }

            mResourceGridAdapter = new ResourceGridAdapter(CreatePostActivity.this);
            mResourceRecyclerView.setAdapter(mResourceGridAdapter);
            //mResourceGridAdapter.notifyDataSetChanged();

            if (!URLConnection.guessContentTypeFromName(mAttachPathList.get(0)).contains("image")) {
                menuAttachVisibility(false);
            }

        } else if (mAttachPathList.size() == 6) {
            menuAttachVisibility(false);
        }

    }

    /**
     * method to hide soft keyboard
     *
     * @param mPostEditText
     */
    public void hideSoftKeyboard(EditText mPostEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPostEditText.getWindowToken(), 0);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSubscription != null) mSubscription.dispose();
    }

    private class ResourceGridAdapter extends RecyclerView.Adapter<ResourceGridAdapter.ViewHolder> {

        private Context mContext;

        public ResourceGridAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public ResourceGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_attach_file_view, parent, false);
            ResourceGridAdapter.ViewHolder mViewHolder = new ResourceGridAdapter.ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ResourceGridAdapter.ViewHolder holder, final int position) {
            String filePath = mAttachPathList.get(position);
            String fileType = URLConnection.guessContentTypeFromName(filePath);
            if (fileType.contains("image")) {
                File imageFile;
                imageFile = new File(FileUtils.getPathFromFilePath(filePath));
                Bitmap bitmap = getScaledBitmapFromPath(getResources(), imageFile.getAbsolutePath());

                holder.mResourceImageView.setImageBitmap(bitmap);
                //FullScreenImage.checkPathOrUrl(mContext, filePath, holder.mResourceImageView);
//                Picasso.with(mContext).load(filePath).into(holder.mResourceImageView);
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_image_white);
            } else if (fileType.contains("video")) {
                Bitmap mBitmap = getScaledBitmapFromPath(mContext.getResources(), filePath);
                holder.mResourceImageView.setImageBitmap(mBitmap);
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_video_white);
            } else if (fileType.contains("audio")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_audio_white);
            } else if (fileType.contains("pdf")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_pdf_white);
            } else if (fileType.contains("doc")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_document_white);
            } else {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_file_white);
            }

            holder.mRemoveResourceImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Removed Attachment:", "" + position);
                    mAttachPathList.remove(position);
                    mAttachUriList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());

                    if (mAttachPathList.size() == 0) {
                        mResourceRecyclerView.setVisibility(View.GONE);
                        mAttachPathList.clear();
                        mAttachUriList.clear();

                    }
                    menuAttachVisibility(true);

                }
            });

            holder.mResourceImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mimeType = URLConnection.guessContentTypeFromName(mAttachPathList.get(position));
                    if (mimeType.contains("image")) {
                        FullScreenImage.setUpFullImageView(CreatePostActivity.this, position, true, true, false, FullScreenImage.getResourceArrayList(mAttachPathList));
                    } else if (mimeType.contains("video")) {
                        Resource item = new Resource();
                        item.setType("video");
                        item.setUrlMain(mAttachPathList.get(position));
                        mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mAttachPathList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            View mRootView;
            ImageView mResourceImageView, mResourceTypeImageView, mRemoveResourceImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mResourceImageView = (ImageView) mRootView.findViewById(R.id.imageView_attach);
                mResourceTypeImageView = (ImageView) mRootView.findViewById(R.id.imageViewFileType);
                mRemoveResourceImageView = (ImageView) mRootView.findViewById(R.id.imageView_remove_attachment);
            }
        }

    }

}
