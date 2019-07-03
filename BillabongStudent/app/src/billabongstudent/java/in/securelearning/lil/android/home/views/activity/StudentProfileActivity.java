package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProfileTabViewpagerBinding;
import in.securelearning.lil.android.base.dataobjects.BranchDetail;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.GradeSectionSuper;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.LearningLevel;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.views.fragment.StudentAchievementFragment;
import in.securelearning.lil.android.home.views.fragment.StudentParentFragment;
import in.securelearning.lil.android.home.views.fragment.StudentPersonalFragment;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfile;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class StudentProfileActivity extends AppCompatActivity {

    LayoutProfileTabViewpagerBinding mBinding;
    private static String USER_ID = "userId";
    private ProgressDialog mProgressDialog;
    @Inject
    FlavorHomeModel mFlavorHomeModel;
    @Inject
    RxBus mRxBus;
    private boolean isFromLoggedInUser = false;
    private String mUserObjectId = null;
    boolean canFullView = false;
    private StudentProfile mUserProfile = new StudentProfile();
    private StudentAchievement mStudentAchievement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        //appUserModel = InjectorSyncAdapter.INSTANCE.getComponent().appUserModel();
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_profile_tab_viewpager);
        //loadProfileForLoggedInUser();
        handleIntent();

        //listenRxEvent();
        /*Setting status bar style immersive*/
        mFlavorHomeModel.setImmersiveStatusBar(getWindow());


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("CheckResult")
    private void fetchUserProfileFromServer(String mUserId) {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            mProgressDialog = ProgressDialog.show(StudentProfileActivity.this, "", getString(R.string.message_fetching_user_profile), false);
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

            final Call<StudentProfile> appUserCall = downloadApiInterface.getStudentProfile();

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
        mFlavorHomeModel.fetchStudentAchievements()
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

    private void initializeUIAndListeners(StudentProfile userProfile) {
        mUserProfile = userProfile;
        setUpViewPager();
        setUserThumbnail(userProfile);
        setGradeSection(userProfile.getGrade(), userProfile.getSection());
        setAssociation(userProfile.getAssociation(), userProfile.getBranchDetail());
        setFullUserName(userProfile.getName());

        mBinding.imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionEditProfile();
            }
        });

        mBinding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
            String text = "Grade - " + grade.getName() + " (" + section.getName() + ")";
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
        final String finalImageUrl = imageUrl;
        mBinding.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalCanFullView && !TextUtils.isEmpty(finalImageUrl)) {
                    startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), finalImageUrl, true));
                }
            }
        });
    }

    private void setUpViewPager() {
        final ArrayList<String> tabTitles = getTabTitles();
        mBinding.viewpager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), tabTitles));
        mBinding.tabLayout.setupWithViewPager(mBinding.viewpager);

        for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mBinding.tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(getTabView(i, tabTitles));
        }

        highLightCurrentTab(0, tabTitles); // for initial selected tab view

        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(final int position) {
                highLightCurrentTab(position, tabTitles);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });

    }

    private ArrayList<String> getTabTitles() {
        return new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_student_profile_tab)));
    }

    private void highLightCurrentTab(int position, ArrayList<String> tabTitles) {
        for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mBinding.tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(getTabView(i, getTabTitles()));
        }

        TabLayout.Tab tab = mBinding.tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(getSelectedTabView(position, tabTitles));
    }

    public View getTabView(int position, ArrayList<String> tabTitles) {
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_subject_detail_custom_tab, null);
        view.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.chip_white));
        TextView tabTextView = view.findViewById(R.id.tabTextView);
        tabTextView.setTextColor(ContextCompat.getColor(getBaseContext(), android.R.color.black));
        tabTextView.setText(tabTitles.get(position));
        TextView tabImageViewBadge = view.findViewById(R.id.tabTextView2);
        tabImageViewBadge.setVisibility(View.GONE);
        return view;
    }

    public View getSelectedTabView(int position, ArrayList<String> tabTitles) {
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_subject_detail_custom_tab, null);
        view.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.chip_blue_white_stroke));
        TextView tabTextView = view.findViewById(R.id.tabTextView);
        tabTextView.setText(tabTitles.get(position));
        tabTextView.setTextColor(ContextCompat.getColor(getBaseContext(), android.R.color.white));
        TextView tabImageViewBadge = view.findViewById(R.id.tabTextView2);
        tabImageViewBadge.setVisibility(View.GONE);
        return view;
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
                return StudentPersonalFragment.newInstance(mUserProfile.getClassGroup(), mUserProfile.getEnrollmentNumber(), mUserProfile.getDob(), mUserProfile.getAddress(), mUserProfile.getLocation());
            } else if (mList.get(position).equals(getString(R.string.label_parent))) {
                return StudentParentFragment.newInstance(mUserProfile.getFatherName(), mUserProfile.getFatherMobile(), mUserProfile.getFatherEmail(), mUserProfile.getMotherName(), mUserProfile.getMotherMobile(), mUserProfile.getMotherEmail());
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

    }


}
