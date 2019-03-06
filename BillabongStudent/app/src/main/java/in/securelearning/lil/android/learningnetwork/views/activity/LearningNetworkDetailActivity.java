package in.securelearning.lil.android.learningnetwork.views.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.fragment.BaseLNFragment;
import in.securelearning.lil.android.learningnetwork.views.fragment.MembersFragment;

/**
 * Fragment to show groups, groups members and there post.
 * Created by Pushkar Raj on 8/26/2016.
 */
public class LearningNetworkDetailActivity extends AppCompatActivity {
    /**
     * Request code for select photo
     */
    @Inject
    RxBus mRxBus;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    GroupModel mGroupModel;
    PostDataLearningModel mPostDataLearningModel;
    private Group mCurrentSelectedGroup;
    private TextView mSelectedGroupName;
    private ImageView mSelectedGroupBanner;
    private ImageView mSelectedGroupIcon;
    private ImageButton mBackButton;
    private int mSelectedGroupIndex;
//    private ArrayList<PostDataDetail> duplicatePostDataList;
//    private int lastIndex = 0;
    //   private Timer timer;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.post_w,
            R.drawable.favorite_white,
            R.drawable.members
    };
    public static FloatingActionButton mAddPostButton;
    private ViewPagerAdapter mViewPagerAdapter;
    private long lasAddPostBtnClickTime;

    public static String SELECTED_GROUP_ID = "selectedGroupObjectId";
    private String mSelectedGroupObjectId;
    private Bitmap myBitmap;
    private int bannerImageColor;

    @Override
    public void onBackPressed() {
        finish();
    }

    public static Intent startLearningNetworkActivity(Context context, String groupID) {
        Intent intent = new Intent(context, LearningNetworkDetailActivity.class);
        intent.putExtra(SELECTED_GROUP_ID, groupID);
        return intent;

    }

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_learning_network_viewpager);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryLN));

        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        initializeViews();
        initializeUIandClickListeners();

        Bundle bundle = getIntent().getExtras();
        mSelectedGroupObjectId = bundle.getString("selectedGroupObjectId");

        fetchGroupFromDB();

        setupViewPager(viewPager);
        setUpTabLayout();
    }

    private void fetchGroupFromDB() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mCurrentSelectedGroup = mGroupModel.getGroupFromUidSync(mSelectedGroupObjectId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupGroupDetails(mCurrentSelectedGroup);
                    }
                });
            }
        }).start();


    }

    private void setUpTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(ContextCompat.getColor(LearningNetworkDetailActivity.this, R.color.colorWhite),
                ContextCompat.getColor(LearningNetworkDetailActivity.this, R.color.colorWhite));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(LearningNetworkDetailActivity.this, R.color.tabIndicatorLN));
        tabLayout.setSelectedTabIndicatorHeight(8);

        setupTabIcons();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * Initialize all UI layouts elements.
     */
    private void initializeViews() {
        mBackButton = (ImageButton) findViewById(R.id.button_back);
        mSelectedGroupName = (TextView) findViewById(R.id.selected_group_name);
        mSelectedGroupBanner = (ImageView) findViewById(R.id.imageview_group_banner);
        mSelectedGroupIcon = (ImageView) findViewById(R.id.imageview_group_icon);
        mAddPostButton = (FloatingActionButton) findViewById(R.id.add_post);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        //tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        PostFragment postFragment = new PostFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(BaseLNFragment.ARG_GROUP_OBJECT_ID, mSelectedGroupObjectId);
//        postFragment.setArguments(bundle);
//        mViewPagerAdapter.addFragment(postFragment, "Post");


//        BulletinFragment bulletinFragment = new BulletinFragment();
//        Bundle bundle4 = new Bundle();
//        bundle4.putString(BaseLNFragment.ARG_GROUP_OBJECT_ID, mSelectedGroupObjectId);
//        bulletinFragment.setArguments(bundle4);
//        mViewPagerAdapter.addFragment(bulletinFragment, "Bulletin");

//        FavoritePostFragment favoritePostFragment = new FavoritePostFragment();
//        Bundle bundle1 = new Bundle();
//        bundle1.putString(BaseLNFragment.ARG_GROUP_OBJECT_ID, mSelectedGroupObjectId);
//        favoritePostFragment.setArguments(bundle1);
//
//        mViewPagerAdapter.addFragment(favoritePostFragment, "Favorite");

        MembersFragment membersFragment = new MembersFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString(BaseLNFragment.ARG_GROUP_OBJECT_ID, mSelectedGroupObjectId);
        membersFragment.setArguments(bundle2);

        mViewPagerAdapter.addFragment(membersFragment, "Members");
        viewPager.setAdapter(mViewPagerAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * setup all UI elements listener for click and touch
     */
    private void initializeUIandClickListeners() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mAddPostButton.setVisibility(View.GONE);
        mAddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - lasAddPostBtnClickTime < 2000) {
                    return;
                }
                lasAddPostBtnClickTime = SystemClock.elapsedRealtime();

                Intent mIntent = new Intent(LearningNetworkDetailActivity.this, CreatePostActivity.class);
                mIntent.putExtra("group_object_id", mSelectedGroupObjectId);
                startActivity(mIntent);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0)
                    mAddPostButton.setVisibility(View.INVISIBLE);
                else
                    mAddPostButton.setVisibility(View.VISIBLE);

