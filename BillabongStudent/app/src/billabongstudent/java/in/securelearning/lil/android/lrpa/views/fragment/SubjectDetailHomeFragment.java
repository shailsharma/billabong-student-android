package in.securelearning.lil.android.lrpa.views.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSubjectDetailsHomeFragmentBinding;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.dataobjects.Board;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.events.QuizCompletedEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.gamification.event.GamificationEventDone;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.helper.OnStartPracticeActivityListener;
import in.securelearning.lil.android.lrpa.model.LRPAModel;
import in.securelearning.lil.android.lrpa.views.adapter.LRAAdapter;
import in.securelearning.lil.android.lrpa.views.adapter.PracticeAdapter;
import in.securelearning.lil.android.player.view.activity.PracticePlayerActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPARequest;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHow;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkContentDetails;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkTopicData;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkUnitData;
import in.securelearning.lil.android.thirdparty.dataobjects.TPCurriculumResponse;
import in.securelearning.lil.android.thirdparty.events.MindSparkNoUnitEvent;
import in.securelearning.lil.android.thirdparty.model.ThirdPartyModel;
import in.securelearning.lil.android.thirdparty.views.activity.MindSparkAllTopicListActivity;
import in.securelearning.lil.android.thirdparty.views.activity.MindSparkPlayerActivity;
import in.securelearning.lil.android.thirdparty.views.activity.WikiHowListActivity;
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
    private static final String SUBJECT_BANNER_URL = "subjectBannerUrl";

    @Inject
    LRPAModel mLRPAModel;

    @Inject
    ThirdPartyModel mThirdPartyModel;

    @Inject
    RxBus mRxBus;

    @Inject
    AppUserModel mAppUserModel;

    LayoutSubjectDetailsHomeFragmentBinding mBinding;
    private Context mContext;
    private String mSubjectName;
    private String mSubjectBannerURL;
    private String mTopicName;
    private String mSubjectId;
    Disposable mDisposable;

    public SubjectDetailHomeFragment() {
        // Required empty public constructor
    }


    public static SubjectDetailHomeFragment newInstance(String subjectId, String topicId,
                                                        String topicName, String subjectName,
                                                        String bannerUrl) {
        SubjectDetailHomeFragment fragment = new SubjectDetailHomeFragment();
        Bundle args = new Bundle();
        args.putString(SUBJECT_ID, subjectId);
        args.putString(TOPIC_ID, topicId);
        args.putString(TOPIC_NAME, topicName);
        args.putString(SUBJECT_NAME, subjectName);
        args.putString(SUBJECT_BANNER_URL, bannerUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_subject_details_home_fragment, container, false);

        getBundleData();
        listenRxBusEvents();

        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }

    /*To get bundle data for activity*/
    private void getBundleData() {
        if (getArguments() != null) {
            String topicId = getArguments().getString(TOPIC_ID);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mTopicName = getArguments().getString(TOPIC_NAME);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mSubjectBannerURL = getArguments().getString(SUBJECT_BANNER_URL);

            if (!TextUtils.isEmpty(topicId)) {
                fetchLearnList(topicId);
            }
        }
    }


    /*To listen rx events for the activity*/
    private void listenRxBusEvents() {
        mDisposable = mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object event) {

                        if (event instanceof GamificationEventDone) {

                            GamificationEventDone gamificationEventDone = (GamificationEventDone) event;

                            if (gamificationEventDone.getEventActivity().equalsIgnoreCase("LRPA")
                                    && gamificationEventDone.getSubActivity().equalsIgnoreCase("practise")
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

                        /*if (event instanceof RefreshLRPAAccordingToType) {
                            String lrpaType = ((RefreshLRPAAccordingToType) event).getLRPAType();
                            getBundleData();
                        }*/
                        if (event instanceof QuizCompletedEvent) {
                            getBundleData();
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

        mLRPAModel.fetchLRPA(topicId, LRPARequest.TYPE_LEARN)
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

        mLRPAModel.fetchLRPA(topicId, LRPARequest.TYPE_REINFORCE)
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

        mLRPAModel.fetchLRPA(topicId, LRPARequest.TYPE_PRACTICE)
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
                                if (isPractice) {

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

                                }
//                                else if (mIsEnglish) {
//                                    AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
//                                    aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
//                                    list.add(0, aboutCourseMinimal);
//                                    initializeRecyclerViewPractice(list);
//                                }
                                else {
                                    mBinding.listPractice.setVisibility(View.GONE);
                                    mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                                }

                            }
//                            else if (mIsEnglish) {
//                                ArrayList<AboutCourseMinimal> courseList = new ArrayList<>();
//
//                                AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
//                                aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
//                                courseList.add(0, aboutCourseMinimal);
//
//                                initializeRecyclerViewPractice(courseList);
//
//                            }
                            else {
                                mBinding.listPractice.setVisibility(View.GONE);
                                mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                            }


                        }
//                        else if (mIsEnglish) {
//                            ArrayList<AboutCourseMinimal> courseList = new ArrayList<>();
//
//                            AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
//                            aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
//                            courseList.add(0, aboutCourseMinimal);
//
//                            initializeRecyclerViewPractice(courseList);
//
//                        }
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

        mLRPAModel.fetchLRPA(topicId, LRPARequest.TYPE_APPLY)
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
    private void fetchWikiHowMapping(final String subjectId, final String topicId, final ArrayList<AboutCourseMinimal> applyList) {

        mLRPAModel.fetchThirdPartyMapping(new ThirdPartyMapping(subjectId, topicId, mContext.getString(R.string.type_wikihow)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<String>>() {
                    @Override
                    public void accept(ArrayList<String> strings) {

                        if (strings != null && !strings.isEmpty()) {
                            fetchWikiHowCardDetail(strings.get(0), strings, applyList, subjectId, topicId);
                        } else {
                            getGeoGebraCardDetail(subjectId, topicId, applyList);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();

                        getGeoGebraCardDetail(subjectId, topicId, applyList);

                    }
                });
    }

    @SuppressLint("CheckResult")
    private void fetchWikiHowCardDetail(String wikiHowId, final ArrayList<String> wikiHowTopicIds,
                                        final ArrayList<AboutCourseMinimal> applyList,
                                        final String subjectId, final String topicId) {

        mThirdPartyModel.fetchWikiHowCardDetail(wikiHowId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WikiHowParent>() {
                    @Override
                    public void accept(WikiHowParent wikiHowParent) {

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

                        }

                        getGeoGebraCardDetail(subjectId, topicId, applyList);


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                        throwable.printStackTrace();

                        getGeoGebraCardDetail(subjectId, topicId, applyList);

                    }
                });
    }


    /* To fetch Geo-Gebra card detail for Apply
     * Note - This method/api call will happen with all the subjects
     * because for now we are unable to know the received subject is Math related subject*/
    @SuppressLint("CheckResult")
    private void getGeoGebraCardDetail(String subjectId, String topicId, final ArrayList<AboutCourseMinimal> applyList) {

        ThirdPartyMapping thirdPartyMapping = new ThirdPartyMapping(subjectId, topicId);
        thirdPartyMapping.setTPTypeList(new ArrayList<Integer>(Collections.singleton(ConstantUtil.TP_TYPE_GEO_GEBRA)));

        mThirdPartyModel.fetchGeoGebraCardDetail(thirdPartyMapping)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<TPCurriculumResponse>>() {
                    @Override
                    public void accept(ArrayList<TPCurriculumResponse> list) {
                        mBinding.progressBarApply.setVisibility(View.GONE);

                        if (!list.isEmpty()) {

                            applyList.addAll(0, convertGeoGebraListIntoACMList(list));

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

    /*To convert GeoGebra(Third Party) list into About-Course-Minimal list*/
    private ArrayList<AboutCourseMinimal> convertGeoGebraListIntoACMList(ArrayList<TPCurriculumResponse> geoGebraList) {
        ArrayList<AboutCourseMinimal> courseMinimalsGG = new ArrayList<>();

        for (int i = 0; i < geoGebraList.size(); i++) {

            TPCurriculumResponse tpCurriculumGG = geoGebraList.get(i);

            AboutCourseMinimal aboutCourseMinimalGG = new AboutCourseMinimal();

            if (!TextUtils.isEmpty(tpCurriculumGG.getId())) {
                aboutCourseMinimalGG.setId(tpCurriculumGG.getId());
            }
            if (!TextUtils.isEmpty(tpCurriculumGG.getTpTitle())) {
                aboutCourseMinimalGG.setTitle(tpCurriculumGG.getTpTitle());
            }

            if (!TextUtils.isEmpty(tpCurriculumGG.getTpDescription())) {
                aboutCourseMinimalGG.setTPDescription(tpCurriculumGG.getTpDescription());
            }

            MetaInformation metaInformationGG = new MetaInformation();

            if (!TextUtils.isEmpty(tpCurriculumGG.getBoardId())) {
                Board boardGG = new Board();
                boardGG.setId(tpCurriculumGG.getBoardId());
                metaInformationGG.setBoard(boardGG);
            }

            if (!TextUtils.isEmpty(tpCurriculumGG.getGradeId())) {
                Grade gradeGG = new Grade();
                gradeGG.setId(tpCurriculumGG.getGradeId());
                metaInformationGG.setGrade(gradeGG);
            }

            if (!TextUtils.isEmpty(tpCurriculumGG.getSubjectId())) {
                Subject subjectGG = new Subject();
                subjectGG.setId(tpCurriculumGG.getSubjectId());
                metaInformationGG.setSubject(subjectGG);
            }

            Topic topicGG = new Topic();
            if (!TextUtils.isEmpty(tpCurriculumGG.getTopicId())) {
                topicGG.setId(tpCurriculumGG.getTopicId());
            }
            if (!TextUtils.isEmpty(tpCurriculumGG.getTopicName())) {
                topicGG.setName(tpCurriculumGG.getTopicName());
            }
            metaInformationGG.setTopic(topicGG);

            aboutCourseMinimalGG.setMetaInformation(metaInformationGG);
            aboutCourseMinimalGG.setCourseType(getString(R.string.labelGeoGebra));


            if (!TextUtils.isEmpty(tpCurriculumGG.getTpImageUrl())) {
                Thumbnail thumbnail = new Thumbnail();
                thumbnail.setUrl(tpCurriculumGG.getTpImageUrl());
                thumbnail.setThumb(tpCurriculumGG.getTpImageUrl());
                thumbnail.setThumbXL(tpCurriculumGG.getTpImageUrl());

                aboutCourseMinimalGG.setThumbnail(thumbnail);
            }

            if (!TextUtils.isEmpty(tpCurriculumGG.getTpTopicId())) {
                aboutCourseMinimalGG.setTPId(tpCurriculumGG.getTpTopicId());
                courseMinimalsGG.add(0, aboutCourseMinimalGG);
            }


        }

        return courseMinimalsGG;
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
                final Class finalObjectClass = CommonUtils.getInstance().getCourseClass(course);
                WebPlayerCordovaLiveActivity.startWebPlayer(mContext, course.getId(), ConstantUtil.BLANK, ConstantUtil.BLANK, finalObjectClass, ConstantUtil.BLANK, false);
            }
        }

    }


}

