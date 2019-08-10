package in.securelearning.lil.android.learningnetwork.views.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;

/**
 * A simple {@link Fragment} subclass.
 */
public class MembersFragment extends BaseLNFragment {

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    GroupModel mGroupModel;

    private View mRootView;
    private TextView mGroupPurposeTextView, mGroupPurposeLabelTextView, mViewMoreLessTextView;
    private RecyclerView mMemberRecyclerView;
    private RecyclerView mModeratorRecyclerView;
    private ArrayList<Moderator> mGroupModeratorArrayList;
    private ArrayList<Group> mGroupArrayList;
    private ArrayList<GroupMember> mGroupMembersArrayList;
    private MemberAdapter mMemberAdapter;
    private ModeratorAdapter mModeratorAdapter;
    private int intGroupIndex;
    private Group mSelectedGroup;
    private Context mContext;

    public MembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_member, container, false);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        initializeViews();
        fetchGroup();
        return mRootView;
    }

    private void initializeViews() {
        mGroupPurposeTextView = (TextView) mRootView.findViewById(R.id.textviewGroupPurpose);
        mGroupPurposeLabelTextView = (TextView) mRootView.findViewById(R.id.textViewGroupPurposeLabel);
        mViewMoreLessTextView = (TextView) mRootView.findViewById(R.id.textViewMoreLess);
        mMemberRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_members);
        mModeratorRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_moderators);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void fetchGroup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSelectedGroup = mGroupModel.getGroupFromUidSync(getArguments().getString(ARG_GROUP_OBJECT_ID));

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupGroupDetails(mSelectedGroup);
                        initializeRecyclerView();
                    }
                });
            }
        }).start();

    }

    private void setupGroupDetails(Group mSelectedGroup) {

        mGroupMembersArrayList = mSelectedGroup.getMembers();
        mGroupModeratorArrayList = mSelectedGroup.getModerators();
        if (!TextUtils.isEmpty(mSelectedGroup.getPurpose())) {
            TextViewMore.viewMore(mSelectedGroup.getPurpose(), mGroupPurposeTextView, mViewMoreLessTextView);
            mGroupPurposeTextView.clearComposingText();
        } else {
            mGroupPurposeTextView.setVisibility(View.GONE);
            mGroupPurposeLabelTextView.setVisibility(View.GONE);
        }

    }


    private void initializeRecyclerView() {
        mMemberRecyclerView.setNestedScrollingEnabled(false);
        mModeratorRecyclerView.setNestedScrollingEnabled(false);
        mMemberRecyclerView.setHasFixedSize(true);
        mModeratorRecyclerView.setHasFixedSize(true);
        if (mContext.getResources().getBoolean(R.bool.isTablet)) {
            mMemberRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
            mModeratorRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        } else {
            mMemberRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
            mModeratorRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        }
        mMemberAdapter = new MemberAdapter(mContext, mGroupMembersArrayList);
        mMemberRecyclerView.setAdapter(mMemberAdapter);

        mModeratorAdapter = new ModeratorAdapter(mContext, mGroupModeratorArrayList);
        mModeratorRecyclerView.setAdapter(mModeratorAdapter);
    }

    /**
     * Adapter to bind member data recycler view
     * Created by Cp on 12/2/2016.
     */
    public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
        Context mContext;
        ArrayList<GroupMember> mUserArrayList = new ArrayList<>();

        public MemberAdapter(Context context, ArrayList<GroupMember> userArrayList) {
            mContext = context;
            this.mUserArrayList = userArrayList;
        }

        @Override
        public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member_grid_itemview, parent, false);
            MemberAdapter.ViewHolder mViewHolder = new MemberAdapter.ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(MemberAdapter.ViewHolder holder, final int position) {
            final GroupMember groupMember = mUserArrayList.get(position);
            holder.mMemberNameTextView.setText(mUserArrayList.get(position).getName());

            setUserThumbnail(groupMember, holder.mUserThumbnailImageView);

            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BuildConfig.ViewVisibilty == false) {

                    } else {
                        if (GeneralUtils.isNetworkAvailable(mContext)) {
                            Intent mIntent = UserProfileActivity.getStartIntent(mUserArrayList.get(position).getObjectId(), mContext);
                            startActivity(mIntent);
                        } else {

                            ToastUtils.showToastAlert(mContext, getString(R.string.connect_internet));
                        }
                    }
                }
            });

        }

        private void setUserThumbnail(GroupMember groupMember, ImageView userThumbnailImageView) {
            if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getLocalUrl())) {
                Picasso.with(mContext).load(groupMember.getPic().getLocalUrl()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(userThumbnailImageView);
            } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getUrl())) {
                Picasso.with(mContext).load(groupMember.getPic().getUrl()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(userThumbnailImageView);
            } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getThumb())) {
                Picasso.with(mContext).load(groupMember.getPic().getThumb()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(userThumbnailImageView);
            } else {
                String firstWord = groupMember.getName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRect(firstWord, R.color.colorPrimaryLN);
                userThumbnailImageView.setImageDrawable(textDrawable);
            }
        }

        @Override
        public int getItemCount() {
            return mUserArrayList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private View mRootView;
            private TextView mMemberNameTextView;
            private ImageView mUserThumbnailImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mMemberNameTextView = (TextView) mRootView.findViewById(R.id.textview_member_name);
                mUserThumbnailImageView = (ImageView) mRootView.findViewById(R.id.imageview_member_icon);
            }
        }
    }

    /**
     * Adapter to bind moderator data recycler view
     * Created by Cp on 12/2/2016.
     */
    public class ModeratorAdapter extends RecyclerView.Adapter<ModeratorAdapter.ViewHolder> {
        Context mContext;
        ArrayList<Moderator> mModeratorList = new ArrayList<>();

        public ModeratorAdapter(Context context, ArrayList<Moderator> userArrayList) {
            mContext = context;
            this.mModeratorList = userArrayList;
        }

        @Override
        public ModeratorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member_grid_itemview, parent, false);
            ModeratorAdapter.ViewHolder mViewHolder = new ModeratorAdapter.ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ModeratorAdapter.ViewHolder holder, final int position) {
            final Moderator moderator = mModeratorList.get(position);
            holder.mMemberNameTextView.setText(mModeratorList.get(position).getName());

            if (moderator.getPic().getUrl() != null && !moderator.getPic().getUrl().isEmpty()) {
                Picasso.with(mContext).load(moderator.getPic().getUrl()).resize(600, 600).placeholder(R.drawable.icon_profile_large).centerCrop().into(holder.mMemberIconImage);
            } else if (moderator.getPic().getThumb() != null && !moderator.getPic().getThumb().isEmpty()) {
                Picasso.with(mContext).load(moderator.getPic().getUrl()).resize(600, 600).placeholder(R.drawable.icon_profile_large).centerCrop().into(holder.mMemberIconImage);
            } else {
                String firstWord = moderator.getName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRect(firstWord, R.color.colorPrimaryLN);
                holder.mMemberIconImage.setImageDrawable(textDrawable);
            }

            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BuildConfig.ViewVisibilty == false) {

                    } else {
                        if (GeneralUtils.isNetworkAvailable(mContext)) {
                            Intent mIntent = new Intent(mContext, UserProfileActivity.class);
                            mIntent.putExtra(UserProfileActivity.USER_ID, mModeratorList.get(position).getId());
                            startActivity(mIntent);
                        } else {

                            ToastUtils.showToastAlert(mContext, "Please connect to internet");
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return mModeratorList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private View mRootView;
            private TextView mMemberNameTextView;
            private ImageView mMemberIconImage;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mMemberNameTextView = (TextView) mRootView.findViewById(R.id.textview_member_name);
                mMemberIconImage = (ImageView) mRootView.findViewById(R.id.imageview_member_icon);
            }
        }
    }

}
