package in.securelearning.lil.android.resources.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubePlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentRecommendedBinding;
import in.securelearning.lil.android.base.dataobjects.CalendarData;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.resources.adapter.RecommendedAdapter;
import in.securelearning.lil.android.resources.model.ResourcesMapModel;
import in.securelearning.lil.android.resources.view.InjectorYoutube;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResourcesResults;
import in.securelearning.lil.android.syncadapter.events.FavouriteResourceEvent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
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
 * Created by Secure on 09-06-2017.
 */

public class RecommendedListFragment extends Fragment implements YouTubePlayer.OnFullscreenListener {
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    ResourcesMapModel mResourcesMapModel;
    @Inject
    public RxBus mRxBus;
    private ArrayList<String> mSubjectId = null;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String SUBJECT_ID = "subject_id";
    private static final String TOPIC_ID = "topic_id";
    private static final String GRADE_ID = "grade_id";
    private static final String FLAG = "flag";
    private RecommendedAdapter mAdapter;
    private boolean isFullscreen;
    FragmentRecommendedBinding binding;
    private int mColumnCount = 1;
    private String mTopicId = "";
    private int mTotalResultCount = 0;
    private int mCurrentResultCount = 0;
    private int mDefaultCount = 20;
    String mGradeId;
    boolean mFlag;
    private Disposable mSubscription;
    List<PeriodNew> nPeriodList = new ArrayList<>();

