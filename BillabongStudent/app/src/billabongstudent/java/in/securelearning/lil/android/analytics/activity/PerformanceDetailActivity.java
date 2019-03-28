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

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsPerformanceDetailBinding;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsPerformanceTopicItemBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.dataobjects.PerformanceChartData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PerformanceDetailActivity extends AppCompatActivity {
    LayoutAnalyticsPerformanceDetailBinding mBinding;
    @Inject
    AnalyticsModel mAnalyticsModel;
    private static final String SUBJECT_ID = "subjectId";
    private static final String SUBJECT_NAME = "subjectName";

    public static Intent getStartIntent(Context context, String id, String name) {
        Intent intent = new Intent(context, PerformanceDetailActivity.class);
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_analytics_performance_detail);
        mAnalyticsModel.setImmersiveStatusBar(getWindow());
        handleIntent();

    }


    private void setUpToolbar(String subjectName) {
        String toolbarText = "Performance of " + subjectName;
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
            fetchPerformanceData(subjectId);

        }
    }

    @SuppressLint("CheckResult")
    private void fetchPerformanceData(String subjectId) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mAnalyticsModel.fetchPerformanceData(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<PerformanceChartData>>() {
                        @Override
                        public void accept(ArrayList<PerformanceChartData> performanceChartData) throws Exception {
                            mBinding.progressBarPerformance.setVisibility(View.GONE);
                            if (!performanceChartData.isEmpty()) {
                                mBinding.layoutRecyclerView.setVisibility(View.VISIBLE);
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
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(new RecyclerViewAdapter(getBaseContext(), performanceChartDataList));
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<PerformanceChartData> mList;

        private RecyclerViewAdapter(Context context, ArrayList<PerformanceChartData> performanceChartDataList) {
            mContext = context;
            this.mList = performanceChartDataList;
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutAnalyticsPerformanceTopicItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_analytics_performance_topic_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, int position) {
            PerformanceChartData performanceChartData = mList.get(position);

            holder.mBinding.textViewTopicName.setText(performanceChartData.getName());

            String performance = String.valueOf(new DecimalFormat("##.##").format(performanceChartData.getPerformance()) + "%");
            holder.mBinding.textViewTopicPerformance.setText(performance);

            if (performanceChartData.getPerformance() > 0 && performanceChartData.getPerformance() <= 40) {
                holder.mBinding.progressBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorRed)));
            } else if (performanceChartData.getPerformance() > 40 && performanceChartData.getPerformance() <= 70) {
                holder.mBinding.progressBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorAnnouncement)));

            } else if (performanceChartData.getPerformance() > 70) {
                holder.mBinding.progressBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorGreenDark)));

            }

            holder.mBinding.progressBar.setMax(100);
            holder.mBinding.progressBar.setProgress((int) performanceChartData.getPerformance());
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
                        fetchPerformanceData(subjectId);

                    }
                })
                .show();

    }
}
