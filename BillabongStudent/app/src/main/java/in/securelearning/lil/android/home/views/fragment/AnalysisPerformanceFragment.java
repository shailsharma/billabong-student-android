package in.securelearning.lil.android.home.views.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutPerformanceActivityBinding;
import in.securelearning.lil.android.app.databinding.LayoutSubjectItemActivityBinding;
import in.securelearning.lil.android.base.dataobjects.PerformanceResponseCount;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.PrefManagerStudentSubjectMapping;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.utils.PrefManager.getDefaultSubject;

/**
 * Created by Rupsi on 6/26/2018.
 */

public class AnalysisPerformanceFragment extends Fragment implements
        OnChartValueSelectedListener {
    LayoutPerformanceActivityBinding mBinding;
    SubjectAdapter mSubjectAdapter;
    //private RecentReadListAdapter mTopicCoveredAdapter;
    private ArrayList<Subject> mSubjects;
    HashMap<String, PrefManager.SubjectExt> mSubjectMap = new HashMap<>();
    @Inject
    HomeModel mHomeModel;
    String subjectid;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_performance_activity, null, false);
        //InjectorHome.INSTANCE.getComponent().inject(this);
        // getDownloadedData();
        // listenRxBusEvents();
        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.recyclerView.setAdapter(mSubjectAdapter);
        ArrayList<PrefManager.SubjectExt> subjectList = new ArrayList<>();
        subjectList = PrefManagerStudentSubjectMapping.getSubjectExtList(getActivity());
        if (subjectid == null) {
            subjectid = subjectList.get(0).getSubjects().get(0).getId();
        }
        getData();
        getCounts(subjectid);

