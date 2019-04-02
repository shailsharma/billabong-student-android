package in.securelearning.lil.android.analytics.model;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutAnalyticsTimeSpentDetailPopupBinding;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.ChartDataRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.CoverageChartData;
import in.securelearning.lil.android.syncadapter.dataobjects.EffortChartDataParent;
import in.securelearning.lil.android.syncadapter.dataobjects.EffortChartDataRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.EffortChartDataWeekly;
import in.securelearning.lil.android.syncadapter.dataobjects.PerformanceChartData;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

public class AnalyticsModel {

    @Inject
    FlavorNetworkModel mFlavorNetworkModel;

    @Inject
    Context mContext;

    @Inject
    AppUserModel mAppUserModel;

    public AnalyticsModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    /*Set activity status bar style immersive*/
    public void setImmersiveStatusBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /*convert seconds to natural time format*/
    public String convertSecondToHourMinuteSecond(long actualSeconds) {
        String strHours = "";
        String strMinutes = "";

        long hours = actualSeconds / 3600;
        long hoursMod = actualSeconds % 3600;
        long minutes = hoursMod / 60;
        long seconds = hoursMod % 60;

        /*This is to convert into a minute, if seconds value is >= 30 seconds else seconds
        value will become zero as discussed*/
        if (seconds >= 30) {
            minutes = minutes + 1;
        }

        strHours = String.valueOf(hours);
        strMinutes = String.valueOf(minutes);
        // strSeconds = String.valueOf(newSeconds);


        if (hours >= 0 && hours <= 9) {
            strHours = "0" + strHours;
        }

        if (minutes >= 0 && minutes <= 9) {
            strMinutes = "0" + strMinutes;
        }
//        if (newSeconds >= 0 && newSeconds <= 9) {
//            strSeconds = "0" + strSeconds;
//
//        }

        return strHours + ":" + strMinutes; // + ":" + strSeconds;
    }

