package in.securelearning.lil.android.learningnetwork.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutItemGroupMembersBinding;
import in.securelearning.lil.android.app.databinding.LayoutLikedListBinding;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 2/22/2017.
 */
public class PostLikeActivity extends AppCompatActivity {

    @Inject
    GroupModel mGroupModel;

    @Inject
    PostDataLearningModel mPostDataLearningModel;

    private LayoutLikedListBinding mBinding;
    private static String GROUP_ID = "groupId";
    private static String POST_ID = "postId";
    private String mGroupId = "";
    private String mPostId = "";
    private PostLikeAdapter mPostLikeAdapter;
    private Group mGroup;
    private int mSkip = 0;
    private int mLimit = 30;

    public static Intent getIntentForPostLikeList(Context context, String groupId, String postId) {
        Intent intent = new Intent(context, PostLikeActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        intent.putExtra(POST_ID, postId);
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_liked_list);
        handleIntent();

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


    private void handleIntent() {
        mGroupId = getIntent().getStringExtra(GROUP_ID);
        mPostId = getIntent().getStringExtra(POST_ID);
        initializeRecyclerView(new ArrayList<PostResponse>());
        setUpToolbar();
        getGroup(mGroupId);
        getLikedPostResponses(mPostId, mSkip, mLimit);
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryLN));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Post Liked By");

    }

    private void getLikedPostResponses(String postId, int skip, final int limit) {
        mPostDataLearningModel.getPostResponseListForPost(postId, PostResponseType.TYPE_RECOMMEND.getPostResponseType(), skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PostResponse>>() {
            @Override
            public void accept(ArrayList<PostResponse> postResponses) throws Exception {
                mSkip += postResponses.size();
                if (postResponses.size() < limit) {
                    mBinding.recyclerView.removeOnScrollListener(null);
                }
                mPostLikeAdapter.addItem(postResponses);

            }
        });
    }

    private void getGroup(String groupId) {
        if (!TextUtils.isEmpty(groupId)) {
            mGroup = mGroupModel.getGroupFromUidSync(groupId);
        }
    }


    private void initializeRecyclerView(ArrayList<PostResponse> postResponses) {

        LinearLayoutManager layoutManager = null;
        mBinding.recyclerView.getItemAnimator().setChangeDuration(0);
        ((DefaultItemAnimator) mBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mPostLikeAdapter = new PostLikeAdapter(getBaseContext(), postResponses);
        mPostLikeAdapter.setHasStableIds(true);
        mBinding.recyclerView.setAdapter(mPostLikeAdapter);


        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy < 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mSkip - 1) {

                            getLikedPostResponses(mPostId, mSkip, mLimit);

                        }
                    }

                }

            });
        }
    }

    public class PostLikeAdapter extends RecyclerView.Adapter<PostLikeAdapter.ViewHolder> {

        @Inject
        Context mContext;
        private ArrayList<PostResponse> mPostResponses = new ArrayList<>();

        public PostLikeAdapter(Context context, ArrayList<PostResponse> postResponses) {
            this.mContext = context;
            this.mPostResponses = postResponses;
        }

        @Override
        public PostLikeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemGroupMembersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_group_members, parent, false);
            return new PostLikeAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(PostLikeAdapter.ViewHolder holder, final int position) {

            final PostResponse postResponse = mPostResponses.get(position);
            holder.mBinding.textViewUserName.setText(postResponse.getFrom().getName());
            setUserThumbnailToView(holder.mBinding.imageViewUserIcon, postResponse.getFrom().getId());

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        startActivity(UserProfileActivity.getStartIntent(postResponse.getFrom().getId(), mContext));

                    } else {
                        ToastUtils.showToastAlert(mContext, getString(R.string.connect_internet));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPostResponses.size();
        }

        public void addItem(ArrayList<PostResponse> postResponses) {
            if (mPostResponses != null) {
                mPostResponses.addAll(postResponses);
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutItemGroupMembersBinding mBinding;

            public ViewHolder(LayoutItemGroupMembersBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    /**
     * Method for set user thumbnail to view
     *
     * @param imageView in which view you want to set
     * @param userId    id of user for which thumbnail will set
     */
    private void setUserThumbnailToView(ImageView imageView, String userId) {

        boolean isUserThumbnailFetched = false;
        ArrayList<GroupMember> groupMembers = mGroup.getMembers();
        for (GroupMember groupMember : groupMembers) {
            if (groupMember.getObjectId().equalsIgnoreCase(userId)) {
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

                isUserThumbnailFetched = true;
                break;
            }
        }

        if (!isUserThumbnailFetched) {
            ArrayList<Moderator> groupModerators = mGroup.getModerators();
            for (Moderator moderator : groupModerators) {
                if (moderator.getId().equalsIgnoreCase(userId)) {
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
                    break;
                }
            }
        }

    }
}
