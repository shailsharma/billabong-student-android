package in.securelearning.lil.android.learningnetwork.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutActivityGroupDetailBinding;
import in.securelearning.lil.android.app.databinding.LayoutItemGroupMembersBinding;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.views.activity.StudentProfileActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 11-Aug-17.
 */

public class GroupDetailActivity extends AppCompatActivity {

    @Inject
    GroupModel mGroupModel;
    @Inject
    RxBus mRxBus;
    @Inject
    AppUserModel mAppUserModel;

    private LayoutActivityGroupDetailBinding mBinding;
    public static final String GROUP_ID = "groupId";

    public static Intent getIntentForGroupDetail(Context context, String groupId) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_activity_group_detail);
        handleIntent();
    }

    @SuppressLint("CheckResult")
    private void handleIntent() {

        if (getIntent() != null) {
            final String groupId = getIntent().getStringExtra(GROUP_ID);
            mBinding.progressBar.setVisibility(View.VISIBLE);

            Observable.create(new ObservableOnSubscribe<Group>() {
                @Override
                public void subscribe(ObservableEmitter<Group> emitter) throws Exception {
                    Group group = mGroupModel.getGroupFromUidSync(groupId);
                    if (group != null) {
                        emitter.onNext(group);
                    } else {
                        emitter.onError(new Exception(getString(R.string.error_something_went_wrong)));
                    }
                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Group>() {
                        @Override
                        public void accept(Group group) throws Exception {
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.layoutContent.setVisibility(View.VISIBLE);

                            PrefManager.SubjectExt subjectExt = null;
                            if (group.getSubject() != null && !TextUtils.isEmpty(group.getSubject().getId())) {
                                HashMap<String, PrefManager.SubjectExt> mSubjectMap = PrefManager.getSubjectMap(getBaseContext());
                                subjectExt = mSubjectMap.get(group.getSubject().getId());
                            }
                            if (subjectExt == null) {
                                subjectExt = PrefManager.getDefaultSubject();
                            }
                            int color = subjectExt.getTextColor();
                            setAppBarUi(group, color);
                            setGroupDetail(group);
                            setGroupMembersAndModerators(group);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mBinding.progressBar.setVisibility(View.GONE);
                            throwable.printStackTrace();
                        }
                    });


        }
    }

    private void setGroupDetail(Group group) {
        setGroupBanner(group);
        setGroupIcon(group);
        setGroupInfo(group);
    }

    private void setAppBarUi(Group group, int color) {


//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        int heightInPixel = displayMetrics.heightPixels;

        /*app bar layout height set dynamically*/
//        int ablHeight = (int) (heightInPixel * 0.60);
//        mBinding.appBarLayout.getLayoutParams().height = ablHeight;
//        mBinding.appBarLayout.setBackgroundColor(color);

        /*fixing status bar color*/
        // getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLN));

        /*header image height set dynamically*/
//        int headerHeight = (int) (ablHeight * 0.50);
//        mBinding.imageViewGroupBanner.getLayoutParams().height = headerHeight;
//        mBinding.layoutGroupDetail.getLayoutParams().height = headerHeight;

        setTitle("");
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNameOnToolbar(group.getGroupName());
        mBinding.collapsingToolbar.setTitleEnabled(false);
        mBinding.collapsingToolbar.setContentScrimColor(color);
        mBinding.collapsingToolbar.setStatusBarScrimColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNameOnToolbar(final String groupName) {
        mBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isVisible = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mBinding.toolbar.setTitle(groupName);
                    isVisible = true;
                } else if (isVisible) {

                    mBinding.toolbar.setTitle("");
                    isVisible = false;
                }
            }
        });

    }

    private void setGroupInfo(Group group) {
        mBinding.textViewGroupName.setText(group.getGroupName());
        mBinding.textViewGroupPurpose.setText(group.getPurpose());
    }

    private void setGroupIcon(Group group) {
        if (group.getThumbnail().getLocalUrl() != null && !group.getThumbnail().getLocalUrl().isEmpty()) {
            Picasso.with(getBaseContext()).load(group.getThumbnail().getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(mBinding.imageViewGroupIcon);
        } else if (group.getThumbnail().getUrl() != null && !group.getThumbnail().getUrl().isEmpty()) {
            Picasso.with(getBaseContext()).load(group.getThumbnail().getUrl()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(mBinding.imageViewGroupIcon);
        } else if (group.getThumbnail().getThumb() != null && !group.getThumbnail().getThumb().isEmpty()) {
            Picasso.with(getBaseContext()).load(group.getThumbnail().getThumb()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(mBinding.imageViewGroupIcon);
        } else {
            Picasso.with(getBaseContext()).load(R.drawable.audience_g_w).transform(new CircleTransform()).into(mBinding.imageViewGroupIcon);
        }
    }

    private void setGroupBanner(Group group) {

        if (group.getBanner().getLocalUrl() != null && !group.getBanner().getLocalUrl().isEmpty()) {
            Picasso.with(getBaseContext()).load(group.getBanner().getLocalUrl()).fit().centerCrop().into(mBinding.imageViewGroupBanner);
        } else if (group.getBanner().getUrl() != null && !group.getBanner().getUrl().isEmpty()) {
            Picasso.with(getBaseContext()).load(group.getBanner().getUrl()).fit().centerCrop().into(mBinding.imageViewGroupBanner);
        } else if (group.getBanner().getThumb() != null && !group.getBanner().getThumb().isEmpty()) {
            Picasso.with(getBaseContext()).load(group.getBanner().getThumb()).fit().centerCrop().into(mBinding.imageViewGroupBanner);
        } else {
            mBinding.imageViewGroupBanner.setVisibility(View.GONE);
        }

    }

    private void setGroupMembersAndModerators(Group group) {
        mBinding.recyclerViewMembers.setNestedScrollingEnabled(false);
        mBinding.recyclerViewModerators.setNestedScrollingEnabled(false);
        mBinding.recyclerViewMembers.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewModerators.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<GroupMember> groupMembers = new ArrayList<>();
        ArrayList<Moderator> moderators = new ArrayList<>();
        groupMembers.addAll(group.getMembers());
        moderators.addAll(group.getModerators());

        MemberAdapter memberAdapter = new MemberAdapter(groupMembers);
        mBinding.recyclerViewMembers.setAdapter(memberAdapter);

        ModeratorAdapter moderatorAdapter = new ModeratorAdapter(moderators);
        mBinding.recyclerViewModerators.setAdapter(moderatorAdapter);
    }


    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
        ArrayList<GroupMember> mGroupMembers = new ArrayList<>();

        public MemberAdapter(ArrayList<GroupMember> groupMembers) {
            this.mGroupMembers = groupMembers;
        }

        @Override
        public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemGroupMembersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_group_members, parent, false);
            return new MemberAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(MemberAdapter.ViewHolder holder, int position) {
            final GroupMember groupMember = mGroupMembers.get(position);
            holder.mBinding.textViewUserName.setText(groupMember.getName());

            setMemberThumbnail(groupMember, holder.mBinding.imageViewUserIcon);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        if (groupMember.getObjectId().equals(mAppUserModel.getObjectId())) {
                            startActivity(StudentProfileActivity.getStartIntent(mAppUserModel.getObjectId(), getBaseContext()));
                        } else {
                            startActivity(UserProfileActivity.getStartIntent(groupMember.getObjectId(), getBaseContext()));
                        }
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getBaseContext(), view);
                    }

                }
            });
        }

        private void setMemberThumbnail(GroupMember groupMember, AppCompatImageView imageView) {
            if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getLocalUrl())) {
                Picasso.with(getBaseContext()).load(groupMember.getPic().getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getUrl())) {
                Picasso.with(getBaseContext()).load(groupMember.getPic().getUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getThumb())) {
                Picasso.with(getBaseContext()).load(groupMember.getPic().getThumb()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else {
                String firstWord = groupMember.getName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimaryLN);
                imageView.setImageDrawable(textDrawable);
            }
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

    private class ModeratorAdapter extends RecyclerView.Adapter<ModeratorAdapter.ViewHolder> {

        private ArrayList<Moderator> mModerators = new ArrayList<>();

        public ModeratorAdapter(ArrayList<Moderator> moderators) {
            this.mModerators = moderators;
        }

        @Override
        public ModeratorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemGroupMembersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_group_members, parent, false);
            return new ModeratorAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ModeratorAdapter.ViewHolder holder, int position) {

            final Moderator moderator = mModerators.get(position);
            holder.mBinding.textViewUserName.setText(moderator.getName());

            setModeratorThumbnail(moderator, holder.mBinding.imageViewUserIcon);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        if (moderator.getId().equals(mAppUserModel.getObjectId())) {
                            startActivity(StudentProfileActivity.getStartIntent(mAppUserModel.getObjectId(), getBaseContext()));
                        } else {
                            startActivity(UserProfileActivity.getStartIntent(moderator.getId(), getBaseContext()));
                        }
                    } else {
                        ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
                    }

                }
            });
        }

        private void setModeratorThumbnail(Moderator moderator, AppCompatImageView imageView) {
            if (moderator.getPic() != null && !TextUtils.isEmpty(moderator.getPic().getLocalUrl())) {
                Picasso.with(getBaseContext()).load(moderator.getPic().getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else if (moderator.getPic() != null && !TextUtils.isEmpty(moderator.getPic().getUrl())) {
                Picasso.with(getBaseContext()).load(moderator.getPic().getUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else if (moderator.getPic() != null && !TextUtils.isEmpty(moderator.getPic().getThumb())) {
                Picasso.with(getBaseContext()).load(moderator.getPic().getThumb()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else {
                String firstWord = moderator.getName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimaryLN);
                imageView.setImageDrawable(textDrawable);
            }
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
}
