package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import in.securelearning.lil.android.base.comparators.SortResource;
import in.securelearning.lil.android.base.dataobjects.FilterList;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.home.interfaces.Filterable;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Resource} and makes a call to the
 * specified {@link ResourceFragment.OnResourceFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ResourceRecyclerViewAdapter extends RecyclerView.Adapter<ResourceRecyclerViewAdapter.ViewHolder> implements Filterable {
    public static final String[] FILTER_BY_LIST = {"Images", "Videos"};
    public static final String[] SORT_BY_LIST = {"by Title", "by Size"};
    private static FilterList filterList;

    private final List<Resource> mValues;
    private final List<Resource> mPermanentValues = new ArrayList<>();
    private final HashMap<String, Resource> mObjects;
    private final ResourceFragment.OnResourceFragmentInteractionListener mListener;

    public ResourceRecyclerViewAdapter(List<Resource> items, ResourceFragment.OnResourceFragmentInteractionListener listener) {
        mPermanentValues.addAll(items);
        mValues = items;
        mListener = listener;
        mObjects = getMapFromList(mValues);
    }

    public static FilterList getFilter() {
        if (filterList == null) {
            filterList = buildFilterListWithTitle("SkillMasteryFilter Resource");
        }
        return filterList;
    }

    private static FilterList buildFilterList(Context context) {
        String title = context.getResources().getString(R.string.filter_title_resources);
        FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
        return builder.addSection(new FilterList.SectionBuilder()
                .addSectionItems(ResourceRecyclerViewAdapter.FILTER_BY_LIST)
                .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
                .sectionTitle("SkillMasteryFilter By")
                .build())
                .addSection(new FilterList.SectionBuilder()
                        .addSectionItems(ResourceRecyclerViewAdapter.SORT_BY_LIST)
                        .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
                        .sectionTitle("Sort By")
                        .build())
                .title(title)
                .build();
    }

    private static FilterList buildFilterListWithTitle(String title) {
        FilterList.FilterBuilder builder = new FilterList.FilterBuilder();
        return builder.addSection(new FilterList.SectionBuilder()
                .addSectionItems(ResourceRecyclerViewAdapter.FILTER_BY_LIST)
                .sectionType(FilterList.SECTION_SELECTION_TYPE_CHECKBOX)
                .sectionTitle("SkillMasteryFilter By")
                .build())
                .addSection(new FilterList.SectionBuilder()
                        .addSectionItems(ResourceRecyclerViewAdapter.SORT_BY_LIST)
                        .sectionType(FilterList.SECTION_SELECTION_TYPE_RADIO)
                        .sectionTitle("Sort By")
                        .build())
                .title(title)
                .build();
    }

    private HashMap<String, Resource> getMapFromList(List<Resource> values) {
        HashMap<String, Resource> map = new HashMap<>();
        for (Resource resource :
                values) {
            map.put(resource.getName(), resource);
        }
        return map;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_resource_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(holder.mItem.getTitle());
        if (!holder.mItem.getUrlThumbnail().isEmpty()) {

            try {
                Picasso.with(holder.mView.getContext()).load(holder.mItem.getUrlThumbnail()).into(holder.mImageView);
            } catch (Exception e) {

            }
        } else {
            int resId;
            if (holder.mItem.getType().toLowerCase().equals("image")) {
                resId = R.drawable.image_large;
            } else {
                resId = R.drawable.video_large;
            }
            try {
                Picasso.with(holder.mView.getContext()).load(resId).into(holder.mImageView);
            } catch (Exception e) {

            }
        }

        holder.mTypeView.setText(holder.mItem.getType());
//        holder.mIdView.setText(mValues.get(position).getName());
//        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onResourceFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addValues(List<Resource> values) {
        if (values != null && mObjects != null && mValues != null && mPermanentValues != null) {
            for (Resource resource :
                    values) {
                if (!mObjects.containsKey(resource.getName())) {
                    mObjects.put(resource.getName(), resource);
                    mPermanentValues.add(resource);
                    mValues.add(resource);
                }
            }
            filter();
            sort();
        }
    }

    public void setValues(List<Resource> values) {
        if (values != null && mObjects != null && mValues != null && mPermanentValues != null) {
            mObjects.clear();
            mValues.clear();
            mPermanentValues.clear();
            for (Resource resource :
                    values) {
                mValues.add(resource);
                mObjects.put(resource.getName(), resource);
                mPermanentValues.add(resource);
            }
            filter();
            sort();
        }
    }

    public void applyFilter() {
        filter();
        sort();
    }

    @Override
    public void filter() {
        boolean noneApplied = true;
        mValues.clear();
        if (filterList != null) {
            if (filterList.getSections().get(0).getItems().get(0).isSelected()) {
                noneApplied = false;
                for (Resource resource :
                        mPermanentValues) {
                    if (resource.getType().toLowerCase().equals("image")) {
                        mValues.add(resource);
                    }
                }
            }
            if (filterList.getSections().get(0).getItems().get(1).isSelected()) {
                noneApplied = false;
                for (Resource resource :
                        mPermanentValues) {
                    if (resource.getType().toLowerCase().equals("video")) {
                        mValues.add(resource);
                    }
                }
            }

        }
        if (noneApplied) {
            mValues.addAll(mPermanentValues);
        }
        notifyDataSetChanged();
    }

    public void sort() {
        if (filterList != null) {
            if (filterList.getSections().get(1).getItems().get(0).isSelected()) {
                Collections.sort(mValues, new SortResource.TitleSorter());
            } else if (filterList.getSections().get(1).getItems().get(1).isSelected()) {
                Collections.sort(mValues, new SortResource.SizeSorter());
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mTypeView;
        public final ImageView mImageView;
        public Resource mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.textview_title);
            mTypeView = (TextView) view.findViewById(R.id.textview_type);
            mImageView = (ImageView) view.findViewById(R.id.image_view);
        }

    }
}
