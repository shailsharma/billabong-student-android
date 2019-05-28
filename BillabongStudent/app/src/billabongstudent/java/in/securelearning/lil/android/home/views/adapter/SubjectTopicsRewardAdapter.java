package in.securelearning.lil.android.home.views.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementTopicItemBinding;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentSubjectReward;

public class SubjectTopicsRewardAdapter extends RecyclerView.Adapter<SubjectTopicsRewardAdapter.ViewHolder> {
    private ArrayList<StudentSubjectReward> mList;

    public SubjectTopicsRewardAdapter(ArrayList<StudentSubjectReward> list) {
        this.mList = list;

    }

    @NonNull
    @Override
    public SubjectTopicsRewardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutMyAchievementTopicItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_topic_item, parent, false);
        return new SubjectTopicsRewardAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubjectTopicsRewardAdapter.ViewHolder holder, int position) {
        StudentSubjectReward topicList = mList.get(position);
        if (!TextUtils.isEmpty(topicList.getSubjectName())) {
            holder.mBinding.textViewTopicName.setVisibility(View.VISIBLE);
            holder.mBinding.textViewTopicName.setText(topicList.getSubjectName());
        } else {
            holder.mBinding.textViewTopicName.setVisibility(View.GONE);
        }
        holder.mBinding.textViewScore.setText(String.valueOf(topicList.getPointsRewarded()));


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutMyAchievementTopicItemBinding mBinding;

        public ViewHolder(LayoutMyAchievementTopicItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

}



