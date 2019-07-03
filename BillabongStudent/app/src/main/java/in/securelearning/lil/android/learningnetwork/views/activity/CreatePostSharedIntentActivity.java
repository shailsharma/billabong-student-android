package in.securelearning.lil.android.learningnetwork.views.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCreatePostSharedIntentBinding;
import in.securelearning.lil.android.app.databinding.LayoutPickCaptureImageBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerviewPopupBinding;
import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.dataobjects.AppUser;
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
import in.securelearning.lil.android.base.utils.AppPrefs;
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
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.FileChooser;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.base.utils.FileUtils.copyFiles;
import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Chaitendra on 3/9/2017.
 */


public class CreatePostSharedIntentActivity extends AppCompatActivity {

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
    private static final int VIDEO_PICK = 103;
    private static final int IMAGE_PICK = 104;
    private final static int IMAGE_CAPTURE = 105;
    private final static int POST_TYPE_LIST = 201;
    private final static int GROUP_LIST = 202;
    public static final String THUMB_FILE_PATH = File.separator + "tempLNImage" + File.separator + "tempImage";
    public static final String THUMB_FILE_DIRECTORY = File.separator + "tempLNImage";
    private ArrayList<Group> mGroupsList = new ArrayList<>();
    private ArrayList<String> mAttachPathList = new ArrayList<>();
    private String mLearningNetworkFolder;
    private MenuItem mAttachMenuItem;
    private Group mGroup;
    private int mPrimaryColor = 0;
    private LayoutCreatePostSharedIntentBinding mBinding;
    private String mFirstUrl = "";
    private File mBaseFolder;
    private String mSelectedGroupId = "";
    private AlertDialog mLoginDialog;
    private String mGroupName, mPostType;

    public static Intent getIntentForCreatePost(Context context, String type, Bundle extras) {
        Intent intent = new Intent(context, CreatePostSharedIntentActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(type);
        intent.putExtras(extras);
        return intent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.includeEditText.editTextWritePost.getText().toString().isEmpty() && mAttachPathList.isEmpty()) {
            closeActivity();
        } else {
            showExitConfirmationDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUserLoggedIn();
        if (mGroupsList == null || mGroupsList.isEmpty()) {
            getGroupList();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_create_post_shared_intent);
        mPrimaryColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLN);
        mBaseFolder = getFilesDir();
        initializeResourceFolders(getString(R.string.pathLearningNetwork));
        setUpToolbar();
        initializeUiAndListeners();
        handleSharedIntent();

    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(mPrimaryColor);
        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void isUserLoggedIn() {
        if (!AppPrefs.isLoggedIn(getBaseContext())) {
            if (mLoginDialog == null || !mLoginDialog.isShowing()) {
                showLoginConfirmationDialog();
            }
        }
    }

    private void handleSharedIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {

            if (type.equals(ConstantUtil.MIME_TYPE_IMAGE)) {
                handleSharedTextIntent(intent);
            } else if (type.startsWith(ConstantUtil.MIME_TYPE_IMAGE)) {
                handleSharedImageIntent(intent);
            } else if (type.startsWith(ConstantUtil.MIME_TYPE_VIDEO)) {
                handleSharedVideoIntent(intent);
            }
        }

    }

    private void handleSharedImageIntent(Intent intent) {
        Uri imageUri = ShareCompat.IntentReader.from(CreatePostSharedIntentActivity.this).getStream();

        if (imageUri != null) {
            startCropping(imageUri);
        }

    }

