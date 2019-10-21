package in.securelearning.lil.android.courses.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentDemoCourseBinding;
import in.securelearning.lil.android.app.databinding.FragmentFavoriteCourseBinding;
import in.securelearning.lil.android.app.databinding.LayoutCoursePagerItemBinding;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.predictions.Predicate;
import in.securelearning.lil.android.base.predictions.PredicateListFilter;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.SlidingTabLayout;
import in.securelearning.lil.android.courses.InjectorCourses;
import in.securelearning.lil.android.courses.models.ItemListModel;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.syncadapter.events.CourseDeleteEvent;
import in.securelearning.lil.android.syncadapter.events.FavoriteAboutCourseUpdate;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.events.SearchCloseEvent;
import in.securelearning.lil.android.syncadapter.events.SearchOpenEvent;
import in.securelearning.lil.android.syncadapter.events.SearchSubmitEvent;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class CourseFragmentNew extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    //    private FilterList mFilterList;
    private int mTabsCount = 3;
    private View mRootView;
    private ViewPager mCourseViewPager;
    private ViewPagerAdapter pagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private AppBarLayout mAppBarLayout;
    private CharSequence mTabsTitles[] = {"Downloaded", "Recommended", "Favorites"};
    @Inject
    public RxBus mRxBus;
    @Inject
    ItemListModel mItemListModel;
    @Inject
    AppUserModel mAppUserModel;
    private Disposable mSubscription;
    private Disposable mDownloadedSubscription;
    private Disposable mRecommendedSubscription;
    private Disposable mRecommended2Subscription;
    private Disposable mFavSubscription;
    private Disposable mFav2Subscription;
    private int mColumnCount = 1;
    //    private OnListFragmentInteractionListener mListener;
    String mSearchQuery = "";

    private int mPendingSkip = 0;
    private int mPendingLimit = 10;
    private int mPendingPreviousTotal = 0;
    private int mOverDueSkip = 0;
    private int mOverDueLimit = 10;
    private int mOverDuePreviousTotal = 0;
    private int mSubmittedSkip = 0;
    private int mSubmittedLimit = 10;
    private int mSubmittedPreviousTotal = 0;
    private String mFilterBySubject = "";

    private RecommendedRecyclerViewAdapter mRecommendedRecyclerViewAdapter;
    private DownloadedRecyclerViewAdapter mDownloadedRecyclerViewAdapter;
    private FavoriteRecyclerViewAdapter mFavoriteRecyclerViewAdapter;
    final LayoutCoursePagerItemBinding[] mViewPagerItemBindings = new LayoutCoursePagerItemBinding[3];
