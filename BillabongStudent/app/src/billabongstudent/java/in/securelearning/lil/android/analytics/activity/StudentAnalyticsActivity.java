package in.securelearning.lil.android.analytics.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
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

import in.securelearning.lil.android.analytics.helper.MyPercentFormatter;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsStudentBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.dataobjects.CoverageChartData;
import in.securelearning.lil.android.syncadapter.dataobjects.EffortChartData;
import in.securelearning.lil.android.syncadapter.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.syncadapter.dataobjects.PerformanceChartData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Float.NaN;

public class StudentAnalyticsActivity extends AppCompatActivity {

    LayoutAnalyticsStudentBinding mBinding;
    public static final int COVERAGE = 1;
    public static final int EFFORTS = 2;
    public static final int PERFORMANCE = 3;

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
    private void fetchEffortData() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchEffortData().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EffortChartDataParent>() {
                        @Override
                        public void accept(EffortChartDataParent effortChartDataParent) throws Exception {
                            fetchPerformanceData();
                            mBinding.progressBarEffort.setVisibility(View.GONE);
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
                            fetchPerformanceData();
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            mBinding.layoutTotalTimeSpent.setVisibility(View.GONE);
                            mBinding.chartEffort.setVisibility(View.GONE);
                            mBinding.layoutDailyTimeSpent.setVisibility(View.GONE);
                            mBinding.textViewNoEffortData.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            showInternetSnackBar(EFFORTS);
        }

    }

    private void drawPieChart(ArrayList<EffortChartData> effortChartData, int daysCount) {
        float totalTimeSpent = 0;
        float totalReadTime = 0;
        float totalVideoTime = 0;
        float totalPracticeTime = 0;
        ArrayList<PieEntry> entryArrayList = new ArrayList<PieEntry>();
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
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new MyPercentFormatter());
        mBinding.chartEffort.setData(data);
        mBinding.chartEffort.setDrawHoleEnabled(false);
        mBinding.chartEffort.setHoleRadius(0f);
        mBinding.chartEffort.getDescription().setEnabled(false);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        mBinding.chartEffort.invalidate();
        mBinding.chartEffort.setClickable(true);
        mBinding.chartEffort.setTouchEnabled(true);
        mBinding.chartEffort.setRotationEnabled(false);
        mBinding.chartEffort.setDrawSliceText(true);
        Legend legend = mBinding.chartPerformance.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
        legend.setFormSize(8f);
        legend.setFormToTextSpace(8f);
        legend.setXEntrySpace(12f);
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


