package in.securelearning.lil.android.courses.views.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
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


public class CourseRecommendFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    @Inject
    public RxBus mRxBus;
    @Inject
    ItemListModel mItemListModel;
    private Disposable mSubscription;
    private View mRootView;
    private RecyclerView mRecommendRecyclerView;
    private RecommendAdapter mRecommendAdapter;
    private int mColumnCount = 1;
    private static SlidingTabLayout slidingTabLayout;
    String mSearchQuery = "";

    public CourseRecommendFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static CourseRecommendFragment newInstance(int columnCount, SlidingTabLayout mSlidingTabLayout) {
        CourseRecommendFragment fragment = new CourseRecommendFragment();
        slidingTabLayout = mSlidingTabLayout;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this activity
        mRootView = inflater.inflate(R.layout.fragment_demo_course_list, container, false);
        initializeViews();
        initializeRecyclerView();
        getData();
        listenRxBusEvents();
        return mRootView;
    }


    private void initializeViews() {
        mRecommendRecyclerView = (RecyclerView) mRootView.findViewById(R.id.list);

    }

    private void initializeRecyclerView() {

        if (mColumnCount > 1) {
            final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
            mRecommendRecyclerView.setLayoutManager(mLayoutManager);

        } else {
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecommendRecyclerView.setLayoutManager(mLayoutManager);
        }

        mRecommendAdapter = new RecommendAdapter(new ArrayList<AboutCourse>());
        mRecommendRecyclerView.setAdapter(mRecommendAdapter);

    }

    private void getData() {

        mItemListModel.getRecommendedCoursesList().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<AboutCourse>() {
                    @Override
                    public void accept(AboutCourse course) {
                        mRecommendAdapter.additem(course);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("CRF", "OOM");
                        throwable.printStackTrace();
                    }
                });

//        mRecommendRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribe();
        mRecommendAdapter = null;
    }

    private void unsubscribe() {
        if (mSubscription != null)
            mSubscription.dispose();
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) {
                if (event instanceof ObjectDownloadComplete) {
                    if (mRecommendAdapter != null) {
                        if (((ObjectDownloadComplete) event).getObjectClass().equals(AboutCourse.class)) {
                            getData();
//                            mRecommendAdapter.updateSearchResultData(((ObjectDownloadComplete) event).getId(), ((ObjectDownloadComplete) event).getSyncStatus());
                        } else if (((ObjectDownloadComplete) event).getObjectClass().equals(DigitalBook.class)
                                || ((ObjectDownloadComplete) event).getObjectClass().equals(VideoCourse.class)
                                || ((ObjectDownloadComplete) event).getObjectClass().equals(InteractiveImage.class)
                                || ((ObjectDownloadComplete) event).getObjectClass().equals(PopUps.class)
                                || ((ObjectDownloadComplete) event).getObjectClass().equals(ConceptMap.class)) {
//                            getData();
                            mRecommendAdapter.updateSearchResultData(((ObjectDownloadComplete) event).getId(), ((ObjectDownloadComplete) event).getSyncStatus());
                        }

                    }

                } else if (event instanceof SearchSubmitEvent) {
                    mSearchQuery = ((SearchSubmitEvent) event).getQueryText();
                    mRecommendAdapter.search(mSearchQuery);
                } else if (event instanceof SearchOpenEvent) {
                    mSearchQuery = "";
                } else if (event instanceof SearchCloseEvent) {
                    mSearchQuery = "";
                    mRecommendAdapter.clearSearch();
                }


            }
        });
    }

    public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {

        private final List<AboutCourse> mValues;
        private final List<AboutCourse> mPermanentValues;
        private final List<String> mIds;


        public RecommendAdapter(List<AboutCourse> values) {
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_course_recommend_itemview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mItem = mValues.get(position);
            holder.mTitleTV.setText(holder.mItem.getTitle());
            holder.mSubjectTv.setText(holder.mItem.getMetaInformation().getSubjectText());
            holder.mCouseDetailTv.setText(holder.mItem.getDescription());
            holder.mPublishedOnTv.setText(DateUtils.getTimeStringFromDateString(holder.mItem.getPublishedDate()) + " ago");
            holder.mRatingBar.setRating((float) holder.mItem.getReviews().getAvgRating());
            holder.mViewsCountTv.setText("" + holder.mItem.getReviews().getTotalViews());
            String imagePath = holder.mItem.getThumbnail().getThumb();
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getThumbnail().getUrl();
            }
            if (imagePath.isEmpty()) {
                imagePath = holder.mItem.getMetaInformation().getBanner();
            }
            Class objectClass = null;
            String type = "";
            int typeImage = R.drawable.digital_book;
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

}