//                if (position == 2) {
//                    Fragment fragment = mViewPagerAdapter.getItem(viewPager.getCurrentItem());
//                    if (fragment instanceof FavoritePostFragment)
//                        ((FavoritePostFragment) fragment).getData();
//                }
//
//
//                if (position == 0) {
//                    Fragment fragment = mViewPagerAdapter.getItem(viewPager.getCurrentItem());
//                    if (fragment instanceof PostFragment)
//                        ((PostFragment) fragment).getData();
//                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Method used for predict the postdata by the attribute pass by it's caller like Discussion,Events,Favorite etc.
     *
     * @param postDataType SkillMasteryFilter by this attribute
     */
    private void filterPostDatListByAttribute(final String postDataType) {
        mPostDataLearningModel.getFilterPostByGroupIdNdAttribute(mSelectedGroupObjectId, postDataType);
    }

    /**
     * Method to setup RHS view of the the layout like list of post, group name and group icon etc.
     *
     * @param group
     */
    public void setupGroupDetails(final Group group) {

        mSelectedGroupName.setText(group.getGroupName());
        AnimationUtils.fadeIn(LearningNetworkDetailActivity.this, mSelectedGroupName);

        if (group.getBanner().getLocalUrl() != null && !group.getBanner().getLocalUrl().isEmpty()) {
            Picasso.with(LearningNetworkDetailActivity.this).load(group.getBanner().getLocalUrl()).fit().into(mSelectedGroupBanner);
        } else if (group.getBanner().getUrl() != null && !group.getBanner().getUrl().isEmpty()) {
            Picasso.with(LearningNetworkDetailActivity.this).load(group.getBanner().getUrl()).fit().into(mSelectedGroupBanner);
        } else if (group.getBanner().getThumb() != null && !group.getBanner().getThumb().isEmpty()) {
            Picasso.with(LearningNetworkDetailActivity.this).load(group.getBanner().getThumb()).fit().into(mSelectedGroupBanner);
        } else {
            mSelectedGroupBanner.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_audience_large));
            //Picasso.with(LearningNetworkDetailActivity.this).load(group.getBanner().getThumb()).fit().into(mSelectedGroupBanner);
        }

        if (group.getThumbnail().getUrl() != null && !group.getThumbnail().getUrl().isEmpty()) {
            Picasso.with(LearningNetworkDetailActivity.this).load(group.getThumbnail().getUrl()).placeholder(R.drawable.icon_audience_large).resize(300, 300).centerCrop().into(mSelectedGroupIcon);
        } else if (group.getThumbnail().getThumb() != null && !group.getThumbnail().getThumb().isEmpty()) {
            Picasso.with(LearningNetworkDetailActivity.this).load(group.getThumbnail().getThumb()).placeholder(R.drawable.icon_audience_large).resize(300, 300).centerCrop().into(mSelectedGroupIcon);
        } else {
            Picasso.with(LearningNetworkDetailActivity.this).load(R.drawable.icon_audience_large).into(mSelectedGroupIcon);
        }

    }

    /**
     * method to hide soft keyboard
     *
     * @param editText
     */
    public void hideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    public Bitmap getBitmapFromURL(final String src) {

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            // Log exception
        }

        return myBitmap;
    }

    public int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

}






