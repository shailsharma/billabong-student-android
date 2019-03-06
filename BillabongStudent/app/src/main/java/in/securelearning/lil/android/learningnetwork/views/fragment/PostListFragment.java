package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutBadgeImageviewBinding;
import in.securelearning.lil.android.app.databinding.LayoutFragmentGroupPostListBinding;
import in.securelearning.lil.android.app.databinding.LayoutItemPostBinding;
import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.OGMetaDataResponse;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Result;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.widget.CustomImageButton;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.learningnetwork.events.EventLatestCommentAdded;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostCreatedEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostReceivedEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostResponseReceivedEvent;
import in.securelearning.lil.android.learningnetwork.events.PostRemovedFromFavorite;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.PostLikeActivity;
import in.securelearning.lil.android.learningnetwork.views.activity.PostResponseListActivity;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 02-Aug-17.
 */

public class PostListFragment extends Fragment {

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    PostDataLearningModel mPostDataLearningModel;

    @Inject
    GroupModel mGroupModel;

    @Inject
    BadgesModel mBadgesModel;

    @Inject
    RxBus mRxBus;

    @Inject
    OgUtils mOgUtils;

    @Inject
    Context mContext;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String GROUP_ID = "groupId";
    private static final String COLOR = "color";
    public static final String REQUEST_FAVORITE_LIST = "requestFavoriteList";
    private int mColumnCount = 1;
    private int mColor = 0, mAlphaColor = 0;
    private String mGroupId = "";
    private boolean mIsFavoriteListVisible = false;
    private LayoutFragmentGroupPostListBinding mBinding;
    private Disposable mSubscription;
    private int mSkip = 0;
    private int mLimit = 10;
    private String mPostType = "";
    private PostAdapter mPostAdapter;
    private Group mGroup;
    private boolean isTeacher = false;
    private ArrayList<LILBadges> mLILBadges = new ArrayList<>();
    private Dialog mBadgeDialog;
    private int mNewPostReceivedCount = 0;
    private RecyclerView.OnScrollListener mDataScrollListener;
    private int mNoResultMessageType = R.string.label_no_post;

