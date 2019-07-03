package in.securelearning.lil.android.home.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHomeworkRecyclerViewBinding;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.homework.dataobject.AssignedHomeworkParent;
import in.securelearning.lil.android.homework.dataobject.Homework;
import in.securelearning.lil.android.homework.event.RefreshHomeworkEvent;
import in.securelearning.lil.android.homework.model.HomeworkModel;
import in.securelearning.lil.android.homework.views.adapter.HomeworkSubjectWiseAdapter;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubjectHomeworkFragment extends Fragment {

    @Inject
    HomeworkModel mHomeworkModel;
    @Inject
    RxBus mRxBus;
    LayoutHomeworkRecyclerViewBinding mBinding;
    private Context mContext;
    private String mSubjectId;
    private Disposable mSubscription;

    public SubjectHomeworkFragment() {

    }

    public static SubjectHomeworkFragment newInstance(String subjectId) {
        SubjectHomeworkFragment subjectHomeworkFragment = new SubjectHomeworkFragment();
        Bundle args = new Bundle();
        args.putString(ConstantUtil.SUBJECT_ID, subjectId);
        subjectHomeworkFragment.setArguments(args);
        return subjectHomeworkFragment;
    }

    private void getBundleData() {
        if (getArguments() != null) {
            mSubjectId = getArguments().getString(ConstantUtil.SUBJECT_ID);
            if (!TextUtils.isEmpty(mSubjectId)) {

                fetchAssignedHomework(mSubjectId);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_homework_recycler_view, container, false);
        getBundleData();
        listenRxEvent();
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.dispose();
        }
    }

    private void listenRxEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof RefreshHomeworkEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {

                                    fetchAssignedHomework(mSubjectId);


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
    private void fetchAssignedHomework(final String subjectId) {
        if (GeneralUtils.isNetworkAvailable(mContext)) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mHomeworkModel.fetchHomework(subjectId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<AssignedHomeworkParent>() {
                        @Override
                        public void accept(AssignedHomeworkParent homeworkParent) throws Exception {
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.imageViewNoResult.setVisibility(View.GONE);
                            mBinding.layoutNoResult.setVisibility(View.GONE);
                            List<Homework> overdueList = null, pendingList = null;
                            if (homeworkParent != null) {
                                if (homeworkParent.getOverDueStudentAssignment() != null && homeworkParent.getOverDueStudentAssignment().getCount() > 0) {
                                    overdueList = homeworkParent.getOverDueStudentAssignment().getAssignmentsList();
                                }

                                if (homeworkParent.getPendingAssignmentList() != null && !homeworkParent.getPendingAssignmentList().isEmpty()) {
                                    pendingList = homeworkParent.getPendingAssignmentList();
                                }
                                setAdapter(overdueList, pendingList);


                            } else {
                                mBinding.progressBar.setVisibility(View.GONE);
                                mBinding.imageViewNoResult.setVisibility(View.VISIBLE);
                                mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.imageViewNoResult.setVisibility(View.VISIBLE);
                            mBinding.layoutNoResult.setVisibility(View.VISIBLE);

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
                        fetchAssignedHomework(mSubjectId);
                    }
                })
                .show();

    }

    private void setAdapter(List<Homework> overdueList, List<Homework> pendingList) {
        List<Homework> homeworkList = new ArrayList<>();
        ArrayList<Integer> selectedPosition = new ArrayList<>();

        if (overdueList != null && !overdueList.isEmpty()) {
            for (Homework homework : overdueList) {
                homework.setHomeworkType(ConstantUtil.OVERDUE);
            }
            homeworkList.addAll(overdueList);
        }
        if (pendingList != null && !pendingList.isEmpty()) {
            for (Homework homework : pendingList) {
                homework.setHomeworkType(ConstantUtil.PENDING);
            }
            homeworkList.addAll(pendingList);
            selectedPosition = getTypePosition(selectedPosition, homeworkList);
        }


        if (!homeworkList.isEmpty()) {
            mBinding.layoutNoResult.setVisibility(View.GONE);
            mBinding.list.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mBinding.list.setLayoutManager(linearLayoutManager);
            HomeworkSubjectWiseAdapter adapter = new HomeworkSubjectWiseAdapter(homeworkList, selectedPosition);
            mBinding.list.setAdapter(adapter);
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mContext = null;
    }

    private ArrayList<Integer> getTypePosition(ArrayList<Integer> selectedPosition, List<Homework> homeworkArrayList) {

        for (Homework homework : homeworkArrayList) {
            if (homework.getHomeworkType().equalsIgnoreCase(ConstantUtil.OVERDUE)) {
                selectedPosition.add(homeworkArrayList.indexOf(homework));
                break;
            }
        }
        for (Homework homework : homeworkArrayList) {
            if (homework.getHomeworkType().equalsIgnoreCase(ConstantUtil.PENDING)) {
                selectedPosition.add(homeworkArrayList.indexOf(homework));
                break;
            }
        }


        return selectedPosition;

    }

}
