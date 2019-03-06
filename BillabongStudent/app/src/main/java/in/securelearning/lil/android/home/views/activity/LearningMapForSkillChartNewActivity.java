package in.securelearning.lil.android.home.views.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityLearningMapForSkillChartBinding;
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

public class LearningMapForSkillChartNewActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {
    private ActivityLearningMapForSkillChartBinding binding;
    @Inject
    HomeModel mHomeModel;
    LearnigMapUtils learnigMapUtilsList;
    ArrayList<HomeModel.SkillMap> skillList;
    String topicId;
    String subjectId;
    int size;
    int[] colorCode;
    int selectedItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_learning_map_for_skill_chart);
        getWindow().setStatusBarColor(ContextCompat.getColor(LearningMapForSkillChartNewActivity.this, R.color.colorPrimary));
        topicId = getIntent().getStringExtra("topicId");
        subjectId = getIntent().getStringExtra("subjectId");
        binding.imagebuttonBack.setOnClickListener(LearningMapForSkillChartNewActivity.this);
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
                        renderSkillChart();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void addItemsOnSpinner(List<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                v = super.getDropDownView(position, null, parent);
                // If this is the selected item position
                if (position == selectedItem) {
                    v.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorBackground));
                } else {
                    // for other views
                    v.setBackgroundColor(Color.WHITE);
                }
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSkillList.setAdapter(dataAdapter);
        binding.spinnerSkillList.setOnItemSelectedListener(this);
        binding.skillChart.invalidate();
    }

    private void renderSkillChart() {
        ArrayList<BarEntry> yvalues = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        final List<String> list = new ArrayList<>();

        if (learnigMapUtilsList != null) {
            HomeModel.SubjectMap subjectMap = learnigMapUtilsList.getSubjectArrayList().get(subjectId);
            final HomeModel.TopicMap topicMap = subjectMap.getmTopicMap().get(topicId);
            skillList = new ArrayList<>(topicMap.getmSkillMap().values());
            colorCode = new int[skillList.size()];
            for (int i = 0; i < skillList.size(); i++) {
                HomeModel.SkillMap skillMap = skillList.get(i);
                yvalues.add(new BarEntry(((float) Math.round(skillMap.getTotalObtained())), i));
                xVals.add(getWrappedString(skillMap.getName()));
                getColorCode(skillMap.getTotalObtained(), i);
                list.add(skillMap.getName());
            }

            BarDataSet barDataSet1 = new BarDataSet(yvalues, "");
            barDataSet1.setColors(colorCode);
//            barDataSet1.setBarSpacePercent(100f); // 100f Creates problem to render bar on screen
            final BarData data = new BarData(xVals, barDataSet1);
            data.setValueTextSize(0f); // for removeItem Text on bar

            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                @Override
                public void run() throws Exception {

                    binding.skillChart.setData(data);
                    binding.skillChart.setDescription("");
                    binding.skillChart.animateXY(2000, 2000);
                    binding.skillChart.setOnChartValueSelectedListener(LearningMapForSkillChartNewActivity.this);
                    binding.learningMapHeading.setText("Skill Map For " + topicMap.getName());

                    XAxis xAxis = binding.skillChart.getXAxis();
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setEnabled(false);

                    YAxis yAxis = binding.skillChart.getAxisLeft(); // upper part
                    yAxis.setEnabled(false);
                    yAxis = binding.skillChart.getAxisRight(); // lower part
                    yAxis.setEnabled(false);
                    yAxis.setSpaceTop(40f);

                    Legend l = binding.skillChart.getLegend();
                    l.setEnabled(false);
                    binding.skillChart.setVisibleXRangeMaximum(5f);
                    binding.skillChart.setVisibleXRangeMinimum(5f);
                    binding.skillChart.setDoubleTapToZoomEnabled(false);
                    binding.skillChart.setPinchZoom(false);
                    binding.skillChart.setVisibleYRangeMaximum(100f, YAxis.AxisDependency.LEFT);
                    binding.skillChart.invalidate();

                    addItemsOnSpinner(list);
                }
            });

        }
    }

    private void getColorCode(double totalObtained, int i) {
        if (totalObtained >= 70) {
            colorCode[i] = Color.rgb(167, 223, 102);
        } else if (totalObtained >= 40) {
            colorCode[i] = Color.rgb(252, 253, 120);
        } else {
            colorCode[i] = Color.rgb(255, 106, 106);
        }
    }

    private String getWrappedString(String name) {
        String result = "";
        int wraps = name.length() / 50;
        if (wraps > 2) {
            result = name.substring(0, 50) + "\n" + name.substring(50, 100) + "\n" + name.substring(100);
        } else if (wraps > 1) {
            result = name.substring(0, 50) + "\n" + name.substring(50);
        } else {
            result = name;
        }
        return result;
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        binding.textviewSkillName.setText(parent.getItemAtPosition(position).toString());
        binding.textviewSkillNameExtra.setText(skillList.get(position).getSkillLevel().toString());
        binding.skillChart.highlightValue(position, 0); // For selection in Skill chart highlight
        selectedItem = position; // For highlight spinner
        binding.skillChart.centerViewTo(position, 100, YAxis.AxisDependency.RIGHT);
        binding.skillChart.invalidate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        binding.spinnerSkillList.setSelection(h.getXIndex());
        binding.skillChart.invalidate();
    }

    @Override
    public void onNothingSelected() {
        System.out.println("Nothing selected");
    }
}
