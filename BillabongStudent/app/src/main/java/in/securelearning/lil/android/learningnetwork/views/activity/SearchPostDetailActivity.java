package in.securelearning.lil.android.learningnetwork.views.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import in.securelearning.lil.android.app.databinding.LayoutLearningNetworkSearchedPostDetailBinding;
import in.securelearning.lil.android.base.comparators.SortPostResponseByDate;
import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.constants.SyncStatus;
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
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.model.PostResponseModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.widget.ImageViewPager;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.adapter.FullScreenImage;
import in.securelearning.lil.android.learningnetwork.adapter.LearningNetworkDetailAdapter;
import in.securelearning.lil.android.learningnetwork.adapter.PostDataBadgeAdapter;
import in.securelearning.lil.android.learningnetwork.adapter.PostResponseBadgeAdapter;
import in.securelearning.lil.android.learningnetwork.adapter.RecyclerViewImageAdapter;
import in.securelearning.lil.android.learningnetwork.adapter.ViewPagerImageAdapter;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.fragment.BaseLNFragment;
import in.securelearning.lil.android.syncadapter.rest.ApiModule;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Chaitendra on 4/26/2017.
 */

public class SearchPostDetailActivity extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    PostDataLearningModel mLearningModel;
    @Inject
    PostDataModel mPostDataModel;
    @Inject
    PostResponseModel mPostResponseModel;
    @Inject
    BadgesModel mBadgesModel;
    @Inject
    GroupModel mGroupModel;
    @Inject
    RxBus mRxBus;
    public static String strPostDataId = "postDataId";
    public static String strPostDataDocId = "postDataDocId";
    public static String strIsDocId = "isDocId";
    private PostDataDetail postDataDetail;
    private LayoutLearningNetworkSearchedPostDetailBinding mBinding;
    private Group group;
    private BaseLNFragment mBaseLNFragment;
    private CommentAdapter commentAdapter;
    private ImageViewPager mImageViewPager;
    private GridLayoutManager mLayoutManager;
    int likeCount = 0, commentCount = 0;
    public static String strPostResponseId = "postResponseId";
    private String mPostResponseId = "";
    private int intPosition = 0;

    public static void startSearchDetailActivity(Context context, String postDataId, String postResponseId) {
        Intent intent = new Intent(context, SearchPostDetailActivity.class);
        intent.putExtra(strPostDataId, postDataId);
        intent.putExtra(strIsDocId, false);
        intent.putExtra(strPostResponseId, postResponseId);
        context.startActivity(intent);
    }

    public static void startSearchDetailActivityWithDocId(Context context, String docId) {
        Intent intent = new Intent(context, SearchPostDetailActivity.class);
        intent.putExtra(strPostDataDocId, docId);
        intent.putExtra(strIsDocId, true);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!mBinding.editTextWriteComment.getText().toString().isEmpty()) {
            new AlertDialog.Builder(SearchPostDetailActivity.this).setTitle(getString(R.string.close_alert_title)).setMessage(getString(R.string.close_alert_message)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } else {
            finish();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_learning_network_searched_post_detail);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorLearningNetworkPrimary));
        getData();

    }

    private void getData() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                if (getIntent().getExtras().getBoolean(strIsDocId)) {
                    String strPostId = getIntent().getExtras().getString(strPostDataDocId);
                    PostData postData = mPostDataModel.getPostDataFromDocId(strPostId);
                    ArrayList<PostResponse> postResponses = mPostResponseModel.getPostResponseListForPostByPostId(postData.getObjectId(), 0, 10);
                    postDataDetail = new PostDataDetail();
                    postDataDetail.setPostData(postData);
                    postDataDetail.setPostResponses(postResponses);
                    group = mGroupModel.getGroupFromUidSync(postData.getTo().getId());
                    e.onNext(true);
                } else {
                    String strPostId = getIntent().getExtras().getString(strPostDataId);
                    mPostResponseId = getIntent().getExtras().getString(strPostResponseId);
                    PostData postData = mPostDataModel.getPostDataFromUidSync(strPostId);
                    ArrayList<PostResponse> postResponses = mPostResponseModel.getPostResponseListForPostByPostId(postData.getObjectId(), 0, 10);
                    postDataDetail = new PostDataDetail();
                    postDataDetail.setPostData(postData);
                    postDataDetail.setPostResponses(postResponses);
                    group = mGroupModel.getGroupFromUidSync(postData.getTo().getId());
                    e.onNext(true);
                }

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (postDataDetail != null) {
                            initializeUiAndListeners();
                        } else {
                            finish();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }

    private void initializeUiAndListeners() {
        mBaseLNFragment = BaseLNFragment.newInstance(group.getObjectId());
        showComments();
        if (postDataDetail.getPostData().getSyncStatus().equals(SyncStatus.NOT_SYNC.toString())) {
            checkPostSyncStatus(false);
            if (GeneralUtils.isNetworkAvailable(SearchPostDetailActivity.this)) {
                syncPost();
            } else {
                showSnackBar();
            }
        } else {
            checkPostSyncStatus(true);
        }

        mBinding.textviewToolbar.setText(group.getGroupName());
        mBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mBinding.textviewToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (group != null && group.getObjectId() != null) {
                    startActivity(LearningNetworkDetailActivity.startLearningNetworkActivity(SearchPostDetailActivity.this, group.getObjectId()));
                    finish();
                }

            }
        });
        final PostData postData = postDataDetail.getPostData();
        final ArrayList<Resource> mAttachmentPathList = (ArrayList<Resource>) postData.getPostResources();
        if (mAttachmentPathList != null && !mAttachmentPathList.isEmpty() && !mAttachmentPathList.get(0).getDeviceURL().isEmpty()) {
            mBinding.includeOG.layoutOgCard.setVisibility(View.GONE);
            String strMimeType = URLConnection.guessContentTypeFromName(mAttachmentPathList.get(0).getDeviceURL());
            if (strMimeType != null && strMimeType.contains("image")) {
                mBinding.postedResourcesLayout.setVisibility(View.VISIBLE);
                String strImagePath = mAttachmentPathList.get(0).getDeviceURL();
                Picasso.with(SearchPostDetailActivity.this).load(strImagePath).resize(800, 640).centerInside().into(mBinding.imageViewPosted);
                mBinding.imageViewFileType.setImageResource(R.drawable.icon_image_white);
                mBinding.imageViewPosted.setTag(strImagePath);
            } else if (strMimeType != null && strMimeType.contains("video")) {
                mBinding.postedResourcesLayout.setVisibility(View.VISIBLE);
                final String strVideoPath = mAttachmentPathList.get(0).getDeviceURL();
                Bitmap mBitmap = getScaledBitmapFromPath(SearchPostDetailActivity.this.getResources(), strVideoPath);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, 800, 640, false);
                mBinding.imageViewPosted.setImageBitmap(mBitmap);
                mBinding.imageViewFileType.setImageResource(R.drawable.icon_video_white);
                mBinding.imageViewPosted.setTag(strVideoPath);
            }

        } else {
            mBinding.postedResourcesLayout.setVisibility(View.GONE);
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
                    Picasso.with(SearchPostDetailActivity.this).load(urlImage).into(mBinding.includeOG.imgOgCard);
                } else {
                    mBinding.includeOG.imgOgCard.setImageResource(R.drawable.icon_og_broken);
                }
                mBinding.includeOG.layoutOgCard.setVisibility(View.VISIBLE);
                mBinding.includeOG.textviewOgCard.setText(postData.getPositiveResult().getUrl());
                mBinding.includeOG.textviewOgCardDesc.setText(siteName);
                final String finalUrlData = postData.getPositiveResult().getUrl();
                mBinding.includeOG.imgOgCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!finalUrlData.isEmpty()) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(finalUrlData));
                                SearchPostDetailActivity.this.startActivity(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            } else {
                mBinding.includeOG.layoutOgCard.setVisibility(View.GONE);
                if (GeneralUtils.isNetworkAvailable(SearchPostDetailActivity.this)) {
                    //Here we are setting http url in ogDataList
                    if (postData.getoGDataList().size() <= 0) {
                        postData.setoGDataList(checkCommentForOGIcon(postData.getPostText()));
                    }
                    if (postData.getoGDataList().size() > 0) {
                        Completable.complete().observeOn(Schedulers.computation())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() {
                                        try {
                                            ApiModule apiModule = new ApiModule(SearchPostDetailActivity.this);
                                            Response<OGMetaDataResponse> response = apiModule.getDownloadClient().getOGData(postData.getoGDataList()).execute();
                                            if (response != null && response.isSuccessful()) {
                                                Log.e("OgIconData", "successful");
                                                OGMetaDataResponse ogData = response.body();
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
                                                            Picasso.with(SearchPostDetailActivity.this).load(finalResponseToSet.getOgMeta().getOgImage().getUrl()).placeholder(R.drawable.icon_og_broken).into(mBinding.includeOG.imgOgCard);
                                                            mBinding.includeOG.layoutOgCard.setVisibility(View.VISIBLE);
                                                            mBinding.includeOG.textviewOgCard.setText(finalResponseToSet.getUrl());
                                                            mBinding.includeOG.textviewOgCardDesc.setText(finalResponseToSet.getOgMeta().getOgTitle());
                                                            mBinding.includeOG.imgOgCard.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                                    i.setData(Uri.parse(finalResponseToSet.getUrl()));
                                                                    SearchPostDetailActivity.this.startActivity(i);
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
                                                            mBinding.includeOG.imgOgCard.setImageResource(R.drawable.icon_og_broken);
                                                            mBinding.includeOG.layoutOgCard.setVisibility(View.VISIBLE);
                                                            mBinding.includeOG.textviewOgCard.setText(finalResponse.getUrl());
                                                            mBinding.includeOG.textviewOgCardDesc.setText("");
                                                        }
                                                    });
                                                }
                                                mPostDataModel.savePostData(postData);
                                            } else {
                                                Log.e("OgIconData", "err fetching getOgIconData" + response.message());
                                            }
                                        } catch (Exception t) {
                                            t.printStackTrace();
                                            Log.e("OgIconData", "err fetching getOgIconData" + t.toString());
                                        }
                                    }
                                });
                    }
                }
            }
        }


        /** setup comment section of the post*/
        setUpPostResponseLayout();

        /**Fetch user thumbnail who posted and set on mUserImageThumbnail view*/
        setUserThumbnailToView(mBinding.imageViewUserThumbnail, postData.getFrom().getId());

        checkIfUserLikedOrFavoritePostBefore();

        setBadgeToPost();

        /**fetch post type and set post type image icon*/
        if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_DISCUSSION.getPostDataType()))
            mBinding.imageBtnPostType.setImageResource(R.drawable.query_red);
        else if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_EVENT.getPostDataType()))
            mBinding.imageBtnPostType.setImageResource(R.drawable.activities_r);
        else if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_ANNOUNCEMENT.getPostDataType()))
            mBinding.imageBtnPostType.setImageResource(R.drawable.announcements_r);
        else if (postData.getPostType().equalsIgnoreCase(PostDataType.TYPE_REFERENCE_POST.getPostDataType()))
            mBinding.imageBtnPostType.setImageResource(R.drawable.reference_r);
        else mBinding.imageBtnPostType.setImageResource(R.drawable.transparent);
        TextViewMore.viewMore(postData.getPostText(), mBinding.textViewPosted, mBinding.includeViewMore.textViewMoreLess);
        mBinding.textViewPostDate.setText(DateUtils.getDateTimeStringWithDateMonthHourMinuteAMPM(DateUtils.convertrIsoDate(postData.getCreatedTime())));
        mBinding.textViewUserName.setText(postData.getFrom().getName());

        if (!PermissionPrefsCommon.getPostBadgeAssignPermission(this) || checkIfPostHaveBadgeAssigned(postDataDetail) || mAppUserModel.getObjectId().equalsIgnoreCase(postData.getFrom().getId())) {
            mBinding.buttonBadgePopup.setVisibility(View.GONE);
        } else {
            mBinding.buttonBadgePopup.setVisibility(View.VISIBLE);
        }


        mBinding.buttonBadgePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.buttonBadgePopup.setEnabled(false);
                showBadgePopUp(mBinding.textViewUserName, postData, 0);
                mBinding.buttonBadgePopup.setEnabled(true);
            }
        });


        if (mAttachmentPathList != null && mAttachmentPathList.size() > 1)
            mBinding.buttonMultipleResources.setVisibility(View.VISIBLE);
        else mBinding.buttonMultipleResources.setVisibility(View.GONE);

        mBinding.buttonMultipleResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strHeaderText = mBinding.textViewUserName.getText().toString().trim() + "'s posts- " + mBinding.textViewPostDate.getText().toString().trim();
                setUpImageGridView(mAttachmentPathList, strHeaderText);

            }
        });

        /**
         * Handle click event of cover photo of the post
         * */
        mBinding.imageViewPosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPath = view.getTag().toString();
                String strMimeType = URLConnection.guessContentTypeFromName(strPath);
                if (strMimeType.contains("image")) {
                    FullScreenImage.setUpFullImageView(SearchPostDetailActivity.this, 0, false, true,mAttachmentPathList);
                } else if (strMimeType.contains("video")) {
                    Resource item = new Resource();
                    item.setType("video");
                    item.setUrlMain(strPath);
                    SearchPostDetailActivity.this.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(SearchPostDetailActivity.this, PlayVideoFullScreenActivity.NETWORK_TYPE_LOCAL, (Resource) item));
                }

            }
        });
        /**
         * Handle click event of recommend button
         * */
        mBinding.includeResponseClicks.imageBtnLikeThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.includeResponseClicks.imageBtnLikeThumb.setEnabled(false);

                if (checkPostDataObjectIdIsExists(postDataDetail, "You can't like right now!\nPlease sync")) {
                    AnimationUtils.zoomIn(SearchPostDetailActivity.this, mBinding.includeResponseClicks.imageBtnLikeThumb);
                    PostResponse postResponse = getPostResponse(postData, PostResponseType.TYPE_RECOMMEND);
                    mLearningModel.savePostResponse(postResponse);
                    SyncServiceHelper.startUploadPostResponse(SearchPostDetailActivity.this, postResponse.getAlias());
                    postDataDetail.getPostResponses().add(postResponse);
                    likeCount++;
                    mBinding.includeResponseClicks.textViewLikeCount.setText(String.valueOf(likeCount));

                }

            }
        });
        /**
         * Handle click event of add to favorite button
         */
        mBinding.includeResponseClicks.imageBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> mPostFavoriteUserIds = postData.getPostFavoriteUserIds();

                if (!mPostFavoriteUserIds.isEmpty() && mPostFavoriteUserIds.contains(mAppUserModel.getObjectId())) {
                    mPostFavoriteUserIds.remove(mAppUserModel.getObjectId());
                    postData.setPostFavoriteUserIds(mPostFavoriteUserIds);
                    mPostDataModel.savePostData(postData);
                    AnimationUtils.zoomIn(SearchPostDetailActivity.this, mBinding.includeResponseClicks.imageBtnFavorite);
                    ToastUtils.showToastSuccess(SearchPostDetailActivity.this, "Post removed from favorites");
                    mBinding.includeResponseClicks.imageBtnFavorite.setSelected(false);
                } else {
                    if (postData != null && postData.getObjectId() != null && !postData.getObjectId().isEmpty()) {
                        List<String> favoriteUserIds = postData.getPostFavoriteUserIds();
                        favoriteUserIds.add(mAppUserModel.getObjectId());
                        postData.setPostFavoriteUserIds((ArrayList) favoriteUserIds);
                        mPostDataModel.savePostData(postData);
                        ToastUtils.showToastSuccess(SearchPostDetailActivity.this, "Post added to favorites");
                        AnimationUtils.zoomIn(SearchPostDetailActivity.this, mBinding.includeResponseClicks.imageBtnFavorite);
                        mBinding.includeResponseClicks.imageBtnFavorite.setSelected(true);
                    } else {
                        ToastUtils.showToastAlert(SearchPostDetailActivity.this, "You can't make favorite right now!\nPlease sync");
                    }
                }
            }
        });


        mBinding.includeResponseClicks.textViewLikeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((TextView) view).getText().toString().equalsIgnoreCase("0")) {
                    Intent mIntent = new Intent(SearchPostDetailActivity.this, PostLikeActivity.class);
//                    mIntent.putExtra(PostLikeActivity.SELECTED_GROUP, group);
//                    mIntent.putExtra(PostLikeActivity.SELECTED_POST, postDataDetail);
                    SearchPostDetailActivity.this.startActivity(mIntent);
                }
            }
        });
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(mBinding.layoutMain, "No internet connection!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (GeneralUtils.isNetworkAvailable(SearchPostDetailActivity.this)) {
                            syncPost();
                        } else {
                            showSnackBar();
                        }
                    }
                });

        snackbar.setActionTextColor(Color.RED);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    /**
     * sync the post if internet is available
     */
    private void syncPost() {
    }

    private void checkPostSyncStatus(boolean value) {
        mBinding.includeResponseClicks.imageBtnLikeThumb.setEnabled(value);
        mBinding.includeResponseClicks.imageBtnComment.setEnabled(value);
        mBinding.includeResponseClicks.imageBtnFavorite.setEnabled(value);
        mBinding.editTextWriteComment.setEnabled(value);
        mBinding.buttonPostComment.setEnabled(value);
    }

    /**
     * Method for get http url from post string data
     */
    private ArrayList<String> checkCommentForOGIcon(String processData) {
        String data[] = GeneralUtils.getArrayOfAllUrls(processData);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(data));
        return list;
    }

    /**
     * Method for set user thumbnail to view
     *
     * @param imageView in which view you want to set
     * @param userUid   id of user for which thumbnail will set
     */
    private void setUserThumbnailToView(ImageView imageView, final String userUid) {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BuildConfig.ViewVisibilty == false) {

                } else {
                    if (GeneralUtils.isNetworkAvailable(SearchPostDetailActivity.this)) {
                        Intent mIntent = new Intent(SearchPostDetailActivity.this, UserProfileActivity.class);
                        mIntent.putExtra(UserProfileActivity.USER_ID, userUid);
                        startActivity(mIntent);
                    } else {

                        ToastUtils.showToastAlert(SearchPostDetailActivity.this, "Please connect to internet");
                    }
                }
            }
        });

        ArrayList<GroupMember> groupMembers = group.getMembers();
        for (GroupMember groupMember : groupMembers) {
            if (groupMember.getObjectId().equalsIgnoreCase(userUid)) {

                if (groupMember.getPic().getUrl() != null && !groupMember.getPic().getUrl().isEmpty()) {
                    Picasso.with(SearchPostDetailActivity.this).load(groupMember.getPic().getUrl()).placeholder(R.drawable.icon_profile_large).resize(200, 200).centerCrop().into(imageView);
                } else if (groupMember.getPic().getThumb() != null && !groupMember.getPic().getThumb().isEmpty()) {
                    Picasso.with(SearchPostDetailActivity.this).load(groupMember.getPic().getThumb()).placeholder(R.drawable.icon_profile_large).resize(200, 200).centerCrop().into(imageView);
                } else {
                    if (!TextUtils.isEmpty(groupMember.getName())) {
                        String firstWord = groupMember.getName().substring(0, 1).toUpperCase();
                        TextDrawable textDrawable = TextDrawable.builder().buildRect(firstWord, R.color.colorPrimary);
                        imageView.setImageDrawable(textDrawable);

                    }
                }

                break;
            }
        }

    }

    /**
     * check if there is liked or favorites in the post and set liked and favorite button selected true otherwise false.
     */
    private void checkIfUserLikedOrFavoritePostBefore() {

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

            mBinding.includeResponseClicks.imageBtnLikeThumb.setEnabled(!isLikedByCurrentUser);
            mBinding.includeResponseClicks.imageBtnLikeThumb.setSelected(isLikedByCurrentUser);
            mBinding.includeResponseClicks.imageBtnComment.setSelected(isHaveComments);


        } else {
            mBinding.includeResponseClicks.imageBtnLikeThumb.setEnabled(true);
            mBinding.includeResponseClicks.imageBtnLikeThumb.setSelected(false);
            mBinding.includeResponseClicks.imageBtnComment.setEnabled(true);
            mBinding.includeResponseClicks.imageBtnComment.setSelected(false);

        }

        if (postDataDetail.getPostData() != null && postDataDetail.getPostData().getPostFavoriteUserIds().contains(mAppUserModel.getObjectId()))
            isAddToFavByUser = true;
        else
            isAddToFavByUser = false;

        mBinding.includeResponseClicks.imageBtnFavorite.setSelected(isAddToFavByUser);


    }

    /**
     * check if post have any badge assigned.
     *
     * @param postDataDetail
     * @return boolean tru if post have a badge assign
     */
    private boolean checkIfPostHaveBadgeAssigned(PostDataDetail postDataDetail) {

        boolean isPostHaveBadge = false;
        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();

        for (PostResponse postResponse : postResponses) {
            if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType())) {
                isPostHaveBadge = true;
                break;
            }
        }
        return isPostHaveBadge;
    }

    private void setBadgeToPost() {

        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();
        for (PostResponse postResponse : postResponses) {
            if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType()) && postResponse.getAssignedPostResponseId() == null) {
                LILBadges lilBadges = mBadgesModel.fetchLilBadgeFromUidSync(postResponse.getAssignedBadgeId());
                mBinding.imageViewBadge.setVisibility(View.VISIBLE);
                mBinding.imageViewBadge.setImageResource(getIResourcesIdentifier(lilBadges.getThumbnail()));
                break;
            } else {
                mBinding.imageViewBadge.setVisibility(View.GONE);
            }
        }
    }

    private int getIResourcesIdentifier(String name) {
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }

    /**
     * Method to setup view for the comment post response.
     */
    private void setUpPostResponseLayout() {

        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();
        if (postResponses != null && postResponses.size() > 0) {

            int i = 0;
            for (PostResponse postResponse : postResponses) {
                /**
                 * Check if PostResponse type is Comment  then add view to comment layout
                 */
                if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_COMMENT.getPostResponseType())) {
                    commentCount++;
                }

                /**
                 * Check if PostResponse type is Recommend  then only increase count
                 */
                else if (postResponse.getType().equalsIgnoreCase(PostResponseType.TYPE_RECOMMEND.getPostResponseType()))
                    likeCount++;

                i++;
            }
            if (likeCount == 0) {
                mBinding.includeResponseClicks.textViewLikeCount.setText("0");
            } else {
                mBinding.includeResponseClicks.textViewLikeCount.setText("" + likeCount);
            }

            if (commentCount == 0) {
                mBinding.includeResponseClicks.textViewCommentCount.setText("0");
            } else {
                mBinding.includeResponseClicks.textViewCommentCount.setText("" + commentCount);
            }


        }

    }

    /**
     * show badge grid view to assign badges for the post
     */
    private void showBadgePopUp(TextView textView, Object object, int position) {
        final Dialog mDialog = new Dialog(SearchPostDetailActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_teacher_popup_badges);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77000000")));
        TextView mUserNameTextView = (TextView) mDialog.findViewById(R.id.textview_assign_badge_user);
        RecyclerView mBadgeGridView = (RecyclerView) mDialog.findViewById(R.id.recycler_grid_badge);
        mUserNameTextView.setText("Assign badge to" + " " + textView.getText().toString().trim());

        if (object instanceof PostData) {
            setUpBadgeGridView(mBadgeGridView, mDialog, (PostData) object);
        } else if (object instanceof PostResponse) {
            setUpBadgeGridView(mBadgeGridView, mDialog, (PostResponse) object, position);
        }

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
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
     */
    private void setUpBadgeGridView(final RecyclerView mBadgeGridView, final Dialog dialog, final PostData object) {

        ArrayList<LILBadges> badges = mBadgesModel.fetchLilBadgesListSync();
        GridLayoutManager mLayoutManager = new GridLayoutManager(SearchPostDetailActivity.this, 3);
        mBadgeGridView.setLayoutManager(mLayoutManager);

        LearningNetworkDetailAdapter.BadgeAssignedInterface badgeAssignedInterface = new LearningNetworkDetailAdapter.BadgeAssignedInterface() {
            @Override
            public void onBadgeAssigned(PostResponse postResponse) {
                postDataDetail.getPostResponses().add(postResponse);
            }
        };

        PostDataBadgeAdapter mPostDataBadgeAdapter = new PostDataBadgeAdapter(SearchPostDetailActivity.this, badges, dialog, (PostData) object, badgeAssignedInterface);
        mBadgeGridView.setAdapter(mPostDataBadgeAdapter);

    }

    /**
     * setup gridview for badges
     *
     * @param mBadgeGridView
     * @param object
     */
    private void setUpBadgeGridView(final RecyclerView mBadgeGridView, final Dialog dialog, final PostResponse object, final int position) {

        ArrayList<LILBadges> badges = mBadgesModel.fetchLilBadgesListSync();
        GridLayoutManager mLayoutManager = new GridLayoutManager(SearchPostDetailActivity.this, 3);
        mBadgeGridView.setLayoutManager(mLayoutManager);
        LearningNetworkDetailAdapter.BadgeAssignedInterface badgeAssignedInterface = new LearningNetworkDetailAdapter.BadgeAssignedInterface() {
            @Override
            public void onBadgeAssigned(PostResponse postResponse) {
                commentAdapter.mPostDataDetail.getPostResponses().add(postResponse);
                commentAdapter.notifyItemChanged(position);
            }
        };

        PostResponseBadgeAdapter postResponseBadgeAdapter = new PostResponseBadgeAdapter(SearchPostDetailActivity.this, badges, dialog, (PostResponse) object, group.getObjectId(), badgeAssignedInterface);
        mBadgeGridView.setAdapter(postResponseBadgeAdapter);

    }

    private boolean checkPostDataObjectIdIsExists(PostDataDetail postDataDetail, String message) {
        if (postDataDetail.getPostData().getObjectId() == null || postDataDetail.getPostData().getObjectId().isEmpty()) {
            PostData postData1 = mPostDataModel.fetchPostDataFromAlias(postDataDetail.getPostData().getAlias());

            if (postData1 == null || postData1.getObjectId() == null || postData1.getObjectId().isEmpty()) {
                ToastUtils.showToastAlert(SearchPostDetailActivity.this, message);
                return false;
            }
            postDataDetail.setPostData(postData1);

        }
        return true;
    }

    /**
     * Method to create a PostResponse Data object for a new comment
     *
     * @param postData         Post which you are respond
     * @param postResponseType type of post response
     * @return PostResponse object
     */
    private PostResponse getPostResponse(PostData postData, PostResponseType postResponseType) {

        PostResponse postResponse = new PostResponse();
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getName());
        fromUser.setId(mAppUserModel.getObjectId());
        postResponse.setFrom(fromUser);
        postResponse.setTo(postData.getTo());
        postResponse.setObjectId(null);
        postResponse.setAlias(GeneralUtils.generateAlias("LNPostResponse", "" + mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));
        postResponse.setPostID(postData.getObjectId());
        postResponse.setResources(new ArrayList<String>());
        postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        postResponse.setText(mBinding.editTextWriteComment.getText().toString().trim());
        postResponse.setType(postResponseType.getPostResponseType());//Like,favorite etc
        postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        postResponse.setUnread(false);
        postResponse.setUpdatedTime(new Date());

        return postResponse;
    }

    /**
     * shows popup for comment list and to write comment for the post.
     */
    private void showComments() {
        final PostData postData = postDataDetail.getPostData();
        mBinding.buttonPostComment.setEnabled(false);
        mBinding.buttonPostComment.setSelected(false);

        List<PostResponse> postResponses = getCommentPostResponseList(postDataDetail);
        Collections.sort(postResponses, new SortPostResponseByDate.CreatedDateSorter());


        if (!postDataDetail.getPostResponses().isEmpty()) {
            mBinding.layoutCommentsList.setVisibility(View.VISIBLE);
            commentAdapter = new CommentAdapter(postResponses, postDataDetail);
            mBinding.recyclerViewComments.setNestedScrollingEnabled(false);
            mBinding.recyclerViewComments.setLayoutManager(new LinearLayoutManager(SearchPostDetailActivity.this, LinearLayoutManager.VERTICAL, false));
            int i = 0;
            for (i = 0; i < postResponses.size(); i++) {
                if (mPostResponseId.equals(postResponses.get(i).getObjectId())) {
                    break;
                }
            }
            mBinding.recyclerViewComments.setAdapter(commentAdapter);
            if (commentAdapter.mPostResponses.size() > i)
                mBinding.recyclerViewComments.smoothScrollToPosition(i);
        } else {
            commentAdapter = new CommentAdapter(new ArrayList<PostResponse>(), postDataDetail);
            mBinding.layoutCommentsList.setVisibility(View.GONE);
        }


        mBinding.editTextWriteComment.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mBinding.editTextWriteComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                return false;
            }
        });

        mBinding.editTextWriteComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mBinding.editTextWriteComment.getText().toString().trim().isEmpty()) {
                    mBinding.buttonPostComment.setEnabled(true);
                    mBinding.buttonPostComment.setSelected(true);
                } else {
                    mBinding.buttonPostComment.setEnabled(false);
                    mBinding.buttonPostComment.setSelected(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.includeResponseClicks.imageBtnComment.setSelected(true);
                PostResponse postResponse = getPostResponse(postData, PostResponseType.TYPE_COMMENT);
                hideSoftKeyboard(mBinding.editTextWriteComment);
                postDataDetail.getPostResponses().add(postResponse);
                mBinding.editTextWriteComment.setText("");
                mLearningModel.savePostResponse(postResponse);
                SyncServiceHelper.startUploadPostResponse(SearchPostDetailActivity.this, postResponse.getAlias());
                commentAdapter.mPostResponses.add(postResponse);
                commentAdapter.notifyDataSetChanged();
                commentCount++;
                mBinding.includeResponseClicks.textViewCommentCount.setText(String.valueOf(commentCount));

                if (postDataDetail.getPostResponses().size() == 1) {
                    showComments();
                    mBinding.layoutCommentsList.setVisibility(View.VISIBLE);
                }


            }
        });

    }

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
     * Adapter to bind comment data recycler view
     */
    public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
        List<PostResponse> mPostResponses = new ArrayList<>();
        PostDataDetail mPostDataDetail = new PostDataDetail();
        boolean shouldScroll = true;

        public CommentAdapter(List<PostResponse> postResponses, PostDataDetail postDataDetail) {
            this.mPostDataDetail = postDataDetail;
            this.mPostResponses = postResponses;
        }

        @Override
        public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_searched_users_comment_view, parent, false);
            CommentAdapter.ViewHolder mViewHolder = new CommentAdapter.ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(final CommentAdapter.ViewHolder holder, final int position) {
            final PostResponse postResponse = mPostResponses.get(position);

            if (!PermissionPrefsCommon.getPostBadgeAssignPermission(SearchPostDetailActivity.this) || checkIfPostResponseHaveBadgeAssigned(mPostDataDetail, postResponse) || mAppUserModel.getObjectId().equalsIgnoreCase(postResponse.getFrom().getId())) {
                holder.mResponseBadgePopupButton.setVisibility(View.INVISIBLE);
            } else {
                holder.mResponseBadgePopupButton.setVisibility(View.VISIBLE);
            }

            setBadgesToPostResponse(postResponse, mPostDataDetail, holder.mBadgeImageView);
            setUserThumbnailToView(holder.mUserThumbnailImageView, postResponse.getFrom().getId());

            holder.mCommentDatetimeTextView.setText(DateUtils.getDateTimeStringWithDateMonthHourMinuteAMPM(DateUtils.convertrIsoDate(postResponse.getCreatedTime())));
            holder.mCommentTextView.setText(TextViewMore.viewMore(postResponse.getText(), holder.mCommentTextView, holder.mViewMoreLessTextView));
            holder.mUserNameTextView.setText(postResponse.getFrom().getName());


            holder.mResponseBadgePopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBadgePopUp(holder.mUserNameTextView, postResponse, position);
                }
            });

            if (!PermissionPrefsCommon.getPostBadgeAssignPermission(SearchPostDetailActivity.this) || checkIfPostResponseHaveBadgeAssigned(mPostDataDetail, postResponse) || mAppUserModel.getObjectId().equalsIgnoreCase(postResponse.getFrom().getId())) {
                holder.mResponseBadgePopupButton.setVisibility(View.INVISIBLE);
            } else {
                holder.mResponseBadgePopupButton.setVisibility(View.VISIBLE);
            }
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
                    Picasso.with(SearchPostDetailActivity.this).load(urlImage).into(holder.img_og_card);
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
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(finalUrlData));
                                SearchPostDetailActivity.this.startActivity(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                holder.layout_og_card.setVisibility(View.GONE);
                if (GeneralUtils.isNetworkAvailable(SearchPostDetailActivity.this)) {
                    if (postResponse.getoGDataList().size() <= 0) {
                        postResponse.setoGDataList(checkCommentForOGIcon(postResponse.getText()));
                    }
                    if (postResponse.getoGDataList().size() > 0) {
                        Completable.complete().observeOn(Schedulers.computation())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() {
                                        try {
                                            ApiModule apiModule = new ApiModule(SearchPostDetailActivity.this);
                                            Response<OGMetaDataResponse> response1 = apiModule.getDownloadClient().getOGData(postResponse.getoGDataList()).execute();
                                            if (response1 != null && response1.isSuccessful()) {
                                                Log.e("OgIconData", "successful");
                                                OGMetaDataResponse ogData = response1.body();
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
                                                            Picasso.with(SearchPostDetailActivity.this).load(finalResponseToSet.getOgMeta().getOgImage().getUrl()).into(holder.img_og_card);
                                                            holder.layout_og_card.setVisibility(View.VISIBLE);
                                                            holder.textview_og_card.setText(finalResponseToSet.getUrl());
                                                            holder.textview_og_card_desc.setText(finalResponseToSet.getOgMeta().getOgTitle());
                                                            holder.img_og_card.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                                    i.setData(Uri.parse(finalResponseToSet.getUrl()));
                                                                    SearchPostDetailActivity.this.startActivity(i);
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
                                            } else {
                                                Log.e("OgIconData", "err fetching getOgIconData" + response1.message());
                                            }
                                        } catch (Exception t) {
                                            t.printStackTrace();
                                            Log.e("OgIconData", "err fetching getOgIconData" + t.toString());
                                        }
                                    }
                                });
                    }
                }
            }


            if (shouldScroll) {
                mBinding.scrollViewMain.fullScroll(View.FOCUS_DOWN);
            }
            if (mPostResponseId.equals(postResponse.getObjectId())) {
                holder.mRootView.requestFocus();
                shouldScroll = false;
                AnimationUtils.blink(SearchPostDetailActivity.this, holder.mRootView);

            }


        }

        @Override
        public int getItemCount() {
            return mPostResponses.size();
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

    private void setBadgesToPostResponse(PostResponse postResponse, PostDataDetail postDataDetail, ImageView mBadgeImageView) {
        ArrayList<PostResponse> postResponses = postDataDetail.getPostResponses();
        for (PostResponse response : postResponses) {
            if (response.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType())) {
                if (response.getAssignedPostResponseId() != null && response.getAssignedPostResponseId().equalsIgnoreCase(postResponse.getObjectId())) {
                    LILBadges lilBadges = mBadgesModel.fetchLilBadgeFromUidSync(response.getAssignedBadgeId());
                    mBadgeImageView.setImageResource(getIResourcesIdentifier(lilBadges.getThumbnail()));
                    mBadgeImageView.setVisibility(View.VISIBLE);
                    break;
                } else {
                    mBadgeImageView.setVisibility(View.GONE);
                }
            }
        }
    }

    public void hideSoftKeyboard(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

    }

    /**
     * setup grid view for resources of post like images for now
     */
    public void setUpImageGridView(ArrayList<Resource> mAttachResourcesList, String strHeaderText) {

        final Dialog mDialog = new Dialog(SearchPostDetailActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_image_grid_view);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        RecyclerView mImageRecyclerView = (RecyclerView) mDialog.findViewById(R.id.recyclerview_gridview);
        ImageButton mCloseDialogButton = (ImageButton) mDialog.findViewById(R.id.button_close);
        TextView mHeaderTextView = (TextView) mDialog.findViewById(R.id.textview_grid_dialog_header);
        mHeaderTextView.setText(strHeaderText);
        mCloseDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        setUpImageGridViewItem(mImageRecyclerView, mAttachResourcesList);

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.show();

    }

    /**
     * set up recycler view item contain post images
     *
     * @param mImageRecyclerView
     * @param mAttachResourcesList
     */
    private void setUpImageGridViewItem(RecyclerView mImageRecyclerView, ArrayList<Resource> mAttachResourcesList) {

        RecyclerViewImageAdapter mRecyclerViewImageAdapter;
        if (getResources().getBoolean(R.bool.isTablet)) {
            mLayoutManager = new GridLayoutManager(SearchPostDetailActivity.this, 3);
        } else {
            mLayoutManager = new GridLayoutManager(SearchPostDetailActivity.this, 2);
        }

        mImageRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewImageAdapter = new RecyclerViewImageAdapter(SearchPostDetailActivity.this, mAttachResourcesList);
        mImageRecyclerView.setAdapter(mRecyclerViewImageAdapter);

    }

    /**
     * setup view for showing images in full view
     * user can slide images.
     *
     * @param position
     * @param isComesFromGrid
     * @param mAttachmentPathList
     */
    public void setUpFullImageView(int position, boolean isComesFromGrid, ArrayList<Resource> mAttachmentPathList) {
        final Dialog mDialog = new Dialog(SearchPostDetailActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_gallery_view);
        mDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77000000")));
        mImageViewPager = (ImageViewPager) mDialog.findViewById(R.id.viewpager_images);
        ImageButton mCloseButton = (ImageButton) mDialog.findViewById(R.id.button_back);
        final ImageButton mPreviousButton = (ImageButton) mDialog.findViewById(R.id.button_previous);
        final ImageButton mNextButton = (ImageButton) mDialog.findViewById(R.id.button_next);

        setUpFullImageViewItem(mImageViewPager, mAttachmentPathList);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageViewPager.setCurrentItem(mImageViewPager.getCurrentItem() - 1, true);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageViewPager.setCurrentItem(mImageViewPager.getCurrentItem() + 1, true);
            }
        });

        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    mPreviousButton.setVisibility(View.INVISIBLE);

                } else {
                    mPreviousButton.setVisibility(View.VISIBLE);
                }

                if (position == (mImageViewPager.getAdapter().getCount() - 1)) {
                    mNextButton.setVisibility(View.INVISIBLE);

                } else {
                    mNextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialog.show();

        if (isComesFromGrid) {
            if (position == 0) mPreviousButton.setVisibility(View.INVISIBLE);

            if (mImageViewPager.getAdapter().getCount() <= 1)
                mNextButton.setVisibility(View.INVISIBLE);

            mImageViewPager.setCurrentItem(position, true);
        } else {

            if (position == 0) mPreviousButton.setVisibility(View.INVISIBLE);

            if (mImageViewPager.getAdapter().getCount() <= 1)
                mNextButton.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * set up viewpager item for showing images
     *
     * @param mImageViewPager
     * @param mAttachmentPathList
     */
    private void setUpFullImageViewItem(ViewPager mImageViewPager, ArrayList<Resource> mAttachmentPathList) {
        ViewPagerImageAdapter mViewPagerImageAdapter;
        mViewPagerImageAdapter = new ViewPagerImageAdapter(SearchPostDetailActivity.this, mAttachmentPathList);
        mImageViewPager.setAdapter(mViewPagerImageAdapter);
        mImageViewPager.setOffscreenPageLimit(2);
    }

}
