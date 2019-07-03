package in.securelearning.lil.android.home.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.views.activity.StudentAnalyticsTabActivity;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutDashboardFragmentBinding;
import in.securelearning.lil.android.app.databinding.LayoutDashboardStudentSubjectItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutLessonPlanCardItemBinding;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.model.GamificationModel;
import in.securelearning.lil.android.gamification.views.fragment.GamificationDialog;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.AnimateFragmentEvent;
import in.securelearning.lil.android.home.events.HomeworkTabOpeningEvent;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.utils.RecyclerViewPagerIndicator;
import in.securelearning.lil.android.home.views.activity.NavigationDrawerActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailsActivity;
import in.securelearning.lil.android.homework.dataobject.AssignedHomeworkParent;
import in.securelearning.lil.android.homework.event.RefreshHomeworkEvent;
import in.securelearning.lil.android.homework.views.fragment.HomeworkFragment;
import in.securelearning.lil.android.learningnetwork.views.activity.NotificationActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.dataobject.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectResult;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DashboardFragment extends Fragment {

    private static final int DASHBOARD_SUBJECT_SPAN_COUNT = 4;
    @Inject
    public RxBus mRxBus;
    @Inject
    public AppUserModel mAppUserModel;
    @Inject
    public AssignmentTeacherModel mTeacherModel;
    @Inject
    public GamificationDialog mGamificationDialog;
    @Inject
    AssignmentResponseStudentModel mAssignmentResponseStudentModel;
    @Inject
    FlavorHomeModel mFlavorHomeModel;
    LayoutDashboardFragmentBinding mBinding;
    ArrayList<String> mSnackBarMsg = new ArrayList<>();
    @Inject
    GamificationModel mGamificationModel;
    private OnDashboardFragmentInteractionListener mListener;
    private Disposable mSubscription;
    private Context mContext;
    private Timer mGreetingTimer;
    private String mStudentId;
    private int mOverDueCount = 0, mNewCount = 0, mDueSoonCount = 0;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DashboardFragment.
     */
    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_dashboard_fragment, container, false);
        listenRxBusEvents();
        initializeUIAndClickListeners();
        getTodayRecaps();


        return mBinding.getRoot();

    }


    @Override
    public void onResume() {
        super.onResume();

        handleGreetingTimer();
        checkGamificationEvent();
//        if (PermissionPrefs.setRanBefore(mContext)) {
//            //showDynamicSnackBar();
//
//            //showGamificationDialog();
//            // checkGamificationEvent();
//
//        }


    }

    private void showGamificationDialog() {
        String msg;

        if (mAppUserModel != null && mAppUserModel.getApplicationUser() != null) {
            UserProfile userProfile = mAppUserModel.getApplicationUser();
            msg = String.format(getString(R.string.snackbar_login_msg), userProfile.getFirstName());
        } else {
            msg = getString(R.string.snackbar_login_msg_without_name);
        }
        mGamificationDialog.display(getFragmentManager(), mContext, msg, null);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    /*handling greeting timer calling from onResume*/
    private void handleGreetingTimer() {
        if (mGreetingTimer != null) {
            mGreetingTimer.cancel();
            mGreetingTimer.purge();
            mGreetingTimer = null;
            try {
                startGreetingTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                startGreetingTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*starting greeting timer with refresh interval of 10000 ms and setting values on textView*/
    private void startGreetingTimer() throws Exception {
        mGreetingTimer = new Timer();
        mGreetingTimer.scheduleAtFixedRate(new TimerTask() {

                                               @Override
                                               public void run() {
                                                   getActivity().runOnUiThread(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                           mBinding.textViewGreeting.setText(greetingMessage());

                                                       }
                                                   });
                                               }

                                           },
                0,
                10000);

    }

    /*creating greeting message according to hour of the day*/
    private String greetingMessage() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else if (hour >= 17 && hour < 24) {
            return "Good Evening";
        } else {
            return "Hello";
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
                getTodayRecaps();

            }
        });

        mBinding.buttonAnalyticsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(StudentAnalyticsTabActivity.getStartIntent(getContext()));
            }
        });

        mBinding.buttonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDashboardFragmentInteraction(NavigationDrawerActivity.class);
            }
        });

        mBinding.buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NotificationActivity.getStartIntent(getContext()));
            }
        });

        mBinding.assignmentView.textViewAssignmentCount1.setOnClickListener(new View.OnClickListener() {
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

        mBinding.assignmentView.textViewAssignmentCount2.setOnClickListener(new View.OnClickListener() {
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

        mBinding.assignmentView.textViewAssignmentCount3.setOnClickListener(new View.OnClickListener() {
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
            Picasso.with(getContext()).load(userProfile.getThumbnail().getLocalUrl()).placeholder(R.drawable.icon_profile_large).transform(new CircleTransform()).resize(256, 256).centerCrop().into(mBinding.imageViewUser);
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getUrl())) {
            Picasso.with(getContext()).load(userProfile.getThumbnail().getUrl()).placeholder(R.drawable.icon_profile_large).transform(new CircleTransform()).resize(256, 256).centerCrop().into(mBinding.imageViewUser);
        } else if (userProfile.getThumbnail() != null && !TextUtils.isEmpty(userProfile.getThumbnail().getThumb())) {
            Picasso.with(getContext()).load(userProfile.getThumbnail().getThumb()).placeholder(R.drawable.icon_profile_large).transform(new CircleTransform()).resize(256, 256).centerCrop().into(mBinding.imageViewUser);
        } else {
            if (!TextUtils.isEmpty(userProfile.getFirstName())) {
                String firstWord = userProfile.getFirstName().substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                mBinding.imageViewUser.setImageDrawable(textDrawable);
            }
        }

    }

    @SuppressLint("CheckResult")
    private void getAssignmentCounts() {
//Making Homework online
        if (GeneralUtils.isNetworkAvailable(mContext)) {


            mFlavorHomeModel.fetchHomeworkCount(mStudentId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<AssignedHomeworkParent>() {
                        @Override
                        public void accept(AssignedHomeworkParent assignedHomeworkParent) throws Exception {
                            getMySubjects();

                            if (assignedHomeworkParent != null) {

                                if (assignedHomeworkParent.getNewStudentAssignment() != null) {
                                    mNewCount = assignedHomeworkParent.getNewStudentAssignment().getCount();
                                    makeCountBold(String.valueOf(mNewCount), mContext.getString(R.string.string_new), mBinding.assignmentView.textViewAssignmentCount1);

                                } else {
                                    makeCountBold(String.valueOf(mNewCount), mContext.getString(R.string.string_new), mBinding.assignmentView.textViewAssignmentCount1);
                                }
                                if (assignedHomeworkParent.getOverDueStudentAssignment() != null) {
                                    mOverDueCount = assignedHomeworkParent.getOverDueStudentAssignment().getCount();
                                    makeCountBold(String.valueOf(mOverDueCount), mContext.getString(R.string.string_over_due), mBinding.assignmentView.textViewAssignmentCount3);

                                } else {
                                    makeCountBold(String.valueOf(mOverDueCount), mContext.getString(R.string.string_over_due), mBinding.assignmentView.textViewAssignmentCount3);
                                }
                                if (assignedHomeworkParent.getTodayStudentAssignment() != null) {
                                    mDueSoonCount = assignedHomeworkParent.getTodayStudentAssignment().getCount();
                                }
                                if (assignedHomeworkParent.getUpComingStudentAssignment() != null) {
                                    mDueSoonCount = mDueSoonCount + assignedHomeworkParent.getUpComingStudentAssignment().getCount();
                                }
                                makeCountBold(String.valueOf(mDueSoonCount), mContext.getString(R.string.due_soon), mBinding.assignmentView.textViewAssignmentCount2);


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

//        final String mEndDate = getTodayDate();
//        final String mStartDate = "";
//
//        mAssignmentResponseStudentModel.getNewAssignmentsCount().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                makeCountBold(String.valueOf(integer), mContext.getString(R.string.string_new), mBinding.assignmentView.textViewAssignmentCount1);
//                //  mSnackBarMsg.add(1,String.valueOf(integer));
//
//            }
//        });
//
//        mAssignmentResponseStudentModel.getOverDueAssignmentList(mStartDate, mEndDate, "", 0, 0).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
//            @Override
//            public void accept(final ArrayList<AssignmentStudent> overDueAssignment) throws Exception {
//                mAssignmentResponseStudentModel.getPendingAssignmentList(mStartDate, mEndDate, "", 0, 0).subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
//                    @Override
//                    public void accept(ArrayList<AssignmentStudent> pendingAssignment) throws Exception {
//                        makeCountBold(String.valueOf(pendingAssignment.size()), mContext.getString(R.string.string_due), mBinding.assignmentView.textViewAssignmentCount2);
//                        makeCountBold(String.valueOf(overDueAssignment.size()), mContext.getString(R.string.string_over_due), mBinding.assignmentView.textViewAssignmentCount3);
//                        // mSnackBarMsg.add(2,String.valueOf(pendingAssignment.size()));
//                        // mSnackBarMsg.add(3,String.valueOf(overDueAssignment.size()));
//                    }
//                });
//
//            }
//        });
//

    }

    private void checkGamificationEvent() {
        ArrayList<GamificationEvent> eventList = mGamificationModel.getGamificationEvent();
        String msg = null;
        String userName = "";
        if (mAppUserModel != null && mAppUserModel.getApplicationUser() != null) {
            userName = mAppUserModel.getApplicationUser().getFirstName();
        }
        if (eventList != null && !eventList.isEmpty()) {
            for (GamificationEvent event : eventList) {
                if (event != null) {
                    if (event.getActivity().equalsIgnoreCase("dashboard") && event.getEventType().equalsIgnoreCase("message")) {
                        if (event.getEventOccurrenceDate() == null && !event.isGamingEventDone()) {
                            msg = String.format(event.getMessage(), userName);
                            mGamificationDialog.display(getFragmentManager(), mContext, msg, event);
                            break;
                        } else if (CommonUtils.getInstance().getDateDiff(event.getEventOccurrenceDate())) {
                            msg = String.format(event.getMessage(), userName);
                            mGamificationDialog.display(getFragmentManager(), mContext, msg, event);
                            break;

                        }
                    }

                    if (event.getActivity().equalsIgnoreCase("dashboard") && event.getOnActionCriteria().equalsIgnoreCase("new_assignment_count") && mNewCount != 0) {
                        if (event.getEventOccurrenceDate() == null && !event.isGamingEventDone()) {
                            msg = String.format(event.getMessage(), userName, mNewCount);
                            mGamificationDialog.display(getFragmentManager(), mContext, msg, event);
                            break;
                        } else if (CommonUtils.getInstance().getDateDiff(event.getEventOccurrenceDate())) {
                            msg = String.format(event.getMessage(), userName, mNewCount);
                            mGamificationDialog.display(getFragmentManager(), mContext, msg, event);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void showDynamicSnackBar() {
        String msg = getString(R.string.snackbar_login_msg);
        if (mAppUserModel != null && mAppUserModel.getName() != null && !mAppUserModel.getName().trim().equalsIgnoreCase("")) {
            msg = String.format(msg, mAppUserModel.getName());
            mSnackBarMsg.add(0, mAppUserModel.getName());
            SnackBarUtils.getInstance().getSnackBarWithImage(
                    getActivity(), mBinding.getRoot(), R.drawable.m_lil_red_main, "left",
                    mSnackBarMsg, 0, R.color.white, msg);

        } else {
            msg = getString(R.string.snackbar_login_msg_without_name);
            SnackBarUtils.getInstance().getSnackBarWithImage(
                    getActivity(), mBinding.getRoot(), R.drawable.m_lil_red_main, "left",
                    mSnackBarMsg, 0, R.color.white, msg);

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
                            if (!lessonPlanMinimals.isEmpty()) {
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

    @SuppressLint("CheckResult")
    private void getMySubjects() {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mBinding.layoutSubjectProgressBar.setVisibility(View.VISIBLE);
            mBinding.textViewErrorSubject.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.GONE);

            mFlavorHomeModel.getMySubject().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LessonPlanSubjectResult>() {
                        @Override
                        public void accept(LessonPlanSubjectResult lessonPlanSubjectResult) throws Exception {
                            // PreferenceSettingUtilClass.setDashboardDataFetch(true, mContext);

                            mBinding.layoutSubjectProgressBar.setVisibility(View.GONE);
                            mBinding.swipeRefreshLayout.setRefreshing(false);

                            if (lessonPlanSubjectResult != null && lessonPlanSubjectResult.getLessonPlanSubjects() != null && !lessonPlanSubjectResult.getLessonPlanSubjects().isEmpty()) {
                                initializeSubjectRecyclerView(lessonPlanSubjectResult.getLessonPlanSubjects());
                                mBinding.recyclerView.setVisibility(View.VISIBLE);
                                mBinding.textViewErrorSubject.setVisibility(View.GONE);

                            } else {
                                mBinding.textViewErrorSubject.setVisibility(View.VISIBLE);
                                mBinding.recyclerView.setVisibility(View.GONE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            //PreferenceSettingUtilClass.setDashboardDataFetch(true, mContext);

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


    private void initializeSubjectRecyclerView(ArrayList<LessonPlanSubject> lessonPlanSubjects) {

        mBinding.recyclerView.setNestedScrollingEnabled(false);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), DASHBOARD_SUBJECT_SPAN_COUNT, GridLayoutManager.VERTICAL, false));

        mBinding.recyclerView.setAdapter(new SubjectAdapter(lessonPlanSubjects));

    }

    private void makeCountBold(String count, String label, AppCompatTextView textView) {
        final SpannableString countSpannableString = new SpannableString(count);
        final StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
        countSpannableString.setSpan(bold, 0, count.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(TextUtils.concat(countSpannableString, " ", label));
    }


    /**
     * Create RxBus Disposable for the listen event raised from anywhere in the app
     */
    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(Object event) {
                if (event instanceof RefreshHomeworkEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
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
                }
                if (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(UserProfile.class)) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            setUserInfo();

                        }
                    });
                }

            }
        });
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

        if (mGreetingTimer != null) {
            mGreetingTimer.cancel();
            mGreetingTimer.purge();
            mGreetingTimer = null;
        }
    }

    /*initializing recap recycler view pager*/
    private void initializeRecapPager(ArrayList<LessonPlanMinimal> lessonPlanMinimals) {

        mBinding.recapView.recycleViewRecap.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.recapView.recycleViewRecap.setHasFixedSize(true);
        mBinding.recapView.recycleViewRecap.setAdapter(new RecapPagerAdapter(lessonPlanMinimals));
        mBinding.recapView.recycleViewRecap.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int childCount = mBinding.recapView.recycleViewRecap.getChildCount();
                int width = mBinding.recapView.recycleViewRecap.getChildAt(0).getWidth();
                int padding = (mBinding.recapView.recycleViewRecap.getWidth() - width) / 2;

                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);
                    float rate = 0;
                    if (v.getLeft() <= padding) {
                        if (v.getLeft() >= padding - v.getWidth()) {
                            rate = (padding - v.getLeft()) * 1f / v.getWidth();
                        } else {
                            rate = 1;
                        }
                        v.setScaleY(1 - rate * 0.1f);
                        v.setScaleX(1 - rate * 0.1f);

                    } else {
                        if (v.getLeft() <= recyclerView.getWidth() - padding) {
                            rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                        }
                        v.setScaleY(0.9f + rate * 0.1f);
                        v.setScaleX(0.9f + rate * 0.1f);
                    }
                }
            }
        });
        if (mBinding.recapView.recycleViewRecap.getItemDecorationCount() == 0) {
            mBinding.recapView.recycleViewRecap.addItemDecoration(new RecyclerViewPagerIndicator());

        }

        mBinding.recapView.recycleViewRecap.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mBinding.recapView.recycleViewRecap.getChildCount() < 3) {
                    if (mBinding.recapView.recycleViewRecap.getChildAt(1) != null) {
                        if (mBinding.recapView.recycleViewRecap.getCurrentPosition() == 0) {
                            View v1 = mBinding.recapView.recycleViewRecap.getChildAt(1);
                            v1.setScaleY(0.9f);
                            v1.setScaleX(0.9f);
                        } else {
                            View v1 = mBinding.recapView.recycleViewRecap.getChildAt(0);
                            v1.setScaleY(0.9f);
                            v1.setScaleX(0.9f);
                        }
                    }
                } else {
                    if (mBinding.recapView.recycleViewRecap.getChildAt(0) != null) {
                        View v0 = mBinding.recapView.recycleViewRecap.getChildAt(0);
                        v0.setScaleY(0.9f);
                        v0.setScaleX(0.9f);
                    }
                    if (mBinding.recapView.recycleViewRecap.getChildAt(2) != null) {
                        View v2 = mBinding.recapView.recycleViewRecap.getChildAt(2);
                        v2.setScaleY(0.9f);
                        v2.setScaleX(0.9f);
                    }
                }

            }
        });
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
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

    private class RecapPagerAdapter extends RecyclerView.Adapter<RecapPagerAdapter.ViewHolder> {
        private ArrayList<LessonPlanMinimal> mList;

        RecapPagerAdapter(ArrayList<LessonPlanMinimal> list) {
            mList = list;
        }

        @Override
        public RecapPagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutLessonPlanCardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_lesson_plan_card_item, parent, false);
            return new RecapPagerAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final LessonPlanMinimal lessonPlan = mList.get(position);
            setTitle(holder.mBinding.textViewTitle, lessonPlan.getTitle());
            setDescription(holder.mBinding.textViewDescription, lessonPlan.getDescription());
            setGrade(holder.mBinding.textViewGrade, lessonPlan.getGrade(), lessonPlan.getSection());
            setSubject(holder.mBinding.textViewSubject, lessonPlan.getSubject());
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        mFlavorHomeModel.checkUserStatus(ConstantUtil.TYPE_LESSON_PLAN);
                        startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, lessonPlan.getCourseId()));
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(mContext, mBinding.getRoot());
                    }
                }

            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private void setTitle(AppCompatTextView textView, String title) {
            if (!TextUtils.isEmpty(title)) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(Html.fromHtml(title));
            } else {
                textView.setVisibility(View.GONE);
            }

        }

        private void setDescription(AppCompatTextView textView, String description) {
            if (!TextUtils.isEmpty(description)) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(Html.fromHtml(description));
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void setGrade(AppCompatTextView textView, String grade, String section) {
            if (!TextUtils.isEmpty(grade) && !TextUtils.isEmpty(section)) {
                textView.setVisibility(View.VISIBLE);
                String gradeString = grade.toUpperCase().trim();
                String value = gradeString + "-" + section;
                textView.setText(value);
            } else if (!TextUtils.isEmpty(grade)) {
                textView.setVisibility(View.VISIBLE);
                String gradeString = grade.toUpperCase().trim();
                textView.setText(gradeString);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        }

        private void setSubject(AppCompatTextView textView, String subject) {
            if (!TextUtils.isEmpty(subject)) {
                textView.setVisibility(View.VISIBLE);
                String subjectString = subject.trim();
                textView.setText(subjectString);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutLessonPlanCardItemBinding mBinding;

            ViewHolder(LayoutLessonPlanCardItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

    private class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

        private ArrayList<LessonPlanSubject> mList;

        SubjectAdapter(ArrayList<LessonPlanSubject> list) {
            this.mList = list;
        }

        @Override
        public SubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutDashboardStudentSubjectItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_dashboard_student_subject_item, parent, false);
            return new SubjectAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final LessonPlanSubject lessonPlanSubject = mList.get(position);
            setSubjectName(lessonPlanSubject.getShortName(), lessonPlanSubject.getName(), holder.mBinding.textViewSubjectName);
            setSubjectIcon(lessonPlanSubject.getIconUrl(), holder.mBinding.imageViewSubjectIcon);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        startActivity(SubjectDetailsActivity.getStartIntent(getContext(), lessonPlanSubject.getId()));
                    } else {
                        Toast.makeText(getContext(), getString(R.string.connect_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void setSubjectIcon(String imageUrl, AppCompatImageView imageView) {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(getContext()).load(imageUrl).placeholder(R.drawable.icon_book).fit().centerCrop().into(imageView);
            } else {
                Picasso.with(getContext()).load(R.drawable.icon_book).placeholder(R.drawable.icon_book).fit().centerCrop().into(imageView);
            }
        }

        private void setSubjectName(String shortName, String name, AppCompatTextView textView) {
            if (!TextUtils.isEmpty(shortName)) {
                textView.setText(shortName);
            } else if (!TextUtils.isEmpty(name)) {
                textView.setText(name);
            }
        }

        @Override
        public int getItemCount() {

            return mList.size();

        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutDashboardStudentSubjectItemBinding mBinding;

            public ViewHolder(LayoutDashboardStudentSubjectItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }


}
