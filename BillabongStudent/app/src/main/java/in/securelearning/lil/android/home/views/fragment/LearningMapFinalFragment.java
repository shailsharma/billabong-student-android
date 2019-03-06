package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import in.securelearning.lil.android.app.databinding.ActivityLearningMapFinalBinding;
import in.securelearning.lil.android.app.databinding.DialogSampleBinding;
import in.securelearning.lil.android.app.databinding.FragmentDialogDetailsBinding;
import in.securelearning.lil.android.app.databinding.LayoutLearningMapViewPagerItemBinding;
import in.securelearning.lil.android.app.databinding.SkillListRowBinding;
import in.securelearning.lil.android.app.databinding.TopicListRowBinding;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.quizpreview.activity.PracticeTopicActivity;
import in.securelearning.lil.android.quizpreview.activity.QuestionPlayerActivity;
import in.securelearning.lil.android.quizpreview.events.RefreshLearningMapEvent;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.utils.PrefManager.getDefaultSubject;


public class LearningMapFinalFragment extends Fragment {

    private ActivityLearningMapFinalBinding binding;

    private OnFragmentInteractionListener mListener;
    @Inject
    HomeModel mHomeModel;
    @Inject
    RxBus mRxBus;
    private Disposable mDisposable;
    ArrayList<HomeModel.SubjectMap> subjectList = null;
    LearnigMapUtils learnigMapUtilsList;
    LearningMapPagerAdapter mAdapter;
    HashMap<String, PrefManager.SubjectExt> mSubjectMap = new HashMap<>();
    private static String ARG_COLUMN_COUNT = "column-count";
    public static final String SUBJECT_ID = "subject_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String DATE = "date";
    private static final String TABBED_DIALOG_SKILL_LIST = "skill_list";
    private static final String TABBED_DIALOG_POSITION = "position";
    private static final String TABBED_DIALOG_SUBJECT = "subject";
    private static final String TABBED_DIALOG_SUBJECT_ID = "subject_id";
    private static final String TABBED_DIALOG_TOPIC_ID = "topic_id";
    private static final String TABBED_DIALOG_TOPIC_NAME = "topic_name";
    private static final String TABBED_DIALOG_COLOR = "color";
    private int mSubjectPosition = -1;
    private int mTopicPosition = -1;
    private int mSkillPosition = -1;
    private String mSubjectPositionId = "";
    private String mTopicPositionId = "";
    private String mSkillPositionId = "";
    private static TabbedDialog mTabbedDialog;

    public LearningMapFinalFragment() {
    }

    public static LearningMapFinalFragment newInstance() {
        LearningMapFinalFragment fragment = new LearningMapFinalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//            mSubjectId = getArguments().getString(SUBJECT_ID);
//            mTopicId = getArguments().getString(TOPIC_ID);
//            mGradeId = getArguments().getString(GRADE_ID);
//            mSectionId = getArguments().getString(SECTION_ID);
//            mDate = getArguments().getString(DATE);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_learning_map_final, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
        getData();
        listenRxEvent();
        return binding.getRoot();
    }