//    public FilterList getFilterList() {
//        return mFilterList;
//    }
//
//    public void setFilterList(FilterList filterList) {
//        mFilterList = filterList;
//    }

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            if (dy > 0) {
//                mSlidingTabLayout.setVisibility(View.GONE);
//                recyclerView.setPadding(0, 0, 0, 0);
//                NavigationDrawerActivity.mBottomTabLayout.setVisibility(View.GONE);
//            } else if (dy < 0) {
//                mSlidingTabLayout.setVisibility(View.VISIBLE);
//                recyclerView.setPadding(0, 63, 0, 0);
//                NavigationDrawerActivity.mBottomTabLayout.setVisibility(View.VISIBLE);
//            }
        }
    };

    public CourseFragmentNew() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static CourseFragmentNew newInstance(int columnCount) {
        CourseFragmentNew fragment = new CourseFragmentNew();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorCourses.INSTANCE.getComponent().inject(this);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    public void filter(FilterList filterList) {
//        if (mFilterList != null) {
        if (mDownloadedRecyclerViewAdapter != null) {
            mDownloadedRecyclerViewAdapter.filter(filterList);
            mDownloadedRecyclerViewAdapter.sort(filterList);
        }
        if (mRecommendedRecyclerViewAdapter != null) {
            mRecommendedRecyclerViewAdapter.filter(filterList);
            mRecommendedRecyclerViewAdapter.sort(filterList);
        }
        if (mFavoriteRecyclerViewAdapter != null) {
            mFavoriteRecyclerViewAdapter.filter(filterList);
            mFavoriteRecyclerViewAdapter.sort(filterList);
        }
//        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Object object);
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
////        if (context instanceof FragmentActivity)
////            mActivity = (FragmentActivity) context;
//
//
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unSubscribe();
        mCourseViewPager = null;
        pagerAdapter = null;
        mDownloadedRecyclerViewAdapter = null;
        mRecommendedRecyclerViewAdapter = null;
        mFavoriteRecyclerViewAdapter = null;
    }

    @Override
    public void onPause() {
        super.onPause();
//        unSubscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_fragment_course, container, false);
        initializeViews();
        initializeSlidingTabs();
        listenRxBusEvents();
        return mRootView;
    }

    /**
     * find ids of views
     */
    private void initializeViews() {

        mCourseViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager_course);
        mSlidingTabLayout = (SlidingTabLayout) mRootView.findViewById(R.id.tabs);
        mAppBarLayout = (AppBarLayout) mRootView.findViewById(R.id.my_appbar_container_2);
        mAppBarLayout.setElevation(0f);

    }

    /**
     * set up slide views and set them into viewpager
     */
    private void initializeSlidingTabs() {

        pagerAdapter = new ViewPagerAdapter(mTabsTitles, mTabsCount);
        mCourseViewPager.setAdapter(pagerAdapter);
        mCourseViewPager.setOffscreenPageLimit(2);

        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            mSlidingTabLayout.setDistributeEvenly(true);
        } else {
            mSlidingTabLayout.setDistributeEvenly(false);
        }

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.colorAccent);
            }

        });
        mSlidingTabLayout.setViewPager(mCourseViewPager);

        mCourseViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // mSlidingTabLayout.setVisibility(View.VISIBLE);
                //NavigationDrawerActivity.mBottomTabLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void unSubscribe() {
        Log.e("CFN", "Disposing");
        if (mSubscription != null)
            mSubscription.dispose();
        if (mDownloadedSubscription != null)
            mDownloadedSubscription.dispose();
        if (mRecommendedSubscription != null)
            mRecommendedSubscription.dispose();
        if (mRecommended2Subscription != null)
            mRecommended2Subscription.dispose();
        if (mFavSubscription != null)
            mFavSubscription.dispose();
        if (mFav2Subscription != null)
            mFav2Subscription.dispose();
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof ObjectDownloadComplete) {
                    getDownloadedData();
                    if (mRecommendedRecyclerViewAdapter != null) {
                        if (((ObjectDownloadComplete) event).getObjectClass().equals(AboutCourse.class)) {
                            getRecommendedData();
                        } else if (((ObjectDownloadComplete) event).getObjectClass().equals(DigitalBook.class)
                                || ((ObjectDownloadComplete) event).getObjectClass().equals(VideoCourse.class)
                                || ((ObjectDownloadComplete) event).getObjectClass().equals(InteractiveImage.class)
                                || ((ObjectDownloadComplete) event).getObjectClass().equals(PopUps.class)
                                || ((ObjectDownloadComplete) event).getObjectClass().equals(ConceptMap.class)) {
                            mRecommendedRecyclerViewAdapter.updateSearchResultData(((ObjectDownloadComplete) event).getId(), ((ObjectDownloadComplete) event).getSyncStatus());
                        }

                    }
                    if (mFavoriteRecyclerViewAdapter != null)
                        mFavoriteRecyclerViewAdapter.updateSearchResultData(((ObjectDownloadComplete) event).getId(), ((ObjectDownloadComplete) event).getSyncStatus());

                } else if (event instanceof FavoriteAboutCourseUpdate) {
                    if (((FavoriteAboutCourseUpdate) event).isAdded()) {
                        if (mFavoriteRecyclerViewAdapter != null)
                            mFavoriteRecyclerViewAdapter.additem(mItemListModel.getAboutCourseFromDatabase(((FavoriteAboutCourseUpdate) event).getId()));
                    } else {
                        if (mFavoriteRecyclerViewAdapter != null)
                            mFavoriteRecyclerViewAdapter.removeItem(((FavoriteAboutCourseUpdate) event).getId());
                    }
                } else if (event instanceof SearchSubmitEvent) {
                    mSearchQuery = ((SearchSubmitEvent) event).getQueryText();
                    search(mSearchQuery);
                } else if (event instanceof SearchOpenEvent) {
                    mSearchQuery = "";
                } else if (event instanceof SearchCloseEvent) {
                    mSearchQuery = "";
                    clearSearch();
                } else if (event instanceof CourseDeleteEvent) {
                    if (mDownloadedRecyclerViewAdapter != null) {
                        mDownloadedRecyclerViewAdapter.removeItem(((CourseDeleteEvent) event).getId());
                        mFavoriteRecyclerViewAdapter.removeItem(((CourseDeleteEvent) event).getId());
                    }

                }


            }
        });
    }

    private void clearSearch() {
        if (mDownloadedRecyclerViewAdapter != null) {
            mDownloadedRecyclerViewAdapter.clearSearch();
        }
        if (mRecommendedRecyclerViewAdapter != null) {
            mRecommendedRecyclerViewAdapter.clearSearch();
        }
        if (mFavoriteRecyclerViewAdapter != null) {
            mFavoriteRecyclerViewAdapter.clearSearch();
        }
    }

    private void search(String searchQuery) {
        if (mDownloadedRecyclerViewAdapter != null) {
            mDownloadedRecyclerViewAdapter.search(searchQuery);
        }
        if (mRecommendedRecyclerViewAdapter != null) {
            mRecommendedRecyclerViewAdapter.search(searchQuery);
        }
        if (mFavoriteRecyclerViewAdapter != null) {
            mFavoriteRecyclerViewAdapter.search(searchQuery);
        }
    }

    private void getDownloadedData() {
//        mCourseAdapter = new DemoCourseRecyclerViewAdapter(new ArrayList<Course>());
        mDownloadedSubscription = mItemListModel.getCoursesList().subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<Course>() {
                    @Override
                    public void accept(Course course) {
                        if (mDownloadedRecyclerViewAdapter != null)
                            mDownloadedRecyclerViewAdapter.additem(course);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("DCF", "OOM");
                        throwable.printStackTrace();
                    }
                });
    }

    private void getRecommendedData() {

        mRecommendedSubscription = mItemListModel.getRecommendedCoursesList().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse course) {
                        if (mRecommendedRecyclerViewAdapter != null)
                            mRecommendedRecyclerViewAdapter.additem(course);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("CRF", "OOM");
                        throwable.printStackTrace();
                    }
                });
        mRecommended2Subscription = mItemListModel.getRecommendedCoursesListOnlineEs().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse course) {
                        if (mRecommendedRecyclerViewAdapter != null)
                            mRecommendedRecyclerViewAdapter.additem(course);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("CRF", "OOM");
                        throwable.printStackTrace();
                    }
                });

    }

    private void getFavoriteData() {

        mFavSubscription = mItemListModel.getFavoritesFromDatabase().subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse course) {
                        if (mFavoriteRecyclerViewAdapter != null)
                            mFavoriteRecyclerViewAdapter.additem(course);
                    }
                });
        mFav2Subscription = mItemListModel.getFavoriteCoursesList().subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse course) {
                        if (mFavoriteRecyclerViewAdapter != null)
                            mFavoriteRecyclerViewAdapter.additem(course);
                    }
                });
    }

    /**
     * Class to handle sliding fragments.
     */
    public class ViewPagerAdapter extends PagerAdapter {

        CharSequence mTabsTitles[];
        int mTabsCount;

        public ViewPagerAdapter(CharSequence[] tabsTitles, int tabsCount) {
            mTabsCount = tabsCount;
            mTabsTitles = tabsTitles;
        }

        @Override
        public int getCount() {
            return mTabsCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutCoursePagerItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_course_pager_item, container, false);
//
//            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
//            imageView.setImageResource(mResources[position]);
//
            if (mColumnCount > 1) {
                final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
                binding.list.setLayoutManager(mLayoutManager);

            } else {
                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                binding.list.setLayoutManager(mLayoutManager);
            }

            if (position == 0) {
                if (mDownloadedRecyclerViewAdapter == null)
                    mDownloadedRecyclerViewAdapter = new DownloadedRecyclerViewAdapter(new ArrayList<Course>());
                binding.list.setAdapter(mDownloadedRecyclerViewAdapter);
                getDownloadedData();
            } else if (position == 1) {
                if (mRecommendedRecyclerViewAdapter == null)
                    mRecommendedRecyclerViewAdapter = new RecommendedRecyclerViewAdapter(new ArrayList<AboutCourse>());
                binding.list.setAdapter(mRecommendedRecyclerViewAdapter);
                getRecommendedData();
            } else if (position == 2) {
                if (mFavoriteRecyclerViewAdapter == null)
                    mFavoriteRecyclerViewAdapter = new FavoriteRecyclerViewAdapter(new ArrayList<AboutCourse>());
                binding.list.setAdapter(mFavoriteRecyclerViewAdapter);
                getFavoriteData();
            }
            container.addView(binding.getRoot());
            mViewPagerItemBindings[position] = binding;
            return binding.getRoot();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitles[position];
        }


    }

    public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.ViewHolder> {

        private final List<AboutCourse> mValues;
        private final List<AboutCourse> mPermanentValues;
        private final List<String> mIds;

        public FavoriteRecyclerViewAdapter(List<AboutCourse> values) {
            mValues = new ArrayList<>();
            mValues.addAll(values);
            mPermanentValues = new ArrayList<>();
            mPermanentValues.addAll(values);
            mIds = getIdList(values);
        }

        private List<String> getIdList(List<AboutCourse> values) {
            List<String> ids = new ArrayList<>();
            for (AboutCourse c :
                    values) {
                ids.add(c.getObjectId());
            }
            return ids;
        }
//        private final OnListFragmentInteractionListener mListener;
//
//        public DemoCourseRecyclerViewAdapter(List<Course> items, OnListFragmentInteractionListener listener) {
//            mValues = items;
//            mListener = listener;
//        }

        @Override
        public FavoriteRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FragmentFavoriteCourseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_favorite_course, parent, false);
            return new FavoriteRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final FavoriteRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            String imagePath = holder.mItem.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getMetaInformation().getBanner();
            }
            String type = "";
            int typeImage = R.drawable.digital_book;
            Class objectClass = null;

            if (holder.mItem.getCourseType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
                type = "Digital Book";
                typeImage = R.drawable.digital_book;
            } else if (holder.mItem.getCourseType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
                type = "Video Course";
                typeImage = R.drawable.video_course;
            } else if (holder.mItem.getMicroCourseType().toLowerCase().contains("map")) {
                objectClass = ConceptMap.class;
                type = "Concept Map";
                typeImage = R.drawable.concept_map;
            } else if (holder.mItem.getMicroCourseType().toLowerCase().contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
                type = "Interactive Image";
                typeImage = R.drawable.interactive_image;
            } else if (holder.mItem.getMicroCourseType().toLowerCase().contains("video")) {
                objectClass = InteractiveVideo.class;
                type = "Interactive Video";
                typeImage = R.drawable.interactive_image;
            } else {
                objectClass = PopUps.class;
                type = "Pop Up";
                if (holder.mItem.getPopUpType() != null && !TextUtils.isEmpty(holder.mItem.getPopUpType().getValue())) {
                    objectClass = PopUps.class;
                    type = holder.mItem.getPopUpType().getValue();
                    typeImage = R.drawable.popup;
                }
                typeImage = R.drawable.popup;
            }

            if (mItemListModel.isCourseDownloading(holder.mItem.getObjectId(), objectClass))

            {
                holder.mBinding.progressBar.setVisibility(View.VISIBLE);
            } else

            {
                holder.mBinding.progressBar.setVisibility(View.GONE);
            }


