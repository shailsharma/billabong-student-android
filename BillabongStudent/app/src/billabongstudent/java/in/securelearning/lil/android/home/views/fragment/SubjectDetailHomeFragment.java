package in.securelearning.lil.android.home.views.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCourseListItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutMicroCourseListItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutPracticeListItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutSubjectDetailsHomeFragmentBinding;
import in.securelearning.lil.android.base.customchrometabutils.CustomChromeTabHelper;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.event.GamificationEventDone;
import in.securelearning.lil.android.gamification.model.GamificationModel;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.gamification.views.fragment.GamificationDialog;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.MindSparkNoUnitEvent;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.views.activity.WikiHowListActivity;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkContentDetails;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkTopicData;
import in.securelearning.lil.android.mindspark.model.MindSparkModel;
import in.securelearning.lil.android.mindspark.views.activity.MindSparkAllTopicListActivity;
import in.securelearning.lil.android.mindspark.views.activity.MindSparkPlayerActivity;
import in.securelearning.lil.android.player.mindspark.dataobjects.MindSparkUnitData;
import in.securelearning.lil.android.player.view.activity.PracticePlayerActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPARequest;
import in.securelearning.lil.android.syncadapter.dataobjects.LRPAResult;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHow;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectDetailHomeFragment extends Fragment {

    private static final String TOPIC_ID = "topicId";
    private static final String SUBJECT_ID = "subjectId";
    private static final String TOPIC_NAME = "topicName";
    private static final String SUBJECT_NAME = "subjectName";
    private static final String GRADE_NAME = "gradeName";
    private static final String THIRD_PARTY_TOPIC_ID_LIST = "thirdPartyTopicIdList";
    private static final String SUBJECT_BANNER_URL = "subjectBannerUrl";
    @Inject
    public GamificationDialog mGamificationDialog;
    @Inject
    FlavorHomeModel mFlavorHomeModel;
    @Inject
    MindSparkModel mMindSparkModel;
    @Inject
    RxBus mRxBus;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    GamificationModel mGamificationModel;
    LayoutSubjectDetailsHomeFragmentBinding mBinding;
    private Context mContext;
    private String mSubjectName;
    private String mGradeName;
    private String mSubjectBannerURL;
    private ArrayList<String> mThirdPartyTopicIds;
    private String mTopicName;
    private String mSubjectId;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        mContext = getActivity();
    }


    @Override
    public void onResume() {
        super.onResume();
        getBundleData();
    }

    private void getBundleData() {
        if (getArguments() != null) {
            String topicId = getArguments().getString(TOPIC_ID);
            mSubjectId = getArguments().getString(SUBJECT_ID);
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
                    public void accept(Throwable throwable) throws Exception {
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
                    public void accept(LRPAResult lrpaResult) throws Exception {
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

                                boolean isMathOrEnglish;
                                if (mSubjectName.contains("Eng")) {
                                    isMathOrEnglish = true;
                                } else {
                                    isMathOrEnglish = mSubjectName.contains("Math");
                                }

                                /*Create a single object of practice and insert into array at 0*/

                                if (isPractice && !isMathOrEnglish) {

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

                                } else {
                                    if (mSubjectName.contains("Eng")) {
                                        if (mGradeName.equals("PG") || mGradeName.equals("EuroJunior") || mGradeName.equals("EuroSenior")
                                                || mGradeName.equals("EJ") || mGradeName.equals("ES")
                                                || mGradeName.equals("Nursery") || mGradeName.equals("I")
                                                || mGradeName.equals("II") || mGradeName.equals("III")) {

                                            AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                            aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
                                            list.add(0, aboutCourseMinimal);
                                        } else {
                                            AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                            aboutCourseMinimal.setCourseType(mContext.getString(R.string.lightsail));
                                            list.add(0, aboutCourseMinimal);
                                        }
                                        initializeRecyclerViewPractice(list);

                                    } else if (mSubjectName.contains("Math")) {
                                        fetchMindSparkPractice(list, mThirdPartyTopicIds, topicId);
                                    } else {
                                        mBinding.listPractice.setVisibility(View.GONE);
                                        mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                                    }
                                }

                            } else if (mSubjectName.contains("Eng")) {
                                ArrayList<AboutCourseMinimal> courseList = new ArrayList<>();
                                if (mGradeName.equals("PG") || mGradeName.equals("EuroJunior") || mGradeName.equals("EuroSenior")
                                        || mGradeName.equals("Nursery") || mGradeName.equals("I")
                                        || mGradeName.equals("II") || mGradeName.equals("III")) {

                                    AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                    aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
                                    courseList.add(0, aboutCourseMinimal);

                                } else {
                                    AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                    aboutCourseMinimal.setCourseType(mContext.getString(R.string.lightsail));
                                    courseList.add(0, aboutCourseMinimal);

                                }
                                initializeRecyclerViewPractice(courseList);

                            } else if (mSubjectName.contains("Math")) {
                                fetchMindSparkPractice(new ArrayList<AboutCourseMinimal>(), mThirdPartyTopicIds, topicId);
                            } else {
                                mBinding.listPractice.setVisibility(View.GONE);
                                mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                            }


                        } else if (mSubjectName.contains("Eng")) {
                            ArrayList<AboutCourseMinimal> courseList = new ArrayList<>();
                            if (mGradeName.equals("PG") || mGradeName.equals("EuroJunior") || mGradeName.equals("EuroSenior")
                                    || mGradeName.equals("Nursery") || mGradeName.equals("I")
                                    || mGradeName.equals("II") || mGradeName.equals("III")) {

                                AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                aboutCourseMinimal.setCourseType(mContext.getString(R.string.freadom));
                                courseList.add(0, aboutCourseMinimal);

                            } else {
                                AboutCourseMinimal aboutCourseMinimal = new AboutCourseMinimal();
                                aboutCourseMinimal.setCourseType(mContext.getString(R.string.lightsail));
                                courseList.add(0, aboutCourseMinimal);

                            }
                            initializeRecyclerViewPractice(courseList);

                        } else if (mSubjectName.contains("Math")) {
                            fetchMindSparkPractice(new ArrayList<AboutCourseMinimal>(), mThirdPartyTopicIds, topicId);
                        } else {
                            mBinding.listPractice.setVisibility(View.GONE);
                            mBinding.textViewNoDataPractice.setVisibility(View.VISIBLE);
                        }
                        mBinding.progressBarPractice.setVisibility(View.GONE);

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
    private void fetchApplyList(final String topicId) {
        mBinding.listApply.setVisibility(View.GONE);
        mBinding.progressBarApply.setVisibility(View.VISIBLE);
        mBinding.textViewNoDataApply.setVisibility(View.GONE);
        mFlavorHomeModel.fetchLRPA(topicId, LRPARequest.TYPE_APPLY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LRPAResult>() {
                    @Override
                    public void accept(LRPAResult lrpaResult) throws Exception {
                        //mBinding.progressBarApply.setVisibility(View.GONE);

                        if (lrpaResult != null && lrpaResult.getResults() != null
                                && !lrpaResult.getResults().isEmpty()) {

                            fetchWikiHowMapping(mSubjectId, topicId, lrpaResult.getResults());
//                            mBinding.listApply.setVisibility(View.VISIBLE);
//                            mBinding.textViewNoDataApply.setVisibility(View.GONE);
//                            initializeRecyclerViewApply(lrpaResult.getResults());
                        } else {
                            fetchWikiHowMapping(mSubjectId, topicId, new ArrayList<AboutCourseMinimal>());
//                            mBinding.listApply.setVisibility(View.GONE);
//                            mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        fetchWikiHowMapping(mSubjectId, topicId, new ArrayList<AboutCourseMinimal>());

//                        mBinding.listApply.setVisibility(View.GONE);
//                        mBinding.progressBarApply.setVisibility(View.GONE);
//                        mBinding.textViewNoDataApply.setVisibility(View.VISIBLE);
//                        mBinding.textViewNoDataApply.setText(throwable.getMessage());
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
                    public void accept(ArrayList<String> strings) throws Exception {
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
                    public void accept(Throwable throwable) throws Exception {
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
                    public void accept(WikiHowParent wikiHowParent) throws Exception {
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

                                        startActivity(WikiHowListActivity.getStartIntent(mContext, mTopicName, wikiHowTopicIds));
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
                    public void accept(Throwable throwable) throws Exception {
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
            mMindSparkModel.loginUserToMindSpark(thirdPartyTopicIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<MindSparkLoginResponse>>() {
                        @Override
                        public void accept(ArrayList<MindSparkLoginResponse> responseArrayList) throws Exception {

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
                                            startActivity(MindSparkAllTopicListActivity.getStartIntent(getContext()));
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
                        public void accept(Throwable throwable) throws Exception {
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

    private void setCoverage(float coverage, String colorCode, ProgressBar progressBar) {
        if (coverage > 0) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress((int) coverage);
            progressBar.setProgressTintList(ColorStateList.valueOf((Color.parseColor(colorCode))));
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setRewardPoint(int totalMarks, float coverage, String colorCode, LinearLayoutCompat layoutReward, AppCompatTextView textViewRewardPoints, AppCompatTextView viewCourseStatus) {
        if (totalMarks > 0) {
            layoutReward.setVisibility(View.VISIBLE);
            textViewRewardPoints.setText(String.valueOf(totalMarks));
            viewCourseStatus.setVisibility(View.VISIBLE);

            if (coverage > 0) {
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(mContext, R.drawable.circle_solid_primary);
                assert unwrappedDrawable != null;
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor(colorCode));

                viewCourseStatus.setBackgroundDrawable(unwrappedDrawable);
            } else {
                viewCourseStatus.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.circle_solid_primary_outlined));

            }
        } else {
            layoutReward.setVisibility(View.GONE);
        }
    }

    private void openPracticeActivity(AboutCourseMinimal course) {
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
                    startActivity(MindSparkPlayerActivity.getStartIntent(mContext, course.getId(), course.getTitle()));
                } else {
                    mRxBus.send(new MindSparkNoUnitEvent());
                }
            } else if (course.getCourseType().equals(mContext.getString(R.string.labelPractice).toLowerCase())) {
                String subjectId = getSubjectId(course.getMetaInformation());
                String topicId = getTopicId(course.getMetaInformation());

                startActivity(PracticePlayerActivity.getStartIntent(mContext, course.getTitle(), subjectId, topicId));
            } else {
                final Class finalObjectClass = mFlavorHomeModel.getCourseClass(course);
                WebPlayerCordovaLiveActivity.startWebPlayer(mContext, course.getId(), ConstantUtil.BLANK, ConstantUtil.BLANK, finalObjectClass, ConstantUtil.BLANK, false);
            }
        }

    }


    private void listenRxBusEvents() {
        Disposable subscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
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
                            openPracticeActivity(courseMinimal);
                        }
                        if (mContext != null) {
                            GamificationPrefs.clearPractiseObject(mContext);
                        }

                    }
                }
            }
        });
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
            setRewardPoint(course.getTotalMarks(), course.getCoverage(), course.getColorCode(), holder.mBinding.layoutReward, holder.mBinding.textViewRewardPoints, holder.mBinding.viewCourseStatus);
            holder.mBinding.textViewTitle.setText(course.getTitle());
            holder.mBinding.textViewType.setText(mFlavorHomeModel.getCourseType(course));

            final Class finalObjectClass = mFlavorHomeModel.getCourseClass(course);
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(course.getId())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, course.getId()));
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

                Picasso.with(mContext)
                        .load(thumbnail.getLocalUrl())
                        .placeholder(R.drawable.image_placeholder)
                        .transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL))
                        .fit().centerCrop()
                        .into(imageView);

            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutCourseListItemBinding mBinding;

            ViewHolder(LayoutCourseListItemBinding binding) {
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
            LayoutCourseListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_course_list_item, parent, false);
            return new ReinforceAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ReinforceAdapter.ViewHolder holder, int position) {
            final AboutCourseMinimal course = mList.get(position);

            setThumbnail(course.getThumbnail(), holder.mBinding.imageViewBackground);
            setRewardPoint(course.getTotalMarks(), course.getCoverage(), course.getColorCode(), holder.mBinding.layoutReward, holder.mBinding.textViewRewardPoints, holder.mBinding.viewCourseStatus);

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
                Picasso.with(mContext).load(thumbnail.getLocalUrl()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            }

        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutCourseListItemBinding mBinding;

            public ViewHolder(LayoutCourseListItemBinding binding) {
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

            holder.mBinding.textViewTitle.setText(course.getTitle());

            setThumbnail(course.getCourseType(), course.getThumbnail(), holder.mBinding.imageViewBackground);
            setCourseType(course.getCourseType(), course.getColorCode(), holder.mBinding.layoutTextContents, holder.mBinding.textViewType);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        try {
                            if (!TextUtils.isEmpty(mSubjectName)
                                    && !mSubjectName.contains("Eng")
                                    && !TextUtils.isEmpty(course.getCourseType())
                                    && !course.getCourseType().equals(mContext.getString(R.string.labelWorksheet).toLowerCase())) {
                                ArrayList<GamificationEvent> eventList = mGamificationModel.getGamificationEvent();
                                if (eventList != null && !eventList.isEmpty()) {
                                    if (eventList.size() > 4) {
                                        GamificationEvent mGamificationPracticeEvent = eventList.get(4);
                                        if (mGamificationPracticeEvent != null
                                                && mGamificationPracticeEvent.getActivity().equalsIgnoreCase("LRPA")
                                                && mGamificationPracticeEvent.getSubActivity().equalsIgnoreCase("practise")) {
                                            if (mGamificationPracticeEvent.getEventOccurrenceDate() == null && !mGamificationPracticeEvent.isGamingEventDone()) {
                                                GamificationPrefs.savePractiseObject(mContext, course);
                                                mGamificationDialog.display(getFragmentManager(), mContext, mGamificationPracticeEvent.getMessage(), mGamificationPracticeEvent);
                                            } else if (CommonUtils.getInstance().checkEventOccurrence(mGamificationPracticeEvent.getFrequency(), mGamificationPracticeEvent.getFrequencyUnit(), mGamificationPracticeEvent.getEventOccurrenceDate())) {
                                                GamificationPrefs.savePractiseObject(mContext, course);
                                                mGamificationDialog.display(getFragmentManager(), mContext, mGamificationPracticeEvent.getMessage(), mGamificationPracticeEvent);
                                            } else {
                                                openPracticeActivity(course);
                                            }
                                        } else {
                                            openPracticeActivity(course);
                                        }
                                    } else {
                                        openPracticeActivity(course);
                                    }
                                } else {
                                    openPracticeActivity(course);
                                }
                            } else {
                                openPracticeActivity(course);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        GeneralUtils.showToastShort(mContext, getString(R.string.connect_internet));
                    }

                }
            });
        }

        private void setCourseType(String courseType, String mindSparkUnitValue, RelativeLayout layoutTextContents, AppCompatTextView textView) {
            if (courseType.equals(mContext.getString(R.string.labelPractice).toLowerCase())) {
                textView.setText(mContext.getString(R.string.labelPractice));
            } else if (courseType.equals(mContext.getString(R.string.labelWorksheet).toLowerCase())) {
                textView.setText(mContext.getString(R.string.labelWorksheet));
            } else if (courseType.equals(mContext.getString(R.string.mindspark))) {
                textView.setText(mindSparkUnitValue);
            } else {
                textView.setText(ConstantUtil.BLANK);
            }

        }

        private void setThumbnail(String courseType, Thumbnail thumbnail, ImageView imageView) {
            if (courseType.equals(mContext.getString(R.string.freadom))) {
                imageView.setColorFilter(R.color.colorPrimary);
                Picasso.with(mContext).load(R.drawable.logo_freadom).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(18, 8, RoundedCornersTransformation.CornerType.ALL)).into(imageView);
            } else if (courseType.equals(mContext.getString(R.string.lightsail))) {
                Picasso.with(mContext).load(R.drawable.logo_lightsail).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(18, 8, RoundedCornersTransformation.CornerType.ALL)).into(imageView);
            } else if (courseType.equals(mContext.getString(R.string.mindspark))) {
                Picasso.with(mContext).load(R.drawable.background_thumb_mind_spark).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (courseType.equals(mContext.getString(R.string.labelWorksheet).toLowerCase())) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                    Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).into(imageView);
                } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                    Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).into(imageView);
                } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                    Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).into(imageView);
                } else {
                    Picasso.with(mContext).load(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
                }
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
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

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutPracticeListItemBinding mBinding;

            ViewHolder(LayoutPracticeListItemBinding binding) {
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
            LayoutCourseListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_course_list_item, parent, false);
            return new ApplyAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ApplyAdapter.ViewHolder holder, int position) {
            final AboutCourseMinimal course = mList.get(position);

            setThumbnail(course.getThumbnail(), holder.mBinding.imageViewBackground);
            setRewardPoint(course.getTotalMarks(), course.getCoverage(), course.getColorCode(), holder.mBinding.layoutReward, holder.mBinding.textViewRewardPoints, holder.mBinding.viewCourseStatus);

            holder.mBinding.textViewTitle.setText(course.getTitle());
            setCourseType(holder.mBinding.textViewType, course);

            final Class finalObjectClass = mFlavorHomeModel.getCourseClass(course);
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(course.getId())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            mContext.startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, course.getId()));
                        } else if (!TextUtils.isEmpty(course.getCourseType()) && course.getCourseType().equals(mContext.getString(R.string.label_wikiHow))) {
                            WebPlayerCordovaLiveActivity.startWebPlayerForWikiHow(mContext, course.getId());
                        } else {
                            WebPlayerCordovaLiveActivity.startWebPlayer(mContext, course.getId(), "", "", finalObjectClass, "", false);

                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        private void setCourseType(AppCompatTextView textView, AboutCourseMinimal course) {
            if (!TextUtils.isEmpty(course.getCourseType()) && course.getCourseType().equals(mContext.getString(R.string.label_wikiHow))) {
                textView.setText(course.getCourseType());
            } else {
                textView.setText(mFlavorHomeModel.getCourseType(course));
            }
        }


        private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {
            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
                Picasso.with(mContext).load(thumbnail.getLocalUrl()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).transform(new RoundedCornersTransformation(36, 8, RoundedCornersTransformation.CornerType.ALL)).fit().centerCrop().into(imageView);
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutCourseListItemBinding mBinding;

            ViewHolder(LayoutCourseListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mSubscription != null)
//            mSubscription.dispose();
//    }
}

