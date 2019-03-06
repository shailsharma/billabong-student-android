package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ClassPeriodDetailNewBinding;
import in.securelearning.lil.android.app.databinding.FragmentPeriodicForClassDetailsBinding;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.PeriodicEventsModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.home.views.widget.PeriodDetailPopUp;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class PeriodicFragmentForClassDetails extends Fragment {
    public static final String SUBJECT_ID = "subject_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String DATE = "date";
    private int mColumnCount = 1;
    private String mSubjectId;
    private String mTopicId;
    private String mGradeId;
    private String mSectionId;
    private String mDate;
    private String mSubjectName;
    FragmentPeriodicForClassDetailsBinding mBinding;

    @Inject
    PeriodicEventsModel mPeriodicEventsModel;
    @Inject
    public AppUserModel mAppUserModel;
    int mSubjectColor = Color.BLACK;
    PeriodAdapter mPeriodAdapter;

    public PeriodicFragmentForClassDetails() {
    }

    public static PeriodicFragmentForClassDetails newInstance() {
        PeriodicFragmentForClassDetails fragment = new PeriodicFragmentForClassDetails();
        return fragment;
    }

    public static Fragment newInstance(String subjectId, String topicId, String gradeId, String sectionId, String date, int columnCount) {
        PeriodicFragmentForClassDetails fragment = new PeriodicFragmentForClassDetails();
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
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_periodic_for_class_details, container, false);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mSectionId = getArguments().getString(SECTION_ID);
            mDate = getArguments().getString(DATE);
        }
        mSubjectColor = PrefManager.getColorForSubject(getActivity(), mSubjectId);
        getPeriodIcData();
        return mBinding.getRoot();
    }

    private void getPeriodIcData() {
        Observable.create(new ObservableOnSubscribe<ArrayList<PeriodNew>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<PeriodNew>> e) throws Exception {
                ArrayList<PeriodNew> periodList = mPeriodicEventsModel.fetchPeriodicEventsBySubjectGradeSection(mSubjectId, mGradeId, mSectionId, DateUtils.getSecondsForMorningFromDate(DateUtils.convertrIsoDate(mDate)), DateUtils.getSecondsForMidnightFromDate(DateUtils.convertrIsoDate(mDate)));
                if (periodList != null) {
                    e.onNext(periodList);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PeriodNew>>() {
            @Override
            public void accept(final ArrayList<PeriodNew> periodNew) throws Exception {
                if (periodNew != null && periodNew.size() > 0) {
                    final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mBinding.recycleViewPeriodEvent.setLayoutManager(mLayoutManager);
                    mPeriodAdapter = new PeriodAdapter(periodNew, mSubjectColor, getContext());
                    mBinding.recycleViewPeriodEvent.setAdapter(mPeriodAdapter);
                } else {
                    mBinding.recycleViewPeriodEvent.setVisibility(View.GONE);
                    mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.ViewHolder> {
        private List<PeriodNew> mPeriodAdapterList = new ArrayList<>();
        private Context mContext;
        private int mSubjectColor;

        public PeriodAdapter(List<PeriodNew> mPeriodList, int subjectColor, Context baseContext) {
            this.mContext = baseContext;
            this.mPeriodAdapterList = mPeriodList;
            this.mSubjectColor = subjectColor;
        }

        @Override
        public PeriodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ClassPeriodDetailNewBinding view = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.class_period_detail_new, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PeriodAdapter.ViewHolder holder, int position) {
            final PeriodNew period = mPeriodAdapterList.get(position);

            setUserView(period, holder.periodBinding);
            setSubject(period, holder.periodBinding.textViewPeriodName);
            setPeriodTime(period, holder.periodBinding.textViewPeriodTiming);
            setPeriodNumber(period, holder.periodBinding.textViewPeriodNumber);
            setTopic(period, holder.periodBinding.textViewPeriodTopic);
            setSubTopic(period, holder.periodBinding.textViewPeriodSubTopic);


            holder.periodBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Intent i = ResourceListActivity.getIntentForTopicBrowse(mContext, period.getSubject().getId(), period.getSubjectIds(), period.getTopic().getId(), period.getGrade().getId(), "Resources for " + period.getTopic().getName());
//                    startActivity(i);

                }
            });


            holder.periodBinding.layoutTeacherView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        if (period.getTeacher() != null && !TextUtils.isEmpty(period.getTeacher().getId())) {
                            startActivity(UserProfileActivity.getStartIntent(period.getTeacher().getId(), getContext()));

                        }

                    } else {
                        ToastUtils.showToastAlert(mContext, getString(R.string.connect_internet));
                    }
                }
            });

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
                textView.setVisibility(View.VISIBLE);
                textView.setText(period.getTopic().getName());

            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void setPeriodNumber(PeriodNew period, TextView textView) {
            textView.setText(String.valueOf(period.getPeriodNo()));
            textView.setTextColor(mSubjectColor);
        }

        private void setSubject(PeriodNew period, TextView textView) {
            if (period.getSubject() != null && !TextUtils.isEmpty(period.getSubject().getName())) {
                String subject = period.getSubject().getName().trim();
                textView.setText(subject);
                textView.setTextColor(mSubjectColor);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void setUserView(PeriodNew period, ClassPeriodDetailNewBinding binding) {

            if (PermissionPrefsCommon.getClassDetailTeacherViewPermission(getContext())) {
                binding.layoutTeacherView.setVisibility(View.GONE);
                binding.viewSeparator.setVisibility(View.GONE);

            } else {
                binding.layoutTeacherView.setVisibility(View.VISIBLE);
                binding.viewSeparator.setVisibility(View.VISIBLE);
                if (period.getTeacher() != null) {
                    binding.textViewPeriodTeacherName.setText(period.getTeacher().getName());
                    if (period.getTeacher().getThumbnail() != null) {
                        if (period.getTeacher().getThumbnail().getUrl() != null && !period.getTeacher().getThumbnail().getUrl().isEmpty()) {
                            Picasso.with(mContext).load(period.getTeacher().getThumbnail().getUrl()).placeholder(R.drawable.icon_profile_large).transform(new CircleTransform()).resize(200, 200).centerCrop().into(binding.imageViewPeriodTeacherPic);
                        } else if (period.getTeacher().getThumbnail().getThumb() != null && !period.getTeacher().getThumbnail().getThumb().isEmpty()) {
                            Picasso.with(mContext).load(period.getTeacher().getThumbnail().getThumb()).placeholder(R.drawable.icon_profile_large).transform(new CircleTransform()).resize(200, 200).centerCrop().into(binding.imageViewPeriodTeacherPic);
                        } else {
                            String firstWord = period.getTeacher().getName().substring(0, 1).toUpperCase();
                            TextDrawable textDrawable = TextDrawable.builder().buildRect(firstWord, R.color.colorPrimaryLN);
                            binding.imageViewPeriodTeacherPic.setImageDrawable(textDrawable);
                        }
                    } else {
                        String firstWord = period.getTeacher().getName().substring(0, 1).toUpperCase();
                        TextDrawable textDrawable = TextDrawable.builder().buildRect(firstWord, R.color.colorPrimaryLN);
                        binding.imageViewPeriodTeacherPic.setImageDrawable(textDrawable);

                    }
                }

            }
        }

        private void setPeriodTime(PeriodNew period, TextView textView) {
            if (!TextUtils.isEmpty(period.getStartTime()) && !TextUtils.isEmpty(period.getEndTime())) {
                String periodStartTime = PeriodDetailPopUp.getTimeFromMilliSeconds(DateUtils.convertrIsoDate(period.getStartTime()).getTime());
                String periodEndTime = PeriodDetailPopUp.getTimeFromMilliSeconds(DateUtils.convertrIsoDate(period.getEndTime()).getTime());
                textView.setVisibility(View.VISIBLE);
                textView.setText(periodStartTime + " - " + periodEndTime);
            } else {
                textView.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {

            return mPeriodAdapterList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ClassPeriodDetailNewBinding periodBinding;

            public ViewHolder(ClassPeriodDetailNewBinding itemView) {
                super(itemView.getRoot());
                periodBinding = itemView;

            }
        }
    }

//    public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.ViewHolder> {
//        private List<PeriodNew> mPeriodAdapterList = new ArrayList<>();
//        private int mRandomColorPosition;
//        private int[] mExtraColorArray;
//
//        public PeriodAdapter(List<PeriodNew> mPeriodList) {
//            this.mPeriodAdapterList = mPeriodList;
//            this.mRandomColorPosition = 0;
//            this.mExtraColorArray = getContext().getResources().getIntArray(R.array.subject_text_color_extra);
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
////            LayoutCalendarPeriodItemBinding view = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_calendar_period_item, parent, false);
//            ClassPeriodDetailNewBinding view = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.class_period_detail_new, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            final PeriodNew periodNew = mPeriodAdapterList.get(position);
//            if (mAppUserModel.getApplicationUser().getUserType().equals(AppUser.USERTYPE.TEACHER)) {
////                holder.periodBinding.layoutPeriodTeacher.setVisibility(View.GONE);
////                holder.periodBinding.layoutPeriodStudent.setVisibility(View.VISIBLE);
////                holder.periodBinding.textViewClass.setText(periodNew.getGrade().getName() + " - " + periodNew.getSection().getName());
//            } else {
////                holder.periodBinding.layoutPeriodTeacher.setVisibility(View.VISIBLE);
////                holder.periodBinding.layoutPeriodStudent.setVisibility(View.GONE);
//
//
//                try {
//                    Transformation transformation = new RoundedTransformationBuilder()
//                            .borderColor(Color.GRAY)
//                            .borderWidthDp(1)
//                            .cornerRadiusDp(40)
//                            .oval(false)
//                            .build();
////                            Picasso.with(this).load(curatorImageFilePath).fit()
////                                    .transform(transformation).into(mBinding.content.courseCuratorImage);
//                    if (periodNew.getTeacher().getThumbnail().getUrl() != null && !periodNew.getTeacher().getThumbnail().getUrl().isEmpty()) {
//                        Picasso.with(getActivity()).load(periodNew.getTeacher().getThumbnail().getUrl()).fit().transform(transformation).placeholder(R.drawable.icon_profile_large).into(holder.periodBinding.imageViewPeriodTeacherPic);
//                    } else if (periodNew.getTeacher().getThumbnail().getThumb() != null && !periodNew.getTeacher().getThumbnail().getThumb().isEmpty()) {
//                        Picasso.with(getActivity()).load(periodNew.getTeacher().getThumbnail().getThumb()).fit().transform(transformation).placeholder(R.drawable.icon_profile_large).into(holder.periodBinding.imageViewPeriodTeacherPic);
//                    } else {
//                        Picasso.with(getActivity()).load(R.drawable.icon_profile_large).fit().transform(transformation).into(holder.periodBinding.imageViewPeriodTeacherPic);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            String subject = periodNew.getSubject().getName().trim();
//            String subjectId = periodNew.getSubject().getId();
//            holder.periodBinding.textViewPeriodNumber.setText(String.valueOf(periodNew.getPeriodNo()));
//            holder.periodBinding.textViewPeriodName.setText(subject);
//            holder.periodBinding.textViewPeriodName.setTextColor(mSubjectColor);
//            holder.periodBinding.textViewPeriodTopic.setText(periodNew.getTopic().getName());
//            holder.periodBinding.textViewPeriodTeacherName.setText(periodNew.getTeacher().getName());
//            String startTime = "";
//            String endTime = "";
//            try {
//                startTime = PeriodDetailPopUp.getTimeFromMilliSeconds(DateUtils.convertrIsoDate(periodNew.getStartTime()).getTime());
//                endTime = PeriodDetailPopUp.getTimeFromMilliSeconds(DateUtils.convertrIsoDate(periodNew.getEndTime()).getTime());
//                holder.periodBinding.textViewPeriodTiming.setText(startTime + " - " + endTime);
//            } catch (Exception e) {
//                holder.periodBinding.textViewPeriodTiming.setText(startTime + " - " + endTime);
//                e.printStackTrace();
//            }
//
//            holder.periodBinding.textViewPeriodNumber.setTextColor(mSubjectColor);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mPeriodAdapterList.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            ClassPeriodDetailNewBinding periodBinding;
//
//            public ViewHolder(ClassPeriodDetailNewBinding itemView) {
//                super(itemView.getRoot());
//                periodBinding = itemView;
//
//            }
//        }
//    }

}
