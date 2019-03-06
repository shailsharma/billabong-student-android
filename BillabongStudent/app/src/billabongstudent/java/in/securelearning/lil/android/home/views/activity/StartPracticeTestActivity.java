package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutSubjectItemBinding;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.PrefManagerStudentSubjectMapping;

import static in.securelearning.lil.android.syncadapter.utils.PrefManager.getDefaultSubject;
import static in.securelearning.lil.android.syncadapter.utils.PrefManager.getSubjectMap;

public class StartPracticeTestActivity extends AppCompatActivity {
    LayoutRecyclerViewBinding mBinding;
    public static final String GRADE_ID = "gradeId";
    private String mGradeId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_recycler_view);
        mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorBackground));
        setUpToolbar();
        handleIntent();
        initializeRecyclerView();
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

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.labelSelectSubject));
    }

    public static Intent getStartIntent(Context context, String gradeId) {
        Intent intent = new Intent(context, StartPracticeTestActivity.class);
        intent.putExtra(GRADE_ID, gradeId);
        return intent;
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mGradeId = getIntent().getStringExtra(GRADE_ID);
        } else {
            finish();
        }
    }

    private void initializeRecyclerView() {
        mBinding.list.setVisibility(View.VISIBLE);
        mBinding.list.setLayoutManager(new GridLayoutManager(getBaseContext(), 2, GridLayoutManager.VERTICAL, false));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(PrefManagerStudentSubjectMapping.getSubjectExtList(getBaseContext()), getSubjectMap(getBaseContext()));
        mBinding.list.setAdapter(adapter);

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private final HashMap<String, PrefManager.SubjectExt> mSubjectMap;
        private ArrayList<PrefManager.SubjectExt> mSubjects;


        public void clear() {
            if (mSubjectMap != null) {
                mSubjectMap.clear();
            }
            if (mSubjects != null) {
                mSubjects.clear();
            }
        }

        public RecyclerViewAdapter(ArrayList<PrefManager.SubjectExt> subjectList, HashMap<String, PrefManager.SubjectExt> subjectMap) {
            this.mSubjects = subjectList;
            this.mSubjectMap = subjectMap;
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutSubjectItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_subject_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
            final PrefManager.SubjectExt subject = mSubjects.get(position);
            PrefManager.SubjectExt subjectExt = mSubjectMap.get(subject.getId());
            if (subjectExt == null) {
                subjectExt = getDefaultSubject();
            }
            int iconId = 0;
            if (subject.getSubjectIds() != null && subject.getSubjectIds().size() > 0) {
                for (String id : subject.getSubjectIds()) {
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
            Picasso.with(getBaseContext()).load(iconId).into(holder.mBinding.imageViewSubjectIcon);
            holder.mBinding.imageViewSubjectIcon.setBackgroundColor(color);
            holder.mBinding.textViewSubjectName.setText(subject.getName());
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(TopicListActivity.getStartIntent(getBaseContext(), mGradeId, subject.getSubjectIds(),subject.getName()));
                }
            });
        }

        @Override
        public int getItemCount() {

            return mSubjects.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutSubjectItemBinding mBinding;

            public ViewHolder(LayoutSubjectItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }
}
