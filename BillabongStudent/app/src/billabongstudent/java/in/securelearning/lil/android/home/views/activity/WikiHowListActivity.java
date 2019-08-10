package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewFitWindowBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.views.adapter.WikiHowRecyclerViewAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WikiHowListActivity extends AppCompatActivity {
    private static final String TOPIC_NAME = "topicName";
    private static final String TOPIC_IDS = "topicIds";
    LayoutRecyclerViewFitWindowBinding mBinding;

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    public static Intent getStartIntent(Context context, String topicName, ArrayList<String> topicIds) {
        Intent intent = new Intent(context, WikiHowListActivity.class);
        intent.putExtra(TOPIC_NAME, topicName);
        intent.putStringArrayListExtra(TOPIC_IDS, topicIds);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_recycler_view_fit_window);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setBackgroundDrawableResource(R.drawable.gradient_app);
        handleIntent();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar(String title) {
        setSupportActionBar(mBinding.toolbar);
        mBinding.imageViewLogo.setImageResource(R.drawable.logo_wikihow);
        if (!TextUtils.isEmpty(title)) {
            mBinding.textViewToolbarTitle.setVisibility(View.VISIBLE);
            mBinding.textViewToolbarTitle.setText(title);
        } else {
            mBinding.textViewToolbarTitle.setVisibility(View.GONE);
        }

        setTitle(ConstantUtil.BLANK);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.gradient_app));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.action_arrow_left_light);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void handleIntent() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            String title = intent.getStringExtra(TOPIC_NAME);
            ArrayList<String> topicIds = intent.getStringArrayListExtra(TOPIC_IDS);
            setUpToolbar(title);
            getWikiHowData(topicIds);

        } else {
            GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
            finish();
        }
    }

    @SuppressLint("CheckResult")
    private void getWikiHowData(ArrayList<String> topicIds) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
            mBinding.textViewError.setVisibility(View.GONE);
            mFlavorHomeModel.getWikiHowData(topicIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<WikiHowParent>>() {
                        @Override
                        public void accept(ArrayList<WikiHowParent> list) throws Exception {
                            mBinding.progressBar.setVisibility(View.GONE);
                            if (list != null && !list.isEmpty()) {
                                mBinding.recyclerView.setVisibility(View.VISIBLE);
                                mBinding.textViewError.setVisibility(View.GONE);
                                initializeRecyclerView(list);
                            } else {
                                mBinding.recyclerView.setVisibility(View.GONE);
                                mBinding.textViewError.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.recyclerView.setVisibility(View.GONE);
                            mBinding.textViewError.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.connect_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeRecyclerView(ArrayList<WikiHowParent> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(new WikiHowRecyclerViewAdapter(getBaseContext(), list));
    }

}
