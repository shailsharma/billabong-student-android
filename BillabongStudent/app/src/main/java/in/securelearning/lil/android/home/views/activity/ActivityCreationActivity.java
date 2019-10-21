package in.securelearning.lil.android.home.views.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialcamera.MaterialCamera;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCalendarActivityCreationBinding;
import in.securelearning.lil.android.app.databinding.LayoutPickCaptureImageBinding;
import in.securelearning.lil.android.base.constants.EventType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.Location;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.DialogUtils;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.KeyBoardUtil;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.LoadNewEventCreated;
import in.securelearning.lil.android.home.model.CalendarEventModel;
import in.securelearning.lil.android.home.views.widget.AndroidPermissions;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.syncadapter.dataobject.FileChooser;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;

import static in.securelearning.lil.android.base.utils.FileUtils.copyFiles;
import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Chaitendra on 3/22/2017.
 */

public class ActivityCreationActivity extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    CalendarEventModel calendarEventModel;
    @Inject
    RxBus mRxBus;
    private static final int CAMERA_REQUEST = 102;
    private static final int IMAGE_REQUEST = 103;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int IMAGE_PICK = 104;
    private final static int IMAGE_CAPTURE = 105;
    public static final String THUMB_FILE_DIRECTORY = File.separator + "tempActivityImages";
    public static final String THUMB_FILE_PATH = File.separator + "tempActivityImages" + File.separator + "tempImage";
    private int mPrimaryColor;
    private String mBaseFolder, mCalendarActivityPath;
    private ArrayList<Group> mGroupArrayList = new ArrayList<>();
    private AlertDialog mGroupSelectDialog;
    private int TIME_DIALOG_ID = 1001;
    private int hour, minute, timeClick;
    private PopupWindow popupMenu;
    private ArrayList<String> mAttachPathList = new ArrayList<>();
    private ArrayList<Uri> mAttachUriList = new ArrayList<>();
    private int intStartSelectedIndex, intEndSelectedIndex;
    public static final int SPINNER_PERIOD_INSTANCE = 501;
    private String mTitle, mGroup, mLocation, mStartDate, mStartPeriod, mEndPeriod;
    private LayoutCalendarActivityCreationBinding mBinding;
    private MenuItem mAttachMenuItem;

    @Override
    public void onBackPressed() {
        if (mBinding.edittextEventName.getText().toString().isEmpty() &&
                mBinding.edittextEventDescription.getText().toString().isEmpty() &&
                mBinding.edittextEventLocation.getText().toString().isEmpty() &&
                mBinding.edittextEventStartDate.getText().toString().isEmpty() &&
                mBinding.edittextEventStartPeriod.getText().toString().isEmpty() &&
                mBinding.edittextEventEndPeriod.getText().toString().isEmpty() &&
                mBinding.edittextEventAddInvitees.getText().toString().isEmpty() &&
                mAttachPathList.isEmpty()) {
            closeActivity();
        } else {
            new AlertDialog.Builder(ActivityCreationActivity.this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            closeActivity();
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_calendar_activity_creation);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBaseFolder = getFilesDir().getAbsolutePath();
        mCalendarActivityPath = getString(R.string.pathCalendarActivity);
        mPrimaryColor = ContextCompat.getColor(getBaseContext(), R.color.colorActivities);
        initializeGroupSelectViews();
        initializeUiAndClickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar_event_creation, menu);
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
                menuAttach();
                return true;

            case R.id.actionCreate:
                menuCreateEvent();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        if (data != null) {
//            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                Uri cameraUri = FileChooser.getCameraImageUri(getBaseContext(), photo);
//                String strPath = copyFiles(FileChooser.getPath(getBaseContext(), cameraUri), mBaseFolder, mLearningNetworkFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
//                addFileToPreviewLayout(strPath, cameraUri);
//            } else if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//                Uri selectedImage = data.getData();
//                if (FileChooser.fileSize(getBaseContext(), selectedImage) < 10) {
//                    String[] filePath = {MediaStore.Images.Media.DATA};
//                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                    c.moveToFirst();
//                    int columnIndex = c.getColumnIndex(filePath[0]);
//                    String picturePathOriginal = c.getString(columnIndex);
//                    c.close();
//
//                    String strPath = copyFiles(picturePathOriginal, mBaseFolder, mLearningNetworkFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
//                    addFileToPreviewLayout(strPath, selectedImage);
//                } else {
//                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit_10MB));
//                }
//
//            }
//
//        }

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
                    String picturePathOriginal = resultUri.getPath();
                    String strPath = copyFiles(picturePathOriginal, mBaseFolder, mCalendarActivityPath, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    addFileToPreviewLayout(strPath, resultUri);
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

    private void menuAttach() {
        if (mAttachPathList.size() == 6) {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_attach_limit));
        } else {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                showImagePickOptionDialog();
            } else {
                actionGalleryClick();
            }
            //showAttachmentOptionPopup(mBinding.toolbar);
        }
    }

    private void menuCreateEvent() {
        if (!validateTitle()) {
            return;
        }
        if (!validateGroup()) {
            return;
        }
        if (!validateLocation()) {
            return;
        }
        if (!validateStartDate()) {
            return;
        }
        if (!mBinding.checkBoxAllDay.isChecked()) {
            if (!validateStartPeriod()) {
                return;
            }
            if (!validateEndPeriod()) {
                return;
            } else {
                performActivityCreation();
            }
        } else {
            performActivityCreation();
        }
    }

    private void menuAttachVisibility(boolean b) {
        if (mAttachMenuItem != null) {
            mAttachMenuItem.setVisible(b);
        }
    }

    private void initializeUiAndClickListeners() {
        getWindow().setStatusBarColor(mPrimaryColor);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.new_act));
        mBinding.recyclerViewResource.setNestedScrollingEnabled(false);

        long dateTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        SimpleDateFormat sdfDate = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        String strCurrentDate = sdfDate.format(dateTime);
        mBinding.edittextEventStartDate.setText(strCurrentDate);


        mBinding.edittextEventStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                showDatePickerDialog(mBinding.edittextEventStartDate);
            }
        });


        mBinding.checkBoxAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mBinding.layoutPeriodSelection.setVisibility(View.GONE);

                } else {
                    mBinding.layoutPeriodSelection.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.edittextEventStartPeriod.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideSoftKeyboard(view);
                        popupWindowEditText(getPeriodList(), mBinding.edittextEventStartPeriod, PeriodAdapter.SPINNER_PERIOD);
                    }
                });

        mBinding.edittextEventEndPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBinding.edittextEventStartPeriod.getText().toString().trim().isEmpty()) {

//                    if (intSelectedIndex > 0)
//                        intSelectedIndex = intSelectedIndex - 1;

                    // ArrayList<String> endPeriodList = new ArrayList<String>(getPeriodList().subList(intSelectedIndex, getPeriodList().size()));
                    hideSoftKeyboard(view);
                    popupWindowEditText(getPeriodList(), mBinding.edittextEventEndPeriod, PeriodAdapter.SPINNER_PERIOD);
                } else {
                    mBinding.inputLayoutStartPeriod.setError(getString(R.string.alert_start_period));
                }
            }
        });

        mBinding.edittextEventAddInvitees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mGroupSelectDialog.show();
                KeyBoardUtil.hideSoftKeyboard(getCurrentFocus(), getBaseContext());

            }
        });

    }

    private ArrayList getPeriodList() {
        final ArrayList<String> periodList = new ArrayList<String>();
        periodList.add("1st");
        periodList.add("2nd");
        periodList.add("3rd");
        periodList.add("4th");
        periodList.add("5th");
        periodList.add("6th");
        periodList.add("7th");
        periodList.add("8th");
        periodList.add("9th");
        periodList.add("10th");

        return periodList;
    }

    private boolean validateTitle() {
        mTitle = mBinding.edittextEventName.getText().toString().trim();
        if (mTitle.isEmpty()) {
            mBinding.inputLayoutName.setError(getString(R.string.alert_title));
            return false;
        } else {
            mBinding.edittextEventName.clearFocus();
            mBinding.inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateGroup() {
        mGroup = mBinding.edittextEventAddInvitees.getText().toString().trim();
        if (mGroup.isEmpty()) {
            mBinding.inputLayoutInvitees.setError(getString(R.string.alert_invitees));
            return false;
        } else {
            mBinding.edittextEventAddInvitees.clearFocus();
            mBinding.inputLayoutInvitees.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLocation() {
        mLocation = mBinding.edittextEventLocation.getText().toString().trim();
        if (mLocation.isEmpty()) {
            mBinding.inputLayoutLocation.setError(getString(R.string.alert_location));
            mBinding.edittextEventLocation.requestFocus();
            KeyBoardUtil.showSoftKeyboard(mBinding.edittextEventLocation, getBaseContext());
            return false;
        } else {
            mBinding.edittextEventLocation.clearFocus();
            mBinding.inputLayoutLocation.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateStartDate() {
        mStartDate = mBinding.edittextEventStartDate.getText().toString().trim();
        if (mStartDate.isEmpty()) {
            mBinding.inputLayoutStartDate.setError(getString(R.string.alert_start_date));
            return false;
        } else {
            mBinding.edittextEventStartDate.clearFocus();
            mBinding.inputLayoutStartDate.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateStartPeriod() {
        mStartPeriod = mBinding.edittextEventStartPeriod.getText().toString().trim();
        if (mStartPeriod.isEmpty()) {
            mBinding.inputLayoutStartPeriod.setError(getString(R.string.alert_start_period));
            return false;
        } else {
            mBinding.edittextEventStartPeriod.clearFocus();
            mBinding.inputLayoutStartPeriod.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEndPeriod() {
        mEndPeriod = mBinding.edittextEventEndPeriod.getText().toString().trim();
        if (mEndPeriod.isEmpty()) {
            mBinding.inputLayoutEndPeriod.setError(getString(R.string.alert_end_period));
            return false;
        } else {
            mBinding.edittextEventEndPeriod.clearFocus();
            mBinding.inputLayoutEndPeriod.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * show image pick option dialog
     */
    private void showImagePickOptionDialog() {

        final Dialog dialog = new Dialog(ActivityCreationActivity.this);
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
                    AndroidPermissions.checkCameraPermission(ActivityCreationActivity.this);
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
                    AndroidPermissions.checkStoragePermission(ActivityCreationActivity.this);
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
                    .withOptions(options)
                    .start(this);

        } else {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit_10MB));
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

        mImageButton.setVisibility(View.GONE);
        mVideoButton.setVisibility(View.GONE);
        mAudioButton.setVisibility(View.GONE);
        mDocumentButton.setVisibility(View.GONE);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    PackageManager pm = getPackageManager();
                    int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());

                    if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                        checkCameraPermission();
                    } else {

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);

                    }
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                dismissAttachmentPopUp();

            }
        });

        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    PackageManager pm = getPackageManager();
                    int hasPerm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                    if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                        checkStoragePermission();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, IMAGE_REQUEST);


                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST);

                }
                dismissAttachmentPopUp();
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
        popupMenu.showAtLocation(view, Gravity.NO_GRAVITY, Gravity.CENTER_HORIZONTAL, location.bottom - 9);
        mBinding.viewFocus.setVisibility(View.VISIBLE);
        popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismissAttachmentPopUp();
            }
        });

    }

    private void dismissAttachmentPopUp() {
        popupMenu.dismiss();
        mBinding.viewFocus.setVisibility(View.GONE);
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
            //mAttachRelativePathList = new ArrayList<>();
            mAttachUriList = new ArrayList<>();
        }

        mAttachPathList.add("file://" + mBaseFolder + File.separator + selectedFilePath);
        //mAttachRelativePathList.add("file://" + mBaseFolder + File.separator + selectedFilePath);
        mAttachUriList.add(fileUri);

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

            ResourceGridAdapter mResourceGridAdapter = new ResourceGridAdapter(getBaseContext());
            mBinding.recyclerViewResource.setAdapter(mResourceGridAdapter);
            mResourceGridAdapter.notifyDataSetChanged();

            if (!URLConnection.guessContentTypeFromName(mAttachPathList.get(0)).contains("image")) {
                menuAttachVisibility(false);
            }

        } else if (mAttachPathList.size() == 6) {
            menuAttachVisibility(false);
        }

    }

    private void initializeGroupSelectViews() {

        View mGroupSelectDialogView;
        final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        ViewGroup viewGroupPicker;

        final ArrayList<Group> groups = calendarEventModel.getGroupListEvent();
        mGroupSelectDialogView = getLayoutInflater().inflate(R.layout.layout_calendar_event_select_groups, null);

        viewGroupPicker = (ViewGroup) mGroupSelectDialogView.findViewById(R.id.layout_groups);
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            View view = getLayoutInflater().inflate(R.layout.layout_assign_resource_group_picker_dialog_item, null);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_group_picker);
            checkBox.setTag(i);
            checkBox.setText(group.getGroupName());
            checkBoxes.add(checkBox);
            viewGroupPicker.addView(view);
        }

        AlertDialog.Builder groupSelectDialogBuilder = new AlertDialog.Builder(this);
        groupSelectDialogBuilder.setView(mGroupSelectDialogView);
        groupSelectDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGroupArrayList.clear();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isChecked()) {

                        mGroupArrayList.add(groups.get(i));

//                                groupNameArrayList.add(groups.get(i).getGroupName());
                    }
