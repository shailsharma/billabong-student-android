package in.securelearning.lil.android.home.views.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutFullImageBinding;
import in.securelearning.lil.android.app.databinding.LayoutProfileTeacherClassesItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutUserProfileActivityBinding;
import in.securelearning.lil.android.base.dataobjects.Board;
import in.securelearning.lil.android.base.dataobjects.CuratorMapping;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.GradeSectionSuper;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.LearningLevel;
import in.securelearning.lil.android.base.dataobjects.Role;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.dataobjects.UserQualification;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.CuratorMappingModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.learningnetwork.events.LoadUserEarnBadgeListEvent;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.activity.PostListActivity;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.events.UserProfileChangeEvent;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 2/22/2017.
 */

public class UserProfileActivity extends AppCompatActivity {

    AppUserModel appUserModel;
    private File mImageFile;
    private LayoutUserProfileActivityBinding mBinding;
    private List<String> mEarnBadges = new java.util.ArrayList<>();
    public static String USER_ID = "userId";
    private String userProfilePath = "";
    boolean canFullView = false;
    @Inject
    RxBus mRxBus;
    @Inject
    CuratorMappingModel mCuratorMappingModel;
    Disposable mSubscription;

    @Inject
    BadgesModel mBadgesModel;
    private UserGroupAdapter mUserGroupAdapter;
    private ProgressDialog mProgressDialog;
    private boolean isFromLoggedInUser = false;
    private MenuItem menuItemEdit;
    private String mUserObjectId = null;

