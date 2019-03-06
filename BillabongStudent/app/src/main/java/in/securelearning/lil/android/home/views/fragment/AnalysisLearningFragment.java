package in.securelearning.lil.android.home.views.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutActivityAnalysisBinding;
import in.securelearning.lil.android.app.databinding.LayoutSubjectItemActivityBinding;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityData;
import in.securelearning.lil.android.base.dataobjects.AnalysisTopicCovered;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.PrefManagerStudentSubjectMapping;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.utils.PrefManager.getDefaultSubject;
import static in.securelearning.lil.android.syncadapter.utils.PrefManager.getSubjectMap;

/**
 * Created by Rupsi on 6/20/2018.
 */

public class AnalysisLearningFragment extends Fragment implements OnChartGestureListener,
        OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {

    @Inject
    HomeModel mHomeModel;
    LayoutActivityAnalysisBinding mBinding;
    SubjectAdapter subjectAdapter;
    ArrayList<AnalysisActivityData> data = new ArrayList<>();
    TopicCoveredAdapter mTopicCoveredAdapter;
    String weekitem;
    String mEndDate = null, mStartDate = null;
    String mSubjectId;
    private int mSkip = 0;
    private int mLimit = 10;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_activity_analysis, null, false);
        InjectorHome.INSTANCE.getComponent().inject(this);
        // getDownloadedData();
        // listenRxBusEvents();
        initializeSpinner();
        initializeSubjectRecyclerview();
        initializeRecentReadRecyclerView();
        initializeLineChart();

        ArrayList<PrefManager.SubjectExt> subjectList = new ArrayList<>();
        subjectList = PrefManagerStudentSubjectMapping.getSubjectExtList(getActivity());
        if (mSubjectId == null) {
            mSubjectId = subjectList.get(0).getSubjects().get(0).getId();
        }
        Date sdate = null, edate = null;
        Calendar c = Calendar.getInstance();
        String stdate = DateUtils.getCurrentStartWeek(c);
        String enate = DateUtils.getCurrentEndWeek(c);
        SimpleDateFormat parseFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            sdate = parseFormat.parse(String.valueOf(stdate));
            edate = parseFormat.parse(String.valueOf(enate));
        } catch (ParseException p) {
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        mStartDate = format.format(sdate);
        mEndDate = format.format(edate);
        Log.e("sdate", mStartDate + "" + mEndDate);

        setDefaults();
        getData(mSubjectId, mStartDate, mEndDate);
        getTopicCoveredData(mSubjectId, mSkip, mLimit);

        mBinding.textRecent.setText("Topic Covered");
        return mBinding.getRoot();
    }

    private void setDefaults() {
        mSkip = 0;
        mLimit = 10;
        initializeRecentReadRecyclerView();
    }

    private void initializeLineChart() {
        mBinding.chart1.setOnChartGestureListener(this);
        mBinding.chart1.setOnChartValueSelectedListener(this);
        mBinding.chart1.setDrawGridBackground(false);
        Legend l = mBinding.chart1.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        mBinding.chart1.setDescription("Number of topic count/Date");
        mBinding.chart1.setNoDataTextDescription("You need to provide data for the chart.");
        mBinding.chart1.setTouchEnabled(true);
        mBinding.chart1.setDragEnabled(true);
        mBinding.chart1.setScaleEnabled(true);
        YAxis leftAxis = mBinding.chart1.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        mBinding.chart1.getAxisRight().setEnabled(false);
        mBinding.chart1.animateX(2500, Easing.EasingOption.EaseInOutQuart);


    }


    private void initializeRecentReadRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewRecentRead.setLayoutManager(layoutManager);
        mBinding.recyclerViewRecentRead.setNestedScrollingEnabled(false);
        mTopicCoveredAdapter = new TopicCoveredAdapter(new ArrayList<AnalysisTopicCovered>());
        mBinding.recyclerViewRecentRead.setAdapter(mTopicCoveredAdapter);
        mBinding.textLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.layoutProgress.setVisibility(View.VISIBLE);
                getTopicCoveredData(mSubjectId, mSkip, mLimit);
            }
        });

    }


    private void initializeSubjectRecyclerview() {
        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        subjectAdapter = new SubjectAdapter(PrefManagerStudentSubjectMapping.getSubjectExtList(getActivity()), getSubjectMap(getActivity()));
        mBinding.recyclerView.setAdapter(subjectAdapter);
    }

    private void initializeSpinner() {
        // Spinner Drop down elements
        mBinding.spinner.setOnItemSelectedListener(this);
        List<String> selectWeek = new ArrayList<String>();
        //selectWeek.add("All");
        selectWeek.add("Current Week");
        selectWeek.add("Last Week");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, selectWeek);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinner.setAdapter(dataAdapter);
    }


    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        // On selecting a spinner item
        weekitem = parent.getItemAtPosition(position).toString();
        if (mSubjectId == null) {
            ArrayList<PrefManager.SubjectExt> subjectList = new ArrayList<>();
            subjectList = PrefManagerStudentSubjectMapping.getSubjectExtList(getActivity());
            mSubjectId = subjectList.get(0).getSubjects().get(0).getId();
        }
        Date sdate = null, edate = null;
        if (weekitem.equals("Current Week")) {
            Calendar c = Calendar.getInstance();
            String stdate = DateUtils.getCurrentStartWeek(c);
            String enate = DateUtils.getCurrentEndWeek(c);
            SimpleDateFormat parseFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            try {
                sdate = parseFormat.parse(String.valueOf(stdate));
                edate = parseFormat.parse(String.valueOf(enate));
            } catch (ParseException p) {
            }
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            mStartDate = format.format(sdate);
            mEndDate = format.format(edate);
            Log.e("sdate", mStartDate + "" + mEndDate);
            // getData(mSubjectId, mStartDate, mEndDate);
        } else {
            Calendar c = Calendar.getInstance();
            Date d = Calendar.getInstance().getTime();
            System.out.println("Current time => " + d);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            String formattedDate = df.format(d);
            String strDateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
            String day = DateUtils.getDayFromDateString(formattedDate, strDateFormat);
            String stdate = DateUtils.getLastWeekStartdate(c, day);
            String enate = DateUtils.getLastWeekEnddate(c, day);
            SimpleDateFormat parseFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            try {
                sdate = parseFormat.parse(String.valueOf(stdate));
                edate = parseFormat.parse(String.valueOf(enate));
            } catch (ParseException p) {
            }
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            mStartDate = format.format(sdate);
            mEndDate = format.format(edate);
            Log.e("sdate", mStartDate + "" + mEndDate);
            Log.e("sdate", mStartDate + "" + mEndDate);

        }
        getData(mSubjectId, mStartDate, mEndDate);

    }

    public void onNothingSelected(AdapterView arg0) {
        // TODO Auto-generated method stub

    }


    private void getTopicCoveredData(final String subId, final int skip, final int limit) {

        Observable.create(new ObservableOnSubscribe<ArrayList<AnalysisTopicCovered>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<AnalysisTopicCovered>> e) throws Exception {
                e.onNext(mHomeModel.getTopicCovered(subId, skip, limit));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<AnalysisTopicCovered>>() {
                    @Override
                    public void accept(ArrayList<AnalysisTopicCovered> list) throws Exception {
                        mSkip += list.size();
                        //noResultFound(mSkip, true);
                        if (list.size() < limit) {
                            mBinding.textLoadMore.setVisibility(View.INVISIBLE);
                            mBinding.layoutProgress.setVisibility(View.GONE);
                        }
                        mTopicCoveredAdapter.addData(list);
                        mBinding.layoutProgress.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    //get data
    private void getData(final String subId, final String startDate, final String endDate) {
        data.clear();
        Observable.create(new ObservableOnSubscribe<ArrayList<AnalysisActivityData>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<AnalysisActivityData>> e) throws Exception {
                e.onNext(mHomeModel.getLearningDataList(subId, startDate, endDate));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<AnalysisActivityData>>() {
                    @Override
                    public void accept(ArrayList<AnalysisActivityData> list) throws Exception {
                        data.clear();
                        data.addAll(list);
                        mBinding.chart1.notifyDataSetChanged();
                        mBinding.chart1.invalidate();
                        setData(data);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });


    }


    private void setData(ArrayList<AnalysisActivityData> data) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<>();
        //if (analysisActivityData != null) {
        //data = mHomeModel.getActivityDataList();

        for (int i = 0; i < data.size(); i++) {
            float num = Float.parseFloat(data.get(i).getCount());
            // float den = Float.parseFloat(data.get(i).getDate());
            //innerCount.add((num / den) * 100);
            yVals.add(new Entry(num, i));
            //convert date
            String dtStart = data.get(i).getDate();
            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            try {
                date = format.parse(dtStart);
                System.out.println(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String datadate = DateUtils.getDayMonthStringFromDate(date);
            xVals.add(datadate);
        }

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "");

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.gradient_blue_pink);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.BLUE);
        }
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        mBinding.chart1.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        // set the line to be drawn like this "- - - - - -"
        //   set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLUE);
        //set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setDrawCubic(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data1 = new LineData(xVals, dataSets);

        // set data
        mBinding.chart1.setData(data1);
        mBinding.chart1.invalidate();
        mBinding.chart1.notifyDataSetChanged();
    }


    @Override
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            mBinding.chart1.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mBinding.chart1.getLowestVisibleXIndex()
                + ", high: " + mBinding.chart1.getHighestVisibleXIndex());

        Log.i("MIN MAX", "xmin: " + mBinding.chart1.getXChartMin()
                + ", xmax: " + mBinding.chart1.getXChartMax()
                + ", ymin: " + mBinding.chart1.getYChartMin()
                + ", ymax: " + mBinding.chart1.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }


    private class SubjectAdapter extends RecyclerView.Adapter<AnalysisLearningFragment.SubjectAdapter.ViewHolder> {
        private final HashMap<String, PrefManager.SubjectExt> mSubjectMap;
        private ArrayList<PrefManager.SubjectExt> mSubjects = new ArrayList<>();
        int row_index;


        public void clear() {
            if (mSubjectMap != null) {
                mSubjectMap.clear();
            }
            if (mSubjects != null) {
                mSubjects.clear();
            }
        }

        public SubjectAdapter(ArrayList<PrefManager.SubjectExt> subjectList, HashMap<String, PrefManager.SubjectExt> subjectMap) {
            this.mSubjects = subjectList;
            this.mSubjectMap = subjectMap;

        }

        @Override
        public AnalysisLearningFragment.SubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutSubjectItemActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_subject_item_activity, parent, false);
            return new AnalysisLearningFragment.SubjectAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(AnalysisLearningFragment.SubjectAdapter.ViewHolder holder, final int position) {
            final PrefManager.SubjectExt subject = mSubjects.get(position);
            PrefManager.SubjectExt subjectExt = mSubjectMap.get(subject.getId());

            holder.mbinding.l1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.item_anim));
                    row_index = position;
                    notifyDataSetChanged();
                    if (subject.getSubjectIds() != null && subject.getSubjectIds().size() > 0) {
                        for (String id : subject.getSubjectIds()) {
                            PrefManager.SubjectExt ext = mSubjectMap.get(id);
                            mSubjectId = id;
                        }
                    }
                    Date sdate = null, edate = null;
                    if (weekitem.equals("Current Week")) {
                        Calendar c = Calendar.getInstance();
                        String stdate = DateUtils.getCurrentStartWeek(c);
                        String enate = DateUtils.getCurrentEndWeek(c);
                        SimpleDateFormat parseFormat =
                                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        try {
                            sdate = parseFormat.parse(String.valueOf(stdate));
                            edate = parseFormat.parse(String.valueOf(enate));
                        } catch (ParseException p) {
                        }
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        mStartDate = format.format(sdate);
                        mEndDate = format.format(edate);
                        Log.e("sdate", mStartDate + "" + mEndDate);
                        //getData(mSubjectId, mStartDate, mEndDate);
                    }
