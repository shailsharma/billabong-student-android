package in.securelearning.lil.android.courses.views.fragment;

import android.databinding.DataBindingUtil;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentDownloadCourseBinding;
import in.securelearning.lil.android.app.databinding.VideoListItemBinding;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.predictions.Predicate;
import in.securelearning.lil.android.base.predictions.PredicateListFilter;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.courses.InjectorCourses;
import in.securelearning.lil.android.courses.models.ItemListModel;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.events.CourseDeleteEvent;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class DownloadCourseFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    FragmentDownloadCourseBinding mBinding;
    private int mColumnCount = 1;
    private DownloadedRecyclerViewAdapter mDownloadedRecyclerViewAdapter;
    private Disposable mDownloadedSubscription;
    private Disposable mSubscription;
    @Inject
    ItemListModel mItemListModel;
    @Inject
    public RxBus mRxBus;
    String mSearchQuery = "";

    public DownloadCourseFragment() {

    }


    public static DownloadCourseFragment newInstance(int colCount) {
        DownloadCourseFragment fragment = new DownloadCourseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, colCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unSubscribe();
        if (mDownloadedRecyclerViewAdapter != null) {
            mDownloadedRecyclerViewAdapter.clear();
            mDownloadedRecyclerViewAdapter = null;
        }
    }

    private void unSubscribe() {
        Log.e("CFN", "Disposing");
        if (mSubscription != null)
            mSubscription.dispose();
        if (mDownloadedSubscription != null)
            mDownloadedSubscription.dispose();
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
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_download_course, container, false);
        if (mColumnCount > 1) {
            final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
            mBinding.list.setLayoutManager(mLayoutManager);
        } else {
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mBinding.list.setLayoutManager(mLayoutManager);
        }

        if (mDownloadedRecyclerViewAdapter == null)
            mDownloadedRecyclerViewAdapter = new DownloadedRecyclerViewAdapter(new ArrayList<Course>());
        mBinding.list.setAdapter(mDownloadedRecyclerViewAdapter);
        getDownloadedData();
        listenRxBusEvents();
        return mBinding.getRoot();
    }

    private void getDownloadedData() {
        mDownloadedSubscription = mItemListModel.getCoursesList().subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<Course>() {
                    @Override
                    public void accept(Course course) {
                        if (mDownloadedRecyclerViewAdapter != null) {
                            mDownloadedRecyclerViewAdapter.addItem(course);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("DCF", "OOM");
                        throwable.printStackTrace();
                    }
                });
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) {
                if (event instanceof ObjectDownloadComplete) {
                    getDownloadedData();
                } else if (event instanceof CourseDeleteEvent) {
                    if (mDownloadedRecyclerViewAdapter != null) {
                        mDownloadedRecyclerViewAdapter.removeItem(((CourseDeleteEvent) event).getId());
                    }

                }
            }
        });
    }

    public class DownloadedRecyclerViewAdapter extends RecyclerView.Adapter<DownloadCourseFragment.DownloadedRecyclerViewAdapter.ViewHolder> {

        private List<Course> mValues;
        private List<Course> mPermanentValues;
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

        @Override
        public DownloadCourseFragment.DownloadedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            VideoListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_list_item, parent, false);
            return new DownloadCourseFragment.DownloadedRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final DownloadCourseFragment.DownloadedRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            setThumbnail(holder.mItem, holder.mBinding.imageViewThumbnail);
            holder.mBinding.textViewVideoDuration.setVisibility(View.GONE);

            holder.mBinding.text.setText(holder.mItem.getTitle());
            if (!TextUtils.isEmpty(holder.mItem.getMetaInformation().getTopic().getName()))
                holder.mBinding.textViewTopic.setText(holder.mItem.getMetaInformation().getTopic().getName());


            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Course item = holder.mItem;
                    if (item instanceof MicroLearningCourse) {
                        startActivity(RapidLearningSectionListActivity.getStartIntent(getContext(), item.getObjectId()));
                    } else {
                        startActivity(CourseDetailActivity.getStartActivityIntent(getContext(), item.getObjectId(), item.getClass(), ""));

                    }
                }
            });


        }

        private void setThumbnail(Course item, ImageView imageView) {
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
            } catch (Exception e) {
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
        }

        public void addItem(final Course course) {
            if (!mIds.contains(course.getObjectId())) {
                mPermanentValues.add(course);
                mIds.add(course.getObjectId());
                if (TextUtils.isEmpty(mSearchQuery) || course.getSearchableText().contains(mSearchQuery)) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    mBinding.layoutProgress.setVisibility(View.GONE);
                                    if (mBinding != null && !mPermanentValues.isEmpty()) {
                                        mBinding.list.setVisibility(View.VISIBLE);
                                        mBinding.layoutNoResult.setVisibility(View.GONE);
                                    } else {
                                        mBinding.list.setVisibility(View.GONE);
                                        mBinding.layoutNoResult.setVisibility(View.VISIBLE);
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
                                        mBinding.list.setVisibility(View.GONE);
                                    }

                                }
                            }
                        }
                    });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final VideoListItemBinding mBinding;
            public Course mItem;


            public ViewHolder(VideoListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }

        }
    }

}
