package in.securelearning.lil.android.courses.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentFavouriteListCourseBinding;
import in.securelearning.lil.android.app.databinding.VideoListItemBinding;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.predictions.Predicate;
import in.securelearning.lil.android.base.predictions.PredicateListFilter;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.courses.InjectorCourses;
import in.securelearning.lil.android.courses.models.CoursesModel;
import in.securelearning.lil.android.courses.models.ItemListModel;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.events.FavoriteAboutCourseUpdate;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class FavouriteListCourseFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    FragmentFavouriteListCourseBinding mBinding;
    private int mColumnCount = 1;
    private FavoriteRecyclerViewAdapter mFavoriteRecyclerViewAdapter;
    private Disposable mSubscription;
    private Disposable mFavSubscription;
    private Disposable mFav2Subscription;
    @Inject
    ItemListModel mItemListModel;
    @Inject
    CoursesModel mCoursesModel;
    @Inject
    public RxBus mRxBus;

    @Inject
    Context mContext;
    String mSearchQuery = "";
    private int mLimit = 10;
    private int mSkip = 0;

    public FavouriteListCourseFragment() {
    }


    public static FavouriteListCourseFragment newInstance(int colCount) {
        FavouriteListCourseFragment fragment = new FavouriteListCourseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, colCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unSubscribe();
        if (mFavoriteRecyclerViewAdapter != null) {
            mFavoriteRecyclerViewAdapter.clear();
            mFavoriteRecyclerViewAdapter = null;
        }
    }

    private void unSubscribe() {
        if (mFavSubscription != null)
            mFavSubscription.dispose();
        if (mFav2Subscription != null)
            mFav2Subscription.dispose();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorCourses.INSTANCE.getComponent().inject(this);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_favourite_list_course, container, false);
        if (mColumnCount > 1) {
            final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
            mBinding.list.setLayoutManager(mLayoutManager);
        } else {
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mBinding.list.setLayoutManager(mLayoutManager);
        }
        if (mFavoriteRecyclerViewAdapter == null)
            mFavoriteRecyclerViewAdapter = new FavoriteRecyclerViewAdapter(new ArrayList<AboutCourse>());
        mBinding.list.setAdapter(mFavoriteRecyclerViewAdapter);
