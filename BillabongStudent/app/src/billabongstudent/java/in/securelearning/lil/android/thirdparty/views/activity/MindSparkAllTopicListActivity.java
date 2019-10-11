package in.securelearning.lil.android.thirdparty.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutPracticeListItemFullWidthBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewFitWindowBinding;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.thirdparty.InjectorThirdParty;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkContentDetails;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkTopicResult;
import in.securelearning.lil.android.thirdparty.model.ThirdPartyModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MindSparkAllTopicListActivity extends AppCompatActivity {
    LayoutRecyclerViewFitWindowBinding mBinding;

    @Inject
    ThirdPartyModel mThirdPartyModel;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MindSparkAllTopicListActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorThirdParty.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_recycler_view_fit_window);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorCenterGradient));
        setUpToolbar();
        fetchAllMindSparkTopic();
    }

    @SuppressLint("CheckResult")
    private void fetchAllMindSparkTopic() {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.recyclerView.setVisibility(View.GONE);
            mBinding.textViewError.setVisibility(View.GONE);
            mThirdPartyModel.getMindSparkTopicResult()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MindSparkTopicResult>() {
                        @Override
                        public void accept(MindSparkTopicResult mindSparkTopicResult) throws Exception {
                            mBinding.progressBar.setVisibility(View.GONE);
                            if (mindSparkTopicResult != null && mindSparkTopicResult.getTopicList() != null && !mindSparkTopicResult.getTopicList().isEmpty()) {
                                mBinding.recyclerView.setVisibility(View.VISIBLE);
                                mBinding.textViewError.setVisibility(View.GONE);
                                initializeRecyclerView(mindSparkTopicResult.getTopicList());
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

    private void initializeRecyclerView(ArrayList<MindSparkContentDetails> topicList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(new PracticeAdapterMS(getBaseContext(), topicList));
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

    private void setUpToolbar() {
        setSupportActionBar(mBinding.toolbar);
        mBinding.imageViewLogo.setImageResource(R.drawable.logo_mind_spark);
        setTitle("Topic Practice");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.gradient_app));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.chevron_left_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class PracticeAdapterMS extends RecyclerView.Adapter<PracticeAdapterMS.ViewHolder> {
        private Context mContext;
        private ArrayList<MindSparkContentDetails> mList;

        PracticeAdapterMS(Context context, ArrayList<MindSparkContentDetails> list) {
            mContext = context;
            this.mList = list;
        }

        @NonNull
        @Override
        public PracticeAdapterMS.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutPracticeListItemFullWidthBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_practice_list_item_full_width, parent, false);
            return new PracticeAdapterMS.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final PracticeAdapterMS.ViewHolder holder, int position) {
            final MindSparkContentDetails response = mList.get(position);
            setThumbnail(holder.mBinding.imageViewBackground);
            setStatus(response.getUnitsCleared(), response.getUnitsOverall(), holder.mBinding.textViewStatus);
            holder.mBinding.textViewTitle.setText(response.getContentName());
            holder.mBinding.textViewRewardPoints.setVisibility(View.GONE);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        if (response.getUnitsOverall() > 0) {
                            startActivity(MindSparkPlayerActivity.getStartIntent(mContext, response.getContentId(), response.getContentName()));
                        } else {
                            CommonUtils.getInstance().showAlertDialog(MindSparkAllTopicListActivity.this, getString(R.string.mindSparkNoUnitMessageList));
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.connect_internet), Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }

        private void setStatus(int unitCleared, int unitOverall, AppCompatTextView textViewStatus) {
            String value;
            if (unitCleared > 1) {
                value = unitCleared + " out of " + unitOverall + " units covered";
            } else {
                value = unitCleared + " out of " + unitOverall + " unit covered";
            }
            textViewStatus.setText(value);
        }

        private void setThumbnail(ImageView imageView) {
            Picasso.with(mContext).load(R.drawable.background_thumb_mind_spark).fit().centerCrop().into(imageView);
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutPracticeListItemFullWidthBinding mBinding;

            public ViewHolder(LayoutPracticeListItemFullWidthBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
