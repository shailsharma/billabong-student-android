package in.securelearning.lil.android.home.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.dataobjects.EffortvsPerformanceData;
import in.securelearning.lil.android.analytics.views.activity.StudentAnalyticsTabActivity;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutDashboardFragmentBinding;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.gamification.dataobject.GamificationBonus;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.event.GamificationEventDone;
import in.securelearning.lil.android.gamification.model.MascotModel;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.gamification.views.activity.MascotActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.AnimateFragmentEvent;
import in.securelearning.lil.android.home.events.ChallengeAndVideoAsInterestRefreshEvent;
import in.securelearning.lil.android.home.events.HomeworkTabOpeningEvent;
import in.securelearning.lil.android.home.helper.OnCheckBonusAvailabilityListener;
import in.securelearning.lil.android.home.helper.OnCheckUserRecapOpenListener;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.views.activity.NavigationDrawerActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailsActivity;
import in.securelearning.lil.android.home.views.adapter.RecapPagerAdapter;
import in.securelearning.lil.android.home.views.adapter.SubjectAdapter;
import in.securelearning.lil.android.homework.dataobject.AssignedHomeworkParent;
import in.securelearning.lil.android.homework.event.RefreshHomeworkEvent;
import in.securelearning.lil.android.homework.views.fragment.HomeworkFragment;
import in.securelearning.lil.android.profile.views.activity.StudentProfileActivity;
import in.securelearning.lil.android.syncadapter.dataobject.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.dataobject.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.ChallengeDetail;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectResult;
import in.securelearning.lil.android.syncadapter.dataobjects.LogiQidsChallenge;
import in.securelearning.lil.android.syncadapter.dataobjects.LogiQidsChallengeParent;
import in.securelearning.lil.android.syncadapter.dataobjects.UserChallengePost;
import in.securelearning.lil.android.syncadapter.dataobjects.VideoForDay;
import in.securelearning.lil.android.syncadapter.dataobjects.VideoForDayParent;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopicRequest;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.thirdparty.views.activity.LogiqidsQuizPlayerActivity;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;

public class DashboardFragment extends Fragment {

    private static final int DASHBOARD_SUBJECT_SPAN_COUNT = 4;
    private static final int STATUS_JOIN = 0;
    private static final int STATUS_COMPLETE = 1;

    @Inject
    public RxBus mRxBus;

    @Inject
    public AppUserModel mAppUserModel;


    @Inject
    FlavorHomeModel mFlavorHomeModel;

    @Inject
    MascotModel mMascotModel;

    LayoutDashboardFragmentBinding mBinding;
    private OnDashboardFragmentInteractionListener mListener;
    private Disposable mSubscription;
    private Context mContext;
    private boolean mIsSurveyDone = false;
    private int mOverDueCount = 0, mNewCount = 0, mDueSoonCount = 0;
    private boolean mIsLogiqidsChallengeShowing, mIsVideoForDayShowing, mIsVideoForDayAlreadyCalled;


    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     *
     * @return A new instance of activity DashboardFragment.
     */
    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_dashboard_fragment, container, false);
        listenRxBusEvents();
        initializeUIAndClickListeners();
        getTodayRecaps();

        mBinding.layoutLogiqidsChallengeForDay.getRoot().setVisibility(View.GONE);
        mBinding.layoutVideoForDay.getRoot().setVisibility(View.GONE);

        return mBinding.getRoot();

    }

    /* Mascot is showing before token expire so code changes are done, it will be visible
     * first time when subject call done and second time onResume if condition match*/
    @Override
    public void onResume() {
        super.onResume();


        if (!PrefManager.getCurrentDateLastSaved(mContext).equalsIgnoreCase(DateUtils.getCurrentDate())) {
            PrefManager.setCurrentDateLastSaved(DateUtils.getCurrentDate(), mContext);
            mIsVideoForDayAlreadyCalled = true; // this means that getVideoForTheDay() method should not be called again from getChallengeForTheDay()
            getVideoForTheDay();
        } else {
            if (GamificationPrefs.getSubjectDone(mContext) &&
                    GamificationPrefs.getFirstTimeApplicationLoaded(mContext)) {
                showMascot();
            }
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        if (context instanceof OnDashboardFragmentInteractionListener) {
            mListener = (OnDashboardFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null)
            mSubscription.dispose();
    }

    /* Create RxBus Disposable for the listen event raised from anywhere in the app*/
    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable()
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<Object>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(Object event) {
                        if (event instanceof RefreshHomeworkEvent) {
                            Completable.complete().observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                            getAssignmentCounts();


                                        }
                                    });
                        } else if (event instanceof AnimateFragmentEvent) {
                            int id = ((AnimateFragmentEvent) event).getId();
                            if (id == R.id.nav_dashboard) {
                                Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        AnimationUtils.fadeInFast(getContext(), mBinding.scrollView);
                                        AnimationUtils.fadeIn(getContext(), mBinding.layoutToolbar);

                                    }
                                });

                            }
                        } else if (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(UserProfile.class)) {
                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() throws Exception {
                                    setUserInfo();

                                }
                            });
                        } else if (event instanceof GamificationEventDone) {
                            GamificationEventDone gamificationEventDone = (GamificationEventDone) event;
                            if (gamificationEventDone.getEventActivity() != null
                                    && gamificationEventDone.getEventActivity().equalsIgnoreCase("dashboard")
                                    && gamificationEventDone.getSubActivity() != null
                                    && gamificationEventDone.getSubActivity().equalsIgnoreCase("subject")
                                    && !TextUtils.isEmpty(GamificationPrefs.getSelectedId(mContext)) && gamificationEventDone.isDone()) {
                                String subjectId = GamificationPrefs.getSelectedId(mContext);
                                mContext.startActivity(SubjectDetailsActivity.getStartIntent(getContext(), subjectId));
                            } else if (gamificationEventDone.isDone() &&
                                    gamificationEventDone.getEventActivity() != null &&
                                    gamificationEventDone.getEventActivity().
                                            equalsIgnoreCase("dashboard") && gamificationEventDone.getSubActivity() != null &&
                                    gamificationEventDone.getSubActivity().equalsIgnoreCase("survey")) {
                                mIsSurveyDone = true;
                            } else if (gamificationEventDone.isDone() &&
                                    gamificationEventDone.getEventActivity() != null &&
                                    gamificationEventDone.getEventActivity().
                                            equalsIgnoreCase("dashboard") && gamificationEventDone.getSubActivity() != null &&
                                    gamificationEventDone.getSubActivity().equalsIgnoreCase("welcome")) {
                                checkHomeWorkMsg();
                            }
                        }
