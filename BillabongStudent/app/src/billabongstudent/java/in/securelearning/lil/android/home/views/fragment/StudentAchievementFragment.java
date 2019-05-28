package in.securelearning.lil.android.home.views.fragment;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutDialogSubjectItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementsBinding;
import in.securelearning.lil.android.home.views.adapter.SubjectTopicsRewardAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentSubjectReward;

public class StudentAchievementFragment extends Fragment {

    public static final String STUDENT_ACHIEVEMENT = "studentAchievement";
    LayoutMyAchievementsBinding mBinding;

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

    private void setRewards(final ArrayList<StudentSubjectReward> rewardsList, int totalRewards) {

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

    private void hideDividerForLastIndex(int size, int position, View viewDivider) {
        if (size == position) {
            viewDivider.setVisibility(View.GONE);
        } else {
            viewDivider.setVisibility(View.VISIBLE);
        }
    }

    private void setSubjectTopicsRewards(String subjectName, String score, String thumbnailUrl, ArrayList<StudentSubjectReward> topicList) {
        if (topicList != null && !topicList.isEmpty()) {
            final Dialog dialog = new Dialog(getContext());

            LayoutDialogSubjectItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_dialog_subject_item, null, false);
            dialog.setCancelable(true);
            dialog.setContentView(binding.getRoot());
            binding.textViewTopicName.setText(subjectName);
            binding.textViewScore.setText(score);
            if (!TextUtils.isEmpty(thumbnailUrl)) {
                Picasso.with(getContext()).load(thumbnailUrl).placeholder(R.drawable.icon_book).fit().centerCrop().into(binding.imageViewThumbnail);
            } else {
                Picasso.with(getContext()).load(R.drawable.icon_book).placeholder(R.drawable.icon_book).fit().centerCrop().into(binding.imageViewThumbnail);
            }
            binding.recyclerViewTopic.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            binding.recyclerViewTopic.setNestedScrollingEnabled(false);
            binding.recyclerViewTopic.setAdapter(new SubjectTopicsRewardAdapter(topicList));

            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }
    }

    private class RewardsRecyclerViewAdapter extends RecyclerView.Adapter<RewardsRecyclerViewAdapter.ViewHolder> {
        private ArrayList<StudentSubjectReward> mList;

        private RewardsRecyclerViewAdapter(ArrayList<StudentSubjectReward> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public RewardsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMyAchievementItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_item, parent, false);
            return new RewardsRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RewardsRecyclerViewAdapter.ViewHolder holder, final int position) {
            final StudentSubjectReward studentSubjectReward = mList.get(position);
            final String subjectName = studentSubjectReward.getSubjectName();
            final String thumbnailUrl = studentSubjectReward.getThumbnailUrl();
            hideDividerForLastIndex(mList.size() - 1, position, holder.mBinding.viewDivider);
            holder.mBinding.textViewName.setText(studentSubjectReward.getSubjectName());
            final String rewards = "+" + String.valueOf(studentSubjectReward.getPointsRewarded());
            holder.mBinding.textViewScore.setText(rewards);
            // holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_rewards);
            // holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            if (!TextUtils.isEmpty(thumbnailUrl)) {
                Picasso.with(getContext()).load(thumbnailUrl).placeholder(R.drawable.icon_book).fit().centerCrop().into(holder.mBinding.imageViewIcon);
            } else {
                Picasso.with(getContext()).load(R.drawable.icon_book).placeholder(R.drawable.icon_book).fit().centerCrop().into(holder.mBinding.imageViewIcon);
            }
            // trello.com/c/0VRYno15/267-as-a-student-i-want-reward-points-to-be-show-topic-wise start
            //need to pass topic list related to subjects ,
            holder.mBinding.getRoot().
                    setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               ArrayList<StudentSubjectReward> topicRewardList = mList.get(position).getTopicRewardList();

                                               setSubjectTopicsRewards(subjectName, rewards, thumbnailUrl, topicRewardList);

                                           }
                                       }
                    );


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
            hideDividerForLastIndex(mList.size() - 1, position, holder.mBinding.viewDivider);
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
            String badge = mList.get(position);
            hideDividerForLastIndex(mList.size() - 1, position, holder.mBinding.viewDivider);
            holder.mBinding.textViewName.setText(badge);
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

