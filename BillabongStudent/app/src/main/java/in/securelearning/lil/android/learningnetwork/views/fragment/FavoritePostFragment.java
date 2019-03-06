package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.adapter.LearningNetworkDetailAdapter;
import in.securelearning.lil.android.learningnetwork.comparator.SortPostByCreatedTime;
import in.securelearning.lil.android.learningnetwork.events.LoadFavoritePostListEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostAddToFavoriteEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostLikedEvent;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class FavoritePostFragment extends BaseLNFragment {

    @Inject
    RxBus mRxBus;

    @Inject
    PostDataLearningModel mPostDataLearningModel;

    @Inject
    GroupModel mGroupModel;

    @Inject
    AppUserModel mAppUserModel;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_GROUP_OBJECT_ID = "group_object_id";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private String mSelectedGroupObjectId;
    private Disposable mSubscription;
    private RecyclerView mDetailRecyclerView;
    private ProgressBar mProgressBar;
    public LinearLayout mNoFavoriteLayout;
    private View mRootView;
    private Context mContext;
    private LearningNetworkDetailAdapter mLearningNetworkDetailAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoritePostFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FavoritePostFragment newInstance(int columnCount) {
        FavoritePostFragment fragment = new FavoritePostFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        initializeViews();
        setupSubscription();
        getData();
        return mRootView;
    }

    /**
     * Initialize all UI layouts elements.
     */
    private void initializeViews() {
        mDetailRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview_detail);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBarPost);
        mNoFavoriteLayout = (LinearLayout) mRootView.findViewById(R.id.layout_no_favorite);
        mProgressBar.setVisibility(View.VISIBLE);


        setupRecyclerViewForPost(new ArrayList<PostDataDetail>(), true);

    }

    /**
     * Method used for predict the postdata by the attribute pass by it's caller like Discussion,Events,Favorite etc.
     */
    public void getData() {
        mPostDataLearningModel.getFavoritePostByGroupIdNdUserId(mSelectedGroupObjectId, mAppUserModel.getObjectId());

        // mPostDataLearningModel.getFilterPostByGroupIdNdAttribute(mSelectedGroupObjectId, postDataType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSubscription != null) mSubscription.dispose();
    }

    /**
     * set up Disposable to listen to RxBus
     */
    private void setupSubscription() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object eventObject) {
                if (eventObject instanceof LoadFavoritePostListEvent) {
                    setupRecyclerViewForPost(((LoadFavoritePostListEvent) eventObject).getPostDatas(), false);
                } else if (eventObject instanceof LoadPostAddToFavoriteEvent) {

                    if (mLearningNetworkDetailAdapter != null) {
                        if (((LoadPostAddToFavoriteEvent) eventObject).isAddedToFav()) {

                            mLearningNetworkDetailAdapter.addItem(((LoadPostAddToFavoriteEvent) eventObject).getPostDataDetail(), false);
                        } else {
                            mLearningNetworkDetailAdapter.removeItem(((LoadPostAddToFavoriteEvent) eventObject).getPostDataDetail().getPostData().getAlias());
                        }


                        if (mLearningNetworkDetailAdapter != null && mLearningNetworkDetailAdapter.getItemCount() > 0) {
                            mNoFavoriteLayout.setVisibility(View.GONE);
                            if (mDetailRecyclerView.getVisibility() != View.VISIBLE)
                                mDetailRecyclerView.setVisibility(View.VISIBLE);
                        } else
                            mNoFavoriteLayout.setVisibility(View.VISIBLE);

                    }

                } else if (eventObject instanceof LoadPostLikedEvent) {
                    if (mLearningNetworkDetailAdapter != null) {
                        mLearningNetworkDetailAdapter.notifyDataSetChanged();
                    }
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
        if (postDatas.size() == 0 || postDatas == null) {
            if (!isFirstTime) {
                mNoFavoriteLayout.setVisibility(View.VISIBLE);
                mDetailRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
            Group group = mGroupModel.fetchGroupFromUUidSync(mSelectedGroupObjectId);
            mLearningNetworkDetailAdapter = new LearningNetworkDetailAdapter(mContext, FavoritePostFragment.this, postDatas, group);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mDetailRecyclerView.setLayoutManager(layoutManager);
            mDetailRecyclerView.setAdapter(mLearningNetworkDetailAdapter);

        } else {

            Collections.sort(postDatas, new SortPostByCreatedTime.LastConversationTimeSorter());

            mDetailRecyclerView.setVisibility(View.VISIBLE);
            mNoFavoriteLayout.setVisibility(View.GONE);

            Group group = mGroupModel.fetchGroupFromUUidSync(mSelectedGroupObjectId);
            mLearningNetworkDetailAdapter = new LearningNetworkDetailAdapter(mContext, FavoritePostFragment.this, postDatas, group);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mDetailRecyclerView.setLayoutManager(layoutManager);
            mDetailRecyclerView.setAdapter(mLearningNetworkDetailAdapter);
//        }

            mProgressBar.setVisibility(View.GONE);

        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }
}