//            if (mAppUserModel.getApplicationUser().getUserType().equals(AppUser.USERTYPE.TEACHER)) {
//                holder.mBinding.assignCourse.setVisibility(View.VISIBLE);
//                holder.mBinding.totalViewsGroup.setVisibility(View.GONE);
//            } else {
            holder.mBinding.assignCourse.setVisibility(View.GONE);
            holder.mBinding.totalViewsGroup.setVisibility(View.VISIBLE);
//            }

//            Picasso.with(holder.mBinding.getRoot().getContext()).load(imagePath).into(holder.mBinding.imageView);
            try

            {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageView);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageView);
                }

            } catch (
                    Exception e)

            {
                try {
                    Picasso.with(getContext()).load(holder.mItem.getThumbnail().getThumb()).into(holder.mBinding.imageView);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageView);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            holder.mBinding.textviewTitle.setText(holder.mItem.getTitle());
            holder.mBinding.textviewCourseSubject.setText(holder.mItem.getMetaInformation().

                    getSubjectText());
            holder.mBinding.ratingBarCourse.setRating((float) holder.mItem.getReviews().

                    getAvgRating());
            holder.mBinding.textviewCourseDescription.setText(Html.fromHtml(holder.mItem.getDescription()));
            holder.mBinding.textviewViewsCount.setText(holder.mItem.getReviews().

                    getTotalViews() + "");
            holder.mBinding.textviewCourseDate.setText(DateUtils.getTimeStringFromDateString(holder.mItem.getPublishedDate()) + " ago");

            // TODO: 2/8/2017  start course detail activity
            final Class finalObjectClass = objectClass;
            holder.mBinding.getRoot().

                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                    if (null != mListener) {
                            AboutCourse item = holder.mItem;
                            // Notify the active callbacks interface (the activity, if the
                            // activity is attached to one) that an item has been selected.
                            // mListener.onListFragmentInteraction(holder.mItem);
//                        if (item instanceof DigitalBook) {
//                        /*requires the json of the digital book and the referenced jsons*/
//                            WebPlayerActivity.startWebPlayer(getContext(), item.getObjectId(), item.getClass());
////                            CoursePlayerActivity.startCoursePlayer(getContext(), GeneralUtils.toGson(item), mItemListModel.getResources((DigitalBook) item), item.getClass());
//                        } else {
//                        /*requires the json of the digital book and the referenced jsons*/
//                            WebPlayerActivity.startWebPlayer(getContext(), item.getObjectId(), item.getClass());
//                        }

                            if (finalObjectClass != null)
                                startActivity(CourseDetailActivity.getStartActivityIntent(getContext(), item.getObjectId(), finalObjectClass, ""));
//                    }
                        }
                    });

