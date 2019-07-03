package in.securelearning.lil.android.analytics.views.fragment;

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

import in.securelearning.lil.android.analytics.views.adapter.StudentCoverageAdapter;
import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationData;
import in.securelearning.lil.android.analytics.dataobjects.CoverageChartData;
import in.securelearning.lil.android.analytics.helper.MyPercentFormatter;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutStudentAnalyticsCoverageBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.analytics.helper.ChartXAxisRenderer;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Float.NaN;

public class StudentCoverageFragment extends Fragment {

    @Inject
    AnalyticsModel mAnalyticsModel;
    private Context mContext;
    private LayoutStudentAnalyticsCoverageBinding mBinding;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;
    private ArrayList<ChartConfigurationData> mChartConfigurationData = null;

    public static Fragment newInstance(ArrayList<ChartConfigurationData> coverageConfiguration) {
        StudentCoverageFragment fragment = new StudentCoverageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ConstantUtil.COVERAGE, (Serializable) coverageConfiguration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable(ConstantUtil.COVERAGE) != null) {

            this.mChartConfigurationData = (ArrayList<ChartConfigurationData>) getArguments().getSerializable(ConstantUtil.COVERAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_student_analytics_coverage, container, false);
        if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            fetchCoverageData(mChartConfigurationData);
        } else {
            mBinding.textViewNoCoverageData.setVisibility(View.VISIBLE);
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
                fetchCoverageData(mChartConfigurationData);
            } else {
                mBinding.textViewNoCoverageData.setVisibility(View.VISIBLE);
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
    private void fetchCoverageData(final ArrayList<ChartConfigurationData> coverageConfiguration) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mBinding.progressBarCoverage.setVisibility(View.VISIBLE);
            mAnalyticsModel.fetchCoverageData("").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<CoverageChartData>>() {
                        @Override
                        public void accept(ArrayList<CoverageChartData> coverageChartData) throws Exception {
                            mBinding.progressBarCoverage.setVisibility(View.GONE);
                            if (coverageConfiguration != null && !coverageConfiguration.isEmpty() && !coverageChartData.isEmpty()) {
                                mBinding.chartCoverage.setVisibility(View.VISIBLE);
                                mBinding.textViewNoCoverageData.setVisibility(View.GONE);

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

    /*Draw bar chart for progress*/
    private void drawCoverageChart(ArrayList<ChartConfigurationData> coverageConfiguration, ArrayList<CoverageChartData> coverageChartData) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<BarEntry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        List<LegendEntry> legendEntries = new ArrayList<>();
        Collections.sort(coverageChartData);
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

            int coverage = Math.round(data.getCoverage());
            values.add(new BarEntry(i, coverage, data));

            xAxisLabel.add(data.getName());
            colors.add(pickColorAccording(coverage));
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
        rightAxis.setGridColor(ContextCompat.getColor(mContext, R.color.colorTransparent));

        YAxis leftAxis = mBinding.chartCoverage.getAxisLeft();
        leftAxis.setGridColor(ContextCompat.getColor(mContext, R.color.colorTransparent));
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
        mBinding.chartCoverage.getLegend().setWordWrapEnabled(true);
        mBinding.chartCoverage.setXAxisRenderer(new ChartXAxisRenderer(mBinding.chartCoverage.getViewPortHandler(), mBinding.chartCoverage.getXAxis(), mBinding.chartCoverage.getTransformer(YAxis.AxisDependency.LEFT)));
        mBinding.chartCoverage.setExtraBottomOffset(10);

        mBinding.chartCoverage.clear();
        mBinding.chartCoverage.setData(data);
        mBinding.chartCoverage.invalidate();

        showHighestValue(coverageChartData);

        mBinding.chartCoverage.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                CoverageChartData ccd = (CoverageChartData) e.getData();
                int coverage = Math.round(ccd.getCoverage());

                //startActivity(ProgressDetailActivity.getStartIntent(mContext, ccd.getId(), ccd.getName(), coverage));
                drawProgress(coverage);
                fetchCoverageData(ccd.getId());
                pickColorAccording(coverage);
                mBinding.llExcellence.setVisibility(View.VISIBLE);
                mBinding.textViewPerformance.setText(ccd.getName());
                mBinding.textViewPerformanceCount.setText(String.valueOf(coverage + "%"));
            }

            @Override
            public void onNothingSelected() {

            }
        });


    }

    private void showHighestValue(ArrayList<CoverageChartData> list) {
        if (list != null && !list.isEmpty() && list.get(0) != null) {
            CoverageChartData data = list.get(0);
            int coverage = Math.round(data.getCoverage());

            mBinding.llExcellence.setVisibility(View.VISIBLE);
            mBinding.textViewPerformance.setText(data.getName());
            mBinding.textViewPerformanceCount.setText(coverage + "%");
            mBinding.pieChartPerformance.setVisibility(View.VISIBLE);
            drawProgress(coverage);
            fetchCoverageData(data.getId());
        }
    }

    public int pickColorAccording(int coverage) {


        for (int j = 0; j < mChartConfigurationData.size(); j++) {
            ChartConfigurationData chartConfigurationData = mChartConfigurationData.get(j);
            if (coverage >= chartConfigurationData.getFrom() && coverage <= chartConfigurationData.getTo()) {
                return Color.parseColor(chartConfigurationData.getColorCode());
            }
        }
        return R.color.colorRed;
    }

    private void drawProgress(int performance) {
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
        String centerTextValue =(performance) + "%";
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
    private void fetchCoverageData(String subjectId) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mBinding.progressBarCoverage.setVisibility(View.VISIBLE);
            mAnalyticsModel.fetchCoverageData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<CoverageChartData>>() {
                        @Override
                        public void accept(ArrayList<CoverageChartData> coverageChartData) throws Exception {
                            mBinding.progressBarCoverage.setVisibility(View.GONE);
                            if (!coverageChartData.isEmpty()) {
                                mBinding.layoutRecyclerView.setVisibility(View.VISIBLE);
                                mBinding.textViewNoCoverageData.setVisibility(View.GONE);
                                setPerformanceTopicList(coverageChartData);
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
            showInternetSnackBar(subjectId);
        }
    }

    private void setPerformanceTopicList(ArrayList<CoverageChartData> coverageChartData) {
        mBinding.recyclerView.setNestedScrollingEnabled(false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(new StudentCoverageAdapter(mContext, coverageChartData, StudentCoverageFragment.this));
    }

    private void showInternetSnackBar(final String subjectId) {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchCoverageData(subjectId);

                    }
                })
                .show();

    }

    private void showInternetSnackBar() {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchCoverageData(mChartConfigurationData);
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
