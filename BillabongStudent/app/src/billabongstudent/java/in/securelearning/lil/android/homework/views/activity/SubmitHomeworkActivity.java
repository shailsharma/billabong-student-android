package in.securelearning.lil.android.homework.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHomeworkRecyclerViewBinding;
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
import in.securelearning.lil.android.homework.views.adapter.HomeworkAssignedAdapter;
import in.securelearning.lil.android.learningnetwork.events.AssignmentResponseDownloadEvent;
import in.securelearning.lil.android.quizpreview.events.AssignmentSubmittedEvent;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubmitHomeworkActivity extends AppCompatActivity {

    @Inject
    HomeworkModel mHomeworkModel;
    @Inject
    RxBus mRxBus;
    private LayoutHomeworkRecyclerViewBinding mBinding;
    private Disposable mSubscription;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SubmitHomeworkActivity.class);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_homework_recycler_view);
        setUpToolbar();
        initializeClickListeners();
        fetchSubmittedList(0);
        listenRxBusEvents();

    }


    private void initializeClickListeners() {
        mBinding.layoutToolbar.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpToolbar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mBinding.layoutToolbar.getRoot().setVisibility(View.VISIBLE);
        mBinding.layoutToolbar.textViewToolbarTitle.setText(getString(R.string.submitted_homework));
        mBinding.layoutToolbar.textViewToolbarTitle.setGravity(Gravity.START);
        mBinding.layoutToolbar.getRoot().setElevation(0f);
    }

    private void listenRxBusEvents() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof RefreshHomeworkEvent) {
                    fetchSubmittedList(0);
                }

            }
        });
    }


    @SuppressLint("CheckResult")
    private void fetchSubmittedList(final int overduePosition) {
        if (GeneralUtils.isNetworkAvailable(this)) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mHomeworkModel.fetchHomework(null).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<AssignedHomeworkParent>() {
                        @Override
                        public void accept(AssignedHomeworkParent assignedHomeworkParent) throws Exception {
                            mBinding.progressBar.setVisibility(View.GONE);
                            if (assignedHomeworkParent != null) {
                                if (!assignedHomeworkParent.getSubmittedAssignment().getAssignmentsList().isEmpty()) {
                                    mBinding.layoutNoResult.setVisibility(View.GONE);
                                    mBinding.list.setVisibility(View.VISIBLE);
                                    setAdapter(assignedHomeworkParent.getSubmittedAssignment().getAssignmentsList());
                                } else {
                                    mBinding.list.setVisibility(View.GONE);
                                    mBinding.layoutNoResult.setVisibility(View.VISIBLE);
                                }


                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBar.setVisibility(View.GONE);

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
                        fetchSubmittedList(0);
                    }
                })
                .show();

    }

    private void setAdapter(List<Homework> assignmentsList) {
        for (Homework submit : assignmentsList) {
            submit.setHomeworkType(ConstantUtil.SUBMITTED);
        }

        mBinding.list.setLayoutManager(new LinearLayoutManager(SubmitHomeworkActivity.this));
        mBinding.list.setAdapter(new HomeworkAssignedAdapter(assignmentsList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.dispose();
        }

    }

}