//            holder.mBinding.assignCourse.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            });


            holder.mBinding.textviewCourseType.setText(type);
            holder.mBinding.imageviewCourseType.setImageResource(typeImage);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void filter(FilterList filterList) {
            boolean noneApplied = true;
            mValues.clear();
            List<String> filterBySubjectsList = new ArrayList<>();
            if (filterList != null) {

                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        filterBySubjectsList.add(filterSectionItem.getName());
                    }
                }
            }


            if (filterBySubjectsList != null && filterBySubjectsList.isEmpty() || (filterBySubjectsList.contains("All"))) {
                mValues.addAll(mPermanentValues);
            } else {
                filterValues(filterBySubjectsList);
            }
            if (mViewPagerItemBindings[2] != null && mValues.isEmpty()) {
                mViewPagerItemBindings[2].list.setVisibility(View.GONE);
                mViewPagerItemBindings[2].layoutNoResult.setVisibility(View.VISIBLE);
            } else {
                mViewPagerItemBindings[2].list.setVisibility(View.VISIBLE);
                mViewPagerItemBindings[2].layoutNoResult.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }

        private void filterValues(final List<String> filterBySubjectsList) {
            mValues.clear();
            mValues.addAll(PredicateListFilter.filter((ArrayList<AboutCourse>) mPermanentValues, new Predicate<AboutCourse>() {
                @Override
                public boolean apply(AboutCourse course) {
                    boolean isMatched = false;
                    for (String s : filterBySubjectsList) {
                        if (course.getSearchableText().contains(s.toLowerCase())) {
                            isMatched = true;
                            break;
                        }
                    }
                    return isMatched;
                }
            }));
        }

        public void sort(FilterList filterList) {
//            if (filterList != null) {
//                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
//                    if (filterSectionItem.isSelected()) {
//                    }
//                }
//                notifyDataSetChanged();
//            }
        }

        public void search(String query) {
            if (!TextUtils.isEmpty(query)) {
                mValues.clear();
                for (AboutCourse course :
                        mPermanentValues) {
                    if (course.getSearchableText().contains(query)) {
                        mValues.add(course);
                    }
                }
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                if (mViewPagerItemBindings[2] != null && mValues.isEmpty()) {
                                    mViewPagerItemBindings[2].list.setVisibility(View.GONE);
                                    mViewPagerItemBindings[2].layoutNoResult.setVisibility(View.VISIBLE);
                                } else {
                                    mViewPagerItemBindings[2].list.setVisibility(View.VISIBLE);
                                    mViewPagerItemBindings[2].layoutNoResult.setVisibility(View.GONE);
                                }
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        public void clearSearch() {
            if (mValues.size() != mPermanentValues.size()) {
                mValues.clear();
                mValues.addAll(mPermanentValues);
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                if (mViewPagerItemBindings[2] != null && mValues.isEmpty()) {
                                    mViewPagerItemBindings[2].list.setVisibility(View.GONE);
                                    mViewPagerItemBindings[2].layoutNoResult.setVisibility(View.VISIBLE);
                                } else {
                                    mViewPagerItemBindings[2].list.setVisibility(View.VISIBLE);
                                    mViewPagerItemBindings[2].layoutNoResult.setVisibility(View.GONE);
                                }
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        private void updateSearchResultData(String objectId, SyncStatus syncStatus) {

            if (mIds.contains(objectId)) {
                for (int i = 0; i < mValues.size(); i++) {
                    BaseDataObject object = mValues.get(i);
                    if (object.getObjectId().equals(objectId)) {
                        object.setSyncStatus(syncStatus.toString());
                        final int finalI = i;
                        Completable.complete().observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() {
                                        notifyItemChanged(finalI);
                                    }
                                });
                    }
                }
            }
        }

        public void additem(final AboutCourse course) {
            if (!mIds.contains(course.getObjectId())) {
                mPermanentValues.add(course);

                mIds.add(course.getObjectId());

                if (TextUtils.isEmpty(mSearchQuery) || course.getSearchableText().contains(mSearchQuery)) {
//                    final int pos = mValues.size();
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    if (mViewPagerItemBindings[2] != null && mValues.isEmpty()) {
                                        mViewPagerItemBindings[2].list.setVisibility(View.VISIBLE);
                                        mViewPagerItemBindings[2].layoutNoResult.setVisibility(View.GONE);
                                    }
                                    mValues.add(0, course);
                                    notifyItemInserted(0);
                                }
                            });
                }
            }
        }

        public void removeItem(final String id) {
            Completable.complete().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() {
                            if (mIds.contains(id)) {
                                for (int i = 0; i < mPermanentValues.size(); i++) {
                                    if (mPermanentValues.get(i).getObjectId().equals(id)) {
                                        mPermanentValues.remove(i);
                                    }
                                }
                                for (int i = 0; i < mValues.size(); i++) {
                                    if (mValues.get(i).getObjectId().equals(id)) {
                                        mValues.remove(i);
                                        final int finalI = i;

                                        notifyItemRemoved(finalI);

                                    }
                                }
                                mIds.remove(id);
                                if (mValues.isEmpty()) {
                                    if (mViewPagerItemBindings[2] != null) {
                                        mViewPagerItemBindings[2].layoutNoResult.setVisibility(View.VISIBLE);
                                        mViewPagerItemBindings[2].list.setVisibility(View.VISIBLE);
                                    }

                                }
                            }

                        }
                    });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final FragmentFavoriteCourseBinding mBinding;
            public AboutCourse mItem;


            public ViewHolder(FragmentFavoriteCourseBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }

        }
    }

    public class RecommendedRecyclerViewAdapter extends RecyclerView.Adapter<RecommendedRecyclerViewAdapter.ViewHolder> {

        private final List<AboutCourse> mValues;
        private final List<AboutCourse> mPermanentValues;
        private final List<String> mIds;


        public RecommendedRecyclerViewAdapter(List<AboutCourse> values) {
            mValues = new ArrayList<>();
            mValues.addAll(values);
            mPermanentValues = new ArrayList<>();
            mPermanentValues.addAll(values);
            mIds = getIdList(values);
        }

        private List<String> getIdList(List<AboutCourse> values) {
            List<String> ids = new ArrayList<>();
            for (AboutCourse c :
                    values) {
                ids.add(c.getObjectId());
            }
            return ids;
        }

        @Override
        public RecommendedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_course_recommend_itemview, parent, false);
            return new RecommendedRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecommendedRecyclerViewAdapter.ViewHolder holder, int position) {

            holder.mItem = mValues.get(position);
            holder.mTitleTV.setText(holder.mItem.getTitle());
            holder.mSubjectTv.setText(holder.mItem.getMetaInformation().getSubjectText());
            holder.mCouseDetailTv.setText(Html.fromHtml(holder.mItem.getDescription()));
            holder.mPublishedOnTv.setText(DateUtils.getTimeStringFromDateString(holder.mItem.getPublishedDate()) + " ago");
            holder.mRatingBar.setRating((float) holder.mItem.getReviews().getAvgRating());
            holder.mViewsCountTv.setText("" + holder.mItem.getReviews().getTotalViews());

            Class objectClass = null;
            String type = "";
            int typeImage = R.drawable.digital_book;

            String imagePath = holder.mItem.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getMetaInformation().getBanner();
            }
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mThumbnail);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(holder.mItem.getThumbnail().getThumb()).into(holder.mThumbnail);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mThumbnail);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

            String typeExt = holder.mItem.getMicroCourseType().toLowerCase();

            if (holder.mItem.getCourseType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
                type = "Digital Book";
                typeImage = R.drawable.digital_book;
            } else if (holder.mItem.getCourseType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
                type = "Video Course";
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
                if (holder.mItem.getPopUpType() != null && !TextUtils.isEmpty(holder.mItem.getPopUpType().getValue())) {
                    objectClass = PopUps.class;
                    type = holder.mItem.getPopUpType().getValue();
                    typeImage = R.drawable.popup;
                }
            }

            if (mItemListModel.isCourseDownloading(holder.mItem.getObjectId(), objectClass)) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
            } else {
                holder.mProgressBar.setVisibility(View.GONE);
            }
            holder.mCourseTypeTV.setText(type);
            holder.mCourseTypeImg.setImageResource(typeImage);

