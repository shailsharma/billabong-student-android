package in.securelearning.lil.android.home.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHelpAndFaqBinding;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.home.views.adapter.HelpAndFaqCategoryListAdapter;
import in.securelearning.lil.android.syncadapter.dataobjects.HelpAndFaqCategory;
import in.securelearning.lil.android.syncadapter.utils.AppBarStateChangeListener;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE;

public class HelpAndFAQActivity extends AppCompatActivity {

    @Inject
    HomeModel mHomeModel;

    private LayoutHelpAndFaqBinding mBinding;

    private Snackbar mInternetSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_help_and_faq);

        setUpToolbarAndStatusBar(getString(R.string.help_and_faqs_amp));
        getHelpAndFAQ();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, HelpAndFAQActivity.class);
    }

    /*Handle intent*/
    private void handleIntent() {
        if (getIntent() != null) {

        }
    }

    /*Setup toolbar and status bar*/
    private void setUpToolbarAndStatusBar(String title) {

        setSupportActionBar(mBinding.toolbar);
        setTitle(ConstantUtil.BLANK);
        mBinding.textViewToolbarTitle.setText(title);
        mBinding.textViewHeaderTitle.setText(title);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mBinding.collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite66));
        mBinding.collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparent));

        mBinding.appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
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
                    mBinding.textViewToolbarTitle.setVisibility(View.VISIBLE);
                    mBinding.toolbar.setNavigationIcon(R.drawable.icon_arrow_left_dark);
                    mBinding.textViewToolbarTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));

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
                    mBinding.textViewToolbarTitle.setVisibility(View.GONE);
                    mBinding.toolbar.setNavigationIcon(R.drawable.arrow_left_white);
                    mBinding.textViewToolbarTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));

                }
            }
        });


    }

    private void initializeViewAndListeners() {

    }

    /*To get help and faq data*/
    @SuppressLint("CheckResult")
    private void getHelpAndFAQ() {

        dismissInternetSnackBar();
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {

            showBottomProgress();
            mHomeModel.fetchHelpAndFAQ()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<HelpAndFaqCategory>>() {
                        @Override
                        public void accept(ArrayList<HelpAndFaqCategory> list) throws Exception {

                            hideBottomProgress();
                            if (!list.isEmpty()) {

                                initializeRecyclerView(list);

                            } else {
                                mBinding.list.setVisibility(View.GONE);
                                mBinding.textViewError.setVisibility(View.VISIBLE);
                            }


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            hideBottomProgress();
                            mBinding.list.setVisibility(View.GONE);
                            mBinding.textViewError.setVisibility(View.VISIBLE);
                            showAlertMessage(getString(R.string.error_something_went_wrong));

                            throwable.printStackTrace();

                        }
                    });

        } else {
            showInternetSnackBar();
        }

    }

    /*Common method to show any error or prompt as snackBar*/
    private void showAlertMessage(String message) {
        SnackBarUtils.showSnackBar(getBaseContext(), mBinding.getRoot(), message, SnackBarUtils.UNSUCCESSFUL);
    }

    private void showInternetSnackBar() {

        hideBottomProgress();

        mInternetSnackBar = Snackbar.make(mBinding.getRoot(), getString(R.string.error_message_no_internet), LENGTH_INDEFINITE);

        mInternetSnackBar.setAction((R.string.labelRetry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getHelpAndFAQ();

            }
        }).show();

    }

    private void dismissInternetSnackBar() {
        if (mInternetSnackBar != null && mInternetSnackBar.isShown()) {
            mInternetSnackBar.dismiss();
        }
    }

    private void hideBottomProgress() {
        AnimationUtils.pushDownExit(getBaseContext(), mBinding.progressContent);
        mBinding.layoutProgressBottom.setVisibility(View.GONE);
    }

    private void showBottomProgress() {
//        mBinding.textViewBottomProgressMessage.setText(message);
        mBinding.layoutProgressBottom.setVisibility(View.VISIBLE);
        AnimationUtils.pushUpEnter(getBaseContext(), mBinding.progressContent);
    }

    private void initializeRecyclerView(ArrayList<HelpAndFaqCategory> list) {
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.list.setLayoutManager(layoutManager);
        HelpAndFaqCategoryListAdapter faqCategoryListAdapter = new HelpAndFaqCategoryListAdapter(getBaseContext(), list);
        mBinding.list.setAdapter(faqCategoryListAdapter);
    }

}
