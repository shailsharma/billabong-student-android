package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutSkillItemBinding;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 28-Dec-17.
 */

public class TraineeLearningObjectiveFragment extends Fragment {
    @Inject
    HomeModel mHomeModel;
    public static final String TRAINING_ID = "trainingId";
    public static final String COLUMN_COUNT = "columnCount";
    private int mColumnCount;
    private String mTrainingId;
    LayoutRecyclerViewBinding mBinding;

    public static Fragment newInstance(int columnCount, String trainingId) {
        TraineeLearningObjectiveFragment traineeLearningObjectiveFragment = new TraineeLearningObjectiveFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        args.putString(TRAINING_ID, trainingId);
        traineeLearningObjectiveFragment.setArguments(args);
        return traineeLearningObjectiveFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_recycler_view, container, false);
        mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
            mTrainingId = getArguments().getString(TRAINING_ID);
        }
        getSessions(mTrainingId);
        return mBinding.getRoot();
    }

    private void getSessions(final String trainingId) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<ArrayList<TrainingSession>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<TrainingSession>> e) throws Exception {
                ArrayList<TrainingSession> trainingSessions = mHomeModel.getTrainingSessionsByTrainingId(trainingId);
                e.onNext(trainingSessions);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<TrainingSession>>() {
                    @Override
                    public void accept(ArrayList<TrainingSession> trainingSessions) throws Exception {
                        mBinding.progressBar.setVisibility(View.GONE);
                        ArrayList<Skill> skills = new ArrayList<>();
                        for (TrainingSession trainingSession : trainingSessions) {
                            skills.addAll(trainingSession.getSkills());
                        }
                        if (skills != null && !skills.isEmpty()) {
                            initializeRecyclerView(skills);
                        } else {
                            noResultFound();
                        }

                    }
                });
    }

    private void initializeRecyclerView(ArrayList<Skill> skills) {
        SkillAdapter skillAdapter = new SkillAdapter(skills);
        mBinding.list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.list.setAdapter(skillAdapter);
    }

    private void noResultFound() {
        mBinding.list.setVisibility(View.GONE);
        mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        mBinding.imageViewNoResult.setImageResource(R.drawable.logo_profile_g);
        mBinding.textViewNoResult.setText(getContext().getString(R.string.messageNoLearningObjectivesFound));
    }

    private class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {
        private ArrayList<Skill> mSkills = new ArrayList<>();

        public SkillAdapter(ArrayList<Skill> skills) {
            mSkills = skills;
        }

        @Override
        public SkillAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutSkillItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_skill_item, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(SkillAdapter.ViewHolder holder, int position) {
            Skill skill = mSkills.get(position);
            holder.mBinding.textViewName.setText(skill.getSkillName());
            holder.mBinding.textViewTopic.setText(skill.getTopic().getName());
        }

        @Override
        public int getItemCount() {
            return mSkills.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutSkillItemBinding mBinding;

            public ViewHolder(LayoutSkillItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
