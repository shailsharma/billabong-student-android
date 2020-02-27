package in.securelearning.lil.android.learningnetwork.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import in.securelearning.lil.android.base.dataobjects.PostByUser;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.profile.views.activity.StudentProfileActivity;
import in.securelearning.lil.android.profile.views.activity.UserPublicProfileActivity;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by Chaitendra on 2/22/2017.
 */
public class PostLikeActivity extends AppCompatActivity {

    @Inject
    GroupModel mGroupModel;

    @Inject
    PostDataLearningModel mPostDataLearningModel;

    @Inject
    AppUserModel mAppUserModel;

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
        if (item.getItemId() == android.R.id.home) {
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

        setSupportActionBar(mBinding.toolbar);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_light);
        CommonUtils.getInstance().setStatusBarIconsDark(PostLikeActivity.this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.post_liked_by);

    }

    @SuppressLint("CheckResult")
    private void getLikedPostResponses(String postId, int skip, final int limit) {
        mPostDataLearningModel.getPostResponseListForPost(postId, PostResponseType.TYPE_RECOMMEND.getPostResponseType(), skip, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<PostResponse>>() {
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
            setUserThumbnailToView(holder.mBinding.imageViewUserIcon, postResponse.getFrom().getId(), postResponse.getFrom());

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {

                        if (postResponse.getFrom().getId().equals(mAppUserModel.getObjectId())) {
                            // private profile of student
                            startActivity(StudentProfileActivity.getStartIntent(mAppUserModel.getObjectId(), getBaseContext()));

                        } else {

                            if (postResponse.getFrom().getRole().equalsIgnoreCase("Student")) {
                                // public profile of student
//                                startActivity(StudentPublicProfileActivity.getStartIntent(postResponse.getFrom().getId(), getBaseContext()));
                            } else {
                                // public profile of non-student
                                startActivity(UserPublicProfileActivity.getStartIntent(getBaseContext(), postResponse.getFrom().getId()));
                            }

                        }

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
    private void setUserThumbnailToView(ImageView imageView, String userId, PostByUser fromUser) {

        if (!TextUtils.isEmpty(fromUser.getId())
                && fromUser.getId().equalsIgnoreCase(mAppUserModel.getApplicationUser().getObjectId())) {

            Context context = getBaseContext();
            String name = "";
            if (!TextUtils.isEmpty(mAppUserModel.getApplicationUser().getName())) {
                name = mAppUserModel.getApplicationUser().getName();
            }

            Thumbnail thumbnail = new Thumbnail();
            if (mAppUserModel.getApplicationUser().getThumbnail() != null) {
                thumbnail = mAppUserModel.getApplicationUser().getThumbnail();
            }


            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(context).load(thumbnail.getThumbXL()).transform(new CropCircleTransformation()).placeholder(R.drawable.icon_profile_large).fit().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(context).load(thumbnail.getThumb()).transform(new CropCircleTransformation()).placeholder(R.drawable.icon_profile_large).fit().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(context).load(thumbnail.getUrl()).transform(new CropCircleTransformation()).placeholder(R.drawable.icon_profile_large).fit().into(imageView);
            } else {
                if (!TextUtils.isEmpty(name)) {
                    String firstWord = name.substring(0, 1).toUpperCase();
                    TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                    imageView.setImageDrawable(textDrawable);
                }/* else {
                    Picasso.with(context).load(R.drawable.icon_profile_large).transform(new CropCircleTransformation()).fit().centerCrop().into(imageView);
                }*/
            }

        } else if (fromUser.getUserProfileLite() != null) {

            Context context = getBaseContext();
            String name = "";
            if (!TextUtils.isEmpty(fromUser.getName())) {
                name = fromUser.getName();
            }

            Thumbnail thumbnail = new Thumbnail();
            if (fromUser.getUserProfileLite().getThumbnail() != null) {
                thumbnail = fromUser.getUserProfileLite().getThumbnail();
            }


            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(context).load(thumbnail.getThumbXL()).transform(new CropCircleTransformation()).placeholder(R.drawable.icon_profile_large).fit().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(context).load(thumbnail.getThumb()).transform(new CropCircleTransformation()).placeholder(R.drawable.icon_profile_large).fit().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(context).load(thumbnail.getUrl()).transform(new CropCircleTransformation()).placeholder(R.drawable.icon_profile_large).fit().into(imageView);
            } else {
                if (!TextUtils.isEmpty(name)) {
                    String firstWord = name.substring(0, 1).toUpperCase();
                    TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                    imageView.setImageDrawable(textDrawable);
                }/* else {
                    Picasso.with(context).load(R.drawable.icon_profile_large).transform(new CropCircleTransformation()).fit().centerCrop().into(imageView);
                }*/
            }

        } else {

            // For older post


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
}
