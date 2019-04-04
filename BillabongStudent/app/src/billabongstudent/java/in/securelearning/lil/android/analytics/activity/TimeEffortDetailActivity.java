package in.securelearning.lil.android.analytics.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.helper.MyXAxisValueFormatter;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsEffortTopicItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsTimeSpentDetailBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartData;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataWeekly;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TimeEffortDetailActivity extends AppCompatActivity {

    @Inject
    AnalyticsModel mAnalyticsModel;
    private static final String SUBJECT_ID = "subjectId";
    private static final String SUBJECT_NAME = "subjectName";

    LayoutAnalyticsTimeSpentDetailBinding mBinding;
    private float mTotalTimeSpent = 0;

    public static Intent getStartIntent(Context context, String id, String name) {
        Intent intent = new Intent(context, TimeEffortDetailActivity.class);
        intent.putExtra(SUBJECT_ID, id);
        intent.putExtra(SUBJECT_NAME, name);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_analytics_time_spent_detail);
        mAnalyticsModel.setImmersiveStatusBar(getWindow());
        handleIntent();
    }

    private void setUpToolbar(String subjectName) {
        String toolbarText = "Time Spent of " + subjectName;
        mBinding.layoutToolbar.textViewToolbarTitle.setText(toolbarText);
        mBinding.layoutToolbar.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void handleIntent() {
        if (getIntent() != null) {
            String subjectId = getIntent().getStringExtra(SUBJECT_ID);
            String subjectName = getIntent().getStringExtra(SUBJECT_NAME);
            setUpToolbar(subjectName);
            fetchSubjectWiseEffortData(subjectId);

        }
    }

    @SuppressLint("CheckResult")
    private void fetchSubjectWiseEffortData(final String subjectId) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchSubjectWiseEffortData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EffortChartDataParent>() {
                        @Override
                        public void accept(EffortChartDataParent effortChartDataParent) throws Exception {
                            fetchWeeklyEffortData(subjectId);
                            if (!effortChartDataParent.getEffortChartDataList().isEmpty()) {
                                mBinding.layoutTotalTimeSpent.setVisibility(View.VISIBLE);
                                mBinding.layoutDailyTimeSpent.setVisibility(View.VISIBLE);
                                setTimeSpentView(effortChartDataParent.getEffortChartDataList(), effortChartDataParent.getDaysCount());
                                setTimeSpentTopicList(effortChartDataParent.getEffortChartDataList());

                            } else {
                                mBinding.layoutTotalTimeSpent.setVisibility(View.GONE);
                                mBinding.layoutDailyTimeSpent.setVisibility(View.GONE);
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


                        }
                    });
        } else {
            showInternetSnackBar(subjectId);
        }

    }

    @SuppressLint("CheckResult")
    private void fetchWeeklyEffortData(String subjectId) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchWeeklyEffortData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<EffortChartDataWeekly>>() {
                        @Override
                        public void accept(ArrayList<EffortChartDataWeekly> effortChartDataWeeklies) throws Exception {
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            if (!effortChartDataWeeklies.isEmpty()) {
                                mBinding.chartEffort.setVisibility(View.VISIBLE);
                                drawEffortLineChart(effortChartDataWeeklies);
                            } else {
                                mBinding.chartEffort.setVisibility(View.GONE);
                                mBinding.textViewNoEffortData.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBarEffort.setVisibility(View.GONE);
                            mBinding.textViewNoEffortData.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            showInternetSnackBar("");
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
        lineDataSet.setCircleColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        lineDataSet.setCircleColorHole(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));


        LineData lineData = new LineData(lineDataSet);
        mBinding.chartEffort.setData(lineData);

        mBinding.chartEffort.setDrawGridBackground(false);
        mBinding.chartEffort.getDescription().setEnabled(false);
        mBinding.chartEffort.setTouchEnabled(true);
        mBinding.chartEffort.setDragEnabled(false);
        mBinding.chartEffort.setScaleEnabled(false);
        mBinding.chartEffort.setPinchZoom(false);

        XAxis xAxis = mBinding.chartEffort.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xAxisLabel));


        YAxis leftAxis = mBinding.chartEffort.getAxisLeft();
        leftAxis.setInverted(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGridColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

        YAxis rightAxis = mBinding.chartEffort.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawAxisLine(false);

        Legend l = mBinding.chartEffort.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setEnabled(false);

        mBinding.chartEffort.invalidate();
    }

    private void setTimeSpentTopicList(ArrayList<EffortChartData> effortChartDataList) {
        mBinding.recyclerView.setNestedScrollingEnabled(false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(new RecyclerViewAdapter(getBaseContext(), effortChartDataList, mTotalTimeSpent));
    }

    private void setTimeSpentView(ArrayList<EffortChartData> effortChartDataList, int daysCount) {

        float totalReadTime = 0;
        float totalVideoTime = 0;
        float totalPracticeTime = 0;
        for (int i = 0; i < effortChartDataList.size(); i++) {
            mTotalTimeSpent += effortChartDataList.get(i).getTotalTimeSpent();
            totalReadTime += effortChartDataList.get(i).getTotalReadTimeSpent();
            totalVideoTime += effortChartDataList.get(i).getTotalVideoTimeSpent();
            totalPracticeTime += effortChartDataList.get(i).getTotalPracticeTimeSpent();
        }
        /*Total time spent*/
        String formattedTotalTimeSpent = mAnalyticsModel.convertSecondToHourMinuteSecond((long) mTotalTimeSpent);
        mBinding.textViewTotalTimeSpent.setText(formattedTotalTimeSpent);
        final float finalTotalTimeSpent = mTotalTimeSpent;
        final float finalTotalReadTime = totalReadTime;
        final float finalTotalVideoTime = totalVideoTime;
        final float finalTotalPracticeTime = totalPracticeTime;

        mBinding.textViewDailyTimeSpentLabel.setPaintFlags(mBinding.textViewDailyTimeSpentLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mBinding.textViewTotalTimeSpentLabel.setPaintFlags(mBinding.textViewTotalTimeSpentLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mBinding.layoutTotalTimeSpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedTotalTimeSpent(TimeEffortDetailActivity.this, finalTotalTimeSpent, finalTotalReadTime, finalTotalVideoTime, finalTotalPracticeTime);
            }
        });

        /*Daily time spent*/
        final float dailyTimeSpent = mTotalTimeSpent / daysCount;
        final float dailyReadTimeSpent = totalReadTime / daysCount;
        final float dailyVideoTimeSpent = totalVideoTime / daysCount;
        final float dailyPracticeTimeSpent = totalPracticeTime / daysCount;
        String formattedDailyTimeSpent = mAnalyticsModel.convertSecondToMinute((long) dailyTimeSpent);
        mBinding.textViewDailyTimeSpent.setText(formattedDailyTimeSpent);


        mBinding.layoutDailyTimeSpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnalyticsModel.showDetailedDailyTimeSpent(TimeEffortDetailActivity.this, dailyTimeSpent, dailyReadTimeSpent, dailyVideoTimeSpent, dailyPracticeTimeSpent);
            }
        });
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<EffortChartData> mList;
        private float mTotalTimeSpent;

        private RecyclerViewAdapter(Context context, ArrayList<EffortChartData> effortChartDataList, float totalTimeSpent) {
            mContext = context;
            this.mList = effortChartDataList;
            mTotalTimeSpent = totalTimeSpent;
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutAnalyticsEffortTopicItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_analytics_effort_topic_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, int position) {
            EffortChartData effortChartData = mList.get(position);
            holder.mBinding.textViewTopicName.setText(effortChartData.getTopic().get(0).getName());
            holder.mBinding.textViewTopicTime.setText(mAnalyticsModel.convertSecondToMinute((long) effortChartData.getTotalTimeSpent()));
            holder.mBinding.progressBar.setMax((int) mTotalTimeSpent);
            holder.mBinding.progressBar.setProgress((int) effortChartData.getTotalTimeSpent());
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutAnalyticsEffortTopicItemBinding mBinding;

            public ViewHolder(LayoutAnalyticsEffortTopicItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }


    private void showInternetSnackBar(final String subjectId) {

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

}