    public static Intent getStartIntent(String userId, Context context) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(USER_ID, userId);
        return intent;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mSubscription != null) {
            mSubscription.dispose();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_user_profile_activity);
        InjectorHome.INSTANCE.getComponent().inject(this);
        appUserModel = InjectorSyncAdapter.INSTANCE.getComponent().appUserModel();
        listenRxBusEvent();
        checkOfflineOrOnlineProfile();
        setTitle(getString(R.string.label_profile));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mBinding.collapsingToolbar.setTitleEnabled(false);
        mBinding.collapsingToolbar.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        mBinding.collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        mBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isVisible = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mBinding.toolbar.setTitle(getString(R.string.label_profile));
                    isVisible = true;
                } else if (isVisible) {
                    mBinding.toolbar.setTitle("");
                    isVisible = false;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        menuItemEdit = menu.findItem(R.id.actionEdit);
        if (isFromLoggedInUser) {
            editVisibility(true);
        } else {
            editVisibility(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.actionEdit:
                actionEditProfile();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void editVisibility(boolean b) {
        if (menuItemEdit != null) {
            menuItemEdit.setVisible(b);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // checkOfflineOrOnlineProfile();
    }

    private void actionEditProfile() {
        startActivity(UserProfileEditActivity.getStartIntent(getBaseContext(), mUserObjectId));
    }

    private void checkOfflineOrOnlineProfile() {

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(USER_ID)) {
            mUserObjectId = getIntent().getStringExtra(USER_ID);
            if (mUserObjectId.equals(appUserModel.getObjectId())) {
                Log.e("UPA", "offline");

                loadProfileForLoggedInUser();
            } else {
                Log.e("UPA", "online");

                fetchUserProfileFromServer();
                mBinding.layoutTeacherClasses.setVisibility(View.GONE);
                mBinding.textViewGroupListLabel.setText(getString(R.string.label_common_groups));
            }
        } else {
            finish();
        }
    }

    private void loadProfileForLoggedInUser() {
        isFromLoggedInUser = true;
        UserProfile userProfile = appUserModel.getApplicationUser();
        mUserObjectId = userProfile.getObjectId();
        initializeUIAndListeners(userProfile);
        initializeGroupRecyclerView(userProfile);
        if (isFromLoggedInUser && PermissionPrefsCommon.getClassDetailTeacherViewPermission(this)) {
            initializeTeacherClassRecyclerView();
        } else {
            mBinding.layoutTeacherClasses.setVisibility(View.GONE);
        }
        mBinding.textViewGroupListLabel.setText(getString(R.string.groups));
    }

    private void initializeTeacherClassRecyclerView() {
        java.util.ArrayList<CuratorMapping> list = mCuratorMappingModel.getCompleteList();
        if (list != null && list.size() > 0) {
            mBinding.layoutTeacherClasses.setVisibility(View.VISIBLE);
            mBinding.recyclerViewTeacherClasses.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
            TeacherClassAdapter teacherClassAdapter = new TeacherClassAdapter(list, PrefManager.getSubjectMap(getBaseContext()));
            mBinding.recyclerViewTeacherClasses.setAdapter(teacherClassAdapter);
        } else {
            mBinding.layoutTeacherClasses.setVisibility(View.GONE);
        }

    }

    /**
     * get user profile through server if network is available
     */
    private void fetchUserProfileFromServer() {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            mProgressDialog = ProgressDialog.show(UserProfileActivity.this, "", getString(R.string.message_fetching_user_profile), false);
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

            final Call<UserProfile> appUserCall = downloadApiInterface.getUserProfile(mUserObjectId);

            Observable.create(new ObservableOnSubscribe<UserProfile>() {
                @Override
                public void subscribe(ObservableEmitter<UserProfile> subscriber) {

                    try {
                        Response<UserProfile> response = appUserCall.execute();
                        if (response != null && response.isSuccessful()) {
                            com.couchbase.lite.util.Log.e("UserProfile", "successful");

                            UserProfile userProfile = response.body();
                            subscriber.onNext(userProfile);
                            subscriber.onComplete();
                            mProgressDialog.dismiss();

                        } else if (response.code() == 401 && SyncServiceHelper.refreshToken(getBaseContext())) {

                            Response<UserProfile> response2 = appUserCall.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                com.couchbase.lite.util.Log.e("UserProfile", "successful");

                                UserProfile userProfile = response2.body();
                                subscriber.onNext(userProfile);
                                subscriber.onComplete();
                                mProgressDialog.dismiss();

                            } else if (response.code() == 401) {
                                startActivity(LoginActivity.getUnauthorizedIntent(getBaseContext()));
                            }
                        } else {
                            mProgressDialog.dismiss();
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
                    .subscribe(new Consumer<UserProfile>() {
                        @Override
                        public void accept(UserProfile userProfile) {
                            initializeUIAndListeners(userProfile);
                            initializeGroupRecyclerView(userProfile);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable t) {
                            mProgressDialog.dismiss();
                            finish();
                            Log.e("UserProfile", "err fetching UserProfile" + t.toString());

                            t.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToastAlert(getBaseContext(), getString(R.string.error_fetching_user_profile));
                                }
                            });
                        }
                    });
        }

    }

    private void initializeUIAndListeners(UserProfile userProfile) {

        setBanner();
        setUserThumbnail(userProfile);
        setFullUserName(userProfile.getName());
        setUserRole(userProfile.getRole(), userProfile.getAssociation().getName());
        setUserAboutMe(userProfile.getAboutMe());
        setPersonalInfo(userProfile);
        setAddress(userProfile);
        setInterest(userProfile);


    }

    private void setFullUserName(String name) {
        if (!TextUtils.isEmpty(name)) {
            mBinding.textViewUserName.setText(upperCaseFirstLetter(name));
        }
    }

    private void setBanner() {
        mBinding.imageViewProfileBanner.bringToFront();
        final String splashPath = AppPrefs.getBannerPath(getBaseContext());
        try {
            if (!TextUtils.isEmpty(splashPath)) {
                Picasso.with(getBaseContext()).load(splashPath).into(mBinding.imageViewProfileBanner);
            } else {
                Picasso.with(getBaseContext()).load(R.drawable.image_placeholder).resize(1280, 720).centerCrop().into(mBinding.imageViewProfileBanner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setUserName(String firstName, String lastName) {

        if (!TextUtils.isEmpty(firstName)) {
            firstName = upperCaseFirstLetter(firstName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            lastName = upperCaseFirstLetter(lastName);
        }

        mBinding.textViewUserName.setText(firstName + " " + lastName);
    }

    private void setInterest(UserProfile userProfile) {
        if (isFromLoggedInUser) {
            setInterestLearningLevel(userProfile.getInterest().getLearningLevel());
            setInterestSubject(userProfile.getInterest().getSubject());
            if (mBinding.layoutLearningLevel.getVisibility() == View.GONE &&
                    mBinding.layoutSubject.getVisibility() == View.GONE) {
                mBinding.layoutInterest.setVisibility(View.GONE);
            } else {
                mBinding.layoutInterest.setVisibility(View.VISIBLE);
            }
        } else {
            mBinding.layoutInterest.setVisibility(View.GONE);
        }

    }

    private void setAddress(UserProfile userProfile) {
        setCountry(userProfile.getLocation().getCountry().getName());
        setState(userProfile.getLocation().getState().getName());
        setCity(userProfile.getLocation().getCity());
        setPinCode(userProfile.getLocation().getPin());
        if (isFromLoggedInUser) {
            if (!TextUtils.isEmpty(userProfile.getLocation().getCity()) &&
                    !TextUtils.isEmpty(userProfile.getLocation().getState().getName()) &&
                    !TextUtils.isEmpty(userProfile.getLocation().getCountry().getName())) {
                mBinding.layoutLocation.setVisibility(View.VISIBLE);
                mBinding.textViewAddress.setText(upperCaseFirstLetter(userProfile.getLocation().getCity()) + ", " + upperCaseFirstLetter(userProfile.getLocation().getState().getName()) + ", " + upperCaseFirstLetter(userProfile.getLocation().getCountry().getName()));
            } else {
                mBinding.layoutLocation.setVisibility(View.GONE);
            }
        } else {
            mBinding.layoutLocation.setVisibility(View.GONE);
        }


//        if (mBinding.layoutCountry.getVisibility() == View.GONE &&
//                mBinding.layoutState.getVisibility() == View.GONE &&
//                mBinding.layoutCity.getVisibility() == View.GONE &&
//                mBinding.layoutPinCode.getVisibility() == View.GONE) {
//            mBinding.layoutLocation.setVisibility(View.GONE);
//        } else {
//            mBinding.layoutLocation.setVisibility(View.VISIBLE);
//        }
    }

    private void setPersonalInfo(UserProfile userProfile) {

        setUserEmail(userProfile.getEmail());
        setContactNo(userProfile.getMobile());
        setDateOfBirth(userProfile.getDob());
        setGrade(userProfile.getGrade());
        setSection(userProfile.getSection());
        setBoard(userProfile.getBoard());
        setDesignation(userProfile.getDesignationId(), userProfile.getDepartment());
        setQualification(userProfile.getQualification());


        if (mBinding.layoutEmail.getVisibility() == View.GONE &&
                mBinding.layoutContact.getVisibility() == View.GONE &&
                mBinding.layoutDateOfBirth.getVisibility() == View.GONE &&
                mBinding.layoutGrade.getVisibility() == View.GONE &&
                mBinding.layoutSection.getVisibility() == View.GONE &&
                mBinding.layoutBoard.getVisibility() == View.GONE &&
                mBinding.layoutDesignation.getVisibility() == View.GONE &&
                mBinding.layoutQualifications.getVisibility() == View.GONE) {
            mBinding.layoutPersonalInfo.setVisibility(View.GONE);
        } else {
            mBinding.layoutPersonalInfo.setVisibility(View.VISIBLE);
        }

        int childCount = mBinding.layoutPersonalInfo.getChildCount();
        int count = 0;
        for (int i = 0; i < childCount; i++) {
            if (mBinding.layoutPersonalInfo.getChildAt(i).getVisibility() == View.VISIBLE) {
                count++;
            }
        }

        if (count == 0) {
            mBinding.layoutPersonalInfo.setVisibility(View.GONE);
        }

    }

    private void setInterestSubject(List<Subject> list) {
        try {
            if (list != null) {
                java.util.ArrayList<String> subjectList = new java.util.ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    subjectList.add(list.get(i).getName());
                }
                if (subjectList.size() > 0) {
                    mBinding.layoutSubject.setVisibility(View.VISIBLE);
                    mBinding.textViewLabelUserSubject.setText(getString(R.string.label_subjects));
                    mBinding.textViewUserSubject.setText(TextUtils.join(", ", subjectList));
                } else if (subjectList.size() == 1) {
                    mBinding.layoutSubject.setVisibility(View.VISIBLE);
                    mBinding.textViewLabelUserSubject.setText(getString(R.string.label_subject));
                    mBinding.textViewUserSubject.setText(TextUtils.join(", ", subjectList));
                } else {
                    mBinding.layoutSubject.setVisibility(View.GONE);
                }
            } else {
                mBinding.layoutSubject.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mBinding.layoutSubject.setVisibility(View.GONE);
        }

    }

    private void setInterestLearningLevel(List<LearningLevel> list) {
        try {
            if (list != null) {
                java.util.ArrayList<String> learningLevelList = new java.util.ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    learningLevelList.add(list.get(i).getName());
                }
                if (learningLevelList.size() > 0) {
                    mBinding.layoutLearningLevel.setVisibility(View.VISIBLE);
                    mBinding.textViewLabelLearningLevel.setText(getString(R.string.label_learning_levels));
                    mBinding.textViewUserLearningLevels.setText(TextUtils.join(", ", learningLevelList));
                } else if (learningLevelList.size() == 1) {
                    mBinding.layoutLearningLevel.setVisibility(View.VISIBLE);
                    mBinding.textViewLabelLearningLevel.setText(getString(R.string.label_learning_level));
                    mBinding.textViewUserLearningLevels.setText(TextUtils.join(", ", learningLevelList));
                } else {
                    mBinding.layoutLearningLevel.setVisibility(View.GONE);
                }
            } else {
                mBinding.layoutLearningLevel.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mBinding.layoutLearningLevel.setVisibility(View.GONE);
        }


    }

    private void setPinCode(String pin) {
        if (!TextUtils.isEmpty(pin)) {
            mBinding.layoutPinCode.setVisibility(View.VISIBLE);
            mBinding.textViewUserPinCode.setText(pin);
        } else {
            mBinding.layoutPinCode.setVisibility(View.GONE);
        }
    }

    private void setCity(String city) {
        if (!TextUtils.isEmpty(city)) {
            mBinding.layoutCity.setVisibility(View.VISIBLE);
            mBinding.textViewUserCity.setText(upperCaseFirstLetter(city));
        } else {
            mBinding.layoutCity.setVisibility(View.GONE);
        }
    }

    private void setState(String state) {
        if (!TextUtils.isEmpty(state)) {
            mBinding.layoutState.setVisibility(View.VISIBLE);
            mBinding.textViewUserState.setText(upperCaseFirstLetter(state));
        } else {
            mBinding.layoutState.setVisibility(View.GONE);
        }
    }

    private void setCountry(String country) {
        if (!TextUtils.isEmpty(country)) {
            mBinding.layoutCountry.setVisibility(View.VISIBLE);
            mBinding.textViewUserCountry.setText(upperCaseFirstLetter(country));
        } else {
            mBinding.layoutCountry.setVisibility(View.GONE);
        }
    }

    private void setQualification(UserQualification userQualification) {
        if (isFromLoggedInUser) {
            try {
                if (userQualification != null) {
                    java.util.ArrayList<String> qualifications = new java.util.ArrayList<>();

                    if (userQualification.getGraduateList() != null && !userQualification.getGraduateList().isEmpty()) {
                        qualifications.addAll(userQualification.getGraduateList());
                    }

                    if (userQualification.getPostGraduateList() != null && !userQualification.getPostGraduateList().isEmpty()) {
                        qualifications.addAll(userQualification.getPostGraduateList());
                    }

                    if (userQualification.getProfessionalList() != null && !userQualification.getProfessionalList().isEmpty()) {
                        qualifications.addAll(userQualification.getProfessionalList());
                    }

                    if (userQualification.getOtherQualificationList() != null && !userQualification.getOtherQualificationList().isEmpty()) {
                        qualifications.addAll(userQualification.getOtherQualificationList());
                    }
                    if (qualifications.size() == 1) {
                        mBinding.layoutQualifications.setVisibility(View.VISIBLE);
                        mBinding.textViewLabelQualification.setText(getString(R.string.label_qualification));
                        mBinding.textViewUserQualification.setText(TextUtils.join(", ", qualifications));
                    } else if (qualifications.size() > 1) {
                        mBinding.layoutQualifications.setVisibility(View.VISIBLE);
                        mBinding.textViewLabelQualification.setText(getString(R.string.label_qualifications));
                        mBinding.textViewUserQualification.setText(TextUtils.join(", ", qualifications));
                    } else {
                        mBinding.layoutQualifications.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.layoutQualifications.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mBinding.layoutQualifications.setVisibility(View.GONE);
        }


    }


    private void setDesignation(String designation, String department) {
        if (isFromLoggedInUser) {
            if (!TextUtils.isEmpty(designation)) {
                mBinding.layoutDesignation.setVisibility(View.GONE);
                mBinding.textViewUserDesignation.setText(designation);

            } else {
                mBinding.layoutDesignation.setVisibility(View.GONE);
            }
        } else {
            mBinding.layoutDesignation.setVisibility(View.GONE);
        }

    }

    private void setBoard(Board board) {
        if (board != null) {
            if (!TextUtils.isEmpty(board.getName())) {
                mBinding.layoutBoard.setVisibility(View.VISIBLE);
                mBinding.textViewUserBoard.setText(board.getName());
            } else {
                mBinding.layoutBoard.setVisibility(View.GONE);
            }
        } else {
            mBinding.layoutBoard.setVisibility(View.GONE);
        }

    }

    private void setGrade(Grade grade) {
        if (grade != null) {
            if (!TextUtils.isEmpty(grade.getName())) {
                mBinding.layoutGrade.setVisibility(View.VISIBLE);
                mBinding.textViewUserGrade.setText(grade.getName());
            } else {
                mBinding.layoutGrade.setVisibility(View.GONE);
            }
        } else {
            mBinding.layoutGrade.setVisibility(View.GONE);
        }

    }

    private void setSection(GradeSectionSuper section) {
        if (section != null) {
            if (!TextUtils.isEmpty(section.getName())) {
                mBinding.layoutSection.setVisibility(View.VISIBLE);
                mBinding.textViewUserSection.setText(section.getName());
            } else {
                mBinding.layoutSection.setVisibility(View.GONE);
            }
        } else {
            mBinding.layoutSection.setVisibility(View.GONE);
        }
    }

    private void setDateOfBirth(String dob) {
        if (isFromLoggedInUser) {
            if (!TextUtils.isEmpty(dob)) {
                mBinding.layoutDateOfBirth.setVisibility(View.VISIBLE);
                mBinding.textViewUserDateOfBirth.setText(DateUtils.getMonthDayYearStringFromISODateString(dob));
            } else {
                mBinding.layoutDateOfBirth.setVisibility(View.GONE);
            }
        } else {
            mBinding.layoutDateOfBirth.setVisibility(View.GONE);
        }

    }

    private void setContactNo(String mobile) {
        if (isFromLoggedInUser) {
            if (!TextUtils.isEmpty(mobile)) {
                mBinding.layoutContact.setVisibility(View.VISIBLE);
                mBinding.textViewUserContact.setText(mobile);
            } else {
                mBinding.layoutContact.setVisibility(View.GONE);
            }
        } else {
            mBinding.layoutContact.setVisibility(View.GONE);
        }

    }

    private void setUserRole(Role role, String association) {
        if (role != null && !TextUtils.isEmpty(role.getName())) {
            mBinding.textViewUserRole.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(association)) {
                mBinding.textViewUserRole.setText(upperCaseFirstLetter(role.getName()) + " at " + upperCaseFirstLetter(association));
            } else {
                mBinding.textViewUserRole.setText(role.getName());
            }

        } else {
            mBinding.textViewUserRole.setVisibility(View.GONE);
        }
    }

    private void setUserAboutMe(String aboutMe) {
        if (!TextUtils.isEmpty(aboutMe)) {
            mBinding.layoutAboutMe.setVisibility(View.VISIBLE);
            TextViewMore.viewMore(aboutMe, mBinding.textViewUserAboutMe, mBinding.layoutViewMoreLess.textViewMoreLess);
            mBinding.textViewUserAboutMe.clearComposingText();
        } else {
            mBinding.layoutAboutMe.setVisibility(View.GONE);
        }
    }

    private void setUserEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            mBinding.layoutEmail.setVisibility(View.VISIBLE);
            mBinding.textViewUserEmail.setText(email);
        } else {
            mBinding.layoutEmail.setVisibility(View.GONE);
        }


    }

    private void setUserThumbnail(UserProfile userProfile) {
        String userProfilePath = null;
        boolean canFullView = false;
        if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getLocalUrl())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getLocalUrl()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewUserPic);
            canFullView = true;
            userProfilePath = userProfile.getThumbnail().getLocalUrl();
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getUrl())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getUrl()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewUserPic);
            canFullView = true;
            userProfilePath = userProfile.getThumbnail().getUrl();
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getThumb())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getThumb()).transform(new CircleTransform()).resize(300, 300).centerCrop().into(mBinding.imageViewUserPic);
            canFullView = true;
            userProfilePath = userProfile.getThumbnail().getThumb();
        } else {
            if (!TextUtils.isEmpty(userProfile.getFirstName())) {
                String firstWord = userProfile.getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                mBinding.imageViewUserPic.setImageDrawable(textDrawable);
                canFullView = false;
            }

        }

        final boolean finalCanFullView = canFullView;
        final String finalUserProfilePath = userProfilePath;
        mBinding.imageViewUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalCanFullView && !TextUtils.isEmpty(finalUserProfilePath)) {
                    startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), finalUserProfilePath, true));

                }
            }
        });
    }

    public static void showFullImage(String userProfilePath, Context context) {
        final Dialog dialog = new Dialog(context);
        final LayoutFullImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_full_image, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorDialogBackground)));

        Picasso.with(context).load(userProfilePath).into(binding.imageViewUserImage);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void initializeGroupRecyclerView(UserProfile userProfile) {
        mBinding.recyclerViewGroups.setNestedScrollingEnabled(false);
        fetchUserEarnLilBadgesList(userProfile.getObjectId());

        mBinding.recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (isFromLoggedInUser) {
            List<GroupAbstract> groupAbstractList = PostDataLearningModel.getDistinctGroups(userProfile.getMemberGroups(), userProfile.getModeratedGroups());
            mUserGroupAdapter = new UserGroupAdapter(groupAbstractList);
            mBinding.recyclerViewGroups.setAdapter(mUserGroupAdapter);
        } else {
            java.util.ArrayList<GroupAbstract> commonGroups = getCommonGroups(userProfile.getMemberGroups(), userProfile.getModeratedGroups());
            if (commonGroups.size() > 0) {
                mUserGroupAdapter = new UserGroupAdapter(commonGroups);
                mBinding.recyclerViewGroups.setAdapter(mUserGroupAdapter);
            } else {
                mBinding.layoutGroupList.setVisibility(View.GONE);
            }

        }


    }

    private java.util.ArrayList<GroupAbstract> getCommonGroups(List<GroupAbstract> groupAbstractList1, List<GroupAbstract> groupAbstractList2) {

        HashMap<String, GroupAbstract> commonGroups = new HashMap<>();
        HashMap<String, GroupAbstract> loggedInUserGroups = new HashMap<>();

        UserProfile loggedInUser = appUserModel.getApplicationUser();
        for (GroupAbstract groupAbstract : loggedInUser.getModeratedGroups()) {
            if (groupAbstract != null)
                loggedInUserGroups.put(groupAbstract.getObjectId(), groupAbstract);
        }
        for (GroupAbstract groupAbstract :
                loggedInUser.getMemberGroups()) {
            if (groupAbstract != null)
                loggedInUserGroups.put(groupAbstract.getObjectId(), groupAbstract);
        }


        for (GroupAbstract groupAbstract :
                groupAbstractList1) {
            if (groupAbstract != null && loggedInUserGroups.containsKey(groupAbstract.getObjectId()))
                commonGroups.put(groupAbstract.getObjectId(), groupAbstract);
        }
        for (GroupAbstract groupAbstract :
                groupAbstractList2) {
            if (groupAbstract != null && loggedInUserGroups.containsKey(groupAbstract.getObjectId()))
                commonGroups.put(groupAbstract.getObjectId(), groupAbstract);
        }
        return new java.util.ArrayList<>(commonGroups.values());
    }

    /**
     * @param uid
     */
    public void fetchUserEarnLilBadgesList(String uid) {
        Observable<java.util.ArrayList<LILBadges>> fetchGroupsFromDb = mBadgesModel.fetchUserEarnLilBadgesList(uid);
        fetchGroupsFromDb.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<java.util.ArrayList<LILBadges>>() {
            @Override
            public void accept(java.util.ArrayList<LILBadges> lilBadges) {
                mRxBus.send(new LoadUserEarnBadgeListEvent(lilBadges));
            }
        });
    }

    private void setupBadgesRecyclerView(List lilBadges) {

        if (lilBadges != null && !lilBadges.isEmpty()) {
            mBinding.recyclerViewBadges.setVisibility(View.VISIBLE);
            mBinding.recyclerViewBadges.setLayoutManager(new LinearLayoutManager(UserProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
            UserBadgesAdapter userBadgesAdapter = new UserBadgesAdapter(lilBadges);
            mBinding.recyclerViewBadges.setAdapter(userBadgesAdapter);
        } else {
            mBinding.recyclerViewBadges.setVisibility(View.GONE);
        }
    }

    /**
     * set up Disposable to listen to RxBus
     */
    private void listenRxBusEvent() {

        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) {
                if (event instanceof LoadUserEarnBadgeListEvent) {
                    setupBadgesRecyclerView(((LoadUserEarnBadgeListEvent) event).getLilBadges());
                } else if (event instanceof ObjectDownloadComplete &&
                        ((((ObjectDownloadComplete) event).getObjectClass().equals(UserProfile.class) && ((ObjectDownloadComplete) event).getId().equals(mUserObjectId)) || ((ObjectDownloadComplete) event).getObjectClass().equals(Group.class))) {
                    Log.e("UPA", "ObjectDownloadComplete");
                    checkOfflineOrOnlineProfile();
                } else if (event instanceof UserProfileChangeEvent && ((UserProfileChangeEvent) event).getId().equals(mUserObjectId)) {
                    Log.e("UPA", "UserProfileChangeEvent");
                    checkOfflineOrOnlineProfile();

                }
            }
        });
    }

    private class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.ViewHolder> {

        List<GroupAbstract> mGroupAbstractArrayList = new ArrayList<>();

        public UserGroupAdapter(List<GroupAbstract> groupAbstracts) {
            this.mGroupAbstractArrayList = groupAbstracts;
        }

        @Override
        public UserGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_itemview, parent, false);
            return new UserGroupAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserGroupAdapter.ViewHolder holder, final int position) {
            final GroupAbstract groupAbstract = mGroupAbstractArrayList.get(position);
            holder.mGroupNameTextView.setText(groupAbstract.getName());
            if (groupAbstract.getThumbnail().getUrl() != null && !groupAbstract.getThumbnail().getUrl().isEmpty()) {
                Picasso.with(UserProfileActivity.this).load(groupAbstract.getThumbnail().getUrl()).placeholder(R.drawable.icon_audience_large).resize(300, 300).centerCrop().into(holder.mGroupImageView);
            } else if (groupAbstract.getThumbnail().getThumb() != null && !groupAbstract.getThumbnail().getThumb().isEmpty()) {
                Picasso.with(UserProfileActivity.this).load(groupAbstract.getThumbnail().getThumb()).placeholder(R.drawable.icon_audience_large).resize(300, 300).centerCrop().into(holder.mGroupImageView);
            } else {
                Picasso.with(UserProfileActivity.this).load(R.drawable.icon_audience_large).into(holder.mGroupImageView);
            }

            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(PostListActivity.getIntentForPostList(getBaseContext(), groupAbstract.getObjectId(), false));

                }
            });

        }

        @Override
        public int getItemCount() {
            return mGroupAbstractArrayList.size();
        }

        public void clear() {
            if (mGroupAbstractArrayList != null) {
                mGroupAbstractArrayList.clear();
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View mRootView;
            private ImageView mGroupImageView;
            private TextView mGroupNameTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mGroupImageView = (ImageView) mRootView.findViewById(R.id.imageview_group_icon);
                mGroupNameTextView = (TextView) mRootView.findViewById(R.id.textview_group_name);
            }
        }
    }

    private class UserBadgesAdapter extends RecyclerView.Adapter<UserBadgesAdapter.ViewHolder> {


        List<LILBadges> mEarnBadges = new java.util.ArrayList<>();

        public UserBadgesAdapter(List lilBadges) {
            this.mEarnBadges = lilBadges;
        }

        @Override
        public UserBadgesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_badge_itemview, parent, false);
            return new UserBadgesAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserBadgesAdapter.ViewHolder holder, int position) {
            LILBadges lilBadge = mEarnBadges.get(position);

//            Transformation transformation = new RoundedTransformationBuilder()
//                    .borderColor(Color.TRANSPARENT)
//                    .borderWidthDp(0)
//                    .cornerRadiusDp(70)
//                    .oval(false)
//                    .build();
//            if (strGroupImage.equals("") || strGroupImage.equals(null) || strGroupImage.isEmpty()) {
//                holder.mGroupImageView.setImageResource(R.drawable.audience);
//            } else {
//            File mImageFile = new File(FileUtils.getPathFromFilePath(mGroupAbstractList.get(position).getThumbnail().getThumb()));
//            Picasso.with(UserProfileActivity.this).load(mImageFile).placeholder(R.drawable.audience).fit().transform(transformation).into(holder.mGroupImageView);
////            }
            Picasso.with(UserProfileActivity.this).load(getIResourcesIdentifier(lilBadge.getThumbnail())).into(holder.mBadgeImageView);

        }

        @Override
        public int getItemCount() {
            return mEarnBadges.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View mRootView;
            private ImageView mBadgeImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mBadgeImageView = (ImageView) mRootView.findViewById(R.id.imageview_badge_icon);
            }
        }
    }

    private class TeacherClassAdapter extends RecyclerView.Adapter<TeacherClassAdapter.ViewHolder> {
        private final HashMap<String, PrefManager.SubjectExt> mSubjectMap;
        private java.util.ArrayList<CuratorMapping> mCuratorMappings = new java.util.ArrayList<>();

        public TeacherClassAdapter(java.util.ArrayList<CuratorMapping> completeList, HashMap<String, PrefManager.SubjectExt> subjectMap) {
            this.mSubjectMap = subjectMap;
            this.mCuratorMappings = completeList;
        }

        @Override
        public TeacherClassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutProfileTeacherClassesItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_profile_teacher_classes_item, parent, false);
            return new TeacherClassAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(TeacherClassAdapter.ViewHolder holder, int position) {
            final CuratorMapping curatorMapping = mCuratorMappings.get(position);
            holder.mBinding.textViewGradeSection.setText(curatorMapping.getGrade().getName() + " " + curatorMapping.getSection().getName());
            setTextColor(mSubjectMap, curatorMapping, holder.mBinding.textViewGradeSection);
            holder.mBinding.textViewSubjectName.setText(curatorMapping.getSubject().getName());
            holder.mBinding.cardViewCuratorMapping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = curatorMapping.getGrade().getName() + " " + curatorMapping.getSection().getName() + " " + curatorMapping.getSubject().getName();
                    //startActivity(ClassDetailsActivity.getStartIntent(UserProfileActivity.this, curatorMapping.getSubject().getId(), curatorMapping.getSubject().getSubjectIds(), "", "", curatorMapping.getGrade().getId(), curatorMapping.getSection().getId(), DateUtils.getCurrentISO8601DateString(), false, curatorMapping.getSubject().getName(), title));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCuratorMappings.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutProfileTeacherClassesItemBinding mBinding;

            public ViewHolder(LayoutProfileTeacherClassesItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

    private void setTextColor(HashMap<String, PrefManager.SubjectExt> subjectMap, CuratorMapping curatorMapping, TextViewCustom textView) {
        PrefManager.SubjectExt subjectExt = subjectMap.get(curatorMapping.getSubject().getId());
        if (subjectExt == null) {
            subjectExt = PrefManager.getDefaultSubject();
        }
        int color = subjectExt.getTextColor();
        textView.setTextColor(color);
    }

    private int getIResourcesIdentifier(String name) {
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }

    private String upperCaseFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

}
