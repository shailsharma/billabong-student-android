package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecentReadListItemBinding;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityRecentlyRead;


/**
 * Created by Rupsi on 6/23/2018.
 */

public class RecentReadAdapter extends RecyclerView.Adapter<RecentReadAdapter.MyViewHolder> {

    ArrayList<AnalysisActivityRecentlyRead> mList;
    //recentlyReadItems mList;
    String s;

    public void addData(ArrayList<AnalysisActivityRecentlyRead> list) {
        if (mList != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LayoutRecentReadListItemBinding mBinding;

        public MyViewHolder(LayoutRecentReadListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }


    public RecentReadAdapter(ArrayList<AnalysisActivityRecentlyRead> recentlyReadItems) {
        this.mList = recentlyReadItems;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutRecentReadListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_recent_read_list_item, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String url;
        final AnalysisActivityRecentlyRead readItem = mList.get(position);
        try {

            holder.mBinding.txtTitle.setText(readItem.getTitle());
            holder.mBinding.txtDesc.setText(Html.fromHtml(readItem.getDescription(), Html.FROM_HTML_MODE_COMPACT));
            //holder.mBinding.txtPercent.setText("50 %");
            //holder.mBinding.progressBar.setProgress(50);


        } catch (IndexOutOfBoundsException e) {

        }

    }


    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}