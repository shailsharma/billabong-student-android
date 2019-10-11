package in.securelearning.lil.android.home.views.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutItemGroupMembersBinding;
import in.securelearning.lil.android.app.databinding.LayoutSkillItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutTrainingStaticDetailBinding;
import in.securelearning.lil.android.base.dataobjects.Coordinator;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.profile.views.activity.UserPublicProfileActivity;
import in.securelearning.lil.android.syncadapter.dataobject.EnrollTrainingResponse;
import in.securelearning.lil.android.syncadapter.events.RefreshAvailableTrainingListEvent;
import in.securelearning.lil.android.syncadapter.events.RefreshTrainingListEvent;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 28-Dec-17.
 */

public class TrainingStaticDetailsFragment extends Fragment {

    @Inject
    HomeModel mHomeModel;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    JobModel mJobModel;
    @Inject
    RxBus mRxBus;
    @Inject
    AppUserModel mAppUserModel;
    public static final String TRAINING_ID = "trainingId";
    public static final String TRAINING_GROUP_ID = "trainingGroupId";
    public static final String VIEW_TYPE = "viewType";
    private String mTrainingId;
    private String mTrainingGroupId;
    private int mViewType;
    LayoutTrainingStaticDetailBinding mBinding;
    private Training mOnlineTraining;
    private Group mOnlineGroup;

