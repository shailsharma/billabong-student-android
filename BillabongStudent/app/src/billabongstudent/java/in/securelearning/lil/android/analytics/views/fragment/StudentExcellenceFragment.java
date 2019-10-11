package in.securelearning.lil.android.analytics.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
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
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.ChartConfigurationData;
import in.securelearning.lil.android.analytics.dataobjects.PerformanceChartData;
import in.securelearning.lil.android.analytics.helper.ChartXAxisRenderer;
import in.securelearning.lil.android.analytics.helper.MyPercentFormatter;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.analytics.views.adapter.StudentExcellenceAdapter;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutStudentAnalyticsExcellenceBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Float.NaN;

public class StudentExcellenceFragment extends Fragment {

    @Inject
    AnalyticsModel mAnalyticsModel;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;
    private LayoutStudentAnalyticsExcellenceBinding mBinding;
    private ArrayList<ChartConfigurationData> mChartConfigurationData = null;
    private Context mContext;

    public static Fragment newInstance(ArrayList<ChartConfigurationData> performanceConfiguration) {
        StudentExcellenceFragment fragment = new StudentExcellenceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ConstantUtil.EXCELLENCE, (Serializable) performanceConfiguration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable(ConstantUtil.EXCELLENCE) != null) {

            this.mChartConfigurationData = (ArrayList<ChartConfigurationData>) getArguments().getSerializable(ConstantUtil.EXCELLENCE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_student_analytics_excellence, container, false);

        if (!fragmentResume && fragmentVisible) {   //only when first time activity is created
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
        if (visible && isResumed()) {   // only at activity screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            if (mChartConfigurationData != null && !mChartConfigurationData.isEmpty()) {
                fetchExcellenceData(mChartConfigurationData);
            } else {
                mBinding.textViewNoExcellenceData.setVisibility(View.VISIBLE);
            }
        } else if (visible) {        // only at activity onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) {// only when you go out of activity screen
            fragmentVisible = false;
            fragmentResume = false;
        }
    }


    @SuppressLint("CheckResult")
    private void fetchExcellenceData(final ArrayList<ChartConfigurationData> performanceConfiguration) {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mBinding.progressBarExcellence.setVisibility(View.VISIBLE);

            mAnalyticsModel.fetchPerformanceData("")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<PerformanceChartData>>() {
                        @Override
                        public void accept(ArrayList<PerformanceChartData> performanceChartData) throws Exception {

                            mBinding.progressBarExcellence.setVisibility(View.GONE);

                            if (performanceConfiguration != null
                                    && !performanceConfiguration.isEmpty()
                                    && !performanceChartData.isEmpty()) {

                                mBinding.chartPerformance.setVisibility(View.VISIBLE);
                                mBinding.pieChartPerformance.setVisibility(View.VISIBLE);
                                mBinding.textViewNoExcellenceData.setVisibility(View.GONE);

                                Collections.sort(performanceChartData);
                                drawPerformanceBarChart(performanceConfiguration, performanceChartData);

                            } else {
                                mBinding.textViewNoExcellenceData.setVisibility(View.VISIBLE);
                                mBinding.chartPerformance.setVisibility(View.GONE);
                                mBinding.pieChartPerformance.setVisibility(View.GONE);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.textViewNoExcellenceData.setVisibility(View.VISIBLE);
                            mBinding.progressBarExcellence.setVisibility(View.GONE);
                            mBinding.chartPerformance.setVisibility(View.GONE);
                            mBinding.pieChartPerformance.setVisibility(View.GONE);

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
                if (value >= 0 && !xAxisLabel.isEmpty()) {
                    if (value <= xAxisLabel.size() - 1) {
                        return xAxisLabel.get((int) value);
                    } else
                        return ConstantUtil.BLANK;
                } else
                    return ConstantUtil.BLANK;

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
        mBinding.chartPerformance.setDrawBarShadow(true);
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
                int performance = Math.round(ccd.getPerformance());
                drawProgress(performance);
                fetchPerformanceData(ccd.getId());
                setSubjectIcon(ccd.getSubjectIcon());
                mBinding.llExcellence.setVisibility(View.VISIBLE);
                mBinding.textViewPerformance.setText(ccd.getName());
                //  startActivity(PerformanceDetailActivity.getStartIntent(mContext, ccd.getId(), ccd.getName(), performance));

            }

            @Override
            public void onNothingSelected() {
                // mBinding.llExcellence.setVisibility(View.GONE);
            }

        });

    }

    public int pickColorAccording(int performance) {


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
            drawProgress(Math.round(data.getPerformance()));
            fetchPerformanceData(data.getId());
            setSubjectIcon(data.getSubjectIcon());
            mBinding.llExcellence.setVisibility(View.VISIBLE);
            mBinding.textViewPerformance.setText(data.getName());
        }
    }


    private void setSubjectIcon(String subjectIcon) {
        if (!TextUtils.isEmpty(subjectIcon)) {
            Picasso.with(getContext()).load(subjectIcon).placeholder(R.drawable.icon_book).fit().centerCrop().into(mBinding.imageViewSubjectIcon);
        } else {
            Picasso.with(getContext()).load(R.drawable.icon_book).fit().centerCrop().into(mBinding.imageViewSubjectIcon);

        }
    }

    /*draw and set values for time spent pie chart*/
    private void drawProgress(int performance) {
        List<Integer> colorList = new ArrayList<>();
        float total = 100;
        float remaining = total - performance;
        ArrayList<PieEntry> fillValues = new ArrayList<>();
        fillValues.add(new PieEntry(performance));
        fillValues.add(new PieEntry(remaining));
        PieDataSet dataSet = new PieDataSet(fillValues, "");
        colorList.add(pickColorAccording(performance));
        if (getActivity() != null && getActivity().getResources() != null) {
            colorList.add(getActivity().getResources().getColor(R.color.colorGrey400));
        } else {
            colorList.add(Color.GRAY);
        }
        dataSet.setColors(colorList);
        dataSet.setValueTextSize(0f);
        PieData data = new PieData(dataSet);
        mBinding.pieChartPerformance.setData(data);
        mBinding.pieChartPerformance.setHoleRadius(85f);
        mBinding.pieChartPerformance.setDrawHoleEnabled(true);
        mBinding.pieChartPerformance.setUsePercentValues(true);
        mBinding.pieChartPerformance.getDescription().setEnabled(false);
        mBinding.pieChartPerformance.setDrawCenterText(true);
        String centerTextValue = performance + "%";
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

            mBinding.progressBarExcellence.setVisibility(View.VISIBLE);

            mAnalyticsModel.fetchPerformanceData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<PerformanceChartData>>() {
                        @Override
                        public void accept(ArrayList<PerformanceChartData> performanceChartData) throws Exception {
                            mBinding.progressBarExcellence.setVisibility(View.GONE);
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
                            mBinding.progressBarExcellence.setVisibility(View.GONE);
                            mBinding.textViewNoPerformanceData.setVisibility(View.VISIBLE);

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
        mContext = context;
    }


//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mContext = null;
//
//    }
}
