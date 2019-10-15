package in.securelearning.lil.android.profile.views.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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
import java.util.Objects;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutDialogSubjectItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementBadgesItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementEurosItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementTrophiesItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMyAchievementsBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.views.adapter.SubjectTopicsRewardAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentAchievement;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentSubjectReward;
import in.securelearning.lil.android.syncadapter.dataobjects.StudentTopicReward;

public class StudentAchievementFragment extends Fragment {

    public static final String STUDENT_ACHIEVEMENT = "studentAchievement";
    private static final int EUROS_SPAN_COUNT = 4;
    private static final int TROPHIES_SPAN_COUNT = 4;
    private static final int BADGES_SPAN_COUNT = 4;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                GeneralUtils.showToastShort(getContext(), getString(R.string.error_something_went_wrong));
            }
        } else {
            mBinding.layoutContent.setVisibility(View.GONE);
            GeneralUtils.showToastShort(getContext(), getString(R.string.error_something_went_wrong));
        }
    }

    /*Rewards are now Euros*/
    private void setRewards(final ArrayList<StudentSubjectReward> rewardsList, int totalRewards) {

        mBinding.textViewTotalRewards.setText(String.valueOf(totalRewards));
        mBinding.recyclerViewRewards.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewRewards.setNestedScrollingEnabled(false);
        mBinding.recyclerViewRewards.setAdapter(new RewardsRecyclerViewAdapter(rewardsList));

        mBinding.bottomLineEuros.setVisibility(View.VISIBLE);
        setEurosVisibility(rewardsList);

        mBinding.layoutRewardHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.bottomLineEuros.setVisibility(View.VISIBLE);
                mBinding.bottomLineTrophies.setVisibility(View.GONE);
                mBinding.bottomLineBadges.setVisibility(View.GONE);


                setEurosVisibility(rewardsList);

            }
        });
    }

    /*To set visibility of label_currency_value tab for first time and other times*/
    private void setEurosVisibility(ArrayList<StudentSubjectReward> rewardsList) {
        mBinding.recyclerViewTrophies.setVisibility(View.GONE);
        mBinding.recyclerViewBadges.setVisibility(View.GONE);

        if (!rewardsList.isEmpty()) {
            mBinding.recyclerViewRewards.setVisibility(View.VISIBLE);
            mBinding.layoutEurosError.setVisibility(View.GONE);
        } else {
            mBinding.recyclerViewRewards.setVisibility(View.GONE);
            mBinding.layoutEurosError.setVisibility(View.VISIBLE);
        }
    }

    private void setTrophies(final ArrayList<String> trophiesList, int totalTrophies) {

        mBinding.textViewTotalTrophies.setText(String.valueOf(totalTrophies));
        mBinding.recyclerViewTrophies.setLayoutManager(new GridLayoutManager(getContext(), TROPHIES_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerViewTrophies.setNestedScrollingEnabled(false);
        mBinding.recyclerViewTrophies.setAdapter(new TrophiesRecyclerViewAdapter(trophiesList));

        mBinding.layoutTrophiesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.bottomLineEuros.setVisibility(View.GONE);
                mBinding.bottomLineTrophies.setVisibility(View.VISIBLE);
                mBinding.bottomLineBadges.setVisibility(View.GONE);

                mBinding.layoutEurosError.setVisibility(View.GONE);
                mBinding.recyclerViewRewards.setVisibility(View.GONE);
                mBinding.recyclerViewTrophies.setVisibility(View.VISIBLE);
                mBinding.recyclerViewBadges.setVisibility(View.GONE);

            }
        });
    }

    private void setBadges(final ArrayList<String> badgesList, int totalBadges) {

        mBinding.textViewTotalBadges.setText(String.valueOf(totalBadges));
        mBinding.recyclerViewBadges.setLayoutManager(new GridLayoutManager(getContext(), BADGES_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerViewBadges.setNestedScrollingEnabled(false);
        mBinding.recyclerViewBadges.setAdapter(new BadgesRecyclerViewAdapter(badgesList));
        mBinding.layoutBadgesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mBinding.bottomLineEuros.setVisibility(View.GONE);
                mBinding.bottomLineTrophies.setVisibility(View.GONE);
                mBinding.bottomLineBadges.setVisibility(View.VISIBLE);

                mBinding.layoutEurosError.setVisibility(View.GONE);
                mBinding.recyclerViewRewards.setVisibility(View.GONE);
                mBinding.recyclerViewTrophies.setVisibility(View.GONE);
                mBinding.recyclerViewBadges.setVisibility(View.VISIBLE);

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

    private void setSubjectTopicsRewards(String subjectName, String score, String thumbnailUrl, ArrayList<StudentTopicReward> topicList) {
        if (topicList != null && !topicList.isEmpty()) {
            final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));

            /*Dialog box when user click on subject in total rewards/label_currency_value */
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
            binding.recyclerViewTopic.setAdapter(new SubjectTopicsRewardAdapter(getActivity(), topicList));

            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            Objects.requireNonNull(dialog.getWindow()).setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }
    }

    /*Adapter*/
    private class RewardsRecyclerViewAdapter extends RecyclerView.Adapter<RewardsRecyclerViewAdapter.ViewHolder> {
        private ArrayList<StudentSubjectReward> mList;

        private RewardsRecyclerViewAdapter(ArrayList<StudentSubjectReward> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public RewardsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMyAchievementEurosItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_euros_item, parent, false);
            return new RewardsRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RewardsRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final StudentSubjectReward studentSubjectReward = mList.get(position);
            final String subjectName = studentSubjectReward.getSubjectName();
            final String thumbnailUrl = studentSubjectReward.getThumbnailUrl();

            hideDividerForLastIndex(mList.size() - 1, position, holder.mBinding.viewDivider);
            holder.mBinding.textViewName.setText(studentSubjectReward.getSubjectName());
            final String rewards = String.valueOf(studentSubjectReward.getPointsRewarded());
            holder.mBinding.textViewScore.setText(rewards);
            if (!TextUtils.isEmpty(thumbnailUrl)) {
                Picasso.with(getContext()).load(thumbnailUrl).placeholder(R.drawable.icon_book).fit().centerCrop().into(holder.mBinding.imageViewIcon);
            } else {
                Picasso.with(getContext()).load(R.drawable.icon_book).placeholder(R.drawable.icon_book).fit().centerCrop().into(holder.mBinding.imageViewIcon);
            }

            holder.mBinding.getRoot().
                    setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               ArrayList<StudentTopicReward> topicRewardList = mList.get(position).getTopicRewardList();

                                               setSubjectTopicsRewards(subjectName, rewards, thumbnailUrl, topicRewardList);

                                           }
                                       }
                    );


        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementEurosItemBinding mBinding;

            public ViewHolder(LayoutMyAchievementEurosItemBinding binding) {
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
            LayoutMyAchievementTrophiesItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_trophies_item, parent, false);
            return new TrophiesRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final TrophiesRecyclerViewAdapter.ViewHolder holder, int position) {
            String trophy = mList.get(position);
            holder.mBinding.textViewName.setText(trophy);
            holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_trophies);
            holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimary));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementTrophiesItemBinding mBinding;

            ViewHolder(LayoutMyAchievementTrophiesItemBinding binding) {
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
            LayoutMyAchievementBadgesItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_my_achievement_badges_item, parent, false);
            return new BadgesRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final BadgesRecyclerViewAdapter.ViewHolder holder, int position) {
            String badge = mList.get(position);
            holder.mBinding.textViewName.setText(badge);
            holder.mBinding.imageViewIcon.setImageResource(R.drawable.icon_badges);
            holder.mBinding.imageViewIcon.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimary));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMyAchievementBadgesItemBinding mBinding;

            ViewHolder(LayoutMyAchievementBadgesItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }


}