//                else if (event instanceof ChallengeForTheDayCompleteEvent) {
//                    getChallengeForTheDay(false);
//                }

                        if (event instanceof ChallengeAndVideoAsInterestRefreshEvent) {
                            Completable.complete()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                            /*Even if user changes his/her interests in personal tab
                                             * for now we do not need to refresh instantly
                                             * so below line is commented*/
//                                    getVideoForTheDay();
                                        }
                                    });
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void checkHomeWorkMsg() {
        final ArrayList<GamificationEvent> eventList = mMascotModel.getGamificationEvent();
        String userName = ConstantUtil.BLANK;
        if (mAppUserModel != null && mAppUserModel.getApplicationUser() != null) {
            userName = mAppUserModel.getApplicationUser().getFirstName();
        }
        if (eventList != null && !eventList.isEmpty() && eventList.size() > 1) {
            GamificationEvent homeworkEvent = eventList.get(1);
            if (homeworkEvent != null && homeworkEvent.getActivity().equalsIgnoreCase("dashboard") && homeworkEvent.getOnActionCriteria().equalsIgnoreCase("new_assignment_count") && mNewCount != 0) {
                if (homeworkEvent.getEventOccurrenceDate() == null && !homeworkEvent.isGamingEventDone()) {
                    String msg = String.format(homeworkEvent.getMessage(), userName, mNewCount);
                    mContext.startActivity(MascotActivity.getStartIntent(mContext, msg, homeworkEvent));
                } else if (CommonUtils.getInstance().checkEventOccurrence(homeworkEvent.getFrequency(), homeworkEvent.getFrequencyUnit(), homeworkEvent.getEventOccurrenceDate())) {
                    String msg = String.format(homeworkEvent.getMessage(), userName, mNewCount);
                    mContext.startActivity(MascotActivity.getStartIntent(mContext, msg, homeworkEvent));
                }
            }
        }
    }

    private void checkWelcomeMsg() {
        final ArrayList<GamificationEvent> eventList = mMascotModel.getGamificationEvent();
        String userName = "";
        if (mAppUserModel != null && mAppUserModel.getApplicationUser() != null) {
            userName = mAppUserModel.getApplicationUser().getFirstName();
        }
        if (eventList != null && !eventList.isEmpty()) {
            GamificationEvent loginEvent = eventList.get(0);
            if (loginEvent != null && loginEvent.getActivity().equalsIgnoreCase("dashboard")
                    && loginEvent.getEventType().equalsIgnoreCase("welcome_message")) {
                if (loginEvent.getEventOccurrenceDate() == null && !loginEvent.isGamingEventDone()) {
                    String msg = String.format(loginEvent.getMessage(), userName);
                    mContext.startActivity(MascotActivity.getStartIntent(mContext, msg, loginEvent));
                } else if (CommonUtils.getInstance().checkEventOccurrence(loginEvent.getFrequency(), loginEvent.getFrequencyUnit(), loginEvent.getEventOccurrenceDate())) {
                    String msg = String.format(loginEvent.getMessage(), userName);
                    mContext.startActivity(MascotActivity.getStartIntent(mContext, msg, loginEvent));
                }
            }

        }
    }

    private void checkGamificationEventForSurvey() {
        ArrayList<GamificationEvent> eventList = mMascotModel.getGamificationEvent();
        if (eventList != null && !eventList.isEmpty()) {
            for (GamificationEvent event : eventList) {
                if (event != null) {

                    /*School survey*/
                    if (event.getActivity().equalsIgnoreCase("dashboard") && event.getOnActionCriteria().equalsIgnoreCase("3pm")) {
                        if (event.getEventOccurrenceDate() == null && !event.isGamingEventDone()) {
                            mContext.startActivity(MascotActivity.getStartIntent(mContext, event.getMessage(), event));
                            break;
                        } else if (CommonUtils.getInstance().compareTwoDatesForSurvey(event.getEventOccurrenceDate())) {
                            mContext.startActivity(MascotActivity.getStartIntent(mContext, event.getMessage(), event));
                            break;
                        }
                    }
                }
            }
        }

    }

    private void checkGamificationEventForBonus() {
        ArrayList<GamificationEvent> eventList = mMascotModel.getGamificationEvent();

        if (eventList != null && !eventList.isEmpty()) {
            for (GamificationEvent event : eventList) {
                if (event != null) {

                    if (event.getActivity().equalsIgnoreCase("dashboard") && event.isBonusAvailable()) {
                        if (event.getBonusObject() == null && TextUtils.isEmpty(event.getBonusCalculateDate())) {
                            fetchSubjectPerformanceData();
                            break;
                        } else if (event.getBonusObject() != null && !TextUtils.isEmpty(event.getBonusCalculateDate()) && CommonUtils.getInstance().checkEventOccurrenceForBonus(event.getFrequency(), event.getFrequencyUnit(), event.getBonusCalculateDate())) {
                            fetchSubjectPerformanceData();
                            break;
                        }
                    }


                }
            }
        }

    }

    private void showMascot() {

        if (CommonUtils.getInstance().getHoursOfDay() < 15) {
            checkWelcomeMsg();
            checkGamificationEventForBonus();
        } else if (!mIsSurveyDone && CommonUtils.getInstance().getHoursOfDay() > 15) {
            checkGamificationEventForSurvey();
        } else {
            checkHomeWorkMsg();
            checkGamificationEventForBonus();
        }

    }

    private void showInternetSnackBar() {

        mBinding.swipeRefreshLayout.setRefreshing(false);
        Snackbar.make(mBinding.swipeRefreshLayout, mContext.getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getTodayRecaps();
                    }
                })
                .show();

    }

    private void initializeUIAndClickListeners() {

        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsVideoForDayAlreadyCalled = false; // this means that user deliberately calls getVideoForTheDay() on refresh
                getTodayRecaps();

            }
        });

        mBinding.buttonAnalyticsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(StudentAnalyticsTabActivity.getStartIntent(mContext));
            }
        });

        mBinding.buttonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDashboardFragmentInteraction(NavigationDrawerActivity.class);
            }
        });

        mBinding.imageViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(StudentProfileActivity.getStartIntent(mAppUserModel.getObjectId(), mContext));
            }
        });

        mBinding.assignmentView.layoutAssignmentCount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDashboardFragmentInteraction(HomeworkFragment.class);
                mRxBus.send(new HomeworkTabOpeningEvent(HomeworkTabOpeningEvent.DUE));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRxBus.send(new HomeworkTabOpeningEvent(HomeworkTabOpeningEvent.DUE));
                    }
                }, 500);

            }
        });

        mBinding.assignmentView.layoutAssignmentCount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDashboardFragmentInteraction(HomeworkFragment.class);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRxBus.send(new HomeworkTabOpeningEvent(HomeworkTabOpeningEvent.DUE));
                    }
                }, 500);


            }
        });

        mBinding.assignmentView.layoutAssignmentCount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDashboardFragmentInteraction(HomeworkFragment.class);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRxBus.send(new HomeworkTabOpeningEvent(HomeworkTabOpeningEvent.OVERDUE));
                    }
                }, 500);

            }
        });

        setUserInfo();

    }

    /*setting user image on toolbar*/
    private void setUserInfo() {
        UserProfile userProfile = mAppUserModel.getApplicationUser();
        mBinding.textViewUserName.setText(userProfile.getFirstName());
        if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getLocalUrl())) {
            Picasso.with(getContext()).load(userProfile.getThumbnail().getLocalUrl()).placeholder(R.drawable.icon_profile_large).transform(new CropCircleTransformation()).centerCrop().fit().into(mBinding.imageViewUser);
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getUrl())) {
            Picasso.with(getContext()).load(userProfile.getThumbnail().getUrl()).placeholder(R.drawable.icon_profile_large).transform(new CropCircleTransformation()).centerCrop().fit().into(mBinding.imageViewUser);
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getThumb())) {
            Picasso.with(getContext()).load(userProfile.getThumbnail().getThumb()).placeholder(R.drawable.icon_profile_large).transform(new CropCircleTransformation()).centerCrop().fit().into(mBinding.imageViewUser);
        } else {
            if (!TextUtils.isEmpty(userProfile.getFirstName())) {
                String firstWord = userProfile.getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, ContextCompat.getColor(mContext, R.color.colorPrimary));
                mBinding.imageViewUser.setImageDrawable(textDrawable);
            }
        }

    }

    @SuppressLint("CheckResult")
    private void getAssignmentCounts() {
        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mFlavorHomeModel.fetchHomeworkCount(null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<AssignedHomeworkParent>() {
                        @Override
                        public void accept(AssignedHomeworkParent assignedHomeworkParent) throws Exception {

                            getMySubjects();

                            if (assignedHomeworkParent != null) {

                                if (assignedHomeworkParent.getNewStudentAssignment() != null) {
                                    mNewCount = assignedHomeworkParent.getNewStudentAssignment().getCount();
                                    mBinding.assignmentView.textViewAssignmentCount1.setText(String.valueOf(mNewCount));
                                } else {
                                    mBinding.assignmentView.textViewAssignmentCount1.setText(String.valueOf(mNewCount));
                                }
                                if (assignedHomeworkParent.getOverDueStudentAssignment() != null) {
                                    mOverDueCount = assignedHomeworkParent.getOverDueStudentAssignment().getCount();
                                    mBinding.assignmentView.textViewAssignmentCount3.setText(String.valueOf(mOverDueCount));

                                } else {
                                    mBinding.assignmentView.textViewAssignmentCount3.setText(String.valueOf(mOverDueCount));
                                }
                                if (assignedHomeworkParent.getTodayStudentAssignment() != null) {
                                    mDueSoonCount = assignedHomeworkParent.getTodayStudentAssignment().getCount();
                                }
                                if (assignedHomeworkParent.getUpComingStudentAssignment() != null) {
                                    mDueSoonCount = mDueSoonCount + assignedHomeworkParent.getUpComingStudentAssignment().getCount();
                                }
                                mBinding.assignmentView.textViewAssignmentCount2.setText(String.valueOf(mDueSoonCount));


                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            getMySubjects();
                            throwable.printStackTrace();

                        }
                    });
        } else {
            showInternetSnackBar();
        }

    }

    @SuppressLint("CheckResult")
    private void getTodayRecaps() {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mBinding.recapView.layoutRecapProgressBar.setVisibility(View.VISIBLE);
            mBinding.recapView.recycleViewRecap.setVisibility(View.GONE);
            mBinding.recapView.textViewErrorRecap.setVisibility(View.GONE);

            mFlavorHomeModel.getTodayRecaps().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<LessonPlanMinimal>>() {
                        @Override
                        public void accept(ArrayList<LessonPlanMinimal> lessonPlanMinimals) throws Exception {
                            getAssignmentCounts();
                            mBinding.recapView.layoutRecapProgressBar.setVisibility(View.GONE);
                            if (lessonPlanMinimals != null && !lessonPlanMinimals.isEmpty()) {
                                mBinding.recapView.recycleViewRecap.setVisibility(View.VISIBLE);
                                mBinding.recapView.textViewErrorRecap.setVisibility(View.GONE);
                                initializeRecapPager(lessonPlanMinimals);
                            } else {
                                mBinding.recapView.recycleViewRecap.setVisibility(View.GONE);
                                mBinding.recapView.textViewErrorRecap.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            getAssignmentCounts();
                            mBinding.recapView.layoutRecapProgressBar.setVisibility(View.GONE);
                            mBinding.recapView.recycleViewRecap.setVisibility(View.GONE);
                            mBinding.recapView.textViewErrorRecap.setVisibility(View.VISIBLE);
                            throwable.printStackTrace();
                        }

                    });
        } else {
            showInternetSnackBar();
        }


    }

    // Mascot need to show when subject loading done
    @SuppressLint("CheckResult")
    private void getMySubjects() {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mBinding.layoutSubjectProgressBar.setVisibility(View.VISIBLE);
            mBinding.textViewErrorSubject.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.GONE);

            mFlavorHomeModel.getMySubject()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LessonPlanSubjectResult>() {
                        @Override
                        public void accept(LessonPlanSubjectResult lessonPlanSubjectResult) throws Exception {

                            GamificationPrefs.setSubjectCallDone(mContext, true);

                            mBinding.layoutSubjectProgressBar.setVisibility(View.GONE);
                            mBinding.swipeRefreshLayout.setRefreshing(false);

                            if (lessonPlanSubjectResult != null
                                    && lessonPlanSubjectResult.getLessonPlanSubjects() != null
                                    && !lessonPlanSubjectResult.getLessonPlanSubjects().isEmpty()) {

                                initializeSubjectRecyclerView(lessonPlanSubjectResult.getLessonPlanSubjects());


                            } else {
                                mBinding.textViewErrorSubject.setVisibility(View.VISIBLE);
                                mBinding.recyclerView.setVisibility(View.GONE);
                            }

                            if (!mIsVideoForDayAlreadyCalled) {
                                getVideoForTheDay();
                            }

                            if (!GamificationPrefs.getFirstTimeApplicationLoaded(mContext)) {
                                showMascot();
                                GamificationPrefs.setFirstTimeApplicationLoaded(mContext, true);
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            GamificationPrefs.setSubjectCallDone(mContext, true);

                            if (!mIsVideoForDayAlreadyCalled) {
                                getVideoForTheDay();
                            }

                            mBinding.swipeRefreshLayout.setRefreshing(false);
                            mBinding.layoutSubjectProgressBar.setVisibility(View.GONE);
                            mBinding.textViewErrorSubject.setVisibility(View.VISIBLE);
                            mBinding.recyclerView.setVisibility(View.GONE);

                        }
                    });

        } else {
            showInternetSnackBar();
        }

    }

    /*To fetch vocational subject (for now name = life skill)*/
    @SuppressLint("CheckResult")
    private void getVocationalSubject(final ArrayList<LessonPlanSubject> lessonPlanSubjectList) {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mBinding.layoutSubjectProgressBar.setVisibility(View.VISIBLE);
            mBinding.textViewErrorSubject.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.GONE);

            mFlavorHomeModel.fetchVocationalSubject(new VocationalTopicRequest())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<VocationalSubject>>() {
                        @Override
                        public void accept(ArrayList<VocationalSubject> vocationalSubjectArrayList) throws Exception {

                            mBinding.layoutSubjectProgressBar.setVisibility(View.GONE);
                            mBinding.swipeRefreshLayout.setRefreshing(false);

                            if (vocationalSubjectArrayList != null
                                    && !vocationalSubjectArrayList.isEmpty()) {

                                /*Converting vocational subject to lesson plan subject so we can show this as an subject on dashboard*/
                                if (vocationalSubjectArrayList.get(0) != null) {
                                    VocationalSubject lifeSkillSubject = vocationalSubjectArrayList.get(0);

                                    LessonPlanSubject vPlanSubject = new LessonPlanSubject();
                                    vPlanSubject.setId(lifeSkillSubject.getId());
                                    vPlanSubject.setName(lifeSkillSubject.getSubjectName());
                                    vPlanSubject.setIconUrl(lifeSkillSubject.getThumbnailUrl());
                                    vPlanSubject.setVocationalSubject(true);
                                    lessonPlanSubjectList.add(vPlanSubject);

                                }

                            } else {
                                mBinding.textViewErrorSubject.setVisibility(View.VISIBLE);
                                mBinding.recyclerView.setVisibility(View.GONE);
                            }
                            initializeSubjectRecyclerView(lessonPlanSubjectList);
                            //getChallengeForTheDay();

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();

                            mBinding.swipeRefreshLayout.setRefreshing(false);
                            mBinding.layoutSubjectProgressBar.setVisibility(View.GONE);
                            mBinding.textViewErrorSubject.setVisibility(View.VISIBLE);
                            mBinding.recyclerView.setVisibility(View.GONE);

                            initializeSubjectRecyclerView(lessonPlanSubjectList);
                            // getChallengeForTheDay();

                        }
                    });

        } else {
            showInternetSnackBar();
        }

    }

    /*To fetch challenge for the day*/
    @SuppressLint("CheckResult")
    private void getChallengeForTheDay(final boolean shouldCallVideoForTheDay) {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mFlavorHomeModel.fetchChallengeForTheDay(ConstantUtil.CHALLENGE_PER_DAY_LOGIQIDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LogiQidsChallengeParent>() {
                        @Override
                        public void accept(LogiQidsChallengeParent logiQidsChallengeParent) throws Exception {

                            if (logiQidsChallengeParent.getLogiQidsChallenge() != null) {

                                mBinding.layoutLogiqidsChallengeForDay.getRoot().setVisibility(View.VISIBLE);
                                setLogiqidsChallengeForTheDay(logiQidsChallengeParent);
                                mIsLogiqidsChallengeShowing = true;

                            } else {
                                mBinding.layoutLogiqidsChallengeForDay.getRoot().setVisibility(View.GONE);
                                mIsLogiqidsChallengeShowing = false;
                            }

                            showChallengeAndVideoItemDivider();
                            if (!mIsVideoForDayAlreadyCalled) {
                                getVideoForTheDay();
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.layoutLogiqidsChallengeForDay.getRoot().setVisibility(View.GONE);
                            mIsLogiqidsChallengeShowing = false;
                            showChallengeAndVideoItemDivider();
                            if (!mIsVideoForDayAlreadyCalled) {
                                getVideoForTheDay();
                            }
                        }
                    });

        } else {
            showInternetSnackBar();
        }

    }

    /*To fetch video for the day*/
    @SuppressLint("CheckResult")
    private void getVideoForTheDay() {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mFlavorHomeModel.fetchVideoForTheDay(ConstantUtil.VIDEO_PER_DAY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<VideoForDayParent>() {
                        @Override
                        public void accept(VideoForDayParent videoForDayParent) throws Exception {

                            if (videoForDayParent.getVideoForDay() != null) {

                                mBinding.layoutVideoForDay.getRoot().setVisibility(View.VISIBLE);
                                setVideoForTheDayData(videoForDayParent);
                                mIsVideoForDayShowing = true;

                            } else {
                                mBinding.layoutVideoForDay.getRoot().setVisibility(View.GONE);
                                mIsVideoForDayShowing = false;
                            }

                            showChallengeAndVideoItemDivider();

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.layoutVideoForDay.getRoot().setVisibility(View.GONE);
                            mIsVideoForDayShowing = false;
                            showChallengeAndVideoItemDivider();
                        }
                    });

        } else {
            showInternetSnackBar();
        }

    }

    /*To upload take challenge data*/
    @SuppressLint("CheckResult")
    private void uploadTakeChallengeData(final LogiQidsChallengeParent logiQidsChallengeParent) {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            int status = STATUS_JOIN;

            UserChallengePost post = new UserChallengePost();
            if (!TextUtils.isEmpty(logiQidsChallengeParent.getLogiQidsChallenge().getChallengeId())) {
                mContext.startActivity(LogiqidsQuizPlayerActivity.getStartIntent(mContext, logiQidsChallengeParent.getLogiQidsChallenge().getLogiQidsTopicName(), logiQidsChallengeParent.getLogiQidsChallenge().getLogiQidsTopicId()));

                post.setChallengeId(logiQidsChallengeParent.getLogiQidsChallenge().getChallengeId());
                status = STATUS_COMPLETE;
            } else {
                ChallengeDetail challengeDetail = new ChallengeDetail();
                challengeDetail.setType(ConstantUtil.CHALLENGE_PER_DAY_LOGIQIDS);
                challengeDetail.setIdChallengeForDay(logiQidsChallengeParent.getLogiQidsChallenge().getLogiQidsTopicId());
                challengeDetail.setSubjectId(logiQidsChallengeParent.getLogiQidsChallenge().getSubjectId());
                post.setChallengeDetail(challengeDetail);

                mBinding.layoutLogiqidsChallengeForDay.progressBarChallengeForDay.setVisibility(View.VISIBLE);

                mFlavorHomeModel.uploadTakeChallengeOrVideo(post, status)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody body) throws Exception {
                                mContext.startActivity(LogiqidsQuizPlayerActivity.getStartIntent(mContext, logiQidsChallengeParent.getLogiQidsChallenge().getLogiQidsTopicName(), logiQidsChallengeParent.getLogiQidsChallenge().getLogiQidsTopicId()));
                                mBinding.layoutLogiqidsChallengeForDay.progressBarChallengeForDay.setVisibility(View.GONE);
                                //getChallengeForTheDay(false);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mBinding.layoutLogiqidsChallengeForDay.progressBarChallengeForDay.setVisibility(View.GONE);
                                GeneralUtils.showToastLong(mContext, "Unable to join challenge, please retry.");
                                throwable.printStackTrace();
                            }
                        });
            }


        } else {
            showInternetSnackBar();
        }
    }

    /*To upload Video Watch And Learn Data*/
    @SuppressLint("CheckResult")
    private void uploadVideoWatchAndLearnData(final VideoForDayParent videoForDayParent) {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            int status = STATUS_COMPLETE;

            UserChallengePost post = new UserChallengePost();
            ChallengeDetail challengeDetail = new ChallengeDetail();

            challengeDetail.setType(ConstantUtil.VIDEO_PER_DAY);
            challengeDetail.setIdVideoForDay(videoForDayParent.getVideoForDay().getVideoResource().getObjectId());
            challengeDetail.setSubjectId(videoForDayParent.getVideoForDay().getSubject().getId());
            post.setChallengeDetail(challengeDetail);

            mBinding.layoutVideoForDay.progressBarVideoForDay.setVisibility(View.VISIBLE);

            mFlavorHomeModel.uploadTakeChallengeOrVideo(post, status)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody body) throws Exception {
                            mBinding.layoutVideoForDay.progressBarVideoForDay.setVisibility(View.GONE);
//                            getVideoForTheDay();
                            mFlavorHomeModel.playVideo(videoForDayParent.getVideoForDay().getVideoResource());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mBinding.layoutVideoForDay.progressBarVideoForDay.setVisibility(View.GONE);
                            GeneralUtils.showToastShort(mContext, mContext.getString(R.string.error_something_went_wrong));
                            throwable.printStackTrace();
                        }
                    });

        } else {
            showInternetSnackBar();
        }
    }

    /*Set logiQids challenge for the day data and listener*/
    private void setLogiqidsChallengeForTheDay(final LogiQidsChallengeParent logiQidsChallengeParent) {
        final LogiQidsChallenge logiQidsChallenge = logiQidsChallengeParent.getLogiQidsChallenge();

        if (!TextUtils.isEmpty(logiQidsChallenge.getTopicName())) {
            mBinding.layoutLogiqidsChallengeForDay.textViewTopicName.setText(logiQidsChallenge.getTopicName());
        }

        if (!TextUtils.isEmpty(logiQidsChallenge.getLogiQidsTopicName())) {
            mBinding.layoutLogiqidsChallengeForDay.textViewLogiQidsTopicName.setText(logiQidsChallenge.getLogiQidsTopicName());
        }

        if (!TextUtils.isEmpty(logiQidsChallenge.getLogiQidsDescription())) {
            mBinding.layoutLogiqidsChallengeForDay.textViewLogiQidsDescription.setText(logiQidsChallenge.getLogiQidsDescription());
        }

        if (!TextUtils.isEmpty(logiQidsChallenge.getLogiQidsImageUrl())) {
            Picasso.with(mContext).load(logiQidsChallenge.getLogiQidsImageUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(mBinding.layoutLogiqidsChallengeForDay.imageViewThumbnail);
        } else {
            Picasso.with(mContext).load(R.drawable.image_placeholder).fit().centerCrop().into(mBinding.layoutLogiqidsChallengeForDay.imageViewThumbnail);
        }

        if (logiQidsChallengeParent.getViews() > 0) {
            mBinding.layoutLogiqidsChallengeForDay.textViewWorkingCount.setVisibility(View.VISIBLE);
            String challengeCountString = "";
            if (logiQidsChallengeParent.getViews() == 1) {
                challengeCountString = logiQidsChallengeParent.getViews() + " person working on this";
            } else {
                challengeCountString = logiQidsChallengeParent.getViews() + " people working on this";
            }
            mBinding.layoutLogiqidsChallengeForDay.textViewWorkingCount.setText(challengeCountString);
        } else {
            mBinding.layoutLogiqidsChallengeForDay.textViewWorkingCount.setVisibility(View.INVISIBLE);
        }

        mBinding.layoutLogiqidsChallengeForDay.buttonTakeChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsLogiqidsChallengeShowing) {
                    uploadTakeChallengeData(logiQidsChallengeParent);
                }
            }
        });


    }

    /*Set video for the day data and listener*/
    private void setVideoForTheDayData(final VideoForDayParent videoForDayParent) {
        VideoForDay videoForDay = videoForDayParent.getVideoForDay();

        if (videoForDay.getSubject() != null && !TextUtils.isEmpty(videoForDay.getSubject().getName())) {
            mBinding.layoutVideoForDay.textViewSubject.setText(videoForDay.getSubject().getName());
        }

        if (videoForDayParent.getViews() > 0) {
            mBinding.layoutVideoForDay.textViewViewsCount.setVisibility(View.VISIBLE);
            String videoViewCountString = "";
            if (videoForDayParent.getViews() == 1) {
                videoViewCountString = videoForDayParent.getViews() + " View";
            } else {
                videoViewCountString = videoForDayParent.getViews() + " Views";
            }
            mBinding.layoutVideoForDay.textViewViewsCount.setText(videoViewCountString);
        } else {
            mBinding.layoutVideoForDay.textViewViewsCount.setVisibility(View.INVISIBLE);
        }

        if (videoForDay.getVideoResource() != null) {
            Resource videoResource = videoForDay.getVideoResource();

            if (!TextUtils.isEmpty(videoResource.getTitle())) {
                mBinding.layoutVideoForDay.textViewTitle.setText(videoResource.getTitle());
            }

            if (!TextUtils.isEmpty(videoResource.getCaption())) {
                mBinding.layoutVideoForDay.textViewCaption.setText(videoResource.getCaption());
            }

            if (!TextUtils.isEmpty(videoResource.getUrlThumbnail())) {
                Picasso.with(mContext).load(videoResource.getUrlThumbnail()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(mBinding.layoutVideoForDay.imageViewThumbnail);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder).fit().centerCrop().into(mBinding.layoutVideoForDay.imageViewThumbnail);
            }

            if (videoResource.getDuration() > 0) {
                mBinding.layoutVideoForDay.textViewDuration.setVisibility(View.VISIBLE);
                long durationLong = (long) (videoResource.getDuration() * 1000);//here getting duration in seconds
                mBinding.layoutVideoForDay.textViewDuration.setText(CommonUtils.getInstance().showSecondAndMinutesFromLong(durationLong));
            } else {
                mBinding.layoutVideoForDay.textViewDuration.setVisibility(View.GONE);
            }

        }

        mBinding.layoutVideoForDay.buttonPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoForDayParent.getVideoForDay().isViewed()) {
                    mFlavorHomeModel.playVideo(videoForDayParent.getVideoForDay().getVideoResource());
                } else {
                    uploadVideoWatchAndLearnData(videoForDayParent);
                }
            }
        });

    }

    /*Show item divider if challenge for day or Video for day is displaying*/
    private void showChallengeAndVideoItemDivider() {
        if (mIsLogiqidsChallengeShowing || mIsVideoForDayShowing) {
            mBinding.itemDividerChallengeVideo.setVisibility(View.VISIBLE);
        } else {
            mBinding.itemDividerChallengeVideo.setVisibility(View.GONE);
        }
    }

    /*initializing recap recycler view pager*/
    private void initializeRecapPager(ArrayList<LessonPlanMinimal> lessonPlanMinimals) {

        mBinding.recapView.recycleViewRecap.setAdapter(null);
        mBinding.recapView.recycleViewRecap.setLayoutManager(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBinding.recapView.recycleViewRecap.setLayoutManager(layoutManager);
        mBinding.recapView.recycleViewRecap.setHasFixedSize(true);

        OnCheckUserRecapOpenListener onCheckUserRecapOpenListener = new OnCheckUserRecapOpenListener() {
            @Override
            public void OnCheckUserStatusListener() {
                mFlavorHomeModel.checkUserStatus(ConstantUtil.TYPE_LESSON_PLAN);
            }
        };

        mBinding.recapView.recycleViewRecap.setAdapter(new RecapPagerAdapter(mContext, onCheckUserRecapOpenListener, lessonPlanMinimals));
    }

    private void initializeSubjectRecyclerView(ArrayList<LessonPlanSubject> lessonPlanSubjects) {


        if (lessonPlanSubjects != null && !lessonPlanSubjects.isEmpty()) {

            mBinding.recyclerView.setVisibility(View.VISIBLE);
            mBinding.textViewErrorSubject.setVisibility(View.GONE);

            mBinding.recyclerView.setAdapter(null);
            mBinding.recyclerView.setLayoutManager(null);

            mBinding.recyclerView.setNestedScrollingEnabled(false);
            mBinding.recyclerView.setHasFixedSize(true);

            GridLayoutManager layoutManager = new GridLayoutManager(mContext, DASHBOARD_SUBJECT_SPAN_COUNT, GridLayoutManager.VERTICAL, false);
            mBinding.recyclerView.setLayoutManager(layoutManager);

            OnCheckBonusAvailabilityListener bonusAvailabilityListener = new OnCheckBonusAvailabilityListener() {
                @Override
                public void OnCheckBonusAvailability(String subjectId) {
                    checkBonusAvailableForSubjectId(subjectId);
                }
            };

            SubjectAdapter subjectAdapter = new SubjectAdapter(mContext, lessonPlanSubjects, bonusAvailabilityListener);
            mBinding.recyclerView.setAdapter(subjectAdapter);

        } else {

            mBinding.textViewErrorSubject.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);

        }
    }

    @SuppressLint("CheckResult")
    private void fetchSubjectPerformanceData() {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mFlavorHomeModel.fetchEffortvsPerformanceData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<EffortvsPerformanceData>>() {
                        @Override
                        public void accept(ArrayList<EffortvsPerformanceData> responses) throws Exception {
                            float avgDaily = 0f;
                            boolean noSubjectFound = false;
                            if (responses != null && !responses.isEmpty()) {
                                for (EffortvsPerformanceData subjectResponse : responses) {
                                    if (subjectResponse != null) {
                                        float percentage = subjectResponse.getPercentage();

                                        EffortvsPerformanceData.TimeResponse timeResponse = subjectResponse.getTimeResponseList();
                                        if (timeResponse != null) {
                                            avgDaily = timeResponse.getAvgDaily();
                                        }
                                        if ((percentage <= ConstantUtil.BONUS_PERCENTAGE) && (avgDaily <= ConstantUtil.BONUS_TIME)) {
                                            //From config getting bonusValue
                                            fetchBonusValueFromConfig(subjectResponse);
                                            noSubjectFound = true;

                                            break;
                                        }
                                    }
                                }
                                if (!noSubjectFound) {

                                    mMascotModel.setBonusCalculateDateGamificationEvent(DateUtils.getCurrentISO8601DateString());
                                }

                            } else {
                                mMascotModel.setBonusCalculateDateGamificationEvent(DateUtils.getCurrentISO8601DateString());
                            }
                        }

                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });

        }
    }

    /* Added bonus from config after
    getting the flash bonus create gamification bonus object */
    @SuppressLint("CheckResult")
    private void fetchBonusValueFromConfig(final EffortvsPerformanceData subjectResponse) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mMascotModel.fetchBonusConfiguration().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GlobalConfigurationParent>() {
                        @Override
                        public void accept(GlobalConfigurationParent globalConfigurationParent) throws Exception {
                            if (globalConfigurationParent != null && globalConfigurationParent.getBonus() != null && globalConfigurationParent.getBonus().getFlashBonus() != 0) {
                                GamificationBonus bonus = mMascotModel.createGamificationBonusForServer(3, 1, "dashboard", "subjects",
                                        AppPrefs.getUserId(mContext), subjectResponse.getId(), null, CommonUtils.getInstance().getNextDayISODate(1, 0), "FlashBonus", globalConfigurationParent.getBonus().getFlashBonus(), null, null, subjectResponse.getName());
                                // Need to save it to event
                                //saveBonusToServer(bonus);
                                mMascotModel.createGamificationBonusObject(bonus, true);
                            } else {
                                GamificationBonus bonus = mMascotModel.createGamificationBonusForServer(3, 1, "dashboard", "subjects",
                                        AppPrefs.getUserId(mContext), subjectResponse.getId(), null, CommonUtils.getInstance().getNextDayISODate(1, 0), "FlashBonus", 100, null, null, subjectResponse.getName());
                                mMascotModel.createGamificationBonusObject(bonus, true);
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            GamificationBonus bonus = mMascotModel.createGamificationBonusForServer(3, 1, "dashboard", "subjects",
                                    AppPrefs.getUserId(mContext), subjectResponse.getId(), null, CommonUtils.getInstance().getNextDayISODate(1, 0), "FlashBonus", 100, null, null, subjectResponse.getName());
                            mMascotModel.createGamificationBonusObject(bonus, true);
                            throwable.printStackTrace();

                        }
                    });
        }


    }

    /*Checking bonus for this student is there and not expiry*/
    @SuppressLint("CheckResult")
    private void checkBonusAvailableForSubjectId(final String lrpaSubjectId) {
        ArrayList<GamificationEvent> eventList = mMascotModel.getGamificationEvent();
        GamificationBonus bonusObject;
        if (eventList != null && !eventList.isEmpty()) {
            if (eventList.size() > 4) {
                GamificationEvent event = eventList.get(2);
                if (event != null && event.getActivity().equalsIgnoreCase("dashboard") && event.isBonusAvailable()) {
                    bonusObject = event.getBonusObject();
                    if (bonusObject != null && bonusObject.getBonusAvail()
                            && !TextUtils.isEmpty(bonusObject.getUserId())
                            && !TextUtils.isEmpty(AppPrefs.getUserId(mContext))
                            && bonusObject.getUserId().
                            equalsIgnoreCase(AppPrefs.getUserId(mContext))
                            && CommonUtils.getInstance().checkDateRange(bonusObject.getStartDate(), bonusObject.getEndDate())
                            && !TextUtils.isEmpty(bonusObject.getSubjectName())) {
                        String msg = "Congratulations! We have unlocked an exclusive Flash Bonus for " + bonusObject.getSubjectName() + "." + " You will secure Extra Billabucks. Should you \n" + "either Learn or Reinforce " + bonusObject.getSubjectName() + " topics Today?";
                        mContext.startActivity(MascotActivity.getStartIntent(mContext, msg, event));
                    } else {
                        mContext.startActivity(SubjectDetailsActivity.getStartIntent(getContext(), lrpaSubjectId));
                    }
                } else {
                    mContext.startActivity(SubjectDetailsActivity.getStartIntent(getContext(), lrpaSubjectId));
                }
            } else {
                mContext.startActivity(SubjectDetailsActivity.getStartIntent(getContext(), lrpaSubjectId));
            }

        } else {
            mContext.startActivity(SubjectDetailsActivity.getStartIntent(getContext(), lrpaSubjectId));
        }
        //get The bonus for the user
    }

    /**
     * This interface must be implemented by activities that contain this
     * activity to allow an interaction in this activity to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDashboardFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDashboardFragmentInteraction(Class aClass);

    }


}