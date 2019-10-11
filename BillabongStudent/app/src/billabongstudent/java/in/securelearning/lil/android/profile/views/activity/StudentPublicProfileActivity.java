package in.securelearning.lil.android.profile.views.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutDialogSubjectItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementBadgesItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementEurosItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementTrophiesItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutStudentPublicProfileBinding;
import in.securelearning.lil.android.base.dataobjects.BranchDetail;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.GradeSectionSuper;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.LearningLevel;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.views.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.home.views.adapter.SubjectTopicsRewardAdapter;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.profile.model.ProfileModel;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentProfile;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentSubjectReward;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentTopicReward;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.rest.DownloadApiInterface;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.AppBarStateChangeListener;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class StudentPublicProfileActivity extends AppCompatActivity {

    @Inject
    ProfileModel mProfileModel;

    private static final int EUROS_SPAN_COUNT = 4;
    private static final int TROPHIES_SPAN_COUNT = 4;
    private static final int BADGES_SPAN_COUNT = 4;
    private static final String USER_ID = "userId";

    LayoutStudentPublicProfileBinding mBinding;
    private ProgressDialog mProgressDialog;

    private boolean isFromLoggedInUser = false;
    private String mUserObjectId = null;
    private boolean canFullView = false;
    private StudentProfile mUserProfile = new StudentProfile();
    private StudentAchievement mStudentAchievement;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        //appUserModel = InjectorSyncAdapter.INSTANCE.getComponent().appUserModel();
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_student_public_profile);

        listenRxEvent();
        handleIntent();
        setUpToolbarAndStatusBar(ConstantUtil.BLANK);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public static Intent getStartIntent(String userId, Context context) {
        Intent intent = new Intent(context, StudentPublicProfileActivity.class);
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

                    mBinding.viewDividerTop.setVisibility(View.GONE);


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

                    mBinding.viewDividerTop.setVisibility(View.VISIBLE);

                }
            }
        });


    }

    private void listenRxEvent() {

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

            mProgressDialog = ProgressDialog.show(StudentPublicProfileActivity.this, ConstantUtil.BLANK, getString(R.string.labelPleaseWait), false);
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
                        setAchievements();

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
        setUpToolbarAndStatusBar(userProfile.getName());
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


    private void setAchievements() {
        if (mStudentAchievement != null) {
            setRewards(mStudentAchievement.getRewardsList(), mStudentAchievement.getTotalRewards());
            setTrophies(createTrophiesData(), createTrophiesData().size());
            setBadges(createBadgesData(), createBadgesData().size());
        } else {
            mBinding.layoutContent.setVisibility(View.GONE);
//            mBinding.layoutError.setVisibility(View.VISIBLE);
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
        }
    }

    /*Rewards are now Euros*/
    private void setRewards(final ArrayList<StudentSubjectReward> rewardsList, int totalRewards) {

        mBinding.textViewTotalRewards.setText(String.valueOf(totalRewards));
        mBinding.recyclerViewRewards.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewRewards.setNestedScrollingEnabled(false);
        mBinding.recyclerViewRewards.setAdapter(new RewardsRecyclerViewAdapter(rewardsList));

        mBinding.bottomLineEuros.setVisibility(View.VISIBLE);
        setEurosVisibility(rewardsList);

        mBinding.layoutRewardHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.bottomLineEuros.setVisibility(View.VISIBLE);
                mBinding.bottomLineTrophies.setVisibility(View.GONE);
                mBinding.bottomLineBadges.setVisibility(View.GONE);


                setEurosVisibility(rewardsList);

            }
        });
    }

    /*To set visibility of euros tab for first time and other times*/
    private void setEurosVisibility(ArrayList<StudentSubjectReward> rewardsList) {
        mBinding.recyclerViewTrophies.setVisibility(View.GONE);
        mBinding.recyclerViewBadges.setVisibility(View.GONE);

        if (!rewardsList.isEmpty()) {
            mBinding.recyclerViewRewards.setVisibility(View.VISIBLE);
            mBinding.layoutEurosError.setVisibility(View.GONE);
        } else {
            mBinding.recyclerViewRewards.setVisibility(View.GONE);
            mBinding.layoutEurosError.setVisibility(View.VISIBLE);
        }
    }

    private void setTrophies(final ArrayList<String> trophiesList, int totalTrophies) {

        mBinding.textViewTotalTrophies.setText(String.valueOf(totalTrophies));
        mBinding.recyclerViewTrophies.setLayoutManager(new GridLayoutManager(getBaseContext(), TROPHIES_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerViewTrophies.setNestedScrollingEnabled(false);
        mBinding.recyclerViewTrophies.setAdapter(new TrophiesRecyclerViewAdapter(trophiesList));

        mBinding.layoutTrophiesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.bottomLineEuros.setVisibility(View.GONE);
                mBinding.bottomLineTrophies.setVisibility(View.VISIBLE);
                mBinding.bottomLineBadges.setVisibility(View.GONE);

                mBinding.layoutEurosError.setVisibility(View.GONE);
                mBinding.recyclerViewRewards.setVisibility(View.GONE);
                mBinding.recyclerViewTrophies.setVisibility(View.VISIBLE);
                mBinding.recyclerViewBadges.setVisibility(View.GONE);

            }
        });
    }

    private void setBadges(final ArrayList<String> badgesList, int totalBadges) {

        mBinding.textViewTotalBadges.setText(String.valueOf(totalBadges));
        mBinding.recyclerViewBadges.setLayoutManager(new GridLayoutManager(getBaseContext(), BADGES_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerViewBadges.setNestedScrollingEnabled(false);
        mBinding.recyclerViewBadges.setAdapter(new BadgesRecyclerViewAdapter(badgesList));
        mBinding.layoutBadgesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mBinding.bottomLineEuros.setVisibility(View.GONE);
                mBinding.bottomLineTrophies.setVisibility(View.GONE);
                mBinding.bottomLineBadges.setVisibility(View.VISIBLE);

                mBinding.layoutEurosError.setVisibility(View.GONE);
                mBinding.recyclerViewRewards.setVisibility(View.GONE);
                mBinding.recyclerViewTrophies.setVisibility(View.GONE);
                mBinding.recyclerViewBadges.setVisibility(View.VISIBLE);

            }
        });
    }

    private ArrayList<String> createTrophiesData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Best Artist of the month");
        list.add("Art work of the week");
        list.add("Best Writing");
        list.add("Star of Month");
        return list;
    }

    private ArrayList<String> createBadgesData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Perfect");
        list.add("Excellent Work");
        list.add("Brilliant");
        list.add("Well Done");
        return list;
    }

    private void hideDividerForLastIndex(int size, int position, View viewDivider) {
        if (size == position) {
            viewDivider.setVisibility(View.GONE);
        } else {
            viewDivider.setVisibility(View.VISIBLE);
        }
    }

    private void setSubjectTopicsRewards(String subjectName, String score, String thumbnailUrl, ArrayList<StudentTopicReward> topicList) {
        if (topicList != null && !topicList.isEmpty()) {
            final Dialog dialog = new Dialog(StudentPublicProfileActivity.this);

            /*Dialog box when user click on subject in total rewards/euros */
            LayoutDialogSubjectItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_dialog_subject_item, null, false);
            dialog.setCancelable(true);
            dialog.setContentView(binding.getRoot());
            binding.textViewTopicName.setText(subjectName);
            binding.textViewScore.setText(score);
            if (!TextUtils.isEmpty(thumbnailUrl)) {
                Picasso.with(getBaseContext()).load(thumbnailUrl).placeholder(R.drawable.icon_book).fit().centerCrop().into(binding.imageViewThumbnail);
            } else {
                Picasso.with(getBaseContext()).load(R.drawable.icon_book).placeholder(R.drawable.icon_book).fit().centerCrop().into(binding.imageViewThumbnail);
            }
            binding.recyclerViewTopic.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
            binding.recyclerViewTopic.setNestedScrollingEnabled(false);
            binding.recyclerViewTopic.setAdapter(new SubjectTopicsRewardAdapter(getBaseContext(), topicList));

            DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            Objects.requireNonNull(dialog.getWindow()).setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }
    }

    /*Adapter*/
    private class RewardsRecyclerViewAdapter extends RecyclerView.Adapter<RewardsRecyclerViewAdapter.ViewHolder> {
        private ArrayList<StudentSubjectReward> mList;

        private RewardsRecyclerViewAdapter(ArrayList<StudentSubjectReward> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public RewardsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMyAchievementEurosItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_euros_item, parent, false);
            return new RewardsRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RewardsRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final StudentSubjectReward studentSubjectReward = mList.get(position);
            final String subjectName = studentSubjectReward.getSubjectName();
            final String thumbnailUrl = studentSubjectReward.getThumbnailUrl();

            hideDividerForLastIndex(mList.size() - 1, position, holder.mBinding.viewDivider);
            holder.mBinding.textViewName.setText(studentSubjectReward.getSubjectName());
            final String rewards = String.valueOf(studentSubjectReward.getPointsRewarded());
            holder.mBinding.textViewScore.setText(rewards);
            if (!TextUtils.isEmpty(thumbnailUrl)) {
                Picasso.with(getBaseContext()).load(thumbnailUrl).placeholder(R.drawable.icon_book).fit().centerCrop().into(holder.mBinding.imageViewIcon);
            } else {
                Picasso.with(getBaseContext()).load(R.drawable.icon_book).placeholder(R.drawable.icon_book).fit().centerCrop().into(holder.mBinding.imageViewIcon);
            }

            holder.mBinding.getRoot().
                    setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               ArrayList<StudentTopicReward> topicRewardList = mList.get(position).getTopicRewardList();

                                               setSubjectTopicsRewards(subjectName, rewards, thumbnailUrl, topicRewardList);

                                           }
                                       }
                    );


        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementEurosItemBinding mBinding;

            public ViewHolder(LayoutMyAchievementEurosItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

    private class TrophiesRecyclerViewAdapter extends RecyclerView.Adapter<TrophiesRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String> mList;

        private TrophiesRecyclerViewAdapter(ArrayList<String> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public TrophiesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMyAchievementTrophiesItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_trophies_item, parent, false);
            return new TrophiesRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final TrophiesRecyclerViewAdapter.ViewHolder holder, int position) {
            String trophy = mList.get(position);
            holder.mBinding.textViewName.setText(trophy);
            holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_trophies);
            holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getBaseContext()), R.color.colorPrimary));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementTrophiesItemBinding mBinding;

            ViewHolder(LayoutMyAchievementTrophiesItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

    private class BadgesRecyclerViewAdapter extends RecyclerView.Adapter<BadgesRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String> mList;

        private BadgesRecyclerViewAdapter(ArrayList<String> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public BadgesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMyAchievementBadgesItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_badges_item, parent, false);
            return new BadgesRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final BadgesRecyclerViewAdapter.ViewHolder holder, int position) {
            String badge = mList.get(position);
            holder.mBinding.textViewName.setText(badge);
            holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_badges);
            holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getBaseContext()), R.color.colorPrimary));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementBadgesItemBinding mBinding;

            ViewHolder(LayoutMyAchievementBadgesItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }


}
