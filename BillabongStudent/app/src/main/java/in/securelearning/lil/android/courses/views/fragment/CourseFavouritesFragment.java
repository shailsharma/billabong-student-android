package in.securelearning.lil.android.courses.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentDemoCourseListBinding;
import in.securelearning.lil.android.app.databinding.FragmentFavoriteCourseBinding;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.SlidingTabLayout;
import in.securelearning.lil.android.courses.InjectorCourses;
import in.securelearning.lil.android.courses.models.ItemListModel;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CourseFavouritesFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    //    protected FragmentActivity mActivity;
    @Inject
    public RxBus mRxBus;
    @Inject
    ItemListModel mItemListModel;
    @Inject
    AppUserModel mAppUserModel;
    private Disposable mSubscription;
    //    List<Course> mCourseList = new ArrayList<>();
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    //    private List<Course> mDuplicateCourseList = new ArrayList<>();
    private int lastIndex;
    private Timer timer;
    private DemoCourseRecyclerViewAdapter mCourseAdapter;
    private static SlidingTabLayout slidingTabLayout;
    String mSearchQuery = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseFavouritesFragment() {
    }

    @SuppressWarnings("unused")
    public static CourseFavouritesFragment newInstance(int columnCount, SlidingTabLayout mSlidingTabLayout) {
        CourseFavouritesFragment fragment = new CourseFavouritesFragment();
        slidingTabLayout = mSlidingTabLayout;
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if (context instanceof FragmentActivity)
//            mActivity = (FragmentActivity) context;


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
        InjectorCourses.INSTANCE.getComponent().inject(this);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDemoCourseListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_demo_course_list, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorCoursePrimaryDark));

        initializeRecyclerView(binding);
        listenRxBusEvents();
        getData();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribe();
        mCourseAdapter = null;
    }

    private void unsubscribe() {
        if (mSubscription != null)
            mSubscription.dispose();
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) {
                if (event instanceof FavoriteAboutCourseUpdate) {


                    if (((FavoriteAboutCourseUpdate) event).isAdded()) {
                        addItemToRecyclerView(mItemListModel.getAboutCourseFromDatabase(((FavoriteAboutCourseUpdate) event).getId()));
                    } else {
                        removeItemFromRecyclerView(((FavoriteAboutCourseUpdate) event).getId());
                    }
                }
                if (event instanceof ObjectDownloadComplete) {
                    if (mCourseAdapter != null)
                        mCourseAdapter.updateSearchResultData(((ObjectDownloadComplete) event).getId(), ((ObjectDownloadComplete) event).getSyncStatus());

                }

                if (event instanceof SearchSubmitEvent) {
                    mSearchQuery = ((SearchSubmitEvent) event).getQueryText();
                    mCourseAdapter.search(mSearchQuery);
                } else if (event instanceof SearchOpenEvent) {
                    mSearchQuery = "";
                } else if (event instanceof SearchCloseEvent) {
                    mSearchQuery = "";
                    mCourseAdapter.clearSearch();
                }


            }
        });
    }

    /**
     * initialize recycler view for courses
     * check device is tablet or phone and load recycler view according to device
     *
     * @param binding
     */
    private void initializeRecyclerView(FragmentDemoCourseListBinding binding) {
        //     Context context = binding.getRoot().getContext();
        final RecyclerView recyclerView = binding.list;
        if (mColumnCount > 1) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount, GridLayoutManager.VERTICAL, false));
            mCourseAdapter = new DemoCourseRecyclerViewAdapter(new ArrayList<AboutCourse>());
            recyclerView.setAdapter(mCourseAdapter);

        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            mCourseAdapter = new DemoCourseRecyclerViewAdapter(new ArrayList<AboutCourse>());
            recyclerView.setAdapter(mCourseAdapter);
        }

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) {
//                    slidingTabLayout.setVisibility(View.GONE);
//                    recyclerView.setPadding(0, 0, 0, 0);
//                    NavigationDrawerActivity.mBottomTabLayout.setVisibility(View.GONE);
//                } else if (dy < 0) {
//                    slidingTabLayout.setVisibility(View.VISIBLE);
//                    recyclerView.setPadding(0, 63, 0, 0);
//                    NavigationDrawerActivity.mBottomTabLayout.setVisibility(View.VISIBLE);
//                }
//            }
//        });


    }

    private void getData() {
//        mCourseAdapter = new DemoCourseRecyclerViewAdapter(new ArrayList<Course>());

        mItemListModel.getFavoritesFromDatabase().subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse course) {
                        addItemToRecyclerView(course);
                    }
                });
        mItemListModel.getFavoriteCoursesList().subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse course) {
                        addItemToRecyclerView(course);
                    }
                });
    }

    private void addItemToRecyclerView(final AboutCourse course) {
        mCourseAdapter.additem(course);
    }

    private void removeItemFromRecyclerView(final String id) {
        mCourseAdapter.removeitem(id);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Object object);
    }

    public class DemoCourseRecyclerViewAdapter extends RecyclerView.Adapter<DemoCourseRecyclerViewAdapter.ViewHolder> {

        private final List<AboutCourse> mValues;
        private final List<AboutCourse> mPermanentValues;
        private final List<String> mIds;

        public DemoCourseRecyclerViewAdapter(List<AboutCourse> values) {
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
        public DemoCourseRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FragmentFavoriteCourseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_favorite_course, parent, false);
            return new DemoCourseRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final DemoCourseRecyclerViewAdapter.ViewHolder holder, int position) {
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
            } else if (holder.mItem.getMicroCourseType().toLowerCase().contains("pop")) {
                objectClass = PopUps.class;
                type = "Pop Up";
                typeImage = R.drawable.popup;
            } else if (holder.mItem.getMicroCourseType().toLowerCase().contains("video")) {
                objectClass = InteractiveVideo.class;
                type = "Interactive Video";
                typeImage = R.drawable.videio_course_white;
            }

            if (mItemListModel.isCourseDownloading(holder.mItem.getObjectId(), objectClass)) {
                holder.mBinding.progressBar.setVisibility(View.VISIBLE);
            } else {
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
            holder.mBinding.textviewTitle.setText(holder.mItem.getTitle());
            holder.mBinding.textviewCourseSubject.setText(holder.mItem.getMetaInformation().getSubjectText());
            holder.mBinding.ratingBarCourse.setRating((float) holder.mItem.getReviews().getAvgRating());
            holder.mBinding.textviewCourseDescription.setText(holder.mItem.getDescription());
            holder.mBinding.textviewViewsCount.setText(holder.mItem.getReviews().getTotalViews() + "");
            holder.mBinding.textviewCourseDate.setText(DateUtils.getTimeStringFromDateString(holder.mItem.getPublishedDate()) + " ago");

            // TODO: 2/8/2017  start course detail activity
            final Class finalObjectClass = objectClass;
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        AboutCourse item = holder.mItem;
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
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
                    }
                }
            });

            holder.mBinding.assignCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            });


            holder.mBinding.textviewCourseType.setText(type);
            holder.mBinding.imageviewCourseType.setImageResource(typeImage);
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
                    final int pos = mValues.size();
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    mValues.add(pos, course);
                                    notifyItemInserted(pos);
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
            public final FragmentFavoriteCourseBinding mBinding;
            public AboutCourse mItem;


            public ViewHolder(FragmentFavoriteCourseBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }

        }
    }
}
