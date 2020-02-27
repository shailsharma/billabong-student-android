package in.securelearning.lil.android.analytics.model;

import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.ChartDataRequest;
import in.securelearning.lil.android.analytics.dataobjects.CoverageChartData;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.analytics.dataobjects.EffortChartDataRequest;
import in.securelearning.lil.android.analytics.dataobjects.EffortvsPerformanceData;
import in.securelearning.lil.android.analytics.dataobjects.PerformanceChartData;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsTimeSpentDetailPopupBinding;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.dataobjects.GlobalConfigurationRequest;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

public class AnalyticsModel {

    @Inject
    NetworkModel mNetworkModel;

    @Inject
    Context mContext;

    @Inject
    AppUserModel mAppUserModel;

    public AnalyticsModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    /*convert seconds to minutes*/
    public String convertSecondToMinute(long actualSeconds) {
        String strMinutes;

        long hoursMod = actualSeconds % 3600;
        long minutes = hoursMod / 60;
        long seconds = hoursMod % 60;

        /*This is to convert into a minute, if seconds value is >= 30 seconds else seconds
        value will become zero as discussed*/
        if (seconds >= 30) {
            minutes = minutes + 1;
        }

        strMinutes = String.valueOf(minutes);

        if (minutes >= 0 && minutes <= 9) {
            strMinutes = "0" + strMinutes;
        }


        return strMinutes;
    }

    /**
     * Convert seconds to natural time format which is hour:minutes
     * For example 120 seconds = 2 minutes
     *
     * @param actualSeconds
     * @return String which is in format - hour:minutes
     */
    public String showHoursMinutesFromSeconds(long actualSeconds) {
        String strMinutes, strHours;


        long hours = actualSeconds / 3600;
        long hoursMod = actualSeconds % 3600;
        long minutes = hoursMod / 60;

        strHours = String.valueOf(hours);
        strMinutes = String.valueOf(minutes);


        if (hours >= 0 && hours <= 9) {
            strHours = "0" + strHours;
        }

        if (minutes >= 0 && minutes <= 9) {
            strMinutes = "0" + strMinutes;
        }

        return strHours + ":" + strMinutes;
    }

