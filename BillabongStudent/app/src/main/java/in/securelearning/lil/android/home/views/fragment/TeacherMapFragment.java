package in.securelearning.lil.android.home.views.fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityLearningMapTeacherBinding;
import in.securelearning.lil.android.app.databinding.FragmentTeacherAnalysisForStudentBinding;
import in.securelearning.lil.android.app.databinding.StudentCategoryListRowBinding;
import in.securelearning.lil.android.app.databinding.StudentImageListRowBinding;
import in.securelearning.lil.android.base.dataobjects.CuratorMapping;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class TeacherMapFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ActivityLearningMapTeacherBinding mBinding;
    LearningMapPagerAdapter mAdapter;
    @Inject
    HomeModel mHomeModel;
    @Inject
    NetworkModel mNetworkModel;
    private static String ARG_COLUMN_COUNT = "column-count";
    public static final String SUBJECT_ID = "subject_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String DATE = "date";
    private int mColumnCount = 1;
    private String mSubjectId;
    private String mTopicId;
    private String mGradeId;
    private String mSectionId;
    private String mDate;

    public TeacherMapFragment() {
    }

    public static TeacherMapFragment newInstance() {
        return new TeacherMapFragment();
    }

    public static Fragment newInstance(int columnCount, String subjectId, String topicId, String gradeId, String sectionId, String date) {
        TeacherMapFragment fragment = new TeacherMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SUBJECT_ID, subjectId);
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putString(SECTION_ID, sectionId);
        args.putString(DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mSectionId = getArguments().getString(SECTION_ID);
            mDate = getArguments().getString(DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_learning_map_teacher, container, false);
        InjectorHome.INSTANCE.getComponent().inject(this);
        Observable.create(new ObservableOnSubscribe<ArrayList<CuratorMapping>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CuratorMapping>> e) throws Exception {

//                ArrayList<CuratorMapping> curatorDataList = mHomeModel.getTeacherMapCompleteList();
                ArrayList<CuratorMapping> curatorDataList = mHomeModel.getTeacherMapCompleteListFromNetwork();
                if (curatorDataList != null) {
                    e.onNext(curatorDataList);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<CuratorMapping>>() {
                    @Override
                    public void accept(ArrayList<CuratorMapping> groupData) throws Exception {
                        if (groupData != null) {
                            mAdapter = new LearningMapPagerAdapter(getContext(), groupData);
                            mBinding.learningMapPager.setAdapter(mAdapter);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        return mBinding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    class LearningMapPagerAdapter extends PagerAdapter {

        private LayoutInflater mInflater;
        Context mContext;
        ArrayList<CuratorMapping> mGroupArrayList;

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public LearningMapPagerAdapter(Context context, ArrayList<CuratorMapping> groupData) {
            this.mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mGroupArrayList = groupData;
        }

        @Override
        public float getPageWidth(int position) {
            if (position == getCount() - 1) {
                return 1f;
            }
            return 0.9f;
        }

        @Override
        public int getCount() {
            return mGroupArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final FragmentTeacherAnalysisForStudentBinding fBinding = DataBindingUtil.inflate(mInflater, R.layout.fragment_teacher_analysis_for_student, container, false);
            // TODO: 12-07-2017 get data from database/ API call

            if (GeneralUtils.isNetworkAvailable(getActivity())) {
                Observable.create(new ObservableOnSubscribe<ArrayList<HomeModel.StudentScore>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<HomeModel.StudentScore>> e) throws Exception {
                        String subjectId = mGroupArrayList.get(position).getSubject().getId();
                        String gradeId = mGroupArrayList.get(position).getGrade().getId();
                        String sectionId = mGroupArrayList.get(position).getSection().getId();
                        ArrayList<String> ids = new ArrayList<String>();
                        ids.add(sectionId);
                        final Call<ArrayList<HomeModel.StudentScore>> studentDataCall = mNetworkModel.getStudentMapData(ids, gradeId, sectionId, null);
                        final Response<ArrayList<HomeModel.StudentScore>> studentData = studentDataCall.execute();
                        if (studentData != null && studentData.isSuccessful()) {
                            e.onNext(studentData.body());
                            e.onComplete();
                        }
                    }
                })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<HomeModel.StudentScore>>() {
                            @Override
                            public void accept(ArrayList<HomeModel.StudentScore> groupData) throws Exception {
                                // TODO: 14-07-2017 colors array works only for 10 classes, after increase no of classes we have to look into
                                int[] colors = getActivity().getResources().getIntArray(R.array.subject_text_color);
                                int colorSize = colors.length;
                                if (groupData != null) {
                                    int high = 0;
                                    int medium = 0;
                                    int low = 0;
                                    double avergaeClassScroe = 0;
                                    ArrayList<String> hUrlList = new ArrayList<>();
                                    ArrayList<String> mUrlList = new ArrayList<>();
                                    ArrayList<String> lUrlList = new ArrayList<>();
                                    ArrayList<String> hUrlList1 = new ArrayList<>();
                                    ArrayList<String> mUrlList1 = new ArrayList<>();
                                    ArrayList<String> lUrlList1 = new ArrayList<>();
                                    int i;
                                    for (i = 0; i < groupData.size(); i++) {
                                        if (groupData.get(i).getThumbnail() != null) {
                                            avergaeClassScroe += groupData.get(i).getScore();
                                            if ((groupData.get(i).getScore() * 100) >= 60) {
                                                high += 1;
                                                hUrlList.add(groupData.get(i).getThumbnail().getThumb());
                                                hUrlList1.add(groupData.get(i).getName());
                                            } else if ((groupData.get(i).getScore() * 100) >= 36) {
                                                medium += 1;
                                                mUrlList.add(groupData.get(i).getThumbnail().getThumb());
                                                mUrlList1.add(groupData.get(i).getName());
                                            } else {
                                                low += 1;
                                                lUrlList.add(groupData.get(i).getThumbnail().getThumb());
                                                lUrlList1.add(groupData.get(i).getName());
                                            }
                                        }
                                    }

                                    avergaeClassScroe = (avergaeClassScroe * 100) / (i - 1);
                                    String className = mGroupArrayList.get(position).getGrade().getName() + " " + mGroupArrayList.get(position).getSection().getName() + " - " + mGroupArrayList.get(position).getSubject().getName();
                                    fBinding.titleName.setText("Class " + className);
                                    fBinding.learningMapProgressText1.setText(high + " Student " + getResources().getString(R.string.studentAboveAverage));
                                    fBinding.learningMapProgressText2.setText(medium + " Student " + getResources().getString(R.string.studentAverage));
                                    fBinding.learningMapProgressText3.setText(low + " Student " + getResources().getString(R.string.studentBelowAverage));
                                    fBinding.learningMapProgressImage1.setBackgroundColor(colors[position % colorSize]);
                                    fBinding.learningMapProgressImage2.setBackgroundColor(colors[position % colorSize]);
                                    fBinding.learningMapProgressImage2.setAlpha(.6f);
                                    fBinding.learningMapProgressImage3.setBackgroundColor(colors[position % colorSize]);
                                    fBinding.learningMapProgressImage3.setAlpha(.3f);
                                    int[] circleColor = new int[3];
                                    int red = Color.red(colors[position % colorSize]);
                                    int green = Color.green(colors[position % colorSize]);
                                    int blue = Color.blue(colors[position % colorSize]);
                                    circleColor[0] = Color.rgb(red, green, blue);
                                    circleColor[1] = Color.argb(153, red, green, blue);
                                    circleColor[2] = Color.argb(76, red, green, blue);
                                    drawCircle(fBinding.progressChart, high, medium, low, circleColor, avergaeClassScroe);
                                    fillRecycleListForTopic(fBinding.learningMapProgressRecycleList, hUrlList, mUrlList, lUrlList, hUrlList1, mUrlList1, lUrlList1);
                                    container.addView(fBinding.getRoot());
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });
            } else {
                ToastUtils.showToastAlert(getActivity(), getString(R.string.connect_internet));
            }
            return fBinding.getRoot();
        }

        private void fillRecycleListForTopic(RecyclerView learningMapProgressRecycleList, ArrayList<String> highStudentMap, ArrayList<String> mediumStudentMap, ArrayList<String> lowStudentMap, ArrayList<String> hUrlList1, ArrayList<String> mUrlList1, ArrayList<String> lUrlList1) {
            learningMapProgressRecycleList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            StudentProgressAdapter adapter = new StudentProgressAdapter(highStudentMap, mediumStudentMap, lowStudentMap, hUrlList1, mUrlList1, lUrlList1);
            learningMapProgressRecycleList.setAdapter(adapter);
        }

        private void drawCircle(PieChart subjectChart, int i, int j, int k, int[] colors, double avergaeClassScroe) {
            float a = i;
            float b = j;
            float c = k;
            ArrayList<Entry> yvalues = new ArrayList<>();
            yvalues.add(new Entry(a, 0));
            yvalues.add(new Entry(b, 1));
            yvalues.add(new Entry(c, 2));
            PieDataSet dataSet = new PieDataSet(yvalues, "");
            dataSet.setColors(colors);
            dataSet.setValueTextSize(0f);
            ArrayList<String> xVals = new ArrayList<>();
            xVals.add("");
            xVals.add("");
            xVals.add("");
            PieData data = new PieData(xVals, dataSet);
            subjectChart.setData(data);
            subjectChart.setHoleRadius(90f);
            subjectChart.setDrawHoleEnabled(true);
            subjectChart.setUsePercentValues(false);
            subjectChart.setDescription("");
            subjectChart.setDrawCenterText(true);
            subjectChart.setCenterTextColor(colors[0]);
            int val = getResources().getInteger(R.integer.learning_map_progress_text_size);
            subjectChart.setCenterTextSizePixels(val);
            subjectChart.setCenterText(String.format("%.0f", avergaeClassScroe));
            Legend l = subjectChart.getLegend();
            l.setEnabled(false);
            subjectChart.invalidate();
            subjectChart.setClickable(false);
            subjectChart.setTouchEnabled(false);
        }


        private class StudentProgressAdapter extends RecyclerView.Adapter<StudentProgressAdapter.MyViewHolder> {
            ArrayList<String> mHighStudentMap = new ArrayList<>();
            ArrayList<String> mMediumStudentMap = new ArrayList<>();
            ArrayList<String> mLowStudentMap = new ArrayList<>();
            ArrayList<String> mHighStudentMap1 = new ArrayList<>();
            ArrayList<String> mMediumStudentMap1 = new ArrayList<>();
            ArrayList<String> mLowStudentMap1 = new ArrayList<>();

            public StudentProgressAdapter(ArrayList<String> highStudentMap, ArrayList<String> mediumStudentMap, ArrayList<String> lowStudentMap, ArrayList<String> hUrlList1, ArrayList<String> mUrlList1, ArrayList<String> lUrlList1) {
                mHighStudentMap = highStudentMap;
                mMediumStudentMap = mediumStudentMap;
                mLowStudentMap = lowStudentMap;
                mHighStudentMap1 = hUrlList1;
                mMediumStudentMap1 = mUrlList1;
                mLowStudentMap1 = lUrlList1;
            }

            @Override
            public StudentProgressAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                StudentCategoryListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.student_category_list_row, parent, false);
                return new StudentProgressAdapter.MyViewHolder(binding);
            }

            @Override
            public void onBindViewHolder(StudentProgressAdapter.MyViewHolder holder, int position) {
                if (position == 0) {
                    if (mHighStudentMap.size() > 0) {
                        holder.scBinding.studentCategoryName.setText(getResources().getString(R.string.studentAboveAverage) + " student");
                        drawStudentCategoryMap(holder.scBinding.horizontalStudentList, mHighStudentMap, mHighStudentMap1);
                    } else {
                        holder.scBinding.studentCategoryName.setVisibility(View.GONE);
                    }
                } else if (position == 1) {
                    if (mMediumStudentMap.size() > 0) {
                        holder.scBinding.studentCategoryName.setText(getResources().getString(R.string.studentAverage) + " student");
                        drawStudentCategoryMap(holder.scBinding.horizontalStudentList, mMediumStudentMap, mMediumStudentMap1);
                    } else {
                        holder.scBinding.studentCategoryName.setVisibility(View.GONE);
                    }

                } else if (position == 2) {
                    if (mLowStudentMap.size() > 0) {
                        holder.scBinding.studentCategoryName.setText(getResources().getString(R.string.studentBelowAverage) + " student");
                        drawStudentCategoryMap(holder.scBinding.horizontalStudentList, mLowStudentMap, mLowStudentMap1);
                    } else {
                        holder.scBinding.studentCategoryName.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }

            public class MyViewHolder extends RecyclerView.ViewHolder {
                StudentCategoryListRowBinding scBinding;

                public MyViewHolder(StudentCategoryListRowBinding view) {
                    super(view.getRoot());
                    scBinding = view;
                }
            }

            private void drawStudentCategoryMap(RecyclerView horizontalStudentList, ArrayList<String> values, ArrayList<String> lowStudentMap) {
                horizontalStudentList.setHasFixedSize(true);
                horizontalStudentList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                StudentCategoryAdapter adapter = new StudentCategoryAdapter(values, lowStudentMap);
                horizontalStudentList.setAdapter(adapter);
            }
        }
    }


    class StudentCategoryAdapter extends RecyclerView.Adapter<StudentCategoryAdapter.StudentCategoryHolder> {
        ArrayList<String> urlList = new ArrayList<>();
        ArrayList<String> urlList1 = new ArrayList<>();

        public StudentCategoryAdapter(ArrayList<String> values, ArrayList<String> lowStudentMap) {
            urlList = values;
            urlList1 = lowStudentMap;
        }

        @Override
        public StudentCategoryAdapter.StudentCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            StudentImageListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.student_image_list_row, parent, false);
            binding.layoutStudentScore.setVisibility(View.GONE);
            return new StudentCategoryAdapter.StudentCategoryHolder(binding);
        }

        @Override
        public void onBindViewHolder(StudentCategoryAdapter.StudentCategoryHolder holder, int position) {
            String thumbnailPath = urlList.get(position);
            holder.slBinding.studentName.setText(urlList1.get(position));
            try {
                if (!TextUtils.isEmpty(thumbnailPath)) {
                    Picasso.with(getActivity().getBaseContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).into(holder.slBinding.student);
                } else {
                    Picasso.with(getActivity().getBaseContext()).load(R.drawable.icon_profile_large).resize(300, 300).centerInside().into(holder.slBinding.student);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getActivity().getBaseContext()).load(thumbnailPath).placeholder(R.drawable.gradient_black_bottom).resize(300, 300).centerInside().into(holder.slBinding.student);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getActivity().getBaseContext()).load(R.drawable.icon_profile_large).resize(300, 300).centerInside().into(holder.slBinding.student);

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return urlList.size();
        }

        public class StudentCategoryHolder extends RecyclerView.ViewHolder {
            StudentImageListRowBinding slBinding;

            public StudentCategoryHolder(StudentImageListRowBinding view) {
                super(view.getRoot());
                slBinding = view;
            }
        }
    }
}