    private void handleSharedVideoIntent(Intent intent) {
        try {
            Uri videoUri = ShareCompat.IntentReader.from(CreatePostSharedIntentActivity.this).getStream();
            if (videoUri != null) {
                String[] filePath = {MediaStore.Video.Media.DATA};
                Cursor c = getContentResolver().query(videoUri, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String videoPathOriginal = c.getString(columnIndex);
                c.close();
                if (videoPathOriginal.endsWith(".mp4")) {
                    String strPath = copyFiles(videoPathOriginal, mBaseFolder + "", mLearningNetworkFolder, String.valueOf(System.currentTimeMillis()) + ".mp4");
                    addFileToPreviewLayout(strPath);
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.format_not_supported));
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.cant_pick_this_file), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), getString(R.string.cant_receive_video_from_this_app), Toast.LENGTH_LONG).show();
            finish();
        }


    }

    private void handleSharedTextIntent(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (!TextUtils.isEmpty(sharedText)) {

            mBinding.includeEditText.editTextWritePost.requestFocus();
            mBinding.includeEditText.editTextWritePost.append(sharedText);
        }
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
                    String strPath = copyFiles(picturePathOriginal, mBaseFolder + "", mLearningNetworkFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    addFileToPreviewLayout(strPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        } else if (data != null && requestCode == VIDEO_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedVideo = data.getData();
            if (FileChooser.fileSize(getBaseContext(), selectedVideo) < 10) {
                String videoPathOriginal = FileChooser.getPath(this, selectedVideo);
                String extension = "";

                int i = videoPathOriginal.lastIndexOf('.');
                if (i > 0) {
                    extension = videoPathOriginal.substring(i + 1);
                }
                if (!TextUtils.isEmpty(extension)) {
                    String strPath = copyFiles(videoPathOriginal, mBaseFolder + "", mLearningNetworkFolder, String.valueOf(System.currentTimeMillis()) + "." + extension);
                    if (!TextUtils.isEmpty(strPath)) addFileToPreviewLayout(strPath);
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.valid_file));

                }
            } else {
                ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit));
            }
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private ArrayList<String> getPostTypeList() {
        final ArrayList<String> postTypeList = new ArrayList<>();
        if (PermissionPrefsCommon.getPostCreateReferencePermission(this)) {
            postTypeList.add(PostDataType.TYPE_REFERENCE_POST.getPostDataType());
        }
        if (PermissionPrefsCommon.getPostCreateDiscussionPermission(this)) {
            postTypeList.add(PostDataType.TYPE_DISCUSSION.getPostDataType());
        }
        return postTypeList;
    }

    @SuppressLint("CheckResult")
    private void getGroupList() {
        mPostDataLearningModel.getGroupForUser().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Group>() {
            @Override
            public void accept(Group group) throws Exception {
                mGroupsList.add(group);
                if (mGroupsList.isEmpty()) {
                    Toast.makeText(getBaseContext(), getString(R.string.error_no_groups), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String text = getString(R.string.selected_group) + " - " + mGroupsList.get(0).getGroupName();
                    mBinding.textViewSelectedGroup.setText(text);
                    mSelectedGroupId = mGroupsList.get(0).getObjectId();
                    mGroupName = mGroupsList.get(0).getGroupName();
                    mGroup = mGroupsList.get(0);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                if (!mGroupsList.isEmpty()) {
                    mSelectedGroupId = mGroupsList.get(0).getObjectId();
                    mGroup = mGroupsList.get(0);
                    String text = getString(R.string.selected_group) + " - " + mGroupsList.get(0).getGroupName();
                    mBinding.textViewSelectedGroup.setText(text);
                    mGroupName = mGroupsList.get(0).getGroupName();
                }

            }
        });
    }

    private void menuActionCreatePost() {
        if (!TextUtils.isEmpty(mBinding.includeEditText.editTextWritePost.getText().toString().trim())
                && !TextUtils.isEmpty(mPostType)
                && !TextUtils.isEmpty(mGroupName)) {
            PostData postData = new PostData();
            postData.setObjectId(null);
            postData.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));

            PostByUser fromUser = new PostByUser();
            fromUser.setName(mAppUserModel.getApplicationUser().getName());
            fromUser.setId(mAppUserModel.getObjectId());
            fromUser.setRole(setUserRole());
            postData.setFrom(fromUser);

            PostToGroup toGroup = new PostToGroup();
            toGroup.setName(mGroupName);
            toGroup.setId(mSelectedGroupId);
            postData.setTo(toGroup);
            postData.setAlias(GeneralUtils.generateAlias("LNPostData", "" + mAppUserModel.getObjectId(), System.currentTimeMillis() + ""));
            postData.setSyncStatus(SyncStatus.NOT_SYNC.toString());
            postData.setUnread(false);
            postData.setLastMessageTime(new Date());
            postData.setoGDataList(mOgUtils.extractUrls(mBinding.includeEditText.editTextWritePost.getText().toString().trim()));
            postData.setPostResources(FullScreenImage.getResourceArrayList(mAttachPathList));
            mBinding.includeEditText.editTextWritePost.clearComposingText();
            postData.setPostText(Html.toHtml(mBinding.includeEditText.editTextWritePost.getText()).toString().trim());
            postData.setPostType(mPostType);
            postData.setLastUpdationTime(new Date());
            postData.setBadgeAssigningEnabled(isBadgeAssigningEnabled());
            mPostDataLearningModel.savePost(postData);
            mRxBus.send(new LoadNewPostCreatedEvent());
            closeActivity();

        } else {
            mBinding.includeEditText.editTextWritePost.requestFocus();
            mBinding.includeEditText.editTextWritePost.setCursorVisible(true);
            SnackBarUtils.showColoredSnackBar(getBaseContext(), getCurrentFocus(), getString(R.string.label_empty_post), ContextCompat.getColor(getBaseContext(), R.color.colorRed));
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

    private void initializeUiAndListeners() {

        mBinding.toolbar.setBackgroundColor(mPrimaryColor);

        setTitle(getString(R.string.label_create_post));
        ArrayList<String> postType = getPostTypeList();
        if (postType != null && !postType.isEmpty()) {
            String text = getString(R.string.select_post_type) + " - " + postType.get(0);
            mBinding.textViewSelectedPostType.setText(text);
            mPostType = postType.get(0);
            mBinding.includeEditText.editTextWritePost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String typed = mBinding.includeEditText.editTextWritePost.getText().toString().trim();
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
                        mBinding.includeOgLayout.layoutOgCard.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mBinding.textViewSelectedGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGroupListDialog(mGroupsList, GROUP_LIST);
                }
            });


            mBinding.textViewSelectedPostType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getPostTypeList().size() > 1) {
                        showGroupListDialog(getPostTypeList(), POST_TYPE_LIST);
                    }
                }
            });
        } else {
            GeneralUtils.showToastShort(getBaseContext(), "You are not allowed to create post.");
            finish();
        }


    }

    public static ArrayList<String> extractUrls(String text) {
        String data[] = GeneralUtils.getArrayOfAllUrls(text);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(data));

        return list;
    }

    @SuppressLint("CheckResult")
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
                        } else {

                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() {
                                    mBinding.includeOgLayout.layoutOgCard.setVisibility(View.GONE);
                                    AnimationUtils.pushDownExit(getBaseContext(), mBinding.includeOgLayout.layoutOgCard);

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
        mBinding.includeOgLayout.layoutOgCard.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.includeOgLayout.layoutOgCard);
        Picasso.with(getBaseContext()).load(finalResponseToSet.getOgMeta().getOgImage().getUrl()).placeholder(R.drawable.icon_og_broken).resize(90, 90).centerCrop().into(mBinding.includeOgLayout.imageViewOg);
        mBinding.includeOgLayout.textViewOgTitle.setText(finalResponseToSet.getOgMeta().getOgTitle());
        mBinding.includeOgLayout.textViewOgUrl.setText(finalResponseToSet.getUrl());
        mBinding.includeOgLayout.textViewOgDescription.setText(finalResponseToSet.getOgMeta().getOgTitle());
        mBinding.includeOgLayout.layoutOgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomChromeTabHelper.loadCustomDataUsingColorResource(CreatePostSharedIntentActivity.this, finalResponseToSet.getUrl(), R.color.colorLearningNetworkPrimary);
            }
        });
    }

    private void showGroupListDialog(final ArrayList adapterData, int type) {
        LayoutRecyclerviewPopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_recyclerview_popup, null, false);
        Dialog dialog = new Dialog(CreatePostSharedIntentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        binding.textViewTitle.setVisibility(View.VISIBLE);
        binding.listviewEdittextData.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        if (type == POST_TYPE_LIST) {
            binding.textViewTitle.setText(getString(R.string.select_post_type));
            final PostTypeAdapter arrayAdapter = new PostTypeAdapter(getBaseContext(), adapterData, dialog);
            binding.listviewEdittextData.setAdapter(arrayAdapter);
        } else if (type == GROUP_LIST) {
            binding.textViewTitle.setText(getString(R.string.select_group));
            final GroupAdapter arrayAdapter = new GroupAdapter(getBaseContext(), adapterData, dialog);
            binding.listviewEdittextData.setAdapter(arrayAdapter);
        }

        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Double width = metrics.widthPixels * 0.80;
        Window win = dialog.getWindow();
        win.setLayout(width.intValue(), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

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

        hideSoftKeyboard(getCurrentFocus());
        mAttachPathList.clear();
        finish();
        startActivity(PostListActivity.getIntentForPostList(getBaseContext(), mSelectedGroupId, false));
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

    private void showLoginConfirmationDialog() {
        mLoginDialog = new AlertDialog.Builder(CreatePostSharedIntentActivity.this)
                .setCancelable(false)
                .setMessage(getString(R.string.error_not_logged_in))
                .setPositiveButton(R.string.title_activity_login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = LoginActivity.startLoginActivityFromPostActivity(getBaseContext(), LoginActivity.ACTION_LN_SHARE, getIntent().getType(), getIntent().getExtras());
                        finishAffinity();
                        int pendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(CreatePostSharedIntentActivity.this, pendingIntentId, intent,
                                PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        mLoginDialog.show();
    }

    private void showImagePickOptionDialog(boolean isCameraAvailble) {

        final Dialog dialog = new Dialog(CreatePostSharedIntentActivity.this);
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
                actionGalleryClick();
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
                    AndroidPermissions.checkCameraPermission(CreatePostSharedIntentActivity.this);
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
                    AndroidPermissions.checkStoragePermission(CreatePostSharedIntentActivity.this);
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
                        AndroidPermissions.checkStoragePermission(CreatePostSharedIntentActivity.this);
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
            }
        }
    }

    private void startImagePick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK);
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

//        Intent callCameraApplicationIntent = new Intent();
//        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        File photoFile = null;
//        try {
//            photoFile = createImageFile();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//
//        startActivityForResult(callCameraApplicationIntent, IMAGE_CAPTURE);

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

    private void startCropping(Uri uri) {
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), getString(R.string.cant_receive_image_from_this_app), Toast.LENGTH_LONG).show();
            finish();
        }


    }

    private void addFileToPreviewLayout(final String selectedFilePath) {
        if (mAttachPathList == null) {
            mAttachPathList = new ArrayList<>();
        }

        mAttachPathList.add("file://" + mBaseFolder + File.separator + selectedFilePath);

        if (mAttachPathList.size() > 0) {
            mBinding.recyclerViewResource.setVisibility(View.VISIBLE);
            String fileType = URLConnection.guessContentTypeFromName(selectedFilePath);
            if (fileType.contains("video") || fileType.contains("pdf") || fileType.contains("doc")) {
                mBinding.recyclerViewResource.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
            } else {
                if (getBaseContext().getResources().getBoolean(R.bool.isTablet)) {
                    mBinding.recyclerViewResource.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
                } else {
                    mBinding.recyclerViewResource.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
                }
            }

            ResourceGridAdapter resourceGridAdapter = new ResourceGridAdapter(CreatePostSharedIntentActivity.this);
            mBinding.recyclerViewResource.setAdapter(resourceGridAdapter);

            if (!URLConnection.guessContentTypeFromName(mAttachPathList.get(0)).contains("image")) {
                menuAttachVisibility(false);
            }

        } else if (mAttachPathList.size() == 6) {
            menuAttachVisibility(false);
        }

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
        private Dialog mGroupListDialog;
        private ArrayList<Group> groupList = new ArrayList<>();
        private Context mContext;

        public GroupAdapter(Context context, ArrayList adapterData, Dialog dialog) {
            this.mContext = context;
            this.mGroupListDialog = dialog;
            this.groupList = adapterData;
        }

        @Override
        public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_metadata_spinner_item, parent, false);
            GroupAdapter.ViewHolder viewHolder = new GroupAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Group group = groupList.get(position);
            holder.mItemTextView.setText(group.getGroupName());
            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = getString(R.string.selected_group) + " - " + holder.mItemTextView.getText().toString();
                    mBinding.textViewSelectedGroup.setText(title);
                    mBinding.textViewSelectedGroup.setTag(group.getObjectId());
                    mSelectedGroupId = group.getObjectId();
                    mGroupName = holder.mItemTextView.getText().toString();
                    mGroup = group;
                    mGroupListDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mItemTextView;
            private View mRootView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mItemTextView = (TextView) itemView.findViewById(R.id.textview_quiz_metadata_value);
            }
        }
    }

    private class PostTypeAdapter extends RecyclerView.Adapter<PostTypeAdapter.ViewHolder> {
        private Dialog mGroupListDialog;
        private ArrayList<String> typeList = new ArrayList<>();
        private Context mContext;

        public PostTypeAdapter(Context context, ArrayList adapterData, Dialog dialog) {
            this.mContext = context;
            this.mGroupListDialog = dialog;
            this.typeList = adapterData;
        }

        @Override
        public PostTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_metadata_spinner_item, parent, false);
            PostTypeAdapter.ViewHolder viewHolder = new PostTypeAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final String type = typeList.get(position);
            holder.mItemTextView.setText(type);
            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpannableString title = new SpannableString(getString(R.string.select_post_type) + " - " + holder.mItemTextView.getText().toString());
                    title.setSpan(new TypefaceSpan("sans-serif-condensed"), 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    mBinding.textViewSelectedPostType.setText(title);
                    mBinding.textViewSelectedGroup.setTag(title);
                    mPostType = holder.mItemTextView.getText().toString();
                    mGroupListDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return typeList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mItemTextView;
            private View mRootView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mItemTextView = (TextView) itemView.findViewById(R.id.textview_quiz_metadata_value);
            }
        }
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
                //FullScreenImage.checkPathOrUrl(mContext, filePath, holder.mResourceImageView);
                File imageFile;
                imageFile = new File(FileUtils.getPathFromFilePath(filePath));
                Bitmap bitmap = getScaledBitmapFromPath(getResources(), imageFile.getAbsolutePath());

                holder.mResourceImageView.setImageBitmap(bitmap);
//                Picasso.with(mContext).load(filePath).into(holder.mResourceImageView);
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_image_w);
            } else if (fileType.contains("video")) {
                Bitmap mBitmap = getScaledBitmapFromPath(mContext.getResources(), filePath);
                holder.mResourceImageView.setImageBitmap(mBitmap);
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_video_w);
            } else if (fileType.contains("audio")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_audio_w);
            } else if (fileType.contains("pdf")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_pdf_w);
            } else if (fileType.contains("doc")) {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_document_w);
            } else {
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_file_w);
            }

            holder.mRemoveResourceImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Removed Attachment:", "" + position);
                    mAttachPathList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());

                    if (mAttachPathList.size() == 0) {
                        mBinding.recyclerViewResource.setVisibility(View.GONE);
                        mAttachPathList.clear();

                    }
                    menuAttachVisibility(true);

                }
            });

            holder.mResourceImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mimeType = URLConnection.guessContentTypeFromName(mAttachPathList.get(position));
                    if (mimeType.contains("image")) {
                        FullScreenImage.setUpFullImageView(CreatePostSharedIntentActivity.this, position, true, true, FullScreenImage.getResourceArrayList(mAttachPathList));
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
