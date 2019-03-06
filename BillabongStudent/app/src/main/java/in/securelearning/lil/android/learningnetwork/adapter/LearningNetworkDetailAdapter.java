package in.securelearning.lil.android.learningnetwork.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.base.comparators.SortPostResponseByDate;
import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.customchrometabutils.LinkTransformationMethod;
import in.securelearning.lil.android.base.dataobjects.AppUser;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.OGMetaDataResponse;
import in.securelearning.lil.android.base.dataobjects.PostByUser;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Result;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ImageUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostResponseReceivedEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostAddToFavoriteEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostLikedEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostLikedFromFavEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostRemoveEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostRemoveToFavoriteEvent;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.activity.PostLikeActivity;
import in.securelearning.lil.android.learningnetwork.views.fragment.BaseLNFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.FavoritePostFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostFragment;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Adapter for binds Post Data and Post Response data
 * Created by Pushkar Raj on 8/27/2016.
 */
public class LearningNetworkDetailAdapter extends RecyclerView.Adapter<LearningNetworkDetailAdapter.ViewHolder> {
    LearningNetworkDetailAdapter.CommentAdapter commentAdapter;
    RecyclerView mCommentRecyclerView;
    private final Group mCurrentSelectedGroup;
    private final BaseLNFragment mBaseLNFragment;
    private final Context mContext;
    private long lastAddPostBtnClickTime;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    PostDataLearningModel mLearningModel;
    @Inject
    PostDataModel mPostDataModel;
    @Inject
    BadgesModel mBadgesModel;
    @Inject
    RxBus mRxBus;
    @Inject
    OgUtils mOgUtils;
    private ArrayList<PostDataDetail> mPostDatas = new ArrayList<>();

    private final List<String> mIds;
    private Disposable mDisposable;
    private CardView mFloatingCountView;
    private TextView mNewPostResponseCountTextView;
    private int mNewPostCount = 0;
    private LinearLayoutManager commentLayoutManager;

    public LearningNetworkDetailAdapter(Context context, BaseLNFragment baseLNFragment, ArrayList<PostDataDetail> postDatas, Group mCurrentSelectedGroup) {
        super();
        mContext = context;
        mBaseLNFragment = baseLNFragment;
        this.mPostDatas = postDatas;
        mIds = getIdList(postDatas);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        this.mCurrentSelectedGroup = mCurrentSelectedGroup;
        setUpSubscription();

    }

    public void dispose() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void setUpSubscription() {
        final ArrayList<PostDataDetail> postDataDetails = mPostDatas;
        mDisposable = mRxBus.toFlowable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object o) throws Exception {
                if (o instanceof LoadNewPostResponseReceivedEvent) {
                    if (postDataDetails != null) {
                        final int index = postDataDetails.indexOf(new PostDataDetail(((LoadNewPostResponseReceivedEvent) o).getPostResponse().getPostID()));
                        if (index >= 0) {


                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() throws Exception {
                                    if (!postDataDetails.get(index).getPostResponses().contains(((LoadNewPostResponseReceivedEvent) o).getPostResponse())) {
                                        postDataDetails.get(index).getPostResponses().add(((LoadNewPostResponseReceivedEvent) o).getPostResponse());
                                        notifyItemChanged(index);
                                    }

                                    if (mCommentRecyclerView != null && commentAdapter != null) {
                                        if (commentAdapter.getPostId().equals(((LoadNewPostResponseReceivedEvent) o).getPostResponse().getPostID())) {

                                            if (commentAdapter.addItem(((LoadNewPostResponseReceivedEvent) o).getPostResponse())) {
                                                mNewPostCount++;
                                                AnimationUtils.zoomInFast(mContext, mFloatingCountView);
                                                mFloatingCountView.setVisibility(View.VISIBLE);
                                                SoundUtils.playSound(mContext, SoundUtils.LEARNING_NETWORK_NEW_POST_RESPONSE);
                                                mNewPostResponseCountTextView.setText(String.valueOf(mNewPostCount));
                                            }

                                        }

                                        mFloatingCountView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (commentAdapter.mPostResponses.size() > 0) {
                                                    mCommentRecyclerView.getAdapter().notifyDataSetChanged();
                                                    mFloatingCountView.setVisibility(View.GONE);
                                                    mNewPostCount = 0;
                                                    mCommentRecyclerView.smoothScrollToPosition(commentAdapter.mPostResponses.size() - 1);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_learning_network_detail_item_view, parent, false);
        view.setClickable(false);
        view.setFocusable(false);
        view.setFocusableInTouchMode(false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PostDataDetail postDataDetail = mPostDatas.get(position);
        final PostData postData = postDataDetail.getPostData();
        final ArrayList<Resource> mAttachmentPathList = (ArrayList<Resource>) postData.getPostResources();
        if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_STREAM.getPostDataType())) {
            holder.layout_og_card_for_mobile.setVisibility(View.GONE);
            holder.mPostedResLayout.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(R.drawable.video_c).resize(800, 640).centerInside().into(holder.mPostedCoverImageView);
            holder.mFileTypeImageView.setImageResource(R.drawable.icon_image_white);
            holder.mPostedCoverImageView.setTag("");
            /**
             * Handle click event of cover photo of the post
             * */
            holder.mPostedCoverImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    mContext.startActivity(new Intent(mContext, WowzaWebPlayerActivity.class));
                }
            });
        } else if (mAttachmentPathList != null && !mAttachmentPathList.isEmpty() && !mAttachmentPathList.get(0).getDeviceURL().isEmpty()) {
            holder.layout_og_card_for_mobile.setVisibility(View.GONE);
            String strMimeType = URLConnection.guessContentTypeFromName(mAttachmentPathList.get(0).getDeviceURL());
            if (strMimeType != null && strMimeType.contains("image")) {
                if (ImageUtils.isExternalStorageReadable() && mAttachmentPathList != null && !mAttachmentPathList.isEmpty() && !mAttachmentPathList.get(0).getDeviceURL().isEmpty()) {
                    holder.mPostedResLayout.setVisibility(View.VISIBLE);
                    String strImagePath = mAttachmentPathList.get(0).getDeviceURL();
                    Picasso.with(mContext).load(strImagePath).resize(800, 640).centerInside().into(holder.mPostedCoverImageView);
                    holder.mFileTypeImageView.setImageResource(R.drawable.icon_image_white);
                    holder.mPostedCoverImageView.setTag(strImagePath);

                } else {
                    holder.mPostedResLayout.setVisibility(View.GONE);
                }

            } else if (strMimeType != null && strMimeType.contains("video")) {
                holder.mPostedResLayout.setVisibility(View.VISIBLE);
                final String strVideoPath = mAttachmentPathList.get(0).getDeviceURL();
                Bitmap mBitmap = getScaledBitmapFromPath(mContext.getResources(), strVideoPath);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, 800, 640, false);
                holder.mPostedCoverImageView.setImageBitmap(mBitmap);
                holder.mFileTypeImageView.setImageResource(R.drawable.icon_video_white);
                holder.mPostedCoverImageView.setTag(strVideoPath);
            }
            /**
             * Handle click event of cover photo of the post
             * */
            holder.mPostedCoverImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String strPath = view.getTag().toString();
                    String strMimeType = URLConnection.guessContentTypeFromName(strPath);
                    if (strMimeType.contains("image")) {
                        FullScreenImage.setUpFullImageView(mContext, 0, false, true,mAttachmentPathList);
                    } else if (strMimeType.contains("video")) {
                        Resource item = new Resource();
                        item.setType("video");
                        item.setUrlMain(strPath);
                        mContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mContext, PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
                    }
                }
            });
        } else {
            holder.mPostedResLayout.setVisibility(View.GONE);
            //Here we manipulating for OG Data Image

            //First we are checking it for locally, if it is exist then we have not go for network call.
            if (postData.getLocalOgDataList().size() > 0) {
                String urlImage = "";
                String siteName = "";
                try {
                    urlImage = postData.getPositiveResult().getOgMeta().getOgImage().getUrl();
                    siteName = postData.getPositiveResult().getOgMeta().getOgTitle();
                } catch (Exception e) {
                    Log.e("OgIconData", "not get url image and site");
                }
                if (!urlImage.isEmpty()) {
                    //Matches means data is exist on locally we are not require to call service in this case.
                    Picasso.with(mContext).load(urlImage).into(holder.img_og_card);
                } else {
                    holder.img_og_card.setImageResource(R.drawable.icon_og_broken);
                }
                holder.layout_og_card_for_mobile.setVisibility(View.VISIBLE);
                holder.textview_og_card.setText(postData.getPositiveResult().getUrl());
                holder.textview_og_card_desc.setText(siteName);
                final String finalUrlData = postData.getPositiveResult().getUrl();
                holder.img_og_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!finalUrlData.isEmpty()) {
                            CustomChromeTabHelper.loadCustomDataUsingColorResource(mContext, finalUrlData, R.color.colorLearningNetworkPrimary);
                        }
                    }
                });
            } else {
                holder.layout_og_card_for_mobile.setVisibility(View.GONE);
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    //Here we are setting http url in ogDataList
                    if (postData.getoGDataList().size() <= 0) {
                        postData.setoGDataList(checkCommentForOGIcon(postData.getPostText()));
                    }
                    if (postData.getoGDataList().size() > 0) {
                        try {
                            mOgUtils.getOgDataFromServer(postData.getoGDataList()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OGMetaDataResponse>() {
                                @Override
                                public void accept(OGMetaDataResponse ogData) throws Exception {
                                    Result response1 = new Result();
                                    Result responseToSet = null;
                                    for (int i = 0; i < ogData.getResults().size(); i++) {
                                        response1 = ogData.getResults().get(i);
                                        postData.getLocalOgDataList().put(response1.getUrl(), response1); // Here we are making Map for url as key and image as value
                                        if (response1.getOg().equals(true)) {
                                            if (responseToSet == null) {
                                                responseToSet = response1;
                                                postData.setPositiveResult(responseToSet);
                                            }
                                        }
                                    }
                                    if (responseToSet != null) {
                                        final Result finalResponseToSet = responseToSet;
                                        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                            @Override
                                            public void run() {
                                                Picasso.with(mContext).load(finalResponseToSet.getOgMeta().getOgImage().getUrl()).placeholder(R.drawable.icon_og_broken).into(holder.img_og_card);
                                                holder.layout_og_card_for_mobile.setVisibility(View.VISIBLE);
                                                holder.textview_og_card.setText(finalResponseToSet.getUrl());
                                                holder.textview_og_card_desc.setText(finalResponseToSet.getOgMeta().getOgTitle());
                                                holder.img_og_card.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CustomChromeTabHelper.loadCustomDataUsingColorResource(mContext, finalResponseToSet.getUrl(), R.color.colorLearningNetworkPrimary);
                                                    }
                                                });
                                            }
                                        });
                                    } else if (response1 != null) {
                                        postData.setPositiveResult(response1);
                                        final Result finalResponse = response1;
                                        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                            @Override
                                            public void run() {
                                                holder.img_og_card.setImageResource(R.drawable.icon_og_broken);
                                                holder.layout_og_card_for_mobile.setVisibility(View.VISIBLE);
                                                holder.textview_og_card.setText(finalResponse.getUrl());
                                                holder.textview_og_card_desc.setText("");
                                            }
                                        });
                                    }
                                    mPostDataModel.savePostData(postData);
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

        setUpPostResponseLayout(postDataDetail, position, holder);

        setUserThumbnailToView(holder.mUserImageThumbnail, postData.getFrom().getId());

        setUserThumbnailToView(holder.mCurrentUserIcon, mAppUserModel.getObjectId());

        checkIfUserLikedOrFavoritePostBefore(postDataDetail, holder);

        setMultipleResourceButtonVisibility(mAttachmentPathList, holder.mMultipleResourceButton);

        setPostTypeIcon(postData, holder.imagePostDataType);

        setBadgeToPost(holder, postDataDetail);

        /******************************************************************************************************
         Apply click listener to open stream
         ******************************************************************************************************/

        if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_STREAM.getPostDataType())) {
            final View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    mContext.startActivity(new Intent(mContext, WowzaWebPlayerActivity.class));
                }
            };
            holder.imagePostDataType.setOnClickListener(listener);
            holder.img_og_card.setOnClickListener(listener);
        }
        holder.mPostTxt.setTransformationMethod(new LinkTransformationMethod(mContext, Linkify.WEB_URLS |
                Linkify.EMAIL_ADDRESSES |
                Linkify.PHONE_NUMBERS, R.color.colorLearningNetworkPrimary));
        holder.mPostTxt.setMovementMethod(LinkMovementMethod.getInstance());
        String text = postData.getPostText();
