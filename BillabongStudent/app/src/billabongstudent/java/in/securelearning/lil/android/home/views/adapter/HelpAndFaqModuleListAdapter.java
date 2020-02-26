package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutHelpAndFaqModuleListItemBinding;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.syncadapter.dataobjects.FAQuestionAnswer;
import in.securelearning.lil.android.syncadapter.dataobjects.HelpAndFaqModule;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;

public class HelpAndFaqModuleListAdapter extends RecyclerView.Adapter<HelpAndFaqModuleListAdapter.ViewHolder> {

    @Inject
    HomeModel mHomeModel;

    private Context mContext;
    private ArrayList<HelpAndFaqModule> mList;
    private int mExpandedPosition = -1;

    public HelpAndFaqModuleListAdapter(Context context, ArrayList<HelpAndFaqModule> list) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public HelpAndFaqModuleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutHelpAndFaqModuleListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_help_and_faq_module_list_item, parent, false);
        return new HelpAndFaqModuleListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final HelpAndFaqModuleListAdapter.ViewHolder holder, final int position) {
        final HelpAndFaqModule helpAndFaqModule = mList.get(position);


        Picasso.with(mContext).load(R.drawable.image_faq).into(holder.mBinding.imageViewBackgroundFaq);
        if (!TextUtils.isEmpty(helpAndFaqModule.getThumbnailUrl())) {
            Picasso.with(mContext).load(helpAndFaqModule.getThumbnailUrl()).into(holder.mBinding.imageViewVideoModule);
        }

        setDuration(helpAndFaqModule.getVideoDuration(), holder.mBinding.textViewDuration);
        setModuleName(helpAndFaqModule.getModule(), holder.mBinding.textViewModuleTitle);
        initializeRecyclerView(helpAndFaqModule.getFAQuestionAnswerList(), holder.mBinding);

        if (mExpandedPosition == position) {
            holder.mBinding.recyclerViewFaq.setVisibility(View.VISIBLE);
        } else {
            holder.mBinding.recyclerViewFaq.setVisibility(View.GONE);
        }

        holder.mBinding.cardViewFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        mExpandedPosition = position;
                        notifyDataSetChanged();

                    }
                }, 300);

            }
        });

        holder.mBinding.cardViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(helpAndFaqModule.getVideoLink())) {

                    Resource resource = new Resource();
                    resource.setUrl(helpAndFaqModule.getVideoLink());

                    mHomeModel.playVideo(resource);

                } else {
                    GeneralUtils.showToastShort(mContext, mContext.getString(R.string.error_something_went_wrong));
                }

            }
        });


    }

    private void setModuleName(String category, AppCompatTextView textView) {
        if (!TextUtils.isEmpty(category)) {
            textView.setText(category);
        }
    }

    private void setDuration(int videoDuration, AppCompatTextView textView) {
        if (videoDuration > 0) {
            textView.setVisibility(View.VISIBLE);
            long durationLong = (long) (videoDuration * 1000);//here getting duration in seconds
            textView.setText(CommonUtils.getInstance().showSecondAndMinutesFromLong(durationLong));
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void initializeRecyclerView(ArrayList<FAQuestionAnswer> faqList, LayoutHelpAndFaqModuleListItemBinding binding) {
        if (!faqList.isEmpty()) {
            binding.recyclerViewFaq.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            FaqListAdapter faqListAdapter = new FaqListAdapter(mContext, faqList);
            binding.recyclerViewFaq.setAdapter(faqListAdapter);
            binding.cardViewFaq.setVisibility(View.VISIBLE);
        } else {
            binding.cardViewFaq.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addValues(ArrayList<HelpAndFaqModule> list) {
        if (mList != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutHelpAndFaqModuleListItemBinding mBinding;

        public ViewHolder(LayoutHelpAndFaqModuleListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}