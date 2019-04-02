
package in.securelearning.lil.android.home.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import android.view.animation.Animation;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import in.securelearning.lil.android.analytics.activity.StudentAnalyticsActivity;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutDashboardFragmentBinding;
import in.securelearning.lil.android.app.databinding.LayoutDashboardStudentSubjectItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutLessonPlanCardItemBinding;
import in.securelearning.lil.android.assignments.model.AssignmentResponseStudentModel;
import in.securelearning.lil.android.assignments.model.AssignmentTeacherModel;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentStudentFragment;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.AnimateFragmentEvent;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.home.utils.RecyclerViewPagerIndicator;
import in.securelearning.lil.android.home.views.activity.NavigationDrawerActivity;
import in.securelearning.lil.android.home.views.activity.SubjectDetailsActivity;
import in.securelearning.lil.android.learningnetwork.events.LoadNewAssignmentDownloadEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadRefreshAssignmentStageEvent;
import in.securelearning.lil.android.learningnetwork.views.activity.NotificationActivity;
import in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.quizpreview.events.AssignmentSubmittedEvent;
import in.securelearning.lil.android.syncadapter.dataobject.LessonPlanMinimal;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectResult;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DashboardFragment extends Fragment {

    @Inject
    public RxBus mRxBus;

    @Inject
    public AppUserModel mAppUserModel;

    @Inject
    public AssignmentTeacherModel mTeacherModel;

    @Inject
    AssignmentResponseStudentModel mAssignmentResponseStudentModel;

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    LayoutDashboardFragmentBinding mBinding;

    private OnDashboardFragmentInteractionListener mListener;
    private Disposable mSubscription;
    private Context mContext;
    private Timer mGreetingTimer;

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
        getTodayRecaps();
        initializeUIAndClickListeners();

//        mBinding.layoutSubjectProgressBar.setVisibility(View.GONE);
//        mBinding.recyclerView.setVisibility(View.VISIBLE);
//        LessonPlanSubject lessonPlanSubject = new LessonPlanSubject();
//        lessonPlanSubject.setIconUrl("");
//        lessonPlanSubject.setName("LightSail");
//        initializeSubjectRecyclerView(new ArrayList<LessonPlanSubject>(Collections.singleton(lessonPlanSubject)));

        return mBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        getAssignmentCounts();
        handleGreetingTimer();
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

        mBinding.assignmentView.buttonAssignmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (PermissionPrefsCommon.getDashboardTeacherViewPermission(mContext)) {
//                    mListener.onDashboardFragmentInteraction(AssignmentFragmentTeacher.class);
//                } else {
//                    mListener.onDashboardFragmentInteraction(AssignmentStudentFragment.class);
//                }
            }
        });

        mBinding.buttonAnalyticsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(StudentAnalyticsActivity.getStartIntent(getContext()));
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
                mListener.onDashboardFragmentInteraction(AssignmentStudentFragment.class);

            }
        });

        mBinding.assignmentView.textViewAssignmentCount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDashboardFragmentInteraction(AssignmentStudentFragment.class);

            }
        });

        mBinding.assignmentView.textViewAssignmentCount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDashboardFragmentInteraction(AssignmentStudentFragment.class);

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
        final String mEndDate = getTodayDate();
        final String mStartDate = "";
        final String mPendingFromDate = getDayAfterTomorrow();
        final String mUserId = mAppUserModel.getObjectId();

        if (PermissionPrefsCommon.getDashboardStudentViewPermission(mContext)) {

            mAssignmentResponseStudentModel.getNewAssignmentsCount().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    makeCountBold(String.valueOf(integer), mContext.getString(R.string.string_new), mBinding.assignmentView.textViewAssignmentCount1);

                }
            });

            mAssignmentResponseStudentModel.getOverDueAssignmentList(mStartDate, mEndDate, "", 0, 0).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
                @Override
                public void accept(final ArrayList<AssignmentStudent> overDueAssignment) throws Exception {
                    mAssignmentResponseStudentModel.getPendingAssignmentList(mStartDate, mEndDate, "", 0, 0).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<AssignmentStudent>>() {
                        @Override
                        public void accept(ArrayList<AssignmentStudent> pendingAssignment) throws Exception {
                            makeCountBold(String.valueOf(pendingAssignment.size()), mContext.getString(R.string.string_due), mBinding.assignmentView.textViewAssignmentCount2);
                            makeCountBold(String.valueOf(overDueAssignment.size()), mContext.getString(R.string.string_over_due), mBinding.assignmentView.textViewAssignmentCount3);


                        }
                    });

                }
            });

        } else {

            mTeacherModel.getAllPendingAndOverDueAssignmentCounts(mUserId, "", mStartDate, mEndDate, 0, 0).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    makeCountBold(String.valueOf(integer), mContext.getString(R.string.string_pending), mBinding.assignmentView.textViewAssignmentCount1);
                }
            });

            mTeacherModel.getSubmittedAssignmentResponseCount("", "").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    makeCountBold(String.valueOf(integer), mContext.getString(R.string.string_turned_in), mBinding.assignmentView.textViewAssignmentCount2);
                }
            });


            mTeacherModel.getAllPendingAndOverDueAssignmentCounts(mUserId, "", mEndDate, mStartDate, 0, 0).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    makeCountBold(String.valueOf(integer), mContext.getString(R.string.string_over_due), mBinding.assignmentView.textViewAssignmentCount3);
                }
            });

            Calendar cal = Calendar.getInstance();
            mTeacherModel.getAssignmentListAssignedByTeacherToday(cal.getTime());
        }
    }

    @SuppressLint("CheckResult")
    private void getTodayRecaps() {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            mBinding.recapView.layoutRecapProgressBar.setVisibility(View.VISIBLE);
            mBinding.recapView.recycleViewRecap.setVisibility(View.GONE);
            mBinding.recapView.textViewErrorRecap.setVisibility(View.GONE);

            mFlavorHomeModel.getTodayRecaps().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<LessonPlanMinimal>>() {
                        @Override
                        public void accept(ArrayList<LessonPlanMinimal> lessonPlanMinimals) throws Exception {
                            getMySubjects();
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
                            getMySubjects();
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
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            mBinding.layoutSubjectProgressBar.setVisibility(View.VISIBLE);
            mBinding.textViewErrorSubject.setVisibility(View.GONE);
            mBinding.recyclerView.setVisibility(View.GONE);

            mFlavorHomeModel.getMySubject().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LessonPlanSubjectResult>() {
                        @Override
                        public void accept(LessonPlanSubjectResult lessonPlanSubjectResult) throws Exception {

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
        int columnCount = 4;
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), columnCount, GridLayoutManager.VERTICAL, false));

        SubjectAdapter subjectAdapter = new SubjectAdapter(lessonPlanSubjects);
        mBinding.recyclerView.setAdapter(subjectAdapter);

    }

    private void makeCountBold(String count, String label, AppCompatTextView textView) {
        final SpannableString countSpannableString = new SpannableString(count);
        final StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
        countSpannableString.setSpan(bold, 0, count.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(TextUtils.concat(countSpannableString, " ", label));
    }


    /**
     * Create Rxbus Disposable for the listen event raised from anywhere in the app
     */
    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(Object event) {
                if (event instanceof LoadNewAssignmentDownloadEvent) {
                    getAssignmentCounts();
                } else if (event instanceof LoadRefreshAssignmentStageEvent) {
                    getAssignmentCounts();
                } else if (event instanceof AssignmentSubmittedEvent) {
                    getAssignmentCounts();
                } else if (event instanceof AnimateFragmentEvent) {
                    int id = ((AnimateFragmentEvent) event).getId();
                    if (id == R.id.nav_dashboard) {
                        AnimationUtils.fadeInFast(getContext(), mBinding.scrollView);
                        AnimationUtils.fadeIn(getContext(), mBinding.layoutToolbar);
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

    private String getTodayDate() {

        long date = DateUtils.getSecondsForMorningFromDate(new Date());
        return DateUtils.getISO8601DateStringFromSeconds(date);
    }

    private String getDayAfterTomorrow() {
        long date = DateUtils.getSecondsForMidnightFromDate(new Date(DateUtils.getFutureDay(new Date(), 1) * 1000));
        return DateUtils.getISO8601DateStringFromSeconds(date);
    }

    private class RecapPagerAdapter extends RecyclerView.Adapter<RecapPagerAdapter.ViewHolder> {
        private ArrayList<LessonPlanMinimal> mList;

        public RecapPagerAdapter(ArrayList<LessonPlanMinimal> list) {
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutLessonPlanCardItemBinding mBinding;

            public ViewHolder(LayoutLessonPlanCardItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

    private class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

        private ArrayList<LessonPlanSubject> mList;

        public SubjectAdapter(ArrayList<LessonPlanSubject> list) {
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
            setSubjectName(lessonPlanSubject.getName(), holder.mBinding.textViewSubjectName);
            setSubjectIcon(lessonPlanSubject.getIconUrl(), holder.mBinding.imageViewSubjectIcon);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        //startActivity(SampleWebActivity.getStartIntent(getContext()));
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

        private void setSubjectName(String name, AppCompatTextView textView) {
            if (!TextUtils.isEmpty(name)) {
                textView.setText(name);
            }
        }

        @Override
        public int getItemCount() {

            return mList.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutDashboardStudentSubjectItemBinding mBinding;

            public ViewHolder(LayoutDashboardStudentSubjectItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }


}
