package in.securelearning.lil.android.home.views.activity;

import android.Manifest;
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
import android.widget.DatePicker;
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
import in.securelearning.lil.android.app.databinding.LayoutAttachFileViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutAttachmentPopupBinding;
import in.securelearning.lil.android.app.databinding.LayoutCalendarAnnouncementCreationBinding;
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

public class AnnouncementCreationActivity extends AppCompatActivity {
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    CalendarEventModel calendarEventModel;
    @Inject
    RxBus mRxBus;
    private static final int CAMERA_REQUEST = 102;
    private static final int IMAGE_REQUEST = 103;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    public static String USER_ID = "userId";
    private static final int IMAGE_PICK = 104;
    private final static int IMAGE_CAPTURE = 105;
    public static final String THUMB_FILE_DIRECTORY = File.separator + "tempAnnouncementImages";
    public static final String THUMB_FILE_PATH = File.separator + "tempAnnouncementImages" + File.separator + "tempImage";
    private int mPrimaryColor;
    private String mBaseFolder, mCalendarAnnouncementPath;
    private LayoutCalendarAnnouncementCreationBinding mBinding;
    private ArrayList<String> mAttachPathList = new ArrayList<>();
    private ArrayList<Uri> mAttachUriList = new ArrayList<>();
    private ArrayList<Group> mGroupArrayList = new ArrayList<>();
    private PopupWindow popupMenu;
    private AlertDialog mGroupSelectDialog;
    private String mTitle, mGroup, mLocation, mStartDate;
    private MenuItem mAttachMenuItem;

    @Override
    public void onBackPressed() {
        if (mBinding.edittextEventName.getText().toString().isEmpty() &&
                mBinding.edittextEventDescription.getText().toString().isEmpty() &&
                mBinding.edittextEventLocation.getText().toString().isEmpty() &&
                mBinding.edittextEventAddInvitees.getText().toString().isEmpty() &&
                mBinding.edittextEventStartDate.getText().toString().isEmpty() &&
                mAttachPathList.isEmpty()) {
            closeActivity();
        } else {

            new AlertDialog.Builder(AnnouncementCreationActivity.this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message))
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_calendar_announcement_creation);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBaseFolder = getFilesDir().getAbsolutePath();
        mCalendarAnnouncementPath = getString(R.string.pathCalendarAnnouncement);
        mPrimaryColor = ContextCompat.getColor(getBaseContext(), R.color.colorAnnouncement);
        initializeGroupSelectViews();
        initializeUIAndClickListeners();
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
                menuActionAttach();
                return true;

            case R.id.actionCreate:
                menuActionCreateEvent();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
                    String strPath = copyFiles(picturePathOriginal, mBaseFolder, mCalendarAnnouncementPath, String.valueOf(System.currentTimeMillis()) + ".jpg");
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

