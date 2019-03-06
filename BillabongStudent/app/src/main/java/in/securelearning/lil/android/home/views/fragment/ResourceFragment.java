package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnResourceFragmentInteractionListener}
 * interface.
 */
public class ResourceFragment extends Fragment {
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    @Inject
    HomeModel mHomeModel;
    private Disposable mSubscription;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnResourceFragmentInteractionListener mListener;
    private ResourceRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ResourceFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ResourceFragment newInstance(int columnCount) {
        ResourceFragment fragment = new ResourceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResourceFragmentInteractionListener) {
            mListener = (OnResourceFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnResourceFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource_list, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new ResourceRecyclerViewAdapter(mHomeModel.getResourceFileListFromInternalStorage(), mListener);
            mSubscription = Observable.create(new ObservableOnSubscribe<ArrayList<Resource>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<Resource>> subscriber) {
                    subscriber.onNext(mHomeModel.getFtpFileList());
                    subscriber.onComplete();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<Resource>>() {
                        @Override
                        public void accept(ArrayList<Resource> resources) {
                            mAdapter.addValues(resources);
                        }
                    });
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    public void filter() {
        if (mAdapter != null) mAdapter.applyFilter();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mSubscription != null) mSubscription.dispose();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnResourceFragmentInteractionListener {
        // TODO: Update argument type and name
        void onResourceFragmentInteraction(Resource item);
    }

    /**
     * {@link RecyclerView.Adapter} that can display a {@link Resource} and makes a call to the
     * specified {@link ResourceFragment.OnResourceFragmentInteractionListener}.
     * TODO: Replace the implementation with code for your data type.
     */
//    public static class ResourceRecyclerViewAdapter extends RecyclerView.Adapter<ResourceRecyclerViewAdapter.ViewHolder> implements Filterable {
//        public static final String[] FILTER_BY_LIST = {"Images", "Videos"};
//        public static final String[] SORT_BY_LIST = {"by Title", "by Size"};
//        public static FilterList filterList;
//        private final List<Resource> mValues;
//        private final List<Resource> mPermanentValues = new ArrayList<>();
//        private final HashMap<String, Resource> mObjects;
//        private final ResourceFragment.OnResourceFragmentInteractionListener mListener;
//
//        public ResourceRecyclerViewAdapter(List<Resource> items, ResourceFragment.OnResourceFragmentInteractionListener listener) {
//            mPermanentValues.addAll(items);
//            mValues = items;
//            mListener = listener;
//            mObjects = getMapFromList(mValues);
//        }
//
//        public static FilterList getFilter() {
//            if (filterList == null) {
//                filterList = buildFilterListWithTitle("SkillMasteryFilter Resource");
//            }
//            return filterList;
//        }
//
//        private static FilterList buildFilterListWithTitle(String title) {
//            FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
//            return builder.addSection(new FilterList.SectionBuilder()
//                    .addSectionItems(FILTER_BY_LIST)
//                    .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
//                    .sectionTitle("SkillMasteryFilter By")
//                    .build())
//                    .addSection(new FilterList.SectionBuilder()
//                            .addSectionItems(SORT_BY_LIST)
//                            .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
//                            .sectionTitle("Sort By")
//                            .build())
//                    .title(title)
//                    .build();
//        }
//
//        private FilterList buildFilterList(Context context) {
//            String title = context.getResources().getString(R.string.filter_title_resources);
//            FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
//            return builder.addSection(new FilterList.SectionBuilder()
//                    .addSectionItems(FILTER_BY_LIST)
//                    .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
//                    .sectionTitle("SkillMasteryFilter By")
//                    .build())
//                    .addSection(new FilterList.SectionBuilder()
//                            .addSectionItems(SORT_BY_LIST)
//                            .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
//                            .sectionTitle("Sort By")
//                            .build())
//                    .title(title)
//                    .build();
//        }
//
//        private HashMap<String, Resource> getMapFromList(List<Resource> values) {
//            HashMap<String, Resource> map = new HashMap<>();
//            for (Resource resource :
//                    values) {
//                map.put(resource.getName(), resource);
//            }
//            return map;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.layout_resource_recycler_item, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ResourceRecyclerViewAdapter.ViewHolder holder, int position) {
//            holder.mItem = mValues.get(position);
//            holder.mTitleView.setText(holder.mItem.getTitle());
//            if (!holder.mItem.getUrlThumbnail().isEmpty()) {
//
//                try {
//                    Picasso.with(holder.mView.getContext()).load(holder.mItem.getUrlThumbnail()).into(holder.mImageView);
//                } catch (Exception e) {
//
//                }
//            } else {
//                int resId;
//                if (holder.mItem.getType().toLowerCase().equals("image")) {
//                    resId = R.drawable.image_large;
//                } else {
//                    resId = R.drawable.video_large;
//                }
//                try {
//                    Picasso.with(holder.mView.getContext()).load(resId).into(holder.mImageView);
//                } catch (Exception e) {
//
//                }
//            }
//
//            holder.mTypeView.setText(holder.mItem.getType());
////        holder.mIdView.setText(mValues.get(position).getName());
////        holder.mContentView.setText(mValues.get(position).content);
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (null != mListener) {
//                        // Notify the active callbacks interface (the activity, if the
//                        // fragment is attached to one) that an item has been selected.
//                        mListener.onResourceFragmentInteraction(holder.mItem);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//        public void addValues(List<Resource> values) {
//            if (values != null && mObjects != null && mValues != null && mPermanentValues != null) {
//                for (Resource resource :
//                        values) {
//                    if (!mObjects.containsKey(resource.getName())) {
//                        mObjects.put(resource.getName(), resource);
//                        mPermanentValues.add(resource);
//                        mValues.add(resource);
//                    }
//                }
//                filter();
//                sort();
//            }
//        }
//
//        public void setValues(List<Resource> values) {
//            if (values != null && mObjects != null && mValues != null && mPermanentValues != null) {
//                mObjects.clear();
//                mValues.clear();
//                mPermanentValues.clear();
//                for (Resource resource :
//                        values) {
//                    mValues.add(resource);
//                    mObjects.put(resource.getName(), resource);
//                    mPermanentValues.add(resource);
//                }
//                filter();
//                sort();
//            }
//        }
//
//        public void applyFilter() {
//            filter();
//            sort();
//        }
//
//        @Override
//        public void filter() {
//            boolean noneApplied = true;
//            mValues.clear();
//            if (filterList != null) {
//
//                if (filterList.getSections().get(0).getItems().get(0).isSelected()) {
//                    noneApplied = false;
//                    for (Resource resource :
//                            mPermanentValues) {
//                        if (resource.getType().toLowerCase().equals("image")) {
//                            mValues.add(resource);
//                        }
//                    }
//                }
//                if (filterList.getSections().get(0).getItems().get(1).isSelected()) {
//                    noneApplied = false;
//                    for (Resource resource :
//                            mPermanentValues) {
//                        if (resource.getType().toLowerCase().equals("video")) {
//                            mValues.add(resource);
//                        }
//                    }
//                }
//
//            }
//            if (noneApplied) {
//                mValues.addAll(mPermanentValues);
//            }
//            notifyDataSetChanged();
//        }
//
//        public void sort() {
//            if (filterList != null) {
//                if (filterList.getSections().get(1).getItems().get(0).isSelected()) {
//                    Collections.sort(mValues, new SortResource.TitleSorter());
//                } else if (filterList.getSections().get(1).getItems().get(1).isSelected()) {
//                    Collections.sort(mValues, new SortResource.SizeSorter());
//                }
//            }
//            notifyDataSetChanged();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public final View mView;
//            public final TextView mTitleView;
//            public final TextView mTypeView;
//            public final ImageView mImageView;
//            public Resource mItem;
//
//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mTitleView = (TextView) view.findViewById(R.id.textview_title);
//                mTypeView = (TextView) view.findViewById(R.id.textview_type);
//                mImageView = (ImageView) view.findViewById(R.id.image_view);
//            }
//
//        }
//    }

}