//        if (postData.getLocalOgDataList().size() > 0) {
//            for (Result result :
//                    postData.getLocalOgDataList().values()) {
//                if (result.getOg()) {
//                    String url = result.getUrl();
//                    String url2 = "<a href='" + url + "'>" + result.getOgMeta().getOgTitle() + "</a>";
//                    text = text.replace(url, url2);
//                }
//            }
//        }
        holder.mPostTxt.setText(TextViewMore.viewMore(text, holder.mPostTxt, holder.mViewMoreLessTextView));

        holder.mPostDateTimeTxt.setText(DateUtils.getDateTimeStringWithDateMonthHourMinuteAMPM(DateUtils.convertrIsoDate(postData.getCreatedTime())));
        holder.mPostByUserTxt.setText(postData.getFrom().getName());

        if (!PermissionPrefsCommon.getPostBadgeAssignPermission(mContext) ||
                checkIfPostHaveBadgeAssigned(postDataDetail) ||
                postData.getFrom().getRole().equals(AppUser.USERTYPE.TEACHER.toString()) ||
                postData.getFrom().getRole().equals("") ||
                mAppUserModel.getObjectId().equalsIgnoreCase(postData.getFrom().getId())) {
            holder.mOptionMenuButton.setVisibility(View.GONE);
        } else {
            holder.mOptionMenuButton.setVisibility(View.VISIBLE);
        }

        holder.mOptionMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionPopupMenu(mContext, view, postDataDetail, position, holder.mPostByUserTxt, postData);

            }
        });

        holder.mMultipleResourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strHeaderText = holder.mPostByUserTxt.getText().toString().trim() + "'s posts- " + holder.mPostDateTimeTxt.getText().toString().trim();
                mBaseLNFragment.setUpImageGridView(mAttachmentPathList, strHeaderText);

            }
        });


        holder.mWriteCommentEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (holder.mCommentPostImgBtn.getVisibility() == View.GONE && s.toString().trim().length() > 0)
                    holder.mCommentPostImgBtn.setVisibility(View.VISIBLE);
                else if (holder.mCommentPostImgBtn.getVisibility() == View.VISIBLE && s.toString().trim().length() == 0)
                    holder.mCommentPostImgBtn.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        holder.mWriteCommentEdtTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postDataDetail.getPostData().getObjectId() != null && !postDataDetail.getPostData().getObjectId().isEmpty()) {
                    showCommentsPopUp(postDataDetail, position, holder, 1);
                } else {
                    ToastUtils.showToastAlert(mContext, "You can't comment right now!\nPlease sync");
                }
            }
        });

        /**
         * Handle click event of post comment button, It will send a comment type post response for a particular group

         * */
        holder.mCommentPostImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(mContext,"Hell mCommentPostImgBtn click",Toast.LENGTH_LONG).show();
                PostResponse postResponse = getPostResponse(postData, holder, PostResponseType.TYPE_COMMENT);

                if (holder.mWriteCommentEdtTxt.getText().toString().isEmpty()) {
                    holder.mWriteCommentEdtTxt.setError(mContext.getString(R.string.please_write_your_post));
                } else {
                    mPostDatas.get(position).getPostResponses().add(postResponse);
                    holder.mWriteCommentEdtTxt.setText("");
                    mLearningModel.savePostResponse(postResponse);
                    SyncServiceHelper.startUploadPostResponse(mContext, postResponse.getAlias());
                    notifyItemChanged(holder.getAdapterPosition());
                }


            }
        });

        /**
         * Handle click event of recommend button
         * */
        holder.mLikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - lastAddPostBtnClickTime < 1000) {
                    return;
                }
                lastAddPostBtnClickTime = SystemClock.elapsedRealtime();

                if (checkPostDataObjectIdIsExists(postDataDetail, "You can't like right now!\nPlease sync")) {

                    AnimationUtils.zoomIn(mContext, holder.mLikeButton);
                    SoundUtils.playSound(mContext, SoundUtils.LEARNING_NETWORK_POST_LIKE);
                    PostResponse postResponse = getPostResponse(postData, holder, PostResponseType.TYPE_RECOMMEND);
                    mLearningModel.savePostResponse(postResponse);
                    //SyncServiceHelper.startUploadPostResponse(mContext, postResponse.getAlias());
                    mPostDatas.get(position).getPostResponses().add(postResponse);
                    if (mBaseLNFragment instanceof FavoritePostFragment) {
                        mRxBus.send(new LoadPostLikedFromFavEvent(postDataDetail));
                    } else {
                        mRxBus.send(new LoadPostLikedEvent(postDataDetail));
                    }
                    notifyItemChanged(position);
                }

            }
        });
        /**
         * Handle click event of add to favorite button
         */
        holder.mFavoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> mPostFavoriteUserIds = postData.getPostFavoriteUserIds();

                if (!mPostFavoriteUserIds.isEmpty() && mPostFavoriteUserIds.contains(mAppUserModel.getObjectId())) {
                    mPostFavoriteUserIds.remove(mAppUserModel.getObjectId());
                    postData.setPostFavoriteUserIds(mPostFavoriteUserIds);
                    mPostDataModel.savePostData(postData);

                    //Remove post when we make it to un favorite
                    if (mBaseLNFragment instanceof FavoritePostFragment) {
                        AnimationUtils.zoomOut(mContext, holder.mFavoriteButton);
                        ToastUtils.showToastSuccess(mContext, "Post removed from favorite");
                        if (mPostDatas.get(position) != null) {
                            PostDataDetail dataDetail = mPostDatas.get(position);
                            dataDetail.setPostData(postData);
                            mRxBus.send(new LoadPostRemoveToFavoriteEvent(dataDetail));
                        }

                        mPostDatas.remove(position);
                        mIds.remove(position);
                        if (mPostDatas.size() == 0)
                            ((FavoritePostFragment) mBaseLNFragment).mNoFavoriteLayout.setVisibility(View.VISIBLE);

                    } else {
                        if (mPostDatas.get(position) != null)
                            mRxBus.send(new LoadPostAddToFavoriteEvent(mPostDatas.get(position), false));

                    }


                } else {
                    if (postData != null && postData.getObjectId() != null && !postData.getObjectId().isEmpty()) {

                        List<String> favoriteUserIds = postData.getPostFavoriteUserIds();

                        favoriteUserIds.add(mAppUserModel.getObjectId());
                        postData.setPostFavoriteUserIds((ArrayList) favoriteUserIds);
                        mPostDataModel.savePostData(postData);

                        ToastUtils.showToastSuccess(mContext, "Post added to favorite");
                        AnimationUtils.zoomIn(mContext, holder.mFavoriteButton);
                        SoundUtils.playSound(mContext, SoundUtils.LEARNING_NETWORK_POST_FAVORITE);
                        if (mPostDatas.get(position) != null)
                            mRxBus.send(new LoadPostAddToFavoriteEvent(mPostDatas.get(position), true));

                    } else {
                        ToastUtils.showToastAlert(mContext, "You can't make favorite right now!\nPlease sync");
                    }
                }

                notifyDataSetChanged();

            }
        });


        holder.mCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - lastAddPostBtnClickTime < 1000) {
                    return;
                }
                lastAddPostBtnClickTime = SystemClock.elapsedRealtime();
                if (checkPostDataObjectIdIsExists(postDataDetail, "You can't comment right now!\nPlease sync"))
                    showCommentsPopUp(postDataDetail, position, holder, 0);


            }
        });

        holder.mTotalLikeCountTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((TextView) view).getText().toString().equalsIgnoreCase("0")) {
                    Intent mIntent = new Intent(mContext, PostLikeActivity.class);
//                    mIntent.putExtra(PostLikeActivity.SELECTED_GROUP, mCurrentSelectedGroup);
//                    mIntent.putExtra(PostLikeActivity.SELECTED_POST, postDataDetail);
                    mContext.startActivity(mIntent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPostDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        ((ViewHolder) holder).clearAnimation();
    }

    public void itemRefreshed(PostDataDetail postDataDetail) {
        if (postDataDetail != null) {
            if (mIds.contains(postDataDetail.getPostData().getAlias())) {
                for (int i = 0; i < mPostDatas.size(); i++) {
                    if (mPostDatas.get(i).getPostData().getAlias().equals(postDataDetail.getPostData().getAlias())) {
                        notifyItemChanged(i);
                    }
                }
            }
        }

    }

    public boolean addItem(PostDataDetail postDataDetail, boolean isPutOnTop) {
        if (postDataDetail != null && !mIds.contains(postDataDetail.getPostData().getAlias())) {
            if (isPutOnTop) {
                mPostDatas.add(0, postDataDetail);
                mIds.add(0, postDataDetail.getPostData().getAlias());
                notifyDataSetChanged();

            } else {
                mPostDatas.add(postDataDetail);
                mIds.add(postDataDetail.getPostData().getAlias());
                if (mPostDatas.size() > 0)
                    notifyItemInserted(mPostDatas.size() - 1);
            }
            return true;

        } else {
            for (PostDataDetail postDataDetail1 :
                    mPostDatas) {
                if (postDataDetail1.getPostData().getAlias().equals(postDataDetail.getPostData().getAlias())) {
                    postDataDetail1.getPostData().setObjectId(postDataDetail.getPostData().getObjectId());
                    postDataDetail1.setObjectId(postDataDetail.getPostData().getObjectId());
                }
            }
            return false;
        }
    }

    public void addItemSilent(PostDataDetail postDataDetail, boolean isPutOnTop) {
        if (postDataDetail != null && !mIds.contains(postDataDetail.getPostData().getAlias())) {
            if (isPutOnTop) {
                mPostDatas.add(0, postDataDetail);
                mIds.add(0, postDataDetail.getPostData().getAlias());

            } else {
                mPostDatas.add(postDataDetail);
                mIds.add(postDataDetail.getPostData().getAlias());
            }

        }
    }

    public void removeItem(String id) {
        if (mIds.contains(id)) {
            for (int i = 0; i < mPostDatas.size(); i++) {
                if (mPostDatas.get(i).getPostData().getAlias().equals(id)) {
                    mPostDatas.remove(i);
                    notifyItemRemoved(i);
                }
            }
            mIds.remove(id);

        }
    }

    private List<String> getIdList(List<PostDataDetail> values) {
        List<String> ids = new ArrayList<>();
        for (PostDataDetail dataDetail :
                values) {
            ids.add(dataDetail.getPostData().getAlias());
        }
        return ids;
    }

    public void addItems(ArrayList<PostDataDetail> postDatas) {
        this.mPostDatas.addAll(postDatas);
        notifyDataSetChanged();
    }

    private boolean checkPostDataObjectIdIsExists(PostDataDetail postDataDetail, String message) {
        if (postDataDetail.getPostData().getObjectId() == null || postDataDetail.getPostData().getObjectId().isEmpty()) {
            PostData postData1 = mPostDataModel.fetchPostDataFromAlias(postDataDetail.getPostData().getAlias());

            if (postData1 == null || postData1.getObjectId() == null || postData1.getObjectId().isEmpty()) {
                ToastUtils.showToastAlert(mContext, message);
                return false;
            }
            postDataDetail.setPostData(postData1);

        }
        return true;
    }

    /**
     * fetch post type and set post type image icon
     */
    private void setPostTypeIcon(PostData postData, ImageButton imagePostDataType) {
        if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_DISCUSSION.getPostDataType()))
            imagePostDataType.setImageResource(R.drawable.query_red);
        else if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_EVENT.getPostDataType()))
            imagePostDataType.setImageResource(R.drawable.activities_r);
        else if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_ANNOUNCEMENT.getPostDataType()))
            imagePostDataType.setImageResource(R.drawable.announcements_r);
        else if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_REFERENCE_POST.getPostDataType()))
            imagePostDataType.setImageResource(R.drawable.reference_r);
        else if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_STREAM.getPostDataType()))
            imagePostDataType.setImageResource(R.drawable.video_streaming_cir);
        else imagePostDataType.setImageResource(R.drawable.transparent);
    }

    /**
     * check post attachment list contains one or more assignment
     * if post has only one resource then hide multiple resource view button
     * if post has more then one resources then show multiple resource view button
     *
     * @param mAttachmentPathList
     * @param multipleResourceButton
     */
    private void setMultipleResourceButtonVisibility(ArrayList<Resource> mAttachmentPathList, RelativeLayout multipleResourceButton) {
        if (mAttachmentPathList != null && mAttachmentPathList.size() > 1)
            multipleResourceButton.setVisibility(View.VISIBLE);
        else multipleResourceButton.setVisibility(View.GONE);
    }

    /**
     * show option menu for post item
     *
     * @param context
     * @param view
     * @param postDataDetail
     * @param position
     * @param postByUserTxt
     * @param postData
     */
    private void showOptionPopupMenu(Context context, View view, final PostDataDetail postDataDetail, final int position, final TextView postByUserTxt, final PostData postData) {
        final PopupMenu popup = new PopupMenu(context, view, Gravity.RIGHT);
        if (!PermissionPrefsCommon.getPostBadgeAssignPermission(context) ||
                checkIfPostHaveBadgeAssigned(postDataDetail) ||
                postData.getFrom().getRole().equals(AppUser.USERTYPE.TEACHER.toString()) ||
                postData.getFrom().getRole().equals("") ||
                mAppUserModel.getObjectId().equalsIgnoreCase(postData.getFrom().getId())) {
            popup.inflate(R.menu.option_menu_post_item_student);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete_post:
                            showPostDeleteConfirmationDialog(postDataDetail, position);
                            popup.dismiss();
                            break;
                    }
                    return false;
                }

            });

            popup.show();

        } else {
            popup.inflate(R.menu.option_menu_post_item_teacher);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.assign_badge:
                            showBadgePopUp(postByUserTxt, postData, position);
                            popup.dismiss();
                            break;
                        case R.id.delete_post:
                            showPostDeleteConfirmationDialog(postDataDetail, position);
                            popup.dismiss();
                            break;
                    }
                    return false;
                }


            });

            popup.show();
        }

    }

    /**
     * show confirmation dialog before deleting post data
     *
     * @param postDataDetail
     * @param position
     */
    private void showPostDeleteConfirmationDialog(final PostDataDetail postDataDetail, final int position) {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.post_delete_title))
                .setMessage(mContext.getString(R.string.post_delete_message))
                .setPositiveButton(mContext.getString(R.string.action_delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        mPostDatas.removeItem(postDataDetail);
//                        notifyItemRemoved(position);
//                        notifyItemRangeChanged(position, getItemCount());
                        dialog.dismiss();
                        deletePost(postDataDetail, position);
                        Completable.complete().subscribeOn(Schedulers.computation())
                                .observeOn(Schedulers.computation())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() throws Exception {

                                        mLearningModel.deletePost(postDataDetail);
                                    }
                                });

                    }
                })
                .setNegativeButton(mContext.getString(R.string.action_keep), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void deletePost(PostDataDetail postDataDetail, int position) {
        ArrayList<String> mPostFavoriteUserIds = postDataDetail.getPostData().getPostFavoriteUserIds();

        if (!mPostFavoriteUserIds.isEmpty() && mPostFavoriteUserIds.contains(mAppUserModel.getObjectId())) {
            mPostFavoriteUserIds.remove(mAppUserModel.getObjectId());
            postDataDetail.getPostData().setPostFavoriteUserIds(mPostFavoriteUserIds);
            mPostDataModel.savePostData(postDataDetail.getPostData());

            if (mBaseLNFragment instanceof FavoritePostFragment) {
                if (mPostDatas.get(position) != null) {
                    PostDataDetail dataDetail = mPostDatas.get(position);
                    dataDetail.setPostData(postDataDetail.getPostData());
                    mRxBus.send(new LoadPostRemoveEvent(dataDetail));

                }

                mPostDatas.remove(position);
                mIds.remove(position);
                if (mPostDatas.size() == 0)
                    ((FavoritePostFragment) mBaseLNFragment).mNoFavoriteLayout.setVisibility(View.VISIBLE);

            } else if (mBaseLNFragment instanceof PostFragment) {
                if (mPostDatas.get(position) != null)
                    mRxBus.send(new LoadPostAddToFavoriteEvent(mPostDatas.get(position), false));

                mPostDatas.remove(position);
                mIds.remove(position);
                if (mPostDatas.size() == 0)
                    ((PostFragment) mBaseLNFragment).mNoPostLayout.setVisibility(View.VISIBLE);

            }

        } else {
            if (mBaseLNFragment instanceof PostFragment) {
                if (mPostDatas.get(position) != null) {
                    mRxBus.send(new LoadPostAddToFavoriteEvent(mPostDatas.get(position), false));
                }

                mPostDatas.remove(position);
                mIds.remove(position);
                if (mPostDatas.size() == 0) {
                    ((PostFragment) mBaseLNFragment).mNoPostLayout.setVisibility(View.VISIBLE);
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * check if response list contains og data
     *
     * @param processData
     * @return
     */
    private ArrayList<String> checkCommentForOGIcon(String processData) {
        String data[] = GeneralUtils.getArrayOfAllUrls(processData);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(data));
        return list;
    }

    /**
     * check if there is liked or favorites in the post and set liked and favorite button selected true otherwise false.
     *
     * @param postDataDetail
     * @param holder
     */
    private void checkIfUserLikedOrFavoritePostBefore(PostDataDetail postDataDetail, ViewHolder holder) {

        boolean isLikedByCurrentUser = false, isAddToFavByUser = false, isHaveComments = false;
        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();
        if (postResponses != null && postResponses.size() > 0) {

            for (PostResponse postResponse : postResponses) {
                if (postResponse.getFrom().getId().equalsIgnoreCase(mAppUserModel.getObjectId())) {
                    if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_COMMENT.getPostResponseType()))
                        isHaveComments = true;
                    else if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_RECOMMEND.getPostResponseType()))
                        isLikedByCurrentUser = true;
                }
            }

            if (isLikedByCurrentUser) {
                holder.mLikeButton.setEnabled(false);
                holder.mLikeButton.setSelected(true);

                holder.mLikeLayout.setEnabled(false);
                holder.mLikeLayout.setSelected(true);


            } else {
                holder.mLikeButton.setEnabled(true);
                holder.mLikeButton.setSelected(false);

                holder.mLikeLayout.setEnabled(true);
                holder.mLikeLayout.setSelected(false);
            }
            if (isHaveComments) {
                holder.mCommentsButton.setSelected(true);
            } else {
                holder.mCommentsButton.setSelected(false);
            }

        } else {
            holder.mLikeButton.setEnabled(true);
            holder.mLikeButton.setSelected(false);
            holder.mCommentsButton.setEnabled(true);
            holder.mCommentsButton.setSelected(false);

        }

        if (postDataDetail.getPostData() != null && postDataDetail.getPostData().getPostFavoriteUserIds().contains(mAppUserModel.getObjectId()))
            isAddToFavByUser = true;
        else
            isAddToFavByUser = false;

        if (isAddToFavByUser) {
            //holder.mFavoriteButton.setEnabled(false);
            holder.mFavoriteButton.setSelected(true);
            // holder.mFavoriteLayout.setEnabled(false);
            holder.mFavoriteLayout.setSelected(true);

        } else {
            // holder.mFavoriteButton.setEnabled(true);
            holder.mFavoriteButton.setSelected(false);
//            holder.mFavoriteLayout.setEnabled(true);
            holder.mFavoriteLayout.setSelected(false);
        }
    }

    /**
     * check if post have any badge assigned.
     *
     * @param postDataDetail
     * @return boolean tru if post have a badge assign
     */
    private boolean checkIfPostHaveBadgeAssigned(PostDataDetail postDataDetail) {

        boolean isPostContainBadge = false;
        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();

        for (PostResponse postResponse : postResponses) {
            if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType())) {
                isPostContainBadge = true;
                break;
            }
        }
        return isPostContainBadge;
    }

    /**
     * if post has badge, then show assigned badge other wise hide badge image view
     *
     * @param holder
     * @param postDataDetail
     */
    private void setBadgeToPost(ViewHolder holder, PostDataDetail postDataDetail) {

        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();
        for (PostResponse postResponse : postResponses) {
            if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType()) && postResponse.getAssignedPostResponseId() == null) {
                LILBadges lilBadges = mBadgesModel.fetchLilBadgeFromUidSync(postResponse.getAssignedBadgeId());
                holder.mBadgeImageView.setVisibility(View.VISIBLE);
                holder.mBadgeImageView.setImageResource(getIResourcesIdentifier(lilBadges.getThumbnail()));
                break;
            } else {
                holder.mBadgeImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * check if post has badge assigned or not
     *
     * @param postDataDetail
     * @param postResponse
     * @return
     */
    private boolean checkIfPostResponseHaveBadgeAssigned(PostDataDetail postDataDetail, PostResponse postResponse) {

        boolean isPostResponseHaveBadge = false;
        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();

        for (PostResponse response : postResponses) {
            if (response.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType()) && response.getAssignedPostResponseId() != null && response.getAssignedPostResponseId().equalsIgnoreCase(postResponse.getObjectId())) {
                isPostResponseHaveBadge = true;
                break;
            }
        }

        return isPostResponseHaveBadge;
    }

    /**
     * if post response has assigned badge then show badge other wise hide badge image view
     *
     * @param postResponse
     * @param postDataDetail
     * @param mBadgeImageView
     */
    private boolean setBadgesToPostResponse(PostResponse postResponse, PostDataDetail postDataDetail, ImageView mBadgeImageView) {
        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();
        boolean isHasBadge = false;
        for (PostResponse response : postResponses) {
            if (response.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType())) {
                if (response.getAssignedPostResponseId() != null && response.getAssignedPostResponseId().equalsIgnoreCase(postResponse.getObjectId())) {
                    LILBadges lilBadges = mBadgesModel.fetchLilBadgeFromUidSync(response.getAssignedBadgeId());
                    mBadgeImageView.setImageResource(getIResourcesIdentifier(lilBadges.getThumbnail()));
                    mBadgeImageView.setVisibility(View.VISIBLE);
                    isHasBadge = true;
                    break;
                }
            }
        }
        if (!isHasBadge) {
            mBadgeImageView.setVisibility(View.GONE);
        }
        return isHasBadge;
    }

    /**
     * Method to create a PostResponse Data object for a new comment
     *
     * @param postData         Post which you are respond
     * @param holder           view holder for a item
     * @param postResponseType type of post response
     * @return PostResponse object
     */
    private PostResponse getPostResponse(PostData postData, ViewHolder holder, PostResponseType postResponseType) {

        PostResponse postResponse = new PostResponse();
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getName());
        fromUser.setId(mAppUserModel.getObjectId());
        fromUser.setRole(setUserRole());
        postResponse.setFrom(fromUser);
        postResponse.setTo(postData.getTo());
        postResponse.setObjectId(null);
        postResponse.setAlias(GeneralUtils.generateAlias("LNPostResponse", "" + mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));
        postResponse.setPostID(postData.getObjectId());
        postResponse.setResources(null);
        postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        postResponse.setText(holder.mWriteCommentEdtTxt.getText().toString());
        postResponse.setType(postResponseType.getPostResponseType());//Like,favorite etc
        postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        postResponse.setUnread(false);
        postResponse.setUpdatedTime(new Date());
        postResponse.setGroupId(postData.getTo().getId());

        return postResponse;
    }

    private String setUserRole() {
        String strRole = "";
        if (PermissionPrefsCommon.getPostBadgeAssignPermission(mContext)) {
            strRole = AppUser.USERTYPE.TEACHER.toString();
        } else {
            strRole = AppUser.USERTYPE.STUDENT.toString();
        }
        return strRole;
    }

    /**
     * Method to create a PostResponse Data object for a new comment (Comment PopUp)
     *
     * @param postData         Post which you are respond
     * @param postResponseType type of post response
     * @return PostResponse object
     */
    private PostResponse getPostResponseFromCommentPopUp(PostData postData, EditText mDialogCommentEditText, PostResponseType postResponseType) {

        PostResponse postResponse = new PostResponse();
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getName());
        fromUser.setId(mAppUserModel.getObjectId());
        fromUser.setRole(setUserRole());
        postResponse.setFrom(fromUser);
        postResponse.setTo(postData.getTo());
        postResponse.setObjectId(null);
        postResponse.setPostID(postData.getObjectId());
        postResponse.setResources(null);
        mDialogCommentEditText.clearComposingText();
        postResponse.setText(Html.toHtml(mDialogCommentEditText.getText()).toString());
        postResponse.setType(postResponseType.getPostResponseType());
        postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        postResponse.setUpdatedTime(new Date());
        postResponse.setGroupId(postData.getTo().getId());
        postResponse.setAlias(GeneralUtils.generateAlias("PostResponse", fromUser.getId(), "" + System.currentTimeMillis()));
        postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());

        return postResponse;
    }

    /**
     * Method to setup view for the comment post reponse.
     *
     * @param postDataDetail
     * @param position
     * @param viewHolder
     */
    private void setUpPostResponseLayout(final PostDataDetail postDataDetail, final int position, final ViewHolder viewHolder) {

        int likeCount = 0, favoriteCount = 0, commentCount = 0, lastComment = -1;

        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();
        if (postResponses != null && postResponses.size() > 0) {

            if (viewHolder.mLayoutPostResponse != null)
                viewHolder.mLayoutPostResponse.removeAllViews();

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mLayout;

            Collections.sort(postResponses, new SortPostResponseByDate.CreatedDateSorter());

            int i = 0;
            for (PostResponse postResponse : postResponses) {
                /**
                 * Check if PostResponse type is Comment  then add view to comment layout
                 */
                if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_COMMENT.getPostResponseType())) {
                    lastComment = i;
                    commentCount++;
                }
                /**
                 * Check if PostResponse type is Favorite  then only increase count
                 */
                else if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_FAVORITE.getPostResponseType()))
                    favoriteCount++;

                /**
                 * Check if PostResponse type is Recommend  then only increase count
                 */
                else if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_RECOMMEND.getPostResponseType()))
                    likeCount++;

                if (likeCount == 0) {
                    viewHolder.mTotalLikeCountTxt.setText("0");
                } else {
                    viewHolder.mTotalLikeCountTxt.setText("" + likeCount);
                }

                if (commentCount == 0) {
                    viewHolder.mCommentCountTxt.setText("0");
                } else {
                    viewHolder.mCommentCountTxt.setText("" + commentCount);
                }
                i++;
            }

            /**
             * Show only last comment
             */
            if (lastComment != -1) {
                mLayout = vi.inflate(R.layout.layout_users_comment_view, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(20, 0, 0, 0);
                mLayout.setLayoutParams(params);
                TextView mUserNameTxt = (TextView) mLayout.findViewById(R.id.textView_user_name);
                TextView mCommentTxt = (TextView) mLayout.findViewById(R.id.textView_posted);
                TextView mViewMoreLessTextView = (TextView) mLayout.findViewById(R.id.textViewMoreLess);
                TextView mCommentDatetime = (TextView) mLayout.findViewById(R.id.textView_post_date);
                ImageView mUserThumbnailImage = (ImageView) mLayout.findViewById(R.id.imageView_user_thumbnail);
                ImageButton mResponseBadgePopupButton = (ImageButton) mLayout.findViewById(R.id.button_response_badge_popup);
                mLayout.findViewById(R.id.view_end).setVisibility(View.GONE);
                setUserThumbnailToView(mUserThumbnailImage, postResponses.get(lastComment).getFrom().getId());

                mCommentDatetime.setText(DateUtils.getTimeFromDate(DateUtils.convertrIsoDate(postResponses.get(lastComment).getCreatedTime())));
                TextViewMore.viewMore(postResponses.get(lastComment).getText(), mCommentTxt, mViewMoreLessTextView);
                mUserNameTxt.setText(postResponses.get(lastComment).getFrom().getName());
                viewHolder.mLayoutPostResponse.addView(mLayout);
                viewHolder.mLayoutPostResponse.setVisibility(View.VISIBLE);
                mResponseBadgePopupButton.setVisibility(View.INVISIBLE);


                mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - lastAddPostBtnClickTime < 1000) {
                            return;
                        }
                        lastAddPostBtnClickTime = SystemClock.elapsedRealtime();

                        showCommentsPopUp(postDataDetail, position, viewHolder, 0);
                    }
                });

            }

        } else {

            if (viewHolder.mLayoutPostResponse != null)
                viewHolder.mLayoutPostResponse.removeAllViews();

            viewHolder.mLayoutPostResponse.setVisibility(View.GONE);
            if (likeCount == 0) {
                viewHolder.mTotalLikeCountTxt.setText("0");
            } else {
                viewHolder.mTotalLikeCountTxt.setText("" + likeCount);
            }

            if (commentCount == 0) {
                viewHolder.mCommentCountTxt.setText("0");
            } else {
                viewHolder.mCommentCountTxt.setText("" + commentCount);
            }

        }

    }

    /**
     * shows popup for comment list and to write comment for the post.
     *
     * @param postDataDetail
     * @param adapterPosition
     * @param holder
     * @param iKeyboard
     */
    private void showCommentsPopUp(final PostDataDetail postDataDetail, final int adapterPosition, final ViewHolder holder, int iKeyboard) {

        final Dialog mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_comment_popup);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        if (iKeyboard == 0) {
            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } else if (iKeyboard == 1) {
            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        final ImageButton mDialogCommentsImgBtn = (ImageButton) mDialog.findViewById(R.id.imageBtn_comment);
        final RelativeLayout mDialogNoCommentsLayout = (RelativeLayout) mDialog.findViewById(R.id.layout_no_comments);
        mCommentRecyclerView = (RecyclerView) mDialog.findViewById(R.id.recycler_view_post_comment);
        final EditText mDialogCommentEditText = (EditText) mDialog.findViewById(R.id.editText_write_comment_popup);
        final ImageButton mDialogPostCommentButton = (ImageButton) mDialog.findViewById(R.id.button_post_comment);
        mFloatingCountView = (CardView) mDialog.findViewById(R.id.cardViewFloatingCount);
        mNewPostResponseCountTextView = (TextView) mDialog.findViewById(R.id.textViewNewResponseCount);
        final PostData postData = postDataDetail.getPostData();
        mDialogPostCommentButton.setEnabled(false);
        mDialogPostCommentButton.setSelected(false);

        List<PostResponse> postResponses = getCommentPostResponseList(postDataDetail);
        Collections.sort(postResponses, new SortPostResponseByDate.CreatedDateSorter());

        mCommentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int isLastItem = commentLayoutManager.findLastCompletelyVisibleItemPosition();
                if (isLastItem == commentAdapter.getItemCount() - 1) {
                    mFloatingCountView.performClick();
                }
            }
        });

        if (mCommentRecyclerView != null) {
            if (postDataDetail != null && !postDataDetail.getPostResponses().isEmpty()) {
                commentAdapter = new LearningNetworkDetailAdapter.CommentAdapter(postResponses, postDataDetail);
                commentLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                mCommentRecyclerView.setLayoutManager(commentLayoutManager);
                mCommentRecyclerView.setAdapter(commentAdapter);
                mCommentRecyclerView.smoothScrollToPosition(postResponses.size());
            } else {
                mDialogNoCommentsLayout.setVisibility(View.VISIBLE);
                commentAdapter = new LearningNetworkDetailAdapter.CommentAdapter(new ArrayList<PostResponse>(), postDataDetail);
                commentLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                mCommentRecyclerView.setLayoutManager(commentLayoutManager);
                mCommentRecyclerView.setAdapter(commentAdapter);
                mCommentRecyclerView.smoothScrollToPosition(postResponses.size());
            }
        }

        mDialogCommentEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mDialogCommentEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                return false;
            }
        });

        mDialogCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mDialogCommentEditText.getText().toString().trim().isEmpty()) {
                    mDialogPostCommentButton.setEnabled(true);
                    mDialogPostCommentButton.setSelected(true);
                } else {
                    mDialogPostCommentButton.setEnabled(false);
                    mDialogPostCommentButton.setSelected(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDialogPostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogNoCommentsLayout.setVisibility(View.GONE);
                mDialogCommentsImgBtn.setSelected(true);
                PostResponse postResponse = getPostResponseFromCommentPopUp(postData, mDialogCommentEditText, PostResponseType.TYPE_COMMENT);
                //hideSoftKeyboard(mDialogCommentEditText);
                mPostDatas.get(adapterPosition).getPostResponses().add(postResponse);
                mDialogCommentEditText.setText("");
                mLearningModel.savePostResponse(postResponse);
                //SyncServiceHelper.startUploadPostResponse(mContext, postResponse.getAlias());
                notifyItemChanged(adapterPosition);
                SoundUtils.playSound(mContext, SoundUtils.LEARNING_NETWORK_POST_RESPONSE_ADDED);

                if (mCommentRecyclerView != null && commentAdapter != null) {
                    commentAdapter.addItem(postResponse);

                    if (commentAdapter.mPostResponses.size() > 0)
                        mCommentRecyclerView.smoothScrollToPosition(commentAdapter.mPostResponses.size() - 1);
                }


            }
        });

        if (mContext.getResources().getBoolean(R.bool.isTablet)) {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            Double mDialogWidth = metrics.widthPixels * 0.75;
            Double mDialogHeight = metrics.heightPixels * 0.75;
            Window win = mDialog.getWindow();
            win.setLayout(mDialogWidth.intValue(), mDialogHeight.intValue());
            mDialog.show();

        } else {
            Window window = mDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mDialog.show();
        }


    }

    /**
     * get post responses list of post
     *
     * @param postDataDetail
     * @return
     */
    private List<PostResponse> getCommentPostResponseList(PostDataDetail postDataDetail) {
        List<PostResponse> postResponses = new ArrayList<>();
        if (postDataDetail != null && postDataDetail.getPostResponses() != null && !postDataDetail.getPostResponses().isEmpty()) {
            for (PostResponse postResponse : postDataDetail.getPostResponses()) {
                if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_COMMENT.getPostResponseType()))
                    postResponses.add(postResponse);
            }
        }
        return postResponses;
    }

    /**
     * show badge grid view to assign badges for the post
     *
     * @param position
     */
    private void showBadgePopUp(TextView textView, Object object, int position) {
        final Dialog mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_teacher_popup_badges);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77000000")));
        TextView mUserNameTextView = (TextView) mDialog.findViewById(R.id.textview_assign_badge_user);
        RecyclerView mBadgeGridView = (RecyclerView) mDialog.findViewById(R.id.recycler_grid_badge);
        mUserNameTextView.setText("Assign badge to" + " " + textView.getText().toString().trim());

        setUpBadgeGridView(mBadgeGridView, mDialog, object, position);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Double width = metrics.widthPixels * 0.80;
        Window win = mDialog.getWindow();
        win.setLayout(width.intValue(), ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }

    /**
     * setup gridview for badges
     *
     * @param mBadgeGridView
     * @param object
     * @param position
     */
    private void setUpBadgeGridView(final RecyclerView mBadgeGridView, final Dialog dialog, final Object object, final int position) {

        Observable.just(mContext).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                if (object instanceof PostData) {
                    ArrayList<LILBadges> badges = mBadgesModel.fetchLilBadgesListSync();
                    GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                    mBadgeGridView.setLayoutManager(mLayoutManager);

                    BadgeAssignedInterface badgeAssignedInterface = new BadgeAssignedInterface() {
                        @Override
                        public void onBadgeAssigned(PostResponse postResponse) {
                            mPostDatas.get(position).getPostResponses().add(postResponse);
                            notifyItemChanged(position);
                        }
                    };

                    PostDataBadgeAdapter mPostDataBadgeAdapter = new PostDataBadgeAdapter(mContext, badges, dialog, (PostData) object, badgeAssignedInterface);
                    mBadgeGridView.setAdapter(mPostDataBadgeAdapter);

                } else {
                    ArrayList<LILBadges> badges = mBadgesModel.fetchLilBadgesListSync();
                    GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                    mBadgeGridView.setLayoutManager(mLayoutManager);
                    BadgeAssignedInterface badgeAssignedInterface = new BadgeAssignedInterface() {
                        @Override
                        public void onBadgeAssigned(PostResponse postResponse) {
                            commentAdapter.mPostDataDetail.getPostResponses().add(postResponse);
                            commentAdapter.notifyItemChanged(position);
                        }
                    };

                    PostResponseBadgeAdapter postResponseBadgeAdapter = new PostResponseBadgeAdapter(mContext, badges, dialog, (PostResponse) object, mCurrentSelectedGroup.getObjectId(), badgeAssignedInterface);
                    mBadgeGridView.setAdapter(postResponseBadgeAdapter);
                }

            }
        });


    }

    /**
     * Method for set user thumbnail to view
     *
     * @param imageView in which view you want to set
     * @param userUid   id of user for which thumbnail will set
     */
    private void setUserThumbnailToView(ImageView imageView, final String userUid) {

        boolean isUserThumbnailFetched = false;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BuildConfig.ViewVisibilty == false) {

                } else {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        Intent mIntent = new Intent(mContext, UserProfileActivity.class);
                        mIntent.putExtra(UserProfileActivity.USER_ID, userUid);
                        mContext.startActivity(mIntent);
                    } else {

                        ToastUtils.showToastAlert(mContext, "Please connect to internet");
                    }
                }
            }
        });

        ArrayList<GroupMember> groupMembers = mCurrentSelectedGroup.getMembers();
        for (GroupMember groupMember : groupMembers) {
            if (groupMember.getObjectId().equalsIgnoreCase(userUid)) {

                if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getLocalUrl())) {
                    Picasso.with(mContext).load(groupMember.getPic().getLocalUrl()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
                } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getUrl())) {
                    Picasso.with(mContext).load(groupMember.getPic().getUrl()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
                } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getThumb())) {
                    Picasso.with(mContext).load(groupMember.getPic().getThumb()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
                } else {
                    String firstWord = groupMember.getName().substring(0, 1).toUpperCase();
                    TextDrawable textDrawable = TextDrawable.builder().buildRect(firstWord, R.color.colorPrimaryLN);
                    imageView.setImageDrawable(textDrawable);
                }

                isUserThumbnailFetched = true;
                break;
            }
        }

