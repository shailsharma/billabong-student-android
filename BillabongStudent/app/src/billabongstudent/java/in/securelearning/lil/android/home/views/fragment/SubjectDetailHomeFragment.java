package in.securelearning.lil.android.home.views.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCourseListItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMicroCourseListItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutPracticeListItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutSubjectDetailsHomeFragmentBinding;
import in.securelearning.lil.android.app.databinding.LayoutVideoListItemBinding;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.FetchSubjectDetailEvent;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.activity.MindSparkAllTopicListActivity;
import in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.player.views.activity.PracticePlayerActivity;
import in.securelearning.lil.android.quizpreview.activity.PracticeTopicActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkContentDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkTopicData;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkUnitData;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectDetailHomeFragment extends Fragment {

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    @Inject
    RxBus mRxBus;
    private Context mContext;
    private static final String TOPIC_ID = "topicId";
    private static final String SUBJECT_NAME = "subjectName";
    private static final String THIRD_PARTY_TOPIC_ID = "thirdPartyTopicId";
    private String mSubjectName;
    private String mThirdPartyTopicId;
    LayoutSubjectDetailsHomeFragmentBinding mBinding;

    private Disposable mDisposable;

    public SubjectDetailHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_subject_details_home_fragment, container, false);
        getBundleData();
        listenRxEvent();
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void getBundleData() {
        if (getArguments() != null) {
            String topicId = getArguments().getString(TOPIC_ID);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mThirdPartyTopicId = getArguments().getString(THIRD_PARTY_TOPIC_ID);
            if (!TextUtils.isEmpty(topicId)) {
                fetchLearnList(topicId);
            }
        }
    }

    @SuppressLint("CheckResult")
    private void listenRxEvent() {
        mDisposable = mRxBus.toFlowable().observeOn(Schedulers.io()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) throws Exception {
                if (event instanceof FetchSubjectDetailEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            String topicId = ((FetchSubjectDetailEvent) event).getId();
                            fetchLearnList(topicId);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void fetchLearnList(final String topicId) {
        mBinding.listLearn.setVisibility(View.GONE);
        mBinding.progressBarLearn.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataLearn.setVisibility(View.GONE);
        mFlavorHomeModel.fetchLRPA(topicId, "l")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LRPAResult>() {
                    @Override
                    public void accept(LRPAResult lrpaResult) throws Exception {
                        mBinding.progressBarLearn.setVisibility(View.GONE);
                        fetchReinforceList(topicId);
                        if (lrpaResult != null && lrpaResult.getResults() != null && !lrpaResult.getResults().isEmpty()) {
                            mBinding.listLearn.setVisibility(View.VISIBLE);
                            mBinding.textViewNoDataLearn.setVisibility(View.GONE);
                            initializeRecyclerViewLearn(lrpaResult.getResults());
                        } else {
                            mBinding.textViewNoDataLearn.setVisibility(View.VISIBLE);

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        fetchReinforceList(topicId);
                        mBinding.listLearn.setVisibility(View.GONE);
                        mBinding.progressBarLearn.setVisibility(View.GONE);
                        mBinding.textViewNoDataLearn.setVisibility(View.VISIBLE);
                        mBinding.textViewNoDataLearn.setText(throwable.getMessage());
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void fetchReinforceList(final String topicId) {
        mBinding.listReinforce.setVisibility(View.GONE);
        mBinding.progressBarReinforce.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataReinforce.setVisibility(View.GONE);
        mFlavorHomeModel.fetchLRPA(topicId, "r")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LRPAResult>() {
                    @Override
                    public void accept(LRPAResult lrpaResult) throws Exception {
                        checkSubject(topicId);

                        mBinding.progressBarReinforce.setVisibility(View.GONE);
                        if (lrpaResult != null && lrpaResult.getResults() != null && !lrpaResult.getResults().isEmpty()) {
                            mBinding.listReinforce.setVisibility(View.VISIBLE);
                            mBinding.textViewNoDataReinforce.setVisibility(View.GONE);
                            initializeRecyclerViewReinforce(lrpaResult.getResults());
                        } else {
                            mBinding.listReinforce.setVisibility(View.GONE);
                            mBinding.textViewNoDataReinforce.setVisibility(View.VISIBLE);

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();

                        checkSubject(topicId);
                        mBinding.listReinforce.setVisibility(View.GONE);
                        mBinding.progressBarReinforce.setVisibility(View.GONE);
                        mBinding.textViewNoDataReinforce.setVisibility(View.VISIBLE);
                        mBinding.textViewNoDataReinforce.setText(throwable.getMessage());
                    }
                });
    }

    /*To fetch practice if subject is maths then from mind spark
     * else from LIL*/
    private void checkSubject(String topicId) {
//        if (mSubjectName.contains("Math")) {
//            fetchMindSparkData(mThirdPartyTopicId, topicId);
//        } else if (mSubjectName.contains("Eng")) {
//            displayLightSailCard();
//            fetchApplyList(topicId);
//        } else {
//            fetchPracticeList(topicId);
//        }
        fetchPracticeList(topicId);
    }

    /*Check whether thirdPartyTopicId null or empty*/
    private void fetchMindSparkData(String thirdPartyTopicId, String topicId) {
        if (!TextUtils.isEmpty(thirdPartyTopicId)) {
            loginUserToMindSpark(thirdPartyTopicId, topicId);
        } else {
            mBinding.progressBarPractice.setVisibility(View.GONE);
            mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
            fetchApplyList(topicId);
        }
    }

    private void displayLightSailCard() {
        mBinding.progressBarPractice.setVisibility(View.GONE);
        mBinding.cardViewLightSail.setVisibility(View.VISIBLE);

        mBinding.cardViewLightSail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(getContext())) {
                    String jwt = AppPrefs.getIdToken(getContext());
                    CustomChromeTabHelper.loadCustomDataUsingColorResource(getContext(), "https://reader.lightsailed.com/Reader/index.html?jwtToken=" + jwt, R.color.colorPrimary);
                } else {
                    Toast.makeText(getContext(), getString(R.string.connect_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void fetchPracticeList(final String topicId) {
        mBinding.listPractice.setVisibility(View.GONE);
        mBinding.progressBarPractice.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataPractice.setVisibility(View.GONE);
        mFlavorHomeModel.fetchLRPA(topicId, "p")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LRPAResult>() {
                    @Override
                    public void accept(LRPAResult lrpaResult) throws Exception {
                        fetchApplyList(topicId);
                        mBinding.progressBarPractice.setVisibility(View.GONE);
                        if (lrpaResult != null && lrpaResult.getResults() != null && !lrpaResult.getResults().isEmpty()) {
                            mBinding.listPractice.setVisibility(View.VISIBLE);
                            mBinding.textViewNoDataPractice.setVisibility(View.GONE);
                            initializeRecyclerViewPractice(lrpaResult.getResults());
                        } else {
                            mBinding.listPractice.setVisibility(View.GONE);
                            mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        fetchApplyList(topicId);
                        mBinding.listPractice.setVisibility(View.GONE);
                        mBinding.progressBarPractice.setVisibility(View.GONE);
                        mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                        mBinding.textViewNoDataPractice.setText(throwable.getMessage());
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void fetchApplyList(String topicId) {
        mBinding.listApply.setVisibility(View.GONE);
        mBinding.progressBarApply.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataApply.setVisibility(View.GONE);
        mFlavorHomeModel.fetchLRPA(topicId, "a")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LRPAResult>() {
                    @Override
                    public void accept(LRPAResult lrpaResult) throws Exception {
                        mBinding.progressBarApply.setVisibility(View.GONE);
                        if (lrpaResult != null && lrpaResult.getResults() != null && !lrpaResult.getResults().isEmpty()) {
                            mBinding.listApply.setVisibility(View.VISIBLE);
                            mBinding.textViewNoDataApply.setVisibility(View.GONE);
                            initializeRecyclerViewApply(lrpaResult.getResults());
                        } else {
                            mBinding.listApply.setVisibility(View.GONE);
                            mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mBinding.listApply.setVisibility(View.GONE);
                        mBinding.progressBarApply.setVisibility(View.GONE);
                        mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);
                        mBinding.textViewNoDataApply.setText(throwable.getMessage());
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void loginUserToMindSpark(String thirdPartyTopicId, final String topicId) {
        mBinding.listPractice.setVisibility(View.GONE);
        mBinding.progressBarPractice.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataPractice.setVisibility(View.GONE);
        mFlavorHomeModel.loginUserToMindSpark(thirdPartyTopicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MindSparkLoginResponse>() {
                    @Override
                    public void accept(MindSparkLoginResponse mindSparkLoginResponse) throws Exception {
                        fetchApplyList(topicId);

                        mBinding.progressBarPractice.setVisibility(View.GONE);
                        if (mindSparkLoginResponse != null && mindSparkLoginResponse.getMindSparkContentDetails() != null) {
                            ArrayList<MindSparkLoginResponse> list = new ArrayList<>(Collections.singleton(mindSparkLoginResponse));
                            initializeRecyclerViewPracticeMS(list);
                            mBinding.listPractice.setVisibility(View.VISIBLE);
                            mBinding.textViewNoDataPractice.setVisibility(View.GONE);
                        } else {
                            mBinding.listPractice.setVisibility(View.GONE);
                            mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        fetchApplyList(topicId);
                        mBinding.listPractice.setVisibility(View.GONE);
                        mBinding.progressBarPractice.setVisibility(View.GONE);
                        mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                        mBinding.textViewNoDataPractice.setText(throwable.getMessage());
                    }
                });
    }

    public static SubjectDetailHomeFragment newInstance(String topicId, String subjectName, String thirdPartyTopicId) {
        SubjectDetailHomeFragment fragment = new SubjectDetailHomeFragment();
        Bundle args = new Bundle();
        args.putString(TOPIC_ID, topicId);
        args.putString(SUBJECT_NAME, subjectName);
        args.putString(THIRD_PARTY_TOPIC_ID, thirdPartyTopicId);
        fragment.setArguments(args);
        return fragment;
    }

    /*Recycler view initialization*/
    private void initializeRecyclerViewLearn(ArrayList<AboutCourseMinimal> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listLearn.setLayoutManager(layoutManager);
        LearnAdapter learnAdapter = new LearnAdapter(mContext, list);
        mBinding.listLearn.setAdapter(learnAdapter);

    }

    private void initializeRecyclerViewReinforce(ArrayList<AboutCourseMinimal> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listReinforce.setLayoutManager(layoutManager);
        ReinforceAdapter reinforceAdapter = new ReinforceAdapter(mContext, list);
        mBinding.listReinforce.setAdapter(reinforceAdapter);

    }

    private void initializeRecyclerViewPracticeMS(ArrayList<MindSparkLoginResponse> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listPractice.setLayoutManager(layoutManager);
        PracticeAdapterMS practiceAdapterMS = new PracticeAdapterMS(mContext, list);
        mBinding.listPractice.setAdapter(practiceAdapterMS);
        mBinding.buttonMorePractice.setVisibility(View.VISIBLE);
        mBinding.buttonMorePractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MindSparkAllTopicListActivity.getStartIntent(getContext()));
            }
        });

    }

    private void initializeRecyclerViewPractice(ArrayList<AboutCourseMinimal> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listPractice.setLayoutManager(layoutManager);
        mBinding.listPractice.setAdapter(new PracticeAdapter(mContext, list));

    }

    private void initializeRecyclerViewApply(ArrayList<AboutCourseMinimal> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listApply.setLayoutManager(layoutManager);
        ApplyAdapter applyAdapter = new ApplyAdapter(mContext, list);
        mBinding.listApply.setAdapter(applyAdapter);

    }

    /*Adapter*/
    private class LearnAdapter extends RecyclerView.Adapter<LearnAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<AboutCourseMinimal> mList;

        LearnAdapter(Context context, ArrayList<AboutCourseMinimal> list) {
            mContext = context;
            this.mList = list;
        }

        @NonNull
        @Override
        public LearnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutCourseListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_course_list_item, parent, false);
            return new LearnAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final LearnAdapter.ViewHolder holder, int position) {
            final AboutCourseMinimal course = mList.get(position);

            setThumbnail(course.getThumbnail(), holder.mBinding.imageViewBackground);

            holder.mBinding.textViewTitle.setText(course.getTitle());
            holder.mBinding.textViewType.setText(mFlavorHomeModel.getCourseType(course));

            final Class finalObjectClass = mFlavorHomeModel.getCourseClass(course);
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(course.getId())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            mContext.startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, course.getId()));
                        } else {
                            WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), course.getId(), "", "", finalObjectClass, "", false);

                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {
            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
                Picasso.with(mContext).load(thumbnail.getLocalUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutCourseListItemBinding mBinding;

            public ViewHolder(LayoutCourseListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class ReinforceAdapter extends RecyclerView.Adapter<ReinforceAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<AboutCourseMinimal> mList;

        ReinforceAdapter(Context context, ArrayList<AboutCourseMinimal> list) {
            mContext = context;
            this.mList = list;
        }

        @NonNull
        @Override
        public ReinforceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutVideoListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_video_list_item, parent, false);
            return new ReinforceAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ReinforceAdapter.ViewHolder holder, int position) {
            final AboutCourseMinimal course = mList.get(position);

            setThumbnail(course.getThumbnail(), holder.mBinding.imageViewBackground);

            holder.mBinding.textViewTitle.setText(course.getTitle());
            holder.mBinding.textViewType.setText(mFlavorHomeModel.getCourseType(course));

            final Class finalObjectClass = mFlavorHomeModel.getCourseClass(course);
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(course.getId())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            mContext.startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, course.getId()));
                        } else {
                            WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), course.getId(), "", "", finalObjectClass, "", false);

                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {
            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
                Picasso.with(mContext).load(thumbnail.getLocalUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutVideoListItemBinding mBinding;

            public ViewHolder(LayoutVideoListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<AboutCourseMinimal> mList;

        PracticeAdapter(Context context, ArrayList<AboutCourseMinimal> list) {
            mContext = context;
            this.mList = list;
        }

        @NonNull
        @Override
        public PracticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMicroCourseListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_micro_course_list_item, parent, false);
            return new PracticeAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final PracticeAdapter.ViewHolder holder, int position) {
            final AboutCourseMinimal course = mList.get(position);

            setThumbnail(course.getThumbnail(), holder.mBinding.imageViewBackground);

            holder.mBinding.textViewTitle.setText(course.getTitle());
            //holder.mBinding.textViewType.setText(mFlavorHomeModel.getCourseType(course));

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(course.getId())) {

                        ArrayList<String> skillIds = getSkillIds(course.getMetaInformation());
                        HomeModel.SkillMap skillMap = getSkillMap(course.getMetaInformation());

                        if (skillMap != null && skillIds != null && !skillIds.isEmpty()) {
                            startActivity(PracticeTopicActivity.getStartIntent(getContext(), skillIds, course.getTitle(), getString(R.string.label_low), skillMap));
                        } else {
                            Toast.makeText(getContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        private ArrayList<String> getSkillIds(MetaInformation metaInformation) {
            if (metaInformation != null) {
                ArrayList<String> skillIds = new ArrayList<>();
                if (metaInformation.getSkills() != null && !metaInformation.getSkills().isEmpty()) {
                    for (int i = 0; i < metaInformation.getSkills().size(); i++) {
                        skillIds.add(metaInformation.getSkills().get(i).getId());
                    }
                    return skillIds;
                } else {
                    return null;
                }

            } else {
                return null;

            }
        }

        private HomeModel.SkillMap getSkillMap(MetaInformation metaInformation) {
            if (metaInformation != null) {
                HomeModel.SkillMap skillMap = new HomeModel.SkillMap();
                if (metaInformation.getBoard() != null) {
                    skillMap.setBoard(metaInformation.getBoard());
                } else {
                    return null;
                }

                if (metaInformation.getGrade() != null) {
                    skillMap.setGrade(metaInformation.getGrade());
                } else {
                    return null;
                }
                if (metaInformation.getLanguage() != null) {
                    skillMap.setLanguage(metaInformation.getLanguage());

                } else {
                    return null;
                }
                if (metaInformation.getLearningLevel() != null) {
                    skillMap.setLearningLevel(metaInformation.getLearningLevel());

                } else {
                    return null;
                }

                if (metaInformation.getSubject() != null) {
                    skillMap.setSubject(metaInformation.getSubject());
                } else {
                    return null;
                }

                if (metaInformation.getTopic() != null) {
                    skillMap.setTopic(metaInformation.getTopic());

                } else {
                    return null;
                }
                return skillMap;
            } else {
                return null;
            }

        }

        private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {
            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
                Picasso.with(mContext).load(thumbnail.getLocalUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMicroCourseListItemBinding mBinding;

            public ViewHolder(LayoutMicroCourseListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class PracticeAdapterMS extends RecyclerView.Adapter<PracticeAdapterMS.ViewHolder> {
        private Context mContext;
        private ArrayList<MindSparkLoginResponse> mList;

        PracticeAdapterMS(Context context, ArrayList<MindSparkLoginResponse> list) {
            mContext = context;
            this.mList = list;
        }

        @NonNull
        @Override
        public PracticeAdapterMS.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutPracticeListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_practice_list_item, parent, false);
            return new PracticeAdapterMS.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final PracticeAdapterMS.ViewHolder holder, int position) {
            final MindSparkLoginResponse response = mList.get(position);
            final MindSparkContentDetails mindSparkContentDetails = response.getMindSparkContentDetails();
            MindSparkTopicData mindSparkTopicData = response.getMindSparkTopicData();
            if (mindSparkTopicData.getMindSparkUnitList() != null && mindSparkTopicData.getMindSparkUnitList().size() > 0) {
                MindSparkUnitData mindSparkUnitData = mindSparkTopicData.getMindSparkUnitList().get(0);
                holder.mBinding.textViewSubTitle.setText(mindSparkUnitData.getUnitName());

            }
            setThumbnail(mindSparkContentDetails.getBackgroundThumb(), holder.mBinding.imageViewBackground);
            setStatus(mindSparkContentDetails, holder.mBinding.textViewStatus);
            holder.mBinding.textViewTitle.setText(mindSparkContentDetails.getContentName());
            holder.mBinding.textViewRewardPoints.setVisibility(View.GONE);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        startActivity(PracticePlayerActivity.getStartIntent(getContext(), mindSparkContentDetails.getContentId()));
                    }
                }
            });
        }

        private void setStatus(MindSparkContentDetails mindSparkContentDetails, AppCompatTextView textViewStatus) {
            String value;
            if (mindSparkContentDetails.getUnitsCleared() > 1) {
                value = mindSparkContentDetails.getUnitsCleared() + " out of " + mindSparkContentDetails.getUnitsOverall() + " units covered";
            } else {
                value = mindSparkContentDetails.getUnitsCleared() + " out of " + mindSparkContentDetails.getUnitsOverall() + " unit covered";
            }
            textViewStatus.setText(value);
        }

        private void setThumbnail(int thumbnail, ImageView imageView) {
            Picasso.with(mContext).load(thumbnail).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutPracticeListItemBinding mBinding;

            public ViewHolder(LayoutPracticeListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class ApplyAdapter extends RecyclerView.Adapter<ApplyAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<AboutCourseMinimal> mList;

        ApplyAdapter(Context context, ArrayList<AboutCourseMinimal> list) {
            mContext = context;
            this.mList = list;
        }

        @NonNull
        @Override
        public ApplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutMicroCourseListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_micro_course_list_item, parent, false);
            return new ApplyAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ApplyAdapter.ViewHolder holder, int position) {
            final AboutCourseMinimal course = mList.get(position);

            setThumbnail(course.getThumbnail(), holder.mBinding.imageViewBackground);

            holder.mBinding.textViewTitle.setText(course.getTitle());
            // holder.mBinding.textViewType.setText(mFlavorHomeModel.getCourseType(course));

            final Class finalObjectClass = mFlavorHomeModel.getCourseClass(course);
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(course.getId())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            mContext.startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, course.getId()));
                        } else {
                            WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), course.getId(), "", "", finalObjectClass, "", false);

                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {
            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
                Picasso.with(mContext).load(thumbnail.getLocalUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).fit().centerCrop().into(imageView);
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        /*public void addValues(ArrayList<Training> list) {
            if (mList != null) {
                mList.addAll(list);
                notifyDataSetChanged();
            }
        }*/

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMicroCourseListItemBinding mBinding;

            public ViewHolder(LayoutMicroCourseListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

}
