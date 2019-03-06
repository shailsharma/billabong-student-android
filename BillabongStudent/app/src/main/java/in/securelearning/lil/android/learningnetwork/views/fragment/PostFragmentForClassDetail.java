package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.adapter.LearningNetworkDetailAdapter;
import in.securelearning.lil.android.learningnetwork.comparator.SortPostByCreatedTime;
import in.securelearning.lil.android.learningnetwork.events.LoadGroupListEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostCreatedEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostReceivedEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostLikedFromFavEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostListEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostRemoveEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostRemoveToFavoriteEvent;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostActivity;
import in.securelearning.lil.android.syncadapter.utils.SoundUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class PostFragmentForClassDetail extends BaseLNFragment {

    @Inject
    RxBus mRxBus;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    PostDataLearningModel mPostDataLearningModel;

    @Inject
    GroupModel mGroupModel;

    private String mBaseFolder, mLearningNetworkFolder;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private String mSelectedGroupObjectId;
    private Disposable mSubscription;
    private RecyclerView mDetailRecyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar mProgressBar;
    private static ArrayList<Group> mGroupsList = new ArrayList<>();
    private Context mContext;
    private LearningNetworkDetailAdapter mLearningNetworkDetailAdapter;
    private int mColumnCount;
    private View mRootView;
    public LinearLayout mNoPostLayout;
    private CardView mFloatingCountView;
    private TextView mNewPostCountTextView;
    private int mNewPostCount = 0;
    public FloatingActionButton mAddPostButton;
    private long lasAddPostBtnClickTime;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostFragmentForClassDetail() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PostFragmentForClassDetail newInstance(int columnCount, String groupId) {
        PostFragmentForClassDetail fragment = new PostFragmentForClassDetail();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_GROUP_OBJECT_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSelectedGroupObjectId = getArguments().getString(ARG_GROUP_OBJECT_ID);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_post_for_class_detail, container, false);
        mBaseFolder = mContext.getFilesDir().getAbsolutePath();
        initializeViews();
        initializeUiAndClickListeners();
        initializeResourceFolders("LearningNetwork");
        setupSubscription();
        getData();
        return mRootView;
    }

    public void getData() {

        if (mGroupsList != null && mGroupsList.isEmpty())
           // mPostDataLearningModel.fetchGroupListByUSerUId(mAppUserModel.getObjectId());

        mPostDataLearningModel.getFilterPostByGroupIdNdAttribute(mSelectedGroupObjectId, PostDataType.TYPE_ALL.getPostDataType());

    }

    /**
     * Initialize all UI layouts elements.
     */
    private void initializeViews() {
        mDetailRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_detail);
        mNoPostLayout = (LinearLayout) mRootView.findViewById(R.id.layout_no_post);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBarPost);
        mFloatingCountView = (CardView) mRootView.findViewById(R.id.cardViewFloatingCount);
        mNewPostCountTextView = (TextView) mRootView.findViewById(R.id.textViewNewPostCount);
        mAddPostButton = (FloatingActionButton) mRootView.findViewById(R.id.add_post);

        mAddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - lasAddPostBtnClickTime < 2000) {
                    return;
                }
                lasAddPostBtnClickTime = SystemClock.elapsedRealtime();

                Intent mIntent = new Intent(getContext(), CreatePostActivity.class);
                mIntent.putExtra("group_object_id", mSelectedGroupObjectId);
                startActivity(mIntent);
            }
        });

        mDetailRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int isFirstItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (isFirstItem == 0) {
                    mFloatingCountView.performClick();

                }

                if (dy > 0) {
                    mAddPostButton.hide();
                } else if (dy < 0) {
                    mAddPostButton.show();
                }
            }
        });


        setupRecyclerViewForPost(new ArrayList<PostDataDetail>(), true);
    }

    private void initializeUiAndClickListeners() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSubscription != null) mSubscription.dispose();
        if (mLearningNetworkDetailAdapter != null) {
            mLearningNetworkDetailAdapter.dispose();
        }

    }

    /**
     * set up Disposable to listen to RxBus
     */
    private void setupSubscription() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object eventObject) {
                if (eventObject instanceof LoadPostListEvent) {
                    setupRecyclerViewForPost(((LoadPostListEvent) eventObject).getPostDatas(), false);
                } else if (eventObject instanceof LoadGroupListEvent) {
                    mGroupsList = ((LoadGroupListEvent) eventObject).getGropList();

                } else if (eventObject instanceof LoadPostRemoveToFavoriteEvent) {
                    if (mLearningNetworkDetailAdapter != null) {

                        mLearningNetworkDetailAdapter.itemRefreshed(((LoadPostRemoveToFavoriteEvent) eventObject).getPostDataDetail());
                    }
                } else if (eventObject instanceof LoadPostRemoveEvent) {
                    if (mLearningNetworkDetailAdapter != null) {

                        mLearningNetworkDetailAdapter.removeItem(((LoadPostRemoveEvent) eventObject).getPostDataDetail().getPostData().getAlias());
                    }
                } else if (eventObject instanceof LoadNewPostCreatedEvent) {
//                    if (!TextUtils.isEmpty(mSelectedGroupObjectId) && mSelectedGroupObjectId.equals(((LoadNewPostCreatedEvent) eventObject).getPostDataDetail().getPostData().getTo().getId())) {
//                        if (!((LoadNewPostCreatedEvent) eventObject).getPostDataDetail().getPostData().getPostType().equals(PostDataType.TYPE_TRACKING.toString()) && ((LoadNewPostCreatedEvent) eventObject).isCreated() && mLearningNetworkDetailAdapter != null) {
//                            mLearningNetworkDetailAdapter.addItem(((LoadNewPostCreatedEvent) eventObject).getPostDataDetail(), true);
//                            mDetailRecyclerView.smoothScrollToPosition(0);
//                        }
//
//                        if (mLearningNetworkDetailAdapter != null && mLearningNetworkDetailAdapter.getItemCount() > 0) {
//                            mNoPostLayout.setVisibility(View.GONE);
//                            if (mDetailRecyclerView.getVisibility() != View.VISIBLE)
//                                mDetailRecyclerView.setVisibility(View.VISIBLE);
//                        } else
//                            mNoPostLayout.setVisibility(View.VISIBLE);
//                    }
                } else if (eventObject instanceof LoadPostLikedFromFavEvent) {
                    if (mLearningNetworkDetailAdapter != null) {
                        mLearningNetworkDetailAdapter.notifyDataSetChanged();
                    }
                } else if (eventObject instanceof LoadNewPostReceivedEvent) {
//                    if (!TextUtils.isEmpty(mSelectedGroupObjectId) && mSelectedGroupObjectId.equals(((LoadNewPostReceivedEvent) eventObject).getPostData().getTo().getId())) {
//                        if (!((LoadNewPostReceivedEvent) eventObject).getPostData().getPostType().equals(PostDataType.TYPE_TRACKING.toString()) &&
//                                mLearningNetworkDetailAdapter != null) {
//
//                            PostDataDetail postDataDetail = new PostDataDetail();
//                            postDataDetail.setPostData(((LoadNewPostReceivedEvent) eventObject).getPostData());
//
//                            if (mLearningNetworkDetailAdapter.addItem(postDataDetail, true)) {
//                                mNewPostCount++;
//                                AnimationUtils.zoomInFast(mContext, mFloatingCountView);
//                                mFloatingCountView.setVisibility(View.VISIBLE);
//                                SoundUtils.playSound(mContext, SoundUtils.LEARNING_NETWORK_NEW_POST);
//                                if (mNewPostCount == 1) {
//                                    mNewPostCountTextView.setText(String.valueOf(mNewPostCount) + " NEW POST");
//                                } else if (mNewPostCount > 1) {
//                                    mNewPostCountTextView.setText(String.valueOf(mNewPostCount) + " NEW POSTS");
//                                }
//                            }
//
//                            mFloatingCountView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    mLearningNetworkDetailAdapter.notifyDataSetChanged();
//                                    mFloatingCountView.setVisibility(View.GONE);
//                                    mNewPostCount = 0;
//                                    mDetailRecyclerView.smoothScrollToPosition(0);
//
//                                }
//                            });
//
//                        }
//
//                        if (mLearningNetworkDetailAdapter != null && mLearningNetworkDetailAdapter.getItemCount() > 0) {
//                            mNoPostLayout.setVisibility(View.GONE);
//                            if (mDetailRecyclerView.getVisibility() != View.VISIBLE)
//                                mDetailRecyclerView.setVisibility(View.VISIBLE);
//                        } else
//                            mNoPostLayout.setVisibility(View.VISIBLE);
//
//                    }
                }

            }
        });
    }

    /**
     * Method to setup recycler view to show the posted items of a group.
     *
     * @param postDatas
     * @param isFirstTime
     */
    private void setupRecyclerViewForPost(ArrayList<PostDataDetail> postDatas, boolean isFirstTime) {
        ((DefaultItemAnimator) mDetailRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        if (postDatas == null || postDatas.isEmpty()) {
            if (!isFirstTime) {
                mNoPostLayout.setVisibility(View.VISIBLE);
                mDetailRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
            AnimationUtils.fadeIn(mContext, mNoPostLayout);
        } else {
            mNoPostLayout.setVisibility(View.GONE);
            mDetailRecyclerView.setVisibility(View.VISIBLE);
            Collections.sort(postDatas, new SortPostByCreatedTime.LastConversationTimeSorter());


        }

        Group group = mGroupModel.fetchGroupFromUUidSync(mSelectedGroupObjectId);
        mLearningNetworkDetailAdapter = new LearningNetworkDetailAdapter(mContext, PostFragmentForClassDetail.this, postDatas, group);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mDetailRecyclerView.setLayoutManager(layoutManager);
        mDetailRecyclerView.setAdapter(mLearningNetworkDetailAdapter);

        mProgressBar.setVisibility(View.GONE);

    }


    /**
     * method to hide soft keyboard
     *
     * @param mPostEditText
     */
    public void hideSoftKeyboard(EditText mPostEditText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPostEditText.getWindowToken(), 0);

    }


    /**
     * initialize the folders for resources
     *
     * @param parentFolderAbsolutePath
     */
    private void initializeResourceFolders(String parentFolderAbsolutePath) {
        mLearningNetworkFolder = parentFolderAbsolutePath + File.separator + "learning network resources";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


}
