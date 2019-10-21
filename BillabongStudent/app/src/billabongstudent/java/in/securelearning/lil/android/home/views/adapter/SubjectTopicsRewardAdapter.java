package in.securelearning.lil.android.home.views.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementTopicItemBinding;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentTopicReward;

/*To set/show data Reward/Billabucks in SubjectTopics*/
public class SubjectTopicsRewardAdapter extends RecyclerView.Adapter<SubjectTopicsRewardAdapter.ViewHolder> {

    private ArrayList<StudentTopicReward> mList;
    private Context mContext;
    private int mExpandedPosition = 0;//already expanded position in recycler view; by default it is 0th position

    public SubjectTopicsRewardAdapter(Context context, ArrayList<StudentTopicReward> list) {
        mList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public SubjectTopicsRewardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutMyAchievementTopicItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_topic_item, parent, false);
        return new SubjectTopicsRewardAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubjectTopicsRewardAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        StudentTopicReward topic = mList.get(position);
        if (!TextUtils.isEmpty(topic.getSubjectName())) {
            holder.mBinding.textViewTopicName.setVisibility(View.VISIBLE);
            holder.mBinding.textViewTopicName.setText(topic.getSubjectName());
        } else {
            holder.mBinding.textViewTopicName.setVisibility(View.GONE);
        }
        holder.mBinding.textViewScore.setText(String.valueOf(topic.getPointsRewarded()));

        holder.mBinding.textViewEurosLearn.setText(String.valueOf(topic.getLearnEuros()));
        holder.mBinding.textViewEurosPractice.setText(String.valueOf(topic.getPracticeEuros()));
        holder.mBinding.textViewEurosReinforce.setText(String.valueOf(topic.getReinforceEuros()));
        holder.mBinding.textViewEurosApplication.setText(String.valueOf(topic.getApplicationEuros()));
        holder.mBinding.textViewEurosMiscellaneous.setText(String.valueOf(topic.getMiscellaneousEuros()));

        if (mExpandedPosition == position) {
            holder.mBinding.layoutLRPAM.setVisibility(View.VISIBLE);
        } else {
            holder.mBinding.layoutLRPAM.setVisibility(View.GONE);
        }

        holder.mBinding.layoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        mExpandedPosition = position;
                        notifyDataSetChanged();

                    }
                }, 300);
            }
        });


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