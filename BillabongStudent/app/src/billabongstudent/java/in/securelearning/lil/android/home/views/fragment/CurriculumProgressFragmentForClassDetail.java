package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCurriculumProgressFragmentBinding;
import in.securelearning.lil.android.app.databinding.LayoutTopicProgressItemBinding;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 08-Feb-18.
 */

public class CurriculumProgressFragmentForClassDetail extends Fragment {

    @Inject
    HomeModel mHomeModel;

    LayoutCurriculumProgressFragmentBinding mBinding;

    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECTS = "subjects";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String DATE = "date";

    private int mColumnCount = 1;
    private String mSubjectId;
    private ArrayList<String> mSubjects;
    private String mSubjectName;
    private String mTopicId;
    private String mGradeId;
    private String mSectionId;
    private String mDate;

    public static Fragment newInstance(int columnCount, String subjectId, ArrayList<String> subjects, String subjectName, String topicId, String gradeId, String sectionId, String date) {
        CurriculumProgressFragmentForClassDetail fragment = new CurriculumProgressFragmentForClassDetail();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SUBJECT_ID, subjectId);
        args.putStringArrayList(SUBJECTS, subjects);
        args.putString(SUBJECT_NAME, subjectName);
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putString(SECTION_ID, sectionId);
        args.putString(DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_curriculum_progress_fragment, container, false);
        mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mSubjects = getArguments().getStringArrayList(SUBJECTS);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mSectionId = getArguments().getString(SECTION_ID);
            mDate = getArguments().getString(DATE);
        }
        getTopicList(mSubjectId, mSubjects, mGradeId);
        return mBinding.getRoot();
    }

    private void getTopicList(final String subjectId, final ArrayList<String> subjects, final String gradeId) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.list.setVisibility(View.GONE);
        Observable.create(new ObservableOnSubscribe<ArrayList<Topic>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Topic>> e) throws Exception {

                ArrayList<Topic> list = mHomeModel.getTopicListForSubjectIdAndGradeId(subjectId, subjects, gradeId);
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Topic>>() {
            @Override
            public void accept(ArrayList<Topic> topics) throws Exception {
                noResultFound(topics.size());
                initializeRecyclerView(topics);
            }
        }, new Consumer<Throwable>() {

            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    private void noResultFound(int size) {
        mBinding.progressBar.setVisibility(View.GONE);
        if (size > 0) {
            mBinding.list.setVisibility(View.VISIBLE);
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.textViewNoResult.setText(getString(R.string.messageNoTopics));
        }
    }


    private void initializeRecyclerView(ArrayList<Topic> topics) {
        int count = 2;
        if (topics.size() > 8) {
            count = 4;
        } else if (topics.size() > 12) {
            count = 6;
        }
        int colors = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        int[] circleColor = new int[3];
        int red = Color.red(colors);
        int green = Color.green(colors);
        int blue = Color.blue(colors);
        circleColor[0] = Color.rgb(red, green, blue);
        circleColor[1] = Color.argb(76, red, green, blue);
        circleColor[2] = Color.argb(76, red, green, blue);
        drawCircle(mBinding.progressChart, count, count, count, circleColor);
        mBinding.textViewMessage.setText(String.valueOf(count) + " Topics covered\nProgress is On Track");
        mBinding.list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), topics, count);
        mBinding.list.setAdapter(recyclerViewAdapter);
    }

    private void drawCircle(PieChart subjectChart, int i, int j, int k, int[] colors) {
        float a = i;
        float b = j;
        float c = k;
        ArrayList<Entry> yvalues = new ArrayList<>();
        yvalues.add(new Entry(a, 0));
        yvalues.add(new Entry(b, 1));
        yvalues.add(new Entry(c, 2));
        PieDataSet dataSet = new PieDataSet(yvalues, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(0f);
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("");
        xVals.add("");
        xVals.add("");
        PieData data = new PieData(xVals, dataSet);
        subjectChart.setData(data);
        subjectChart.setHoleRadius(90f);
        subjectChart.setDrawHoleEnabled(true);
        subjectChart.setUsePercentValues(true);
        subjectChart.setDescription("");
        subjectChart.setDrawCenterText(true);
        subjectChart.setCenterTextColor(colors[0]);
        int val = getResources().getInteger(R.integer.learning_map_progress_text_size);
        subjectChart.setCenterTextSize(val);
        subjectChart.setCenterText(String.valueOf(i));
        Legend l = subjectChart.getLegend();
        l.setEnabled(false);
        subjectChart.invalidate();
        subjectChart.setClickable(false);
        subjectChart.setTouchEnabled(false);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private ArrayList<Topic> mList;
        private Context mContext;
        private int mCount;

        public RecyclerViewAdapter(Context context, ArrayList<Topic> topics, int count) {
            mContext = context;
            mList = topics;
            mCount = count;
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutTopicProgressItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_topic_progress_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
            Topic topic = mList.get(position);
            setTopicTitle(topic.getName(), holder.mBinding.textViewTopicTitle);
            setTopicProgress(holder.mBinding, position);
        }

        public void setTopicProgress(LayoutTopicProgressItemBinding binding, int position) {
            int max = 100;
            int progress = 0;
            if (position < mCount) {
                binding.progressBar.setProgress(max);
                binding.textViewProgress.setText(String.valueOf(max)+"%");
            } else {
                Random random = new Random();
                progress = random.nextInt(50);
                binding.progressBar.setProgress(progress);
                binding.textViewProgress.setText(String.valueOf(progress)+"%");

            }


        }

        public void setTopicTitle(String name, TextViewCustom textView) {
            if (!TextUtils.isEmpty(name)) {
                textView.setText(name);
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutTopicProgressItemBinding mBinding;

            public ViewHolder(LayoutTopicProgressItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
