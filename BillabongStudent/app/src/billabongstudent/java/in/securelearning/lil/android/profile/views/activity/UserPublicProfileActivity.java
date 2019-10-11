package in.securelearning.lil.android.profile.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProfileShieldListItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutUserPublicProfileBinding;
import in.securelearning.lil.android.base.dataobjects.BranchDetail;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.views.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.profile.dataobject.TeacherAchievementRewards;
import in.securelearning.lil.android.profile.dataobject.TeacherProfile;
import in.securelearning.lil.android.profile.model.ProfileModel;
import in.securelearning.lil.android.syncadapter.dataobject.IdNameObject;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;

/*Activity for non-student users' public profile*/
public class UserPublicProfileActivity extends AppCompatActivity {


    @Inject
    ProfileModel mProfileModel;

    public static final String USER_ID = "userId";
    private static final int SHIELDS_SPAN_COUNT = 3;

    private LayoutUserPublicProfileBinding mBinding;

    private String mUserObjectId;
    private Snackbar mInternetSnackBar;
    private TeacherProfile mTeacherProfile;
    private TeacherAchievementRewards mAchievementRewards;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_user_public_profile);
        InjectorHome.INSTANCE.getComponent().inject(this);

        setUpStatusBarAndToolbar();
        handleIntent();
        initializeUiAndListeners();
        getUserProfile();