    public static Fragment newInstance(String trainingId, String trainingGroupId, int viewType) {
        TrainingStaticDetailsFragment trainingStaticDetailsFragment = new TrainingStaticDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TRAINING_ID, trainingId);
        bundle.putString(TRAINING_GROUP_ID, trainingGroupId);
        bundle.putInt(VIEW_TYPE, viewType);
        trainingStaticDetailsFragment.setArguments(bundle);
        return trainingStaticDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_training_static_detail, container, false);
        if (getArguments() != null) {
            mTrainingId = getArguments().getString(TRAINING_ID);
            mTrainingGroupId = getArguments().getString(TRAINING_GROUP_ID);
            mViewType = getArguments().getInt(VIEW_TYPE);
        }

        handleType(mViewType);
        return mBinding.getRoot();
    }

    private void handleType(final int viewType) {
        if (viewType == 0) {
            getTrainingDetailsOffline(mTrainingId);
        } else if (viewType == 1) {
            getTrainingDetailsOnline(mTrainingId);
            mBinding.buttonJoinTraining.setVisibility(View.VISIBLE);
        }
    }

    private void getTrainingDetailsOnline(final String trainingId) {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            mBinding.layoutProgress.setVisibility(View.VISIBLE);
            Observable.create(new ObservableOnSubscribe<Training>() {
                @Override
                public void subscribe(ObservableEmitter<Training> e) throws Exception {
                    Call<Training> call = mNetworkModel.fetchTraining(trainingId);
                    Response<Training> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        Training training = response.body();
                        Log.e("TrainingDetail1", "successful");
                        e.onNext(training);
                    } else if (response.code() == 404) {
                        throw new Exception(getString(R.string.messageNoTrainingsFound));
                    } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
                        Response<Training> response2 = call.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            Training training = response2.body();
                            Log.e("TrainingDetail2--", "Successful");
                            e.onNext(training);
                        } else if ((response2.code() == 401)) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
                        } else if (response2.code() == 404) {
                            throw new Exception(getString(R.string.messageNoTrainingsFound));
                        } else {
                            Log.e("TrainingDetail2--", "Failed");
                            throw new Exception(getString(R.string.messageTrainingsFetchFailed));
                        }
                    } else {
                        Log.e("TrainingDetail1--", "Failed");
                        throw new Exception(getString(R.string.messageTrainingsFetchFailed));
                    }

                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Training>() {
                        @Override
                        public void accept(Training training) throws Exception {
                            mOnlineTraining = training;
                            if (mOnlineTraining != null) {
                                initializeViewsAndListeners(mOnlineTraining);
                                getTrainingGroupOnline(mOnlineTraining.getGroupId());
                                getSessions(mOnlineTraining.getSessions());
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            //Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void getTrainingDetailsOffline(final String trainingId) {
        mBinding.layoutProgress.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<Training>() {
            @Override
            public void subscribe(ObservableEmitter<Training> e) throws Exception {
                Training training = mHomeModel.getTrainingById(trainingId);
                e.onNext(training);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Training>() {
                    @Override
                    public void accept(Training training) throws Exception {
                        initializeViewsAndListeners(training);
                        getTrainingGroupOffline(mTrainingGroupId);
                        getSessions(training.getSessions());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getTrainingGroupOnline(final String trainingGroupId) {
        Observable.create(new ObservableOnSubscribe<Group>() {
            @Override
            public void subscribe(ObservableEmitter<Group> e) throws Exception {
                Call<Group> call = mNetworkModel.fetchGroup(trainingGroupId);
                Response<Group> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    Group group = response.body();
                    Log.e("TrainingGroup1", "successful");
                    e.onNext(group);
                } else if (response.code() == 404) {
                    throw new Exception(getString(R.string.messageTrainingGroupFetchFailed));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
                    Response<Group> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Group group = response.body();
                        Log.e("TrainingGroup2--", "Successful");
                        e.onNext(group);
                    } else if ((response2.code() == 401)) {
                        startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
                    } else if (response2.code() == 404) {
                        throw new Exception(getString(R.string.messageTrainingGroupFetchFailed));
                    } else {
                        Log.e("TrainingGroup2--", "Failed");
                        throw new Exception(getString(R.string.messageTrainingGroupFetchFailed));
                    }
                } else {
                    Log.e("TrainingGroup1--", "Failed");
                    throw new Exception(getString(R.string.messageTrainingGroupFetchFailed));
                }

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Group>() {
                    @Override
                    public void accept(Group group) throws Exception {
                        mOnlineGroup = group;
                        mBinding.layoutProgress.setVisibility(View.GONE);
                        mBinding.layoutContent.setVisibility(View.VISIBLE);
                        if (mOnlineGroup != null) {
                            setCoordinators(mOnlineGroup.getCoordinators());
                            setTrainers(mOnlineGroup.getModerators());
                            if (mOnlineGroup.getModerators().contains(new Moderator(mAppUserModel.getObjectId(), ""))) {
                                setTrainees(mOnlineGroup.getMembers());
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        //Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getTrainingGroupOffline(final String trainingGroupId) {
        Observable.create(new ObservableOnSubscribe<Group>() {
            @Override
            public void subscribe(ObservableEmitter<Group> e) throws Exception {
                Group group = mHomeModel.getGroupFromId(trainingGroupId);
                e.onNext(group);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Group>() {
                    @Override
                    public void accept(Group group) throws Exception {
                        mBinding.layoutProgress.setVisibility(View.GONE);
                        mBinding.layoutContent.setVisibility(View.VISIBLE);
                        setCoordinators(group.getCoordinators());
                        setTrainers(group.getModerators());
                        if (group.getModerators().contains(new Moderator(mAppUserModel.getObjectId(), ""))) {
                            setTrainees(group.getMembers());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getSessions(final ArrayList<TrainingSession> trainingSessions) {
        ArrayList<Skill> skills = new ArrayList<>();
        HashMap<String, Topic> topicHashMap = new HashMap<>();
        for (TrainingSession trainingSession : trainingSessions) {
            for (Topic topic : trainingSession.getTopics()) {
                topicHashMap.put(topic.getId(), topic);
            }
        }
        for (Topic topic : topicHashMap.values()) {
            skills.addAll(topic.getSkills());

        }
        if (skills != null) {
            setLearningObjectives(skills);
        }
    }

    private void initializeViewsAndListeners(Training training) {
        setThumbnail(training.getThumbnail());
        setTitle(training.getTitle());
        setGoal(training.getGoal());
        setTime(training.getStartDate(), training.getEndDate());
        setVenue(training.getVenue());
        setEligibility(training.getEligibilityTypeOther());
        setInstruction(training.getInstructionSet());

        mBinding.buttonJoinTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mTrainingId)) {
                    enrollTraining(mTrainingId);
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void enrollTraining(final String trainingId) {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", getString(R.string.messagePleaseWait), false);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progressDialog.dismiss();
                }
            });
            Observable.create(new ObservableOnSubscribe<EnrollTrainingResponse>() {
                @Override
                public void subscribe(ObservableEmitter<EnrollTrainingResponse> e) throws Exception {
                    Call<EnrollTrainingResponse> call = mNetworkModel.enrollTraining(trainingId);
                    Response<EnrollTrainingResponse> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        EnrollTrainingResponse enrollTrainingResponse = response.body();
                        Log.e("EnrollResponse1--", "successful");
                        e.onNext(enrollTrainingResponse);
                    } else if (response.code() == 404) {
                        throw new Exception(getString(R.string.messageEnrollTrainingFailed));
                    } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
                        Response<EnrollTrainingResponse> response2 = call.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            EnrollTrainingResponse enrollTrainingResponse = response2.body();
                            Log.e("EnrollResponse2--", "successful");
                            e.onNext(enrollTrainingResponse);
                        } else if ((response2.code() == 401)) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
                        } else if (response2.code() == 404) {
                            throw new Exception(getString(R.string.messageEnrollTrainingFailed));
                        } else {
                            Log.e("EnrollResponse2--", "Failed");
                            throw new Exception(getString(R.string.messageEnrollTrainingFailed));
                        }
                    } else {
                        Log.e("EnrollResponse1--", "Failed");
                        throw new Exception(getString(R.string.messageEnrollTrainingFailed));
                    }
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<EnrollTrainingResponse>() {
                @Override
                public void accept(EnrollTrainingResponse enrollTrainingResponse) throws Exception {
                    progressDialog.dismiss();
                    if (enrollTrainingResponse.getStatus()) {
                        if (mOnlineTraining != null && mOnlineGroup != null) {
                            mJobModel.saveTrainingAndSession(mOnlineTraining);
                            mJobModel.saveGroup(mOnlineGroup);
                            UserProfile userProfile = mAppUserModel.getApplicationUser();
                            GroupAbstract groupAbstract = new GroupAbstract();
                            groupAbstract.setName(mOnlineGroup.getGroupName());
                            groupAbstract.setObjectId(mOnlineGroup.getObjectId());
                            groupAbstract.setThumbnail(mOnlineGroup.getThumbnail());
                            userProfile.getMemberGroups().add(groupAbstract);
                            mAppUserModel.saveUserProfile(userProfile);
                            mBinding.buttonJoinTraining.setVisibility(View.GONE);
                            mRxBus.send(new RefreshTrainingListEvent());
                            mRxBus.send(new RefreshAvailableTrainingListEvent());
                            if (!TextUtils.isEmpty(mOnlineTraining.getTitle())) {
                                SnackBarUtils.showSuccessSnackBar(getContext(), mBinding.layoutContent, "You have successfully enrolled into " + mOnlineTraining.getTitle());
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.messageEnrollTrainingFailed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });
        } else {
            SnackBarUtils.showNoInternetSnackBar(getContext(), mBinding.layoutContent);
        }

    }

    private void setThumbnail(Thumbnail thumbnail) {
        if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
            Picasso.with(getContext()).load(thumbnail.getLocalUrl()).resize(680, 420).centerCrop().into(mBinding.imageViewThumbnail);

        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
            Picasso.with(getContext()).load(thumbnail.getUrl()).resize(680, 420).centerCrop().into(mBinding.imageViewThumbnail);

        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
            Picasso.with(getContext()).load(thumbnail.getThumb()).resize(680, 420).centerCrop().into(mBinding.imageViewThumbnail);

        }
    }

    private void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mBinding.textViewTitle.setText(title);
        }
    }

    private void setGoal(String goal) {
        if (!TextUtils.isEmpty(goal)) {
            TextViewMore.viewMore(goal, mBinding.textViewGoal, mBinding.includeTextViewMoreLessGoal.textViewMoreLess);
        }
    }

    private void setTime(String startDate, String endDate) {
        if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            mBinding.textViewTime.setText("From " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(startDate)) + " - To " + DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(endDate)));
        }
    }

    private void setVenue(String venue) {
        if (!TextUtils.isEmpty(venue)) {
            mBinding.textViewVenue.setText(venue);
        } else {
            mBinding.layoutVenue.setVisibility(View.GONE);
        }
    }

    private void setEligibility(String eligibility) {
        if (!TextUtils.isEmpty(eligibility)) {
            mBinding.textViewEligibility.setText(eligibility);
        } else {
            mBinding.layoutEligibility.setVisibility(View.GONE);
        }
    }

    private void setInstruction(String instruction) {
        if (!TextUtils.isEmpty(instruction)) {
            TextViewMore.viewMore(instruction, mBinding.textViewInstruction, mBinding.includeTextViewMoreLessInstructions.textViewMoreLess);
        } else {
            mBinding.layoutIntruction.setVisibility(View.GONE);
        }
    }

    private void setCoordinators(ArrayList<Coordinator> coordinators) {
        if (!coordinators.isEmpty()) {
            mBinding.layoutCoordinators.setVisibility(View.VISIBLE);
            mBinding.recyclerViewCoordinators.setNestedScrollingEnabled(false);
            mBinding.recyclerViewCoordinators.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            CoordinatorAdapter coordinatorAdapter = new CoordinatorAdapter(coordinators);
            mBinding.recyclerViewCoordinators.setAdapter(coordinatorAdapter);
        }
    }

    private void setTrainers(ArrayList<Moderator> moderators) {
        if (!moderators.isEmpty()) {
            mBinding.layoutTrainers.setVisibility(View.VISIBLE);
            mBinding.recyclerViewTrainers.setNestedScrollingEnabled(false);
            mBinding.recyclerViewTrainers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            TrainerAdapter trainerAdapter = new TrainerAdapter(moderators);
            mBinding.recyclerViewTrainers.setAdapter(trainerAdapter);
        }

    }

    private void setTrainees(ArrayList<GroupMember> trainees) {

        if (!trainees.isEmpty()) {
            mBinding.layoutTrainees.setVisibility(View.VISIBLE);
            mBinding.recyclerViewTrainees.setNestedScrollingEnabled(false);
            mBinding.recyclerViewTrainees.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            TraineesAdapter traineesAdapter = new TraineesAdapter(trainees);
            mBinding.recyclerViewTrainees.setAdapter(traineesAdapter);
        }


    }

    private void setLearningObjectives(ArrayList<Skill> skills) {
        if (!skills.isEmpty()) {
            mBinding.layoutLearningObjectives.setVisibility(View.VISIBLE);
            mBinding.recyclerViewLearningObjectives.setNestedScrollingEnabled(false);
            mBinding.recyclerViewLearningObjectives.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            SkillAdapter skillAdapter = new SkillAdapter(skills);
            mBinding.recyclerViewLearningObjectives.setAdapter(skillAdapter);
        }

    }

    private void setUserThumbnail(String name, Thumbnail thumbnail, AppCompatImageView imageView) {
        if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
            Picasso.with(getContext()).load(thumbnail.getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
            Picasso.with(getContext()).load(thumbnail.getUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
            Picasso.with(getContext()).load(thumbnail.getThumb()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
        } else {
            String firstWord = name.substring(0, 1).toUpperCase();
            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
            imageView.setImageDrawable(textDrawable);
        }
    }

    private class CoordinatorAdapter extends RecyclerView.Adapter<CoordinatorAdapter.ViewHolder> {
        private ArrayList<Coordinator> mCoordinators;

        public CoordinatorAdapter(ArrayList<Coordinator> coordinators) {
            mCoordinators = coordinators;
        }

        @Override
        public CoordinatorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemGroupMembersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_group_members, parent, false);
            return new CoordinatorAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(CoordinatorAdapter.ViewHolder holder, int position) {
            final Coordinator coordinator = mCoordinators.get(position);
            holder.mBinding.textViewUserName.setText(coordinator.getName());

            setUserThumbnail(coordinator.getName(), coordinator.getThumbnail(), holder.mBinding.imageViewUserIcon);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        startActivity(UserPublicProfileActivity.getStartIntent(getContext(), coordinator.getId()));
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getContext(), view);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mCoordinators.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutItemGroupMembersBinding mBinding;

            public ViewHolder(LayoutItemGroupMembersBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.ViewHolder> {
        ArrayList<Moderator> mModerators;

        public TrainerAdapter(ArrayList<Moderator> moderators) {
            mModerators = moderators;
        }

        @Override
        public TrainerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemGroupMembersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_group_members, parent, false);
            return new TrainerAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(TrainerAdapter.ViewHolder holder, int position) {
            final Moderator moderator = mModerators.get(position);
            holder.mBinding.textViewUserName.setText(moderator.getName());

            setUserThumbnail(moderator.getName(), moderator.getPic(), holder.mBinding.imageViewUserIcon);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        startActivity(UserPublicProfileActivity.getStartIntent(getContext(), moderator.getId()));
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getContext(), view);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mModerators.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutItemGroupMembersBinding mBinding;

            public ViewHolder(LayoutItemGroupMembersBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class TraineesAdapter extends RecyclerView.Adapter<TraineesAdapter.ViewHolder> {
        ArrayList<GroupMember> mGroupMembers;

        public TraineesAdapter(ArrayList<GroupMember> groupMembers) {
            mGroupMembers = groupMembers;
        }

        @Override
        public TraineesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemGroupMembersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_group_members, parent, false);
            return new TraineesAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(TraineesAdapter.ViewHolder holder, int position) {
            final GroupMember groupMember = mGroupMembers.get(position);
            holder.mBinding.textViewUserName.setText(groupMember.getName());

            setUserThumbnail(groupMember.getName(), groupMember.getPic(), holder.mBinding.imageViewUserIcon);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        // group member always teacher as told
                        startActivity(UserPublicProfileActivity.getStartIntent(getContext(), groupMember.getObjectId()));
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getContext(), view);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mGroupMembers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutItemGroupMembersBinding mBinding;

            public ViewHolder(LayoutItemGroupMembersBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {
        private ArrayList<Skill> mSkills = new ArrayList<>();

        public SkillAdapter(ArrayList<Skill> skills) {
            mSkills = skills;
        }

        @Override
        public SkillAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutSkillItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_skill_item, parent, false);
            return new SkillAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(SkillAdapter.ViewHolder holder, int position) {
            Skill skill = mSkills.get(position);
            holder.mBinding.textViewName.setText(skill.getSkillName());
            holder.mBinding.textViewTopic.setText(skill.getTopic().getName());
        }

        @Override
        public int getItemCount() {
            return mSkills.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutSkillItemBinding mBinding;

            public ViewHolder(LayoutSkillItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