    /*convert seconds to minutes*/
    public String convertSecondToMinute(long actualSeconds) {
        String strMinutes = "";

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

    /*convert seconds to minutes*/
    public long longConvertSecondToMinute(long actualSeconds) {

        long hoursMod = actualSeconds % 3600;
        long minutes = hoursMod / 60;
        long seconds = hoursMod % 60;

        /*This is to convert into a minute, if seconds value is >= 30 seconds else seconds
        value will become zero as discussed*/
        if (seconds >= 30) {
            minutes = minutes + 1;
        }

        return minutes;
    }

    /*Method to fetch coverage data for particular subject and all subject*/
    public Observable<ArrayList<CoverageChartData>> fetchCoverageData(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<CoverageChartData>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CoverageChartData>> e) throws Exception {
                Call<ArrayList<CoverageChartData>> call;
                if (!TextUtils.isEmpty(subjectId)) {
                    call = mFlavorNetworkModel.fetchSubjectWiseCoverageData(new ChartDataRequest(subjectId));
                } else {
                    call = mFlavorNetworkModel.fetchAllSubjectCoverageData(new ChartDataRequest());
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
                        e.onNext(response.body());
                    } else if (response2.code() == 401) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("CoverageChartData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }

//                ArrayList<CoverageChartData> coverageChartData = new ArrayList<>();
//                CoverageChartData coverageChartData1 = new CoverageChartData();
//                coverageChartData1.setCoverage(2);
//                coverageChartData1.setPending(8);
//                coverageChartData1.setTotal(10);
//                coverageChartData1.setId("5c782304b9a6350013426226");
//                coverageChartData1.setName("Maths");
//
//                CoverageChartData coverageChartData2 = new CoverageChartData();
//                coverageChartData2.setCoverage(5);
//                coverageChartData2.setPending(5);
//                coverageChartData2.setTotal(10);
//                coverageChartData2.setId("5c782304b9a6350013426226");
//                coverageChartData2.setName("Hindi");
//
//                CoverageChartData coverageChartData3 = new CoverageChartData();
//                coverageChartData3.setCoverage(10);
//                coverageChartData3.setPending(8);
//                coverageChartData3.setTotal(10);
//                coverageChartData3.setId("5c782304b9a6350013426226");
//                coverageChartData3.setName("English");
//
//                coverageChartData.add(coverageChartData3);
//                coverageChartData.add(coverageChartData1);
//                coverageChartData.add(coverageChartData2);
//                e.onNext(coverageChartData);
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
                    call = mFlavorNetworkModel.fetchSubjectWisePerformanceData(new ChartDataRequest(subjectId));
                } else {
                    call = mFlavorNetworkModel.fetchAllSubjectPerformanceData(new ChartDataRequest());
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
                        e.onNext(response.body());
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
                Call<EffortChartDataParent> call = mFlavorNetworkModel.fetchEffortData(effortChartDataRequest);
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

    /*To fetch effort (time spent) data for individual subject*/
    public Observable<EffortChartDataParent> fetchSubjectWiseEffortData(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<EffortChartDataParent>() {
            @Override
            public void subscribe(ObservableEmitter<EffortChartDataParent> e) throws Exception {
                EffortChartDataRequest effortChartDataRequest = new EffortChartDataRequest();
                effortChartDataRequest.setEmail(mAppUserModel.getApplicationUser().getEmail());
                effortChartDataRequest.setSubjectId(subjectId);
                Call<EffortChartDataParent> call = mFlavorNetworkModel.fetchSubjectWiseEffortData(effortChartDataRequest);
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

//                EffortChartDataParent effortChartDataParent = new EffortChartDataParent();
//                ArrayList<EffortChartData> chartDataArrayList = new ArrayList<>();
//                EffortChartData effortChartData1 = new EffortChartData();
//                effortChartData1.setTotalTimeSpent(310);
//                effortChartData1.setTotalReadTimeSpent(107);
//                effortChartData1.setTotalVideoTimeSpent(100);
//                effortChartData1.setTotalPracticeTimeSpent(100);
//                effortChartData1.setTopic(new ArrayList<IdNameObject>(Collections.singleton(new IdNameObject("", "Cooking and other skills needed at home"))));
//
//                EffortChartData effortChartData2 = new EffortChartData();
//                effortChartData2.setTotalTimeSpent(1200);
//                effortChartData2.setTotalReadTimeSpent(300);
//                effortChartData2.setTotalVideoTimeSpent(400);
//                effortChartData2.setTotalPracticeTimeSpent(500);
//                effortChartData2.setTopic(new ArrayList<IdNameObject>(Collections.singleton(new IdNameObject("", "The study of substances and the different ways in which they react or combine with other substances"))));
//
//                EffortChartData effortChartData3 = new EffortChartData();
//                effortChartData3.setTotalTimeSpent(600);
//                effortChartData3.setTotalReadTimeSpent(300);
//                effortChartData3.setTotalVideoTimeSpent(150);
//                effortChartData3.setTotalPracticeTimeSpent(150);
//                effortChartData3.setTopic(new ArrayList<IdNameObject>(Collections.singleton(new IdNameObject("", "Driving"))));
//                chartDataArrayList.add(effortChartData1);
//                chartDataArrayList.add(effortChartData2);
//                chartDataArrayList.add(effortChartData3);
//
//                effortChartDataParent.setEffortChartDataList(chartDataArrayList);
//                effortChartDataParent.setDaysCount(4);
//                e.onNext(effortChartDataParent);
                e.onComplete();
            }
        });
    }

    /*To fetch effort (time spent) weekly data for individual subject*/
    public Observable<ArrayList<EffortChartDataWeekly>> fetchWeeklyEffortData(final String subjectId) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<EffortChartDataWeekly>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<EffortChartDataWeekly>> e) throws Exception {
                EffortChartDataRequest effortChartDataRequest = new EffortChartDataRequest();
                effortChartDataRequest.setEmail(mAppUserModel.getApplicationUser().getEmail());
                effortChartDataRequest.setSubjectId(subjectId);
                Call<ArrayList<EffortChartDataWeekly>> call = mFlavorNetworkModel.fetchWeeklyEffortData(effortChartDataRequest);
                Response<ArrayList<EffortChartDataWeekly>> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("EffortChartDataWeekly", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mContext)) {
                    Response<ArrayList<EffortChartDataWeekly>> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("EffortChartDataWeekly", "Successful");
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

//                ArrayList<EffortChartDataWeekly> chartDataArrayList = new ArrayList<>();
//                EffortChartDataWeekly effortChartDataWeekly1 = new EffortChartDataWeekly();
//                effortChartDataWeekly1.setDate("2019-03-23T00:00:00.000Z");
//                effortChartDataWeekly1.setTime(720);
//
//                EffortChartDataWeekly effortChartDataWeekly2 = new EffortChartDataWeekly();
//                effortChartDataWeekly2.setDate("2019-03-24T00:00:00.000Z");
//                effortChartDataWeekly2.setTime(400);
//
//                EffortChartDataWeekly effortChartDataWeekly3 = new EffortChartDataWeekly();
//                effortChartDataWeekly3.setDate("2019-03-25T00:00:00.000Z");
//                effortChartDataWeekly3.setTime(540);
//
//                EffortChartDataWeekly effortChartDataWeekly4 = new EffortChartDataWeekly();
//                effortChartDataWeekly4.setDate("2019-03-26T00:00:00.000Z");
//                effortChartDataWeekly4.setTime(999);
//
//                EffortChartDataWeekly effortChartDataWeekly5 = new EffortChartDataWeekly();
//                effortChartDataWeekly5.setDate("2019-03-27T00:00:00.000Z");
//                effortChartDataWeekly5.setTime(250);
//
//                EffortChartDataWeekly effortChartDataWeekly6 = new EffortChartDataWeekly();
//                effortChartDataWeekly6.setDate("2019-03-28T00:00:00.000Z");
//                effortChartDataWeekly6.setTime(250);
//
//                EffortChartDataWeekly effortChartDataWeekly7 = new EffortChartDataWeekly();
//                effortChartDataWeekly7.setDate("2019-03-29T00:00:00.000Z");
//                effortChartDataWeekly7.setTime(150);
//
//                chartDataArrayList.add(effortChartDataWeekly1);
//                chartDataArrayList.add(effortChartDataWeekly2);
//                chartDataArrayList.add(effortChartDataWeekly3);
//                chartDataArrayList.add(effortChartDataWeekly4);
//                chartDataArrayList.add(effortChartDataWeekly5);
//                chartDataArrayList.add(effortChartDataWeekly6);
//                chartDataArrayList.add(effortChartDataWeekly7);
//                e.onNext(chartDataArrayList);
                e.onComplete();
            }
        });
    }

    public String getFormattedDateForWeeklyEffortChart(String isoDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM", Locale.ENGLISH);
        return formatter.format(new Date(DateUtils.getSecondsOfISODateString(isoDate) * 1000L));
    }


    public void showDetailedTotalTimeSpent(Context context, float finalTotalTimeSpent, float finalTotalReadTime, float finalTotalVideoTime, float finalTotalPracticeTime) {
        final Dialog dialog = new Dialog(context);
        final LayoutAnalyticsTimeSpentDetailPopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_analytics_time_spent_detail_popup, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);

        String formattedTotalTimeSpent = convertSecondToHourMinuteSecond((long) finalTotalTimeSpent);
        String formattedReadTimeSpent = convertSecondToHourMinuteSecond((long) finalTotalReadTime);
        String formattedVideoTimeSpent = convertSecondToHourMinuteSecond((long) finalTotalVideoTime);
        String formattedPracticeTimeSpent = convertSecondToHourMinuteSecond((long) finalTotalPracticeTime);

        binding.textViewTitle.setText(context.getString(R.string.total_hrs));
        binding.textViewTotalTimeSpent.setText(formattedTotalTimeSpent);
        binding.textViewReadingTime.setText(formattedReadTimeSpent);
        binding.textViewVideoTime.setText(formattedVideoTimeSpent);
        binding.textViewPracticeTime.setText(formattedPracticeTimeSpent);

        binding.progressBarReading.setMax((int) finalTotalTimeSpent);
        binding.progressBarVideo.setMax((int) finalTotalTimeSpent);
        binding.progressBarPractice.setMax((int) finalTotalTimeSpent);

        binding.progressBarReading.setProgress((int) finalTotalReadTime);
        binding.progressBarVideo.setProgress((int) finalTotalVideoTime);
        binding.progressBarPractice.setProgress((int) finalTotalPracticeTime);


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public void showDetailedDailyTimeSpent(Context context, float dailyTimeSpent, float dailyReadTimeSpent, float dailyVideoTimeSpent, float dailyPracticeTimeSpent) {
        final Dialog dialog = new Dialog(context);
        final LayoutAnalyticsTimeSpentDetailPopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_analytics_time_spent_detail_popup, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);

        String formattedTotalTimeSpent = convertSecondToMinute((long) dailyTimeSpent);
        String formattedReadTimeSpent = convertSecondToMinute((long) dailyReadTimeSpent);
        String formattedVideoTimeSpent = convertSecondToMinute((long) dailyVideoTimeSpent);
        String formattedPracticeTimeSpent = convertSecondToMinute((long) dailyPracticeTimeSpent);

        binding.textViewTitle.setText(context.getString(R.string.daily_efforts_min));
        binding.textViewTotalTimeSpent.setText(formattedTotalTimeSpent);
        binding.textViewReadingTime.setText(formattedReadTimeSpent);
        binding.textViewVideoTime.setText(formattedVideoTimeSpent);
        binding.textViewPracticeTime.setText(formattedPracticeTimeSpent);

        binding.progressBarReading.setMax((int) dailyTimeSpent);
        binding.progressBarVideo.setMax((int) dailyTimeSpent);
        binding.progressBarPractice.setMax((int) dailyTimeSpent);

        binding.progressBarReading.setProgress((int) dailyReadTimeSpent);
        binding.progressBarVideo.setProgress((int) dailyVideoTimeSpent);
        binding.progressBarPractice.setProgress((int) dailyPracticeTimeSpent);


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public float barTextSize(){
        return 11f;
    }


    public float barWidth(){
        return 0.36f;
    }
}
