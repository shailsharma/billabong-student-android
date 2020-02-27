package in.securelearning.lil.android.analytics.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import in.securelearning.lil.android.analytics.dataobjects.EffortChartData;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.analytics.helper.EffortBarChartPercentFormatter;
import in.securelearning.lil.android.analytics.helper.PiePercentFormatter;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.analytics.views.adapter.StudentEffortAdapter;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutStudentAnalyticsEffortBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Float.NaN;

public class StudentEffortFragment extends Fragment {

    @Inject
    AnalyticsModel mAnalyticsModel;

    private LayoutStudentAnalyticsEffortBinding mBinding;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;
    private Context mContext;
    private float mTotalTime = 0;
    private ArrayList<ChartConfigurationData> mChartConfigurationData = null;

    public static Fragment newInstance(ArrayList<ChartConfigurationData> performanceConfiguration) {
        StudentEffortFragment fragment = new StudentEffortFragment();
        Bundle args = new Bundle();
        args.putSerializable(ConstantUtil.EFFORT, (Serializable) performanceConfiguration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable(ConstantUtil.EFFORT) != null) {
            this.mChartConfigurationData = (ArrayList<ChartConfigurationData>) getArguments().getSerializable(ConstantUtil.EFFORT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_student_analytics_effort, container, false);

        if (!fragmentResume && fragmentVisible) {   //only when first time activity is created
            fetchEffortData();
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
            fetchEffortData();
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
    private void fetchEffortData() {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mBinding.progressBarEffort.setVisibility(View.VISIBLE);

            mAnalyticsModel.fetchEffortData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EffortChartDataParent>() {
                        @Override
                        public void accept(EffortChartDataParent effortChartDataParent) throws Exception {

                            mBinding.progressBarEffort.setVisibility(View.GONE);

                            if (!effortChartDataParent.getEffortChartDataList().isEmpty()) {
                                mBinding.frameTime1.setVisibility(View.VISIBLE);
                                mBinding.frameLayout2.setVisibility(View.VISIBLE);
                                mBinding.chartEffort.setVisibility(View.VISIBLE);
                                mBinding.layoutDailyTimeSpent.setVisibility(View.VISIBLE);
                                mBinding.textViewNoEffortData.setVisibility(View.GONE);
                                mBinding.layoutRecyclerView.setVisibility(View.VISIBLE);

                                drawPieChart(effortChartDataParent.getEffortChartDataList(), effortChartDataParent.getDaysCount());

                            } else {
                                mBinding.frameLayout2.setVisibility(View.GONE);
                                mBinding.frameTime1.setVisibility(View.GONE);
                                mBinding.layoutRecyclerView.setVisibility(View.GONE);
                                mBinding.chartEffort.setVisibility(View.GONE);
                                mBinding.layoutDailyTimeSpent.setVisibility(View.GONE);
                                mBinding.textViewNoEffortData.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();

                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            mBinding.frameLayout2.setVisibility(View.GONE);
                            mBinding.frameTime1.setVisibility(View.GONE);
                            mBinding.layoutRecyclerView.setVisibility(View.GONE);
                            mBinding.chartEffort.setVisibility(View.GONE);
                            mBinding.layoutDailyTimeSpent.setVisibility(View.GONE);
                            mBinding.textViewNoEffortData.setVisibility(View.VISIBLE);
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

    private void drawPieChart(ArrayList<EffortChartData> effortChartData, int daysCount) {
        float totalTimeSpent = 0;
        float totalReadTime = 0;
        float totalVideoTime = 0;
        float totalPracticeTime = 0;
        Collections.sort(effortChartData);
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
        mTotalTime = totalTimeSpent;

        /*Total time spent*/
        String formattedTotalTimeSpent = mAnalyticsModel.showHoursMinutesFromSeconds((long) totalTimeSpent);
        mBinding.textViewTotalTimeSpent.setText(formattedTotalTimeSpent);
        final float finalTotalTimeSpent = totalTimeSpent;
        final float finalTotalReadTime = totalReadTime;
        final float finalTotalVideoTime = totalVideoTime;
        final float finalTotalPracticeTime = totalPracticeTime;

        mBinding.layoutTotalTimeSpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedTotalTimeSpent(mContext, finalTotalTimeSpent, finalTotalReadTime, finalTotalVideoTime, finalTotalPracticeTime, ConstantUtil.BLANK);
            }
        });

        /*Daily time spent*/
        final float dailyTimeSpent = totalTimeSpent / daysCount;
        final float dailyReadTimeSpent = totalReadTime / daysCount;
        final float dailyVideoTimeSpent = totalVideoTime / daysCount;
        final float dailyPracticeTimeSpent = totalPracticeTime / daysCount;
        String formattedDailyTimeSpent = mAnalyticsModel.showHoursMinutesFromSeconds((long) dailyTimeSpent);
        mBinding.textViewDailyTimeSpent.setText(formattedDailyTimeSpent);


        mBinding.layoutDailyTimeSpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedDailyTimeSpent(mContext, dailyTimeSpent, dailyReadTimeSpent, dailyVideoTimeSpent, dailyPracticeTimeSpent);
            }
        });

        /*Pie chart*/
        PieDataSet dataSet = new PieDataSet(entryArrayList, "");

        dataSet.setColors(pickColorAccording(), mContext);
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
        //by default showing highest
        showHighestValue(effortChartData);
        mBinding.chartEffort.highlightValue(0, 0, false);
        mBinding.chartEffort.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                EffortChartData chartData = (EffortChartData) e.getData();
                fetchSubjectWiseEffortData(chartData.getId());
                setSubjectIcon(chartData.getSubject().get(0).getSubjectIcon());
                mBinding.textViewPerformance.setText(chartData.getSubject().get(0).getName());
                int performance = Math.round((chartData.getTotalTimeSpent() / mTotalTime) * 100);
                drawProgress(performance, (int) h.getX());

            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    private void showHighestValue(ArrayList<EffortChartData> list) {
        if (list != null && !list.isEmpty() && list.get(0) != null) {
            EffortChartData data = list.get(0);
            fetchSubjectWiseEffortData(data.getId());
            mBinding.llPerformance.setVisibility(View.VISIBLE);
            setSubjectIcon(data.getSubject().get(0).getSubjectIcon());
            mBinding.textViewPerformance.setText(data.getSubject().get(0).getName());
            int performance = Math.round((data.getTotalTimeSpent() / mTotalTime) * 100);
            drawProgress(performance, 0);


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
    private void drawProgress(int performance, int dataSetIndex) {
        List<Integer> colorList = new ArrayList<>();
        float total = 100;
        float remaining = total - performance;
        ArrayList<PieEntry> fillValues = new ArrayList<>();
        fillValues.add(new PieEntry(performance));
        fillValues.add(new PieEntry(remaining));
        PieDataSet dataSet = new PieDataSet(fillValues, "");
        int[] color = pickColorAccording();

        if (getActivity() != null && getActivity().getResources() != null) {
            colorList.add(getActivity().getResources().getColor(color[dataSetIndex]));
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

    private int[] pickColorAccording() {
        return new int[]{
                R.color.dot_dark_screen1,
                R.color.dot_dark_screen2,
                R.color.dot_dark_screen3,
                R.color.dot_dark_screen4,
                R.color.dot_dark_screen5,
                R.color.dot_dark_screen6,
                R.color.dot_dark_screen7,
                R.color.dot_dark_screen8,
                R.color.dot_dark_screen9,
                R.color.dot_dark_screen10,
                R.color.dot_dark_screen11};
    }

    @SuppressLint("CheckResult")
    private void fetchSubjectWiseEffortData(final String subjectId) {

        mBinding.progressBarEffort.setVisibility(View.VISIBLE);

        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mAnalyticsModel.fetchSubjectWiseEffortData(subjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EffortChartDataParent>() {
                        @Override
                        public void accept(EffortChartDataParent effortChartDataParent) throws Exception {

                            mBinding.progressBarEffort.setVisibility(View.GONE);

                            fetchWeeklyEffortData(subjectId);

                            if (!effortChartDataParent.getEffortChartDataList().isEmpty()) {
                                mBinding.linearLayoutTopicTotalTime.setVisibility(View.VISIBLE);
                                mBinding.linearLayoutDailyTimeSpent.setVisibility(View.VISIBLE);
                                ArrayList<EffortChartData> effortChartDataList = new ArrayList<>();
                                effortChartDataList = effortChartDataParent.getEffortChartDataList();
                                setTimeSpentView(effortChartDataList, effortChartDataParent.getDaysCount());
                                setTimeSpentTopicList(effortChartDataParent.getEffortChartDataList());

                            } else {
                                mBinding.linearLayoutTopicTotalTime.setVisibility(View.GONE);
                                mBinding.linearLayoutDailyTimeSpent.setVisibility(View.GONE);
                                mBinding.layoutRecyclerView.setVisibility(View.GONE);

                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            throwable.printStackTrace();

                            fetchWeeklyEffortData(subjectId);
                            mBinding.layoutTotalTimeSpent.setVisibility(View.GONE);
                            mBinding.layoutDailyTimeSpent.setVisibility(View.GONE);
                            mBinding.layoutRecyclerView.setVisibility(View.GONE);
                            mBinding.progressBarEffort.setVisibility(View.GONE);

                        }
                    });

        } else {
            showInternetSnackBar();
        }

    }

    @SuppressLint("CheckResult")
    private void fetchWeeklyEffortData(String subjectId) {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mBinding.progressBarEffort.setVisibility(View.VISIBLE);

            mAnalyticsModel.fetchWeeklyEffortData(subjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EffortChartDataParent>() {
                        @Override
                        public void accept(EffortChartDataParent effortChartDataParent) throws Exception {

                            mBinding.progressBarEffort.setVisibility(View.GONE);

                            if (effortChartDataParent != null
                                    && effortChartDataParent.getEffortChartDataList() != null
                                    && !effortChartDataParent.getEffortChartDataList().isEmpty()) {

                                mBinding.llBarChart.setVisibility(View.VISIBLE);
                                mBinding.textViewTopicNoEffortData.setVisibility(View.GONE);
                                //drawEffortLineChart(effortChartDataWeeklies);
                                drawEffortBarChart(mChartConfigurationData, effortChartDataParent.getEffortChartDataList());
                            } else {
                                mBinding.llBarChart.setVisibility(View.GONE);
                                mBinding.textViewTopicNoEffortData.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            mBinding.textViewTopicNoEffortData.setVisibility(View.VISIBLE);
                        }
                    });

        } else {
            showInternetSnackBar();
        }

    }

    /*Draw bar chart for performance*/
    private void drawEffortBarChart(ArrayList<ChartConfigurationData> performanceConfiguration, ArrayList<EffortChartData> weeklyChartData) {
        // Data is coming from 1 to n week , we have to get last 4 records
        Collections.reverse(weeklyChartData);
        ArrayList<EffortChartData> tempDataList = new ArrayList<>();

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<BarEntry> values = new ArrayList<>();
        final List<Integer> colors = new ArrayList<>();
        List<LegendEntry> legendEntries = new ArrayList<>();

        /*Add legend entries according to configuration*/
        for (int i = 0; i < performanceConfiguration.size(); i++) {
            ChartConfigurationData configurationData = performanceConfiguration.get(i);
            legendEntries.add(new LegendEntry(configurationData.getLabel(), Legend.LegendForm.SQUARE, NaN, NaN, null, Color.parseColor(configurationData.getColorCode())));

        }
        for (int i = 0; i < weeklyChartData.size(); i++) {
            if (i <= 3) {

                tempDataList.add(weeklyChartData.get(i));
            }
        }

        // Last week data to be right most
        Collections.reverse(tempDataList);


        /*Adding chart data for Y and X axis*/

        for (int i = 0; i < tempDataList.size(); i++) {

            EffortChartData data = tempDataList.get(i);
            float totalMin = Math.round(data.getTotalTimeSpent() / 60);
            values.add(new BarEntry(i, totalMin, data));

            xAxisLabel.add(CommonUtils.getInstance().getChartWeekLabel(data.getWeekNo()));

            for (int j = 0; j < performanceConfiguration.size(); j++) {
                ChartConfigurationData chartConfigurationData = performanceConfiguration.get(j);
                if (totalMin >= chartConfigurationData.getFrom() && totalMin <= chartConfigurationData.getTo()) {
                    colors.add(Color.parseColor(chartConfigurationData.getColorCode()));
                }
            }

        }

        YAxis rightAxis = mBinding.topicChartEffort.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawAxisLine(false);

        YAxis leftAxis = mBinding.topicChartEffort.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        //Need to dynamic Y axis
        //leftAxis.setAxisMaximum(100);
        leftAxis.setGridColor(ContextCompat.getColor(mContext, R.color.colorTransparent));

        Legend legend = mBinding.topicChartEffort.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setCustom(legendEntries);
        legend.setFormSize(8f);
        legend.setFormToTextSpace(8f);
        legend.setXEntrySpace(12f);
        legend.setEnabled(true);

        XAxis xAxis = mBinding.topicChartEffort.getXAxis();
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
        barDataSet.setDrawValues(false);
        if (!colors.isEmpty()) {
            barDataSet.setColors(colors);
        }
        barDataSet.setDrawIcons(false);
        barDataSet.setHighLightAlpha(0);
        barDataSet.setDrawValues(true);
        dataSets.add(barDataSet);
        BarData barData = new BarData(dataSets);
        barData.setValueTextColor(Color.BLACK);
        barData.setValueFormatter(new EffortBarChartPercentFormatter());
        barData.setValueTextSize(mAnalyticsModel.barTextSize());
        barData.setBarWidth(mAnalyticsModel.effortBarWidth());
        mBinding.topicChartEffort.setExtraBottomOffset(10);
        mBinding.topicChartEffort.getDescription().setEnabled(false);
        mBinding.topicChartEffort.setPinchZoom(false);
        mBinding.topicChartEffort.setDrawGridBackground(false);
        mBinding.topicChartEffort.getXAxis().setDrawGridLines(false);
        mBinding.topicChartEffort.setDrawBarShadow(false);
        mBinding.topicChartEffort.setDrawValueAboveBar(true);
        mBinding.topicChartEffort.setHighlightFullBarEnabled(false);
        mBinding.topicChartEffort.getLegend().setWordWrapEnabled(true);
        mBinding.topicChartEffort.setScaleXEnabled(false);
        mBinding.topicChartEffort.setScaleYEnabled(false);
        mBinding.topicChartEffort.setFitBars(true);
        mBinding.topicChartEffort.animateY(1400);
        mBinding.topicChartEffort.clear();
        mBinding.topicChartEffort.setData(barData);
        mBinding.topicChartEffort.invalidate();

        //Need to show highest value


    }


    private void setTimeSpentTopicList(ArrayList<EffortChartData> effortChartDataList) {

        mBinding.recyclerViewTopic.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewTopic.setAdapter(new StudentEffortAdapter(mContext, effortChartDataList));
    }

    private void setTimeSpentView(ArrayList<EffortChartData> effortChartDataList, int daysCount) {
        float totalTimeSpent = 0;
        float totalReadTime = 0;
        float totalVideoTime = 0;
        float totalPracticeTime = 0;
        for (int i = 0; i < effortChartDataList.size(); i++) {
            totalTimeSpent += effortChartDataList.get(i).getTotalTimeSpent();
            totalReadTime += effortChartDataList.get(i).getTotalReadTimeSpent();
            totalVideoTime += effortChartDataList.get(i).getTotalVideoTimeSpent();
            totalPracticeTime += effortChartDataList.get(i).getTotalPracticeTimeSpent();
        }
        /*Total time spent*/
        String formattedTotalTimeSpent = mAnalyticsModel.showHoursMinutesFromSeconds((long) totalTimeSpent);
        mBinding.textViewTopicTotalTimeSpent.setText(formattedTotalTimeSpent);
        final float finalTotalTimeSpent = totalTimeSpent;
        final float finalTotalReadTime = totalReadTime;
        final float finalTotalVideoTime = totalVideoTime;
        final float finalTotalPracticeTime = totalPracticeTime;


        mBinding.linearLayoutTopicTotalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mBinding.textViewPerformance.getText())) {
                    String subjectName = mBinding.textViewPerformance.getText().toString();
                    mAnalyticsModel.showDetailedTotalTimeSpent(mContext, finalTotalTimeSpent,
                            finalTotalReadTime, finalTotalVideoTime, finalTotalPracticeTime, subjectName);
                } else {
                    mAnalyticsModel.showDetailedTotalTimeSpent(mContext, finalTotalTimeSpent,
                            finalTotalReadTime, finalTotalVideoTime, finalTotalPracticeTime, ConstantUtil.BLANK);
                }

            }
        });

        /*Daily time spent*/
        final float dailyTimeSpent = totalTimeSpent / daysCount;
        final float dailyReadTimeSpent = totalReadTime / daysCount;
        final float dailyVideoTimeSpent = totalVideoTime / daysCount;
        final float dailyPracticeTimeSpent = totalPracticeTime / daysCount;
        String formattedDailyTimeSpent = mAnalyticsModel.showHoursMinutesFromSeconds((long) dailyTimeSpent);
        mBinding.textViewTopicDailyTimeSpent.setText(formattedDailyTimeSpent);


        mBinding.linearLayoutDailyTimeSpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedDailyTimeSpent(mContext, dailyTimeSpent, dailyReadTimeSpent,
                        dailyVideoTimeSpent, dailyPracticeTimeSpent);
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

}

