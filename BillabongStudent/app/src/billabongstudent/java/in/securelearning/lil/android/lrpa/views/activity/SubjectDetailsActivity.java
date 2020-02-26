package in.securelearning.lil.android.lrpa.views.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCustomAppBarViewpagerBinding;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.lrpa.events.FetchSubjectDetailEvent;
import in.securelearning.lil.android.lrpa.model.LRPAModel;
import in.securelearning.lil.android.lrpa.views.adapter.SubjectDetailsTabAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapter;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanGroupDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectDetails;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.utils.AppBarStateChangeListener;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.thirdparty.events.MindSparkNoUnitEvent;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubjectDetailsActivity extends AppCompatActivity {

    @Inject
    LRPAModel mLRPAModel;

    @Inject
    RxBus mRxBus;

    LayoutCustomAppBarViewpagerBinding mBinding;
    private final static String SUBJECT_ID = "subjectId";
    private String mSubjectId;
    private String mTopicId;
    private String mTopicName;
    private String mSubjectName;
    private String mGroupId;
    private String mBannerUrl;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_custom_app_bar_viewpager);

        handleIntent();
        listenRxEvent();

        /*Setting status bar style immersive*/
        CommonUtils.getInstance().setImmersiveStatusBar(getWindow());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @SuppressLint("CheckResult")
    private void listenRxEvent() {
        mDisposable = mRxBus.toFlowable()
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(final Object event) {
                        if (event instanceof FetchSubjectDetailEvent) {
                            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                                @Override
                                public void run() {

                                    String topicId = ((FetchSubjectDetailEvent) event).getId();
                                    String topicName = ((FetchSubjectDetailEvent) event).getChapterTitle();
                                    String chapterStatus = ((FetchSubjectDetailEvent) event).getChapterStatus();

                                    LessonPlanChapter lessonPlanChapter = new LessonPlanChapter();
                                    lessonPlanChapter.setName(topicName);
                                    lessonPlanChapter.setId(topicId);
                                    lessonPlanChapter.setStatus(chapterStatus);

                                    setTopicContent(lessonPlanChapter);

                                    mBinding.viewPager.setCurrentItem(0, true);

                                    handleViewPagerRefresh();

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                        } else if (event instanceof MindSparkNoUnitEvent) {
                            Completable.complete()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                            CommonUtils.getInstance().showAlertDialog(SubjectDetailsActivity.this, getString(R.string.mindSparkNoUnitMessageHome));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("CheckResult")
    private void fetchSubjectDetails(final String subjectId) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            final Dialog progressDialog = CommonUtils.getInstance().loadingDialog(SubjectDetailsActivity.this, getString(R.string.messagePleaseWait));

            mLRPAModel.getSubjectDetails(subjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LessonPlanSubjectDetails>() {
                        @Override
                        public void accept(LessonPlanSubjectDetails lessonPlanSubjectDetails) throws Exception {
                            progressDialog.dismiss();

                            setSubjectContent(lessonPlanSubjectDetails.getSubject());
                            setTopicContent(lessonPlanSubjectDetails.getTopic());
                            checkGroupExistence(lessonPlanSubjectDetails.getGroup());

                            handleViewPagerRefresh();


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            progressDialog.dismiss();
                            if (!TextUtils.isEmpty(throwable.getMessage())) {
                                retryDialog(throwable.getMessage(), subjectId);
                            } else {
                                retryDialog(getString(R.string.error_something_went_wrong), subjectId);
                            }
                            throwable.printStackTrace();
                        }
                    });
        } else {
            retryDialog(getString(R.string.error_message_no_internet), subjectId);
        }

    }

    public static Intent getStartIntent(Context context, String subjectId) {
        Intent intent = new Intent(context, SubjectDetailsActivity.class);
        intent.putExtra(SUBJECT_ID, subjectId);
        return intent;
    }

    /*If view pager is already setup,than just notify the changes
     * and if not then setup viewpager.*/
    private void handleViewPagerRefresh() {
        if (mBinding.viewPager.getAdapter() != null) {
            mBinding.viewPager.getAdapter().notifyDataSetChanged();
            setUpViewPager();
        } else {
            setUpViewPager();
        }
    }

    /*Checking group is exist or not in database*/
    private void checkGroupExistence(LessonPlanGroupDetails group) {
        if (group != null && !TextUtils.isEmpty(group.getId())) {
            Group offlineGroup = mLRPAModel.getGroupFromId(group.getId());

            /*saving the updated group, if not exist in database*/
            if (TextUtils.isEmpty(offlineGroup.getObjectId())) {
                mLRPAModel.downloadAndSaveGroup(group.getId());
                mGroupId = group.getId();
            } else {
                mGroupId = offlineGroup.getObjectId();

                if (!offlineGroup.isNetworkDataDownloaded()) {
                    JobModel.startLearningNetworkSyncForGroup(mGroupId);

                }
            }
        } else {
            mGroupId = null;
        }

    }

    /*Set subject details*/
    private void setSubjectContent(LessonPlanSubject lessonPlanSubject) {
        if (lessonPlanSubject != null) {

            if (!TextUtils.isEmpty(lessonPlanSubject.getBannerUrl())) {
                Picasso.with(getBaseContext()).load(lessonPlanSubject.getBannerUrl()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(mBinding.headerImageView);
                mBannerUrl = lessonPlanSubject.getBannerUrl();
            } else {
                Picasso.with(getBaseContext()).load(R.drawable.image_placeholder).fit().centerCrop().into(mBinding.headerImageView);
            }

            if (!TextUtils.isEmpty(lessonPlanSubject.getName())) {
                setUpToolbar(lessonPlanSubject.getName());
                mSubjectName = lessonPlanSubject.getName();
            } else {
                setUpToolbar(ConstantUtil.BLANK);
            }

        }


    }

    /*Set topic details*/
    private void setTopicContent(LessonPlanChapter lessonPlanChapter) {
        if (lessonPlanChapter != null) {
            mBinding.layoutHeaderContent.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(lessonPlanChapter.getName())) {
                mBinding.textViewHeaderTitle.setText(lessonPlanChapter.getName());
                mTopicName = lessonPlanChapter.getName();
            }

            String chapterStatus = "(" + mLRPAModel.getChapterStatus(lessonPlanChapter.getStatus()) + ")";
            mBinding.textViewHeaderSubTitle.setText(chapterStatus);


            if (!TextUtils.isEmpty(lessonPlanChapter.getId())) {
                mTopicId = lessonPlanChapter.getId();
            }
        } else {
            mBinding.layoutHeaderContent.setVisibility(View.GONE);
        }
    }


    /*Handle intent and get bundle data*/
    private void handleIntent() {
        if (getIntent() != null) {
            mSubjectId = getIntent().getStringExtra(SUBJECT_ID);
            fetchSubjectDetails(mSubjectId);

        }
    }

    /*Setup toolbar*/
    private void setUpToolbar(String title) {
        setSupportActionBar(mBinding.toolbar);
        setTitle(ConstantUtil.BLANK);
        mBinding.textViewToolbarTitle.setText(title);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));
        mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

        mBinding.appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.e("STATE", state.name());
                if (state.name().equalsIgnoreCase(State.COLLAPSED.toString())) {
                    /*collapsed completely*/

                    /*for status bar*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                    } else {
                        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
                    }

                    mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
                    mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));

                    /*For toolbar*/
                    mBinding.toolbar.setNavigationIcon(R.drawable.icon_arrow_left_dark);
                    mBinding.textViewToolbarTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));

                    requestLayout(mBinding.viewPager);

                } else if (state.name().equalsIgnoreCase(State.EXPANDED.toString())) {
                    /* not collapsed*/

                    /*for status bar*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(0);

                    } else {
                        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
                    }


                    mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));
                    mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

                    /*For toolbar*/
                    mBinding.toolbar.setNavigationIcon(R.drawable.arrow_left_white);
                    mBinding.textViewToolbarTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));

                    requestLayout(mBinding.appBarLayout);

                }
            }
        });


    }

    private void requestLayout(View view) {
        view.requestLayout();
    }

    /*alert dialog to show error, message and providing option to retry the respective call*/
    private void retryDialog(String message, final String subjectId) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SubjectDetailsActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.labelRetry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fetchSubjectDetails(subjectId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.go_back), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();

                    }
                })
                .setCancelable(false);
        final android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    /*setup viewpager and it adapter*/
    private void setUpViewPager() {
        final ArrayList<String> tabTitles = mLRPAModel.getSubjectTabTitles(mGroupId);
        mBinding.viewPager.setAdapter(new SubjectDetailsTabAdapter(getBaseContext(), getSupportFragmentManager(),
                tabTitles, mSubjectId, mTopicId, mTopicName,
                mSubjectName, mGroupId, mBannerUrl));
        mBinding.viewPager.setOffscreenPageLimit(tabTitles.size());
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
    }


}