//            if (holder.mItem instanceof DigitalBook) {
//
//                holder.mCourseTypeTV.setText("Digital Book");
//                holder.mCourseTypeImg.setImageResource(R.drawable.digital_book);
//
//            } else if (holder.mItem instanceof InteractiveImage) {
//                holder.mCourseTypeTV.setText("Interactive Image");
//                holder.mCourseTypeImg.setImageResource(R.drawable.interactive_image);
//                // holder.mLineThree.setText(((InteractiveImage) holder.mItem).getMetaInformation().getTopic());
//
//            } else if (holder.mItem instanceof ConceptMap) {
//                holder.mCourseTypeTV.setText("Concept Map");
//                holder.mCourseTypeImg.setImageResource(R.drawable.concept_map);
//                // holder.mLineThree.setText(((ConceptMap) holder.mItem).getMetaInformation().getTopic());
//
//            } else if (holder.mItem instanceof PopUps) {
//                holder.mCourseTypeTV.setText("Pop Up");
//                holder.mCourseTypeImg.setImageResource(R.drawable.popup);
//                //holder.mLineThree.setText(((PopUps) holder.mItem).getMetaInformation().getTopic());
//            } else if (holder.mItem instanceof VideoCourse) {
//                holder.mCourseTypeTV.setText("Video Course");
//                holder.mCourseTypeImg.setImageResource(R.drawable.video_course_gray);
//                //holder.mLineThree.setText(((VideoCourse) holder.mItem).getMetaInformation().getTopic());
//            }

            final Class finalObjectClass = objectClass;
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                        CoursePlayerActivity.startCoursePlayer(holder.mView.getContext(), GeneralUtils.toGson(holder.mItem), mItemListModel.getResources((DigitalBook) holder.mItem), DigitalBook.class);
//                        WebPlayerActivity.startWebPlayer(holder.mView.getContext(), ((DigitalBook) holder.mItem).getObjectId(), DigitalBook.class);
                    if (!finalObjectClass.equals(AboutCourse.class))
                        startActivity(CourseDetailActivity.getStartActivityIntent(holder.mView.getContext(), holder.mItem.getObjectId(), finalObjectClass, ""));
                }
            });