//        getFavoriteData();
        getFavouriteCourseList();
        listenRxBusEvents();
        return mBinding.getRoot();
    }


    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof FavoriteAboutCourseUpdate) {
                    setDefault();
                    getFavouriteCourseList();
                }
            }
        });
    }

    private void setDefault() {
        initializeRecyclerView(new ArrayList<AboutCourse>());
        mSkip = 0;
    }

    private void initializeRecyclerView(ArrayList<AboutCourse> aboutCourses) {
        noResultFound(aboutCourses.size());
        setAdapterAndDoPagination(aboutCourses);
    }

    private void setAdapterAndDoPagination(ArrayList<AboutCourse> aboutCourses) {
        LinearLayoutManager layoutManager = null;
        if (mContext != null) {
            if (mContext.getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new GridLayoutManager(mContext, 2);
                mBinding.list.setLayoutManager(layoutManager);
                mFavoriteRecyclerViewAdapter = new FavoriteRecyclerViewAdapter(aboutCourses);
                mBinding.list.setAdapter(mFavoriteRecyclerViewAdapter);
            } else {
                layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                mBinding.list.setLayoutManager(layoutManager);
                mFavoriteRecyclerViewAdapter = new FavoriteRecyclerViewAdapter(aboutCourses);
                mBinding.list.setAdapter(mFavoriteRecyclerViewAdapter);
            }
        }
        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        getFavouriteCourseList();
//                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPreviousTotal - 1) {
//                            getData(mAssignById, mFilterBySubject, mStartDate, mEndDate, mSkip, mLimit);
//                        }
                    }
                }
            });
        }
    }

    private void noResultFound(int size) {
        if (size > 0) {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.list.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.list.setVisibility(View.GONE);
        }

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

    private void getFavouriteCourseList() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<ArrayList<AboutCourse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<AboutCourse>> e) throws Exception {
                ArrayList<AboutCourse> list;
                list = mCoursesModel.getCompleteListOfFavoriteCourse(mSkip, mLimit);
                if (list != null) {
                    mSkip += list.size();
                    e.onNext(list);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<AboutCourse>>() {
                    @Override
                    public void accept(ArrayList<AboutCourse> list) throws Exception {
                        addValuesToRecyclerView(list);
                        mBinding.progressBar.setVisibility(View.GONE);
                        if (mSkip > 0) {
                            mBinding.layoutNoResult.setVisibility(View.GONE);
                            mBinding.list.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.list.setVisibility(View.GONE);
                            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void addValuesToRecyclerView(ArrayList<AboutCourse> list) {
        if (list.size() > 0) {
            mFavoriteRecyclerViewAdapter.addValues(list);
        }
    }


    public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.ViewHolder> {

        private List<AboutCourse> mValues;
        private List<AboutCourse> mPermanentValues;
        private List<String> mIds;

        public void clear() {
            if (mValues != null) {
                mValues.clear();
                mValues = null;
            }
            if (mPermanentValues != null) {
                mPermanentValues.clear();
                mPermanentValues = null;
            }
            if (mIds != null) {
                mIds.clear();
                mIds = null;
            }
        }

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

        @Override
        public FavoriteRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            VideoListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_list_item, parent, false);
            return new FavoriteRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final FavoriteRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            Class objectClass = null;

            if (holder.mItem.getCourseType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
            } else if (holder.mItem.getCourseType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
            } else if (holder.mItem.getCourseType().contains("feature")) {
                objectClass = MicroLearningCourse.class;
            } else if (holder.mItem.getMicroCourseType().toLowerCase().contains("map")) {
                objectClass = ConceptMap.class;
            } else if (holder.mItem.getMicroCourseType().toLowerCase().contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
            } else if (holder.mItem.getMicroCourseType().toLowerCase().contains("video")) {
                objectClass = InteractiveVideo.class;
            } else {
                objectClass = PopUps.class;
                if (holder.mItem.getPopUpType() != null && !TextUtils.isEmpty(holder.mItem.getPopUpType().getValue())) {
                    objectClass = PopUps.class;
                }
            }

            if (mItemListModel.isCourseDownloading(holder.mItem.getObjectId(), objectClass)) {
                holder.mBinding.progressBar.setVisibility(View.VISIBLE);
            } else {
                holder.mBinding.progressBar.setVisibility(View.GONE);
            }

            setThumbnail(holder.mItem, holder.mBinding.imageViewThumbnail);
            holder.mBinding.textViewVideoDuration.setVisibility(View.GONE);
            holder.mBinding.text.setText(holder.mItem.getTitle());
            if (!TextUtils.isEmpty(holder.mItem.getMetaInformation().getTopic().getName()))
                holder.mBinding.textViewTopic.setText(holder.mItem.getMetaInformation().getTopic().getName());

            final Class finalObjectClass = objectClass;
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AboutCourse item = holder.mItem;
                    if (finalObjectClass != null) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, item.getObjectId()));
                        } else {
                            startActivity(CourseDetailActivity.getStartActivityIntent(getContext(), item.getObjectId(), finalObjectClass, ""));
                        }
                    }
                }
            });


            objectClass = null;

        }

        private void setThumbnail(AboutCourse item, ImageView imageView) {
            String imagePath = item.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = item.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = item.getMetaInformation().getBanner();
            }
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(imageView);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(imageView);
                }
            } catch (
                    Exception e) {
                try {
                    Picasso.with(getContext()).load(item.getThumbnail().getThumb()).into(imageView);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(imageView);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            if (mValues != null) {
                return mValues.size();
            }
            return 0;
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
            if (mBinding != null && mValues.isEmpty()) {
                mBinding.list.setVisibility(View.GONE);
                mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            } else {
                mBinding.list.setVisibility(View.VISIBLE);
                mBinding.layoutNoResult.setVisibility(View.GONE);
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
                                if (mBinding != null && mValues.isEmpty()) {
                                    mBinding.list.setVisibility(View.GONE);
                                    mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                                } else {
                                    mBinding.list.setVisibility(View.VISIBLE);
                                    mBinding.layoutNoResult.setVisibility(View.GONE);
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
                                if (mBinding != null && mValues.isEmpty()) {
                                    mBinding.list.setVisibility(View.GONE);
                                    mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                                } else {
                                    mBinding.list.setVisibility(View.VISIBLE);
                                    mBinding.layoutNoResult.setVisibility(View.GONE);
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
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    if (mBinding != null && mValues.isEmpty()) {
                                        mBinding.list.setVisibility(View.VISIBLE);
                                        mBinding.layoutNoResult.setVisibility(View.GONE);
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
                                    if (mBinding != null) {
                                        mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                                        mBinding.list.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                        }
                    });
        }

        public void addValues(List<AboutCourse> courseList) {
            if (mValues != null) {
                mValues.addAll(courseList);
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final VideoListItemBinding mBinding;
            public AboutCourse mItem;

            public ViewHolder(VideoListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

        }
    }
}
