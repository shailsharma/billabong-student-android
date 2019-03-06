package in.securelearning.lil.android.courses.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentRecommendedBinding;
import in.securelearning.lil.android.app.databinding.VideoListItemBinding;
import in.securelearning.lil.android.base.dataobjects.CalendarData;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.resources.model.ResourcesMapModel;
import in.securelearning.lil.android.resources.view.InjectorYoutube;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesResults;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
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

public class CourseListFragment extends Fragment implements YouTubePlayer.OnFullscreenListener {
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
    private static final String COURSE_TYPE = "courseType";
    private static final String FEATURE_TYPE = "featureType";
    private RecommendedAdapter mAdapter;
    private boolean isFullscreen;
    FragmentRecommendedBinding binding;
    private int mColumnCount = 1;
    private String mTopicId = "";
    private int mTotalResultCount = 0;
    private int mCurrentResultCount = 0;
    private int mDefaultCount = 20;
    String mGradeId;
    String[] mCourseType;
    String[] mFeatureType;
    boolean mFlag;
    private Disposable mSubscription;
    List<PeriodNew> nPeriodList = new ArrayList<>();

    public static Fragment newInstance(int columnCount) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static CourseListFragment newInstanceForBrowse(int columnCount) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static CourseListFragment newInstanceForTopicBrowse(ArrayList<String> subjectId, String topicId, String gradeId, int columnCount) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(SUBJECT_ID, subjectId);
        args.putStringArray(COURSE_TYPE, new String[]{"digitalbook", "videocourse", "conceptmap", "featuredcard"});
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putBoolean(FLAG, false);
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static CourseListFragment newInstanceForTopicBrowseCourses(ArrayList<String> subjectId, String topicId, String gradeId, int columnCount) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(SUBJECT_ID, subjectId);
        args.putStringArray(COURSE_TYPE, new String[]{"digitalbook", "videocourse", "conceptmap"});
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putBoolean(FLAG, false);
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static CourseListFragment newInstanceForTopicBrowseLessonPlan(ArrayList<String> subjectId, String topicId, String gradeId, int columnCount) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(SUBJECT_ID, subjectId);
        args.putStringArray(FEATURE_TYPE, new String[]{"lessonPlan"});
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putBoolean(FLAG, false);
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static CourseListFragment newInstanceForTopicBrowseRecap(ArrayList<String> subjectId, String topicId, String gradeId, int columnCount) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(SUBJECT_ID, subjectId);
        args.putStringArray(FEATURE_TYPE, new String[]{"recap"});
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
//        listenRxBusEvents();
    }
