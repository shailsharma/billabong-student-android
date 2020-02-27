package in.securelearning.lil.android.analytics.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.analytics.dataobjects.EffortChartData;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsEffortTopicItemBinding;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;

public class StudentEffortAdapter extends RecyclerView.Adapter<StudentEffortAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<EffortChartData> mList;


    public StudentEffortAdapter(Context context, ArrayList<EffortChartData> effortChartDataList) {
        mContext = context;
        this.mList = effortChartDataList;
    }

    @NonNull
    @Override
    public StudentEffortAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAnalyticsEffortTopicItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_analytics_effort_topic_item, parent, false);
        return new StudentEffortAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentEffortAdapter.ViewHolder holder, int position) {
        EffortChartData effortChartData = mList.get(position);
        holder.mBinding.textViewTopicName.setText(effortChartData.getTopic().get(0).getName());
        holder.mBinding.textViewTopicTime.setText(CommonUtils.getInstance().convertSecondToHourMinuteSecond((long) effortChartData.getTotalTimeSpent()));
        holder.mBinding.progressBar.setVisibility(View.GONE);
        holder.mBinding.progressBar.setMax((int) effortChartData.getTotalTimeSpent());
        holder.mBinding.progressBar.setProgress((int) effortChartData.getTotalTimeSpent());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutAnalyticsEffortTopicItemBinding mBinding;

        public ViewHolder(LayoutAnalyticsEffortTopicItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}