//
                    else {

                        Calendar c = Calendar.getInstance();
                        Date d = Calendar.getInstance().getTime();
                        System.out.println("Current time => " + d);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        String formattedDate = df.format(d);
                        String strDateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
                        String day = DateUtils.getDayFromDateString(formattedDate, strDateFormat);
                        String stdate = DateUtils.getLastWeekStartdate(c, day);
                        String enate = DateUtils.getLastWeekEnddate(c, day);
                        SimpleDateFormat parseFormat =
                                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        try {
                            sdate = parseFormat.parse(String.valueOf(stdate));
                            edate = parseFormat.parse(String.valueOf(enate));
                        } catch (ParseException p) {
                        }
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        mStartDate = format.format(sdate);
                        mEndDate = format.format(edate);
                        Log.e("sdate", mStartDate + "" + mEndDate);
                        Log.e("sdate", mStartDate + "" + mEndDate);

                    }
                    setDefaults();
                    getData(mSubjectId, mStartDate, mEndDate);
                    getTopicCoveredData(mSubjectId, mSkip, mLimit);

                }
            });
            if (row_index == position) {
                holder.mbinding.l1.setBackgroundResource(R.drawable.background_circle_selected_subject_icon);
                int iconId = 0;
                if (subject.getSubjectIds() != null && subject.getSubjectIds().size() > 0) {
                    for (String id :
                            subject.getSubjectIds()) {
                        PrefManager.SubjectExt ext = mSubjectMap.get(id);
                        if (ext != null) {
                            iconId = ext.getIconTransparentId();
                            break;
                        }
                    }
                }
                if (iconId == 0) {
                    iconId = subjectExt.getIconTransparentId();
                }
                Picasso.with(getActivity()).load(iconId).into(holder.mbinding.imageViewSubjectIcon);
                holder.mbinding.imageViewSubjectIcon.setBackgroundColor(getResources().getColor(R.color.color2_foreground));
                //holder.mBinding.imageViewSubjectIcon.setBackground(getResources().getDrawable(R.drawable.background_circle_selected_subject_icon));
            } else {
                holder.mbinding.l1.setBackgroundResource(R.drawable.background_circle_subject_icon_unselected);
                if (subjectExt == null) {
                    subjectExt = getDefaultSubject();
                }
                int iconId = 0;
                if (subject.getSubjectIds() != null && subject.getSubjectIds().size() > 0) {
                    for (String id :
                            subject.getSubjectIds()) {
                        PrefManager.SubjectExt ext = mSubjectMap.get(id);
                        if (ext != null) {
                            iconId = ext.getIconWhiteId();
                            break;
                        }
                    }
                }
                if (iconId == 0) {
                    iconId = subjectExt.getIconWhiteId();
                }
                int color = subjectExt.getTextColor();

                Picasso.with(getActivity()).load(iconId).into(holder.mbinding.imageViewSubjectIcon);
                holder.mbinding.imageViewSubjectIcon.setBackgroundColor(color);
            }

            holder.mbinding.textViewSubjectName.setText(subject.getName());

        }

        @Override
        public int getItemCount() {

            return mSubjects.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutSubjectItemActivityBinding mbinding;

            public ViewHolder(LayoutSubjectItemActivityBinding binding) {
                super(binding.getRoot());
                mbinding = binding;
            }
        }

    }
}
