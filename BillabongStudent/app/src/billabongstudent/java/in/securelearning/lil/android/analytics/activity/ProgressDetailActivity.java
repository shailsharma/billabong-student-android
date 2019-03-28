package in.securelearning.lil.android.analytics.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsPerformanceTopicItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsProgressDetailBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.dataobjects.CoverageChartData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ProgressDetailActivity extends AppCompatActivity {

    LayoutAnalyticsProgressDetailBinding mBinding;
    @Inject
    AnalyticsModel mAnalyticsModel;
    private static final String SUBJECT_ID = "subjectId";
    private static final String SUBJECT_NAME = "subjectName";
    private static final String PROGRESS = "progress";

    public static Intent getStartIntent(Context context, String id, String name, float progress) {
        Intent intent = new Intent(context, ProgressDetailActivity.class);
        intent.putExtra(SUBJECT_ID, id);
        intent.putExtra(SUBJECT_NAME, name);
        intent.putExtra(PROGRESS, progress);
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_analytics_progress_detail);
        mAnalyticsModel.setImmersiveStatusBar(getWindow());
        handleIntent();

    }


    private void setUpToolbar(String subjectName) {
        String toolbarText = "Progress of " + subjectName;
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
            float progress = getIntent().getFloatExtra(PROGRESS, 0f);
            setUpToolbar(subjectName);
            fetchCoverageData(subjectId);
            drawProgress(progress);
        }
    }

    @SuppressLint("CheckResult")
    private void fetchCoverageData(String subjectId) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchCoverageData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<CoverageChartData>>() {
                        @Override
                        public void accept(ArrayList<CoverageChartData> coverageChartData) throws Exception {
                            mBinding.progressBarCoverage.setVisibility(View.GONE);
                            if (!coverageChartData.isEmpty()) {
                                mBinding.layoutRecyclerView.setVisibility(View.VISIBLE);
                                setPerformanceTopicList(coverageChartData);
                            } else {
                                mBinding.textViewNoData.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBarCoverage.setVisibility(View.GONE);
                            mBinding.textViewNoData.setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            showInternetSnackBar(subjectId);
        }
    }

    /*draw and set values for time spent pie chart*/
    private void drawProgress(float progress) {
        float total = 100;
        float remaining = total - progress;
        ArrayList<PieEntry> fillValues = new ArrayList<>();
        fillValues.add(new PieEntry(progress));
        fillValues.add(new PieEntry(remaining));
        PieDataSet dataSet = new PieDataSet(fillValues, "");
        if (progress > 0 && progress <= 40) {
            int[] colors = new int[]{ContextCompat.getColor(Objects.requireNonNull(getBaseContext()), R.color.colorRed), ContextCompat.getColor(getBaseContext(), R.color.colorGrey)};
            dataSet.setColors(colors);
        } else if (progress > 40 && progress <= 70) {
            int[] colors = new int[]{ContextCompat.getColor(Objects.requireNonNull(getBaseContext()), R.color.colorAnnouncement), ContextCompat.getColor(getBaseContext(), R.color.colorGrey)};
            dataSet.setColors(colors);
        } else if (progress > 70) {
            int[] colors = new int[]{ContextCompat.getColor(Objects.requireNonNull(getBaseContext()), R.color.colorGreenDark), ContextCompat.getColor(getBaseContext(), R.color.colorGrey)};
            dataSet.setColors(colors);
        }
        dataSet.setValueTextSize(0f);
        PieData data = new PieData(dataSet);
        mBinding.pieChartProgress.setData(data);
        mBinding.pieChartProgress.setHoleRadius(85f);
        mBinding.pieChartProgress.setDrawHoleEnabled(true);
        mBinding.pieChartProgress.setUsePercentValues(true);
        mBinding.pieChartProgress.getDescription().setEnabled(false);
        mBinding.pieChartProgress.setDrawCenterText(true);
        String centerTextValue = String.valueOf(new DecimalFormat("##.##").format(progress)) + "%";
        if (centerTextValue.contains("NaN")) {
            mBinding.pieChartProgress.setCenterText("0%");
        } else {
            mBinding.pieChartProgress.setCenterText(centerTextValue);
        }
        mBinding.pieChartProgress.setCenterTextSize(18f);
        mBinding.pieChartProgress.getLegend().setEnabled(false);
        mBinding.pieChartProgress.invalidate();
        mBinding.pieChartProgress.setClickable(false);
        mBinding.pieChartProgress.setTouchEnabled(false);
    }

    private void setPerformanceTopicList(ArrayList<CoverageChartData> coverageChartData) {
        mBinding.recyclerView.setNestedScrollingEnabled(false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(new RecyclerViewAdapter(getBaseContext(), coverageChartData));
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<CoverageChartData> mList;

        private RecyclerViewAdapter(Context context, ArrayList<CoverageChartData> coverageChartData) {
            mContext = context;
            this.mList = coverageChartData;
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutAnalyticsPerformanceTopicItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_analytics_performance_topic_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, int position) {
            CoverageChartData coverageChartData = mList.get(position);

            holder.mBinding.textViewTopicName.setText(coverageChartData.getName());

            float coveragePercent = (coverageChartData.getCoverage() / coverageChartData.getTotal()) * 100;
            String performance = String.valueOf(new DecimalFormat("##.##").format(coveragePercent) + "%");
            holder.mBinding.textViewTopicPerformance.setText(performance);

            if (coveragePercent > 0 && coveragePercent <= 40) {
                holder.mBinding.progressBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorRed)));
            } else if (coveragePercent > 40 && coveragePercent <= 70) {
                holder.mBinding.progressBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorAnnouncement)));

            } else if (coveragePercent > 70) {
                holder.mBinding.progressBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorGreenDark)));

            }

            holder.mBinding.progressBar.setMax(100);
            holder.mBinding.progressBar.setProgress((int) coveragePercent);
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutAnalyticsPerformanceTopicItemBinding mBinding;

            public ViewHolder(LayoutAnalyticsPerformanceTopicItemBinding binding) {
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
                        fetchCoverageData(subjectId);

                    }
                })
                .show();

    }
}