//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (null != mListener) {
//                        // Notify the active callbacks interface (the activity, if the
//                        // activity is attached to one) that an item has been selected.
//                        mListener.onListFragmentInteraction(holder.mItem);
//                    }
//                }
//            });

//            holder.mDetailsButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (holder.mDetailsView.isShown()) {
//                        holder.mDetailsView.setVisibility(View.GONE);
//                    } else {
//                        holder.mDetailsView.setVisibility(View.VISIBLE);
//                    }
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void search(String query) {
            if (!TextUtils.isEmpty(query)) {
                mValues.clear();
                for (AboutCourse course :
                        mPermanentValues) {
                    if (course.getSearchableText().contains(query)) {
                        mValues.add(course);
                    }
                }
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                if (mViewPagerItemBindings[1] != null && mValues.isEmpty()) {
                                    mViewPagerItemBindings[1].list.setVisibility(View.GONE);
                                    mViewPagerItemBindings[1].layoutNoResult.setVisibility(View.VISIBLE);
                                } else {
                                    mViewPagerItemBindings[1].list.setVisibility(View.VISIBLE);
                                    mViewPagerItemBindings[1].layoutNoResult.setVisibility(View.GONE);
                                }
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        public void clearSearch() {
            if (mValues.size() != mPermanentValues.size()) {
                mValues.clear();
                mValues.addAll(mPermanentValues);
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                if (mViewPagerItemBindings[1] != null && mValues.isEmpty()) {
                                    mViewPagerItemBindings[1].list.setVisibility(View.GONE);
                                    mViewPagerItemBindings[1].layoutNoResult.setVisibility(View.VISIBLE);
                                } else {
                                    mViewPagerItemBindings[1].list.setVisibility(View.VISIBLE);
                                    mViewPagerItemBindings[1].layoutNoResult.setVisibility(View.GONE);
                                }
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        private void updateSearchResultData(String objectId, SyncStatus syncStatus) {

            if (mIds.contains(objectId)) {
                if (syncStatus.equals(SyncStatus.COMPLETE_SYNC)) {
                    removeitem(objectId);
                    return;
                }
                for (int i = 0; i < mValues.size(); i++) {
                    BaseDataObject object = mValues.get(i);
                    if (object.getObjectId().equals(objectId)) {
                        object.setSyncStatus(syncStatus.toString());
                        final int finalI = i;
                        Completable.complete().observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() {
                                        notifyItemChanged(finalI);
                                    }
                                });
                    }
                }
            }
        }

        public void filter(FilterList filterList) {
            boolean noneApplied = true;
            mValues.clear();
            List<String> filterBySubjectsList = new ArrayList<>();
            if (filterList != null) {

                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        filterBySubjectsList.add(filterSectionItem.getName());
                    }
                }
            }


            if (filterBySubjectsList != null && filterBySubjectsList.isEmpty() || (filterBySubjectsList.contains("All"))) {
                mValues.addAll(mPermanentValues);
            } else {
                filterValues(filterBySubjectsList);
            }
            if (mViewPagerItemBindings[1] != null && mValues.isEmpty()) {
                mViewPagerItemBindings[1].list.setVisibility(View.GONE);
                mViewPagerItemBindings[1].layoutNoResult.setVisibility(View.VISIBLE);
            } else {
                mViewPagerItemBindings[1].list.setVisibility(View.VISIBLE);
                mViewPagerItemBindings[1].layoutNoResult.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }

        private void filterValues(final List<String> filterBySubjectsList) {
            mValues.clear();
            mValues.addAll(PredicateListFilter.filter((ArrayList<AboutCourse>) mPermanentValues, new Predicate<AboutCourse>() {
                @Override
                public boolean apply(AboutCourse course) {
                    boolean isMatched = false;
                    for (String s : filterBySubjectsList) {
                        if (course.getSearchableText().contains(s.toLowerCase())) {
                            isMatched = true;
                            break;
                        }
                    }
                    return isMatched;
                }
            }));
        }

        public void sort(FilterList filterList) {
//            if (filterList != null) {
//                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
//                    if (filterSectionItem.isSelected()) {
//                    }
//                }
//                notifyDataSetChanged();
//            }
        }

        public void additem(final AboutCourse course) {

            if (!mIds.contains(course.getObjectId())) {
                mPermanentValues.add(course);

                mIds.add(course.getObjectId());

                if (TextUtils.isEmpty(mSearchQuery) || course.getSearchableText().contains(mSearchQuery)) {
//                    final int pos = mValues.size();

                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    if (mViewPagerItemBindings[1] != null && mValues.isEmpty()) {
                                        mViewPagerItemBindings[1].list.setVisibility(View.VISIBLE);
                                        mViewPagerItemBindings[1].layoutNoResult.setVisibility(View.GONE);
                                    }
                                    mValues.add(course);
                                    notifyItemInserted(mValues.size() - 1);
                                }
                            });
                }
            }
        }

        public void removeitem(String id) {
            if (mIds.contains(id)) {
                for (int i = 0; i < mPermanentValues.size(); i++) {
                    if (mPermanentValues.get(i).getObjectId().equals(id)) {
                        mPermanentValues.remove(i);
                    }
                }
                for (int i = 0; i < mValues.size(); i++) {
                    if (mValues.get(i).getObjectId().equals(id)) {
                        mValues.remove(i);
                        final int finalI = i;
                        Completable.complete().observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action() {
                                    @Override
                                    public void run() {
                                        notifyItemRemoved(finalI);
                                    }
                                });
                    }
                }
                mIds.remove(id);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final ImageView mCourseTypeImg;
            public final TextView mCourseTypeTV;
            public final TextView mTitleTV;
            public final TextView mCouseDetailTv;
            public final TextView mSubjectTv;
            public final TextView mViewsCountTv;
            public final TextView mPublishedOnTv;
            public final ImageView mThumbnail;
            public final RatingBar mRatingBar;
            public final ProgressBar mProgressBar;
            public AboutCourse mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                mCourseTypeImg = (ImageView) view.findViewById(R.id.imageview_course_type);
                mThumbnail = (ImageView) view.findViewById(R.id.imageview_course_thumbnail);
                mCourseTypeTV = (TextView) view.findViewById(R.id.textview_course_type);
                mTitleTV = (TextView) view.findViewById(R.id.textview_course_title);
                mCouseDetailTv = (TextView) view.findViewById(R.id.textview_course_detail);
                mSubjectTv = (TextView) view.findViewById(R.id.textview_subject);
                mPublishedOnTv = (TextView) view.findViewById(R.id.textview_published_on);
                mViewsCountTv = (TextView) view.findViewById(R.id.textview_views_count);
                mRatingBar = (RatingBar) view.findViewById(R.id.ratingbar);
                mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            }
        }
    }

    public class DownloadedRecyclerViewAdapter extends RecyclerView.Adapter<DownloadedRecyclerViewAdapter.ViewHolder> {

        private final List<Course> mValues;
        private final List<Course> mPermanentValues;
        private final List<String> mIds;

        public DownloadedRecyclerViewAdapter(List<Course> values) {
            mValues = new ArrayList<>();
            mValues.addAll(values);
            mPermanentValues = new ArrayList<>();
            mPermanentValues.addAll(values);
            mIds = getIdList(values);
        }

        private List<String> getIdList(List<Course> values) {
            List<String> ids = new ArrayList<>();
            for (Course c :
                    values) {
                ids.add(c.getObjectId());
            }
            return ids;
        }
//        private final OnListFragmentInteractionListener mListener;
//
//        public DemoCourseRecyclerViewAdapter(List<Course> items, OnListFragmentInteractionListener listener) {
//            mValues = items;
//            mListener = listener;
//        }

        @Override
        public DownloadedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FragmentDemoCourseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_demo_course, parent, false);
            return new DownloadedRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final DownloadedRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            String imagePath = holder.mItem.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getMetaInformation().getBanner();
            }
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageView);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageView);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(holder.mItem.getThumbnail().getThumb()).into(holder.mBinding.imageView);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageView);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

