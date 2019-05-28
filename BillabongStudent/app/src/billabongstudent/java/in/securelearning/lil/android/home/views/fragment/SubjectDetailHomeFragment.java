package in.securelearning.lil.android.home.views.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.MindSparkNoUnitEvent;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkContentDetails;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkTopicData;
import in.securelearning.lil.android.mindspark.model.MindSparkModel;
import in.securelearning.lil.android.mindspark.views.activity.MindSparkAllTopicListActivity;
import in.securelearning.lil.android.mindspark.views.activity.MindSparkPlayerActivity;
import in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.player.mindspark.dataobjects.MindSparkUnitData;
import in.securelearning.lil.android.quizpreview.activity.PracticeTopicActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPARequest;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectDetailHomeFragment extends Fragment {

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    @Inject
    MindSparkModel mMindSparkModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AppUserModel mAppUserModel;
    private Context mContext;
    private static final String TOPIC_ID = "topicId";
    private static final String TOPIC_NAME = "topicName";
    private static final String SUBJECT_NAME = "subjectName";
    private static final String GRADE_NAME = "gradeName";
    private static final String THIRD_PARTY_TOPIC_ID_LIST = "thirdPartyTopicIdList";
    private static final String SUBJECT_BANNER_URL = "subjectBannerUrl";
    private String mSubjectName;
    private String mGradeName;
    private String mSubjectBannerURL;
    private ArrayList<String> mThirdPartyTopicIds;
    LayoutSubjectDetailsHomeFragmentBinding mBinding;

    private String mTopicName;

    public SubjectDetailHomeFragment() {
        // Required empty public constructor
    }


    public static SubjectDetailHomeFragment newInstance(String topicId, String topicName, String subjectName, String gradeName, ArrayList<String> thirdPartyTopicIds, String bannerUrl) {
        SubjectDetailHomeFragment fragment = new SubjectDetailHomeFragment();
        Bundle args = new Bundle();
        args.putString(TOPIC_ID, topicId);
        args.putString(TOPIC_NAME, topicName);
        args.putString(SUBJECT_NAME, subjectName);
        args.putString(GRADE_NAME, gradeName);
        args.putStringArrayList(THIRD_PARTY_TOPIC_ID_LIST, thirdPartyTopicIds);
        args.putString(SUBJECT_BANNER_URL, bannerUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_subject_details_home_fragment, container, false);
        getBundleData();
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;

    }

    private void getBundleData() {
        if (getArguments() != null) {
            String topicId = getArguments().getString(TOPIC_ID);
            mTopicName = getArguments().getString(TOPIC_NAME);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mGradeName = getArguments().getString(GRADE_NAME);
            mThirdPartyTopicIds = getArguments().getStringArrayList(THIRD_PARTY_TOPIC_ID_LIST);
            mSubjectBannerURL = getArguments().getString(SUBJECT_BANNER_URL);
            if (!TextUtils.isEmpty(topicId)) {
                fetchLearnList(topicId);
            }
        }
    }


    @SuppressLint("CheckResult")
    private void fetchLearnList(final String topicId) {
        mBinding.listLearn.setVisibility(View.GONE);
        mBinding.progressBarLearn.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataLearn.setVisibility(View.GONE);
        mFlavorHomeModel.fetchLRPA(topicId, LRPARequest.TYPE_LEARN)
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
        mFlavorHomeModel.fetchLRPA(topicId, LRPARequest.TYPE_REINFORCE)
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
    /*TODO hard coded logic for subject check, remove when dynamically done*/
    private void checkSubject(String topicId) {
        if (mSubjectName.contains("Math")) {
            fetchMindSparkData(mThirdPartyTopicIds, topicId);
        } else if (mSubjectName.contains("Eng")) {
            /*TODO hard coded logic, remove when dynamically done*/
            if (mGradeName.equals("PG") || mGradeName.equals("EuroJunior") || mGradeName.equals("EuroSenior")
                    || mGradeName.equals("Nursery") || mGradeName.equals("I")
                    || mGradeName.equals("II") || mGradeName.equals("III")) {
                displayFreadomCard();
            } else {
                displayLightSailCard();
            }
            fetchApplyList(topicId);
        } else {
            fetchPracticeList(topicId);
        }
    }

    /*Check whether thirdPartyTopicId null or empty*/
    private void fetchMindSparkData(ArrayList<String> thirdPartyTopicIds, String topicId) {
        if (thirdPartyTopicIds != null && !thirdPartyTopicIds.isEmpty()) {
            loginUserToMindSparkAndFetchPractice(thirdPartyTopicIds, topicId);
        } else {
            mBinding.progressBarPractice.setVisibility(View.GONE);
            mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
            fetchApplyList(topicId);
        }
    }

    /*Display Light Sail card if subject is English*/
    private void displayLightSailCard() {
        mBinding.progressBarPractice.setVisibility(View.GONE);
        mBinding.cardViewPractice.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(R.drawable.logo_lightsail).placeholder(R.drawable.image_placeholder).into(mBinding.imageViewPractice);
        mBinding.cardViewPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    String jwt = AppPrefs.getIdToken(mContext);
                    CustomChromeTabHelper.loadCustomDataUsingColorResource(mContext, mContext.getString(R.string.base_url_lightsail) + jwt, R.color.colorPrimary);
                    Log.e("LightSail---", mContext.getString(R.string.base_url_lightsail) + jwt);
                } else {
                    Toast.makeText(mContext, getString(R.string.connect_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*Display Freadom card if subject is English*/
    private void displayFreadomCard() {
        mBinding.progressBarPractice.setVisibility(View.GONE);
        mBinding.cardViewPractice.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(R.drawable.logo_freadom).placeholder(R.drawable.image_placeholder).into(mBinding.imageViewPractice);
        mBinding.imageViewPractice.setColorFilter(R.color.colorPrimary);

        mBinding.cardViewPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    String jwt = AppPrefs.getIdToken(mContext);
                    String email = mAppUserModel.getApplicationUser().getEmail();
                    CustomChromeTabHelper.loadCustomDataUsingColorResource(mContext, mContext.getString(R.string.freadom_url) + "?email=" + email + "&token=" + jwt, R.color.colorPrimary);
                    Log.e("Freadom---", mContext.getString(R.string.freadom_url) + "?email=" + email + "&token=" + jwt);
                } else {
                    Toast.makeText(mContext, getString(R.string.connect_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*Display MindSpark card if subject is Maths*/
    private void displayMindSparkCard(MindSparkLoginResponse response) {
        mBinding.layoutMindSparkPractice.getRoot().setVisibility(View.VISIBLE);
        mBinding.buttonMorePractice.setVisibility(View.VISIBLE);
        mBinding.imageViewMindSparkLogo.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(R.drawable.background_thumb_mind_spark).placeholder(R.drawable.image_placeholder).into(mBinding.layoutMindSparkPractice.imageViewBackground);

        final MindSparkContentDetails mindSparkContentDetails = response.getMindSparkContentDetails();
        MindSparkTopicData mindSparkTopicData = response.getMindSparkTopicData();
        if (mindSparkTopicData.getMindSparkUnitList() != null && mindSparkTopicData.getMindSparkUnitList().size() > 0) {
            MindSparkUnitData mindSparkUnitData = mindSparkTopicData.getMindSparkUnitList().get(0);
            mBinding.layoutMindSparkPractice.textViewSubTitle.setText(mindSparkUnitData.getUnitName());

        }
        String value;
        if (mindSparkContentDetails.getUnitsCleared() > 1) {
            value = mindSparkContentDetails.getUnitsCleared() + " out of " + mindSparkContentDetails.getUnitsOverall() + " units covered";
        } else {
            value = mindSparkContentDetails.getUnitsCleared() + " out of " + mindSparkContentDetails.getUnitsOverall() + " unit covered";
        }
        mBinding.layoutMindSparkPractice.textViewStatus.setText(value);
        mBinding.layoutMindSparkPractice.textViewTitle.setText(mindSparkContentDetails.getContentName());
        mBinding.layoutMindSparkPractice.textViewRewardPoints.setVisibility(View.GONE);

        mBinding.layoutMindSparkPractice.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    if (mindSparkContentDetails.getUnitsOverall() > 0) {
                        startActivity(MindSparkPlayerActivity.getStartIntent(mContext, mindSparkContentDetails.getContentId(), mindSparkContentDetails.getContentName()));
                    } else {
                        mRxBus.send(new MindSparkNoUnitEvent());
                    }
                }
            }
        });

        mBinding.buttonMorePractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MindSparkAllTopicListActivity.getStartIntent(getContext()));

            }
        });
    }

    /*Display Practice card if subject is English*/
    private void displayPracticeCard(final ArrayList<String> skillIds, final HomeModel.SkillMap skillMap) {
        mBinding.layoutPractice.getRoot().setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mSubjectBannerURL)) {
            Picasso.with(mContext).load(mSubjectBannerURL).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(mBinding.layoutPractice.imageViewBackground);
        } else {
            Picasso.with(mContext).load(R.drawable.background_thumb_mind_spark).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(mBinding.layoutPractice.imageViewBackground);
        }
        mBinding.layoutPractice.textViewTitle.setText(mTopicName);
        mBinding.layoutPractice.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    startActivity(PracticeTopicActivity.getStartIntent(getContext(), skillIds, mTopicName, getString(R.string.label_low), skillMap));

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
        mFlavorHomeModel.fetchLRPA(topicId, LRPARequest.TYPE_PRACTICE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LRPAResult>() {
                    @Override
                    public void accept(LRPAResult lrpaResult) throws Exception {
                        fetchApplyList(topicId);
                        mBinding.progressBarPractice.setVisibility(View.GONE);
                        if (lrpaResult != null && lrpaResult.getResults() != null && !lrpaResult.getResults().isEmpty()) {
                            mBinding.listPractice.setVisibility(View.GONE);
                            mBinding.textViewNoDataPractice.setVisibility(View.GONE);

                            //initializeRecyclerViewPractice(lrpaResult.getResults());

                            ArrayList<AboutCourseMinimal> list = lrpaResult.getResults();
                            ArrayList<MetaInformation> metaInformationList = new ArrayList<>();
                            for (int i = 0; i < list.size(); i++) {
                                metaInformationList.add(list.get(i).getMetaInformation());
                            }

                            if (!metaInformationList.isEmpty()) {
                                ArrayList<String> skillIds = new ArrayList<>();
                                HomeModel.SkillMap skillMap = getSkillMap(metaInformationList.get(0));
                                for (int i = 0; i < metaInformationList.size(); i++) {
                                    if (!metaInformationList.get(i).getSkills().isEmpty()) {
                                        skillIds.addAll(getSkillIds(metaInformationList.get(i).getSkills()));
                                    }
                                }

                                /*removing duplicate entries if any*/
                                HashSet<String> hashSet = new HashSet<String>(skillIds);
                                skillIds.clear();
                                skillIds.addAll(hashSet);

                                displayPracticeCard(skillIds, skillMap);
                            } else {
                                mBinding.listPractice.setVisibility(View.GONE);
                                mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                            }


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
        mFlavorHomeModel.fetchLRPA(topicId, LRPARequest.TYPE_APPLY)
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
    private void loginUserToMindSparkAndFetchPractice(ArrayList<String> thirdPartyTopicIds, final String topicId) {
        mBinding.listPractice.setVisibility(View.GONE);
        mBinding.progressBarPractice.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataPractice.setVisibility(View.GONE);
        mMindSparkModel.loginUserToMindSpark(thirdPartyTopicIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<MindSparkLoginResponse>>() {
                    @Override
                    public void accept(ArrayList<MindSparkLoginResponse> responseArrayList) throws Exception {

                        fetchApplyList(topicId);

                        mBinding.progressBarPractice.setVisibility(View.GONE);
                        if (responseArrayList != null && !responseArrayList.isEmpty()) {
                            //displayMindSparkCard(responseArrayList);
                            initializeRecyclerViewPracticeMS(responseArrayList);
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
        mBinding.listPractice.setAdapter(new PracticeAdapterMS(mContext, list));
        mBinding.buttonMorePractice.setVisibility(View.VISIBLE);
        mBinding.imageViewMindSparkLogo.setVisibility(View.VISIBLE);
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

    private ArrayList<String> getSkillIds(List<Skill> skillIdList) {
        if (skillIdList != null) {
            ArrayList<String> skillIds = new ArrayList<>();
            if (!skillIdList.isEmpty()) {
                for (int i = 0; i < skillIdList.size(); i++) {
                    skillIds.add(skillIdList.get(i).getId());
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
            setRewardPoint(course.getTotalMarks(), holder.mBinding.layoutReward, holder.mBinding.textViewRewardPoints);
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

        private void setRewardPoint(int totalMarks, LinearLayoutCompat layoutReward, AppCompatTextView textViewRewardPoints) {
            if (totalMarks > 0) {
                layoutReward.setVisibility(View.VISIBLE);
                textViewRewardPoints.setText(String.valueOf(totalMarks));
            } else {
                layoutReward.setVisibility(View.GONE);
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
            setRewardPoint(course.getTotalMarks(), holder.mBinding.layoutReward, holder.mBinding.textViewRewardPoints);

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

        private void setRewardPoint(int totalMarks, LinearLayoutCompat layoutReward, AppCompatTextView textViewRewardPoints) {
            if (totalMarks > 0) {
                layoutReward.setVisibility(View.VISIBLE);
                textViewRewardPoints.setText(String.valueOf(totalMarks));
            } else {
                layoutReward.setVisibility(View.GONE);
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

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(course.getId())) {

                        ArrayList<String> skillIds = getSkillIds(course.getMetaInformation().getSkills());
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

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutMicroCourseListItemBinding mBinding;

            ViewHolder(LayoutMicroCourseListItemBinding binding) {
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
            if (mindSparkTopicData.getMindSparkUnitList() != null && !mindSparkTopicData.getMindSparkUnitList().isEmpty()) {
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
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        if (mindSparkContentDetails.getUnitsOverall() > 0) {
                            startActivity(MindSparkPlayerActivity.getStartIntent(mContext, mindSparkContentDetails.getContentId(), mindSparkContentDetails.getContentName()));

                        } else {
                            mRxBus.send(new MindSparkNoUnitEvent());
                        }
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
            LayoutVideoListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_video_list_item, parent, false);
            return new ApplyAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ApplyAdapter.ViewHolder holder, int position) {
            final AboutCourseMinimal course = mList.get(position);

            setThumbnail(course.getThumbnail(), holder.mBinding.imageViewBackground);
            setRewardPoint(course.getTotalMarks(), holder.mBinding.layoutReward, holder.mBinding.textViewRewardPoints);
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

        private void setRewardPoint(int totalMarks, LinearLayoutCompat layoutReward, AppCompatTextView textViewRewardPoints) {
            if (totalMarks > 0) {
                layoutReward.setVisibility(View.VISIBLE);
                textViewRewardPoints.setText(String.valueOf(totalMarks));
            } else {
                layoutReward.setVisibility(View.GONE);
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

        /*public void addValues(ArrayList<Training> list) {
            if (mList != null) {
                mList.addAll(list);
                notifyDataSetChanged();
            }
        }*/

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutVideoListItemBinding mBinding;

            public ViewHolder(LayoutVideoListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

}
