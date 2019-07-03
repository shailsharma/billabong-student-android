package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutFragmentPostResponseBinding;
import in.securelearning.lil.android.app.databinding.LayoutItemPostResponseBinding;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.OGMetaDataResponse;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.Result;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.dataobjects.TimeUtils;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostResponseReceivedEvent;
import in.securelearning.lil.android.learningnetwork.events.NewPostResponseAdded;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 04-Aug-17.
 */

public class PostResponseListFragment extends Fragment {

    @Inject
    GroupModel mGroupModel;

    @Inject
    PostDataLearningModel mPostDataLearningModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    RxBus mRxBus;

    @Inject
    OgUtils mOgUtils;

    private static String ARG_COLUMN_COUNT = "column-count";
    private static String GROUP_ID = "groupId";
    private static String POST_ID = "postId";
    private static String POST_ALIAS = "postAlias";
    private int mColumnCount = 1;
    private String mGroupId = "";
    private String mPostAlias = "";
    private String mPostId = "";
    private String myUserId = "";
    private int mSkip = 0;
    private int mLimit = 30;
    private LayoutFragmentPostResponseBinding mBinding;
    private PostResponseAdapter mPostResponseAdapter;
    private Group mGroup;
    private PostData mPostData;
    private Disposable mSubscription;
    private int mNewPostResponseReceivedCount = 0;
    private LinearLayoutManager finalLayoutManager;
    private String mFirstUrl = "";

