package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutTopicPractiseItemBinding;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.quizpreview.activity.PracticeTopicActivity;
import in.securelearning.lil.android.quizpreview.activity.QuestionPlayerActivity;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TopicListActivity extends AppCompatActivity {
    LayoutRecyclerViewBinding mBinding;
    @Inject
    HomeModel mHomeModel;
    public static final String GRADE_ID = "gradeId";
    public static final String SUBJECT_ID = "subjectId";
    public static final String SUBJECT_NAME = "subjectName";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_recycler_view);
        mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorBackground));
        handleIntent();
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

    private void setUpToolbar(String subjectName) {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!android.text.TextUtils.isEmpty(subjectName)) {
            setTitle(subjectName);
        } else {
            setTitle(getString(R.string.labelSelectTopic));
        }
    }

    public static Intent getStartIntent(Context context, String gradeId, ArrayList<String> subjectId, String subjectName) {
        Intent intent = new Intent(context, TopicListActivity.class);
        intent.putExtra(GRADE_ID, gradeId);
        intent.putExtra(SUBJECT_ID, subjectId);
        intent.putExtra(SUBJECT_NAME, subjectName);
        return intent;
    }

    private void handleIntent() {
        if (getIntent() != null) {
            String gradeId = getIntent().getStringExtra(GRADE_ID);
            ArrayList<String> subjectIds = getIntent().getStringArrayListExtra(SUBJECT_ID);
            String subjectName = getIntent().getStringExtra(SUBJECT_NAME);
            setUpToolbar(subjectName);
            fetchTopicList(gradeId, subjectIds);
        } else {
            finish();
            Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchTopicList(String gradeId, ArrayList<String> subjectIds) {
        mHomeModel.getCurriculumList(gradeId, subjectIds, "").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Curriculum>>() {
                    @Override
                    public void accept(ArrayList<Curriculum> list) throws Exception {
                        if (list != null && !list.isEmpty()) {
                            initializeRecyclerView(list);
                        } else {
                            finish();
                            Toast.makeText(getBaseContext(), getString(R.string.messageNoTopicsFound), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void initializeRecyclerView(ArrayList<Curriculum> list) {
        mBinding.list.setVisibility(View.VISIBLE);
        mBinding.list.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(list);
        mBinding.list.setAdapter(adapter);
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private ArrayList<Curriculum> mList;

        public RecyclerViewAdapter(ArrayList<Curriculum> list) {
            this.mList = list;
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutTopicPractiseItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_topic_practise_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
            final Curriculum curriculum = mList.get(position);
            holder.mBinding.textViewTopicName.setText(curriculum.getTopic().getName());
            holder.mBinding.buttonPractice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        try {
                            startActivity(PracticeTopicActivity.getStartIntentForSkills(getBaseContext(), curriculum.getSkills(), curriculum.getTopic().getName(), getString(R.string.label_low), getSkillMap(curriculum)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getBaseContext(), v);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {

            return mList.size();

        }

        private HomeModel.SkillMap getSkillMap(Curriculum curriculum) {
            HomeModel.SkillMap skillMap = new HomeModel.SkillMap();
            skillMap.setBoard(curriculum.getBoard());
            skillMap.setGrade(curriculum.getGrade());
            skillMap.setLanguage(curriculum.getLang());
            skillMap.setLearningLevel(curriculum.getLearningLevel());
            skillMap.setTopic(curriculum.getTopic());
            skillMap.setSubject(curriculum.getSubject());
            return skillMap;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutTopicPractiseItemBinding mBinding;

            public ViewHolder(LayoutTopicPractiseItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

}
