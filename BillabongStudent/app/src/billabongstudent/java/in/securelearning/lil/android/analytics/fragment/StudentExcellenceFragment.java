package in.securelearning.lil.android.analytics.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.adapter.StudentExcellenceAdapter;
import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationData;
import in.securelearning.lil.android.analytics.dataobjects.PerformanceChartData;
import in.securelearning.lil.android.analytics.helper.MyPercentFormatter;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutStudentAnalyticsExcellenceBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.ChartXAxisRenderer;
import in.securelearning.lil.android.syncadapter.utils.StudentConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Float.NaN;

public class StudentExcellenceFragment extends Fragment {

    @Inject
    AnalyticsModel mAnalyticsModel;
    private Context mContext;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;
    private LayoutStudentAnalyticsExcellenceBinding mBinding;
    private ArrayList<ChartConfigurationData> mChartConfigurationData = null;

    public static Fragment newInstance(ArrayList<ChartConfigurationData> performanceConfiguration) {
        StudentExcellenceFragment fragment = new StudentExcellenceFragment();
        Bundle args = new Bundle();
        args.putSerializable(StudentConstantUtil.EXCELLENCE, (Serializable) performanceConfiguration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable(StudentConstantUtil.EXCELLENCE) != null) {

            this.mChartConfigurationData = (ArrayList<ChartConfigurationData>) getArguments().getSerializable(StudentConstantUtil.EXCELLENCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_student_analytics_excellence, container, false);
        if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            if (mChartConfigurationData != null && !mChartConfigurationData.isEmpty()) {
                fetchExcellenceData(mChartConfigurationData);
            } else {
                mBinding.textViewNoExcellenceData.setVisibility(View.VISIBLE);
            }
        }

        return mBinding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            if (mChartConfigurationData != null && !mChartConfigurationData.isEmpty()) {
                fetchExcellenceData(mChartConfigurationData);
            } else {
                mBinding.textViewNoExcellenceData.setVisibility(View.VISIBLE);
            }
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
        }
    }


    @SuppressLint("CheckResult")
    private void fetchExcellenceData(final ArrayList<ChartConfigurationData> performanceConfiguration) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mAnalyticsModel.fetchPerformanceData("").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<PerformanceChartData>>() {
                        @Override
                        public void accept(ArrayList<PerformanceChartData> performanceChartData) throws Exception {
                            mBinding.progressBarExcellence.setVisibility(View.GONE);
                            if (performanceConfiguration != null && !performanceConfiguration.isEmpty() && !performanceChartData.isEmpty()) {
                                mBinding.chartPerformance.setVisibility(View.VISIBLE);
                                mBinding.textViewNoExcellenceData.setVisibility(View.GONE);
                                Collections.sort(performanceChartData);
                                drawPerformanceBarChart(performanceConfiguration, performanceChartData);

                            } else {
                                mBinding.textViewNoExcellenceData.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.textViewNoExcellenceData.setVisibility(View.VISIBLE);
                            mBinding.progressBarExcellence.setVisibility(View.GONE);

                        }
                    });
        } else {
            showInternetSnackBar();
        }

    }

    /*Draw bar chart for performance*/
    private void drawPerformanceBarChart(ArrayList<ChartConfigurationData> performanceConfiguration, ArrayList<PerformanceChartData> performanceChartData) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<BarEntry> values = new ArrayList<>();
        final List<Integer> colors = new ArrayList<>();
        List<LegendEntry> legendEntries = new ArrayList<>();

        /*Add legend entries according to configuration*/
        for (int i = 0; i < performanceConfiguration.size(); i++) {
            ChartConfigurationData configurationData = performanceConfiguration.get(i);
            legendEntries.add(new LegendEntry(configurationData.getLabel(), Legend.LegendForm.SQUARE, NaN, NaN, null, Color.parseColor(configurationData.getColorCode())));

        }

        /*To remove data from list in which performance value is 0*/
        for (int i = 0; i < performanceChartData.size(); i++) {
            if (performanceChartData.get(i).getPerformance() <= 0) {
                performanceChartData.remove(performanceChartData.get(i));
            }
        }

        /*Adding chart data for Y and X axis*/
        for (int i = 0; i < performanceChartData.size(); i++) {

            PerformanceChartData data = performanceChartData.get(i);

            float performance = data.getPerformance();
            values.add(new BarEntry(i, performance, data));

            xAxisLabel.add(data.getName());

            for (int j = 0; j < performanceConfiguration.size(); j++) {
                ChartConfigurationData chartConfigurationData = performanceConfiguration.get(j);
                if (performance >= chartConfigurationData.getFrom() && performance <= chartConfigurationData.getTo()) {
                    colors.add(Color.parseColor(chartConfigurationData.getColorCode()));
                }
            }
        }

        YAxis rightAxis = mBinding.chartPerformance.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawAxisLine(false);

        YAxis leftAxis = mBinding.chartPerformance.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setValueFormatter(new MyPercentFormatter());
        leftAxis.setGridColor(ContextCompat.getColor(mContext, R.color.colorTransparent));

        Legend legend = mBinding.chartPerformance.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setCustom(legendEntries);
        legend.setFormSize(8f);
        legend.setFormToTextSpace(8f);
        legend.setXEntrySpace(12f);
        legend.setEnabled(true);

        XAxis xAxis = mBinding.chartPerformance.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabel.get((int) value);
            }
        });

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        BarDataSet barDataSet = new BarDataSet(values, "");
        if (!colors.isEmpty()) {
            barDataSet.setColors(colors);
        }
        barDataSet.setDrawIcons(false);
        barDataSet.setHighLightAlpha(0);
        dataSets.add(barDataSet);

        BarData barData = new BarData(dataSets);
        barData.setValueTextColor(Color.BLACK);
        barData.setValueFormatter(new MyPercentFormatter());
        barData.setValueTextSize(mAnalyticsModel.barTextSize());
        barData.setBarWidth(mAnalyticsModel.barWidth());
        mBinding.chartPerformance.setXAxisRenderer(new ChartXAxisRenderer(mBinding.chartPerformance.getViewPortHandler(), mBinding.chartPerformance.getXAxis(), mBinding.chartPerformance.getTransformer(YAxis.AxisDependency.LEFT)));
        mBinding.chartPerformance.setExtraBottomOffset(10);
        mBinding.chartPerformance.getDescription().setEnabled(false);
        mBinding.chartPerformance.setMaxVisibleValueCount(100);
        mBinding.chartPerformance.setPinchZoom(false);
        mBinding.chartPerformance.setDrawGridBackground(false);
        mBinding.chartPerformance.getXAxis().setDrawGridLines(false);
        mBinding.chartPerformance.setDrawBarShadow(false);
        mBinding.chartPerformance.setDrawValueAboveBar(true);
        mBinding.chartPerformance.setHighlightFullBarEnabled(false);
        mBinding.chartPerformance.getLegend().setWordWrapEnabled(true);
        mBinding.chartPerformance.setScaleXEnabled(false);
        mBinding.chartPerformance.setScaleYEnabled(false);
        mBinding.chartPerformance.setFitBars(true);
        mBinding.chartPerformance.animateY(1400);
        mBinding.chartPerformance.clear();
        mBinding.chartPerformance.setData(barData);
        mBinding.chartPerformance.invalidate();

        //Need to show highest value
        showHighestValue(performanceChartData, colors);

        mBinding.chartPerformance.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PerformanceChartData ccd = (PerformanceChartData) e.getData();
                float performance = Math.round(ccd.getPerformance());
                drawProgress(performance);
                fetchPerformanceData(ccd.getId());
                mBinding.llExcellence.setVisibility(View.VISIBLE);
                mBinding.textViewPerformance.setText(ccd.getName());
                mBinding.textViewPerformanceCount.setText(String.format("%d %%", Math.round(ccd.getPerformance())));
                //  startActivity(PerformanceDetailActivity.getStartIntent(mContext, ccd.getId(), ccd.getName(), performance));

            }

            @Override
            public void onNothingSelected() {
                // mBinding.llExcellence.setVisibility(View.GONE);
            }

        });

    }

    public int pickColorAccording(float performance) {


        for (int j = 0; j < mChartConfigurationData.size(); j++) {
            ChartConfigurationData chartConfigurationData = mChartConfigurationData.get(j);
            if (performance >= chartConfigurationData.getFrom() && performance <= chartConfigurationData.getTo()) {
                return Color.parseColor(chartConfigurationData.getColorCode());
            }
        }
        return R.color.colorRed;
    }


    private void showHighestValue(ArrayList<PerformanceChartData> list, List<Integer> colors) {
        if (list != null && !list.isEmpty() && list.get(0) != null) {
            PerformanceChartData data = list.get(0);
            drawProgress(data.getPerformance());
            fetchPerformanceData(data.getId());
            mBinding.llExcellence.setVisibility(View.VISIBLE);
            mBinding.textViewPerformance.setText(data.getName());
            mBinding.textViewPerformanceCount.setText(String.valueOf(Math.round(data.getPerformance()) + " %"));
        }
    }

    /*draw and set values for time spent pie chart*/
    private void drawProgress(float performance) {
        float total = 100;
        float remaining = total - performance;
        ArrayList<PieEntry> fillValues = new ArrayList<>();
        fillValues.add(new PieEntry(performance));
        fillValues.add(new PieEntry(remaining));
        PieDataSet dataSet = new PieDataSet(fillValues, "");
        dataSet.setColors(pickColorAccording(performance));
        dataSet.setValueTextSize(0f);
        PieData data = new PieData(dataSet);
        mBinding.pieChartPerformance.setData(data);
        mBinding.pieChartPerformance.setHoleRadius(85f);
        mBinding.pieChartPerformance.setDrawHoleEnabled(true);
        mBinding.pieChartPerformance.setUsePercentValues(true);
        mBinding.pieChartPerformance.getDescription().setEnabled(false);
        mBinding.pieChartPerformance.setDrawCenterText(true);
        String centerTextValue = String.valueOf(Math.round(performance)) + "%";
        if (centerTextValue.contains("NaN")) {
            mBinding.pieChartPerformance.setCenterText("0%");
        } else {
            mBinding.pieChartPerformance.setCenterText(centerTextValue);
        }
        mBinding.pieChartPerformance.setCenterTextSize(18f);
        mBinding.pieChartPerformance.getLegend().setEnabled(false);
        mBinding.pieChartPerformance.invalidate();
        mBinding.pieChartPerformance.setClickable(false);
        mBinding.pieChartPerformance.setTouchEnabled(false);
    }

    @SuppressLint("CheckResult")
    private void fetchPerformanceData(String subjectId) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mAnalyticsModel.fetchPerformanceData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<PerformanceChartData>>() {
                        @Override
                        public void accept(ArrayList<PerformanceChartData> performanceChartData) throws Exception {
                            mBinding.progressBarPerformance.setVisibility(View.GONE);
                            if (!performanceChartData.isEmpty()) {
                                mBinding.layoutRecyclerView.setVisibility(View.VISIBLE);
                                mBinding.textViewNoPerformanceData.setVisibility(View.GONE);
                                setPerformanceTopicList(performanceChartData);
                            } else {
                                mBinding.textViewNoPerformanceData.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.textViewNoPerformanceData.setVisibility(View.VISIBLE);
                            mBinding.progressBarPerformance.setVisibility(View.GONE);

                        }
                    });
        } else {
            showInternetSnackBar(subjectId);
        }
    }

    private void setPerformanceTopicList(ArrayList<PerformanceChartData> performanceChartDataList) {
        mBinding.recyclerView.setNestedScrollingEnabled(false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(new StudentExcellenceAdapter(mContext, performanceChartDataList, StudentExcellenceFragment.this));
    }

    private void showInternetSnackBar() {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchExcellenceData(mChartConfigurationData);
                    }
                })
                .show();

    }
    private void showInternetSnackBar(final String subjectId) {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchPerformanceData(subjectId);

                    }
                })
                .show();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() != null) {
            mContext = getActivity();
        } else {
            mContext = context;
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;

    }
}
