package in.securelearning.lil.android.analytics.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.analytics.activity.ProgressDetailActivity;
import in.securelearning.lil.android.analytics.dataobjects.CoverageChartData;
import in.securelearning.lil.android.analytics.fragment.StudentCoverageFragment;
import in.securelearning.lil.android.analytics.fragment.StudentExcellenceFragment;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsPerformanceTopicItemBinding;

public class StudentCoverageAdapter extends RecyclerView.Adapter<StudentCoverageAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<CoverageChartData> mList;
    private StudentCoverageFragment mFragment;

    public StudentCoverageAdapter(Context context, ArrayList<CoverageChartData> coverageChartData, StudentCoverageFragment fragment) {
        mContext = context;
        this.mList = coverageChartData;
        this.mFragment=fragment;
    }

    @NonNull
    @Override
    public StudentCoverageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAnalyticsPerformanceTopicItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_analytics_performance_topic_item, parent, false);
        return new StudentCoverageAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentCoverageAdapter.ViewHolder holder, int position) {
        CoverageChartData coverageChartData = mList.get(position);

        holder.mBinding.textViewTopicName.setText(coverageChartData.getName());

        float coveragePercent = Math.round(coverageChartData.getCoverage());

        holder.mBinding.textViewTopicPerformance.setText(String.valueOf(coveragePercent + "%"));

        if (mFragment != null) {
            holder.mBinding.progressBar.
                    setProgressTintList(ColorStateList.valueOf
                            (((mFragment.pickColorAccording
                                    (coveragePercent)))));

        }



        holder.mBinding.progressBar.setMax(100);
        holder.mBinding.progressBar.setProgress((int) coveragePercent);
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




