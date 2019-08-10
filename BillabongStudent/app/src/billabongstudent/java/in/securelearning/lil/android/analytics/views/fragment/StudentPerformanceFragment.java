package in.securelearning.lil.android.analytics.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.BenchMarkPerformance;
import in.securelearning.lil.android.analytics.dataobjects.EffortvsPerformanceData;
import in.securelearning.lil.android.analytics.model.AnalyticsModel;
import in.securelearning.lil.android.analytics.views.adapter.StudentPerformanceAdapter;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutStudentAnalyticsPerformanceBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StudentPerformanceFragment extends Fragment implements View.OnClickListener {

    LayoutStudentAnalyticsPerformanceBinding mBinding;
    @Inject
    AnalyticsModel mAnalyticsModel;
    ArrayList<EffortvsPerformanceData> mBrilliantSubjectList = null,
            mCatchingSubjectList = null, mWorkHarderList = null, mStudyingLot = null;
    BenchMarkPerformance mBenchMarkPerformance;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;
    private Context mContext;

    public static Fragment newInstance(BenchMarkPerformance benchMarkPerformance) {
        StudentPerformanceFragment fragment = new StudentPerformanceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ConstantUtil.BENCHMARK_PERFORMANCE, benchMarkPerformance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mBenchMarkPerformance = (BenchMarkPerformance) getArguments().getSerializable(ConstantUtil.BENCHMARK_PERFORMANCE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_student_analytics_performance, container, false);

        if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            if (mBenchMarkPerformance != null) {
                mBinding.progressBarPerformance.setVisibility(View.VISIBLE);
                fetchSubjectPerformanceData();
            } else {
                mBinding.progressBarPerformance.setVisibility(View.GONE);
                showNoData();
            }
        }
        mBinding.llBrilliant.setOnClickListener(this);
        mBinding.llCatching.setOnClickListener(this);
        mBinding.llStudying.setOnClickListener(this);
        mBinding.llWorkHarder.setOnClickListener(this);
        return mBinding.getRoot();
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            if (mBenchMarkPerformance != null) {
                fetchSubjectPerformanceData();
            } else {
                showNoData();
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
    private void fetchSubjectPerformanceData() {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mAnalyticsModel.fetchEffortvsPerformanceData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<EffortvsPerformanceData>>() {
                        @Override
                        public void accept(ArrayList<EffortvsPerformanceData> responses) throws Exception {
                            mBinding.progressBarPerformance.setVisibility(View.GONE);
                            if (responses != null && !responses.isEmpty()) {

                                showEffortChart(responses);
                            } else {
                                showNoData();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mBinding.progressBarPerformance.setVisibility(View.GONE);
                            throwable.printStackTrace();
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            mBinding.progressBarPerformance.setVisibility(View.GONE);

                        }
                    });

        } else
            showNoData();
    }

    private void showNoData() {

        mBinding.textViewBrilliant.setText(R.string.zero);
        mBinding.textViewCatching.setText(R.string.zero);
        mBinding.textViewStudying.setText(R.string.zero);
        mBinding.textViewWorkHarder.setText(R.string.zero);

    }

    /* < 1min >=50% gifted/brilliant
     * >= 1 min >=50% studious /catching well
     *<1 <50 Lazy/work harder
     * >=1 <50  Remediation
     * */
    private void showEffortChart(ArrayList<EffortvsPerformanceData> responses) {

        mBrilliantSubjectList = new ArrayList<>();
        mWorkHarderList = new ArrayList<>();
        mCatchingSubjectList = new ArrayList<>();
        mStudyingLot = new ArrayList<>();

        float avgDaily = 0f;

        EffortvsPerformanceData.TimeResponse timeResponse = null;

        for (EffortvsPerformanceData subjectResponse : responses) {
            if (subjectResponse != null) {
                float percentage = subjectResponse.getPercentage();
                timeResponse = subjectResponse.getTimeResponseList();
                if (timeResponse != null) {
                    avgDaily = timeResponse.getAvgDaily();
                }

//                if (avgDaily != 0f) {
                if ((percentage >= mBenchMarkPerformance.getBenchMarkPercentage()) && (avgDaily < mBenchMarkPerformance.getBenchMarkTime())) {
                    mBrilliantSubjectList.add(subjectResponse);
                } else if (percentage >= mBenchMarkPerformance.getBenchMarkPercentage() && (avgDaily >= mBenchMarkPerformance.getBenchMarkTime())) {
                    mCatchingSubjectList.add(subjectResponse);
                } else if (percentage < mBenchMarkPerformance.getBenchMarkPercentage() && (avgDaily >= mBenchMarkPerformance.getBenchMarkTime())) {
                    mStudyingLot.add(subjectResponse);
                } else if (percentage < mBenchMarkPerformance.getBenchMarkPercentage() && (avgDaily < mBenchMarkPerformance.getBenchMarkTime())) {
                    mWorkHarderList.add(subjectResponse);
                }

//


            }
        }
        if (mBrilliantSubjectList != null && !mBrilliantSubjectList.isEmpty()) {
            mBinding.textViewBrilliant.setText(String.valueOf(mBrilliantSubjectList.size()));
        }
        if (mCatchingSubjectList != null && !mCatchingSubjectList.isEmpty()) {
            mBinding.textViewCatching.setText(String.valueOf(mCatchingSubjectList.size()));
        }
        if (mWorkHarderList != null && !mWorkHarderList.isEmpty()) {
            mBinding.textViewWorkHarder.setText(String.valueOf(mWorkHarderList.size()));
        }
        if (mStudyingLot != null && !mStudyingLot.isEmpty()) {
            mBinding.textViewStudying.setText(String.valueOf(mStudyingLot.size()));
        }
        //logic of showing by default value
        if (mBrilliantSubjectList != null && !mBrilliantSubjectList.isEmpty()) {
            setEffortDaily(R.string.you_are_briliant, mBrilliantSubjectList, R.drawable.background_circle_briliant);
            highlightSelectedStudent(mBinding.textViewBrilliant, mBinding.textViewCatching, mBinding.textViewWorkHarder, mBinding.textViewStudying);
        } else if (mCatchingSubjectList != null && !mCatchingSubjectList.isEmpty()) {
            setEffortDaily(R.string.you_are_catching, mCatchingSubjectList, R.drawable.background_circle_catching_up);
            highlightSelectedStudent(mBinding.textViewCatching, mBinding.textViewWorkHarder, mBinding.textViewStudying, mBinding.textViewBrilliant);
        } else if (mWorkHarderList != null && !mWorkHarderList.isEmpty()) {
            setEffortDaily(R.string.you_are_harder, mWorkHarderList, R.drawable.background_circle_work_harder);
            highlightSelectedStudent(mBinding.textViewWorkHarder, mBinding.textViewStudying, mBinding.textViewBrilliant, mBinding.textViewCatching);
        } else if (mStudyingLot != null && !mStudyingLot.isEmpty()) {
            setEffortDaily(R.string.you_are_study, mStudyingLot, R.drawable.background_circle_studying_lot);
            highlightSelectedStudent(mBinding.textViewStudying, mBinding.textViewBrilliant, mBinding.textViewCatching, mBinding.textViewWorkHarder);

        } else {
            mBinding.llPerformance.setVisibility(View.GONE);
            mBinding.textViewBrilliant.setText(R.string.zero);
            mBinding.textViewCatching.setText(R.string.zero);
            mBinding.textViewWorkHarder.setText(R.string.zero);
            mBinding.textViewStudying.setText(R.string.zero);
        }


    }

    private void setEffortDaily(int studentType, ArrayList<EffortvsPerformanceData> subjectList, int drawable) {
        mBinding.llPerformance.setVisibility(View.VISIBLE);
        mBinding.textViewPerformance.setText(studentType);
        mBinding.textViewCount.setText(String.valueOf(subjectList.size()));
        mBinding.textViewCount.setBackgroundResource(drawable);
        showStudentList(subjectList);
    }


    private void showStudentList(ArrayList<EffortvsPerformanceData> subjectList) {


        mBinding.rvSubject.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mBinding.rvSubject.setLayoutManager(linearLayoutManager);
        StudentPerformanceAdapter adapter = new StudentPerformanceAdapter(subjectList, mContext);
        mBinding.rvSubject.setAdapter(adapter);

    }

    private void highlightSelectedStudent(TextView highLightTextView, TextView textView_1, TextView textView_2, TextView textView_3) {

        highLightTextView.setTypeface(highLightTextView.getTypeface(), Typeface.BOLD);

        textView_1.setTypeface(textView_1.getTypeface(), Typeface.NORMAL);
        textView_2.setTypeface(textView_2.getTypeface(), Typeface.NORMAL);
        textView_3.setTypeface(textView_3.getTypeface(), Typeface.NORMAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            highLightTextView.setTextAppearance(android.R.style.TextAppearance_Large);
            textView_1.setTextAppearance(android.R.style.TextAppearance_Small);
            textView_2.setTextAppearance(android.R.style.TextAppearance_Small);
            textView_3.setTextAppearance(android.R.style.TextAppearance_Small);
        } else {
            highLightTextView.setTextSize(22);
            textView_1.setTextSize(14);
            textView_2.setTextSize(14);
            textView_3.setTextSize(14);
        }


    }






    /*Fetching the assigned homework of student there are two list overdue
    and combination of new ,today,upcoming list*/


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_brilliant:
                if (mBrilliantSubjectList != null && !mBrilliantSubjectList.isEmpty()) {
                    setEffortDaily(R.string.you_are_briliant, mBrilliantSubjectList, R.drawable.background_circle_briliant);
                    highlightSelectedStudent(mBinding.textViewBrilliant, mBinding.textViewCatching, mBinding.textViewWorkHarder, mBinding.textViewStudying);
                }


                break;
            case R.id.ll_Catching:
                if (mCatchingSubjectList != null && !mCatchingSubjectList.isEmpty()) {
                    setEffortDaily(R.string.you_are_catching, mCatchingSubjectList, R.drawable.background_circle_catching_up);
                    highlightSelectedStudent(mBinding.textViewCatching, mBinding.textViewWorkHarder, mBinding.textViewStudying, mBinding.textViewBrilliant);
                }
                break;
            case R.id.ll_work_harder:
                if (mWorkHarderList != null && !mWorkHarderList.isEmpty()) {
                    setEffortDaily(R.string.you_are_harder, mWorkHarderList, R.drawable.background_circle_work_harder);
                    highlightSelectedStudent(mBinding.textViewWorkHarder, mBinding.textViewStudying, mBinding.textViewBrilliant, mBinding.textViewCatching);
                }
                break;
            case R.id.ll_studying:
                if (mStudyingLot != null && !mStudyingLot.isEmpty()) {
                    setEffortDaily(R.string.you_are_study, mStudyingLot, R.drawable.background_circle_studying_lot);
                    highlightSelectedStudent(mBinding.textViewStudying, mBinding.textViewBrilliant, mBinding.textViewCatching, mBinding.textViewWorkHarder);
                }
                break;
            default:
                mBinding.llPerformance.setVisibility(View.GONE);
                break;

        }
    }
}



