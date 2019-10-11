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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentDemoCourseBinding;
import in.securelearning.lil.android.app.databinding.FragmentDemoCourseListBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.SlidingTabLayout;
import in.securelearning.lil.android.courses.InjectorCourses;
import in.securelearning.lil.android.courses.models.ItemListModel;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
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
 * A activity representing a list of Items.
 * <p/>
 * Activities containing this activity MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DemoCourseFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static SlidingTabLayout slidingTabLayout;
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
    String mSearchQuery = "";

    /**
     * Mandatory empty constructor for the activity manager to instantiate the
     * activity (e.g. upon screen orientation changes).
     */
    public DemoCourseFragment() {
    }

    @SuppressWarnings("unused")
    public static DemoCourseFragment newInstance(int columnCount, SlidingTabLayout mSlidingTabLayout) {
        DemoCourseFragment fragment = new DemoCourseFragment();
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
            public void accept(Object event) {
                if (event instanceof ObjectDownloadComplete) {
                    getData();
                } else if (event instanceof SearchSubmitEvent) {
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
            mCourseAdapter = new DemoCourseRecyclerViewAdapter(new ArrayList<Course>());
            recyclerView.setAdapter(mCourseAdapter);

        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            mCourseAdapter = new DemoCourseRecyclerViewAdapter(new ArrayList<Course>());
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
        mItemListModel.getCoursesList().subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<Course>() {
                    @Override
                    public void accept(Course course) {
                        addItemToRecyclerView(course);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("DCF", "OOM");
                        throwable.printStackTrace();
                    }
                });
    }

    private void addItemToRecyclerView(final Course course) {
        mCourseAdapter.additem(course);
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

        private final List<Course> mValues;
        private final List<Course> mPermanentValues;
        private final List<String> mIds;

        public DemoCourseRecyclerViewAdapter(List<Course> values) {
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
        public DemoCourseRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FragmentDemoCourseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_demo_course, parent, false);
            return new DemoCourseRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final DemoCourseRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            String imagePath = holder.mItem.getThumbnail().getThumb();
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getThumbnail().getUrl();
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
            holder.mBinding.textviewCourseDescription.setText(holder.mItem.getDescription());
            holder.mBinding.textviewViewsCount.setText(holder.mItem.getReviews().getTotalViews() + "");
            holder.mBinding.textviewCourseDate.setText(DateUtils.getTimeStringFromDateString(holder.mItem.getPublishedDate()) + " ago");

            // TODO: 2/8/2017  start course detail activity
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
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
                    }
                }
            });

            holder.mBinding.assignCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            });

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
                typeImage = R.drawable.videio_course_white;
            }
            holder.mBinding.textviewCourseType.setText(type);
            holder.mBinding.imageviewCourseType.setImageResource(typeImage);
        }

        @Override
        public int getItemCount() {
            if (mValues != null) {
                return mValues.size();
            }
            return 0;
        }

        public void additem(final Course course) {
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
