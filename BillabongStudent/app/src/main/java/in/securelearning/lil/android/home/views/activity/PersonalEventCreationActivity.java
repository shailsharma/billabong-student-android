package in.securelearning.lil.android.home.views.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialcamera.MaterialCamera;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.net.URLConnection;
import java.text.ParseException;
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
import in.securelearning.lil.android.app.databinding.LayoutCalendarPersonalEventCreationBinding;
import in.securelearning.lil.android.app.databinding.LayoutPickCaptureImageBinding;
import in.securelearning.lil.android.base.constants.EventRepeatFreqType;
import in.securelearning.lil.android.base.constants.EventType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.Location;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.CalEventModel;
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

public class PersonalEventCreationActivity extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    CalendarEventModel calendarEventModel;

    @Inject
    CalEventModel mCalEventModel;

    @Inject
    RxBus mRxBus;
    private static final int CAMERA_REQUEST = 102;
    private static final int IMAGE_REQUEST = 103;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int IMAGE_PICK = 104;
    private final static int IMAGE_CAPTURE = 105;
    public static final String THUMB_FILE_DIRECTORY = File.separator + "tempPersonalEventImages";
    public static final String THUMB_FILE_PATH = File.separator + "tempPersonalEventImages" + File.separator + "tempImage";
    private int mPrimaryColor;
    private String mBaseFolder, mCalendarPersonalEventPath;
    private ArrayList<String> groupIdsArrayList = new ArrayList<>();
    private AlertDialog mGroupSelectDialog;
    private int TIME_DIALOG_ID = 1001;
    private int hour, minute;
    private String strStartHour, strStartMinute, strStartAMPM, strEndHour, strEndMinute, strEndAMPM;
    private String mTitle, mLocation, mStartDate, mEndDate, mStartTime, mEndTime;
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            //view.setHour(hourOfDay);
            //view.setMinute(minutes);
            hour = hourOfDay;
            minute = minutes;
            updateTime(hour, minute);


        }


    };
    private PopupWindow popupMenu;
    private ArrayList<String> mAttachPathList = new ArrayList<>();
    private CalendarEvent mCalendarEvent = new CalendarEvent();
    private int timeClick;
    private int actionBarHeight;
    private LayoutCalendarPersonalEventCreationBinding mBinding;
    private MenuItem mAttachMenuItem;

    @Override
    public void onBackPressed() {
        if (mBinding.edittextEventName.getText().toString().isEmpty() &&
                mBinding.edittextEventDescription.getText().toString().isEmpty() &&
                mBinding.edittextEventLocation.getText().toString().isEmpty() &&
                mBinding.edittextEventStartDate.getText().toString().isEmpty() &&
                mBinding.edittextEventEndDate.getText().toString().isEmpty() &&
                mBinding.edittextEventStartTime.getText().toString().isEmpty() &&
                mBinding.edittextEventEndTime.getText().toString().isEmpty() &&
                mAttachPathList.isEmpty()) {
            closeActivity();

        } else {

            new AlertDialog.Builder(PersonalEventCreationActivity.this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message))
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_calendar_personal_event_creation);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBaseFolder = getFilesDir().getAbsolutePath();
        mCalendarPersonalEventPath = getString(R.string.pathCalendarPersonalEvent);
        mPrimaryColor = ContextCompat.getColor(getBaseContext(), R.color.colorPersonal);
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

    private void menuAttach() {
        if (mAttachPathList.size() == 6) {
            ToastUtils.showToastAlert(getBaseContext(), getString(R.string.message_attach_limit));
        } else {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                showImagePickOptionDialog();
            } else {
                actionGalleryClick();
            }
            // showAttachmentOptionPopup(mBinding.toolbar);
        }
    }

    private void menuCreateEvent() {
        if (!validateTitle()) {
            return;
        }
        if (!validateLocation()) {
            return;
        }
        if (!validateStartDate()) {
            return;
        }
        if (!validateEndDate()) {
            return;
        }
        if (!validateStartTime()) {
            return;
        }
        if (!validateEndTime()) {
            return;
        } else {
            performEventCreation();
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
        setTitle(getString(R.string.new_per_event));
        setDefaultDateAndTime();

        mBinding.recyclerViewResource.setNestedScrollingEnabled(false);


        mBinding.edittextEventRepeatFreq.setText(getRepeatFrequency().get(0));

        mBinding.edittextEventRepeatFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                if (mBinding.checkboxEventRepeat.isChecked()) {
                    popupWindowEditText(getRepeatFrequency(), mBinding.edittextEventRepeatFreq, EventMetaDataAdapter.SPINNER_REPEAT);
                }

            }
        });

        mBinding.edittextEventRepeatInstance.setText(getRepeatInstance().get(0));
        mBinding.edittextEventRepeatInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                if (mBinding.checkboxEventRepeat.isChecked()) {
                    popupWindowEditText(getRepeatInstance(), mBinding.edittextEventRepeatInstance, EventMetaDataAdapter.SPINNER_REPEAT_INSTANCE);
                }
            }
        });

        mBinding.edittextEventReminder.setText(getReminderMinute().get(0));
        mBinding.edittextEventReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                if (mBinding.checkboxEventReminder.isChecked()) {

                    popupWindowEditText(getReminderMinute(), mBinding.edittextEventReminder, EventMetaDataAdapter.SPINNER_REMINDER);
                }

            }
        });

        mBinding.edittextEventStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                showDatePickerDialog(mBinding.edittextEventStartDate);
            }
        });

        mBinding.edittextEventEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                if (!mBinding.edittextEventStartDate.getText().toString().isEmpty()) {

                    showDatePickerDialog(mBinding.edittextEventEndDate);
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.select_start_date));
                }

            }
        });

        mBinding.edittextEventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard(view);
                //showDialog(TIME_DIALOG_ID);
                showNumberPicker(0, getString(R.string.select_start_time));


            }
        });

        mBinding.edittextEventEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBinding.edittextEventStartTime.getText().toString().isEmpty()) {
                    hideSoftKeyboard(view);
                    // showDialog(TIME_DIALOG_ID);
                    showNumberPicker(1, getString(R.string.select_end_time));
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.select_start_time));
                }

            }
        });


    }

    private boolean validateTitle() {
        mTitle = mBinding.edittextEventName.getText().toString().trim();
        if (mTitle.isEmpty()) {
            mBinding.inputLayoutName.setError(getString(R.string.alert_title));
            mBinding.edittextEventName.requestFocus();
            KeyBoardUtil.showSoftKeyboard(mBinding.edittextEventName, getBaseContext());
            return false;
        } else {
            mBinding.edittextEventName.clearFocus();
            mBinding.inputLayoutName.setErrorEnabled(false);
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

    private boolean validateEndDate() {
        mEndDate = mBinding.edittextEventEndDate.getText().toString().trim();
        if (mEndDate.isEmpty()) {
            mBinding.inputLayoutEndDate.setError(getString(R.string.alert_end_date));
            return false;
        } else {
            mBinding.edittextEventEndDate.clearFocus();
            mBinding.inputLayoutEndDate.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateStartTime() {
        mStartTime = mBinding.edittextEventStartTime.getText().toString().trim();
        if (mStartTime.isEmpty()) {
            mBinding.inputLayoutStartTime.setError(getString(R.string.alert_end_date));
            return false;
        } else {
            mBinding.edittextEventStartTime.clearFocus();
            mBinding.inputLayoutStartTime.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEndTime() {
        mEndTime = mBinding.edittextEventEndTime.getText().toString().trim();
        if (mEndTime.isEmpty()) {
            mBinding.inputLayoutEndTime.setError(getString(R.string.alert_end_date));
            return false;
        } else {
            mBinding.edittextEventEndTime.clearFocus();
            mBinding.inputLayoutEndTime.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * show image pick option dialog
     */
    private void showImagePickOptionDialog() {

        final Dialog dialog = new Dialog(PersonalEventCreationActivity.this);
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
                    AndroidPermissions.checkCameraPermission(PersonalEventCreationActivity.this);
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
                    AndroidPermissions.checkStoragePermission(PersonalEventCreationActivity.this);
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

    private ArrayList<String> getReminderMinute() {

        ArrayList<String> mReminderMinute = new ArrayList<String>();
        mReminderMinute.add("15");
        mReminderMinute.add("30");
        mReminderMinute.add("45");
        mReminderMinute.add("60");
        return mReminderMinute;
    }

    private ArrayList<String> getRepeatInstance() {
        ArrayList<String> mRepeatInstance = new ArrayList<String>();
        mRepeatInstance.add("0");
        mRepeatInstance.add("5");
        mRepeatInstance.add("10");
        mRepeatInstance.add("15");
        mRepeatInstance.add("20");
        mRepeatInstance.add("25");
        mRepeatInstance.add("30");

        return mRepeatInstance;
    }

    private ArrayList<String> getRepeatFrequency() {
        ArrayList<String> mRepeatFrequency = new ArrayList<String>();
        mRepeatFrequency.add(EventRepeatFreqType.TYPE_DAILY.getRepeatFreq());
        mRepeatFrequency.add(EventRepeatFreqType.TYPE_WEEKLY.getRepeatFreq());
        mRepeatFrequency.add(EventRepeatFreqType.TYPE_MONTHLY.getRepeatFreq());
        return mRepeatFrequency;
    }

    /**
     * set default date and time to edit texts
     */
    private void setDefaultDateAndTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        SimpleDateFormat sdfHr = new SimpleDateFormat("hh", Locale.ENGLISH);
        SimpleDateFormat sdfMin = new SimpleDateFormat("mm", Locale.ENGLISH);
        SimpleDateFormat sdfAMPM = new SimpleDateFormat("a", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, Integer.valueOf(sdfHr.format(calendar.getTime())));
        calendar.set(Calendar.MINUTE, Integer.valueOf(sdfMin.format(calendar.getTime())));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int intAMPM = 0;
        if (sdfAMPM.format(calendar.getTime()).equalsIgnoreCase("AM")) {
            intAMPM = 0;
        } else {
            intAMPM = 1;
        }
        calendar.set(Calendar.AM_PM, intAMPM);
        mBinding.edittextEventStartTime.setTag(calendar);

        long dateTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        String strCurrentDate = sdfDate.format(dateTime);
        mBinding.edittextEventStartDate.setText(strCurrentDate);
        mBinding.edittextEventEndDate.setText(strCurrentDate);
        String strCurrentTime = sdfTime.format(dateTime);
        String strCurrentHour = strCurrentTime.substring(0, strCurrentTime.length() - 6);
        int intCurrentHour = Integer.parseInt(strCurrentHour);
        String strCurrentMinute = strCurrentTime.substring(3, strCurrentTime.length() - 3);
        String strCurrentAMPM = strCurrentTime.substring(6);
        int intCurrentMinute = Integer.parseInt(strCurrentMinute);
        if (intCurrentMinute > 0 && intCurrentMinute <= 15) {
            if (intCurrentHour == 11 && strCurrentAMPM.equals("AM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "15" + " " + "AM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "15" + " " + "PM");
            } else if (intCurrentHour == 11 && strCurrentAMPM.equals("PM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "15" + " " + "PM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "15" + " " + "AM");
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.DAY_OF_YEAR, 1);
//                Date tomorrow = calendar.getTime();
//                String strTomorrow = sdfDate.format(tomorrow);
//                mBinding.edittextEventEndDate.setText(strTomorrow);
            } else if (intCurrentHour == 12) {
                mBinding.edittextEventStartTime.setText("12" + ":" + "15" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("01" + ":" + "15" + " " + strCurrentAMPM);
            } else if (intCurrentHour > 0 && intCurrentHour <= 8) {
                mBinding.edittextEventStartTime.setText("0" + String.valueOf(intCurrentHour) + ":" + "15" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("0" + String.valueOf(intCurrentHour + 1) + ":" + "15" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 9) {
                mBinding.edittextEventStartTime.setText("09" + ":" + "15" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("10" + ":" + "15" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 10) {
                mBinding.edittextEventStartTime.setText("10" + ":" + "15" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("11" + ":" + "15" + " " + strCurrentAMPM);
            }

        } else if (intCurrentMinute > 15 && intCurrentMinute <= 30) {
            if (intCurrentHour == 11 && strCurrentAMPM.equals("AM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "30" + " " + "AM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "30" + " " + "PM");
            } else if (intCurrentHour == 11 && strCurrentAMPM.equals("PM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "30" + " " + "PM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "30" + " " + "AM");
            } else if (intCurrentHour == 12) {
                mBinding.edittextEventStartTime.setText("12" + ":" + "30" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("01" + ":" + "30" + " " + strCurrentAMPM);
            } else if (intCurrentHour > 0 && intCurrentHour <= 8) {
                mBinding.edittextEventStartTime.setText("0" + String.valueOf(intCurrentHour) + ":" + "30" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("0" + String.valueOf(intCurrentHour + 1) + ":" + "30" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 9) {
                mBinding.edittextEventStartTime.setText("09" + ":" + "30" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("10" + ":" + "30" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 10) {
                mBinding.edittextEventStartTime.setText("10" + ":" + "30" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("11" + ":" + "30" + " " + strCurrentAMPM);
            }

        } else if (intCurrentMinute > 30 && intCurrentMinute <= 45) {
            if (intCurrentHour == 11 && strCurrentAMPM.equals("AM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "45" + " " + "AM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "45" + " " + "PM");
            } else if (intCurrentHour == 11 && strCurrentAMPM.equals("PM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "45" + " " + "PM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "45" + " " + "AM");
            } else if (intCurrentHour == 12) {
                mBinding.edittextEventStartTime.setText("12" + ":" + "45" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("01" + ":" + "45" + " " + strCurrentAMPM);
            } else if (intCurrentHour > 0 && intCurrentHour <= 8) {
                mBinding.edittextEventStartTime.setText("0" + String.valueOf(intCurrentHour) + ":" + "45" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("0" + String.valueOf(intCurrentHour + 1) + ":" + "45" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 9) {
                mBinding.edittextEventStartTime.setText("09" + ":" + "45" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("10" + ":" + "45" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 10) {
                mBinding.edittextEventStartTime.setText("10" + ":" + "45" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("11" + ":" + "45" + " " + strCurrentAMPM);
            }

        } else if (intCurrentMinute > 45 && intCurrentMinute < 60) {
            if (intCurrentHour == 11 && strCurrentAMPM.equals("AM")) {
                mBinding.edittextEventStartTime.setText("12" + ":" + "00" + " " + "PM");
                mBinding.edittextEventEndTime.setText("01" + ":" + "00" + " " + "PM");
            } else if (intCurrentHour == 11 && strCurrentAMPM.equals("PM")) {
                mBinding.edittextEventStartTime.setText("12" + ":" + "00" + " " + "AM");
                mBinding.edittextEventEndTime.setText("01" + ":" + "00" + " " + "AM");
            } else if (intCurrentHour == 12) {
                mBinding.edittextEventStartTime.setText("01" + ":" + "00" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("02" + ":" + "00" + " " + strCurrentAMPM);
            } else if (intCurrentHour > 0 && intCurrentHour <= 7) {
                mBinding.edittextEventStartTime.setText("0" + String.valueOf(intCurrentHour + 1) + ":" + "00" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("0" + String.valueOf(intCurrentHour + 2) + ":" + "00" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 8) {
                mBinding.edittextEventStartTime.setText("09" + ":" + "00" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("10" + ":" + "00" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 9) {
                mBinding.edittextEventStartTime.setText("10" + ":" + "00" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("11" + ":" + "00" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 10 && strCurrentAMPM.equals("AM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "00" + " " + "AM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "00" + " " + "PM");
            } else if (intCurrentHour == 10 && strCurrentAMPM.equals("PM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "00" + " " + "PM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "00" + " " + "PM");
            }
        } else if (intCurrentMinute == 0) {
            if (intCurrentHour == 11 && strCurrentAMPM.equals("AM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "00" + " " + "AM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "00" + " " + "PM");
            } else if (intCurrentHour == 11 && strCurrentAMPM.equals("PM")) {
                mBinding.edittextEventStartTime.setText("11" + ":" + "00" + " " + "PM");
                mBinding.edittextEventEndTime.setText("12" + ":" + "00" + " " + "AM");
            } else if (intCurrentHour == 12) {
                mBinding.edittextEventStartTime.setText("12" + ":" + "00" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("01" + ":" + "00" + " " + strCurrentAMPM);
            } else if (intCurrentHour > 0 && intCurrentHour <= 8) {
                mBinding.edittextEventStartTime.setText("0" + String.valueOf(intCurrentHour) + ":" + "00" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("0" + String.valueOf(intCurrentHour + 1) + ":" + "00" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 9) {
                mBinding.edittextEventStartTime.setText("09" + ":" + "00" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("10" + ":" + "00" + " " + strCurrentAMPM);
            } else if (intCurrentHour == 10) {
                mBinding.edittextEventStartTime.setText("10" + ":" + "00" + " " + strCurrentAMPM);
                mBinding.edittextEventEndTime.setText("11" + ":" + "00" + " " + strCurrentAMPM);
            }
        } else {
            mBinding.edittextEventStartTime.setText("");
            mBinding.edittextEventEndTime.setText("");
        }
    }

    private boolean validateSelectedStartTime() {
        String strStartTime = mBinding.edittextEventStartTime.getText().toString().trim();
        String strStartHour = strStartTime.substring(0, strStartTime.length() - 6);
        String strStartMinute = strStartTime.substring(3, strStartTime.length() - 3);
        String strStartAMPM = strStartTime.substring(6);
        int intStartAMPM = 0;
        if (strStartAMPM.equalsIgnoreCase("AM")) {
            intStartAMPM = 0;
        } else {
            intStartAMPM = 1;
        }
        Calendar calendarSelectedTime = Calendar.getInstance();
        calendarSelectedTime.set(Calendar.HOUR, Integer.parseInt(strStartHour));
        calendarSelectedTime.set(Calendar.MINUTE, Integer.parseInt(strStartMinute));
        calendarSelectedTime.set(Calendar.SECOND, 0);
        calendarSelectedTime.set(Calendar.MILLISECOND, 0);
        calendarSelectedTime.set(Calendar.AM_PM, intStartAMPM);

        SimpleDateFormat df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        String strCurrentTime = df.format(Calendar.getInstance().getTime());
        String strCurrentHour = strCurrentTime.substring(0, strCurrentTime.length() - 6);
        String strCurrentMinute = strCurrentTime.substring(3, strCurrentTime.length() - 3);
        String strCurrentAMPM = strCurrentTime.substring(6);
        int intCurrentAMPM = 0;
        if (strCurrentAMPM.equalsIgnoreCase("AM")) {
            intCurrentAMPM = 0;
        } else {
            intCurrentAMPM = 1;
        }
        Calendar calendarCurrentTime = Calendar.getInstance();
        calendarCurrentTime.set(Calendar.HOUR, Integer.parseInt(strCurrentHour));
        calendarCurrentTime.set(Calendar.MINUTE, Integer.parseInt(strCurrentMinute));
        calendarCurrentTime.set(Calendar.SECOND, 0);
        calendarCurrentTime.set(Calendar.MILLISECOND, 0);
        calendarCurrentTime.set(Calendar.AM_PM, intCurrentAMPM);

        if (calendarSelectedTime.getTimeInMillis() > calendarCurrentTime.getTimeInMillis()) {
            return true;
        } else {
            return false;
        }
    }

    private void showNumberPicker(final int timeClick, String title) {
        final Dialog d = new Dialog(PersonalEventCreationActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.layout_calendar_event_number_picker);
        TextView toolbarTextView = (TextView) d.findViewById(R.id.textViewToolbar);
        toolbarTextView.setText(title);
        ImageButton pickerOkButton = (ImageButton) d.findViewById(R.id.button_ok_picker);
        final NumberPicker numberPickerHour = (NumberPicker) d.findViewById(R.id.numberPicker_hour);
        final String[] arrayHour = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        numberPickerHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(arrayHour.length - 1);
        numberPickerHour.setDisplayedValues(arrayHour);
        numberPickerHour.setWrapSelectorWheel(false);
        final NumberPicker numberPickerMinute = (NumberPicker) d.findViewById(R.id.numberPicker_minute);
        final String[] arrayMinute = {"00", "15", "30", "45"};
        numberPickerMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(arrayMinute.length - 1);
        numberPickerMinute.setDisplayedValues(arrayMinute);
        numberPickerMinute.setWrapSelectorWheel(false);

        final NumberPicker numberPickerAMPM = (NumberPicker) d.findViewById(R.id.numberPicker_AMPM);
        final String[] arrayAMPM = new String[]{"AM", "PM"};
        numberPickerAMPM.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerAMPM.setMinValue(0);
        numberPickerAMPM.setMaxValue(arrayAMPM.length - 1);
        numberPickerAMPM.setDisplayedValues(arrayAMPM);
        numberPickerAMPM.setWrapSelectorWheel(false);

        pickerOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posHour = numberPickerHour.getValue();
                int posMinute = numberPickerMinute.getValue();
                int posAMPM = numberPickerAMPM.getValue();
                updatePickerTime(timeClick, arrayHour[posHour], arrayMinute[posMinute], arrayAMPM[posAMPM]);
                d.dismiss();
            }
        });
        d.show();
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

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);

                    }
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                dismissAttachmentOptionPopup();

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
                dismissAttachmentOptionPopup();
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
                dismissAttachmentOptionPopup();
            }
        });


    }

    private void dismissAttachmentOptionPopup() {
        popupMenu.dismiss();
        mBinding.viewFocus.setVisibility(View.GONE);
    }

    /**
     * Handle result comes from other activity
     *
     * @param requestCode Send code when make request to a Component
     * @param resultCode  Recieve code send by the sender
     * @param data        Data to be requested
     */
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
                    String strPath = copyFiles(picturePathOriginal, mBaseFolder, mCalendarPersonalEventPath, String.valueOf(System.currentTimeMillis()) + ".jpg");
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

    /**
     * Add image to attachment layout
     *
     * @param selectedFilePath
     * @param fileUri
     */
    private void addFileToPreviewLayout(final String selectedFilePath, Uri fileUri) {
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

                groupIdsArrayList.clear();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isChecked()) {
                        groupIdsArrayList.add(groups.get(i).getObjectId());
                    } else {
                        groupIdsArrayList.clear();
                        //mEventInviteesEditText.setText("");
                    }
                }


                //if (groupIdsArrayList.size() > 0)
                // mEventInviteesEditText.setText(groups.get(0).getGroupName());
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
                    if (dateTextView.getId() == R.id.edittext_event_start_date) {
                        dateTextView.setText(DateUtils.getSimpleDateStringFromSeconds(secondsSelected));
                        mBinding.edittextEventEndDate.setText(dateTextView.getText().toString());

                    } else if (dateTextView.getId() == R.id.edittext_event_end_date) {

                        String pattern = "d MMM yyyy";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        try {
                            Date dateStart = simpleDateFormat.parse(mBinding.edittextEventStartDate.getText().toString().trim());
                            Date dateEnd = simpleDateFormat.parse(DateUtils.getSimpleDateStringFromSeconds(secondsSelected));
                            if (dateStart.before(dateEnd)) {
                                dateTextView.setText(DateUtils.getSimpleDateStringFromSeconds(secondsSelected));

                            } else if (dateStart.equals(dateEnd)) {
                                mBinding.edittextEventEndTime.setText("");
                                dateTextView.setText(DateUtils.getSimpleDateStringFromSeconds(secondsSelected));
                            } else {
                                ToastUtils.showToastAlert(getBaseContext(), getString(R.string.error_end_date));
                                mBinding.edittextEventEndTime.setText("");
                                dateTextView.setText(mBinding.edittextEventStartDate.getText().toString().trim());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1001:

                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);

        }
        return null;
    }

    /**
     * update time with selected time by user in time picker and set it to related textview
     *
     * @param hours
     * @param minute
     */
    private void updateTime(int hours, int minute) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String zeroToHours = "";
        if (hours < 10) {
            zeroToHours = "0" + hours;
        } else {
            zeroToHours = String.valueOf(hours);
        }

        String zeroToMinutes = "";
        if (minute < 10) {
            zeroToMinutes = "0" + minute;
        } else {
            zeroToMinutes = String.valueOf(minute);
        }

        String strTime = new StringBuilder().append(zeroToHours).append(':')
                .append(zeroToMinutes).append(" ").append(timeSet).toString();

        if (timeClick == 0) {
            mBinding.edittextEventStartTime.setText(strTime);
            if (hours < 10) {
                mBinding.edittextEventEndTime.setText("0" + String.valueOf(hours + 1) + ":" + zeroToMinutes + " " + timeSet);
            } else if (hours == 12) {
                mBinding.edittextEventEndTime.setText("01:" + zeroToMinutes + " " + timeSet);
            } else if (hours == 11 && timeSet.equals("AM")) {
                mBinding.edittextEventEndTime.setText("12:" + zeroToMinutes + " PM");
            } else if (hours == 11 && timeSet.equals("PM")) {
                mBinding.edittextEventEndTime.setText("12:" + zeroToMinutes + " AM");
                try {
                    long dateTime = System.currentTimeMillis();
                    SimpleDateFormat sdfDate = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                    String strCurrentDate = sdfDate.format(dateTime);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sdfDate.parse(strCurrentDate));
                    cal.add(Calendar.DATE, 1);
                    mBinding.edittextEventEndDate.setText(sdfDate.format(cal.getTime()));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                mBinding.edittextEventEndTime.setText(String.valueOf(hours + 1) + ":" + String.valueOf(minute) + " " + timeSet);
            }

        } else {
            if (mBinding.edittextEventStartDate.getText().toString().equals(mBinding.edittextEventEndDate.getText().toString())) {
                String pattern = "hh:mm a";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                try {
                    Date timeStart = simpleDateFormat.parse(mBinding.edittextEventStartTime.getText().toString().trim());
                    Date timeEnd = simpleDateFormat.parse(strTime);
                    if (timeStart.before(timeEnd)) {
                        mBinding.edittextEventEndTime.setText(strTime);

                    } else if (timeStart.equals(timeEnd)) {
                        mBinding.edittextEventEndTime.setText("");
                        ToastUtils.showToastAlert(getBaseContext(), getString(R.string.error_event_same_time));
                    } else {
                        mBinding.edittextEventEndTime.setText("");
                        ToastUtils.showToastAlert(getBaseContext(), getString(R.string.error_event_time));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                mBinding.edittextEventEndTime.setText(strTime);
            }

        }


    }

    /**
     * update time with selected time by user in time picker and set it to related textview
     *
     * @param timeClick
     * @param hour
     * @param minute
     * @param ampm
     */
    private void updatePickerTime(int timeClick, String hour, String minute, String ampm) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int intAMPM = 0;
        if (ampm.equalsIgnoreCase("AM"))
            intAMPM = 0;
        else
            intAMPM = 1;
        calendar.set(Calendar.AM_PM, intAMPM);

        if (timeClick == 0) {
            strStartHour = hour;
            strStartMinute = minute;
            strStartAMPM = ampm;
            mBinding.edittextEventStartTime.setTag(calendar);
            mBinding.edittextEventStartTime.setText(strStartHour + ":" + strStartMinute + " " + strStartAMPM);

            if (strStartHour.equals("11") && strStartAMPM.equals("AM")) {
                mBinding.edittextEventEndTime.setText("12" + ":" + strStartMinute + " " + "PM");
            } else if (strStartHour.equals("11") && strStartAMPM.equals("PM")) {
                mBinding.edittextEventEndTime.setText("12" + ":" + strStartMinute + " " + "AM");
                try {
                    long dateTime = System.currentTimeMillis();
                    SimpleDateFormat sdfDate = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                    String strCurrentDate = sdfDate.format(dateTime);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sdfDate.parse(strCurrentDate));
                    cal.add(Calendar.DATE, 2);
                    mBinding.edittextEventEndDate.setText(sdfDate.format(cal.getTime()));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (strStartHour.equals("12")) {
                mBinding.edittextEventEndTime.setText("01" + ":" + strStartMinute + " " + strStartAMPM);
            } else {
                if (Integer.parseInt(strStartHour) > 0 && Integer.parseInt(strStartHour) <= 9) {
                    mBinding.edittextEventEndTime.setText("0" + String.valueOf((Integer.parseInt(strStartHour) + 1)) + ":" + strStartMinute + " " + strStartAMPM);
                } else {
                    mBinding.edittextEventEndTime.setText(String.valueOf((Integer.parseInt(strStartHour) + 1)) + ":" + strStartMinute + " " + strStartAMPM);
                }

            }

        } else {
            strEndHour = hour;
            strEndMinute = minute;
            strEndAMPM = ampm;

            Calendar startCal = (Calendar) mBinding.edittextEventStartTime.getTag();


            if (mBinding.edittextEventStartDate.getText().toString().equals(mBinding.edittextEventEndDate.getText().toString())) {
                if (startCal.getTimeInMillis() < calendar.getTimeInMillis()) {
                    mBinding.edittextEventEndTime.setText(strEndHour + ":" + strEndMinute + " " + strEndAMPM);
                } else if (startCal.getTimeInMillis() == calendar.getTimeInMillis()) {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.error_event_same_time));
                } else {
                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.error_event_time));
                }

            } else {
                mBinding.edittextEventEndTime.setText(strEndHour + ":" + strEndMinute + " " + strEndAMPM);
            }
        }
    }

    /**
     * Pop up window for fields like event type, group.
     *
     * @param adapterData
     * @param viewBox
     * @param spinnerType
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void popupWindowEditText(final ArrayList adapterData, final EditText viewBox,
                                     final int spinnerType) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_calendar_simple_recycler_view, null);
        RecyclerView mEditTextDataListView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        mEditTextDataListView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        EventMetaDataAdapter arrayAdapter = new EventMetaDataAdapter(getBaseContext(), adapterData, spinnerType);
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
                viewBox.setText(((TextView) view).getText().toString());
                viewBox.setTag(view.getTag());
                popupEditBox.dismiss();


            }
        });

    }

    /**
     * after validating all fields- insert all values to CalendarEvent dataobject
     * save dataobject to database
     */
    private void performEventCreation() {
        String currentTime = DateUtils.getCurrentDateTime();
        Date startDate = DateUtils.getFormattedDateFromString(mBinding.edittextEventStartDate.getText().toString().trim());
        Date endDate = DateUtils.getFormattedDateFromString(mBinding.edittextEventEndDate.getText().toString().trim());
        Location location = new Location();
        location.setCity(mBinding.edittextEventLocation.getText().toString().trim());

        mCalendarEvent.setAlias(GeneralUtils.generateAlias("CalendarPersonalEvent", mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));
        mCalendarEvent.setObjectId(null);
        mCalendarEvent.setEventType(EventType.TYPE_PERSONAL.getEventType());
        mCalendarEvent.setEventTitle(mBinding.edittextEventName.getText().toString().trim());
        mCalendarEvent.setLocation(location);
        mCalendarEvent.setStartDate(DateUtils.getISO8601DateStringFromDate(startDate));
        mCalendarEvent.setEndDate(DateUtils.getISO8601DateStringFromDate(endDate));
        mCalendarEvent.setStartTime(mBinding.edittextEventStartTime.getText().toString().trim());
        mCalendarEvent.setEndTime(mBinding.edittextEventEndTime.getText().toString().trim());
        mCalendarEvent.setGroupAbstract(null);
        mCalendarEvent.setDescription(Html.toHtml(mBinding.edittextEventDescription.getEditableText()).toString());
        mCalendarEvent.setURL(null);
        final UserProfile userProfile = mAppUserModel.getApplicationUser();
        String userId = userProfile.getObjectId();
        Institution institution = userProfile.getAssociation();
        Institution institution1 = new Institution();
        institution1.setSplashThumbnail(null);
        institution1.setThumbnail(null);
        institution1.setId(institution.getId());
        institution1.setName(institution.getName());
        mCalendarEvent.setCreatedBy(userId);
        mCalendarEvent.setInstitution(institution1);
        mCalendarEvent.setCreationTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        mCalendarEvent.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        mCalendarEvent.setAllDay(false);
        mCalendarEvent.setAttachments(FullScreenImage.getResourceArrayList(mAttachPathList));

        if (mBinding.checkboxEventRepeat.isChecked()) {
            mCalendarEvent.setRepeat(mBinding.checkboxEventRepeat.isChecked());
            mCalendarEvent.setRepeatFreq(mBinding.edittextEventRepeatFreq.getText().toString().trim());
        }

        mCalendarEvent.setReminder(mBinding.checkboxEventReminder.isChecked());
        calendarEventModel.saveEvent(mCalendarEvent);
        ToastUtils.showToastSuccess(getBaseContext(), getString(R.string.event_created));
        mRxBus.send(new LoadNewEventCreated(startDate));
        // MessageService.startActionDownloadCalendarEvent(getBaseContext(), mCalendarEvent.getAlias());
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
            ActivityCompat.requestPermissions(PersonalEventCreationActivity.this,
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
            ActivityCompat.requestPermissions(PersonalEventCreationActivity.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    /**
     * adapter for event type and group items
     */
    public class EventMetaDataAdapter extends RecyclerView.Adapter<EventMetaDataAdapter.ViewHolder> {
        public static final int SPINNER_EVENT_TYPE = 101;
        public static final int SPINNER_GROUP = 102;
        public static final int SPINNER_REPEAT = 103;
        public static final int SPINNER_REPEAT_INSTANCE = 104;
        public static final int SPINNER_REMINDER = 105;
        ArrayList<String> eventDataList = new ArrayList<String>();
        Context mContext;
        View.OnClickListener mItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        private int viewType;


        public EventMetaDataAdapter(Context context, ArrayList<String> dataList, int viewType) {
            mContext = context;
            eventDataList = dataList;
            this.viewType = viewType;
        }

        public void setItemClickAction(View.OnClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

        @Override
        public EventMetaDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_calendar_event_simple_text, parent, false);
            EventMetaDataAdapter.ViewHolder vh = new EventMetaDataAdapter.ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final EventMetaDataAdapter.ViewHolder holder, final int position) {
            holder.mItemValueTxt.setOnClickListener(mItemClickListener);

            switch (viewType) {

                case SPINNER_EVENT_TYPE:

                    holder.mItemValueTxt.setText(eventDataList.get(position));
                    //holder.mItemValueTxt.setTag(board);
                    break;
                case SPINNER_REPEAT:
                    holder.mItemValueTxt.setText(eventDataList.get(position));
                    //holder.mItemValueTxt.setTag(board);
                    break;
                case SPINNER_REPEAT_INSTANCE:
                    holder.mItemValueTxt.setText(eventDataList.get(position));
                    //holder.mItemValueTxt.setTag(board);
                    break;
                case SPINNER_REMINDER:
                    holder.mItemValueTxt.setText(eventDataList.get(position) + " min");
                    //holder.mItemValueTxt.setTag(board);
                    break;
                default:
                    holder.mItemValueTxt.setText("");
                    break;
            }


        }

        @Override
        public int getItemCount() {
            return eventDataList.size();
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

//                Bitmap mBitmap = getScaledBitmapFromPath(mContext.getResources(), filePath);
//                mBitmap = Bitmap.createScaledBitmap(mBitmap, 300, 300, false);
//                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBitmap, "any_Title", null);
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
                        FullScreenImage.setUpFullImageView(PersonalEventCreationActivity.this, position, true, true,FullScreenImage.getResourceArrayList(mAttachPathList));
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
        DialogUtils.showAlertDialog(PersonalEventCreationActivity.this, message, getString(R.string.alert_title_string), false);
    }

}
