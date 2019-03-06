package in.securelearning.lil.android.home.views.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityLearningMapNewBinding;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.activity.LearningMapForTopicBarChartActivity;
import in.securelearning.lil.android.home.views.activity.LearningMapForTopicChartActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LearningMapOldFragment extends Fragment implements View.OnClickListener {

    private ActivityLearningMapNewBinding binding;

    private OnFragmentInteractionListener mListener;
    @Inject
    HomeModel mHomeModel;

    int mode = 2;
    int size;
    ArrayList<HomeModel.SubjectMap> subjectList = null;
    LearnigMapUtils learnigMapUtilsList;
    int[] COLORS;
    private boolean flag = false;

    public LearningMapOldFragment() {
    }

    public static LearningMapOldFragment newInstance() {
        LearningMapOldFragment fragment = new LearningMapOldFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_learning_map_new, container, false);
        Observable.create(new ObservableOnSubscribe<LearnigMapUtils>() {
            @Override
            public void subscribe(ObservableEmitter<LearnigMapUtils> e) throws Exception {
                LearnigMapUtils learnigMapUtils = mHomeModel.getDefaultMapValue();
                if (learnigMapUtils != null) {
                    e.onNext(learnigMapUtils);
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
                        if (learnigMapUtils.getSubjectArrayList().size() > 0) {
                            getPieChartData();
                            addItemsOnSpinner();
                            getBarChartData();
                        } else {
                            binding.layPieChart.setVisibility(View.GONE);
                            binding.layBarChart.setVisibility(View.GONE);
                            binding.btnChangeChart.setVisibility(View.GONE);
                            binding.layChartError.setVisibility(View.VISIBLE);
                            binding.txtErrorMessage.setText("You have not attempted any quiz to visualize your sc" +
                                    "ore");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        binding.btnChangeChart.setOnClickListener(this);
        return binding.getRoot();
    }

    private void checkChartView() {
        if (binding.layPieChart.getVisibility() == View.VISIBLE) {
            binding.layPieChart.setVisibility(View.GONE);
            binding.btnChangeChart.setBackgroundResource(R.drawable.ic_insert_chart_black_24dp);
            binding.layBarChart.setVisibility(View.VISIBLE);
        } else {
            binding.layBarChart.setVisibility(View.GONE);
            binding.btnChangeChart.setBackgroundResource(R.drawable.ic_pie_chart_black_24dp);
            binding.layPieChart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_chart:
                checkChartView();
                break;
        }
    }

    private void getPieChartData() {
        Observable.create(new ObservableOnSubscribe<PieDataExt>() {
            @Override
            public void subscribe(ObservableEmitter<PieDataExt> e) throws Exception {
                ArrayList<Entry> yvalues = new ArrayList<>();
                ArrayList<String> xVals = new ArrayList<>();
                ArrayList<Float> innerCount = new ArrayList<>();
                if (learnigMapUtilsList != null) {
                    subjectList = new ArrayList<>(learnigMapUtilsList.getSubjectArrayList().values());
                    size = subjectList.size();
                    COLORS = new int[size];
                    for (int i = 0; i < subjectList.size(); i++) {
                        HomeModel.SubjectMap subjectMap = subjectList.get(i);
                        float num = subjectMap.getTopic()[mode];
                        float den = subjectMap.getmTopicMap().size();
                        innerCount.add((num / den) * 100);
                        yvalues.add(new Entry(den, i));
                        xVals.add(subjectMap.getName() + " (" + (int) num + "/" + (int) den + ")");
                        getColorCodeForSubject(subjectMap.getName(), i);
                    }

                    PieDataSet dataSet = new PieDataSet(yvalues, "");
                    dataSet.setColors(COLORS);
                    dataSet.setValueTextSize(12f);
                    dataSet.setValueTextColor(Color.WHITE);

                    final PieDataExt data = new PieDataExt(xVals, dataSet, innerCount);
                    data.setValueFormatter(new PercentFormatter());

                    e.onNext(data);
                    e.onComplete();
                }
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PieDataExt>() {
                    @Override
                    public void accept(PieDataExt data) throws Exception {
                        binding.piechartSubject.setData(data);
                        binding.piechartSubject.setUsePercentValues(true);
                        binding.piechartSubject.setDrawHoleEnabled(false);
                        binding.piechartSubject.setDescription("");
                        binding.piechartSubject.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                            @Override
                            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                                Intent intent = new Intent(getActivity(), LearningMapForTopicChartActivity.class);
                                HomeModel.SubjectMap topicData = subjectList.get(e.getXIndex());
                                intent.putExtra("subjectId", topicData.getSid());
                                startActivity(intent);
                                binding.piechartSubject.highlightValue(-1, -1);
                            }

                            @Override
                            public void onNothingSelected() {

                            }
                        });
                        Legend l = binding.piechartSubject.getLegend();
                        l.setEnabled(false);
                        binding.piechartSubject.invalidate();
                    }
                });
    }

    private void addItemsOnSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Outstanding");
        list.add("In-progress");
        list.add("Need's Attention");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMode.setAdapter(dataAdapter);
        binding.spinnerMode.setSelection(mode);
        binding.spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mode = position;
                getPieChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getColorCodeForSubject(String subject, int i) {
        int colorSet;
        if (subject.equalsIgnoreCase("english")) {
            colorSet = Color.rgb(244, 67, 54);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("mathematics")) {
            colorSet = Color.rgb(123, 31, 162);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("history")) {
            colorSet = Color.rgb(139, 195, 74);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("science")) {
            colorSet = Color.rgb(211, 47, 47);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("hindi")) {
            colorSet = Color.rgb(0, 150, 136);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("sanskrit")) {
            colorSet = Color.rgb(255, 193, 7);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("physics")) {
            colorSet = Color.rgb(25, 118, 210);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("chemistry")) {
            colorSet = Color.rgb(76, 175, 80);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("geography")) {
            colorSet = Color.rgb(158, 158, 158);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("economics")) {
            colorSet = Color.rgb(81, 45, 168);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("social science ")) {
            colorSet = Color.rgb(255, 87, 34);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("geometry")) {
            colorSet = Color.rgb(96, 125, 139);
            COLORS[i] = colorSet;
        } else if (subject.equalsIgnoreCase("computer science")) {
            colorSet = Color.rgb(194, 24, 91);
            COLORS[i] = colorSet;
        } else {
            colorSet = Color.rgb(171, 71, 188);
            COLORS[i] = colorSet;
        }
    }

    private void getBarChartData() {
        Observable.create(new ObservableOnSubscribe<BarData>() {
            @Override
            public void subscribe(ObservableEmitter<BarData> e) throws Exception {
                subjectList = new ArrayList<>(learnigMapUtilsList.getSubjectArrayList().values());
                size = subjectList.size();
                final ArrayList<BarEntry> yVals1 = new ArrayList<>();
                final ArrayList<String> xVals = new ArrayList<>();

                for (int i = 0; i < subjectList.size(); i++) {
                    HomeModel.SubjectMap subjectMap = subjectList.get(i);
                    final float value = (float) (subjectMap.getTotalObtained() / subjectMap.getmTopicMap().size());
                    final float num1 = subjectMap.getTopic()[0] * value;
                    final float num2 = subjectMap.getTopic()[1] * value;
                    final float num3 = subjectMap.getTopic()[2] * value;

                    yVals1.add(new BarEntry(new float[]{num1, num2, num3}, i));
                    xVals.add(subjectMap.getName());
                }

                final int[] colorCode = {Color.rgb(167, 223, 102), Color.rgb(252, 253, 120), Color.rgb(255, 106, 106)};
                BarDataSet set1 = new BarDataSet(yVals1, "             Subject Composite Chart");
                set1.setColors(colorCode);
                set1.setStackLabels(new String[]{"Outstanding", "In progress", "Needs Attention"});
                final BarData data = new BarData(xVals, set1);
                data.setValueFormatter(new LearnigMapUtils.MyValueFormatter());
                data.setValueTextColor(Color.TRANSPARENT);
                data.setValueTextSize(0f);
                e.onNext(data);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BarData>() {
                    @Override
                    public void accept(BarData data) throws Exception {
                        binding.chart.setData(data);
                        binding.chart.setVisibleXRangeMaximum(5f);
                        binding.chart.setVisibleXRangeMinimum(5f);
                        binding.chart.setDoubleTapToZoomEnabled(false);
                        binding.chart.setPinchZoom(false);
                        binding.chart.setDescription("");
                        binding.chart.setDrawValueAboveBar(false);
                        binding.chart.setDrawGridBackground(false);
                        binding.chart.setDrawBarShadow(false);

                        XAxis xLabels = binding.chart.getXAxis();
                        xLabels.setPosition(XAxis.XAxisPosition.TOP);
                        xLabels.setDrawGridLines(false);
                        xLabels.setDrawAxisLine(false);

                        Legend l = binding.chart.getLegend();
                        l.setFormSize(8f);
                        l.setFormToTextSpace(4f);
                        l.setXEntrySpace(6f);

                        YAxis yAxis = binding.chart.getAxisLeft(); // upper part
                        yAxis.setEnabled(false);
                        yAxis = binding.chart.getAxisRight(); // lower part
                        yAxis.setEnabled(false);
                        yAxis.setValueFormatter(new LearnigMapUtils.MyAxisValueFormatter());
                        binding.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                            @Override
                            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                                if (!flag) {
                                    flag = true;
                                    Intent intent = new Intent(getActivity(), LearningMapForTopicBarChartActivity.class);
                                    HomeModel.SubjectMap topicData = subjectList.get(e.getXIndex());
                                    intent.putExtra("subjectId", topicData.getSid());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onNothingSelected() {
                            }
                        });
                        binding.chart.invalidate();
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        flag = false;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
