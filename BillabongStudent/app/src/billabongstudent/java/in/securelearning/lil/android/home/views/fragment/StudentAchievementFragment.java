package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementsBinding;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentReward;

public class StudentAchievementFragment extends Fragment {

    LayoutMyAchievementsBinding mBinding;
    public static final String STUDENT_ACHIEVEMENT = "studentAchievement";

    public static StudentAchievementFragment newInstance(StudentAchievement studentAchievement) {
        StudentAchievementFragment fragment = new StudentAchievementFragment();
        Bundle args = new Bundle();
        args.putSerializable(STUDENT_ACHIEVEMENT, studentAchievement);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_my_achievements, container, false);
        setAchievements();
        return mBinding.getRoot();
    }

    private void setAchievements() {
        if (getArguments() != null) {

            StudentAchievement studentAchievement = (StudentAchievement) getArguments().getSerializable(STUDENT_ACHIEVEMENT);
            if (studentAchievement != null) {
                setRewards(studentAchievement.getRewardsList(), studentAchievement.getTotalRewards());
                setTrophies(createTrophiesData(), createTrophiesData().size());
                setBadges(createBadgesData(), createBadgesData().size());
            } else {
                mBinding.layoutContent.setVisibility(View.GONE);
                mBinding.layoutError.setVisibility(View.VISIBLE);
            }
        } else {
            mBinding.layoutContent.setVisibility(View.GONE);
            mBinding.layoutError.setVisibility(View.VISIBLE);
        }
    }

    private void setRewards(final ArrayList<StudentReward> rewardsList, int totalRewards) {

        mBinding.textViewTotalRewards.setText(String.valueOf(totalRewards));
        mBinding.recyclerViewRewards.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewRewards.setNestedScrollingEnabled(false);
        mBinding.recyclerViewRewards.setAdapter(new RewardsRecyclerViewAdapter(rewardsList));

        mBinding.layoutRewardHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.layoutRewardsChild.isCollapsed()) {
                    if (!rewardsList.isEmpty()) {
                        mBinding.layoutRewardsChild.expand(true);
                        mBinding.imageViewRewardsIndicator.setImageResource(R.drawable.chevron_down_white);
                        mBinding.imageViewRewardsIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));

                        mBinding.layoutTrophiesChild.collapse(true);
                        mBinding.imageViewTrophiesIndicator.setImageResource(R.drawable.chevron_right_white);
                        mBinding.imageViewTrophiesIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));

                        mBinding.layoutBadgesChild.collapse(true);
                        mBinding.imageViewBadgesIndicator.setImageResource(R.drawable.chevron_right_white);
                        mBinding.imageViewBadgesIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));

                    }
                } else {
                    mBinding.layoutRewardsChild.collapse(true);
                    mBinding.imageViewRewardsIndicator.setImageResource(R.drawable.chevron_right_white);
                    mBinding.imageViewRewardsIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!rewardsList.isEmpty()) {
                    mBinding.layoutRewardsChild.expand(true);
                    mBinding.imageViewRewardsIndicator.setImageResource(R.drawable.chevron_down_white);
                    mBinding.imageViewRewardsIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
                }
            }
        }, 200);
    }

    private void setBadges(final ArrayList<String> badgesList, int totalBadges) {

        mBinding.textViewTotalBadges.setText(String.valueOf(totalBadges));
        mBinding.recyclerViewBadges.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewBadges.setNestedScrollingEnabled(false);
        mBinding.recyclerViewBadges.setAdapter(new BadgesRecyclerViewAdapter(badgesList));
        mBinding.layoutBadgesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.layoutBadgesChild.isCollapsed()) {
                    if (!badgesList.isEmpty()) {
                        mBinding.layoutBadgesChild.expand(true);
                        mBinding.imageViewBadgesIndicator.setImageResource(R.drawable.chevron_down_white);
                        mBinding.imageViewBadgesIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));

                        mBinding.layoutRewardsChild.collapse(true);
                        mBinding.imageViewRewardsIndicator.setImageResource(R.drawable.chevron_right_white);
                        mBinding.imageViewRewardsIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));

                        mBinding.layoutTrophiesChild.collapse(true);
                        mBinding.imageViewTrophiesIndicator.setImageResource(R.drawable.chevron_right_white);
                        mBinding.imageViewTrophiesIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
                    }
                } else {
                    mBinding.layoutBadgesChild.collapse(true);
                    mBinding.imageViewBadgesIndicator.setImageResource(R.drawable.chevron_right_white);
                    mBinding.imageViewBadgesIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
                }
            }
        });
    }

    private void setTrophies(final ArrayList<String> trophiesList, int totalTrophies) {

        mBinding.textViewTotalTrophies.setText(String.valueOf(totalTrophies));
        mBinding.recyclerViewTrophies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewTrophies.setNestedScrollingEnabled(false);
        mBinding.recyclerViewTrophies.setAdapter(new TrophiesRecyclerViewAdapter(trophiesList));
        mBinding.layoutTrophiesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.layoutTrophiesChild.isCollapsed()) {
                    if (!trophiesList.isEmpty()) {
                        mBinding.layoutTrophiesChild.expand(true);
                        mBinding.imageViewTrophiesIndicator.setImageResource(R.drawable.chevron_down_white);
                        mBinding.imageViewTrophiesIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));

                        mBinding.layoutRewardsChild.collapse(true);
                        mBinding.imageViewRewardsIndicator.setImageResource(R.drawable.chevron_right_white);
                        mBinding.imageViewRewardsIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));

                        mBinding.layoutBadgesChild.collapse(true);
                        mBinding.imageViewBadgesIndicator.setImageResource(R.drawable.chevron_right_white);
                        mBinding.imageViewBadgesIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
                    }
                } else {
                    mBinding.layoutTrophiesChild.collapse(true);
                    mBinding.imageViewTrophiesIndicator.setImageResource(R.drawable.chevron_right_white);
                    mBinding.imageViewTrophiesIndicator.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
                }
            }
        });
    }

    private ArrayList<String> createTrophiesData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Best Artist of the month");
        list.add("Art work of the week");
        list.add("Best Writing");
        list.add("Star of Month");
        return list;
    }

    private ArrayList<String> createBadgesData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Perfect");
        list.add("Excellent Work");
        list.add("Brilliant");
        list.add("Well Done");
        return list;
    }

    private class RewardsRecyclerViewAdapter extends RecyclerView.Adapter<RewardsRecyclerViewAdapter.ViewHolder> {
        private ArrayList<StudentReward> mList;

        private RewardsRecyclerViewAdapter(ArrayList<StudentReward> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public RewardsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMyAchievementItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_item, parent, false);
            return new RewardsRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RewardsRecyclerViewAdapter.ViewHolder holder, int position) {
            StudentReward studentReward = mList.get(position);
            holder.mBinding.textViewName.setText(studentReward.getSubjectName());
            String rewards = "+" + String.valueOf(studentReward.getPointsRewarded());
            holder.mBinding.textViewScore.setText(rewards);
            holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_rewards);
            holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementItemBinding mBinding;

            public ViewHolder(LayoutMyAchievementItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

    private class TrophiesRecyclerViewAdapter extends RecyclerView.Adapter<TrophiesRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String> mList;

        private TrophiesRecyclerViewAdapter(ArrayList<String> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public TrophiesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMyAchievementItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_item, parent, false);
            return new TrophiesRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final TrophiesRecyclerViewAdapter.ViewHolder holder, int position) {
            String trophy = mList.get(position);
            holder.mBinding.textViewName.setText(trophy);
            holder.mBinding.textViewScore.setVisibility(View.INVISIBLE);
            holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_trophies);
            holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementItemBinding mBinding;

            public ViewHolder(LayoutMyAchievementItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

    private class BadgesRecyclerViewAdapter extends RecyclerView.Adapter<BadgesRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String> mList;

        private BadgesRecyclerViewAdapter(ArrayList<String> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public BadgesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMyAchievementItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_item, parent, false);
            return new BadgesRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final BadgesRecyclerViewAdapter.ViewHolder holder, int position) {
            String trophy = mList.get(position);
            holder.mBinding.textViewName.setText(trophy);
            holder.mBinding.textViewScore.setVisibility(View.INVISIBLE);
            holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_badges);
            holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementItemBinding mBinding;

            public ViewHolder(LayoutMyAchievementItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

}
