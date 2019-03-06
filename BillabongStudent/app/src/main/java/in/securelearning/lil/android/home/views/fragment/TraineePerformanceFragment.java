package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.DialogSampleBinding;
import in.securelearning.lil.android.app.databinding.FragmentDialogDetailsBinding;
import in.securelearning.lil.android.app.databinding.FragmentLearningMapFragmentForClassDetailsBinding;
import in.securelearning.lil.android.app.databinding.SkillListRowBinding;
import in.securelearning.lil.android.app.databinding.TopicListRowBinding;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.quizpreview.activity.PracticeTopicActivity;
import in.securelearning.lil.android.quizpreview.activity.QuestionPlayerActivity;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 28-Dec-17.
 */

public class TraineePerformanceFragment extends Fragment {

    @Inject
    HomeModel mHomeModel;
    FragmentLearningMapFragmentForClassDetailsBinding mBinding;
    public static final String TRAINING_ID = "trainingId";
    private String mTrainingId;

    public static Fragment newInstance(String trainingId) {
        TraineePerformanceFragment traineePerformanceFragment = new TraineePerformanceFragment();
        Bundle args = new Bundle();
        args.putString(TRAINING_ID, trainingId);
        traineePerformanceFragment.setArguments(args);
        return traineePerformanceFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_learning_map_fragment_for_class_details, container, false);
        if (getArguments() != null) {
            mTrainingId = getArguments().getString(TRAINING_ID);
        }
        createSkillIdsList();
        return mBinding.getRoot();
    }

    private void createSkillIdsList() {
        Observable.create(new ObservableOnSubscribe<Training>() {
            @Override
            public void subscribe(ObservableEmitter<Training> e) throws Exception {
                Training training = mHomeModel.getTrainingById(mTrainingId);
                e.onNext(training);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Training>() {
                    @Override
                    public void accept(Training training) throws Exception {
                        HashMap<String, HomeModel.SkillMap> map = new HashMap<>();
                        ArrayList<TrainingSession> trainingSessions = training.getSessions();
                        if (trainingSessions != null && !trainingSessions.isEmpty()) {
                            for (TrainingSession trainingSession : trainingSessions) {
                                if (trainingSession.getTopics() != null && !trainingSession.getTopics().isEmpty()) {
                                    for (Topic topic : trainingSession.getTopics()) {
                                        if (topic.getSkills() != null && !topic.getSkills().isEmpty()) {
                                            for (int i = 0; i < topic.getSkills().size(); i++) {
                                                HomeModel.SkillMap skillMap = new HomeModel.SkillMap();
                                                Skill skill = topic.getSkills().get(i);
                                                skillMap.setId(skill.getId());
                                                skillMap.setTid(skill.getTopic().getId());
                                                skillMap.setSid("");
                                                skillMap.setName(skill.getSkillName());
                                                skillMap.setSkillLevel(skill.getSkillLevel());
                                                skillMap.setTopic(topic);
                                                map.put(topic.getSkills().get(i).getId(), skillMap);

                                            }
                                        }
                                    }
                                }


                            }
                            getLearningMap(new ArrayList<Object>(map.keySet()), new ArrayList<>(map.values()));
                        } else {
                            mBinding.nestedLayoutChartMap.setVisibility(View.GONE);
                            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                        }


                    }
                });
    }

    private void getLearningMap(final ArrayList<Object> skillIds, final ArrayList<HomeModel.SkillMap> skillMaps) {
        Observable.create(new ObservableOnSubscribe<HomeModel.SubjectMap>() {
            @Override
            public void subscribe(ObservableEmitter<HomeModel.SubjectMap> e) throws Exception {
                HomeModel.SubjectMap subjectMap = mHomeModel.getSubjectMapForTraining(skillIds, skillMaps);
                if (subjectMap != null) {
                    e.onNext(subjectMap);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HomeModel.SubjectMap>() {
                    @Override
                    public void accept(HomeModel.SubjectMap subjectMap) throws Exception {
                        if (subjectMap.getmTopicMap() != null && !subjectMap.getmTopicMap().isEmpty()) {
                            mBinding.subjectName.setVisibility(View.GONE);
                            mBinding.learningMapSkillText1.setText(subjectMap.getSkill()[0] + " Skills " + getResources().getString(R.string.studentAboveAverage));
                            mBinding.learningMapSkillText2.setText(subjectMap.getSkill()[1] + " Skills " + getResources().getString(R.string.studentAverage));
                            mBinding.learningMapSkillText3.setText(subjectMap.getSkill()[2] + " Skills " + getResources().getString(R.string.studentBelowAverage));
                            mBinding.learningMapSkillText4.setText(subjectMap.getSkill()[3] + " Skills " + getResources().getString(R.string.studentUnAttempted));
                            final int subjectColor = PrefManager.getColorForSubject(getActivity(), "");

                            mBinding.learningMapSkillImage1.setBackgroundColor(subjectColor);
                            mBinding.learningMapSkillImage2.setBackgroundColor(subjectColor);
                            mBinding.learningMapSkillImage2.setAlpha(.6f);
                            mBinding.learningMapSkillImage3.setBackgroundColor(subjectColor);
                            mBinding.learningMapSkillImage3.setAlpha(.3f);
                            mBinding.learningMapSkillImage4.setBackgroundColor(Color.GRAY);
                            mBinding.learningMapSkillImage4.setAlpha(.3f);
                            int[] colors = new int[4];
                            int red = Color.red(subjectColor);
                            int green = Color.green(subjectColor);
                            int blue = Color.blue(subjectColor);
                            colors[0] = Color.rgb(red, green, blue);
                            colors[1] = Color.argb(153, red, green, blue);
                            colors[2] = Color.argb(76, red, green, blue);
                            colors[3] = Color.argb(76, 88, 88, 88);

                            drawCircle(mBinding.subjectChart, subjectMap.getSkill()[0], subjectMap.getSkill()[1], subjectMap.getSkill()[2], subjectMap.getSkill()[3], colors);

                            fillRecycleListForTopic(mBinding.learningMapSkillRecycleList, subjectMap.getmTopicMap().values(), subjectMap.getSid(), subjectMap.getName(), colors);
                            mBinding.nestedLayoutChartMap.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.nestedLayoutChartMap.setVisibility(View.GONE);
                            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void fillRecycleListForTopic(RecyclerView learningMapSkillRecycleList, Collection<HomeModel.TopicMap> values, String subjectId, String subjectName, int[] colors) {
        learningMapSkillRecycleList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        TopicAdapter adapter = new TopicAdapter(new ArrayList<>(values), subjectId, subjectName, colors);
        learningMapSkillRecycleList.setAdapter(adapter);
    }

    private void drawCircle(PieChart subjectChart, int i, int j, int k, int m, int[] colors) {
        float a = i;
        float b = j;
        float c = k;
        float d = m;
        ArrayList<Entry> yvalues = new ArrayList<>();
        yvalues.add(new Entry(a, 0));
        yvalues.add(new Entry(b, 1));
        yvalues.add(new Entry(c, 2));
        yvalues.add(new Entry(d, 3));
        PieDataSet dataSet = new PieDataSet(yvalues, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(0f);
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        PieData data = new PieData(xVals, dataSet);
        subjectChart.setData(data);
        subjectChart.setHoleRadius(90f);
        subjectChart.setDrawHoleEnabled(true);
        subjectChart.setUsePercentValues(false);
        subjectChart.setDescription("");
        subjectChart.setDrawCenterText(true);
        subjectChart.setCenterTextColor(colors[0]);
        int val = getResources().getInteger(R.integer.learning_map_progress_text_size);
        subjectChart.setCenterTextSize(val);
        float sum = i + j + k + m;
        if (sum == 0) {
            sum = 1;
        }
        float progress = i * 100 / sum;
        //subjectChart.setCenterText(Float.valueOf(progress).toString().replaceAll("\\.?0*$", "") + "%");
        subjectChart.setCenterText(String.valueOf(new DecimalFormat("##.##").format(progress)) + "%");
        Legend l = subjectChart.getLegend();
        l.setEnabled(false);
        subjectChart.invalidate();
        subjectChart.setClickable(false);
        subjectChart.setTouchEnabled(false);
    }

    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.MyViewHolder> {
        private ArrayList<HomeModel.TopicMap> mTopicList;
        int[] mColors;
        String mSubjectId;
        String mSubjectName;
        HashMap<String, HomeModel.SkillMap> skillMap;

        public TopicAdapter(ArrayList<HomeModel.TopicMap> topicList, String subjectId, String subjectName, int[] colors) {
            this.mTopicList = topicList;
            mColors = colors;
            mSubjectId = subjectId;
            mSubjectName = subjectName;
        }

        @Override
        public TopicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TopicListRowBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.topic_list_row, parent, false);
            return new TopicAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TopicAdapter.MyViewHolder holder, int position) {
            final HomeModel.TopicMap topicMap = mTopicList.get(position);
            holder.mBinding.topicName.setText(topicMap.getName());
            skillMap = topicMap.getmSkillMap();

            final String masteryQuestionLevel = getDifficulty(topicMap);
            HomeModel.SkillMap[] map = {};
            map = skillMap.values().toArray(map);
            final HomeModel.SkillMap[] finalMap = map;
            holder.mBinding.buttonPractice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        if (topicMap != null && !TextUtils.isEmpty(topicMap.getTid())) {
                            if (finalMap.length > 0) {
                                startActivity(PracticeTopicActivity.getStartIntentForSkills(getContext(), topicMap.getmSkillMap().values(), topicMap.getName(), masteryQuestionLevel, finalMap[0]));
                            }
                        }
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getContext(), holder.mBinding.getRoot());

                    }
                }
            });

            drawSkillMap(holder.mBinding.horizontalSkillList, new ArrayList<>(skillMap.values()), mColors, mSubjectId, mSubjectName, topicMap.getTid(), topicMap.getName());
        }

        @Override
        public int getItemCount() {
            return mTopicList.size();
        }

        private String getDifficulty(HomeModel.TopicMap topicMap) {
            if (topicMap.getTotalQuestionsAttempted() > 0) {
                if (topicMap.getTotalObtained() >= 60) {
                    return getString(R.string.label_high);
                } else if (topicMap.getTotalObtained() >= 36) {
                    return getString(R.string.label_medium);
                } else {
                    return getString(R.string.label_low);
                }
            } else {
                return getString(R.string.label_low);
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TopicListRowBinding mBinding;

            public MyViewHolder(TopicListRowBinding view) {
                super(view.getRoot());
                mBinding = view;
            }
        }

        private void drawSkillMap(RecyclerView horizontalSkillList, ArrayList<HomeModel.SkillMap> skillMapList, int[] colors, String subjectId, String subjectName, String topicId, String topicName) {
            horizontalSkillList.setHasFixedSize(true);
            if (skillMapList.size() > 0) {
                int columnCount = 1;
                int val = getResources().getInteger(R.integer.skill_map_text_size);
                if (skillMapList.size() > (val * 3)) {
                    columnCount = 4;
                } else if (skillMapList.size() > (val * 2)) {
                    columnCount = 3;
                } else if (skillMapList.size() > val) {
                    columnCount = 2;
                }
                horizontalSkillList.setLayoutManager(new GridLayoutManager(getContext(), columnCount, GridLayoutManager.HORIZONTAL, false));
                SkillAdapter adapter = new SkillAdapter(new ArrayList<>(skillMapList), mSubjectId, mSubjectName, colors, topicId, topicName);
                horizontalSkillList.setAdapter(adapter);
            }
//            horizontalSkillList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//            TrainingSessionAdapter adapter = new TrainingSessionAdapter(new ArrayList<>(skillMapList), colors);
//            horizontalSkillList.setAdapter(adapter);
        }
    }

    class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.MySkillHolder> {
        ArrayList<HomeModel.SkillMap> mSkillMapList;
        int[] mColors;
        String topicName;
        String mTopicId;
        String mSubjectId;
        String mSubjectName;

        public SkillAdapter(ArrayList<HomeModel.SkillMap> skillMapList, String subjectId, String subjectName, int[] colors, String topicId, String topicName) {
            this.mSkillMapList = skillMapList;
            this.mColors = colors;
            this.topicName = topicName;
            this.mSubjectId = subjectId;
            this.mSubjectName = subjectName;
            this.mTopicId = topicId;
        }

        @Override
        public SkillAdapter.MySkillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SkillListRowBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.skill_list_row, parent, false);
            return new SkillAdapter.MySkillHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SkillAdapter.MySkillHolder holder, int position) {
            HomeModel.SkillMap skillMap = mSkillMapList.get(position);
            final int pos = position;
            if (skillMap.getTotalQuestionsAttempted() > 0) {
                if (skillMap.getTotalObtained() >= 60) {
                    holder.mBinding.skill.setBackgroundColor(mColors[0]);
                } else if (skillMap.getTotalObtained() >= 36) {
                    holder.mBinding.skill.setBackgroundColor(mColors[1]);
                } else {
                    holder.mBinding.skill.setBackgroundColor(mColors[2]);
                }
            } else {
                holder.mBinding.skill.setBackgroundColor(mColors[3]);
            }
            //Here we are populting skill map data in dialog fragment as view pager
            holder.mBinding.skill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTheDialog(mSkillMapList, pos, mSubjectId, mSubjectName, mTopicId, topicName, mColors);
                }
            });
        }

        protected void showTheDialog(ArrayList<HomeModel.SkillMap> skillMapList, int position, String subjectId, String subjectName, String topicId, String topicName, int[] color) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            TabbedDialog td = TabbedDialog.newInstance(skillMapList, position, subjectId, subjectName, topicId, topicName, color);
            td.show(ft, "dialog");
        }


        @Override
        public int getItemCount() {
            return mSkillMapList.size();
        }

        public class MySkillHolder extends RecyclerView.ViewHolder {
            SkillListRowBinding mBinding;

            public MySkillHolder(SkillListRowBinding view) {
                super(view.getRoot());
                mBinding = view;
            }
        }
    }

    public static class TabbedDialog extends DialogFragment {
        private static final String TABBED_DIALOG_SKILL_LIST = "skill_list";
        private static final String TABBED_DIALOG_POSITION = "position";
        private static final String TABBED_DIALOG_SUBJECT = "subject";
        private static final String TABBED_DIALOG_SUBJECT_ID = "subject_id";
        private static final String TABBED_DIALOG_TOPIC_ID = "topic_id";
        private static final String TABBED_DIALOG_TOPIC_NAME = "topic_name";
        private static final String TABBED_DIALOG_COLOR = "color";
        private ArrayList<HomeModel.SkillMap> skillMapList;
        int itemPosition;
        String topicName;
        String mTopicId;
        String mSubjectId;
        String mSubjectName;
        int[] color;

        public static TabbedDialog newInstance(ArrayList<HomeModel.SkillMap> skillMapList, int position, String subjectId, String subjectName, String topicId, String topicName, int[] color) {
            TabbedDialog dialogFragment = new TabbedDialog();
            Bundle args = new Bundle();
            args.putSerializable(TABBED_DIALOG_SKILL_LIST, skillMapList);
            args.putInt(TABBED_DIALOG_POSITION, position);
            args.putString(TABBED_DIALOG_SUBJECT_ID, subjectId);
            args.putString(TABBED_DIALOG_SUBJECT, subjectName);
            args.putString(TABBED_DIALOG_TOPIC_ID, topicId);
            args.putString(TABBED_DIALOG_TOPIC_NAME, topicName);
            args.putIntArray(TABBED_DIALOG_COLOR, color);
            dialogFragment.setArguments(args);
            return dialogFragment;
        }

        public TabbedDialog() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                skillMapList = (ArrayList<HomeModel.SkillMap>) getArguments().getSerializable(TABBED_DIALOG_SKILL_LIST);
                itemPosition = getArguments().getInt(TABBED_DIALOG_POSITION);
                mSubjectName = getArguments().getString(TABBED_DIALOG_SUBJECT);
                mSubjectId = getArguments().getString(TABBED_DIALOG_SUBJECT_ID);
                mTopicId = getArguments().getString(TABBED_DIALOG_TOPIC_ID);
                topicName = getArguments().getString(TABBED_DIALOG_TOPIC_NAME);
                color = getArguments().getIntArray(TABBED_DIALOG_COLOR);
            } else {
                dismiss();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            DialogSampleBinding dialogSampleBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_sample, container, false);
            TabbedDialog.CustomAdapter adapter = new TabbedDialog.CustomAdapter(skillMapList, color, mSubjectId, mSubjectName);
            dialogSampleBinding.masterViewPager.setAdapter(adapter);
            dialogSampleBinding.masterViewPager.setCurrentItem(itemPosition);
            dialogSampleBinding.tabLayout.setupWithViewPager(dialogSampleBinding.masterViewPager);
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogSampleBinding.dialogTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            dialogSampleBinding.dialogTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            dialogSampleBinding.dialogTitle.setText(topicName);
            return dialogSampleBinding.getRoot();
        }

        public class CustomAdapter extends PagerAdapter {
            ArrayList<HomeModel.SkillMap> skillMapList;
            int[] color;
            String mSubjectId;
            String mSubjectName;
            String mMasterQuestionLevel = "";

            public CustomAdapter(ArrayList<HomeModel.SkillMap> skillMapList, int[] color, String subjectId, String subjectName) {
                this.skillMapList = skillMapList;
                this.color = color;
                this.mSubjectId = subjectId;
                this.mSubjectName = subjectName;
            }

            @Override
            public int getCount() {
                return skillMapList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == (object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                final HomeModel.SkillMap skillMap = skillMapList.get(position);
                String name = "";
                String skillLeavel = "";
                String totalNoOfQuestionAttemped = "";
                String message = getString(R.string.skill_detail_message_no_data);
                final FragmentDialogDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_dialog_details, container, false);
                if (!TextUtils.isEmpty(skillMap.getSkillLevel()) && skillMap.getSkillLevel() != null) {
                    skillLeavel = skillMap.getSkillLevel();
                    skillLeavel = skillLeavel.substring(0, 1).toUpperCase() + skillLeavel.substring(1);
                }
                if (!TextUtils.isEmpty(skillMap.getName()) && skillMap.getName() != null) {
                    name = skillMap.getName();
                }
                if (skillMap.getTotalQuestionsAttempted() > 0) {
                    totalNoOfQuestionAttemped = skillMap.getTotalQuestionsAttempted() + "";
                    if (skillMap.getTotalObtained() >= 60) {
                        binding.textSmiley.setText(R.string.smileyAboveAverage);
                        message = getString(R.string.skill_detail_message_above_average);
                        mMasterQuestionLevel = getString(R.string.label_high);
                        binding.performancetText.setText(R.string.performanceAboveAverage);
                        binding.skill.setBackgroundColor(color[0]);
                    } else if (skillMap.getTotalObtained() >= 36) {
                        binding.textSmiley.setText(R.string.smileyAverage);
                        message = getString(R.string.skill_detail_message_average);
                        mMasterQuestionLevel = getString(R.string.label_medium);
                        binding.skill.setBackgroundColor(color[1]);
                        binding.performancetText.setText(R.string.performanceAverage);
                    } else {
                        binding.textSmiley.setText(R.string.smileyBelowAverage);
                        message = getString(R.string.skill_detail_message_below_average);
                        mMasterQuestionLevel = getString(R.string.label_low);
                        binding.skill.setBackgroundColor(color[2]);
                        binding.performancetText.setText(R.string.performanceBelowAverage);
                    }
                } else {
                    binding.textSmiley.setText(R.string.smileyNoData);
                    binding.laySkillPerformance.setVisibility(View.GONE);
                    binding.skill.setBackgroundColor(color[3]);
                    binding.performancetText.setText(R.string.skill_detail_message_not_attempted);
                    mMasterQuestionLevel = getString(R.string.label_low);
                }
                binding.additionalButton.setVisibility(View.GONE);
//                v.additionalButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivity(ClassDetailsActivity.getStartIntent(getContext(), mSubjectId, null, topicName, mTopicId, "", "", DateUtils.getCurrentISO8601DateString(), false, mSubjectName, mSubjectName));
//                    }
//                });

                binding.buttonStartMasterQuiz.setVisibility(View.GONE);
                binding.buttonStartMasterQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GeneralUtils.isNetworkAvailable(getContext())) {
                            if (skillMap != null && !TextUtils.isEmpty(skillMap.getId())) {
                                startActivity(QuestionPlayerActivity.getStartIntent(getContext(), skillMap.getId(), skillMap.getName(), mMasterQuestionLevel, skillMap, getString(R.string.labelSkill)));
                            }
                        } else {
                            SnackBarUtils.showColoredSnackBar(getContext(), binding.buttonStartMasterQuiz, getString(R.string.connect_internet), ContextCompat.getColor(getContext(), R.color.colorRed));

                        }
                    }
                });
                binding.textName.setText(name);
                binding.textView2.setText(skillLeavel);
                binding.marksCircle.setText(totalNoOfQuestionAttemped);
                binding.textSuggestion.setText(message);
                container.addView(binding.getRoot());
                return binding.getRoot();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        }
    }

}
