package in.securelearning.lil.android.analytics.views.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import in.securelearning.lil.android.analytics.dataobjects.EffortvsPerformanceData;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutStudentAnalyticsPerformanceItemBinding;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;

public class StudentPerformanceAdapter extends RecyclerView.Adapter<StudentPerformanceAdapter.ViewHolder> {


    private ArrayList<EffortvsPerformanceData> subjectList;
    private Context mContext;


    public StudentPerformanceAdapter(ArrayList<EffortvsPerformanceData> subjectList, Context context) {
        this.subjectList = subjectList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public StudentPerformanceAdapter.ViewHolder onCreateViewHolder(@NotNull final ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutStudentAnalyticsPerformanceItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_student_analytics_performance_item, parent, false);
        return new StudentPerformanceAdapter.ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StudentPerformanceAdapter.ViewHolder holder, final int position) {

        if (subjectList != null) {
            EffortvsPerformanceData subjectData = subjectList.get(position);

            EffortvsPerformanceData.TimeResponse time = subjectList.get(position).getTimeResponseList();

            if (subjectData != null) {

                if (!TextUtils.isEmpty(subjectData.getName())) {
                    holder.mBinding.textViewSubjectName.setVisibility(View.VISIBLE);
                    holder.mBinding.textViewSubjectName.setText((subjectData.getName()));
                } else {
                    holder.mBinding.textViewSubjectName.setVisibility(View.GONE);

                }
                if (!TextUtils.isEmpty(subjectData.getThumbnailUrl())) {
                    Picasso.with(mContext).load(subjectData.getThumbnailUrl()).placeholder(R.drawable.icon_book).fit().centerCrop().into(holder.mBinding.imageSubject);
                } else {
                    Picasso.with(mContext).load(R.drawable.icon_book).placeholder(R.drawable.icon_book).fit().centerCrop().into(holder.mBinding.imageSubject);
                }

                holder.mBinding.progress.setVisibility(View.VISIBLE);
                holder.mBinding.textViewPercentage.setVisibility(View.VISIBLE);
                holder.mBinding.progress.setProgress((int) (subjectData.getPercentage()));
                holder.mBinding.textViewPercentage.setText(Math.round(subjectData.getPercentage()) + "%");

                if (time != null) {
                    holder.mBinding.textViewTime.setVisibility(View.VISIBLE);
                    StringBuffer totalTime = new StringBuffer();

                    totalTime.append(CommonUtils.getInstance().convertSecondToHourMinuteSecond((long) time.getTotalTime() * 60))
                            .append(" Hours ");

                    if (time.getAvgDaily() != 0) {
                        totalTime.append("\n(Avg. Time ").append(Math.round(time.getAvgDaily())).append(" Min.)");
                    }
                    holder.mBinding.textViewTime.setText(totalTime);
                } else {
                    holder.mBinding.textViewTime.setVisibility(View.GONE);
                }
                holder.mBinding.textViewCoverage.setVisibility(View.VISIBLE);
                String coverage = "\n" + Math.round(subjectList.get(position).getCoverage()) + "% Progress";
                holder.mBinding.textViewCoverage.setText(coverage);


            }
        } else {
            holder.mBinding.getRoot().setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        if (subjectList != null && !subjectList.isEmpty()) {
            return subjectList.size();
        } else {
            return 0;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LayoutStudentAnalyticsPerformanceItemBinding mBinding;

        public ViewHolder(LayoutStudentAnalyticsPerformanceItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
