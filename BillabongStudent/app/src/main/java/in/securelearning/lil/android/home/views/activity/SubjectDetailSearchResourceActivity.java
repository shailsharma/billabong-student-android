package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivitySubjectDetailSearchBinding;
import in.securelearning.lil.android.app.databinding.ItemImageBinding;
import in.securelearning.lil.android.app.databinding.SearchDetailListRowItemBinding;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.CurriculumModel;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.resources.view.activity.YoutubePlayActivity;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResourcesResults;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;


public class SubjectDetailSearchResourceActivity extends AppCompatActivity {
    private static final String SUBJECT_ID = "subject_id";
    private static final String SUBJECT_DETAILS = "subject_detail";
    private static final String SUBJECT_COLOR = "subject_color";
    private static final String SUBJECT_NAME = "subject_name";
    ActivitySubjectDetailSearchBinding mBinding;
    private RecyclerView.Adapter mAdapter;
    String mSubjectId = null;
    String mSubjectName = null;
    int mSubjectColor = Color.BLACK;
    @Inject
    CurriculumModel mCurriculumModel;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    AppUserModel mAppUserModel;
    int mSkip = 0;
    int mLimit = 10;
    String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_subject_detail_search);
        handleIntent();
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        setTitle(mSubjectName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getDataFromCurriculam();
        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressBar() {
        mBinding.laySubjectList.setVisibility(View.GONE);
        mBinding.layoutNoResult.setVisibility(View.GONE);
        mBinding.layProgressbar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mBinding.layProgressbar.setVisibility(View.GONE);
    }

    private void getData() {
        showProgressBar();
        Grade grade = mAppUserModel.getApplicationUser().getGrade();
        String gradeId = "";
        if (grade != null && !TextUtils.isEmpty(grade.getId())) {
            gradeId = grade.getId();
            mBinding.layoutFilter.setVisibility(View.GONE);
            identifyOperation(gradeId);
        } else {
            mBinding.layoutFilter.setVisibility(View.VISIBLE);
            fillSpinnerList();
        }

    }

    private void fillSpinnerList() {
        final ArrayList<Grade> gradeList = PrefManager.getGradeList(SubjectDetailSearchResourceActivity.this);
        if (gradeList != null && gradeList.size() > 0) {
            List<String> spinnerArray = new ArrayList<>();
            for (int i = 0; i < gradeList.size(); i++) {
                spinnerArray.add( gradeList.get(i).getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mBinding.spinnerGradeList.setAdapter(adapter);
            mBinding.spinnerGradeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    identifyOperation(gradeList.get(position).getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void identifyOperation(final String gradeId) {
        showProgressBar();
        Observable.create(new ObservableOnSubscribe<ArrayList<Curriculum>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Curriculum>> e) throws Exception {
                ArrayList<Curriculum> mCurriculamData = mCurriculumModel.getCurriculumList(gradeId, mSubjectId, "", mSkip, mLimit);
                if (mCurriculamData != null) {
                    e.onNext(mCurriculamData);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Curriculum>>() {
                    @Override
                    public void accept(ArrayList<Curriculum> curricula) throws Exception {
                        if (curricula.size() > 0) {
                            setUpRecycleView(curricula);
                        } else {
                            hideProgressBar();
                            mBinding.laySubjectList.setVisibility(View.GONE);
                            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mBinding.txtNoResult.setText(getString(R.string.technical_problem));
                    }
                });
    }

    private void getDataFromCurriculam() {
        showProgressBar();
        Observable.create(new ObservableOnSubscribe<ArrayList<Curriculum>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Curriculum>> e) throws Exception {
                Grade grade = mAppUserModel.getApplicationUser().getGrade();
                String gradeId = "";
                if (grade != null && !TextUtils.isEmpty(grade.getId())) {
                    gradeId = grade.getId();
                }
                ArrayList<Curriculum> mCurriculamData = mCurriculumModel.getCurriculumList(gradeId, mSubjectId, "", mSkip, mLimit);
                if (mCurriculamData != null) {
                    e.onNext(mCurriculamData);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Curriculum>>() {
                    @Override
                    public void accept(ArrayList<Curriculum> curricula) throws Exception {
                        if (curricula.size() > 0) {
                            setUpRecycleView(curricula);
                        } else {
                            hideProgressBar();
                            mBinding.laySubjectList.setVisibility(View.GONE);
                            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mBinding.txtNoResult.setText(getString(R.string.technical_problem));
                    }
                });
    }

    private void handleIntent() {
        Bundle appData = getIntent().getBundleExtra(SUBJECT_DETAILS);
        if (appData != null) {
            mSubjectId = appData.getString(SUBJECT_ID);
            mSubjectName = appData.getString(SUBJECT_NAME);
            mSubjectColor = appData.getInt(SUBJECT_COLOR);
        }
    }

    private void setUpRecycleView(ArrayList<Curriculum> curricula) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.subjectTopicList.setLayoutManager(mLayoutManager);
        // here we check if topic have course or not , if topic have course then add in list otherwise romove it from list
        checkTopicCourse(curricula);
    }

    private void checkTopicCourse(final ArrayList<Curriculum> curricula) {
        ArrayList<String> subjectList = new ArrayList<>();
        final List<TopicDataObject> topicData = new ArrayList<>();
        subjectList.add(mSubjectId);
        Observable.create(new ObservableOnSubscribe<TopicDataObject>() {
            @Override
            public void subscribe(ObservableEmitter<TopicDataObject> e) throws Exception {
                String gradeId;
                ArrayList<String> subjectIds;
                String subjectName;
                for (int i = 0; i < curricula.size(); i++) {
                    topicId = curricula.get(i).getTopic().getId();
                    gradeId = curricula.get(i).getGrade().getId();
                    subjectIds = curricula.get(i).getSubject().getSubjectIds();
                    subjectName = curricula.get(i).getSubject().getName();
                    ArrayList<FavouriteResource> list = new ArrayList<>();
                    ArrayList<String> subjectList = new ArrayList<>();
                    subjectList.add(mSubjectId);
                    Call<SearchResourcesResults> call = mNetworkModel.getRecommendedResources(subjectList, topicId, gradeId, mSkip, mLimit);
                    Response<SearchResourcesResults> response = call.execute();
                    if (response != null && response.isSuccessful() && response.body().getList().size() > 0) {
                        list = response.body().getList();
                        TopicDataObject object = new TopicDataObject(topicId, curricula.get(i).getTopic().getName(), gradeId, subjectIds, subjectName);
                        object.setTopicResourceList(list);
                        e.onNext(object);
                    }
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<TopicDataObject>() {
            @Override
            public void accept(TopicDataObject object) throws Exception {
                if (object != null && object.getTopicResourceList().size() > 0) {
                    topicData.add(object);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                mBinding.laySubjectList.setVisibility(View.GONE);
                mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                hideProgressBar();
            }

        }, new Action() {
            @Override
            public void run() throws Exception {
                // here we check if given topicId have course data or not, if it contains then we add it in main list otherwise remove it from main list
                if (topicData.size() > 0) {
                    mAdapter = new TopicDetilsAdapter(topicData);
                    mBinding.subjectTopicList.setAdapter(mAdapter);
                    mBinding.layoutNoResult.setVisibility(View.GONE);
                    mBinding.laySubjectList.setVisibility(View.VISIBLE);
                } else {
                    mBinding.laySubjectList.setVisibility(View.GONE);
                    mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                }
                hideProgressBar();
            }
        });
    }

    public static Intent getIntent(Context context, String subjectId, int subjectColor, String subjectName) {
        Intent intent = new Intent(context, SubjectDetailSearchResourceActivity.class);
        Bundle appData = new Bundle();
        appData.putString(SUBJECT_ID, subjectId);
        appData.putString(SUBJECT_NAME, subjectName);
        appData.putInt(SUBJECT_COLOR, subjectColor);
        intent.putExtra(SUBJECT_DETAILS, appData);
        return intent;
    }

    private class TopicDetilsAdapter extends RecyclerView.Adapter<TopicDetilsAdapter.TopicViewHolder> {
        List<TopicDataObject> mTopicData;

        private TopicDetilsAdapter(List<TopicDataObject> topicList) {
            mTopicData = topicList;
        }


        @Override
        public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SearchDetailListRowItemBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.search_detail_list_row_item, parent, false);
            return new TopicViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return mTopicData.size();
        }

        @Override
        public void onBindViewHolder(TopicViewHolder holder, int position) {
            final String topicName = mTopicData.get(position).getTopicName();
            final String topicId = mTopicData.get(position).getTopicId();
            final String gradeId = mTopicData.get(position).getGradeId();
            final ArrayList<String> subjectIds = mTopicData.get(position).getSubjectIds();
            final String subjectName = mTopicData.get(position).getSubjectName();
            final boolean isResource = true;
            holder.mTopicListRowBinding.topicName.setText(topicName);
            holder.mTopicListRowBinding.txtViewAll.setBackgroundColor(mSubjectColor);
            holder.mTopicListRowBinding.txtViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int colCount = 1;
                    if (getResources().getBoolean(R.bool.isTablet)) {
                        colCount = 2;
                    }
                    if (GeneralUtils.isNetworkAvailable(SubjectDetailSearchResourceActivity.this)) {
//                        startActivity(NewSearchResourceFilterActivity.getStartIntent(getApplicationContext(), colCount, topicId, mSubjectId));

                        startActivity(ViewAllResoursesActivity.getIntent(getApplicationContext(), colCount, topicId, mSubjectId, gradeId, subjectIds, subjectName, topicName, isResource));
                    } else {
                        ToastUtils.showToastAlert(SubjectDetailSearchResourceActivity.this, getString(R.string.connect_internet));
                    }
                }
            });
            ArrayList<FavouriteResource> list;
            list = mTopicData.get(position).getTopicResourceList();
            addValuesToCourseRecyclerView(holder.mTopicListRowBinding.horizontalSkillList, list);

//            fetchTopicCourseData(topicId, topicName, holder.mTopicListRowBinding.horizontalSkillList, holder.mTopicListRowBinding.txtNoTopicCourseData, holder.mTopicListRowBinding);
        }

        private void fetchTopicCourseData(final String topicId, final String topicName, final RecyclerView horizontalSkillList, final TextView txtNoTopicCourseData, final SearchDetailListRowItemBinding topicListRowBinding) {
            Observable.create(new ObservableOnSubscribe<ArrayList<FavouriteResource>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<FavouriteResource>> e) throws Exception {
                    ArrayList<FavouriteResource> list = new ArrayList<>();
                    ArrayList<String> subjectList = new ArrayList<>();
                    subjectList.add(mSubjectId);
                    Call<SearchResourcesResults> call = mNetworkModel.getRecommendedResources(subjectList, topicId, "", mSkip, mLimit);
                    Response<SearchResourcesResults> response = call.execute();
                    if (response != null && response.isSuccessful() && response.body().getList().size() > 0) {
                        list = response.body().getList();
                    }
                    e.onNext(list);
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<FavouriteResource>>() {
                        @Override
                        public void accept(ArrayList<FavouriteResource> courseList) throws Exception {
                            if (courseList != null && courseList.size() > 0) {
                                addValuesToCourseRecyclerView(horizontalSkillList, courseList);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                        }
                    });
        }

        public class TopicViewHolder extends RecyclerView.ViewHolder {
            SearchDetailListRowItemBinding mTopicListRowBinding;

            private TopicViewHolder(SearchDetailListRowItemBinding view) {
                super(view.getRoot());
                mTopicListRowBinding = view;
            }
        }

        private void addValuesToCourseRecyclerView(RecyclerView horizontalSkillList, ArrayList<FavouriteResource> courseList) {
            horizontalSkillList.setNestedScrollingEnabled(false);
            horizontalSkillList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.HORIZONTAL, false));
            SkillAdapter adapter = new SkillAdapter(courseList, horizontalSkillList);
            horizontalSkillList.setAdapter(adapter);
        }

        class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.MySkillHolder> {
            ArrayList<FavouriteResource> mCourseList;
            RecyclerView mHRecyclerView;

            private SkillAdapter(ArrayList<FavouriteResource> courseList, RecyclerView horizontalSkillList) {
                this.mCourseList = courseList;
                this.mHRecyclerView = horizontalSkillList;
            }

            @Override
            public MySkillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ItemImageBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_image, parent, false);
                return new MySkillHolder(itemView);
            }

            @Override
            public void onBindViewHolder(MySkillHolder holder, int position) {
                final FavouriteResource courseData = mCourseList.get(position);
                holder.mItemImageBinding.txtTitle.setText(courseData.getTitle());
                String imageFilePath = courseData.getUrlThumbnail();
                try {
                    if (!imageFilePath.isEmpty()) {
                        Picasso.with(getBaseContext()).load(imageFilePath).into(holder.mItemImageBinding.imgItem);
                    } else {
                        Picasso.with(getBaseContext()).load(R.drawable.image_loading_thumbnail).into(holder.mItemImageBinding.imgItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.mItemImageBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubjectDetailSearchResourceActivity.this.startActivity(YoutubePlayActivity.getStartIntent(SubjectDetailSearchResourceActivity.this, courseData, ""));
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mCourseList.size();
            }

            public class MySkillHolder extends RecyclerView.ViewHolder {
                ItemImageBinding mItemImageBinding;

                private MySkillHolder(ItemImageBinding view) {
                    super(view.getRoot());
                    mItemImageBinding = view;
                }
            }
        }
    }
}