//        setEuros();
        setShields(createTrophiesData(), 5);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpStatusBarAndToolbar() {
        CommonUtils.getInstance().setImmersiveUiWithoutFitSystemWindow(getWindow(), ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle("");
    }

    public static Intent getStartIntent(Context context, String userObjectId) {
        Intent intent = new Intent(context, UserPublicProfileActivity.class);
        intent.putExtra(USER_ID, userObjectId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /*Handle intent*/
    private void handleIntent() {
        if (getIntent() != null) {
            mUserObjectId = getIntent().getStringExtra(USER_ID);
        }
    }

    private void initializeUiAndListeners() {

        /*By default*/
        mBinding.bottomLineEuros.setVisibility(View.VISIBLE);
        mAchievementRewards = new TeacherAchievementRewards();

        mBinding.imageViewPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                    mProfileModel.playVideo(mTeacherProfile.getProfileVideo());
                } else {
                    SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
                }

            }
        });


    }

    /*Get non-student users' profile*/
    @SuppressLint("CheckResult")
    private void getUserProfile() {
        dismissInternetSnackBar();
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            showBottomProgress();
            mBinding.layoutContainer.setVisibility(View.GONE);

            mProfileModel.fetchNonStudentUserProfile(mUserObjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<TeacherProfile>() {
                        @Override
                        public void accept(TeacherProfile teacherProfile) throws Exception {

                            hideBottomProgress();
                            mBinding.layoutContainer.setVisibility(View.VISIBLE);

                            if (teacherProfile != null) {
                                mTeacherProfile = teacherProfile;
                                setUserProfileDetails();
                            } else {
                                GeneralUtils.showToastShort(getBaseContext(), getString(R.string.messageUnableToGetData));
                                finish();
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            hideBottomProgress();

                            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.messageUnableToGetData));
                            throwable.printStackTrace();
                            finish();

                        }
                    });

        } else {
            showInternetSnackBar();
        }

    }

    /*Set data of profile*/
    private void setUserProfileDetails() {

        setUserThumbnail(mTeacherProfile);
        setUserVideoThumbnail(mTeacherProfile);
        setFullUserName(mTeacherProfile.getName());
        setEmail(mTeacherProfile.getEmail());
        setUserRoleSubject(mTeacherProfile.getSubjects());
        setUserLearningLevel(mTeacherProfile.getLearningLevels());
        setBranchName(mTeacherProfile.getBranchDetail());

        if (mTeacherProfile.getTeacherAchievementRewards() != null) {
            mAchievementRewards = mTeacherProfile.getTeacherAchievementRewards();
        }

        setEuros();

    }

    private void setUserThumbnail(UserProfile userProfile) {

        boolean canFullView = false;
        String userProfilePath = null;
        if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getLocalUrl())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getLocalUrl()).transform(new CircleTransform()).fit().centerCrop().into(mBinding.imageViewProfilePicture);
            canFullView = true;
            userProfilePath = userProfile.getThumbnail().getLocalUrl();
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getUrl())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getUrl()).transform(new CircleTransform()).fit().centerCrop().into(mBinding.imageViewProfilePicture);
            canFullView = true;
            userProfilePath = userProfile.getThumbnail().getUrl();
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getThumb())) {
            Picasso.with(getBaseContext()).load(userProfile.getThumbnail().getThumb()).transform(new CircleTransform()).fit().centerCrop().into(mBinding.imageViewProfilePicture);
            canFullView = true;
            userProfilePath = userProfile.getThumbnail().getThumb();
        } else {
            if (!TextUtils.isEmpty(userProfile.getFirstName())) {
                String firstWord = userProfile.getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                mBinding.imageViewProfilePicture.setImageDrawable(textDrawable);
                canFullView = false;
            }
        }

        final boolean finalCanFullView = canFullView;
        final String finalUserProfilePath = userProfilePath;
        mBinding.imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalCanFullView && !TextUtils.isEmpty(finalUserProfilePath)) {
                    startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), finalUserProfilePath, true));
                }
            }
        });
    }

    private void setUserVideoThumbnail(TeacherProfile teacherProfile) {

        if (teacherProfile.getProfileVideo() != null && !TextUtils.isEmpty(teacherProfile.getProfileVideo().getUrlThumbnail())) {
            mBinding.imageViewPlayVideo.setVisibility(View.VISIBLE);
            Picasso.with(getBaseContext()).load(teacherProfile.getProfileVideo().getUrlThumbnail())
                    .placeholder(R.drawable.app_banner).fit().centerCrop().into(mBinding.imageViewProfileVideo);
        } else {
            Picasso.with(getBaseContext()).load(R.drawable.app_banner).fit().centerCrop().into(mBinding.imageViewProfileVideo);
        }

    }

    private void setFullUserName(String name) {
        if (!TextUtils.isEmpty(name)) {
            mBinding.textViewUserName.setText(name);
        }
    }

    private void setEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            String text = makeTextBold("Email - ") + email;
            mBinding.textViewUserEmail.setText(Html.fromHtml(text));
        }
    }

    private String makeTextBold(String string) {
        return "<b>" + string + "</b>";
    }

    private void setBranchName(BranchDetail branchDetail) {

        if (branchDetail != null && !TextUtils.isEmpty(branchDetail.getName())) {
            mBinding.textViewUserBranch.setText(branchDetail.getName());
            mBinding.textViewUserBranch.setVisibility(View.VISIBLE);
        } else {
            mBinding.textViewUserBranch.setVisibility(View.GONE);
        }

    }

    private void setUserRoleSubject(ArrayList<IdNameObject> subjects) {
        String subjectNameList = "";
//        if (!learningLevels.isEmpty()) {
//            for (int i = 0; i < learningLevels.size(); i++) {
//                learningLevelList = learningLevelList + learningLevels.get(i).getName() + ConstantUtil.BLANK_SPACE + getString(R.string.teacher);
//                if (i < (learningLevels.size()) - 1) {
//                    learningLevelList = learningLevelList + ", ";
//                }
//            }
//        }

        if (!subjects.isEmpty()) {
            for (int i = 0; i < subjects.size(); i++) {
                subjectNameList = subjectNameList + subjects.get(i).getName();
                if (i < (subjects.size()) - 1) {
                    subjectNameList = subjectNameList + ", ";
                }
            }
        }

//        if (!TextUtils.isEmpty(learningLevelList) && !TextUtils.isEmpty(subjectNameList)) {
//            finalString = learningLevelList + " - " + subjectNameList;
//        } else if (!TextUtils.isEmpty(learningLevelList)) {
//            finalString = learningLevelList;
//        } else if (!TextUtils.isEmpty(subjectNameList)) {
//            finalString = subjectNameList;
//        }


        //  if (!TextUtils.isEmpty(finalString)) {
        //  Spannable wordToColorSpan = new SpannableString(finalString);

        if (!TextUtils.isEmpty(subjectNameList)) {

//                wordToColorSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(), R.color.colorEuroGrey)),
//                        (finalString.length() - subjectNameList.length()),
//                        finalString.length(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            String text = makeTextBold("Subjects - ") + subjectNameList;
            mBinding.textViewUserRoleSubject.setText(Html.fromHtml(text));
            mBinding.textViewUserRoleSubject.setVisibility(View.VISIBLE);
        } else {
            mBinding.textViewUserRoleSubject.setVisibility(View.GONE);

        }

//        } else {
//            mBinding.textViewUserRoleSubject.setVisibility(View.GONE);
//        }

    }

    private void setUserLearningLevel(ArrayList<IdNameObject> learningLevel) {
        String learningLevelList = "";
//        if (!learningLevels.isEmpty()) {
//            for (int i = 0; i < learningLevels.size(); i++) {
//                learningLevelList = learningLevelList + learningLevels.get(i).getName() + ConstantUtil.BLANK_SPACE + getString(R.string.teacher);
//                if (i < (learningLevels.size()) - 1) {
//                    learningLevelList = learningLevelList + ", ";
//                }
//            }
//        }

        if (!learningLevel.isEmpty()) {
            for (int i = 0; i < learningLevel.size(); i++) {
                learningLevelList = learningLevelList + learningLevel.get(i).getName();
                if (i < (learningLevel.size()) - 1) {
                    learningLevelList = learningLevelList + ", ";
                }
            }
        }

//        if (!TextUtils.isEmpty(learningLevelList) && !TextUtils.isEmpty(subjectNameList)) {
//            finalString = learningLevelList + " - " + subjectNameList;
//        } else if (!TextUtils.isEmpty(learningLevelList)) {
//            finalString = learningLevelList;
//        } else if (!TextUtils.isEmpty(subjectNameList)) {
//            finalString = subjectNameList;
//        }


        //  if (!TextUtils.isEmpty(finalString)) {
        //  Spannable wordToColorSpan = new SpannableString(finalString);

        if (!TextUtils.isEmpty(learningLevelList)) {

//                wordToColorSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(), R.color.colorEuroGrey)),
//                        (finalString.length() - subjectNameList.length()),
//                        finalString.length(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            String text = makeTextBold("Teaches - ") + learningLevelList;
            mBinding.textViewUserLearningLevel.setText(Html.fromHtml(text));
            mBinding.textViewUserLearningLevel.setVisibility(View.VISIBLE);
        } else {
            mBinding.textViewUserLearningLevel.setVisibility(View.GONE);

        }

//        } else {
//            mBinding.textViewUserRoleSubject.setVisibility(View.GONE);
//        }

    }

    /*Common method to show any error or prompt as snackBar*/
    private void showAlertMessage(String message) {
        SnackBarUtils.showSnackBar(getBaseContext(), mBinding.getRoot(), message, SnackBarUtils.UNSUCCESSFUL);
    }

    private void showInternetSnackBar() {

        hideBottomProgress();

        mInternetSnackBar = Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), LENGTH_INDEFINITE);

        mInternetSnackBar.setAction((R.string.labelRetry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getUserProfile();

            }
        }).show();

    }

    private void dismissInternetSnackBar() {
        if (mInternetSnackBar != null && mInternetSnackBar.isShown()) {
            mInternetSnackBar.dismiss();
        }
    }

    private void hideBottomProgress() {
        AnimationUtils.pushDownExit(getBaseContext(), mBinding.progressContent);
        mBinding.layoutProgressBottom.setVisibility(View.GONE);
    }

    private void showBottomProgress() {
//        mBinding.textViewBottomProgressMessage.setText(message);
        mBinding.layoutProgressBottom.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.progressContent);
    }

    /*Set tabs data*/
    private void setEuros() {

        mBinding.textViewTotalEuros.setText(new DecimalFormat("##.##").format(mAchievementRewards.getTotalReward()));
        setEurosVisibility();

        mBinding.layoutEuroHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.bottomLineEuros.setVisibility(View.VISIBLE);
                mBinding.bottomLineShields.setVisibility(View.GONE);
                mBinding.recyclerViewShields.setVisibility(View.GONE);

                setEurosVisibility();

            }
        });
    }

    /*To set visibility of euros tab for first time and other times*/
    private void setEurosVisibility() {
        mBinding.layoutEuros.setVisibility(View.VISIBLE);

        /*Training*/
        mBinding.layoutEurosTraining.imageViewIcon.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.icon_training_blue_48_vector));
        mBinding.layoutEurosTraining.textViewName.setText(getString(R.string.labelTraining));
        mBinding.layoutEurosTraining.textViewScore.setText(new DecimalFormat("##.##").format(mAchievementRewards.getTraining()));

        /*ETAT*/
        mBinding.layoutEurosEtat.imageViewIcon.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.icon_etat_blue_48_vector));
        mBinding.layoutEurosEtat.textViewName.setText(getString(R.string.labelEtat));
        mBinding.layoutEurosEtat.textViewScore.setText(new DecimalFormat("##.##").format(mAchievementRewards.getEtat()));

        /*Lesson Plan Reflections*/
        mBinding.layoutEurosReflection.imageViewIcon.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.icon_reflection_blue_48_vector));
        mBinding.layoutEurosReflection.textViewName.setText(getString(R.string.labelLessonPlanReflections));
        mBinding.layoutEurosReflection.textViewScore.setText(new DecimalFormat("##.##").format(mAchievementRewards.getLessonPlanReflection()));

        /*Contribution*/
        mBinding.layoutEurosContribution.imageViewIcon.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.icon_contribution_blue_48_vector));
        mBinding.layoutEurosContribution.textViewName.setText(getString(R.string.labelContribution));
        mBinding.layoutEurosContribution.textViewScore.setText(new DecimalFormat("##.##").format(mAchievementRewards.getContribution()));

        /*Classroom Observations*/
        mBinding.layoutEurosObservation.imageViewIcon.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.icon_observation_blue_48_vector));
        mBinding.layoutEurosObservation.textViewName.setText(getString(R.string.labelClassroomObservations));
        mBinding.layoutEurosObservation.textViewScore.setText(new DecimalFormat("##.##").format(mAchievementRewards.getClassRoomObservation()));

    }

    private void setShields(final ArrayList<String> trophiesList, int totalShields) {

        mBinding.textViewTotalShields.setText(String.valueOf(totalShields));
        mBinding.recyclerViewShields.setLayoutManager(new GridLayoutManager(getBaseContext(), SHIELDS_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerViewShields.setNestedScrollingEnabled(false);
        mBinding.recyclerViewShields.setAdapter(new ShieldsRecyclerViewAdapter(trophiesList));

        mBinding.layoutShieldsHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.bottomLineEuros.setVisibility(View.GONE);
                mBinding.layoutEuros.setVisibility(View.GONE);

                mBinding.bottomLineShields.setVisibility(View.VISIBLE);
                mBinding.recyclerViewShields.setVisibility(View.VISIBLE);

            }
        });
    }

    private ArrayList<String> createTrophiesData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("July 19");
        list.add("2018");
        list.add("November, 18");
        list.add("September, 18");
        list.add("June, 18");
        return list;
    }

    /*Adapter*/
    private class ShieldsRecyclerViewAdapter extends RecyclerView.Adapter<ShieldsRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String> mList;

        private ShieldsRecyclerViewAdapter(ArrayList<String> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public ShieldsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutProfileShieldListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_profile_shield_list_item, parent, false);
            return new ShieldsRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ShieldsRecyclerViewAdapter.ViewHolder holder, int position) {
            String trophy = mList.get(position);
            holder.mBinding.textViewName.setText(trophy);
            holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_shield_blue_48_vector);
//            holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getBaseContext()), R.color.colorPrimary));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutProfileShieldListItemBinding mBinding;

            ViewHolder(LayoutProfileShieldListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

}