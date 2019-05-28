package in.securelearning.lil.android.analytics.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.adapter.StudentEffortAdapter;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartData;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataWeekly;
import in.securelearning.lil.android.analytics.helper.MyXAxisValueFormatter;
import in.securelearning.lil.android.analytics.helper.PiePercentFormatter;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutStudentAnalyticsEffotBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.StudentCommonUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StudentEffortFragment extends Fragment {

    @Inject
    AnalyticsModel mAnalyticsModel;
    private LayoutStudentAnalyticsEffotBinding mBinding;
    private Context mContext;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;


    public static Fragment newInstance() {
        StudentEffortFragment fragment = new StudentEffortFragment();
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            fetchEffortData();
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_student_analytics_effot, container, false);
        if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            fetchEffortData();
        }


        return mBinding.getRoot();
    }

    @SuppressLint("CheckResult")
    private void fetchEffortData() {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mAnalyticsModel.fetchEffortData().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EffortChartDataParent>() {
                        @Override
                        public void accept(EffortChartDataParent effortChartDataParent) throws Exception {
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            mBinding.progressBarTopicEffort.setVisibility(View.GONE);
                            if (!effortChartDataParent.getEffortChartDataList().isEmpty()) {
                                mBinding.layoutTotalTimeSpent.setVisibility(View.VISIBLE);
                                mBinding.chartEffort.setVisibility(View.VISIBLE);
                                mBinding.layoutDailyTimeSpent.setVisibility(View.VISIBLE);
                                mBinding.textViewNoEffortData.setVisibility(View.GONE);

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
                            mBinding.progressBarTopicEffort.setVisibility(View.GONE);
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
                mAnalyticsModel.showDetailedTotalTimeSpent(mContext, finalTotalTimeSpent, finalTotalReadTime, finalTotalVideoTime, finalTotalPracticeTime);
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
                mAnalyticsModel.showDetailedDailyTimeSpent(mContext, dailyTimeSpent, dailyReadTimeSpent, dailyVideoTimeSpent, dailyPracticeTimeSpent);
            }
        });

        /*Pie chart*/
        PieDataSet dataSet = new PieDataSet(entryArrayList, "");
        //int colorArray[] = new int[]{R.color.color_graph_green, R.color.color_graph_orange, R.color.colorPendingHomework};
        int[] colors = new int[]{R.color.dot_dark_screen1,
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
        dataSet.setColors(colors, mContext);
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
                mBinding.textViewPerformance.setText(chartData.getSubject().get(0).getName());
                float totalCount = chartData.getTotalTimeSpent() / 60;
                if (totalCount > 60) {

                    mBinding.textViewTimeCount.setText(String.format("%s\nMin", Math.round(totalCount)));
                } else {
                    mBinding.textViewTimeCount.setText(String.format("%s\nMin", StudentCommonUtils.getInstance().convertSecondToMinute((long) chartData.getTotalTimeSpent())));
                }

            }

            @Override
            public void onNothingSelected() {
                /// mBinding.llPerformance.setVisibility(View.GONE);
            }
        });
    }

    private void showHighestValue(ArrayList<EffortChartData> list) {
        if (list != null && !list.isEmpty() && list.get(0) != null) {
            EffortChartData data = list.get(0);
            fetchSubjectWiseEffortData(data.getId());
            mBinding.llPerformance.setVisibility(View.VISIBLE);
            mBinding.textViewPerformance.setText(data.getSubject().get(0).getName());
            float totalCount = data.getTotalTimeSpent() / 60;
            if (totalCount > 60) {

                mBinding.textViewTimeCount.setText(String.format("%s\nMin", Math.round(totalCount)));
            } else {
                mBinding.textViewTimeCount.setText(String.format("%s\nMin", StudentCommonUtils.getInstance().convertSecondToMinute((long) data.getTotalTimeSpent())));
            }


        }
    }

    @SuppressLint("CheckResult")
    private void fetchSubjectWiseEffortData(final String subjectId) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mAnalyticsModel.fetchSubjectWiseEffortData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EffortChartDataParent>() {
                        @Override
                        public void accept(EffortChartDataParent effortChartDataParent) throws Exception {
                            mBinding.progressBarTopicEffort.setVisibility(View.GONE);
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
                            mBinding.progressBarTopicEffort.setVisibility(View.GONE);
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
            mAnalyticsModel.fetchWeeklyEffortData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<EffortChartDataWeekly>>() {
                        @Override
                        public void accept(ArrayList<EffortChartDataWeekly> effortChartDataWeeklies) throws Exception {
                            mBinding.progressBarTopicEffort.setVisibility(View.GONE);
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            if (!effortChartDataWeeklies.isEmpty()) {
                                Collections.sort(effortChartDataWeeklies);
                                mBinding.topicChartEffort.setVisibility(View.VISIBLE);
                                mBinding.textViewTopicNoEffortData.setVisibility(View.GONE);
                                drawEffortLineChart(effortChartDataWeeklies);
                            } else {
                                mBinding.topicChartEffort.setVisibility(View.GONE);
                                mBinding.textViewTopicNoEffortData.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            mBinding.progressBarTopicEffort.setVisibility(View.GONE);
                            mBinding.textViewTopicNoEffortData.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            showInternetSnackBar();
        }
    }

    private void drawEffortLineChart(ArrayList<EffortChartDataWeekly> effortChartDataWeeklies) {
        ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i = 0; i < effortChartDataWeeklies.size(); i++) {
            entries.add(new Entry(i, mAnalyticsModel.longConvertSecondToMinute((long) effortChartDataWeeklies.get(i).getTime())));
            xAxisLabel.add(mAnalyticsModel.getFormattedDateForWeeklyEffortChart(effortChartDataWeeklies.get(i).getDate()));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(6f);
        lineDataSet.setDrawFilled(false);
        lineDataSet.setCircleColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        lineDataSet.setCircleColorHole(ContextCompat.getColor(mContext, R.color.colorPrimary));


        LineData lineData = new LineData(lineDataSet);
        mBinding.topicChartEffort.setData(lineData);

        mBinding.topicChartEffort.setDrawGridBackground(false);
        mBinding.topicChartEffort.getDescription().setEnabled(false);
        mBinding.topicChartEffort.setTouchEnabled(true);
        mBinding.topicChartEffort.setDragEnabled(false);
        mBinding.topicChartEffort.setScaleEnabled(false);
        mBinding.topicChartEffort.setPinchZoom(false);

        XAxis xAxis = mBinding.topicChartEffort.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xAxisLabel));


        YAxis leftAxis = mBinding.topicChartEffort.getAxisLeft();
        leftAxis.setInverted(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGridColor(ContextCompat.getColor(mContext, R.color.colorTransparent));

        YAxis rightAxis = mBinding.topicChartEffort.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawAxisLine(false);

        Legend l = mBinding.topicChartEffort.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setEnabled(false);

        mBinding.topicChartEffort.invalidate();
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
        String formattedTotalTimeSpent = mAnalyticsModel.convertSecondToHourMinuteSecond((long) totalTimeSpent);
        mBinding.textViewTopicTotalTimeSpent.setText(formattedTotalTimeSpent);
        final float finalTotalTimeSpent = totalTimeSpent;
        final float finalTotalReadTime = totalReadTime;
        final float finalTotalVideoTime = totalVideoTime;
        final float finalTotalPracticeTime = totalPracticeTime;

        mBinding.textViewTopicDailyTimeSpentLabel.setPaintFlags(mBinding.textViewDailyTimeSpentLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mBinding.textViewTopicTotalTimeSpentLabel.setPaintFlags(mBinding.textViewTotalTimeSpentLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mBinding.linearLayoutTopicTotalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedTotalTimeSpent(mContext, finalTotalTimeSpent, finalTotalReadTime, finalTotalVideoTime, finalTotalPracticeTime);
            }
        });

        /*Daily time spent*/
        final float dailyTimeSpent = totalTimeSpent / daysCount;
        final float dailyReadTimeSpent = totalReadTime / daysCount;
        final float dailyVideoTimeSpent = totalVideoTime / daysCount;
        final float dailyPracticeTimeSpent = totalPracticeTime / daysCount;
        String formattedDailyTimeSpent = mAnalyticsModel.convertSecondToMinute((long) dailyTimeSpent);
        mBinding.textViewTopicDailyTimeSpent.setText(formattedDailyTimeSpent);


        mBinding.linearLayoutDailyTimeSpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedDailyTimeSpent(mContext, dailyTimeSpent, dailyReadTimeSpent, dailyVideoTimeSpent, dailyPracticeTimeSpent);
            }
        });
    }


    private void showTopicInternetSnackBar(final String subjectId) {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(subjectId)) {
                            fetchSubjectWiseEffortData(subjectId);
                        } else {
                            fetchWeeklyEffortData(subjectId);
                        }

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

