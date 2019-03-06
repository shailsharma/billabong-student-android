package in.securelearning.lil.android.learningnetwork.views.activity;

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
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Chaitendra on 11-Aug-17.
 */

public class GroupDetailActivity extends AppCompatActivity {

    @Inject
    GroupModel mGroupModel;
    @Inject
    RxBus mRxBus;

    private Disposable mDisposable;
    private LayoutActivityGroupDetailBinding mBinding;
    public static final String GROUP_ID = "groupId";
    private String mGroupId = "";
    private String mGroupTitle = "";
    private Group mGroup;
    private int mColor = 0;

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
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_activity_group_detail);
        setupSubscription();
        handleIntent();
    }

    private void handleIntent() {

        if (getIntent() != null) {
            mGroupId = getIntent().getStringExtra(GROUP_ID);
            mGroup = mGroupModel.getGroupFromUidSync(mGroupId);
            mGroupTitle = mGroup.getGroupName();
            if (!TextUtils.isEmpty(mGroup.getNameTeacher())) {
                mGroupTitle = mGroup.getNameTeacher();
            }
            PrefManager.SubjectExt subjectExt = null;
            if (mGroup.getSubject() != null && !TextUtils.isEmpty(mGroup.getSubject().getId())) {
                HashMap<String, PrefManager.SubjectExt> mSubjectMap = PrefManager.getSubjectMap(getBaseContext());
                subjectExt = mSubjectMap.get(mGroup.getSubject().getId());
            }
            if (subjectExt == null) {
                subjectExt = PrefManager.getDefaultSubject();
            }
            mColor = subjectExt.getTextColor();
            setAppBarUi(mColor);
            setGroupDetail();
            setGroupMembersAndModerators();
        }
    }

    private void setGroupDetail() {
        setGroupBanner();
        setGroupIcon();
        setGroupInfo();
    }

    private void setAppBarUi(int color) {



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
        setNameOnToolbar();
        mBinding.collapsingToolbar.setTitleEnabled(false);
        mBinding.collapsingToolbar.setContentScrimColor(color);
        mBinding.collapsingToolbar.setStatusBarScrimColor(color);
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

    private void setNameOnToolbar() {
        mBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isVisible = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mBinding.toolbar.setTitle(mGroupTitle);
                    isVisible = true;
                } else if (isVisible) {

                    mBinding.toolbar.setTitle("");
                    isVisible = false;
                }
            }
        });

    }

    private void setGroupInfo() {
        mBinding.textViewGroupName.setText(mGroupTitle);
        mBinding.textViewGroupPurpose.setText(mGroup.getPurpose());
    }

    private void setGroupIcon() {
        if (mGroup.getThumbnail().getLocalUrl() != null && !mGroup.getThumbnail().getLocalUrl().isEmpty()) {
            Picasso.with(getBaseContext()).load(mGroup.getThumbnail().getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(mBinding.imageViewGroupIcon);
        } else if (mGroup.getThumbnail().getUrl() != null && !mGroup.getThumbnail().getUrl().isEmpty()) {
            Picasso.with(getBaseContext()).load(mGroup.getThumbnail().getUrl()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(mBinding.imageViewGroupIcon);
        } else if (mGroup.getThumbnail().getThumb() != null && !mGroup.getThumbnail().getThumb().isEmpty()) {
            Picasso.with(getBaseContext()).load(mGroup.getThumbnail().getThumb()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(mBinding.imageViewGroupIcon);
        } else {
            Picasso.with(getBaseContext()).load(R.drawable.audience_g_w).transform(new CircleTransform()).into(mBinding.imageViewGroupIcon);
        }
    }

    private void setGroupBanner() {

        if (mGroup.getBanner().getLocalUrl() != null && !mGroup.getBanner().getLocalUrl().isEmpty()) {
            Picasso.with(getBaseContext()).load(mGroup.getBanner().getLocalUrl()).fit().into(mBinding.imageViewGroupBanner);
        } else if (mGroup.getBanner().getUrl() != null && !mGroup.getBanner().getUrl().isEmpty()) {
            Picasso.with(getBaseContext()).load(mGroup.getBanner().getUrl()).fit().into(mBinding.imageViewGroupBanner);
        } else if (mGroup.getBanner().getThumb() != null && !mGroup.getBanner().getThumb().isEmpty()) {
            Picasso.with(getBaseContext()).load(mGroup.getBanner().getThumb()).fit().into(mBinding.imageViewGroupBanner);
        } else {
            mBinding.imageViewGroupBanner.setVisibility(View.GONE);
        }

    }

    private void setGroupMembersAndModerators() {
        mBinding.recyclerViewMembers.setNestedScrollingEnabled(false);
        mBinding.recyclerViewModerators.setNestedScrollingEnabled(false);
        mBinding.recyclerViewMembers.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewModerators.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<GroupMember> groupMembers = new ArrayList<>();
        ArrayList<Moderator> moderators = new ArrayList<>();
        groupMembers.addAll(mGroup.getMembers());
        moderators.addAll(mGroup.getModerators());

        MemberAdapter memberAdapter = new MemberAdapter(groupMembers);
        mBinding.recyclerViewMembers.setAdapter(memberAdapter);

        ModeratorAdapter moderatorAdapter = new ModeratorAdapter(moderators);
        mBinding.recyclerViewModerators.setAdapter(moderatorAdapter);
    }

    private void setupSubscription() {
        mDisposable = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(Group.class) && ((ObjectDownloadComplete) event).getId().equals(mGroupId)) {
                    handleIntent();
                }
            }
        });
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
                        startActivity(UserProfileActivity.getStartIntent(groupMember.getObjectId(), getBaseContext()));
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
                        Intent mIntent = UserProfileActivity.getStartIntent(moderator.getId(), getBaseContext());
                        startActivity(mIntent);
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