    /*Method to fetch coverage data for particular subject and all subject*/
    public Observable<ArrayList<CoverageChartData>> fetchCoverageData(final String subjectId) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<CoverageChartData>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<CoverageChartData>> e) throws Exception {
                        Call<ArrayList<CoverageChartData>> call;
                        if (!TextUtils.isEmpty(subjectId)) {
                            call = mNetworkModel.fetchSubjectWiseCoverageData(new ChartDataRequest(subjectId));
                        } else {
                            call = mNetworkModel.fetchAllSubjectCoverageData(new ChartDataRequest());
                        }
                        Response<ArrayList<CoverageChartData>> response = call.execute();

                        if (response != null && response.isSuccessful()) {
                            Log.e("CoverageChartData", "Successful");
                            e.onNext(response.body());
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<CoverageChartData>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                Log.e("CoverageChartData", "Successful");
                                e.onNext(response2.body());
                            } else if (response2.code() == 401) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("CoverageChartData", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }

                        e.onComplete();
                    }
                });

    }

    /*Method to fetch performance for all subject*/
    public Observable<ArrayList<PerformanceChartData>> fetchPerformanceData(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<PerformanceChartData>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<PerformanceChartData>> e) throws Exception {
                Call<ArrayList<PerformanceChartData>> call;
                if (!TextUtils.isEmpty(subjectId)) {
                    call = mNetworkModel.fetchSubjectWisePerformanceData(new ChartDataRequest(subjectId));
                } else {
                    call = mNetworkModel.fetchAllSubjectPerformanceData(new ChartDataRequest());
                }
                Response<ArrayList<PerformanceChartData>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("PerformanceChartData", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<PerformanceChartData>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("PerformanceChartData", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("PerformanceChartData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });

    }

    /*To fetch effort (time spent) data for all subjects*/
    public Observable<EffortChartDataParent> fetchEffortData() {
        return Observable.create(new ObservableOnSubscribe<EffortChartDataParent>() {
            @Override
            public void subscribe(ObservableEmitter<EffortChartDataParent> e) throws Exception {
                EffortChartDataRequest effortChartDataRequest = new EffortChartDataRequest();
                effortChartDataRequest.setEmail(mAppUserModel.getApplicationUser().getEmail());
                Call<EffortChartDataParent> call = mNetworkModel.fetchEffortData(effortChartDataRequest);
                Response<EffortChartDataParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("EffortChartData", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<EffortChartDataParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("EffortChartData", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("EffortChartData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To fetch effort (time spent) data for individual subject*/
    public Observable<EffortChartDataParent> fetchSubjectWiseEffortData(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<EffortChartDataParent>() {
            @Override
            public void subscribe(ObservableEmitter<EffortChartDataParent> e) throws Exception {
                EffortChartDataRequest effortChartDataRequest = new EffortChartDataRequest();
                effortChartDataRequest.setEmail(mAppUserModel.getApplicationUser().getEmail());
                effortChartDataRequest.setSubjectId(subjectId);
                Call<EffortChartDataParent> call = mNetworkModel.fetchSubjectWiseEffortData(effortChartDataRequest);
                Response<EffortChartDataParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("EffortChartData", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<EffortChartDataParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("EffortChartData", "Successful");
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("EffortChartData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    /*To fetch effort (time spent) weekly data for individual subject*/
    public Observable<EffortChartDataParent> fetchWeeklyEffortData(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<EffortChartDataParent>() {
            @Override
            public void subscribe(ObservableEmitter<EffortChartDataParent> e) throws Exception {
                EffortChartDataRequest effortChartDataRequest = new EffortChartDataRequest();
                effortChartDataRequest.setEmail(mAppUserModel.getApplicationUser().getEmail());
                effortChartDataRequest.setSubjectId(subjectId);
                Call<EffortChartDataParent> call = mNetworkModel.fetchWeeklyEffortData(effortChartDataRequest);
                Response<EffortChartDataParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("EffortChartDataWeekly", "Successful");
                    e.onNext(response.body());
                } else if (response != null && response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response != null && response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<EffortChartDataParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("EffortChartDataWeekly", "Successful");
                        e.onNext(response2.body());
                    } else if (response2 != null && response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2 != null && response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("EffortChartData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    /*To fetch chart configuration for performance and coverage*/
    public Observable<GlobalConfigurationParent> fetchChartConfiguration() {
        return Observable.create(new ObservableOnSubscribe<GlobalConfigurationParent>() {
            @Override
            public void subscribe(ObservableEmitter<GlobalConfigurationParent> e) throws Exception {
                GlobalConfigurationRequest chartConfigurationRequest = new GlobalConfigurationRequest();
                chartConfigurationRequest.setPerformance(true);
                chartConfigurationRequest.setCoverage(true);
                chartConfigurationRequest.setPerformanceStandards(true);
                Call<GlobalConfigurationParent> call = mNetworkModel.fetchGlobalConfiguration(chartConfigurationRequest);
                Response<GlobalConfigurationParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("ChartConfiguration", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<GlobalConfigurationParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("ChartConfiguration", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("ChartConfiguration", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

    /*To show detailed time spent in popup-reading, video and practice*/
    public void showDetailedTotalTimeSpent(Context context, float finalTotalTimeSpent, float finalTotalReadTime, float finalTotalVideoTime, float finalTotalPracticeTime, String SubjectName) {
        final Dialog dialog = new Dialog(context);
        final LayoutAnalyticsTimeSpentDetailPopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_analytics_time_spent_detail_popup, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);

        String formattedTotalTimeSpent = showHoursMinutesFromSeconds((long) finalTotalTimeSpent);
        String formattedReadTimeSpent = showHoursMinutesFromSeconds((long) finalTotalReadTime);
        String formattedVideoTimeSpent = showHoursMinutesFromSeconds((long) finalTotalVideoTime);
        String formattedPracticeTimeSpent = showHoursMinutesFromSeconds((long) finalTotalPracticeTime);
        int readingPercentage = Math.round((finalTotalReadTime / finalTotalTimeSpent) * 100);
        int videoPercentage = Math.round((finalTotalVideoTime / finalTotalTimeSpent) * 100);
        int practicePercentage = Math.round((finalTotalPracticeTime / finalTotalTimeSpent) * 100);
        if (!TextUtils.isEmpty(SubjectName)) {
            binding.textViewTitle.setText(SubjectName + " " + context.getString(R.string.efforts));
        } else {
            binding.textViewTitle.setText(context.getString(R.string.total_efforts));
        }

        binding.textViewTotalTimeSpent.setText(formattedTotalTimeSpent);
        binding.textViewReadingTime.setText(formattedReadTimeSpent);
        binding.textViewVideoTime.setText(formattedVideoTimeSpent);
        binding.textViewPracticeTime.setText(formattedPracticeTimeSpent);
        binding.progressBarReading.setProgress(readingPercentage);
        binding.progressBarVideo.setProgress(videoPercentage);
        binding.progressBarPractice.setProgress(practicePercentage);


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    /*To show detailed daily time spent in popup-reading, video and practice*/
    public void showDetailedDailyTimeSpent(Context context, float dailyTimeSpent, float dailyReadTimeSpent, float dailyVideoTimeSpent, float dailyPracticeTimeSpent) {
        final Dialog dialog = new Dialog(context);
        final LayoutAnalyticsTimeSpentDetailPopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_analytics_time_spent_detail_popup, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);

        String formattedTotalTimeSpent = showHoursMinutesFromSeconds((long) dailyTimeSpent);
        String formattedReadTimeSpent = showHoursMinutesFromSeconds((long) dailyReadTimeSpent);
        String formattedVideoTimeSpent = showHoursMinutesFromSeconds((long) dailyVideoTimeSpent);
        String formattedPracticeTimeSpent = showHoursMinutesFromSeconds((long) dailyPracticeTimeSpent);
        int readingPercentage = Math.round((dailyReadTimeSpent / dailyTimeSpent) * 100);
        int videoPercentage = Math.round((dailyVideoTimeSpent / dailyTimeSpent) * 100);
        int practicePercentage = Math.round((dailyPracticeTimeSpent / dailyTimeSpent) * 100);

        binding.textViewTitle.setText(context.getString(R.string.daily_avg));
        binding.textViewTotalTimeSpent.setText(formattedTotalTimeSpent);
        binding.textViewReadingTime.setText(formattedReadTimeSpent);
        binding.textViewVideoTime.setText(formattedVideoTimeSpent);
        binding.textViewPracticeTime.setText(formattedPracticeTimeSpent);
        binding.progressBarReading.setProgress(readingPercentage);
        binding.progressBarVideo.setProgress(videoPercentage);
        binding.progressBarPractice.setProgress(practicePercentage);


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    /*Configurable Bar Text size*/
    public float barTextSize() {
        return 11f;
    }

    /*Configurable Bar Width*/
    public float barWidth() {
        return 0.36f;
    }

    public float effortBarWidth() {
        return 0.78f;
    }

    /*To fetch effort (time spent) data for all subjects*/
    public Observable<ArrayList<EffortvsPerformanceData>> fetchEffortvsPerformanceData() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<EffortvsPerformanceData>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<EffortvsPerformanceData>> e) throws Exception {
                Call<ArrayList<EffortvsPerformanceData>> call = mNetworkModel.fetchEffortvsPerformanceData();
                Response<ArrayList<EffortvsPerformanceData>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("EffortChartData", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<EffortvsPerformanceData>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("EffortChartData", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("EffortChartData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }
}