    public static PostResponseListFragment newInstance(int columnCount, String groupId, String postId, String postAlias) {
        PostResponseListFragment fragment = new PostResponseListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(GROUP_ID, groupId);
        args.putString(POST_ID, postId);
        args.putString(POST_ALIAS, postAlias);
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
        myUserId = mAppUserModel.getObjectId();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mGroupId = getArguments().getString(GROUP_ID);
            mPostId = getArguments().getString(POST_ID);
            mPostAlias = getArguments().getString(POST_ALIAS);
            mGroup = mGroupModel.getGroupFromUidSync(mGroupId);
            //getPostByAlias(mPostAlias);
            getPostById(mPostId);
        }

    }

    private void getPostByAlias(String postAlias) {
        mPostDataLearningModel.getPostDataByAlias(postAlias).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<PostData>() {
            @Override
            public void accept(final PostData postData) throws Exception {
                mPostData = postData;
            }
        });
    }

    private void getPostById(String id) {
        mPostData = mPostDataLearningModel.getPostDataByObjectId(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_fragment_post_response, container, false);
        initializeViews();
        setDefault();
        listenRxBusEvent();
        getData(mPostId, mSkip, mLimit);
        return mBinding.getRoot();
    }

    private void getData(String postId, int skip, final int limit) {
        mPostDataLearningModel.getPostResponseListForPost(postId, PostResponseType.TYPE_COMMENT.getPostResponseType(), skip, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PostResponse>>() {
            @Override
            public void accept(ArrayList<PostResponse> postResponses) throws Exception {
                mSkip += postResponses.size();
                noResultFound(mSkip);
                if (postResponses.size() < limit) {
                    mBinding.recyclerView.removeOnScrollListener(null);
                }
                mPostResponseAdapter.addItem(postResponses);

            }

        });
    }

    private void listenRxBusEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) throws Exception {
                if (event instanceof NewPostResponseAdded) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    setDefault();
                                    getData(mPostId, mSkip, mLimit);

                                }
                            });

                } else if (event instanceof LoadNewPostResponseReceivedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {

                                    if (mPostId.equals(((LoadNewPostResponseReceivedEvent) event).getPostResponse().getPostID())) {
                                        if (finalLayoutManager != null) {
                                            int isFirstItem = finalLayoutManager.findFirstCompletelyVisibleItemPosition();
                                            if (isFirstItem == 0) {
                                                mPostDataLearningModel.clearLearningNetworkGroupNotification(getActivity(), mGroupId.hashCode());
                                                setDefault();
                                                getData(mPostId, mSkip, mLimit);
                                            } else {
                                                mNewPostResponseReceivedCount++;
                                                mBinding.textViewNewResponseCount.setText(String.valueOf(mNewPostResponseReceivedCount));
                                                if (mBinding.cardViewFloatingCount.getVisibility() == View.GONE) {
                                                    mBinding.cardViewFloatingCount.setVisibility(View.VISIBLE);
                                                    AnimationUtils.zoomInFast(getActivity(), mBinding.cardViewFloatingCount);
                                                }
                                            }
                                        }

                                    }

                                }
                            });

                }
            }
        });
    }

    private void setDefault() {
        mNewPostResponseReceivedCount = 0;
        mSkip = 0;
        initializeRecyclerView(new ArrayList<PostResponse>());
    }

    private void initializeRecyclerView(ArrayList<PostResponse> postResponses) {
        LinearLayoutManager layoutManager = null;
        mBinding.recyclerView.getItemAnimator().setChangeDuration(0);
        ((DefaultItemAnimator) mBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        if (getActivity() != null) {

            layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.setReverseLayout(true);
            mBinding.recyclerView.setLayoutManager(layoutManager);
            mPostResponseAdapter = new PostResponseAdapter(postResponses);
            mPostResponseAdapter.setHasStableIds(true);
            mBinding.recyclerView.setAdapter(mPostResponseAdapter);

        }

        if (layoutManager != null) {
            finalLayoutManager = layoutManager;
            mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    int isFirstItem = finalLayoutManager.findFirstCompletelyVisibleItemPosition();
//                    if (isFirstItem == 0) {
//                        mBinding.cardViewFloatingCount.performClick();
//                    }

                    if (dy < 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mSkip - 1) {

                            getData(mPostId, mSkip, mLimit);

                        }
                    }

                }

            });
        }
    }

    private void initializeViews() {

        mBinding.editTextWriteComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.editTextWriteComment.getText().toString().trim().length() == 1) {
                    mBinding.buttonPostComment.setSelected(true);
                    mBinding.buttonPostComment.setEnabled(true);
                } else if (mBinding.editTextWriteComment.getText().toString().trim().length() == 0) {
                    mBinding.buttonPostComment.setSelected(false);
                    mBinding.buttonPostComment.setEnabled(false);
                } else {
                    mBinding.buttonPostComment.setSelected(true);
                    mBinding.buttonPostComment.setEnabled(true);
                }

                String typed = mBinding.editTextWriteComment.getText().toString().trim();
                ArrayList<String> urlArrayList = mOgUtils.extractUrls(typed);

                if (urlArrayList.size() > 0) {
                    if (!TextUtils.isEmpty(mFirstUrl)) {
                        if (!mFirstUrl.equals(urlArrayList.get(0))) {
                            mFirstUrl = urlArrayList.get(0);
                            //   mBinding.textViewUrl.setText(TextUtils.join("\n", urlArrayList));
                            fetchLiveOgFromServer(urlArrayList);
                        }
                    } else {
                        mFirstUrl = urlArrayList.get(0);
                        //  mBinding.textViewUrl.setText(TextUtils.join("\n", urlArrayList));
                        fetchLiveOgFromServer(urlArrayList);
                    }

                } else {
                    mBinding.includeOgLayout.layoutOgCard.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mBinding.editTextWriteComment.getText().toString().trim())) {
                    mBinding.editTextWriteComment.clearComposingText();
                    mPostDataLearningModel.setPostResponseForComment(mPostData, Html.toHtml(mBinding.editTextWriteComment.getText()).toString().trim());
                    mBinding.editTextWriteComment.clearComposingText();
                    mBinding.editTextWriteComment.setText("");
                    SoundUtils.playSound(getContext(), SoundUtils.LEARNING_NETWORK_POST_RESPONSE_ADDED);
                }

            }
        });

        mBinding.cardViewFloatingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPostDataLearningModel.clearLearningNetworkGroupNotification(getActivity(), mGroupId.hashCode());
                AnimationUtils.zoomOutFast(getActivity(), mBinding.cardViewFloatingCount);
                mBinding.cardViewFloatingCount.setVisibility(View.GONE);
                setDefault();
                getData(mPostId, mSkip, mLimit);
            }
        });

    }

    private void fetchLiveOgFromServer(ArrayList<String> urlArrayList) {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            try {
                mOgUtils.getOgDataFromServer(urlArrayList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OGMetaDataResponse>() {
                    @Override
                    public void accept(OGMetaDataResponse ogData) throws Exception {
                        Result response = new Result();
                        Result responseToSet = null;
                        for (int i = 0; i < ogData.getResults().size(); i++) {
                            response = ogData.getResults().get(i);
                            if (response.getOg().equals(true)) {
                                if (responseToSet == null) {
                                    responseToSet = response;
                                }
                            }
                        }
                        if (responseToSet != null) {
                            final Result finalResponseToSet = responseToSet;
                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() {

                                    showLiveOgView(finalResponseToSet);


                                }
                            });
                        } else if (response != null) {

                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() {
                                    mBinding.includeOgLayout.layoutOgCard.setVisibility(View.GONE);

                                }
                            });
                        }

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

    private void showLiveOgView(final Result finalResponseToSet) {
        mBinding.includeOgLayout.layoutOgCard.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getContext(), mBinding.includeOgLayout.layoutOgCard);
        Picasso.with(getContext()).load(finalResponseToSet.getOgMeta().getOgImage().getUrl()).placeholder(R.drawable.icon_og_broken).resize(90, 90).centerCrop().into(mBinding.includeOgLayout.imageViewOg);
        mBinding.includeOgLayout.textViewOgTitle.setText(finalResponseToSet.getOgMeta().getOgTitle());
        mBinding.includeOgLayout.textViewOgUrl.setText(finalResponseToSet.getUrl());
        mBinding.includeOgLayout.textViewOgDescription.setText(finalResponseToSet.getOgMeta().getOgTitle());
        mBinding.includeOgLayout.layoutOgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomChromeTabHelper.loadCustomDataUsingColorResource(getContext(), finalResponseToSet.getUrl(), R.color.colorLearningNetworkPrimary);
            }
        });
    }

    private void noResultFound(int size) {
        if (size > 0) {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
        }

    }

    private class PostResponseAdapter extends RecyclerView.Adapter<PostResponseAdapter.ViewHolder> {
        private ArrayList<PostResponse> mPostResponses = new ArrayList<>();

        public PostResponseAdapter(ArrayList<PostResponse> postResponses) {
            this.mPostResponses = postResponses;
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }

        @Override
        public PostResponseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemPostResponseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_post_response, parent, false);
            return new PostResponseAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(PostResponseAdapter.ViewHolder holder, int position) {
            final PostResponse postResponse = mPostResponses.get(position);
            setDateSeparator(holder.mBinding, postResponse, position);
            setResponseView(postResponse, holder.mBinding, position);
            holder.mBinding.textViewCommentMe.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TextViewMore.copyTextToClipboard(getContext(), postResponse.getText());
                    return false;
                }
            });

            holder.mBinding.textViewCommentOther.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TextViewMore.copyTextToClipboard(getContext(), postResponse.getText());
                    return false;
                }
            });

        }

        private void setViewForUserIfContinueComment(LayoutItemPostResponseBinding binding, int position) {
            if (position == mPostResponses.size() - 1) {
                binding.layoutOther.setPadding(8, 16, 48, 16);
                binding.imageViewOther.setVisibility(View.VISIBLE);
                binding.textViewNameOther.setVisibility(View.VISIBLE);
                binding.textViewSeparatorOther.setVisibility(View.VISIBLE);
            } else {
                int namePosition = position + 1;
                if (mPostResponses.get(namePosition).getFrom().getId().equals(mPostResponses.get(position).getFrom().getId())) {
                    binding.layoutOther.setPadding(8, 4, 48, 4);
                    binding.imageViewOther.setVisibility(View.INVISIBLE);
                    binding.textViewNameOther.setVisibility(View.GONE);
                    binding.textViewSeparatorOther.setVisibility(View.GONE);
                } else {
                    binding.layoutOther.setPadding(8, 16, 48, 16);
                    binding.imageViewOther.setVisibility(View.VISIBLE);
                    binding.textViewNameOther.setVisibility(View.VISIBLE);
                    binding.textViewSeparatorOther.setVisibility(View.VISIBLE);
                }
            }

        }

        private void setDateSeparator(LayoutItemPostResponseBinding binding, PostResponse postResponse, int position) {
            int datePosition;
            if (position == mPostResponses.size() - 1) {
                datePosition = position;

            } else {
                datePosition = position + 1;
            }

            Calendar calendarOld = Calendar.getInstance();
            calendarOld.setTime(DateUtils.convertrIsoDate(mPostResponses.get(datePosition).getCreatedTime()));
            final int dateOld = calendarOld.get(Calendar.DATE);

            Calendar calendarNew = Calendar.getInstance();
            calendarNew.setTime(DateUtils.convertrIsoDate(postResponse.getCreatedTime()));
            final int dateNew = calendarNew.get(Calendar.DATE);

            if (position != datePosition && dateNew == dateOld) {
                binding.cardViewDateSeparator.setVisibility(View.GONE);
            } else {
                binding.cardViewDateSeparator.setVisibility(View.VISIBLE);
                binding.textViewDateSeparator.setText(TimeUtils.getRealDateString(DateUtils.convertrIsoDate(postResponse.getCreatedTime())));
            }
        }

        @Override
        public int getItemCount() {
            return mPostResponses.size();
        }

        @Override
        public long getItemId(int position) {
            return mPostResponses.get(position).getAlias().hashCode();
        }

        private void setResponseView(PostResponse postResponse, LayoutItemPostResponseBinding binding, int position) {
            if (postResponse.getFrom().getId().equals(myUserId)) {
                setOgCard(postResponse, binding, true);
                binding.layoutMe.setVisibility(View.VISIBLE);
                binding.layoutOther.setVisibility(View.GONE);
                binding.textViewTimeMe.setText(TimeUtils.getTimeString(DateUtils.convertrIsoDate(postResponse.getCreatedTime())));
                TextViewMore.setPostText(getContext(), postResponse.getText(), binding.textViewCommentMe, binding.includeTextViewMoreLessMe.textViewMoreLess);
            } else {
                setOgCard(postResponse, binding, false);
                binding.layoutMe.setVisibility(View.GONE);
                binding.layoutOther.setVisibility(View.VISIBLE);
                binding.textViewNameOther.setText(postResponse.getFrom().getName());
                setUserThumbnail(postResponse.getFrom().getId(), binding.imageViewOther);
                binding.textViewTimeOther.setText(TimeUtils.getTimeString(DateUtils.convertrIsoDate(postResponse.getCreatedTime())));
                TextViewMore.setPostText(getContext(), postResponse.getText(), binding.textViewCommentOther, binding.includeTextViewMoreLessOther.textViewMoreLess);
                setViewForUserIfContinueComment(binding, position);
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
                        Picasso.with(getContext()).load(groupMember.getPic().getLocalUrl()).transform(new CircleTransform()).into(imageView);
                    } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getUrl())) {
                        Picasso.with(getContext()).load(groupMember.getPic().getUrl()).transform(new CircleTransform()).into(imageView);
                    } else if (groupMember.getPic() != null && !TextUtils.isEmpty(groupMember.getPic().getThumb())) {
                        Picasso.with(getContext()).load(groupMember.getPic().getThumb()).transform(new CircleTransform()).into(imageView);
                    } else {
                        String firstWord = groupMember.getName().substring(0, 1).toUpperCase();
                        TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimaryLN);
                        imageView.setImageDrawable(textDrawable);
                    }

                    break;
                }
            }
        }

        private void setOgCard(final PostResponse postResponse, final LayoutItemPostResponseBinding binding, final boolean isMe) {
            if (!TextUtils.isEmpty(postResponse.getObjectId())) {
                if (postResponse.getLocalOgDataList().size() > 0) {
                    if (isMe) {
                        setOgViewForMe(binding, postResponse.getPositiveResult());
                    } else {
                        setOgViewForOther(binding, postResponse.getPositiveResult());
                    }
                } else {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        if (postResponse.getoGDataList().size() > 0) {
                            try {
                                mOgUtils.getOgDataFromServer(postResponse.getoGDataList()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OGMetaDataResponse>() {
                                    @Override
                                    public void accept(OGMetaDataResponse ogData) throws Exception {
                                        Result response = new Result();
                                        Result responseToSet = null;
                                        for (int i = 0; i < ogData.getResults().size(); i++) {
                                            response = ogData.getResults().get(i);
                                            postResponse.getLocalOgDataList().put(response.getUrl(), response); // Here we are making Map for url as key and image as value
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

                                                    if (isMe) {
                                                        setOgViewForMe(binding, finalResponseToSet);
                                                    } else {
                                                        setOgViewForOther(binding, finalResponseToSet);
                                                    }
                                                }
                                            });
                                        } else if (response != null) {
                                            postResponse.setPositiveResult(response);
                                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                                @Override
                                                public void run() {
                                                    if (isMe) {
                                                        binding.includeOgLayoutMe.layoutOgCard.setVisibility(View.GONE);

                                                    } else {
                                                        binding.includeOgLayoutOther.layoutOgCard.setVisibility(View.GONE);

                                                    }
                                                }
                                            });
                                        }
                                        mPostDataLearningModel.savePostResponseForOg(postResponse);
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

        private void setOgViewForOther(LayoutItemPostResponseBinding binding, final Result positiveResult) {
            if (positiveResult.getOgMeta() != null) {
                binding.includeOgLayoutOther.layoutOgCard.setVisibility(View.VISIBLE);
                if (positiveResult.getOgMeta().getOgImage() != null && !TextUtils.isEmpty(positiveResult.getOgMeta().getOgImage().getUrl())) {
                    Picasso.with(getContext()).load(positiveResult.getOgMeta().getOgImage().getUrl()).placeholder(R.drawable.icon_og_broken).resize(60, 60).centerCrop().into(binding.includeOgLayoutOther.imageViewOg);
                } else {
                    Picasso.with(getContext()).load(R.drawable.icon_og_broken).resize(60, 60).centerCrop().into(binding.includeOgLayoutOther.imageViewOg);
                }
                binding.includeOgLayoutOther.textViewOgTitle.setText(positiveResult.getOgMeta().getOgTitle());
                binding.includeOgLayoutOther.textViewOgUrl.setText(positiveResult.getUrl());
                binding.includeOgLayoutOther.textViewOgDescription.setText(positiveResult.getOgMeta().getOgDescription());
                binding.includeOgLayoutOther.layoutOgCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CustomChromeTabHelper.loadCustomDataUsingColorResource(getContext(), positiveResult.getUrl(), R.color.colorLearningNetworkPrimary);
                    }
                });
            }
        }

        private void setOgViewForMe(LayoutItemPostResponseBinding binding, final Result positiveResult) {
            if (positiveResult.getOgMeta() != null) {
                binding.includeOgLayoutMe.layoutOgCard.setVisibility(View.VISIBLE);
                if (positiveResult.getOgMeta().getOgImage() != null && !TextUtils.isEmpty(positiveResult.getOgMeta().getOgImage().getUrl())) {
                    Picasso.with(getContext()).load(positiveResult.getOgMeta().getOgImage().getUrl()).placeholder(R.drawable.icon_og_broken).resize(60, 60).centerCrop().into(binding.includeOgLayoutMe.imageViewOg);
                } else {
                    Picasso.with(getContext()).load(R.drawable.icon_og_broken).resize(60, 60).centerCrop().into(binding.includeOgLayoutMe.imageViewOg);
                }
                binding.includeOgLayoutMe.textViewOgTitle.setText(positiveResult.getOgMeta().getOgTitle());
                binding.includeOgLayoutMe.textViewOgUrl.setText(positiveResult.getUrl());
                binding.includeOgLayoutMe.textViewOgDescription.setText(positiveResult.getOgMeta().getOgDescription());
                binding.includeOgLayoutMe.layoutOgCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CustomChromeTabHelper.loadCustomDataUsingColorResource(getContext(), positiveResult.getUrl(), R.color.colorLearningNetworkPrimary);
                    }
                });
            }
        }

        public void addItem(ArrayList<PostResponse> postResponses) {
            if (mPostResponses != null) {
                mPostResponses.addAll(postResponses);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (mPostResponses != null) {
                mPostResponses.clear();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutItemPostResponseBinding mBinding;

            public ViewHolder(LayoutItemPostResponseBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }
}
