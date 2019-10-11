package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCustomAppBarViewpagerBinding;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.events.FetchSubjectDetailEvent;
import in.securelearning.lil.android.home.events.MindSparkNoUnitEvent;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.views.fragment.ChaptersFragment;
import in.securelearning.lil.android.home.views.fragment.SubjectDetailHomeFragment;
import in.securelearning.lil.android.home.views.fragment.SubjectHomeworkFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostListFragment;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapter;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanGroupDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubject;
import in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanSubjectDetails;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.utils.AppBarStateChangeListener;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_COMPLETED;
import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_IN_PROGRESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_YET_TO_START;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_COMPLETED;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_IN_PROGRESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_YET_TO_START;

public class SubjectDetailsActivity extends AppCompatActivity {

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    @Inject
    RxBus mRxBus;

    LayoutCustomAppBarViewpagerBinding mBinding;
    private final static String SUBJECT_ID = "subjectId";
    private String mSubjectId;
    private String mGradeName;
    private String mTopicId;
    private String mTopicName;
    private ArrayList<String> mThirdPartyTopicIds = new ArrayList<>();
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
        mDisposable = mRxBus.toFlowable().observeOn(Schedulers.io()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) throws Exception {
                if (event instanceof FetchSubjectDetailEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            String topicId = ((FetchSubjectDetailEvent) event).getId();
                            String topicName = ((FetchSubjectDetailEvent) event).getChapterTitle();
                            String chapterStatus = ((FetchSubjectDetailEvent) event).getChapterStatus();
                            LessonPlanChapter lessonPlanChapter = new LessonPlanChapter();
                            lessonPlanChapter.setName(topicName);
                            lessonPlanChapter.setId(topicId);
                            lessonPlanChapter.setStatus(chapterStatus);
                            setTopicContent(lessonPlanChapter);
                            mBinding.viewPager.setCurrentItem(0, true);

//                            if (mSubjectName.contains("Math")) {
//                                fetchThirdPartyMapping(mSubjectId, topicId);
//                            } else {
//                                handleViewPagerRefresh();
//                            }
                            handleViewPagerRefresh();

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
                } else if (event instanceof MindSparkNoUnitEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
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

            mFlavorHomeModel.getSubjectDetails(subjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LessonPlanSubjectDetails>() {
                        @Override
                        public void accept(LessonPlanSubjectDetails lessonPlanSubjectDetails) throws Exception {
                            progressDialog.dismiss();

                            setSubjectContent(lessonPlanSubjectDetails.getSubject());
                            setTopicContent(lessonPlanSubjectDetails.getTopic());
                            checkGroupExistence(lessonPlanSubjectDetails.getGroup());
                            setGradeDetail(lessonPlanSubjectDetails.getGroup(), subjectId);

                            /*TODO hard coded logic for subject check, remove when dynamically done*/
//                            if (mSubjectName.contains("Math")) {
//                                fetchThirdPartyMapping(lessonPlanSubjectDetails.getSubject().getId(), lessonPlanSubjectDetails.getTopic().getId());
//                            } else {
//                                handleViewPagerRefresh();
//                            }
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

    @SuppressLint("CheckResult")
    private void fetchThirdPartyMapping(String subjectId, String topicId) {
        final Dialog progressDialog = CommonUtils.getInstance().loadingDialog(SubjectDetailsActivity.this, getString(R.string.messagePleaseWait));

        mFlavorHomeModel.fetchThirdPartyMapping(new ThirdPartyMapping(subjectId, topicId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<String>>() {
                    @Override
                    public void accept(ArrayList<String> thirdPartyMapping) throws Exception {
                        progressDialog.dismiss();
                        if (thirdPartyMapping != null && !thirdPartyMapping.isEmpty()) {
                            mThirdPartyTopicIds = thirdPartyMapping;
                        } else {
                            mThirdPartyTopicIds = new ArrayList<>();
                        }
                        handleViewPagerRefresh();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        progressDialog.dismiss();
                        handleViewPagerRefresh();

                    }
                });
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
            //setUpTabLayout();
            setUpViewPager();
        } else {
            setUpViewPager();
        }
    }

    /*Checking group is exist or not in database*/
    private void checkGroupExistence(LessonPlanGroupDetails group) {
        if (group != null && !TextUtils.isEmpty(group.getId())) {
            Group offlineGroup = mFlavorHomeModel.getGroupFromId(group.getId());

            /*saving the updated group, if not exist in database*/
            if (TextUtils.isEmpty(offlineGroup.getObjectId())) {
                saveGroup(group.getId());
                mGroupId = group.getId();
            } else {
                mGroupId = offlineGroup.getObjectId();
            }
        } else {
            mGroupId = null;
        }

    }

    /*saving the group offline and starting network sync*/
    private void saveGroup(final String groupId) {
        Disposable subscribe = Completable.complete().subscribeOn(Schedulers.computation()).subscribe(new Action() {
            @Override
            public void run() {
                mFlavorHomeModel.downloadGroup(groupId);
                mFlavorHomeModel.downloadGroupPostAndResponse(groupId);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
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

//            mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));
//            mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));

//            int color;
//            if (!TextUtils.isEmpty(lessonPlanSubject.getColorCode())) {
//                color = Color.parseColor(lessonPlanSubject.getColorCode());
//            } else {
//                color = ContextCompat.getColor(getBaseContext(), R.color.colorStartGradient);
//            }

//            mTabsGradientDrawable = CommonUtils.getInstance().getGradientDrawableFromSingleColor(color);
//            mTabsGradientDrawable.setCornerRadius(ConstantUtil.LRPA_TAB_CORNER_RADIUS);
//            mTabsGradientDrawable.setStroke(ConstantUtil.LRPA_TAB_STROKE_SIZE, Color.WHITE);
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

            String chapterStatus = "(" + getChapterStatus(lessonPlanChapter.getStatus()) + ")";
            mBinding.textViewHeaderSubTitle.setText(chapterStatus);


            if (!TextUtils.isEmpty(lessonPlanChapter.getId())) {
                mTopicId = lessonPlanChapter.getId();
            }
        } else {
            mBinding.layoutHeaderContent.setVisibility(View.GONE);
        }
    }

    /*get status of chapter in string value*/
    private String getChapterStatus(String status) {
        switch (status) {
            case STATUS_IN_PROGRESS:
                return HEADER_IN_PROGRESS;
            case STATUS_YET_TO_START:
                return HEADER_YET_TO_START;
            case STATUS_COMPLETED:
                return HEADER_COMPLETED;
            default:
                return HEADER_IN_PROGRESS;
        }
    }


    /*Handle intent and get bundle data*/
    private void handleIntent() {
        if (getIntent() != null) {
            mSubjectId = getIntent().getStringExtra(SUBJECT_ID);
            fetchSubjectDetails(mSubjectId);

        }
    }

    private void setGradeDetail(LessonPlanGroupDetails group, String lessonPlanId) {
        if (group != null) {
            if (group.getGrade() != null && !TextUtils.isEmpty(group.getGrade().getName())) {
                mGradeName = group.getGrade().getName();
            } else {
                retryDialog(getString(R.string.lrpaGradeNull), lessonPlanId);
            }
        } else {
            retryDialog(getString(R.string.lrpaGroupNull), lessonPlanId);
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
                    mBinding.toolbar.setNavigationIcon(R.drawable.action_arrow_left_dark);
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
                    mBinding.toolbar.setNavigationIcon(R.drawable.action_arrow_left_light);
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


    /*get tab titles from string array according to group availability*/
    private ArrayList<String> getTabTitles() {
        if (!TextUtils.isEmpty(mGroupId)) {
            return new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_subject_detail_tab)));
        } else {
            return new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_subject_detail_tab_no_post)));
        }
    }

    /*setup viewpager and it adapter*/
    private void setUpViewPager() {
        final ArrayList<String> tabTitles = getTabTitles();
        mBinding.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), tabTitles));
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
//        mBinding.tabLayout.setSelectedTabIndicatorHeight(0);
//        setUpTabLayout();

//        ViewGroup vg = (ViewGroup) mBinding.tabLayout.getChildAt(0);
//        int tabCount = vg.getChildCount();
//
//        for (int j = 0; j < tabCount; j++) {
//            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
//
//            int tabChildCount = vgTab.getChildCount();
//
//            for (int i = 0; i < tabChildCount; i++) {
//                View tabViewChild = vgTab.getChildAt(i);
//                if (tabViewChild instanceof TextView) {
//
//                    ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(getBaseContext(), R.font.poppins_regular));
//                    ((TextView) tabViewChild).setAllCaps(false);
//
//                }
//            }
//        }


        /*mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        ViewGroup vg = (ViewGroup) mBinding.tabLayout.getChildAt(0);
                        ViewGroup vgTab = (ViewGroup) vg.getChildAt(tab.getPosition());
                        int tabChildsCount = vgTab.getChildCount();

                        for (int i = 0; i < tabChildsCount; i++) {
                            View tabViewChild = vgTab.getChildAt(i);
                            if (tabViewChild instanceof TextView) {
//                                ((TextView) tabViewChild).setTextSize(20);
                                ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(getBaseContext(), R.font.poppins_regular));
// ((TextView) tabViewChild).setTextAppearance(StudentAnalyticsTabActivity.this, android.R.style.TextAppearance_Large);

                            }
                        }

                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                        ViewGroup vg = (ViewGroup) mBinding.tabLayout.getChildAt(0);
                        ViewGroup vgTab = (ViewGroup) vg.getChildAt(tab.getPosition());
                        int tabChildsCount = vgTab.getChildCount();

                        for (int i = 0; i < tabChildsCount; i++) {
                            View tabViewChild = vgTab.getChildAt(i);
                            if (tabViewChild instanceof TextView) {
//                                ((TextView) tabViewChild).setTextSize(15);
                                ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(getBaseContext(), R.font.poppins_regular));
                            }
                        }

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });*/
    }