//        mBinding.layoutSecond.progressBar.setProgress(40);
//        mBinding.layoutSecond.progressBar2.setProgress(10);
//        mBinding.layoutSecond.progressBar3.setProgress(80);

        return mBinding.getRoot();
    }


    private void getCounts(final String subId) {
        Observable.create(new ObservableOnSubscribe<PerformanceResponseCount>() {
            @Override
            public void subscribe(ObservableEmitter<PerformanceResponseCount> e) throws Exception {
                e.onNext(mHomeModel.getPerformanceCount(subId));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PerformanceResponseCount>() {
                    @Override
                    public void accept(PerformanceResponseCount count) throws Exception {
                        mBinding.layoutFirst.textNumberCorrect.setText(String.valueOf(count.getQuestionsCorrect() + "/" + count.getTotalQuestions()));
                        mBinding.layoutFirst.textTestAttemptNumber.setText(String.valueOf(count.getTestAttempted()));
                        mBinding.layoutFirst.textAvgTimeNumber.setText(String.valueOf(new DecimalFormat("##.##").format(count.getAverageTimePerQuestion())));

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });


    }


    private void getData() {
        Observable.create(new ObservableOnSubscribe<ArrayList<Subject>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Subject>> e) throws Exception {
                ArrayList<PrefManager.SubjectExt> subjectList = PrefManager.getSubjectList(getContext());
                for (int i = 0; i < subjectList.size(); i++) {
                    PrefManager.SubjectExt subject = subjectList.get(i);
                    mSubjectMap.put(subject.getId(), subject);
                }
                ArrayList<Subject> subjects = PrefManagerStudentSubjectMapping.getSubjectList(getContext());
                if (subjects != null) {
                    e.onNext(subjects);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Subject>>() {
                    @Override
                    public void accept(ArrayList<Subject> subjects) throws Exception {
                        if (subjects.size() > 0) {
                            mSubjects = subjects;
                            initializeSubjectViewpager(mSubjects);

                        } else {
                            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void initializeSubjectViewpager(ArrayList<Subject> subjects) {
        if (subjects != null && !subjects.isEmpty()) {
            final Subject subject = subjects.get(0);

            Observable.create(new ObservableOnSubscribe<HomeModel.SubjectMap>() {
                @Override
                public void subscribe(ObservableEmitter<HomeModel.SubjectMap> e) throws Exception {
                    HomeModel.SubjectMap subjectMap = mHomeModel.getLearningMapFromCurriculum(subject.getId(), subject.getName(), subject.getSubjectIds());
                    e.onNext(subjectMap);
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<HomeModel.SubjectMap>() {
                        @Override
                        public void accept(HomeModel.SubjectMap subjectMap) throws Exception {
                            PrefManager.SubjectExt subjectExt = mSubjectMap.get(subject.getId());
                            if (subjectExt == null) {
                                subjectExt = PrefManager.getDefaultSubject();
                            }
                            int[] colors = new int[4];
                            final int subjectColor = subjectExt.getColor();
                            int red = Color.red(subjectColor);
                            int green = Color.green(subjectColor);
                            int blue = Color.blue(subjectColor);
                            colors[0] = Color.rgb(red, green, blue);
                            colors[1] = Color.argb(153, red, green, blue);
                            colors[2] = Color.argb(76, red, green, blue);
                            colors[3] = Color.argb(76, 88, 88, 88);
                            drawCircle(subjectMap.getTotalObtained(), subjectExt.getTextColor(), subjectMap.getTotalQuestionsAttempted(), subject.getName(), subjectMap.getSkill()[0], subjectMap.getSkill()[1], subjectMap.getSkill()[2], subjectMap.getSkill()[3], colors);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });


            mSubjectAdapter = new SubjectAdapter(getContext(), subjects);
            mBinding.recyclerView.setAdapter(mSubjectAdapter);


        }
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PiChart", "nothing selected");
    }


    private class SubjectAdapter extends RecyclerView.Adapter<AnalysisPerformanceFragment.SubjectAdapter.ViewHolder> {
        ArrayList<Subject> mSubjects = new ArrayList<>();
        int row_index;

        public void clear() {
            if (mSubjectMap != null) {
                mSubjectMap.clear();
            }
            if (mSubjects != null) {
                mSubjects.clear();
            }
        }

        public SubjectAdapter(Context context, ArrayList<Subject> subjects) {
            this.mSubjects.addAll(subjects);

        }

        @Override
        public AnalysisPerformanceFragment.SubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutSubjectItemActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_subject_item_activity, parent, false);
            return new AnalysisPerformanceFragment.SubjectAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(AnalysisPerformanceFragment.SubjectAdapter.ViewHolder holder, final int position) {
            final Subject subject = mSubjects.get(position);
            PrefManager.SubjectExt subjectExt = mSubjectMap.get(subject.getId());
            holder.mbinding.l1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    row_index = position;
                    notifyDataSetChanged();
                    if (subject.getSubjectIds() != null && subject.getSubjectIds().size() > 0) {
                        for (String id : subject.getSubjectIds()) {
                            PrefManager.SubjectExt ext = mSubjectMap.get(id);
                            subjectid = id;
                        }
                    }
                    final Subject subject = mSubjects.get(position);

                    final ProgressDialog progressDialog = progressDialog(getActivity());
                    Observable.create(new ObservableOnSubscribe<HomeModel.SubjectMap>() {
                        @Override
                        public void subscribe(ObservableEmitter<HomeModel.SubjectMap> e) throws Exception {
                            HomeModel.SubjectMap subjectMap = mHomeModel.getLearningMapFromCurriculum(subject.getId(), subject.getName(), subject.getSubjectIds());
                            if (subjectMap != null) {
                                e.onNext(subjectMap);
                            } else {
                                e.onError(new NullPointerException(subject.getName()));
                            }
                            e.onComplete();
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<HomeModel.SubjectMap>() {
                                @Override
                                public void accept(HomeModel.SubjectMap subjectMap) throws Exception {
                                    // mBinding.layoutFirst.textNumberCorrect.setText(String.valueOf(subjectMap.getTotalObtained()));

                                    PrefManager.SubjectExt subjectExt = mSubjectMap.get(subjectMap.getSid());
                                    if (subjectExt == null) {
                                        subjectExt = PrefManager.getDefaultSubject();
                                    }
//                                    mBinding.layoutFirst.learningMapSkillImage1.setBackgroundColor(subjectExt.getTextColor());

                                    int[] colors = new int[4];
                                    final int subjectColor = subjectExt.getTextColor();
                                    int red = Color.red(subjectColor);
                                    int green = Color.green(subjectColor);
                                    int blue = Color.blue(subjectColor);
                                    colors[0] = Color.rgb(red, green, blue);
                                    colors[1] = Color.argb(153, red, green, blue);
                                    colors[2] = Color.argb(76, red, green, blue);
                                    colors[3] = Color.argb(76, 88, 88, 88);
                                    progressDialog.dismiss();
                                    drawCircle(subjectMap.getTotalObtained(), subjectExt.getTextColor(), subjectMap.getTotalQuestionsAttempted(), subject.getName(), subjectMap.getSkill()[0], subjectMap.getSkill()[1], subjectMap.getSkill()[2], subjectMap.getSkill()[3], colors);
                                    getCounts(subjectid);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            }, new Action() {
                                @Override
                                public void run() throws Exception {
//                            hideProgressBar(bindingPager);
                                }
                            });
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

            if (subjectExt == null) {
                subjectExt = getDefaultSubject();
            }
//            int iconId = 0;
//            if (subject.getSubjectIds() != null && subject.getSubjectIds().size() > 0) {
//                for (String id :
//                        subject.getSubjectIds()) {
//                    PrefManager.SubjectExt ext = mSubjectMap.get(id);
//                    if (ext != null) {
//                        iconId = ext.getIconWhiteId();
//                        break;
//                    }
//                }
//            }
//            if (iconId == 0) {
//                iconId = subjectExt.getIconWhiteId();
//            }
//            int color = subjectExt.getTextColor();
//            Picasso.with(getActivity()).load(iconId).into(holder.mbinding.imageViewSubjectIcon);
//            holder.mbinding.imageViewSubjectIcon.setBackgroundColor(getResources().getColor(R.color.color2_foreground));
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

    private void drawCircle(double mk, int cr, int tQ, String subject, int i, int j, int k, int m, int color[]) {
        String mval = String.format("%.2f", mk);
        mBinding.layoutFirst.learningMapSkillText1.setText(i + " Skills " + getResources().getString(R.string.studentAboveAverage));
        mBinding.layoutFirst.learningMapSkillText2.setText(j + " Skills " + getResources().getString(R.string.studentAverage));
        mBinding.layoutFirst.learningMapSkillText3.setText(k + " Skills " + getResources().getString(R.string.studentBelowAverage));
        mBinding.layoutFirst.learningMapSkillText4.setText(m + " Skills " + getResources().getString(R.string.studentUnAttempted));
        mBinding.layoutFirst.learningMapSkillImage1.setBackgroundColor(cr);
        mBinding.layoutFirst.learningMapSkillImage2.setBackgroundColor(cr);
        mBinding.layoutFirst.learningMapSkillImage2.setAlpha(.6f);
        mBinding.layoutFirst.learningMapSkillImage3.setBackgroundColor(cr);
        mBinding.layoutFirst.learningMapSkillImage3.setAlpha(.3f);
        mBinding.layoutFirst.learningMapSkillImage4.setBackgroundColor(Color.GRAY);
        mBinding.layoutFirst.learningMapSkillImage4.setAlpha(.3f);
        float a = i;
        float b = j;
        float c = k;
        float d = m;
        ArrayList<Entry> yvalues = new ArrayList<>();
        yvalues.add(new Entry(a, 0));
        yvalues.add(new Entry(b, 1));
        yvalues.add(new Entry(c, 2));
        yvalues.add(new Entry(d, 3));
        PieDataSet dataSet = new PieDataSet(yvalues, "");
        dataSet.setColors(color);
        dataSet.setValueTextSize(0f);
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        //xVals.add(subject);
        //}
        PieData data = new PieData(xVals, dataSet);
        mBinding.layoutFirst.chart1.setData(data);
        mBinding.layoutFirst.chart1.setHoleRadius(90f);
        mBinding.layoutFirst.chart1.setDrawHoleEnabled(true);
        mBinding.layoutFirst.chart1.setUsePercentValues(false);
        mBinding.layoutFirst.chart1.setDescription("");
        mBinding.layoutFirst.chart1.setDrawCenterText(true);
        mBinding.layoutFirst.chart1.setCenterTextColor(color[0]);
        int val = getResources().getInteger(R.integer.learning_map_progress_text_size);
        mBinding.layoutFirst.chart1.setCenterTextSize(val);
        float progress = i * 100 / (i + j + k + m);
        mBinding.layoutFirst.chart1.setCenterText(String.valueOf(new DecimalFormat("##.##").format(progress)) + "%");
        Legend l = mBinding.layoutFirst.chart1.getLegend();
        l.setEnabled(false);
        mBinding.layoutFirst.chart1.invalidate();
        mBinding.layoutFirst.chart1.setClickable(false);
        mBinding.layoutFirst.chart1.setTouchEnabled(false);
        mBinding.layoutFirst.chart1.animateXY(1400, 1400);
    }

    public ProgressDialog progressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_progress_bar, null);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }

        return dialog;
    }
}
