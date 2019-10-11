package in.securelearning.lil.android.homework.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHomeworkTabViewpagerBinding;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.AnimateFragmentEvent;
import in.securelearning.lil.android.home.events.HomeworkTabOpeningEvent;
import in.securelearning.lil.android.homework.dataobject.AssignedHomeworkParent;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.event.RefreshHomeworkEvent;
import in.securelearning.lil.android.homework.model.HomeworkModel;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeworkFragment extends Fragment {

    @Inject
    RxBus mRxBus;

    @Inject
    HomeworkModel mHomeworkModel;

    private static final int OVERDUE_POSITION = 0;
    private static final int PENDING_POSITION = 1;

    private LayoutHomeworkTabViewpagerBinding mBinding;
    private Disposable mDisposable;
    private AssignedHomeworkParent mAssignedHomeworkParent;
    private List<Homework> mPendingList = new ArrayList<>();
    private List<Homework> mOverdueList = new ArrayList<>();
    private Context mContext;

    public HomeworkFragment() {

    }

    public static HomeworkFragment newInstance() {
        return new HomeworkFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_homework_tab_viewpager, container, false);


        getAssignedHomework(OVERDUE_POSITION);
        listenRxEvent();
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void listenRxEvent() {
        mDisposable = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(Object event) throws Exception {
                        if (event instanceof AnimateFragmentEvent) {
                            int id = ((AnimateFragmentEvent) event).getId();
                            if (id == R.id.nav_assignments) {
                                AnimationUtils.fadeInFast(mContext, mBinding.viewPager);

                            }
                        } else if (event instanceof RefreshHomeworkEvent) {
                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() throws Exception {
                                    getAssignedHomework(0);

                                }
                            });
                        } else if (event instanceof HomeworkTabOpeningEvent) {
                            final int index = ((HomeworkTabOpeningEvent) event).getIndex();
                            mBinding.viewPager.setCurrentItem(index, true);

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void setDefaults() {
        mPendingList.clear();
        mOverdueList.clear();
    }

    /*
     * To get all assigned homework from all subjects
     * For all assigned homework from all subjects - subjectId must be null
     */
    @SuppressLint("CheckResult")
    private void getAssignedHomework(final int overduePosition) {

        if (GeneralUtils.isNetworkAvailable(mContext)) {

            mBinding.layoutProgressBar.setVisibility(View.VISIBLE);
            mHomeworkModel.fetchHomework(null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<AssignedHomeworkParent>() {
                        @Override
                        public void accept(AssignedHomeworkParent assignedHomeworkParent) throws Exception {

                            mBinding.layoutProgressBar.setVisibility(View.GONE);

                            if (assignedHomeworkParent != null) {

                                mAssignedHomeworkParent = assignedHomeworkParent;
                                setDefaults();
                                if (mAssignedHomeworkParent.getOverDueStudentAssignment() != null
                                        && mAssignedHomeworkParent.getOverDueStudentAssignment().getCount() > 0) {
                                    mOverdueList = mAssignedHomeworkParent.getOverDueStudentAssignment().getAssignmentsList();
                                }

                                if (mAssignedHomeworkParent.getPendingAssignmentList() != null
                                        && !mAssignedHomeworkParent.getPendingAssignmentList().isEmpty()) {
                                    mPendingList = mAssignedHomeworkParent.getPendingAssignmentList();
                                }

                                setUpViewPager(overduePosition);
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.layoutProgressBar.setVisibility(View.GONE);
                            setUpViewPager(overduePosition);

                        }
                    });
        } else {
            showInternetSnackBar();
        }

    }

    private void showInternetSnackBar() {

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction((R.string.labelRetry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getAssignedHomework(OVERDUE_POSITION);
                    }
                })
                .show();

    }

    private void setUpViewPager(int pagerPosition) {
        mBinding.viewPager.setVisibility(View.VISIBLE);
        mBinding.viewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));
        mBinding.viewPager.setCurrentItem(pagerPosition, true);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        this.mContext = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] mTabTitles = new String[]{getString(R.string.string_over_due), getString(R.string.pendingBy)};

        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return HomeworkOverdueFragment.newInstance(mOverdueList);
            } else if (position == 1) {
                return HomeworkPendingFragment.newInstance(mPendingList);
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }

    }


}
