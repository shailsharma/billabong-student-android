package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCalendarCategoryListBinding;
import in.securelearning.lil.android.app.databinding.LayoutPeriodicEventItemBinding;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.dataobjects.Category;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.widget.PeriodDetailPopUp;
import in.securelearning.lil.android.profile.views.activity.UserPublicProfileActivity;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 3/19/2017.
 */
public class CalendarPeriodListActivity extends AppCompatActivity {

    @Inject
    AppUserModel mAppUserModel;
    @Inject
    HomeModel mHomeModel;


    private PeriodAdapter mPeriodAdapter;
    private LayoutCalendarCategoryListBinding mBinding;
    private HashMap<String, Category> mSubjectMap;
    private String titleDate;

    public static Intent startCalendarPeriodActivity(Context context, long startSecond, long endSecond, String titleDate) {
        Intent intent = new Intent(context, CalendarPeriodListActivity.class);
        intent.putExtra("startSecond", startSecond);
        intent.putExtra("endSecond", endSecond);
        intent.putExtra("titleDate", titleDate);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_calendar_category_list);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPeriod));
        InjectorHome.INSTANCE.getComponent().inject(this);
        mSubjectMap = PeriodDetailPopUp.getSubjectMap(getBaseContext());

        long startSecond = getIntent().getLongExtra("startSecond", 0);
        long endSecond = getIntent().getLongExtra("endSecond", 0);
        titleDate = getIntent().getStringExtra("titleDate");


        initializeUIAndClickListeners();
        getData(startSecond, endSecond);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_calendar_period_list, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("CheckResult")
    private void getData(long startSecond, long endSecond) {

        mHomeModel.getPeriodOfSelectedDate(startSecond, endSecond)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<PeriodNew>>() {
                    @Override
                    public void accept(ArrayList<PeriodNew> periodNew) throws Exception {
                        initializeRecyclerView(periodNew);

                    }
                });
    }


    private void initializeUIAndClickListeners() {

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleDate = titleDate.replaceAll("\n", ", ");
        setTitle(titleDate);
        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPeriod));
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPeriod));

    }

    private void initializeRecyclerView(List<PeriodNew> mPeriodNewList) {
        mBinding.recyclerView.setPadding(10, 10, 10, 10);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        mPeriodAdapter = new PeriodAdapter(mPeriodNewList, getBaseContext());
        mPeriodAdapter.setHasStableIds(true);
        mBinding.recyclerView.setAdapter(mPeriodAdapter);
    }

    private class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.ViewHolder> {
        private List<PeriodNew> mPeriodAdapterList = new ArrayList<>();
        private Context mContext;
        private int mRandomColorPosition;
        private int[] mExtraColorArray;

        @Override
        public long getItemId(int position) {
            return mPeriodAdapterList.get(position).getObjectId().hashCode();
        }

        public PeriodAdapter(List<PeriodNew> mPeriodList, Context baseContext) {
            this.mContext = baseContext;
            this.mPeriodAdapterList = mPeriodList;
            this.mRandomColorPosition = 0;
            this.mExtraColorArray = getResources().getIntArray(R.array.subject_text_color_extra);
        }

        @NotNull
        @Override
        public PeriodAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            LayoutPeriodicEventItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_periodic_event_item, parent, false);
            return new PeriodAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NotNull final PeriodAdapter.ViewHolder holder, int position) {
            final PeriodNew period = mPeriodAdapterList.get(position);
            final boolean isBreak = period.isBreak();
//            setPeriodStatus(period, holder.mBinding);
            if (isBreak) {
                holder.mBinding.layoutBreak.setVisibility(View.VISIBLE);
                holder.mBinding.layoutPeriod.setVisibility(View.GONE);
                holder.mBinding.textViewBreak.setText(period.getName());
//                setPeriodTime(period, holder.mBinding.textViewBreakStartTime, holder.mBinding.textViewBreakEndTime);
                setPeriodTime(period, holder.mBinding.textViewBreakStartTime, null);
            } else {
                holder.mBinding.layoutBreak.setVisibility(View.GONE);
                holder.mBinding.layoutPeriod.setVisibility(View.VISIBLE);
                setUserView(period, holder.mBinding);
                setSubject(period, holder.mBinding.textViewPeriodSubject);
//                setPeriodTime(period, holder.mBinding.textViewPeriodStartTime, holder.mBinding.textViewPeriodEndTime);
                setPeriodTime(period, holder.mBinding.textViewPeriodStartTime, null);
                setPeriodNumber(period, holder.mBinding.textViewPeriodNumber);
                setTopic(period, holder.mBinding.textViewPeriodTopic);
                setSubTopic(period, holder.mBinding.textViewPeriodSubTopic);
            }

            if (!period.isBreak()) {
                holder.mBinding.layoutPeriod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String gradeName = "", sectionName = "", subjectName = "";
                        if (period.getGrade() != null && !TextUtils.isEmpty(period.getGrade().getName())) {
                            gradeName = period.getGrade().getName();
                        }
                        if (period.getSection() != null && !TextUtils.isEmpty(period.getSection().getName())) {
                            sectionName = period.getSection().getName();
                        }
                        if (period.getSubject() != null && !TextUtils.isEmpty(period.getSubject().getName())) {
                            subjectName = period.getSubject().getName();
                        }
                        String subjectId = "", topicId = "", topicName = "", gradeId = "", sectionId = "";
                        if (period.getGrade() != null && !TextUtils.isEmpty(period.getGrade().getId())) {
                            gradeId = period.getGrade().getId();
                        }
                        if (period.getTopic() != null && !TextUtils.isEmpty(period.getTopic().getId())) {
                            topicId = period.getTopic().getId();
                            topicName = period.getTopic().getName();
                        }
                        if (period.getSection() != null && !TextUtils.isEmpty(period.getSection().getId())) {
                            sectionId = period.getSection().getId();
                        }
                        if (period.getSubject() != null && !TextUtils.isEmpty(period.getSubject().getId())) {
                            subjectId = period.getSubject().getId();
                        }

                        String title = subjectName;
                        if (PermissionPrefsCommon.getClassDetailTeacherViewPermission(CalendarPeriodListActivity.this)) {
                            title = gradeName + " " + sectionName + " " + subjectName;
                        }

//                        Intent i = ClassDetailsActivity.getStartIntent(getBaseContext(), subjectId, period.getSubjectIds(), topicName, topicId, gradeId, sectionId, period.getStartTime(), false, subjectName, title);
//                        startActivity(i);

                    }
                });
            }


        }


        private void setPeriodStatus(PeriodNew period, LayoutPeriodicEventItemBinding binding) {
//            Date endDate = DateUtils.convertrIsoDate(period.getEndTime());
//            Date today = new Date();
//            if (DateUtils.compareTwoDate(endDate, today) == 0) {
//                if (today.compareTo(endDate) > 0) {
//                    binding.containerForeground.setVisibility(View.VISIBLE);
//                    binding.containerForeground.bringToFront();
//                } else {
//                    binding.containerForeground.setVisibility(View.GONE);
//                }
//            }
        }

        private void setSubTopic(PeriodNew period, TextView textView) {
            if (!TextUtils.isEmpty(period.getSubTopic())) {
                textView.setText(period.getSubTopic());
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void setTopic(PeriodNew period, TextView textView) {
            if (period.getTopic() != null && !TextUtils.isEmpty(period.getTopic().getName())) {
                textView.setText(period.getTopic().getName());
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void setPeriodNumber(PeriodNew period, TextView textView) {
            textView.setText(String.valueOf(period.getPeriodNo()));
        }

        private void setSubject(PeriodNew period, TextView textView) {
            if (period.getSubject() != null && !TextUtils.isEmpty(period.getSubject().getId()) && !TextUtils.isEmpty(period.getSubject().getName())) {
                String subject = period.getSubject().getName().trim();
                String subjectId = period.getSubject().getId();
                textView.setText(subject);
                textView.setVisibility(View.VISIBLE);
//                if (mSubjectMap.containsKey(subjectId)) {
//                    textView.setTextColor(mSubjectMap.get(subjectId).getTextColor());
//                }
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void setUserView(final PeriodNew period, LayoutPeriodicEventItemBinding binding) {

            if (PermissionPrefsCommon.getClassDetailTeacherViewPermission(CalendarPeriodListActivity.this)) {
                binding.imageViewPeriodTeacherPic.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(period.getGrade().getName())) {
                    binding.textViewClass.setVisibility(View.VISIBLE);
                    binding.textViewClass.setText(period.getGrade().getName());
                } else {
                    binding.textViewClass.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(period.getSection().getName())) {
                    binding.textViewClassSection.setVisibility(View.VISIBLE);
                    binding.textViewClassSection.setText(period.getSection().getName());
                } else {
                    binding.textViewClassSection.setVisibility(View.GONE);
                }

            } else {


                binding.textViewClassSection.setVisibility(View.GONE);
                binding.textViewClass.setVisibility(View.GONE);

                if (period.getTeacher() != null) {
                    if (!TextUtils.isEmpty(period.getTeacher().getId())) {
                        binding.imageViewPeriodTeacherPic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                                    startActivity(UserPublicProfileActivity.getStartIntent(CalendarPeriodListActivity.this, period.getTeacher().getId()));
                                } else {
                                    SnackBarUtils.showColoredSnackBar(getBaseContext(), v, getString(R.string.connect_internet), ContextCompat.getColor(getBaseContext(), R.color.colorRed));
                                }
                            }
                        });
                    }

                    binding.imageViewPeriodTeacherPic.setVisibility(View.VISIBLE);
                    binding.layoutTeacherImageView.setVisibility(View.VISIBLE);
                    // holder.mPeriodTeacherNameTextView.setText(period.getTeacher().getName());
                    if (period.getTeacher().getThumbnail() != null) {
                        if (period.getTeacher().getThumbnail().getUrl() != null && !period.getTeacher().getThumbnail().getUrl().isEmpty()) {
                            Picasso.with(mContext).load(period.getTeacher().getThumbnail().getUrl()).placeholder(R.drawable.icon_profile_large).transform(new CircleTransform()).resize(200, 200).centerCrop().into(binding.imageViewPeriodTeacherPic);

                        } else if (period.getTeacher().getThumbnail().getThumb() != null && !period.getTeacher().getThumbnail().getThumb().isEmpty()) {
                            Picasso.with(mContext).load(period.getTeacher().getThumbnail().getThumb()).placeholder(R.drawable.icon_profile_large).transform(new CircleTransform()).resize(200, 200).centerCrop().into(binding.imageViewPeriodTeacherPic);

                        } else {
                            String firstWord = period.getTeacher().getName().substring(0, 1).toUpperCase();
                            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimaryLN);
                            binding.imageViewPeriodTeacherPic.setImageDrawable(textDrawable);
                        }
                    } else {
                        String firstWord = period.getTeacher().getName().substring(0, 1).toUpperCase();
                        TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimaryLN);
                        binding.imageViewPeriodTeacherPic.setImageDrawable(textDrawable);
                    }

//                    String teacherName = in.securelearning.lil.android.base.utils.TextUtils.join(" ", period.getTeacher().getFirstName(), period.getTeacher().getMiddleName(), period.getTeacher().getLastName());
//                    if (!TextUtils.isEmpty(teacherName)) {
//                        binding.textViewPeriodTeacherName.setText(teacherName);
//                        binding.textViewPeriodTeacherName.setVisibility(View.VISIBLE);
//                    } else {
//                        binding.textViewPeriodTeacherName.setVisibility(View.GONE);
//                    }
                } else {
                    binding.imageViewPeriodTeacherPic.setVisibility(View.GONE);
                }

            }
        }

        private void setPeriodTime(PeriodNew period, TextView textViewStartTime, TextView textViewEndTime) {
            if (!TextUtils.isEmpty(period.getStartTime()) && !TextUtils.isEmpty(period.getEndTime())) {
                String periodStartTime = PeriodDetailPopUp.getTimeFromMilliSeconds(DateUtils.convertrIsoDate(period.getStartTime()).getTime());
                String periodEndTime = PeriodDetailPopUp.getTimeFromMilliSeconds(DateUtils.convertrIsoDate(period.getEndTime()).getTime());
                ((View) textViewStartTime.getParent()).setVisibility(View.VISIBLE);
                textViewStartTime.setText(periodStartTime + " - " + periodEndTime);
//                textViewEndTime.setText(periodEndTime);
            } else {
//                ((View) textViewStartTime.getParent()).setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {

            return mPeriodAdapterList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutPeriodicEventItemBinding mBinding;

            public ViewHolder(LayoutPeriodicEventItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;

            }
        }
    }


}