//        if (!isUserThumbnailFetched) {
//            ArrayList<Moderator> groupModerators = mCurrentSelectedGroup.getModerators();
//            for (Moderator moderator : groupModerators) {
//                if (moderator.getId().equalsIgnoreCase(userUid)) {
//                    String url = moderator.getPic().getThumb();
//                    File mImageFile;
//                    mImageFile = new File(FileUtils.getPathFromFilePath(url));
//                    if (FileUtils.checkIsFilePath(url)) {
//                        mImageFile = new File(FileUtils.getPathFromFilePath(url));
//                        Picasso.with(mContext).load(mImageFile).placeholder(R.drawable.icon_profile_large).resize(200, 200).centerCrop().into(imageView);
//                    }
//                    break;
//                }
//            }
//        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    Intent mIntent = new Intent(mContext, UserProfileActivity.class);
                    mIntent.putExtra(UserProfileActivity.USER_ID, userUid);
                    mContext.startActivity(mIntent);
                } else {

                    ToastUtils.showToastAlert(mContext, "Please connect to internet");
                }
            }
        });

    }

    /**
     * hide keyboard
     *
     * @param mEditText
     */
    public void hideSoftKeyboard(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

    }

    /**
     * Adapter to bind comment data recycler view
     */
    public class CommentAdapter extends RecyclerView.Adapter<LearningNetworkDetailAdapter.CommentAdapter.ViewHolder> {
        List<PostResponse> mPostResponses = new ArrayList<>();
        PostDataDetail mPostDataDetail = new PostDataDetail();
        ArrayList<String> mPrIds = new ArrayList<>();

        public CommentAdapter(List<PostResponse> postResponses, PostDataDetail postDataDetail) {
            this.mPostDataDetail = postDataDetail;
            this.mPostResponses = postResponses;
            this.mPrIds = getIdList(mPostResponses);
        }

        private ArrayList<String> getIdList(List<PostResponse> values) {
            ArrayList<String> ids = new ArrayList<>();
            for (PostResponse postResponse :
                    values) {
                ids.add(postResponse.getAlias());
            }
            return ids;
        }

        @Override
        public LearningNetworkDetailAdapter.CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            if (viewType == 0)
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_users_comment_view_right, parent, false);
//            else
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_users_comment_view, parent, false);
            LearningNetworkDetailAdapter.CommentAdapter.ViewHolder mViewHolder = new LearningNetworkDetailAdapter.CommentAdapter.ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(final LearningNetworkDetailAdapter.CommentAdapter.ViewHolder holder, final int position) {
            final PostResponse postResponse = mPostResponses.get(position);
            boolean isHasBadge = setBadgesToPostResponse(postResponse, mPostDataDetail, holder.mBadgeImageView);
            if (!PermissionPrefsCommon.getPostBadgeAssignPermission(mContext) ||
                    postResponse.getFrom().getRole().equals(AppUser.USERTYPE.TEACHER.toString()) ||
                    postResponse.getFrom().getRole().equals("") ||
                    isHasBadge ||
                    mAppUserModel.getObjectId().equalsIgnoreCase(postResponse.getFrom().getId())) {
                holder.mResponseBadgePopupButton.setVisibility(View.INVISIBLE);
            } else {
                holder.mResponseBadgePopupButton.setVisibility(View.VISIBLE);
            }


            setUserThumbnailToView(holder.mUserThumbnailImageView, postResponse.getFrom().getId());

            holder.mCommentDatetimeTextView.setText(DateUtils.getDateTimeStringWithDateMonthHourMinuteAMPM(DateUtils.convertrIsoDate(postResponse.getCreatedTime())));
            holder.mCommentTextView.setText(TextViewMore.viewMore(postResponse.getText(), holder.mCommentTextView, holder.mViewMoreLessTextView));
            holder.mUserNameTextView.setText(postResponse.getFrom().getName());

            holder.mResponseBadgePopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBadgePopUp(holder.mUserNameTextView, postResponse, position);
                }
            });

            if (postResponse.getLocalOgDataList().size() > 0) {
                String urlImage = "";
                String siteName = "";
                try {
                    urlImage = postResponse.getPositiveResult().getOgMeta().getOgImage().getUrl();
                    siteName = postResponse.getPositiveResult().getOgMeta().getOgTitle();
                } catch (Exception e) {
                    Log.e("OgIconData", "not get url image and site");
                }
                if (!urlImage.isEmpty()) {
                    //Matches means data is exist on locally we are not require to call service for this case.
                    Picasso.with(mContext).load(urlImage).into(holder.img_og_card);
                } else {
                    holder.img_og_card.setImageResource(R.drawable.icon_og_broken);
                }
                holder.layout_og_card.setVisibility(View.VISIBLE);
                holder.textview_og_card.setText(postResponse.getPositiveResult().getUrl());
                holder.textview_og_card_desc.setText(siteName);
                final String finalUrlData = postResponse.getPositiveResult().getUrl();
                holder.img_og_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!finalUrlData.isEmpty()) {
                            CustomChromeTabHelper.loadCustomDataUsingColorResource(mContext, finalUrlData, R.color.colorLearningNetworkAccent);
                        }
                    }
                });
            } else {
                holder.layout_og_card.setVisibility(View.GONE);
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    if (postResponse.getoGDataList().size() <= 0) {
                        postResponse.setoGDataList(checkCommentForOGIcon(postResponse.getText()));
                    }
                    if (postResponse.getoGDataList().size() > 0) {
                        try {
                            mOgUtils.getOgDataFromServer(postResponse.getoGDataList()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OGMetaDataResponse>() {
                                @Override
                                public void accept(OGMetaDataResponse ogData) throws Exception {
                                    Result response = new Result();
                                    Result responseToSet = null;
                                    for (int i = 0; i < ogData.getResults().size(); i++) {
                                        response = ogData.getResults().get(i);
                                        postResponse.getLocalOgDataList().put(response.getUrl(), response);
                                        if (response.getOg().equals(true)) {
                                            if (responseToSet == null) {
                                                responseToSet = response;
                                                postResponse.setPositiveResult(responseToSet);
                                            }
                                        }
                                    }
                                    if (responseToSet != null) {
                                        final Result finalResponseToSet = responseToSet;
                                        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                            @Override
                                            public void run() {
                                                Picasso.with(mContext).load(finalResponseToSet.getOgMeta().getOgImage().getUrl()).into(holder.img_og_card);
                                                holder.layout_og_card.setVisibility(View.VISIBLE);
                                                holder.textview_og_card.setText(finalResponseToSet.getUrl());
                                                holder.textview_og_card_desc.setText(finalResponseToSet.getOgMeta().getOgTitle());
                                                holder.img_og_card.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CustomChromeTabHelper.loadCustomDataUsingColorResource(mContext, finalResponseToSet.getUrl(), R.color.colorLearningNetworkPrimary);
                                                    }
                                                });
                                            }
                                        });
                                    } else if (response != null) {
                                        postResponse.setPositiveResult(response);
                                        final Result finalResponse = response;
                                        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                            @Override
                                            public void run() {
                                                holder.img_og_card.setImageResource(R.drawable.icon_og_broken);
                                                holder.layout_og_card.setVisibility(View.VISIBLE);
                                                holder.textview_og_card.setText(finalResponse.getUrl());
                                                holder.textview_og_card_desc.setText("");
                                            }
                                        });
                                    }
                                    mLearningModel.savePostResponse(postResponse);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mPostResponses.size();
        }

        public String getPostId() {
            return mPostDataDetail.getObjectId();
        }

        public boolean addItem(PostResponse postResponse) {

            if (!mPrIds.contains(postResponse.getAlias())) {
                this.mPostResponses.add(postResponse);
                mPrIds.add(postResponse.getAlias());
                this.notifyItemInserted(mPostResponses.size() - 1);
                return true;
            }
            return false;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View mRootView;
            final TextView mUserNameTextView;
            TextView mCommentTextView;
            TextView mCommentDatetimeTextView;
            TextView mViewMoreLessTextView;
            ImageView mUserThumbnailImageView;
            ImageView mBadgeImageView;
            ImageButton mResponseBadgePopupButton;
            ImageView img_og_card;
            TextView textview_og_card;
            TextView textview_og_card_desc;
            RelativeLayout layout_og_card;

            public ViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mUserNameTextView = (TextView) itemView.findViewById(R.id.textView_user_name);
                mCommentTextView = (TextView) itemView.findViewById(R.id.textView_posted);
                mViewMoreLessTextView = (TextView) itemView.findViewById(R.id.textViewMoreLess);
                mCommentDatetimeTextView = (TextView) itemView.findViewById(R.id.textView_post_date);
                mUserThumbnailImageView = (ImageView) itemView.findViewById(R.id.imageView_user_thumbnail);
                mBadgeImageView = (ImageView) itemView.findViewById(R.id.imageView_badge);
                mResponseBadgePopupButton = (ImageButton) itemView.findViewById(R.id.button_response_badge_popup);
                img_og_card = (ImageView) itemView.findViewById(R.id.img_og_card);
                textview_og_card = (TextView) itemView.findViewById(R.id.textview_og_card);
                textview_og_card_desc = (TextView) itemView.findViewById(R.id.textview_og_card_desc);
                layout_og_card = (RelativeLayout) itemView.findViewById(R.id.layout_og_card);
            }
        }
    }

    /**
     * get resource identifier for badge icons
     *
     * @param name
     * @return
     */
    private int getIResourcesIdentifier(String name) {
        return mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
    }

    public interface BadgeAssignedInterface {
        void onBadgeAssigned(PostResponse postResponse);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageButton mLikeButton, mFavoriteButton, imagePostDataType, mCommentsButton;
        public final ImageView mBadgeImageView, mPostedCoverImageView, mUserImageThumbnail, mCurrentUserIcon, mFileTypeImageView, img_og_card;
        private final LinearLayout mLayoutPostResponse, mUserCommentView, mAddCommentLayout, mLikeLayout, mCommentLayout, mFavoriteLayout;
        private final View mRootView;
        private final EditText mWriteCommentEdtTxt;
        private final TextView mPostByUserTxt, mPostTxt, mViewMoreLessTextView, mPostDateTimeTxt, mCommentCountTxt, mTotalLikeCountTxt, mFavoriteCountTxt, textview_og_card, textview_og_card_desc;
        private final Button mCommentPostImgBtn;
        private final ImageButton mOptionMenuButton;
        public RelativeLayout mMultipleResourceButton, layout_og_card_for_mobile;
        private FrameLayout mPostedResLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mMultipleResourceButton = (RelativeLayout) mRootView.findViewById(R.id.button_multiple_resources);
            mLikeButton = (ImageButton) mRootView.findViewById(R.id.imageBtn_like_thumb);
            mFavoriteButton = (ImageButton) mRootView.findViewById(R.id.imageBtn_favorite);
            mCommentsButton = (ImageButton) mRootView.findViewById(R.id.imageBtn_comment);
            mCommentPostImgBtn = (Button) mRootView.findViewById(R.id.imageBtn_comment_post);
            mOptionMenuButton = (ImageButton) mRootView.findViewById(R.id.button_badge_popup);
            mPostedCoverImageView = (ImageView) mRootView.findViewById(R.id.imageView_posted);
            mBadgeImageView = (ImageView) mRootView.findViewById(R.id.imageView_badge);
            mFileTypeImageView = (ImageView) mRootView.findViewById(R.id.imageViewFileType);
            imagePostDataType = (ImageButton) mRootView.findViewById(R.id.imageBtn_post_type);
            mUserImageThumbnail = (ImageView) mRootView.findViewById(R.id.imageView_user_thumbnail);
            mCurrentUserIcon = (ImageView) mRootView.findViewById(R.id.imageView_current_user_icon);
            mLayoutPostResponse = (LinearLayout) mRootView.findViewById(R.id.layout_post_response_view);
            mUserCommentView = (LinearLayout) mRootView.findViewById(R.id.layout_post_response);
            mPostByUserTxt = (TextView) mRootView.findViewById(R.id.textView_user_name);
            mWriteCommentEdtTxt = (EditText) mRootView.findViewById(R.id.editText_write_comment);
            mPostTxt = (TextView) mRootView.findViewById(R.id.textView_posted);
            mViewMoreLessTextView = (TextView) mRootView.findViewById(R.id.textViewMoreLess);
            mTotalLikeCountTxt = (TextView) mRootView.findViewById(R.id.textView_like_count);
            mCommentCountTxt = (TextView) mRootView.findViewById(R.id.textView_comment_count);
            mFavoriteCountTxt = (TextView) mRootView.findViewById(R.id.textView_favorite_count);
            mPostDateTimeTxt = (TextView) mRootView.findViewById(R.id.textView_post_date);
            textview_og_card = (TextView) mRootView.findViewById(R.id.textview_og_card);
            textview_og_card_desc = (TextView) mRootView.findViewById(R.id.textview_og_card_desc);
            mPostedResLayout = (FrameLayout) mRootView.findViewById(R.id.posted_resources_layout);
            layout_og_card_for_mobile = (RelativeLayout) mRootView.findViewById(R.id.layout_og_card);
            mAddCommentLayout = (LinearLayout) mRootView.findViewById(R.id.layout_add_comment_main);
            mLikeLayout = (LinearLayout) mRootView.findViewById(R.id.layout_like);
            mCommentLayout = (LinearLayout) mRootView.findViewById(R.id.layout_comment);
            mFavoriteLayout = (LinearLayout) mRootView.findViewById(R.id.layout_fav);
            img_og_card = (ImageView) mRootView.findViewById(R.id.img_og_card);
        }

        public void clearAnimation() {
            mRootView.clearAnimation();
        }


    }


}