    private void menuActionAttach() {
        if (mAttachPathList.size() == 6) {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_attach_limit));
        } else {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                showImagePickOptionDialog();
            } else {
                actionGalleryClick();
            }
        }
    }

    private void menuActionCreateEvent() {
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
        } else {
            performAnnouncementCreation();
        }
    }

    private void menuAttachVisibility(boolean b) {
        if (mAttachMenuItem != null) {
            mAttachMenuItem.setVisible(b);
        }
    }

    private void initializeUIAndClickListeners() {

        setSupportActionBar(mBinding.toolbar);
        getWindow().setStatusBarColor(mPrimaryColor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.new_ann));

        mBinding.recyclerViewResource.setNestedScrollingEnabled(false);

        mBinding.edittextEventAddInvitees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mGroupSelectDialog.show();
                hideSoftKeyboard(getCurrentFocus());

            }
        });

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

    }

    /**
     * show image pick option dialog
     */
    private void showImagePickOptionDialog() {

        final Dialog dialog = new Dialog(AnnouncementCreationActivity.this);
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
                    AndroidPermissions.checkCameraPermission(AnnouncementCreationActivity.this);
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
                    AndroidPermissions.checkStoragePermission(AnnouncementCreationActivity.this);
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
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.file_size_limit));
        }
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

    private void initializeGroupSelectViews() {

        final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        final ArrayList<Group> groups = calendarEventModel.getGroupListEvent();

        View  groupSelectDialogView = getLayoutInflater().inflate(R.layout.layout_calendar_event_select_groups, null);

        ViewGroup viewGroupPicker = (ViewGroup) groupSelectDialogView.findViewById(R.id.layout_groups);
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
        groupSelectDialogBuilder.setView(groupSelectDialogView);
        groupSelectDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mGroupArrayList.clear();
                        for (int i = 0; i < checkBoxes.size(); i++) {
                            if (checkBoxes.get(i).isChecked()) {
                                mGroupArrayList.add(groups.get(i));

                            }

                        }

                        if (mGroupArrayList.size() > 0) {
                            mBinding.edittextEventAddInvitees.setText(TextUtils.join(", ", mGroupArrayList));
                        } else {
                            mBinding.edittextEventAddInvitees.setText("");
                        }
                    }
                }

        );
        groupSelectDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void
                    onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }

        );
        mGroupSelectDialog = groupSelectDialogBuilder.create();

    }

    private String join(CharSequence delimiter, ArrayList<GroupAbstract> groupAbstracts) {
        if (groupAbstracts.size() == 0) {
            return "";
        }

        if (groupAbstracts.size() == 1) {
            return groupAbstracts.get(0).getName();
        }

        String result = "";
        for (int i = 0; i < groupAbstracts.size(); i++) {
            if (!TextUtils.isEmpty(groupAbstracts.get(i).getName()))
                result += groupAbstracts.get(i).getName() + delimiter;
        }
        return result.trim();
    }

    private void performAnnouncementCreation() {
        String eventTitle = mBinding.edittextEventName.getText().toString().trim();
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
        for (Group group : mGroupArrayList) {
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setAlias(GeneralUtils.generateAlias("CalendarAnnouncement", userId, "" + System.currentTimeMillis()));
            calendarEvent.setObjectId(null);
            calendarEvent.setEventType(EventType.TYPE_ANNOUNCEMENT.getEventType());
            calendarEvent.setEventTitle(eventTitle);
            calendarEvent.setLocation(location);
            calendarEvent.setInstitution(institution1);
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
            calendarEvent.setDescription(description);
            calendarEvent.setCreatedBy(userId);
            calendarEvent.setCreationTime(DateUtils.getISO8601DateStringFromDate(new Date()));
            calendarEvent.setSyncStatus(SyncStatus.NOT_SYNC.toString());
            calendarEvent.setAllDay(false);
            calendarEvent.setAttachments(resources);
            calendarEventModel.saveEvent(calendarEvent);
        }
        ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.announcement_created));
        mRxBus.send(new LoadNewEventCreated(startDate));
        //MessageService.startActionDownloadCalendarEvent(getBaseContext(), calendarEvent.getAlias());
        finish();

    }

    private void showAttachmentOptionPopup(View view) {

        int[] loc_int = new int[2];
        view.getLocationOnScreen(loc_int);
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + view.getWidth();
        location.bottom = location.top + view.getHeight() + 10;
        LayoutAttachmentPopupBinding mPopupBinding;
        mPopupBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_attachment_popup, null, false);

        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        mPopupBinding.rootLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = ViewGroup.LayoutParams.MATCH_PARENT;

        mPopupBinding.buttonImage.setVisibility(View.GONE);
        mPopupBinding.buttonVideo.setVisibility(View.GONE);
        mPopupBinding.buttonAudio.setVisibility(View.GONE);
        mPopupBinding.buttonDocument.setVisibility(View.GONE);

        mPopupBinding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    PackageManager pm = getPackageManager();
                    int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());

                    if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                        checkCameraPermission();
                    } else {

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);

                    }
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                dismissAttachmentPopUp();

            }
        });

        mPopupBinding.buttonGallery.setOnClickListener(new View.OnClickListener() {
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
        popupMenu.setContentView(mPopupBinding.rootLayout);
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
            mAttachUriList = new ArrayList<>();
        }

        mAttachPathList.add("file://" + mBaseFolder + File.separator + selectedFilePath);
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

    private class ResourceGridAdapter extends RecyclerView.Adapter<ResourceGridAdapter.ViewHolder> {

        private Context mContext;

        public ResourceGridAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public ResourceGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutAttachFileViewBinding mViewBinding;
            mViewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_attach_file_view, parent, false);
            return new AnnouncementCreationActivity.ResourceGridAdapter.ViewHolder(mViewBinding);
        }

        @Override
        public void onBindViewHolder(ResourceGridAdapter.ViewHolder holder, final int position) {
            String filePath = mAttachPathList.get(position);

            String fileType = URLConnection.guessContentTypeFromName(filePath);
            if (fileType.contains("image")) {

                File imageFile;
                imageFile = new File(FileUtils.getPathFromFilePath(filePath));
                Bitmap bitmap = getScaledBitmapFromPath(getResources(), imageFile.getAbsolutePath());

                holder.viewBinding.imageViewAttach.setImageBitmap(bitmap);

//                Picasso.with(mContext).load(filePath).resize(300, 300).centerInside().into(holder.viewBinding.imageViewAttach);
                holder.viewBinding.imageViewFileType.setImageResource(R.drawable.icon_image_white);
            } else if (fileType.contains("video")) {
                Bitmap bitmap = getScaledBitmapFromPath(mContext.getResources(), filePath);
                holder.viewBinding.imageViewAttach.setImageBitmap(bitmap);

//                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
//                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "any_Title", null);
//                Picasso.with(mContext).load(path).resize(300, 300).centerInside().into(holder.viewBinding.imageViewAttach);
                holder.viewBinding.imageViewFileType.setImageResource(R.drawable.icon_video_white);
            } else if (fileType.contains("audio")) {
                holder.viewBinding.imageViewFileType.setImageResource(R.drawable.icon_audio_white);
            } else if (fileType.contains("pdf")) {
                holder.viewBinding.imageViewFileType.setImageResource(R.drawable.icon_pdf_white);
            } else if (fileType.contains("doc")) {
                holder.viewBinding.imageViewFileType.setImageResource(R.drawable.icon_document_white);
            } else {
                holder.viewBinding.imageViewFileType.setImageResource(R.drawable.icon_file_white);
            }

            holder.viewBinding.imageViewRemoveAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Removed Attachment:", "" + position);
                    mAttachPathList.remove(position);
                    mAttachUriList.remove(position);

                    notifyDataSetChanged();
                    if (mAttachPathList.size() == 0) {
                        mBinding.recyclerViewResource.setVisibility(View.GONE);
                        mAttachUriList.clear();
                        mAttachPathList.clear();

                    }
                    menuAttachVisibility(true);

                }
            });

            holder.viewBinding.imageViewAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mimeType = URLConnection.guessContentTypeFromName(mAttachPathList.get(position));
                    if (mimeType.contains("image")) {
                        FullScreenImage.setUpFullImageView(AnnouncementCreationActivity.this, position, true, true,FullScreenImage.getResourceArrayList(mAttachPathList));
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
            View mAdapterView;
            LayoutAttachFileViewBinding viewBinding;

            public ViewHolder(LayoutAttachFileViewBinding viewBinding) {
                super(viewBinding.getRoot());
                this.viewBinding = viewBinding;
                mAdapterView = viewBinding.getRoot();


            }
        }

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
            ActivityCompat.requestPermissions(AnnouncementCreationActivity.this,
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
            ActivityCompat.requestPermissions(AnnouncementCreationActivity.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
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
     * @param mViewFocus
     */
    public void hideSoftKeyboard(View mViewFocus) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewFocus.getWindowToken(), 0);

    }

    /**
     * show error toast
     *
     * @param message
     */
    private void showErrorMessage(String message) {
        DialogUtils.showAlertDialog(AnnouncementCreationActivity.this, message, getString(R.string.alert_title_string), false);
    }
}
