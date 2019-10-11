package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutVocationalTopicBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.home.views.adapter.VocationalTopicsAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopic;
import in.securelearning.lil.android.syncadapter.dataobjects.VocationalTopicRequest;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VocationalTopicsActivity extends AppCompatActivity {

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    private static final String SUBJECT_ID = "subjectId";
    private static final String SUBJECT_NAME = "subjectName";

    LayoutVocationalTopicBinding mBinding;

    private String mSubjectId, mSubjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_vocational_topic);

        handleIntent();
        setUpStatusBarAndToolbar();

        getVocationalTopics();

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

    private void setUpStatusBarAndToolbar() {

        CommonUtils.getInstance().setStatusBarIconsDark(VocationalTopicsActivity.this);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.white));

        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle(ConstantUtil.BLANK);

        if (!TextUtils.isEmpty(mSubjectName)) {
            mBinding.textViewToolbarTitle.setText(mSubjectName);
        }
    }

    public static Intent getStartIntent(Context context, String subjectId, String subjectName) {
        Intent intent = new Intent(context, VocationalTopicsActivity.class);
        intent.putExtra(SUBJECT_ID, subjectId);
        intent.putExtra(SUBJECT_NAME, subjectName);
        return intent;
    }

    /*Handle intent*/
    private void handleIntent() {
        if (getIntent() != null) {

            mSubjectId = getIntent().getStringExtra(SUBJECT_ID);
            mSubjectName = getIntent().getStringExtra(SUBJECT_NAME);

        }
    }

    private void initializeViewsAndListeners() {


    }


    /*To fetch vocational topic, to get this vocational subject id is compulsory in post object*/
    @SuppressLint("CheckResult")
    private void getVocationalTopics() {

        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {


            VocationalTopicRequest topicRequest = new VocationalTopicRequest();
            topicRequest.setSubjectId(mSubjectId);

            mBinding.layoutProgressBottom.setVisibility(View.VISIBLE);
            mBinding.textViewError.setVisibility(View.GONE);
            mBinding.recyclerViewTopic.setVisibility(View.GONE);

            mFlavorHomeModel.fetchVocationalTopics(topicRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<VocationalTopic>>() {
                        @Override
                        public void accept(ArrayList<VocationalTopic> topicList) throws Exception {

                            mBinding.layoutProgressBottom.setVisibility(View.GONE);

                            if (topicList != null && !topicList.isEmpty()) {
                                mBinding.textViewError.setVisibility(View.GONE);
                                mBinding.recyclerViewTopic.setVisibility(View.VISIBLE);

                                initializeRecyclerView(topicList);

                            } else {
                                mBinding.textViewError.setVisibility(View.VISIBLE);
                                mBinding.recyclerViewTopic.setVisibility(View.GONE);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();

                            mBinding.layoutProgressBottom.setVisibility(View.GONE);
                            mBinding.textViewError.setVisibility(View.VISIBLE);
                            mBinding.recyclerViewTopic.setVisibility(View.GONE);
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
                        getVocationalTopics();
                    }
                })
                .show();

    }

    private void initializeRecyclerView(ArrayList<VocationalTopic> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewTopic.setLayoutManager(layoutManager);
        VocationalTopicsAdapter vocationalTopicsAdapter = new VocationalTopicsAdapter(getBaseContext(), list);
        mBinding.recyclerViewTopic.setAdapter(vocationalTopicsAdapter);
    }


}
