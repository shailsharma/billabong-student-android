package in.securelearning.lil.android.analytics.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationData;
import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationParentData;
import in.securelearning.lil.android.analytics.dataobjects.CoverageChartData;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartData;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.analytics.dataobjects.PerformanceChartData;
import in.securelearning.lil.android.analytics.helper.MyPercentFormatter;
import in.securelearning.lil.android.analytics.helper.PiePercentFormatter;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsStudentBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Float.NaN;

public class StudentAnalyticsActivity extends AppCompatActivity {

    LayoutAnalyticsStudentBinding mBinding;

    @Inject
    AnalyticsModel mAnalyticsModel;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, StudentAnalyticsActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_analytics_student);

        mAnalyticsModel.setImmersiveStatusBar(getWindow());
        fetchEffortData();
        initializeClickListeners();
    }

    private void initializeClickListeners() {
        mBinding.layoutToolbar.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mBinding.layoutToolbar.textViewToolbarTitle.setText(getString(R.string.labelAnalytics));

    }

    @SuppressLint("CheckResult")
    private void fetchChartConfiguration() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchChartConfiguration().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ChartConfigurationParentData>() {
                        @Override
                        public void accept(ChartConfigurationParentData chartConfigurationParentData) throws Exception {

                            fetchPerformanceData(chartConfigurationParentData.getPerformanceConfiguration());

                            fetchCoverageData(chartConfigurationParentData.getCoverageConfiguration());

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBarPerformance.setVisibility(View.GONE);
                            mBinding.progressBarCoverage.setVisibility(View.GONE);
                            mBinding.textViewNoPerformanceData.setVisibility(View.VISIBLE);
                            mBinding.textViewNoCoverageData.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            showInternetSnackBar();
        }

    }

    @SuppressLint("CheckResult")
    private void fetchEffortData() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchEffortData().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EffortChartDataParent>() {
                        @Override
                        public void accept(EffortChartDataParent effortChartDataParent) throws Exception {
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            fetchChartConfiguration();
                            if (!effortChartDataParent.getEffortChartDataList().isEmpty()) {
                                mBinding.layoutTotalTimeSpent.setVisibility(View.VISIBLE);
                                mBinding.chartEffort.setVisibility(View.VISIBLE);
                                mBinding.layoutDailyTimeSpent.setVisibility(View.VISIBLE);
                                drawPieChart(effortChartDataParent.getEffortChartDataList(), effortChartDataParent.getDaysCount());
                            } else {
                                mBinding.layoutTotalTimeSpent.setVisibility(View.GONE);
                                mBinding.chartEffort.setVisibility(View.GONE);
                                mBinding.layoutDailyTimeSpent.setVisibility(View.GONE);
                                mBinding.textViewNoEffortData.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            fetchChartConfiguration();
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            mBinding.layoutTotalTimeSpent.setVisibility(View.GONE);
                            mBinding.chartEffort.setVisibility(View.GONE);
                            mBinding.layoutDailyTimeSpent.setVisibility(View.GONE);
                            mBinding.textViewNoEffortData.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            showInternetSnackBar();
        }

    }

    @SuppressLint("CheckResult")
    private void fetchCoverageData(final ArrayList<ChartConfigurationData> coverageConfiguration) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchCoverageData("").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<CoverageChartData>>() {
                        @Override
                        public void accept(ArrayList<CoverageChartData> coverageChartData) throws Exception {
                            mBinding.progressBarCoverage.setVisibility(View.GONE);
                            if (coverageConfiguration != null && !coverageConfiguration.isEmpty() && !coverageChartData.isEmpty()) {
                                mBinding.chartCoverage.setVisibility(View.VISIBLE);
                                drawCoverageChart(coverageConfiguration, coverageChartData);
                            } else {
                                mBinding.textViewNoCoverageData.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBarCoverage.setVisibility(View.GONE);
                            mBinding.textViewNoCoverageData.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            showInternetSnackBar();
        }

    }

    @SuppressLint("CheckResult")
    private void fetchPerformanceData(final ArrayList<ChartConfigurationData> performanceConfiguration) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchPerformanceData("").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<PerformanceChartData>>() {
                        @Override
                        public void accept(ArrayList<PerformanceChartData> performanceChartData) throws Exception {
                            mBinding.progressBarPerformance.setVisibility(View.GONE);
                            if (performanceConfiguration != null && !performanceConfiguration.isEmpty() && !performanceChartData.isEmpty()) {
                                mBinding.chartPerformance.setVisibility(View.VISIBLE);
                                drawPerformanceBarChart(performanceConfiguration, performanceChartData);
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
            showInternetSnackBar();
        }

    }

    private void showInternetSnackBar() {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchEffortData();
                    }
                })
                .show();

    }

    /*Draw pie chart for time spent*/
    private void drawPieChart(ArrayList<EffortChartData> effortChartData, int daysCount) {
        float totalTimeSpent = 0;
        float totalReadTime = 0;
        float totalVideoTime = 0;
        float totalPracticeTime = 0;
        ArrayList<PieEntry> entryArrayList = new ArrayList<PieEntry>();
        for (int i = 0; i < effortChartData.size(); i++) {
            if (effortChartData.get(i).getTotalTimeSpent() <= 0 || effortChartData.get(i).getSubject().isEmpty()) {
                effortChartData.remove(effortChartData.get(i));
            }
        }
        for (int i = 0; i < effortChartData.size(); i++) {
            entryArrayList.add(new PieEntry(effortChartData.get(i).getTotalTimeSpent(), effortChartData.get(i).getSubject().get(0).getName(), effortChartData.get(i)));
            totalTimeSpent += effortChartData.get(i).getTotalTimeSpent();
            totalReadTime += effortChartData.get(i).getTotalReadTimeSpent();
            totalVideoTime += effortChartData.get(i).getTotalVideoTimeSpent();
            totalPracticeTime += effortChartData.get(i).getTotalPracticeTimeSpent();
        }
        /*Total time spent*/
        String formattedTotalTimeSpent = mAnalyticsModel.convertSecondToHourMinuteSecond((long) totalTimeSpent);
        mBinding.textViewTotalTimeSpent.setText(formattedTotalTimeSpent);
        final float finalTotalTimeSpent = totalTimeSpent;
        final float finalTotalReadTime = totalReadTime;
        final float finalTotalVideoTime = totalVideoTime;
        final float finalTotalPracticeTime = totalPracticeTime;

        mBinding.textViewDailyTimeSpentLabel.setPaintFlags(mBinding.textViewDailyTimeSpentLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mBinding.textViewTotalTimeSpentLabel.setPaintFlags(mBinding.textViewTotalTimeSpentLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mBinding.layoutTotalTimeSpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedTotalTimeSpent(StudentAnalyticsActivity.this, finalTotalTimeSpent, finalTotalReadTime, finalTotalVideoTime, finalTotalPracticeTime);
            }
        });

        /*Daily time spent*/
        final float dailyTimeSpent = totalTimeSpent / daysCount;
        final float dailyReadTimeSpent = totalReadTime / daysCount;
        final float dailyVideoTimeSpent = totalVideoTime / daysCount;
        final float dailyPracticeTimeSpent = totalPracticeTime / daysCount;
        String formattedDailyTimeSpent = mAnalyticsModel.convertSecondToMinute((long) dailyTimeSpent);
        mBinding.textViewDailyTimeSpent.setText(formattedDailyTimeSpent);


        mBinding.layoutDailyTimeSpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedDailyTimeSpent(StudentAnalyticsActivity.this, dailyTimeSpent, dailyReadTimeSpent, dailyVideoTimeSpent, dailyPracticeTimeSpent);
            }
        });

        /*Pie chart*/
        PieDataSet dataSet = new PieDataSet(entryArrayList, "");
        int[] colors = new int[]{ContextCompat.getColor(getBaseContext(), R.color.dot_dark_screen1),
                ContextCompat.getColor(getBaseContext(), R.color.dot_dark_screen2),
                ContextCompat.getColor(getBaseContext(), R.color.dot_dark_screen3),
                ContextCompat.getColor(getBaseContext(), R.color.dot_dark_screen4),
                ContextCompat.getColor(getBaseContext(), R.color.dot_dark_screen5),
                ContextCompat.getColor(getBaseContext(), R.color.dot_dark_screen6),
                ContextCompat.getColor(getBaseContext(), R.color.dot_dark_screen7),
                ContextCompat.getColor(getBaseContext(), R.color.colorGreyDark),
                ContextCompat.getColor(getBaseContext(), R.color.colorCenterGradient),
                ContextCompat.getColor(getBaseContext(), R.color.colorLNRed),
                ContextCompat.getColor(getBaseContext(), R.color.colorGreyDark)};
        dataSet.setColors(ColorTemplate.createColors(colors));
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PiePercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        Legend legend = mBinding.chartEffort.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
        legend.setFormToTextSpace(8f);
        legend.setXEntrySpace(12f);

        mBinding.chartEffort.setData(data);
        mBinding.chartEffort.invalidate();
        mBinding.chartEffort.setDrawHoleEnabled(false);
        mBinding.chartEffort.setHoleRadius(0f);
        mBinding.chartEffort.getDescription().setEnabled(false);
        mBinding.chartEffort.setClickable(true);
        mBinding.chartEffort.setTouchEnabled(true);
        mBinding.chartEffort.setRotationEnabled(false);
        mBinding.chartEffort.setDrawEntryLabels(false);
        mBinding.chartEffort.animateXY(1400, 1400);
        mBinding.chartEffort.setUsePercentValues(true);

        mBinding.chartEffort.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                EffortChartData chartData = (EffortChartData) e.getData();
                startActivity(TimeEffortDetailActivity.getStartIntent(getBaseContext(), chartData.getId(), chartData.getSubject().get(0).getName()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    /*Draw bar chart for performance*/
    private void drawPerformanceBarChart(ArrayList<ChartConfigurationData> performanceConfiguration, ArrayList<PerformanceChartData> performanceChartData) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<BarEntry> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
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
        leftAxis.setGridColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

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

        mBinding.chartPerformance.getDescription().setEnabled(false);
        mBinding.chartPerformance.setMaxVisibleValueCount(100);
        mBinding.chartPerformance.setPinchZoom(false);
        mBinding.chartPerformance.setDrawGridBackground(false);
        mBinding.chartPerformance.getXAxis().setDrawGridLines(false);
        mBinding.chartPerformance.setDrawBarShadow(false);
        mBinding.chartPerformance.setDrawValueAboveBar(true);
        mBinding.chartPerformance.setHighlightFullBarEnabled(false);
        mBinding.chartPerformance.setScaleXEnabled(false);
        mBinding.chartPerformance.setScaleYEnabled(false);
        mBinding.chartPerformance.setFitBars(true);
        mBinding.chartPerformance.animateY(1400);
        mBinding.chartPerformance.clear();
        mBinding.chartPerformance.setData(barData);
        mBinding.chartPerformance.invalidate();

        mBinding.chartPerformance.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PerformanceChartData ccd = (PerformanceChartData) e.getData();
                float performance = Math.round(ccd.getPerformance());
                startActivity(PerformanceDetailActivity.getStartIntent(getBaseContext(), ccd.getId(), ccd.getName(), performance));

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    /*Draw bar chart for progress*/
    private void drawCoverageChart(ArrayList<ChartConfigurationData> coverageConfiguration, ArrayList<CoverageChartData> coverageChartData) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<BarEntry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        List<LegendEntry> legendEntries = new ArrayList<>();

        /*Add legend entries according to configuration*/
        for (int i = 0; i < coverageConfiguration.size(); i++) {
            ChartConfigurationData configurationData = coverageConfiguration.get(i);
            legendEntries.add(new LegendEntry(configurationData.getLabel(), Legend.LegendForm.SQUARE, NaN, NaN, null, Color.parseColor(configurationData.getColorCode())));

        }

        /*To remove data from list in which coverage value is 0*/
        for (int i = 0; i < coverageChartData.size(); i++) {
            if (coverageChartData.get(i).getCoverage() <= 0) {
                coverageChartData.remove(coverageChartData.get(i));
            }
        }

        /*Adding chart data for Y and X axis*/
        for (int i = 0; i < coverageChartData.size(); i++) {
            CoverageChartData data = coverageChartData.get(i);

            float coverage = (data.getCoverage() / data.getTotal()) * 100;
            values.add(new BarEntry(i, coverage, data));

            xAxisLabel.add(data.getName());


            for (int j = 0; j < coverageConfiguration.size(); j++) {
                ChartConfigurationData chartConfigurationData = coverageConfiguration.get(j);
                if (coverage >= chartConfigurationData.getFrom() && coverage <= chartConfigurationData.getTo()) {
                    colors.add(Color.parseColor(chartConfigurationData.getColorCode()));
                }
            }

        }

        BarDataSet set = new BarDataSet(values, "");
        if (!colors.isEmpty()) {
            set.setColors(colors);
        }
        set.setDrawIcons(false);
        set.setStackLabels(new String[]{""});
        set.setHighLightAlpha(0);
        set.setValueFormatter(new MyPercentFormatter());

        YAxis rightAxis = mBinding.chartCoverage.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setGridColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

        YAxis leftAxis = mBinding.chartCoverage.getAxisLeft();
        leftAxis.setGridColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setValueFormatter(new MyPercentFormatter());

        Legend legend = mBinding.chartCoverage.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setFormSize(8f);
        legend.setFormToTextSpace(8f);
        legend.setXEntrySpace(12f);
        legend.setCustom(legendEntries);

        XAxis xAxis = mBinding.chartCoverage.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabel.get((int) value);
            }
        });


        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(mAnalyticsModel.barTextSize());
        data.setBarWidth(mAnalyticsModel.barWidth());

        mBinding.chartCoverage.getDescription().setEnabled(false);
        mBinding.chartCoverage.setMaxVisibleValueCount(100);
        mBinding.chartCoverage.setPinchZoom(false);
        mBinding.chartCoverage.setDrawGridBackground(false);
        mBinding.chartCoverage.getXAxis().setDrawGridLines(false);
        mBinding.chartCoverage.setDrawBarShadow(true);
        mBinding.chartCoverage.setDrawValueAboveBar(true);
        mBinding.chartCoverage.setHighlightFullBarEnabled(false);
        mBinding.chartCoverage.setScaleXEnabled(false);
        mBinding.chartCoverage.setScaleYEnabled(false);
        mBinding.chartCoverage.setFitBars(true);
        mBinding.chartCoverage.animateY(1400);
        mBinding.chartCoverage.clear();
        mBinding.chartCoverage.setData(data);
        mBinding.chartCoverage.invalidate();

        mBinding.chartCoverage.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                CoverageChartData ccd = (CoverageChartData) e.getData();
                float coverage = Math.round((ccd.getCoverage() / ccd.getTotal()) * 100);
                startActivity(ProgressDetailActivity.getStartIntent(getBaseContext(), ccd.getId(), ccd.getName(), coverage));

            }

            @Override
            public void onNothingSelected() {

            }
        });


    }


}