    /*viewpager adapter to handle and attach the respective fragments of activity*/
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<String> mList;

        ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<String> list) {
            super(fragmentManager);
            mList = list;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position);
        }

        @Override
        public Fragment getItem(int position) {

            if (mList.get(position).equals(getString(R.string.label_home))) {
                return SubjectDetailHomeFragment.newInstance(mSubjectId, mTopicId, mTopicName, mSubjectName, mGradeName, mThirdPartyTopicIds, mBannerUrl);
            } else if (mList.get(position).equals(getString(R.string.chapters))) {
                return ChaptersFragment.newInstance(mSubjectId);
            } else if (mList.get(position).equals(getString(R.string.homework))) {
                return SubjectHomeworkFragment.newInstance(mSubjectId);
            } else if (mList.get(position).equals(getString(R.string.string_post))) {
                return PostListFragment.newInstance(1, mGroupId, false, R.color.colorPrimary);
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

    }


    /*
     *//*setup tab layout - customization of tabs*//*
    private void setUpTabLayout() {
        final ArrayList<String> tabTitles = getTabTitles();
        for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mBinding.tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(getTabView(i, tabTitles));
        }

        highlightCurrentTab(0, tabTitles); // for initial selected tab view

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(final int position) {
                highlightCurrentTab(position, tabTitles);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }


        });
    }

    *//*highlight the current active tab with different background from other tabs*//*
    private void highlightCurrentTab(int position, ArrayList<String> tabTitles) {
        for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mBinding.tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(getTabView(i, getTabTitles()));
        }

        TabLayout.Tab tab = mBinding.tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(getSelectedTabView(position, tabTitles));
    }

    *//*get normal or inactive tab view*//*
    public View getTabView(int position, ArrayList<String> tabTitles) {
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_subject_detail_custom_tab, null);
//        view.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.chip_white));
        TextView tabTextView = view.findViewById(R.id.tabTextView);
        tabTextView.setTextColor(ContextCompat.getColor(getBaseContext(), android.R.color.black));
        tabTextView.setText(tabTitles.get(position));
        tabTextView.setTypeface(ResourcesCompat.getFont(getBaseContext(), R.font.poppins_regular));
        TextView tabImageViewBadge = view.findViewById(R.id.tabTextView2);
        tabImageViewBadge.setVisibility(View.GONE);
        return view;
    }

    *//*get selected or active tab view*//*
    public View getSelectedTabView(int position, ArrayList<String> tabTitles) {
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_subject_detail_custom_tab, null);
//        view.setBackground(mTabsGradientDrawable);
        TextView tabTextView = view.findViewById(R.id.tabTextView);
        tabTextView.setText(tabTitles.get(position));
        tabTextView.setTypeface(ResourcesCompat.getFont(getBaseContext(), R.font.poppins_regular));
        tabTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorLRPACustomTab));
        TextView tabImageViewBadge = view.findViewById(R.id.tabTextView2);
        tabImageViewBadge.setVisibility(View.GONE);
        return view;
    }

    */

}
