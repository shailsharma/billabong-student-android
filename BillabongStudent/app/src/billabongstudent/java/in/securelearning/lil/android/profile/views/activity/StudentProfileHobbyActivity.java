package in.securelearning.lil.android.profile.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

public class StudentProfileHobbyActivity extends AppCompatActivity {

    @Inject
    ProfileModel mProfileModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    RxBus mRxBus;

    private static final String HOBBBY_LIST = "hobbyList";
    private static final String ALREADY_SELECTED_HOBBY_IDS = "alreadySelectedHobbyIds";
    private static final String USER_INTEREST_ID = "userInterestId";

    LayoutProfileGoalInterestBubbleBinding mBinding;

    private String[] mHobbyTitles;
    private ArrayList<UserInterest> mHobbyList;
    private String mUserInterestId;
    private List<PickerItem> mPickerItemList = new ArrayList<>();
    private ArrayList<String> mSelectedHobbyIds = new ArrayList<>();
    private ArrayList<UserInterest> mAlreadySelectedHobbies = new ArrayList<>();
    private ArrayList<String> mAlreadySelectedHobbyTitles = new ArrayList<>();

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

    public static Intent getStartIntent(Context context, ArrayList<UserInterest> hobbyList,
                                        String userInterestId, ArrayList<UserInterest> alreadySelectedHobbyIds) {
        Intent intent = new Intent(context, StudentProfileHobbyActivity.class);
        intent.putExtra(HOBBBY_LIST, hobbyList);
        intent.putExtra(USER_INTEREST_ID, userInterestId);
        intent.putExtra(ALREADY_SELECTED_HOBBY_IDS, alreadySelectedHobbyIds);
        return intent;
    }

    /*Handle intent*/
    private void handleIntent() {
        if (getIntent() != null) {
            mHobbyList = (ArrayList<UserInterest>) getIntent().getSerializableExtra(HOBBBY_LIST);
            mUserInterestId = getIntent().getStringExtra(USER_INTEREST_ID);
            mAlreadySelectedHobbies = (ArrayList<UserInterest>) getIntent().getSerializableExtra(ALREADY_SELECTED_HOBBY_IDS);
        }
    }

    private void setUpStatusBarAndToolbar() {
        CommonUtils.getInstance().setImmersiveUiWithoutFitSystemWindow(getWindow(), ContextCompat.getColor(getBaseContext(), R.color.colorGrey55));
        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle(ConstantUtil.BLANK);

        mBinding.textViewToolbarTitle.setText("Select Hobby");
    }

    private void initializeViewsAndListeners() {

        Picasso.with(getBaseContext())
                .load(R.drawable.background_hobby)
                .into(mBinding.imageViewHeader);

        /*Already Selected interest title for picker*/
        if (mAlreadySelectedHobbies != null && !mAlreadySelectedHobbies.isEmpty()) {
            for (int i = 0; i < mAlreadySelectedHobbies.size(); i++) {
                mAlreadySelectedHobbyTitles.add(mAlreadySelectedHobbies.get(i).getName());
            }
        }

        if (mHobbyList != null && !mHobbyList.isEmpty()) {
            initializeCoCurricularActivityPicker(mHobbyList);
        } else {
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.messageNoDataFound));
            finish();
        }

        mBinding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSelectedHobbyIds.clear();

                /*user interest ids selected by user on save*/
                if (mBinding.bubblePicker.getSelectedItems() != null && !mBinding.bubblePicker.getSelectedItems().isEmpty()) {
                    mPickerItemList = mBinding.bubblePicker.getSelectedItems();

                    for (int i = 0; i < mPickerItemList.size(); i++) {

                        for (int j = 0; j < mHobbyList.size(); j++) {
                            if (mPickerItemList.get(i).getTitle().equals(mHobbyList.get(j).getName())) {
                                mSelectedHobbyIds.add(mHobbyList.get(j).getId());
                            }
                        }

                    }

                    sendHobbyToServer();

                } else {
                    GeneralUtils.showToastShort(getBaseContext(), "Please select hobby");
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

        mHobbyTitles = new String[stringArrayList.size()];
        mHobbyTitles = stringArrayList.toArray(mHobbyTitles);

        mBinding.bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return mHobbyTitles.length;
            }


            @NonNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(mHobbyTitles[position]);
                item.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorGoalBubbleBackground));
                item.setTextColor(ContextCompat.getColor(StudentProfileHobbyActivity.this, android.R.color.white));
                item.setBackgroundImage(ContextCompat.getDrawable(StudentProfileHobbyActivity.this, R.drawable.image_circle_bubble_light));

                /*setting already selected interests highlighted*/
                if (!mAlreadySelectedHobbyTitles.isEmpty()) {

                    for (int i = 0; i < mAlreadySelectedHobbyTitles.size(); i++) {

                        if (mAlreadySelectedHobbyTitles.get(i).equals(mHobbyTitles[position])) {
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
    private void sendHobbyToServer() {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            mBinding.layoutProgressBottom.setVisibility(View.VISIBLE);

            UserInterestPost post = new UserInterestPost();
            post.setHobbyIds(mSelectedHobbyIds);
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
