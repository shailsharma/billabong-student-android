package in.securelearning.lil.android.analytics.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.analytics.dataobjects.PerformanceChartData;
import in.securelearning.lil.android.analytics.fragment.StudentExcellenceFragment;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsPerformanceTopicItemBinding;

public class StudentExcellenceAdapter extends RecyclerView.Adapter<StudentExcellenceAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<PerformanceChartData> mList;
    private StudentExcellenceFragment mFragment;

    public StudentExcellenceAdapter(Context context, ArrayList<PerformanceChartData> performanceChartDataList, StudentExcellenceFragment fragment) {
        mContext = context;
        this.mList = performanceChartDataList;
        this.mFragment = fragment;
    }

    @NonNull
    @Override
    public StudentExcellenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAnalyticsPerformanceTopicItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_analytics_performance_topic_item, parent, false);
        return new StudentExcellenceAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentExcellenceAdapter.ViewHolder holder, int position) {
        PerformanceChartData performanceChartData = mList.get(position);

        holder.mBinding.textViewTopicName.setText(performanceChartData.getName());

        String performance = (Math.round(performanceChartData.getPerformance()) + "%");
        holder.mBinding.textViewTopicPerformance.setText(performance);

        if (mFragment != null) {
            holder.mBinding.progressBar.
                    setProgressTintList(ColorStateList.valueOf
                            (((mFragment.pickColorAccording
                                    (performanceChartData.
                                            getPerformance())))));

        }

        holder.mBinding.progressBar.setMax(100);
        holder.mBinding.progressBar.setProgress((int) performanceChartData.getPerformance());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutAnalyticsPerformanceTopicItemBinding mBinding;

        public ViewHolder(LayoutAnalyticsPerformanceTopicItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

}


