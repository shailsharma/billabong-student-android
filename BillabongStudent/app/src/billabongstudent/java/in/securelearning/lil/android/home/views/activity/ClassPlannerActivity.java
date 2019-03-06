package in.securelearning.lil.android.home.views.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutClassPlannerBinding;
import in.securelearning.lil.android.app.databinding.LayoutClassPlannerPeriodItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutClassPlannerReferanceItemBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.dataobjects.TopicSuper;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.learningnetwork.views.activity.PostListActivity;
import in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.resources.view.activity.VideoPlayActivity;
import in.securelearning.lil.android.resources.view.activity.VimeoActivity;
import in.securelearning.lil.android.resources.view.activity.YoutubePlayActivity;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResourcesResults;
import in.securelearning.lil.android.syncadapter.masterdata.PracticeLevelListData;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 02-Apr-18.
 */

public class ClassPlannerActivity extends AppCompatActivity {
    @Inject
    HomeModel mHomeModel;
    @Inject
    NetworkModel mNetworkModel;
    LayoutClassPlannerBinding mBinding;
    private ArrayList<String> mSubjectIds = new ArrayList<>();
    private String mTopicId, mGradeId;

    private int mCourseTotalResultCount = 0;
    private int mCourseCurrentResultCount = 0;
    private int mDefaultCount = 20;
    private int mPreResourceTotalResultCount = 0;
    private int mPreResourceCurrentResultCount = 0;
    private int mRefResourceTotalResultCount = 0;
    private int mRefResourceCurrentResultCount = 0;
    PrescribedResourcesAdapter mPrescribedResourcesAdapter;
    CourseAndLessonPlanAdapter mCourseAndLessonPlanAdapter;
    ReferenceResourcesAdapter mReferenceResourcesAdapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_class_planner);
        initializeToolbar();
        setDateOnToolbar(DateUtils.getFormatedDateFromDate(new Date()));
        fetchPeriodicEvents(Calendar.getInstance().getTime());
        initializeCourseAndLessonPlanRecyclerView(new ArrayList<AboutCourseExt>());
        initializePrescribedResourcesRecyclerView(new ArrayList<FavouriteResource>());
        initializeReferenceResourcesRecyclerView(new ArrayList<FavouriteResource>());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class_planner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.actionCalendar:
                showDatePickerDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void setDateOnToolbar(String date) {
        setTitle(date);

    }

    private void initializeToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorClassPlannerPrimary));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorClassPlannerPrimary)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, ClassPlannerActivity.class);
        return intent;
    }

    private void initializePeriodRecyclerView(ArrayList<PeriodNew> periods) {
        mBinding.recyclerViewClass.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        PeriodAdapter periodAdapter = new PeriodAdapter(periods, PrefManager.getSubjectMap(getBaseContext()));
        mBinding.recyclerViewClass.setAdapter(periodAdapter);
        if (periods != null && !periods.isEmpty()) {
            loadDataForSelectedPeriod(periods.get(0));
        }

    }

    private void initializeReferenceResourcesRecyclerView(ArrayList<FavouriteResource> list) {
        LinearLayoutManager layoutManager = null;
        layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerViewReferenceResources.setLayoutManager(layoutManager);
        mReferenceResourcesAdapter = new ReferenceResourcesAdapter(list);
        mBinding.recyclerViewReferenceResources.setAdapter(mReferenceResourcesAdapter);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.recyclerViewReferenceResources.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mRefResourceCurrentResultCount - 1) {

                            if (mRefResourceCurrentResultCount < mRefResourceTotalResultCount) {
                                fetchReferenceResources(mSubjectIds, mTopicId, mGradeId, mRefResourceTotalResultCount, mDefaultCount);

                            }
                        }
                    }
                }
            });
        }
    }

    private void initializePrescribedResourcesRecyclerView(ArrayList<FavouriteResource> list) {
        LinearLayoutManager layoutManager = null;
        layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerViewPrescribedResources.setLayoutManager(layoutManager);
        mPrescribedResourcesAdapter = new PrescribedResourcesAdapter(list);
        mBinding.recyclerViewPrescribedResources.setAdapter(mPrescribedResourcesAdapter);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.recyclerViewPrescribedResources.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPreResourceCurrentResultCount - 1) {

                            if (mPreResourceCurrentResultCount < mPreResourceTotalResultCount) {
                                fetchPrescribedResources(mSubjectIds, mTopicId, mGradeId, mPreResourceTotalResultCount, mDefaultCount);

                            }
                        }
                    }
                }
            });
        }
    }

    private void initializeCourseAndLessonPlanRecyclerView(ArrayList<AboutCourseExt> list) {
        LinearLayoutManager layoutManager = null;
        layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerViewCourseAndLessonPlan.setLayoutManager(layoutManager);
        mCourseAndLessonPlanAdapter = new CourseAndLessonPlanAdapter(list);
        mBinding.recyclerViewCourseAndLessonPlan.setAdapter(mCourseAndLessonPlanAdapter);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;
            mBinding.recyclerViewCourseAndLessonPlan.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mCourseCurrentResultCount - 1) {

                            if (mCourseCurrentResultCount < mCourseTotalResultCount) {
                                fetchCourseAndLessonPlan(mSubjectIds, mTopicId, mGradeId, mCourseCurrentResultCount, mDefaultCount);

                            }
                        }
                    }
                }
            });
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();

        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(ClassPlannerActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat("MM dd, yyyy");
                setDateOnToolbar(DateUtils.getFormatedDateFromDate(calendar.getTime()));
                fetchPeriodicEvents(calendar.getTime());
            }
        }, yy, mm, dd);
        datePicker.show();
    }

    private void fetchPeriodicEvents(Date date) {

        final long startSecond = DateUtils.getSecondsForMorningFromDate(date);
        final long endSecond = DateUtils.getSecondsForMidnightFromDate(date);

        mHomeModel.getPeriodOfSelectedDate(startSecond, endSecond).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PeriodNew>>() {
            @Override
            public void accept(ArrayList<PeriodNew> periodNew) throws Exception {
                if (periodNew != null && !periodNew.isEmpty()) {
                    mBinding.layoutContent.setVisibility(View.VISIBLE);
                    mBinding.layoutContent.setVisibility(View.VISIBLE);
                    initializePeriodRecyclerView(periodNew);
                } else {
                    mBinding.layoutContent.setVisibility(View.GONE);
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

    private void fetchCourseAndLessonPlan(final ArrayList<String> subjectIds, final String topicId, final String gradeId, final int skip, final int limit) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            Observable.create(new ObservableOnSubscribe<ArrayList<AboutCourseExt>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<AboutCourseExt>> e) throws Exception {
                    ArrayList<AboutCourseExt> list = new ArrayList<>();
                    Call<SearchCoursesResults> call = mNetworkModel.getRecommendedCourses(subjectIds, topicId, gradeId, skip, limit, new String[]{"digitalbook", "videocourse", "conceptmap", "featuredcard"}, null);
                    Response<SearchCoursesResults> response = call.execute();
                    if (response != null && response.isSuccessful() && response.body().getList().size() > 0) {
                        list = response.body().getList();
                        mCourseCurrentResultCount += list.size();
                        mCourseTotalResultCount = response.body().getTotalResult();
                    }
                    e.onNext(list);
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<AboutCourseExt>>() {
                        @Override
                        public void accept(ArrayList<AboutCourseExt> videoList) throws Exception {
                            if (videoList != null && videoList.size() > 0) {
                                mCourseAndLessonPlanAdapter.addValues(videoList);
                                mBinding.layoutCourseAndLessonPlan.setVisibility(View.VISIBLE);
                            } else {
                                mBinding.layoutCourseAndLessonPlan.setVisibility(View.GONE);

                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();

                        }
                    });
        }


    }

    private void fetchPrescribedResources(final ArrayList<String> subjectIds, final String topicId, final String gradeId, final int skip, final int limit) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            Observable.create(new ObservableOnSubscribe<ArrayList<FavouriteResource>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<FavouriteResource>> e) throws Exception {
                    ArrayList<FavouriteResource> list = new ArrayList<>();
                    Call<SearchResourcesResults> call = mNetworkModel.getRecommendedResources(subjectIds, topicId, gradeId, skip, limit);
                    Response<SearchResourcesResults> response = call.execute();
                    if (response != null && response.isSuccessful() && response.body().getList().size() > 0) {
                        list = response.body().getList();
                        mPreResourceCurrentResultCount += list.size();
                        mPreResourceTotalResultCount = response.body().getTotalResult();
                    }
                    e.onNext(list);
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<FavouriteResource>>() {
                        @Override
                        public void accept(ArrayList<FavouriteResource> videoList) throws Exception {
                            if (videoList != null && videoList.size() > 0) {
                                mBinding.layoutPrescribedResources.setVisibility(View.VISIBLE);
                                mPrescribedResourcesAdapter.addValues(videoList);
                            } else {
                                mBinding.layoutPrescribedResources.setVisibility(View.GONE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();

                        }
                    });
        }


    }

    private void fetchReferenceResources(final ArrayList<String> subjectIds, final String topicId, final String gradeId, final int skip, final int limit) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            Observable.create(new ObservableOnSubscribe<ArrayList<FavouriteResource>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<FavouriteResource>> e) throws Exception {
                    ArrayList<FavouriteResource> list = new ArrayList<>();
                    Call<SearchResourcesResults> call = mNetworkModel.getRecommendedResources(subjectIds, topicId, gradeId, skip, limit);
                    Response<SearchResourcesResults> response = call.execute();
                    if (response != null && response.isSuccessful() && response.body().getList().size() > 0) {
                        list = response.body().getList();
                        mRefResourceCurrentResultCount += list.size();
                        mRefResourceTotalResultCount = response.body().getTotalResult();
                    }
                    e.onNext(list);
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<FavouriteResource>>() {
                        @Override
                        public void accept(ArrayList<FavouriteResource> videoList) throws Exception {
                            if (videoList != null && videoList.size() > 0) {
                                mBinding.layoutReferenceResources.setVisibility(View.VISIBLE);
                                mReferenceResourcesAdapter.addValues(videoList);
                            } else {
                                mBinding.layoutReferenceResources.setVisibility(View.GONE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();

                        }
                    });
        }


    }

    private void setGlobalDefault() {
        mSubjectIds.clear();
        mTopicId = "";
        mGradeId = "";
    }

    private void loadDataForSelectedPeriod(PeriodNew period) {
        setGlobalDefault();
        mSubjectIds.add(period.getSubject().getId());
        if (period.getSubjectIds() != null) {
            mSubjectIds.addAll(period.getSubjectIds());
        }
        mTopicId = period.getTopic().getId();
        mGradeId = period.getGrade().getId();
        setTopicSpinner(period.getGrade().getId(), mSubjectIds, period.getTopic());
        setLNAndLm(period.getSubject().getId(), mSubjectIds, period.getTopic().getId(), period.getGrade().getId(), period.getSection().getId(), DateUtils.getCurrentISO8601DateString());
    }

    private void setTopicSpinner(final String gradeId, final ArrayList<String> subjectIds, final TopicSuper topic) {

        mHomeModel.getTopicListFromCurriculum(gradeId, subjectIds, topic.getId()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Topic>>() {
                    @Override
                    public void accept(final ArrayList<Topic> list) throws Exception {

                        if (list != null && !list.isEmpty()) {
                            mBinding.spinnerTopic.setVisibility(View.VISIBLE);
                            ArrayAdapter<Topic> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.layout_spinner, R.id.txt_spinner, list);
                            mBinding.spinnerTopic.setAdapter(adapter);
                            mBinding.spinnerTopic.setSelection(0);
                            mBinding.spinnerTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                                        mBinding.layoutCourseAndLessonPlan.setVisibility(View.GONE);
                                        mBinding.layoutPrescribedResources.setVisibility(View.GONE);
                                        mBinding.layoutReferenceResources.setVisibility(View.GONE);
                                        initializeCourseAndLessonPlanRecyclerView(new ArrayList<AboutCourseExt>());
                                        initializePrescribedResourcesRecyclerView(new ArrayList<FavouriteResource>());
                                        initializeReferenceResourcesRecyclerView(new ArrayList<FavouriteResource>());
                                        fetchCourseAndLessonPlan(subjectIds, list.get(position).getId(), gradeId, 0, mDefaultCount);
                                        fetchPrescribedResources(subjectIds, list.get(position).getId(), gradeId, 0, mDefaultCount);
                                        fetchReferenceResources(subjectIds, list.get(position).getId(), gradeId, 0, mDefaultCount);
                                    } else {
                                        SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutContent);
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            mBinding.spinnerTopic.setVisibility(View.INVISIBLE);
                            Toast.makeText(getBaseContext(), getString(R.string.messageNoTopicsFound), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void setLNAndLm(final String subjectId, final ArrayList<String> subjectIds, final String topicId, final String gradeId, final String sectionId, final String date) {
        final String groupId = mHomeModel.getGroupId(gradeId, sectionId, subjectId);
        mBinding.buttonLearningNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PostListActivity.getIntentForPostList(getBaseContext(), groupId, false));
            }
        });

        mBinding.buttonLearningMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ClassPerformanceActivity.getStartIntent(getBaseContext(), 1, subjectId, subjectIds, topicId, gradeId, sectionId, date));
            }
        });

    }

    class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.ViewHolder> {
        private ArrayList<PeriodNew> mList;
        private final HashMap<String, PrefManager.SubjectExt> mSubjectMap;
        private int mLastSelectedPosition = -1;

        public PeriodAdapter(ArrayList<PeriodNew> periods, HashMap<String, PrefManager.SubjectExt> subjectMap) {
            mList = periods;
            this.mSubjectMap = subjectMap;
        }

        @Override
        public PeriodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutClassPlannerPeriodItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_class_planner_period_item, parent, false);
            return new PeriodAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final PeriodAdapter.ViewHolder holder, final int position) {
            final PeriodNew period = mList.get(position);
            checkIsBreak(period.isBreak(), holder.mBinding);
            HashMap<String, Integer> icons = new PracticeLevelListData().getAllIcons();
            int iconId = 0;
            if (icons.containsKey(period.getSubject().getId())) {
                iconId = icons.get(period.getSubject().getId());
            } else {
                if (period.getSubjectIds() != null) {
                    for (int i = 0; i < period.getSubjectIds().size(); i++) {
                        if (icons.containsKey(period.getSubjectIds().get(i))) {
                            iconId = icons.get(period.getSubjectIds().get(i));
                            break;
                        }
                    }
                }

            }

            if (iconId == 0) {
                iconId = R.drawable.white_default_course;
                holder.mBinding.imageViewPeriodIcon.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            }
            Picasso.with(getBaseContext()).load(iconId).into(holder.mBinding.imageViewPeriodIcon);

            holder.mBinding.textViewPeriodName.setText(period.getGrade().getName() + " " + period.getSection().getName());
            selectFirstItem(position, holder.mBinding.layoutIcon);
            holder.mBinding.layoutIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mLastSelectedPosition > -1) {
                        View layoutIcon = mBinding.recyclerViewClass.findViewHolderForAdapterPosition(mLastSelectedPosition).itemView.findViewById(R.id.layoutIcon);
                        if (layoutIcon != null) {
                            layoutIcon.setSelected(false);
                        }
                    }
                    mLastSelectedPosition = position;
                    view.setSelected(true);
                    loadDataForSelectedPeriod(period);

                }
            });
        }


        private void checkIsBreak(boolean isBreak, LayoutClassPlannerPeriodItemBinding binding) {
            if (isBreak) {
                binding.getRoot().setVisibility(View.GONE);
            }
        }

        private void selectFirstItem(final int position, LinearLayout layout) {
            if (position == 0) {
                layout.setSelected(true);
                mLastSelectedPosition = 0;
//                mBinding.recyclerViewClass.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mBinding.recyclerViewClass.findViewHolderForAdapterPosition(position).itemView.performClick();
//
//                    }
//                }, 800);

            } else {
                layout.setSelected(false);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutClassPlannerPeriodItemBinding mBinding;

            public ViewHolder(LayoutClassPlannerPeriodItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }


    private class CourseAndLessonPlanAdapter extends RecyclerView.Adapter<CourseAndLessonPlanAdapter.ViewHolder> {
        ArrayList<AboutCourseExt> mList;

        public CourseAndLessonPlanAdapter(ArrayList<AboutCourseExt> list) {
            mList = list;
        }

        @Override
        public CourseAndLessonPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutClassPlannerReferanceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_class_planner_referance_item, parent, false);
            return new CourseAndLessonPlanAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(CourseAndLessonPlanAdapter.ViewHolder holder, int position) {
            final AboutCourseExt object = mList.get(position);
            holder.mBinding.textViewTitle.setText(object.getTitle());
            setThumbnail(object, holder.mBinding.imageViewThumbnail);

            final Class finalObjectClass = getObjectClass(object);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            startActivity(RapidLearningSectionListActivity.getStartIntent(getBaseContext(), object.getObjectId()));
                        } else {
                            startActivity(CourseDetailActivity.getStartActivityIntent(getBaseContext(), object.getObjectId(), finalObjectClass, ""));

                        }
                    } else {
                        ToastUtils.showToastAlert(getBaseContext(), getString(R.string.connect_internet));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void addValues(ArrayList<AboutCourseExt> videoList) {
            if (mList != null) {
                mList.addAll(videoList);
                notifyDataSetChanged();
            }
        }

        private void setThumbnail(AboutCourseExt object, ImageView imageView) {
            String imagePath = object.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = object.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = object.getMetaInformation().getBanner();
            }
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getBaseContext()).load(imagePath).placeholder(R.drawable.image_loading_thumbnail).into(imageView);
                } else {
                    Picasso.with(getBaseContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(imageView);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getBaseContext()).load(object.getThumbnail().getThumb()).placeholder(R.drawable.image_loading_thumbnail).into(imageView);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getBaseContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(imageView);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        private Class getObjectClass(AboutCourseExt object) {
            String typeExt = object.getMicroCourseType().toLowerCase();
            if (object.getCourseType().equalsIgnoreCase("digitalbook")) {
                return DigitalBook.class;
            } else if (object.getCourseType().equalsIgnoreCase("videocourse")) {
                return VideoCourse.class;
            } else if (object.getCourseType().contains("feature")) {
                return MicroLearningCourse.class;
            } else if (typeExt.contains("map")) {
                return ConceptMap.class;
            } else if (typeExt.contains("interactiveimage")) {
                return InteractiveImage.class;
            } else if (typeExt.contains("video")) {
                return InteractiveVideo.class;
            } else {
                if (object.getPopUpType() != null && !TextUtils.isEmpty(object.getPopUpType().getValue())) {
                    return PopUps.class;
                } else {
                    return null;
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutClassPlannerReferanceItemBinding mBinding;

            public ViewHolder(LayoutClassPlannerReferanceItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class PrescribedResourcesAdapter extends RecyclerView.Adapter<PrescribedResourcesAdapter.ViewHolder> {
        ArrayList<FavouriteResource> mList;

        public PrescribedResourcesAdapter(ArrayList<FavouriteResource> list) {
            mList = list;
        }

        @Override
        public PrescribedResourcesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutClassPlannerReferanceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_class_planner_referance_item, parent, false);
            return new PrescribedResourcesAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(PrescribedResourcesAdapter.ViewHolder holder, int position) {
            final FavouriteResource favouriteResource = mList.get(position);
            holder.mBinding.textViewTitle.setText(favouriteResource.getTitle());
            try {
                Picasso.with(getBaseContext()).load(favouriteResource.getUrlThumbnail()).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(getBaseContext()).load(R.drawable.image_large).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
            }

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        if (favouriteResource.getType().equalsIgnoreCase("video")) {
                            startActivity(VideoPlayActivity.getStartIntent(getBaseContext(), favouriteResource));
                        } else if (favouriteResource.getType().equalsIgnoreCase("youtube#video")) {
                            startActivity(YoutubePlayActivity.getStartIntent(getBaseContext(), favouriteResource, ""));
                        } else if (favouriteResource.getType().equalsIgnoreCase("vimeo")) {
                            startActivity(VimeoActivity.getStartIntent(getBaseContext(), favouriteResource));
                        }
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getBaseContext(), view);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void addValues(ArrayList<FavouriteResource> videoList) {
            if (mList != null) {
                mList.addAll(videoList);
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutClassPlannerReferanceItemBinding mBinding;

            public ViewHolder(LayoutClassPlannerReferanceItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class ReferenceResourcesAdapter extends RecyclerView.Adapter<ReferenceResourcesAdapter.ViewHolder> {
        ArrayList<FavouriteResource> mList;

        public ReferenceResourcesAdapter(ArrayList<FavouriteResource> list) {
            mList = list;
        }

        @Override
        public ReferenceResourcesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutClassPlannerReferanceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_class_planner_referance_item, parent, false);
            return new ReferenceResourcesAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ReferenceResourcesAdapter.ViewHolder holder, int position) {
            final FavouriteResource favouriteResource = mList.get(position);
            holder.mBinding.textViewTitle.setText(favouriteResource.getTitle());
            try {
                Picasso.with(getBaseContext()).load(favouriteResource.getUrlThumbnail()).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(getBaseContext()).load(R.drawable.image_loading_thumbnail).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
            }

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        if (favouriteResource.getType().equalsIgnoreCase("video")) {
                            startActivity(VideoPlayActivity.getStartIntent(getBaseContext(), favouriteResource));
                        } else if (favouriteResource.getType().equalsIgnoreCase("youtube#video")) {
                            startActivity(YoutubePlayActivity.getStartIntent(getBaseContext(), favouriteResource, ""));
                        } else if (favouriteResource.getType().equalsIgnoreCase("vimeo")) {
                            startActivity(VimeoActivity.getStartIntent(getBaseContext(), favouriteResource));
                        }
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getBaseContext(), view);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void addValues(ArrayList<FavouriteResource> videoList) {
            if (mList != null) {
                mList.addAll(videoList);
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutClassPlannerReferanceItemBinding mBinding;

            public ViewHolder(LayoutClassPlannerReferanceItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
