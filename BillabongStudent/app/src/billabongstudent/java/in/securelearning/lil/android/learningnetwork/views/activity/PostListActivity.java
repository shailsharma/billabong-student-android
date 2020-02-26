package in.securelearning.lil.android.learningnetwork.views.activity;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutActivityGroupPostBinding;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostListFragment;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Chaitendra on 01-Aug-17.
 */

public class PostListActivity extends AppCompatActivity {
    @Inject
    GroupModel mGroupModel;

    @Inject
    RxBus mRxBus;
    Disposable mSubscription;
    private LayoutActivityGroupPostBinding mBinding;
    public static final String GROUP_ID = "groupId";
    public static final String REQUEST_FAVORITE_LIST = "requestFavoriteList";
    private String mGroupId = "";
    private boolean mIsFavoriteListVisible = true;
    private MenuItem mFavoriteListMenuItem;

    public static Intent getIntentForPostList(Context context, String groupId, boolean requestFavoriteList) {
        Intent intent = new Intent(context, PostListActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        intent.putExtra(REQUEST_FAVORITE_LIST, requestFavoriteList);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_activity_group_post);
        listenRxBusEvent();

    }

    private void listenRxBusEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object event) {
                if (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(Group.class)) {

                    if (!mIsFavoriteListVisible) {
                        Group group = mGroupModel.getGroupFromUidSync(mGroupId);
                        setUpToolbar(group);
                    }


                }
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handleIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_post, menu);
        mFavoriteListMenuItem = menu.findItem(R.id.action_favorite_posts);
        favoriteListMenuItemVisibility(!mIsFavoriteListVisible);
        return true;
    }

    private void favoriteListMenuItemVisibility(boolean b) {
        if (mFavoriteListMenuItem != null) {
            mFavoriteListMenuItem.setVisible(b);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_favorite_posts:
                favoriteMenuItemClickAction();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void favoriteMenuItemClickAction() {
        startActivity(PostListActivity.getIntentForPostList(getBaseContext(), mGroupId, true));
    }

    private void setUpToolbar(Group group) {

        setSupportActionBar(mBinding.toolbar);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_light);
        CommonUtils.getInstance().setStatusBarIconsDark(PostListActivity.this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setGroupLogo(group);

        String name = group.getGroupName();

        if (!TextUtils.isEmpty(group.getNameTeacher())) {
            name = group.getNameTeacher();
        }


        if (!mIsFavoriteListVisible) {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setSubtitle(getString(R.string.learning_network_subtitle));
            if (!TextUtils.isEmpty(group.getPurpose())) {
                getSupportActionBar().setSubtitle(group.getPurpose());

            }
        } else {
            getSupportActionBar().setTitle(getString(R.string.learning_network_favorite_title) + " " + name);

        }

        mBinding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsFavoriteListVisible) {
                    startActivity(GroupDetailActivity.getIntentForGroupDetail(getBaseContext(), mGroupId));
                }
            }
        });


    }

    private void setGroupLogo(Group group) {
        if (group.getThumbnail().getLocalUrl() != null && !group.getThumbnail().getLocalUrl().isEmpty()) {
            loadGroupIconToToolbar(group.getThumbnail().getLocalUrl());
        } else if (group.getThumbnail().getUrl() != null && !group.getThumbnail().getUrl().isEmpty()) {
            loadGroupIconToToolbar(group.getThumbnail().getUrl());
        } else if (group.getThumbnail().getThumb() != null && !group.getThumbnail().getThumb().isEmpty()) {
            loadGroupIconToToolbar(group.getThumbnail().getThumb());
        }
    }

    private void loadGroupIconToToolbar(String url) {

        Picasso.with(this)
                .load(url)
                .transform(new CircleTransform())
                .resize(72, 72)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                        getSupportActionBar().setIcon(d);
                        getSupportActionBar().setDisplayShowHomeEnabled(true);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });

    }

    private void handleIntent() {

        mGroupId = getIntent().getStringExtra(GROUP_ID);
        mIsFavoriteListVisible = getIntent().getBooleanExtra(REQUEST_FAVORITE_LIST, true);
        Group group = mGroupModel.getGroupFromUidSync(mGroupId);
        if (mIsFavoriteListVisible) {
            favoriteListMenuItemVisibility(false);
        } else {
            favoriteListMenuItemVisibility(true);
        }

        setUpToolbar(group);
        PostListFragment fragment = PostListFragment.newInstance(1, mGroupId, mIsFavoriteListVisible);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerMain, fragment);
        fragmentTransaction.commit();
    }
}