    public static Fragment newInstance(int columnCount) {
        RecommendedListFragment fragment = new RecommendedListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static RecommendedListFragment newInstanceForBrowse(int columnCount) {
        RecommendedListFragment fragment = new RecommendedListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static RecommendedListFragment newInstanceForTopicBrowse(ArrayList<String> subjectId, String topicId, String gradeId, int columnCount) {
        RecommendedListFragment fragment = new RecommendedListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(SUBJECT_ID, subjectId);
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putBoolean(FLAG, false);
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        InjectorYoutube.INSTANCE.getComponent().inject(this);
        listenRxBusEvents();
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof FavouriteResourceEvent) {
                    refreshResourceList(((FavouriteResourceEvent) event).getObjectId());
                }
            }
        });
    }

    private void refreshResourceList(String docId) {
        if (mAdapter!=null) {
            mAdapter.refresh(docId);
        }
    }

    // Inflate the view for the activity based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_recommended, container, false);
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        setUpVideoListRecyclerView(new ArrayList<FavouriteResource>());
        mFlag = getArguments().getBoolean("flag");
        // Header is dynamic generate , so need to hide and show
        if (!mFlag) {
            mSubjectId = getArguments().getStringArrayList(SUBJECT_ID);
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            setDefault();
            if (GeneralUtils.isNetworkAvailable(getContext())) {
                getYoutubeVideoList(mSubjectId, mTopicId, mGradeId, mCurrentResultCount, mDefaultCount);
            } else {
                binding.listContainer.setVisibility(View.GONE);
                binding.layoutNoResult.setVisibility(View.VISIBLE);
                binding.txtNoResult.setText(getString(R.string.connect_internet));
            }
        } else {
            mGradeId = mResourcesMapModel.getUserGradeId();
            getPeriodList();
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.dispose();
            mAdapter = null;
        }
    }

    private void setUpVideoListRecyclerView(ArrayList<FavouriteResource> resources) {
        mAdapter = new RecommendedAdapter(resources, getActivity(), mColumnCount);
        binding.videoList.setHasFixedSize(true);
        binding.videoList.setItemViewCacheSize(40);
        binding.videoList.setDrawingCacheEnabled(true);
        LinearLayoutManager layoutManager = null;
        if (mColumnCount > 1) {
            layoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        binding.videoList.setLayoutManager(layoutManager);
        binding.videoList.setItemAnimator(new DefaultItemAnimator());
        binding.videoList.setAdapter(mAdapter);
        // TODO: 25-08-2017 below code for to detect user scroll on top screen when device is online or offline
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (GeneralUtils.isNetworkAvailable(getContext())) {
                    setDefault();
                    getYoutubeVideoList(mSubjectId, mTopicId, mGradeId, mCurrentResultCount, mDefaultCount);
                } else {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                }
            }
        });
        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;

            binding.videoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mCurrentResultCount - 1) {
                            if (mCurrentResultCount < mTotalResultCount) {
                                if (GeneralUtils.isNetworkAvailable(getContext())) {
                                    getYoutubeVideoList(mSubjectId, mTopicId, mGradeId, mCurrentResultCount, mDefaultCount);
                                } else {
                                    ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                                }
                            } else if (mCurrentResultCount == mTotalResultCount) {
                                binding.layoutBottomProgress.setVisibility(View.GONE);
                            }
                        }
                    }
                }

            });
        }
    }

    private CalendarData getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfPeriodicEventDate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE");
        CalendarData calendarData = new CalendarData();
        calendarData.setPeriodicEventDate(sdfPeriodicEventDate.format(cal.getTime()));
        calendarData.setDayNameFull(sdfDay.format(cal.getTime()));
        calendarData.setTodaysDate(cal.getTime());
        return calendarData;
    }

    private void getPeriodList() {
        final CalendarData calendarData = getCurrentDate();
        mResourcesMapModel.getPeriodList(DateUtils.getSecondsForMorningFromDate(calendarData.getTodaysDate()), DateUtils.getSecondsForMidnightFromDate(calendarData.getTodaysDate()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<PeriodNew>>() {
            @Override
            public void accept(List<PeriodNew> periodNews) throws Exception {
                nPeriodList = periodNews;
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();

            }
        }, new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    public void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.videoList.setVisibility(View.GONE);
    }

    public void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
        binding.videoList.setVisibility(View.VISIBLE);
    }

    private void setDefault() {
        mCurrentResultCount = 0;
        mTotalResultCount = 0;
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    private void getYoutubeVideoList(final ArrayList<String> subjectId, final String topicId, final String gradeId, final int skip, final int limit) {

        if (mCurrentResultCount > 0) {
            binding.layoutBottomProgress.setVisibility(View.VISIBLE);
        } else {
            binding.layoutBottomProgress.setVisibility(View.GONE);
            showProgressBar();
        }

        Observable.create(new ObservableOnSubscribe<ArrayList<FavouriteResource>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<FavouriteResource>> e) throws Exception {
                ArrayList<FavouriteResource> list = new ArrayList<>();
                Call<SearchResourcesResults> call = mNetworkModel.getRecommendedResources(subjectId, topicId, gradeId, skip, limit);
                Response<SearchResourcesResults> response = call.execute();
                if (response != null && response.isSuccessful() && response.body().getList().size() > 0) {
                        list = response.body().getList();
                        RecommendedListFragment.this.mCurrentResultCount += list.size();
                        RecommendedListFragment.this.mTotalResultCount = response.body().getTotalResult();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<FavouriteResource>>() {
                    @Override
                    public void accept(ArrayList<FavouriteResource> videoList) throws Exception {
                        if (videoList != null && videoList.size() > 0) {
                            addValuesToRecyclerView(videoList);
                        } else {
                            if (skip <= 0) {
                                binding.listContainer.setVisibility(View.GONE);
                                binding.layoutNoResult.setVisibility(View.VISIBLE);
                            } else {
                                binding.listContainer.setVisibility(View.VISIBLE);
                                binding.layoutNoResult.setVisibility(View.GONE);
                            }
                        }
                        binding.layoutBottomProgress.setVisibility(View.GONE);
                        binding.swipeRefreshLayout.setRefreshing(false);
                        hideProgressBar();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        hideProgressBar();
                        if (skip <= 0) {
                            binding.listContainer.setVisibility(View.GONE);
                            binding.layoutNoResult.setVisibility(View.VISIBLE);
                        } else {
                            binding.listContainer.setVisibility(View.VISIBLE);
                            binding.layoutNoResult.setVisibility(View.GONE);
                        }
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        hideProgressBar();
                    }
                });
    }

    private void addValuesToRecyclerView(ArrayList<FavouriteResource> videoList) {
        if (videoList.size() > 0) {
            mAdapter.addValues(videoList);
            binding.layoutNoResult.setVisibility(View.GONE);
            binding.listContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFullscreen(boolean b) {
        this.isFullscreen = b;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
