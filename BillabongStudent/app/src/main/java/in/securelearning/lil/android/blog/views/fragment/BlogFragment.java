package in.securelearning.lil.android.blog.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.FragmentBlogListItem3Binding;
import in.securelearning.lil.android.app.databinding.LayoutBlogRecyclerViewBinding;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Blog;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.blog.InjectorBlog;
import in.securelearning.lil.android.blog.model.BlogListModel;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.BlogResponse;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BlogFragment extends Fragment {

    public static final String ARG_COLUMN_COUNT = "column-count";
    @Inject
    public RxBus mRxBus;
    @Inject
    BlogListModel mBlogListModel;
    @Inject
    NetworkModel mNetworkModel;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private Disposable mSubscription;
    private RecyclerViewAdapter mAdapter;
    private int mLimit = 10;
    private int mSkip = 0;
    private String mWebUrl = "";
    private LayoutBlogRecyclerViewBinding mBinding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BlogFragment() {
    }

    @SuppressWarnings("unused")
    public static BlogFragment newInstance(int columnCount) {
        BlogFragment fragment = new BlogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorBlog.INSTANCE.getComponent().inject(this);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        mWebUrl = getString(R.string.web_url) + "blog/";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_blog_recycler_view, container, false);
        setBackground();
        setUpRecyclerView();
        setDefault();
        fetchBlogList(mSkip, mLimit, false);
        listenRxBusEvents();
        return mBinding.getRoot();
    }

    private void setBackground() {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("fitness")) {
            mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
        }
    }

    private void fetchBlogList(int skip, final int limit, boolean isBottomProgress) {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            if (isBottomProgress) {
                mBinding.bottomProgress.setVisibility(View.VISIBLE);
            } else {
                mBinding.progressBar.setVisibility(View.VISIBLE);
            }

            final Call<BlogResponse> blogResponseCall = mNetworkModel.fetchBlogList(skip, limit);


            Observable.create(new ObservableOnSubscribe<BlogResponse>() {
                @Override
                public void subscribe(ObservableEmitter<BlogResponse> subscriber) throws Exception {

                    Response<BlogResponse> response = blogResponseCall.execute();
                    if (response != null && response.isSuccessful()) {
                        com.couchbase.lite.util.Log.e("BlogResponse", "successful");

                        BlogResponse blogResponse = response.body();
                        subscriber.onNext(blogResponse);
                        if (mSkip == 0) {
                            deleteAndSaveBlog(blogResponse.getBlogs());
                        }
                        subscriber.onComplete();

                    } else if (response.code() == 401 && SyncServiceHelper.refreshToken(getContext())) {

                        Response<BlogResponse> response2 = blogResponseCall.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            com.couchbase.lite.util.Log.e("BlogResponse", "successful");

                            BlogResponse blogResponse = response2.body();
                            subscriber.onNext(blogResponse);
                            subscriber.onComplete();

                        } else if (response2.code() == 401) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
                        }
                    } else {
                        throw new Exception();
                    }

                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.computation())
                    .subscribe(new Consumer<BlogResponse>() {
                        @Override
                        public void accept(BlogResponse blogResponse) {
                            mBinding.bottomProgress.setVisibility(View.GONE);
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.swipeRefreshLayout.setRefreshing(false);

                            ArrayList<Blog> list = blogResponse.getBlogs();

                            mSkip += list.size();
                            noResultFound(mSkip);
                            addItemToRecyclerView(list);
                            if (list.size() < limit) {
                                mBinding.list.clearOnScrollListeners();
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable t) {
                            Log.e("BlogResponse", "err fetching BlogResponse" + t.toString());
                            t.printStackTrace();
                            mBinding.bottomProgress.setVisibility(View.GONE);
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.swipeRefreshLayout.setRefreshing(false);

                            if (mSkip > 0) {
                                SnackBarUtils.showAlertSnackBar(getContext(), mBinding.list, getString(R.string.errorFetchNews));
                            } else {
                                showRetryLayout();
                            }

                        }
                    });
        } else {
            mBinding.list.removeOnScrollListener(null);
            fetchBlogFromDatabase();

        }
    }

    private void showRetryLayout() {
        mBinding.layoutRetry.setVisibility(View.VISIBLE);
        setRetryView();
        mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.layoutRetry.setVisibility(View.GONE);
                fetchBlogList(mSkip, mLimit, false);
            }
        });
    }

    private void setRetryView() {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("fitness")) {
            mBinding.imageViewRetry.setImageResource(R.drawable.daily_tips_g);
            mBinding.textViewRetry.setText("Unable to get Tips.");
        } else {
            mBinding.imageViewRetry.setImageResource(R.drawable.logo_news_g);
            mBinding.textViewRetry.setText(getString(R.string.errorFetchNews));
        }

    }

    private void noResultFound(int skip) {
        if (skip > 0) {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.list.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            setNoDataView();
            mBinding.list.setVisibility(View.GONE);
        }
    }

    private void setNoDataView() {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("fitness")) {
            mBinding.imageViewNoResult.setImageResource(R.drawable.daily_tips_g);
            mBinding.textViewNoResult.setText("No Tips available.");
        } else {
            mBinding.imageViewNoResult.setImageResource(R.drawable.logo_news_g);
            mBinding.textViewNoResult.setText(getString(R.string.messageNoNewsAvailable));
        }

    }

    private void deleteAndSaveBlog(ArrayList<Blog> blogs) {
        mBlogListModel.deleteAllBlogs();
        int blogSize = blogs.size() - 1;
        for (int i = 0; i <= blogSize; i++) {
            mBlogListModel.saveObject(blogs.get(blogSize - i));
        }
    }

    private void fetchBlogFromDatabase() {
        Observable.create(new ObservableOnSubscribe<ArrayList<Blog>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Blog>> e) throws Exception {
                e.onNext(mBlogListModel.fetchBlogs());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Blog>>() {
            @Override
            public void accept(ArrayList<Blog> blogs) throws Exception {
                addItemToRecyclerView(blogs);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    private void setDefault() {
        mSkip = 0;
        setUpRecyclerView();
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    private void initializeView() {
//        mBinding.swipeRefresh.setColorSchemeResources(R.color.colorAccent);
//        mBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (GeneralUtils.isNetworkAvailable(getContext())) {
//                    setDefault();
//                    fetchBlogList(mSkip, mLimit);
//                } else {
//                    mBinding.swipeRefresh.setRefreshing(false);
//                    SnackBarUtils.showNoInternetSnackBar(getContext(), mBlogRecyclerView);
//                }
//            }
//        });

    }

    private void getData(int skip, final int limit) {
//        mBlogListModel.getBlogList(skip, limit).subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ArrayList<BlogDetails>>() {
//                    @Override
//                    public void accept(ArrayList<BlogDetails> blogs) {
//                        mSkip += blogs.size();
//                        mPreviousTotal = blogs.size();
//                        if (mSkip > 0) {
//                            addItemToRecyclerView(blogs);
//                            mNoResultLayout.setVisibility(View.GONE);
//                            mBlogRecyclerView.setVisibility(View.VISIBLE);
//                        } else {
//                            mNoResultLayout.setVisibility(View.VISIBLE);
//                            mBlogRecyclerView.setVisibility(View.GONE);
//                        }
//                        if (blogs.size() < limit) {
//                            mBlogRecyclerView.removeOnScrollListener(null);
//                        }
//
//                    }
//                });
    }

    private void addItemToRecyclerView(ArrayList<Blog> blogs) {
        if (mAdapter != null) mAdapter.addItems(blogs);
    }

    private void setUpRecyclerView() {

        mAdapter = new RecyclerViewAdapter(new ArrayList<Blog>(), mListener);
        mBinding.list.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = null;
        if (getActivity() != null) {
            if (mColumnCount > 1) {
                layoutManager = new GridLayoutManager(getActivity(), mColumnCount);
                mBinding.list.setLayoutManager(layoutManager);

            } else {
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mBinding.list.setLayoutManager(layoutManager);

            }
            mAdapter = new RecyclerViewAdapter(new ArrayList<Blog>(), mListener);
            mBinding.list.setAdapter(mAdapter);
        }

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mSkip - 1) {

                            fetchBlogList(mSkip, mLimit, true);

                        }
                    }

                }

            });
        }
        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (GeneralUtils.isNetworkAvailable(getContext())) {
                    setDefault();
                    fetchBlogList(mSkip, mLimit, false);
                } else {
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                    ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unSubscribe();
    }

    private void unSubscribe() {
        if (mSubscription != null)
            mSubscription.dispose();
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) {
                if (event instanceof ObjectDownloadComplete) {
                    if (mAdapter != null)
                        Completable.complete().observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() {
                                        mAdapter.updateSearchResultData(((ObjectDownloadComplete) event).getId(), ((ObjectDownloadComplete) event).getSyncStatus());
                                    }
                                });

                }

            }
        });
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Object object);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final ArrayList<Blog> mValues;
        private final ArrayList<String> mIds;
        private final OnListFragmentInteractionListener mListener;

        public RecyclerViewAdapter(ArrayList<Blog> items, OnListFragmentInteractionListener listener) {
            mValues = items;
            mListener = listener;
            mIds = getIdList(items);
        }

        private ArrayList<String> getIdList(ArrayList<Blog> values) {
            ArrayList<String> ids = new ArrayList<>();
            for (Blog c :
                    values) {
                ids.add(c.getObjectId());
            }
            return ids;
        }

        private void updateSearchResultData(String objectId, SyncStatus syncStatus) {

            if (mIds.contains(objectId)) {
                for (int i = 0; i < mValues.size(); i++) {
                    Blog object = mValues.get(i);
                    if (object.getObjectId().equals(objectId)) {
                        object.setSyncStatus(syncStatus.toString());
                        notifyItemChanged(i);
                    }
                }
            } else {
                Blog object = mBlogListModel.getBlog(objectId);
                if (object.getObjectId().equals(objectId)) {
                    object.setSyncStatus(syncStatus.toString());
                    addItem(object);
                }

            }
        }

        public void addItem(Blog object) {
            if (!mIds.contains(object.getObjectId())) {
                mValues.add(object);
                mIds.add(object.getObjectId());
                if (mValues.size() > 0)
                    notifyItemInserted(mValues.size() - 1);
            }
        }

        public void removeItem(String id) {
            if (mIds.contains(id)) {
                for (int i = 0; i < mValues.size(); i++) {
                    if (mValues.get(i).getObjectId().equals(id)) {
                        mValues.remove(i);
                        notifyItemRemoved(i);
                    }
                }
                mIds.remove(id);

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FragmentBlogListItem3Binding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_blog_list_item3, parent, false);
            return new ViewHolder(binding);


        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Blog blog = mValues.get(position);
            String imagePath = blog.getThumbnail().getUrl();
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.mBinding.textviewTitle.setText(blog.getTitle());
            TextViewMore.viewMore(blog.getDescription(), holder.mBinding.textViewDescription, holder.mBinding.includeTextViewMoreLess.textViewMoreLess);
            holder.mBinding.textviewCourseSubject.setText(blog.getMetaInformation().getDomain().getName());
            holder.mBinding.ratingBar.setRating(blog.getAvgRating());
            holder.mBinding.ratingCount.setText("" + blog.getAvgRating());

            holder.mBinding.textviewViewsCount.setText(blog.getTotalViews() + "");

            holder.mBinding.textViewPublishTime.setText(DateUtils.getTimeStringFromDateString(blog.getLastUpdationTime()) + " ago");

            holder.mBinding.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, mWebUrl + blog.getObjectId());
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Send to"));
                }
            });
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        mListener.onListFragmentInteraction(mValues.get(position));
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getContext(), mBinding.list);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void addItems(ArrayList<Blog> blogs) {
            for (Blog object : blogs) {
                if (!mIds.contains(object.getObjectId())) {
                    mValues.add(object);
                    mIds.add(object.getObjectId());
                }
            }
            notifyDataSetChanged();

        }

        public void clear() {
            mValues.clear();
            notifyDataSetChanged();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private FragmentBlogListItem3Binding mBinding;

            public ViewHolder(FragmentBlogListItem3Binding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

        }
    }

}