    private void getData() {


        Observable.create(new ObservableOnSubscribe<LearnigMapUtils>() {
            @Override
            public void subscribe(ObservableEmitter<LearnigMapUtils> e) throws Exception {
                ArrayList<PrefManager.SubjectExt> subjectList = PrefManager.getSubjectList(getContext());
                for (int i = 0; i < subjectList.size(); i++) {
                    PrefManager.SubjectExt subject = subjectList.get(i);
                    mSubjectMap.put(subject.getId(), subject);
                }
                LearnigMapUtils learnigMapUtils = mHomeModel.getDefaultMapValue();
                if (learnigMapUtils != null) {
                    e.onNext(learnigMapUtils);
                } else {
                    e.onNext(new LearnigMapUtils());
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LearnigMapUtils>() {
                    @Override
                    public void accept(LearnigMapUtils learnigMapUtils) throws Exception {
                        learnigMapUtilsList = learnigMapUtils;
                        if (learnigMapUtilsList.getSubjectArrayList().size() > 0) {
                            binding.layoutNoResult.setVisibility(View.GONE);
                            binding.learningMapPager.setVisibility(View.VISIBLE);
                            final ArrayList<HomeModel.SubjectMap> subjectMaps = new ArrayList<HomeModel.SubjectMap>();
                            subjectMaps.addAll(learnigMapUtilsList.getSubjectArrayList().values());
                            mAdapter = new LearningMapPagerAdapter(getContext(), subjectMaps);
                            binding.learningMapPager.setAdapter(mAdapter);
                            binding.tabLayout.setupWithViewPager(binding.learningMapPager);
                            binding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                            binding.tabLayout.setSelectedTabIndicatorHeight(4);
                            binding.tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.colorWhite66),
                                    ContextCompat.getColor(getContext(), R.color.colorWhite));
                            if (!TextUtils.isEmpty(mSubjectPositionId)) {
                                binding.learningMapPager.setCurrentItem(subjectMaps.indexOf(new HomeModel.SubjectMap(0, 0, "", mSubjectPositionId)));
                            }

                        } else {
                            binding.learningMapPager.setVisibility(View.GONE);
                            binding.layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void setId(String subjectId, String topicId, String skillId) {
        mSkillPositionId = skillId;
        mSubjectPositionId = subjectId;
        mTopicPositionId = topicId;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void listenRxEvent() {
        mDisposable = mRxBus.toFlowable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof RefreshLearningMapEvent) {
                    getData();
                } else if (event instanceof TabbedDialog.OnFragmentInteractionEvent) {
                    setId(((TabbedDialog.OnFragmentInteractionEvent) event).getSubjectPositionId(), ((TabbedDialog.OnFragmentInteractionEvent) event).getTopicPositionId(), ((TabbedDialog.OnFragmentInteractionEvent) event).getSkillPositionId());
                }
            }
        }, new Consumer<Throwable>() {

            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    class LearningMapPagerAdapter extends PagerAdapter {
        ArrayList<HomeModel.SubjectMap> mSubjectMaps = new ArrayList<>();


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public LearningMapPagerAdapter(Context context, ArrayList<HomeModel.SubjectMap> utils) {
            this.mSubjectMaps.addAll(utils);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSubjectMaps.get(position).getName();
        }

        @Override
        public int getCount() {
            return mSubjectMaps.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final HomeModel.SubjectMap subjectMap = mSubjectMaps.get(position);
            final LayoutLearningMapViewPagerItemBinding bindingPager = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.layout_learning_map_view_pager_item, container, false);


            if (subjectMap.getSkill()[0] > 1) {
                bindingPager.learningMapSkillText1.setText(getResources().getString(R.string.labelStrong) + " in " + String.valueOf(subjectMap.getSkill()[0]) + " Skills");
            } else {
                bindingPager.learningMapSkillText1.setText(getResources().getString(R.string.labelStrong) + " in " + String.valueOf(subjectMap.getSkill()[0]) + " Skill");
            }
            if (subjectMap.getSkill()[1] > 1) {
                bindingPager.learningMapSkillText2.setText(getResources().getString(R.string.labelCruisingWell) + " in " + String.valueOf(subjectMap.getSkill()[1]) + " Skills");
            } else {
                bindingPager.learningMapSkillText2.setText(getResources().getString(R.string.labelCruisingWell) + " in " + String.valueOf(subjectMap.getSkill()[1]) + " Skill");
            }
            if (subjectMap.getSkill()[2] > 1) {
                bindingPager.learningMapSkillText3.setText(getResources().getString(R.string.labelNeedsAttention) + " in " + String.valueOf(subjectMap.getSkill()[2]) + " Skills");
            } else {
                bindingPager.learningMapSkillText3.setText(getResources().getString(R.string.labelNeedsAttention) + " in " + String.valueOf(subjectMap.getSkill()[2]) + " Skill");
            }
            if (subjectMap.getSkill()[3] > 1) {
                bindingPager.learningMapSkillText4.setText(getResources().getString(R.string.labelStillHaveToOpenUp) + " " + String.valueOf(subjectMap.getSkill()[3]) + " Skills");
            } else {
                bindingPager.learningMapSkillText4.setText(getResources().getString(R.string.labelStillHaveToOpenUp) + " " + String.valueOf(subjectMap.getSkill()[3]) + " Skill");
            }

            PrefManager.SubjectExt subjectExt = mSubjectMap.get(subjectMap.getSid());
            if (subjectExt == null) {
                subjectExt = getDefaultSubject();
            }
            bindingPager.learningMapSkillImage1.setBackgroundColor(subjectExt.getTextColor());
            bindingPager.learningMapSkillImage2.setBackgroundColor(subjectExt.getTextColor());
            bindingPager.learningMapSkillImage2.setAlpha(.6f);
            bindingPager.learningMapSkillImage3.setBackgroundColor(subjectExt.getTextColor());
            bindingPager.learningMapSkillImage3.setAlpha(.3f);
            bindingPager.learningMapSkillImage4.setBackgroundColor(Color.GRAY);
            bindingPager.learningMapSkillImage4.setAlpha(.3f);

            int[] colors = new int[4];
            final int subjectColor = subjectExt.getTextColor();
            int red = Color.red(subjectColor);
            int green = Color.green(subjectColor);
            int blue = Color.blue(subjectColor);
            colors[0] = Color.rgb(red, green, blue);
            colors[1] = Color.argb(153, red, green, blue);
            colors[2] = Color.argb(76, red, green, blue);
            colors[3] = Color.argb(76, 88, 88, 88);

            drawCircle(bindingPager.subjectChart, subjectMap.getSkill()[0], subjectMap.getSkill()[1], subjectMap.getSkill()[2], subjectMap.getSkill()[3], colors);

            fillRecycleListForTopic(bindingPager.learningMapSkillRecycleList, subjectMap.getmTopicMap().values(), subjectMap.getSid(), subjectMap.getName(), colors);
            container.addView(bindingPager.getRoot());
            if (!TextUtils.isEmpty(mSubjectPositionId) && mSubjectPositionId.equals(subjectMap.getSid()) && !TextUtils.isEmpty(mTopicPositionId) && !TextUtils.isEmpty(mSkillPositionId)) {
                HomeModel.TopicMap topicMap = subjectMap.getmTopicMap().get(mTopicPositionId);
                if (topicMap != null && topicMap.getmSkillMap() != null && !topicMap.getmSkillMap().isEmpty()) {
                    ArrayList<HomeModel.SkillMap> skillMaps = new ArrayList<>(topicMap.getmSkillMap().values());
                    if (skillMaps != null && !skillMaps.isEmpty()) {
                        showTheDialog(skillMaps, skillMaps.indexOf(new HomeModel.SkillMap(0, 0, "", mSkillPositionId, "", "")), mSubjectPositionId, subjectMap.getName(), mTopicPositionId, subjectMap.getmTopicMap().get(mTopicPositionId).getName(), colors);
                        setId("", "", "");
                    }
                }

            }
            hideProgressBar(bindingPager);
            return bindingPager.getRoot();
        }

        private void showProgressBar(LayoutLearningMapViewPagerItemBinding bindingPager) {
            bindingPager.data.setVisibility(View.GONE);
            bindingPager.progressBar.setVisibility(View.VISIBLE);
        }

        private void hideProgressBar(LayoutLearningMapViewPagerItemBinding bindingPager) {
            bindingPager.progressBar.setVisibility(View.GONE);
            bindingPager.data.setVisibility(View.VISIBLE);
        }

        protected void showTheDialog(ArrayList<HomeModel.SkillMap> skillMapList, int position, String subjectId, String subjectName, String topicId, String topicName, int[] color) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            if (mTabbedDialog != null) {
                mTabbedDialog.dismiss();
            }
            mTabbedDialog = TabbedDialog.newInstance(skillMapList, position, subjectId, subjectName, topicId, topicName, color);
            mTabbedDialog.show(ft, "dialog");
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
            float progress = i * 100 / (i + j + k + m);
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


            public TopicAdapter(ArrayList<HomeModel.TopicMap> topicList, String subjectId, String subjectName, int[] colors) {
                this.mTopicList = topicList;
                mColors = colors;
                mSubjectId = subjectId;
                mSubjectName = subjectName;
            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TopicListRowBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.topic_list_row, parent, false);
                return new MyViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(final MyViewHolder holder, int position) {
                final HomeModel.TopicMap topicMap = mTopicList.get(position);
                holder.mBinding.topicName.setText(topicMap.getName());
                HashMap<String, HomeModel.SkillMap> skillMap = topicMap.getmSkillMap();
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

            @Override
            public int getItemCount() {
                return mTopicList.size();
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
//                horizontalSkillList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//                TrainingSessionAdapter adapter = new TrainingSessionAdapter(new ArrayList<>(skillMapList), colors);
//                horizontalSkillList.setAdapter(adapter);
            }
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
        public MySkillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SkillListRowBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.skill_list_row, parent, false);
            return new MySkillHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MySkillHolder holder, int position) {
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
            mTabbedDialog = TabbedDialog.newInstance(skillMapList, position, subjectId, subjectName, topicId, topicName, color);
            mTabbedDialog.show(ft, "dialog");
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
        private ArrayList<HomeModel.SkillMap> skillMapList;
        int itemPosition;
        String topicName;
        String mTopicId;
        String mSubjectId;
        String mSubjectName;
        int[] color;
        private int mPagerCurrentPosition = 0;

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
            SkillResultAdapter adapter = new SkillResultAdapter(skillMapList, color, mSubjectId, mSubjectName);
            dialogSampleBinding.masterViewPager.setAdapter(adapter);
            dialogSampleBinding.masterViewPager.setCurrentItem(itemPosition);
            dialogSampleBinding.tabLayout.setupWithViewPager(dialogSampleBinding.masterViewPager);
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogSampleBinding.dialogTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            dialogSampleBinding.dialogTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            dialogSampleBinding.dialogTitle.setText(topicName);
            dialogSampleBinding.masterViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mPagerCurrentPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            return dialogSampleBinding.getRoot();
        }

        public class OnFragmentInteractionEvent {
            private String mSubjectPositionId = "";
            private String mTopicPositionId = "";
            private String mSkillPositionId = "";

            public OnFragmentInteractionEvent(String subjectPositionId, String topicPositionId, String skillPositionId) {
                mSubjectPositionId = subjectPositionId;
                mTopicPositionId = topicPositionId;
                mSkillPositionId = skillPositionId;
            }

            public String getSubjectPositionId() {
                return mSubjectPositionId;
            }

            public void setSubjectPositionId(String subjectPositionId) {
                mSubjectPositionId = subjectPositionId;
            }

            public String getTopicPositionId() {
                return mTopicPositionId;
            }

            public void setTopicPositionId(String topicPositionId) {
                mTopicPositionId = topicPositionId;
            }

            public String getSkillPositionId() {
                return mSkillPositionId;
            }

            public void setSkillPositionId(String skillPositionId) {
                mSkillPositionId = skillPositionId;
            }
        }

        public class SkillResultAdapter extends PagerAdapter {
            ArrayList<HomeModel.SkillMap> skillMapList;
            int[] color;
            String mSubjectId;
            String mSubjectName;
            String mMasterQuestionLevel = "";

            public SkillResultAdapter(ArrayList<HomeModel.SkillMap> skillMapList, int[] color, String subjectId, String subjectName) {
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
                String skillLevel = "";
                String totalNoOfQuestionAttemped = "";
                String message = getString(R.string.skill_detail_message_no_data);
                final FragmentDialogDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_dialog_details, container, false);
                if (!TextUtils.isEmpty(skillMap.getSkillLevel()) && skillMap.getSkillLevel() != null) {
                    skillLevel = skillMap.getSkillLevel();
                    skillLevel = skillLevel.substring(0, 1).toUpperCase() + skillLevel.substring(1);
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
                binding.buttonStartMasterQuiz.setVisibility(View.GONE);
                binding.buttonStartMasterQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GeneralUtils.isNetworkAvailable(getContext())) {
                            if (skillMap != null && !TextUtils.isEmpty(skillMap.getId())) {
                                if (mTabbedDialog != null && mTabbedDialog.isVisible()) {
                                    mTabbedDialog.dismiss();
                                }
                                Injector.INSTANCE.getComponent().rxBus().send(new OnFragmentInteractionEvent(mSubjectId, mTopicId, skillMapList.get(position).getId()));
                                startActivity(QuestionPlayerActivity.getStartIntent(getContext(), skillMap.getId(), skillMap.getName(), mMasterQuestionLevel, skillMap, getString(R.string.labelSkill)));
                            }
                        } else {
                            SnackBarUtils.showColoredSnackBar(getContext(), binding.buttonStartMasterQuiz, getString(R.string.connect_internet), ContextCompat.getColor(getContext(), R.color.colorRed));

                        }
                    }
                });
                binding.textName.setText(name);
                binding.textView2.setText(skillLevel);
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