    @SuppressLint("CheckResult")
    private void fetchCoverageData() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchCoverageData("").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<CoverageChartData>>() {
                        @Override
                        public void accept(ArrayList<CoverageChartData> coverageChartData) throws Exception {
                            mBinding.progressBarCoverage.setVisibility(View.GONE);
                            if (!coverageChartData.isEmpty()) {
                                mBinding.chartCoverage.setVisibility(View.VISIBLE);
                                drawCoverageChart(coverageChartData);
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
            showInternetSnackBar(COVERAGE);
        }

    }

    @SuppressLint("CheckResult")
    private void fetchPerformanceData() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchPerformanceData("").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<PerformanceChartData>>() {
                        @Override
                        public void accept(ArrayList<PerformanceChartData> performanceChartData) throws Exception {
                            mBinding.progressBarPerformance.setVisibility(View.GONE);
                            fetchCoverageData();
                            if (!performanceChartData.isEmpty()) {
                                mBinding.chartPerformance.setVisibility(View.VISIBLE);
                                drawPerformanceBarChart(performanceChartData);
                            } else {
                                mBinding.textViewNoPerformanceData.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            fetchCoverageData();
                            mBinding.textViewNoPerformanceData.setVisibility(View.VISIBLE);
                            mBinding.progressBarPerformance.setVisibility(View.GONE);

                        }
                    });
        } else {
            showInternetSnackBar(PERFORMANCE);
        }

    }

    private void showInternetSnackBar(final int type) {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (type == COVERAGE) {
                            fetchCoverageData();
                        } else if (type == EFFORTS) {
                            fetchEffortData();
                        } else if (type == PERFORMANCE) {
                            fetchPerformanceData();
                        }
                    }
                })
                .show();

    }

    /*Draw bar chart for date wise time spent*/
    private void drawPerformanceBarChart(ArrayList<PerformanceChartData> performanceChartData) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<BarEntry> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (int i = 0; i < performanceChartData.size(); i++) {

            PerformanceChartData data = performanceChartData.get(i);

            float performance = data.getPerformance();

            values.add(new BarEntry(i, performance, data));

            xAxisLabel.add(data.getName());

            if (performance > 0 && performance <= 40) {
                colors.add(ContextCompat.getColor(getBaseContext(), R.color.colorRed));
            } else if (performance > 40 && performance <= 70) {
                colors.add(ContextCompat.getColor(getBaseContext(), R.color.colorAnnouncement));
            } else if (performance > 70) {
                colors.add(ContextCompat.getColor(getBaseContext(), R.color.colorGreenDark));
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

        List<LegendEntry> legendEntries = new ArrayList<>();
        legendEntries.add(new LegendEntry("0-40", Legend.LegendForm.SQUARE, NaN, NaN, null, ContextCompat.getColor(getBaseContext(), R.color.colorRed)));
        legendEntries.add(new LegendEntry("40-70", Legend.LegendForm.SQUARE, NaN, NaN, null, ContextCompat.getColor(getBaseContext(), R.color.colorAnnouncement)));
        legendEntries.add(new LegendEntry("70+", Legend.LegendForm.SQUARE, NaN, NaN, null, ContextCompat.getColor(getBaseContext(), R.color.colorGreenDark)));
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
        barData.setBarWidth(0.36f);
        mBinding.chartPerformance.setData(barData);
        mBinding.chartPerformance.invalidate();


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


        mBinding.chartPerformance.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PerformanceChartData ccd = (PerformanceChartData) e.getData();
                startActivity(PerformanceDetailActivity.getStartIntent(getBaseContext(), ccd.getId(), ccd.getName()));

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void drawCoverageChart(ArrayList<CoverageChartData> coverageChartData) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<BarEntry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < coverageChartData.size(); i++) {
            CoverageChartData data = coverageChartData.get(i);
            xAxisLabel.add(data.getName());
            float coverage = (data.getCoverage() / data.getTotal()) * 100;
            values.add(new BarEntry(i, coverage, data));

            if (coverage > 0 && coverage <= 40) {
                colors.add(ContextCompat.getColor(getBaseContext(), R.color.colorRed));
            } else if (coverage > 40 && coverage <= 70) {
                colors.add(ContextCompat.getColor(getBaseContext(), R.color.colorAnnouncement));
            } else if (coverage > 70) {
                colors.add(ContextCompat.getColor(getBaseContext(), R.color.colorGreenDark));
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

        List<LegendEntry> legendEntries = new ArrayList<>();
        legendEntries.add(new LegendEntry("Completed", Legend.LegendForm.SQUARE, NaN, NaN, null, ContextCompat.getColor(getBaseContext(), R.color.colorGreenDark)));
        legendEntries.add(new LegendEntry("Pending", Legend.LegendForm.SQUARE, NaN, NaN, null, ContextCompat.getColor(getBaseContext(), R.color.colorGrey)));
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
        data.setBarWidth(0.36f);

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
        mBinding.chartCoverage.setData(data);
        mBinding.chartCoverage.setFitBars(true);
        mBinding.chartCoverage.animateY(2000);
        mBinding.chartCoverage.invalidate();

        mBinding.chartCoverage.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                CoverageChartData ccd = (CoverageChartData) e.getData();
                float coverage = (ccd.getCoverage() / ccd.getTotal()) * 100;
                startActivity(ProgressDetailActivity.getStartIntent(getBaseContext(), ccd.getId(), ccd.getName(), coverage));

            }

            @Override
            public void onNothingSelected() {

            }
        });


    }


}
