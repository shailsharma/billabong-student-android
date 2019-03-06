package in.securelearning.lil.android.home.views.activity;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityLearningMapForTopicBarChartBinding;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.fragment.LearnigMapUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LearningMapForTopicBarChartActivity extends AppCompatActivity implements OnChartValueSelectedListener, View.OnClickListener {
    private ActivityLearningMapForTopicBarChartBinding binding;
    @Inject
    HomeModel mHomeModel;
    LearnigMapUtils learnigMapUtilsList;
    String subjectId;
    int mode = 2;
    ArrayList<HomeModel.TopicMap> topicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_learning_map_for_topic_bar_chart);
        getWindow().setStatusBarColor(ContextCompat.getColor(LearningMapForTopicBarChartActivity.this, R.color.colorPrimary));
        subjectId = getIntent().getStringExtra("subjectId");
        binding.imagebuttonBack.setOnClickListener(LearningMapForTopicBarChartActivity.this);
        Observable.create(new ObservableOnSubscribe<LearnigMapUtils>() {
            @Override
            public void subscribe(ObservableEmitter<LearnigMapUtils> e) throws Exception {
                e.onNext(mHomeModel.getDefaultMapValue());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<LearnigMapUtils>() {
                    @Override
                    public void accept(LearnigMapUtils learnigMapUtils) throws Exception {
                        learnigMapUtilsList = learnigMapUtils;
                        renderTopicChart();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    private void renderTopicChart() {
        final ArrayList<BarEntry> yVals1 = new ArrayList<>();
        final ArrayList<String> xVals = new ArrayList<>();
        if (learnigMapUtilsList != null) {
            final HomeModel.SubjectMap subjectMap = learnigMapUtilsList.getSubjectArrayList().get(subjectId);
            if (subjectMap != null) {
                topicList = new ArrayList<>(subjectMap.getmTopicMap().values());
                for (int i = 0; i < topicList.size(); i++) {
                    HomeModel.TopicMap topicMap = topicList.get(i);
                    float num1 = topicMap.getSkill()[0];
                    float num2 = topicMap.getSkill()[1];
                    float num3 = topicMap.getSkill()[2];
                    yVals1.add(new BarEntry(new float[]{num1, num2, num3}, i));
                    if(topicMap.getName().length()>10){
                        xVals.add(topicMap.getName().substring(0,10)+"...");
                    }else {
                        xVals.add(topicMap.getName());
                    }
                }
                final int[] colorCode = {Color.rgb(167, 223, 102), Color.rgb(252, 253, 120), Color.rgb(255, 106, 106)};
                Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        BarDataSet set1 = new BarDataSet(yVals1, "             Topic Composite Chart");
                        set1.setColors(colorCode);
                        set1.setStackLabels(new String[]{"Outstanding", "In progress", "Need's Attention"});
                        final BarData data = new BarData(xVals, set1);
                        data.setValueFormatter(new LearnigMapUtils.MyValueFormatter());
                        data.setValueTextColor(Color.WHITE);
                        data.setValueTextSize(0f);
                        binding.chart.setData(data);
                        binding.chart.setVisibleXRangeMaximum(5f);
                        binding.chart.setVisibleXRangeMinimum(5f);
                        binding.chart.setDoubleTapToZoomEnabled(false);
                        binding.chart.setPinchZoom(false);
                        binding.chart.setDescription("");
                        binding.learningMapHeading.setText("Topic Map For " + subjectMap.getName());

                        XAxis xAxis = binding.chart.getXAxis();
                        xAxis.setDrawGridLines(false);
                        xAxis.setDrawAxisLine(false);

                        YAxis yAxis = binding.chart.getAxisLeft(); // upper part
                        yAxis.setEnabled(false);
                        yAxis = binding.chart.getAxisRight(); // lower part
                        yAxis.setEnabled(false);
                        binding.chart.setOnChartValueSelectedListener(LearningMapForTopicBarChartActivity.this);
                        binding.chart.invalidate();
                    }
                });
            }
        }
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Intent intent = new Intent(this, LearningMapForSkillChartNewActivity.class);
        HomeModel.TopicMap skillData = topicList.get(e.getXIndex());
        intent.putExtra("topicId", skillData.getTid());
        intent.putExtra("subjectId", subjectId);
        startActivity(intent);
        binding.chart.highlightValue(-1, -1);
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding != null) {
            binding.chart.highlightValue(-1, -1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebutton_back:
                onBackPressed();
                break;
        }
    }

}
