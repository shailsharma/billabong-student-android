package in.securelearning.lil.android.home.views.activity;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityLearningMapForTopicChartBinding;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.fragment.LearnigMapUtils;
import in.securelearning.lil.android.home.views.fragment.PieDataExt;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LearningMapForTopicChartActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener, View.OnClickListener {
    private ActivityLearningMapForTopicChartBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_learning_map_for_topic_chart);
        getWindow().setStatusBarColor(ContextCompat.getColor(LearningMapForTopicChartActivity.this, R.color.colorPrimary));
        subjectId = getIntent().getStringExtra("subjectId");
        binding.imagebuttonBack.setOnClickListener(LearningMapForTopicChartActivity.this);

        Observable.create(new ObservableOnSubscribe<LearnigMapUtils>() {
            @Override
            public void subscribe(ObservableEmitter<LearnigMapUtils> e) throws Exception {
                e.onNext(mHomeModel.getDefaultMapValue());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
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
        addItemsOnSpinner();
    }

    private void addItemsOnSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Outstanding");
        list.add("In-progress");
        list.add("Need's Attention");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMode.setAdapter(dataAdapter);
        binding.spinnerMode.setOnItemSelectedListener(this);
        binding.spinnerMode.setSelection(mode);
    }

    private void renderTopicChart() {
        ArrayList<Entry> yvalues = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Float> innerCount = new ArrayList<>();
        if(learnigMapUtilsList != null) {
            final HomeModel.SubjectMap subjectMap = learnigMapUtilsList.getSubjectArrayList().get(subjectId);
            if (subjectMap != null) {
                topicList = new ArrayList<>(subjectMap.getmTopicMap().values());

                for (int i = 0; i < topicList.size(); i++) {
                    HomeModel.TopicMap topicMap = topicList.get(i);
                    float num = topicMap.getSkill()[mode];
                    float den = topicMap.getmSkillMap().size();
                    innerCount.add((num / den) * 100);
                    yvalues.add(new Entry(den, i));
                    xVals.add(topicMap.getName() + " (" + (int) num + "/" + (int) den + ")");
                }
                PieDataSet dataSet = new PieDataSet(yvalues, "");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                dataSet.setValueTextSize(12f);
                dataSet.setValueTextColor(Color.WHITE);
                final PieDataExt data = new PieDataExt(xVals, dataSet, innerCount);
                data.setValueFormatter(new PercentFormatter());
                Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        binding.piechartTopic.setData(data);
                        binding.piechartTopic.setUsePercentValues(true);
                        binding.piechartTopic.setDrawHoleEnabled(false);
                        binding.piechartTopic.setDescription("");
                        binding.piechartTopic.spin(500, 0, -360f, Easing.EasingOption.EaseInOutQuad);
                        binding.piechartTopic.setOnChartValueSelectedListener(LearningMapForTopicChartActivity.this);
                        binding.learningMapHeading.setText("Topic Map For " + subjectMap.getName());
                        Legend l = binding.piechartTopic.getLegend();
                        l.setEnabled(false);

                        binding.piechartTopic.invalidate();
                    }
                });
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mode = position;
        renderTopicChart();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Intent intent = new Intent(this, LearningMapForSkillChartNewActivity.class);
        HomeModel.TopicMap skillData = topicList.get(e.getXIndex());
        intent.putExtra("topicId", skillData.getTid());
        intent.putExtra("subjectId", subjectId);
        startActivity(intent);
        binding.piechartTopic.highlightValue(-1,-1);
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding != null){
            binding.piechartTopic.highlightValue(-1,-1);
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