//            if (mAppUserModel.getApplicationUser().getUserType().equals(AppUser.USERTYPE.TEACHER)) {
//                holder.mBinding.assignCourse.setVisibility(View.VISIBLE);
//                holder.mBinding.totalViewsGroup.setVisibility(View.GONE);
//            } else {
            holder.mBinding.assignCourse.setVisibility(View.GONE);
            holder.mBinding.totalViewsGroup.setVisibility(View.VISIBLE);
//            }

            holder.mBinding.textviewTitle.setText(holder.mItem.getTitle());
            holder.mBinding.textviewCourseSubject.setText(holder.mItem.getMetaInformation().getSubjectText());
            holder.mBinding.ratingBarCourse.setRating((float) holder.mItem.getReviews().getAvgRating());
            holder.mBinding.textviewCourseDescription.setText(Html.fromHtml(holder.mItem.getDescription()));
            holder.mBinding.textviewViewsCount.setText(holder.mItem.getReviews().getTotalViews() + "");
            holder.mBinding.textviewCourseDate.setText(DateUtils.getTimeStringFromDateString(holder.mItem.getPublishedDate()) + " ago");

            // TODO: 2/8/2017  start course detail activity
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (null != mListener) {
                    Course item = holder.mItem;
                    // Notify the active callbacks interface (the activity, if the
                    // activity is attached to one) that an item has been selected.
                    // mListener.onListFragmentInteraction(holder.mItem);
//                        if (item instanceof DigitalBook) {
//                        /*requires the json of the digital book and the referenced jsons*/
//                            WebPlayerActivity.startWebPlayer(getContext(), item.getObjectId(), item.getClass());
////                            CoursePlayerActivity.startCoursePlayer(getContext(), GeneralUtils.toGson(item), mItemListModel.getResources((DigitalBook) item), item.getClass());
//                        } else {
//                        /*requires the json of the digital book and the referenced jsons*/
//                            WebPlayerActivity.startWebPlayer(getContext(), item.getObjectId(), item.getClass());
//                        }
                    startActivity(CourseDetailActivity.getStartActivityIntent(getContext(), item.getObjectId(), item.getClass(), ""));
//                    }
                }
            });

