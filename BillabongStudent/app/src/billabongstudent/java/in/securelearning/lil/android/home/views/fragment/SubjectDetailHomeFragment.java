package in.securelearning.lil.android.home.views.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSubjectDetailsHomeFragmentBinding;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.gamification.event.GamificationEventDone;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.MindSparkNoUnitEvent;
import in.securelearning.lil.android.home.helper.OnStartPracticeActivityListener;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.views.activity.WikiHowListActivity;
import in.securelearning.lil.android.home.views.adapter.LRAAdapter;
import in.securelearning.lil.android.home.views.adapter.PracticeAdapter;
import in.securelearning.lil.android.player.view.activity.PracticePlayerActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPARequest;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHow;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkContentDetails;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkTopicData;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkUnitData;
import in.securelearning.lil.android.thirdparty.model.ThirdPartyModel;
import in.securelearning.lil.android.thirdparty.views.activity.MindSparkAllTopicListActivity;
import in.securelearning.lil.android.thirdparty.views.activity.MindSparkPlayerActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubjectDetailHomeFragment extends Fragment {

    private static final String TOPIC_ID = "topicId";
    private static final String SUBJECT_ID = "subjectId";
    private static final String TOPIC_NAME = "topicName";
    private static final String SUBJECT_NAME = "subjectName";
    private static final String GRADE_NAME = "gradeName";
    private static final String THIRD_PARTY_TOPIC_ID_LIST = "thirdPartyTopicIdList";
    private static final String SUBJECT_BANNER_URL = "subjectBannerUrl";

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    @Inject
    ThirdPartyModel mThirdPartyModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AppUserModel mAppUserModel;

    LayoutSubjectDetailsHomeFragmentBinding mBinding;
    private Context mContext;
    private String mSubjectName;
    private String mGradeName;
    private String mSubjectBannerURL;
    private String mTopicName;
    private String mSubjectId;
    private boolean mIsEnglish;

    public SubjectDetailHomeFragment() {
        // Required empty public constructor
    }


    public static SubjectDetailHomeFragment newInstance(String subjectId, String topicId, String topicName, String subjectName, String gradeName, ArrayList<String> thirdPartyTopicIds, String bannerUrl) {
        SubjectDetailHomeFragment fragment = new SubjectDetailHomeFragment();
        Bundle args = new Bundle();
        args.putString(SUBJECT_ID, subjectId);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_subject_details_home_fragment, container, false);

        listenRxBusEvents();
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        getBundleData();
    }

    /*To get bundle data for activity*/
    private void getBundleData() {
        if (getArguments() != null) {
            String topicId = getArguments().getString(TOPIC_ID);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mTopicName = getArguments().getString(TOPIC_NAME);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mGradeName = getArguments().getString(GRADE_NAME);
            mSubjectBannerURL = getArguments().getString(SUBJECT_BANNER_URL);

            mIsEnglish = isEnglishSubject();

            if (!TextUtils.isEmpty(topicId)) {
                fetchLearnList(topicId);
            }
        }
    }

    /*To check current subject is English
     * and grade is below IV to show Freadom card in practice.*/
    private boolean isEnglishSubject() {
        if (mSubjectName.contains("Eng")) {
            return mGradeName.equals("PG")
                    || mGradeName.equals("EuroJunior")
                    || mGradeName.equals("EuroSenior")
                    || mGradeName.equals("EJ")
                    || mGradeName.equals("ES")
                    || mGradeName.equals("Nursery")
                    || mGradeName.equals("I")
                    || mGradeName.equals("II")
                    || mGradeName.equals("III");
        } else {
            return false;
        }
    }

    /*To listen rx events for the activity*/
    private void listenRxBusEvents() {
        Disposable subscription = mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object event) {
                        if (event instanceof GamificationEventDone) {
                            GamificationEventDone gamificationEventDone = (GamificationEventDone) event;
                            if (gamificationEventDone.getEventActivity().
                                    equalsIgnoreCase("LRPA") &&
                                    gamificationEventDone.getSubActivity().equalsIgnoreCase("practise")
                                    && gamificationEventDone.isDone()) {
                                if (GamificationPrefs.getPractiseObject(mContext) != null) {
                                    AboutCourseMinimal courseMinimal = GamificationPrefs.getPractiseObject(mContext);
                                    startPracticeActivity(courseMinimal);
                                }
                                if (mContext != null) {
                                    GamificationPrefs.clearPractiseObject(mContext);
                                }

                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
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
                    public void accept(LRPAResult lrpaResult) {
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
                    public void accept(Throwable throwable) {
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
                    public void accept(LRPAResult lrpaResult) {
                        fetchPracticeList(topicId);
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
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        fetchPracticeList(topicId);
                        mBinding.listReinforce.setVisibility(View.GONE);
                        mBinding.progressBarReinforce.setVisibility(View.GONE);
                        mBinding.textViewNoDataReinforce.setVisibility(View.VISIBLE);
                        mBinding.textViewNoDataReinforce.setText(throwable.getMessage());
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
                    public void accept(LRPAResult lrpaResult) {
                        fetchApplyList(topicId);
                        if (lrpaResult != null) {
                            mBinding.listPractice.setVisibility(View.VISIBLE);
                            mBinding.textViewNoDataPractice.setVisibility(View.GONE);

                            ArrayList<AboutCourseMinimal> list = lrpaResult.getResults();
                            ArrayList<MetaInformation> metaInformationList = new ArrayList<>();
                            boolean isPractice = false;

                            if (list != null && !list.isEmpty()) {
                                for (Iterator<AboutCourseMinimal> it = list.iterator(); it.hasNext(); ) {
                                    AboutCourseMinimal course = it.next();
                                    if (!TextUtils.isEmpty(course.getMicroCourseType())
                                            && course.getMicroCourseType().equals("quiz")) {
                                        metaInformationList.add(course.getMetaInformation());
                                        isPractice = true;

                                        it.remove();
                                        list.remove(course);
                                    }
                                }

                                /*Create a single object of practice and insert into array at 0*/
                                if (isPractice && !mIsEnglish) {

                                    AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                    aboutCourseMinimal.setTitle(mTopicName);
                                    aboutCourseMinimal.setCourseType(mContext.getString(R.string.labelPractice).toLowerCase());

                                    if (!metaInformationList.isEmpty()) {
                                        aboutCourseMinimal.setMetaInformation(metaInformationList.get(0));
                                    }

                                    Thumbnail thumbnail = new Thumbnail();
                                    if (!TextUtils.isEmpty(mSubjectBannerURL)) {
                                        thumbnail.setUrl(mSubjectBannerURL);
                                        aboutCourseMinimal.setThumbnail(thumbnail);
                                    }

                                    list.add(0, aboutCourseMinimal);
                                    initializeRecyclerViewPractice(list);

                                } else if (mIsEnglish) {
                                    AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                    aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
                                    list.add(0, aboutCourseMinimal);
                                    initializeRecyclerViewPractice(list);
                                } else {
                                    mBinding.listPractice.setVisibility(View.GONE);
                                    mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                                }

                            } else if (mIsEnglish) {
                                ArrayList<AboutCourseMinimal> courseList = new ArrayList<>();

                                AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
                                courseList.add(0, aboutCourseMinimal);

                                initializeRecyclerViewPractice(courseList);

                            } else {
                                mBinding.listPractice.setVisibility(View.GONE);
                                mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                            }


                        } else if (mIsEnglish) {
                            ArrayList<AboutCourseMinimal> courseList = new ArrayList<>();

                            AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                            aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
                            courseList.add(0, aboutCourseMinimal);

                            initializeRecyclerViewPractice(courseList);

                        }
//                        else if (mSubjectName.contains("Math")) {
//                            fetchMindSparkPractice(new ArrayList<AboutCourseMinimal>(), mThirdPartyTopicIds, topicId);
//                        }
                        else {
                            mBinding.listPractice.setVisibility(View.GONE);
                            mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                        }
                        mBinding.progressBarPractice.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
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
    private void fetchApplyList(final String topicId) {
        mBinding.listApply.setVisibility(View.GONE);
        mBinding.progressBarApply.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataApply.setVisibility(View.GONE);
        mFlavorHomeModel.fetchLRPA(topicId, LRPARequest.TYPE_APPLY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LRPAResult>() {
                    @Override
                    public void accept(LRPAResult lrpaResult) {

                        if (lrpaResult != null && lrpaResult.getResults() != null
                                && !lrpaResult.getResults().isEmpty()) {

                            fetchWikiHowMapping(mSubjectId, topicId, lrpaResult.getResults());
                        } else {
                            fetchWikiHowMapping(mSubjectId, topicId, new ArrayList<AboutCourseMinimal>());

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        fetchWikiHowMapping(mSubjectId, topicId, new ArrayList<AboutCourseMinimal>());
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void fetchWikiHowMapping(String subjectId, String topicId, final ArrayList<AboutCourseMinimal> applyList) {
        mFlavorHomeModel.fetchThirdPartyMapping(new ThirdPartyMapping(subjectId, topicId, mContext.getString(R.string.type_wikihow)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<String>>() {
                    @Override
                    public void accept(ArrayList<String> strings) {
                        if (strings != null && !strings.isEmpty()) {
                            fetchWikiHowCardDetail(strings.get(0), strings, applyList);
                        } else {
                            mBinding.progressBarApply.setVisibility(View.GONE);
                            if (applyList != null && !applyList.isEmpty()) {
                                mBinding.listApply.setVisibility(View.VISIBLE);
                                mBinding.textViewNoDataApply.setVisibility(View.GONE);
                                initializeRecyclerViewApply(applyList);
                            } else {
                                mBinding.listApply.setVisibility(View.GONE);
                                mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        mBinding.progressBarApply.setVisibility(View.GONE);
                        if (applyList != null && !applyList.isEmpty()) {
                            mBinding.listApply.setVisibility(View.VISIBLE);
                            mBinding.textViewNoDataApply.setVisibility(View.GONE);
                            initializeRecyclerViewApply(applyList);
                        } else {
                            mBinding.listApply.setVisibility(View.GONE);
                            mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void fetchWikiHowCardDetail(String wikiHowId, final ArrayList<String> wikiHowTopicIds, final ArrayList<AboutCourseMinimal> applyList) {
        mFlavorHomeModel.fetchWikiHowCardDetail(wikiHowId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WikiHowParent>() {
                    @Override
                    public void accept(WikiHowParent wikiHowParent) {
                        mBinding.progressBarApply.setVisibility(View.GONE);

                        if (wikiHowParent != null && wikiHowParent.getWikiHow() != null) {
                            WikiHow wikiHow = wikiHowParent.getWikiHow();
                            AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                            aboutCourseMinimal.setTitle(wikiHow.getTitle());
                            aboutCourseMinimal.setId(wikiHow.getId());
                            aboutCourseMinimal.setCourseType(mContext.getString(R.string.label_wikiHow));
                            if (wikiHow.getThumbnail() != null) {
                                Thumbnail thumbnail = new Thumbnail();
                                if (!TextUtils.isEmpty(wikiHow.getThumbnail().getThumb())) {
                                    thumbnail.setUrl(wikiHow.getThumbnail().getThumb());
                                }
                                if (!TextUtils.isEmpty(wikiHow.getThumbnail().getThumbLarge())) {
                                    thumbnail.setThumb(wikiHow.getThumbnail().getThumbLarge());

                                }
                                aboutCourseMinimal.setThumbnail(thumbnail);

                            }

                            applyList.add(0, aboutCourseMinimal);

                            if (wikiHowTopicIds.size() > 1) {
                                mBinding.imageViewWikiHowLogo.setVisibility(View.VISIBLE);
                                mBinding.buttonMoreApply.setVisibility(View.VISIBLE);
                            }

                            mBinding.buttonMoreApply.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (GeneralUtils.isNetworkAvailable(mContext)) {

                                        mContext.startActivity(WikiHowListActivity.getStartIntent(mContext, mTopicName, wikiHowTopicIds));
                                    } else {
                                        GeneralUtils.showToastShort(mContext, getString(R.string.connect_internet));
                                    }
                                }
                            });

                            if (!applyList.isEmpty()) {
                                mBinding.listApply.setVisibility(View.VISIBLE);
                                mBinding.textViewNoDataApply.setVisibility(View.GONE);
                                initializeRecyclerViewApply(applyList);
                            } else {
                                mBinding.listApply.setVisibility(View.GONE);
                                mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (!applyList.isEmpty()) {
                                mBinding.listApply.setVisibility(View.VISIBLE);
                                mBinding.textViewNoDataApply.setVisibility(View.GONE);
                                initializeRecyclerViewApply(applyList);
                            } else {
                                mBinding.listApply.setVisibility(View.GONE);
                                mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        if (!applyList.isEmpty()) {
                            mBinding.listApply.setVisibility(View.VISIBLE);
                            mBinding.textViewNoDataApply.setVisibility(View.GONE);
                            initializeRecyclerViewApply(applyList);
                        } else {
                            mBinding.listApply.setVisibility(View.GONE);
                            mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void fetchMindSparkPractice(final ArrayList<AboutCourseMinimal> list, final ArrayList<String> thirdPartyTopicIds, final String topicId) {
        final ArrayList<AboutCourseMinimal> courseList = new ArrayList<>();
        mBinding.progressBarPractice.setVisibility(View.VISIBLE);
        if (thirdPartyTopicIds != null && !thirdPartyTopicIds.isEmpty()) {
            mThirdPartyModel.loginUserToMindSpark(thirdPartyTopicIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<MindSparkLoginResponse>>() {
                        @Override
                        public void accept(ArrayList<MindSparkLoginResponse> responseArrayList) {

                            if (responseArrayList != null && !responseArrayList.isEmpty()) {

                                for (int i = 0; i < responseArrayList.size(); i++) {

                                    AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();

                                    final MindSparkLoginResponse response = responseArrayList.get(i);
                                    final MindSparkContentDetails mindSparkContentDetails = response.getMindSparkContentDetails();
                                    MindSparkTopicData mindSparkTopicData = response.getMindSparkTopicData();
                                    if (mindSparkTopicData.getMindSparkUnitList() != null && !mindSparkTopicData.getMindSparkUnitList().isEmpty()) {
                                        MindSparkUnitData mindSparkUnitData = mindSparkTopicData.getMindSparkUnitList().get(0);
                                        aboutCourseMinimal.setMicroCourseType(mindSparkUnitData.getUnitName());
                                    }

                                    String value;
                                    if (mindSparkContentDetails.getUnitsCleared() > 1) {
                                        value = mindSparkContentDetails.getUnitsCleared() + " out of " + mindSparkContentDetails.getUnitsOverall() + " units covered";
                                    } else {
                                        value = mindSparkContentDetails.getUnitsCleared() + " out of " + mindSparkContentDetails.getUnitsOverall() + " unit covered";
                                    }
                                    aboutCourseMinimal.setColorCode(value);
                                    aboutCourseMinimal.setId(mindSparkContentDetails.getContentId());
                                    aboutCourseMinimal.setTitle(mindSparkContentDetails.getContentName());
                                    aboutCourseMinimal.setCourseType(mContext.getString(R.string.mindspark));
                                    aboutCourseMinimal.setTotalMarks(mindSparkContentDetails.getUnitsOverall());
                                    courseList.add(aboutCourseMinimal);
                                }

                                for (int i = 0; i < courseList.size(); i++) {
                                    list.add(i, courseList.get(i));
                                }

                                if (!list.isEmpty()) {
                                    mBinding.listPractice.setVisibility(View.VISIBLE);
                                    mBinding.buttonMorePractice.setVisibility(View.VISIBLE);
                                    mBinding.imageViewMindSparkLogo.setVisibility(View.VISIBLE);
                                    initializeRecyclerViewPractice(list);
                                    mBinding.buttonMorePractice.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mContext.startActivity(MindSparkAllTopicListActivity.getStartIntent(getContext()));
                                        }
                                    });

                                } else {
                                    mBinding.listPractice.setVisibility(View.GONE);
                                    mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mBinding.buttonMorePractice.setVisibility(View.GONE);
                                mBinding.imageViewMindSparkLogo.setVisibility(View.GONE);
                                if (!list.isEmpty()) {
                                    mBinding.listPractice.setVisibility(View.VISIBLE);
                                    mBinding.textViewNoDataPractice.setVisibility(View.GONE);
                                    initializeRecyclerViewPractice(list);
                                } else {
                                    mBinding.listPractice.setVisibility(View.GONE);
                                    mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                                }
                            }

                            mBinding.progressBarPractice.setVisibility(View.GONE);

                        }


                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            mBinding.progressBarPractice.setVisibility(View.GONE);
                            mBinding.listPractice.setVisibility(View.GONE);
                            mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            if (!list.isEmpty()) {
                mBinding.listPractice.setVisibility(View.VISIBLE);
                mBinding.textViewNoDataPractice.setVisibility(View.GONE);
                initializeRecyclerViewPractice(list);
            } else {
                mBinding.listPractice.setVisibility(View.GONE);
                mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
            }
        }

    }

    /*initialization of Learn Adapter and Recycler view  */
    private void initializeRecyclerViewLearn(ArrayList<AboutCourseMinimal> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listLearn.setLayoutManager(layoutManager);
        LRAAdapter LRAAdapter = new LRAAdapter(mContext, list);
        mBinding.listLearn.setAdapter(LRAAdapter);

    }

    /*initialization of Reinforce Adapter and Recycler view  */
    private void initializeRecyclerViewReinforce(ArrayList<AboutCourseMinimal> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listReinforce.setLayoutManager(layoutManager);
        LRAAdapter reinforceAdapter = new LRAAdapter(mContext, list);
        mBinding.listReinforce.setAdapter(reinforceAdapter);

    }

    /*initialization of Practice Adapter and Recycler view  */
    private void initializeRecyclerViewPractice(ArrayList<AboutCourseMinimal> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listPractice.setLayoutManager(layoutManager);

        OnStartPracticeActivityListener startPracticeActivityListener = new OnStartPracticeActivityListener() {
            @Override
            public void OnStartPracticeActivity(AboutCourseMinimal aboutCourseMinimal) {
                startPracticeActivity(aboutCourseMinimal);
            }
        };
        mBinding.listPractice.setAdapter(new PracticeAdapter(mContext, list, mSubjectName, startPracticeActivityListener));

    }

    /*initialization of Apply Adapter and Recycler view  */
    private void initializeRecyclerViewApply(ArrayList<AboutCourseMinimal> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.listApply.setLayoutManager(layoutManager);
        LRAAdapter applyAdapter = new LRAAdapter(mContext, list);
        mBinding.listApply.setAdapter(applyAdapter);

    }

    /*get subjectId from course metaInformation*/
    private String getSubjectId(MetaInformation metaInformation) {

        if (metaInformation != null && metaInformation.getSubject() != null && !TextUtils.isEmpty(metaInformation.getSubject().getId())) {
            return metaInformation.getSubject().getId();
        } else {
            return null;
        }

    }

    /*get topicId from course metaInformation*/
    private String getTopicId(MetaInformation metaInformation) {

        if (metaInformation != null && metaInformation.getTopic() != null && !TextUtils.isEmpty(metaInformation.getTopic().getId())) {
            return metaInformation.getTopic().getId();
        } else {
            return null;
        }

    }

    /*To start practice activity from various events*/
    private void startPracticeActivity(AboutCourseMinimal course) {
        if (course != null && !TextUtils.isEmpty(course.getCourseType()) && mContext != null) {

            if (course.getCourseType().equals(mContext.getString(R.string.freadom))) {
                String jwt = AppPrefs.getIdToken(mContext);
                String email = mAppUserModel.getApplicationUser().getEmail();

                try {
                    CustomChromeTabHelper.loadCustomDataUsingColorResource(mContext, mContext.getString(R.string.freadom_url) + "?email=" + email + "&token=" + jwt, R.color.colorPrimary);
                } catch (Exception e) {
                    e.printStackTrace();
                    GeneralUtils.showToastLong(mContext, getString(R.string.chrome_warning_message));
                }

            } else if (course.getCourseType().equals(mContext.getString(R.string.lightsail))) {

                String jwt = AppPrefs.getIdToken(mContext);

                try {
                    CustomChromeTabHelper.loadCustomDataUsingColorResource(mContext, mContext.getString(R.string.base_url_lightsail) + jwt, R.color.colorPrimary);
                } catch (Exception e) {
                    e.printStackTrace();
                    GeneralUtils.showToastLong(mContext, getString(R.string.chrome_warning_message));
                }

            } else if (course.getCourseType().equals(mContext.getString(R.string.mindspark))) {
                if (course.getTotalMarks() > 0) {
                    mContext.startActivity(MindSparkPlayerActivity.getStartIntent(mContext, course.getId(), course.getTitle()));
                } else {
                    mRxBus.send(new MindSparkNoUnitEvent());
                }
            } else if (course.getCourseType().equals(mContext.getString(R.string.labelPractice).toLowerCase())) {
                String subjectId = getSubjectId(course.getMetaInformation());
                String topicId = getTopicId(course.getMetaInformation());

                mContext.startActivity(PracticePlayerActivity.getStartIntent(mContext, course.getTitle(), subjectId, topicId));
            } else {
                final Class finalObjectClass = mFlavorHomeModel.getCourseClass(course);
                WebPlayerCordovaLiveActivity.startWebPlayer(mContext, course.getId(), ConstantUtil.BLANK, ConstantUtil.BLANK, finalObjectClass, ConstantUtil.BLANK, false);
            }
        }

    }


}