//
//    private void listenRxBusEvents() {
//        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object event) {
//                if (event instanceof FavouriteResourceEvent) {
//                    refreshResourceList( ((FavouriteResourceEvent) event).getObjectId());
//                }
//            }
//        });
//    }

    private void refreshResourceList(String docId) {
        mAdapter.refresh(docId);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_recommended, container, false);
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.accent_colorRed);
        setUpVideoListRecyclerView(new ArrayList<AboutCourseExt>());
        mFlag = getArguments().getBoolean("flag");
        // Header is dynamic generate , so need to hide and show
        if (!mFlag) {
            mSubjectId = getArguments().getStringArrayList(SUBJECT_ID);
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mCourseType = getArguments().getStringArray(COURSE_TYPE);
            mFeatureType = getArguments().getStringArray(FEATURE_TYPE);
            setDefault();
            if (GeneralUtils.isNetworkAvailable(getContext())) {
                getCoursesList(mSubjectId, mTopicId, mGradeId, mCurrentResultCount, mDefaultCount);
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

    private void setUpVideoListRecyclerView(ArrayList<AboutCourseExt> resources) {
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
                    getCoursesList(mSubjectId, mTopicId, mGradeId, mCurrentResultCount, mDefaultCount);
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
                                    getCoursesList(mSubjectId, mTopicId, mGradeId, mCurrentResultCount, mDefaultCount);
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


    private void getCoursesList(final ArrayList<String> subjectId, final String topicId, final String gradeId, final int skip, final int limit) {

        if (mCurrentResultCount > 0) {
            binding.layoutBottomProgress.setVisibility(View.VISIBLE);
        } else {
            binding.layoutBottomProgress.setVisibility(View.GONE);
            showProgressBar();
        }

        Observable.create(new ObservableOnSubscribe<ArrayList<AboutCourseExt>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<AboutCourseExt>> e) throws Exception {
                Call<SearchCoursesResults> call = mNetworkModel.getRecommendedCourses(subjectId, topicId, gradeId, skip, limit, mCourseType, mFeatureType);
                Response<SearchCoursesResults> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    ArrayList<AboutCourseExt> list = response.body().getList();
                    CourseListFragment.this.mCurrentResultCount += list.size();
                    CourseListFragment.this.mTotalResultCount = response.body().getTotalResult();
                    Log.e("CourseList1--", "Successful");
                    e.onNext(list);
                } else if (response.code() == 404) {
                    throw new Exception(getString(R.string.messageCoursesNotFound));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
                    Response<SearchCoursesResults> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        ArrayList<AboutCourseExt> list = response2.body().getList();
                        CourseListFragment.this.mCurrentResultCount += list.size();
                        CourseListFragment.this.mTotalResultCount = response2.body().getTotalResult();
                        Log.e("CourseList2--", "Successful");
                        e.onNext(list);
                    } else if ((response2.code() == 401)) {
                        startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
                    } else if (response2.code() == 404) {
                        throw new Exception(getString(R.string.messageCoursesNotFound));
                    } else {
                        Log.e("CourseList2--", "Failed");
                        throw new Exception(getString(R.string.messageCoursesFetchFailed));
                    }
                } else {
                    Log.e("CourseList1--", "Failed");
                    throw new Exception(getString(R.string.messageCoursesFetchFailed));
                }

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<AboutCourseExt>>() {
                    @Override
                    public void accept(ArrayList<AboutCourseExt> videoList) throws Exception {
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

    private void addValuesToRecyclerView(ArrayList<AboutCourseExt> videoList) {
        if (mAdapter != null) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.dispose();
            mAdapter = null;
        }
    }

    class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.VideoViewHolder> {
        private List<AboutCourseExt> mValues = new ArrayList<>();
        Context mContext;
        private boolean labelsVisible;
        private int mColumnCount = 1;
        private Observable<ArrayList<AboutCourseExt>> favorites;


        public void dispose() {
            if (mValues != null) {
                mValues.clear();
                mValues = null;
            }
        }

        public RecommendedAdapter(List<AboutCourseExt> videoList, Context context, int columnCount) {
            this.mValues = videoList;
            this.mContext = context;
            labelsVisible = true;
            this.mColumnCount = columnCount;
        }

        @Override
        public RecommendedAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            VideoListItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_list_item, parent, false);
            return new RecommendedAdapter.VideoViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(final RecommendedAdapter.VideoViewHolder holder, final int position) {
            final AboutCourseExt video = mValues.get(position);
//        holder.mBinding.thumbnail.setTag(video.getName());
//        holder.mBinding.thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
//        holder.mBinding.thumbnail.setImageResource(R.drawable.image_loading_thumbnail);
            holder.mBinding.imageViewThumbnail.setTag(video.getName());
            if (!TextUtils.isEmpty(video.getMetaInformation().getTopic().getName()))
                holder.mBinding.textViewTopic.setText(video.getMetaInformation().getTopic().getName());

            String imagePath = video.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = video.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = video.getMetaInformation().getBanner();
            }
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageViewThumbnail);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(video.getThumbnail().getThumb()).into(holder.mBinding.imageViewThumbnail);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

            Class objectClass = null;
            String type = "";
            int typeImage = R.drawable.digital_book;
            String typeExt = video.getMicroCourseType().toLowerCase();
            if (video.getCourseType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
                type = "Digital Book";
                typeImage = R.drawable.digital_book;
            } else if (video.getCourseType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
                type = "Video Course";
                typeImage = R.drawable.video_course;
            } else if (video.getCourseType().contains("feature")) {
                objectClass = MicroLearningCourse.class;
                type = "Feature Card";
                typeImage = R.drawable.video_course;
            } else if (typeExt.contains("map")) {
                objectClass = ConceptMap.class;
                type = "Concept Map";
                typeImage = R.drawable.concept_map;
            } else if (typeExt.contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
                type = "Interactive Image";
                typeImage = R.drawable.interactive_image;
            } else if (typeExt.contains("video")) {
                objectClass = InteractiveVideo.class;
                type = "Interactive Video";
                typeImage = R.drawable.interactive_image;
            } else {
                type = "Pop Up";
                if (video.getPopUpType() != null && !TextUtils.isEmpty(video.getPopUpType().getValue())) {
                    objectClass = PopUps.class;
                    type = video.getPopUpType().getValue();
                    typeImage = R.drawable.popup;
                }
            }
            final Class finalObjectClass = objectClass;
            holder.mBinding.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalObjectClass.equals(MicroLearningCourse.class)) {
                        mContext.startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, video.getObjectId()));
                    } else {
                        mContext.startActivity(CourseDetailActivity.getStartActivityIntent(mContext, video.getObjectId(), finalObjectClass, ""));

                    }
                }
            });
            holder.mBinding.text.setText(video.getTitle());
            holder.mBinding.textViewVideoDuration.setVisibility(View.GONE);
            holder.mBinding.favoriteImg.setVisibility(View.GONE);
        }


        @Override
        public int getItemCount() {
            if (mValues != null) {
                return mValues.size();

            }
            return 0;
        }

        public void addValues(ArrayList<AboutCourseExt> videoList) {
            if (mValues != null) {
                mValues.addAll(videoList);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (mValues != null) {
                mValues.clear();
                notifyDataSetChanged();
            }
        }

        public void refresh(String objectId) {
            for (int i = 0; i < mValues.size(); i++) {
                if (mValues.get(i).getObjectId().equalsIgnoreCase(objectId)) {
                    mValues.get(i).setDocId("");
                    notifyItemChanged(i);
                    break;
                }
            }
        }

        class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            VideoListItemBinding mBinding;

            VideoViewHolder(VideoListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                mBinding.imgShare.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.img_share:
                        shareVideo(mBinding.thumbnail.getTag().toString());
                        break;
                }
            }

            private void shareVideo(String path) {
                Toast.makeText(mContext, "in Share  mode " + path, Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(path);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                shareIntent.setType("video/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(Intent.createChooser(shareIntent, "send"));
            }
        }

        public String convert(int seconds) {
            String strMinutes = "";
            String strSeconds = "";
            int minutes;
            int hours;
            int newSeconds;
            int secondMod = (seconds % 3600);
            hours = seconds / 3600;
            minutes = secondMod / 60;
            newSeconds = secondMod % 60;

            strMinutes = String.valueOf(minutes);
            strSeconds = String.valueOf(newSeconds);

            if (minutes >= 0 && minutes <= 9) {
                strMinutes = "0" + strMinutes;
            }
            if (newSeconds >= 0 && newSeconds <= 9) {
                strSeconds = "0" + strSeconds;

            }
            if (hours == 0) {
                return strMinutes + ":" + strSeconds;

            } else {
                return String.valueOf(hours) + ":" + strMinutes + ":" + strSeconds;
            }

        }
    }


}