//            holder.mBinding.assignCourse.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            });

            String type = "";
            int typeImage = R.drawable.digital_book;
            if (holder.mItem instanceof DigitalBook) {
                type = "Digital Book";
                typeImage = R.drawable.digital_book;
            } else if (holder.mItem instanceof ConceptMap) {
                type = "Concept Map";
                typeImage = R.drawable.concept_map;
            } else if (holder.mItem instanceof InteractiveImage) {
                type = "Interactive Image";
                typeImage = R.drawable.interactive_image;
            } else if (holder.mItem instanceof PopUps) {
                type = "Pop Up";
                typeImage = R.drawable.popup;
            } else if (holder.mItem instanceof VideoCourse) {
                type = "Video Course";
                typeImage = R.drawable.interactive_image;
            } else if (holder.mItem instanceof InteractiveVideo) {
                type = "Interactive Video";
                typeImage = R.drawable.interactive_video;
            }
            holder.mBinding.textviewCourseType.setText(type);
            holder.mBinding.imageviewCourseType.setImageResource(typeImage);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void filter(FilterList filterList) {
            boolean noneApplied = true;
            mValues.clear();
            List<String> filterBySubjectsList = new ArrayList<>();
            if (filterList != null) {

                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(0).getItems()) {
                    if (filterSectionItem.isSelected()) {
                        filterBySubjectsList.add(filterSectionItem.getName());
                    }
                }
            }


            if (filterBySubjectsList != null && filterBySubjectsList.isEmpty() || (filterBySubjectsList.contains("All"))) {
                mValues.addAll(mPermanentValues);
            } else {
                filterValues(filterBySubjectsList);
            }
            if (mViewPagerItemBindings[0] != null && mValues.isEmpty()) {
                mViewPagerItemBindings[0].list.setVisibility(View.GONE);
                mViewPagerItemBindings[0].layoutNoResult.setVisibility(View.VISIBLE);
            } else {
                mViewPagerItemBindings[0].list.setVisibility(View.VISIBLE);
                mViewPagerItemBindings[0].layoutNoResult.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }

        private void filterValues(final List<String> filterBySubjectsList) {
            mValues.clear();
            mValues.addAll(PredicateListFilter.filter((ArrayList<Course>) mPermanentValues, new Predicate<Course>() {
                @Override
                public boolean apply(Course course) {
                    boolean isMatched = false;
                    for (String s : filterBySubjectsList) {
                        if (course.getSearchableText().contains(s.toLowerCase())) {
                            isMatched = true;
                            break;
                        }
                    }
                    return isMatched;
                }
            }));
        }

        public void sort(FilterList filterList) {
//            if (filterList != null) {
//                for (FilterList.FilterSectionItem filterSectionItem : filterList.getSections().get(1).getItems()) {
//                    if (filterSectionItem.isSelected()) {
//                    }
//                }
//                notifyDataSetChanged();
//            }
        }

        public void additem(final Course course) {

            if (!mIds.contains(course.getObjectId())) {
                mPermanentValues.add(course);
                mIds.add(course.getObjectId());
                if (TextUtils.isEmpty(mSearchQuery) || course.getSearchableText().contains(mSearchQuery)) {
//                    final int pos = mValues.size();
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    if (mViewPagerItemBindings[0] != null && mValues.isEmpty()) {
                                        mViewPagerItemBindings[0].list.setVisibility(View.VISIBLE);
                                        mViewPagerItemBindings[0].layoutNoResult.setVisibility(View.GONE);
                                    }
                                    mValues.add(0, course);
                                    notifyItemInserted(0);
                                }
                            });
                }
            }
        }

        public void search(String query) {
            if (!TextUtils.isEmpty(query)) {
                mValues.clear();
                for (Course course :
                        mPermanentValues) {
                    if (course.getSearchableText().contains(query)) {
                        mValues.add(course);
                    }
                }
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                if (mViewPagerItemBindings[0] != null && mValues.isEmpty()) {
                                    mViewPagerItemBindings[0].list.setVisibility(View.GONE);
                                    mViewPagerItemBindings[0].layoutNoResult.setVisibility(View.VISIBLE);
                                } else {
                                    mViewPagerItemBindings[0].list.setVisibility(View.VISIBLE);
                                    mViewPagerItemBindings[0].layoutNoResult.setVisibility(View.GONE);
                                }
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        public void clearSearch() {
            if (mValues.size() != mPermanentValues.size()) {
                mValues.clear();
                mValues.addAll(mPermanentValues);
                Completable.complete().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                if (mViewPagerItemBindings[0] != null && mValues.isEmpty()) {
                                    mViewPagerItemBindings[0].list.setVisibility(View.GONE);
                                    mViewPagerItemBindings[0].layoutNoResult.setVisibility(View.VISIBLE);
                                } else {
                                    mViewPagerItemBindings[0].list.setVisibility(View.VISIBLE);
                                    mViewPagerItemBindings[0].layoutNoResult.setVisibility(View.GONE);
                                }
                                notifyDataSetChanged();
                            }
                        });
            }
        }

        public void removeItem(final String id) {
            Completable.complete().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() {
                            if (mIds.contains(id)) {
                                for (int i = 0; i < mPermanentValues.size(); i++) {
                                    if (mPermanentValues.get(i).getObjectId().equals(id)) {
                                        mPermanentValues.remove(i);
                                    }
                                }
                                for (int i = 0; i < mValues.size(); i++) {
                                    if (mValues.get(i).getObjectId().equals(id)) {
                                        mValues.remove(i);
                                        final int finalI = i;

                                        notifyItemRemoved(finalI);

                                    }
                                }
                                mIds.remove(id);
                                if (mValues.isEmpty()) {
                                    if (mViewPagerItemBindings[0] != null) {
                                        mViewPagerItemBindings[0].layoutNoResult.setVisibility(View.VISIBLE);
                                        mViewPagerItemBindings[0].list.setVisibility(View.VISIBLE);
                                    }

                                }
                            }
                        }
                    });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final FragmentDemoCourseBinding mBinding;
            public Course mItem;


            public ViewHolder(FragmentDemoCourseBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }

        }
    }

}
