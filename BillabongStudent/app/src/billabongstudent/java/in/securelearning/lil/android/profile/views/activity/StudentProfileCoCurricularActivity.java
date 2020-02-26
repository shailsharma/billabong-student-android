package in.securelearning.lil.android.profile.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;
import com.squareup.picasso.Picasso;

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
import in.securelearning.lil.android.home.events.ChallengeAndVideoAsInterestRefreshEvent;
import in.securelearning.lil.android.profile.dataobject.UserInterest;
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

public class StudentProfileCoCurricularActivity extends AppCompatActivity {

    @Inject
    ProfileModel mProfileModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    RxBus mRxBus;

    private static final String CO_CURRICULAR_ACTIVITY_LIST = "coCurricularActivityList";
    private static final String ALREADY_SELECTED_ACTIVITIES = "alreadySelectedActivities";
    private static final String USER_INTEREST_ID = "userInterestId";

    LayoutProfileGoalInterestBubbleBinding mBinding;

    private String[] mCoCurricularActivityTitles;
    private ArrayList<UserInterest> mActivityList;
    private String mUserInterestId;
    private List<PickerItem> mPickerItemList = new ArrayList<>();
    private ArrayList<String> mSelectedActivityIds = new ArrayList<>();
    private ArrayList<UserInterest> mAlreadySelectedActivities = new ArrayList<>();
    private ArrayList<String> mAlreadySelectedActivityTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_profile_goal_interest_bubble);

        mActivityList = new ArrayList<>();

        setUpStatusBarAndToolbar();
        handleIntent();

        initializeViewsAndListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
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

    public static Intent getStartIntent(Context context, ArrayList<UserInterest> coCurricularList,
                                        String userInterestId, ArrayList<UserInterest> alreadySelectedActivities) {
        Intent intent = new Intent(context, StudentProfileCoCurricularActivity.class);
        intent.putExtra(CO_CURRICULAR_ACTIVITY_LIST, coCurricularList);
        intent.putExtra(USER_INTEREST_ID, userInterestId);
        intent.putExtra(ALREADY_SELECTED_ACTIVITIES, alreadySelectedActivities);
        return intent;
    }

    /*Handle intent*/
    private void handleIntent() {
        if (getIntent() != null) {
            mActivityList = (ArrayList<UserInterest>) getIntent().getSerializableExtra(CO_CURRICULAR_ACTIVITY_LIST);
            mUserInterestId = getIntent().getStringExtra(USER_INTEREST_ID);
            mAlreadySelectedActivities = (ArrayList<UserInterest>) getIntent().getSerializableExtra(ALREADY_SELECTED_ACTIVITIES);
        }
    }

    private void setUpStatusBarAndToolbar() {
        CommonUtils.getInstance().setImmersiveUiWithoutFitSystemWindow(getWindow(), ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle(ConstantUtil.BLANK);

        mBinding.textViewToolbarTitle.setText("Select Co-curricular Activity");
    }

    private void initializeViewsAndListeners() {

        Picasso.with(getBaseContext())
                .load(R.drawable.background_extra_activity)
                .into(mBinding.imageViewHeader);

        /*Already Selected interest title for picker*/
        if (mAlreadySelectedActivities != null && !mAlreadySelectedActivities.isEmpty()) {
            for (int i = 0; i < mAlreadySelectedActivities.size(); i++) {
                mAlreadySelectedActivityTitles.add(mAlreadySelectedActivities.get(i).getName());
            }
        }

        if (mActivityList != null && !mActivityList.isEmpty()) {
            initializeCoCurricularActivityPicker(mActivityList);
        } else {
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.messageNoDataFound));
            finish();
        }

        mBinding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSelectedActivityIds.clear();

                /*user interest ids selected by user on save*/
                if (mBinding.bubblePicker.getSelectedItems() != null && !mBinding.bubblePicker.getSelectedItems().isEmpty()) {
                    mPickerItemList = mBinding.bubblePicker.getSelectedItems();

                    for (int i = 0; i < mPickerItemList.size(); i++) {

                        for (int j = 0; j < mActivityList.size(); j++) {
                            if (mPickerItemList.get(i).getTitle().equals(mActivityList.get(j).getName())) {
                                mSelectedActivityIds.add(mActivityList.get(j).getId());
                            }
                        }

                    }

                    sendCoCurricularActivityToServer();

                } else {
                    GeneralUtils.showToastShort(getBaseContext(), "Please select co-curricular activity");
                }
            }
        });

    }

    private void initializeCoCurricularActivityPicker(ArrayList<UserInterest> list) {

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.clear();
        for (int i = 0; i < list.size(); i++) {
            stringArrayList.add(list.get(i).getName());
        }

        mCoCurricularActivityTitles = new String[stringArrayList.size()];
        mCoCurricularActivityTitles = stringArrayList.toArray(mCoCurricularActivityTitles);

        mBinding.bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return mCoCurricularActivityTitles.length;
            }


            @NonNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(mCoCurricularActivityTitles[position]);
                item.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorGoalBubbleBackground));
                item.setTextColor(ContextCompat.getColor(StudentProfileCoCurricularActivity.this, android.R.color.white));
                item.setBackgroundImage(ContextCompat.getDrawable(StudentProfileCoCurricularActivity.this, R.drawable.image_circle_bubble_light));

                /*setting already selected interests highlighted*/
                if (!mAlreadySelectedActivityTitles.isEmpty()) {

                    for (int i = 0; i < mAlreadySelectedActivityTitles.size(); i++) {

                        if (mAlreadySelectedActivityTitles.get(i).equals(mCoCurricularActivityTitles[position])) {
                            item.setSelected(true);
                        }

                    }

                }

                return item;
            }
        });


        mBinding.bubblePicker.setCenterImmediately(true);
        mBinding.bubblePicker.setMaxSelectedCount(1);
        mBinding.bubblePicker.setBubbleSize(40);

    }

    /*To send selected user interest to server*/
    @SuppressLint("CheckResult")
    private void sendCoCurricularActivityToServer() {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            mBinding.layoutProgressBottom.setVisibility(View.VISIBLE);

            UserInterestPost post = new UserInterestPost();
            post.setCoCurricularActivityIds(mSelectedActivityIds);
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
                            mRxBus.send(new ChallengeAndVideoAsInterestRefreshEvent());
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