    public static PostListFragment newInstance(int columnCount, String groupId, boolean isFavoriteListVisible) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(GROUP_ID, groupId);
        args.putBoolean(REQUEST_FAVORITE_LIST, isFavoriteListVisible);
        fragment.setArguments(args);
        return fragment;
    }

    public static PostListFragment newInstance(int columnCount, String groupId, boolean isFavoriteListVisible, int color) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(GROUP_ID, groupId);
        args.putBoolean(REQUEST_FAVORITE_LIST, isFavoriteListVisible);
        args.putInt(COLOR, color);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        isTeacher = PermissionPrefsCommon.getPostBadgeAssignPermission(mContext);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mGroupId = getArguments().getString(GROUP_ID);
            mIsFavoriteListVisible = getArguments().getBoolean(REQUEST_FAVORITE_LIST);
            mPostDataLearningModel.deleteAllNewPostByGroupId(mGroupId);
            mGroup = mGroupModel.getGroupFromUidSync(mGroupId);
            mColor = getArguments().getInt(COLOR, -1);


            if (mColor == -1) {
                // mRxBus.send(new RefreshGroupListUnreadCount(mGroup.getAlias()));
                PrefManager.SubjectExt subjectExt = null;
                if (mGroup.getSubject() != null && !TextUtils.isEmpty(mGroup.getSubject().getId())) {
                    HashMap<String, PrefManager.SubjectExt> mSubjectMap = PrefManager.getSubjectMap(getActivity());
                    subjectExt = mSubjectMap.get(mGroup.getSubject().getId());
                }
                if (subjectExt == null) {
                    subjectExt = PrefManager.getDefaultSubject();
                }
                mColor = subjectExt.getTextColor();
            }
            mAlphaColor = ColorUtils.setAlphaComponent(mColor, 10);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_fragment_group_post_list, container, false);
        initializeViews();
        setDefault();
        listenRxBusEvent();
        getLilBadges();
        getData(mGroupId, mSkip, mLimit, mIsFavoriteListVisible);
        return mBinding.getRoot();
    }

    private void listenRxBusEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) throws Exception {
                if (event instanceof PostRemovedFromFavorite) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    if (!mIsFavoriteListVisible) {
                                        String alias = ((PostRemovedFromFavorite) event).getAlias();
                                        mPostAdapter.updatePost(alias);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });

                } else if (event instanceof LoadNewPostCreatedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    setDefault();
                                    getData(mGroupId, mSkip, mLimit, mIsFavoriteListVisible);
                                    //        SoundUtils.playSound(getActivity(), SoundUtils.LEARNING_NETWORK_POST_ADDED);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });

                } else if (event instanceof EventLatestCommentAdded) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {

                                    String alias = ((EventLatestCommentAdded) event).getAlias();
                                    mPostAdapter.updatePost(alias);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });

                } else if (event instanceof LoadNewPostResponseReceivedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {

                                    PostResponse postResponse = ((LoadNewPostResponseReceivedEvent) event).getPostResponse();
                                    PostData postData = mPostDataLearningModel.getPostDataByObjectId(postResponse.getPostID());
                                    String alias = postData.getAlias();
                                    mPostAdapter.updatePost(alias);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });

                } else if (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(PostData.class)) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {

                                    String alias = ((ObjectDownloadComplete) event).getAlias();
                                    mPostAdapter.updatePostObjectId(alias, ((ObjectDownloadComplete) event).getId());
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });

                } else if (event instanceof LoadNewPostReceivedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    if (mGroupId.equals(((LoadNewPostReceivedEvent) event).getGroupId())) {
                                        mNewPostReceivedCount++;

                                        if (mNewPostReceivedCount == 1) {
                                            mBinding.textViewNewPostCount.setText(String.valueOf(mNewPostReceivedCount) + " NEW POST");

                                        } else {
                                            mBinding.textViewNewPostCount.setText(String.valueOf(mNewPostReceivedCount) + " NEW POSTS");
                                        }

                                        if (mBinding.cardViewFloatingCount.getVisibility() == View.GONE) {
                                            mBinding.cardViewFloatingCount.setVisibility(View.VISIBLE);
                                            AnimationUtils.zoomInFast(getActivity(), mBinding.cardViewFloatingCount);
                                        }
                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });
                }
            }
        });
    }

    private void getLilBadges() {

        mLILBadges = mPostDataLearningModel.getBadges();
    }

    private void initializeViews() {

        if (PermissionPrefsCommon.getPostCreateReferencePermission(getContext()) || PermissionPrefsCommon.getPostCreateDiscussionPermission(getContext())) {
            if (!mIsFavoriteListVisible) {
                mNoResultMessageType = R.string.label_no_post_create;
                mBinding.addPost.setVisibility(View.VISIBLE);
                mBinding.addPost.setBackgroundTintList(ColorStateList.valueOf(mColor));
                mBinding.addPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(CreatePostActivity.getIntentForCreatePost(getActivity(), mGroupId));
                    }
                });

                mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0) {
                            mBinding.addPost.hide();
                        } else {
                            mBinding.addPost.show();
                        }

                    }
                });
            } else {
                mNoResultMessageType = R.string.label_no_favorite_post;
                mBinding.addPost.setVisibility(View.GONE);
            }

            mBinding.textViewNoPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinding.addPost.performClick();
                }
            });

        } else {

            mNoResultMessageType = R.string.label_no_post;
            mBinding.addPost.setVisibility(View.GONE);
        }


        mBinding.cardViewFloatingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPostDataLearningModel.deleteAllNewPostByGroupId(mGroupId);
                mPostDataLearningModel.clearLearningNetworkGroupNotification(getActivity(), mGroupId.hashCode());
                AnimationUtils.zoomOutFast(getActivity(), mBinding.cardViewFloatingCount);
                mBinding.cardViewFloatingCount.setVisibility(View.GONE);
                setDefault();
                getData(mGroupId, mSkip, mLimit, mIsFavoriteListVisible);
            }
        });

    }

    private void setDefault() {
        mNewPostReceivedCount = 0;
        mPostType = "";
        mSkip = 0;
        initializeRecyclerView(new ArrayList<PostData>());
        mPostDataLearningModel.clearLearningNetworkGroupNotification(getActivity(), mGroupId.hashCode());

    }

    private void getData(String groupId, int skip, final int limit, boolean requestFavoriteList) {
        if (requestFavoriteList) {
            mPostDataLearningModel.getFavoritePostListForGroup(groupId, skip, limit).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PostData>>() {
                @Override
                public void accept(ArrayList<PostData> posts) throws Exception {
                    mSkip += posts.size();
                    noResultFound(mSkip, true);
                    if (posts.size() < limit) {
                        mBinding.recyclerView.removeOnScrollListener(mDataScrollListener);
                    }
                    mPostAdapter.addItem(posts);

                }
            });
        } else {
            mPostDataLearningModel.getPostListForGroup(groupId, skip, limit).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PostData>>() {
                @Override
                public void accept(ArrayList<PostData> posts) throws Exception {
                    mSkip += posts.size();
                    noResultFound(mSkip, false);
                    if (posts.size() < limit) {
                        mBinding.recyclerView.removeOnScrollListener(mDataScrollListener);
                    }
                    mPostAdapter.addItem(posts);

                }
            });
        }


    }

    private void initializeRecyclerView(ArrayList<PostData> posts) {
        LinearLayoutManager layoutManager = null;
        mBinding.recyclerView.getItemAnimator().setChangeDuration(0);
        ((DefaultItemAnimator) mBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        if (getActivity() != null) {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mBinding.recyclerView.setLayoutManager(layoutManager);
            mPostAdapter = new PostAdapter(posts, mGroupId);
            mBinding.recyclerView.setAdapter(mPostAdapter);
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mDataScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastVisibleItemPosition() == mSkip - 1) {

                            getData(mGroupId, mSkip, mLimit, mIsFavoriteListVisible);

                        }
                    }

                }

            };
            mBinding.recyclerView.addOnScrollListener(mDataScrollListener);
        }

    }

    private void noResultFound(int size, boolean isFavoriteList) {
        if (size > 0) {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.VISIBLE);
        } else {

            mBinding.textViewNoPost.setText(getString(mNoResultMessageType));
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
        }

    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
        private ArrayList<PostData> mPostData = new ArrayList<>();
        private String mSelectedGroupId = "";

        public PostAdapter(ArrayList<PostData> posts, String groupId) {
            this.mPostData = posts;
            this.mSelectedGroupId = groupId;
        }

        @Override
        public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemPostBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_post, parent, false);
            return new PostAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final PostAdapter.ViewHolder holder, int position) {
            final PostData postData = mPostData.get(position);

            setPostText(postData.getPostText(), holder.mBinding);
            setUserName(postData.getFrom().getName(), holder.mBinding.textViewUserName);
            setPostedTime(postData.getCreatedTime(), holder.mBinding.textViewPostTime);
            setUserThumbnail(postData.getFrom().getId(), holder.mBinding.imageViewUserImage);
            setPostType(postData.getPostType(), holder.mBinding.imageViewPostType);
            setPostResources(postData, holder.mBinding);
            setOgCard(postData, holder.mBinding);
            setBadgePermissions(holder.mBinding, postData, position);
            setAssignedBadgeToPost(postData.getObjectId(), holder.mBinding.imageButtonAssignBadge, holder.mBinding.imageViewAssignedBadge);
            setLikeViews(postData, holder.mBinding);
            setCommentViews(postData, holder.mBinding);
            setLatestCommentOnPost(postData, holder.mBinding);
            setFavoriteViews(postData, holder.mBinding, position);

        }

        private void setPostText(final String postText, LayoutItemPostBinding binding) {
            if (!TextUtils.isEmpty(postText)) {
                TextViewMore.setPostText(getContext(), postText, binding.textViewPostText, binding.includeTextViewMoreLess.textViewMoreLess);
                binding.textViewPostText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        TextViewMore.copyTextToClipboard(getContext(), postText);
                        return false;
                    }
                });
            } else {
                binding.textViewPostText.setVisibility(View.GONE);
                binding.includeTextViewMoreLess.textViewMoreLess.setVisibility(View.GONE);
            }

        }

        private void setUserName(String userName, AppCompatTextView textViewUserName) {
            textViewUserName.setText(userName);
        }

        private void setPostedTime(String time, AppCompatTextView textViewPostedTime) {
            textViewPostedTime.setText(DateUtils.getDateTimeStringWithDateMonthHourMinuteAMPM(DateUtils.convertrIsoDate(time)));

        }

        private void setOgCard(final PostData postData, final LayoutItemPostBinding binding) {
            if (!TextUtils.isEmpty(postData.getObjectId())) {
                if (postData.getLocalOgDataList().size() > 0) {
                    setOgViewForPost(binding, postData.getPositiveResult());
                } else {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        if (postData.getoGDataList().size() > 0) {
                            try {
                                mOgUtils.getOgDataFromServer(postData.getoGDataList()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OGMetaDataResponse>() {
                                    @Override
                                    public void accept(OGMetaDataResponse ogData) throws Exception {
                                        Result response = new Result();
                                        Result responseToSet = null;
                                        for (int i = 0; i < ogData.getResults().size(); i++) {
                                            response = ogData.getResults().get(i);
                                            postData.getLocalOgDataList().put(response.getUrl(), response); // Here we are making Map for url as key and image as value
                                            if (response.getOg().equals(true)) {
                                                if (responseToSet == null) {
                                                    responseToSet = response;
                                                    postData.setPositiveResult(responseToSet);
                                                }
                                            }
                                        }
                                        if (responseToSet != null) {
                                            final Result finalResponseToSet = responseToSet;
                                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                                @Override
                                                public void run() {

                                                    setOgViewForPost(binding, finalResponseToSet);
                                                }
                                            });
                                        } else if (response != null) {
                                            postData.setPositiveResult(response);
                                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                                @Override
                                                public void run() {
                                                    binding.includeOgLayout.layoutOgCard.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                        mPostDataLearningModel.savePostForOg(postData);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        throwable.printStackTrace();
                                    }
                                });
                            } catch (Exception t) {
                                t.printStackTrace();
                                Log.e("OgIconData", "err fetching getOgIconData" + t.toString());
                            }
                        }
                    }
                }
            }

        }

        private void setOgViewForPost(LayoutItemPostBinding binding, final Result finalResponseToSet) {
            if (finalResponseToSet.getOgMeta() != null) {
                binding.includeOgLayout.layoutOgCard.setVisibility(View.VISIBLE);

                if (finalResponseToSet.getOgMeta().getOgImage() != null && !TextUtils.isEmpty(finalResponseToSet.getOgMeta().getOgImage().getUrl())) {
                    Picasso.with(mContext).load(finalResponseToSet.getOgMeta().getOgImage().getUrl()).placeholder(R.drawable.icon_og_broken).resize(90, 90).centerCrop().into(binding.includeOgLayout.imageViewOg);
//                    String postText = binding.textViewPostText.getText().toString();
//
//                    String url = TextViewMore.removeHttp(finalResponseToSet.getUrl());
//
//                    if (postText.contains(url)) {
//                        String newPostText = postText.replace(url, "");
//                        setPostText(newPostText, binding);
//                    }
                } else {
                    Picasso.with(mContext).load(R.drawable.icon_og_broken).resize(90, 90).centerCrop().into(binding.includeOgLayout.imageViewOg);
                }
                binding.includeOgLayout.textViewOgTitle.setText(finalResponseToSet.getOgMeta().getOgTitle());
                binding.includeOgLayout.textViewOgUrl.setText(finalResponseToSet.getUrl());
                binding.includeOgLayout.textViewOgDescription.setText(finalResponseToSet.getOgMeta().getOgDescription());
                binding.includeOgLayout.layoutOgCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CustomChromeTabHelper.loadCustomDataUsingColorResource(getContext(), finalResponseToSet.getUrl(), R.color.colorLearningNetworkPrimary);
                    }
                });
            }

        }


        @Override
        public int getItemCount() {
            return mPostData.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        private void setPostResources(PostData postData, LayoutItemPostBinding binding) {
            final ArrayList<Resource> mResourceList = (ArrayList<Resource>) postData.getPostResources();
            if (mResourceList != null && !mResourceList.isEmpty() && !mResourceList.get(0).getDeviceURL().isEmpty()) {
                String mimeType = URLConnection.guessContentTypeFromName(mResourceList.get(0).getDeviceURL());
                if (mimeType != null && mimeType.contains("image")) {
                    binding.layoutPostedImage.setVisibility(View.VISIBLE);
                    setImageResource(mResourceList, binding);
                } else if (mimeType != null && mimeType.contains("video")) {
                    binding.layoutPostedVideo.setVisibility(View.VISIBLE);
                    setVideoResource(mResourceList, binding);
                } else {
                    binding.layoutPostedImage.setVisibility(View.GONE);
                    binding.layoutPostedVideo.setVisibility(View.GONE);

                }
            }
        }

        private void setVideoResource(final ArrayList<Resource> mResourceList, final LayoutItemPostBinding binding) {

            final String videoPath = mResourceList.get(0).getDeviceURL();
            Observable.create(new ObservableOnSubscribe<Bitmap>() {
                @Override
                public void subscribe(ObservableEmitter<Bitmap> subscriber) {
                    Bitmap bitmap = getScaledBitmapFromPath(getContext().getResources(), videoPath);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 600, 340, false);
                    subscriber.onNext(bitmap);
                    subscriber.onComplete();
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.computation())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) {

                            binding.imageViewPostedVideo.setImageBitmap(bitmap);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable t) {

                            t.printStackTrace();

                        }
                    });

            binding.imageViewPostedVideo.setTag(videoPath);
            if (mResourceList.size() > 1) {
                binding.imageViewMultipleVideos.setVisibility(View.VISIBLE);
            } else {
                binding.imageViewMultipleVideos.setVisibility(View.GONE);
            }
            binding.imageViewPostedVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Resource item = new Resource();
                    item.setType("video");
                    item.setUrlMain(binding.imageViewPostedVideo.getTag().toString());
                    getContext().startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
                }
            });
            binding.imageViewMultipleVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strHeaderText = binding.textViewUserName.getText().toString().trim() + "'s posts- " + binding.textViewPostTime.getText().toString().trim();
                    FullScreenImage.setUpImageGridView(getContext(), mResourceList, strHeaderText);
                }
            });

        }

        private void setImageResource(final ArrayList<Resource> mResourceList, final LayoutItemPostBinding binding) {
            String imagePath = mResourceList.get(0).getDeviceURL();
            File imageFile;
            imageFile = new File(FileUtils.getPathFromFilePath(imagePath));
            Bitmap bitmap = getScaledBitmapFromPath(getResources(), imageFile.getAbsolutePath());

            binding.imageViewPostedImage.setImageBitmap(bitmap);
//            Picasso.with(getContext()).load(imageFile).resize(800, 640).onlyScaleDown().centerInside().into(binding.imageViewPostedImage);
            binding.imageViewPostedImage.setTag(imagePath);
            if (mResourceList.size() > 1) {
                binding.imageViewMultipleImages.setVisibility(View.VISIBLE);
            } else {
                binding.imageViewMultipleImages.setVisibility(View.GONE);
            }

            binding.imageViewPostedImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImage.setUpFullImageView(getContext(), 0, false, true, mResourceList);
                }
            });

            binding.imageViewMultipleImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strHeaderText = binding.textViewUserName.getText().toString().trim() + "'s posts- " + binding.textViewPostTime.getText().toString().trim();
                    FullScreenImage.setUpImageGridView(getContext(), mResourceList, strHeaderText);
                }
            });
        }

        public Bitmap getScaledBitmapFromPath(Resources resources, String path) {

            final String filePrefix = "file:///";
            final String filePrefix2 = "http:";
            if (path.contains(filePrefix)) {
                path = path.trim().replace(filePrefix, "");
            }

            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_add_a_photo_black_48dp);
            Log.e("path--", path);
            String mimeType = URLConnection.guessContentTypeFromName(path);

            Log.e("mimeType", "" + mimeType);
            if (mimeType != null) {
                if (mimeType.contains("image")) {
                    if (new File(path).exists())
                        bitmap = BitmapFactory.decodeFile(path);

                } else if (mimeType.contains("video")) {
                    if (new File(path).exists())
                        bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
                }
            }
            if (bitmap != null) {
                if (bitmap.getHeight() > bitmap.getWidth()) {
                    return Bitmap.createScaledBitmap(bitmap, 340, 600, false);
                } else {
                    return Bitmap.createScaledBitmap(bitmap, 600, 340, false);
                }
            }
            return null;
        }

        private void setPostType(String postType, AppCompatImageView imageView) {
            if (!TextUtils.isEmpty(postType)) {
                if (postType.equals(PostDataType.TYPE_REFERENCE_POST.getPostDataType())) {
                    Picasso.with(getContext()).load(R.drawable.reference_g).into(imageView);
                } else if (postType.equals(PostDataType.TYPE_DISCUSSION.getPostDataType())) {
                    Picasso.with(getContext()).load(R.drawable.query_g).into(imageView);
                }
            }

        }

        private void setUserThumbnail(final String userId, AppCompatImageView imageView) {

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        getContext().startActivity(UserProfileActivity.getStartIntent(userId, getContext()));
                    } else {
                        ToastUtils.showToastAlert(getContext(), getContext().getString(R.string.connect_internet));
                    }

                }
            });

            ArrayList<GroupMember> groupMembers = mGroup.getMembers();
            for (GroupMember groupMember : groupMembers) {
                if (groupMember.getObjectId().equalsIgnoreCase(userId)) {

                    if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getLocalUrl())) {
                        Picasso.with(getContext()).load(groupMember.getPic().getLocalUrl()).transform(new CircleTransform()).resize(48, 48).centerCrop().into(imageView);
                    } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getUrl())) {
                        Picasso.with(getContext()).load(groupMember.getPic().getUrl()).transform(new CircleTransform()).resize(48, 48).centerCrop().into(imageView);
                    } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getThumb())) {
                        Picasso.with(getContext()).load(groupMember.getPic().getThumb()).transform(new CircleTransform()).resize(48, 48).centerCrop().into(imageView);
                    } else {
                        String firstWord = groupMember.getName().substring(0, 1).toUpperCase();
                        TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimaryLN);
                        imageView.setImageDrawable(textDrawable);
                    }

                    break;
                }
            }

            ArrayList<Moderator> moderators = mGroup.getModerators();
            for (Moderator moderator : moderators) {
                if (moderator.getId().equalsIgnoreCase(userId)) {

                    if (moderator.getPic() != null && !TextUtils.isEmpty(moderator.getPic().getLocalUrl())) {
                        Picasso.with(getContext()).load(moderator.getPic().getLocalUrl()).transform(new CircleTransform()).resize(48, 48).centerCrop().into(imageView);
                    } else if (moderator.getPic() != null && !TextUtils.isEmpty(moderator.getPic().getUrl())) {
                        Picasso.with(getContext()).load(moderator.getPic().getUrl()).transform(new CircleTransform()).resize(48, 48).centerCrop().into(imageView);
                    } else if (moderator.getPic() != null && !TextUtils.isEmpty(moderator.getPic().getThumb())) {
                        Picasso.with(getContext()).load(moderator.getPic().getThumb()).transform(new CircleTransform()).resize(48, 48).centerCrop().into(imageView);
                    } else {
                        String firstWord = moderator.getName().substring(0, 1).toUpperCase();
                        TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimaryLN);
                        imageView.setImageDrawable(textDrawable);
                    }

                    break;
                }
            }
        }

        private void setLikeViews(final PostData postData, final LayoutItemPostBinding binding) {

            mPostDataLearningModel.getPostResponseCountByPostIdAndResponseType(postData.getObjectId(), PostResponseType.TYPE_RECOMMEND.getPostResponseType())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(final Integer likeCounts) throws Exception {
                    setDefaultAndNewLikeCounts(likeCounts, binding);

                    mPostDataLearningModel.isPostLikedByUser(postData.getObjectId(), PostResponseType.TYPE_RECOMMEND.getPostResponseType())
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean isAlreadyLikedByUser) throws Exception {

                            if (isAlreadyLikedByUser) {
                                binding.imageViewLikeClick.setSelected(true);
                                binding.layoutLikeClick.setClickable(false);
                                binding.imageViewLikeClick.setImageResource(R.drawable.like_trans);
                                binding.imageViewLikeClick.setBackgroundColor(mColor);
                            } else {
                                binding.imageViewLikeClick.setSelected(false);
                                binding.layoutLikeClick.setClickable(true);
                                binding.imageViewLikeClick.setImageResource(R.drawable.like_g_w);
                                binding.layoutLikeClick.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!TextUtils.isEmpty(postData.getObjectId())) {
                                            mPostDataLearningModel.setPostResponseForLike(postData);
                                            binding.imageViewLikeClick.setSelected(true);
                                            binding.layoutLikeClick.setClickable(false);
                                            binding.imageViewLikeClick.setImageResource(R.drawable.like_trans);
                                            binding.imageViewLikeClick.setBackgroundColor(mColor);
                                            AnimationUtils.zoomInFast(getActivity(), binding.imageViewLikeClick);
                                            setDefaultAndNewLikeCounts(likeCounts + 1, binding);
                                            SoundUtils.playSound(getContext(), SoundUtils.LEARNING_NETWORK_POST_LIKE);

                                        } else {
                                            SnackBarUtils.showColoredSnackBar(getActivity(), view, getActivity().getString(R.string.label_sync_post), ContextCompat.getColor(getActivity(), R.color.colorRed));
                                        }


                                    }
                                });

                            }
                        }
                    });


                }
            });

            binding.layoutLikeCounts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(PostLikeActivity.getIntentForPostLikeList(getActivity(), mGroupId, postData.getObjectId()));
                }
            });


        }

        private void setDefaultAndNewLikeCounts(int likeCounts, LayoutItemPostBinding binding) {
            if (likeCounts == 1) {
                binding.layoutLikeCounts.setVisibility(View.VISIBLE);
                binding.imageViewLikeCount.setImageResource(R.drawable.like_round_trans);
                binding.imageViewLikeCount.setBackgroundColor(mColor);
                binding.textViewLikeCounts.setText(String.valueOf(likeCounts) + " " + getContext().getString(R.string.lable_like));
            } else if (likeCounts > 1) {
                binding.layoutLikeCounts.setVisibility(View.VISIBLE);
                binding.imageViewLikeCount.setImageResource(R.drawable.like_round_trans);
                binding.imageViewLikeCount.setBackgroundColor(mColor);
                binding.textViewLikeCounts.setText(String.valueOf(likeCounts) + " " + getContext().getString(R.string.lable_likes));
            } else {
                binding.layoutLikeCounts.setVisibility(View.GONE);
            }
        }

        private void setCommentViews(final PostData postData, final LayoutItemPostBinding binding) {

            mPostDataLearningModel.getPostResponseCountByPostIdAndResponseType(postData.getObjectId(), PostResponseType.TYPE_COMMENT.getPostResponseType())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(final Integer commentCounts) throws Exception {
                    if (commentCounts == 1) {
                        binding.layoutCommentCounts.setVisibility(View.VISIBLE);
                        binding.imageViewCommentCount.setImageResource(R.drawable.comment_round_trans);
                        binding.imageViewCommentCount.setBackgroundColor(mColor);
                        binding.textViewCommentCounts.setText(String.valueOf(commentCounts) + " " + getContext().getString(R.string.lable_comment));
                    } else if (commentCounts > 1) {
                        binding.layoutCommentCounts.setVisibility(View.VISIBLE);
                        binding.imageViewCommentCount.setImageResource(R.drawable.comment_round_trans);
                        binding.imageViewCommentCount.setBackgroundColor(mColor);
                        binding.textViewCommentCounts.setText(String.valueOf(commentCounts) + " " + getContext().getString(R.string.lable_comments));
                    } else {
                        binding.layoutCommentCounts.setVisibility(View.GONE);
                    }
                }
            });


            binding.layoutCommentClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(postData.getObjectId())) {
                        getContext().startActivity(PostResponseListActivity.getIntentForPostResponseList(getContext(), mGroupId, postData.getObjectId(), postData.getAlias()));
                        mPostDataLearningModel.deleteAllNewPostResponseByPostId(postData.getObjectId());
                        mPostDataLearningModel.clearLearningNetworkGroupNotification(getActivity(), mGroupId.hashCode());
                    } else {
                        SnackBarUtils.showColoredSnackBar(getActivity(), view, getActivity().getString(R.string.label_sync_post), ContextCompat.getColor(getActivity(), R.color.colorRed));
                    }
                }
            });

            binding.layoutCommentCounts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.layoutCommentClick.performClick();
                }
            });
        }

        private void setLatestCommentOnPost(PostData postData, final LayoutItemPostBinding binding) {

            mPostDataLearningModel.getLatestCommentOnPost(postData.getObjectId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<PostResponse>() {
                @Override
                public void accept(final PostResponse postResponse) throws Exception {
                    if (postResponse != null && !TextUtils.isEmpty(postResponse.getAlias())) {
                        binding.layoutLatestComment.setBackgroundColor(mAlphaColor);
                        binding.layoutLatestComment.setVisibility(View.VISIBLE);
                        binding.viewCommentSeparator.setVisibility(View.VISIBLE);
                        setUserThumbnail(postResponse.getFrom().getId(), binding.imageViewLatestCommentUserImage);
                        binding.textViewLatestCommentUserName.setText(postResponse.getFrom().getName());
                        String postResponseText = String.valueOf(Html.fromHtml(postResponse.getText()));
                        postResponseText = postResponseText.replaceAll("\n", " ");
                        binding.textViewLatestComment.setText(postResponseText);
                    }
                }
            });


            binding.layoutLatestComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.layoutCommentClick.performClick();
                }
            });

        }

        private void setFavoriteViews(final PostData postData, final LayoutItemPostBinding binding, final int position) {

            mPostDataLearningModel.isPostFavoriteByUser(postData.getObjectId(), postData.getAlias())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean isAlreadyFavoriteByUser) throws Exception {
                    if (isAlreadyFavoriteByUser) {
                        binding.imageViewFavoriteClick.setSelected(true);
                        binding.imageViewFavoriteClick.setImageResource(R.drawable.favorite_trans);
                        binding.imageViewFavoriteClick.setBackgroundColor(mColor);
                    } else {
                        binding.imageViewFavoriteClick.setSelected(false);
                        binding.imageViewFavoriteClick.setImageResource(R.drawable.favorite_g);
                        binding.imageViewFavoriteClick.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTransparent));
                    }

                }
            });

            binding.layoutFavoriteClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (binding.imageViewFavoriteClick.isSelected()) {
                        //un favorite
                        if (mIsFavoriteListVisible) {
                            mPostData.remove(postData);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mPostData.size());
                        }

                        binding.imageViewFavoriteClick.setSelected(false);
                        binding.imageViewFavoriteClick.setImageResource(R.drawable.favorite_g);
                        binding.imageViewFavoriteClick.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorTransparent));
                        AnimationUtils.zoomInFast(getActivity(), binding.imageViewFavoriteClick);
                        mPostDataLearningModel.removePostFromFavorite(postData);

                    } else {
                        //favorite
                        mPostDataLearningModel.addPostToFavorite(postData);
                        binding.imageViewFavoriteClick.setSelected(true);
                        binding.imageViewFavoriteClick.setImageResource(R.drawable.favorite_trans);
                        binding.imageViewFavoriteClick.setBackgroundColor(mColor);
                        AnimationUtils.zoomInFast(getActivity(), binding.imageViewFavoriteClick);
                        SoundUtils.playSound(getContext(), SoundUtils.LEARNING_NETWORK_POST_FAVORITE);
                    }
                }
            });


        }

        private void setBadgePermissions(final LayoutItemPostBinding binding, final PostData postData, final int position) {

            if (!BuildConfig.FLAVOR.equals("henkel")) {
//                if (PermissionPrefsCommon.getPostBadgeAssignPermission(mContext) && postData.getFrom().getRole().equals(AppUser.USERTYPE.STUDENT.toString())) {
//                    binding.imageButtonAssignBadge.setVisibility(View.VISIBLE);
//
//                }
                if (postData.isBadgeAssigningEnabled() &&
                        mGroup.getModerators() != null &&
                        !mGroup.getModerators().isEmpty() &&
                        mGroup.getModerators().contains(new Moderator(mAppUserModel.getObjectId(), ""))) {
                    binding.imageButtonAssignBadge.setVisibility(View.VISIBLE);
                } else {
                    binding.imageButtonAssignBadge.setVisibility(View.GONE);
                }


                binding.imageButtonAssignBadge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBadgePopUp(postData, position);
                    }
                });
            }


        }

        private void setAssignedBadgeToPost(String postId, final CustomImageButton imageButtonAssignBadge, final ImageView imageView) {

            if (!BuildConfig.FLAVOR.equals("henkel")) {
                mPostDataLearningModel.getLatestBadgeOnPost(postId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<PostResponse>() {
                    @Override
                    public void accept(final PostResponse postResponse) throws Exception {

                        if (postResponse != null) {
                            LILBadges lilBadges = mBadgesModel.fetchLilBadgeFromUidSync(postResponse.getAssignedBadgeId());
                            if (lilBadges.getThumbnail() != null) {
                                imageButtonAssignBadge.setVisibility(View.GONE);
                                imageView.setVisibility(View.VISIBLE);
                                imageView.setImageResource(getResourcesIdentifier(lilBadges.getThumbnail()));
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        SnackBarUtils.showColoredSnackBar(getActivity(), view, "Badge assigned by " + postResponse.getFrom().getName(), ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                                    }
                                });

                            }
                        }

                    }
                });
            }

        }

        /**
         * get resource identifier for badge icons
         *
         * @param name
         * @return
         */
        private int getResourcesIdentifier(String name) {
            return getActivity().getResources().getIdentifier(name, "drawable", getActivity().getPackageName());
        }

        private void showBadgePopUp(PostData postData, int position) {
            mBadgeDialog = new Dialog(getContext());
            mBadgeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mBadgeDialog.setContentView(R.layout.layout_teacher_popup_badges);
            mBadgeDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            mBadgeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77000000")));
            TextView userNameTextView = (TextView) mBadgeDialog.findViewById(R.id.textview_assign_badge_user);
            RecyclerView badgeGridView = (RecyclerView) mBadgeDialog.findViewById(R.id.recycler_grid_badge);
            userNameTextView.setText("Assign badge to" + " " + postData.getFrom().getName());

            badgeGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            BadgeAdapter badgeAdapter = new BadgeAdapter(postData, mLILBadges, position);
            badgeGridView.setAdapter(badgeAdapter);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            Double width = metrics.widthPixels * 0.80;
            Window win = mBadgeDialog.getWindow();
            win.setLayout(width.intValue(), ViewGroup.LayoutParams.WRAP_CONTENT);
            mBadgeDialog.show();
        }

        public void addItem(ArrayList<PostData> posts) {
            if (mPostData != null) {
                mPostData.addAll(posts);
                notifyDataSetChanged();
            }

        }

        public void updatePost(String alias) {
            for (int i = 0; i < mPostData.size(); i++) {
                if (mPostData.get(i).getAlias().equals(alias)) {
                    notifyItemChanged(i);
                }
            }
        }

        public void updatePostObjectId(String alias, String objectId) {
            if (!TextUtils.isEmpty(alias) && !TextUtils.isEmpty(objectId)) {
                for (int i = 0; i < mPostData.size(); i++) {
                    if (mPostData.get(i).getAlias().equals(alias)) {
                        mPostData.get(i).setObjectId(objectId);
                        notifyItemChanged(i);
                    }
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutItemPostBinding mBinding;

            public ViewHolder(LayoutItemPostBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {
        private final ArrayList<LILBadges> mLILBadges;
        private final PostData mPostData;
        private final int mPosition;

        public BadgeAdapter(PostData postData, ArrayList<LILBadges> lilBadges, int position) {
            this.mPostData = postData;
            this.mLILBadges = lilBadges;
            this.mPosition = position;

        }

        @Override
        public BadgeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutBadgeImageviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_badge_imageview, parent, false);
            return new BadgeAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(BadgeAdapter.ViewHolder holder, int position) {
            final LILBadges badge = mLILBadges.get(position);
            Picasso.with(getContext()).load(getRawId(badge.getThumbnail())).into(holder.mBinding.imageViewBadge);
            holder.mBinding.imageViewBadge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPostDataLearningModel.setPostResponseForBadge(mPostData, badge);
                    mPostAdapter.notifyItemChanged(mPosition);
                    mBadgeDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mLILBadges.size();
        }

        private int getRawId(String mName) {

            int resId = 0;
            try {
                Field field = R.drawable.class.getField(mName);
                resId = field.getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resId;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutBadgeImageviewBinding mBinding;

            public ViewHolder(LayoutBadgeImageviewBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
