package in.securelearning.lil.android.profile.views.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutProfileGoalInterestBubbleBinding;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.profile.dataobject.UserInterestPost;
import in.securelearning.lil.android.profile.event.StudentPersonalProfileRefreshEvent;
import in.securelearning.lil.android.profile.model.ProfileModel;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class StudentProfileGoalActivity extends AppCompatActivity {

    @Inject
    ProfileModel mProfileModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    RxBus mRxBus;

    private static final String USER_INTEREST_ID = "userInterestId";
    private static final String ALREADY_DAILY_GOAL = "alreadyDailyGoal";


    LayoutProfileGoalInterestBubbleBinding mBinding;

    private String[] mDailyGoals;
    private List<PickerItem> mPickerItemList = new ArrayList<>();
    private String mUserInterestId, mSelectedDailyGoal, mAlreadyDailyGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_profile_goal_interest_bubble);

        setUpStatusBarAndToolbar();
        handleIntent();

        initializeViewsAndListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.bubblePicker.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.bubblePicker.onPause();
    }

    public static Intent getStartIntent(Context context, String userInterestId, int alreadyDailyGoal) {
        Intent intent = new Intent(context, StudentProfileGoalActivity.class);
        intent.putExtra(USER_INTEREST_ID, userInterestId);
        intent.putExtra(ALREADY_DAILY_GOAL, alreadyDailyGoal);
        return intent;
    }

    /*Handle intent*/
    private void handleIntent() {
        if (getIntent() != null) {
            mUserInterestId = getIntent().getStringExtra(USER_INTEREST_ID);
            mAlreadyDailyGoal = String.valueOf(getIntent().getIntExtra(ALREADY_DAILY_GOAL, 0));
        }
    }

    private void setUpStatusBarAndToolbar() {
        CommonUtils.getInstance().setImmersiveUiWithoutFitSystemWindow(getWindow(), ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle(ConstantUtil.BLANK);

        mBinding.textViewToolbarTitle.setText("Set your Goal/Day (min)");
    }

    private void initializeViewsAndListeners() {

        Picasso.with(getBaseContext())
                .load(R.drawable.background_goal)
                .into(mBinding.imageViewHeader);

        mDailyGoals = new String[]{"10", "15", "20", "30", "45", "60"};

        initializeCoCurricularActivityPicker();

        mBinding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBinding.bubblePicker.getSelectedItems() != null && !mBinding.bubblePicker.getSelectedItems().isEmpty()) {
                    mPickerItemList = mBinding.bubblePicker.getSelectedItems();

                    for (int i = 0; i < mPickerItemList.size(); i++) {
                        Log.e("ZOOM", "onClick: " + mPickerItemList.get(i).getTitle());
                    }

                    if (mPickerItemList.get(0) != null && !TextUtils.isEmpty(mPickerItemList.get(0).getTitle())) {
                        mSelectedDailyGoal = mPickerItemList.get(0).getTitle();
                    }


                    sendDailyGoalToServer();

                } else {
                    GeneralUtils.showToastShort(getBaseContext(), "Please select goal");
                }
            }
        });


    }

    private void initializeCoCurricularActivityPicker() {

        mBinding.bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return mDailyGoals.length;
            }

            @NotNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(mDailyGoals[position]);
                item.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorGoalBubbleBackground));
                item.setTextColor(ContextCompat.getColor(StudentProfileGoalActivity.this, android.R.color.white));
                item.setBackgroundImage(ContextCompat.getDrawable(StudentProfileGoalActivity.this, R.drawable.image_circle_bubble_light));

                /*setting already selected goal highlighted*/
                if (mAlreadyDailyGoal.equalsIgnoreCase(mDailyGoals[position])) {
                    item.setSelected(true);
                }

                return item;
            }
        });


        mBinding.bubblePicker.setCenterImmediately(true);
        mBinding.bubblePicker.setMaxSelectedCount(1);
        mBinding.bubblePicker.setBubbleSize(40);

    }

    /*To send selected user daily goal to server*/
    @SuppressLint("CheckResult")
    private void sendDailyGoalToServer() {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            mBinding.layoutProgressBottom.setVisibility(View.VISIBLE);

            UserInterestPost post = new UserInterestPost();
            if (!TextUtils.isEmpty(mSelectedDailyGoal) && Integer.parseInt(mSelectedDailyGoal) > 0) {
                post.setDailyTarget(Integer.parseInt(mSelectedDailyGoal));
            }
            if (!TextUtils.isEmpty(mUserInterestId)) {
                post.setUserInterestId(mUserInterestId);
            }

            mProfileModel.sendUserInterestToServer(post, mUserInterestId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody body) throws Exception {
                            mBinding.layoutProgressBottom.setVisibility(View.GONE);

                            mRxBus.send(new StudentPersonalProfileRefreshEvent());
                            finish();

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.layoutProgressBottom.setVisibility(View.GONE);
                            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
                        }
                    });

        } else {
            SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
        }

    }

}