//                            else {
//                                groupIdsArrayList.remove(groups.get(i).getObjectId());
//                                groupNameArrayList.remove(groups.get(i).getGroupName());
//                            }
                }

                if (mGroupArrayList.size() > 0) {
                    mBinding.edittextEventAddInvitees.setText(TextUtils.join(", ", mGroupArrayList));
                } else {
                    mBinding.edittextEventAddInvitees.setText("");
                }
            }
        });
        groupSelectDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        mGroupSelectDialog = groupSelectDialogBuilder.create();

    }

    /**
     * show the date picker dialog
     *
     * @param dateTextView the text view to show the selected date on
     */
    private void showDatePickerDialog(final TextView dateTextView) {
        Calendar calendar = new GregorianCalendar();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                long secondsSelected = DateUtils.getSecondsForMidnight(year, monthOfYear, dayOfMonth);
                if (DateUtils.isFutureDay(secondsSelected, TimeUnit.SECONDS)) {
                    dateTextView.setText(DateUtils.getSimpleDateStringFromSeconds(secondsSelected));
                } else {
                    showErrorMessage("Please select date after " + DateUtils.getCurrentDate());
                }

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(TimeUnit.SECONDS.toMillis(DateUtils.getSecondsForMidnightFromDate(new Date()) + 1000));
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        datePickerDialog.setCustomTitle(linearLayout);
        datePickerDialog.show();
    }

    /**
     * Pop up window for fields like event type, group.
     *
     * @param adapterData
     * @param viewBox
     * @param spinnerType
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void popupWindowEditText(final ArrayList adapterData, final EditText viewBox, final int spinnerType) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_calendar_simple_recycler_view, null);
        RecyclerView mEditTextDataListView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        mEditTextDataListView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        final PeriodAdapter arrayAdapter = new PeriodAdapter(getBaseContext(), adapterData, spinnerType);
        mEditTextDataListView.setAdapter(arrayAdapter);

        int popupWidth = viewBox.getWidth() - 20;
        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupEditBox = new PopupWindow(viewBox.getContext());
        popupEditBox.setContentView(layout);
        popupEditBox.setWidth(popupWidth);
        popupEditBox.setHeight(popupHeight);
        popupEditBox.setFocusable(true);

        int OFFSET_X = 10;
        int OFFSET_Y = viewBox.getHeight();

        popupEditBox.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupEditBox.setOutsideTouchable(true);
        popupEditBox.setElevation(10f);
        popupEditBox.setAnimationStyle(android.R.style.Animation_Dialog);

        int[] location = new int[2];
        viewBox.getLocationOnScreen(location);
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        popupEditBox.showAtLocation(layout, Gravity.NO_GRAVITY, point.x + OFFSET_X, point.y + OFFSET_Y);

        arrayAdapter.setItemClickAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewBox.setText(((TextView) view).getText().toString());
//                viewBox.setTag(view.getTag());
                String strPeriod = ((TextView) view).getText().toString();
                if (viewBox == mBinding.edittextEventStartPeriod) {
                    intStartSelectedIndex = Integer.parseInt(strPeriod.substring(0, strPeriod.length() - 2));
                    mBinding.edittextEventStartPeriod.setText(((TextView) view).getText().toString());
                    mBinding.edittextEventEndPeriod.setText(((TextView) view).getText().toString());
                    mBinding.inputLayoutStartPeriod.setErrorEnabled(false);
                    mBinding.edittextEventStartPeriod.clearFocus();
                    mBinding.inputLayoutEndPeriod.setErrorEnabled(false);
                    mBinding.edittextEventEndPeriod.clearFocus();

                } else if (viewBox == mBinding.edittextEventEndPeriod) {
                    intEndSelectedIndex = Integer.parseInt(strPeriod.substring(0, strPeriod.length() - 2));
                    if (intEndSelectedIndex < intStartSelectedIndex) {
                        ToastUtils.showToastAlert(getBaseContext(), "Select end period greater then start period");
                    } else {
                        mBinding.edittextEventEndPeriod.setText(((TextView) view).getText().toString());
                        mBinding.inputLayoutEndPeriod.setErrorEnabled(false);
                        mBinding.edittextEventEndPeriod.clearFocus();
                    }
                }

                popupEditBox.dismiss();


            }
        });

    }

    /**
     * after validating all fields- insert all values to CalendarEvent dataobject
     * save data object to database
     */
    private void performActivityCreation() {
        String description = Html.toHtml(mBinding.edittextEventDescription.getEditableText()).toString();
        final UserProfile userProfile = mAppUserModel.getApplicationUser();
        String userId = userProfile.getObjectId();
        Institution institution = userProfile.getAssociation();
        Institution institution1 = new Institution();
        institution1.setSplashThumbnail(null);
        institution1.setThumbnail(null);
        institution1.setId(institution.getId());
        institution1.setName(institution.getName());
        Date startDate = DateUtils.getFormattedDateFromString(mBinding.edittextEventStartDate.getText().toString().trim());
        String startDateISO = DateUtils.getISO8601DateStringFromDate(startDate);
        Location location = new Location();
        location.setCity(mBinding.edittextEventLocation.getText().toString().trim());
        ArrayList<Resource> resources = FullScreenImage.getResourceArrayList(mAttachPathList);
        String periodFrom = mBinding.edittextEventStartPeriod.getText().toString().trim();
        String periodTo = mBinding.edittextEventEndPeriod.getText().toString().trim();
        boolean isEndPeriodVisibile = mBinding.edittextEventEndPeriod.getVisibility() == View.VISIBLE;
        for (Group group : mGroupArrayList) {
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setAlias(GeneralUtils.generateAlias("CalendarActivity", userId, "" + System.currentTimeMillis()));
            calendarEvent.setObjectId(null);
            calendarEvent.setDescription(description);
            calendarEvent.setEventType(EventType.TYPE_ACTIVITY.getEventType());
            calendarEvent.setEventTitle(mTitle);
            calendarEvent.setLocation(location);
            calendarEvent.setStartDate(startDateISO);
            calendarEvent.setEndDate(startDateISO);
            GroupAbstract groupAbstract = new GroupAbstract();
            groupAbstract.setObjectId(group.getObjectId());
            groupAbstract.setName(group.getGroupName());
            calendarEvent.setGroupAbstract(groupAbstract);

            if (group.getGrade() != null && !TextUtils.isEmpty(group.getGrade().getId())) {
                calendarEvent.setGrade(group.getGrade());
            } else {
                calendarEvent.setGrade(null);
            }
            if (group.getSection() != null && !TextUtils.isEmpty(group.getSection().getId())) {
                calendarEvent.setSection(group.getSection());
            } else {
                calendarEvent.setSection(null);
            }
            if (group.getSubject() != null && !TextUtils.isEmpty(group.getSubject().getId())) {
                calendarEvent.setSubject(group.getSubject());
            } else {
                calendarEvent.setSubject(null);

            }
            calendarEvent.setCreatedBy(userId);
            calendarEvent.setInstitution(institution1);
            calendarEvent.setCreationTime(DateUtils.getISO8601DateStringFromDate(new Date()));
            calendarEvent.setSyncStatus(SyncStatus.NOT_SYNC.toString());
            calendarEvent.setAttachments(resources);

            if (mBinding.checkBoxAllDay.isChecked()) {
                calendarEvent.setAllDay(true);
            } else {
                if (isEndPeriodVisibile) {
                    calendarEvent.setPeriodFrom(periodFrom);
                    calendarEvent.setPeriodTo(periodTo);
                } else {
                    calendarEvent.setPeriodFrom(periodFrom);
                }

            }

            calendarEventModel.saveEvent(calendarEvent);
        }
        ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.activity_created));
        mRxBus.send(new LoadNewEventCreated(startDate));
        //MessageService.startActionDownloadCalendarEvent(getBaseContext(), calendarEvent.getAlias());
        finish();

    }

    /**
     * ask permission for camera if user denied at Login
     *
     * @return
     */
    private boolean checkCameraPermission() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(ActivityCreationActivity.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    /**
     * ask permission for storage if user denied at Login
     *
     * @return
     */
    private boolean checkStoragePermission() {
        int permissionStorage = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(ActivityCreationActivity.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    /**
     * adapter for event type and group items
     */
    public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.ViewHolder> {

        public static final int SPINNER_PERIOD = 101;

        ArrayList<String> mValues = new ArrayList<String>();
        Context mContext;
        View.OnClickListener mItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        private int viewType;


        public PeriodAdapter(Context context, ArrayList<String> dataList, int viewType) {
            mContext = context;
            mValues = dataList;
            this.viewType = viewType;
        }

        public void setItemClickAction(View.OnClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

        @Override
        public PeriodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_calendar_event_simple_text, parent, false);
            PeriodAdapter.ViewHolder vh = new PeriodAdapter.ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final PeriodAdapter.ViewHolder holder, final int position) {
            holder.mItemValueTxt.setOnClickListener(mItemClickListener);

            switch (viewType) {


                case SPINNER_PERIOD:

                    holder.mItemValueTxt.setText(String.valueOf(mValues.get(position)));

                    break;
                default:
                    holder.mItemValueTxt.setText("");
                    break;
            }


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private TextView mItemValueTxt;
            private View mRootView;

            public ViewHolder(View v) {
                super(v);
                mRootView = v;
                mItemValueTxt = (TextView) v.findViewById(R.id.textview_item);

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

                File imageFile;
                imageFile = new File(FileUtils.getPathFromFilePath(filePath));
                Bitmap bitmap = getScaledBitmapFromPath(getResources(), imageFile.getAbsolutePath());

                holder.mResourceImageView.setImageBitmap(bitmap);

//                Picasso.with(mContext).load(filePath).resize(300, 300).centerInside().into(holder.mResourceImageView);
                holder.mResourceTypeImageView.setImageResource(R.drawable.icon_image_white);
            } else if (fileType.contains("video")) {

                Bitmap bitmap = getScaledBitmapFromPath(mContext.getResources(), filePath);
                holder.mResourceImageView.setImageBitmap(bitmap);

//                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
//                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "any_Title", null);
//                Picasso.with(mContext).load(path).resize(300, 300).centerInside().into(holder.mResourceImageView);
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
                    notifyDataSetChanged();
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
                        FullScreenImage.setUpFullImageView(ActivityCreationActivity.this, position, true, true, false, FullScreenImage.getResourceArrayList(mAttachPathList));
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

    private void closeActivity() {
        hideSoftKeyboard(getCurrentFocus());
        mAttachPathList.clear();
        mAttachUriList.clear();
        finish();
    }

    /**
     * method to hide soft keyboard
     *
     * @param viewFocus
     */
    public void hideSoftKeyboard(View viewFocus) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);

    }

    /**
     * show error toast
     *
     * @param message
     */
    private void showErrorMessage(String message) {
        DialogUtils.showAlertDialog(ActivityCreationActivity.this, message, getString(R.string.alert_title_string), false);
    }

}
